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

import org.girod.javafx.svgimage.Viewport;
import org.girod.javafx.svgimage.xml.parsers.xmltree.XMLNode;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.PercentParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Transform;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;

/**
 * Contains the specification for a radial or linear gradient.
 *
 * @version 1.4
 */
public abstract class GradientSpec implements SVGTags {
   /**
    * Optional reference to another gradient spec.
    */
   protected String href = null;
   /**
    * Gradient identifier.
    */
   protected String id = null;
   /**
    * Source XML node for this gradient.
    */
   protected XMLNode xmlNode = null;
   /**
    * True once the gradient has been resolved.
    */
   protected boolean isResolved = false;
   /**
    * List of stops defined for this gradient.
    */
   protected List<StopSpec> specStops = new ArrayList<>();
   /**
    * Optional list of transforms applied to the gradient.
    */
   protected List<Transform> transformList = null;

   /**
    * Create a gradient spec from an XML node.
    *
    * @param node the gradient XML node
    */
   public GradientSpec(XMLNode node) {
      this.xmlNode = node;
   }

   /**
    * Create a gradient spec from an XML node with a reference href.
    *
    * @param node the gradient XML node
    * @param href the referenced gradient id
    */
   public GradientSpec(XMLNode node, String href) {
      this.xmlNode = node;
      this.href = href;
   }
   
   /**
    * Set the gradient id.
    *
    * @param id the gradient id
    */
   public void setID(String id) {
      this.id = id;
   }
   
   /**
    * Return the gradient id.
    *
    * @return the gradient id
    */
   public String getID() {
      return id;
   }   

   /**
    * Return the backing XML node.
    *
    * @return the XML node
    */
   public XMLNode getNode() {
      return xmlNode;
   }

   /**
    * Return true if the gradient has been resolved.
    *
    * @return true if resolved
    */
   public boolean isResolved() {
      return isResolved;
   }

   /**
    * Return the resolved JavaFX paint.
    *
    * @return the paint
    */
   public abstract Paint getPaint();

   /**
    * Resolve the gradient using referenced gradients and viewport.
    *
    * @param gradients the gradient map by id
    * @param viewport the viewport
    */
   public abstract void resolve(Map<String, GradientSpec> gradients, Viewport viewport);

   /**
    * Set the gradient transform list.
    *
    * @param transformList the transform list
    */
   public void setTransformList(List<Transform> transformList) {
      this.transformList = transformList;
   }

   /**
    * Return the gradient transform list.
    *
    * @return the transform list
    */
   public List<Transform> getTransformList() {
      return transformList;
   }

   /**
    * Parse a gradient position attribute value.
    *
    * @param xmlNode the gradient node
    * @param id the attribute name
    * @return the parsed position
    */
   protected double getGradientPos(XMLNode xmlNode, String id) {
      String attrvalue = xmlNode.getAttributeValue(id);
      if (attrvalue.endsWith("%") && attrvalue.length() > 1) {
         attrvalue = attrvalue.substring(0, attrvalue.length() - 1);
         return ParserUtils.parseDoubleProtected(attrvalue) / 100;
      } else {
         return ParserUtils.parseDoubleProtected(attrvalue);
      }
   }

   /**
    * Map a spread method string to a JavaFX cycle method.
    *
    * @param value the spread method value
    * @return the cycle method
    */
   protected CycleMethod getCycleMethod(String value) {
      switch (value) {
         case SPREAD_REFLECT:
            return CycleMethod.REFLECT;
         case SPREAD_REPEAT:
            return CycleMethod.REPEAT;
         default:
            return CycleMethod.NO_CYCLE;
      }
   }

   /**
    * Add a stop to this gradient.
    *
    * @param offset the stop offset
    * @param opacity the stop opacity
    * @param color the stop color
    * @return the created stop spec
    */
   public StopSpec addStop(double offset, double opacity, Color color) {
      StopSpec stop = new StopSpec(offset, opacity, color);
      specStops.add(stop);
      return stop;
   }

