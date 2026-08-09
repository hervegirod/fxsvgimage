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

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/** 
 * This class represents a 2D Vector.
 * It offers several utility methods to manage vectors :</p>
 * <ul>
 * <li>normalization, angle, product of a vector by a scalar</li>
 * <li>rotation of a vector, perpendicular vector</li>
 * <li>addition, soustraction, scalar product of two vectors</li>
 * <li>intersection between two lines defined by a point and a vector</li>
 * <li>projection of a point on a line defined by this vector and an origin point</li>
 * <li>determination if two lines are colinear</li>
 * <li>result of an affine transformation</li>
 * </ul>
 *
 * @since 1.8
 */
public class Vector2D implements Cloneable {
   /**
    * The x coordinate of the Vector.
    */
   public double x;
   /**
    * The y coordinate of the Vector.
    */
   public double y;
   /**
    * {@value}: Constant used for coordinates equality. Its value is 0.0001.
    */
   public final static double ASSUME_ZERO = 0.0001d;

   /**
    * {@value}: Constant used for angles equality. Its value is approximately equal to 1 degree
    * expressed in radians (0.017 radians).
    */
   public final static double ASSUME_ANGLE_ZERO = 0.017d;

   /**
    * Create a (0, 0) 2D Vector.
    */
   public Vector2D() {
      x = 0;
      y = 0;
   }

   /**
    * Create a 2D Vector, knowing its x and y coordinates.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    */
   public Vector2D(double x, double y) {
      this.x = x;
      this.y = y;
   }

   /**
    * Create a 2D Vector, knowing its two associated points.
    *
    * @param p0 the first point
    * @param p1 the second point
    */
   public Vector2D(Point2D p0, Point2D p1) {
      this.x = p1.getX() - p0.getX();
      this.y = p1.getY() - p0.getY();
   }

   /**
    * Create a 2D Vector, identical to another Vector2D.
    *
    * @param v the vector to clone
    */
   public Vector2D(Vector2D v) {
      this.x = v.x;
      this.y = v.y;
   }

   /**
    * Return the X coordinate of this Vector.
    *
    * @return the X coordinate
    */
   public double getX() {
      return x;
   }

   /**
    * Return the Y coordinate of this Vector.
    *
    * @return the Y coordinate
    */
   public double getY() {
      return y;
   }

   /**
    * Set the X coordinate of this Vector.
    *
    * @param x the X coordinate
    */
   public void setX(double x) {
      this.x = x;
   }

   /**
    * Set the Y coordinate of this Vector.
    *
    * @param y the Y coordinate
    */
   public void setY(double y) {
      this.y = y;
   }

   /**
    * Return the scalar product of this vector with another vector.
    *
    * @param v the other Vector
    * @return the scalar product
    */
   public double getScalarProduct(Vector2D v) {
      return (x * v.x + y * v.y);
   }

   /**
    * Substracts another vector from this one.
    *
    * @param v the other Vector
    * @return a new vector which is the result of the substraction
    */
   public Vector2D minus(Vector2D v) {
      return new Vector2D(x - v.x, y - v.y);
   }

   /**
    * Rotates the Vector.
    *
    * @param alpha the angle in Radians
    * @return a new vector which is the result of the rotation
    */
   public Vector2D rotate(double alpha) {
      double xs = x * Math.cos(alpha) - y * Math.sin(alpha);
      double ys = x * Math.sin(alpha) + y * Math.cos(alpha);

      return new Vector2D(xs, ys);
   }

   /**
    * Adds another Vector to this one.
    *
    * @param v the other Vector
    * @return a new Vector which is the result of the addition
    */
   public Vector2D add(Vector2D v) {
      return new Vector2D(x + v.x, y + v.y);
   }

   /**
    * Return the euclidian norm of this Vector.
    *
    * @return the euclidian norm of this Vector
    */
   public double getNorm() {
      return Math.sqrt(x * x + y * y);
   }

