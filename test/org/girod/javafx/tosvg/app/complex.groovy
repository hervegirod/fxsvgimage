import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public javafx.scene.Node getContent() {
   VBox vbox = new VBox(8);
   Button button = new Button("Hello World!");
   HBox hbox = new HBox(4);
   TextField tf = new TextField("The text...");
   hbox.getChildren().addAll(new Label("Text"), tf);
   CheckBox cb = new CheckBox("Check Me!");
   cb.setSelected(true);
   ComboBox combo = new ComboBox<String>();
   combo.getItems().addAll("One", "Two", "Three");
   combo.getSelectionModel().select(0);
   
   vbox.getChildren().addAll(hbox, cb, combo);
   return vbox;
}
