/**
 * Created by
 * User: arkadyark
 * Date: Jul 24, 2014
 * Time: 12:50:09 PM
 */
package org.baderlab.csplugins.enrichmentmap.autoannotate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.baderlab.csplugins.enrichmentmap.autoannotate.model.AnnotationSet;
import org.baderlab.csplugins.enrichmentmap.autoannotate.model.Cluster;
import org.baderlab.csplugins.enrichmentmap.autoannotate.model.WordInfo;
import org.baderlab.csplugins.enrichmentmap.autoannotate.model.WordInfoNumberComparator;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.annotations.AnnotationFactory;
import org.cytoscape.view.presentation.annotations.AnnotationManager;
import org.cytoscape.view.presentation.annotations.ShapeAnnotation;
import org.cytoscape.view.presentation.annotations.TextAnnotation;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;

public class AutoAnnotationUtils {
	
	private static int min_size = 50; // Minimum size of the ellipse
	
	public static void selectCluster(Cluster selectedCluster, CyNetwork network, 
			CommandExecutorTaskFactory executor, SynchronousTaskManager<?> syncTaskManager,
			int ellipseBorderWidth) {
		if (!selectedCluster.isSelected()) {
			AutoAnnotationManager autoAnnotationManager = AutoAnnotationManager.getInstance();
			autoAnnotationManager.flushPayloadEvents();
			// Wait for heatmap to finish updating
			boolean heatMapUpdating = true;
			while (heatMapUpdating) {
				heatMapUpdating = autoAnnotationManager.isHeatMapUpdating();
			}
			selectedCluster.setSelected(true);
			// Select node(s) in the cluster
			if (selectedCluster.isCollapsed()) {
				network.getRow(selectedCluster.getGroupNode()).set(CyNetwork.SELECTED, true);
			} else {
				for (CyNode node : selectedCluster.getNodesToCoordinates().keySet()) {
					network.getRow(node).set(CyNetwork.SELECTED, true);
				}
			}
			// Select the corresponding WordCloud through command line
			ArrayList<String> commands = new ArrayList<String>();
			String command = "wordcloud select cloudName=\"" + selectedCluster.getCloudName() + "\"";
			commands.add(command);
			TaskIterator task = executor.createTaskIterator(commands, null);
			syncTaskManager.execute(task);
			// Select the annotations (ellipse and text label)
			selectedCluster.getEllipse().setBorderColor(Color.YELLOW);
			selectedCluster.getEllipse().setBorderWidth(3*ellipseBorderWidth);
			selectedCluster.getTextAnnotation().setTextColor(Color.YELLOW);
		}
	}

	public static void deselectCluster(Cluster deselectedCluster, CyNetwork network, int ellipseBorderWidth) {
		if (deselectedCluster.isSelected()) {
			deselectedCluster.setSelected(false);
			// Deselect node(s) in the cluster
			if (deselectedCluster.isCollapsed()) {
				network.getRow(deselectedCluster.getGroupNode()).set(CyNetwork.SELECTED, false);
			} else {
				for (CyNode node : deselectedCluster.getNodesToCoordinates().keySet()) {
					network.getRow(node).set(CyNetwork.SELECTED, false);
				}
			}
			// Deselect the annotations
			deselectedCluster.getEllipse().setBorderColor(Color.DARK_GRAY);
			deselectedCluster.getEllipse().setBorderWidth(ellipseBorderWidth);
			deselectedCluster.getTextAnnotation().setTextColor(Color.BLACK);
		}
	}

	public static void destroyCluster(Cluster clusterToDestroy, CommandExecutorTaskFactory executor, 
			SynchronousTaskManager<?> syncTaskManager) {
		destroyCloud(clusterToDestroy, executor, syncTaskManager);
		// Erase the annotations
		clusterToDestroy.erase();
		// Remove the cluster from the annotation set
		clusterToDestroy.getParent().getClusterMap().remove(clusterToDestroy.getClusterNumber());
	}
	
