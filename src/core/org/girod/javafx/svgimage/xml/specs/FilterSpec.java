/*
Copyright (c) 2021, 2022 Hervé Girod
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
package org.girod.javafx.svgimage.xml.specs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
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
import org.girod.javafx.svgimage.xml.parsers.SVGTags;

/**
 * Contains the specification for a filter.
 *
 * @version 1.0
 */
public class FilterSpec implements SVGTags {
   /**
    * Input type indicating the previous effect output.
    */
   public static final short PREVIOUS_EFFECT = 0;
   /**
    * Input type indicating a named effect reference.
    */
   public static final short NAMED_EFFECT = 1;
   /**
    * Input type indicating the source graphic.
    */
   public static final short SOURCE_GRAPHIC_EFFECT = 2;
   /**
    * Input type indicating the source alpha.
    */
   public static final short SOURCE_ALPHA_EFFECT = 3;
   private final List<FilterEffect> effects = new ArrayList<>();
   private final Map<String, FilterEffect> namedEffects = new HashMap<>();

   /**
    * Create an empty filter specification.
    */
   public FilterSpec() {
   }

   /**
    * Add a filter effect and optionally register it by result id.
    *
    * @param resultId the result id, or null if not referenced
    * @param effect the effect to add
    */
   public void addEffect(String resultId, FilterEffect effect) {
      effects.add(effect);
      if (resultId != null) {
         namedEffects.put(resultId, effect);
      }
   }

   /**
    * Return the list of filter effects in order.
    *
    * @return the effects
    */
   public List<FilterEffect> getEffects() {
      return effects;
   }

   /**
    * Defines a filter effect specification that can produce a JavaFX effect.
    */
   public interface FilterEffect {
      /**
       * Return the filter result Id.
       *
       * @return the result Id
       */
      public String getResultId();

      /**
       * Return the input type for this effect.
       *
       * @return the input type
       */
      public short getInputType();

      /**
       * Set the input reference for the effect.
       *
       * @param in the input id or keyword
       */
      public void setIn(String in);

      /**
       * Return the associated JavaFX effect.
       *
       * @param node the node on which the effect applies
       * @return the effect
       */
      public Effect getEffect(Node node);

