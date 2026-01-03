/*
Copyright (c) 2022, Hervé Girod
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
package org.girod.javafx.svgimage.tosvg.converters;

import java.util.Iterator;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

/**
 * A converter which convert Shapes.
 *
 * @since 1.0
 */
public class ShapeConverter extends AbstractConverter implements DefaultStrokeValues {
   private Shape shape = null;

   /**
    * Constructor.
    *
    * @param delegate the converter delegate
    * @param shape the shape to convert
    * @param xmlParent the parent xml node
    */
   public ShapeConverter(ConverterDelegate delegate, Shape shape, XMLNode xmlParent) {
      super(delegate, shape, xmlParent);
      this.shape = shape;
   }

   private boolean hasStrokeProperty() {
      return properties.containsKey(STROKE_WIDTH) || properties.containsKey(STROKE_LINECAP) || properties.containsKey(STROKE_LINEJOIN)
              || properties.containsKey(STROKE_MITERLIMIT) || properties.containsKey(STROKE_DASHARRAY) || properties.containsKey(STROKE_DASHOFFSET);
   }

   private boolean hasFontProperty() {
      return properties.containsKey(FONT_FAMILY) || properties.containsKey(FONT_SIZE) || properties.containsKey(FONT_STYLE)
              || properties.containsKey(FONT_WEIGHT);
   }

   /**
    * Return the Arc Width of a Rectangle.
    *
    * @param rec the Rectangle
    * @return the Arc Width
    */
   private double getArcWidth(Rectangle rec) {
      if (properties.containsKey(ARC_WIDTH)) {
         double arcWidth = (Double) properties.get(ARC_WIDTH);
         return arcWidth;
      } else {
         return rec.getArcWidth();
      }
   }

   /**
    * Return the Arc Height of a Rectangle.
    *
    * @param rec the Rectangle
    * @return the Arc Height
    */
   private double getArcHeight(Rectangle rec) {
      if (properties.containsKey(ARC_HEIGHT)) {
         double arcWidth = (Double) properties.get(ARC_HEIGHT);
         return arcWidth;
      } else {
         return rec.getArcWidth();
      }
   }

   private Font getFont(Text text) {
      if (properties.containsKey(FONT)) {
         javafx.scene.text.Font font = (javafx.scene.text.Font) properties.get(FONT);
         if (font != javafx.scene.text.Font.getDefault()) {
            return font;
         }
      }
      if (hasFontProperty()) {
         javafx.scene.text.Font font = text.getFont();
         double size = font.getSize();
         String family = font.getFamily();
         FontWeight weight = Utilities.getWeight(font);
         FontPosture posture = Utilities.getPosture(font);
         font = javafx.scene.text.Font.font(family, weight, posture, size);
         return font;
      } else {
         return text.getFont();
      }
   }

   /**
    * Return the opacity of the Node Fill. The way it work for a Shape is slightly different from a Region, because
    * contrary to a Region, a Shape can set its own Stroke and Fill. Hence foe example if the Fill of the Shape is
    * transparent or null, the opacity will be considered 0 even if the opacity value in the CSS is not 0.
    *
    * @return the opacity
    */
   protected double getOpacityFill() {
      if (properties.containsKey(OPACITY)) {
         Number opacity = (Number) properties.get(OPACITY);
         return opacity.doubleValue();
      } else {
         double opacity = shape.getOpacity();
         javafx.scene.paint.Paint paint = shape.getFill();
         if (paint == null) {
            opacity = 0;
         } else if (paint instanceof javafx.scene.paint.Color) {
            javafx.scene.paint.Color color = (javafx.scene.paint.Color) paint;
            double _opacity = color.getOpacity();
            if (_opacity < opacity) {
               opacity = _opacity;
            }
         }
         return opacity;
      }
   }

   /**
    * Return the opacity of the Node Stroke.
    *
    * @return the opacity
    */
   protected double getOpacityStroke() {
      if (properties.containsKey(OPACITY)) {
         Number opacity = (Number) properties.get(OPACITY);
         return opacity.doubleValue();
      } else {
         double opacity = shape.getOpacity();
         javafx.scene.paint.Paint paint = shape.getStroke();
         if (paint == null) {
            opacity = 0;
         } else if (paint instanceof javafx.scene.paint.Color) {
            javafx.scene.paint.Color color = (javafx.scene.paint.Color) paint;
            double _opacity = color.getOpacity();
            if (_opacity < opacity) {
               opacity = _opacity;
            }
         }
         return opacity;
      }
   }

