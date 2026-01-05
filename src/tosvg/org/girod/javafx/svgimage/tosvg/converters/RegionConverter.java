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

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.wrappers.BackgroundWrapper;
import org.girod.javafx.svgimage.tosvg.wrappers.BorderWrapper;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

/**
 * A converter which converts Regions.
 *
 * @since 1.0
 */
public class RegionConverter extends AbstractConverter {
   /**
    * The region being converted.
    */
   protected Region region = null;

   /**
    * Constructor.
    *
    * @param delegate the ConverterDelegate
    * @param region the Region
    * @param xmlParent the parent node
    */
   public RegionConverter(ConverterDelegate delegate, Region region, XMLNode xmlParent) {
      super(delegate, region, xmlParent);
      this.region = region;
   }

   /**
    * Return the converter Region Node.
    *
    * @return the converter Region Node
    */
   @Override
   public Parent getParent() {
      return region;
   }

   @Override
   public void applyStyle(XMLNode node, String clipID) {
      if (clipID != null) {
         StringBuilder buf = new StringBuilder();
         String style = buf.toString();
         node.addAttribute("style", style);
      }
   }

   /**
    * Return the SVGPath associated with the Region (may be null). Correspond to the "-fx-shape" CSS property.
    *
    * @return the SVGPath associated with the Region
    */
   private SVGPath getSVGPath() {
      if (allProperties.containsKey(SHAPE)) {
         return (SVGPath) allProperties.get(SHAPE);
      } else {
         return null;
      }
   }

   /**
    * Return the Region Background associated with the Region (may be null). Correspond to the "-fx-region-background"
    * CSS property.
    *
    * @return the Region Background associated with the Region
    * @see org.jfxconverter.utils.CSSProperties#REGION_BACKGROUND
    */
   private List<BackgroundWrapper> getRegionBackground() {
      double opacity = this.getOpacity();
      if (allProperties.containsKey(REGION_BACKGROUND)) {
         Background background = (Background) allProperties.get(REGION_BACKGROUND);
         List<BackgroundWrapper> awtPaints = Utilities.getPaintList(region, background, this, opacity);
         return awtPaints;
      } else if (region.getBackground() != null) {
         List<BackgroundWrapper> awtPaints = Utilities.getPaintList(region, region.getBackground(), this, opacity);
         return awtPaints;
      } else {
         return null;
      }
   }

   /**
    * Return the Region Border associated with the Region (may be null). Correspond to the "-fx-region-border" CSS
    * property.
    *
    * @return the Region Border associated with ther Region
    * @see org.jfxconverter.utils.CSSProperties#REGION_BORDER
    */
   private List<BorderWrapper> getRegionBorder() {
      if (allProperties.containsKey(REGION_BORDER)) {
         Border border = (Border) allProperties.get(REGION_BORDER);
         List<BorderWrapper> paints = Utilities.getPaintList(region, border);
         return paints;
      } else if (region.getBorder() != null) {
         List<BorderWrapper> paints = Utilities.getPaintList(region, region.getBorder());
         return paints;
      } else {
         return null;
      }
   }

   /**
    * Return true if the Region associated shape is scaled. Correspond to the "-fx-scale-shape" CSS property.
    *
    * @return true if the Region associated shape is scaled
    * @see org.jfxconverter.utils.CSSProperties#SCALE_SHAPE
    */
   private boolean isScaleShape() {
      if (allProperties.containsKey(SCALE_SHAPE)) {
         return (Boolean) allProperties.get(SCALE_SHAPE);
      } else {
         return false;
      }
   }

   /**
    * Return the Background Paint of the Region. Correspond to the "-fx-background-color" and "-fx-background-image" CSS
    * properties.
    *
    * @return the Background Paint of the Region
    * @see org.jfxconverter.utils.CSSProperties#BACKGROUND_COLOR
    * @see org.jfxconverter.utils.CSSProperties#BACKGROUND_IMAGE
    */
   private Paint getBackground() {
      double opacity = this.getOpacity();
      if (allProperties.containsKey(BACKGROUND_COLOR)) {
         Paint paint = (javafx.scene.paint.Paint) allProperties.get(BACKGROUND_COLOR);
         return paint;
      }
      if (allProperties.containsKey(BACKGROUND_IMAGE)) {
         URL url = (URL) allProperties.get(BACKGROUND_IMAGE);
         return null;
      } else {
         return null;
      }
   }

   /**
    * Convert the Region.
    *
    * @return the xml node
    */
   @Override
   public XMLNode convert() {
      SVGPath path = getSVGPath();
      boolean isScaled = isScaleShape();
      XMLNode node = new XMLNode("g");
      convertBackground(node, path, isScaled);
      convertBorder(node, path, isScaled);
      xmlParent.addChild(node);
      return node;
   }

   private Scale getScale(SVGPath path, double width, double height) {
      Bounds rec = path.getBoundsInLocal();
      double scaleX = width / rec.getWidth();
      double scaleY = height / rec.getHeight();
      return new Scale(scaleX, scaleY);
   }

