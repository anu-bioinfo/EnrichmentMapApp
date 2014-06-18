package org.baderlab.csplugins.enrichmentmap.autoannotate;

import java.awt.Paint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.annotations.AnnotationFactory;
import org.cytoscape.view.presentation.annotations.AnnotationManager;
import org.cytoscape.view.presentation.annotations.BoundedTextAnnotation;
import org.cytoscape.view.presentation.annotations.ShapeAnnotation;
import org.cytoscape.view.presentation.annotations.TextAnnotation;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.baderlab.csplugins.enrichmentmap.autoannotate.WordRanker;

/**
 * Created by:
 * @author arkadyark
 * <p>
 * Date   Jun 12, 2014<br>
 * Time   05:32 PM<br>
 */

public final class AutoAnnotator {
    
    private CySwingApplication application;
    private OpenBrowser browser;
	private CyNetwork network;
	private String clusterColumnName;
	private String nameColumnName;
	private CyNetworkViewManager networkViewManager;
	private ArrayList<Cluster> clusters;
	private CyNetworkView networkView;
	private AnnotationManager annotationManager;
	private CyServiceRegistrar registrar;
	private HashMap<Integer, String> clustersToLabels;

	public AutoAnnotator(CySwingApplication application, OpenBrowser browser, 
			CyNetworkManager networkManager, CyNetworkViewManager networkViewManager,
			AnnotationManager annotationManager, long networkID, String clusterColumnName,
			String nameColumnName, CyServiceRegistrar registrar) {
		// get all of the nodes and their corresponding clusters
    	this.application = application;
    	this.browser = browser;
    	this.registrar = registrar;
    	this.network = networkManager.getNetwork(networkID);
    	this.clusterColumnName = clusterColumnName;
    	this.nameColumnName = nameColumnName;
    	
    	this.networkViewManager = networkViewManager;
    	try {
    		this.networkView = getEMNetworkView();
    	} catch (Exception e) {
    		// TODO - this should make some pop-up window
    		System.out.println("Could not find network view!");
    	}
    	
    	this.annotationManager = annotationManager;
    	List<CyNode> nodes = this.network.getNodeList();
    	for (CyNode node : nodes) {
    		this.network.getRow(node).get("name", String.class);
    	}
    	this.clusters = makeClusters();
		drawClusters();
		
		WordRanker wordRanker = new WordRanker(clusters);
		this.clustersToLabels = wordRanker.getClustersToLabels();
		drawAnnotations();
    }
	
	private CyNetworkView getEMNetworkView() throws Exception {
    	if (this.networkViewManager.viewExists(this.network)) {
    		Collection<CyNetworkView> networkViews = this.networkViewManager.getNetworkViews(this.network);
    		return (CyNetworkView) networkViews.toArray()[0];
    	}
    	else {
    		throw new Exception();
    	}
	}
	
	private ArrayList<Cluster> makeClusters() {
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		List<CyNode> nodes = network.getNodeList();
		for (CyNode node : nodes) {
			Integer clusterNumber = this.network.getRow(node).get(this.clusterColumnName, Integer.class);
			if (clusterNumber != null) {
				View<CyNode> nodeView = this.networkView.getNodeView(node);
				double x = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
				double y = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
				double[] coordinates = {x, y};
				
				String nodeName = this.network.getRow(node).get(nameColumnName, String.class);
				NodeText nodeText = new NodeText();
				nodeText.setName(nodeName);
				
				// empty values (no cluster) are given null
				boolean flag = true;
				for (Cluster cluster : clusters) {
	 				if (cluster.getClusterNumber() == clusterNumber && flag) {
						cluster.addNode(node);
						cluster.addCoordinates(coordinates);
						cluster.addNodeText(nodeText);
						flag = false;
					}
				}
				if (flag) {
					Cluster cluster = new Cluster(clusterNumber);
					cluster.addNode(node);
					cluster.addCoordinates(coordinates);
					cluster.addNodeText(nodeText);
					clusters.add(cluster);
				}
			}
		}
		return clusters;
	}
		
