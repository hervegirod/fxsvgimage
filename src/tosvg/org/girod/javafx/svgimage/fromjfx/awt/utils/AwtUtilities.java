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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import org.girod.javafx.svgimage.fromjfx.utils.CSSProperties;
import org.girod.javafx.svgimage.fromjfx.utils.CSSProperty;
import org.girod.javafx.svgimage.fromjfx.awt.wrappers.AwtBackgroundWrapper;
import org.girod.javafx.svgimage.fromjfx.awt.wrappers.AwtBorderWrapper;
import org.girod.javafx.svgimage.fromjfx.utils.OpacityFilter;
import org.girod.javafx.svgimage.fromjfx.utils.Utilities;
import org.girod.javafx.svgimage.fromjfx.awt.NodeGraphics2DConverter;

/**
 * A utilities class.
 *
 * @since 1.8
 */
public class AwtUtilities implements CSSProperties {

   private AwtUtilities() {
   }

   /**
    * Return a Node bounds.
    *
    * @param node the node
    * @return the node bounds
    */
   public static Rectangle2D getBounds(Node node) {
      Bounds bounds = node.getBoundsInLocal();
      Rectangle2D rec = new Rectangle2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
      return rec;
   }

   /**
    * Convert a SVGPath to an Awt Shape and apply an AffineTransform on it.
    *
    * @param path the SVGPath
    * @param transform the AffineTransform
    * @return the Awt Shape
    */
   public static Shape toAWTPath(SVGPath path, AffineTransform transform) {
      if (path != null) {
         SVGPathConverter converter = new SVGPathConverter();
         return converter.convert(path, transform);
      } else {
         return null;
      }
   }

   /**
    * Return the origin of a SVGPath (using the first MoveTo command).
    *
    * @param path the SVGPath
    * @return the origin of a SVGPath
    */
   public static Point2D getOrigin(SVGPath path) {
      if (path != null) {
         SVGPathConverter converter = new SVGPathConverter();
         return converter.getOrigin(path);
      } else {
         return null;
      }
   }

   /**
    * Return true if the position of a SVGPath is relative (using the first m or M moveTo command). Note that it seems that the type of the first
    * moveTo command seems to have no effect on the appearance of the Shape itself.
    *
    * @param path the SVGPath
    * @return true if the position of a SVGPath is relative
    */
   public static boolean isRelative(SVGPath path) {
      if (path != null) {
         SVGPathConverter converter = new SVGPathConverter();
         return converter.isRelative(path);
      } else {
         return false;
      }
   }

   /**
    * Extract the pseudo-classes of a Node.
    *
    * @param node the Node
    * @return the pseudo-classes
    */
   public static Set<String> extractPseudoClasses(Node node) {
      ObservableSet<PseudoClass> pseudoClasses = node.getPseudoClassStates();
      Set<String> set = new HashSet<>();
      Iterator<PseudoClass> it = pseudoClasses.iterator();
      while (it.hasNext()) {
         set.add(it.next().getPseudoClassName());
      }
      return set;
   }

   /**
    * Extract the properties of a Node.
    *
    * @param node the Node
    * @return the properties
    */
   public static Map<String, CSSProperty> extractProperties(Node node) {
      Map<String, CSSProperty> props = new HashMap<>();
      List<CssMetaData<? extends Styleable, ?>> metaDatas = node.getCssMetaData();
      Iterator<CssMetaData<? extends Styleable, ?>> it = metaDatas.iterator();
      while (it.hasNext()) {
         // the cast is necessary to avoid to have raw types
         CssMetaData<Node, ?> metaData = (CssMetaData<Node, ?>) it.next();
         StyleableProperty<?> prop = metaData.getStyleableProperty(node);
         String propName = metaData.getProperty();
         Object value = prop.getValue();
         if (value != null) {
            props.put(propName, new CSSProperty(prop, value));
         }
      }
      return props;
   }

