package org.girod.javafx.loaders;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TestTimeline extends Application {

   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage primaryStage) {
      Group root = new Group();
      Scene scene = new Scene(root, 800, 600);
      primaryStage.setScene(scene);
      Rectangle rect = new Rectangle(100, 100, 50, 50);
      root.getChildren().add(rect);
      rect.setFill(Color.BLUE);

      Timeline timeline = new Timeline();
      KeyFrame kf0 = new KeyFrame(Duration.ZERO, new KeyValue(rect.xProperty(), 0));
      KeyFrame kf1 = new KeyFrame(Duration.seconds(10), new KeyValue(rect.xProperty(), 100));
      timeline.getKeyFrames().addAll(kf0, kf1);
      timeline.play();

      primaryStage.show();
   }
}
