/*
Copyright (c) 2021, 2022, 2025 Hervé Girod
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

import javafx.scene.layout.BorderPane;

/**
 * The SVGImageRegion allows to use the image in a layout (for example a BorderPane, or a StackPane), and redimension
 * the image accordingly.
 *
 * @since 1.2
 */
public class SVGImageRegion extends BorderPane {
   private final SVGImage svgImage;
   private Viewport viewport;
   private double scaleX = 1d;
   private double scaleY = 1d;
   private boolean isConform = false;

   SVGImageRegion(SVGImage svgImage) {
      this.svgImage = svgImage;
   }

   void setup() {
      this.setCenter(svgImage);
      this.setMinWidth(1d);
      this.setMinHeight(1d);
   }

   /**
    * Set if the resulting image must be conform.
    *
    * @param isConform true if the resulting image must be conform
    */
   public void setConform(boolean isConform) {
      this.isConform = isConform;
   }

   /**
    * Return true if the resulting image must be conform.
    *
    * @return true if the resulting image must be conform
    */
   public boolean isConform() {
      return isConform;
   }

   @Override
   public void relocate(double x, double y) {
      super.relocate(x, y);
      if (viewport == null) {
         viewport = svgImage.getViewport();
      }
      double widthInitial = viewport.getBestWidth();
      double heightInitial = viewport.getBestHeight();
      double width = widthInitial * scaleX;
      double height = heightInitial * scaleY;
      double widthRegion = this.getWidth();
      double heightRegion = this.getHeight();
      svgImage.setTranslateX(widthRegion / 2d - width / 2d);
      svgImage.setTranslateY(heightRegion / 2d - height / 2d);
   }

   @Override
   public void resize(double width, double height) {
      super.resize(width, height);
      if (viewport == null) {
         viewport = svgImage.getViewport();
      }      
      double widthInitial = viewport.getBestWidth();
      double heightInitial = viewport.getBestHeight();
      if (isConform) {
         if (width <= height) {
            scaleX = width / widthInitial;
            scaleY = scaleX;
         } else {
            scaleY = height / heightInitial;
            scaleX = scaleY;
         }
      } else {
         scaleX = width / widthInitial;
         scaleY = height / heightInitial;

      }
      svgImage.setScaleX(scaleX);
      svgImage.setScaleY(scaleY);
   }
}
