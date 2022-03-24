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
package org.girod.javafx.svgimage.tosvg.utils;

import java.awt.image.RGBImageFilter;

/**
 * An RGB Filter which changes the opacity of an Image.
 *
 * @since 1.0
 */
public class OpacityFilter extends RGBImageFilter {
   private double opacity = 0;
   private boolean multiply = false;

   /**
    * Constructor.
    *
    * @param opacity the opacity value
    * @param multiply true if the current pixel opacity must be multiplied by the opacity value, false if the opaciy value must
    * replace the current pixel opacity
    */
   public OpacityFilter(double opacity, boolean multiply) {
      this.opacity = opacity;
      this.multiply = multiply;
   }

   /**
    * Convert a single input pixel in the default RGB ColorModel to a single output pixel. The resulting pixel will have the same color but
    * a modified opacity (alpha) value, depending on the <code>multiply</code> flag:
    * <ul>
    * <li>If the <code>multiply</code> flag is set to false, the pixel opacity will be replaced by the <code>opacity</code> value</li>
    * <li>If the <code>multiply</code> flag is set to true, the pixel opacity will be multiplied by the <code>opacity</code> value</li>
    * </ul>
    *
    * @param x the X coordinate of the pixel
    * @param y the Y coordinate of the pixel
    * @param rgb the integer pixel representation in the default RGB color model
    * @return a filtered pixel in the default RGB color model
    */
   @Override
   public int filterRGB(int x, int y, int rgb) {
      int alpha;
      if (multiply) {
         alpha = rgb & 0xFF000000;
         alpha = (int) ((double) alpha * opacity);
      } else {
         alpha = (int) (opacity * 255);
      }
      if (alpha < 0) {
         alpha = 0;
      } else if (alpha > 255) {
         alpha = 255;
      }
      return (rgb & 0x00FFFFFF) + (alpha << 24);
   }

}
