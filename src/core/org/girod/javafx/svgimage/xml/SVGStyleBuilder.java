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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import static org.girod.javafx.svgimage.xml.SVGTags.BEVEL;
import static org.girod.javafx.svgimage.xml.SVGTags.BUTT;
import static org.girod.javafx.svgimage.xml.SVGTags.CLASS;
import static org.girod.javafx.svgimage.xml.SVGTags.CLIP_PATH;
import static org.girod.javafx.svgimage.xml.SVGTags.FILL;
import static org.girod.javafx.svgimage.xml.SVGTags.FILL_OPACITY;
import static org.girod.javafx.svgimage.xml.SVGTags.FILTER;
import static org.girod.javafx.svgimage.xml.SVGTags.FONT_FAMILY;
import static org.girod.javafx.svgimage.xml.SVGTags.FONT_SIZE;
import static org.girod.javafx.svgimage.xml.SVGTags.FONT_STYLE;
import static org.girod.javafx.svgimage.xml.SVGTags.FONT_WEIGHT;
import static org.girod.javafx.svgimage.xml.SVGTags.MITER;
import static org.girod.javafx.svgimage.xml.SVGTags.OPACITY;
import static org.girod.javafx.svgimage.xml.SVGTags.ROUND;
import static org.girod.javafx.svgimage.xml.SVGTags.SQUARE;
import static org.girod.javafx.svgimage.xml.SVGTags.STROKE;
import static org.girod.javafx.svgimage.xml.SVGTags.STROKE_DASHARRAY;
import static org.girod.javafx.svgimage.xml.SVGTags.STROKE_DASHOFFSET;
import static org.girod.javafx.svgimage.xml.SVGTags.STROKE_LINECAP;
import static org.girod.javafx.svgimage.xml.SVGTags.STROKE_LINEJOIN;
import static org.girod.javafx.svgimage.xml.SVGTags.STROKE_MITERLIMIT;
import static org.girod.javafx.svgimage.xml.SVGTags.STROKE_WIDTH;
import static org.girod.javafx.svgimage.xml.SVGTags.STYLE;
import static org.girod.javafx.svgimage.xml.SVGTags.TEXT_DECORATION;
import static org.girod.javafx.svgimage.xml.SVGTags.TRANSFORM;

/**
 * This class parse a style declaration.
 *
 * @version 0.5.5
 */
