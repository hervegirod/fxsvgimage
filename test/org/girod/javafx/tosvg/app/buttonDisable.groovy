import javafx.scene.control.Button;

public javafx.scene.Node getContent() {
   Button button = new Button("Hello World!");
   button.setDisable(true);
   return button;
}