   /**
    * Return the Awt Paint corresponing to the JavaFX fill.
    *
    * @return the Awt Paint
    */
   private Paint getFillPaint() {
      double opacity = this.getOpacityFill();
      if (opacity <= 0) {
         return null;
      }
      if (properties.containsKey(FILL_PAINT)) {
         Paint paint = (Paint) properties.get(FILL_PAINT);
         return paint;
      } else {
         // we must use the fillProperty() and to the getFill() method, because getFill() will return a Color.BLACK for a null fill
         return shape.fillProperty().get();
      }
   }

   /**
    * Return the Awt Paint corresponing to the JavaFX Stroke.
    *
    * @return the Awt Paint
    * @see CSSProperties#STROKE_PAINT
    */
   private Paint getStrokePaint() {
      double opacity = this.getOpacityStroke();
      if (opacity <= 0) {
         return null;
      }
      if (properties.containsKey(STROKE_PAINT)) {
         Paint paint = (Paint) properties.get(STROKE_PAINT);
         return paint;
      } else {
         // we must use the strokeProperty() and to the getFill() method, because getStroke() will return a Color.BLACK for a null stroke
         return shape.strokeProperty().get();
      }
   }

   private void setOpacity(Paint paint, XMLNode node) {
      if (paint instanceof Color) {
         Color color = (Color) paint;
         if (color.getOpacity() != 1) {
            node.addAttribute("opacity", color.getOpacity());
         }
      }
   }

