import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.task.TaskMonitor;
import cytoscape.task.Task;


import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;

/**
 * Created by
 * User: risserlin
 * Date: Jan 8, 2009
 * Time: 1:35:40 PM
 */
public class BuildEnrichmentMapActionListener implements ActionListener {

    private JTaskConfig config;

    private GenericInputFilesPanel inputPanel;
    private EnrichmentMapParameters params;

    public BuildEnrichmentMapActionListener (GenericInputFilesPanel inputPanel, EnrichmentMapParameters params) {
        this.inputPanel = inputPanel;
        this.params = params;

    }

    public BuildEnrichmentMapActionListener (EnrichmentMapParameters params) {
        this.params = params;

    }

   public void actionPerformed(ActionEvent event) {

        config = new JTaskConfig();
        config.displayCancelButton(true);
        config.displayCloseButton(true);
        config.displayStatus(true);

       //Check to see if the user changed any of the fields manually
       if(this.inputPanel.checkForChanges()){



        //set the pvalue, qvalue, and jaccardCurOff
        double pvalue = this.inputPanel.getPvalue();
        if(pvalue > 1.0 || pvalue < 0.0)
             JOptionPane.showMessageDialog(inputPanel,"invalid p-value");
        else{
            params.setPvalue(pvalue);
        }

        double qvalue = this.inputPanel.getQvalue();
        if(qvalue > 100.0 || qvalue < 0.0)
             JOptionPane.showMessageDialog(inputPanel,"invalid q-value");
        else{
            params.setQvalue(qvalue);
        }

        double jaccardCutOff = this.inputPanel.getJaccard();
        if(jaccardCutOff > 1.0 || jaccardCutOff < 0.0)
             JOptionPane.showMessageDialog(inputPanel,"invalid jaccard CutOff");
        else{
            params.setJaccardCutOff(jaccardCutOff);
        }

       if(params.isGSEA()){
            BuildGSEAEnrichmentMapTask new_map = new BuildGSEAEnrichmentMapTask(inputPanel,params);
            boolean success = TaskManager.executeTask(new_map,config);
       }
       else{
           BuildGenericEnrichmentMapTask new_map = new BuildGenericEnrichmentMapTask(inputPanel, params);
           boolean success = TaskManager.executeTask(new_map, config);
       }

       inputPanel.dispose();
       inputPanel.close();
     }
   }


}
