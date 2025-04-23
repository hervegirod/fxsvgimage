/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.girod.javafx.loaders;

import java.io.File;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGImageRegion;
import org.girod.javafx.svgimage.SVGLoader;
import org.girod.javafx.svgimage.xml.parsers.SVGParsingException;

/**
 *
 * @author herve
 */
public class TestBorderPaneImage extends Application {
   public static void main(String[] args) {
      launch(args);
   }
   
   @Override
   public void start(Stage stage) {
      stage.setTitle("TestSVGLoaderFX");
      
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
            SVGImageRegion region = svgImg.createRegion();
            region.setConform(true);
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(region);
            stage.setScene(new Scene(borderPane, 300, 250));
            stage.show();
         } catch (SVGParsingException e) {
            Throwable th = e.getCause();
            th.printStackTrace();
         }
      } else {
         System.exit(0);
      }
   }
   
}
