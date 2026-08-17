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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * This class contain several utilities about Shapes.
 *
 * @since 1.8
 */
public class ShapeUtilities {
   private static final AffineTransform IDENTITY = new AffineTransform();
   private static final double ASSUME_ZERO = 0.001d;
   private static ShapeUtilities util = null;

   private ShapeUtilities() {
   }

   /**
    * Return the unique instance of this class.
    *
    * @return the unique instance
    */
   public static ShapeUtilities getInstance() {
      if (util == null) {
         util = new ShapeUtilities();
      }
      return util;
   }

   /**
    * Apply a transformation to a Shape, trying to keep the type of the Shape.
    *
    * @param src the source Shape
    * @param trans the AffineTransform
    * @return the transformed Shape
    * @see #transform(Shape, AffineTransform)
    */
   public static Shape createTransformedShape(Shape src, AffineTransform trans) {
      return ShapeUtilities.getInstance().transform(src, trans);
   }

   /**
    * Return the rotation angle for an AffineTransform. It will return 0 if the
    * AffineTransform has no rotation Component.
    *
    * @param tr the AffineTransform
    * @return the rotation angle
    */
   public static double getRotationAngle(AffineTransform tr) {
      double m00 = tr.getScaleX();
      double m01 = tr.getShearX();
      double m10 = tr.getShearY();
      double m11 = tr.getScaleY();
      double norm = Math.sqrt(m00 * m00 + m11 * m11);
      if (norm == 0) {
         return m10 >= 0 ? Math.PI / 2 : -Math.PI / 2;
      }
      double xx = m00 / norm;
      double xy = m01 / norm;
      double theta = -Math.atan2(xy, xx);

      if (Math.abs(theta) < ASSUME_ZERO) {
         return 0;
      }
      return theta;
   }

   /**
    * Return the rotation Component of an AffineTransform. It will return an Identity transform if the
    * AffineTransform has no rotation Component.
    *
    * @param tr the AffineTransform
    * @return the rotation Component of an AffineTransform (or the Identity transform)
    */
   public static AffineTransform getRotationTransform(AffineTransform tr) {
      double m00 = tr.getScaleX();
      double m01 = tr.getShearX();
      double m10 = tr.getShearY();
      double m11 = tr.getScaleY();
      double m02 = tr.getTranslateX();
      double m12 = tr.getTranslateY();
      double norm = Math.sqrt(m00 * m00 + m11 * m11);
      double theta = 0;
      if (norm == 0) {
         theta = m10 >= 0 ? Math.PI / 2 : -Math.PI / 2;
      } else {
         double xx = m00 / norm;
         double xy = m01 / norm;
         theta = -Math.atan2(xy, xx);
      }
      if (Math.abs(theta) < ASSUME_ZERO) {
         return IDENTITY;
      }
      double denom = m00 * m00 - m00 - m10;
      if (denom == 0 || m10 == 0) {
         return AffineTransform.getRotateInstance(theta);
      } else {
         double tx = ((m10 * m12 - (1 - m00) * m02)) / denom;
         double ty = (m02 - (1 - m00) * tx) / m10;
         return AffineTransform.getRotateInstance(theta, tx, ty);
      }
   }

   /**
    * Apply a transformation to a Shape, trying to keep the type of the Shape.
    * The transformation will be delegated to <i>AffineTransform</i> in the following cases:
    * <ul>
    * <li>The transformation has a scale component, and the scale is not uniform</li>
    * <li>The Shape is an Ellipse2D or a Rectangle2D, with different width and height values,
    * the transformation is not a Quadrant rotation, and the scale is not uniform,</li>
    * <li>The Shape is a RoundRectangle2D, with different width and height values,
    * the transformation is not a Quadrant rotation, and the scale is not uniform,</li>
    * <li>The Shape is an Arc2D with different width and height values</li>
    * <li>The Shape is not one of the following: Ellipse2D, Rectangle2D, RoundRectangle2D, Line2D, Arc2D,
    * Polygon2D, Polyline2D</li>
    * </ul>
    *
    * @param src the source Shape
    * @param trans the AffineTransform
    * @return the transformed Shape
    */
   public Shape transform(Shape src, AffineTransform trans) {
      if (src instanceof Line2D) {
         return createTransformedLine((Line2D) src, trans);
      } else if (src instanceof Polyline2D) {
         return createTransformedPolyline((Polyline2D) src, trans);
      } else if (src instanceof Polygon2D) {
         return createTransformedPolygon((Polygon2D) src, trans);
      } else if (src instanceof Rectangle2D) {
         return createTransformedRectangle((Rectangle2D) src, trans);
      } else if (src instanceof RoundRectangle2D) {
         return createTransformedRoundRectangle((RoundRectangle2D) src, trans);
      } else if (!isUniformScale(trans)) {
         return trans.createTransformedShape(src);
      } else if (src instanceof Ellipse2D) {
         return createTransformedEllipse((Ellipse2D) src, trans);
      } else if (src instanceof Arc2D) {
         return createTransformedArc((Arc2D) src, trans);
      } else {
         return trans.createTransformedShape(src);
      }
   }

