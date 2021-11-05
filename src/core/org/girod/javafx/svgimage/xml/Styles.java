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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * Represents a "style" node in the SVG content.
 *
 * @since 0.4
 */
public class Styles {
   public static final short FILL = 0;
   public static final short STROKE = 1;
   public static final short STROKE_WIDTH = 2;
   public static final short STROKE_DASHARRAY = 3;
   private final Map<String, Rule> rules = new HashMap<>();

   public Styles() {
   }

   public void addRule(Rule rule) {
      rules.put(rule.getStyleClass(), rule);
   }

   public boolean hasRule(String styleClass) {
      return rules.containsKey(styleClass);
   }

   public Rule getRule(String styleClass) {
      return rules.get(styleClass);
   }

   public static class Rule {
      private final String styleClass;
      private final Map<String, Property> properties = new HashMap<>();

      public Rule(String styleClass) {
         this.styleClass = styleClass;
      }

      public String getStyleClass() {
         return styleClass;
      }

      public void addProperty(String key, short type, Object value) {
         properties.put(key, new Property(type, value));
      }

      public Map<String, Property> getProperties() {
         return properties;
      }

      public void apply(Shape shape) {
         Iterator<Property> it = properties.values().iterator();
         while (it.hasNext()) {
            Property property = it.next();
            Object value = property.value;
            switch (property.type) {
               case FILL:
                  shape.setFill((Color) value);
                  break;
               case STROKE:
                  shape.setStroke((Color) value);
                  break;
               case STROKE_WIDTH:
                  shape.setStrokeWidth((Double) value);
                  break;
               case STROKE_DASHARRAY:
                  ObservableList<Double> strokeArray = shape.getStrokeDashArray();
                  List<Double> theArray = (List<Double>)value;
                  strokeArray.addAll(theArray);
                  break;                  
            }
         }
      }
   }

   public static class Property {
      public final short type;
      public final Object value;

      public Property(short type, Object value) {
         this.type = type;
         this.value = value;
      }
   }
}