   /**
    * Return the angle of this vector with the X axis.
    * The angle is in the range of <i>-pi</i> to <i>pi</i> (using <i>Math.atan2</i> formula)
    *
    * @return the angle in Radians
    */
   public double getAngle() {
      return Math.atan2(y, x);
   }

   /**
    * Normalize this vector. If this vector has a zero norm, then the normalized vector will be the (0, 0) vector.
    *
    * @return a new vector which is the result of the normalization
    */
   public Vector2D normalize() {
      if (getNorm() == 0) {
         return new Vector2D();
      }
      double d = getNorm();
      return new Vector2D(x / d, y / d);
   }

   /**
    * Multiply the coordinates of this Vector by a scalar value.
    *
    * @param d the factor
    * @return a new Vector which is the result of the multiplication
    */
   public Vector2D scaleTo(double d) {
      return new Vector2D(x * d, y * d);
   }

   /**
    * Translate a Point by this vector.
    *
    * @param p the Point
    * @return the translated Point
    */
   public Point2D translate(Point2D p) {
      return new Point2D.Double(p.getX() + x, p.getY() + y);
   }

   /**
    * Return the normal of this vector.
    * If this vector has a zero norm, then the normal will be the (0, 0) vector, else it will have
    * a unit norm.
    *
    * @return the normal Vector
    */
   public Vector2D getNormal() {
      if (getNorm() == 0) {
         return new Vector2D();
      }
      double alpha = Math.atan2(y, x);
      return new Vector2D(Math.sin(alpha), -Math.cos(alpha));
   }

   /**
    * Return the inverse of this Vector, which is this vector rotated by PI.
    *
    * @return the inverse of this Vector
    */
   public Vector2D getInverse() {
      return new Vector2D(-x, -y);
   }

   /**
    * Return the orthogonal projection of this point on the line defined by the Vector2D.
    * If this vector has a zero norm, then the projection will be the initial point.
    *
    * @param o the origin of the line
    * @param p the point to project
    * @return the orthogonal projection of p
    */
   public Point2D getNormalProjection(Point2D o, Point2D p) {
      if (getNorm() == 0) {
         return p;
      }

      double t = ((p.getX() - o.getX()) * x + (p.getY() - o.getY()) * y) / (x * x + y * y);
      double px = t * x + o.getX();
      double py = t * y + o.getY();

      return new Point2D.Double(px, py);
   }

   /**
    * Determines if two lines are colinear. The two lines are :
    * <ul>
    * <li>the line defined by the point p0 and this Vector2D</li>
    * <li>the line defined by the point p1 and the Vector2D v1</li>
    * </ul>
    * <p>
    * The two lines are considered colinear if :
    * <ul>
    * <li>the distance between the projection of p1 on the line defined by p0 and this Vector2D,
    * and the point p1, is less than <i>delta</i></li>
    * <li>the difference between the angles of the two vectors are less than <i>delta_angle</i></li>
    * </ul>
    * </p>
    *
    * @param p0 origin of the first line (defined by this vector)
    * @param v1 vector defining the second line
    * @param p1 origin of the second line
    * @param delta the delta concerning the distance of p1 to the first line
    * @param deltaAngle the delta concerning the difference between the two angles
    * @return true if the two lines are colinear
    */
   public boolean isColinear(Point2D p0, Vector2D v1, Point2D p1, double delta, double deltaAngle) {
      double d = getAngle() - v1.getAngle();
      if ((Math.abs(d) > deltaAngle) && (Math.abs(d - Math.PI) > deltaAngle)
         && (Math.abs(d + Math.PI) > deltaAngle)) {
         return false;
      }
      if (getNormalProjection(p0, p1).distance(p1) > delta) {
         return false;
      }
      return true;
   }

   /**
    * Determines if two lines are colinear.
    * <p>
    * Same as {link #isColinear(Point2D, Vector2D, Point2D, double, double)} where :
    * <ul>
    * <li>the <i>delta</i> parameter is equal to {@link #ASSUME_ZERO}</li>
    * <li>the <i>delta_angle</i> parameter is equal to {@link #ASSUME_ANGLE_ZERO}</li>
    * </ul>
    *
    * @param p0 origin of the first line (defined by this vector)
    * @param v1 vector defining the second line
    * @param p1 origin of the second line
    * @return true if the two lines are colinear
    */
   public boolean isColinear(Point2D p0, Vector2D v1, Point2D p1) {
      return isColinear(p0, v1, p1, ASSUME_ZERO, ASSUME_ANGLE_ZERO);
   }

