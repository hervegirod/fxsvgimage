/*
Copyright (c) 2026, Hervé Girod
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
package org.girod.javafx.svgimage.fromjfx.awt.converters;

import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.girod.javafx.svgimage.fromjfx.utils.CSSProperties;
import org.girod.javafx.svgimage.fromjfx.utils.CSSProperty;
import org.girod.javafx.svgimage.fromjfx.awt.utils.AwtUtilities;
import org.girod.javafx.svgimage.fromjfx.utils.Utilities;
import org.girod.javafx.svgimage.fromjfx.awt.NodeGraphics2DConverter;

/**
 * The abstract awt Converter class.
 *
 * @since 1.8
 */
public abstract class AbstractAwtConverter implements CSSProperties, NodeGraphics2DConverter {
   /**
    * The Identity transform.
    */
   protected static final AffineTransform IDENTITY = new AffineTransform();
   /**
    * The AwtConverterDelegate.
    */
   protected AwtConverterDelegate delegate = null;
   private Node node = null;
   /**
    * The CSS Properties of the Node to convert, including all the properties.
    */
   protected Map<String, CSSProperty> cssProperties = new HashMap<>();
   /**
    * The CSS Properties of the Node to convert, including only those which have a not null StyleOrigin. These propeties only include those
    * set by the CSS user file or inline for the widget.
    */
   protected Map<String, Object> properties = new HashMap<>();
   /**
    * The CSS Properties of the Node to convert, including only those which have a not null StyleOrigin. These propeties only include those
    * set by the CSS user file or inline for the widget.
    */
   protected Map<String, Object> allProperties = new HashMap<>();

   /**
    * Constructor.
    *
    * @param converter the AwtConverterDelegate
    * @param node the Node
    */
   public AbstractAwtConverter(AwtConverterDelegate converter, Node node) {
      this.delegate = converter;
      this.node = node;
      this.cssProperties = Utilities.extractProperties(node);
      extractSetProperties();
   }

   /**
    * Return the converter Parent Node (may be null). Return null by default.
    *
    * @return the delegate Parent Node
    */
   public Parent getParent() {
      return null;
   }

