/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.javafx.svgimage.samples;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

/**
 *
 * @author Herve
 */
public class TestViewer extends Application {
   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage stage) {
      stage.setOnHidden(new EventHandler<WindowEvent>() {
         @Override
         public void handle(WindowEvent t) {
            stage.close();
            System.exit(0);
         }
      });

      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
      fileChooser.setTitle("Open SVG File");
      File file = fileChooser.showOpenDialog(stage);

      try {
         SVGImage image = SVGLoader.load(file.toURI().toURL());
         StackPane root = new StackPane();
         root.getChildren().add(image);
         stage.setScene(new Scene(root, 300, 250));
         stage.show();
      } catch (IOException ex) {
         System.exit(0);
      }
   }
}
