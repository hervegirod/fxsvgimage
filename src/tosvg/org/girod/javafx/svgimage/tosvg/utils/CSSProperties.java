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

/**
 * Hold the name of several useful CSS properties.
 *
 * @since 1.0
 */
public interface CSSProperties {
   /**
    * the {@value} CSS property.
    */
   public String VISIBILITY = "-visibility";// all Nodes
   /**
    * the {@value} CSS property.
    */
   public String OPACITY = "-fx-opacity";
   /**
    * the {@value} CSS property.
    */
   public String TRANSLATE_X = "-fx-translate-x";
   /**
    * the {@value} CSS property.
    */
   public String TRANSLATE_Y = "-fx-translate-y";
   /**
    * the {@value} CSS property.
    */
   public String SCALE_X = "-fx-scale-x";
   /**
    * the {@value} CSS property.
    */
   public String SCALE_Y = "-fx-scale-y";
   // Regions
   /**
    * the {@value} CSS property.
    */
   public String REGION_BACKGROUND = "-fx-region-background";
   /**
    * the {@value} CSS property.
    */
   public String REGION_BORDER = "-fx-region-border";
   /**
    * the {@value} CSS property.
    */
   public String BACKGROUND_COLOR = "-fx-background-color";
   /**
    * the {@value} CSS property.
    */
   public String BACKGROUND_IMAGE = "-fx-background-image";
   /**
    * the {@value} CSS property.
    */
   public String BACKGROUND_RADIUS = "-fx-background-radius";
   /**
    * the {@value} CSS property.
    */
   public String REGION_BACKGROUND_SIZE = "-fx-background-size";
   /**
    * the {@value} CSS property.
    */
   public String REGION_BACKGROUND_RADIUS = "-fx-background-radius";
   /**
    * the {@value} CSS property.
    */
   public String REGION_BACKGROUND_POSITION = "-fx-background-position";
   /**
    * the {@value} CSS property.
    */
   public String BORDER_COLOR = "-fx-border-color";
   /**
    * the {@value} CSS property.
    */
   public String BORDER_RADIUS = "-fx-border-radius";
   /**
    * the {@value} CSS property.
    */
   public String SHAPE = "-fx-shape";
   /**
    * the {@value} CSS property.
    */
   public String SCALE_SHAPE = "-fx-scale-shape";
   // Shapes
   /**
    * the {@value} CSS property.
    */
   public String ARC_WIDTH = "-fx-arc-width";
   /**
    * the {@value} CSS property.
    */
   public String ARC_HEIGHT = "-fx-arc-height";
   /**
    * the {@value} CSS property.
    */
   public String IMAGE = "-fx-image";
   /**
    * the {@value} CSS property.
    */
   public String STROKE_PAINT = "-fx-stroke";
   /**
    * the {@value} CSS property.
    */
   public String FILL_PAINT = "-fx-fill";
   /**
    * the {@value} CSS property.
    */
   public String TEXT_FILL = "-fx-text-fill";
   // Texts
   /**
    * the {@value} CSS property.
    */
   public String TEXT_BOUNDS_TYPE = "-fx-bounds-type";
   // font properties
   /**
    * the {@value} CSS property.
    */
   public String FONT = "-fx-font";
   /**
    * the {@value} CSS property.
    */
   public String FONT_FAMILY = "-fx-font-family";
   /**
    * the {@value} CSS property.
    */
   public String FONT_SIZE = "-fx-font-size";
   /**
    * the {@value} CSS property.
    */
   public String FONT_STYLE = "-fx-font-style";
   /**
    * the {@value} CSS property.
    */
   public String FONT_WEIGHT = "-fx-font-weight";
   // stroke properties
   /**
    * the {@value} CSS property.
    */
   public String STROKE_WIDTH = "-fx-stroke-width";
   /**
    * the {@value} CSS property.
    */
   public String STROKE_LINECAP = "-fx-stroke-line-cap";
   /**
    * the {@value} CSS property.
    */
   public String STROKE_LINEJOIN = "-fx-stroke-line-join";
   /**
    * the {@value} CSS property.
    */
   public String STROKE_MITERLIMIT = "-fx-stroke-miter-limit";
   /**
    * the {@value} CSS property.
    */
   public String STROKE_DASHARRAY = "-fx-stroke-dash-array";
   /**
    * the {@value} CSS property.
    */
   public String STROKE_DASHOFFSET = "-fx-stroke-dash-offset";
   /**
    * the {@value} CSS property.
    */
   public String STROKE_TYPE = "-fx-stroke-type";
}
