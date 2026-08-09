/*
Copyright (c) 2026 Hervé Girod
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
package org.girod.javafx.tosvg.app.awt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.girod.javafx.svgimage.fromjfx.awt.Graphics2DConverter;
import org.girod.javafx.svgimage.fromjfx.ConverterParameters;
import org.girod.javafx.tosvg.JFXInvoker;

/**
 * An utility class to convert Nodes to Graphics2D content.
 *
 * @since 1.8
 */
public class AwtDriverAppUtils extends Application {
   private Node node = null;
   private File file = null;
   private Color background = null;
   private StackPane root = null;
   private String title = null;
   private boolean hasTransform = false;
   private boolean hasBackground = false;
   private int insets = -1;
   private String styleSheet = null;

   protected Dimension2D getDimension() {
      return new Dimension2D(300, 250);
   }

   /**
    * Set the background color used for the conversion.
    *
    * @param background the background
    */
   public void setBackground(Color background) {
      this.background = background;
   }

   /**
    * Convert the JavaFX content to a png content.
    *
    * @param node the JavaFX node
    * @param file the output png file to generate
    * @param styleSheet the associated styleSheet (can be null)
    * @param title the title of the output png file
    * @param hasTransform true if result has a transform applied
    * @param hasBackground true if a background is applied
    * @param insets the insets
    */
   public void convert(Node node, File file, String styleSheet, String title, boolean hasTransform, boolean hasBackground, int insets) throws Exception {
      this.node = node;
      this.file = file;
      this.title = title;
      this.hasTransform = hasTransform;
      this.hasBackground = hasBackground;
      this.insets = insets;
      this.styleSheet = styleSheet;

      JFXInvoker invoker = JFXInvoker.getInstance();
      invoker.invokeBlocking(new Runnable() {
         @Override
         public void run() {
            showStage();
         }
      });
      BufferedImage image;
      Graphics2DConverter converter = new Graphics2DConverter();
      if (hasTransform) {
         Rectangle2D rect = converter.getViewbox(node);
         if (insets != 0) {
            image = new BufferedImage((int) rect.getWidth() + insets * 2, (int) rect.getHeight() + insets * 2, BufferedImage.TYPE_INT_ARGB);
         } else {
            image = new BufferedImage((int) rect.getWidth(), (int) rect.getHeight(), BufferedImage.TYPE_INT_ARGB);
         }
      } else {
         image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
      }
      Graphics2D g2d = image.createGraphics();
      if (hasBackground) {
         int width = image.getWidth();
         int height = image.getHeight();
         g2d.setColor(Color.YELLOW);
         g2d.fillRect(0, 0, width, height);
      }
      converter.setGraphics2D(g2d);

      ConverterParameters params = new ConverterParameters();
      params.insets = insets;
      params.allowTransformForRoot = hasTransform;
      converter.convert(node, params);
      ImageIO.write(image, "png", file);
   }

   private void showStage() {
      root = new StackPane();
      Dimension2D dim = getDimension();
      Scene scene = new Scene(root, dim.getWidth(), dim.getHeight());
      if (styleSheet != null) {
         scene.getStylesheets().add(styleSheet);
      }
      Stage stage = new Stage();
      stage.setTitle(title);
      stage.setScene(scene);
      start(stage);
   }

   @Override
   public void start(Stage primaryStage) {
      root.getChildren().add(node);
      primaryStage.show();
   }
}
