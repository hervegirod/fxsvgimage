/*
Copyright (c) 2021, 2022, 2025 Hervé Girod
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
package org.girod.javafx.svgimage.xml.parsers;

/**
 * The list of svg tags handled by the library.
 *
 * @version 1.2
 */
public interface SVGTags {
   /**
    * SVG constant for VIEWBOX.
    */
   public static String VIEWBOX = "viewBox";
   /**
    * SVG constant for FILL.
    */
   public static String FILL = "fill";
   /**
    * SVG constant for CONTEXT_FILL.
    */
   public static String CONTEXT_FILL = "context-fill";
   /**
    * SVG constant for STROKE.
    */
   public static String STROKE = "stroke";
   /**
    * SVG constant for CONTEXT_STROKE.
    */
   public static String CONTEXT_STROKE = "context-stroke";
   /**
    * SVG constant for RECT.
    */
   public static String RECT = "rect";
   /**
    * SVG constant for CIRCLE.
    */
   public static String CIRCLE = "circle";
   /**
    * SVG constant for ELLIPSE.
    */
   public static String ELLIPSE = "ellipse";
   /**
    * SVG constant for PATH.
    */
   public static String PATH = "path";
   /**
    * SVG constant for MASK.
    */
   public static String MASK = "mask";
   /**
    * SVG constant for POLYGON.
    */
   public static String POLYGON = "polygon";
   /**
    * SVG constant for LINE.
    */
   public static String LINE = "line";
   /**
    * SVG constant for POLYLINE.
    */
   public static String POLYLINE = "polyline";
   /**
    * SVG constant for TEXT.
    */
   public static String TEXT = "text";
   /**
    * SVG constant for IMAGE.
    */
   public static String IMAGE = "image";
   /**
    * SVG constant for D.
    */
   public static String D = "d";
   /**
    * SVG constant for SVG.
    */
   public static String SVG = "svg";
   /**
    * SVG constant for USE.
    */
   public static String USE = "use";
   /**
    * SVG constant for MARKER.
    */
   public static String MARKER = "marker";
   /**
    * SVG constant for G.
    */
   public static String G = "g";
   /**
    * SVG constant for SYMBOL.
    */
   public static String SYMBOL = "symbol";
   /**
    * SVG constant for DEFS.
    */
   public static String DEFS = "defs";
   /**
    * SVG constant for NONE.
    */
   public static String NONE = "none";
   /**
    * SVG constant for CLIP_PATH_SPEC.
    */
   public static String CLIP_PATH_SPEC = "clipPath";
   /**
    * SVG constant for CLIP_PATH.
    */
   public static String CLIP_PATH = "clip-path";
   /**
    * SVG constant for LINEAR_GRADIENT.
    */
   public static String LINEAR_GRADIENT = "linearGradient";
   /**
    * SVG constant for RADIAL_GRADIENT.
    */
   public static String RADIAL_GRADIENT = "radialGradient";
   /**
    * SVG constant for GRADIENT_UNITS.
    */
   public static String GRADIENT_UNITS = "gradientUnits";
   /**
    * SVG constant for SPREAD_METHOD.
    */
   public static String SPREAD_METHOD = "spreadMethod";
   /**
    * SVG constant for SPREAD_PAD.
    */
   public static String SPREAD_PAD = "pad";
   /**
    * SVG constant for SPREAD_REFLECT.
    */
   public static String SPREAD_REFLECT = "reflect";
   /**
    * SVG constant for SPREAD_REPEAT.
    */
   public static String SPREAD_REPEAT = "repeat";
   /**
    * SVG constant for STOP.
    */
   public static String STOP = "stop";
   /**
    * SVG constant for STOP_COLOR.
    */
   public static String STOP_COLOR = "stop-color";
   /**
    * SVG constant for STOP_OPACITY.
    */
   public static String STOP_OPACITY = "stop-opacity";
   /**
    * SVG constant for GRADIENT_TRANSFORM.
    */
   public static String GRADIENT_TRANSFORM = "gradientTransform";
   /**
    * SVG constant for MARKER_START.
    */
   public static String MARKER_START = "marker-start";
   /**
    * SVG constant for MARKER_MID.
    */
   public static String MARKER_MID = "marker-mid";
   /**
    * SVG constant for MARKER_END.
    */
   public static String MARKER_END = "marker-end";
   /**
    * SVG constant for MARKER_WIDTH.
    */
   public static String MARKER_WIDTH = "markerWidth";
   /**
    * SVG constant for MARKER_HEIGHT.
    */
   public static String MARKER_HEIGHT = "markerHeight";
   /**
    * SVG constant for ORIENT.
    */
   public static String ORIENT = "orient";
   /**
    * SVG constant for ORIENT_AUTO.
    */
   public static String ORIENT_AUTO = "auto";
   /**
    * SVG constant for ORIENT_AUTO_REVERSE.
    */
   public static String ORIENT_AUTO_REVERSE = "auto-start-reverse";
   /**
    * SVG constant for REFX.
    */
   public static String REFX = "refX";
   /**
    * SVG constant for REFY.
    */
   public static String REFY = "refY";
   /**
    * SVG constant for TSPAN.
    */
   public static String TSPAN = "tspan";
   /**
    * SVG constant for ID.
    */
   public static String ID = "id";
   /**
    * SVG constant for FILL_RULE.
    */
   public static String FILL_RULE = "fill-rule";
   /**
    * SVG constant for CLIP_RULE.
    */
   public static String CLIP_RULE = "clip-rule";
   /**
    * SVG constant for CLIP_PATH_UNITS.
    */
   public static String CLIP_PATH_UNITS = "clipPathUnits";
   /**
    * SVG constant for USERSPACE_ON_USE.
    */
   public static String USERSPACE_ON_USE = "userSpaceOnUse";
   /**
    * SVG constant for OBJECT_BOUNDINGBOX.
    */
   public static String OBJECT_BOUNDINGBOX = "objectBoundingBox";
   /**
    * SVG constant for NON_ZERO.
    */
   public static String NON_ZERO = "nonzero";
   /**
    * SVG constant for EVEN_ODD.
    */
   public static String EVEN_ODD = "evenodd";
   /**
    * SVG constant for XLINK_HREF.
    */
   public static String XLINK_HREF = "xlink:href";
   /**
    * SVG constant for HREF.
    */
   public static String HREF = "href";
   /**
    * SVG constant for ANIMATE.
    */
   public static String ANIMATE = "animate";
   /**
    * SVG constant for ANIMATE_MOTION.
    */
   public static String ANIMATE_MOTION = "animateMotion";
   /**
    * SVG constant for ANIMATE_TRANSFORM.
    */
   public static String ANIMATE_TRANSFORM = "animateTransform";
   /**
    * SVG constant for SET.
    */
   public static String SET = "set";
   /**
    * SVG constant for ATTRIBUTE_NAME.
    */
   public static String ATTRIBUTE_NAME = "attributeName";
   /**
    * SVG constant for REPEAT_COUNT.
    */
   public static String REPEAT_COUNT = "repeatCount";
   /**
    * SVG constant for FROM.
    */
   public static String FROM = "from";
   /**
    * SVG constant for TO.
    */
   public static String TO = "to";
   /**
    * SVG constant for VALUES.
    */
   public static String VALUES = "values";
   /**
    * SVG constant for BEGIN.
    */
   public static String BEGIN = "begin";
   /**
    * SVG constant for DUR.
    */
   public static String DUR = "dur";
   /**
    * SVG constant for TYPE.
    */
   public static String TYPE = "type";
   /**
    * SVG constant for ADDITIVE.
    */
   public static String ADDITIVE = "additive";
   /**
    * SVG constant for TRANSLATE.
    */
   public static String TRANSLATE = "translate";
   /**
    * SVG constant for SCALE.
    */
   public static String SCALE = "scale";
   /**
    * SVG constant for ROTATE.
    */
   public static String ROTATE = "rotate";
   /**
    * SVG constant for SKEW_X.
    */
   public static String SKEW_X = "skewX";
   /**
    * SVG constant for SKEW_Y.
    */
   public static String SKEW_Y = "skewY";
   /**
    * SVG constant for INDEFINITE.
    */
   public static String INDEFINITE = "indefinite";
   /**
    * SVG constant for FILTER.
    */
   public static String FILTER = "filter";
   /**
    * SVG constant for FE_GAUSSIAN_BLUR.
    */
   public static String FE_GAUSSIAN_BLUR = "feGaussianBlur";
   /**
    * SVG constant for FE_DROP_SHADOW.
    */
   public static String FE_DROP_SHADOW = "feDropShadow";
   /**
    * SVG constant for FE_FLOOD.
    */
   public static String FE_FLOOD = "feFlood";
   /**
    * SVG constant for FE_IMAGE.
    */
   public static String FE_IMAGE = "feImage";
   /**
    * SVG constant for FE_OFFSET.
    */
   public static String FE_OFFSET = "feOffset";
   /**
    * SVG constant for FE_MORPHOLOGY.
    */
   public static String FE_MORPHOLOGY = "feMorphology";
   /**
    * SVG constant for FE_COMPOSITE.
    */
   public static String FE_COMPOSITE = "feComposite";
   /**
    * SVG constant for FE_MERGE.
    */
   public static String FE_MERGE = "feMerge";
   /**
    * SVG constant for FE_MERGE_NODE.
    */
   public static String FE_MERGE_NODE = "feMergeNode";
   /**
    * SVG constant for FE_DISTANT_LIGHT.
    */
   public static String FE_DISTANT_LIGHT = "feDistantLight";
   /**
    * SVG constant for FE_POINT_LIGHT.
    */
   public static String FE_POINT_LIGHT = "fePointLight";
   /**
    * SVG constant for FE_SPOT_LIGHT.
    */
   public static String FE_SPOT_LIGHT = "feSpotLight";
   /**
    * SVG constant for FE_SPECULAR_LIGHTING.
    */
   public static String FE_SPECULAR_LIGHTING = "feSpecularLighting";
   /**
    * SVG constant for FE_DIFFUSE_LIGHTING.
    */
   public static String FE_DIFFUSE_LIGHTING = "feDiffuseLighting";
   /**
    * SVG constant for FLOOD_COLOR.
    */
   public static String FLOOD_COLOR = "flood-color";
   /**
    * SVG constant for FLOOD_OPACITY.
    */
   public static String FLOOD_OPACITY = "flood-opacity";
   /**
    * SVG constant for STD_DEVIATION.
    */
   public static String STD_DEVIATION = "stdDeviation";
   /**
    * SVG constant for PRESERVE_ASPECT_RATIO.
    */
   public static String PRESERVE_ASPECT_RATIO = "preserveAspectRatio";
   /**
    * SVG constant for IN.
    */
   public static String IN = "in";
   /**
    * SVG constant for IN2.
    */
   public static String IN2 = "in2";
   /**
    * SVG constant for OPERATOR.
    */
   public static String OPERATOR = "operator";
   /**
    * SVG constant for OPERATOR_OVER.
    */
   public static String OPERATOR_OVER = "over";
   /**
    * SVG constant for OPERATOR_IN.
    */
   public static String OPERATOR_IN = "in";
   /**
    * SVG constant for OPERATOR_OUT.
    */
   public static String OPERATOR_OUT = "out";
   /**
    * SVG constant for OPERATOR_ATOP.
    */
   public static String OPERATOR_ATOP = "atop";
   /**
    * SVG constant for OPERATOR_XOR.
    */
   public static String OPERATOR_XOR = "xor";
   /**
    * SVG constant for OPERATOR_ARITHMETIC.
    */
   public static String OPERATOR_ARITHMETIC = "arithmetic";
   /**
    * SVG constant for SURFACE_SCALE.
    */
   public static String SURFACE_SCALE = "surfaceScale";
   /**
    * SVG constant for DIFFUSE_CONSTANT.
    */
   public static String DIFFUSE_CONSTANT = "diffuseConstant";
   /**
    * SVG constant for SPECULAR_CONSTANT.
    */
   public static String SPECULAR_CONSTANT = "specularConstant";
   /**
    * SVG constant for SPECULAR_EXPONENT.
    */
   public static String SPECULAR_EXPONENT = "specularExponent";
   /**
    * SVG constant for LIGHTING_COLOR.
    */
   public static String LIGHTING_COLOR = "lighting-color";
   /**
    * SVG constant for AZIMUTH.
    */
   public static String AZIMUTH = "azimuth";
   /**
    * SVG constant for ELEVATION.
    */
   public static String ELEVATION = "elevation";
   /**
    * SVG constant for SOURCE_GRAPHIC.
    */
   public static String SOURCE_GRAPHIC = "SourceGraphic";
   /**
    * SVG constant for SOURCE_ALPHA.
    */
   public static String SOURCE_ALPHA = "SourceAlpha";
   /**
    * SVG constant for RESULT.
    */
   public static String RESULT = "result";
   /**
    * SVG constant for FX.
    */
   public static String FX = "fx";
   /**
    * SVG constant for FY.
    */
   public static String FY = "fy";
   /**
    * SVG constant for CX.
    */
   public static String CX = "cx";
   /**
    * SVG constant for CY.
    */
   public static String CY = "cy";
   /**
    * SVG constant for DX.
    */
   public static String DX = "dx";
   /**
    * SVG constant for DY.
    */
   public static String DY = "dy";
   /**
    * SVG constant for RX.
    */
   public static String RX = "rx";
   /**
    * SVG constant for RY.
    */
   public static String RY = "ry";
   /**
    * SVG constant for R.
    */
   public static String R = "r";
   /**
    * SVG constant for X.
    */
   public static String X = "x";
   /**
    * SVG constant for Y.
    */
   public static String Y = "y";
   /**
    * SVG constant for Z.
    */
   public static String Z = "z";
   /**
    * SVG constant for X1.
    */
   public static String X1 = "x1";
   /**
    * SVG constant for Y1.
    */
   public static String Y1 = "y1";
   /**
    * SVG constant for X2.
    */
   public static String X2 = "x2";
   /**
    * SVG constant for Y2.
    */
   public static String Y2 = "y2";
   /**
    * SVG constant for POINTS.
    */
   public static String POINTS = "points";
   /**
    * SVG constant for RADIUS.
    */
   public static String RADIUS = "radius";
   /**
    * SVG constant for POINT_AT_X.
    */
   public static String POINT_AT_X = "pointsAtX";
   /**
    * SVG constant for POINT_AT_Y.
    */
   public static String POINT_AT_Y = "pointsAtY";
   /**
    * SVG constant for POINT_AT_Z.
    */
   public static String POINT_AT_Z = "pointsAtZ";
   /**
    * SVG constant for DILATE.
    */
   public static String DILATE = "dilate";
   /**
    * SVG constant for OFFSET.
    */
   public static String OFFSET = "offset";
   /**
    * SVG constant for STYLE.
    */
   public static String STYLE = "style";
   /**
    * SVG constant for SQUARE.
    */
   public static String SQUARE = "square";
   /**
    * SVG constant for ROUND.
    */
   public static String ROUND = "round";
   /**
    * SVG constant for BUTT.
    */
   public static String BUTT = "butt";
   /**
    * SVG constant for BEVEL.
    */
   public static String BEVEL = "bevel";
   /**
    * SVG constant for MITER.
    */
   public static String MITER = "miter";
   /**
    * SVG constant for FONT_FAMILY.
    */
   public static String FONT_FAMILY = "font-family";
   /**
    * SVG constant for FONT_STYLE.
    */
   public static String FONT_STYLE = "font-style";
   /**
    * SVG constant for FONT_SIZE.
    */
   public static String FONT_SIZE = "font-size";
   /**
    * SVG constant for FONT_WEIGHT.
    */
   public static String FONT_WEIGHT = "font-weight";
   /**
    * SVG constant for TEXT_DECORATION.
    */
   public static String TEXT_DECORATION = "text-decoration";
   /**
    * SVG constant for TEXT_ANCHOR.
    */
   public static String TEXT_ANCHOR = "text-anchor";
   /**
    * SVG constant for START.
    */
   public static String START = "start";
   /**
    * SVG constant for MIDDLE.
    */
   public static String MIDDLE = "middle";
   /**
    * SVG constant for END.
    */
   public static String END = "end";
   /**
    * SVG constant for NORMAL.
    */
   public static String NORMAL = "normal";
   /**
    * SVG constant for BOLD.
    */
   public static String BOLD = "bold";
   /**
    * SVG constant for BOLDER.
    */
   public static String BOLDER = "bolder";
   /**
    * SVG constant for LIGHTER.
    */
   public static String LIGHTER = "lighter";
   /**
    * SVG constant for ITALIC.
    */
   public static String ITALIC = "italic";
   /**
    * SVG constant for OBLIQUE.
    */
   public static String OBLIQUE = "oblique";
   /**
    * SVG constant for LINE_THROUGH.
    */
   public static String LINE_THROUGH = "line-through";
   /**
    * SVG constant for BASELINE_SHIFT.
    */
   public static String BASELINE_SHIFT = "baseline-shift";
   /**
    * SVG constant for BASELINE_SUB.
    */
   public static String BASELINE_SUB = "sub";
   /**
    * SVG constant for BASELINE_SUPER.
    */
   public static String BASELINE_SUPER = "super";
   /**
    * SVG constant for UNDERLINE.
    */
   public static String UNDERLINE = "underline";
   /**
    * SVG constant for WIDTH.
    */
   public static String WIDTH = "width";
   /**
    * SVG constant for HEIGHT.
    */
   public static String HEIGHT = "height";
   /**
    * SVG constant for TRANSFORM.
    */
   public static String TRANSFORM = "transform";
   /**
    * SVG constant for OPACITY.
    */
   public static String OPACITY = "opacity";
   /**
    * SVG constant for VISIBILITY.
    */
   public static String VISIBILITY = "visibility";
   /**
    * SVG constant for VISIBLE.
    */
   public static String VISIBLE = "visible";
   /**
    * SVG constant for HIDDEN.
    */
   public static String HIDDEN = "hidden";
   /**
    * SVG constant for FILL_OPACITY.
    */
   public static String FILL_OPACITY = "fill-opacity";
   /**
    * SVG constant for STROKE_WIDTH.
    */
   public static String STROKE_WIDTH = "stroke-width";
   /**
    * SVG constant for STROKE_LINECAP.
    */
   public static String STROKE_LINECAP = "stroke-linecap";
   /**
    * SVG constant for STROKE_MITERLIMIT.
    */
   public static String STROKE_MITERLIMIT = "stroke-miterlimit";
   /**
    * SVG constant for STROKE_LINEJOIN.
    */
   public static String STROKE_LINEJOIN = "stroke-linejoin";
   /**
    * SVG constant for STROKE_DASHARRAY.
    */
   public static String STROKE_DASHARRAY = "stroke-dasharray";
   /**
    * SVG constant for STROKE_DASHOFFSET.
    */
   public static String STROKE_DASHOFFSET = "stroke-dashoffset";
   /**
    * SVG constant for CLASS.
    */
   public static String CLASS = "class";
}
