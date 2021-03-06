package vue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map.Entry;

import controleur.AccueilControleur;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import modele.Chemin;
import modele.DemandeLivraison;
import modele.Intersection;
import modele.Livraison;
import modele.Plan;
import modele.Tournee;
import modele.Troncon;

/**
 * La classe DessinerPlan implemente tous les comportements graphiques lies au
 * plan et a l affichage des demandes de livraison et des tournee.
 * 
 * @author Matthieu
 *
 */
public class DessinerPlan {

	// Variables de mise a l'echelle
	private int divX, minusX, divY, minusY;
	private int minX, minY, maxX = 0, maxY = 0;

	// Taille supposee du canvas, a ne pas laisser en dur
	private double tailleCanvas = 400.0;
	private double largeurTrait = 0.5;

	private Pane overlay = new Pane();
	private PannableCanvas canvas;
	private HashMap<Long, Circle> dessine;
	private ArrayList<Text> chiffres;

	private MouseGestures mg;
	private SceneGestures sg;

	final static double TRAITCERCLE = 0.4;
	final static double TRAITENTREPOT = 6;
	final static double TRAITLIVRAISON = 4;
	final static double TRAITITINERAIRE = 4;
	final static double MODIFICATEURPOSITIONCHIFFREX = 7;
	final static double MODIFICATEURPOSITIONCHIFFREY = 8;
	final static Paint COULEURPLAN = Color.GREY;
	final static Paint COULEURENTREPOT = Color.PLUM;
	final static Paint COULEURLIVRAISON = Color.BLUE;
	final static Paint COULEURITINERAIRE = Color.GREEN;
	final static Paint COULEURCHIFFRES = Color.BLACK;
	final static Paint COULEURTENDU = Color.ORANGE;
	final static Paint COULEURATTENTE = Color.PURPLE;
	final static Paint COULEURVIOLEE = Color.RED;

	/**
	 * Constructeur de la classe DessinerPlan
	 * 
	 * @param mouseGestures
	 * @param sceneGestures
	 */
	public DessinerPlan(MouseGestures mouseGestures, SceneGestures sceneGestures) {

		chiffres = new ArrayList<Text>();
		canvas = new PannableCanvas();
		dessine = new HashMap<Long, Circle>();
		mg = mouseGestures;
		sg = sceneGestures;
	}

	// Methode qui dessine les troncons
	/**
	 * La methode dessinerTroncon permet de dessiner un troncon sur le plan a
	 * partir de ses intersections de destination et d origine.
	 * 
	 * @param D
	 * @param O
	 * @param plan
	 */
	public void dessinerTroncon(Intersection D, Intersection O, Plan plan) {

		int x, y;
		Circle cercle1 = new Circle(1);
		cercle1.setStroke(COULEURPLAN);
		cercle1.setFill(COULEURPLAN);

		// Mise a l'echelle
		x = (int) ((D.getX() - minusX) * tailleCanvas / divX);
		y = (int) ((D.getY() - minusY) * tailleCanvas / divY);
		cercle1.setRadius(TRAITCERCLE);

		// Centrage
		cercle1.relocate(y + tailleCanvas / 2, -x + tailleCanvas);

		// Mise a l'echelle
		x = (int) ((O.getX() - minusX) * tailleCanvas / divX);
		y = (int) ((O.getY() - minusY) * tailleCanvas / divY);

		Circle cercle2 = new Circle(1);
		cercle2.setRadius(TRAITCERCLE);
		cercle2.setStroke(COULEURPLAN);
		cercle2.setFill(COULEURPLAN);

		// Centrage
		cercle2.relocate(y + tailleCanvas / 2, -x + tailleCanvas);
		// Rendre les circles clicable
		mg.rendreDoubleCliquable(cercle1);
		mg.rendreDoubleCliquable(cercle2);
		mg.rendreCliquable(cercle1);
		mg.rendreCliquable(cercle2);

		Line ligne = new Line(cercle1.getLayoutX(), cercle1.getLayoutY(), cercle2.getLayoutX(), cercle2.getLayoutY());

		ligne.setStrokeWidth(largeurTrait);
		ligne.setFill(COULEURPLAN);
		ligne.setStroke(COULEURPLAN);

		mg.rendreLigneSurvolable(ligne, D, O);

		canvas.getChildren().add(ligne);

		if (!dessine.containsKey(D.getId())) {
			canvas.getChildren().add(cercle1);
			dessine.put(D.getId(), cercle1);
		}

		else {
			((Circle) dessine.get(D.getId())).toFront();
		}

		if (!dessine.containsKey(O.getId())) {
			canvas.getChildren().add(cercle2);
			dessine.put(O.getId(), cercle2);
		}

		else {
			((Circle) dessine.get(O.getId())).toFront();
		}

	}