   /**
    * Convert the background of the Region.
    *
    * @param node the node
    * @param path the SVGPath (or null if there is no path)
    * @param isScaled true if the SVGPath is scaled
    */
   private void convertBackground(XMLNode node, SVGPath path, boolean isScaled) {
      List<BackgroundWrapper> wrappers = getRegionBackground();
      if (wrappers != null && !wrappers.isEmpty()) {
         Iterator<BackgroundWrapper> it = wrappers.iterator();
         while (it.hasNext()) {
            BackgroundWrapper wrapper = it.next();
            if (wrapper.getPaint() == null) {
               continue;
            }
            int width = (int) wrapper.getWidth();
            int height = (int) wrapper.getHeight();
            if (width > 0 && height > 0) {
               if (path == null) {
                  double x = 0;
                  double y = 0;
                  if (wrapper.isTexture()) {
                     x = wrapper.getX();
                     y = wrapper.getY();
                  }
                  fillRect(node, wrapper, x, y, width, height);
               } else if (isScaled) {
                  Scale scale = getScale(path, width, height);
                  fillPath(node, path, wrapper, scale);
               }
            }
         }
      }

      Paint paint = getBackground();
      if (paint != null) {
         fillRect(node, null, region.getLayoutX(), region.getLayoutY(), region.getPrefWidth(), region.getPrefHeight());
      }
   }

   private void fillPath(XMLNode gNode, SVGPath path, BackgroundWrapper wrapper, Scale scale) {
      if (wrapper.isTexture()) {

      } else {
         XMLNode node = new XMLNode("path");
         double x = path.getLayoutX();
         double y = path.getLayoutY();
         node.addAttribute("x", x);
         node.addAttribute("y", y);
         node.addAttribute("d", path.getContent());
         StringBuilder buf = new StringBuilder();
         Paint paint = wrapper.getPaint();
         addFill(paint, buf);
         setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
         if (scale != null) {
            node.addAttribute("transform", Utilities.getScale(scale));
         }
         gNode.addChild(node);
      }
   }

   private void fillRect(XMLNode gNode, BackgroundWrapper wrapper, double x, double y, double width, double height) {
      if (wrapper.isTexture()) {

      } else {
         double arcWidth = wrapper.getMeanRadiiWidth();
         double arcHeight = wrapper.getMeanRadiiHeight();
         XMLNode node = new XMLNode("rect");
         node.addAttribute("width", width);
         node.addAttribute("height", height);
         node.addAttribute("x", x);
         node.addAttribute("y", y);
         if (arcWidth != 0) {
            node.addAttribute("rx", arcWidth / 2);
         }
         if (arcHeight != 0) {
            node.addAttribute("ry", arcHeight / 2);
         }
         StringBuilder buf = new StringBuilder();
         Paint paint = wrapper.getPaint();
         addFill(paint, buf);
         setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
         gNode.addChild(node);
      }
   }

   /**
    * Convert the background of the Region.
    *
    * @param node the node
    * @param path the SVGPath (or null if there is no path)
    * @param isScaled true if the SVGPath is scaled
    */
   private void convertBorder(XMLNode node, SVGPath path, boolean isScaled) {
      List<BorderWrapper> bWrappers = this.getRegionBorder();
      if (bWrappers != null && !bWrappers.isEmpty()) {
         Iterator<BorderWrapper> it = bWrappers.iterator();
         while (it.hasNext()) {
            BorderWrapper wrapper = it.next();
            if (wrapper.getPaint() == null) {
               continue;
            }
            int width = (int) region.getWidth();
            int height = (int) region.getHeight();
            if (width > 0 && height > 0) {
               if (path == null) {
                  double x = 0;
                  double y = 0;
                  strokeRect(node, wrapper, x, y, width, height);
               } else if (isScaled) {
                  Scale scale = getScale(path, width, height);
                  strokePath(node, path, wrapper, scale);
               } else {
                  strokePath(node, path, wrapper, null);
               }
            }
         }
      }
   }

   private void strokeRect(XMLNode gNode, BorderWrapper wrapper, double x, double y, double width, double height) {
      double arcWidth = wrapper.getMeanRadiiWidth();
      double arcHeight = wrapper.getMeanRadiiHeight();
      XMLNode node = new XMLNode("rect");
      node.addAttribute("width", width);
      node.addAttribute("height", height);
      node.addAttribute("x", x);
      node.addAttribute("y", y);
      if (arcWidth != 0) {
         node.addAttribute("rx", arcWidth / 2);
      }
      if (arcHeight != 0) {
         node.addAttribute("ry", arcHeight / 2);
      }
      StringBuilder buf = new StringBuilder();
      Paint paint = wrapper.getPaint();
      addStroke(paint, buf);
      setStrokeOpacity(paint, node);
      String style = buf.toString();
      node.addAttribute("style", style);
      gNode.addChild(node);
   }

   private void strokePath(XMLNode gNode, SVGPath path, BorderWrapper wrapper, Scale scale) {
      XMLNode node = new XMLNode("path");
      node.addAttribute("d", path.getContent());
      StringBuilder buf = new StringBuilder();
      Paint paint = wrapper.getPaint();
      addStroke(paint, buf);
      setStrokeOpacity(paint, node);
      String style = buf.toString();
      node.addAttribute("style", style);
      if (scale != null) {
         node.addAttribute("transform", Utilities.getScale(scale));
      }
      gNode.addChild(node);
   }
}
