/*
Copyright (c) 2021, 2022 Hervé Girod
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
package org.girod.javafx.svgimage.xml.specs;

import org.girod.javafx.svgimage.xml.builders.SVGShapeBuilder;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;

/**
 * Represents a "style" node in the SVG content.
 *
 * @version 1.0
 */
public class Styles {
   /**
    * Style property type for fill.
    */
   public static final short FILL = 0;
   /**
    * Style property type for stroke.
    */
   public static final short STROKE = 1;
   /**
    * Style property type for stroke width.
    */
   public static final short STROKE_WIDTH = 2;
   /**
    * Style property type for stroke dash array.
    */
   public static final short STROKE_DASHARRAY = 3;
   /**
    * Style property type for font size.
    */
   public static final short FONT_SIZE = 4;
   /**
    * Style property type for font weight.
    */
   public static final short FONT_WEIGHT = 5;
   /**
    * Style property type for font style.
    */
   public static final short FONT_STYLE = 6;
   /**
    * Style property type for font family.
    */
   public static final short FONT_FAMILY = 7;
   /**
    * Style property type for text decoration.
    */
   public static final short TEXT_DECORATION = 8;
   /**
    * Style property type for opacity.
    */
   public static final short OPACITY = 9;
   /**
    * Style property type for fill opacity.
    */
   public static final short FILL_OPACITY = 10;
   /**
    * Style property type for transform.
    */
   public static final short TRANSFORM = 11;
   private final Map<String, Rule> rules = new HashMap<>();

   /**
    * Create an empty styles collection.
    */
   public Styles() {
   }

   /**
    * Add a style rule.
    *
    * @param rule the rule to add
    */
   public void addRule(Rule rule) {
      rules.put(rule.getStyleClass(), rule);
   }

   /**
    * Return true if a rule exists for the style class.
    *
    * @param styleClass the style class
    * @return true if the rule exists
    */
   public boolean hasRule(String styleClass) {
      return rules.containsKey(styleClass);
   }

   /**
    * Return the rule for a style class.
    *
    * @param styleClass the style class
    * @return the rule, or null if absent
    */
   public Rule getRule(String styleClass) {
      return rules.get(styleClass);
   }

   /**
    * A style rule with property definitions.
    */
   public static class Rule {
      private final String styleClass;
      private final Map<String, Property> properties = new HashMap<>();

      /**
       * Create a rule for the style class.
       *
       * @param styleClass the style class
       */
      public Rule(String styleClass) {
         this.styleClass = styleClass;
      }

      /**
       * Return the style class name.
       *
       * @return the style class
       */
      public String getStyleClass() {
         return styleClass;
      }

      /**
       * Add a property to the rule.
       *
       * @param key the property key
       * @param type the property type
       * @param value the property value
       */
      public void addProperty(String key, short type, Object value) {
         properties.put(key, new Property(type, value));
      }

      /**
       * Return the property map.
       *
       * @return the properties
       */
      public Map<String, Property> getProperties() {
         return properties;
      }

      /**
       * Apply the rule to a JavaFX node.
       *
       * @param node the node to update
       */
      public void apply(Node node) {
         FontWeight fontWeight = FontWeight.NORMAL;
         ExtendedFontPosture fontPosture = new ExtendedFontPosture(FontPosture.REGULAR);
         double fontSize = 12d;
         String fontFamily = null;
         boolean hasFontProperties = false;

         Iterator<Property> it = properties.values().iterator();
         while (it.hasNext()) {
            Property property = it.next();
            Object value = property.value;
            switch (property.type) {
               case FILL:
                  if (node instanceof Shape) {
                     ((Shape) node).setFill((Color) value);
                  }
                  break;
               case STROKE:
                  if (node instanceof Shape) {
                     ((Shape) node).setStroke((Color) value);
                  }
                  break;
               case STROKE_WIDTH:
                  if (node instanceof Shape) {
                     ((Shape) node).setStrokeWidth((Double) value);
                  }
                  break;
               case STROKE_DASHARRAY:
                  if (node instanceof Shape) {
                     ObservableList<Double> strokeArray = ((Shape) node).getStrokeDashArray();
                     List<Double> theArray = (List<Double>) value;
                     strokeArray.addAll(theArray);
                  }
                  break;
               case FONT_FAMILY:
                  if (node instanceof Text) {
                     fontFamily = ((String) value).replace("'", "");
                     hasFontProperties = true;
                  }
                  break;
               case FONT_WEIGHT:
                  if (node instanceof Text) {
                     fontWeight = (FontWeight) value;
                     hasFontProperties = true;
                  }
                  break;
               case FONT_STYLE:
                  if (node instanceof Text) {
                     fontPosture = (ExtendedFontPosture) value;
                     hasFontProperties = true;
                  }
                  break;
               case FONT_SIZE:
                  if (node instanceof Text) {
                     fontSize = (Double) value;
                     hasFontProperties = true;
                  }
                  break;
               case TEXT_DECORATION:
                  if (node instanceof Text) {
                     SVGShapeBuilder.applyTextDecoration((Text) node, (String) value);
                  }
                  break;
               case OPACITY:
                  if (node instanceof Shape) {
                     double opacity = (Double) value;
                     ((Shape) node).setOpacity(opacity);
                  }
                  break;
               case FILL_OPACITY:
                  if (node instanceof Shape) {
                     double fillOpacity = (Double) value;
                     ParserUtils.setFillOpacity((Shape) node, fillOpacity);
                  }
                  break;
               case TRANSFORM:
                  Transform transform = (Transform) value;
                  node.getTransforms().add(transform);
                  break;
            }
            if (hasFontProperties && node instanceof Text) {
               Font font = Font.font(fontFamily, fontWeight, fontPosture.posture, fontSize);
               if (fontPosture.isOblique) {
                  SVGShapeBuilder.applyFontOblique((Text) node);
               }
               ((Text) node).setFont(font);
            }
         }
      }
   }

   /**
    * A style property definition.
    */
   public static class Property {
      /**
       * Property type identifier.
       */
      public final short type;
      /**
       * Property value.
       */
      public final Object value;

      /**
       * Create a property definition.
       *
       * @param type the property type
       * @param value the property value
       */
      public Property(short type, Object value) {
         this.type = type;
         this.value = value;
      }
   }
}
