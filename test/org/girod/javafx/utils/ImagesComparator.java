/*------------------------------------------------------------------------------
 * Copyright (C) 2026 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.girod.javafx.utils;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * This class allows to compare two images.
 *
 * @version 1.7.1
 */
public class ImagesComparator {
   private final Params params;

   public ImagesComparator() {
      this.params = new Params();
   }

   public ImagesComparator(Params params) {
      this.params = params;
   }

   /**
    * Return the comparison parameters.
    *
    * @return the parameters
    */
   public Params getParams() {
      return params;
   }

   /**
    * Compare two images.
    *
    * @param fileA the first image
    * @param fileB the second image
    * @return the comparison result
    */
   public Result compareImages(File fileA, File fileB) {
      try {
         BufferedImage biA = ImageIO.read(fileA);
         BufferedImage biB = ImageIO.read(fileB);
         return compareImages(biA, biB);
      } catch (IOException e) {
         return new Result(Result.INVALID);
      }
   }

   /**
    * Compare two images.
    *
    * @param fileA the first image
    * @param fileB the second image
    * @return the comparison result
    */
   public Result compareImages(URL fileA, URL fileB) {
      try {
         BufferedImage biA = ImageIO.read(fileA);
         BufferedImage biB = ImageIO.read(fileB);
         return compareImages(biA, biB);
      } catch (IOException e) {
         return new Result(Result.INVALID);
      }
   }

   /**
    * Compare two images.
    *
    * @param biA the first image
    * @param fileB the second image
    * @return the comparison result
    */
   public Result compareImages(BufferedImage biA, URL fileB) {
      try {
         BufferedImage biB = ImageIO.read(fileB);
         return compareImages(biA, biB);
      } catch (IOException e) {
         return new Result(Result.INVALID);
      }
   }

   /**
    * Compare two images.
    *
    * @param biA the first image
    * @param fileB the second image
    * @return the comparison result
    */
   public Result compareImages(BufferedImage biA, File fileB) {
      try {
         BufferedImage biB = ImageIO.read(fileB);
         return compareImages(biA, biB);
      } catch (IOException e) {
         return new Result(Result.INVALID);
      }
   }

