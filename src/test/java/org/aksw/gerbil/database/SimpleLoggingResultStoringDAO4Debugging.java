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
package org.aksw.gerbil.database;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentTaskResult;

public class SimpleLoggingResultStoringDAO4Debugging extends SimpleLoggingDAO4Debugging {

    private Int2ObjectOpenHashMap<ExperimentTaskResult> results = new Int2ObjectOpenHashMap<ExperimentTaskResult>();
    private Int2IntOpenHashMap states = new Int2IntOpenHashMap();

    @Override
    public void setExperimentTaskResult(int experimentTaskId, ExperimentTaskResult result) {
        super.setExperimentTaskResult(experimentTaskId, result);
        results.put(experimentTaskId, result);
    }

    @Override
    public void setExperimentState(int experimentTaskId, int state) {
        super.setExperimentState(experimentTaskId, state);
        states.put(experimentTaskId, state);
    }

    public ExperimentTaskResult getTaskResult(int experimentTaskId) {
        if (results.containsKey(experimentTaskId)) {
            return results.get(experimentTaskId);
        } else {
            return null;
        }
    }

    @Override
    public int getExperimentState(int experimentTaskId) {
        if (states.containsKey(experimentTaskId)) {
            return states.get(experimentTaskId);
        } else {
            return 0;
        }
    }

    @Override
    protected List<String[]> getAnnotatorDatasetCombinations(String experimentType, String matching) {
        return new ArrayList<String[]>(0);
    }

    @Override
    protected ExperimentTaskResult getLatestExperimentTaskResult(String experimentType, String matching,
            String annotatorName, String datasetName) {
        return null;
    }

}
