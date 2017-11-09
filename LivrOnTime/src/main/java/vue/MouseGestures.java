package vue;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

import model.Intersection;
import model.Plan;

import controller.AccueilController;
import controller.LivraisonController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


public class MouseGestures {
	
    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    private Plan plan;
    private long cle;
    private Paint couleurSelectionne;
	private Circle cercleSelectionne;
	private PannableCanvas canvas;
	private AccueilController accueilController;

    public MouseGestures (AccueilController accueilController) {
    	this.accueilController = accueilController;
    }

	public void makeClickable(Node node) {
        node.setOnMousePressed(circleOnMousePressedEventHandler);
    }
	
	public void rendreCercleSurvolable(Node node) {
		node.setOnMouseEntered(circleOnMouseEnteredEventHandler);
		node.setOnMouseExited(circleOnMouseExitedEventHandler);
	}
	
	//Event handler du survolement du cercle
	EventHandler<MouseEvent> circleOnMouseEnteredEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
       		System.out.println("enter");
       		double orgSceneX = t.getSceneX();
            double orgSceneY = t.getSceneY();
            if (t.getSource() instanceof Circle) {
            		Circle p = ((Circle) (t.getSource()));
                double orgTranslateX = p.getCenterX();
                double orgTranslateY = p.getCenterY();
                long key=0;
                for(Circle circle : DessinerPlan.dessine.values()){
                		
            		if(circle.equals(p)){
            			key = getKeyByValue(DessinerPlan.dessine, circle);
            		}
                }
                Intersection intersectionClicked = plan.getIntersections().get(key);
                System.out.println("Intersection ID "+intersectionClicked.getId());
            }
          }
      };
      
      EventHandler<MouseEvent> circleOnMouseExitedEventHandler = new EventHandler <MouseEvent>(){
          @Override
          public void handle(MouseEvent t) {             
               //tp.hide();
       	   		System.out.println("exit");
             }
         };

    //Event handler du clique sur un cercle
    EventHandler<MouseEvent> circleOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
        	 if (t.getClickCount() == 2) {

	            orgSceneX = t.getSceneX();
	            orgSceneY = t.getSceneY();
	
	            if (t.getSource() instanceof Circle) {
	
	                Circle p = ((Circle) (t.getSource()));
	
	                orgTranslateX = p.getCenterX();
	                orgTranslateY = p.getCenterY();
	                
	                if (cercleSelectionne != null) {
	                	cercleSelectionne.setFill(couleurSelectionne);
	                	cercleSelectionne.setStroke(couleurSelectionne);
	                }
	                
	                cercleSelectionne = p;
	                couleurSelectionne = p.getFill();
	                
	                p.setFill(Color.CYAN);
	                p.setStroke(Color.CYAN);
	                
	                cle = getKeyByValue(DessinerPlan.dessine, p );
	                
	            	Intersection intersectionClicked = plan.getIntersections().get(cle);
	            	System.out.println("Intersection ID "+intersectionClicked.getId());
	            	LivraisonController.setIntersection(intersectionClicked);
	            	if(LivraisonController.getDemandeL()!=null){
		            	Parent root;
				        try {
				        	root = FXMLLoader.load(getClass().getResource("../fxml/Livraison.fxml"));
				            Stage stage = new Stage();
				            stage.setTitle("Modifier Livraison");
				            stage.setAlwaysOnTop(true);
				            stage.setScene(new Scene(root));
				            
				            stage.show();
				            
				        }
				        catch (IOException e) {
				            e.printStackTrace();
				        }
	                	
	            	} 
	            }
	        }
        }
    };
    
    public static <T, E> T getKeyByValue(HashMap<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public PannableCanvas getCanvas() {
		return canvas;
	}

	public void setCanvas(PannableCanvas canvas) {
		this.canvas = canvas;
	}
     
}