   /**
    * Return the Awt AffineTransform corresponding to a JavaFX Transform.
    *
    * @param tr the JavaFX Transform
    * @return the Awt AffineTransform
    */
   public static AffineTransform getAffineTransform(Transform tr) {
      AffineTransform affine;
      if (tr instanceof Rotate) {
         Rotate rotate = (Rotate) tr;
         double angle = Math.toRadians(rotate.getAngle());
         affine = AffineTransform.getRotateInstance(angle, rotate.getPivotX(), rotate.getPivotY());
      } else {
         // The following line is commented out as it has a cut/paste error
         // leading to zero scaling along the y-axis.
         // affine = new AffineTransform(tr.getMxx(), tr.getMyx(), tr.getMxy(), tr.getMxy(), tr.getTx(), tr.getTy());
         // The following line is the correction for that error, courtesy of Mark Schmieder
         affine = new AffineTransform(tr.getMxx(), tr.getMyx(), tr.getMxy(), tr.getMyy(), tr.getTx(), tr.getTy());
      }
      return affine;
   }

   /**
    * Return the FontPosture of a JavaFX Font.
    *
    * @param font the Font
    * @return the FontPosture
    */
   public static FontPosture getPosture(Font font) {
      StringTokenizer tok = new StringTokenizer(font.getStyle());
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken().toLowerCase();
         if (tk.equals("italic")) {
            return FontPosture.ITALIC;
         }
      }
      return FontPosture.REGULAR;
   }

   /**
    * Return the FontWeight of a JavaFX Font.
    *
    * @param font the Font
    * @return the FontWeight
    */
   public static FontWeight getWeight(Font font) {
      StringTokenizer tok = new StringTokenizer(font.getStyle());
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken().toLowerCase();
         if (tk.equals("bold")) {
            return FontWeight.BOLD;
         } else if (tk.equals("bolder")) {
            return FontWeight.EXTRA_BOLD;
         } else if (tk.equals("lighter")) {
            return FontWeight.LIGHT;
         }
      }
      return FontWeight.NORMAL;
   }

   private static int getAWTStyle(Font font) {
      int style = java.awt.Font.PLAIN;
      StringTokenizer tok = new StringTokenizer(font.getStyle());
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken().toLowerCase();
         if (tk.equals("bold") || tk.equals("bolder")) {
            style |= java.awt.Font.BOLD;
         } else if (tk.equals("italic")) {
            style |= java.awt.Font.ITALIC;
         }
      }
      return style;
   }

   /**
    * Return the Awt Font corresponding to a JavaFX Font.
    *
    * @param font the JavaFX Font
    * @return the Awt Font
    */
   public static java.awt.Font getAWTFont(Font font) {
      int sizeInt = (int) font.getSize();
      int style = getAWTStyle(font);
      java.awt.Font awtFont = new java.awt.Font(font.getName(), style, sizeInt);
      if (sizeInt != font.getSize()) {
         awtFont = awtFont.deriveFont((float) font.getSize());
      }
      return awtFont;
   }

   /**
    * Create a stroke of a specified width and a stroke style.
    *
    * @param width the stroke width
    * @param strokeStyle the stroke style
    * @return the stroke
    */   
   public static BasicStroke createStroke(double width, BorderStrokeStyle strokeStyle) {
      if (strokeStyle != null) {
         int lineCap = BasicStroke.CAP_BUTT;
         StrokeLineCap cap = strokeStyle.getLineCap();
         if (cap.equals(StrokeLineCap.ROUND)) {
            lineCap = BasicStroke.CAP_ROUND;
         } else if (cap.equals(StrokeLineCap.SQUARE)) {
            lineCap = BasicStroke.CAP_SQUARE;
         }
         int lineJoin = BasicStroke.JOIN_BEVEL;
         StrokeLineJoin join = strokeStyle.getLineJoin();
         if (join.equals(StrokeLineJoin.MITER)) {
            lineJoin = BasicStroke.JOIN_MITER;
         } else if (cap.equals(StrokeLineJoin.ROUND)) {
            lineJoin = BasicStroke.JOIN_ROUND;
         }
         float miterLimit = (float) strokeStyle.getMiterLimit();
         float dashPhase = (float) strokeStyle.getDashOffset();
         List<Double> darray = strokeStyle.getDashArray();
         BasicStroke stroke;
         if (darray.isEmpty()) {
            stroke = new BasicStroke((float) width, lineCap, lineJoin);
         } else {
            float[] array = new float[darray.size()];
            for (int i = 0; i < darray.size(); i++) {
               array[i] = darray.get(i).floatValue();
            }
            stroke = new BasicStroke((float) width, lineCap, lineJoin, miterLimit, array, dashPhase);
         }
         return stroke;
      } else {
         return createStroke(width);
      }
   }

   /**
    * Create a stroke of a specified width.
    *
    * @param width the stroke width
    * @return the stroke
    */
   public static BasicStroke createStroke(double width) {
      BasicStroke stroke = new BasicStroke((float) width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0);
      return stroke;
   }

   private static BorderStrokeStyle mergeBorderStyles(BorderStroke stroke) {
      BorderStrokeStyle botstyle = stroke.getBottomStyle();
      BorderStrokeStyle topstyle = stroke.getTopStyle();
      BorderStrokeStyle leftstyle = stroke.getLeftStyle();
      BorderStrokeStyle rightstyle = stroke.getRightStyle();
      if (botstyle.getDashArray().isEmpty() && topstyle.getDashArray().isEmpty() && leftstyle.getDashArray().isEmpty() && rightstyle.getDashArray().isEmpty()) {
         return null;
      } else if (!botstyle.getDashArray().isEmpty()) {
         return botstyle;
      } else if (!topstyle.getDashArray().isEmpty()) {
         return topstyle;
      } else if (!leftstyle.getDashArray().isEmpty()) {
         return leftstyle;
      } else if (!rightstyle.getDashArray().isEmpty()) {
         return rightstyle;
      } else {
         return null;
      }
   }

   /**
    * Return true if the Node is Disabled, and the configuration allows to show a gray-Scale color for disabled Nodes.
    *
    * @param node the Node
    * @return true if the Node is to be shown as Disabled
    */
   private static boolean isDisabled(Node node) {
      return node.isDisabled();
   }

   /**
    * Return the list of BorderWrappers for a Region. Note that a JavaFX Border contains a list of strokes or Paints.
    *
    * @param region the Region
    * @param border the Background
    * @return the list of BorderWrappers
    */
   public static List<AwtBorderWrapper> getAWTPaintList(Region region, Border border) {
      List<AwtBorderWrapper> borders = new ArrayList<>();
      List<BorderStroke> strokes = border.getStrokes();
      if (!strokes.isEmpty()) {
         Iterator<BorderStroke> it = strokes.iterator();
         while (it.hasNext()) {
            BorderStroke stroke = it.next();
            CornerRadii radii = stroke.getRadii();
            BorderWidths widths = stroke.getWidths();

            double width = (widths.getBottom() + widths.getLeft() + widths.getRight() + widths.getTop()) / 4;
            if (width != 0) {
               Paint paint = stroke.getBottomStroke();
               if (paint == null) {
                  paint = stroke.getLeftStroke();
               }
               if (paint == null) {
                  paint = stroke.getRightStroke();
               }
               if (paint == null) {
                  paint = stroke.getTopStroke();
               }
               if (paint != null) {
                  java.awt.Paint awtPaint = getAWTPaint(region, paint);
                  AwtBorderWrapper wrapper = new AwtBorderWrapper(awtPaint, width);
                  BorderStrokeStyle strokeStyle = mergeBorderStyles(stroke);
                  if (strokeStyle != null) {
                     wrapper.setStrokeStyle(strokeStyle);
                  }
                  if (radii != null) {
                     wrapper.setRadii(radii);
                  }
                  borders.add(wrapper);
               }
            }
         }
      }
      return borders;
   }

   /**
    * Return the list of BackgroundWrappers for a Region.Note that a JavaFX Background contains a list of fills or Paints.
    *
    * @param region the Region
    * @param background the Background
    * @param converter the NodeConverter
    * @param grayScale the grayScale in percent
    * @param opacity the opacity
    * @return the list of BackgroundWrappers
    */
   public static List<AwtBackgroundWrapper> getAWTPaintList(Region region, Background background, NodeGraphics2DConverter converter, int grayScale, double opacity) {
      List<AwtBackgroundWrapper> paints = new ArrayList<>();
      List<BackgroundFill> fills = background.getFills();
      List<BackgroundImage> images = background.getImages();
      if (!images.isEmpty()) {
         Iterator<BackgroundImage> it = images.iterator();
         while (it.hasNext()) {
            BackgroundImage image = it.next();
            BackgroundSize size = image.getSize();
            double width = size.getWidth();
            double height = size.getHeight();
            if (size.isContain()) {
               width = region.getWidth();
               height = region.getHeight();
            } else if (size.isWidthAsPercentage() || size.isHeightAsPercentage()) {
               double fitWidth = region.getWidth();
               double fitHeight = region.getHeight();
               if (size.isWidthAsPercentage()) {
                  fitWidth = width * fitWidth;
               }
               if (size.isHeightAsPercentage()) {
                  fitHeight = height * fitHeight;
               }
               Image _image = image.getImage();
               if (fitWidth < 0) {
                  fitHeight = _image.getWidth() / _image.getHeight() * fitHeight;
               }
               if (fitHeight < 0) {
                  fitHeight = _image.getHeight() / _image.getWidth() * fitWidth;
               }
               width = fitWidth;
               height = fitHeight;
            }

            if (width > 0 && height > 0) {
               Image _image1 = image.getImage();
               BufferedImage buf = new BufferedImage((int) _image1.getWidth(), (int) _image1.getHeight(), BufferedImage.TYPE_INT_ARGB);
               // this seems pretty complex, but we need to have the right size for the resulting Awt image
               buf = SwingFXUtils.fromFXImage(image.getImage(), buf);
               if (_image1.getWidth() != width || _image1.getHeight() != height) {
                  java.awt.Image _image = buf.getScaledInstance((int) width, (int) height, java.awt.Image.SCALE_SMOOTH);
                  if (_image instanceof BufferedImage) {
                     buf = (BufferedImage) _image;
                  } else {
                     // in the case that the result is not a BufferedImage, we need to create one with our image
                     BufferedImage _buf = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
                     Graphics2D g2d = _buf.createGraphics();
                     g2d.drawImage(_image, 0, 0, null);
                     buf = _buf;
                  }
               }
               Rectangle2D rec = new Rectangle2D.Double(0, 0, width, height);
               TexturePaint paint = new TexturePaint(buf, rec);
               AwtBackgroundWrapper wrapper = new AwtBackgroundWrapper(paint);
               wrapper.setImagePosition(image.getPosition());
               double x = rec.getX();
               double y = rec.getY();
               BackgroundPosition position = image.getPosition();
               if (converter.getAllProperties().containsKey(REGION_BACKGROUND_POSITION)) {
                  position = (BackgroundPosition) converter.getAllProperties().get(REGION_BACKGROUND_POSITION);
               }
               if (position != null) {
                  if (position.getHorizontalPosition() == 0.5) {
                     x += (region.getWidth() - width) / 2;
                  }
                  if (position.getVerticalPosition() == 0.5) {
                     y += (region.getHeight() - height) / 2;
                  }
               }
               wrapper.setPosition(x, y);
               wrapper.setImagePosition(image.getPosition());
               wrapper.setSize(rec.getWidth(), rec.getHeight());
               paints.add(wrapper);
            }
         }
      }
      if (!fills.isEmpty()) {
         Iterator<BackgroundFill> it2 = fills.iterator();
         while (it2.hasNext()) {
            BackgroundFill fill = it2.next();
            Paint paint = fill.getFill();
            Insets insets = fill.getInsets();
            double x = insets.getLeft();
            double y = insets.getTop();
            double width = region.getWidth() - insets.getRight();
            double height = region.getHeight() - insets.getBottom();
            if (width > 0 && height > 0) {
               java.awt.Paint awtPaint = getAWTPaint(region, paint, grayScale, opacity);
               if (awtPaint != null) {
                  AwtBackgroundWrapper wrapper = new AwtBackgroundWrapper(awtPaint);
                  CornerRadii radii = fill.getRadii();
                  wrapper.setRadii(radii);
                  wrapper.setPosition(x, y);
                  wrapper.setSize(width, height);
                  paints.add(wrapper);
               }
            }
         }
      }
      return paints;
   }

   /**
    * Return the Awt Color corresponding to a JavaFX Color, including transparent colors.Transparent JavaFX Colors will return transparent Awt Colors.
    *
    * @param shape the shape
    * @param paint the Paint
    * @param grayScale the grayScale percent
    * @param isDisabled true if the Node is disabled
    * @return the Awt Color
    */
   public static java.awt.Paint getAWTPaintAcceptTransparent(javafx.scene.shape.Shape shape, Paint paint, int grayScale, boolean isDisabled) {
      Bounds bounds = shape.getBoundsInLocal();
      if (paint instanceof Color) {
         return getAWTColor((Color) paint, grayScale, 1d, isDisabled);
      } else if (paint instanceof LinearGradient) {
         return getAWTLinearGradient(bounds, (LinearGradient) paint, grayScale, 1d, isDisabled);
      } else if (paint instanceof RadialGradient) {
         return getAWTRadialGradient(bounds, (RadialGradient) paint, grayScale, 1d, isDisabled);
      } else if (paint instanceof ImagePattern) {
         return getAWTTexture((ImagePattern) paint, grayScale, 1d, isDisabled);
      } else {
         return null;
      }
   }

   /**
    * Return the Awt Paint for a JavaFX Paint.
    *
    * @param shape the Shape where to apply the Paint
    * @param opacity the opacity
    * @param grayScale the grayScale in percent
    * @param paint the Paint
    * @return the Awt Paint
    */
   public static java.awt.Paint getAWTPaint(javafx.scene.shape.Shape shape, Paint paint, int grayScale, double opacity) {
      Bounds bounds = shape.getBoundsInLocal();
      if (paint instanceof Color) {
         return getAWTColor((Color) paint, grayScale, opacity, isDisabled(shape));
      } else if (paint instanceof LinearGradient) {
         return getAWTLinearGradient(bounds, (LinearGradient) paint, grayScale, opacity, isDisabled(shape));
      } else if (paint instanceof RadialGradient) {
         return getAWTRadialGradient(bounds, (RadialGradient) paint, grayScale, opacity, isDisabled(shape));
      } else if (paint instanceof ImagePattern) {
         return getAWTTexture((ImagePattern) paint, grayScale, opacity, isDisabled(shape));
      } else {
         return null;
      }
   }

   /**
    * Return the Awt Paint for a JavaFX Paint.
    *
    * @param region the Region where to apply the Paint
    * @param paint the Paint
    * @return the Awt Paint
    */
   public static java.awt.Paint getAWTPaint(Region region, Paint paint) {
      return getAWTPaint(region, paint, -1, -1);
   }

   /**
    * Return the Awt Paint for a JavaFX Paint.
    *
    * @param region the Region where to apply the Paint
    * @param paint the Paint
    * @param grayScale the grayScale in percent
    * @param opacity the opacity
    * @return the Awt Paint
    */
   public static java.awt.Paint getAWTPaint(Region region, Paint paint, int grayScale, double opacity) {
      Bounds bounds = region.getBoundsInLocal();
      if (paint instanceof Color) {
         return getAWTColor((Color) paint, grayScale, opacity, isDisabled(region));
      } else if (paint instanceof LinearGradient) {
         return getAWTLinearGradient(bounds, (LinearGradient) paint, grayScale, opacity, isDisabled(region));
      } else if (paint instanceof RadialGradient) {
         return getAWTRadialGradient(bounds, (RadialGradient) paint, grayScale, opacity, isDisabled(region));
      } else if (paint instanceof ImagePattern) {
         return getAWTTexture((ImagePattern) paint, grayScale, opacity, isDisabled(region));
      } else {
         return null;
      }
   }

   /**
    * Return the Awt Paint for a JavaFX Region and an associated URL for the corresponding Image texture.
    *
    * @param region the JavaFX Region
    * @param url the URL for the corresponding Image texture
    * @param grayScale the grayScale in percent
    * @param opacity the opaoity
    * @return the Awt TexturePaint
    */
   public static TexturePaint getAWTTexture(URL url, Region region, int grayScale, double opacity) {
      try {
         RenderedImage image = ImageIO.read(url);
         if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            if (isDisabled(region)) {
               java.awt.Image _image = createDisabledImage((BufferedImage) image, grayScale);
               if (_image instanceof BufferedImage) {
                  bimage = (BufferedImage) _image;
               }
            }
            // take care of the opacity CSS property
            if (opacity >= 0) {
               java.awt.Image _image = createOpacifiedImage(bimage, opacity, false);
               if (_image instanceof BufferedImage) {
                  bimage = (BufferedImage) _image;
               }
            }
            Rectangle2D rec = new Rectangle2D.Double(region.getLayoutX(), region.getLayoutY(), region.getPrefWidth(), region.getPrefHeight());
            TexturePaint paint = new TexturePaint(bimage, rec);
            return paint;
         } else {
            return null;
         }
      } catch (IOException e) {
         return null;
      }
   }

   /**
    * Return an Awt TexturePaint corresponding to a JavaFX Image.
    *
    * @param image the JavaFX Image
    * @param region the Region
    * @param widths the associated JavaFX BorderWidths
    * @param grayScale the grayScale in percent
    * @return the Awt TexturePaint
    */
   public static TexturePaint getAWTTexture(Image image, Region region, BorderWidths widths, int grayScale) {
      BufferedImage buf = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
      buf = SwingFXUtils.fromFXImage(image, buf);
      if (isDisabled(region)) {
         java.awt.Image _image = createDisabledImage(buf, grayScale);
         if (_image instanceof BufferedImage) {
            buf = (BufferedImage) _image;
         }
      }
      Rectangle2D rec = new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight());
      TexturePaint paint = new TexturePaint(buf, rec);
      return paint;
   }

   /**
    * Return an Awt TexturePaint corresponding to a JavaFX ImagePattern.
    *
    * @param pattern the ImagePattern
    * @param opacity the opacity
    * @param isDisabled for a disabled Node
    * @return the TexturePaint
    */
   private static TexturePaint getAWTTexture(ImagePattern pattern, int grayScale, double opacity, boolean isDisabled) {
      Image image = pattern.getImage();
      BufferedImage buf = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
      buf = SwingFXUtils.fromFXImage(image, buf);
      // take care of the disabled state of the Node
      if (isDisabled) {
         java.awt.Image _image = createDisabledImage(buf, grayScale);
         if (_image instanceof BufferedImage) {
            buf = (BufferedImage) _image;
         }
      }
      // take care of the opacity CSS property
      if (opacity >= 0) {
         java.awt.Image _image = createOpacifiedImage(buf, opacity, false);
         if (_image instanceof BufferedImage) {
            buf = (BufferedImage) _image;
         }
      }
      Rectangle2D rec = new Rectangle2D.Double(pattern.getX(), pattern.getY(), pattern.getWidth(), pattern.getHeight());
      TexturePaint paint = new TexturePaint(buf, rec);
      return paint;
   }

   private static java.awt.RadialGradientPaint getAWTRadialGradient(Bounds bounds, RadialGradient paint, int grayScale, double opacity, boolean isDisabled) {
      List<Stop> stops = paint.getStops();
      java.awt.Color[] colors = new java.awt.Color[stops.size()];
      float[] fractions = new float[stops.size()];
      for (int i = 0; i < stops.size(); i++) {
         colors[i] = getAWTColorAcceptTransparent(stops.get(i).getColor(), grayScale, opacity, isDisabled);
         fractions[i] = (float) stops.get(i).getOffset();
      }
      MultipleGradientPaint.CycleMethod cycleMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
      if (paint.getCycleMethod() == CycleMethod.REFLECT) {
         cycleMethod = MultipleGradientPaint.CycleMethod.REFLECT;
      } else if (paint.getCycleMethod() == CycleMethod.REPEAT) {
         cycleMethod = MultipleGradientPaint.CycleMethod.REPEAT;
      }
      double x = paint.getCenterX();
      double y = paint.getCenterY();
      double radius = paint.getRadius();
      if (paint.isProportional()) {
         x = x * bounds.getWidth();
         y = y * bounds.getHeight();
         radius = bounds.getWidth() > bounds.getHeight() ? radius * bounds.getWidth() : radius * bounds.getHeight();
      }

      java.awt.RadialGradientPaint gradient = new java.awt.RadialGradientPaint((float) x, (float) y,
              (float) radius, fractions, colors, cycleMethod);
      return gradient;
   }

   private static java.awt.LinearGradientPaint getAWTLinearGradient(Bounds bounds, LinearGradient paint, int grayScale, double opacity, boolean isDisabled) {
      List<Stop> stops = paint.getStops();
      java.awt.Color[] colors = new java.awt.Color[stops.size()];
      float[] fractions = new float[stops.size()];
      for (int i = 0; i < stops.size(); i++) {
         colors[i] = getAWTColorAcceptTransparent(stops.get(i).getColor(), grayScale, opacity, isDisabled);
         fractions[i] = (float) stops.get(i).getOffset();
      }
      MultipleGradientPaint.CycleMethod cycleMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
      if (paint.getCycleMethod() == CycleMethod.REFLECT) {
         cycleMethod = MultipleGradientPaint.CycleMethod.REFLECT;
      } else if (paint.getCycleMethod() == CycleMethod.REPEAT) {
         cycleMethod = MultipleGradientPaint.CycleMethod.REPEAT;
      }
      float startX = (float) (paint.getStartX() * bounds.getWidth());
      float startY = (float) (paint.getStartY() * bounds.getHeight());
      float endX = (float) (paint.getEndX() * bounds.getWidth());
      float endY = (float) (paint.getEndY() * bounds.getHeight());
      java.awt.LinearGradientPaint gradient = new java.awt.LinearGradientPaint(startX, startY, endX, endY, fractions, colors, cycleMethod);
      return gradient;
   }

   /**
    * Return the Awt Color corresponding to a JavaFX Color, including transparent colors.Transparent JavaFX Colors will return transparent Awt Colors.
    *
    * @param col the JavaFX Color
    * @param grayScale the grayScale percent
    * @param opacity the opacity
    * @param isDisabled true if the Node is disabled
    * @return the Awt Color
    */
   public static java.awt.Color getAWTColorAcceptTransparent(Color col, int grayScale, double opacity, boolean isDisabled) {
      double _opacity = col.getOpacity();
      if (opacity < _opacity && opacity >= 0) {
         _opacity = opacity;
      }
      java.awt.Color awtColor = new java.awt.Color((int) (col.getRed() * 255), (int) (col.getGreen() * 255), (int) (col.getBlue() * 255), (int) (_opacity * 255));
      if (isDisabled) {
         awtColor = createDisabledColor(awtColor, grayScale);
      }
      return awtColor;
   }

   /**
    * Return the Awt Color corresponding to a JavaFX Color.Transparent JavaFX Colors will return null.
    *
    * @param col the JavaFX Color
    * @param grayScale the grayScale percent
    * @param opacity the opacity
    * @param isDisabled true if the Region is disabled
    * @return the Awt Color
    */
   public static java.awt.Color getAWTColor(Color col, int grayScale, double opacity, boolean isDisabled) {
      java.awt.Color awtColor = Utilities.getAWTColor(col, opacity);
      if (awtColor != null) {
         if (isDisabled) {
            return createDisabledColor(awtColor, grayScale);
         } else {
            return awtColor;
         }
      } else {
         return null;
      }
   }

   /**
    * Creates a disabled version of an Image.
    *
    * @param image the Image
    * @param grayScale the grayScale percent
    * @return the disabled version of the Image
    */
   public static java.awt.Image createDisabledImage(java.awt.Image image, int grayScale) {
      if (grayScale <= 0) {
         return image;
      }
      ImageFilter filter = new GrayFilter(true, grayScale);
      ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
      java.awt.Image newImage = Toolkit.getDefaultToolkit().createImage(producer);
      return newImage;
   }

   /**
    * Creates a opacified version of an Image. Note that it will always create a more transparent version of the Image.
    *
    * @param image the Image
    * @param opacity the opacity value
    * @param multiply true if the current pixels opacity value must be multiplied by the opacity value, false if the opacity value must replace the
    * current pixel opacity value
    * @return the opacified version of the Image
    */
   public static java.awt.Image createOpacifiedImage(java.awt.Image image, double opacity, boolean multiply) {
      OpacityFilter filter = new OpacityFilter(opacity, multiply);
      ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
      java.awt.Image newImage = Toolkit.getDefaultToolkit().createImage(producer);
      return newImage;
   }

   /**
    * Creates a disabled version of a Color.
    *
    * @param color the Color
    * @param grayScale the grayScale percent
    * @return the disabled version of the Color
    */
   public static java.awt.Color createDisabledColor(java.awt.Color color, int grayScale) {
      if (grayScale <= 0) {
         return color;
      }

      color = new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * (float) grayScale / 100));
      return color;
   }
}
