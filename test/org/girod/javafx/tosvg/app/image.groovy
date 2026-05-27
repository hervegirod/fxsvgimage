import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public javafx.scene.Node getContent() {
   Image image = new Image(context.getURL("converge.jpg"));
   ImageView imageView = new ImageView(image);
   return imageView;
}
