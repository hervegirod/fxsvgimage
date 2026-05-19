import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse

public javafx.scene.Node getContent() {
   Ellipse ellipse = new Ellipse();
   ellipse.setCenterX(100.0f);
   ellipse.setCenterY(100.0f);
   ellipse.setRadiusX(100.0f);   
   ellipse.setRadiusY(50.0f);  
   ellipse.setStroke(Color.RED);
   ellipse.setFill(Color.GREEN);
   return ellipse;
}