   private BufferedImage scale(BufferedImage bi, int width, int height, int toWidth, int toHeight) {
      if (width == toWidth && height == toHeight) {
         return bi;
      } else {
         // see https://stackoverflow.com/questions/4216123/how-to-scale-a-bufferedimage
         BufferedImage after = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_ARGB);
         AffineTransform at = new AffineTransform();
         double scaleX = (double) toWidth / (double) width;
         double scaleY = (double) toHeight / (double) height;
         at.scale(scaleX, scaleY);
         AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
         after = scaleOp.filter(bi, after);
         return after;
      }
   }

   /**
    * Compare two images.
    *
    * @param biA the first image
    * @param biB the second image
    * @return the comparison result
    */
   public Result compareImages(BufferedImage biA, BufferedImage biB) {
      double maxOutsidedeltaELimits = 0;
      int countDiffPixels = 0;
      int widthA = biA.getWidth(null);
      int heightA = biA.getHeight(null);
      int widthB = biB.getWidth(null);
      int heightB = biB.getHeight(null);
      int width = widthA < widthB ? widthA : widthB;
      int height = heightA < heightB ? heightA : heightB;
      if (params.sizeType == Params.SIZE_SCALED && (widthA != widthB || heightA != heightB)) {
         biA = scale(biA, widthA, heightA, width, height);
         biB = scale(biB, widthB, heightB, width, height);
         widthA = width;
         heightA = height;
         widthB = width;
         heightB = height;
      }
      for (int i = 0; i < width; i++) {
         for (int j = 0; j < height; j++) {
            int rgbA = biA.getRGB(i, j);
            Color colA = new Color(rgbA, true);
            int rgbB = biB.getRGB(i, j);
            Color colB = new Color(rgbB, true);
            // we use https://en.wikipedia.org/wiki/Color_difference#CIELAB_%CE%94E* to compute the colors distance
            double deltaRGB = DeltaEDistance.deltaE(colA, colB);
            if (deltaRGB > params.deltaERGB) {
               countDiffPixels++;
               if (deltaRGB > maxOutsidedeltaELimits) {
                  maxOutsidedeltaELimits = deltaRGB;
               }
            }
         }
      }
      int deltaWidth = widthB - widthA;
      if (deltaWidth < 0) {
         deltaWidth = -deltaWidth;
      }
      int deltaHeight = heightB - heightA;
      if (deltaHeight < 0) {
         deltaHeight = -deltaHeight;
      }

      if (params.sizeType == Params.SIZE_UNCHANGED) {
         if (deltaWidth == 0 && deltaHeight != 0) {
            countDiffPixels += deltaHeight;
         } else if (deltaWidth != 0 && deltaHeight == 0) {
            countDiffPixels += deltaWidth;
         } else if (deltaWidth != 0 && deltaHeight != 0) {
            countDiffPixels += deltaWidth * deltaHeight;
         }
      }
      boolean isNotEquals;
      float percentDiffPixels = (float) countDiffPixels / (float) (width * height) * 100f;
      if (params.deltaInPercent) {
         isNotEquals = percentDiffPixels > params.deltaPixels;
      } else {
         isNotEquals = countDiffPixels > params.deltaPixels;
      }
      short state = isNotEquals ? Result.NO_EQUALS : Result.EQUALS;
      Result result = new Result(state, countDiffPixels, percentDiffPixels);
      result.maxOutsidedeltaELimits = maxOutsidedeltaELimits;
      return result;
   }

   /**
    * The comparison parameters.
    *
    * @since 1.3.21
    */
   public static class Params {
      public static final short SIZE_UNCHANGED = 0;
      public static final short SIZE_CROPPED = 1;
      public static final short SIZE_SCALED = 2;
      /**
       * Specified how the size difference is taken into account.
       */
      public short sizeType = SIZE_UNCHANGED;
      /**
       * Set the pixels spread to compute the color.
       */
      public int colorSpread = 0;      
      /**
       * True the maximum difference in rgb value using the deltaE formula for which two pixels will be considered equal.
       */
      public double deltaERGB = 0;
      /**
       * True the maximum difference in the number of different pixels or percentage for the images to be considered equal.
       */
      public double deltaPixels = 0;
      /**
       * True if the check for equality is in percentage of different pixels.
       */
      public boolean deltaInPercent = false;

      public Params() {
      }
   }

   /**
    * The comparison result.
    *
    * @since 1.7.1
    */
   public static class Result {
      /**
       * The state for an invalid image.
       */
      public static final short INVALID = -1;
      /**
       * The state for images which are considered equal.
       */
      public static final short EQUALS = 0;
      /**
       * The state for images which are considered not equal.
       */
      public static final short NO_EQUALS = 1;
      private final short state;
      private int diffPixels = 0;
      private float percentDiffPixels = 0;
      private double maxOutsidedeltaELimits = 0;

      private Result(short state) {
         this.state = state;
      }

      private Result(short state, int diffPixels, float percentDiffPixels) {
         this.state = state;
         this.diffPixels = diffPixels;
         this.percentDiffPixels = percentDiffPixels;
      }
      
      /**
       * Return the number of pixels which are different between the images.
       *
       * @return the number of pixels
       */
      public int countDiffPixels() {
         return diffPixels;
      }      

      /**
       * Return the maximum distance of the two images pixels when outside the fixed deltaE limits.
       *
       * @return the maximum distance of the two images pixels when outside the fixed deltaE limits
       */
      public double getMaximumOutsideDeltaELimits() {
         return maxOutsidedeltaELimits;
      }

      /**
       * Return the percentage of pixels which are different between the images.
       *
       * @return the number of pixels
       */
      public float getPercentDiffPixels() {
         return percentDiffPixels;
      }

      /**
       * Return true if the images are considered as equal.
       *
       * @return true if the images are considered as equal
       */
      public boolean isEquals() {
         return state == EQUALS;
      }

      /**
       * Return the comparison state.
       *
       * @return the comparison state
       */
      public short getState() {
         return state;
      }
   }
}
