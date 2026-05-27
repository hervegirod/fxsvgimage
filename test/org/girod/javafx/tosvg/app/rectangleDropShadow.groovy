import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

public javafx.scene.Node getContent() {
   Rectangle rect = new Rectangle(50, 50, 200, 100);
   rect.setFill(Color.GREEN);
   DropShadow dropShadow = new DropShadow();
   dropShadow.setRadius(5.0);
   dropShadow.setOffsetX(3.0);
   dropShadow.setOffsetY(3.0);
   dropShadow.setColor(Color.BLACK);   
   rect.setEffect(dropShadow);
   return rect;
}
