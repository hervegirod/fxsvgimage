/*
Copyright (c) 2025 Hervé Girod
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

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Utilities for builders.
 *
 * @since 1.3
 */
public class BuilderUtils {
   private BuilderUtils() {
   }

   /**
    * Removes new lines from a text.
    *
    * @param text the text
    * @return the text without new lines
    */
   public static String removeNewLines(String text) {
      if (text == null) {
         return null;
      }
      return text.replace("\n", "").replaceAll("\t", "");
   }

   /**
    *
    * Computes the width of a text.
    *
    * @param text the text
    * @return the width
    */
   public static double getTextWidth(Text text) {
      Text text2 = new Text(text.getText());
      text2.setFont(text.getFont());
      Group group = new Group(text2);
      Scene scene = new Scene(group); // we need to create a scene for the applyCSS to have a result
      text2.applyCss();
      double width = text2.getLayoutBounds().getWidth();
      return width;
   }

   /**
    *
    * Computes the width of a text or a TextHBox.
    *
    * @param node the text or TextHBox
    * @return the width
    */
   public static double getTextWidth(Node node) {
      double width;
      if (node instanceof Text) {
         width = getTextWidth((Text) node);
      } else if (node instanceof TextHBox) {
         width = ((TextHBox) node).getTextWidth();
      } else {
         width = 0d;
      }
      return width;
   }

   /**
    *
    * Computes the Font of a text or a TextHBox.
    *
    * @param node the text or TextHBox
    * @return the Font
    */
   public static Font getTextFont(Node node) {
      Font font;
      if (node instanceof Text) {
         font = ((Text) node).getFont();
      } else if (node instanceof TextHBox) {
         font = ((TextHBox) node).getFont();
      } else {
         font = Font.font(12d);
      }
      return font;
   }

   /**
    *
    * Computes the X position of a text or a TextHBox.
    *
    * @param node the text or TextHBox
    * @return the x position
    */
   public static double getTextX(Node node) {
      double x;
      if (node instanceof Text) {
         x = ((Text) node).getX();
      } else if (node instanceof TextHBox) {
         x = ((HBox) node).getLayoutX();
      } else {
         x = 0d;
      }
      return x;
   }

   /**
    *
    * Computes the Y position of a text or a TextHBox.
    *
    * @param node the text or TextHBox
    * @return the y position
    */
   public static double getTextY(Node node) {
      double y;
      if (node instanceof Text) {
         y = ((Text) node).getY();
      } else if (node instanceof TextHBox) {
         y = ((HBox) node).getLayoutY();
      } else {
         y = 0d;
      }
      return y;
   }

   /**
    * Computes the angle (in degrees) of the vector from {@code p1} to {@code p2}. The angle will be in the range
    * {@code 0} (inclusive) to {@code 360} (exclusive) as measured counterclockwise from the positive x-axis.
    *
    * @param p1 the start point
    * @param p2 the end point
    * @return the angle, in degrees
    */
   public static double computeAngle(Point2D p1, Point2D p2) {
      Point2D vector = new Point2D(p2.getX() - p1.getX(), p2.getY() - p1.getY());
      double angle = vector.angle(1.0, 0.0);
      if (vector.getY() > 0) {
         return 360.0 - angle;
      }
      return angle;
   }

   /**
    * Computes the angle (in degrees) of a Line.
    *
    * @param line the line
    * @return the angle, in degrees
    */
   public static double computeAngle(Line line) {
      Point2D p1 = new Point2D(line.getStartX(), line.getStartY());
      Point2D p2 = new Point2D(line.getEndX(), line.getEndY());
      return computeAngle(p1, p2);
   }

   /**
    * Computes the angle (in degrees) of the last segment of a polyline.
    *
    * @param polyline the polyline
    * @return the angle, in degrees
    */
   public static double computeAngle(Polyline polyline) {
      ObservableList<Double> points = polyline.getPoints();
      int size = points.size();
      if (size < 4) {
         return 0;
      }
      Point2D p1 = new Point2D(size - 2, size - 1);
      Point2D p2 = new Point2D(size - 4, size - 3);
      return computeAngle(p1, p2);
   }

   /**
    * Computes the angle (in degrees) of the last path element of a Path.
    *
    * @param path the Path
    * @return the angle, in degrees
    */
   public static double computeAngle(Path path) {
      ObservableList<PathElement> list = path.getElements();
      if (list.isEmpty()) {
         return 0;
      }
      int lastIndex = list.size() - 1;
      PathElement lastElement = list.get(lastIndex);
      Point2D p1 = null;
      Point2D p2 = null;
      if (lastElement instanceof ClosePath) {
         lastIndex--;
         lastElement = list.get(lastIndex);
      }
      while (true) {
         if (lastElement instanceof CubicCurveTo) {
            lastIndex--;
            lastElement = list.get(lastIndex);
         } else if (lastElement instanceof QuadCurveTo) {
            lastIndex--;
            lastElement = list.get(lastIndex);
         } else {
            break;
         }
      }
      if (lastElement instanceof LineTo) {
         LineTo lineTo = (LineTo) lastElement;
         p2 = new Point2D(lineTo.getX(), lineTo.getY());
         PathElement previousElement = list.get(lastIndex - 1);
         if (previousElement instanceof MoveTo) {
            MoveTo moveTo = (MoveTo) previousElement;
            p1 = new Point2D(moveTo.getX(), moveTo.getY());
         } else if (previousElement instanceof LineTo) {
            LineTo lineTo1 = (LineTo) previousElement;
            p1 = new Point2D(lineTo1.getX(), lineTo1.getY());
         } else if (previousElement instanceof CubicCurveTo) {
            CubicCurveTo curveTo = (CubicCurveTo) previousElement;
            p1 = new Point2D(curveTo.getX(), curveTo.getY());   
         } else if (previousElement instanceof QuadCurveTo) {
            QuadCurveTo curveTo = (QuadCurveTo) previousElement;
            p1 = new Point2D(curveTo.getX(), curveTo.getY());             
         }
      }
      if (p1 != null && p2 != null) {
         return computeAngle(p1, p2);
      } else {
         return 0;
      }
   }
}
