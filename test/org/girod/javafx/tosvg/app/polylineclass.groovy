import javafx.scene.shape.Polyline;

public javafx.scene.Node getContent() {
   context.setStyleSheet("stylesheet.css");
   Polyline polyline = new Polyline();
   polyline.getPoints().addAll(new Double[]{0.0, 0.0, 0.0, 100.0, 100.0, 100.0});
   polyline.getStyleClass().addAll("fill");
   polyline.setFill(null);
   return polyline;
}