	/**
	 * La methode Dessiner dessine le plan dans un canvas.
	 * 
	 * @param plan
	 * @return
	 */
	public Group Dessiner(Plan plan) {
		dessine = new HashMap<Long, Circle>();
		canvas = new PannableCanvas();
		Group groupe = new Group();

		int minX;
		int minY;
		int maxX = 0;
		int maxY = 0;

		// Calcul du minX et du min Y
		for (Troncon T : plan.getTroncons()) {
			if (T.getDestination().getX() > maxX || T.getOrigine().getX() > maxY) {
				maxX = Math.max(T.getDestination().getX(), T.getOrigine().getX());
			}

			if (T.getDestination().getY() > maxY || T.getOrigine().getY() > maxY) {
				maxY = Math.max(T.getDestination().getY(), T.getOrigine().getY());
			}
		}

		minY = maxY;
		minX = maxX;

		// Calcul du minX et du min Y
		for (Troncon T : plan.getTroncons()) {
			if (T.getDestination().getX() < minX || T.getOrigine().getX() < minX) {
				minX = Math.min(T.getDestination().getX(), T.getOrigine().getX());
			}

			if (T.getDestination().getY() < minY || T.getOrigine().getY() < minY) {
				minY = Math.min(T.getDestination().getY(), T.getOrigine().getY());
			}
		}

		minusX = minX;
		divX = maxX - minusX;
		minusY = minY;
		divY = maxY - minusY;

		mg.setCanvas(canvas);
		mg.setPlan(plan);
		sg.setCanvas(canvas);

		for (Troncon T : plan.getTroncons()) {
			dessinerTroncon(T.getDestination(), T.getOrigine(), plan);
		}

		groupe.getChildren().add(canvas);
		return groupe;
	}

	/**
	 * La methode Dessiner permet d ajouter au plan une demande de livraison
	 * chargee ulterieurement.
	 * 
	 * @param dl
	 * @param plan
	 * @return
	 */
	public Group Dessiner(DemandeLivraison dl, Plan plan) {
		ArrayList<Livraison> livraisons = dl.getLivraisons();
		Group groupe = new Group();

		Circle cercle = dessine.get(dl.getAdresseEntrepot().getId());
		canvas.getChildren().remove(cercle);
		cercle.setStroke(Color.BLACK);
		cercle.setFill(COULEURENTREPOT);
		cercle.setRadius(TRAITENTREPOT);
		canvas.getChildren().add(cercle);

		for (Livraison livraison : livraisons) {
			cercle = dessine.get(livraison.getDestination().getId());
			canvas.getChildren().remove(cercle);
			cercle.setStroke(COULEURLIVRAISON);
			cercle.setFill(COULEURLIVRAISON);
			cercle.setRadius(TRAITLIVRAISON);
			canvas.getChildren().add(cercle);

		}

		return groupe;
	}

	/**
	 * La methode afficherChemin permet de dessiner a tournee calculee sur le
	 * plan.
	 * 
	 * @param tournee
	 * @return
	 */
	public Group afficherChemin(Tournee tournee) {
		Group groupe = new Group();

		Circle cercle1 = dessine.get(tournee.getItineraire().get(0).getOrigine().getId());
		actualiserCouleurPoints(tournee);
		Circle cercle2;

		for (Chemin c : tournee.getItineraire()) {

			Text precedent = new Text();

			for (Troncon t : c.getTroncons()) {
				cercle1 = dessine.get(t.getOrigine().getId());
				cercle2 = dessine.get(t.getDestination().getId());

				Line ligne = new Line(cercle1.getLayoutX(), cercle1.getLayoutY(), cercle2.getLayoutX(),
						cercle2.getLayoutY());

				ligne.setStroke(COULEURITINERAIRE);
				ligne.setFill(COULEURITINERAIRE);
				ligne.setStrokeWidth(largeurTrait * TRAITITINERAIRE);
				mg.rendreLigneSurvolable(ligne, t.getDestination(), t.getOrigine());
				canvas.getChildren().add(ligne);
				cercle1.toFront();
			}
		}

		numeroterSommets(tournee);
		passerChiffresDevant();
		return groupe;
	}