   /**
    * Return false if the transform will be delegated to the AffineTransform. If true, the resulting Shape will be
    * a simple Shape: Ellipse2D, Rectangle2D, RoundRectangle2D, Line2D, Arc2D, Polygon2D, Polyline2D.
    *
    * @param src the source Shape
    * @param trans the AffineTransform
    * @return false if the transform will be delegated to the AffineTransform
    */
   public static boolean simpleShapeAsTransform(Shape src, AffineTransform trans) {
      return ShapeUtilities.getInstance().keepSimpleShape(src, trans);
   }

   /**
    * Return false if the transform will be delegated to the AffineTransform. If true, the resulting Shape will be
    * a simple Shape: Ellipse2D, Rectangle2D, RoundRectangle2D, Line2D, Arc2D, Polygon2D, Polyline2D.
    *
    * @param src the source Shape
    * @param trans the AffineTransform
    * @return false if the transform will be delegated to the AffineTransform
    */
   public boolean keepSimpleShape(Shape src, AffineTransform trans) {
      if (src instanceof Line2D) {
         return true;
      } else if (src instanceof Polyline2D) {
         return true;
      } else if (src instanceof Polygon2D) {
         return true;
      } else if (src instanceof Rectangle2D) {
         return true;
      } else if (!isUniformScale(trans)) {
         return false;
      } else if (src instanceof RoundRectangle2D) {
         return willKeepRoundRectangle((RoundRectangle2D) src, trans);
      } else if (src instanceof Ellipse2D) {
         return willKeepEllipse((Ellipse2D) src, trans);
      } else if (src instanceof Arc2D) {
         return willKeepArc((Arc2D) src, trans);
      } else {
         return false;
      }
   }

   private boolean willKeepEllipse(Ellipse2D el, AffineTransform trans) {
      if ((Math.abs(el.getWidth() - el.getHeight()) > ASSUME_ZERO)
         && (!isQuadrantRotation(trans))) {
         return false;
      } else {
         return true;
      }
   }

   private boolean willKeepArc(Arc2D arc, AffineTransform trans) {
      if ((Math.abs(arc.getWidth() - arc.getHeight()) > ASSUME_ZERO)
         && (!isQuadrantRotation(trans))) {
         return false;
      } else {
         return true;
      }
   }

   private boolean willKeepRoundRectangle(RoundRectangle2D rec, AffineTransform trans) {
      if ((Math.abs(rec.getArcHeight() - rec.getArcWidth()) > ASSUME_ZERO)
         || ((Math.abs(rec.getArcWidth() - rec.getArcHeight()) > ASSUME_ZERO)
         && (!isQuadrantRotation(trans)))) {
         return false;
      } else {
         return true;
      }
   }

   private Shape createTransformedLine(Line2D line, AffineTransform trans) {
      Point2D p0 = Vector2D.transform(line.getP1(), trans);
      Point2D p1 = Vector2D.transform(line.getP2(), trans);
      return new Line2D.Double(p0, p1);
   }

   private Shape createTransformedPolyline(Polyline2D pol, AffineTransform trans) {
      Polyline2D _pol = new Polyline2D();
      for (int i = 0; i < pol.npoints; i++) {
         _pol.addPoint(Vector2D.transform(new Point2D.Float(pol.xpoints[i], pol.ypoints[i]), trans));
      }
      return _pol;
   }

   private Shape createTransformedPolygon(Polygon2D pol, AffineTransform trans) {
      Polygon2D _pol = new Polygon2D();
      for (int i = 0; i < pol.npoints; i++) {
         _pol.addPoint(Vector2D.transform(new Point2D.Float(pol.xpoints[i], pol.ypoints[i]), trans));
      }
      return _pol;
   }

   private Shape createTransformedEllipse(Ellipse2D el, AffineTransform trans) {
      double w = el.getWidth();
      double h = el.getHeight();
      if ((Math.abs(w - h) > ASSUME_ZERO)
         && (!isQuadrantRotation(trans))) {
         return trans.createTransformedShape(el);
      } else {
         Rectangle2D rec = el.getBounds2D();
         rec = trans.createTransformedShape(rec).getBounds2D();
         Ellipse2D dst = new Ellipse2D.Double(rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight());

         return dst;
      }
   }

