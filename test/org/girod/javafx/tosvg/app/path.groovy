import javafx.scene.shape.Path;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo
import javafx.scene.shape.HLineTo
import javafx.scene.shape.MoveTo;

public javafx.scene.Node getContent() {
   Path path = new Path();
   
   MoveTo moveTo = new MoveTo();
   moveTo.setX(0.0f);
   moveTo.setY(0.0f);
   path.getElements().add(moveTo);
   
   HLineTo hLineTo = new HLineTo();
   hLineTo.setX(70.0f);
   path.getElements().add(hLineTo);
   
   ArcTo arcTo = new ArcTo();
   arcTo.setX(50.0f);
   arcTo.setY(50.0f);
   arcTo.setRadiusX(50.0f);
   arcTo.setRadiusY(50.0f);   
   path.getElements().add(arcTo);
   
   return path;
}
