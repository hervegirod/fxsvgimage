import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Light;

public javafx.scene.Node getContent() {
   Circle circle = new Circle();
   circle.setRadius(100);
   circle.setCenterX(300);
   circle.setCenterY(160);
   circle.setRadius(100);
   circle.setFill(Color.YELLOW);
   circle.setStroke(null);
   Light.Spot light = new Light.Spot();
   light.setX(100);
   light.setY(100);
   light.setZ(45);
   light.setSpecularExponent(2);
   light.setColor(Color.GRAY);
   Lighting lighting = new Lighting(light); 
   circle.setEffect(lighting);
   return circle;
}
