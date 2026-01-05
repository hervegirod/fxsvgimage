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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javax.swing.GrayFilter;

/**
 * Utilities for converting JavaFX transforms and images to AWT equivalents.
 *
 * @since 1.0
 */
public class AwtImageUtilities {
   private static final double ASSUME_ZERO = 0.001d;

   private AwtImageUtilities() {
   }

   /**
    * Creates a disabled version of an Image.
    *
    * @param image the Image
    * @return the disabled version of the Image
    */
   public static Image createDisabledImage(Image image) {
      ImageFilter filter = new GrayFilter(true, 20);
      ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
      java.awt.Image newImage = Toolkit.getDefaultToolkit().createImage(producer);
      return newImage;
   }

   /**
    * Creates a opacified version of an Image. Note that it will always create a more transparent version of the Image.
    *
    * @param image the Image
    * @param opacity the opacity value
    * @param multiply true if the current pixels opacity value must be multiplied by the opacity value, false if the opacity value must replace
    * the current pixel opacity value
    * @return the opacified version of the Image
    */
   public static Image createOpacifiedImage(Image image, double opacity, boolean multiply) {
      OpacityFilter filter = new OpacityFilter(opacity, multiply);
      ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
      java.awt.Image newImage = Toolkit.getDefaultToolkit().createImage(producer);
      return newImage;
   }

   /**
    * Return the rotation angle for an AffineTransform. It will return 0 if the
    * AffineTransform has no rotation Component.
    *
    * @param tr the AffineTransform
    * @return the rotation angle
    */
   public static double getRotationAngle(AffineTransform tr) {
      double m00 = tr.getScaleX();
      double m01 = tr.getShearX();
      double m10 = tr.getShearY();
      double m11 = tr.getScaleY();
      double norm = Math.sqrt(m00 * m00 + m11 * m11);
      if (norm == 0) {
         return m10 >= 0 ? Math.PI / 2 : -Math.PI / 2;
      }
      double xx = m00 / norm;
      double xy = m01 / norm;
      double theta = -Math.atan2(xy, xx);

      if (Math.abs(theta) < ASSUME_ZERO) {
         return 0;
      }
      return theta;
   }

   /**
    * Return true if the Transform should be applied. The algorithm will allow to not apply any transform which is
    * equivalent to an identity Transform (such as a Rotation with an angle equals to 0).
    *
    * @param tr the Transform
    * @return true if the Transform should be applied
    */
   private static boolean toApply(Transform tr) {
      boolean toApply = true;
      if (tr instanceof Rotate) {
         Rotate rotate = (Rotate) tr;
         if (rotate.getAngle() == 0) {
            toApply = false;
         }
      } else if (tr instanceof Scale) {
         Scale scale = (Scale) tr;
         if ((scale.getMxx() == 1) && (scale.getMyy() == 1)) {
            toApply = false;
         }
      } else if (tr instanceof Translate) {
         Translate translate = (Translate) tr;
         if ((translate.getTx() == 0) && (translate.getTy() == 0)) {
            toApply = false;
         }
      }
      return toApply;
   }

   /**
    * Convert a AffineTransform to an AffineTransform
    *
    * @param tr the transform
    * @return the resulting AffineTransform
    */
   public static AffineTransform getTransform(Transform tr) {
      AffineTransform result = new AffineTransform();
      boolean toApply = toApply(tr);
      // don't apply the transform if this is the Identity Transform
      if (toApply) {
         AffineTransform transform = getAffineTransform(tr);
         result.concatenate(transform);
      }
      return result;
   }

   /**
    * Return the Awt AffineTransform corresponding to a JavaFX Transform.
    *
    * @param tr the JavaFX Transform
    * @return the Awt AffineTransform
    */
   private static AffineTransform getAffineTransform(Transform tr) {
      AffineTransform affine;
      if (tr instanceof Rotate) {
         Rotate rotate = (Rotate) tr;
         double angle = Math.toRadians(rotate.getAngle());
         affine = AffineTransform.getRotateInstance(angle, rotate.getPivotX(), rotate.getPivotY());
      } else {
         // The following line is commented out as it has a cut/paste error
         // leading to zero scaling along the y-axis.
         // affine = new AffineTransform(tr.getMxx(), tr.getMyx(), tr.getMxy(), tr.getMxy(), tr.getTx(), tr.getTy());
         // The following line is the correction for that error, courtesy of Mark Schmieder
         affine = new AffineTransform(tr.getMxx(), tr.getMyx(), tr.getMxy(), tr.getMyy(), tr.getTx(), tr.getTy());
      }
      return affine;
   }
}
