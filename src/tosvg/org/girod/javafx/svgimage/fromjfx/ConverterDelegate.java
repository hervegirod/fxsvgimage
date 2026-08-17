/*
Copyright (c) 2026 Herve Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://sourceforge.net/projects/jfxconverter/
 */
package org.girod.javafx.svgimage.fromjfx;

import javafx.scene.Node;

/**
 * The ConverterDelegate class allows handle the effective conversion.
 *
 * Note that it is preferable to use the {@link org.girod.javafx.svgimage.fromjfx.JFXConverter} class rather than this one. This class is called internally by the
 * {@link org.girod.javafx.svgimage.fromjfx.JFXConverter} class.
 *
 * @since 1.8
 */
public abstract class ConverterDelegate {
   protected Node root = null;
   protected ConverterParameters params = null;

   public ConverterDelegate(ConverterParameters params) {
      this.params = params;
   }
   
   public ConverterParameters getParameters() {
      return params;
   }
   
   public int getGrayScale() {
      if (params.isSupportingDisabled) {
         return params.grayScalePercent;
      } else {
         return -1;
      }
   }
   
   public boolean hasGrayScale() {
      return params.isSupportingDisabled && params.grayScalePercent > 0;
   }

   /**
    * Return the root Node.
    *
    * @return the root Node
    */
   public Node getRoot() {
      return root;
   }
}