	private void drawClusters() {
    	AnnotationFactory<ShapeAnnotation> shapeFactory = (AnnotationFactory<ShapeAnnotation>) registrar.getService(AnnotationFactory.class, "(type=ShapeAnnotation.class)");    	
    	double padding = 1.7;
    	double min_size = 10.0;
    	for (Cluster cluster : clusters) {
    		// extreme initial values
    		double xmin = 100000000;
			double ymin = 100000000;
    		double xmax = -100000000;
    		double ymax = -100000000;
    		for (double[] coordinates : cluster.getCoordinates()) {
    			xmin = coordinates[0] < xmin ? coordinates[0] : xmin;
    			xmax = coordinates[0] > xmax ? coordinates[0] : xmax;
    			ymin = coordinates[1] < ymin ? coordinates[1] : ymin;
    			ymax = coordinates[1] > ymax ? coordinates[1] : ymax;
    		}
    		
    		double zoom = networkView.getVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR);
    		
    		double width = (xmax - xmin)*zoom;
    		width = width > min_size ? width : min_size;
    		double height = (ymax - ymin)*zoom;
    		height = height > min_size ? height : min_size;
    		
    		HashMap<String, String> arguments = new HashMap<String,String>();
    		arguments.put("x", String.valueOf(xmin - width/zoom*padding/5.0));
    		arguments.put("y", String.valueOf(ymin - height/zoom*padding/5.0));
    		arguments.put("zoom", String.valueOf(zoom));
    		arguments.put("canvas", "foreground");
    		ShapeAnnotation ellipse = shapeFactory.createAnnotation(ShapeAnnotation.class, this.networkView, arguments);
    		ellipse.setShapeType("Ellipse");
    		ellipse.setSize(width*padding, height*padding);
    		this.annotationManager.addAnnotation(ellipse);
    	}
	}
	
	private void drawAnnotations() {
    	AnnotationFactory<TextAnnotation> textFactory = (AnnotationFactory<TextAnnotation>) registrar.getService(AnnotationFactory.class, "(type=TextAnnotation.class)");    	
    	double padding = 1.7;
    	double min_size = 10.0;
    	for (Cluster cluster : clusters) {
    		// extreme initial values
    		double xmin = 100000000;
			double ymin = 100000000;
    		double xmax = -100000000;
    		double ymax = -100000000;
    		for (double[] coordinates : cluster.getCoordinates()) {
    			xmin = coordinates[0] < xmin ? coordinates[0] : xmin;
    			xmax = coordinates[0] > xmax ? coordinates[0] : xmax;
    			ymin = coordinates[1] < ymin ? coordinates[1] : ymin;
    			ymax = coordinates[1] > ymax ? coordinates[1] : ymax;
    		}
    		
    		double zoom = networkView.getVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR);
    		
    		// This magic number 10 floating around here isn't good style (nor are the other ones after this)
    		double width = (xmax - xmin)*zoom;
    		width = width > min_size ? width : min_size;
    		double height = (ymax - ymin)*zoom;
    		height = height > min_size ? height : min_size;
    		
    		HashMap<String, String> arguments = new HashMap<String,String>();
    		arguments.put("x", String.valueOf(xmin)); // put your values for the annotation position
    		arguments.put("y", String.valueOf(ymin - height/zoom/1.5)); // put your values for the annotation position
    		arguments.put("zoom", String.valueOf(zoom));
    		arguments.put("canvas", "foreground");
    		TextAnnotation label = textFactory.createAnnotation(TextAnnotation.class, this.networkView, arguments);
    		// not working
    		//label.setFontSize(0.1*Math.sqrt(Math.pow(width, 2)+ Math.pow(height, 2)));
    		label.setFontSize(8.0);
    		label.setText(this.clustersToLabels.get(cluster.getClusterNumber()));
    		this.annotationManager.addAnnotation(label);
    	}
	}
}