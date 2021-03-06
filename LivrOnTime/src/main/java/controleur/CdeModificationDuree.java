package controleur;

import java.util.Date;

import LivrOnTime.Main;
import modele.Livraison;
import modele.Plan;
import modele.Tournee;

/**
 * La classe CdeModificationDuree implemente l interface Commande pour la
 * modification de la duree d une livraison dans la tournee. Elle stocke la
 * livraison modifie et sa duree.
 * 
 * @author Matthieu
 *
 */
public class CdeModificationDuree implements Commande {
	private Plan plan;
	private int duree;
	private Date debutPH; // debut plage horaire
	private Date finPH; // fin plage horaire
	private Tournee tournee;
	private Livraison livraison;
	private int dureeA;
	private Date DPH_A, FPH_A;

	/**
	 * Constructeur de la classe CdeModificationDuree
	 * 
	 * @param plan
	 * @param tournee
	 * @param livraison
	 * @param dureeA
	 * @param DPH_A
	 * @param FPH_A
	 */
	public CdeModificationDuree(Plan plan, Tournee tournee, Livraison livraison, int dureeA, Date DPH_A, Date FPH_A) {
		this.plan = plan;
		this.duree = livraison.getDuree();
		this.debutPH = livraison.getDebutPlageHoraire();
		this.finPH = livraison.getFinPlageHoraire();
		this.tournee = tournee;
		this.livraison = livraison;
		this.dureeA = dureeA;
		this.DPH_A = DPH_A;
		this.FPH_A = FPH_A;
	}

	@Override
	public void redoCde() {
		int i = Main.aController.getDemandeLiv().getLivraisons().indexOf(livraison);
		tournee.ModifierLivraison(plan, livraison, duree);
		tournee.ModifierLivraison(plan, livraison, debutPH, finPH);
		Main.aController.getDemandeLiv().getLivraisons().set(i, livraison);

	}

	@Override
	public void undoCde() {
		int i = Main.aController.getDemandeLiv().getLivraisons().indexOf(livraison);
		tournee.ModifierLivraison(plan, livraison, DPH_A, FPH_A);
		tournee.ModifierLivraison(plan, livraison, dureeA);
		Main.aController.getDemandeLiv().getLivraisons().set(i, livraison);

	}

}
