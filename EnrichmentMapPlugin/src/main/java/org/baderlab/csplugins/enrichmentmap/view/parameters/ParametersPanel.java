/**
d **                       EnrichmentMap Cytoscape Plugin
 **
 ** Copyright (c) 2008-2009 Bader Lab, Donnelly Centre for Cellular and Biomolecular 
 ** Research, University of Toronto
 **
 ** Contact: http://www.baderlab.org
 **
 ** Code written by: Ruth Isserlin
 ** Authors: Daniele Merico, Ruth Isserlin, Oliver Stueker, Gary D. Bader
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** University of Toronto
 ** has no obligations to provide maintenance, support, updates, 
 ** enhancements or modifications.  In no event shall the
 ** University of Toronto
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** University of Toronto
 ** has been advised of the possibility of such damage.  
 ** See the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 **/

// $Id$
// $LastChangedDate$
// $LastChangedRevision$
// $LastChangedBy$
// $HeadURL$

package org.baderlab.csplugins.enrichmentmap.view.parameters;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static org.baderlab.csplugins.enrichmentmap.view.util.SwingUtil.makeSmall;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.baderlab.csplugins.enrichmentmap.model.EMCreationParameters;
import org.baderlab.csplugins.enrichmentmap.model.EMCreationParameters.SimilarityMetric;
import org.baderlab.csplugins.enrichmentmap.model.EnrichmentMap;
import org.baderlab.csplugins.enrichmentmap.model.EnrichmentMapManager;
import org.baderlab.csplugins.enrichmentmap.model.LegacySupport;
import org.baderlab.csplugins.enrichmentmap.style.ColumnDescriptor;
import org.baderlab.csplugins.enrichmentmap.style.EMStyleBuilder.Columns;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.util.swing.BasicCollapsiblePanel;
import org.cytoscape.util.swing.LookAndFeelUtil;
import org.cytoscape.util.swing.OpenBrowser;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Right hand information Panel containing files uploaded and legends
 */
@Singleton
@SuppressWarnings("serial")
public class ParametersPanel extends JPanel {

	@Inject private OpenBrowser browser;
	@Inject private CyApplicationManager applicationManager;
	@Inject private EnrichmentMapManager emManager;
	
//	@Inject private @Nodes HeatMapPanel nodesOverlapPanel;
//	@Inject private @Edges HeatMapPanel edgesOverlapPanel;
//	
	private JCheckBox heatmapAutofocusCheckbox;
	
	private EnrichmentMap map;

	public ParametersPanel() {
		setLayout(new BorderLayout());
	}
	
