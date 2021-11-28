package org.girod.javafx.loaders;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TestTransition extends Application {

   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage primaryStage) {
      Group root = new Group();
      Rectangle rect = new Rectangle(100, 100, 50, 50);
      rect.setFill(Color.BLUE);

      TranslateTransition translate = new TranslateTransition(Duration.seconds(10), rect);
      translate.setFromX(20);
      translate.setToX(500);

      root.getChildren().add(rect);
      translate.play();
      Scene scene = new Scene(root, 800, 600);
      primaryStage.setScene(scene);

      primaryStage.show();
   }
}
