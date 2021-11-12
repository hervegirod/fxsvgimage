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
import static org.girod.javafx.svgimage.xml.SVGTags.CX;
import static org.girod.javafx.svgimage.xml.SVGTags.CY;
import static org.girod.javafx.svgimage.xml.SVGTags.DX;
import static org.girod.javafx.svgimage.xml.SVGTags.DY;
import static org.girod.javafx.svgimage.xml.SVGTags.FLOOD_COLOR;
import static org.girod.javafx.svgimage.xml.SVGTags.FLOOD_OPACITY;
import static org.girod.javafx.svgimage.xml.SVGTags.FX;
import static org.girod.javafx.svgimage.xml.SVGTags.FY;
import static org.girod.javafx.svgimage.xml.SVGTags.GRADIENT_TRANSFORM;
import static org.girod.javafx.svgimage.xml.SVGTags.GRADIENT_UNITS;
import static org.girod.javafx.svgimage.xml.SVGTags.ID;
import static org.girod.javafx.svgimage.xml.SVGTags.LINEAR_GRADIENT;
import static org.girod.javafx.svgimage.xml.SVGTags.OFFSET;
import static org.girod.javafx.svgimage.xml.SVGTags.R;
import static org.girod.javafx.svgimage.xml.SVGTags.RADIAL_GRADIENT;
import static org.girod.javafx.svgimage.xml.SVGTags.STD_DEVIATION;
import static org.girod.javafx.svgimage.xml.SVGTags.STOP;
import static org.girod.javafx.svgimage.xml.SVGTags.STOP_COLOR;
import static org.girod.javafx.svgimage.xml.SVGTags.STOP_OPACITY;
import static org.girod.javafx.svgimage.xml.SVGTags.STYLE;
import static org.girod.javafx.svgimage.xml.SVGTags.USERSPACE_ON_USE;
import static org.girod.javafx.svgimage.xml.SVGTags.X1;
import static org.girod.javafx.svgimage.xml.SVGTags.X2;
import static org.girod.javafx.svgimage.xml.SVGTags.XLINK_HREF;
import static org.girod.javafx.svgimage.xml.SVGTags.Y1;
import static org.girod.javafx.svgimage.xml.SVGTags.Y2;

/**
 * The shape builder.
 *
 * @version 0.5.1
 */
