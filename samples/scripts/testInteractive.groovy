import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.scene.control.*;

public Node getContent() {
   VBox pane = new VBox();
   Button button = new Button("TOTO");
   pane.getChildren().add(button);
   CheckBox checkbox = new CheckBox("Check");
   checkbox.setSelected(true);
   pane.getChildren().add(checkbox);  
   ObservableList<String> names = FXCollections.observableArrayList("Julia", "Ian", "Sue", "Matthew");
   ListView<String> listView = new ListView<String>(names);
   pane.getChildren().add(listView);  
   return pane;
}
