import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javax.swing.text.html.*;

import javafx.scene.paint.*;

public Node getContent() {
   File dir = context.getScriptDirectory();
   File imgFile = new File(dir, "crocodile.png");
   Image im = new Image(imgFile.toURL().toString());
   ImageView view = new ImageView();
   view.setImage(im);
   view.setStyle("-fx-opacity: 0.5;");
   view.setFitWidth(100);
   view.setFitHeight(100);   
   return view;
}
