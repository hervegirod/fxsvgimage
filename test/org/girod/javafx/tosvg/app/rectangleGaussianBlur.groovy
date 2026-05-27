import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.effect.GaussianBlur;

public javafx.scene.Node getContent() {
   Rectangle rect = new Rectangle(50, 50, 200, 100);
   rect.setFill(Color.GREEN);
   GaussianBlur blur = new GaussianBlur();
   blur.setRadius(5.0);   
   rect.setEffect(blur);
   return rect;
}
