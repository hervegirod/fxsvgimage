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

import java.util.StringTokenizer;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

/**
 * The shape builder.
 *
 * @since 0.3
 */
public class SVGShapeBuilder {
   private SVGShapeBuilder() {
   }

   /**
    * Build a "rect" element.
    *
    * @param xmlNode the node
    * @return the shape
    */
   public static Shape buildRect(XMLNode xmlNode) {
      double x = 0.0;
      double y = 0.0;

      if (xmlNode.hasAttribute("x")) {
         x = xmlNode.getAttributeValueAsDouble("x");
      }
      if (xmlNode.hasAttribute("y")) {
         y = xmlNode.getAttributeValueAsDouble("y");
      }
      Rectangle rect = new Rectangle(x, y, xmlNode.getAttributeValueAsDouble("width", 0), xmlNode.getAttributeValueAsDouble("height", 0));
      return rect;
   }

   /**
    * Build a "circle" element.
    *
    * @param xmlNode the node
    * @return the shape
    */   
   public static Shape buildCircle(XMLNode xmlNode) {
      Circle circle = new Circle(xmlNode.getAttributeValueAsDouble("cx", 0), xmlNode.getAttributeValueAsDouble("cy", 0), xmlNode.getAttributeValueAsDouble("r", 0));

      return circle;
   }

   /**
    * Build an "ellipse" element.
    *
    * @param xmlNode the node
    * @return the shape
    */     
   public static Shape buildEllipse(XMLNode xmlNode) {
      Ellipse ellipse = new Ellipse(xmlNode.getAttributeValueAsDouble("cx", 0),
              xmlNode.getAttributeValueAsDouble("cy", 0),
              xmlNode.getAttributeValueAsDouble("rx", 0),
              xmlNode.getAttributeValueAsDouble("ry", 0));

      return ellipse;
   }

   /**
    * Build an "path" element.
    *
    * @param xmlNode the node
    * @return the shape
    */       
   public static Shape buildPath(XMLNode xmlNode) {
      SVGPath path = new SVGPath();
      String content = xmlNode.getAttributeValue("d");
      content = content.replace('−', '-');
      path.setContent(content);

      return path;
   }

   /**
    * Build a "polygon" element.
    *
    * @param xmlNode the node
    * @return the shape
    */       
   public static Shape buildPolygon(XMLNode xmlNode) {
      String pointsAttribute = xmlNode.getAttributeValue("points");
      Polygon polygon = new Polygon();

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");
      while (tokenizer.hasMoreTokens()) {
         String point = tokenizer.nextToken();

         StringTokenizer tokenizer2 = new StringTokenizer(point, ",");
         Double x = ParserUtils.parseDoubleProtected(tokenizer2.nextToken());
         Double y = ParserUtils.parseDoubleProtected(tokenizer2.nextToken());

         polygon.getPoints().add(x);
         polygon.getPoints().add(y);
      }

      return polygon;
   }

   /**
    * Build a "line" element.
    *
    * @param xmlNode the node
    * @return the shape
    */         
   public static Shape buildLine(XMLNode xmlNode) {
      if (xmlNode.hasAttribute("x1") && xmlNode.hasAttribute("y1") && xmlNode.hasAttribute("x2") && xmlNode.hasAttribute("y2")) {
         double x1 = xmlNode.getAttributeValueAsDouble("x1");
         double y1 = xmlNode.getAttributeValueAsDouble("y1");
         double x2 = xmlNode.getAttributeValueAsDouble("x2");
         double y2 = xmlNode.getAttributeValueAsDouble("y2");

         return new Line(x1, y1, x2, y2);
      } else {
         return null;
      }
   }

   /**
    * Build a "polyline" element.
    *
    * @param xmlNode the node
    * @return the shape
    */       
   public static Shape buildPolyline(XMLNode xmlNode) {
      Polyline polyline = new Polyline();
      String pointsAttribute = xmlNode.getAttributeValue("points");

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");
      while (tokenizer.hasMoreTokens()) {
         String points = tokenizer.nextToken();
         StringTokenizer tokenizer2 = new StringTokenizer(points, ",");
         double x = ParserUtils.parseDoubleProtected(tokenizer2.nextToken());
         double y = ParserUtils.parseDoubleProtected(tokenizer2.nextToken());
         polyline.getPoints().add(x);
         polyline.getPoints().add(y);
      }

      return polyline;
   }

}
