import javafx.scene.paint.Color;
import javafx.scene.shape.Circle

public javafx.scene.Node getContent() {
   Circle circle = new Circle();
   circle.setCenterX(100.0f);
   circle.setCenterY(100.0f);
   circle.setRadius(50.0f);   
   circle.setStroke(Color.RED);
   circle.setFill(Color.GREEN);
   return circle;
}
