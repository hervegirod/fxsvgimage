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
package org.girod.javafx.svgimage.xml;

import javafx.scene.Node;

/**
 * Represents a viewbox for a symbol.
 *
 * @since 0.5.6
 */
public class Viewbox {
   private double width = 0;
   private double height = 0;
   private double viewboxWidth = 0;
   private double viewboxHeight = 0;
   private boolean preserveAspectRatio = true;

   public Viewbox(double width, double height) {
      this.width = width;
      this.height = height;
   }

   public void setPreserveAspectRatio(boolean preserveAspectRatio) {
      this.preserveAspectRatio = preserveAspectRatio;
   }

   public boolean isPreservingAspectRatio() {
      return preserveAspectRatio;
   }

   public void setViewbox(double viewboxWidth, double viewboxHeight) {
      this.viewboxWidth = viewboxWidth;
      this.viewboxHeight = viewboxHeight;
   }

   public void scaleNode(Node node) {
      if (!preserveAspectRatio) {
         node.setScaleX(width / viewboxWidth);
         node.setScaleY(height / viewboxHeight);
      }
   }

   public double scaleValue(boolean isWidth, double value) {
      if (preserveAspectRatio) {
         return value * width / viewboxWidth;
      } else if (isWidth) {
         return value;
      } else {
         return value;
      }
   }

   /**
    * Return the width.
    *
    * @return the width
    */
   public double getViewboxWidth() {
      return viewboxWidth;
   }

   /**
    * Return the height.
    *
    * @return the height
    */
   public double getViewboxHeight() {
      return viewboxHeight;
   }

   /**
    * Return the width.
    *
    * @return the width
    */
   public double getWidth() {
      return width;
   }

   /**
    * Return the height.
    *
    * @return the height
    */
   public double getHeight() {
      return height;
   }
}
