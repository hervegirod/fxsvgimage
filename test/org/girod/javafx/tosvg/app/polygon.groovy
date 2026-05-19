import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;

public javafx.scene.Node getContent() {
   Polygon polygon = new Polygon();
   polygon.setStroke(Color.RED);
   polygon.setFill(Color.GREEN);
   polygon.getPoints().addAll(new Double[]{
         0.0, 0.0,
         100.0, 0.0,
         100.0, 100.0, 
         0.0, 100.0});   
   return polygon;
}
