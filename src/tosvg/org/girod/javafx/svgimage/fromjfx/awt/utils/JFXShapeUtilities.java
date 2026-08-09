/*
Copyright (c) 2026, Hervé Girod
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
package org.girod.javafx.svgimage.fromjfx.awt.utils;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Iterator;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.shape.Arc;
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
import javafx.scene.shape.VLineTo;
import javafx.scene.transform.Transform;

/**
 * Various Shape Utilities for JavaFX.
 *
 * @since 1.8
 */
public class JFXShapeUtilities {
   private JFXShapeUtilities() {
   }

   /**
    * Return an Awt Shape from a JavaFX Node.
    *
    * @param node the JavaFX Node
    * @return the Awt Shape
    */
   public static java.awt.Shape getShape(Node node) {
      if (node instanceof SVGPath) {
         return getAWTPath((SVGPath) node);
      } else if (node instanceof QuadCurve) {
         return getAWTPath((QuadCurve) node);
      } else if (node instanceof CubicCurve) {
         return getAWTPath((CubicCurve) node);
      } else if (node instanceof Polygon) {
         return getAWTPath((Polygon) node);
      } else if (node instanceof Polyline) {
         return getAWTPath((Polyline) node);
      } else if (node instanceof Path) {
         return getAWTPath((Path) node);
      } else if (node instanceof Rectangle) {
         return getAWTPath((Rectangle) node);
      } else if (node instanceof Circle) {
         return getAWTPath((Circle) node);
      } else if (node instanceof Ellipse) {
         return getAWTPath((Ellipse) node);
      } else if (node instanceof Arc) {
         return getAWTPath((Arc) node);
      } else if (node instanceof Line) {
         // added courtesy of Mark Schmieder, lines were not directly taken into account, but converted as rectangles
         Line line = (Line) node;
         return new Line2D.Double(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
      } else {
         Bounds bounds = node.getBoundsInLocal();
         return new Rectangle2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
      }
   }

   /**
    * Return the Awt AffineTransform from a Node transforms.
    *
    * @param node the Node
    * @return the Awt AffineTransform
    */
   public static AffineTransform getTransform(Node node) {
      AffineTransform trans = new AffineTransform();
      // transformations list
      ObservableList<Transform> transforms = node.getTransforms();
      Iterator<Transform> it = transforms.iterator();
      while (it.hasNext()) {
         Transform tr = it.next();
         AffineTransform transform = AwtUtilities.getAffineTransform(tr);
         trans.concatenate(transform);
      }
      // translation
      double translateX = node.getTranslateX() + node.getLayoutX();
      double translateY = node.getTranslateY() + node.getLayoutY();
      if (translateX != 0 || translateY != 0) {
         trans.translate(translateX, translateY);
      }
      // scale
      double scaleX = node.getScaleX();
      double scaleY = node.getScaleY();
      if (scaleX != 1 || scaleY != 1) {
         trans.scale(scaleX, scaleY);
      }
      // rotation
      double rotate = node.getRotate();
      Point3D point = node.getRotationAxis();
      if (rotate != 0) {
         trans.rotate(rotate, point.getX(), point.getY());
      }
      return trans;
   }

   /**
    * Transfrom an Awt Shape with the transforms of a Node.
    *
    * @param node the Node
    * @param shape the Shape
    * @return the transformed Shape
    */
   public static java.awt.Shape transformShape(Node node, java.awt.Shape shape) {
      AffineTransform trans = getTransform(node);
      shape = ShapeUtilities.createTransformedShape(shape, trans);
      return shape;
   }

   /**
    * Convert a SVGPath to an Awt Shape without applying any AffineTransform on it.
    *
    * @param path the SVGPath
    * @return the Awt Shape
    */
   public static Shape getAWTPath(SVGPath path) {
      if (path != null) {
         SVGPathConverter converter = new SVGPathConverter();
         return converter.convert(path);
      } else {
         return null;
      }
   }

   public static java.awt.Shape getAWTPath(Circle circle) {
      Ellipse2D awtCircle = new Ellipse2D.Double(circle.getCenterX() - circle.getRadius(), circle.getCenterY() - circle.getRadius(),
         circle.getRadius() * 2, circle.getRadius() * 2);
      return awtCircle;
   }

   public static java.awt.Shape getAWTPath(Ellipse ellipse) {
      Ellipse2D awtCircle = new Ellipse2D.Double(ellipse.getCenterX() - ellipse.getRadiusX(), ellipse.getCenterY() - ellipse.getRadiusY(),
         ellipse.getRadiusX() * 2, ellipse.getRadiusY() * 2);
      return awtCircle;
   }

   public static java.awt.Shape getAWTPath(Arc arc) {
      int type = Arc2D.CHORD;
      if (arc.getType() == ArcType.OPEN) {
         type = Arc2D.OPEN;
      } else if (arc.getType() == ArcType.ROUND) {
         type = Arc2D.PIE;
      }
      java.awt.Shape awtArc;
      if (arc.getLength() >= 360) {
         awtArc = new Ellipse2D.Double(arc.getCenterX() - arc.getRadiusX(), arc.getCenterY() - arc.getRadiusY(), arc.getRadiusX() * 2, arc.getRadiusY() * 2);
      } else {
         double startAngle = arc.getStartAngle();
         double extent = arc.getLength();
         if (extent < 0) {
            startAngle = startAngle + extent;
            extent = -extent;
         }
         awtArc = new Arc2D.Double(arc.getCenterX() - arc.getRadiusX(), arc.getCenterY() - arc.getRadiusY(),
            arc.getRadiusX() * 2, arc.getRadiusY() * 2, startAngle, extent, type);
      }
      return awtArc;
   }

   public static java.awt.Shape getAWTPath(Rectangle rec) {
      if (rec.getArcHeight() == 0 && rec.getArcWidth() == 0) {
         Rectangle2D awtrec = new Rectangle2D.Double(rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight());
         return awtrec;
      } else {
         RoundRectangle2D awtrec = new RoundRectangle2D.Double(rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight(), rec.getArcWidth(), rec.getArcHeight());
         return awtrec;
      }
   }

   public static java.awt.Shape getAWTPath(QuadCurve curve) {
      GeneralPath gPath = new GeneralPath();
      gPath.quadTo(curve.getControlX(), curve.getControlY(), curve.getEndX(), curve.getEndY());
      return gPath;
   }

   public static java.awt.Shape getAWTPath(CubicCurve curve) {
      GeneralPath gPath = new GeneralPath();
      gPath.curveTo(curve.getControlX1(), curve.getControlY1(), curve.getControlX2(), curve.getControlY2(), curve.getEndX(), curve.getEndY());
      return gPath;
   }

   public static java.awt.Shape getAWTPath(Polygon polygon) {
      GeneralPath gPath = new GeneralPath();
      Iterator<Double> it = polygon.getPoints().iterator();
      double x = 0;
      boolean isFirst = true;
      boolean isY = false;
      while (it.hasNext()) {
         double d = it.next();
         if (!isY) {
            x = d;
            isY = true;
         } else if (isFirst) {
            gPath.moveTo(x, d);
            isFirst = false;
            isY = false;
         } else {
            gPath.lineTo(x, d);
            isY = false;
         }
      }
      gPath.closePath();
      return gPath;
   }

   public static java.awt.Shape getAWTPath(Polyline polyline) {
      GeneralPath gPath = new GeneralPath();
      Iterator<Double> it = polyline.getPoints().iterator();
      double x = 0;
      boolean isFirst = true;
      boolean isY = false;
      while (it.hasNext()) {
         double d = it.next();
         if (!isY) {
            x = d;
            isY = true;
         } else if (isFirst) {
            gPath.moveTo(x, d);
            isFirst = false;
            isY = false;
         } else {
            gPath.lineTo(x, d);
            isY = false;
         }
      }
      return gPath;
   }

   public static java.awt.Shape getAWTPath(Path path) {
      GeneralPath gPath = new GeneralPath();
      double x = 0;
      double y = 0;
      // we don't handle ArcTo for the moment
      Iterator<PathElement> it = path.getElements().iterator();
      while (it.hasNext()) {
         PathElement elt = it.next();
         if (elt instanceof MoveTo) {
            MoveTo moveTo = (MoveTo) elt;
            x = moveTo.getX();
            y = moveTo.getY();
            gPath.moveTo(x, y);
         } else if (elt instanceof ClosePath) {
            gPath.closePath();
         } else if (elt instanceof HLineTo) {
            HLineTo lineTo = (HLineTo) elt;
            x = lineTo.getX();
            gPath.lineTo(x, y);
         } else if (elt instanceof VLineTo) {
            VLineTo lineTo = (VLineTo) elt;
            y = lineTo.getY();
            gPath.lineTo(x, y);
         } else if (elt instanceof LineTo) {
            LineTo lineTo = (LineTo) elt;
            x = lineTo.getX();
            y = lineTo.getY();
            gPath.lineTo(x, y);
         } else if (elt instanceof QuadCurveTo) {
            QuadCurveTo curveTo = (QuadCurveTo) elt;
            x = curveTo.getX();
            y = curveTo.getY();
            gPath.quadTo(curveTo.getControlX(), curveTo.getControlY(), x, y);
         } else if (elt instanceof CubicCurveTo) {
            CubicCurveTo curveTo = (CubicCurveTo) elt;
            x = curveTo.getX();
            y = curveTo.getY();
            gPath.curveTo(curveTo.getControlX1(), curveTo.getControlY1(), curveTo.getControlX2(), curveTo.getControlY2(), x, y);
         }
      }
      return gPath;
   }
}
