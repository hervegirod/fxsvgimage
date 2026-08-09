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
package org.girod.javafx.svgimage.fromjfx.awt.converters;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import org.girod.javafx.svgimage.fromjfx.awt.utils.AwtUtilities;
import org.girod.javafx.svgimage.fromjfx.awt.utils.JFXShapeUtilities;
import org.girod.javafx.svgimage.fromjfx.awt.utils.ShapeUtilities;
import org.girod.javafx.svgimage.fromjfx.awt.wrappers.AwtBackgroundWrapper;
import org.girod.javafx.svgimage.fromjfx.awt.wrappers.AwtBorderWrapper;

/**
 * A delegate which converts Regions.
 *
 * @since 1.8
 */
public class RegionConverter extends AbstractAwtConverter {
   protected Region region = null;

   /**
    * Constructor.
    *
    * @param converter the AwtConverterDelegate
    * @param region the Region
    */
   public RegionConverter(AwtConverterDelegate converter, Region region) {
      super(converter, region);
      this.region = region;
   }

   /**
    * Return the delegate Region Node.
    *
    * @return the delegate Region Node
    */
   @Override
   public Parent getParent() {
      return region;
   }

   /**
    * Return the Region Background associated with the Region (may be null). Correspond to the "-fx-region-background"
    * CSS property.
    *
    * @return the Region Background associated with the Region
    * @see org.jfxconverter.utils.CSSProperties#REGION_BACKGROUND
    */
   protected List<AwtBackgroundWrapper> getRegionBackground() {
      double opacity = this.getOpacity();
      if (allProperties.containsKey(REGION_BACKGROUND)) {
         Background background = (Background) allProperties.get(REGION_BACKGROUND);
         List<AwtBackgroundWrapper> awtPaints = AwtUtilities.getAWTPaintList(region, background, this, delegate.getGrayScale(), opacity);
         return awtPaints;
      } else if (region.getBackground() != null) {
         List<AwtBackgroundWrapper> awtPaints = AwtUtilities.getAWTPaintList(region, region.getBackground(), this, delegate.getGrayScale(), opacity);
         return awtPaints;
      } else {
         return null;
      }
   }

   /**
    * Return the Region Border associated with the Region (may be null). Correspond to the "-fx-region-border"
    * CSS property.
    *
    * @return the Region Border associated with ther Region
    * @see org.jfxconverter.utils.CSSProperties#REGION_BORDER
    */
   protected List<AwtBorderWrapper> getRegionBorder() {
      if (allProperties.containsKey(REGION_BORDER)) {
         Border border = (Border) allProperties.get(REGION_BORDER);
         List<AwtBorderWrapper> awtPaints = AwtUtilities.getAWTPaintList(region, border);
         return awtPaints;
      } else if (region.getBorder() != null) {
         List<AwtBorderWrapper> awtPaints = AwtUtilities.getAWTPaintList(region, region.getBorder());
         return awtPaints;
      } else {
         return null;
      }
   }

