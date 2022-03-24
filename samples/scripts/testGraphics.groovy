import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

public Node getContent() {
   Line line = new Line(0, 0, 200, 200);
   line.getStrokeDashArray().addAll(25d, 20d, 5d, 20d);
   return line;
}