public class SVGShapeBuilder implements SVGTags {
   private static final Pattern NUMBER = Pattern.compile("\\d+");

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
      if (xmlNode.hasAttribute(RX)) {
         double rx = xmlNode.getAttributeValueAsDouble(RX, true, viewport, 0);
         rect.setArcWidth(rx);
      }
      if (xmlNode.hasAttribute(RY)) {
         double ry = xmlNode.getAttributeValueAsDouble(RY, false, viewport, 0);
         rect.setArcHeight(ry);
      }
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
      if (value.equals(UNDERLINE)) {
         addStyle = "-fx-underline: true;";
      } else if (value.equals(LINE_THROUGH)) {
         addStyle = "-fx-strikethrough: true;";
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
      if (value.equals("oblique")) {
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
    * Build a "text" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the Text
    */
   public static Text buildText(XMLNode xmlNode, Viewport viewport) {
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
      FontWeight weight = getFontWeight(xmlNode.getAttributeValue(FONT_WEIGHT));
      FontPosture posture = getFontPosture(xmlNode.getAttributeValue(FONT_STYLE));
      Font font = Font.font(family, weight, posture, size);

      String cdata = xmlNode.getCDATA();
      if (cdata != null) {
         double x = xmlNode.getAttributeValueAsDouble(X, true, viewport, 0);
         double y = xmlNode.getAttributeValueAsDouble(Y, false, viewport, 0);
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

   public static void buildRadialGradient(Map<String, GradientSpec> gradientSpecs, Map<String, Paint> gradients, XMLNode xmlNode) {
      String id = null;
      Double fx = null;
      Double fy = null;
      Double cx = null;
      Double cy = null;
      Double r = null;
      Transform transform = null;
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
               transform = ParserUtils.extractTransform(xmlNode.getAttributeValue(attrname));
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

         if (transform != null && transform instanceof Affine) {
            double tempCx = cx;
            double tempCy = cy;
            double tempR = r;

            Affine affine = (Affine) transform;
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

   public static void buildLinearGradient(Map<String, GradientSpec> gradientSpecs, Map<String, Paint> gradients, XMLNode xmlNode) {
      String id = null;
      double x1 = 0;
      double y1 = 0;
      double x2 = 1d;
      double y2 = 0d;
      Transform transform = null;
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
               transform = ParserUtils.extractTransform(xmlNode.getAttributeValue(attrname));
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
         if (transform != null && transform instanceof Affine) {
            double x1d = x1;
            double y1d = y1;
            double x2d = x2;
            double y2d = y2;
            Affine affine = (Affine) transform;
            x1 = x1d * affine.getMxx() + y1d * affine.getMxy() + affine.getTx();
            y1 = x1d * affine.getMyx() + y1d * affine.getMyy() + affine.getTy();
            x2 = x2d * affine.getMxx() + y2d * affine.getMxy() + affine.getTx();
            y2 = x2d * affine.getMyx() + y2d * affine.getMyy() + affine.getTy();
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
                     } else {
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
      double dx = node.getAttributeValueAsDouble(DX, true, viewport);
      double dy = node.getAttributeValueAsDouble(DY, true, viewport);
      double opacity = 1d;
      double stdDeviation = 0d;
      Color col = Color.BLACK;

      String resultId = node.getAttributeValue(RESULT);
      if (node.hasAttribute(FLOOD_OPACITY)) {
         opacity = node.getAttributeValueAsDouble(FLOOD_OPACITY, 1d);
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
      double x = node.getAttributeValueAsDouble(X, true, viewport);
      double y = node.getAttributeValueAsDouble(Y, true, viewport);
      double width = node.getAttributeValueAsDouble(WIDTH, true, viewport);
      double height = node.getAttributeValueAsDouble(HEIGHT, true, viewport);
      double opacity = 1d;
      Color col = Color.BLACK;
      String resultId = node.getAttributeValue(RESULT);

      if (node.hasAttribute(FLOOD_OPACITY)) {
         opacity = node.getAttributeValueAsDouble(FLOOD_OPACITY, 1d);
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
      double dx = node.getAttributeValueAsDouble(DX, true, viewport);
      double dy = node.getAttributeValueAsDouble(DY, true, viewport);
      String resultId = node.getAttributeValue(RESULT);

      FilterSpec.FEOffset effect = new FilterSpec.FEOffset(resultId, dx, dy);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEImage(FilterSpec spec, URL url, XMLNode node, Viewport viewport) {
      double x = node.getAttributeValueAsDouble(X, true, viewport);
      double y = node.getAttributeValueAsDouble(Y, true, viewport);
      double width = node.getAttributeValueAsDouble(WIDTH, true, viewport);
      double height = node.getAttributeValueAsDouble(HEIGHT, true, viewport);
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
               double surfaceScale = node.getAttributeValueAsDouble(SURFACE_SCALE, 1.5d);
               double specularConstant = node.getAttributeValueAsDouble(SPECULAR_CONSTANT, 0.3d);
               double specularExponent = node.getAttributeValueAsDouble(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double azimuth = child.getAttributeValueAsDouble(AZIMUTH);
               double elevation = child.getAttributeValueAsDouble(ELEVATION);
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
               double surfaceScale = node.getAttributeValueAsDouble(SURFACE_SCALE, 1.5d);
               double specularConstant = node.getAttributeValueAsDouble(SPECULAR_CONSTANT, 0.3d);
               double specularExponent = node.getAttributeValueAsDouble(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getAttributeValueAsDouble(X, true, viewport);
               double y = child.getAttributeValueAsDouble(Y, true, viewport);
               double z = child.getAttributeValueAsDouble(Z, true, null);
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
               double surfaceScale = node.getAttributeValueAsDouble(SURFACE_SCALE, 1.5d);
               double specularConstant = node.getAttributeValueAsDouble(SPECULAR_CONSTANT, 0.3d);
               double specularExponent = node.getAttributeValueAsDouble(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getAttributeValueAsDouble(X, true, viewport);
               double y = child.getAttributeValueAsDouble(Y, false, viewport);
               double z = child.getAttributeValueAsDouble(Z, true, viewport);
               double pointAtX = child.getAttributeValueAsDouble(POINT_AT_X, true, viewport);
               double pointAtY = child.getAttributeValueAsDouble(POINT_AT_Y, false, viewport);
               double pointAtZ = child.getAttributeValueAsDouble(POINT_AT_Z, true, viewport);
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
               double diffuseConstant = node.getAttributeValueAsDouble(DIFFUSE_CONSTANT, 0.3d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double azimuth = child.getAttributeValueAsDouble(AZIMUTH);
               double elevation = child.getAttributeValueAsDouble(ELEVATION);
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
               double diffuseConstant = node.getAttributeValueAsDouble(DIFFUSE_CONSTANT, 0.3d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getAttributeValueAsDouble(X, true, viewport);
               double y = child.getAttributeValueAsDouble(Y, true, viewport);
               double z = child.getAttributeValueAsDouble(Z, true, null);
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
               double diffuseConstant = node.getAttributeValueAsDouble(DIFFUSE_CONSTANT, 0.3d);
               double specularExponent = node.getAttributeValueAsDouble(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getAttributeValueAsDouble(X, true, viewport);
               double y = child.getAttributeValueAsDouble(Y, false, viewport);
               double z = child.getAttributeValueAsDouble(Z, true, viewport);
               double pointAtX = child.getAttributeValueAsDouble(POINT_AT_X, true, viewport);
               double pointAtY = child.getAttributeValueAsDouble(POINT_AT_Y, false, viewport);
               double pointAtZ = child.getAttributeValueAsDouble(POINT_AT_Z, true, viewport);
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
