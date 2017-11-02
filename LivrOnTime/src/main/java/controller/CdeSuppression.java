package controller;

import model.Intersection;
import model.Livraison;
import model.Plan;
import model.Tournee;

public class CdeSuppression implements Commande {
	
	private Plan plan;
	private Intersection intersection;
	private int index;
	private Tournee tournee;
	private Livraison livraison;
	
	
	

	public CdeSuppression(Plan plan, Intersection intersection, Tournee tournee,Livraison livraison, int index) {
		super();
		this.plan = plan;
		this.intersection = intersection;
		this.index = index;
		this.tournee = tournee;
		this.livraison=livraison;
	}

	@Override
	public void undoCde() {
		tournee.AjouterLivraison(plan,intersection,livraison,index);
	}

	@Override
	public void redoCde() {
		index =tournee.SupprimerLivraison(plan,intersection,livraison);
	}

}
