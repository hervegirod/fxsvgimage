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
package org.girod.javafx.svgimage.tosvg.converters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.girod.javafx.svgimage.tosvg.utils.CSSProperties;
import org.girod.javafx.svgimage.tosvg.utils.CSSProperty;
import org.girod.javafx.svgimage.tosvg.utils.NodeConverter;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

/**
 * The abstract Converter class.
 *
 * @since 1.0
 */
public abstract class AbstractConverter implements CSSProperties, NodeConverter {
   /**
    * The ConverterDelegate.
    */
   protected ConverterDelegate delegate = null;
   private final Node node;
   protected final XMLNode xmlParent;
   /**
    * The CSS Properties of the Node to convert, including all the properties.
    */
   protected Map<String, CSSProperty> cssProperties = new HashMap<>();
   /**
    * The CSS Properties of the Node to convert, including only those which have a not null StyleOrigin. These propeties
    * only include those set by the CSS user file or inline for the widget.
    */
   protected Map<String, Object> properties = new HashMap<>();
   /**
    * The CSS Properties of the Node to convert, including only those which have a not null StyleOrigin. These propeties
    * only include those set by the CSS user file or inline for the widget.
    */
   protected Map<String, Object> allProperties = new HashMap<>();

   /**
    * Constructor.
    *
    * @param delegate the ConverterDelegate
    * @param node the Node
    * @param xmlParent the parent xml node
    */
   public AbstractConverter(ConverterDelegate delegate, Node node, XMLNode xmlParent) {
      this.delegate = delegate;
      this.node = node;
      this.xmlParent = xmlParent;
      this.cssProperties = Utilities.extractProperties(node);
      extractSetProperties();
   }

   /**
    * Return the converter Parent Node (may be null). Return null by default.
    *
    * @return the converter Parent Node
    */
   public Parent getParent() {
      return null;
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
    * Return the Transform from the root of the Scene to the converter Node.
    *
    * @param node the Node
    * @return the Transform
    */
   protected Transform getTransformFromAncestor(Node node) {
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

   protected void setClip(StringBuilder buf, String clipID) {
      if (clipID != null) {
         buf.append("clip-path: url(#").append(clipID).append(");");
      }
   }

   /**
    * Return the CSS properties Map of the Node. These properties include those which are set by default (null
    * StyleOrigin).
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
    * Return the CSS properties Map of the Node. These propeties only include those set by the CSS user file or inline
    * for the widget.
    *
    * @return the CSS properties Map
    */
   @Override
   public Map<String, Object> getProperties() {
      return properties;
   }

   /**
    * Return true if the Transform should be applied. The algorithm will allow to not apply any transform which is
    * equivalent to an identity Transform (such as a Rotation with an angle equals to 0).
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
      } else if (tr.isIdentity()) {
         toApply = false;
      }
      return toApply;
   }

   /**
    * Apply all the Nodes transformations on the ConverterDelegate.
    *
    * @param xmlNode the node
    */
   public void applyTransforms(XMLNode xmlNode) {
      StringBuilder buf = new StringBuilder();
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
            double a = affine.getMxx();
            double b = affine.getMyx();
            double c = affine.getMxy();
            double d = affine.getMyy();
            double e = affine.getTx();
            double f = affine.getTy();
            buf.append("matrix(").append(a).append(" ").append(b).append(" ").append(c).append(" ").append(d).append(" ").append(e).append(" ").append(f).append(")");
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
      boolean hasPrevious = false;
      if (affine != null) {
         double a = affine.getMxx();
         double b = affine.getMyx();
         double c = affine.getMxy();
         double d = affine.getMyy();
         double e = affine.getTx();
         double f = affine.getTy();
         hasPrevious = true;
         buf.append("matrix(").append(a).append(" ").append(b).append(" ").append(c).append(" ").append(d).append(" ").append(e).append(" ").append(f).append(")");
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
         if (hasPrevious) {
            buf.append(" ");
         }
         hasPrevious = true;
         buf.append("translate(").append(tx).append(" ").append(ty).append(")");
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
         if (hasPrevious) {
            buf.append(" ");
         }
         hasPrevious = true;
         buf.append("scale(").append(scaleX).append(" ").append(scaleY).append(")");
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
         if (hasPrevious) {
            buf.append(" ");
         }
         hasPrevious = true;
         buf.append("rotate(").append(angle).append(" ").append(centerX).append(" ").append(centerY).append(")");
      }
      if (hasPrevious) {
         xmlNode.addAttribute("transform", buf.toString());
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

   protected void addStroke(Paint paint, StringBuilder buf) {
      if (paint instanceof Color) {
         Color color = (Color) paint;
         String colS = Utilities.convertColor(color);
         buf.append("stroke:").append(colS).append(";");
      } else {
         buf.append("stroke:none;");
      }
   }

   protected void addFill(Paint paint, StringBuilder buf) {
      if (paint instanceof Color) {
         Color color = (Color) paint;
         String colS = Utilities.convertColor(color);
         buf.append("fill:").append(colS).append(";");
      } else {
         buf.append("fill:none;");
      }
   }

   protected void setStrokeOpacity(Paint paint, XMLNode node) {
      if (paint instanceof Color) {
         Color color = (Color) paint;
         if (color.getOpacity() != 1) {
            node.addAttribute("stroke-opacity", color.getOpacity());
         }
      }
   }

   protected void setFillOpacity(Paint paint, XMLNode node) {
      if (paint instanceof Color) {
         Color color = (Color) paint;
         if (color.getOpacity() != 1) {
            node.addAttribute("fill-opacity", color.getOpacity());
         }
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
