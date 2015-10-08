package com.andy.trackme.entities;


public class Seance {
	private int id;
	private int duree;
	private int distance;
	private String vitesse;
	private int calories;
	private String dateSeance;
	
	public Seance(){
		super();
	}


	public Seance(int id, int duree, int distance, String vitesse, int calories , String dateSeance) {
		super();
		this.id = id;
		this.duree = duree;
		this.distance = distance;
		this.vitesse = vitesse;
		this.calories = calories;
		this.dateSeance = dateSeance;
	}
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getDuree() {
		return duree;
	}


	public void setDuree(int duree) {
		this.duree = duree;
	}


	public int getDistance() {
		return distance;
	}


	public void setDistance(int distance) {
		this.distance = distance;
	}


	public String getVitesse() {
		return vitesse;
	}


	public void setVitesse(String vitesse) {
		this.vitesse = vitesse;
	}


	public int getCalories() {
		return calories;
	}


	public void setCalories(int calories) {
		this.calories = calories;
	}


	public String getDateSeance() {
		return dateSeance;
	}


	public void setDateSeance(String dateSeance) {
		this.dateSeance = dateSeance;
	}
	
	
}
