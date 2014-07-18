package org.baderlab.csplugins.enrichmentmap.autoannotate;

import javax.swing.JPanel;

import org.baderlab.csplugins.enrichmentmap.autoannotate.view.AutoAnnotatorDisplayPanel;
import org.baderlab.csplugins.enrichmentmap.autoannotate.view.AutoAnnotatorInputPanel;
import org.cytoscape.application.events.SetSelectedNetworkViewsEvent;
import org.cytoscape.application.events.SetSelectedNetworkViewsListener;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.ColumnNameChangedEvent;
import org.cytoscape.model.events.ColumnNameChangedListener;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
/**
 * Created by
 * User: arkadyark
 * Date: July 9, 2014
 * Time: 9:46 AM
 */

public class AutoAnnotationManager implements
		SetSelectedNetworkViewsListener, ColumnCreatedListener, 
		ColumnDeletedListener, ColumnNameChangedListener,
		NetworkViewAboutToBeDestroyedListener {

	private static AutoAnnotationManager manager = null;
	
	public AutoAnnotatorInputPanel inputPanel;
	private AutoAnnotatorDisplayPanel displayPanel;
	
    public static AutoAnnotationManager getInstance() {
        if(manager == null)
            manager = new AutoAnnotationManager();
        return manager;
    }
	
    public void setInputPanel(AutoAnnotatorInputPanel inputPanel) {
    	this.inputPanel = inputPanel;
    }
    
    public void setDisplayPanel(AutoAnnotatorDisplayPanel displayPanel) {
    	this.displayPanel = displayPanel;
    }
	
	@Override
	public void handleEvent(SetSelectedNetworkViewsEvent e) {
		inputPanel.updateSelectedView(e.getNetworkViews().get(0));
		displayPanel.updateSelectedView(e.getNetworkViews().get(0));
	}
	
	@Override
	public void handleEvent(ColumnNameChangedEvent e) {
		if (inputPanel != null) {
			inputPanel.updateColumnName(e.getSource(), e.getOldColumnName(), e.getNewColumnName());
		}
	}

	@Override
	public void handleEvent(ColumnDeletedEvent e) {
		if (inputPanel != null) {
			inputPanel.columnDeleted(e.getSource(), e.getColumnName());
		}
	}

	@Override
	public void handleEvent(ColumnCreatedEvent e) {
		if (inputPanel != null) {
			inputPanel.columnCreated(e.getSource(), e.getColumnName());
		}
	}

	@Override
	public void handleEvent(NetworkViewAboutToBeDestroyedEvent arg0) {
		if (inputPanel != null) {
			inputPanel.networkLabel.setText("No network selected");
			((JPanel) inputPanel.networkLabel.getParent()).updateUI();
		}
	}
}
