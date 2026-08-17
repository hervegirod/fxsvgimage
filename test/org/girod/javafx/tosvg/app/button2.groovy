import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public javafx.scene.Node getContent() {
   HBox pane = new HBox();
   Button button = new Button("Enabled");
   Button button2 = new Button("Disabled");
   button2.setDisable(true);
   pane.getChildren().addAll(button, button2);
   return pane;
}
