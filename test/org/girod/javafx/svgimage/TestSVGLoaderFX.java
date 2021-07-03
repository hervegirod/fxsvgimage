/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.javafx.svgimage;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author scdsahv
 */
public class TestSVGLoaderFX extends Application {
   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage stage) {
      stage.setTitle("TestSVGLoader2");

      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
         @Override
         public void handle(WindowEvent t) {
            System.exit(0);
         }
      });

      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
      File file = fileChooser.showOpenDialog(stage);
      if (file != null) {
         try {
            SVGImage svgImg = SVGLoader.load(file);
            stage.setScene(new Scene(svgImg, svgImg.getWidth(), svgImg.getHeight()));

            StackPane root = new StackPane();
            root.getChildren().add(svgImg);
            stage.setScene(new Scene(root, 300, 250));
            stage.show();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         System.exit(0);
      }
   }

}
