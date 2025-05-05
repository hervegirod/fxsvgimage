/*
Copyright (c) 2025 Hervé Girod
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
package org.girod.javafx.svgimage.xml.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.girod.javafx.svgimage.Viewbox;
import org.girod.javafx.svgimage.Viewport;
import static org.girod.javafx.svgimage.xml.builders.SVGShapeBuilder.getFontPosture;
import static org.girod.javafx.svgimage.xml.builders.SVGShapeBuilder.getFontWeight;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.DX;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.DY;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.FONT_FAMILY;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.FONT_SIZE;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.FONT_STYLE;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.FONT_WEIGHT;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.STROKE;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.TEXT_ANCHOR;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.TEXT_DECORATION;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.TSPAN;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.X;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.Y;
import org.girod.javafx.svgimage.xml.parsers.xmltree.ElementNode;
import org.girod.javafx.svgimage.xml.parsers.xmltree.XMLNode;
import org.girod.javafx.svgimage.xml.parsers.xmltree.XMLTextNode;
import org.girod.javafx.svgimage.xml.specs.SpanGroup;

/**
 * The text builder.
 *
 * @since 1.3
 */
public class SVGTextBuilder {
   private SVGTextBuilder() {
   }

   /**
    * Build a "text" element with tspan children.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @param minTextSize the minimum text size
    * @return the Text
    */
   public static SpanGroup buildTSpanGroup(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport, double minTextSize) {
      return buildTSpanGroup(null, xmlNode, bounds, viewbox, viewport, minTextSize);
   }

   /**
    * Build a "text" element with tspan children.
    *
    * @param theText the initial text
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @param minTextSize the minimum text size
    * @return the Text
    */
   public static SpanGroup buildTSpanGroup(Text theText, XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport, double minTextSize) {
      Group group = new Group();
      double x = xmlNode.getLengthValue(X, true, bounds, viewport, 0);
      double y = xmlNode.getLengthValue(Y, false, bounds, viewport, 0);
      if (viewbox != null) {
         x = viewbox.scaleValue(true, x);
         y = viewbox.scaleValue(false, y);
      }
      group.setLayoutX(x);
      group.setLayoutY(y);
      SpanGroup spanGroup = new SpanGroup(group);
      Node previous = null;
      if (theText != null) {
         group.getChildren().add(theText);
         spanGroup.addTSpan(xmlNode, theText);
         previous = theText;
      }

      boolean isFirst = true;
      OffsetX offset = new OffsetX();
      Iterator<ElementNode> it = xmlNode.getAllChildren().iterator();
      while (it.hasNext()) {
         ElementNode theNode = it.next();
         ElementNode elementNode = null;
         List<Node> tspans = null;
         if (theNode instanceof XMLTextNode && !isFirst) {
            XMLTextNode textNode = (XMLTextNode) theNode;
            elementNode = textNode;
            tspans = buildTspan(group, previous, theText.getFont(), xmlNode, textNode, bounds, viewbox, viewport, minTextSize, offset);
         } else if (theNode instanceof XMLNode) {
            XMLNode childNode = (XMLNode) theNode;
            elementNode = childNode;
            String name = childNode.getName();
            switch (name) {
               case TSPAN: {
                  tspans = buildTspan(spanGroup, group, previous, childNode, bounds, viewbox, viewport, minTextSize, offset);
                  if (tspans.isEmpty()) {
                     tspans = null;
                  } else {
                     previous = tspans.get(tspans.size() - 1);
                  }
                  break;
               }
            }
         }
         isFirst = false;
         offset.isFirst = false;
         if (tspans != null && elementNode != null) {
            Iterator<Node> it2 = tspans.iterator();
            while (it2.hasNext()) {
               Node node = it2.next();
               group.getChildren().add(node);
               spanGroup.addTSpan(elementNode, node);
            }
         }
      }

      return spanGroup;
   }