public class SVGStyleBuilder implements SVGTags {
   private static final Pattern STYLES = Pattern.compile("\\.[a-zA-Z_][a-zA-Z0-9_\\-]*\\s*\\{[a-zA-Z0-9_\\-+\\.\\s,:\\#;]+\\}\\s*");
   private static final Pattern RULE_CONTENT = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_\\-]*\\s*:\\s*[a-zA-Z0-9_\\-+,\\.\\#\\.]*");

   private SVGStyleBuilder() {
   }

   public static Styles parseStyle(String content, Viewport viewport) {
      Matcher m = STYLES.matcher(content);
      Styles styles = null;
      while (m.find()) {
         if (styles == null) {
            styles = new Styles();
         }
         String theRule = m.group();
         int parIndex = theRule.indexOf('{');
         String styleClass = theRule.substring(1, parIndex).trim();
         Styles.Rule rule = new Styles.Rule(styleClass);
         boolean isEmpty = true;
         String ruleContent = theRule.substring(parIndex + 1, theRule.length() - 2).trim();
         Matcher m2 = RULE_CONTENT.matcher(ruleContent);
         while (m2.find()) {
            if (isEmpty) {
               styles.addRule(rule);
               isEmpty = false;
            }
            String theProperty = m2.group();
            int index = theProperty.indexOf(':');
            String key = theProperty.substring(0, index).trim();
            String value = theProperty.substring(index + 1, theProperty.length()).trim();
            switch (key) {
               case FILL: {
                  Color col = ParserUtils.getColor(value);
                  rule.addProperty(key, Styles.FILL, col);
                  break;
               }
               case STROKE: {
                  Color col = ParserUtils.getColor(value);
                  rule.addProperty(key, Styles.STROKE, col);
                  break;
               }
               case STROKE_WIDTH: {
                  double width = ParserUtils.parseLengthValue(value, true, null, viewport);
                  rule.addProperty(key, Styles.STROKE_WIDTH, width);
                  break;
               }
               case STROKE_DASHARRAY: {
                  List<Double> list = ParserUtils.parseDashArray(value, viewport);
                  if (list != null) {
                     rule.addProperty(key, Styles.STROKE_DASHARRAY, list);
                  }
                  break;
               }
               case FONT_FAMILY: {
                  String fontFamily = value.replace("'", "");
                  rule.addProperty(key, Styles.FONT_FAMILY, fontFamily);
                  break;
               }
               case FONT_WEIGHT: {
                  FontWeight fontWeight = SVGShapeBuilder.getFontWeight(value);
                  rule.addProperty(key, Styles.FONT_WEIGHT, fontWeight);
                  break;
               }
               case FONT_STYLE: {
                  ExtendedFontPosture fontPosture = SVGShapeBuilder.getExtendedFontPosture(value);
                  rule.addProperty(key, Styles.FONT_STYLE, fontPosture);
                  break;
               }
               case FONT_SIZE: {
                  double size = ParserUtils.parseFontSize(value);
                  rule.addProperty(key, Styles.FONT_SIZE, size);
                  break;
               }
               case TEXT_DECORATION: {
                  rule.addProperty(key, Styles.TEXT_DECORATION, value);
                  break;
               }
               case OPACITY: {
                  double opacity = ParserUtils.parseOpacity(value);
                  if (opacity >= 0) {
                     rule.addProperty(key, Styles.OPACITY, opacity);
                  }
                  break;
               }
               case FILL_OPACITY: {
                  double opacity = ParserUtils.parseOpacity(value);
                  if (opacity >= 0) {
                     rule.addProperty(key, Styles.FILL_OPACITY, opacity);
                  }
                  break;
               }
               case TRANSFORM: {
                  List<Transform> transformList = ParserUtils.extractTransforms(value, viewport);
                  if (!transformList.isEmpty()) {
                     rule.addProperty(key, Styles.TRANSFORM, transformList);
                  }
                  break;
               }
            }
         }
      }
      return styles;
   }

   public static void setNodeStyle(Node node, Map<String, Paint> gradients, XMLNode xmlNode,
      ClippingFactory clippingFactory, Styles svgStyle, boolean effectsSupported, Map<String, FilterSpec> filterSpecs, Viewport viewport) {
      if (node instanceof Shape) {
         Shape shape = (Shape) node;
         if (xmlNode.hasAttribute(FILL)) {
            shape.setFill(ParserUtils.expressPaint(gradients, xmlNode.getAttributeValue(FILL)));
         }

         if (xmlNode.hasAttribute(STROKE)) {
            shape.setStroke(ParserUtils.expressPaint(gradients, xmlNode.getAttributeValue(STROKE)));
         }

         if (xmlNode.hasAttribute(STROKE_WIDTH)) {
            double strokeWidth = xmlNode.getLengthValue(STROKE_WIDTH, 1);
            shape.setStrokeWidth(strokeWidth);
         }

         if (xmlNode.hasAttribute(STROKE_DASHARRAY)) {
            String dashArray = xmlNode.getAttributeValue(STROKE_DASHARRAY);
            applyDash(shape, dashArray, viewport);
         }

         if (xmlNode.hasAttribute(STROKE_DASHOFFSET)) {
            String dashOffset = xmlNode.getAttributeValue(STROKE_DASHOFFSET);
            double offset = LengthParser.parseLength(dashOffset);
            shape.setStrokeDashOffset(offset);
         }

         if (xmlNode.hasAttribute(STROKE_LINEJOIN)) {
            String lineJoin = xmlNode.getAttributeValue(STROKE_LINEJOIN);
            applyLineJoin(shape, lineJoin);
         }

         if (xmlNode.hasAttribute(STROKE_LINECAP)) {
            String lineCap = xmlNode.getAttributeValue(STROKE_LINECAP);
            applyLineCap(shape, lineCap);
         }

         if (xmlNode.hasAttribute(STROKE_MITERLIMIT)) {
            String miterLimit = xmlNode.getAttributeValue(STROKE_MITERLIMIT);
            applyMiterLimit(shape, miterLimit);
         }
      }

      if (xmlNode.hasAttribute(CLASS)) {
         String styleClasses = xmlNode.getAttributeValue(CLASS);
         setStyleClass(node, styleClasses, svgStyle);
      }

      if (xmlNode.hasAttribute(CLIP_PATH) && clippingFactory != null) {
         String content = xmlNode.getAttributeValue(CLIP_PATH);
         setClipPath(node, content, clippingFactory, viewport);
      }

      if (xmlNode.hasAttribute(STYLE)) {
         FontWeight fontWeight = FontWeight.NORMAL;
         FontPosture fontPosture = FontPosture.REGULAR;
         double fontSize = 12d;
         String fontFamily = null;
         String styles = xmlNode.getAttributeValue(STYLE);
         StringTokenizer tokenizer = new StringTokenizer(styles, ";");
         while (tokenizer.hasMoreTokens()) {
            String style = tokenizer.nextToken();

            StringTokenizer tokenizer2 = new StringTokenizer(style, ":");
            String styleName = tokenizer2.nextToken().trim();
            String styleValue = null;
            if (tokenizer2.hasMoreTokens()) {
               styleValue = tokenizer2.nextToken().trim();
            }
            if (styleValue == null) {
               continue;
            }

            switch (styleName) {
               case CLIP_PATH:
                  setClipPath(node, styleValue, clippingFactory, viewport);
                  break;
               case FONT_FAMILY:
                  if (node instanceof Text) {
                     fontFamily = styleValue.replace("'", "");
                  }
                  break;
               case FONT_WEIGHT:
                  if (node instanceof Text) {
                     fontWeight = SVGShapeBuilder.getFontWeight(styleValue);
                  }
                  break;
               case TEXT_DECORATION:
                  if (node instanceof Text) {
                     SVGShapeBuilder.applyTextDecoration((Text) node, styleValue);
                  }
                  break;
               case FONT_STYLE:
                  if (node instanceof Text) {
                     fontPosture = SVGShapeBuilder.applyFontPosture((Text) node, styleValue);
                  }
                  break;
               case FONT_SIZE:
                  if (node instanceof Text) {
                     fontSize = ParserUtils.parseFontSize(styleValue);
                  }
                  break;
               case FILL:
                  if (node instanceof Shape) {
                     ((Shape) node).setFill(ParserUtils.expressPaint(gradients, styleValue));
                  }
                  break;
               case STROKE:
                  if (node instanceof Shape) {
                     ((Shape) node).setStroke(ParserUtils.expressPaint(gradients, styleValue));
                  }
                  break;
               case STROKE_WIDTH:
                  if (node instanceof Shape) {
                     double strokeWidth = LengthParser.parseLength(styleValue);
                     ((Shape) node).setStrokeWidth(strokeWidth);
                  }
                  break;
               case STROKE_DASHARRAY:
                  if (node instanceof Shape) {
                     applyDash(((Shape) node), styleValue, viewport);
                  }
                  break;
               case STROKE_DASHOFFSET:
                  if (node instanceof Shape) {
                     double offset = LengthParser.parseLength(styleValue);
                     ((Shape) node).setStrokeDashOffset(offset);
                  }
                  break;
               case STROKE_LINECAP:
                  if (node instanceof Shape) {
                     applyLineCap(((Shape) node), styleValue);
                  }
                  break;
               case STROKE_MITERLIMIT:
                  if (node instanceof Shape) {
                     applyMiterLimit(((Shape) node), styleValue);
                  }
                  break;
               case STROKE_LINEJOIN:
                  if (node instanceof Shape) {
                     applyLineJoin(((Shape) node), styleValue);
                  }
                  break;
               case OPACITY: {
                  double opacity = ParserUtils.parseOpacity(styleValue);
                  if (opacity >= 0) {
                     node.setOpacity(opacity);
                  }
                  break;
               }
               case FILL_OPACITY: {
                  if (node instanceof Shape) {
                     double fillOpacity = ParserUtils.parseOpacity(styleValue);
                     if (fillOpacity >= 0) {
                        ParserUtils.setFillOpacity(node, fillOpacity);
                     }
                  }
                  break;
               }
               case TRANSFORM: {
                  List<Transform> transformList = ParserUtils.extractTransforms(styleValue, viewport);
                  if (!transformList.isEmpty()) {
                     ObservableList<Transform> nodeTransforms = node.getTransforms();
                     Iterator<Transform> it = transformList.iterator();
                     while (it.hasNext()) {
                        Transform theTransForm = it.next();
                        nodeTransforms.add(theTransForm);
                     }
                  }
                  break;
               }
               case FILTER: {
                  if (effectsSupported) {
                     Effect effect = expressFilter(node, styleValue, filterSpecs);
                     if (effect != null) {
                        node.setEffect(effect);
                     }
                  }
                  break;
               }
               default:
                  break;
            }
         }
         if (node instanceof Text) {
            Font font = Font.font(fontFamily, fontWeight, fontPosture, fontSize);
            ((Text) node).setFont(font);
         }
      }
   }

   private static void applyMiterLimit(Shape shape, String styleValue) {
      try {
         double miterLimit = Double.parseDouble(styleValue);
         shape.setStrokeMiterLimit(miterLimit);
      } catch (NumberFormatException e) {
      }
   }

   private static void applyLineCap(Shape shape, String styleValue) {
      StrokeLineCap linecap = StrokeLineCap.BUTT;
      if (styleValue.equals(ROUND)) {
         linecap = StrokeLineCap.ROUND;
      } else if (styleValue.equals(SQUARE)) {
         linecap = StrokeLineCap.SQUARE;
      } else if (!styleValue.equals(BUTT)) {
         linecap = StrokeLineCap.BUTT;
      }
      shape.setStrokeLineCap(linecap);
   }

   private static void applyLineJoin(Shape shape, String styleValue) {
      StrokeLineJoin linejoin = StrokeLineJoin.MITER;
      if (styleValue.equals(BEVEL)) {
         linejoin = StrokeLineJoin.BEVEL;
      } else if (styleValue.equals(ROUND)) {
         linejoin = StrokeLineJoin.ROUND;
      } else if (!styleValue.equals(MITER)) {
         linejoin = StrokeLineJoin.MITER;
      }
      shape.setStrokeLineJoin(linejoin);
   }

   private static void applyDash(Shape shape, String styleValue, Viewport viewport) {
      ObservableList<Double> array = shape.getStrokeDashArray();
      List<Double> list = ParserUtils.parseDashArray(styleValue, viewport);
      if (list != null) {
         for (int i = 0; i < list.size(); i++) {
            array.add(list.get(i));
         }
      }
   }

   private static void setClipPath(Node node, String spec, ClippingFactory clippingFactory, Viewport viewport) {
      if (spec.startsWith("url(#")) {
         String clipID = ParserUtils.getURL(spec);
         if (clippingFactory.hasClip(clipID)) {
            Shape clipShape = clippingFactory.createClip(clipID, node, viewport);
            if (clipShape != null) {
               node.setClip(clipShape);
            }
         }
      }
   }

   private static void setStyleClass(Node node, String styleClasses, Styles svgStyle) {
      StringTokenizer tok = new StringTokenizer(styleClasses, " ");
      while (tok.hasMoreTokens()) {
         String styleClass = tok.nextToken();
         node.getStyleClass().add(styleClass);
         if (svgStyle != null && svgStyle.hasRule(styleClass)) {
            Styles.Rule rule = svgStyle.getRule(styleClass);
            rule.apply(node);
         }
      }
   }

   private static Effect expressFilter(Node node, String value, Map<String, FilterSpec> filterSpecs) {
      Effect effect = ParserUtils.expressFilter(filterSpecs, node, value);
      return effect;
   }
}
