/*
Copyright (c) 2021, 2022, 2025 Hervé Girod
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
import org.girod.javafx.svgimage.xml.specs.MarkerContext;
import org.girod.javafx.svgimage.xml.specs.MarkerSpec;
import org.girod.javafx.svgimage.xml.specs.FilterSpec;
import org.girod.javafx.svgimage.xml.specs.ExtendedFontPosture;
import org.girod.javafx.svgimage.xml.parsers.xmltree.XMLNode;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.LengthParser;
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
import org.girod.javafx.svgimage.GlobalConfig;
import org.girod.javafx.svgimage.LoaderContext;
import org.girod.javafx.svgimage.xml.parsers.ClippingFactory;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.specs.Styles;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.Viewport;
import org.girod.javafx.svgimage.xml.parsers.xmltree.ElementNode;

/**
 * This class parse a style declaration.
 *
 * @version 1.3
 */
public class SVGStyleBuilder implements SVGTags {
   private static final Pattern STYLES = Pattern.compile("[^{]*\\s*\\{[a-zA-Z0-9_\\-+\\.\\s,:\\#;]+\\}\\s*");
   private static final Pattern RULE_CONTENT = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_\\-]*\\s*:\\s*[a-zA-Z0-9_\\-+,\\.\\#\\.]*");

   private SVGStyleBuilder() {
   }

   private static void addProperty(List<Styles.Rule> styleRules, String key, short type, Object value) {
      Iterator<Styles.Rule> it = styleRules.iterator();
      while (it.hasNext()) {
         Styles.Rule rule = it.next();
         rule.addProperty(key, type, value);
      }
   }

   public static Styles parseStyle(Styles styles, String content, Viewport viewport) {
      Matcher m = STYLES.matcher(content);
      while (m.find()) {
         String theRule = m.group();
         theRule = theRule.trim();
         int parIndex = theRule.indexOf('{');
         String styleClass;
         if (theRule.charAt(0) != '.') {
            styleClass = theRule.substring(0, parIndex).trim();
         } else {
            styleClass = theRule.substring(1, parIndex).trim();
         }
         List<String> styleClasses = new ArrayList<>();
         if (!styleClass.contains(",")) {
            styleClasses.add(styleClass);
         } else {
            StringTokenizer tok = new StringTokenizer(styleClass, ",");
            while (tok.hasMoreTokens()) {
               String tk = tok.nextToken().trim();
               if (tk.charAt(0) == '.') {
                  tk = tk.substring(1);
               }
               styleClasses.add(tk);
            }
         }
         List<Styles.Rule> styleRules = new ArrayList<>();
         Iterator<String> it = styleClasses.iterator();
         while (it.hasNext()) {
            styleClass = it.next();
            if (styles.hasRule(styleClass)) {
               Styles.Rule theStyleRule = styles.getRule(styleClass);
               styleRules.add(theStyleRule);
            } else {
               Styles.Rule theStyleRule = new Styles.Rule(styleClass);
               styles.addRule(theStyleRule);
               styleRules.add(theStyleRule);
            }
         }
         boolean isEmpty = true;
         String ruleContent = theRule.substring(parIndex + 1, theRule.length() - 2).trim();
         Matcher m2 = RULE_CONTENT.matcher(ruleContent);
         while (m2.find()) {
            if (isEmpty) {
               isEmpty = false;
            }
            String theProperty = m2.group();
            int index = theProperty.indexOf(':');
            String key = theProperty.substring(0, index).trim();
            String value = theProperty.substring(index + 1, theProperty.length()).trim();
            switch (key) {
               case FILL: {
                  Color col = ParserUtils.getColor(value);
                  addProperty(styleRules, key, Styles.FILL, col);
                  break;
               }
               case STROKE: {
                  Color col = ParserUtils.getColor(value);
                  addProperty(styleRules, key, Styles.STROKE, col);
                  break;
               }
               case STROKE_WIDTH: {
                  double width = ParserUtils.parseLineWidth(value, viewport);
                  addProperty(styleRules, key, Styles.STROKE_WIDTH, width);
                  break;
               }
               case STROKE_DASHARRAY: {
                  List<Double> list = ParserUtils.parseDashArray(value, viewport);
                  if (list != null) {
                     addProperty(styleRules, key, Styles.STROKE_DASHARRAY, list);
                  }
                  break;
               }
               case FONT_FAMILY: {
                  String fontFamily = value.replace("'", "");
                  addProperty(styleRules, key, Styles.FONT_FAMILY, fontFamily);
                  break;
               }
               case FONT_WEIGHT: {
                  FontWeight fontWeight = SVGShapeBuilder.getFontWeight(value);
                  addProperty(styleRules, key, Styles.FONT_WEIGHT, fontWeight);
                  break;
               }
               case FONT_STYLE: {
                  ExtendedFontPosture fontPosture = SVGShapeBuilder.getExtendedFontPosture(value);
                  addProperty(styleRules, key, Styles.FONT_STYLE, fontPosture);
                  break;
               }
               case FONT_SIZE: {
                  double size = ParserUtils.parseFontSize(value);
                  addProperty(styleRules, key, Styles.FONT_SIZE, size);
                  break;
               }
               case TEXT_DECORATION: {
                  addProperty(styleRules, key, Styles.TEXT_DECORATION, value);
                  break;
               }
               case OPACITY: {
                  double opacity = ParserUtils.parseOpacity(value);
                  if (opacity >= 0) {
                     addProperty(styleRules, key, Styles.OPACITY, opacity);
                  }
                  break;
               }
               case FILL_OPACITY: {
                  double opacity = ParserUtils.parseOpacity(value);
                  if (opacity >= 0) {
                     addProperty(styleRules, key, Styles.FILL_OPACITY, opacity);
                  }
                  break;
               }
               case TRANSFORM: {
                  List<Transform> transformList = TransformUtils.extractTransforms(value, viewport);
                  if (!transformList.isEmpty()) {
                     addProperty(styleRules, key, Styles.TRANSFORM, transformList);
                  }
                  break;
               }
            }
         }
      }
      return styles;
   }
   
   public static MarkerContext setNodeStyle(Node node, ElementNode elementNode, LoaderContext context, Viewport viewport) {
      if (elementNode instanceof XMLNode) {
         return setNodeStyle(null, node, (XMLNode)elementNode, context, viewport);
      } else  {
         return null;
      }
   }   

   public static MarkerContext setNodeStyle(Node node, XMLNode xmlNode, LoaderContext context, Viewport viewport) {
      return setNodeStyle(null, node, xmlNode, context, viewport);
   }

   public static boolean hasFill(XMLNode xmlNode) {
      if (xmlNode.hasAttribute(FILL)) {
         return true;
      } else if (xmlNode.hasAttribute(CLIP_PATH)) {
         return true;
      } else if (xmlNode.hasAttribute(STYLE)) {
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
                  return true;
               case FILL:
                  return true;
            }
         }
         return false;
      } else {
         return false;
      }
   }

   public static MarkerContext setNodeStyle(MarkerContext markerContext, Node node, XMLNode xmlNode, LoaderContext context, Viewport viewport) {
      MarkerContext markerContextR = null;
      Node contextNode = null;
      if (markerContext != null) {
         contextNode = markerContext.getContextNode();
      }
      if (node instanceof Shape) {
         Shape shape = (Shape) node;
         if (markerContext == null) {
            markerContextR = MarkerBuilder.createMarkerContext(xmlNode, context);
         }
         if (xmlNode.hasAttribute(FILL)) {
            Paint fill = ParserUtils.expressPaint(contextNode, context.gradients, xmlNode.getAttributeValue(FILL));
            shape.setFill(fill);
            if (markerContextR != null) {
               markerContextR.setContextFill(fill);
            }
         }

         if (xmlNode.hasAttribute(STROKE)) {
            Paint stroke = ParserUtils.expressPaint(contextNode, context.gradients, xmlNode.getAttributeValue(STROKE));
            shape.setStroke(stroke);
            if (markerContextR != null) {
               markerContextR.setContextStroke(stroke);
            }
         }

         if (xmlNode.hasAttribute(STROKE_WIDTH)) {
            double strokeWidth = xmlNode.getLineWidthValue(STROKE_WIDTH, viewport, 1);
            shape.setStrokeWidth(strokeWidth);
         }

         if (xmlNode.hasAttribute(STROKE_DASHARRAY)) {
            String dashArray = xmlNode.getAttributeValue(STROKE_DASHARRAY);
            applyDash(shape, dashArray, viewport);
         }

         if (xmlNode.hasAttribute(STROKE_DASHOFFSET)) {
            String dashOffset = xmlNode.getAttributeValue(STROKE_DASHOFFSET);
            double offset = LengthParser.parseLength(dashOffset, viewport);
            shape.setStrokeDashOffset(offset);
         }

         if (xmlNode.hasAttribute(STROKE_LINEJOIN)) {
            String lineJoin = xmlNode.getAttributeValue(STROKE_LINEJOIN);
            applyLineJoin(shape, lineJoin);
         } else {
            shape.setStrokeLineJoin(StrokeLineJoin.MITER);
         }

         if (xmlNode.hasAttribute(STROKE_LINECAP)) {
            String lineCap = xmlNode.getAttributeValue(STROKE_LINECAP);
            applyLineCap(shape, lineCap);
         } else {
            shape.setStrokeLineCap(StrokeLineCap.BUTT);
         }

         if (xmlNode.hasAttribute(STROKE_MITERLIMIT)) {
            String miterLimit = xmlNode.getAttributeValue(STROKE_MITERLIMIT);
            applyMiterLimit(shape, miterLimit, viewport);
         } else {
            shape.setStrokeMiterLimit(4d);
         }
      }

      if (xmlNode.hasAttribute(CLASS)) {
         String styleClasses = xmlNode.getAttributeValue(CLASS);
         setStyleClass(node, styleClasses, context.svgStyle);
      } else {
         setDefaultStyleClass(xmlNode, node, context.svgStyle);
      }

      if (xmlNode.hasAttribute(CLIP_PATH) && context.clippingFactory != null) {
         String content = xmlNode.getAttributeValue(CLIP_PATH);
         setClipPath(node, content, context.clippingFactory, viewport);
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
                  setClipPath(node, styleValue, context.clippingFactory, viewport);
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
                     fontSize = viewport.scaleLength(fontSize);
                  }
                  break;
               case MARKER_START: {
                  String id = ParserUtils.getURL(styleValue);
                  if (markerContextR != null && context.hasMarker(id)) {
                     MarkerSpec marker = context.getMarker(id);
                     markerContextR.setMarkerStart(marker);
                  }
                  break;
               }
               case MARKER_MID: {
                  String id = ParserUtils.getURL(styleValue);
                  if (markerContextR != null && context.hasMarker(id)) {
                     MarkerSpec marker = context.getMarker(id);
                     markerContextR.setMarkerMid(marker);
                  }
                  break;
               }
               case MARKER_END: {
                  String id = ParserUtils.getURL(styleValue);
                  if (markerContextR != null && context.hasMarker(id)) {
                     MarkerSpec marker = context.getMarker(id);
                     markerContextR.setMarkerEnd(marker);
                  }
                  break;
               }
               case FILL:
                  if (node instanceof Shape) {
                     Paint fill = ParserUtils.expressPaint(contextNode, context.gradients, styleValue);
                     ((Shape) node).setFill(fill);
                     if (markerContextR != null) {
                        markerContextR.setContextFill(fill);
                     }
                  }
                  break;
               case STROKE:
                  if (node instanceof Shape) {
                     Paint stroke = ParserUtils.expressPaint(contextNode, context.gradients, styleValue);
                     ((Shape) node).setStroke(stroke);
                     if (markerContextR != null) {
                        markerContextR.setContextStroke(stroke);
                     }
                  }
                  break;
               case STROKE_WIDTH:
                  if (node instanceof Shape) {
                     double strokeWidth = LengthParser.parseLineWidth(styleValue, viewport);
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
                     double offset = LengthParser.parseLength(styleValue, viewport);
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
                     applyMiterLimit(((Shape) node), styleValue, viewport);
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
                  List<Transform> transformList = TransformUtils.extractTransforms(styleValue, viewport);
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
                  if (context.effectsSupported) {
                     Effect effect = expressFilter(node, styleValue, context.filterSpecs);
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
      return markerContextR;
   }

   private static void applyMiterLimit(Shape shape, String styleValue, Viewport viewport) {
      try {
         double miterLimit = viewport.scaleLength(Double.parseDouble(styleValue));
         shape.setStrokeMiterLimit(miterLimit);
      } catch (NumberFormatException e) {
         GlobalConfig.getInstance().handleParsingError("MiterLimit " + styleValue + " is not a number");
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

   public static void setClipPath(Node node, String spec, ClippingFactory clippingFactory, Viewport viewport) {
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

   private static void setDefaultStyleClass(XMLNode xmlNode, Node node, Styles svgStyle) {
      String styleClass = xmlNode.getName();
      if (svgStyle != null && svgStyle.hasRule(styleClass)) {
         Styles.Rule rule = svgStyle.getRule(styleClass);
         rule.apply(node);
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
