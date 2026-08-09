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
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.shape.SVGPath;

/**
 * Convert a JavaFX SVGPath to an Awt Shape.
 *
 * @since 1.8
 */
class SVGPathConverter {
   private static final Pattern NUM_PAT = Pattern.compile("-?\\d+(\\.\\d+)?");
   private static final short UNDEF_TYPE = -1;
   private static final short M_TYPE = 0;
   private static final short H_TYPE = 1;
   private static final short V_TYPE = 2;
   private static final short L_TYPE = 3;
   private static final short C_TYPE = 4;
   private static final short S_TYPE = 5;
   private static final short A_TYPE = 6;

   public SVGPathConverter() {
   }

   /**
    * Normalize the path content to be sure that there is a space between each command or cordinate.
    *
    * @param content the content
    * @return the normalized content
    */
   private String normalize(String content) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < content.length(); i++) {
         char c = content.charAt(i);
         String s = Character.toString(c);
         String _s = s.toLowerCase();
         if (_s.equals("c") || _s.equals("s") || _s.equals("m") || _s.equals("l") || _s.equals("h") || _s.equals("v") || _s.equals("a")) {
            buf.append(" ").append(s).append(" ");
         } else if (_s.equals("z")) {
            buf.append(" ").append(s);
         } else if (_s.equals(",")) {
            buf.append(" ");
         } else if (_s.equals("-")) {
            buf.append(" ").append(c);
         } else if (_s.equals("+")) {
            buf.append(" ").append(c);
         } else {
            buf.append(c);
         }
      }
      return buf.toString();
   }

   /**
    * Return the origin of a SVGPath (using the first MoveTo command).
    *
    * @param path the SVGPath
    * @return the origin of a SVGPath
    */
   Point2D getOrigin(SVGPath path) {
      String content = path.getContent();
      content = normalize(content);
      StringTokenizer tok = new StringTokenizer(content);
      double x = 0;
      double y = 0;
      short type = UNDEF_TYPE;
      int count = 0;
      boolean isRelative = false;
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken();
         String _tk = tk.toLowerCase();
         if (_tk.equals("m")) {
            isRelative = tk.equals("m");
            type = M_TYPE;
            count = 0;
         } else {
            Matcher m = NUM_PAT.matcher(tk);
            if (m.matches()) {
               double num = Double.parseDouble(tk);
               count++;
               if (type == M_TYPE) {
                  if (count % 2 == 0) {
                     y = isRelative ? y + num : num;
                     break;
                  } else {
                     x = isRelative ? x + num : num;
                  }
               }
            }
         }
      }
      return new Point2D.Double(x, y);
   }

   /**
    * Return true if the position of a SVGPath is relative (using the first m or M moveTo command). Note that it seems that the type
    * of the first moveTo command seems to have no effect on the appearance of the Shape itself.
    *
    * @param path the SVGPath
    * @return true if the position of a SVGPath is relative
    */
   boolean isRelative(SVGPath path) {
      String content = path.getContent();
      content = normalize(content);
      StringTokenizer tok = new StringTokenizer(content);
      boolean isRelative = false;
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken();
         String _tk = tk.toLowerCase();
         if (_tk.equals("m")) {
            isRelative = tk.equals("m");
            break;
         }
      }
      return isRelative;
   }

   /**
    * Convert a SVGPath to an Awt Shape without applying any AffineTransform on it.
    *
    * @param path the SVGPath
    * @return the Awt Shape
    */
   Shape convert(SVGPath path) {
      return convert(path, new AffineTransform());
   }

   /**
    * Convert a SVGPath to an Awt Shape and apply an AffineTransform on it.
    *
    * @param path the SVGPath
    * @param transform the AffineTransform
    * @return the Awt Shape
    */
   Shape convert(SVGPath path, AffineTransform transform) {
      String content = path.getContent();
      content = normalize(content);
      GeneralPath gpath = new GeneralPath();
      StringTokenizer tok = new StringTokenizer(content);
      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double rx = 0;
      double ry = 0;
      double axisRotation = 0;
      boolean largeArcFlag = false;
      boolean sweepFlag = false;
      short type = UNDEF_TYPE;
      int count = 0;
      int maxCount = -1;
      boolean isRelative = false;
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken();
         String _tk = tk.toLowerCase();
         if (_tk.equals("z")) {
            gpath.closePath();
            count = 0;
            maxCount = 0;
         } else if (_tk.equals("m")) {
            isRelative = tk.equals("m");
            type = M_TYPE;
            count = 0;
            maxCount = 2;
         } else if (_tk.equals("h")) {
            isRelative = tk.equals("h");
            type = H_TYPE;
            count = 0;
            maxCount = -1;
         } else if (_tk.equals("v")) {
            isRelative = tk.equals("v");
            type = V_TYPE;
            count = 0;
            maxCount = -1;
         } else if (_tk.equals("l")) {
            isRelative = tk.equals("l");
            type = L_TYPE;
            count = 0;
            maxCount = -1;
         } else if (_tk.equals("c")) {
            isRelative = tk.equals("c");
            type = C_TYPE;
            count = 0;
            maxCount = -1;
         } else if (_tk.equals("s")) {
            isRelative = tk.equals("s");
            type = S_TYPE;
            count = 0;
            maxCount = -1;
         } else if (_tk.equals("a")) {
            isRelative = tk.equals("a");
            type = A_TYPE;
            count = 0;
            maxCount = -1;
         } else {
            Matcher m = NUM_PAT.matcher(tk);
            if (m.matches()) {
               double num = Double.parseDouble(tk);
               count++;
               // fix against some badly defined paths (where we have a series of more than 2 coordinates just after a "m" without any other tag)
               if (maxCount != -1 && count > maxCount) {
                  type = L_TYPE;
                  maxCount = -1;
               }
               if (type == H_TYPE) {
                  x = isRelative ? x + num : num;
                  gpath.lineTo(x, y);
               } else if (type == V_TYPE) {
                  y = isRelative ? y + num : num;
                  gpath.lineTo(x, y);
               } else if (type == M_TYPE) {
                  if (count % 2 == 0) {
                     y = isRelative ? y + num : num;
                     gpath.moveTo(x, y);
                  } else {
                     x = isRelative ? x + num : num;
                  }
               } else if (type == L_TYPE) {
                  if (count % 2 == 0) {
                     y = isRelative ? y + num : num;
                     gpath.lineTo(x, y);
                  } else {
                     x = isRelative ? x + num : num;
                  }
               } else if (type == S_TYPE) {
                  if (count == 1) {
                     x1 = isRelative ? x + num : num;
                  } else if (count == 2) {
                     y1 = isRelative ? y + num : num;
                  } else if (count == 3) {
                     x = isRelative ? x + num : num;
                  } else if (count == 4) {
                     y = isRelative ? y + num : num;
                     gpath.quadTo(x1, y1, x, y);
                  }
               } else if (type == C_TYPE) {
                  if (count == 1) {
                     x1 = isRelative ? x + num : num;
                  } else if (count == 2) {
                     y1 = isRelative ? y + num : num;
                  } else if (count == 3) {
                     x2 = isRelative ? x + num : num;
                  } else if (count == 4) {
                     y2 = isRelative ? y + num : num;
                  } else if (count == 5) {
                     x = isRelative ? x + num : num;
                  } else if (count == 6) {
                     y = isRelative ? y + num : num;
                     gpath.curveTo(x1, y1, x2, y2, x, y);
                  }
               } else if (type == A_TYPE) {
                  if (count == 1) {
                     rx = num;
                  } else if (count == 2) {
                     ry = num;
                  } else if (count == 3) {
                     axisRotation = num;
                  } else if (count == 4) {
                     largeArcFlag = num != 0;
                  } else if (count == 5) {
                     sweepFlag = num != 0;
                  } else if (count == 6) {
                     x2 = isRelative ? x + num : num;
                  } else if (count == 7) {
                     y2 = isRelative ? y + num : num;
                     Arc2D arc = computeArc(x, y, rx, ry, axisRotation, largeArcFlag, sweepFlag, x2, y2);
                     if (axisRotation != 0) {
                        AffineTransform rotation
                           = AffineTransform.getRotateInstance(axisRotation, arc.getX() + arc.getWidth() / 2, arc.getY() + arc.getHeight() / 2);
                        Shape shape = ShapeUtilities.createTransformedShape(arc, rotation);
                        gpath.append(shape, true);
                     } else {
                        gpath.append(arc, true);
                     }
                     x = x2;
                     y = y2;
                     // reset the count to able to draw another arc
                     count = 0;
                  }
               }
            }
         }
      }
      return gpath.createTransformedShape(transform);
   }

   /**
    * Converts a SVG arc to an AWT Arc2D.
    */
   private Arc2D computeArc(double x0, double y0, double rx, double ry, double angle, boolean largeArcFlag, boolean sweepFlag, double x, double y) {
      // Compute the half distance between the current and the final point
      double dx2 = (x0 - x) / 2d;
      double dy2 = (y0 - y) / 2d;
      // Convert angle from degrees to radians
      angle = Math.toRadians(angle % 360d);
      double cosAngle = Math.cos(angle);
      double sinAngle = Math.sin(angle);

      double x1 = (cosAngle * dx2 + sinAngle * dy2);
      double y1 = (-sinAngle * dx2 + cosAngle * dy2);
      // Ensure radii are large enough
      rx = Math.abs(rx);
      ry = Math.abs(ry);
      double prx = rx * rx;
      double pry = ry * ry;
      double px1 = x1 * x1;
      double py1 = y1 * y1;
      // check that radii are large enough
      double radiiCheck = px1 / prx + py1 / pry;
      if (radiiCheck > 1) {
         rx = Math.sqrt(radiiCheck) * rx;
         ry = Math.sqrt(radiiCheck) * ry;
         prx = rx * rx;
         pry = ry * ry;
      }
      // Compute (cx1, cy1)
      double sign = (largeArcFlag == sweepFlag) ? -1d : 1d;
      double sq = ((prx * pry) - (prx * py1) - (pry * px1)) / ((prx * py1) + (pry * px1));
      sq = (sq < 0) ? 0 : sq;
      double coef = (sign * Math.sqrt(sq));
      double cx1 = coef * ((rx * y1) / ry);
      double cy1 = coef * -((ry * x1) / rx);
      // Compute (cx, cy) from (cx1, cy1)
      double sx2 = (x0 + x) / 2d;
      double sy2 = (y0 + y) / 2d;
      double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
      double cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);
      // Compute the angleStart and the angleExtent
      double ux = (x1 - cx1) / rx;
      double uy = (y1 - cy1) / ry;
      double vx = (-x1 - cx1) / rx;
      double vy = (-y1 - cy1) / ry;
      // Compute the angle start
      double n = Math.sqrt((ux * ux) + (uy * uy));
      double p = ux;
      sign = (uy < 0) ? -1d : 1d;
      double angleStart = Math.toDegrees(sign * Math.acos(p / n));
      // Compute the angle extent
      n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
      p = ux * vx + uy * vy;
      sign = (ux * vy - uy * vx < 0) ? -1d : 1d;
      double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
      if (!sweepFlag && angleExtent > 0) {
         angleExtent -= 360f;
      } else if (sweepFlag && angleExtent < 0) {
         angleExtent += 360f;
      }
      angleExtent %= 360f;
      angleStart %= 360f;
      // We can now build the resulting Arc2D
      Arc2D.Double arc = new Arc2D.Double(cx - rx, cy - ry, rx * 2d, ry * 2d, angleStart, -angleExtent, Arc2D.OPEN);
      return arc;
   }
}
