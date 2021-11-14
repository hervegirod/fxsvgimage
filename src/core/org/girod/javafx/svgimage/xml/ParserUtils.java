/*
Copyright (c) 2021, Hervé Girod
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
package org.girod.javafx.svgimage.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import static org.girod.javafx.svgimage.xml.SVGTags.CLASS;
import static org.girod.javafx.svgimage.xml.SVGTags.FILL;
import static org.girod.javafx.svgimage.xml.SVGTags.STROKE;
import static org.girod.javafx.svgimage.xml.SVGTags.STROKE_WIDTH;
import static org.girod.javafx.svgimage.xml.SVGTags.STYLE;

/**
 * Several utilities for shape parsing.
 *
 * @version 0.5.6
 */
public class ParserUtils implements SVGTags {
   private static final Pattern ZERO = Pattern.compile("[\\-−+]?0+");
   private static final Pattern FONT_SIZE_PAT = Pattern.compile("(\\d+\\.?\\d*)([a-z]+)?");
   private static final Pattern URL_PAT = Pattern.compile("url\\('?([^']+)'?\\)");
   private static final Pattern TRANSFORM_PAT = Pattern.compile("\\w+\\((.*)\\)");

   private ParserUtils() {
   }

   public static Color getColor(String value) {
      try {
         return Color.web(value);
      } catch (IllegalArgumentException ex) {
         return null;
      }
   }

   public static String getURL(String value) {
      Matcher m = URL_PAT.matcher(value);
      if (m.matches()) {
         value = m.group(1);
      }
      if (value.startsWith("#")) {
         value = value.substring(1);
      }
      return value;
   }

   public static Paint expressPaint(Map<String, Paint> gradients, String value) {
      Paint paint = null;
      if (!value.equals(NONE)) {
         if (value.startsWith("url(")) {
            String id = getURL(value);
            paint = gradients.get(id);
         } else {
            paint = ParserUtils.getColor(value);
         }
      }

      return paint;
   }

