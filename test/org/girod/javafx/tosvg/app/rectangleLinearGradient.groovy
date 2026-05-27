import javafx.scene.shape.Rectangle;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

public javafx.scene.Node getContent() {
   Rectangle rect = new Rectangle(50, 50, 200, 100);
   Stop[] stops = new Stop[] { new Stop(0, Color.GREEN), new Stop(1, Color.BLUE)};
   LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
   rect.setFill(gradient);
   return rect;
}
