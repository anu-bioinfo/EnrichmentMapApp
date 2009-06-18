import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by
 * User: risserlin
 * Date: Jan 9, 2009
 * Time: 2:14:52 PM
*/
public class ComputeSimilarityTask implements Task {

    private EnrichmentMapParameters params;

    private HashMap geneset_similarities;

    // Keep track of progress for monitoring:
    private TaskMonitor taskMonitor = null;
    private boolean interrupted = false;

    public ComputeSimilarityTask(EnrichmentMapParameters params, TaskMonitor taskMonitor) {
          this(params);
          this.taskMonitor = taskMonitor;
      }


    public ComputeSimilarityTask(EnrichmentMapParameters params) {
        this.params = params;
        geneset_similarities = new HashMap();
    }

    public boolean computeMap(){
        try{


            HashMap genesetsOfInterest = params.getGenesetsOfInterest();

            int currentProgress = 0;
            int maxValue = genesetsOfInterest.size();

            //iterate through the each of the GSEA Results of interest
            for(Iterator i = genesetsOfInterest.keySet().iterator(); i.hasNext(); ){

                 // Calculate Percentage.  This must be a value between 0..100.
                int percentComplete = (int) (((double) currentProgress / maxValue) * 100);
                //  Estimate Time Remaining
                long timeRemaining = maxValue - currentProgress;
                if (taskMonitor != null) {
                   taskMonitor.setPercentCompleted(percentComplete);
                   taskMonitor.setStatus("Computing Geneset similirity " + currentProgress + " of " + maxValue);
                   taskMonitor.setEstimatedTimeRemaining(timeRemaining);
                }
                currentProgress++;

                String geneset1_name = i.next().toString();

                //for each individual geneset compute its jaccard index with all other genesets
                 for(Iterator j = genesetsOfInterest.keySet().iterator(); j.hasNext(); ){

                    String geneset2_name = j.next().toString();

                    //Check to see if this comparison has been done
                     //The key for the set of geneset similarities is the
                     //combination of the two names.  Check for either variation name1_name2
                     //or name2_name1
                     String similarity_key1 = geneset1_name + " (pp) " + geneset2_name;
                     String similarity_key2 = geneset2_name + " (pp) " + geneset1_name;

                     //first check to see if the terms are the same
                     if(geneset1_name.equalsIgnoreCase(geneset2_name)){
                        //don't compare two identical genesets
                     }
                     else if(geneset_similarities.containsKey(similarity_key1) || geneset_similarities.containsKey(similarity_key2)){
                         //skip this geneset comparison.  It has already been done.
                     }
                     else{
                         //get the two genesets
                         GeneSet geneset1 = (GeneSet)genesetsOfInterest.get(geneset1_name);
                         GeneSet geneset2 = (GeneSet)genesetsOfInterest.get(geneset2_name);

                         HashSet genes1 = geneset1.getGenes();
                         HashSet genes2 = geneset2.getGenes();

                        //Get the intersection
                         Set<Integer> intersection = new HashSet<Integer>(genes1);
                         intersection.retainAll(genes2);

                         //Get the union of the two sets
                         Set<Integer> union = new HashSet<Integer>(genes1);
                         union.addAll(genes2);

                         double coeffecient;

                         if(params.isJaccard()){

                            //compute Jaccard similarity
                            coeffecient = (double)intersection.size() / (double)union.size();
                         }
                         else{
                             coeffecient = (double)intersection.size() / Math.min((double)genes1.size(), (double)genes2.size());
                         }
                         //create Geneset similarity object
                         GenesetSimilarity comparison = new GenesetSimilarity(geneset1_name,geneset2_name, coeffecient,(HashSet<Integer>)intersection);

                         geneset_similarities.put(similarity_key1,comparison);


                     }

                 }



            }

      /*      System.out.println(geneset_similarities.keySet().toString());
            for(Iterator a = geneset_similarities.keySet().iterator(); a.hasNext();){
                GenesetSimilarity temp = (GenesetSimilarity)geneset_similarities.get(a.next().toString());
                System.out.println(temp.getGeneset1_Name());
                System.out.println(temp.getGeneset2_Name());
                System.out.println(temp.getJaccard_coeffecient());
                System.out.println(temp.getOvarlapping_genes().size());
                System.out.println(temp.getOvarlapping_genes().toString());

            }
            System.out.println(geneset_similarities.size());
        */

        } catch(IllegalThreadStateException e){
            taskMonitor.setException(e, "Unable to compute jaccard coeffecients");
            return false;
        }

       return true;
    }

    public HashMap getGeneset_similarities() {
        return geneset_similarities;
    }

    /**
       * Run the Task.
       */
      public void run() {
         computeMap();
      }

      /**
       * Non-blocking call to interrupt the task.
       */
      public void halt() {
          this.interrupted = true;
      }

       /**
       * Sets the Task Monitor.
       *
       * @param taskMonitor TaskMonitor Object.
       */
      public void setTaskMonitor(TaskMonitor taskMonitor) {
          if (this.taskMonitor != null) {
              throw new IllegalStateException("Task Monitor is already set.");
          }
          this.taskMonitor = taskMonitor;
      }

      /**
       * Gets the Task Title.
       *
       * @return human readable task title.
       */
      public String getTitle() {
          return new String("Computing geneset similarities");
      }

}