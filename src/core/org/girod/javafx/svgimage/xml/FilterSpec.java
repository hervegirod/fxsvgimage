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
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.ImageInput;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Contains the specification for a filter.
 *
 * @since 0.4
 */
public class FilterSpec implements SVGTags {
   public static final short PREVIOUS_EFFECT = 0;
   public static final short NAMED_EFFECT = 1;
   public static final short SOURCE_GRAPHIC_EFFECT = 2;
   public static final short SOURCE_ALPHA_EFFECT = 3;
   private final List<FilterEffect> effects = new ArrayList<>();
   private final Map<String, FilterEffect> namedEffects = new HashMap<>();

   public FilterSpec() {
   }

   public void addEffect(String resultId, FilterEffect effect) {
      effects.add(effect);
      if (resultId != null) {
         namedEffects.put(resultId, effect);
      }
   }

   public List<FilterEffect> getEffects() {
      return effects;
   }

   public interface FilterEffect {
      /**
       * Return the filter result Id.
       *
       * @return the result Id
       */
      public String getResultId();

      public short getInputType();

      public void setIn(String in);

      /**
       * Return the associated JavaFX effect.
       *
       * @param node the node on which the effect applies
       * @param namedEffects the named effects for the filter effect
       * @return the effect
       */
      public Effect getEffect(Node node, Map<String, Effect> namedEffects);

