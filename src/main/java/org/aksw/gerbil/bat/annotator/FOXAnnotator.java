/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.bat.annotator;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.ScoredAnnotation;
import it.acubelab.batframework.data.ScoredTag;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.Sa2WSystem;
import it.acubelab.batframework.utils.AnnotationException;
import it.acubelab.batframework.utils.ProblemReduction;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

import org.aksw.fox.binding.java.FoxApi;
import org.aksw.fox.binding.java.FoxParameter;
import org.aksw.fox.binding.java.FoxResponse;
import org.aksw.fox.binding.java.IFoxApi;
import org.aksw.gerbil.bat.converter.DBpediaToWikiId;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FOXAnnotator implements Sa2WSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(FOXAnnotator.class);

    /*
     * static {
     * PropertyConfigurator.configure(FOXAnnotator.class.getResourceAsStream("log4jFOXAnnotator.properties"));
     * }
     */

    public static final String NAME = "FOX";
    protected IFoxApi fox = new FoxApi();
    protected WikipediaApiInterface wikiApi;

    public static void main(String[] a) {
        String test = "The philosopher and mathematician Gottfried Wilhelm Leibniz was born in Leipzig.";
        HashSet<Annotation> set = new FOXAnnotator(SingletonWikipediaApi.getInstance()).solveA2W(test);
        LOGGER.info("Got {} annotations.", set.size());
    }

    public FOXAnnotator(WikipediaApiInterface wikiApi) {
        this.wikiApi = wikiApi;
    }

    @Override
    public HashSet<ScoredTag> solveSc2W(String text) throws AnnotationException {
        return ProblemReduction.Sa2WToSc2W(solveSa2W(text));
    }

    @Override
    public HashSet<ScoredAnnotation> solveSa2W(String text) throws AnnotationException {
        return (HashSet<ScoredAnnotation>) fox(text);
    }

    @Override
    public HashSet<Annotation> solveA2W(String text) throws AnnotationException {
        return ProblemReduction.Sa2WToA2W(solveSa2W(text), Float.MIN_VALUE);
    }

    @Override
    public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) throws AnnotationException {
        return ProblemReduction.Sa2WToD2W(solveSa2W(text), mentions, Float.MIN_VALUE);

    }

    @Override
    public HashSet<Tag> solveC2W(String text) throws AnnotationException {
        return ProblemReduction.A2WToC2W(solveA2W(text));
    }

    protected Set<ScoredAnnotation> fox(String text) throws AnnotationException {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("Got text \"{}\".", text);

        Set<ScoredAnnotation> set = new HashSet<>();
        try {
            // request FOX
            FoxResponse response = fox
                    .setInput(text)
                    .setLightVersion(FoxParameter.FOXLIGHT.OFF)
                    .setOutputFormat(FoxParameter.OUTPUT.JSONLD)
                    .setTask(FoxParameter.TASK.NER)
                    .send();

            // parse results
            if (response != null && response.getOutput() != null) {
                JSONObject outObj = new JSONObject(response.getOutput());
                if (outObj.has("@graph")) {

                    JSONArray graph = outObj.getJSONArray("@graph");
                    for (int i = 0; i < graph.length(); i++)
                        set.addAll(add(graph.getJSONObject(i)));

                } else
                    set.addAll(add(outObj));
            }
        } catch (Exception e) {
            LOGGER.error("Got an exception while communicating with the FOX web service.", e);
            throw new AnnotationException("Got an exception while communicating with the FOX web service: "
                    + e.getLocalizedMessage());
        }
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("Found {} annotations.", set.size());
        return set;
    }

    protected Set<ScoredAnnotation> add(JSONObject entity) throws Exception {
        Set<ScoredAnnotation> set = new HashSet<>();
        try {

            if (entity != null && entity.has("means") && entity.has("beginIndex") && entity.has("ann:body")) {

                String uri = entity.getString("means");
                String body = entity.getString("ann:body");
                Object begin = entity.get("beginIndex");

                int wikiID = DBpediaToWikiId.getId(wikiApi, URLDecoder.decode(uri, "UTF-8"));
                if (wikiID > -1) {
                    if (begin instanceof JSONArray) {
                        // for all indices
                        for (int ii = 0; ii < ((JSONArray) begin).length(); ii++) {
                            int b = Integer.valueOf(((JSONArray) begin).getString(ii));
                            set.add(new ScoredAnnotation(b, b + body.length(), wikiID, 1f));
                            if (LOGGER.isDebugEnabled())
                                LOGGER.debug("[begin={}, body={}, id={}]", b, body, wikiID);
                        }
                    } else if (begin instanceof String) {
                        // just one index
                        int b = Integer.valueOf((String) begin);
                        set.add(new ScoredAnnotation(b, b + body.length(), wikiID, 1f));
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("[begin={}, body={}, id={}]", b, body, wikiID);

                    } else if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Couldn't find index");
                } else if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Couldn't find ".concat(uri));
            }
        } catch (Exception e) {
            LOGGER.error("Got an Exception while parsing the response of FOX.", e);
            throw new Exception("Got an Exception while parsing the response of FOX.", e);
        }
        return set;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public long getLastAnnotationTime() {
        return -1;
    }
}