	/**
	 * Update parameters panel based on given enrichment map parameters
	 */
	 void update(EnrichmentMap map) {
		this.map = map;
		EMCreationParameters params = map != null ? map.getParams() : null;

		removeAll();
		
		if (params == null) {
			JLabel infoLabel = new JLabel("No EnrichmentMap View selected");
			infoLabel.setEnabled(false);
			infoLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
			infoLabel.setHorizontalAlignment(JLabel.CENTER);
			infoLabel.setVerticalAlignment(JLabel.CENTER);
			infoLabel.setBorder(new EmptyBorder(120, 40, 120, 40));
			
			add(infoLabel, BorderLayout.CENTER);
			revalidate();
			
			return;
		}

		JPanel legendsPanel = createLegendPanel(params, map);

		JButton openReport1Button = new JButton("Open GSEA report Dataset 1");
		openReport1Button.setVisible(false);
		
		JButton openReport2Button = new JButton("Open GSEA-report Dataset 2");
		openReport2Button.setVisible(false);
		
		// MKTODO fix this... or aren't we getting rid of this panel in favor of the new ControlPanel?
//		if (params.getMethod() == Method.GSEA) {
//			final String reportFileDataset1 = resolveGseaReportFilePath(map, 1);
//			final String reportFileDataset2 = resolveGseaReportFilePath(map, 2);
//
//			if (!(reportFileDataset1 == null)) {
//				openReport1Button.setVisible(true);
//				openReport1Button.addActionListener((ActionEvent evt) -> {
//					browser.openURL("file://" + reportFileDataset1);
//				});
//				// Disable button if we can't read the file:
//				if (!new File(reportFileDataset1).canRead()) {
//					openReport1Button.setEnabled(false);
//					openReport1Button.setToolTipText("Report file not found: " + reportFileDataset1);
//				}
//			}
//			
//			if (!(reportFileDataset2 == null)) {
//				openReport2Button.setVisible(true);
//				openReport2Button.addActionListener((ActionEvent evt) -> {
//					browser.openURL("file://" + reportFileDataset2);
//				});
//				// Disable button if we can't read the file:
//				if (!(new File(reportFileDataset2)).canRead()) {
//					openReport2Button.setEnabled(false);
//					openReport2Button.setToolTipText("Report file not found: " + reportFileDataset2);
//				}
//			}
//		}
		
		makeSmall(openReport1Button, openReport2Button);
		
		BasicCollapsiblePanel currentParamsPanel = createCurrentParamsPanel(params);
//		BasicCollapsiblePanel preferencesPanel = createPreferencesPanel(map);

		JPanel mainPanel = new JPanel();
		final GroupLayout layout = new GroupLayout(mainPanel);
		mainPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(!LookAndFeelUtil.isAquaLAF());
		
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER, true)
				.addComponent(legendsPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(openReport1Button, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
				.addComponent(openReport2Button, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
				.addComponent(currentParamsPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
//				.addComponent(preferencesPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(legendsPanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
				.addComponent(openReport1Button, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
				.addComponent(openReport2Button, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
				.addComponent(currentParamsPanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//				.addComponent(preferencesPanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
		);
		
		JScrollPane scrollPane = new JScrollPane(mainPanel);
		
		add(scrollPane, BorderLayout.CENTER);
		revalidate();
	}

//	private BasicCollapsiblePanel createPreferencesPanel(EnrichmentMap map) {
//		// Begin of Code to toggle "Disable Heatmap autofocus"
//		heatmapAutofocusCheckbox = new JCheckBox(new AbstractAction("Heatmap autofocus") {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// Do this in the GUI Event Dispatch thread...
//				SwingUtilities.invokeLater(() -> {
//					// toggle state of overrideHeatmapRevalidation
//					if (emManager.isDisableHeatmapAutofocus()) {
//						emManager.setDisableHeatmapAutofocus(false);
//					} else {
//						emManager.setDisableHeatmapAutofocus(true);
//					}
//					heatmapAutofocusCheckbox.setSelected(!emManager.isDisableHeatmapAutofocus());
//				});
//			}
//		});
//		heatmapAutofocusCheckbox.setSelected(!emManager.isDisableHeatmapAutofocus());
//
//		JRadioButton hc = new JRadioButton(Sort.CLUSTER.display);
//		hc.setActionCommand(Sort.CLUSTER.name());
//		hc.setSelected(false);
//
//		JRadioButton nosort = new JRadioButton(Sort.NONE.display);
//		nosort.setActionCommand(Sort.NONE.name());
//		nosort.setSelected(false);
//
//		JRadioButton ranks = new JRadioButton(Sort.RANK.display);
//		ranks.setActionCommand(Sort.RANK.name());
//		ranks.setSelected(false);
//
//		JRadioButton columns = new JRadioButton(Sort.COLUMN.display);
//		columns.setActionCommand(Sort.COLUMN.name());
//		columns.setSelected(false);
//		
//		HeatMapParameters hmParams = emManager.getHeatMapParameters(map.getNetworkID());
//		
//		if(hmParams == null) {
//			hc.setEnabled(false);
//			nosort.setEnabled(false);
//			ranks.setEnabled(false);
//			columns.setEnabled(false);
//		} 
//		else {
//			if (hmParams.getSort() == Sort.CLUSTER)
//				hc.setSelected(true);
//			if (hmParams.getSort() == Sort.NONE)
//				nosort.setSelected(true);
//			if (hmParams.getSort() == Sort.RANK)
//				ranks.setSelected(true);
//			if (hmParams.getSort() == Sort.COLUMN)
//				columns.setSelected(true);
//			
//			hc.addActionListener(e -> hmParams.setDefaultSort(Sort.CLUSTER));
//			nosort.addActionListener(e -> hmParams.setDefaultSort(Sort.NONE));
//			ranks.addActionListener(e -> hmParams.setDefaultSort(Sort.RANK));
//			columns.addActionListener(e -> hmParams.setDefaultSort(Sort.COLUMN));
//		}
//
//		ButtonGroup sortingMethodsGroup = new ButtonGroup();
//		sortingMethodsGroup.add(hc);
//		sortingMethodsGroup.add(nosort);
//		sortingMethodsGroup.add(ranks);
//		sortingMethodsGroup.add(columns);
//
//		JLabel defSortingOrderLabel = new JLabel("Default Sorting Order:");
//
//		JRadioButton pearson = new JRadioButton(DistanceMetric.PEARSON_CORRELATION.display);
//		pearson.setActionCommand(DistanceMetric.PEARSON_CORRELATION.name());
//		pearson.setSelected(false);
//
//		JRadioButton cosine = new JRadioButton(DistanceMetric.COSINE.display);
//		cosine.setActionCommand(DistanceMetric.COSINE.name());
//		cosine.setSelected(false);
//
//		JRadioButton euclidean = new JRadioButton(DistanceMetric.EUCLIDEAN.display);
//		euclidean.setActionCommand(DistanceMetric.EUCLIDEAN.name());
//		euclidean.setSelected(false);
//
//		if(hmParams == null) {
//			pearson.setEnabled(false);
//			cosine.setEnabled(false);
//			euclidean.setEnabled(false);
//		}
//		else  {
//			if (hmParams.getDistanceMetric() == DistanceMetric.PEARSON_CORRELATION)
//				pearson.setSelected(true);
//			if (hmParams.getDistanceMetric() == DistanceMetric.COSINE)
//				cosine.setSelected(true);
//			if (hmParams.getDistanceMetric() == DistanceMetric.EUCLIDEAN)
//				euclidean.setSelected(true);
//			
//			pearson.addActionListener(e-> {
//				hmParams.setDistanceMetric(DistanceMetric.PEARSON_CORRELATION);
//				edgesOverlapPanel.updatePanel(map);
//				nodesOverlapPanel.updatePanel(map);
//			});
//			cosine.addActionListener(e-> {
//				hmParams.setDistanceMetric(DistanceMetric.COSINE);
//				edgesOverlapPanel.updatePanel(map);
//				nodesOverlapPanel.updatePanel(map);
//			});
//			euclidean.addActionListener(e-> {
//				hmParams.setDistanceMetric(DistanceMetric.EUCLIDEAN);
//				edgesOverlapPanel.updatePanel(map);
//				nodesOverlapPanel.updatePanel(map);
//			});
//		}
//
//		ButtonGroup distanceMetricGroup = new ButtonGroup();
//		distanceMetricGroup.add(pearson);
//		distanceMetricGroup.add(cosine);
//		distanceMetricGroup.add(euclidean);
//
//		JLabel defDistanceMetricLabel = new JLabel("Default Distance Metric:");
//
//		makeSmall(heatmapAutofocusCheckbox);
//		makeSmall(defSortingOrderLabel, hc, ranks, columns, nosort);
//		makeSmall(defDistanceMetricLabel, pearson, cosine, euclidean);
//		
//		BasicCollapsiblePanel panel = new BasicCollapsiblePanel("Advanced Preferences");
//		panel.setCollapsed(true);
//		
//		final GroupLayout layout = new GroupLayout(panel.getContentPane());
//		panel.getContentPane().setLayout(layout);
//		layout.setAutoCreateContainerGaps(true);
//		layout.setAutoCreateGaps(!LookAndFeelUtil.isAquaLAF());
//		
//		layout.setHorizontalGroup(layout.createSequentialGroup()
//				.addGroup(layout.createParallelGroup(Alignment.TRAILING, true)
//						.addComponent(defSortingOrderLabel)
//						.addComponent(defDistanceMetricLabel)
//				)
//				.addPreferredGap(ComponentPlacement.RELATED)
//				.addGroup(layout.createParallelGroup(Alignment.LEADING, true)
//						.addComponent(heatmapAutofocusCheckbox, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//						.addComponent(hc, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//						.addComponent(ranks, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//						.addComponent(columns, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//						.addComponent(nosort, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//						.addComponent(pearson, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//						.addComponent(cosine, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//						.addComponent(euclidean, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//				)
//		);
//		layout.setVerticalGroup(layout.createSequentialGroup()
//				.addComponent(heatmapAutofocusCheckbox, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//				.addPreferredGap(ComponentPlacement.RELATED)
//				.addGroup(layout.createParallelGroup(Alignment.CENTER, false)
//						.addComponent(defSortingOrderLabel)
//						.addComponent(hc, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//				)
//				.addComponent(ranks, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//				.addComponent(columns, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//				.addComponent(nosort, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//				.addPreferredGap(ComponentPlacement.RELATED)
//				.addGroup(layout.createParallelGroup(Alignment.CENTER, false)
//						.addComponent(defDistanceMetricLabel)
//						.addComponent(pearson, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//				)
//				.addComponent(cosine, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//				.addComponent(euclidean, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//		);
//		
//		if (LookAndFeelUtil.isAquaLAF())
//			panel.setOpaque(false);
//		
//		return panel;
//	}

	private BasicCollapsiblePanel createCurrentParamsPanel(EMCreationParameters params) {
		// information about the current analysis
		JTextPane runInfoPane = new JTextPane();
		runInfoPane.setEditable(false);
		
		runInfoPane.setContentType("text/html");
		runInfoPane.setText(getRunInfo(params));
		
		JScrollPane scrollPane = new JScrollPane(runInfoPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		Dimension d = new Dimension(300, 100);
		scrollPane.setPreferredSize(d);
		
		makeSmall(runInfoPane, scrollPane);

		// put Parameters into Collapsible Panel
		BasicCollapsiblePanel panel = new BasicCollapsiblePanel("Current Parameters");
		panel.setCollapsed(true);
		panel.getContentPane().setLayout(new BorderLayout());
		panel.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		if (LookAndFeelUtil.isAquaLAF())
			panel.setOpaque(false);
		
		return panel;
	}

	/**
	 * Get the files and parameters corresponding to the current enrichment map
	 */
	private String getRunInfo(EMCreationParameters params) {
		final String INDENT = "&nbsp;&nbsp;&nbsp;&nbsp;";
		
		String s = "<html><font size='-2' face='sans-serif'>";

		s = s + "<b>P-value Cut-off:</b> " + params.getPvalue() + "<br>";
		s = s + "<b>FDR Q-value Cut-off:</b> " + params.getQvalue() + "<br>";

		if (params.getSimilarityMetric() == SimilarityMetric.JACCARD) {
			s = s + "<b>Jaccard Cut-off:</b> " + params.getSimilarityCutoff() + "<br>";
			s = s + "<b>Test used:</b> Jaccard Index<br>";
		} else if (params.getSimilarityMetric() == SimilarityMetric.OVERLAP) {
			s = s + "<b>Overlap Cut-off:</b> " + params.getSimilarityCutoff() + "<br>";
			s = s + "<b>Test Used:</b> Overlap Index<br>";
		} else if (params.getSimilarityMetric() == SimilarityMetric.COMBINED) {
			s = s + "<b>Jaccard Overlap Combined Cut-off:</b> " + params.getSimilarityCutoff() + "<br>";
			s = s + "<b>Test Used:</b> Jaccard Overlap Combined Index (k constant = " + params.getCombinedConstant() + ")<br>";
		}
		
		if (map.getDataSet(LegacySupport.DATASET1) != null) {
			s = s + "<b>Genesets File: </b><br>"
					+ INDENT + shortenPathname(map.getDataSet(LegacySupport.DATASET1).getDataSetFiles().getGMTFileName()) + "<br>";
		
			String enrichmentFileName1 = map.getDataSet(LegacySupport.DATASET1).getDataSetFiles().getEnrichmentFileName1();
			String enrichmentFileName2 = map.getDataSet(LegacySupport.DATASET1).getDataSetFiles().getEnrichmentFileName2();
		
			if (enrichmentFileName1 != null || enrichmentFileName2 != null) {
				s = s + "<b>Dataset 1 Data Files: </b><br>";
				
				if (enrichmentFileName1 != null)
					s = s + INDENT + shortenPathname(enrichmentFileName1) + "<br>";
				
				if (enrichmentFileName2 != null)
					s = s + INDENT + shortenPathname(enrichmentFileName2) + "<br>";
			}
			
			if (LegacySupport.isLegacyTwoDatasets(map)) {
				enrichmentFileName1 = map.getDataSet(LegacySupport.DATASET2).getDataSetFiles().getEnrichmentFileName1();
				enrichmentFileName2 = map.getDataSet(LegacySupport.DATASET2).getDataSetFiles().getEnrichmentFileName2();
				
				if (enrichmentFileName1 != null || enrichmentFileName2 != null) {
					s = s + "<b>Dataset 2 Data Files: </b><br>";
					
					if (enrichmentFileName1 != null)
						s = s + INDENT + shortenPathname(enrichmentFileName1) + "<br>";
					
					if (enrichmentFileName2 != null)
						s = s + INDENT + shortenPathname(enrichmentFileName2) + "<br>";
				}
			}
			
			s = s + "<b>Data file:</b>" + shortenPathname(map.getDataSet(LegacySupport.DATASET1).getDataSetFiles().getExpressionFileName())
			+ "<br>";
			// TODO:fix second dataset viewing.
			/*
			 * if(params.isData2() && params.getEM().getExpression(LegacySupport.DATASET2) != null)
			 * runInfoText = runInfoText + "<b>Data file 2:</b>" + shortenPathname(params.getExpressionFileName2()) + "<br>";
			 */
			
			if (map.getDataSet(LegacySupport.DATASET1) != null
					&& map.getDataSet(LegacySupport.DATASET1).getDataSetFiles().getGseaHtmlReportFile() != null) {
				s = s + "<b>GSEA Report 1:</b>"
						+ shortenPathname(map.getDataSet(LegacySupport.DATASET1).getDataSetFiles().getGseaHtmlReportFile()) + "<br>";
			}
			if (map.getDataSet(LegacySupport.DATASET2) != null
					&& map.getDataSet(LegacySupport.DATASET2).getDataSetFiles().getGseaHtmlReportFile() != null) {
				s = s + "<b>GSEA Report 2:</b>"
						+ shortenPathname(map.getDataSet(LegacySupport.DATASET2).getDataSetFiles().getGseaHtmlReportFile()) + "<br>";
			}
		}

		s = s + "</font></html>";
		
		return s;
	}

	/**
	 * Create the legend - contains the enrichment score colour mapper and diagram where the colours are
	 */
	private JPanel createLegendPanel(EMCreationParameters params, EnrichmentMap map) {
		JPanel panel = new JPanel();
		
		if (LookAndFeelUtil.isAquaLAF())
			panel.setOpaque(false);
		
		final GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(!LookAndFeelUtil.isAquaLAF());
		
		ParallelGroup hGroup = layout.createParallelGroup(Alignment.CENTER, false);
		SequentialGroup vGroup = layout.createSequentialGroup();
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGap(0, 0, Short.MAX_VALUE)
				.addGroup(hGroup)
				.addGap(0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(vGroup);
		
		// represent the node color as an png/gif instead of using java to generate the representation
		URL nodeIconURL = this.getClass().getResource("node_color_small.png");
		
		if (nodeIconURL != null) {
			ImageIcon nodeIcon;
			nodeIcon = new ImageIcon(nodeIconURL);
			JLabel nodeColorLabel = new JLabel(nodeIcon);
			
			hGroup.addComponent(nodeColorLabel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE);
			vGroup.addComponent(nodeColorLabel).addPreferredGap(ComponentPlacement.RELATED);
		}
		
		// TODO These colors are no longer used
		/* See http://colorbrewer2.org/#type=diverging&scheme=RdBu&n=9 */
		final Color MAX_PHENOTYPE_1 = new Color(178, 24, 43);
		final Color LIGHTER_PHENOTYPE_1 = new Color(214, 96, 77);
		final Color LIGHTEST_PHENOTYPE_1 = new Color(244, 165, 130);
		final Color OVER_COLOR = new Color(247, 247, 247);
		final Color MAX_PHENOTYPE_2 = new Color(33, 102, 172);
		final Color LIGHTER_PHENOTYPE_2 = new Color(67, 147, 195);
		final Color LIGHTEST_PHENOTYPE_2 = new Color(146, 197, 222);
		
		if (map.getDataSet(LegacySupport.DATASET1) != null) {
			LegendPanel nodeLegendPanel = new LegendPanel(
					MAX_PHENOTYPE_1,
					MAX_PHENOTYPE_2,
					map.getDataSet(LegacySupport.DATASET1).getEnrichments().getPhenotype1(),
					map.getDataSet(LegacySupport.DATASET1).getEnrichments().getPhenotype2());
			nodeLegendPanel.setToolTipText("Phenotype * (1-P_value)");
		
			hGroup.addComponent(nodeLegendPanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE);
			vGroup.addComponent(nodeLegendPanel).addPreferredGap(ComponentPlacement.UNRELATED);
		}

		// If there are two datasets then we need to define the node border legend as well.
		if (LegacySupport.isLegacyTwoDatasets(map)) {
			// represent the node border color as an png/gif instead of using java to generate the representation
			URL nodeborderIconURL = this.getClass().getResource("node_border_color_small.png");

			if (nodeborderIconURL != null) {
				ImageIcon nodeBorderIcon = new ImageIcon(nodeborderIconURL);
				JLabel nodeBorderColorLabel = new JLabel(nodeBorderIcon);
				
				hGroup.addComponent(nodeBorderColorLabel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE);
				vGroup.addComponent(nodeBorderColorLabel).addPreferredGap(ComponentPlacement.RELATED);
			}

			LegendPanel nodeLegendPanel2 = new LegendPanel(
					MAX_PHENOTYPE_1,
					MAX_PHENOTYPE_2,
					map.getDataSet(LegacySupport.DATASET2).getEnrichments().getPhenotype1(),
					map.getDataSet(LegacySupport.DATASET2).getEnrichments().getPhenotype2());
			nodeLegendPanel2.setToolTipText("Phenotype * (1-P_value)");
			
			hGroup.addComponent(nodeLegendPanel2, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE);
			vGroup.addComponent(nodeLegendPanel2).addPreferredGap(ComponentPlacement.UNRELATED);
		}

		return panel;
	}
	
	/**
	 * Shorten path name to only contain the parent directory
	 */
	private String shortenPathname(String pathname) {
		if (pathname != null) {
			String[] tokens = pathname.split("\\" + File.separator);

			int numTokens = tokens.length;
			final String newPathname;
			
			if (numTokens >= 2)
				newPathname = "..." + File.separator + tokens[numTokens - 2] + File.separator + tokens[numTokens - 1];
			else
				newPathname = pathname;

			return newPathname;
		}
		
		return "";
	}

	private String resolveGseaReportFilePath(EnrichmentMap map, int dataset) {
		String reportFile = null;
		ColumnDescriptor<String> colDescr = null;
		
		if (dataset == 1) {
			if (map.getDataSet(LegacySupport.DATASET1) != null) {
				reportFile = map.getDataSet(LegacySupport.DATASET1).getDataSetFiles().getGseaHtmlReportFile();
				colDescr = Columns.NET_REPORT1_DIR;
			}
		} else {
			if (map.getDataSet(LegacySupport.DATASET2) != null) {
				reportFile = map.getDataSet(LegacySupport.DATASET2).getDataSetFiles().getGseaHtmlReportFile();
				colDescr = Columns.NET_REPORT2_DIR;
			}
		}

		// Try the path that is stored in the params:
		if (reportFile != null && new File(reportFile).canRead()) {
			return reportFile;
		} else if (colDescr != null) {
			// if not, try from Network attributes:
			CyNetwork network = applicationManager.getCurrentNetwork();
			CyTable networkTable = network.getDefaultNetworkTable();
			String tryPath = colDescr.get(networkTable.getRow(network.getSUID()));

			String tryReportFile = tryPath + File.separator + "index.html";
			
			if (new File(tryReportFile).canRead()) {
				return tryReportFile;
			} else { // we found nothing
				if (reportFile == null || reportFile.equalsIgnoreCase("null"))
					return null;
				else
					return reportFile;
			}
		} else {
			return null;
		}
	}
}