   /**
    * Extract the properties to create a Map with only those which have a not null StyleOrigin.
    */
   private void extractSetProperties() {
      Iterator<Entry<String, CSSProperty>> it = cssProperties.entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, CSSProperty> entry = it.next();
         String key = entry.getKey();
         CSSProperty prop = entry.getValue();
         allProperties.put(key, entry.getValue().getValue());
         if (prop.getStyleOrigin() != null) {
            properties.put(key, entry.getValue().getValue());
         }
      }
   }

   /**
    * Return the CSS properties Map of the Node. These properties include those which are set by default (null StyleOrigin).
    *
    * @return the CSS properties Map
    */
   @Override
   public Map<String, CSSProperty> getCSSProperties() {
      return cssProperties;
   }

   /**
    * Return all the CSS properties Map of the Node.
    *
    * @return the CSS properties Map
    */
   @Override
   public Map<String, Object> getAllProperties() {
      return allProperties;
   }

   /**
    * Return the CSS properties Map of the Node. These propeties only include those set by the CSS user file or inline for the widget.
    *
    * @return the CSS properties Map
    */
   @Override
   public Map<String, Object> getProperties() {
      return properties;
   }

   /**
    * Return true if the Transform should be applied. The algorithm will allow to not apply any transform which is equivalent to an identity
    * Transform (such as a Rotation with an angle equals to 0).
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
      }
      return toApply;
   }

   /**
    * Return the Transform from the root of the Scene to the delegate Node.
    *
    * @param node the Node
    * @return the Transform
    */
   public Transform getTransformFromAncestor(Node node) {
      Node ancestor = node.getScene().getRoot();
      Transform nodeFromScene = node.getLocalToSceneTransform();
      Transform ancestorFromScene = ancestor.getLocalToSceneTransform();
      try {
         Transform inverseAncestor = inverseTransform(ancestorFromScene);
         return nodeFromScene.createConcatenation(inverseAncestor);
      } catch (NonInvertibleTransformException ex) {
         // better than nothing, normally we should never go there however
         return node.getLocalToParentTransform();
      }
   }

   /**
    * Inverse a Transform.
    *
    * @param tr the Transform
    * @return the inverse of the Transform
    * @throws javafx.scene.transform.NonInvertibleTransformException if the transform was not invertible
    */
   private Transform inverseTransform(Transform tr) throws NonInvertibleTransformException {
      return tr.createInverse();
   }

   /**
    * Convert a AffineTransform to an AffineTransform
    *
    * @param tr the transform
    * @return the resulting AffineTransform
    */
   protected AffineTransform getTransform(Transform tr) {
      AffineTransform result = new AffineTransform();
      boolean toApply = toApply(tr);
      // don't apply the transform if this is the Identity Transform
      if (toApply) {
         AffineTransform transform = AwtUtilities.getAffineTransform(tr);
         result.concatenate(transform);
      }
      return result;
   }

   /**
    * Return the result of all the Nodes transformations for the Converter Node.
    *
    * @return the resulting AffineTransform
    * @see #applyTransforms()
    */
   protected AffineTransform getTransform() {
      AffineTransform result = new AffineTransform();
      // transformations list
      ObservableList<Transform> transforms = node.getTransforms();
      Iterator<Transform> it = transforms.iterator();
      while (it.hasNext()) {
         Transform tr = it.next();
         boolean toApply = toApply(tr);
         // don't apply the transform if this is the Identity Transform
         if (toApply) {
            AffineTransform transform = AwtUtilities.getAffineTransform(tr);
            result.concatenate(transform);
         }
      }
      // translation
      double translateX = node.getTranslateX() + node.getLayoutX();
      double translateY = node.getTranslateY() + node.getLayoutY();
      if (translateX != 0 || translateY != 0) {
         result.concatenate(AffineTransform.getTranslateInstance(translateX, translateY));
      }
      // scale
      double scaleX = node.getScaleX();
      double scaleY = node.getScaleY();
      if (scaleX != 1 || scaleY != 1) {
         result.concatenate(AffineTransform.getScaleInstance(scaleX, scaleY));
      }
      // rotation
      double rotate = node.getRotate();
      Point3D point = node.getRotationAxis();
      if (rotate != 0) {
         result.concatenate(AffineTransform.getRotateInstance(rotate, point.getX(), point.getY()));
      }
      return result;
   }

   /**
    * Apply all the Nodes transformations on the ConverterDelegate.Will result on the call of several of the following orders:
    * <ul>
    * <li>{@link AwtConverterDelegate#applyTransform(java.awt.geom.AffineTransform)}</li>
    * <li>{@link AwtConverterDelegate#applyTranslation(double, double)}</li>
    * <li>{@link AwtConverterDelegate#applyScale(double, double)}</li>
    * <li>{@link AwtConverterDelegate#applyRotation(double, double, double)}</li>
    * </ul>
    * Note that only transformations which are different from an Identity transform will be applied on the {@link AwtConverterDelegate}.
    *
    * @param withLayout true if the layoutX and layoutY of the node are takne into account
    */
   public void applyTransforms(boolean withLayout) {
      // transformations list
      ObservableList<Transform> transforms = node.getTransforms();
      Iterator<Transform> it = transforms.iterator();
      while (it.hasNext()) {
         Transform tr = it.next();
         boolean toApply = toApply(tr);
         // don't aff the transform if this is the Identity Transform
         if (toApply) {
            AffineTransform transform = AwtUtilities.getAffineTransform(tr);
            delegate.applyTransform(transform);
         }
      }
      // translation
      double translateX = node.getTranslateX();
      double translateY = node.getTranslateY();
      if (withLayout) {
         translateX += node.getLayoutX();
         translateY += node.getLayoutY();
      }
      if (translateX != 0 || translateY != 0) {
         delegate.applyTranslation(translateX, translateY);
      }
      // scale
      double scaleX = node.getScaleX();
      double scaleY = node.getScaleY();
      if (scaleX != 1 || scaleY != 1) {
         delegate.applyScale(scaleX, scaleY);
      }
      // rotation
      double rotate = node.getRotate();
      if (rotate != 0) {
         Bounds bounds = node.getLayoutBounds();
         // rotation is about the center of the layout bounds of the Node
         double centerX = (bounds.getMaxX() - bounds.getMinX()) / 2d + bounds.getMinX();
         double centerY = (bounds.getMaxY() - bounds.getMinY()) / 2d + bounds.getMinY();
         double angle = Math.toRadians(rotate);
         delegate.applyRotation(angle, centerX, centerY);
      }
   }

   public boolean hasVisibility() {
      return properties.containsKey(VISIBILITY);
   }

   public boolean isVisible() {
      if (properties.containsKey(VISIBILITY)) {
         boolean visibility = (Boolean) properties.get(VISIBILITY);
         return visibility;
      } else {
         return true;
      }
   }

   /**
    * Return the opacity of the Node.
    *
    * @return the opacity
    */
   protected double getOpacity() {
      if (properties.containsKey(OPACITY)) {
         Number opacity = (Number) properties.get(OPACITY);
         return opacity.doubleValue();
      } else {
         return node.getOpacity();
      }
   }

   /**
    * Return the additional child Node on this Node (this is for example the case with the graphics Node on
    * {@link javafx.scene.control.Labeled} Nodes). Return null by default.
    *
    * @return the additional child Node on this Node
    */
   public Node getAdditionalNode() {
      return null;
   }
}
