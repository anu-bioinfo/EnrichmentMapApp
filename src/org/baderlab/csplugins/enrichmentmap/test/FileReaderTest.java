package org.baderlab.csplugins.enrichmentmap.test;

import junit.framework.TestCase;
import org.baderlab.csplugins.enrichmentmap.EnrichmentMapParameters;
import org.baderlab.csplugins.enrichmentmap.EnrichmentResult;
import org.baderlab.csplugins.enrichmentmap.EnrichmentResultFileReaderTask;
import org.baderlab.csplugins.enrichmentmap.GMTFileReaderTask;
import org.baderlab.csplugins.enrichmentmap.ExpressionFileReaderTask;
import org.baderlab.csplugins.enrichmentmap.GenericResult;

import java.util.HashMap;

/**
 * Created by
 * User: risserlin
 * Date: Mar 26, 2010
 * Time: 8:42:21 AM
 */
public class FileReaderTest extends TestCase {

    public void setUp() throws Exception {

    }

    public void testGMTFileReader(){

        String testDataFileName = "src/org/baderlab/csplugins/enrichmentmap/test/resources/Genesetstestfile.gmt";

        EnrichmentMapParameters params = new EnrichmentMapParameters();

        params.setGMTFileName(testDataFileName);

        //set up task
        GMTFileReaderTask task = new GMTFileReaderTask(params);

        //read in file
        task.run();

        //test to make sure that the file loaded in 10 genesets with a total of 75 genes
        assertEquals(10, params.getGenesets().size());
        assertEquals(75, params.getGenes().size());

    }

    public void testExpression1ReaderNormal(){

        //load the test expression file
        String testDataFileName = "src/org/baderlab/csplugins/enrichmentmap/test/resources/Expressiontestfile.gct";

        EnrichmentMapParameters params = new EnrichmentMapParameters();

        params.setExpressionFileName1(testDataFileName);

        //in order to load expression data the genes have to be registered with the application
        HashMap<String, Integer> genes = params.getGenes();
        HashMap<Integer, String> hash2genes = params.getHashkey2gene();

        //make sure that the genes are empty
        assertEquals(0,genes.size());

        //add the gene to the master list of genes
        int value = params.getNumberOfGenes();
        genes.put("GLS", value);
        hash2genes.put(value,"GLS");
        params.setNumberOfGenes(value++);

        genes.put("PSMA1", value);
        hash2genes.put(value,"PSMA1");
        params.setNumberOfGenes(value++);

        //different case to the one in the expression file
        genes.put("ZP1", value);
        hash2genes.put(value,"ZP1");
        params.setNumberOfGenes(value++);

        genes.put("ZYX", value);
        hash2genes.put(value,"ZYX");
        params.setNumberOfGenes(value++);

        //make sure all four genes have been associated
        assertEquals(4,params.getGenes().size());

        //load expression file
        ExpressionFileReaderTask task = new ExpressionFileReaderTask(params,1);

        task.run();

        //There was one more gene in the expression file that wasn't in the set of genes
        //make sure it was was added
        assertEquals(4,params.getGenes().size());

        assertEquals(4, params.getExpression().getNumGenes());
        assertEquals(59, params.getExpression().getNumConditions());
        assertEquals(0.008720342, params.getExpression().getMinExpression());
        assertEquals(5.131481026, params.getExpression().getMaxExpression());

    }
    public void testExpression1ReaderCommentLines(){

        //load the test expression file
        String testDataFileName = "src/org/baderlab/csplugins/enrichmentmap/test/resources/Expressiontestfile_comments.gct";

        EnrichmentMapParameters params = new EnrichmentMapParameters();

        params.setExpressionFileName1(testDataFileName);

        //in order to load expression data the genes have to be registered with the application
        HashMap<String, Integer> genes = params.getGenes();
        HashMap<Integer, String> hash2genes = params.getHashkey2gene();

        //make sure that the genes are empty
        assertEquals(0,genes.size());

        //add the gene to the master list of genes
        int value = params.getNumberOfGenes();
        genes.put("GLS", value);
        hash2genes.put(value,"GLS");
        params.setNumberOfGenes(value++);

        genes.put("PSMA1", value);
        hash2genes.put(value,"PSMA1");
        params.setNumberOfGenes(value++);

        //different case to the one in the expression file
        genes.put("ZP1", value);
        hash2genes.put(value,"ZP1");
        params.setNumberOfGenes(value++);

        genes.put("ZYX", value);
        hash2genes.put(value,"ZYX");
        params.setNumberOfGenes(value++);

        //make sure all four genes have been associated
        assertEquals(4,params.getGenes().size());

        //load expression file
        ExpressionFileReaderTask task = new ExpressionFileReaderTask(params,1);

        task.run();

        //There was one more gene in the expression file that wasn't in the set of genes
        //make sure it was was added
        assertEquals(4,params.getGenes().size());
        assertEquals(5.131481026, params.getExpression().getMaxExpression());

        assertEquals(4, params.getExpression().getNumGenes());
        assertEquals(59, params.getExpression().getNumConditions());
        assertEquals(0.008720342, params.getExpression().getMinExpression());


    }

