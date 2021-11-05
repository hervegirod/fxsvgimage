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
import java.util.List;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;

/**
 * Contains the specification for a filter.
 *
 * @since 0.4
 */
public class FilterSpec {
   private final List<FilterEffect> effects = new ArrayList<>();

   public FilterSpec() {
   }

   public void addEffect(FilterEffect effect) {
      effects.add(effect);
   }

   public List<FilterEffect> getEffects() {
      return effects;
   }

   public interface FilterEffect {
      /**
       * Return the associated JavaFX effect.
       *
       * @param previous the previous effect on the chain
       * @return the effect
       */
      public Effect getEffect(Effect previous);
   }

   public static class FEDropShadow implements FilterEffect {
      public final double dx;
      public final double dy;
      public final double stdDeviation;
      public final Color floodColor;

      public FEDropShadow(double dx, double dy, double stdDeviation, Color floodColor) {
         this.dx = dx;
         this.dy = dy;
         this.stdDeviation = stdDeviation;
         this.floodColor = floodColor;
      }

      @Override
      public Effect getEffect(Effect previous) {
         DropShadow dropShadow = new DropShadow(stdDeviation, dx, dy, floodColor);
         if (previous != null) {
            dropShadow.setInput(previous);
         }
         return dropShadow;
      }
   }

   public static class FEGaussianBlur implements FilterEffect {
      public final double stdDeviation;

      public FEGaussianBlur(double stdDeviation) {
         this.stdDeviation = stdDeviation;
      }

      @Override
      public Effect getEffect(Effect previous) {
         GaussianBlur gaussianBlur = new GaussianBlur(stdDeviation);
         if (previous != null) {
            gaussianBlur.setInput(previous);
         }         
         return gaussianBlur;
      }
   }
}
