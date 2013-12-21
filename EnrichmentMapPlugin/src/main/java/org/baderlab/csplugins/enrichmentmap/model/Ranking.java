package org.baderlab.csplugins.enrichmentmap.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;


public class Ranking {
	
	//constants for names of Ranking set
	public final static String GSEARanking = "GSEARanking";
	public final static String RankingLOADED = "RankingLOADED";
	
	//Set of Ranks
	//key - gene hash key
	//value - Rank
	private HashMap<Integer, Rank> ranking;
	
	//hashes for easy conversion between geneid and rank
	private HashMap<Integer, Integer> gene2rank;
	private HashMap<Integer, Integer> rank2gene;
	
	//hash for easy conversion between geneid and score
	private HashMap<Integer, Double> gene2score;
	
	//array for storing scores of all genes in map
	private double[] scores;
	
	//File associated with this ranking set
    private String filename;
	
	public Ranking(){
		ranking = new HashMap<Integer,Rank>();
		gene2rank = new HashMap<Integer,Integer>();
		rank2gene = new HashMap<Integer,Integer>();
		gene2score = new HashMap<Integer, Double>();
	}

	public HashMap<Integer, Rank> getRanking() {
		return ranking;
	}

	public void setRanking(HashMap<Integer, Rank> ranking) {
		this.ranking = ranking;
		for(Iterator<Integer> i = ranking.keySet().iterator();i.hasNext();){
			Integer cur = (Integer)i.next();
			gene2rank.put(cur,ranking.get(cur).getRank());
			rank2gene.put(ranking.get(cur).getRank(), cur);
        }
	}

	public HashMap<Integer, Integer> getGene2rank() {
		return gene2rank;
	}

	public void setGene2rank(HashMap<Integer, Integer> gene2rank) {
		this.gene2rank = gene2rank;
		if(this.rank2gene == null || this.rank2gene.isEmpty()){
			for(Iterator<Integer> i = gene2rank.keySet().iterator();i.hasNext();){
				Integer cur = (Integer)i.next();
				rank2gene.put(gene2rank.get(cur), cur);
				ranking.put(cur, new Rank(cur.toString(),0.0,gene2rank.get(cur)));
	        }
		}
	}

	public HashMap<Integer, Integer> getRank2gene() {
		return rank2gene;
	}

	public void setRank2gene(HashMap<Integer, Integer> rank2gene) {
		this.rank2gene = rank2gene;
		if(this.gene2rank == null || this.gene2rank.isEmpty()){
			for(Iterator<Integer> i = rank2gene.keySet().iterator();i.hasNext();){
				Integer cur = (Integer)i.next();
				gene2rank.put(rank2gene.get(cur), cur);
				ranking.put(rank2gene.get(cur), new Rank(rank2gene.get(cur).toString(),0.0,cur));
	        }
		}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void addRank(Integer gene, Rank rank){
		
		ranking.put(gene,  rank);
		
		gene2rank.put(gene, rank.getRank());
		rank2gene.put(rank.getRank(), gene);
	}
	
	public int getMaxRank(){
		return Collections.max(rank2gene.keySet());
				
	}
	
	public String toString(){
		StringBuffer paramVariables = new StringBuffer();
		paramVariables.append(filename + "%fileName\t" + filename + "\n");
		
		return paramVariables.toString();
		
		
	}

	/**
	 * Get gene2score hash
	 * @param null
	 * @return HashMap gene2score
	 */
	public HashMap<Integer, Double> getGene2Score() {
		return this.gene2score;
	}

	/**
	 * Set gene2score hash
	 * @param HashMap gene2score
	 * @return null
	 */
	public void setGene2Score(HashMap<Integer, Double> gene2score) {
		this.gene2score = gene2score;
	}

	/**
	 * Get scores array
	 * @param null
	 * @return double[] scores
	 */
	public double[] getScores() {
		return this.scores;
	}

	/**
	 * Set scores array
	 * @param double[] scores
	 * @return null
	 */
	public void setScores(double[] scores) {
		this.scores = scores;
	}
}