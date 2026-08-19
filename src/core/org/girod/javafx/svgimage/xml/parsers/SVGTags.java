/*
Copyright (c) 2021, 2022, 2025, 2026 Hervé Girod
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
 * @version 1.9
 */
public interface SVGTags {
   /**
    * The link element tag.
    */
   public static String LINK = "link";   
   /**
    * The rel attribute tag (used in link).
    */
   public static String LINK_REL = "rel";      
   /**
    * The type attribute tag (used in link).
    */
   public static String LINK_TYPE = "type";   
   
   /**
    * SVG constant for viewBox.
    */
   public static String VIEWBOX = "viewBox";
   /**
    * SVG constant for inherit.
    */
   public static String INHERIT = "inherit";   
   /**
    * SVG constant for fill.
    */
   public static String FILL = "fill";
   /**
    * SVG constant for context-fill.
    */
   public static String CONTEXT_FILL = "context-fill";
   /**
    * SVG constant for color-interpolation.
    */
   public static String COLOR_INTERPOLATION = "color-interpolation";   
   /**
    * SVG constant for stroke.
    */
   public static String STROKE = "stroke";
   /**
    * SVG constant for context-stroke.
    */
   public static String CONTEXT_STROKE = "context-stroke";
   /**
    * SVG constant for rect.
    */
   public static String RECT = "rect";
   /**
    * SVG constant for circle.
    */
   public static String CIRCLE = "circle";
   /**
    * SVG constant for ellipse.
    */
   public static String ELLIPSE = "ellipse";
   /**
    * SVG constant for path.
    */
   public static String PATH = "path";
   /**
    * SVG constant for mask.
    */
   public static String MASK = "mask";
   /**
    * SVG constant for polygon.
    */
   public static String POLYGON = "polygon";
   /**
    * SVG constant for line.
    */
   public static String LINE = "line";
   /**
    * SVG constant for polyline.
    */
   public static String POLYLINE = "polyline";
   /**
    * SVG constant for text.
    */
   public static String TEXT = "text";
   /**
    * SVG constant for image.
    */
   public static String IMAGE = "image";
   /**
    * SVG constant for D.
    */
   public static String D = "d";
   /**
    * SVG constant for svg.
    */
   public static String SVG = "svg";
   /**
    * SVG constant for use.
    */
   public static String USE = "use";
   /**
    * SVG constant for marker.
    */
   public static String MARKER = "marker";
   /**
    * SVG constant for g.
    */
   public static String G = "g";
   /**
    * SVG constant for SYMBOL.
    */
   public static String SYMBOL = "symbol";
   /**
    * SVG constant for defs.
    */
   public static String DEFS = "defs";
   /**
    * SVG constant for none.
    */
   public static String NONE = "none";
   /**
    * SVG constant for clipPath.
    */
   public static String CLIP_PATH_SPEC = "clipPath";
   /**
    * SVG constant for clip-path.
    */
   public static String CLIP_PATH = "clip-path";
   /**
    * SVG constant for linearGradient.
    */
   public static String LINEAR_GRADIENT = "linearGradient";
   /**
    * SVG constant for radialGradient.
    */
   public static String RADIAL_GRADIENT = "radialGradient";
   /**
    * SVG constant for gradientUnits.
    */
   public static String GRADIENT_UNITS = "gradientUnits";
   /**
    * SVG constant for spreadMethod.
    */
   public static String SPREAD_METHOD = "spreadMethod";
   /**
    * SVG constant for pad.
    */
   public static String SPREAD_PAD = "pad";
   /**
    * SVG constant for reflect.
    */
   public static String SPREAD_REFLECT = "reflect";
   /**
    * SVG constant for repeat.
    */
   public static String SPREAD_REPEAT = "repeat";
   /**
    * SVG constant for stop.
    */
   public static String STOP = "stop";
   /**
    * SVG constant for stop-color.
    */
   public static String STOP_COLOR = "stop-color";
   /**
    * SVG constant for stop-opacity.
    */
   public static String STOP_OPACITY = "stop-opacity";
   /**
    * SVG constant for gradientTransform.
    */
   public static String GRADIENT_TRANSFORM = "gradientTransform";
   /**
    * SVG constant for marker-start.
    */
   public static String MARKER_START = "marker-start";
   /**
    * SVG constant for marker-mid.
    */
   public static String MARKER_MID = "marker-mid";
   /**
    * SVG constant for MARKER_END.
    */
   public static String MARKER_END = "marker-end";
   /**
    * SVG constant for markerWidth.
    */
   public static String MARKER_WIDTH = "markerWidth";
   /**
    * SVG constant for markerHeight.
    */
   public static String MARKER_HEIGHT = "markerHeight";
   /**
    * SVG constant for orient.
    */
   public static String ORIENT = "orient";
   /**
    * SVG constant for auto.
    */
   public static String ORIENT_AUTO = "auto";
   /**
    * SVG constant for auto-start-reverse.
    */
   public static String ORIENT_AUTO_REVERSE = "auto-start-reverse";
   /**
    * SVG constant for refX.
    */
   public static String REFX = "refX";
   /**
    * SVG constant for refY.
    */
   public static String REFY = "refY";
   /**
    * SVG constant for tspan.
    */
   public static String TSPAN = "tspan";
   /**
    * SVG constant for id.
    */
   public static String ID = "id";
   /**
    * SVG constant for fill-rule.
    */
   public static String FILL_RULE = "fill-rule";
   /**
    * SVG constant for clip-rule.
    */
   public static String CLIP_RULE = "clip-rule";
   /**
    * SVG constant for clipPathUnits.
    */
   public static String CLIP_PATH_UNITS = "clipPathUnits";
   /**
    * SVG constant for userSpaceOnUse.
    */
   public static String USERSPACE_ON_USE = "userSpaceOnUse";
   /**
    * SVG constant for objectBoundingBox.
    */
   public static String OBJECT_BOUNDINGBOX = "objectBoundingBox";
   /**
    * SVG constant for nonzero.
    */
   public static String NON_ZERO = "nonzero";
   /**
    * SVG constant for evenodd.
    */
   public static String EVEN_ODD = "evenodd";
   /**
    * SVG constant for xlink:href.
    */
   public static String XLINK_HREF = "xlink:href";
   /**
    * SVG constant for href.
    */
   public static String HREF = "href";
   /**
    * SVG constant for animate.
    */
   public static String ANIMATE = "animate";
   /**
    * SVG constant for animateMotion.
    */
   public static String ANIMATE_MOTION = "animateMotion";
   /**
    * SVG constant for animateTransform.
    */
   public static String ANIMATE_TRANSFORM = "animateTransform";
   /**
    * SVG constant for set.
    */
   public static String SET = "set";
   /**
    * SVG constant for attributeName.
    */
   public static String ATTRIBUTE_NAME = "attributeName";
   /**
    * SVG constant for repeatCount.
    */
   public static String REPEAT_COUNT = "repeatCount";
   /**
    * SVG constant for from.
    */
   public static String FROM = "from";
   /**
    * SVG constant for to.
    */
   public static String TO = "to";
   /**
    * SVG constant for values.
    */
   public static String VALUES = "values";
   /**
    * SVG constant for begin.
    */
   public static String BEGIN = "begin";
   /**
    * SVG constant for dur.
    */
   public static String DUR = "dur";
   /**
    * SVG constant for type.
    */
   public static String TYPE = "type";
   /**
    * SVG constant for additive.
    */
   public static String ADDITIVE = "additive";
   /**
    * SVG constant for translate.
    */
   public static String TRANSLATE = "translate";
   /**
    * SVG constant for scale.
    */
   public static String SCALE = "scale";
   /**
    * SVG constant for rotate.
    */
   public static String ROTATE = "rotate";
   /**
    * SVG constant for skewX.
    */
   public static String SKEW_X = "skewX";
   /**
    * SVG constant for skewY.
    */
   public static String SKEW_Y = "skewY";
   /**
    * SVG constant for indefinite.
    */
   public static String INDEFINITE = "indefinite";
   /**
    * SVG constant for filter.
    */
   public static String FILTER = "filter";
   /**
    * SVG constant for feGaussianBlur.
    */
   public static String FE_GAUSSIAN_BLUR = "feGaussianBlur";
   /**
    * SVG constant for feDropShadow.
    */
   public static String FE_DROP_SHADOW = "feDropShadow";
   /**
    * SVG constant for feFlood.
    */
   public static String FE_FLOOD = "feFlood";
   /**
    * SVG constant for feImage.
    */
   public static String FE_IMAGE = "feImage";
   /**
    * SVG constant for feOffset.
    */
   public static String FE_OFFSET = "feOffset";
   /**
    * SVG constant for feMorphology.
    */
   public static String FE_MORPHOLOGY = "feMorphology";
   /**
    * SVG constant for feComposite.
    */
   public static String FE_COMPOSITE = "feComposite";
   /**
    * SVG constant for feMerge.
    */
   public static String FE_MERGE = "feMerge";
   /**
    * SVG constant for feMergeNode.
    */
   public static String FE_MERGE_NODE = "feMergeNode";
   /**
    * SVG constant for feDistantLight.
    */
   public static String FE_DISTANT_LIGHT = "feDistantLight";
   /**
    * SVG constant for fePointLight.
    */
   public static String FE_POINT_LIGHT = "fePointLight";
   /**
    * SVG constant for feSpotLight.
    */
   public static String FE_SPOT_LIGHT = "feSpotLight";
   /**
    * SVG constant for feSpecularLighting.
    */
   public static String FE_SPECULAR_LIGHTING = "feSpecularLighting";
   /**
    * SVG constant for feDiffuseLighting.
    */
   public static String FE_DIFFUSE_LIGHTING = "feDiffuseLighting";
   /**
    * SVG constant for flood-color.
    */
   public static String FLOOD_COLOR = "flood-color";
   /**
    * SVG constant for flood-opacity.
    */
   public static String FLOOD_OPACITY = "flood-opacity";
   /**
    * SVG constant for stdDeviation.
    */
   public static String STD_DEVIATION = "stdDeviation";
   /**
    * SVG constant for preserveAspectRatio.
    */
   public static String PRESERVE_ASPECT_RATIO = "preserveAspectRatio";
   /**
    * SVG constant for in.
    */
   public static String IN = "in";
   /**
    * SVG constant for in2.
    */
   public static String IN2 = "in2";
   /**
    * SVG constant for operator.
    */
   public static String OPERATOR = "operator";
   /**
    * SVG constant for over.
    */
   public static String OPERATOR_OVER = "over";
   /**
    * SVG constant for in.
    */
   public static String OPERATOR_IN = "in";
   /**
    * SVG constant for out.
    */
   public static String OPERATOR_OUT = "out";
   /**
    * SVG constant for atop.
    */
   public static String OPERATOR_ATOP = "atop";
   /**
    * SVG constant for xor.
    */
   public static String OPERATOR_XOR = "xor";
   /**
    * SVG constant for arithmetic.
    */
   public static String OPERATOR_ARITHMETIC = "arithmetic";
   /**
    * SVG constant for surfaceScale.
    */
   public static String SURFACE_SCALE = "surfaceScale";
   /**
    * SVG constant for diffuseConstant.
    */
   public static String DIFFUSE_CONSTANT = "diffuseConstant";
   /**
    * SVG constant for specularConstant.
    */
   public static String SPECULAR_CONSTANT = "specularConstant";
   /**
    * SVG constant for specularExponent.
    */
   public static String SPECULAR_EXPONENT = "specularExponent";
   /**
    * SVG constant for lighting.
    */
   public static String LIGHTING_COLOR = "lighting-color";
   /**
    * SVG constant for azimuth.
    */
   public static String AZIMUTH = "azimuth";
   /**
    * SVG constant for elevation.
    */
   public static String ELEVATION = "elevation";
   /**
    * SVG constant for SourceGraphic.
    */
   public static String SOURCE_GRAPHIC = "SourceGraphic";
   /**
    * SVG constant for SourceAlpha.
    */
   public static String SOURCE_ALPHA = "SourceAlpha";
   /**
    * SVG constant for result.
    */
   public static String RESULT = "result";
   /**
    * SVG constant for fx
    */
   public static String FX = "fx";
   /**
    * SVG constant for fy.
    */
   public static String FY = "fy";
   /**
    * SVG constant for cx.
    */
   public static String CX = "cx";
   /**
    * SVG constant for cy.
    */
   public static String CY = "cy";
   /**
    * SVG constant for dx.
    */
   public static String DX = "dx";
   /**
    * SVG constant for dy.
    */
   public static String DY = "dy";
   /**
    * SVG constant for rx.
    */
   public static String RX = "rx";
   /**
    * SVG constant for ry.
    */
   public static String RY = "ry";
   /**
    * SVG constant for r.
    */
   public static String R = "r";
   /**
    * SVG constant for x.
    */
   public static String X = "x";
   /**
    * SVG constant for y.
    */
   public static String Y = "y";
   /**
    * SVG constant for z.
    */
   public static String Z = "z";
   /**
    * SVG constant for x1.
    */
   public static String X1 = "x1";
   /**
    * SVG constant for y1.
    */
   public static String Y1 = "y1";
   /**
    * SVG constant for x2.
    */
   public static String X2 = "x2";
   /**
    * SVG constant for y2.
    */
   public static String Y2 = "y2";
   /**
    * SVG constant for points.
    */
   public static String POINTS = "points";
   /**
    * SVG constant for radius.
    */
   public static String RADIUS = "radius";
   /**
    * SVG constant for pointsAtX.
    */
   public static String POINT_AT_X = "pointsAtX";
   /**
    * SVG constant for pointsAtY.
    */
   public static String POINT_AT_Y = "pointsAtY";
   /**
    * SVG constant for pointsAtZ.
    */
   public static String POINT_AT_Z = "pointsAtZ";
   /**
    * SVG constant for dilate.
    */
   public static String DILATE = "dilate";
   /**
    * SVG constant for offset.
    */
   public static String OFFSET = "offset";
   /**
    * SVG constant for style.
    */
   public static String STYLE = "style";
   /**
    * SVG constant for square.
    */
   public static String SQUARE = "square";
   /**
    * SVG constant for round.
    */
   public static String ROUND = "round";
   /**
    * SVG constant for butt.
    */
   public static String BUTT = "butt";
   /**
    * SVG constant for bevel.
    */
   public static String BEVEL = "bevel";
   /**
    * SVG constant for miter.
    */
   public static String MITER = "miter";
   /**
    * SVG constant for font-family.
    */
   public static String FONT_FAMILY = "font-family";
   /**
    * SVG constant for font-style.
    */
   public static String FONT_STYLE = "font-style";
   /**
    * SVG constant for font-size.
    */
   public static String FONT_SIZE = "font-size";
   /**
    * SVG constant for font-weight.
    */
   public static String FONT_WEIGHT = "font-weight";
   /**
    * SVG constant for text-decoration.
    */
   public static String TEXT_DECORATION = "text-decoration";
   /**
    * SVG constant for text-anchor.
    */
   public static String TEXT_ANCHOR = "text-anchor";
   /**
    * SVG constant for start.
    */
   public static String START = "start";
   /**
    * SVG constant for middle.
    */
   public static String MIDDLE = "middle";
   /**
    * SVG constant for end.
    */
   public static String END = "end";
   /**
    * SVG constant for normal.
    */
   public static String NORMAL = "normal";
   /**
    * SVG constant for bold.
    */
   public static String BOLD = "bold";
   /**
    * SVG constant for bolder.
    */
   public static String BOLDER = "bolder";
   /**
    * SVG constant for lighter.
    */
   public static String LIGHTER = "lighter";
   /**
    * SVG constant for italic.
    */
   public static String ITALIC = "italic";
   /**
    * SVG constant for oblique.
    */
   public static String OBLIQUE = "oblique";
   /**
    * SVG constant for line-through.
    */
   public static String LINE_THROUGH = "line-through";
   /**
    * SVG constant for baseline-shift.
    */
   public static String BASELINE_SHIFT = "baseline-shift";
   /**
    * SVG constant for sub.
    */
   public static String BASELINE_SUB = "sub";
   /**
    * SVG constant for super.
    */
   public static String BASELINE_SUPER = "super";
   /**
    * SVG constant for underline.
    */
   public static String UNDERLINE = "underline";
   /**
    * SVG constant for width.
    */
   public static String WIDTH = "width";
   /**
    * SVG constant for height.
    */
   public static String HEIGHT = "height";
   /**
    * SVG constant for transform.
    */
   public static String TRANSFORM = "transform";
   /**
    * SVG constant for opacity.
    */
   public static String OPACITY = "opacity";
   /**
    * SVG constant for stroke-opacity.
    */
   public static String STROKE_OPACITY = "stroke-opacity";   
   /**
    * SVG constant for visibility.
    */
   public static String VISIBILITY = "visibility";
   /**
    * SVG constant for visible.
    */
   public static String VISIBLE = "visible";
   /**
    * SVG constant for hidden.
    */
   public static String HIDDEN = "hidden";
   /**
    * SVG constant for fill-opacity.
    */
   public static String FILL_OPACITY = "fill-opacity";
   /**
    * SVG constant for stroke-width.
    */
   public static String STROKE_WIDTH = "stroke-width";
   /**
    * SVG constant for stroke-linecap.
    */
   public static String STROKE_LINECAP = "stroke-linecap";
   /**
    * SVG constant for stroke-miterlimit.
    */
   public static String STROKE_MITERLIMIT = "stroke-miterlimit";
   /**
    * SVG constant for stroke-linejoin.
    */
   public static String STROKE_LINEJOIN = "stroke-linejoin";
   /**
    * SVG constant for stroke-dasharray.
    */
   public static String STROKE_DASHARRAY = "stroke-dasharray";
   /**
    * SVG constant for stroke-dashoffset.
    */
   public static String STROKE_DASHOFFSET = "stroke-dashoffset";
   /**
    * SVG constant for class.
    */
   public static String CLASS = "class";
}
