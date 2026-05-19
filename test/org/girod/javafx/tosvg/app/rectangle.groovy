import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public javafx.scene.Node getContent() {
   Rectangle rect = new Rectangle(50, 50, 200, 100);
   rect.setStroke(Color.RED);
   rect.setFill(Color.GREEN);
   return rect;
}
