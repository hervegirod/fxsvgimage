/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.javafx.svgimage.tosvg.converters;

import java.util.Iterator;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.HLineTo;
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
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

/**
 * Builds SVG clip elements from JavaFX clip nodes.
 *
 * @since 1.0
 */
public class ClipConstructor {
   private int clipID = 0;
   
   /**
    * Create a clip constructor.
    */
   public ClipConstructor() {
   }
   
   /**
    * Return the next clip id.
    *
    * @return the clip id
    */
   public String getClipID() {
      return "clip_" + clipID;
   }

   /**
    * Create an SVG clip element for the provided clip node.
    *
    * @param clip the clip node
    * @return the SVG clip element, or null if unsupported
    */
   public XMLNode createClip(Node clip) {
      XMLNode xmlClip = null;
      if (clip instanceof Polygon) {
         Polygon polygon = (Polygon) clip;
         xmlClip = new XMLNode("polygon");
         appendPoints(polygon, xmlClip);
      } else if (clip instanceof Polyline) {
         Polyline polyline = (Polyline) clip;
         xmlClip = new XMLNode("polyline");
         appendPoints(polyline, xmlClip);
      } else if (clip instanceof SVGPath) {
         SVGPath path = (SVGPath) clip;
         xmlClip = new XMLNode("path");
         xmlClip.addAttribute("d", path.getContent());
      } else if (clip instanceof Path) {
         Path path = (Path) clip;
         xmlClip = new XMLNode("path");
         setPath(path, xmlClip);
      } else if (clip instanceof Circle) {
         Circle circle = (Circle) clip;
         xmlClip = new XMLNode("circle");
         xmlClip.addAttribute("cx", circle.getCenterX());
         xmlClip.addAttribute("cy", circle.getCenterY());
         xmlClip.addAttribute("r", circle.getRadius());
      } else if (clip instanceof Ellipse) {
         Ellipse ellipse = (Ellipse) clip;
         xmlClip = new XMLNode("ellipse");
         xmlClip.addAttribute("cx", ellipse.getCenterX());
         xmlClip.addAttribute("cy", ellipse.getCenterY());
         xmlClip.addAttribute("rx", ellipse.getRadiusX());
         xmlClip.addAttribute("ry", ellipse.getRadiusY());
      } else if (clip instanceof Arc) {
         Arc arc = (Arc) clip;
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
         xmlClip = new XMLNode("path");
         setPath(path, xmlClip);
      } else if (clip instanceof Rectangle) {
         Rectangle rec = (Rectangle) clip;
         double arcWidth = getArcWidth(rec);
         double arcHeight = getArcHeight(rec);
         xmlClip = new XMLNode("rect");
         xmlClip.addAttribute("width", rec.getWidth());
         xmlClip.addAttribute("height", rec.getHeight());
         xmlClip.addAttribute("x", rec.getX());
         xmlClip.addAttribute("y", rec.getY());
         if (arcWidth != 0) {
            xmlClip.addAttribute("rx", arcWidth / 2);
         }
         if (arcHeight != 0) {
            xmlClip.addAttribute("ry", arcHeight / 2);
         }
      } else if (clip instanceof QuadCurve) {
         QuadCurve curve = (QuadCurve) clip;
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
         xmlClip = new XMLNode("path");
         setPath(path, xmlClip);
      } else if (clip instanceof CubicCurve) {
         CubicCurve curve = (CubicCurve) clip;
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
         xmlClip = new XMLNode("path");
         setPath(path, xmlClip);
      }
      if (xmlClip != null) {
         clipID++;
      }
      return xmlClip;
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
    * Return the Arc Width of a Rectangle.
    *
    * @param rec the Rectangle
    * @return the Arc Width
    */
   private double getArcWidth(Rectangle rec) {
      return rec.getArcWidth();
   }

   /**
    * Return the Arc Height of a Rectangle.
    *
    * @param rec the Rectangle
    * @return the Arc Height
    */
   private double getArcHeight(Rectangle rec) {
      return rec.getArcWidth();
   }
}