      public default void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
      }
   }

   public static abstract class AbstractFilterEffect implements FilterEffect {
      protected final String resultId;
      protected String in = null;
      protected short inputType = PREVIOUS_EFFECT;

      public AbstractFilterEffect(String resultId) {
         this.resultId = resultId;
      }

      @Override
      public short getInputType() {
         return inputType;
      }

      @Override
      public void setIn(String in) {
         this.in = in;
         if (in != null) {
            if (in.equals(SOURCE_GRAPHIC)) {
               inputType = SOURCE_GRAPHIC_EFFECT;
            } else if (in.equals(SOURCE_ALPHA)) {
               inputType = SOURCE_ALPHA_EFFECT;
            } else {
               inputType = NAMED_EFFECT;
            }

         }
      }

      @Override
      public String getResultId() {
         return resultId;
      }
   }

   public static class FEMerge extends AbstractFilterEffect {
      private List<String> mergeNodes = new ArrayList<>();

      public FEMerge(String resultId) {
         super(resultId);
      }

      public void addMergeNode(String in) {
         mergeNodes.add(in);
      }

      @Override
      public Effect getEffect(Node node, Map<String, Effect> namedEffects) {
         return new Blend(BlendMode.ADD);
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         Blend blendEffect = (Blend) effect;
         Blend mergedEffect = null;
         for (int i = 0; i < mergeNodes.size(); i++) {
            String _resultId = mergeNodes.get(i);
            if (_resultId != null && (namedEffects.containsKey(_resultId) || _resultId.equals(SOURCE_GRAPHIC))) {
               Effect _effect = namedEffects.get(_resultId);
               if (mergedEffect == null) {
                  mergedEffect = new Blend(BlendMode.ADD);
                  mergedEffect.setTopInput(_effect);
               } else if (mergedEffect.getBottomInput() == null) {
                  mergedEffect.setBottomInput(_effect);
               } else {
                  Blend newMergedEffect = new Blend(BlendMode.ADD);
                  newMergedEffect.setTopInput(mergedEffect);
                  mergedEffect = newMergedEffect;
               }
            } else if (_resultId != null && _resultId.equals(SOURCE_ALPHA)) {
               if (mergedEffect == null) {
                  mergedEffect = new Blend(BlendMode.ADD);
                  mergedEffect.setTopInput(sourceAlpha);
               } else if (mergedEffect.getBottomInput() == null) {
                  mergedEffect.setBottomInput(sourceAlpha);
               } else {
                  Blend newMergedEffect = new Blend(BlendMode.ADD);
                  newMergedEffect.setTopInput(sourceAlpha);
                  mergedEffect = newMergedEffect;
               }
            }
         }
         if (mergedEffect != null) {
            Effect topEffect = mergedEffect.getTopInput();
            blendEffect.setTopInput(topEffect);
            Effect bottomEffect = mergedEffect.getBottomInput();
            blendEffect.setBottomInput(bottomEffect);
         }
      }
   }

   public static class FEDropShadow extends AbstractFilterEffect {
      public final double dx;
      public final double dy;
      public final double stdDeviation;
      public final Color floodColor;

      public FEDropShadow(String resultId, double dx, double dy, double stdDeviation, Color floodColor) {
         super(resultId);
         this.dx = dx;
         this.dy = dy;
         this.stdDeviation = stdDeviation;
         this.floodColor = floodColor;
      }

      @Override
      public Effect getEffect(Node node, Map<String, Effect> namedEffects) {
         DropShadow dropShadow = new DropShadow(stdDeviation, dx, dy, floodColor);
         return dropShadow;
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         if (inputType == NAMED_EFFECT && namedEffects.containsKey(in)) {
            Effect _effect = namedEffects.get(in);
            ((DropShadow) effect).setInput(_effect);
         } else if (inputType == PREVIOUS_EFFECT) {
            ((DropShadow) effect).setInput(previousEffect);
         } else if (inputType == SOURCE_ALPHA_EFFECT) {
            ((GaussianBlur) effect).setInput(sourceAlpha);
         }
      }
   }

   public static class FEGaussianBlur extends AbstractFilterEffect {
      public final double stdDeviation;

      public FEGaussianBlur(String resultId, double stdDeviation) {
         super(resultId);
         // don't know why, but it's better to multiply the stdDeviation by 2 to get the radius
         this.stdDeviation = stdDeviation * 2;
      }

      @Override
      public Effect getEffect(Node node, Map<String, Effect> namedEffects) {
         GaussianBlur gaussianBlur = new GaussianBlur(stdDeviation);
         return gaussianBlur;
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         if (inputType == NAMED_EFFECT && namedEffects.containsKey(in)) {
            Effect _effect = namedEffects.get(in);
            ((GaussianBlur) effect).setInput(_effect);
         } else if (inputType == PREVIOUS_EFFECT) {
            ((GaussianBlur) effect).setInput(previousEffect);
         } else if (inputType == SOURCE_ALPHA_EFFECT) {
            ((GaussianBlur) effect).setInput(sourceAlpha);
         }
      }
   }

   public static class FEFlood extends AbstractFilterEffect {
      public final double x;
      public final double y;
      public final double width;
      public final double height;
      public final Color color;

      public FEFlood(String resultId, double x, double y, double width, double height, Color color) {
         super(resultId);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.color = color;
      }

      @Override
      public Effect getEffect(Node node, Map<String, Effect> namedEffects) {
         ColorInput colInput = new ColorInput(x, y, width, height, color);
         return colInput;
      }
   }

   public static class FEImage extends AbstractFilterEffect {
      public final double x;
      public final double y;
      public final Image source;

      public FEImage(String resultId, double x, double y, Image source) {
         super(resultId);
         this.x = x;
         this.y = y;
         this.source = source;
      }

      @Override
      public Effect getEffect(Node node, Map<String, Effect> namedEffects) {
         ImageInput imageInput = new ImageInput(source, x, y);
         return imageInput;
      }
   }

   public static class FEOffset extends AbstractFilterEffect {
      public final double dx;
      public final double dy;

      public FEOffset(String resultId, double dx, double dy) {
         super(resultId);
         this.dx = dx;
         this.dy = dy;
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         if (inputType == NAMED_EFFECT && namedEffects.containsKey(in)) {
            Effect _effect = namedEffects.get(in);
            ((PerspectiveTransform) effect).setInput(_effect);
         } else if (inputType == PREVIOUS_EFFECT) {
            ((PerspectiveTransform) effect).setInput(previousEffect);
         } else if (inputType == SOURCE_ALPHA_EFFECT) {
            ((PerspectiveTransform) effect).setInput(sourceAlpha);
         }
      }

      @Override
      public Effect getEffect(Node node, Map<String, Effect> namedEffects) {
         double ulx = dx + node.getLayoutX();
         double uly = dy + node.getLayoutY();
         double urx = ulx + node.getBoundsInLocal().getWidth();
         double ury = uly;
         double llx = ulx;
         double lly = uly + node.getBoundsInLocal().getHeight();
         double lrx = ulx + node.getBoundsInLocal().getWidth();
         double lry = lly;
         PerspectiveTransform transform = new PerspectiveTransform(ulx, uly, urx, ury, lrx, lry, llx, lly);
         return transform;
      }
   }

   public static class FESpecularLighting extends AbstractFilterEffect {
      public final double specularConstant;
      public final double specularExponent;
      public final double surfaceScale;
      public final Light light;

      public FESpecularLighting(String resultId, double specularConstant, double specularExponent, double surfaceScale, Light light) {
         super(resultId);
         this.specularConstant = specularConstant;
         this.specularExponent = specularExponent;
         this.surfaceScale = surfaceScale;
         this.light = light;
      }

      @Override
      public Effect getEffect(Node node, Map<String, Effect> namedEffects) {
         Lighting lighting = new Lighting(light);
         lighting.setSpecularConstant(specularConstant);
         lighting.setSpecularExponent(specularExponent);
         lighting.setSurfaceScale(surfaceScale);
         return lighting;
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         if (inputType == NAMED_EFFECT && namedEffects.containsKey(in)) {
            Effect _effect = namedEffects.get(in);
            ((GaussianBlur) effect).setInput(_effect);
         } else if (inputType == PREVIOUS_EFFECT) {
            ((GaussianBlur) effect).setInput(previousEffect);
         } else if (inputType == SOURCE_ALPHA_EFFECT) {
            ((GaussianBlur) effect).setInput(sourceAlpha);
         }
      }
   }

   public static class AppliedEffect {
      private FilterEffect effectSpec;
      private Effect effect;

      public AppliedEffect(FilterEffect effectSpec, Effect effect) {
         this.effectSpec = effectSpec;
         this.effect = effect;
      }

      public FilterEffect getEffectSpec() {
         return effectSpec;
      }

      public Effect getEffect() {
         return effect;
      }
   }
}
