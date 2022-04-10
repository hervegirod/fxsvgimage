/*
Copyright (c) 2021, 2022 Hervé Girod
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.girod.javafx.svgimage.xml.SVGLibraryException;

/**
 * The resulting SVG image. It is a JavaFX Nodes tree.
 *
 * @version 1.0
 */
public class SVGImage extends Group {
   private static SnapshotParameters SNAPSHOT_PARAMS = null;
   private final Map<String, Node> nodes = new HashMap<>();
   private List<Animation> animations = new ArrayList<>();

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
    * Return true if there is a Node indicated by an id.
    *
    * @param id the name of the Node
    * @return true if there is a Node indicated by the id
    */
   public boolean hasNode(String id) {
      return nodes.containsKey(id);
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
    * Set the list of animations.
    *
    * @param animations the animations.
    */
   void setAnimations(List<Animation> animations) {
      this.animations = animations;
   }

   /**
    * Play the animations.
    */
   public void playAnimations() {
      if (Platform.isFxApplicationThread()) {
         playAnimationsImpl();
      } else {
         // the next instruction is only there to initialize the JavaFX platform
         new JFXPanel();
         Platform.runLater(new Runnable() {
            @Override
            public void run() {
               playAnimationsImpl();
            }
         });
      }
   }

   private void playAnimationsImpl() {
      if (!animations.isEmpty()) {
         Iterator<Animation> it = animations.iterator();
         while (it.hasNext()) {
            Animation tr = it.next();
            tr.play();
         }
      }
   }

   /**
    * Stop the animations.
    */
   public void stopAnimations() {
      if (Platform.isFxApplicationThread()) {
         stopAnimationsImpl();
      } else {
         // the next instruction is only there to initialize the JavaFX platform
         new JFXPanel();
         Platform.runLater(new Runnable() {
            @Override
            public void run() {
               stopAnimationsImpl();
            }
         });
      }
   }

   private void stopAnimationsImpl() {
      if (!animations.isEmpty()) {
         Iterator<Animation> it = animations.iterator();
         while (it.hasNext()) {
            Animation tr = it.next();
            tr.stop();
         }
      }
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
      double initialWidth = this.getLayoutBounds().getWidth();
      double initialHeight = this.getLayoutBounds().getHeight();
      this.setScaleX(scaleX);
      this.setScaleY(scaleY);
      double finalWidth = initialWidth * scaleX;
      double finalHeight = initialHeight * scaleY;
      SnapshotParameters params = SNAPSHOT_PARAMS;
      Rectangle2D viewport = new Rectangle2D(0, 0, finalWidth, finalHeight);
      if (params == null) {
         params = new SnapshotParameters();
         params.setViewport(viewport);
      } else {
         params = new SnapshotParameters();
         params.setCamera(SNAPSHOT_PARAMS.getCamera());
         params.setDepthBuffer(SNAPSHOT_PARAMS.isDepthBuffer());
         params.setTransform(SNAPSHOT_PARAMS.getTransform());
         params.setFill(SNAPSHOT_PARAMS.getFill());
         params.setViewport(viewport);
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
      double finalWidth = width;
      double finalHeight = initialHeight * scaleY;
      SnapshotParameters params = SNAPSHOT_PARAMS;
      Rectangle2D viewport = new Rectangle2D(0, 0, finalWidth, finalHeight);
      if (params == null) {
         params = new SnapshotParameters();
         params.setViewport(viewport);
      } else {
         params = new SnapshotParameters();
         params.setCamera(SNAPSHOT_PARAMS.getCamera());
         params.setDepthBuffer(SNAPSHOT_PARAMS.isDepthBuffer());
         params.setTransform(SNAPSHOT_PARAMS.getTransform());
         params.setFill(SNAPSHOT_PARAMS.getFill());
         params.setViewport(viewport);
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
      WritableImage image = snapshotImpl(params);
      return image;
   }

   /**
    * Convert the Node tree to an image.
    *
    * @param params the parameters
    * @return the Image
    */
   public Image toImage(SnapshotParameters params) {
      WritableImage image = snapshotImpl(params);
      return image;
   }

   private WritableImage snapshotImplInJFX(SnapshotParameters params) {
      WritableImage image = this.snapshot(params, null);
      return image;
   }

   /**
    * Saves a snapshot of the image.
    *
    * This method will throw a {@link org.girod.javafx.svgimage.xml.SVGLibraryException} if the
    * snapshot generation generated an exception <b>and</b> {@link GlobalConfig#getExceptionsHandling()} is set to
    * {@link ExceptionsHandling#RETROW_EXCEPTION}.
    * It means that by default the method will simply return false if it could not save the snapshot.
    *
    * Reasons for the save to not being able to generate the snapshot are the directory being read-only, or swing
    * not available.
    *
    * @param params the parameters
    * @param format the format
    * @param file the file
    * @return true if the save was successful
    */
   public boolean snapshot(SnapshotParameters params, String format, File file) throws SVGLibraryException {
      GlobalConfig config = GlobalConfig.getInstance();
      if (config.isSwingAvailable()) {
         try {
            WritableImage image = snapshotImpl(params);
            return AwtImageConverter.snapshot(image, params, format, file);
         } catch (SVGLibraryException ex) {
            config.handleLibraryException(ex);
            return false;
         }
      } else {
         config.handleLibraryError("Swing not available");
         return false;
      }
   }

   /**
    * Saves a snapshot of the image.
    *
    * This method will throw a {@link org.girod.javafx.svgimage.xml.SVGLibraryException} if the
    * snapshot generation generated an exception <b>and</b> {@link GlobalConfig#getExceptionsHandling()} is set to
    * {@link ExceptionsHandling#RETROW_EXCEPTION}. It means that by
    * default the method will simply return false if it could not save the snapshot.
    *
    * Reasons for the save to not being able to generate the snapshot are the directory being read-only, or swing
    * not available.
    *
    * @param format the format
    * @param file the file
    * @return true if the save was successful
    */
   public boolean snapshot(String format, File file) throws SVGLibraryException {
      SnapshotParameters params = SNAPSHOT_PARAMS;
      if (params == null) {
         params = new SnapshotParameters();
         params.setFill(Color.WHITE);
      }
      return snapshot(params, format, file);
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
               WritableImage img = snapshotImplInJFX(params);
               return img;
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
