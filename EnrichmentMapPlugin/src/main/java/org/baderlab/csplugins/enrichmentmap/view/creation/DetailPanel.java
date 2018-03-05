package org.baderlab.csplugins.enrichmentmap.view.creation;

import java.util.List;

import javax.swing.JPanel;

import org.baderlab.csplugins.enrichmentmap.model.DataSetParameters;

public interface DetailPanel {

	JPanel getPanel();
	
	String getDisplayName();
	
	String getDataSetName();
	
	String getIcon();
	
	List<Message> validateInput(MasterDetailDialogPage parent);
	
	default DataSetParameters createDataSetParameters() { return null; };
	
}