   /**
    * Find the intersection between two lines. The two lines are:
    * <ul>
    * <li>the line defined by the point p0 and this Vector2D</li>
    * <li>the line defined by the point p1 and the Vector2D v1</li>
    * </ul>
    * <p>
    * It is considered that there is no intersection if either :</p>
    * <ul>
    * <li>the two x coordinates are closer from 0 than the {@link #ASSUME_ZERO}
    * value</li>
    * <li>the two x coordinates are farther from 0 than the {@link #ASSUME_ZERO}
    * value, but they differ from less than this value</li>
    * </ul>
    * <p>
    * Remark: The intersection is computed by resolving the following equations:</p>
    * <ul>
    * <li>y = a0 * x + b0</li>
    * <li>y = a1 * x + b1</li>
    * </ul>
    * <p>
    * where :
    * <ul>
    * <li>the first (a0, b0) line is defined by this Vector2D and the point p0</li>
    * <li>the second (a1, b1) line is defined by the Vector2D v1 and the point p1</li>
    * </ul>
    *
    * @param p0 origin of the first line (defined by this vector)
    * @param v1 vector defining the second line
    * @param p1 origin of the second line
    * @return the intersection Point
    */
   public Point2D getIntersection(Point2D p0, Vector2D v1, Point2D p1) {
      if ((Math.abs(x) > ASSUME_ZERO) && (Math.abs(v1.x) > ASSUME_ZERO)) {
         double a0 = y / x;
         double b0 = p0.getY() - a0 * p0.getX();

         double a1 = v1.y / v1.x;
         double b1 = p1.getY() - a1 * p1.getX();

         if (Math.abs(a0 - a1) < ASSUME_ZERO) {
            return null;
         }

         double px = (b0 - b1) / (a1 - a0);
         double py = a0 * px + b0;

         return new Point2D.Double(px, py);

      } else if ((Math.abs(x) < ASSUME_ZERO) && (Math.abs(v1.x) > ASSUME_ZERO)) {
         double px = p0.getX();

         double a1 = v1.y / v1.x;
         double b1 = p1.getY() - a1 * p1.getX();

         double py = a1 * px + b1;

         return new Point2D.Double(px, py);

      } else if ((Math.abs(x) > ASSUME_ZERO) && (Math.abs(v1.x) < ASSUME_ZERO)) {
         double px = p1.getX();

         double a0 = y / x;
         double b0 = p0.getY() - a0 * p0.getX();

         double py = a0 * px + b0;

         return new Point2D.Double(px, py);
      } else {
         return null;
      }
   }

   @Override
   public Object clone() {
      Vector2D v = new Vector2D(this);
      return v;
   }

   /**
    * Return the Vector2D after an AffineTransform.
    *
    * @param tr the AffineTransform
    * @return a new Vector2D which is the result of the AffineTransform by tr
    */
   public Vector2D transform(AffineTransform tr) {
      double xdst = x * tr.getScaleX() + y * tr.getShearX();
      double ydst = x * tr.getShearY() + y * tr.getScaleY();

      return new Vector2D(xdst, ydst);
   }

   /**
    * Return a Point2D transformed after an affine transformation.
    *
    * @param p the point
    * @param tr the AffineTransform
    * @return a Point2D which is the result of the AffineTransform of p by tr
    */
   public static Point2D transform(Point2D p, AffineTransform tr) {
      double x = p.getX();
      double y = p.getY();

      Point2D dst = new Point2D.Double(
         x * tr.getScaleX() + y * tr.getShearX() + tr.getTranslateX(),
         x * tr.getShearY() + y * tr.getScaleY() + tr.getTranslateY());

      return dst;
   }
}