   /**
    * Return the SVGPath associated with the Region (may be null). Correspond to the "-fx-shape" CSS property.
    *
    * @return the SVGPath associated with the Region
    * @see org.jfxconverter.utils.CSSProperties#SHAPE
    */
   protected SVGPath getSVGPath() {
      if (allProperties.containsKey(SHAPE)) {
         return (SVGPath) allProperties.get(SHAPE);
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
   protected boolean isScaleShape() {
      if (allProperties.containsKey(SCALE_SHAPE)) {
         return (Boolean) allProperties.get(SCALE_SHAPE);
      } else {
         return false;
      }
   }

   /**
    * Return the Background Paint of the Region. Correspond to the "-fx-background-color" and "-fx-background-image" CSS properties.
    *
    * @return the Background Paint of the Region
    * @see org.jfxconverter.utils.CSSProperties#BACKGROUND_COLOR
    * @see org.jfxconverter.utils.CSSProperties#BACKGROUND_IMAGE
    */
   protected Paint getBackground() {
      double opacity = this.getOpacity();
      if (allProperties.containsKey(BACKGROUND_COLOR)) {
         javafx.scene.paint.Paint paint = (javafx.scene.paint.Paint) allProperties.get(BACKGROUND_COLOR);
         Paint awtPaints = AwtUtilities.getAWTPaint(region, paint, delegate.getGrayScale(), opacity);
         return awtPaints;
      }
      if (allProperties.containsKey(BACKGROUND_IMAGE)) {
         URL url = (URL) allProperties.get(BACKGROUND_IMAGE);
         Paint paint = AwtUtilities.getAWTTexture(url, region, delegate.getGrayScale(), opacity);
         return paint;
      } else {
         return null;
      }
   }

   private java.awt.Shape scalePath(SVGPath path, double width, double height) {
      Bounds rec = path.getBoundsInLocal();
      double scaleX = width / rec.getWidth();
      double scaleY = height / rec.getHeight();
      // this code now works like a charm
      AffineTransform tr = AffineTransform.getScaleInstance(scaleX, scaleY);
      java.awt.Shape awtShape = AwtUtilities.toAWTPath(path, tr);
      Rectangle2D rec2 = awtShape.getBounds2D();
      // we need to be sure that the shape is always put at 0, 0
      if (rec2.getX() != 0 || rec2.getY() != 0) {
         awtShape = ShapeUtilities.createTransformedShape(awtShape, AffineTransform.getTranslateInstance(-rec2.getX(), -rec2.getY()));
      }
      return awtShape;
   }

   /**
    * Convert the background of the Region.
    *
    * @param g2D the Graphics2D
    * @param path the SVGPath (or null if there is no path)
    * @param awtShape the converted Awt Shape if the SVGPath is scaled
    * @param isScaled true if the SVGPath is scaled
    */
   private void convertBackground(Graphics2D g2D, SVGPath path, java.awt.Shape awtShape, boolean isScaled, boolean isDisabled) {
      List<AwtBackgroundWrapper> wrappers = getRegionBackground();
      if (wrappers != null && !wrappers.isEmpty()) {
         Iterator<AwtBackgroundWrapper> it = wrappers.iterator();
         while (it.hasNext()) {
            AwtBackgroundWrapper wrapper = it.next();
            if (wrapper.getPaint() == null) {
               continue;
            }
            
            delegate.setPaint(wrapper.getPaint());
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
                  if (wrapper.hasRadii()) {
                     g2D.fillRoundRect((int) x, (int) y, width, height, wrapper.getMeanRadiiWidth(), wrapper.getMeanRadiiHeight());
                  } else {
                     g2D.fillRect((int) x, (int) y, width, height);
                  }
               } else if (!isScaled && awtShape != null) {
                  if (isSignificant(awtShape)) {
                     g2D.fill(awtShape);
                  }
               } else if (isScaled) {
                  awtShape = scalePath(path, wrapper.getWidth(), wrapper.getHeight());
                  if (isSignificant(awtShape)) {
                     g2D.fill(awtShape);
                  }
               }
            }
         }
      }

      Paint paint = getBackground();
      if (paint != null) {
         delegate.setPaint(paint);
         g2D.fillRect((int) region.getLayoutX(), (int) region.getLayoutY(), (int) region.getPrefWidth(), (int) region.getPrefHeight());
      }
   }

   /**
    * Convert the border of the Region.
    *
    * @param g2D the Graphics2D
    * @param path the SVGPath (or null if there is no path)
    * @param awtShape the converted Awt Shape if the SVGPath is scaled
    * @param isScaled true if the SVGPath is scaled
    */
   private void convertBorder(Graphics2D g2D, SVGPath path, java.awt.Shape awtShape, boolean isScaled, boolean isDisabled) {
      List<AwtBorderWrapper> bWrappers = this.getRegionBorder();
      if (bWrappers != null && !bWrappers.isEmpty()) {
         Iterator<AwtBorderWrapper> it = bWrappers.iterator();
         while (it.hasNext()) {
            AwtBorderWrapper wrapper = it.next();
            if (wrapper.getPaint() == null) {
               continue;
            }
            delegate.setPaint(wrapper.getPaint());
            int width = (int) region.getWidth();
            int height = (int) region.getHeight();
            if (width > 0 && height > 0) {
               Stroke oldStroke = g2D.getStroke();
               g2D.setStroke(AwtUtilities.createStroke(wrapper.getWidth(), wrapper.getStrokeStyle()));
               if (path == null) {
                  if (wrapper.hasRadii()) {
                     g2D.drawRoundRect((int) wrapper.getX(), (int) wrapper.getY(), width, height, wrapper.getMeanRadiiWidth(),
                        wrapper.getMeanRadiiHeight());
                  } else {
                     g2D.drawRect((int) wrapper.getX(), (int) wrapper.getY(), width, height);
                  }
               } else if (!isScaled && awtShape != null) {
                  if (isSignificant(awtShape)) {
                     g2D.draw(awtShape);
                  }
               } else if (isScaled) {
                  awtShape = scalePath(path, width, height);
                  if (isSignificant(awtShape)) {
                     g2D.draw(awtShape);
                  }
               }
               g2D.setStroke(oldStroke);
            }
         }
      }
   }

   /**
    * Convert the Region.
    */
   @Override
   public Boolean convert(boolean isDisabled) {
      Graphics2D g2D = delegate.getGraphics2D();
      SVGPath path = getSVGPath();
      boolean isScaled = isScaleShape();
      java.awt.Shape awtShape = null;
      if (!isScaled) {
         // if the path is not scaled, we will only convert it once, because it will be the same for all Backgrounds and Borders
         awtShape = JFXShapeUtilities.getAWTPath(path);
      }
      convertBackground(g2D, path, awtShape, isScaled, isDisabled);
      convertBorder(g2D, path, awtShape, isScaled, isDisabled);
      return true;
   }

   private boolean isSignificant(java.awt.Shape shape) {
      if (shape == null) {
         return false;
      } else {
         Rectangle rec = shape.getBounds();
         return !(rec.getWidth() < 1 || rec.getHeight() < 1);
      }

   }
}
