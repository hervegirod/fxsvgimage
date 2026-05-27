import javafx.scene.shape.Circle;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

public javafx.scene.Node getContent() {
   Circle circle = new Circle(300, 180, 90);
   Stop[] stops = new Stop[] { new Stop(0, Color.WHITE), new Stop(0.3, Color.RED), new Stop(1, Color.DARKRED)};
   RadialGradient gradient = new RadialGradient(0.5, 0.5, 0, 0, 0.5, true, CycleMethod.NO_CYCLE, stops);
   circle.setFill(gradient);
   return circle;
}