	/**
	 * La methode numeroterSommets ajoute des numeros a cote des livraisons pour
	 * mieux suivre le sens de parcours de la tournee.
	 * 
	 * @param tournee
	 */
	public void numeroterSommets(Tournee tournee) {

		if (!chiffres.isEmpty()) {
			for (Text t : chiffres) {
				canvas.getChildren().remove(t);
			}
			chiffres.clear();
		}

		int indice = 1;
		for (int i = 0; i < tournee.getItineraire().size(); i++) {

			Chemin c = tournee.getItineraire().get(i);
			if (i == 0 || c.getDestination() != tournee.getItineraire().get(i - 1).getDestination()) {
				Text chiffreOrigine = new Text("" + indice);
				chiffreOrigine.setFill(COULEURCHIFFRES);
				chiffreOrigine.setStroke(COULEURCHIFFRES);
				chiffreOrigine.setBoundsType(TextBoundsType.VISUAL);

				chiffreOrigine.setX(
						dessine.get(c.getOrigine().getId()).getLayoutX() + largeurTrait * MODIFICATEURPOSITIONCHIFFREX);
				chiffreOrigine.setY(
						dessine.get(c.getOrigine().getId()).getLayoutY() - largeurTrait * MODIFICATEURPOSITIONCHIFFREX);
				canvas.getChildren().add(chiffreOrigine);

				indice++;
				chiffres.add(chiffreOrigine);
			}
		}
	}

	/**
	 * La methode passerChiffresDevant gere l affichage des numeros sur le plan.
	 */
	public void passerChiffresDevant() {
		for (int i = 0; i < chiffres.size(); i++) {
			chiffres.get(i).toFront();
		}
	}

	/**
	 * La methode surlignerTroncon permet de passer un troncon en jaune quand on
	 * le selectionne.
	 * 
	 * @param t
	 * @param Couleur
	 */
	public void surlignerTroncon(Troncon t, Paint Couleur) {

		Circle cercle1 = dessine.get(t.getOrigine().getId());
		Circle cercle2 = dessine.get(t.getDestination().getId());

		Line ligne = new Line(cercle1.getLayoutX(), cercle1.getLayoutY(), cercle2.getLayoutX(), cercle2.getLayoutY());

		ligne.setStroke(Couleur);
		ligne.setFill(Couleur);
		ligne.setStrokeWidth(largeurTrait * TRAITITINERAIRE);
		canvas.getChildren().add(ligne);
		cercle1.toFront();
		cercle2.toFront();
	}

	/**
	 * La methode actualiserCouleurPoints met a jour la couleur des points selon
	 * la legende etablie quand ils respectent ou pas les plages horaires.
	 * 
	 * @param tournee
	 */
	public void actualiserCouleurPoints(Tournee tournee) {

		for (int i = 0; i < tournee.getItineraire().size() - 1; i++) {
			// retrouver lintersection correspondant a la livraison interLiv
			Livraison liv = tournee.getListeLivraison().get(i);

			int[] valeursPH = tournee.VerifierPlagesHorairesTournee();

			if (valeursPH[i] == 0) {
				Circle cercleActuel = dessine.get(liv.getDestination().getId());
				cercleActuel.setFill(COULEURLIVRAISON);
				cercleActuel.setStroke(COULEURLIVRAISON);
			}
			if (valeursPH[i] == 1) {
				Circle cercleActuel = dessine.get(liv.getDestination().getId());
				cercleActuel.setFill(COULEURTENDU);
				cercleActuel.setStroke(COULEURTENDU);
			}
			if (valeursPH[i] == 2) {
				Circle cercleActuel = dessine.get(liv.getDestination().getId());
				cercleActuel.setFill(COULEURATTENTE);
				cercleActuel.setStroke(COULEURATTENTE);
			}
			if (valeursPH[i] == 3) {
				Circle cercleActuel = dessine.get(liv.getDestination().getId());
				cercleActuel.setFill(COULEURVIOLEE);
				cercleActuel.setStroke(COULEURVIOLEE);
			}
		}
	}

	public static <T, E> T getKeyByValue(HashMap<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public int getDivX() {
		return divX;
	}

	public void setDivX(int divX) {
		this.divX = divX;
	}

	public int getMinusX() {
		return minusX;
	}

	public void setMinusX(int minusX) {
		this.minusX = minusX;
	}

	public int getDivY() {
		return divY;
	}

	public void setDivY(int divY) {
		this.divY = divY;
	}

	public int getMinusY() {
		return minusY;
	}

	public void setMinusY(int minusY) {
		this.minusY = minusY;
	}

	public double getTailleCanvas() {
		return tailleCanvas;
	}

	public void setTailleCanvas(double sizeCanvas) {
		this.tailleCanvas = sizeCanvas;
	}

	public HashMap<Long, Circle> getDessine() {
		return dessine;
	}

	public void setDessine(HashMap<Long, Circle> dessine) {
		this.dessine = dessine;
	}

}
