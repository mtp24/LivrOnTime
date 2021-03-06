package util.genererFeuilleDeRoute;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import modele.*;

/**
 * Cette classe est une classe utilitaire permettant la creation de la feuille
 * de route de la tournee calculee en format txt
 * 
 * @author Perrine
 *
 */

public class feuilleDeRouteTxt {

	/**
	 * Constructeur par defaut de la classe feuilleDeRouteTxt
	 */
	public feuilleDeRouteTxt() {
	}

	/**
	 * La methode genererFeuilleDeRoute genere le contenu de la future feuille
	 * de route a partir de la tournee calculee precedemment.
	 * 
	 * @param tournee
	 * @return
	 */
	public static String genererFeuilleDeRoute(Tournee tournee) {
		String contenu = "";

		int nbChemin = tournee.getItineraire().size();

		SimpleDateFormat dureeHms = new SimpleDateFormat("HH:mm:ss");
		String heureDep = dureeHms.format(tournee.getHeureDepart());

		Intersection entrepotAd = (tournee.getItineraire()).get(0).getOrigine();

		// Premiere ligne de depart
		contenu = contenu + "Depart de l'entrepot " + ruesDelIntersection(entrepotAd) + " a " + heureDep + "\r\n\r\n";

		// Pour tous les chemins, on recup adress dep adress arr
		// ainsi que heure arrivee au depart et heure de depart apres livraison
		for (int index = 0; index < nbChemin; index++) {
			contenu = contenu + retranscriptionDunChemin(tournee, index);
		}

		return contenu;
	}

	/**
	 * La methode testRueIdentique permet de savoir si 2 troncons consecutifs
	 * font partie de la m�me rue.
	 * 
	 * @param premierT
	 * @param deuxiemeT
	 * @return
	 */
	private static boolean testRueIdentique(Troncon premierT, Troncon deuxiemeT) {
		boolean identique = false;
		if (premierT.getNomRue().equals(deuxiemeT.getNomRue())) {
			identique = true;
		}
		return identique;
	}

