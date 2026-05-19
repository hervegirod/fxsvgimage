import javafx.scene.shape.Polyline;
import javafx.scene.paint.Color;

public javafx.scene.Node getContent() {
   Polyline polyline = new Polyline();
   polyline.getPoints().addAll(new Double[]{0.0, 0.0, 0.0, 100.0, 100.0, 100.0});
   polyline.setStroke(Color.RED);
   return polyline;
}
