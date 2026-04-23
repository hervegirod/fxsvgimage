/*------------------------------------------------------------------------------
 * Copyright (C) 2026 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.girod.javafx.utils;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritablePixelFormat;

/**
 * This class convert a JavaFX image to a BufferedImage.
 *
 * @version 1.7.1
 */
public class JavaFXToAwtConverter {
   private JavaFXToAwtConverter() {
   }
   
   public static BufferedImage toAwt2(Image image) {
      BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
      return bi;
   }

   public static BufferedImage toAwt(Image image) {
      int width = (int) image.getWidth();
      int height = (int) image.getHeight();
      int pixels[] = new int[width * height];

      image.getPixelReader().getPixels(0, 0, width, height, (WritablePixelFormat<IntBuffer>) image.getPixelReader().getPixelFormat(), pixels, 0, width);

      BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            // There may be better ways to do this
            // You'll need to make sure your image's format is correct here
            int pixel = pixels[y * width + x];
            int r = (pixel & 0xFF0000) >> 16;
            int g = (pixel & 0xFF00) >> 8;
            int b = (pixel & 0xFF) >> 0;

            bi.getRaster().setPixel(x, y, new int[]{r, g, b});
         }
      }
      return bi;
   }
}
