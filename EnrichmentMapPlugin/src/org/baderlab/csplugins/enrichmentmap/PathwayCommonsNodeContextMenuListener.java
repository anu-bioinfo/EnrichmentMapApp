/**
 **                       EnrichmentMap Cytoscape Plugin
 **
 ** Copyright (c) 2008-2009 Bader Lab, Donnelly Centre for Cellular and Biomolecular 
 ** Research, University of Toronto
 **
 ** Contact: http://www.baderlab.org
 **
 ** Code written by: Carl Song
 ** Authors: Carl Song, Daniele Merico, Ruth Isserlin, Oliver Stueker, Gary D. Bader
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

package org.baderlab.csplugins.enrichmentmap;

import giny.model.Node;
import giny.view.NodeView;

import javax.swing.*;

import ding.view.NodeContextMenuListener;

public class PathwayCommonsNodeContextMenuListener implements
		NodeContextMenuListener {
	public PathwayCommonsNodeContextMenuListener() {}
	/**
	 * @param nodeView The clicked NodeView
	 * @param menu popup menu to add the PathwayCommons menu
	 */
	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {
		if (menu == null) {
			menu = new JPopupMenu();
		}
		Node node = nodeView.getNode();
		JMenu pcmenu = new JMenu("Retrieve PathwayCommons Network");
		pcmenu.add(new JMenuItem(new PathwayCommonsQueryAction(node, false, "SIF")));
		pcmenu.add(new JMenuItem(new PathwayCommonsQueryAction(node, true, "BioPAX")));
		menu.add(pcmenu);
		menu.add(new JSeparator());
	}
}