   /**
    * Build a "text" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the Text
    */
   public static Shape buildText(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      boolean hasFamily = xmlNode.hasAttribute(FONT_FAMILY);
      boolean hasSize = xmlNode.hasAttribute(FONT_SIZE);
      String family = null;
      if (hasFamily) {
         family = xmlNode.getAttributeValue(FONT_FAMILY).replace("'", "");
      }
      double size = 12d;
      if (hasSize) {
         size = ParserUtils.parseFontSize(xmlNode.getAttributeValue(FONT_SIZE));
      }
      size = viewport.scaleLength(size);
      FontWeight weight = getFontWeight(xmlNode.getAttributeValue(FONT_WEIGHT));
      FontPosture posture = getFontPosture(xmlNode.getAttributeValue(FONT_STYLE));
      Font font = Font.font(family, weight, posture, size);

      String cdata = xmlNode.getCDATA();
      cdata = BuilderUtils.removeNewLines(cdata);
      if (cdata != null) {
         double x = xmlNode.getPositionValue(X, true, bounds, viewport, 0);
         double y = xmlNode.getPositionValue(Y, false, bounds, viewport, 0);
         if (viewbox != null) {
            x = viewbox.scaleValue(true, x);
            y = viewbox.scaleValue(false, y);
         }
         Text text = new Text(x, y, cdata);
         if (xmlNode.hasAttribute(TEXT_DECORATION)) {
            SVGShapeBuilder.applyTextDecoration(text, xmlNode.getAttributeValue(TEXT_DECORATION));
         }
         if (xmlNode.hasAttribute(TEXT_ANCHOR)) {
            SVGShapeBuilder.applyTextAnchor(text, xmlNode.getAttributeValue(TEXT_ANCHOR));
         }
         if (font != null) {
            text.setFont(font);
         }
         if (viewbox != null) {
            viewbox.scaleNode(text);
         }
         return text;
      } else {
         return null;
      }
   }

   /**
    * Build a "text" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @param minTextSize the minimum font size to create complete texts
    * @return the Text
    */
   public static Node buildTextAsNode(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport, double minTextSize) {
      boolean hasFamily = xmlNode.hasAttribute(FONT_FAMILY);
      boolean hasSize = xmlNode.hasAttribute(FONT_SIZE);
      String family = null;
      if (hasFamily) {
         family = xmlNode.getAttributeValue(FONT_FAMILY).replace("'", "");
      }
      double size = 12d;
      if (hasSize) {
         size = ParserUtils.parseFontSize(xmlNode.getAttributeValue(FONT_SIZE));
      }
      size = viewport.scaleLength(size);
      FontWeight weight = getFontWeight(xmlNode.getAttributeValue(FONT_WEIGHT));
      FontPosture posture = getFontPosture(xmlNode.getAttributeValue(FONT_STYLE));
      Font font = Font.font(family, weight, posture, size);

      String cdata = xmlNode.getCDATA();
      cdata = BuilderUtils.removeNewLines(cdata);
      if (cdata != null) {
         double x = xmlNode.getPositionValue(X, true, bounds, viewport, 0);
         double y = xmlNode.getPositionValue(Y, false, bounds, viewport, 0);
         if (viewbox != null) {
            x = viewbox.scaleValue(true, x);
            y = viewbox.scaleValue(false, y);
         }
         if (size >= minTextSize) {
            Text text = new Text(x, y, cdata);
            text.setFontSmoothingType(FontSmoothingType.LCD);
            if (xmlNode.hasAttribute(TEXT_DECORATION)) {
               SVGShapeBuilder.applyTextDecoration(text, xmlNode.getAttributeValue(TEXT_DECORATION));
            }
            if (xmlNode.hasAttribute(TEXT_ANCHOR)) {
               SVGShapeBuilder.applyTextAnchor(text, xmlNode.getAttributeValue(TEXT_ANCHOR));
            }
            if (font != null) {
               text.setFont(font);
            }
            if (viewbox != null) {
               viewbox.scaleNode(text);
            }
            return text;
         } else {
            // see https://stackoverflow.com/questions/54410475/how-to-fix-distorted-text-with-small-font-size-in-javafx/54411007
            TextHBox box = new TextHBox(cdata, font);
            if (xmlNode.hasAttribute(STROKE)) {
               Paint stroke = ParserUtils.getColor(xmlNode.getAttributeValue(STROKE));
               box.setFill(stroke);
            }
            if (xmlNode.hasAttribute(TEXT_DECORATION)) {
               box.setTextDecoration(xmlNode.getAttributeValue(TEXT_DECORATION));
            }
            if (xmlNode.hasAttribute(TEXT_ANCHOR)) {
               box.setTextAnchor(xmlNode.getAttributeValue(TEXT_DECORATION));
            }
            box.setLayoutX(x);
            box.setLayoutY(y);
            if (viewbox != null) {
               viewbox.scaleNode(box);
            }
            return box;
         }
      } else {
         return null;
      }
   }

