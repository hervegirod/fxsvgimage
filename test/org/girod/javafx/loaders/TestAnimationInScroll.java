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
package org.girod.javafx.loaders;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @since 0.6.1
 */
public class TestAnimationInScroll extends Application {
   private Group root = null;

   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage primaryStage) {
      Rectangle rect = new Rectangle(100, 100, 50, 50);
      rect.setFill(Color.BLUE);
      root = new Group();
      root.getChildren().add(rect);

      MyStackPane content = new MyStackPane(root);
      root.layoutBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
         content.setMinWidth(newBounds.getWidth());
         content.setMinHeight(newBounds.getHeight());
      });
      ScrollPane scrollPane = new ScrollPane(content);
      scrollPane.setPannable(true);
      scrollPane.setFitToHeight(true);
      scrollPane.setFitToWidth(true);
      scrollPane.setPrefSize(500, 500);
      root.setAutoSizeChildren(false);
      content.allowLayoutChildren(false);

      Timeline timeline = new Timeline();
      KeyFrame kf0 = new KeyFrame(Duration.ZERO, new KeyValue(rect.xProperty(), 0));
      KeyFrame kf1 = new KeyFrame(Duration.seconds(10), new KeyValue(rect.xProperty(), 100));
      timeline.getKeyFrames().addAll(kf0, kf1);
      timeline.play();

      /*
      ScaleTransition transition = new ScaleTransition(Duration.seconds(5), rect);
      transition.setFromX(1d);
      transition.setFromY(1d);
      transition.setToX(3d);
      transition.setToY(3d);
      transition.play();
       */
      content.setOnScroll(new EventHandler<ScrollEvent>() {
         @Override
         public void handle(ScrollEvent event) {
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
               zoomFactor = 1 / zoomFactor;
            }
            Bounds groupBounds = root.getBoundsInLocal();
            final Bounds viewportBounds = scrollPane.getViewportBounds();

            double valX = scrollPane.getHvalue() * (groupBounds.getWidth() - viewportBounds.getWidth());
            double valY = scrollPane.getVvalue() * (groupBounds.getHeight() - viewportBounds.getHeight());
            root.setScaleX(root.getScaleX() * zoomFactor);
            root.setScaleY(root.getScaleY() * zoomFactor);

            Point2D posInZoomTarget = root.parentToLocal(new Point2D(event.getX(), event.getY()));
            Point2D adjustment = root.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));
            scrollPane.layout();
            scrollPane.setViewportBounds(groupBounds);

            groupBounds = root.getBoundsInLocal();
            scrollPane.setHvalue((valX + adjustment.getX()) / (groupBounds.getWidth() - viewportBounds.getWidth()));
            scrollPane.setVvalue((valY + adjustment.getY()) / (groupBounds.getHeight() - viewportBounds.getHeight()));
         }
      });

      Scene scene = new Scene(scrollPane, 800, 600);
      primaryStage.setScene(scene);

      primaryStage.show();
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