	public static void destroyCloud(Cluster clusterToDestroy, CommandExecutorTaskFactory executor, 
			SynchronousTaskManager<?> syncTaskManager) {
		// Delete the WordCloud through the command line
		ArrayList<String> commands = new ArrayList<String>();
		String command = "wordcloud delete cloudName=\"" + clusterToDestroy.getCloudName() + "\"";
		commands.add(command);
		TaskIterator task = executor.createTaskIterator(commands, null);
		syncTaskManager.execute(task);
	}

	public static void drawEllipse(Cluster cluster, CyNetworkView view,
			AnnotationFactory<ShapeAnnotation> shapeFactory, AnnotationManager annotationManager, 
			boolean showEllipses, int ellipseBorderWidth, int ellipseOpacity) {
		double zoom = view.getVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR);
    	// Find the edges of the cluster
		double xmin = 100000000;
		double ymin = 100000000;
		double xmax = -100000000;
		double ymax = -100000000;
		for (double[] coordinates : cluster.getNodesToCoordinates().values()) {
			xmin = coordinates[0] < xmin ? coordinates[0] : xmin;
			xmax = coordinates[0] > xmax ? coordinates[0] : xmax;
			ymin = coordinates[1] < ymin ? coordinates[1] : ymin;
			ymax = coordinates[1] > ymax ? coordinates[1] : ymax;
		}
		
		
		double centreX = (xmin + xmax)/2;
		double centreY = (ymin + ymax)/2;
		double width = (xmax - xmin);
		width = width > min_size ? width : min_size;
		double height = (ymax - ymin);
		height = height > min_size ? height : min_size;
		HashMap<CyNode, double[]> nodesToCoordinates = cluster.getNodesToCoordinates();
		HashMap<CyNode, Double> nodesToRadii = cluster.getNodesToRadii();
		while (nodesOutOfCluster(nodesToCoordinates, nodesToRadii, 
				width, height, centreX, centreY, ellipseBorderWidth)) {
			width *= 1.1;
			height *= 1.1;
		}
		width += 40;
		height += 40;
		
		// Set the position of the top-left corner of the ellipse
		Integer xPos = (int) Math.round(centreX - width/2);
		Integer yPos = (int) Math.round(centreY - height/2);
		