   /**
    * Build a "tspan" element.
    *
    * @param group the parent group
    * @param previous the previous node
    * @param font the parent node font
    * @param parentNode the parent text node
    * @param textNode the parent node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @param minTextSize the minimum font size to create complete texts
    * @param offset the offset
    * @return the Text
    */
   public static List<Node> buildTspan(Group group, Node previous, Font font, XMLNode parentNode, XMLTextNode textNode, Bounds bounds, Viewbox viewbox, Viewport viewport, double minTextSize, OffsetX offset) {
      List<Node> tspans = new ArrayList<>();
      String cdata = textNode.getText();
      cdata = BuilderUtils.removeNewLines(cdata);
      double size = font.getSize();
      if (cdata != null) {
         double x = 0;
         double y = 0;
         if (previous != null) {
            x = 0;
            if (!offset.isFirst) {
               double offsetX = BuilderUtils.getTextWidth(previous);
               offsetX = offset.offset(offsetX);
               x += offsetX;
            }
            x = x - group.getLayoutX();
         }
         if (previous != null) {
            y = y + group.getLayoutY();
         }
         if (size >= minTextSize) {
            Text text = new Text(x, y, cdata);
            tspans.add(text);
            if (parentNode.hasAttribute(STROKE)) {
               Paint stroke = ParserUtils.getColor(parentNode.getAttributeValue(STROKE));
               text.setFill(stroke);
            }
            if (parentNode.hasAttribute(TEXT_DECORATION)) {
               SVGShapeBuilder.applyTextDecoration(text, parentNode.getAttributeValue(TEXT_DECORATION));
            }
            text.setFont(font);
         } else {
            // see https://stackoverflow.com/questions/54410475/how-to-fix-distorted-text-with-small-font-size-in-javafx/54411007
            TextHBox box = new TextHBox(cdata, font);
            if (parentNode.hasAttribute(STROKE)) {
               Paint stroke = ParserUtils.getColor(parentNode.getAttributeValue(STROKE));
               box.setFill(stroke);
            }
            if (parentNode.hasAttribute(TEXT_DECORATION)) {
               box.setTextDecoration(parentNode.getAttributeValue(TEXT_DECORATION));
            }
            box.setLayoutX(x);
            box.setLayoutY(y);
            tspans.add(box);
         }
         return tspans;
      } else {
         return null;
      }
   }

