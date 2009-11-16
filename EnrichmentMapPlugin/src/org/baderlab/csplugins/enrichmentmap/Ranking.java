/**
 **                       EnrichmentMap Cytoscape Plugin
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

package org.baderlab.csplugins.enrichmentmap;

/**
 * Created by
 * User: risserlin
 * Date: May 1, 2009
 * Time: 10:42:44 AM
 * <p>
 * Object representing the rank of an individual gene or protein
 */
public class Ranking {

    //gene/protein name
    private String Name;

    //score given in the ranking file
    private Double Score;

    //rank computed based on sorting the given scores.  (if the score given are actually ranks then
    //the two will be synonmous)
    private Integer Rank;

    /**
     * Class constructor
     *
     * @param name - gene/protein name
     * @param score - score supplied by user
     */
    public Ranking(String name, Double score) {
        this.Name = name;
        this.Score = score;
    }

    /**
     * Class constructor
     *
     * @param name - gene/protein name
     * @param score - score supplied by user
     * @param rank - rank computed based on scores.
     */
    public Ranking(String name, Double score, Integer rank) {
        this.Name = name;
        this.Score = score;
        this.Rank = rank;
    }

    /**
     * Class constructor - for reconstruction from session saved rank files.
     *
     * @param tokens - tokenized version of rank file as saved in a session file
     */
    public Ranking(String[] tokens){

        //make sure that there are 4 tokens (hashkey, name, score, rank)
        if(tokens.length != 4)
            return;
        
        this.Name = tokens[1];
        this.Score = Double.parseDouble(tokens[2]);
        this.Rank = Integer.parseInt(tokens[3]);

    }

    //Getters and Setters

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Double getScore() {
        return Score;
    }

    public void setScore(Double score) {
        Score = score;
    }

    public Integer getRank() {
        return Rank;
    }

    public void setRank(Integer rank) {
        this.Rank = rank;
    }

    public String toString(){
        return Name + "\t" + Score + "\t" + Rank ;
    }
}
