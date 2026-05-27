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
package org.girod.javafx.tosvg;

import java.io.File;
import java.io.IOException;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.girod.javafx.svgimage.tosvg.ConverterParameters;
import org.girod.javafx.svgimage.tosvg.SVGConverter;

/**
 * The abstract converter class.
 *
 * @version 1.7.3
 */
public abstract class AbstractSVGConverterUtils {
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
            } catch (IOException ex) {
               ex.printStackTrace();
            }
         }
      });
   }

   private Dimension2D getDimension() {
      return new Dimension2D(300, 250);
   }

   private void convertImpl() throws IOException {
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
      SVGConverter converter = new SVGConverter(_params);
      converter.convert(node, file);
      stage.close();
   }
}