   public static FillRule getClipRule(XMLNode node) {
      if (node.hasAttribute(CLIP_RULE)) {
         String value = node.getAttributeValue(CLIP_RULE);
         if (value.equals(NON_ZERO)) {
            return FillRule.NON_ZERO;
         } else if (value.equals(EVEN_ODD)) {
            return FillRule.EVEN_ODD;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static FillRule getFillRule(XMLNode node) {
      if (node.hasAttribute(FILL_RULE)) {
         String value = node.getAttributeValue(FILL_RULE);
         if (value.equals(NON_ZERO)) {
            return FillRule.NON_ZERO;
         } else if (value.equals(EVEN_ODD)) {
            return FillRule.EVEN_ODD;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static List<Double> getTransformArguments(String transformTxt, Viewport viewport) {
      List<Double> args = new ArrayList<>();
      Matcher m = TRANSFORM_PAT.matcher(transformTxt);
      if (m.matches()) {
         String content = m.group(1);
         StringTokenizer tok = new StringTokenizer(content, ", ");
         while (tok.hasMoreTokens()) {
            String argumentS = tok.nextToken();
            ParserUtils.parseLengthValue(args, argumentS, true, null, viewport);
         }
      } else {
         return null;
      }
      return args;
   }

   public static void setTransform(Node node, XMLNode xmlNode, Viewport viewport) {
      if (xmlNode.hasAttribute(TRANSFORM)) {
         String transforms = xmlNode.getAttributeValue(TRANSFORM);
         List<Transform> transformList = ParserUtils.extractTransforms(transforms, viewport);
         if (!transformList.isEmpty()) {
            ObservableList<Transform> nodeTransforms = node.getTransforms();
            Iterator<Transform> it = transformList.iterator();
            while (it.hasNext()) {
               Transform theTransForm = it.next();
               nodeTransforms.add(theTransForm);
            }
         }
      }
   }

   public static List<Transform> extractTransforms(String transforms, Viewport viewport) {
      List<Transform> transformList = new ArrayList<>();

      StringTokenizer tokenizer = new StringTokenizer(transforms, ")");

      while (tokenizer.hasMoreTokens()) {
         String transformTxt = tokenizer.nextToken() + ")";
         transformTxt = transformTxt.trim();
         if (transformTxt.startsWith("translate(")) {
            List<Double> args = getTransformArguments(transformTxt, viewport);
            if (args.size() == 2) {
               Transform transform = Transform.translate(args.get(0), args.get(1));
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("translateX(")) {
            List<Double> args = getTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.translate(args.get(0), 0);
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("translateY(")) {
            List<Double> args = getTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.translate(0, args.get(0));
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("scale(")) {
            List<Double> args = getTransformArguments(transformTxt, viewport);
            if (args.size() == 2) {
               Transform transform = Transform.scale(args.get(0), args.get(1));
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("scaleX(")) {
            List<Double> args = getTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.translate(args.get(0), 1);
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("scaleY(")) {
            List<Double> args = getTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.translate(1, args.get(0));
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("rotate(")) {
            List<Double> args = getTransformArguments(transformTxt, viewport);
            if (args.size() == 3) {
               Transform transform = Transform.rotate(args.get(0), args.get(1), args.get(2));
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("skewX(")) {
            List<Double> args = getTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.shear(args.get(0), 1);
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("skewY(")) {
            List<Double> args = getTransformArguments(transformTxt, viewport);
            if (args.size() == 1) {
               Transform transform = Transform.shear(1, args.get(0));
               transformList.add(transform);
            }
         } else if (transformTxt.startsWith("matrix(")) {
            List<Double> args = getTransformArguments(transformTxt, viewport);
            if (args.size() == 6) {
               Transform transform = Transform.affine(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5));
               transformList.add(transform);
            }
         }
      }

      return transformList;
   }

   public static List<Double> parseDashArray(String value, Viewport viewport) {
      if (value == null || value.equals(NONE)) {
         return null;
      }
      List<Double> list = new ArrayList<>();
      StringTokenizer tokenizer = new StringTokenizer(value, " ,");
      while (tokenizer.hasMoreTokens()) {
         String dash = tokenizer.nextToken();
         list.add(parseLengthValue(dash, true, null, viewport));
      }
      return list;
   }

   public static Color getColor(String value, double opacity) {
      try {
         return Color.web(value, opacity);
      } catch (IllegalArgumentException ex) {
         return null;
      }
   }

   public static double parseOpacity(String value) {
      boolean isPercent = false;
      if (value.endsWith("%")) {
         value = value.substring(0, value.length() - 1);
         isPercent = true;
      }
      try {
         double opacity = Double.parseDouble(value);
         if (isPercent) {
            opacity = opacity / 100d;
         }
         return opacity;
      } catch (NumberFormatException e) {
         return -1;
      }
   }

   public static void setFillOpacity(Node node, double fillOpacity) {
      if (node instanceof Shape && fillOpacity < 1d) {
         Shape shape = (Shape) node;
         Paint paint = shape.getFill();
         if (paint != null && paint instanceof Color) {
            Color fill = (Color) paint;
            fill = fill.deriveColor(0, 1, 1, fillOpacity);
            shape.setFill(fill);
         }
      }
   }

   public static String parseFirstArgument(String value) {
      StringTokenizer tok = new StringTokenizer(value, " ");
      return tok.nextToken().trim();
   }

   public static double parseDoubleProtected(String valueS) {
      valueS = valueS.replace('−', '-');
      Matcher m = ZERO.matcher(valueS);
      if (m.matches()) {
         return 0d;
      } else {
         try {
            double valueD = Double.parseDouble(valueS);
            return valueD;
         } catch (NumberFormatException e) {
            return 0d;
         }
      }
   }

   public static double parseFontSize(String valueS) {
      Matcher m = FONT_SIZE_PAT.matcher(valueS);
      if (m.matches()) {
         int groupCount = m.groupCount();
         if (groupCount == 1) {
            double size = Double.parseDouble(valueS);
            return size;
         } else {
            String value1 = m.group(1);
            double size = Double.parseDouble(value1);
            String unit = m.group(2);
            if (unit != null && unit.equals("px")) {
               // see https://stackoverflow.com/questions/12788422/svg-coordinate-system-points-vs-pixels
               size = size * 1.25d;
               return size;
            } else {
               return size;
            }
         }
      } else {
         return 12d;
      }
   }

   /**
    * Parse a position value.
    *
    * @param valueS thre value as a string
    * @param isWidth true if the position represents a width
    * @param bounds the optional bounds of the figure for which it is relative to
    * @param viewport the viewport
    * @return the distance
    */
   public static double parsePositionValue(String valueS, boolean isWidth, Bounds bounds, Viewport viewport) {
      valueS = valueS.replace('−', '-');
      Matcher m = ZERO.matcher(valueS);
      if (m.matches()) {
         return 0d;
      } else {
         return LengthParser.parsePosition(valueS, isWidth, bounds, viewport);
      }
   }

   /**
    * Parse a length value.
    *
    * @param valueS thre value as a string
    * @param isWidth true if the distance represents a width
    * @param bounds the optional bounds of the figure for which it is relative to
    * @param viewport the viewport
    * @return the distance
    */
   public static double parseLengthValue(String valueS, boolean isWidth, Bounds bounds, Viewport viewport) {
      valueS = valueS.replace('−', '-');
      Matcher m = ZERO.matcher(valueS);
      if (m.matches()) {
         return 0d;
      } else {
         return LengthParser.parseLength(valueS, isWidth, bounds, viewport);
      }
   }

   public static void parseLengthValue(List<Double> args, String value, boolean isWidth, Bounds bounds, Viewport viewport) {
      value = value.replace('−', '-');
      double d = LengthParser.parseLength(value, isWidth, bounds, viewport);
      args.add(d);
   }

   /**
    * Apply the appropriate filter on a node.
    *
    * @param filterSpecs the filters specifications
    * @param node the node
    * @param value the filter value
    * @return the effect to apply
    */
   public static Effect expressFilter(Map<String, FilterSpec> filterSpecs, Node node, String value) {
      Map<String, Effect> namedEffects = new HashMap<>();
      List<FilterSpec.AppliedEffect> appliedEffects = new ArrayList<>();
      Effect lastEffect = null;
      boolean useSourceAlpha = false;
      if (!value.equals(NONE)) {
         if (value.startsWith("url(")) {
            String id = ParserUtils.getURL(value);
            if (filterSpecs.containsKey(id)) {
               FilterSpec spec = filterSpecs.get(id);
               List<FilterSpec.FilterEffect> effects = spec.getEffects();
               for (int i = 0; i < effects.size(); i++) {
                  FilterSpec.FilterEffect filterEffect = effects.get(i);
                  if (filterEffect instanceof FilterSpec.FEComposite) {
                     FilterSpec.FEComposite feComposite = (FilterSpec.FEComposite) filterEffect;
                     boolean toApply = feComposite.shouldApply(appliedEffects, i);
                     if (!toApply) {
                        continue;
                     }
                  }
                  if (filterEffect.getInputType() == FilterSpec.SOURCE_ALPHA_EFFECT) {
                     useSourceAlpha = true;
                  }
                  String resultId = filterEffect.getResultId();
                  lastEffect = filterEffect.getEffect(node);
                  appliedEffects.add(new FilterSpec.AppliedEffect(filterEffect, lastEffect));
                  if (resultId != null && lastEffect != null) {
                     namedEffects.put(resultId, lastEffect);
                  }
               }
            }
         }
         /* If at least one of the filters use the source alpha, we create one
          * ColorAdjust effect to create this source alpha equivalent
          */
         ColorAdjust sourceAlpha = null;
         if (useSourceAlpha) {
            sourceAlpha = new ColorAdjust();
            // this works with the brightness. Interestingly, it does not work if we try to use the hue or the saturation
            sourceAlpha.setBrightness(-1d);
         }
         Effect previousEffect = null;
         Iterator<FilterSpec.AppliedEffect> it = appliedEffects.iterator();
         while (it.hasNext()) {
            FilterSpec.AppliedEffect appliedEffect = it.next();
            FilterSpec.FilterEffect spec = appliedEffect.getEffectSpec();
            Effect effect = appliedEffect.getEffect();
            spec.resolveEffect(effect, sourceAlpha, previousEffect, namedEffects);
            previousEffect = effect;
         }
      }

      return lastEffect;
   }

   public static void setBaselineShift(Text text, String value) {
      // http://www.svgbasics.com/font_effects_italic.html
      // https://stackoverflow.com/questions/50295199/javafx-subscript-and-superscript-text-in-textflow
      if (value.equals(BASELINE_SUB)) {
         text.setTranslateY(text.getFont().getSize() * 0.3);
      } else if (value.equals(BASELINE_SUPER)) {
         text.setTranslateY(text.getFont().getSize() * -0.3);
      } else {
         boolean isPercent = false;
         if (value.endsWith("%")) {
            isPercent = true;
            value = value.substring(0, value.length());
         }
         try {
            double shift = -Double.parseDouble(value);
            if (isPercent) {
               shift = shift * 100d;
            }
            text.setTranslateY(text.getFont().getSize() * shift);
         } catch (NumberFormatException e) {
         }
      }
   }

   public static boolean hasXPosition(XMLNode node) {
      return node.hasAttribute(X) || node.hasAttribute(DX);
   }

   public static Map<String, String> getStyles(XMLNode node) {
      Map<String, String> styles = new HashMap<>();
      if (node.hasAttribute(STYLE)) {
         String styleValue = node.getAttributeValue(STYLE);
         StringTokenizer tok = new StringTokenizer(styleValue, ";");
         while (tok.hasMoreTokens()) {
            String tk = tok.nextToken().trim();
            if (tk.isEmpty()) {
               continue;
            }
            int index = tk.indexOf(':');
            if (index != -1) {
               String key = tk.substring(0, index);
               if (index < tk.length() - 1) {
                  String value = tk.substring(index + 1);
                  styles.put(key, value);
               }
            }
         }
      }
      return styles;
   }

   public static String mergeStyles(Map<String, String> styles, XMLNode node) {
      styles = new HashMap<>(styles);
      if (node.hasAttribute(STYLE)) {
         String styleValue = node.getAttributeValue(STYLE);
         StringTokenizer tok = new StringTokenizer(styleValue, ";");
         while (tok.hasMoreTokens()) {
            String tk = tok.nextToken().trim();
            if (tk.isEmpty()) {
               continue;
            }
            int index = tk.indexOf(':');
            if (index != -1) {
               String key = tk.substring(0, index);
               if (index < tk.length() - 1) {
                  String value = tk.substring(index + 1);
                  styles.put(key, value);
               }
            }
         }
      }
      StringBuilder buf = new StringBuilder();
      Iterator<Entry<String, String>> it = styles.entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, String> entry = it.next();
         buf.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
      }
      return buf.toString();
   }

   public static void propagateStyleAttributes(XMLNode parentNode, XMLNode childNode) {
      if (childNode.getName().equals(TSPAN)) {
         return;
      }
      Iterator<Map.Entry<String, String>> it = parentNode.attributes.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<String, String> entry = it.next();
         switch (entry.getKey()) {
            case STYLE:
            case STROKE:
            case FILL:
            case STROKE_WIDTH:
            case CLASS:
               if (!childNode.hasAttribute(entry.getKey())) {
                  childNode.addAttribute(entry.getKey(), entry.getValue());
               }
         }
      }
   }

   public static void setOpacity(Node node, XMLNode xmlNode) {
      if (xmlNode.hasAttribute(OPACITY)) {
         String opacityS = xmlNode.getAttributeValue(OPACITY);
         double opacity = ParserUtils.parseOpacity(opacityS);
         if (opacity >= 0) {
            node.setOpacity(opacity);
         }
      }
      if (xmlNode.hasAttribute(FILL_OPACITY) && node instanceof Shape) {
         String fillOpacityS = xmlNode.getAttributeValue(FILL_OPACITY);
         double fillOpacity = ParserUtils.parseOpacity(fillOpacityS);
         if (fillOpacity >= 0) {
            ParserUtils.setFillOpacity(node, fillOpacity);
         }
      }
   }

   public static Viewport parseViewport(XMLNode xmlNode) {
      double width = 0;
      double height = 0;
      if (xmlNode.hasAttribute(WIDTH) && xmlNode.hasAttribute(HEIGHT)) {
         width = xmlNode.getLengthValue(WIDTH, 0);
         height = xmlNode.getLengthValue(HEIGHT, 0);
      } else if (xmlNode.hasAttribute(VIEWBOX)) {
         String box = xmlNode.getAttributeValue(VIEWBOX);
         StringTokenizer tok = new StringTokenizer(box, " ,");
         if (tok.countTokens() >= 4) {
            tok.nextToken();
            tok.nextToken();
            width = ParserUtils.parseDoubleProtected(tok.nextToken());
            height = ParserUtils.parseDoubleProtected(tok.nextToken());
         }
      }
      Viewport theViewport = new Viewport(width, height);
      return theViewport;
   }

   public static boolean getPreserveAspectRatio(String value) {
      if (!value.contains(" ")) {
         return !value.equals(NONE);
      } else {
         StringTokenizer tok = new StringTokenizer(value, " ");
         return !tok.nextToken().equals(NONE);
      }
   }

   public static Viewbox parseViewbox(XMLNode xmlNode) {
      if (xmlNode.hasAttribute(WIDTH) && xmlNode.hasAttribute(HEIGHT)) {
         double width = xmlNode.getLengthValue(WIDTH, 0);
         double height = xmlNode.getLengthValue(HEIGHT, 0);
         if (xmlNode.hasAttribute(VIEWBOX)) {
            String box = xmlNode.getAttributeValue(VIEWBOX);
            StringTokenizer tok = new StringTokenizer(box, " ,");
            if (tok.countTokens() >= 4) {
               tok.nextToken();
               tok.nextToken();
               double viewboxWidth = ParserUtils.parseDoubleProtected(tok.nextToken());
               double viewboxHeight = ParserUtils.parseDoubleProtected(tok.nextToken());
               Viewbox theViewbox = new Viewbox(width, height);
               theViewbox.setViewbox(viewboxWidth, viewboxHeight);
               return theViewbox;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }

   }
}
