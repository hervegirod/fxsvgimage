/*
Copyright (c) 2026 Hervé Girod
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

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This class has the same behavior than {@link Polygon2D}, except that the figure is not closed.
 *
 * @since 1.8
 */
public class Polyline2D extends Polygon2D {
   protected static final float ASSUME_ZERO = 0.001f;

   /**
    * Constructor.
    */
   public Polyline2D() {
      super();
   }

   /**
    * Constructor.
    *
    * @param xpoints an array of <i>x</i> coordinates
    * @param ypoints an array of <i>y</i> coordinates
    * @param npoints the total number of points in the <code>Polyline2D</code>
    * @exception NegativeArraySizeException if the value of
    * <code>npoints</code> is negative.
    * @exception IndexOutOfBoundsException if <code>npoints</code> is
    * greater than the length of <code>xpoints</code>
    * or the length of <code>ypoints</code>.
    * @exception NullPointerException if <code>xpoints</code> or
    * <code>ypoints</code> is <code>null</code>.
    */
   public Polyline2D(float[] xpoints, float[] ypoints, int npoints) {
      super(xpoints, ypoints, npoints);
   }

   /**
    * Constructor.
    *
    * @param xpoints an array of <i>x</i> coordinates
    * @param ypoints an array of <i>y</i> coordinates
    * @param npoints the total number of points in the <code>Polyline2D</code>
    * @exception NegativeArraySizeException if the value of <code>npoints</code> is negative.
    * @exception IndexOutOfBoundsException if <code>npoints</code> is greater than the length of <code>xpoints</code>
    * or the length of <code>ypoints</code>.
    * @exception NullPointerException if <code>xpoints</code> or <code>ypoints</code> is <code>null</code>.
    */
   public Polyline2D(int xpoints[], int ypoints[], int npoints) {
      super(xpoints, ypoints, npoints);
   }

   /**
    * Constructor.
    *
    * @param line the line
    */
   public Polyline2D(Line2D line) {
      super();
      this.npoints = 2;
      this.xpoints = new float[2];
      this.ypoints = new float[2];
      xpoints[0] = (float) line.getX1();
      xpoints[1] = (float) line.getX2();
      ypoints[0] = (float) line.getY1();
      ypoints[1] = (float) line.getY2();
      calculatePath();
   }

   @Override
   public Object clone() {
      Polyline2D pol = new Polyline2D();
      for (int i = 0; i < npoints; i++) {
         pol.addPoint(xpoints[i], ypoints[i]);
      }
      return pol;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(Point p) {
      return false;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(double x, double y) {
      return false;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(int x, int y) {
      return false;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(Point2D p) {
      return false;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(double x, double y, double w, double h) {
      return false;
   }

   /**
    * Return false.
    *
    * @return false
    */
   @Override
   public boolean contains(Rectangle2D r) {
      return false;
   }

   /**
    * Returns an iterator object that iterates along the boundary of this
    * <code>Polygon</code> and provides access to the geometry
    * of the outline of this <code>Polygon</code>. An optional
    * {@link AffineTransform} can be specified so that the coordinates
    * returned in the iteration are transformed accordingly.
    *
    * @param at an optional <code>AffineTransform</code> to be applied to the
    * coordinates as they are returned in the iteration, or
    * <code>null</code> if untransformed coordinates are desired
    * @return a {@link PathIterator} object that provides access to the
    * geometry of this <code>Polygon</code>.
    */
   @Override
   public PathIterator getPathIterator(AffineTransform at) {
      if (path == null) {
         return null;
      } else {
         return path.getPathIterator(at);
      }
   }

   /* get the associated {@link Polygon2D}.
     * This method take care that may be the last point can
     * be equal to the first. In that case it must not be included in the Polygon,
     * as polygons declare their first point only once.
    */
   public Polygon2D getPolygon2D() {
      Polygon2D pol = new Polygon2D();
      for (int i = 0; i < npoints - 1; i++) {
         pol.addPoint((float) xpoints[i], (float) ypoints[i]);
      }
      Point2D.Double p0
         = new Point2D.Double(xpoints[0], ypoints[0]);
      Point2D.Double p1
         = new Point2D.Double(xpoints[npoints - 1], ypoints[npoints - 1]);

      if (p0.distance(p1) > ASSUME_ZERO) {
         pol.addPoint((float) xpoints[npoints - 1], (float) ypoints[npoints - 1]);
      }

      return pol;
   }
}