   @Override
   public void applyStyle(XMLNode node, String clipID) {
      StringBuilder buf = new StringBuilder();
      if (shape instanceof Line) {
         Paint paint = getStrokePaint();
         setClip(buf, clipID);
         addStroke(paint, buf);
         setLineStroke(shape, buf);
         setOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (shape instanceof Rectangle) {
         Paint paint = getStrokePaint();
         setClip(buf, clipID);
         addStroke(paint, buf);
         setLineStroke(shape, buf);
         setStrokeOpacity(paint, node);
         paint = getFillPaint();
         addFill(paint, buf);
         setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (shape instanceof Circle) {
         Paint paint = getStrokePaint();
         setClip(buf, clipID);
         addStroke(paint, buf);
         setLineStroke(shape, buf);
         setStrokeOpacity(paint, node);
         paint = getFillPaint();
         addFill(paint, buf);
         setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (shape instanceof Ellipse) {
         Paint paint = getStrokePaint();
         setClip(buf, clipID);
         addStroke(paint, buf);
         setLineStroke(shape, buf);
         setStrokeOpacity(paint, node);
         paint = getFillPaint();
         addFill(paint, buf);
         setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (shape instanceof Polyline) {
         Paint paint = getStrokePaint();
         setClip(buf, clipID);
         addStroke(paint, buf);
         setLineStroke(shape, buf);
         setOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (shape instanceof Polygon) {
         Paint paint = getStrokePaint();
         setClip(buf, clipID);
         addStroke(paint, buf);
         setLineStroke(shape, buf);
         setStrokeOpacity(paint, node);
         paint = getFillPaint();
         addFill(paint, buf);
         setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (shape instanceof SVGPath) {
         Paint paint = getStrokePaint();
         setClip(buf, clipID);
         addStroke(paint, buf);
         setLineStroke(shape, buf);
         setStrokeOpacity(paint, node);
         paint = getFillPaint();
         addFill(paint, buf);
         setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (shape instanceof Path) {
         Paint paint = getStrokePaint();
         setClip(buf, clipID);
         addStroke(paint, buf);
         setLineStroke(shape, buf);
         setStrokeOpacity(paint, node);
         paint = getFillPaint();
         addFill(paint, buf);
         setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (shape instanceof Arc) {
         Paint paint = getStrokePaint();
         setClip(buf, clipID);
         addStroke(paint, buf);
         setLineStroke(shape, buf);
         setStrokeOpacity(paint, node);
         paint = getFillPaint();
         addFill(paint, buf);
         setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (shape instanceof Text) {
         Paint paint = getFillPaint();
         setClip(buf, clipID);
         addFill(paint, buf);
         setOpacity(paint, node);
         Text text = (Text) shape;
         addFontStyle(text, buf);
         String style = buf.toString();
         node.addAttribute("style", style);
      }
   }

   private void addFontStyle(Text text, StringBuilder buf) {
      Font font = text.getFont();
      FontWeight weight = Utilities.getWeight(font);
      FontPosture posture = Utilities.getPosture(font);
      if (null == weight) {
         buf.append("font-weight:normal;");
      } else {
         switch (weight) {
            case BOLD:
               buf.append("font-weight:bold;");
               break;
            case EXTRA_BOLD:
               buf.append("font-weight:bold;");
               break;
            case LIGHT:
               buf.append("font-weight:lighter;");
               break;
            default:
               buf.append("font-weight:normal;");
               break;
         }
      }
      if (posture == FontPosture.ITALIC) {
         buf.append("font-style:italic;");
      } else {
         buf.append("font-style:normal;");
      }
      double size = font.getSize();
      buf.append("font-size:").append(size).append(";");
      String family = font.getFamily();
      if (family.equals("System")) {
         family = "Arial";
      }
      buf.append("font-family:").append(family).append(";");
   }

   private void appendPoints(Polygon polygon, XMLNode node) {
      StringBuilder buf = new StringBuilder();
      ObservableList<Double> points = polygon.getPoints();
      for (int i = 0; i < points.size(); i += 2) {
         double x = points.get(i);
         double y = points.get(i + 1);
         buf.append(x).append(",").append(y);
         if (i < points.size() - 1) {
            buf.append(" ");
         }
      }
      node.addAttribute("points", buf.toString());
   }

   private void appendPoints(Polyline polyline, XMLNode node) {
      StringBuilder buf = new StringBuilder();
      ObservableList<Double> points = polyline.getPoints();
      for (int i = 0; i < points.size(); i += 2) {
         double x = points.get(i);
         double y = points.get(i + 1);
         buf.append(x).append(",").append(y);
         if (i < points.size() - 1) {
            buf.append(" ");
         }
      }
      node.addAttribute("points", buf.toString());
   }

   private void setLineStroke(Shape shape, StringBuilder buf) {
      StrokeLineJoin lineJoin = shape.getStrokeLineJoin();
      if (lineJoin != DEFAULT_STROKE_LINE_JOIN) {
         if (lineJoin == StrokeLineJoin.BEVEL) {
            buf.append("stroke-linejoin: bevel;");
         } else if (lineJoin == StrokeLineJoin.MITER) {
            buf.append("stroke-linejoin: miter;");
         } else if (lineJoin == StrokeLineJoin.ROUND) {
            buf.append("stroke-linejoin: round;");
         }
      }
      StrokeLineCap lineCap = shape.getStrokeLineCap();
      if (lineCap != DEFAULT_STROKE_LINE_CAP) {
         if (lineCap == StrokeLineCap.BUTT) {
            buf.append("stroke-linecap: butt;");
         } else if (lineCap == StrokeLineCap.ROUND) {
            buf.append("stroke-linecap: round;");
         } else if (lineCap == StrokeLineCap.SQUARE) {
            buf.append("stroke-linecap: square;");
         }
      }
      double miterLimit = shape.getStrokeMiterLimit();
      if (miterLimit != DEFAULT_STROKE_MITER_LIMIT) {
         buf.append("stroke-miterlimit:").append(miterLimit).append(";");
      }
      double dashoffset = shape.getStrokeDashOffset();
      if (dashoffset != DEFAULT_STROKE_DASH_OFFSET) {
         buf.append("stroke-dashoffset:").append(dashoffset).append(";");
      }
      double width = shape.getStrokeWidth();
      if (width != DEFAULT_STROKE_WIDTH) {
         buf.append("stroke-width:").append(width).append(";");
      }
      ObservableList<Double> array = shape.getStrokeDashArray();
      if (array.size() > 0) {
         buf.append("stroke-dasharray:");
         Iterator<Double> it = array.iterator();
         while (it.hasNext()) {
            double value = it.next();
            buf.append(value);
            if (it.hasNext()) {
               buf.append(",");
            }
         }
         buf.append(";");
      }
   }

   private void setPath(Path path, XMLNode node) {
      StringBuilder buf = new StringBuilder();
      ObservableList<PathElement> elements = path.getElements();
      Iterator<PathElement> it = elements.iterator();
      while (it.hasNext()) {
         PathElement element = it.next();
         if (element instanceof MoveTo) {
            MoveTo moveTo = (MoveTo) element;
            buf.append("M").append(moveTo.getX()).append(",").append(moveTo.getY());
         } else if (element instanceof ClosePath) {
            buf.append("Z");
         } else if (element instanceof HLineTo) {
            HLineTo lineTo = (HLineTo) element;
            buf.append("H").append(lineTo.getX());
         } else if (element instanceof VLineTo) {
            VLineTo lineTo = (VLineTo) element;
            buf.append("V").append(lineTo.getY());
         } else if (element instanceof LineTo) {
            LineTo lineTo = (LineTo) element;
            buf.append("L").append(lineTo.getX()).append(",").append(lineTo.getY());
         } else if (element instanceof ArcTo) {
            ArcTo arcTo = (ArcTo) element;
            buf.append("A").append(arcTo.getRadiusX()).append(",").append(arcTo.getRadiusY());
            buf.append(" ").append(arcTo.getXAxisRotation());
            buf.append(" ").append(arcTo.isLargeArcFlag() ? "1" : "0").append(",").append(arcTo.isSweepFlag() ? "1" : "0");
            buf.append(" ").append(arcTo.getX()).append(",").append(arcTo.getY());
         } else if (element instanceof QuadCurveTo) {
            QuadCurveTo curveTo = (QuadCurveTo) element;
            buf.append("Q").append(curveTo.getControlX()).append(",").append(curveTo.getControlY());
            buf.append(" ").append(curveTo.getX()).append(",").append(curveTo.getY());
         } else if (element instanceof CubicCurveTo) {
            CubicCurveTo curveTo = (CubicCurveTo) element;
            buf.append("C").append(curveTo.getControlX1()).append(",").append(curveTo.getControlY1());
            buf.append(" ").append(curveTo.getControlX2()).append(",").append(curveTo.getControlY2());
            buf.append(" ").append(curveTo.getX()).append(",").append(curveTo.getY());
         }
         if (it.hasNext()) {
            buf.append(" ");
         }
      }
      node.addAttribute("d", buf.toString());
   }

   /**
    * Convert the Shape. The currently suppported Shape classes are:
    * <ul>
    * <li>Line</li>
    * <li>Circle</li>
    * <li>Ellipse</li>
    * <li>Arc</li>
    * <li>Rectangle</li>
    * <li>Path</li>
    * <li>Polygon</li>
    * <li>Polyline</li>
    * <li>Text</li>
    * <li>QuadCurve</li>
    * <li>CubicCurve</li>
    * </ul>
    *
    * @return the XML node
    */
   @Override
   public XMLNode convert() {
      XMLNode node = null;
      if (shape instanceof Line) {
         Line line = (Line) shape;
         node = new XMLNode("line");
         node.addAttribute("x1", line.getStartX());
         node.addAttribute("y1", line.getStartY());
         node.addAttribute("x2", line.getEndX());
         node.addAttribute("y2", line.getEndY());
         xmlParent.addChild(node);
      } else if (shape instanceof Text) {
         Text text = (Text) shape;
         String str = text.getText();
         node = new XMLNode("text");
         node.addAttribute("xml:space", "preserve");
         node.addAttribute("x", text.getX());
         node.addAttribute("y", text.getY());
         node.setCDATA(str);
         xmlParent.addChild(node);
      } else if (shape instanceof Polygon) {
         Polygon polygon = (Polygon) shape;
         node = new XMLNode("polygon");
         appendPoints(polygon, node);
         xmlParent.addChild(node);
      } else if (shape instanceof Polyline) {
         Polyline polyline = (Polyline) shape;
         node = new XMLNode("polyline");
         appendPoints(polyline, node);
         xmlParent.addChild(node);
      } else if (shape instanceof SVGPath) {
         SVGPath path = (SVGPath) shape;
         node = new XMLNode("path");
         node.addAttribute("d", path.getContent());
         xmlParent.addChild(node);
      } else if (shape instanceof Path) {
         Path path = (Path) shape;
         node = new XMLNode("path");
         setPath(path, node);
         xmlParent.addChild(node);
      } else if (shape instanceof Circle) {
         Circle circle = (Circle) shape;
         node = new XMLNode("circle");
         node.addAttribute("cx", circle.getCenterX());
         node.addAttribute("cy", circle.getCenterY());
         node.addAttribute("r", circle.getRadius());
         xmlParent.addChild(node);
      } else if (shape instanceof Ellipse) {
         Ellipse ellipse = (Ellipse) shape;
         node = new XMLNode("ellipse");
         node.addAttribute("cx", ellipse.getCenterX());
         node.addAttribute("cy", ellipse.getCenterY());
         node.addAttribute("rx", ellipse.getRadiusX());
         node.addAttribute("ry", ellipse.getRadiusY());
         xmlParent.addChild(node);
      } else if (shape instanceof Arc) {
         Arc arc = (Arc) shape;
         Path path = new Path();
         double startX = arc.getCenterX() - arc.getRadiusX();
         double startY = arc.getCenterY() - arc.getRadiusY();
         MoveTo moveTo = new MoveTo();
         moveTo.setX(startX);
         moveTo.setY(startY);
         path.getElements().add(moveTo);
         double endX = arc.getCenterX() + arc.getRadiusX() * Math.cos(Math.toRadians(arc.getStartAngle() + arc.getLength()));
         double endY = arc.getCenterY() + arc.getRadiusY() * Math.sin(Math.toRadians(arc.getStartAngle() + arc.getLength()));
         ArcTo arcTo = new ArcTo();
         arcTo.setX(endX);
         arcTo.setY(endY);
         path.getElements().add(arcTo);
         if (arc.getType() == ArcType.CHORD) {
            path.getElements().add(new ClosePath());
         }
         node = new XMLNode("path");
         setPath(path, node);
         xmlParent.addChild(node);
      } else if (shape instanceof Rectangle) {
         Rectangle rec = (Rectangle) shape;
         double arcWidth = getArcWidth(rec);
         double arcHeight = getArcHeight(rec);
         node = new XMLNode("rect");
         node.addAttribute("width", rec.getWidth());
         node.addAttribute("height", rec.getHeight());
         node.addAttribute("x", rec.getX());
         node.addAttribute("y", rec.getY());
         if (arcWidth != 0) {
            node.addAttribute("rx", arcWidth / 2);
         }
         if (arcHeight != 0) {
            node.addAttribute("ry", arcHeight / 2);
         }
         xmlParent.addChild(node);
      } else if (shape instanceof QuadCurve) {
         QuadCurve curve = (QuadCurve) shape;
         Path path = new Path();
         MoveTo moveTo = new MoveTo();
         moveTo.setX(curve.getStartX());
         moveTo.setY(curve.getStartY());
         QuadCurveTo curveTo = new QuadCurveTo();
         curveTo.setX(curve.getEndX());
         curveTo.setY(curve.getEndY());
         curveTo.setControlX(curve.getControlX());
         curveTo.setControlY(curve.getControlY());
         path.getElements().add(moveTo);
         path.getElements().add(curveTo);
         node = new XMLNode("path");
         setPath(path, node);
         xmlParent.addChild(node);
      } else if (shape instanceof CubicCurve) {
         CubicCurve curve = (CubicCurve) shape;
         Path path = new Path();
         MoveTo moveTo = new MoveTo();
         moveTo.setX(curve.getStartX());
         moveTo.setY(curve.getStartY());
         CubicCurveTo curveTo = new CubicCurveTo();
         curveTo.setX(curve.getEndX());
         curveTo.setY(curve.getEndY());
         curveTo.setControlX1(curve.getControlX1());
         curveTo.setControlY1(curve.getControlY1());
         curveTo.setControlX2(curve.getControlX2());
         curveTo.setControlY2(curve.getControlY2());
         path.getElements().add(moveTo);
         path.getElements().add(curveTo);
         node = new XMLNode("path");
         setPath(path, node);
         xmlParent.addChild(node);
      }
      return node;
   }
}
