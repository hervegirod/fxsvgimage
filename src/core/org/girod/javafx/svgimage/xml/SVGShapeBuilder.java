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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import org.girod.javafx.svgimage.xml.FilterSpec.FEDiffuseLighting;
import org.girod.javafx.svgimage.xml.FilterSpec.FESpecularLighting;
import org.girod.javafx.svgimage.LoaderContext;
import org.girod.javafx.svgimage.GlobalConfig;

/**
 * The shape builder.
 *
 * @version 0.6
 */
public class SVGShapeBuilder implements SVGTags {
   private static final Pattern NUMBER = Pattern.compile("\\d+");

   private SVGShapeBuilder() {
   }

   /**
    * Build a "rect" element.
    *
    * @param xmlNode the node
    * @param bounds an optinal bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildRect(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double x = 0.0;
      double y = 0.0;

      if (xmlNode.hasAttribute(X)) {
         x = xmlNode.getPositionValue(X, true, bounds, viewport);
      }
      if (xmlNode.hasAttribute(Y)) {
         y = xmlNode.getPositionValue(Y, false, viewport);
      }
      double width = xmlNode.getLengthValue(WIDTH, true, bounds, viewport, 0);
      double height = xmlNode.getLengthValue(HEIGHT, false, bounds, viewport, 0);
      if (viewbox != null) {
         x = viewbox.scaleValue(true, x);
         y = viewbox.scaleValue(false, y);
         width = viewbox.scaleValue(true, width);
         height = viewbox.scaleValue(false, height);
      }
      Rectangle rect = new Rectangle(x, y, width, height);
      if (xmlNode.hasAttribute(RX)) {
         double rx = 2 * xmlNode.getLengthValue(RX, true, bounds, viewport, 0);
         rect.setArcWidth(rx);
      }
      if (xmlNode.hasAttribute(RY)) {
         double ry = 2 * xmlNode.getLengthValue(RY, false, bounds, viewport, 0);
         rect.setArcHeight(ry);
      }
      if (viewbox != null) {
         viewbox.scaleNode(rect);
      }
      return rect;
   }

   /**
    * Build a "circle" element.
    *
    * @param xmlNode the node
    * @param bounds an optinal bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildCircle(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double cx = xmlNode.getPositionValue(CX, true, bounds, viewport, 0);
      double cy = xmlNode.getPositionValue(CY, false, bounds, viewport, 0);
      double r = xmlNode.getLengthValue(R, true, bounds, viewport, 0);
      if (viewbox != null) {
         cx = viewbox.scaleValue(true, cx);
         cy = viewbox.scaleValue(false, cy);
         r = viewbox.scaleValue(true, r);
      }
      Circle circle = new Circle(cx, cy, r);
      if (viewbox != null) {
         viewbox.scaleNode(circle);
      }
      return circle;
   }

   public static void applyFontOblique(Text text) {
      String style = text.getStyle();
      String addStyle = "-fx-font-style: oblique;";
      if (style == null) {
         text.setStyle(addStyle);
      } else {
         style = style + addStyle;
         text.setStyle(style);
      }
   }

   public static void applyTextDecoration(Text text, String value) {
      String style = text.getStyle();
      String addStyle = null;
      StringTokenizer tok = new StringTokenizer(value, " ");
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken().trim();
         if (tk.isEmpty()) {
            continue;
         }
         if (tk.equals(UNDERLINE)) {
            if (addStyle == null) {
               addStyle = "-fx-underline: true;";
            } else {
               addStyle = addStyle + "-fx-underline: true;";
            }
         } else if (tk.equals(LINE_THROUGH)) {
            if (addStyle == null) {
               addStyle = "-fx-strikethrough: true;";
            } else {
               addStyle = addStyle + "-fx-strikethrough: true;";
            }
         }
      }
      if (addStyle == null) {
         return;
      }
      if (style == null) {
         text.setStyle(addStyle);
      } else {
         style = style + addStyle;
         text.setStyle(style);
      }
   }

   public static FontPosture applyFontPosture(Text text, String value) {
      if (value == null) {
         return FontPosture.REGULAR;
      }
      if (value.equals(OBLIQUE)) {
         String style = text.getStyle();
         String addStyle = "-fx-font-style: oblique;";
         if (style == null) {
            text.setStyle(addStyle);
         } else {
            style = style + addStyle;
            text.setStyle(style);
         }
         return FontPosture.REGULAR;
      }
      FontPosture posture = getFontPosture(value);
      return posture;
   }

   public static ExtendedFontPosture getExtendedFontPosture(String value) {
      if (value == null) {
         return new ExtendedFontPosture(FontPosture.REGULAR);
      }
      if (value.equals(ITALIC)) {
         return new ExtendedFontPosture(FontPosture.ITALIC);
      }
      if (value.equals(OBLIQUE)) {
         return new ExtendedFontPosture(true);
      }
      return new ExtendedFontPosture(FontPosture.REGULAR);
   }

   public static FontPosture getFontPosture(String value) {
      FontPosture posture = FontPosture.REGULAR;
      if (value == null) {
         return posture;
      }
      if (value.equals(ITALIC)) {
         posture = FontPosture.ITALIC;
      }
      return posture;
   }

   public static FontWeight getFontWeight(String value) {
      FontWeight weight = FontWeight.NORMAL;
      if (value == null) {
         return weight;
      }
      if (value.equals(BOLD)) {
         weight = FontWeight.BOLD;
      } else if (value.equals(BOLDER)) {
         weight = FontWeight.EXTRA_BOLD;
      } else if (value.equals(LIGHTER)) {
         weight = FontWeight.LIGHT;
      } else {
         Matcher m = NUMBER.matcher(value);
         if (m.matches()) {
            int weightNumber = Integer.parseInt(value);
            weight = FontWeight.findByWeight(weightNumber);
         }
      }

      return weight;
   }

   /**
    * Build a "text" element with tspan children.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the Text
    */
   public static SpanGroup buildTSpanGroup(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
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
      Text text = null;
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         String name = childNode.getName();
         switch (name) {
            case TSPAN: {
               text = buildTspan(group, text, childNode, bounds, viewbox, viewport);
               break;
            }
         }
         if (text != null) {
            group.getChildren().add(text);
            spanGroup.addTSpan(childNode, text);
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
   public static Text buildText(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
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
    * Build a "tspan" element.
    *
    * @param group the parent group
    * @param previous the previous node
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the Text
    */
   public static Text buildTspan(Group group, Text previous, XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
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
      if (cdata != null) {
         double x = 0;
         double y = 0;
         if (xmlNode.hasAttribute(DX)) {
            x = xmlNode.getPositionValue(DX, true, bounds, viewport, 0);
         } else if (xmlNode.hasAttribute(X)) {
            double _x = xmlNode.getPositionValue(X, true, bounds, viewport, 0);
            if (viewbox != null) {
               _x = viewbox.scaleValue(true, _x);
            }
            x = _x - group.getLayoutX();
         } else if (previous != null) {
            x = previous.getLayoutX() + previous.getLayoutBounds().getWidth();
         }
         if (xmlNode.hasAttribute(DY)) {
            y = xmlNode.getPositionValue(DY, false, bounds, viewport, 0);
         } else if (xmlNode.hasAttribute(Y)) {
            double _y = xmlNode.getPositionValue(Y, true, bounds, viewport, 0);
            if (viewbox != null) {
               _y = viewbox.scaleValue(false, _y);
            }
            y = _y - group.getLayoutY();
         }
         Text text = new Text(x, y, cdata);
         if (xmlNode.hasAttribute(TEXT_DECORATION)) {
            SVGShapeBuilder.applyTextDecoration(text, xmlNode.getAttributeValue(TEXT_DECORATION));
         }
         if (font != null) {
            text.setFont(font);
         }

         return text;
      } else {
         return null;
      }
   }

   private static List<Stop> convertStops(List<GradientSpec.Stop> specstops) {
      List<Stop> stops = new ArrayList<>();
      Iterator<GradientSpec.Stop> it = specstops.iterator();
      while (it.hasNext()) {
         GradientSpec.Stop theStop = it.next();
         Stop stop = new Stop(theStop.offset, theStop.color);
         stops.add(stop);
      }
      return stops;
   }

   private static boolean isPercent(XMLNode xmlNode, String attrname) {
      if (xmlNode.hasAttribute(attrname)) {
         String value = xmlNode.getAttributeValue(attrname);
         return value.endsWith("%");
      } else {
         return true;
      }
   }

   public static void buildRadialGradient(Map<String, GradientSpec> gradientSpecs, Map<String, Paint> gradients, XMLNode xmlNode, Viewport viewport) {
      String id = null;
      Double fx = null;
      Double fy = null;
      Double cx = null;
      Double cy = null;
      Double r = null;
      List<Transform> transformList = null;
      String href = null;
      CycleMethod cycleMethod = CycleMethod.NO_CYCLE;

      boolean isAbsolute = false;
      Iterator<String> it = xmlNode.getAttributes().keySet().iterator();
      while (it.hasNext()) {
         String attrname = it.next();
         switch (attrname) {
            case ID:
               id = xmlNode.getAttributeValue(attrname);
               break;
            case XLINK_HREF:
               href = xmlNode.getAttributeValue(attrname);
               if (href.startsWith("#")) {
                  href = href.substring(1);
               } else {
                  href = null;
               }
               break;
            case GRADIENT_UNITS:
               String gradientUnits = xmlNode.getAttributeValue(attrname);
               if (!gradientUnits.equals(USERSPACE_ON_USE)) {
                  return;
               }
               break;
            case SPREAD_METHOD:
               String methodS = xmlNode.getAttributeValue(attrname);
               cycleMethod = getCycleMethod(methodS);
               break;
            case FX:
               fx = PercentParser.parseValue(xmlNode, attrname, true);
               break;
            case FY:
               fy = PercentParser.parseValue(xmlNode, attrname, true);
               break;
            case CX:
               cx = PercentParser.parseValue(xmlNode, attrname, true);
               isAbsolute = isAbsolute || !isPercent(xmlNode, attrname);
               break;
            case CY:
               cy = PercentParser.parseValue(xmlNode, attrname, true);
               isAbsolute = isAbsolute || !isPercent(xmlNode, attrname);
               break;
            case R:
               r = PercentParser.parseValue(xmlNode, attrname, true);
               isAbsolute = isAbsolute || !isPercent(xmlNode, attrname);
               break;
            case GRADIENT_TRANSFORM:
               transformList = TransformUtils.extractTransforms(xmlNode.getAttributeValue(attrname), viewport);
               break;
            default:
               break;
         }
      }

      GradientSpec spec = new GradientSpec();
      if (id != null) {
         gradientSpecs.put(id, spec);
      }
      List<GradientSpec.Stop> specstops = SVGShapeBuilder.buildStops(spec, xmlNode, RADIAL_GRADIENT);
      if (specstops.isEmpty() && href != null && gradientSpecs.containsKey(href)) {
         specstops = gradientSpecs.get(href).getStops();
      }
      List<Stop> stops = convertStops(specstops);

      if (id != null && cx != null && cy != null && r != null) {
         double fDistance = 0.0;
         double fAngle = 0.0;

         if (transformList != null && !transformList.isEmpty()) {
            Transform concatTransform = null;
            Iterator<Transform> it2 = transformList.iterator();
            while (it2.hasNext()) {
               Transform theTransform = it2.next();
               if (concatTransform == null) {
                  concatTransform = theTransform;
               } else {
                  concatTransform = concatTransform.createConcatenation(theTransform);
               }
            }

            if (concatTransform != null && concatTransform instanceof Affine) {
               double tempCx = cx;
               double tempCy = cy;
               double tempR = r;
               Affine affine = (Affine) concatTransform;
               cx = tempCx * affine.getMxx() + tempCy * affine.getMxy() + affine.getTx();
               cy = tempCx * affine.getMyx() + tempCy * affine.getMyy() + affine.getTy();

               r = Math.sqrt(tempR * affine.getMxx() * tempR * affine.getMxx() + tempR * affine.getMyx() * tempR * affine.getMyx());

               if (fx != null && fy != null) {
                  double tempFx = fx;
                  double tempFy = fy;
                  fx = tempFx * affine.getMxx() + tempFy * affine.getMxy() + affine.getTx();
                  fy = tempFx * affine.getMyx() + tempFy * affine.getMyy() + affine.getTy();
               } else {
                  fAngle = Math.asin(affine.getMyx()) * 180.0 / Math.PI;
                  fDistance = Math.sqrt((cx - tempCx) * (cx - tempCx) + (cy - tempCy) * (cy - tempCy));
               }
            }
         }
         if (fx != null && fy != null) {
            fDistance = Math.sqrt((fx - cx) * (fx - cx) + (fy - cy) * (fy - cy)) / r;
            fAngle = Math.atan2(cy - fy, cx - fx) * 180.0 / Math.PI;
         }

         RadialGradient gradient = new RadialGradient(fAngle, fDistance, cx, cy, r, !isAbsolute, cycleMethod, stops);
         gradients.put(id, gradient);
      }
   }

   private static double getGradientPos(XMLNode xmlNode, String id) {
      String attrvalue = xmlNode.getAttributeValue(id);
      if (attrvalue.endsWith("%") && attrvalue.length() > 1) {
         attrvalue = attrvalue.substring(0, attrvalue.length() - 1);
      }
      return ParserUtils.parseDoubleProtected(attrvalue) / 100;
   }

   private static CycleMethod getCycleMethod(String value) {
      if (value.equals(SPREAD_REFLECT)) {
         return CycleMethod.REFLECT;
      } else if (value.equals(SPREAD_REPEAT)) {
         return CycleMethod.REPEAT;
      } else {
         return CycleMethod.NO_CYCLE;
      }
   }

   public static void buildLinearGradient(Map<String, GradientSpec> gradientSpecs, Map<String, Paint> gradients, XMLNode xmlNode, Viewport viewport) {
      String id = null;
      double x1 = 0;
      double y1 = 0;
      double x2 = 1d;
      double y2 = 0d;
      List<Transform> transformList = null;
      String href = null;
      CycleMethod cycleMethod = CycleMethod.NO_CYCLE;

      Iterator<String> it = xmlNode.getAttributes().keySet().iterator();
      while (it.hasNext()) {
         String attrname = it.next();
         switch (attrname) {
            case ID:
               id = xmlNode.getAttributeValue(attrname);
               break;
            case XLINK_HREF:
               href = xmlNode.getAttributeValue(attrname);
               if (href.startsWith("#")) {
                  href = href.substring(1);
               } else {
                  href = null;
               }
               break;
            case GRADIENT_UNITS:
               String gradientUnits = xmlNode.getAttributeValue(attrname);
               if (!gradientUnits.equals(USERSPACE_ON_USE)) {
                  return;
               }
               break;
            case SPREAD_METHOD:
               String methodS = xmlNode.getAttributeValue(attrname);
               cycleMethod = getCycleMethod(methodS);
               break;
            case X1:
               x1 = getGradientPos(xmlNode, X1);
               break;
            case Y1:
               y1 = getGradientPos(xmlNode, Y1);
               break;
            case X2:
               x2 = getGradientPos(xmlNode, X2);
               break;
            case Y2:
               y2 = getGradientPos(xmlNode, Y2);
               break;
            case GRADIENT_TRANSFORM:
               transformList = TransformUtils.extractTransforms(xmlNode.getAttributeValue(attrname), viewport);
               break;
            default:
               break;
         }
      }

      GradientSpec spec = new GradientSpec();
      if (id != null) {
         gradientSpecs.put(id, spec);
      }
      List<GradientSpec.Stop> specstops = SVGShapeBuilder.buildStops(spec, xmlNode, LINEAR_GRADIENT);
      if (specstops.isEmpty() && href != null && gradientSpecs.containsKey(href)) {
         specstops = gradientSpecs.get(href).getStops();
      }
      List<Stop> stops = convertStops(specstops);

      if (id != null && !(x1 == 0 && y1 == 0 && x2 == 0 && y2 == 0)) {

         if (transformList != null && !transformList.isEmpty()) {
            Transform concatTransform = null;
            Iterator<Transform> it2 = transformList.iterator();
            while (it2.hasNext()) {
               Transform theTransform = it2.next();
               if (concatTransform == null) {
                  concatTransform = theTransform;
               } else {
                  concatTransform = concatTransform.createConcatenation(theTransform);
               }
            }

            if (concatTransform != null && concatTransform instanceof Affine) {
               double x1d = x1;
               double y1d = y1;
               double x2d = x2;
               double y2d = y2;
               Affine affine = (Affine) concatTransform;
               x1 = x1d * affine.getMxx() + y1d * affine.getMxy() + affine.getTx();
               y1 = x1d * affine.getMyx() + y1d * affine.getMyy() + affine.getTy();
               x2 = x2d * affine.getMxx() + y2d * affine.getMxy() + affine.getTx();
               y2 = x2d * affine.getMyx() + y2d * affine.getMyy() + affine.getTy();
            }
         }

         LinearGradient gradient = new LinearGradient(x1, y1, x2, y2, true, cycleMethod, stops);
         gradients.put(id, gradient);
      }
   }

   public static List<GradientSpec.Stop> buildStops(GradientSpec spec, XMLNode xmlNode, String kindOfGradient) {
      List<GradientSpec.Stop> stops = new ArrayList<>();
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
            GradientSpec.Stop stop = spec.addStop(offset, opacity, colour);
            stops.add(stop);
         }
      }

      return stops;
   }

   /**
    * Build a "use" element.
    *
    * @param xmlNode the node
    * @param context the context
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewport the viewport
    * @return the shape
    */
   public static Node buildUse(XMLNode xmlNode, LoaderContext context, Bounds bounds, Viewport viewport) {
      String id = null;
      if (xmlNode.hasAttribute(HREF)) {
         id = xmlNode.getAttributeValue(HREF);
      } else if (xmlNode.hasAttribute(XLINK_HREF)) {
         id = xmlNode.getAttributeValue(XLINK_HREF);
      }
      if (id != null && id.startsWith("#")) {
         id = id.substring(1);
      }

      if (id != null && context.hasNamedNode(id)) {
         XMLNode namedNode = context.getNamedNode(id);
         Node nodeFromUse = null;
         Viewbox viewbox = null;
         String name = namedNode.getName();
         SpanGroup spanGroup = null;
         if (context.hasSymbol(id)) {
            viewbox = context.getSymbol(id).getViewbox();
         }
         switch (name) {
            case RECT:
               nodeFromUse = buildRect(namedNode, null, viewbox, viewport);
               break;
            case CIRCLE:
               nodeFromUse = buildCircle(namedNode, null, viewbox, viewport);
               break;
            case ELLIPSE:
               nodeFromUse = buildEllipse(namedNode, null, viewbox, viewport);
               break;
            case PATH:
               nodeFromUse = buildPath(namedNode, null, viewbox, viewport);
               break;
            case POLYGON:
               nodeFromUse = buildPolygon(namedNode, null, viewbox, viewport);
               break;
            case LINE:
               nodeFromUse = buildLine(namedNode, null, viewbox, viewport);
               break;
            case POLYLINE:
               nodeFromUse = buildPolyline(namedNode, null, viewbox, viewport);
               break;
            case IMAGE:
               nodeFromUse = buildImage(namedNode, context.url, null, viewbox, viewport);
               break;
            case G:
            case SYMBOL:
               nodeFromUse = buildGroupForUse(context, namedNode, viewbox, viewport);
               break;
            case TEXT:
               nodeFromUse = buildText(namedNode, null, viewbox, viewport);
               if (nodeFromUse == null) {
                  spanGroup = buildTSpanGroup(namedNode, null, viewbox, viewport);
               }
               break;
         }
         if (nodeFromUse != null) {
            if (xmlNode.hasAttribute(X)) {
               double x = xmlNode.getPositionValue(X, true, viewport);
               nodeFromUse.setLayoutX(x);
            }
            if (xmlNode.hasAttribute(Y)) {
               double y = xmlNode.getPositionValue(Y, true, viewport);
               nodeFromUse.setLayoutY(y);
            }
            SVGStyleBuilder.setNodeStyle(nodeFromUse, xmlNode, context, viewport);
            return nodeFromUse;
         } else if (spanGroup != null) {
            Map<String, String> theStylesMap = ParserUtils.getStyles(namedNode);
            Iterator<SpanGroup.TSpan> it2 = spanGroup.getSpans().iterator();
            SpanGroup.TSpan previous = null;
            while (it2.hasNext()) {
               SpanGroup.TSpan tspan = it2.next();
               Text tspanText = tspan.text;
               String theStyles = ParserUtils.mergeStyles(theStylesMap, tspan.node);
               tspan.node.addAttribute(STYLE, theStyles);
               addStyles(context, tspanText, tspan.node, viewport);
               if (tspan.node.hasAttribute(BASELINE_SHIFT)) {
                  // http://www.svgbasics.com/font_effects_italic.html
                  // https://stackoverflow.com/questions/50295199/javafx-subscript-and-superscript-text-in-textflow
                  String shiftValue = tspan.node.getAttributeValue(BASELINE_SHIFT);
                  ParserUtils.setBaselineShift(tspanText, shiftValue);
               }
               // https://vanseodesign.com/web-design/svg-text-tspan-element/
               if (!ParserUtils.hasXPosition(tspan.node) && previous != null) {
                  double width = previous.text.getLayoutBounds().getWidth();
                  tspanText.setLayoutX(width + previous.text.getLayoutX());
               }
               previous = tspan;
            }
            return spanGroup.getTextGroup();
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static Group buildGroupForUse(LoaderContext context, XMLNode xmlNode, Viewbox viewbox, Viewport viewport) {
      Group group = new Group();
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         Node node = null;
         SpanGroup spanGroup = null;
         String name = childNode.getName();
         switch (name) {
            case RECT:
               node = buildRect(childNode, null, viewbox, viewport);
               break;
            case CIRCLE:
               node = buildCircle(childNode, null, viewbox, viewport);
               break;
            case ELLIPSE:
               node = buildEllipse(childNode, null, viewbox, viewport);
               break;
            case PATH:
               node = buildPath(childNode, null, viewbox, viewport);
               break;
            case POLYGON:
               node = buildPolygon(childNode, null, viewbox, viewport);
               break;
            case LINE:
               node = buildLine(childNode, null, viewbox, viewport);
               break;
            case POLYLINE:
               node = buildPolyline(childNode, null, viewbox, viewport);
               break;
            case IMAGE:
               node = buildImage(childNode, context.url, null, viewbox, viewport);
               break;
            case TEXT:
               node = buildText(childNode, null, viewbox, viewport);
               if (node == null) {
                  spanGroup = buildTSpanGroup(childNode, null, viewbox, viewport);
               }
               break;
            case G:
               node = buildGroupForUse(context, childNode, viewbox, viewport);
               break;
         }
         if (node != null) {
            addStyles(context, node, childNode, viewport);
            group.getChildren().add(node);
         } else if (spanGroup != null) {
            Map<String, String> theStylesMap = ParserUtils.getStyles(childNode);
            Iterator<SpanGroup.TSpan> it2 = spanGroup.getSpans().iterator();
            SpanGroup.TSpan previous = null;
            while (it2.hasNext()) {
               SpanGroup.TSpan tspan = it2.next();
               Text tspanText = tspan.text;
               String theStyles = ParserUtils.mergeStyles(theStylesMap, tspan.node);
               tspan.node.addAttribute(STYLE, theStyles);
               addStyles(context, tspanText, tspan.node, viewport);
               if (tspan.node.hasAttribute(BASELINE_SHIFT)) {
                  // http://www.svgbasics.com/font_effects_italic.html
                  // https://stackoverflow.com/questions/50295199/javafx-subscript-and-superscript-text-in-textflow
                  String shiftValue = tspan.node.getAttributeValue(BASELINE_SHIFT);
                  ParserUtils.setBaselineShift(tspanText, shiftValue);
               }
               // https://vanseodesign.com/web-design/svg-text-tspan-element/
               if (!ParserUtils.hasXPosition(tspan.node) && previous != null) {
                  double width = previous.text.getLayoutBounds().getWidth();
                  tspanText.setLayoutX(width + previous.text.getLayoutX());
               }
               previous = tspan;
            }
            group.getChildren().add(spanGroup.getTextGroup());
         }
      }

      return group;
   }

   private static void addStyles(LoaderContext context, Node node, XMLNode xmlNode, Viewport viewport) {
      SVGStyleBuilder.setNodeStyle(node, xmlNode, context, viewport);
      ParserUtils.setOpacity(node, xmlNode);
      TransformUtils.setTransforms(node, xmlNode, viewport);
   }

   /**
    * Build an "image" node.
    *
    * @param xmlNode the node
    * @param url the reference url
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the ImageView
    */
   public static ImageView buildImage(XMLNode xmlNode, URL url, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double width = xmlNode.getLengthValue(WIDTH, true, bounds, viewport, 0);
      double height = xmlNode.getLengthValue(HEIGHT, false, bounds, viewport, 0);
      double x = xmlNode.getLengthValue(X, true, bounds, viewport, 0);
      double y = xmlNode.getLengthValue(Y, false, bounds, viewport, 0);
      String hrefAttribute = xmlNode.getAttributeValue(HREF);
      if (hrefAttribute == null) {
         hrefAttribute = xmlNode.getAttributeValue(XLINK_HREF);
      }

      URL imageUrl = null;
      try {
         imageUrl = new URL(hrefAttribute);
      } catch (MalformedURLException ex) {
         try {
            imageUrl = new URL(url, hrefAttribute);
         } catch (MalformedURLException ex1) {
            GlobalConfig.getInstance().handleParsingError("URL " + hrefAttribute + " is not well formed");
         }
      }
      if (imageUrl != null) {
         if (viewbox != null) {
            width = viewbox.scaleValue(true, width);
            height = viewbox.scaleValue(false, height);
            x = viewbox.scaleValue(true, x);
            y = viewbox.scaleValue(false, y);
         }
         Image image = new Image(imageUrl.toString(), width, height, true, true);
         ImageView view = new ImageView(image);
         view.setX(x);
         view.setY(y);
         if (viewbox != null) {
            viewbox.scaleNode(view);
         }
         return view;
      }

      return null;
   }

   /**
    * Build an "ellipse" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildEllipse(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double cx = xmlNode.getPositionValue(CX, true, bounds, viewport, 0);
      double cy = xmlNode.getPositionValue(CY, false, bounds, viewport, 0);
      double rx = xmlNode.getLengthValue(RX, true, bounds, viewport, 0);
      double ry = xmlNode.getLengthValue(RY, false, bounds, viewport, 0);
      if (viewbox != null) {
         cx = viewbox.scaleValue(true, cx);
         cy = viewbox.scaleValue(false, cy);
         rx = viewbox.scaleValue(true, rx);
         ry = viewbox.scaleValue(true, ry);
      }

      Ellipse ellipse = new Ellipse(cx, cy, rx, ry);
      if (viewbox != null) {
         viewbox.scaleNode(ellipse);
      }
      return ellipse;
   }

   /**
    * Build an "path" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static SVGPath buildPath(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      SVGPath path = new SVGPath();
      String content = xmlNode.getAttributeValue(D);
      FillRule rule = ParserUtils.getFillRule(xmlNode);
      if (rule != null) {
         path.setFillRule(rule);
      }
      content = content.replace('−', '-');
      content = PathParser.parsePathContent(content, viewport);
      path.setContent(content);

      if (viewbox != null) {
         viewbox.scaleNode(path);
      }
      return path;
   }

   /**
    * Build a "polygon" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Polygon buildPolygon(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      String pointsAttribute = xmlNode.getAttributeValue(POINTS);
      Polygon polygon = new Polygon();

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");
      while (tokenizer.hasMoreTokens()) {
         String point = tokenizer.nextToken();

         StringTokenizer tokenizer2 = new StringTokenizer(point, ",");
         double x = ParserUtils.parsePositionValue(tokenizer2.nextToken(), true, bounds, viewport);
         double y = ParserUtils.parsePositionValue(tokenizer2.nextToken(), false, bounds, viewport);
         if (viewbox != null) {
            x = viewbox.scaleValue(true, x);
            y = viewbox.scaleValue(false, y);
         }
         polygon.getPoints().add(x);
         polygon.getPoints().add(y);
      }
      if (viewbox != null) {
         viewbox.scaleNode(polygon);
      }
      return polygon;
   }

   /**
    * Build a "line" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Line buildLine(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      if (xmlNode.hasAttribute(X1) && xmlNode.hasAttribute(Y1) && xmlNode.hasAttribute(X2) && xmlNode.hasAttribute(Y2)) {
         double x1 = xmlNode.getPositionValue(X1, true, bounds, viewport);
         double y1 = xmlNode.getPositionValue(Y1, false, bounds, viewport);
         double x2 = xmlNode.getPositionValue(X2, true, bounds, viewport);
         double y2 = xmlNode.getPositionValue(Y2, false, bounds, viewport);

         if (viewbox != null) {
            x1 = viewbox.scaleValue(true, x1);
            y1 = viewbox.scaleValue(false, y1);
            x2 = viewbox.scaleValue(true, x2);
            y2 = viewbox.scaleValue(false, y2);
         }
         Line line = new Line(x1, y1, x2, y2);
         if (viewbox != null) {
            viewbox.scaleNode(line);
         }
         return line;
      } else {
         return null;
      }
   }

   /**
    * Build a "polyline" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Polyline buildPolyline(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      Polyline polyline = new Polyline();
      String pointsAttribute = xmlNode.getAttributeValue(POINTS);

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");
      while (tokenizer.hasMoreTokens()) {
         String points = tokenizer.nextToken();
         StringTokenizer tokenizer2 = new StringTokenizer(points, ",");
         double x = ParserUtils.parsePositionValue(tokenizer2.nextToken(), true, bounds, viewport);
         double y = ParserUtils.parsePositionValue(tokenizer2.nextToken(), false, bounds, viewport);
         if (viewbox != null) {
            x = viewbox.scaleValue(true, x);
            y = viewbox.scaleValue(false, y);
         }
         polyline.getPoints().add(x);
         polyline.getPoints().add(y);
      }

      return polyline;
   }

   public static void buildFEGaussianBlur(FilterSpec spec, XMLNode node) {
      double stdDeviation = 0d;

      String resultId = node.getAttributeValue(RESULT);
      if (node.hasAttribute(STD_DEVIATION)) {
         String stdDevS = node.getAttributeValue(STD_DEVIATION);
         stdDevS = ParserUtils.parseFirstArgument(stdDevS);
         stdDeviation = ParserUtils.parseDoubleProtected(stdDevS);
      }
      FilterSpec.FEGaussianBlur effect = new FilterSpec.FEGaussianBlur(resultId, stdDeviation);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEDropShadow(FilterSpec spec, XMLNode node, Viewport viewport) {
      double dx = node.getLengthValue(DX, true, viewport);
      double dy = node.getLengthValue(DY, true, viewport);
      double opacity = 1d;
      double stdDeviation = 0d;
      Color col = Color.BLACK;

      String resultId = node.getAttributeValue(RESULT);
      if (node.hasAttribute(FLOOD_OPACITY)) {
         opacity = node.getDoubleValue(FLOOD_OPACITY, 1d);
      }
      if (node.hasAttribute(STD_DEVIATION)) {
         String stdDevS = node.getAttributeValue(STD_DEVIATION);
         stdDevS = ParserUtils.parseFirstArgument(stdDevS);
         stdDeviation = ParserUtils.parseDoubleProtected(stdDevS);
      }
      if (node.hasAttribute(FLOOD_COLOR)) {
         String colorS = node.getAttributeValue(FLOOD_COLOR);
         col = ParserUtils.getColor(colorS, opacity);
      }
      FilterSpec.FEDropShadow effect = new FilterSpec.FEDropShadow(resultId, dx, dy, stdDeviation, col);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEFlood(FilterSpec spec, XMLNode node, Viewport viewport) {
      double x = node.getLengthValue(X, true, viewport);
      double y = node.getLengthValue(Y, true, viewport);
      double width = node.getLengthValue(WIDTH, true, viewport);
      double height = node.getLengthValue(HEIGHT, true, viewport);
      double opacity = 1d;
      Color col = Color.BLACK;
      String resultId = node.getAttributeValue(RESULT);

      if (node.hasAttribute(FLOOD_OPACITY)) {
         opacity = node.getDoubleValue(FLOOD_OPACITY, 1d);
      }
      if (node.hasAttribute(FLOOD_COLOR)) {
         String colorS = node.getAttributeValue(FLOOD_COLOR);
         col = ParserUtils.getColor(colorS, opacity);
      }
      FilterSpec.FEFlood effect = new FilterSpec.FEFlood(resultId, x, y, width, height, col);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEOffset(FilterSpec spec, XMLNode node, Viewport viewport) {
      double dx = node.getLengthValue(DX, true, viewport);
      double dy = node.getLengthValue(DY, true, viewport);
      String resultId = node.getAttributeValue(RESULT);

      FilterSpec.FEOffset effect = new FilterSpec.FEOffset(resultId, dx, dy);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEImage(FilterSpec spec, URL url, XMLNode node, Viewport viewport) {
      double x = node.getLengthValue(X, true, viewport);
      double y = node.getLengthValue(Y, true, viewport);
      double width = node.getLengthValue(WIDTH, true, viewport);
      double height = node.getLengthValue(HEIGHT, true, viewport);
      String hrefAttribute = node.getAttributeValue(XLINK_HREF);
      String resultId = node.getAttributeValue(RESULT);

      Image image = null;
      URL imageUrl = null;
      try {
         imageUrl = new URL(hrefAttribute);
      } catch (MalformedURLException ex) {
         try {
            imageUrl = new URL(url, hrefAttribute);
         } catch (MalformedURLException ex1) {
            GlobalConfig.getInstance().handleParsingError("URL " + hrefAttribute + " is not well formed");
         }
      }
      if (imageUrl != null) {
         image = new Image(imageUrl.toString(), width, height, true, true);
      }
      FilterSpec.FEImage effect = new FilterSpec.FEImage(resultId, x, y, image);
      spec.addEffect(resultId, effect);
   }

   public static void buildFESpecularLighting(FilterSpec spec, XMLNode node, Viewport viewport) {
      XMLNode child = node.getFirstChild();
      if (child != null) {
         switch (child.getName()) {
            case FE_DISTANT_LIGHT: {
               double surfaceScale = node.getDoubleValue(SURFACE_SCALE, 1.5d);
               double specularConstant = node.getDoubleValue(SPECULAR_CONSTANT, 0.3d);
               double specularExponent = node.getDoubleValue(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double azimuth = child.getDoubleValue(AZIMUTH);
               double elevation = child.getDoubleValue(ELEVATION);
               Light.Distant light = new Light.Distant(azimuth, elevation, col);
               String resultId = node.getAttributeValue(RESULT);
               FESpecularLighting effect = new FESpecularLighting(resultId, specularConstant, specularExponent, surfaceScale, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
            case FE_POINT_LIGHT: {
               double surfaceScale = node.getDoubleValue(SURFACE_SCALE, 1.5d);
               double specularConstant = node.getDoubleValue(SPECULAR_CONSTANT, 0.3d);
               double specularExponent = node.getDoubleValue(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getLengthValue(X, true, viewport);
               double y = child.getLengthValue(Y, true, viewport);
               double z = child.getLengthValue(Z, true, null);
               Light.Point light = new Light.Point(x, y, z, col);
               String resultId = node.getAttributeValue(RESULT);
               FESpecularLighting effect = new FESpecularLighting(resultId, specularConstant, specularExponent, surfaceScale, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
            case FE_SPOT_LIGHT: {
               double surfaceScale = node.getDoubleValue(SURFACE_SCALE, 1.5d);
               double specularConstant = node.getDoubleValue(SPECULAR_CONSTANT, 0.3d);
               double specularExponent = node.getDoubleValue(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getLengthValue(X, true, viewport);
               double y = child.getLengthValue(Y, false, viewport);
               double z = child.getLengthValue(Z, true, viewport);
               double pointAtX = child.getLengthValue(POINT_AT_X, true, viewport);
               double pointAtY = child.getLengthValue(POINT_AT_Y, false, viewport);
               double pointAtZ = child.getLengthValue(POINT_AT_Z, true, viewport);
               Light.Spot light = new Light.Spot(x, y, z, specularExponent, col);
               light.setPointsAtX(pointAtX);
               light.setPointsAtY(pointAtY);
               light.setPointsAtZ(pointAtZ);
               String resultId = node.getAttributeValue(RESULT);
               FESpecularLighting effect = new FESpecularLighting(resultId, specularConstant, specularExponent, surfaceScale, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
         }
      }
   }

   public static void buildFEDiffuseLighting(FilterSpec spec, XMLNode node, Viewport viewport) {
      XMLNode child = node.getFirstChild();
      if (child != null) {
         switch (child.getName()) {
            case FE_DISTANT_LIGHT: {
               double diffuseConstant = node.getDoubleValue(DIFFUSE_CONSTANT, 0.3d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double azimuth = child.getDoubleValue(AZIMUTH);
               double elevation = child.getDoubleValue(ELEVATION);
               Light.Distant light = new Light.Distant(azimuth, elevation, col);
               String resultId = node.getAttributeValue(RESULT);
               FEDiffuseLighting effect = new FEDiffuseLighting(resultId, diffuseConstant, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
            case FE_POINT_LIGHT: {
               double diffuseConstant = node.getDoubleValue(DIFFUSE_CONSTANT, 0.3d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getLengthValue(X, true, viewport);
               double y = child.getLengthValue(Y, true, viewport);
               double z = child.getLengthValue(Z, true, null);
               Light.Point light = new Light.Point(x, y, z, col);
               String resultId = node.getAttributeValue(RESULT);
               FEDiffuseLighting effect = new FEDiffuseLighting(resultId, diffuseConstant, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
            case FE_SPOT_LIGHT: {
               double diffuseConstant = node.getDoubleValue(DIFFUSE_CONSTANT, 0.3d);
               double specularExponent = node.getDoubleValue(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getLengthValue(X, true, viewport);
               double y = child.getLengthValue(Y, false, viewport);
               double z = child.getLengthValue(Z, true, viewport);
               double pointAtX = child.getLengthValue(POINT_AT_X, true, viewport);
               double pointAtY = child.getLengthValue(POINT_AT_Y, false, viewport);
               double pointAtZ = child.getLengthValue(POINT_AT_Z, true, viewport);
               Light.Spot light = new Light.Spot(x, y, z, specularExponent, col);
               light.setPointsAtX(pointAtX);
               light.setPointsAtY(pointAtY);
               light.setPointsAtZ(pointAtZ);
               String resultId = node.getAttributeValue(RESULT);
               FEDiffuseLighting effect = new FEDiffuseLighting(resultId, diffuseConstant, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
         }
      }
   }

   public static void buildFEComposite(FilterSpec spec, XMLNode node) {
      String resultId = node.getAttributeValue(RESULT);
      String in = node.getAttributeValue(IN);
      String in2 = node.getAttributeValue(IN2);
      String operator = node.getAttributeValue(OPERATOR);
      short type = FilterSpec.FEComposite.OPERATOR_OVER;
      switch (operator) {
         case OPERATOR_OVER:
            type = FilterSpec.FEComposite.OPERATOR_OVER;
            break;
         case OPERATOR_IN:
            type = FilterSpec.FEComposite.OPERATOR_IN;
            break;
         case OPERATOR_OUT:
            type = FilterSpec.FEComposite.OPERATOR_OUT;
            break;
         case OPERATOR_ATOP:
            type = FilterSpec.FEComposite.OPERATOR_ATOP;
            break;
         case OPERATOR_XOR:
            type = FilterSpec.FEComposite.OPERATOR_XOR;
            break;
         case OPERATOR_ARITHMETIC:
            type = FilterSpec.FEComposite.OPERATOR_ARITHMETIC;
            break;
      }
      FilterSpec.FEComposite effect = new FilterSpec.FEComposite(resultId, type, in, in2);
      spec.addEffect(resultId, effect);
   }

   public static void buildFEMerge(FilterSpec spec, XMLNode node) {
      String resultId = node.getAttributeValue(RESULT);
      FilterSpec.FEMerge effect = new FilterSpec.FEMerge(resultId);
      spec.addEffect(resultId, effect);

      Iterator<XMLNode> it = node.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode child = it.next();
         if (child.getName().equals(FE_MERGE_NODE)) {
            String in = child.getAttributeValue(IN);
            if (in != null) {
               effect.addMergeNode(in);
            }
         }
      }
   }
}
