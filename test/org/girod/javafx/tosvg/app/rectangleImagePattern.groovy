import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public javafx.scene.Node getContent() {
   Rectangle rect = new Rectangle(50, 50, 200, 100);
   Image image = new Image(context.getURL("converge.jpg"));
   ImagePattern pattern = new ImagePattern(image);
   rect.setFill(pattern);
   return rect;
}
