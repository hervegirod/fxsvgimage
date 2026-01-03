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
package org.girod.javafx.svgimage.tosvg.utils;

import java.io.File;
import java.io.IOException;
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
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Scale;
import org.girod.javafx.svgimage.tosvg.wrappers.BackgroundWrapper;
import org.girod.javafx.svgimage.tosvg.wrappers.BorderWrapper;

/**
 * A utilities class.
 *
 * @since 1.0
 */
public class Utilities implements CSSProperties {

   private Utilities() {
   }

   /**
    * Convert a JavaFX color to an rgb() string.
    *
    * @param color the color
    * @return the rgb() string
    */
   public static String convertColor(Color color) {
      int red = (int) (255 * color.getRed());
      int green = (int) (255 * color.getGreen());
      int blue = (int) (255 * color.getBlue());
      return "rgb(" + red + "," + green + "," + blue + ")";
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
    * Return the list of BackgroundWrappers for a Region. Note that a JavaFX Background contains a list of fills or Paints.
    *
    * @param region the Region
    * @param background the Background
    * @param converter the NodeConverter
    * @param opacity the opacity
    * @return the list of BackgroundWrappers
    */
   public static List<BackgroundWrapper> getPaintList(Region region, Background background, NodeConverter converter, double opacity) {
      List<BackgroundWrapper> paints = new ArrayList<>();
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
               ImagePattern paint = new ImagePattern(_image1);
               Rectangle2D rec = new Rectangle2D(0, 0, width, height);
               BackgroundWrapper wrapper = new BackgroundWrapper(paint);
               wrapper.setImagePosition(image.getPosition());
               double x = rec.getMinX();
               double y = rec.getMinY();
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
               if (paint != null) {
                  BackgroundWrapper wrapper = new BackgroundWrapper(paint);
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
    * Return the list of BorderWrappers for a Region. Note that a JavaFX Border contains a list of strokes or Paints.
    *
    * @param region the Region
    * @param border the Background
    * @return the list of BorderWrappers
    */
   public static List<BorderWrapper> getPaintList(Region region, Border border) {
      List<BorderWrapper> borders = new ArrayList<>();
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
                  BorderWrapper wrapper = new BorderWrapper(paint, width);
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
    * Return the SVG scale() string for a JavaFX scale.
    *
    * @param scale the scale transform
    * @return the scale() string
    */
   public static String getScale(Scale scale) {
      return "scale(" + scale.getX() + "," + scale.getY() + ")";
   }

   /**
    * Split a string into fixed-size parts.
    *
    * @param string the string to split
    * @param partitionSize the max size of each part
    * @return the list of parts
    */
   public static List<String> splitString(String string, int partitionSize) {
      List<String> parts = new ArrayList<>();
      int len = string.length();
      for (int i = 0; i < len; i += partitionSize) {
         parts.add(string.substring(i, Math.min(len, i + partitionSize)));
      }
      return parts;
   }

   /**
    * Return the path of a File relative to a directory.
    *
    * @param baseDir the base directory
    * @param file the File
    * @param strict true if this will fall back to the absolute path of the File if the File is not under the base directory
    * @return the path of the File relative to the directory
    */
   public static String getRelativePath(File baseDir, File file, boolean strict) {
      File parent = file;
      String path = file.getAbsolutePath();
      boolean isUnderBaseDir = false;
      while (true) {
         parent = parent.getParentFile();
         if (parent == null) {
            break;
         }
         if (parent.equals(baseDir)) {
            isUnderBaseDir = true;
            try {
               String path1 = baseDir.getCanonicalPath();
               String path2 = file.getCanonicalPath();
               int index = path2.lastIndexOf(path1);
               if (index != -1) {
                  String dirPath = path1.substring(index);
                  path = path2.substring(dirPath.length() + 1);
               }
            } catch (IOException e) {
               System.err.println(e.getMessage());
            }
         }
      }
      if (!isUnderBaseDir && !strict) {
         String newPath = convertToRelativePath(baseDir, file);
         // if the path is null, we don't have any common root, so we will return the file path
         if (newPath != null) {
            path = newPath;
         }
      }
      path = path.replace('\\', '/');
      return path;
   }

   private static String convertToRelativePath(File baseDir, File file) {
      StringBuilder relPathBuilder = null;
      String absPath = baseDir.getAbsolutePath();
      String relPath = file.getAbsolutePath();

      absPath = absPath.replaceAll("\\\\", "/");
      relPath = relPath.replaceAll("\\\\", "/");
      String[] absoluteDirs = absPath.split("/");
      String[] relativeDirs = relPath.split("/");

      // find the shortest of the two paths, we will only check up to the associated root index
      int length = absoluteDirs.length < relativeDirs.length ? absoluteDirs.length : relativeDirs.length;

      // this is the position of the last common root of the two paths (if we find it of course)
      int lastCommonRoot = -1;
      int index;

      // iterate through the roots to find the last common root of the two paths
      for (index = 0; index < length; index++) {
         if (absoluteDirs[index].equals(relativeDirs[index])) {
            lastCommonRoot = index;
         } else {
            break;
         }
      }
      // if it is -1, there is no common root at all (means that we are in another drive), so in that case we will
      // return null
      if (lastCommonRoot != -1) {
         relPathBuilder = new StringBuilder();
         for (index = lastCommonRoot + 1; index < absoluteDirs.length; index++) {
            if (absoluteDirs[index].length() > 0) {
               relPathBuilder.append("../");
            }
         }
         for (index = lastCommonRoot + 1; index < relativeDirs.length - 1; index++) {
            relPathBuilder.append(relativeDirs[index]).append("/");
         }
         relPathBuilder.append(relativeDirs[relativeDirs.length - 1]);
      }
      return relPathBuilder == null ? null : relPathBuilder.toString();
   }
}
