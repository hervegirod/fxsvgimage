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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import static org.girod.javafx.svgimage.xml.SVGTags.FONT_FAMILY;
import static org.girod.javafx.svgimage.xml.SVGTags.FONT_SIZE;
import static org.girod.javafx.svgimage.xml.SVGTags.HEIGHT;
import static org.girod.javafx.svgimage.xml.SVGTags.HREF;
import static org.girod.javafx.svgimage.xml.SVGTags.WIDTH;
import static org.girod.javafx.svgimage.xml.SVGTags.X;
import static org.girod.javafx.svgimage.xml.SVGTags.Y;

/**
 * The shape builder.
 *
 * @version 0.3.2
 */
public class SVGShapeBuilder implements SVGTags {
   private SVGShapeBuilder() {
   }

   /**
    * Build a "rect" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildRect(XMLNode xmlNode, Viewport viewport) {
      double x = 0.0;
      double y = 0.0;

      if (xmlNode.hasAttribute(X)) {
         x = xmlNode.getAttributeValueAsDouble(X, true, viewport);
      }
      if (xmlNode.hasAttribute(Y)) {
         y = xmlNode.getAttributeValueAsDouble(Y, false, viewport);
      }
      double width = xmlNode.getAttributeValueAsDouble(WIDTH, true, viewport, 0);
      double height = xmlNode.getAttributeValueAsDouble(HEIGHT, false, viewport, 0);
      Rectangle rect = new Rectangle(x, y, width, height);
      return rect;
   }

   /**
    * Build a "circle" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildCircle(XMLNode xmlNode, Viewport viewport) {
      double cx = xmlNode.getAttributeValueAsDouble(CX, true, viewport, 0);
      double cy = xmlNode.getAttributeValueAsDouble(CY, false, viewport, 0);
      double r = xmlNode.getAttributeValueAsDouble(R, true, viewport, 0);
      Circle circle = new Circle(cx, cy, r);

      return circle;
   }

   /**
    * Build a "text" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the Text
    */
   public static Text buildText(XMLNode xmlNode, Viewport viewport) {
      Font font = null;
      if (xmlNode.hasAttribute(FONT_FAMILY) && xmlNode.hasAttribute(FONT_SIZE)) {
         font = Font.font(xmlNode.getAttributeValue(FONT_FAMILY).replace("'", ""),
            xmlNode.getAttributeValueAsDouble(FONT_SIZE));
      }

      String cdata = xmlNode.getCDATA();
      if (cdata != null) {
         double x = xmlNode.getAttributeValueAsDouble(X, true, viewport, 0);
         double y = xmlNode.getAttributeValueAsDouble(Y, false, viewport, 0);
         Text text = new Text(x, y, cdata);
         if (font != null) {
            text.setFont(font);
         }

         return text;
      } else {
         return null;
      }
   }

   /**
    * Build an "image" node.
    *
    * @param xmlNode the node
    * @param url the reference url
    * @param viewport the viewport
    * @return the ImageView
    */
   public static ImageView buildImage(XMLNode xmlNode, URL url, Viewport viewport) {
      double width = xmlNode.getAttributeValueAsDouble(WIDTH, true, viewport, 0);
      double height = xmlNode.getAttributeValueAsDouble(HEIGHT, false, viewport, 0);
      double x = xmlNode.getAttributeValueAsDouble(X, true, viewport, 0);
      double y = xmlNode.getAttributeValueAsDouble(Y, false, viewport, 0);
      String hrefAttribute = xmlNode.getAttributeValue(HREF);

      URL imageUrl = null;
      try {
         imageUrl = new URL(hrefAttribute);
      } catch (MalformedURLException ex) {
         try {
            imageUrl = new URL(url, hrefAttribute);
         } catch (MalformedURLException ex1) {
         }
      }
      if (imageUrl != null) {
         Image image = new Image(imageUrl.toString(), width, height, true, true);
         ImageView view = new ImageView(image);
         view.setX(x);
         view.setY(y);
         return view;
      }

      return null;
   }

   /**
    * Build an "ellipse" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildEllipse(XMLNode xmlNode, Viewport viewport) {
      Ellipse ellipse = new Ellipse(xmlNode.getAttributeValueAsDouble(CX, true, viewport, 0),
         xmlNode.getAttributeValueAsDouble(CY, false, viewport, 0),
         xmlNode.getAttributeValueAsDouble(RX, true, viewport, 0),
         xmlNode.getAttributeValueAsDouble(RY, false, viewport, 0));

      return ellipse;
   }

   /**
    * Build an "path" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildPath(XMLNode xmlNode, Viewport viewport) {
      SVGPath path = new SVGPath();
      String content = xmlNode.getAttributeValue(D);
      content = content.replace('−', '-');
      path.setContent(content);

      return path;
   }

   /**
    * Build a "polygon" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildPolygon(XMLNode xmlNode, Viewport viewport) {
      String pointsAttribute = xmlNode.getAttributeValue("points");
      Polygon polygon = new Polygon();

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");
      while (tokenizer.hasMoreTokens()) {
         String point = tokenizer.nextToken();

         StringTokenizer tokenizer2 = new StringTokenizer(point, ",");
         Double x = ParserUtils.parseDoubleProtected(tokenizer2.nextToken(), true, viewport);
         Double y = ParserUtils.parseDoubleProtected(tokenizer2.nextToken(), false, viewport);

         polygon.getPoints().add(x);
         polygon.getPoints().add(y);
      }

      return polygon;
   }

   /**
    * Build a "line" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildLine(XMLNode xmlNode, Viewport viewport) {
      if (xmlNode.hasAttribute(X1) && xmlNode.hasAttribute(Y1) && xmlNode.hasAttribute(X2) && xmlNode.hasAttribute(Y2)) {
         double x1 = xmlNode.getAttributeValueAsDouble(X1, true, viewport);
         double y1 = xmlNode.getAttributeValueAsDouble(Y1, false, viewport);
         double x2 = xmlNode.getAttributeValueAsDouble(X2, true, viewport);
         double y2 = xmlNode.getAttributeValueAsDouble(Y2, false, viewport);

         return new Line(x1, y1, x2, y2);
      } else {
         return null;
      }
   }

   /**
    * Build a "polyline" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildPolyline(XMLNode xmlNode, Viewport viewport) {
      Polyline polyline = new Polyline();
      String pointsAttribute = xmlNode.getAttributeValue("points");

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");
      while (tokenizer.hasMoreTokens()) {
         String points = tokenizer.nextToken();
         StringTokenizer tokenizer2 = new StringTokenizer(points, ",");
         double x = ParserUtils.parseDoubleProtected(tokenizer2.nextToken(), true, viewport);
         double y = ParserUtils.parseDoubleProtected(tokenizer2.nextToken(), false, viewport);
         polyline.getPoints().add(x);
         polyline.getPoints().add(y);
      }

      return polyline;
   }

}