   /**
    * Build a "tspan" element.
    *
    * @param spanGroup the span group
    * @param group the parent group
    * @param previous the previous node
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @param minTextSize the minimum font size to create complete texts
    * @param offset the offset
    * @return the Text
    */
   public static List<Node> buildTspan(SpanGroup spanGroup, Group group, Node previous, XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport, double minTextSize, OffsetX offset) {
      List<Node> tspans = new ArrayList<>();
      boolean hasFamily = xmlNode.hasAttribute(FONT_FAMILY);
      boolean hasSize = xmlNode.hasAttribute(FONT_SIZE);
      String family = null;
      if (hasFamily) {
         family = xmlNode.getAttributeValue(FONT_FAMILY).replace("'", "");
      }
      double size = 12d;
      if (hasSize) {
         size = ParserUtils.parseFontSize(xmlNode.getAttributeValue(FONT_SIZE));
      }
      size = viewport.scaleLength(size);
      FontWeight weight = getFontWeight(xmlNode.getAttributeValue(FONT_WEIGHT));
      FontPosture posture = getFontPosture(xmlNode.getAttributeValue(FONT_STYLE));
      Font font;
      if (previous != null && family == null) {
         font = BuilderUtils.getTextFont(previous);
         font = Font.font(font.getFamily(), weight, posture, size);
      } else {
         font = Font.font(family, weight, posture, size);
      }

      String cdata = xmlNode.getCDATA();
      cdata = BuilderUtils.removeNewLines(cdata);
      if (cdata != null) {
         double x = 0;
         double y = 0;
         if (xmlNode.hasAttribute(X)) {
            x = xmlNode.getPositionValue(X, true, bounds, viewport, 0);
         } else if (xmlNode.hasAttribute(DX)) {
            double _x = xmlNode.getPositionValue(DX, true, bounds, viewport, 0);
            if (viewbox != null) {
               _x = viewbox.scaleValue(true, _x);
            }
            if (previous == null) {
               x = _x - group.getLayoutX();
            } else {
               x = _x + BuilderUtils.getTextX(previous) + BuilderUtils.getTextWidth(previous);
            }
         } else if (previous != null) {
            x = 0;
            if (!offset.isFirst) {
               double offsetX = BuilderUtils.getTextWidth(previous);
               offsetX = offset.offset(offsetX);
               x += offsetX;
            }
            //x = x - group.getLayoutX();
            x = x - group.getLayoutBounds().getWidth();
         }
         if (xmlNode.hasAttribute(Y)) {
            y = xmlNode.getPositionValue(Y, false, bounds, viewport, 0);
         } else if (xmlNode.hasAttribute(DY)) {
            double _y = xmlNode.getPositionValue(DY, true, bounds, viewport, 0);
            if (viewbox != null) {
               _y = viewbox.scaleValue(false, _y);
            }
            if (previous == null) {
               y = _y - group.getLayoutY();
            } else {
               y = y + BuilderUtils.getTextY(previous);
            }
         } else if (previous != null) {
            y = y + group.getLayoutY();
         }
         if (size >= minTextSize) {
            Text text = new Text(x, y, cdata);
            tspans.add(text);
            if (xmlNode.hasAttribute(STROKE)) {
               Paint stroke = ParserUtils.getColor(xmlNode.getAttributeValue(STROKE));
               text.setFill(stroke);
            }
            if (xmlNode.hasAttribute(TEXT_DECORATION)) {
               SVGShapeBuilder.applyTextDecoration(text, xmlNode.getAttributeValue(TEXT_DECORATION));
            }
            if (font != null) {
               text.setFont(font);
            }
         } else {
            // see https://stackoverflow.com/questions/54410475/how-to-fix-distorted-text-with-small-font-size-in-javafx/54411007
            TextHBox box = new TextHBox(cdata, font);
            if (xmlNode.hasAttribute(STROKE)) {
               Paint stroke = ParserUtils.getColor(xmlNode.getAttributeValue(STROKE));
               box.setFill(stroke);
            }
            if (xmlNode.hasAttribute(TEXT_DECORATION)) {
               box.setTextDecoration(xmlNode.getAttributeValue(TEXT_DECORATION));
            }
            box.setLayoutX(x);
            box.setLayoutY(y);
            tspans.add(box);
         }
         return tspans;
      } else {
         return null;
      }
   }

   public static class OffsetX {
      private boolean isFirst;
      private double offsetX;

      private OffsetX() {
         this.offsetX = 0;
         this.isFirst = true;
      }

      private OffsetX(double offsetX) {
         this.offsetX = offsetX;
         this.isFirst = true;
      }

      public double offset(double dx) {
         this.offsetX += dx;
         this.isFirst = false;
         return offsetX;
      }
   }
}