   private Shape createTransformedRectangle(Rectangle2D rec, AffineTransform trans) {
      double w = rec.getWidth();
      double h = rec.getHeight();
      if ((Math.abs(w - h) > ASSUME_ZERO)
         && (!isQuadrantRotation(trans))) {
         // we can't return a rectangle, but we can return a Polygon
         float xmin = (float) rec.getMinX();
         float ymin = (float) rec.getMinY();
         float xmax = (float) rec.getMaxX();
         float ymax = (float) rec.getMaxY();
         Polygon2D pol = new Polygon2D();
         pol.addPoint(xmin, ymin);
         pol.addPoint(xmax, ymin);
         pol.addPoint(xmax, ymax);
         pol.addPoint(xmin, ymax);
         return createTransformedPolygon(pol, trans);
      } else {
         // we can return a rectangle
         rec = trans.createTransformedShape(rec).getBounds2D();
         Rectangle2D dst = new Rectangle2D.Double(rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight());
         return dst;
      }
   }

   private Shape createTransformedRoundRectangle(RoundRectangle2D rec, AffineTransform trans) {
      double w = rec.getWidth();
      double h = rec.getHeight();
      double ah = rec.getArcHeight();
      double aw = rec.getArcWidth();

      if ((Math.abs(aw - ah) > ASSUME_ZERO) || ((Math.abs(w - h) > ASSUME_ZERO)
         && (!isQuadrantRotation(trans)))) {
         // we return a Shape
         return trans.createTransformedShape(rec);
      } else {
         // we can return a roundrectangle
         // roundrectangle position, width and height
         // we can return a rectangle
         Rectangle2D rec2 = trans.createTransformedShape(rec).getBounds2D();

         Rectangle2D elRec = new Rectangle2D.Double(0, 0, rec.getArcWidth(), rec.getArcHeight());
         elRec = trans.createTransformedShape(elRec).getBounds2D();
         RoundRectangle2D dst
            = new RoundRectangle2D.Double(rec2.getX(), rec2.getY(), rec2.getWidth(), rec2.getHeight(),
               elRec.getWidth(), elRec.getHeight());
         return dst;
      }
   }

   private Shape createTransformedArc(Arc2D arc, AffineTransform trans) {
      double w = arc.getWidth();
      double h = arc.getHeight();
      if (Math.abs(w - h) > ASSUME_ZERO) {
         return trans.createTransformedShape(arc);
      } else {
         // compute arc transformed bounds
         Rectangle2D rec = new Rectangle2D.Double(arc.getX(), arc.getY(), w, h);
         rec = trans.createTransformedShape(rec).getBounds2D();
         double x = rec.getX();
         double y = rec.getY();
         w = rec.getWidth();
         h = rec.getHeight();

         // compute arc transformed begin and end points
         Point2D pstart = arc.getStartPoint();
         Point2D pend = arc.getEndPoint();
         pstart = Vector2D.transform(pstart, trans);
         pend = Vector2D.transform(pend, trans);

         // compute arc angles
         Point2D pcenter = new Point2D.Double(x + w / 2, y + h / 2);
         Vector2D vstart = new Vector2D(pcenter, pstart);
         Vector2D vend = new Vector2D(pcenter, pend);
         arc = new Arc2D.Double(x, y, w, h, arc.getAngleStart(), arc.getAngleExtent(), arc.getArcType());

         return arc;
      }
   }

   /**
    * Determine if the scale component of the transformation is uniform, that is. :
    * <ul>
    * <li>there is a scale component and this component is uniform</li>
    * <li>there is no scale component</li>
    * </ul>
    */
   private boolean isUniformScale(AffineTransform trans) {
      int type = trans.getType();
      if ((type == AffineTransform.TYPE_UNIFORM_SCALE)
         || ((type != AffineTransform.TYPE_UNIFORM_SCALE)
         && (type != AffineTransform.TYPE_GENERAL_SCALE))) {
         return true;
      } else {
         return false;
      }
   }

   private boolean isQuadrantRotation(AffineTransform trans) {
      int type = trans.getType();
      if ((type == AffineTransform.TYPE_QUADRANT_ROTATION)
         || ((type != AffineTransform.TYPE_QUADRANT_ROTATION)
         && (type != AffineTransform.TYPE_GENERAL_ROTATION))) {
         return true;
      } else {
         return false;
      }
   }

}
