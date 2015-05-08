package org.aksw.gerbil.execute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.annotator.TestEntityExtractor;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datasets.NIFFileDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class OKEChallengeTask1Test extends AbstractExperimentTaskTest {

    private static final String TEXTS[] = new String[] {
            "Florence May Harding studied at a school in Sydney, and with Douglas Robert Dundas , but in effect had no formal training in either botany or art.",
            "Such notables include James Carville, who was the senior political adviser to Bill Clinton, and Donna Brazile, the campaign manager of the 2000 presidential campaign of Vice-President Al Gore.",
            "The senator received a Bachelor of Laws from the Columbia University." };
    private static final DatasetConfiguration GOLD_STD = new NIFFileDatasetConfig("OKE_Task1",
            "src/test/resources/OKE_Challenge/example_data/task1.ttl", false, ExperimentType.EExt);
    private static final UriKBClassifier URI_KB_CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier(
            "http://dbpedia.org/resource/");

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The extractor returns nothing
        testConfigs.add(new Object[] { new Document[] {}, GOLD_STD, Matching.WEAK_ANNOTATION_MATCH,
                new double[] { 0, 0, 0, 0, 0, 0, 0 } });
        // The extractor found everything and marked all entities using the OKE
        // URI --> some of them should be wrong, because they are not linked to
        // the DBpedia
        // FIXME Add typing part of the task!!!
        testConfigs
                .add(new Object[] {
                        new Document[] {
                                new DocumentImpl(
                                        TEXTS[0],
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-1",
                                        Arrays.asList(
                                                (Marking) new NamedEntity(0, 20,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Florence_May_Harding"),
                                                (Marking) new NamedEntity(34, 6,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/National_Art_School"),
                                                (Marking) new NamedEntity(44, 6,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Sydney"),
                                                (Marking) new NamedEntity(61, 21,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Douglas_Robert_Dundas"))),
                                new DocumentImpl(
                                        TEXTS[1],
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-2",
                                        Arrays.asList(
                                                (Marking) new NamedEntity(22, 14,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/James_Carville"),
                                                (Marking) new NamedEntity(57, 17,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Political_adviser"),
                                                (Marking) new NamedEntity(78, 12,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Bill_Clinton"),
                                                (Marking) new NamedEntity(96, 13,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Donna_Brazile"),
                                                (Marking) new NamedEntity(115, 16,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Campaign_manager"),
                                                (Marking) new NamedEntity(184, 7,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Al_Gore"))),
                                new DocumentImpl(
                                        TEXTS[2],
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-3",
                                        Arrays.asList(
                                                (Marking) new NamedEntity(4, 7,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Senator_1"),
                                                (Marking) new NamedEntity(49, 19,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Columbia_University"))) },
                        GOLD_STD, Matching.WEAK_ANNOTATION_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
        // The extractor found everything and marked all entities using dbpedia
        // URIs (if they were available)
        testConfigs
                .add(new Object[] {
                        new Document[] {
                                new DocumentImpl(
                                        TEXTS[0],
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-1",
                                        Arrays.asList(
                                                (Marking) new NamedEntity(0, 20,
                                                        "http://dbpedia.org/resource/Florence_May_Harding"),
                                                (Marking) new NamedEntity(34, 6,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/National_Art_School"),
                                                (Marking) new NamedEntity(44, 6, "http://dbpedia.org/resource/Sydney"),
                                                (Marking) new NamedEntity(61, 21,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Douglas_Robert_Dundas"))),
                                new DocumentImpl(
                                        TEXTS[1],
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-2",
                                        Arrays.asList(
                                                (Marking) new NamedEntity(22, 14,
                                                        "http://dbpedia.org/resource/James_Carville"),
                                                (Marking) new NamedEntity(57, 17,
                                                        "http://dbpedia.org/resource/Political_consulting"),
                                                (Marking) new NamedEntity(78, 12,
                                                        "http://dbpedia.org/resource/Bill_Clinton"),
                                                (Marking) new NamedEntity(96, 13,
                                                        "http://dbpedia.org/resource/Donna_Brazile"),
                                                (Marking) new NamedEntity(115, 16,
                                                        "http://dbpedia.org/resource/Campaign_manager"),
                                                (Marking) new NamedEntity(184, 7, "http://dbpedia.org/resource/Al_Gore"))),
                                new DocumentImpl(
                                        TEXTS[2],
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-3",
                                        Arrays.asList(
                                                (Marking) new NamedEntity(4, 7,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Senator_1"),
                                                (Marking) new NamedEntity(49, 19,
                                                        "http://dbpedia.org/resource/Columbia_University"))) },
                        GOLD_STD, Matching.WEAK_ANNOTATION_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
        // The extractor found everything and marked all entities using dbpedia
        // URIs (if they were available) or own URIs
        testConfigs.add(new Object[] {
                new Document[] {
                        new DocumentImpl(TEXTS[0],
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-1", Arrays
                                        .asList((Marking) new NamedEntity(0, 20,
                                                "http://dbpedia.org/resource/Florence_May_Harding"),
                                                (Marking) new NamedEntity(34, 6,
                                                        "http://aksws.org/notInWiki/National_Art_School"),
                                                (Marking) new NamedEntity(44, 6, "http://dbpedia.org/resource/Sydney"),
                                                (Marking) new NamedEntity(61, 21,
                                                        "http://aksws.org/notInWiki/Douglas_Robert_Dundas"))),
                        new DocumentImpl(TEXTS[1],
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-2",
                                Arrays.asList((Marking) new NamedEntity(22, 14,
                                        "http://dbpedia.org/resource/James_Carville"), (Marking) new NamedEntity(57,
                                        17, "http://dbpedia.org/resource/Political_consulting"),
                                        (Marking) new NamedEntity(78, 12, "http://dbpedia.org/resource/Bill_Clinton"),
                                        (Marking) new NamedEntity(96, 13, "http://dbpedia.org/resource/Donna_Brazile"),
                                        (Marking) new NamedEntity(115, 16,
                                                "http://dbpedia.org/resource/Campaign_manager"),
                                        (Marking) new NamedEntity(184, 7, "http://dbpedia.org/resource/Al_Gore"))),
                        new DocumentImpl(TEXTS[2],
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-3",
                                Arrays.asList((Marking) new NamedEntity(4, 7, "http://aksws.org/notInWiki/Senator_1"),
                                        (Marking) new NamedEntity(49, 19,
                                                "http://dbpedia.org/resource/Columbia_University"))) }, GOLD_STD,
                Matching.WEAK_ANNOTATION_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
        return testConfigs;
    }

    private Document annotatorResults[];
    private DatasetConfiguration dataset;
    private double expectedResults[];
    private Matching matching;

    public OKEChallengeTask1Test(Document[] annotatorResults, DatasetConfiguration dataset, Matching matching,
            double[] expectedResults) {
        this.annotatorResults = annotatorResults;
        this.dataset = dataset;
        this.expectedResults = expectedResults;
        this.matching = matching;
    }

    @Test
    public void test() {
        int experimentTaskId = 1;
        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(new TestEntityExtractor(
                Arrays.asList(annotatorResults)), dataset, ExperimentType.EExt, matching);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(URI_KB_CLASSIFIER), configuration,
                new F1MeasureTestingObserver(this, experimentTaskId, experimentDAO, expectedResults));
    }
}