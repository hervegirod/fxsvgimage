/*
Copyright (c) 2022, Hervé Girod
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
package org.girod.javafx.svgimage.tosvg.wrappers;

import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;

/**
 * Wraps the definition for a JavaFX Border around a Region.
 *
 * <h2>Limitations</h2>
 * For the moment we only support Borders with Paint, and with only one Paint and width for all the Region.
 *
 * @version 0.21
 */
public class BorderWrapper {
   private Paint paint = null;
   private double width = 1;
   private CornerRadii radii = null;
   private BorderStrokeStyle strokeStyle = null;
   private double x = 0;
   private double y = 0;

   /**
    * Constructor.
    *
    * @param paint the Paint
    * @param width the width of the Border
    */
   public BorderWrapper(Paint paint, double width) {
      this.paint = paint;
      this.width = width;
   }

   /**
    * Set the stroke style.
    *
    * @param strokeStyle the stroke style
    */
   public void setStrokeStyle(BorderStrokeStyle strokeStyle) {
      this.strokeStyle = strokeStyle;
   }

   /**
    * Return the stroke style (can be null).
    *
    * @return the stroke style
    */
   public BorderStrokeStyle getStrokeStyle() {
      return strokeStyle;
   }

   /**
    * Set the position of the wrapper.
    *
    * @param x the X position
    * @param y the Y position
    */
   public void setPosition(double x, double y) {
      this.x = x;
      this.y = y;
   }

   /**
    * Return the X position of the wrapper.
    *
    * @return the X position of the wrapper
    */
   public double getX() {
      return x;
   }

   /**
    * Return the Y position of the wrapper.
    *
    * @return the Y position of the wrapper
    */
   public double getY() {
      return y;
   }

   /**
    * Return the Paint of the wrapper.
    *
    * @return the Paint of the wrapper
    */
   public Paint getPaint() {
      return paint;
   }

   /**
    * Set the corners Radii of the wrapper.
    *
    * @param radii the Radii
    */
   public void setRadii(CornerRadii radii) {
      this.radii = radii;
   }

   /**
    * Return the corners Radii of the wrapper.
    *
    * @return the Radii
    */
   public CornerRadii getRadii() {
      return radii;
   }

   /**
    * Return true if the wrapper has a CornerRadii (means that at least one corner radius is 0).
    *
    * @return true if the wrapper has a CornerRadii
    */
   public boolean hasRadii() {
      return radii != null
         && (radii.getBottomLeftHorizontalRadius() != 0 || radii.getBottomLeftVerticalRadius() != 0
         || radii.getBottomRightHorizontalRadius() != 0 || radii.getBottomRightVerticalRadius() != 0
         || radii.getTopLeftHorizontalRadius() != 0 || radii.getTopLeftVerticalRadius() != 0
         || radii.getTopRightHorizontalRadius() != 0 || radii.getTopRightVerticalRadius() != 0);
   }

   /**
    * Return the width of the wrapper.
    *
    * @return the width of the wrapper
    */
   public double getWidth() {
      return width;
   }

   /**
    * Return the mean width value of the corners Radii.
    *
    * @return the mean width value of the corners Radii
    */
   public int getMeanRadiiWidth() {
      if (radii == null) {
         return 0;
      } else {
         double mean = (radii.getBottomLeftHorizontalRadius() + radii.getBottomRightHorizontalRadius()
            + radii.getTopLeftHorizontalRadius() + radii.getTopRightHorizontalRadius()) / 8d;
         return (int) mean;
      }
   }

   /**
    * Return the mean height value of the corners Radii.
    *
    * @return the mean height value of the corners Radii
    */
   public int getMeanRadiiHeight() {
      if (radii == null) {
         return 0;
      } else {
         double mean = (radii.getBottomLeftVerticalRadius() + radii.getBottomRightVerticalRadius()
            + radii.getTopLeftVerticalRadius() + radii.getTopRightVerticalRadius()) / 8d;
         return (int) mean;
      }
   }
}
