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
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

/**
 * Several utilities for shape parsing.
 *
 * @version 0.4
 */
public class ParserUtils {
   private static final Pattern ZERO = Pattern.compile("[\\-−+]?0+");

   private ParserUtils() {
   }

   public static Color getColor(String value) {
      try {
         return Color.web(value);
      } catch (IllegalArgumentException ex) {
         return null;
      }
   }

   public static Color getColor(String value, double opacity) {
      try {
         return Color.web(value, opacity);
      } catch (IllegalArgumentException ex) {
         return null;
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

   public static double parseDoubleProtected(String valueS, boolean isWidth, Viewport viewport) {
      valueS = valueS.replace('−', '-');
      Matcher m = ZERO.matcher(valueS);
      if (m.matches()) {
         return 0d;
      } else {
         return LengthParser.parseLength(valueS, isWidth, viewport);
      }
   }

   public static void parseDoubleArgument(List<Double> args, String value) {
      value = value.replace('−', '-');
      Matcher m = ZERO.matcher(value);
      if (m.matches()) {
         args.add(0d);
      } else {
         try {
            double valueD = Double.parseDouble(value);
            args.add(valueD);
         } catch (NumberFormatException e) {
         }
      }
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
      if (!value.equals("none")) {
         if (value.startsWith("url(#")) {
            String id = value.substring(5, value.length() - 1);
            if (filterSpecs.containsKey(id)) {
               FilterSpec spec = filterSpecs.get(id);
               List<FilterSpec.FilterEffect> effects = spec.getEffects();
               for (int i = 0; i < effects.size(); i++) {
                  FilterSpec.FilterEffect filterEffect = effects.get(i);
                  if (filterEffect.getInputType() == FilterSpec.SOURCE_ALPHA_EFFECT) {
                     useSourceAlpha = true;
                  }
                  String resultId = filterEffect.getResultId();
                  lastEffect = filterEffect.getEffect(node, namedEffects);
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
}