		// Create and draw the ellipse
		HashMap<String, String> arguments = new HashMap<String,String>();
		arguments.put("x", String.valueOf(xPos));
		arguments.put("y", String.valueOf(yPos));
		arguments.put("zoom", String.valueOf(zoom));
		arguments.put("canvas", "background");
		ShapeAnnotation ellipse = shapeFactory.createAnnotation(ShapeAnnotation.class, view, arguments);
		ellipse.setShapeType("Ellipse");
		ellipse.setSize(width*zoom, height*zoom);
		ellipse.setBorderWidth(ellipseBorderWidth);
		ellipse.setBorderColor(Color.DARK_GRAY);
		// Fill opacity is broken for now
		ellipse.setFillColor(Color.getHSBColor(0.19f, 1.25f, 0.95f));
		ellipse.setFillOpacity(ellipseOpacity);
		cluster.setEllipse(ellipse);
		if (showEllipses) {
			annotationManager.addAnnotation(ellipse);
		}
	}
	
	private static boolean nodesOutOfCluster(HashMap<CyNode, double[]> nodesToCoordinates, 
			HashMap<CyNode, Double> nodesToRadii, 
			double width, double height,
			double centreX, double centreY, int ellipseWidth) {
		double semimajor_axis = width/2;
		double semiminor_axis = height/2;
		for (CyNode node : nodesToCoordinates.keySet()) {
			double[] coordinates = nodesToCoordinates.get(node);
			double nodeSize = nodesToRadii.get(node);
			if (Math.pow((coordinates[0] - centreX - ellipseWidth)/semimajor_axis,2) +
					Math.pow(nodeSize/semimajor_axis, 2) +
					Math.pow((coordinates[1] - centreY - ellipseWidth)/semiminor_axis,2) +
					Math.pow(nodeSize/semiminor_axis,  2) >= 1) {
				return true;
			}
		}
		return false;
	}
	
	public static void drawTextLabel(Cluster cluster, CyNetworkView view, AnnotationFactory<TextAnnotation> textFactory,
			AnnotationManager annotationManager, boolean constantFontSize, int fontSize) {
		double zoom = view.getVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR);
		// Set the text of the label
		String labelText = cluster.getLabel();

		Map<String, String> ellipseArgs = cluster.getEllipse().getArgMap();
		double xPos = Double.parseDouble(ellipseArgs.get("x"));
		double yPos = Double.parseDouble(ellipseArgs.get("y"));
		double width = Double.parseDouble(ellipseArgs.get("width"));
		
		// Create the text annotation 
		Integer labelFontSize = null;
		String fontSizeArgument = null;
		if (constantFontSize) {
			labelFontSize = fontSize;
			// Set the position of the label so that it is centered over the ellipse
			xPos = (int) Math.round(xPos + width/2 - 0.1*labelFontSize*cluster.getLabel().length());
			yPos = (int) Math.round(yPos - 8.3*labelFontSize);
			fontSizeArgument = String.valueOf((int) Math.round(labelFontSize*7*zoom));
		} else {
			labelFontSize = (int) Math.round(2.5*Math.pow(cluster.getSize(), 0.4));
			// Set the position of the label so that it is centered over the ellipse
			xPos = (int) Math.round(xPos + width/2 - 1.3*labelFontSize*labelText.length());
			yPos = (int) Math.round(yPos - 11.3*labelFontSize);
			fontSizeArgument = String.valueOf((int) Math.round(labelFontSize*11*zoom));
		}
		
		// Create and draw the label
		HashMap<String, String> arguments = new HashMap<String,String>();
		arguments.put("x", String.valueOf(xPos));
		arguments.put("y", String.valueOf(yPos));
		arguments.put("zoom", String.valueOf(zoom));
		arguments.put("canvas", "foreground");
		arguments.put("fontSize", fontSizeArgument);
		TextAnnotation textAnnotation = textFactory.createAnnotation(TextAnnotation.class, view, arguments);
		textAnnotation.setText(labelText);
		cluster.setTextAnnotation(textAnnotation);
		
		annotationManager.addAnnotation(textAnnotation);
	}
	
	public static void drawCluster(Cluster cluster, CyNetworkView view, 
			AnnotationFactory<ShapeAnnotation> shapeFactory, AnnotationFactory<TextAnnotation> textFactory, 
			AnnotationManager annotationManager, boolean constantFontSize, int fontSize, boolean showEllipses,
			int ellipseBorderWidth, int ellipseOpacity) {
		drawEllipse(cluster, view, shapeFactory, annotationManager, showEllipses, ellipseBorderWidth, ellipseOpacity);
		drawTextLabel(cluster, view, textFactory, annotationManager, constantFontSize, fontSize);
	}
	
	@SuppressWarnings("unchecked")
	public static void updateClusterLabel(Cluster cluster, CyNetwork network, String annotationSetName, CyTable clusterSetTable, String nameColumnName) {
		if (cluster.getSize() == 1) {
			String oldLabel = cluster.getLabel();
			String newLabel = network.getRow(cluster.getNodesToCoordinates().keySet().iterator().next()).
					get(nameColumnName, String.class);
			if (!newLabel.equals(oldLabel)) {
				cluster.setLabel(newLabel);
			}
		} else {
			// Look up the WordCloud info of this cluster in its table
			CyRow clusterRow = clusterSetTable.getRow(cluster.getCloudName());
			// Get each piece of the WordCloud info
			List<String> wordList = clusterRow.get("WC_Word", List.class);
			List<String> sizeList = clusterRow.get("WC_FontSize", List.class);
			List<String> clusterList = clusterRow.get("WC_Cluster", List.class);
			List<String> numberList = clusterRow.get("WC_Number", List.class);
			ArrayList<WordInfo> wordInfos = new ArrayList<WordInfo>();
			for (int i=0; i < wordList.size(); i++) {
				wordInfos.add(new WordInfo(wordList.get(i), 
										Integer.parseInt(sizeList.get(i)),
										Integer.parseInt(clusterList.get(i)),
										Integer.parseInt(numberList.get(i))));
			}
			// Only update the labels if the wordCloud has changed
			if (wordInfos.size() != cluster.getWordInfos().size()) {
				// WordCloud table entry for this cluster has changed
				cluster.setWordInfos(wordInfos);
				cluster.setLabel(makeLabel(wordInfos));
			} else {
				for (int infoIndex = 0; infoIndex < cluster.getWordInfos().size(); infoIndex++) {
					if (!wordInfos.get(infoIndex).equals(cluster.getWordInfos().get(infoIndex))) {
						// WordCloud table entry for this cluster has changed
						cluster.setWordInfos(wordInfos);
						cluster.setLabel(makeLabel(wordInfos));
						return;
					}
				}
			}
		}
	}
	
	public static String makeLabel(ArrayList<WordInfo> wordInfos) {
		// Work with a copy so as to not mess up the order for comparisons
		ArrayList<WordInfo> wordInfosCopy = new ArrayList<WordInfo>();
		for (WordInfo wordInfo : wordInfos) {
			wordInfosCopy.add(wordInfo.clone());
		}
		Collections.sort(wordInfosCopy); // Sorts by size descending
		// Gets the biggest word in the cloud
		WordInfo biggestWord = wordInfosCopy.get(0);
		ArrayList<WordInfo> label = new ArrayList<WordInfo>();
		label.add(biggestWord);
		double[] nextWordSizeThresholds = {0.3, 0.8, 0.9};
		int numWords = 1;
		WordInfo nextWord = biggestWord;
		wordInfosCopy.remove(0);
		while (numWords < 4 && wordInfosCopy.size() > 0) {
			for (WordInfo word : wordInfosCopy) {
				for (WordInfo wordInLabel : label) {
					if (word.getCluster() == wordInLabel.getCluster()) {
						word.setSize(word.getSize() + 10);						
					}
				}
			}
			Collections.sort(wordInfosCopy); // Sizes have changed, resort
			double wordSizeThreshold = nextWord.getSize()*nextWordSizeThresholds[numWords - 1];
			nextWord = wordInfosCopy.get(0);
			wordInfosCopy.remove(0);
			if (nextWord.getSize() > wordSizeThreshold) {
				label.add(nextWord);
				numWords++;
			} else {
				break;
			}
		}
		// Sort first by size, then by WordCloud 'number', tries to preserve original word order
		Collections.sort(label, WordInfoNumberComparator.getInstance());
		// Create a string label from the word infos
		return join(label, " ");
	}

	// Connects strings together, separated by separator
	private static String join(List<WordInfo> stringList, String separator) {
		String joined = "";
		for (int index = 0; index < stringList.size(); index++) {
			if (index == 0) {
				joined += stringList.get(index).getWord();
			} else {
				joined += separator + stringList.get(index).getWord();
			}
		}
		return joined;
	}
	
	public static void updateFontSizes(Integer fontSize, boolean constantFontSize) {
		// Set font size to fontSize
		for (CyNetworkView view : 
			AutoAnnotationManager.getInstance().getNetworkViewToAutoAnnotationParameters().keySet()) {
			AutoAnnotationParameters params = AutoAnnotationManager.getInstance().getNetworkViewToAutoAnnotationParameters().get(view);
			for (AnnotationSet annotationSet : params.getAnnotationSets().values()) {
				for (Cluster cluster : annotationSet.getClusterMap().values()) {
					cluster.eraseText();
					AutoAnnotationManager autoAnnotationManager = AutoAnnotationManager.getInstance();
					AnnotationFactory<TextAnnotation> textFactory = autoAnnotationManager.getTextFactory();
					AnnotationManager annotationManager = autoAnnotationManager.getAnnotationManager();
					AutoAnnotationUtils.drawTextLabel(cluster, view, textFactory, annotationManager, constantFontSize, fontSize);
					if (!annotationSet.isSelected()) {
						cluster.eraseText();
					}
				}
			}
		}
	}
}