	/**
	 * La methode retranscriptionDunChemin permet d obtenir la description de l
	 * itineraire a suivre (tous les troncons) pour aller de l'origine du chemin
	 * a la destination du chemin.
	 * 
	 * @param tourneeEnCours
	 * @param index
	 * @return
	 */
	private static String retranscriptionDunChemin(Tournee tourneeEnCours, int index) {

		Chemin cheminActuel = tourneeEnCours.getItineraire().get(index);

		// temps de passage a l'arrivee du chemin actuel(0 = date d'arrivee 1 =
		// date debut de livraison)
		Date tempsDePassage[] = tourneeEnCours.getTempsPassage()[index];
		Date heureDarriveePDL = tempsDePassage[0];
		Date heureDebutLiv = tempsDePassage[1];

		String contenu = "";
		long idEntrepot = (tourneeEnCours.getItineraire()).get(0).getOrigine().getId();
		int nbTroncons = (cheminActuel.getTroncons().size());
		Intersection destinationCheminActuel = cheminActuel.getDestination();
		Date heureFinCheminActuel = heureDarriveePDL;

		long tempsAttente = 0;
		if (heureDebutLiv != null) {
			tempsAttente = heureDebutLiv.getTime() - heureDarriveePDL.getTime() - 3600000;
		}

		if (nbTroncons == 1) {
			Troncon tronconActuel = cheminActuel.getTroncons().get(0);
			String rueAPrendre = tronconActuel.getNomRue();
			double distanceTroncon = tronconActuel.getLongueur();
			contenu = contenu + "Prendre " + rueAPrendre + " sur " + (int) distanceTroncon + " metres \r\n";
		}

		if (nbTroncons == 2) {
			Troncon tronconActuel = cheminActuel.getTroncons().get(0);
			Troncon tronconSuivant = cheminActuel.getTroncons().get(1);
			if (testRueIdentique(tronconActuel, tronconSuivant)) {
				String rueAPrendre = tronconActuel.getNomRue();
				double distanceTroncon = tronconActuel.getLongueur() + tronconSuivant.getLongueur();
				;
				contenu = contenu + "Prendre " + rueAPrendre + " sur " + (int) distanceTroncon + " metres\r\n";
			} else {
				String rueAPrendre1 = tronconActuel.getNomRue();
				String rueAPrendre2 = tronconSuivant.getNomRue();
				double distanceTroncon1 = tronconActuel.getLongueur();
				double distanceTroncon2 = tronconSuivant.getLongueur();

				contenu = contenu + "Prendre " + rueAPrendre1 + " sur " + (int) distanceTroncon1 + " metres \r\n";
				contenu = contenu + "Prendre " + rueAPrendre2 + " sur " + (int) distanceTroncon2 + " metres \r\n";

			}
		}

		if (nbTroncons > 2) {
			for (int numTroncon = 0; numTroncon < nbTroncons - 1; numTroncon++) {
				Troncon tronconActuel = cheminActuel.getTroncons().get(numTroncon);
				Troncon tronconSuivant = cheminActuel.getTroncons().get(numTroncon + 1);

				// si c'est deux troncons de la meme rue, on les assemble en 1
				// troncon
				if (testRueIdentique(tronconActuel, tronconSuivant) == true) {
					tronconSuivant.setLongueur(tronconActuel.getLongueur() + tronconSuivant.getLongueur());
				} else {
					String rueAPrendre = tronconActuel.getNomRue();
					double distanceTroncon = tronconActuel.getLongueur();
					contenu = contenu + "Prendre " + rueAPrendre + " sur " + (int) distanceTroncon + " metres \r\n";
				}

			}

			// Pour le dernier troncon, test avec le troncon PRECEDENT
			Troncon lastTroncon = (cheminActuel.getTroncons().get(nbTroncons - 1));
			Troncon preLastTroncon = (cheminActuel.getTroncons().get(nbTroncons - 2));
			if (testRueIdentique(lastTroncon, preLastTroncon) == true) {
				lastTroncon.setLongueur(preLastTroncon.getLongueur() + lastTroncon.getLongueur());
				double LastDistanceTroncon = lastTroncon.getLongueur();
				String LastRueAPrendre = lastTroncon.getNomRue();
				contenu = contenu + "Prendre " + LastRueAPrendre + " sur " + (int) LastDistanceTroncon + " metres \r\n";

			}

			else {
				double LastDistanceTroncon = lastTroncon.getLongueur();
				String LastRueAPrendre = lastTroncon.getNomRue();
				contenu = contenu + "Prendre " + LastRueAPrendre + " sur " + (int) LastDistanceTroncon + " metres\r\n";
			}
		}

		// Derniere ligne d'arrivee
		if (idEntrepot == destinationCheminActuel.getId()) {
			SimpleDateFormat dureeHm = new SimpleDateFormat("HH:mm");
			String heureArr = dureeHm.format(tourneeEnCours.getHeureArrive());
			contenu = contenu + "\r" + "Arrivee a l'entrepot a " + heureArr + "\r\n";
			;
		}

		else {
			Livraison livraisonActuelle = combinaisonPointDeLivraison(tourneeEnCours.getListeLivraison(),
					destinationCheminActuel);
			String adresse = ruesDelIntersection(destinationCheminActuel);
			SimpleDateFormat dureeHm = new SimpleDateFormat("HH:mm");
			String heureFinCheminActuelFormat = dureeHm.format(heureFinCheminActuel);
			contenu = contenu + "\r" + "Arrivee au point de livraison " + adresse + " a " + heureFinCheminActuelFormat
					+ "\r\n";

			if (heureDebutLiv != null) {

				SimpleDateFormat dureeM = new SimpleDateFormat("HH'h 'mm");
				contenu = contenu + "Attente pendant " + dureeM.format(tempsAttente) + " minutes \r\n";
			}

			contenu = contenu + "Livraison pendant " + livraisonActuelle.getDuree() / 60 + " minutes \r\n";
			// heureDepartPoint = heureFinCheminActuel +
			// livraisonActuelle.getDuree();
			// contenu = contenu + "Depart du point de livraison "+
			// destinationCheminActuel + " a " + heureDepartPoint+ "\r\n";
			contenu = contenu + "Depart du point de livraison " + adresse + "\r\n\r\n";
		}

		return contenu;
	}

	/**
	 * La methode combinaisonPointDeLivraison permet de retrouver une livraison
	 * a partir d une intersection donnee.
	 * 
	 * @param livraisons
	 * @param intersection
	 * @return
	 */
	private static Livraison combinaisonPointDeLivraison(ArrayList<Livraison> livraisons, Intersection intersection) {

		long idInter = intersection.getId();
		int nLiv = 0;
		Livraison livraisonGagnante = null;

		while (idInter != livraisons.get(nLiv).getDestination().getId()) {
			nLiv++;
		}

		livraisonGagnante = livraisons.get(nLiv);
		return livraisonGagnante;
	}

	/**
	 * La methode ruesDelIntersection retourne, pour une intersection donnee le
	 * nom des rues.
	 * 
	 * @param intersection
	 * @return
	 */
	private static String ruesDelIntersection(Intersection intersection) {
		ArrayList<Troncon> troncons = intersection.getTronconsVersVoisins();
		String rues = null;
		String rue2 = null;

		String rue1 = troncons.get(0).getNomRue();

		if (troncons.size() > 1) {
			rue2 = troncons.get(1).getNomRue();
			rues = rue1 + " " + rue2;
		} else
			rues = rue1;

		return rues;
	}

	/**
	 * La methode Open, ouvre la feuille de route generee.
	 * 
	 * @throws IOException
	 */
	public static void Open() throws IOException {
		Desktop desktop = Desktop.getDesktop();
		if (desktop.isSupported(Desktop.Action.OPEN)) {
			desktop.open(new File("src/main/resources/FeuilleDeRoute.txt"));
		} else {
			System.out.println("Open is not supported");
		}
	}

}