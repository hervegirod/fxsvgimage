/*
Copyright (c) 2026, Hervé Girod
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
package org.girod.javafx.tosvg.awt;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.girod.javafx.svgimage.fromjfx.ConverterParameters;
import org.girod.javafx.svgimage.fromjfx.awt.Graphics2DConverter;
import org.girod.javafx.tosvg.JFXInvoker;

/**
 * The abstract awt converter class.
 *
 * @since 1.8
 */
public abstract class AbstractAwtConverterUtils {
   private Node node = null;
   private File file = null;
   private StackPane root = null;
   private ConverterParameters params = null;
   private String styleSheet = null;
   private static ConverterParameters PARAMS = new ConverterParameters();

   static {
      PARAMS.allowTransformForRoot = false;
   }

   protected abstract Node getContent();

   public void convert(File file) throws Exception {
      convert(file, null, null);
   }

   public void convert(File file, ConverterParameters params) throws Exception {
      convert(file, params, null);
   }

   public void convert(File file, String styleSheet) throws Exception {
      convert(file, null, styleSheet);
   }

   public void convert(File file, ConverterParameters params, String styleSheet) throws Exception {
      this.file = file;
      this.styleSheet = styleSheet;
      this.params = params;
      JFXInvoker invoker = JFXInvoker.getInstance();
      invoker.invokeBlocking(new Runnable() {
         @Override
         public void run() {
            try {
               convertImpl();
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }
      });
   }

   private Dimension2D getDimension() {
      return new Dimension2D(300, 250);
   }

   private void convertImpl() throws Exception {
      node = getContent();
      root = new StackPane();
      Dimension2D dim = getDimension();
      Scene scene = new Scene(root, dim.getWidth(), dim.getHeight());
      if (styleSheet != null) {
         scene.getStylesheets().add(styleSheet);
      }
      Stage stage = new Stage();
      stage.setTitle("JavaFX Content");
      stage.setScene(scene);
      root.getChildren().add(node);
      stage.show();
      root.applyCss();

      ConverterParameters _params;
      if (params != null) {
         _params = params;
      } else {
         _params = PARAMS;
      }
      Graphics2DConverter converter = new Graphics2DConverter(_params);
      BufferedImage image;
      if (_params.allowTransformForRoot) {
         Rectangle2D rect = converter.getViewbox(node);
         if (_params.insets != 0) {
            int insets = (int) _params.insets;
            image = new BufferedImage((int) rect.getWidth() + insets * 2, (int) rect.getHeight() + insets * 2, BufferedImage.TYPE_INT_ARGB);
         } else {
            image = new BufferedImage((int) rect.getWidth(), (int) rect.getHeight(), BufferedImage.TYPE_INT_ARGB);
         }
      } else {
         image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
      }
      Graphics2D g2d = image.createGraphics();
      converter.setGraphics2D(g2d);
      converter.convert(node);
      stage.close();
      ImageIO.write(image, "png", file);
   }
}
