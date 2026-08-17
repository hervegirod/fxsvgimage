/*
Copyright (c) 2026 Hervé Girod
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
package org.girod.javafx.svgimage.fromjfx;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * The abstract JavaFX converter.
 *
 * @param <D> the converter delegate
 * @since 1.8
 */
public abstract class AbstractJFXConverter<D extends ConverterDelegate> implements JFXConverter {
   protected D delegate = null;
   protected ConverterParameters defaultParams;

   /**
    * Create a converter.
    */
   public AbstractJFXConverter() {
      this.defaultParams = new ConverterParameters();
   }

   /**
    * Create a converter.
    *
    * @param defaultParams the default parameters
    */
   public AbstractJFXConverter(ConverterParameters defaultParams) {
      this.defaultParams = defaultParams;
   }

   /**
    * Set the default converter parameters.
    *
    * @param defaultParams the default converter parameters
    */
   @Override
   public void setDefaultParameters(ConverterParameters defaultParams) {
      if (defaultParams == null) {
         this.defaultParams = new ConverterParameters();
      } else {
         this.defaultParams = defaultParams;
      }
   }

   /**
    * Return the converter delegate.
    *
    * @return the converter delegate
    */
   @Override
   public D getConverterDelegate() {
      return delegate;
   }

   /**
    * Return the root Node.
    *
    * @return the root Node
    */
   @Override
   public Node getRoot() {
      return delegate.getRoot();
   }

   /**
    * Return true if the Transform should be applied. The algorithm will allow to not apply any transform which is equivalent to an identity Transform
    * (such as a Rotation with an angle equals to 0).
    *
    * @param tr the Transform
    * @return true if the Transform should be applied
    */
   private boolean toApply(Transform tr) {
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
      } else if (tr == null || tr.isIdentity()) {
         toApply = false;
      }
      return toApply;
   }

   /**
    * Return the translation on a node.
    *
    * @param node the node
    * @return the translation
    */
   @Override
   public AffineTransform getTranslation(Node node) {
      AffineTransform tr = getTransform(node);
      return AffineTransform.getTranslateInstance(tr.getTranslateX(), tr.getTranslateY());
   }

   /**
    * Return the viewbox for a node.
    *
    * @param node the node
    * @return the compute of the viewbox for the node
    */
   @Override
   public Rectangle2D getViewbox(Node node) {
      Bounds bounds = node.getBoundsInLocal();
      double width = bounds.getWidth();
      double height = bounds.getHeight();
      double x = bounds.getMinX();
      double y = bounds.getMinY();
      return new Rectangle2D.Double(x, y, width, height);
   }

   /**
    * Return the Affine transform on a node.
    *
    * @return the Affine transform
    */
   @Override
   public AffineTransform getTransform(Node node) {
      // transformations list
      Transform conTransform = null;
      ObservableList<Transform> transforms = node.getTransforms();
      Iterator<Transform> it = transforms.iterator();
      while (it.hasNext()) {
         Transform tr = it.next();
         boolean toApply = toApply(tr);
         // don't aff the transform if this is the Identity Transform
         if (toApply) {
            if (conTransform == null) {
               conTransform = tr;
            } else {
               conTransform = conTransform.createConcatenation(tr);
            }
         }
      }
      Affine affine = null;
      Translate translate = null;
      Scale scale = null;
      Rotate rotate = null;
      Shear shear = null;
      if (conTransform != null) {
         if (conTransform instanceof Affine) {
            affine = (Affine) conTransform;
         } else if (conTransform instanceof Translate) {
            translate = (Translate) conTransform;
         } else if (conTransform instanceof Scale) {
            scale = (Scale) conTransform;
         } else if (conTransform instanceof Rotate) {
            rotate = (Rotate) conTransform;
         } else if (conTransform instanceof Shear) {
            shear = (Shear) conTransform;
         }
      }
      if (affine != null) {
         double a = affine.getMxx();
         double b = affine.getMyx();
         double c = affine.getMxy();
         double d = affine.getMyy();
         double e = affine.getTx();
         double f = affine.getTy();
         return new AffineTransform(e, b, c, d, e, f);
      }
      // translation
      double translateX = node.getTranslateX() + node.getLayoutX();
      double translateY = node.getTranslateY() + node.getLayoutY();
      if (translateX != 0 || translateY != 0) {
         if (translate != null) {
            translate.setX(translate.getTx() + translateX);
            translate.setY(translate.getTy() + translateY);
         } else {
            translate = new Translate(translateX, translateY);
         }
      }
      if (translate != null) {
         double tx = translate.getTx();
         double ty = translate.getTy();
         return AffineTransform.getTranslateInstance(tx, ty);
      }
      // scale
      double scaleX = node.getScaleX();
      double scaleY = node.getScaleY();
      if (scaleX != 1 || scaleY != 1) {
         if (scale != null) {
            scale.setX(scale.getX() + scaleX);
            scale.setY(scale.getY() + scaleY);
         } else {
            scale = new Scale(scaleX, scaleY);
         }
      }
      if (scale != null) {
         scaleX = scale.getX();
         scaleY = scale.getY();
         return AffineTransform.getScaleInstance(scaleX, scaleY);
      }
      // rotation
      double rotation = node.getRotate();
      if (rotation != 0) {
         if (rotate != null) {
            rotate.setAngle(rotate.getAngle() + rotation);
         } else {
            Bounds bounds = node.getLayoutBounds();
            // rotation is about the center of the layout bounds of the Node
            double centerX = (bounds.getMaxX() - bounds.getMinX()) / 2d + bounds.getMinX();
            double centerY = (bounds.getMaxY() - bounds.getMinY()) / 2d + bounds.getMinY();
            double angle = Math.toRadians(rotation);
            rotate = new Rotate();
            rotate.setAngle(angle);
            rotate.setPivotX(centerX);
            rotate.setPivotY(centerY);
         }
      }
      if (rotate != null) {
         double centerX = rotate.getPivotX();
         double centerY = rotate.getPivotY();
         double angle = rotate.getAngle();
         return AffineTransform.getRotateInstance(angle, centerX, centerY);
      }
      return AffineTransform.getTranslateInstance(0, 0);
   }
}