      /**
       * Resolve the effect inputs once all effects are created.
       *
       * @param effect the JavaFX effect instance
       * @param sourceAlpha the source alpha effect
       * @param previousEffect the previous effect in the chain
       * @param namedEffects the map of named effects by id
       */
      public default void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
      }
   }

   /**
    * Base implementation for filter effects with common input handling.
    */
   public static abstract class AbstractFilterEffect implements FilterEffect {
      /**
       * Effect result id.
       */
      protected final String resultId;
      /**
       * Input reference for the effect.
       */
      protected String in = null;
      /**
       * Input type for the effect.
       */
      protected short inputType = PREVIOUS_EFFECT;

      /**
       * Create a filter effect with the given result id.
       *
       * @param resultId the result id
       */
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

   /**
    * Filter effect for merging multiple inputs.
    */
   public static class FEMerge extends AbstractFilterEffect {
      private final List<String> mergeNodes = new ArrayList<>();

      /**
       * Create a merge effect.
       *
       * @param resultId the result id
       */
      public FEMerge(String resultId) {
         super(resultId);
      }

      /**
       * Add a merge node input reference.
       *
       * @param in the input reference
       */
      public void addMergeNode(String in) {
         mergeNodes.add(in);
      }

      @Override
      public Effect getEffect(Node node) {
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

   /**
    * Filter effect for drop shadows.
    */
   public static class FEDropShadow extends AbstractFilterEffect {
      /**
       * Horizontal offset.
       */
      public final double dx;
      /**
       * Vertical offset.
       */
      public final double dy;
      /**
       * Standard deviation for the blur radius.
       */
      public final double stdDeviation;
      /**
       * Shadow color.
       */
      public final Color floodColor;

      /**
       * Create a drop shadow effect.
       *
       * @param resultId the result id
       * @param dx the horizontal offset
       * @param dy the vertical offset
       * @param stdDeviation the blur standard deviation
       * @param floodColor the shadow color
       */
      public FEDropShadow(String resultId, double dx, double dy, double stdDeviation, Color floodColor) {
         super(resultId);
         this.dx = dx;
         this.dy = dy;
         this.stdDeviation = stdDeviation;
         this.floodColor = floodColor;
      }

      @Override
      public Effect getEffect(Node node) {
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

   /**
    * Filter effect for Gaussian blur.
    */
   public static class FEGaussianBlur extends AbstractFilterEffect {
      /**
       * Standard deviation for the blur radius.
       */
      public final double stdDeviation;

      /**
       * Create a Gaussian blur effect.
       *
       * @param resultId the result id
       * @param stdDeviation the blur standard deviation
       */
      public FEGaussianBlur(String resultId, double stdDeviation) {
         super(resultId);
         // don't know why, but it's better to multiply the stdDeviation by 2 to get the radius
         this.stdDeviation = stdDeviation * 2;
      }

      @Override
      public Effect getEffect(Node node) {
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

   /**
    * Filter effect for a solid color fill.
    */
   public static class FEFlood extends AbstractFilterEffect {
      /**
       * X coordinate of the flood region.
       */
      public final double x;
      /**
       * Y coordinate of the flood region.
       */
      public final double y;
      /**
       * Width of the flood region.
       */
      public final double width;
      /**
       * Height of the flood region.
       */
      public final double height;
      /**
       * Flood color.
       */
      public final Color color;

      /**
       * Create a flood effect.
       *
       * @param resultId the result id
       * @param x the x coordinate
       * @param y the y coordinate
       * @param width the width
       * @param height the height
       * @param color the flood color
       */
      public FEFlood(String resultId, double x, double y, double width, double height, Color color) {
         super(resultId);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.color = color;
      }

      @Override
      public Effect getEffect(Node node) {
         ColorInput colInput = new ColorInput(x, y, width, height, color);
         return colInput;
      }
   }

   /**
    * Filter effect for an image input.
    */
   public static class FEImage extends AbstractFilterEffect {
      /**
       * X coordinate of the image.
       */
      public final double x;
      /**
       * Y coordinate of the image.
       */
      public final double y;
      /**
       * Image source.
       */
      public final Image source;

      /**
       * Create an image input effect.
       *
       * @param resultId the result id
       * @param x the x coordinate
       * @param y the y coordinate
       * @param source the source image
       */
      public FEImage(String resultId, double x, double y, Image source) {
         super(resultId);
         this.x = x;
         this.y = y;
         this.source = source;
      }

      @Override
      public Effect getEffect(Node node) {
         ImageInput imageInput = new ImageInput(source, x, y);
         return imageInput;
      }
   }

   /**
    * Filter effect for offsetting content.
    */
   public static class FEOffset extends AbstractFilterEffect {
      /**
       * Horizontal offset.
       */
      public final double dx;
      /**
       * Vertical offset.
       */
      public final double dy;

      /**
       * Create an offset effect.
       *
       * @param resultId the result id
       * @param dx the horizontal offset
       * @param dy the vertical offset
       */
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
      public Effect getEffect(Node node) {
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

   /**
    * Filter effect for specular lighting.
    */
   public static class FESpecularLighting extends AbstractFilterEffect {
      /**
       * Specular constant.
       */
      public final double specularConstant;
      /**
       * Specular exponent.
       */
      public final double specularExponent;
      /**
       * Surface scale.
       */
      public final double surfaceScale;
      /**
       * Light source.
       */
      public final Light light;

      /**
       * Create a specular lighting effect.
       *
       * @param resultId the result id
       * @param specularConstant the specular constant
       * @param specularExponent the specular exponent
       * @param surfaceScale the surface scale
       * @param light the light source
       */
      public FESpecularLighting(String resultId, double specularConstant, double specularExponent, double surfaceScale, Light light) {
         super(resultId);
         this.specularConstant = specularConstant;
         this.specularExponent = specularExponent;
         this.surfaceScale = surfaceScale;
         this.light = light;
      }

      @Override
      public Effect getEffect(Node node) {
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
            ((Lighting) effect).setContentInput(_effect);
         } else if (inputType == PREVIOUS_EFFECT) {
            ((Lighting) effect).setContentInput(previousEffect);
         } else if (inputType == SOURCE_ALPHA_EFFECT) {
            ((Lighting) effect).setContentInput(sourceAlpha);
         }
      }
   }

   /**
    * Filter effect for diffuse lighting.
    */
   public static class FEDiffuseLighting extends AbstractFilterEffect {
      /**
       * Diffuse constant.
       */
      public final double diffuseConstant;
      /**
       * Light source.
       */
      public final Light light;

      /**
       * Create a diffuse lighting effect.
       *
       * @param resultId the result id
       * @param diffuseConstant the diffuse constant
       * @param light the light source
       */
      public FEDiffuseLighting(String resultId, double diffuseConstant, Light light) {
         super(resultId);
         this.diffuseConstant = diffuseConstant;
         this.light = light;
      }

      @Override
      public Effect getEffect(Node node) {
         Lighting lighting = new Lighting(light);
         lighting.setDiffuseConstant(diffuseConstant);
         lighting.setSpecularConstant(0);
         lighting.setSpecularExponent(0);
         lighting.setSurfaceScale(0);
         return lighting;
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         if (inputType == NAMED_EFFECT && namedEffects.containsKey(in)) {
            Effect _effect = namedEffects.get(in);
            ((Lighting) effect).setContentInput(_effect);
         } else if (inputType == PREVIOUS_EFFECT) {
            ((Lighting) effect).setContentInput(previousEffect);
         } else if (inputType == SOURCE_ALPHA_EFFECT) {
            ((Lighting) effect).setContentInput(sourceAlpha);
         }
      }
   }

   /**
    * Stores an applied effect together with its specification.
    */
   public static class AppliedEffect {
      private final FilterEffect effectSpec;
      private final Effect effect;

      /**
       * Create an applied effect wrapper.
       *
       * @param effectSpec the effect specification
       * @param effect the JavaFX effect
       */
      public AppliedEffect(FilterEffect effectSpec, Effect effect) {
         this.effectSpec = effectSpec;
         this.effect = effect;
      }

      /**
       * Return the effect specification.
       *
       * @return the effect specification
       */
      public FilterEffect getEffectSpec() {
         return effectSpec;
      }

      /**
       * Return the result id for the effect specification.
       *
       * @return the result id
       */
      public String getResultId() {
         return effectSpec.getResultId();
      }

      /**
       * Return the JavaFX effect instance.
       *
       * @return the effect
       */
      public Effect getEffect() {
         return effect;
      }
   }

   /**
    * Filter effect for compositing two inputs.
    */
   public static class FEComposite extends AbstractFilterEffect {
      /**
       * Composite operator: over.
       */
      public static final short OPERATOR_OVER = 0;
      /**
       * Composite operator: in.
       */
      public static final short OPERATOR_IN = 1;
      /**
       * Composite operator: out.
       */
      public static final short OPERATOR_OUT = 2;
      /**
       * Composite operator: atop.
       */
      public static final short OPERATOR_ATOP = 3;
      /**
       * Composite operator: xor.
       */
      public static final short OPERATOR_XOR = 4;
      /**
       * Composite operator: arithmetic.
       */
      public static final short OPERATOR_ARITHMETIC = 5;
      private short type = OPERATOR_OVER;
      private final String compIn;
      private final String compIn2;

      /**
       * Create a composite effect.
       *
       * @param resultId the result id
       * @param type the composite operator
       * @param in the first input reference
       * @param in2 the second input reference
       */
      public FEComposite(String resultId, short type, String in, String in2) {
         super(resultId);
         this.type = type;
         this.compIn = in;
         this.compIn2 = in2;
      }

      /**
       * Return true if the composite should apply at the given index.
       *
       * @param appliedEffects the applied effects list
       * @param index the current index
       * @return true if the composite should apply
       */
      public boolean shouldApply(List<FilterSpec.AppliedEffect> appliedEffects, int index) {
         boolean compInIsSourceGraphics = compIn != null && compIn.equals(SOURCE_GRAPHIC);
         if (!compInIsSourceGraphics) {
            return true;
         }
         if (compIn2 == null || index == 0) {
            return false;
         }
         FilterSpec.AppliedEffect previousEffect = appliedEffects.get(index - 1);
         return !compIn2.equals(previousEffect.getResultId());
      }

      /**
       * Return true if the composite takes the source graphic as the first input.
       *
       * @return true if the first input is the source graphic
       */
      public boolean isSecondLast() {
         return compIn != null && compIn.equals(SOURCE_GRAPHIC);
      }

      @Override
      public Effect getEffect(Node node) {
         switch (type) {
            case OPERATOR_OVER:
               return new Blend(BlendMode.SRC_OVER);
            case OPERATOR_ATOP:
               return new Blend(BlendMode.SRC_ATOP);
            case OPERATOR_IN:
               return new Blend(BlendMode.OVERLAY);
            case OPERATOR_OUT:
               return new Blend(BlendMode.ADD);
            case OPERATOR_XOR:
               return new Blend(BlendMode.EXCLUSION);
            case OPERATOR_ARITHMETIC:
               return new Blend(BlendMode.SRC_OVER);
            default:
               return new Blend(BlendMode.SRC_OVER);
         }
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         Blend blend = (Blend) effect;
         if (compIn != null) {
            if (namedEffects.containsKey(compIn)) {
               blend.setTopInput(namedEffects.get(compIn));
            } else if (compIn.equals(SOURCE_ALPHA)) {
               blend.setTopInput(sourceAlpha);
            } else if (compIn.equals(SOURCE_GRAPHIC)) {
               blend.setTopInput(null);
            } else {
               blend.setTopInput(previousEffect);
            }
         } else {
            blend.setTopInput(previousEffect);
         }
         if (compIn2 != null) {
            if (namedEffects.containsKey(compIn2)) {
               blend.setBottomInput(namedEffects.get(compIn2));
            } else if (compIn2.equals(SOURCE_ALPHA)) {
               blend.setBottomInput(sourceAlpha);
            } else if (compIn2.equals(SOURCE_GRAPHIC)) {
               blend.setBottomInput(null);
            } else {
               blend.setBottomInput(previousEffect);
            }
         } else {
            blend.setBottomInput(previousEffect);
         }
      }
   }
}