   /**
    * Return the list of stop specifications.
    *
    * @return the stop specs
    */
   public List<StopSpec> getStops() {
      return specStops;
   }

   /**
    * Convert stop specs to JavaFX stops.
    *
    * @param specstops the stop specs
    * @return the JavaFX stops
    */
   protected List<Stop> convertStops(List<GradientSpec.StopSpec> specstops) {
      List<Stop> stops = new ArrayList<>();
      Iterator<GradientSpec.StopSpec> it = specstops.iterator();
      while (it.hasNext()) {
         GradientSpec.StopSpec theStop = it.next();
         Stop stop = new Stop(theStop.offset, theStop.color);
         stops.add(stop);
      }
      return stops;
   }

   /**
    * Build stop specs from XML stop children.
    *
    * @param spec the gradient spec to populate
    * @param xmlNode the gradient XML node
    * @param kindOfGradient the gradient element name
    * @return the stop specs
    */
   protected List<GradientSpec.StopSpec> buildStops(GradientSpec spec, XMLNode xmlNode, String kindOfGradient) {
      List<GradientSpec.StopSpec> stops = new ArrayList<>();
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         if (!childNode.getName().equals(STOP)) {
            continue;
         }
         double offset = 0d;
         String color = null;
         double opacity = 1.0;

         Iterator<String> it2 = childNode.getAttributes().keySet().iterator();
         while (it2.hasNext()) {
            String attrname = it2.next();
            switch (attrname) {
               case OFFSET:
                  offset = PercentParser.parseValue(childNode, attrname);
                  break;
               case STOP_COLOR:
                  color = childNode.getAttributeValue(attrname);
                  break;
               case STOP_OPACITY:
                  opacity = ParserUtils.parseDoubleProtected(childNode.getAttributeValue(attrname));
                  break;
               case STYLE:
                  String style = childNode.getAttributeValue(attrname);
                  StringTokenizer tokenizer = new StringTokenizer(style, ";");
                  while (tokenizer.hasMoreTokens()) {
                     String item = tokenizer.nextToken().trim();
                     if (item.startsWith(STOP_COLOR)) {
                        color = item.substring(11);
                     } else if (item.startsWith(STOP_OPACITY)) {
                        opacity = ParserUtils.parseDoubleProtected(item.substring(13));
                     } else if (item.startsWith(OFFSET)) {
                        offset = PercentParser.parseValue(item.substring(7));
                     }
                  }
                  break;
               default:
                  break;
            }
         }

         if (color != null) {
            Color colour = Color.web(color, opacity);
            GradientSpec.StopSpec stop = spec.addStop(offset, opacity, colour);
            stops.add(stop);
         }
      }

      return stops;
   }

   /**
    * Stop specification for a gradient.
    */
   public static class StopSpec {
      /**
       * Stop offset.
       */
      public final double offset;
      /**
       * Stop opacity.
       */
      public final double opacity;
      /**
       * Stop color.
       */
      public final Color color;

      /**
       * Create a stop specification.
       *
       * @param offset the stop offset
       * @param opacity the stop opacity
       * @param color the stop color
       */
      private StopSpec(double offset, double opacity, Color color) {
         this.offset = offset;
         this.opacity = opacity;
         this.color = color;
      }
   }

   /**
    * Coordinate value with optional proportional flag.
    */
   protected static class Coord {
      /**
       * Coordinate value.
       */
      protected final double value;
      /**
       * True if the value is proportional.
       */
      protected final boolean isProportional;

      /**
       * Create a non-proportional coordinate.
       *
       * @param value the coordinate value
       */
      private Coord(double value) {
         this.value = value;
         this.isProportional = false;
      }

      /**
       * Create a coordinate with an explicit proportional flag.
       *
       * @param value the coordinate value
       * @param isProportional true if proportional
       */
      private Coord(double value, boolean isProportional) {
         this.value = value;
         this.isProportional = true;
      }
   }
}
