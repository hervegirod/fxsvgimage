/*
Copyright (c) 2021, Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/fxsvgimage
 */
package org.girod.javafx.svgimage.browser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

/**
 * A sample browser.
 *
 * @version 0.6.1
 */
public class SVGBrowser extends Application {
   private Stage stage = null;
   private MenuBar menuBar = new MenuBar();
   private TabPane tabPane = new TabPane();
   private Map<Integer, SVGImage> imagesByIndex = new HashMap<>();

   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage stage) {
      this.stage = stage;
      stage.setTitle("SVG Browser");
      stage.setOnHidden(new EventHandler<WindowEvent>() {
         @Override
         public void handle(WindowEvent t) {
            stage.close();
            System.exit(0);
         }
      });

      Menu fileMenu = new Menu("File");
      menuBar.getMenus().add(fileMenu);

      MenuItem openItem = new MenuItem("Open");
      fileMenu.getItems().add(openItem);
      MenuItem saveItem = new MenuItem("Save Image");
      fileMenu.getItems().add(saveItem);
      saveItem.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent t) {
            save();
         }
      });

      MenuItem exitItem = new MenuItem("Exit");
      fileMenu.getItems().add(exitItem);
      exitItem.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent t) {
            stage.close();
            System.exit(0);
         }
      });

      openItem.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent t) {
            open();
         }
      });

      VBox vBox = new VBox();
      vBox.getChildren().add(menuBar);
      vBox.getChildren().add(tabPane);

      stage.setScene(new Scene(vBox, 600, 600));
      stage.show();
   }

   private Node outerNode(Node node) {
      Node outerNode = centeredNode(node);
      return outerNode;
   }

   private Node centeredNode(Node node) {
      VBox vBox = new VBox(node);
      vBox.setAlignment(Pos.CENTER);
      return vBox;
   }

   public boolean isEmpty() {
      return tabPane.getTabs().isEmpty();
   }

   public SVGImage getSelectedImage() {
      if (isEmpty()) {
         return null;
      }
      int index = tabPane.getSelectionModel().getSelectedIndex();
      if (imagesByIndex.containsKey(index)) {
         return imagesByIndex.get(index);
      } else {
         return null;
      }
   }

   private void save() {
      SVGImage svgImage = getSelectedImage();
      if (svgImage == null) {
         return;
      }
      FileChooser fileChooser = new FileChooser();
      fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PNG Files", "*.png"),
         new ExtensionFilter("JPEG Files", "*.jpg", ".jpeg"));
      fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
      fileChooser.setTitle("Save as Image");
      File file = fileChooser.showSaveDialog(stage);
      if (file == null) {
         return;
      }
      String name = file.getName();
      int index = name.lastIndexOf('.');
      String ext = null;
      if (index < name.length() - 1) {
         ext = name.substring(index + 1);
      }
      if (ext != null && !ext.equals("png") && !ext.equals("jpg") && !ext.equals("jpeg")) {
         name = name + ".png";
         ext = "png";
         file = new File(file.getParentFile(), name);
      } else {
         ExtensionFilter filter = fileChooser.getSelectedExtensionFilter();
         if (filter == null) {
            name = name + ".png";
            ext = "png";
            file = new File(file.getParentFile(), name);
         }
      }
      if (ext == null) {
         ext = "png";
      } else if (ext.equals("jpeg")) {
         ext = "jpg";
      }
      svgImage.snapshot(ext, file);
   }

   private void open() {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
      fileChooser.setTitle("Open SVG File");
      File file = fileChooser.showOpenDialog(stage);
      if (file == null) {
         return;
      }

      try {
         SVGImage image = SVGLoader.load(file.toURI().toURL());

         Group group = new Group(image);
         MyStackPane content = new MyStackPane(group);
         group.layoutBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            // keep it at least as large as the content
            content.setMinWidth(newBounds.getWidth());
            content.setMinHeight(newBounds.getHeight());
         });
         ScrollPane scrollPane = new ScrollPane(content);
         scrollPane.setPannable(true);
         scrollPane.setFitToHeight(true);
         scrollPane.setFitToWidth(true);
         scrollPane.setPrefSize(500, 500);
         Tab tab = new Tab(file.getName(), scrollPane);
         content.allowLayoutChildren(false);
         content.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
               double zoomFactor = 1.05;
               double deltaY = event.getDeltaY();
               if (deltaY < 0) {
                  zoomFactor = 1 / zoomFactor;
               }
               int index = tabPane.getSelectionModel().getSelectedIndex();
               if (imagesByIndex.containsKey(index)) {
                  // https://stackoverflow.com/questions/38604780/javafx-zoom-scroll-in-scrollpane
                  Node node = imagesByIndex.get(index);
                  Bounds groupBounds = node.getBoundsInLocal();
                  final Bounds viewportBounds = scrollPane.getViewportBounds();

                  double valX = scrollPane.getHvalue() * (groupBounds.getWidth() - viewportBounds.getWidth());
                  double valY = scrollPane.getVvalue() * (groupBounds.getHeight() - viewportBounds.getHeight());
                  node.setScaleX(node.getScaleX() * zoomFactor);
                  node.setScaleY(node.getScaleY() * zoomFactor);

                  Point2D posInZoomTarget = node.parentToLocal(new Point2D(event.getX(), event.getY()));
                  Point2D adjustment = node.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));
                  scrollPane.layout();
                  scrollPane.setViewportBounds(groupBounds);

                  groupBounds = group.getBoundsInLocal();
                  scrollPane.setHvalue((valX + adjustment.getX()) / (groupBounds.getWidth() - viewportBounds.getWidth()));
                  scrollPane.setVvalue((valY + adjustment.getY()) / (groupBounds.getHeight() - viewportBounds.getHeight()));
               }
            }
         });
         tabPane.getTabs().add(tab);
         int tabIndex = tabPane.getTabs().size() - 1;
         imagesByIndex.put(tabIndex, image);
         tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
               imagesByIndex.remove(tabIndex);
            }
         });

      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private class MyStackPane extends StackPane {
      private boolean allowLayoutChildren = true;

      private MyStackPane(Node root) {
         super(root);
      }

      private void allowLayoutChildren(boolean allow) {
         this.allowLayoutChildren = allow;
      }

      @Override
      public void layoutChildren() {
         if (allowLayoutChildren) {
            super.layoutChildren();
         }
      }

   }
}