    public void testExpression1ReaderRnk(){

        //load the test expression file
        String testDataFileName = "src/org/baderlab/csplugins/enrichmentmap/test/resources/ExpressionTestFile.rnk";

        EnrichmentMapParameters params = new EnrichmentMapParameters();

        params.setExpressionFileName1(testDataFileName);

        //in order to load expression data the genes have to be registered with the application
        HashMap<String, Integer> genes = params.getGenes();
        HashMap<Integer, String> hash2genes = params.getHashkey2gene();

        //make sure that the genes are empty
        assertEquals(0,genes.size());

        //add the gene to the master list of genes
        int value = params.getNumberOfGenes();
        genes.put("GLS", value);
        hash2genes.put(value,"GLS");
        params.setNumberOfGenes(value++);

        genes.put("PSMA1", value);
        hash2genes.put(value,"PSMA1");
        params.setNumberOfGenes(value++);

        //different case to the one in the expression file
        genes.put("ZP1", value);
        hash2genes.put(value,"ZP1");
        params.setNumberOfGenes(value++);

        genes.put("ZYX", value);
        hash2genes.put(value,"ZYX");
        params.setNumberOfGenes(value++);

        //make sure all four genes have been associated
        assertEquals(4,params.getGenes().size());

        //load expression file
        ExpressionFileReaderTask task = new ExpressionFileReaderTask(params,1);

        task.run();

        //There was one more gene in the expression file that wasn't in the set of genes
        //make sure it was was added
        assertEquals(4,params.getGenes().size());

        assertEquals(4, params.getExpression().getNumGenes());
        assertEquals(3, params.getExpression().getNumConditions());
        assertEquals(0.47536945, params.getExpression().getMinExpression());
        assertEquals(0.5418719, params.getExpression().getMaxExpression());

    }

    public void testExpression1ReaderEDBRnk(){

        //load the test expression file
        String testDataFileName = "src/org/baderlab/csplugins/enrichmentmap/test/resources/ExpressionTestFile_edbrnk.rnk";

        EnrichmentMapParameters params = new EnrichmentMapParameters();

        params.setExpressionFileName1(testDataFileName);

        //in order to load expression data the genes have to be registered with the application
        HashMap<String, Integer> genes = params.getGenes();
        HashMap<Integer, String> hash2genes = params.getHashkey2gene();

        //make sure that the genes are empty
        assertEquals(0,genes.size());

        //add the gene to the master list of genes
        int value = params.getNumberOfGenes();
        genes.put("GLS", value);
        hash2genes.put(value,"GLS");
        params.setNumberOfGenes(value++);

        genes.put("PSMA1", value);
        hash2genes.put(value,"PSMA1");
        params.setNumberOfGenes(value++);

        //different case to the one in the expression file
        genes.put("ZP1", value);
        hash2genes.put(value,"ZP1");
        params.setNumberOfGenes(value++);

        genes.put("ZYX", value);
        hash2genes.put(value,"ZYX");
        params.setNumberOfGenes(value++);

        //make sure all four genes have been associated
        assertEquals(4,params.getGenes().size());

        //load expression file
        ExpressionFileReaderTask task = new ExpressionFileReaderTask(params,1);

        task.run();

        //There was one more gene in the expression file that wasn't in the set of genes
        //make sure it was was added
        assertEquals(4,params.getGenes().size());

        assertEquals(4, params.getExpression().getNumGenes());
        assertEquals(3, params.getExpression().getNumConditions());
        assertEquals(0.47536945, params.getExpression().getMinExpression());
        assertEquals(0.5418719, params.getExpression().getMaxExpression());

    }
    
    public void testGenericFileReader_5columns(){
        //load the test expression file
        String testDataFileName = "src/org/baderlab/csplugins/enrichmentmap/test/resources/generic_enr_5col.txt";
        
        // create parameters
        EnrichmentMapParameters params = new EnrichmentMapParameters();
        params.setEnrichmentDataset1FileName1(testDataFileName);
        
        // check if empty
        assertEquals(0, params.getEnrichmentResults1().size());
        
        // read
        EnrichmentResultFileReaderTask task = new EnrichmentResultFileReaderTask(params, testDataFileName, 1);
        task.run();

        HashMap<String, EnrichmentResult> results = params.getEnrichmentResults1();
        // check we have 4 results
        assertEquals(4, results.size() );
        
        // check pValues
        assertEquals(0.01,     ((GenericResult)results.get("GO:0000346")).getPvalue());
        assertEquals(0.05,     ((GenericResult)results.get("GO:0030904")).getPvalue());
        assertEquals(0.05,     ((GenericResult)results.get("GO:0008623")).getPvalue());
        assertEquals(5.60E-42, ((GenericResult)results.get("GO:0046540")).getPvalue());

        // check getFdrqvalues
        assertEquals(0.02, ((GenericResult)results.get("GO:0000346")).getFdrqvalue());
        assertEquals(0.10, ((GenericResult)results.get("GO:0030904")).getFdrqvalue());
        assertEquals(0.12, ((GenericResult)results.get("GO:0008623")).getFdrqvalue());
        assertEquals(0.03, ((GenericResult)results.get("GO:0046540")).getFdrqvalue());

        // check phenotypes
        assertEquals( 1.0, ((GenericResult)results.get("GO:0000346")).getNES());
        assertEquals( 1.0, ((GenericResult)results.get("GO:0030904")).getNES());
        assertEquals(-1.0, ((GenericResult)results.get("GO:0008623")).getNES());
        assertEquals(-1.0, ((GenericResult)results.get("GO:0046540")).getNES());
        
        return;
    }
}
