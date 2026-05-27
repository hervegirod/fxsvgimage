import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Light;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public javafx.scene.Node getContent() {
   Text text = new Text("Hello World!");
   text.setFill(Color.BLUE);
   text.setFont(Font.font(null, FontWeight.BOLD, 60));
   Light.Distant light = new Light.Distant();
   light.setAzimuth(0);
   light.setColor(Color.RED);
   Lighting lighting = new Lighting(light); 
   text.setEffect(lighting);
   return text;
}
