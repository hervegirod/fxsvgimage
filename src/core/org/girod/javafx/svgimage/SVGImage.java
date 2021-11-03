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
package org.girod.javafx.svgimage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * The resulting SVG image. It is a JavaFX Nodes tree.
 *
 * @version 0.3
 */
public class SVGImage extends Group {
   private static SnapshotParameters SNAPSHOT_PARAMS = null;
   private final Map<String, Node> nodes = new HashMap<>();

   /**
    * Set the default SnapshotParameters to use when creating a snapshot. The default is null, which means that a
    * default SnapshotParameters will be created when creating a snapshot.
    *
    * @param params the default SnapshotParameters
    */
   public static void setDefaultSnapshotParameters(SnapshotParameters params) {
      SNAPSHOT_PARAMS = params;
   }

   /**
    * Return the default SnapshotParameters used when creating a snapshot.
    *
    * @return the default SnapshotParameters
    */
   public static SnapshotParameters getDefaultSnapshotParameters() {
      return SNAPSHOT_PARAMS;
   }

   void putNode(String id, Node node) {
      nodes.put(id, node);
   }

   /**
    * Return the Node indicated by id.
    *
    * @param id the name of the Node
    * @return the Node
    */
   public Node getNode(String id) {
      return nodes.get(id);
   }

   /**
    * Return the width of the image.
    *
    * @return the width
    */
   public double getWidth() {
      return this.getLayoutBounds().getWidth();
   }

   /**
    * Return the width of the image, taking into account the scaling of the svg image.
    *
    * @return the width
    */
   public double getScaledWidth() {
      return this.getBoundsInParent().getWidth();
   }

   /**
    * Return the height of the image.
    *
    * @return the height
    */
   public double getHeight() {
      return this.getLayoutBounds().getHeight();
   }

   /**
    * Return the height of the image, taking into account the scaling of the svg image.
    *
    * @return the height
    */
   public double getScaledHeight() {
      return this.getBoundsInParent().getHeight();
   }

   /**
    * Convert the Node tree to an image.
    *
    * @param scale the scale
    * @return the Image
    */
   public Image toImageScaled(double scale) {
      return toImageScaled(scale, scale);
   }

   /**
    * Convert the Node tree to a scaled image.
    *
    * @param scaleX the X scale
    * @param scaleY the Y scale
    * @return the Image
    */
   public Image toImageScaled(double scaleX, double scaleY) {
      this.setScaleX(scaleX);
      this.setScaleY(scaleY);
      SnapshotParameters params = SNAPSHOT_PARAMS;
      if (params == null) {
         params = new SnapshotParameters();
      }
      WritableImage image = snapshotImpl(params);
      return image;
   }

   /**
    * Convert the Node tree to an image, specifying the resulting width and preserving the image ratio.
    *
    * @param width the resulting width
    * @return the Image
    */
   public Image toImage(double width) {
      double initialWidth = this.getLayoutBounds().getWidth();
      double initialHeight = this.getLayoutBounds().getHeight();
      double scaleX = width / initialWidth;
      double scaleY = initialHeight * scaleX;
      this.setScaleX(scaleX);
      this.setScaleY(scaleY);
      SnapshotParameters params = SNAPSHOT_PARAMS;
      if (params == null) {
         params = new SnapshotParameters();
      }
      WritableImage image = snapshotImpl(params);
      return image;
   }

   /**
    * Convert the Node tree to an image, without applying a scale.
    *
    * @return the Image
    */
   public Image toImage() {
      SnapshotParameters params = SNAPSHOT_PARAMS;
      if (params == null) {
         params = new SnapshotParameters();
      }
      return snapshotImpl(params);
   }

   /**
    * Convert the Node tree to an image.
    *
    * @param params the parameters
    * @return the Image
    */
   public Image toImage(SnapshotParameters params) {
      return snapshotImpl(params);
   }

   private WritableImage snapshotImplInJFX(SnapshotParameters params) {
      return this.snapshot(params, null);
   }

   private WritableImage snapshotImpl(final SnapshotParameters params) {
      if (Platform.isFxApplicationThread()) {
         return snapshotImplInJFX(params);
      } else {
         // the next instruction is only there to initialize the JavaFX platform
         new JFXPanel();
         FutureTask<WritableImage> future = new FutureTask<>(new Callable<WritableImage>() {
            @Override
            public WritableImage call() throws Exception {
               return snapshotImplInJFX(params);
            }
         });
         Platform.runLater(future);
         try {
            return future.get();
         } catch (Exception ex) {
            return null;
         }
      }
   }
}
