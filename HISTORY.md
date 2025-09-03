# History

# 1.4
 - Fix #61: The gradients which are not defined in a defs node are not taken into account
 - Fix #62, #63: Marker marker-mid attribute of svg <path> element is not taken into account

# 1.3
 - Fix #10: Loader params work on some SVG but another, it doesn't. The problem was due to paths which were not scaled correctly
 - Fix #53: Implement a drag gesture in the browser
 - Fix #18: <style> elements are order-sensitive
 - Fix #54: The parser does not take into account multiple style declarations
 - Fix #55: The styles by element types names are not taken into account
 - Fix #21: Multiple styles don't get applied correctly
 - Fix #34: Decimal font sizes don't work on the 0.0-1.0 range 
 - Fix #51: Failed to render text with tspan
 - Fix #57: Add a refresh option for the browser

# 1.2
 - Use docJGenerator 1.6.10
 - Use groovy 4.0.26 (only used for tests, the binaries have no dependencies
 - Move the Viewport and ViewBox classes in the org.girod.javafx.svgimage package
 - Fix #47: Add a parameter for the Color of the svg background in the JavaFX to SVG parameters
 - Fix #48: Add a parameter to generate the viewbox in the JavaFX to SVG parameters
 - Fix #49: Add a parameter to not allow the transform on the root of the svg document in the JavaFX to SVG parameters
 - Fix #42: Failed to render text with tspan in some cases
 - Fix #45: Mask not working
 - Fix #37: SVGImage's scaleTo doesn't seem to override svg width height parameters
 - Fix #50: The default LineCap, LineJoin, and miterlimit on Shapes are incorrect
 - Fix #36: Allow to scale on parent element

## 1.1
 - Fix #32: Fix some cases where the resulting paths are null
 - Fix #30: Fix some paths rendering issues 

## 1.0
 - Add a converter which is able to convert a JavaFX node tree to a svg file
 - Fix the transformations on nodes which were sometime wrong when using a scale in the LoaderParameters
 - Support images embedded the svg file as base64
 - Fix the width and height of the resulting image when writing a Node which could be off by one pixel, when using a scale
 - Keep the URL or String origin of the SVG in the resulting JavaFX SVGImage
 - Fix #22: Fix the computation of some lengths units, and add support for the em and ex units
 - Fix #17: Accept transforms declarations with optional spaces
 - Fix #19: Support text-anchor for text elements
 - Fix #20: Fix the style and color for use elements not taken into account 
 - Fix #24: Fix some cases where Polygon or Polylines would not be correctly parsed
 - Fix #23: Take correctly into account Line elements with default values
 - Fix #25: By default scaling an SVGImage scale the initial SVGImage rather than creating another one
 - Add zoom commands in the browser
 - Fix #27: The pt unit is not converted correctly
 - Fix #28: The conversion of length values with dots and dots throw an internal exception 
 - Fix #31: Fixed several unit tests which failed after fixing issue #25

## 0.6.1
 - Fix the browser not showing some animations correctly
 - Protect the browser against invalid SVG files
 - Fix the scale transform not working with only one argument
 - Fix the skewX and skewY transforms
 - Support the "none" color
 - Handle the cases where the viewPort position is not at 0,0
 - Add a LoaderParameter property to specify if the viewPort position is taken into account
 - Fix the gradients href parameter only been taken into account if the referenced gradient was defined before the one which referred to it
 - Fix the gradients gradientTransform parameter not taken into account in the majority of cases
 - Handle proportional coordinates for linear gradients
 - Handle width and height specified in % in the svg root

## 0.6
 - Support the "preserveAspectRatio" attribute for the svg root element
 - Allow to load a SVG from a String
 - Specifies a global configuration for the handling of exceptions during parsing
 - Fix a lot of problems which appeared when scaling the content at the loader level
 - Fix the position of the svg content which was not correctly translated if scaled during the loading 
 - Take into account the viewport and the units in the path element
 - Take into account rotate transforms with only one parameter
 - Add a parameter to allow to center the resulting svg
 - Support the visibility attribute
 - Start support for svg animations: animateTransform, animateMotion, and animate are partially suppported

## 0.5.6
- Support the use element for images and g elements
- Support the symbol element
- Support the "viewBox", "width", "height", and "preserveAspectRatio" attributes for the symbol element

## 0.5.5
- Fix the text elements with tspan children not having their position set if they have an x,y position
- Support the tspan "dx" and "dy" attributes
- Support the "clipPathUnits" attribute for clipPaths elements
- Fix the clipPath elements only being taken into account in defs parents
- Fix the values for coordinates parsed to a value of zero if having a pattern without an integer part (such as ".5")
- Support the use element for basic shapes

## 0.5.4
 - Make sure that the snapshot generation class does not emit an exception before the actual generation if Swing is not available
 - Allow to have both underline and line-through for a text
 - Support the "transform" attribute for the clipPath element
 - Support the translateX, translateY, scaleX, and scaleY transformations
 - Allow using units in transformations
 - Allow using more than one transformation in the "transform" or "gradientTransform" attribute

## 0.5.3
 - Add a snapshot method in the SVGImage to simplify the process of writing the content of the result on disk

## 0.5.2
 - Fix the SVG browser not included in the distribution

## 0.5.1
 - Support the "font-style" and "font-weight" attributes for text (also supported in style)
 - Fix the computing of font size when an unit is provided
 - Support the Font characteristics in the style node
 - Support the "oblique" value for the font-style property
 - Fix some styles in the style element which were not correctly parsed
 - Support the "fill-opacity" attribute
 - Support the "text-decoration" attribute
 - Support the "baseline-shift" attribute for tspan elements
 - Support the "rx" and "ry" attributes for rect elements
 - Fix the tspan elements not using the styles from their text parent
 - Fix the tspan elements not correctly positioned if not having a declared position in the SVG document
 - Support percents for the "opacity" and "fill-opacity" attributes
 - Fix the radial gradients
 - Support the spreadMethod argument for gradients
 - Allow urls specifications enclosed with "'" quotes
 - Fix the "clip-path" attribute or style not taken into account for nodes
 - Support text, line, polygon and polyline in the clipPath element
 - Support the fill-rule property for paths
 - Add a SVG browser

## 0.5
 - Fix the parsing of the viewBox element for the width and height
 - Support the filter element for the feSpecularLighting and feDiffuseLighting filters. Note that the result is still
   incorrect in many cases
 - Support the feDistantLight, fePointLight, and feSpotLight elements
 - Fix most stylings not correctly applied on non Shape elements
 - Fix the xlink:href attribute not taken into account for image elements
 - Fix the "none" value for the stroke-dasharray throwing an exception 

## 0.4
 - Parse the viewBox element
 - Support stroke-dasharray and stroke-dashoffset
 - Support the style node
 - Avoid throwing an exception if the svg document contains a DTD with reference to an external http source, on a platform which has no internet access
 - Support the filter element for some filters
 - Propagate style attributes from parent nodes to child nodes
 - Add a wiki

## 0.3.2
 - Fix the position of text and image elements
 - Support the mm and % length unit
 - Parse the SVG viewport width and height
 - Fix some cases where lengths where incorrectly parsed
 - Add a maven pom

## 0.3.1
 - Correctly wrap all exceptions during parsing by a SVGParsingException
 - Add Unit Tests
 - Support the cm length unit

## 0.3
 - Protect against unparsable transforms
 - Accept the "-" character for the minus sign
 - Fix the library not allowing to use several times the same clip 
 - Fix the library not parsing direct "clip-path" elements (only parsed those defined within style values)
 - Fix some style values not properly parsed
 - Fix the stroke of shapes which was incorrectly assigned as a fill
 - Allow to call the loader outside of the JavaFX Platform Thread

## 0.2
- Support percentages when appropriate (for example for GradientStop definitions)
- Support some RadialGradient definitions
- Allow to specify default SnapshotParameters in the SVGImage
- Use a class to specify the parameters for the SVG loading
- Fix the layout position of scaled images
- Fix an exception which could appear after a rotation

## 0.1
- Initial release of the library