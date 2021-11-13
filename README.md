# fxsvgimage
This library allows to convert a SVG file to a JavaFX Node tree or an Image. See https://fxsvgimage.sourceforge.io/ for a more
complete documentation.

Note that contrary to other existing libraries, this library has no external dependencies (including Batik)

# History
## 0.1
- Initial release of the library

## 0.2
- Support percentages when appropriate (for example for GradientStop definitions)
- Support some RadialGradient definitions
- Allow to specify default SnapshotParameters in the SVGImage
- Use a class to specify the parameters for the SVG loading
- Fix the layout position of scaled images
- Fix an exception which could appear after a rotation

## 0.3
 - Protect against unparsable transforms
 - Accept the "-" character for the minus sign
 - Fix the library not allowing to use several times the same clip 
 - Fix the library not parsing direct "clip-path" elements (only parsed those defined within style values)
 - Fix some style values not properly parsed
 - Fix the stroke of shapes which was incorrectly assigned as a fill
 - Allow to call the loader outside of the JavaFX Platform Thread

## 0.3.1
 - Correctly wrap all exceptions during parsing by a SVGParsingException
 - Add Unit Tests
 - Support the cm length unit

## 0.3.2
 - Fix the position of text and image elements
 - Support the mm and % length unit
 - Parse the SVG viewport width and height
 - Fix some cases where lengths where incorrectly parsed
 - Add a maven pom

## 0.4
 - Parse the viewBox element
 - Support stroke-dasharray and stroke-dashoffset
 - Support the style node
 - Avoid throwing an exception if the svg document contains a DTD with reference to an external http source, on a platform which has no internet access
 - Support the filter element for some filters
 - Propagate style attributes from parent nodes to child nodes
 - Add a wiki

## 0.5
 - Fix the parsing of the viewBox element for the width and height
 - Support the filter element for the feSpecularLighting and feDiffuseLighting filters. Note that the result is still
   incorrect in many cases
 - Support the feDistantLight, fePointLight, and feSpotLight elements
 - Fix most stylings not correctly applied on non Shape elements
 - Fix the xlink:href attribute not taken into account for image elements
 - Fix the "none" value for the stroke-dasharray throwing an exception 

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

## 0.5.2
 - Fix the SVG browser not included in the distribution

## 0.5.3
 - Add a snapshot method in the SVGImage to simplify the process of writing the content of the result on disk

## 0.5.4
 - Make sure that the snapshot generation class does not emit an exception before the actual generation if Swing is not available
 - Allow to have both underline and line-through for a text
 - Support the "transform" attribute for the clipPath element
 - Support the translateX, translateY, scaleX, and scaleY transformations
 - Allow to use units in transformations
 - Allow to use more than one transformation in the "transform" or "gradientTransform" attribute

## 0.5.5 
 - Fix the text elements with tspan children not having their position set if they have an x,y position

# Usage
Just calls one of the static `load` methods, for example:
~~~~
SVGImage img = SVGLoader.load(<my SVG file>);
~~~~
The `SVGImage` class is a `Group`.

# Supported SVG constructs
This library support:
- clip paths
- linear gradients
- radial gradients
- filters
- rect, circle, ellipse, path, polygon, polyline, line, image, text, tspan
- fill, stroke, style, class, transform attributes

# Limitations
- Radial gradients which use absolute values for their definitions do not work correctly for the moment

# Other libraries
The following libraries also convert a SVG file to a JavaFX Node tree:
- https://github.com/afester/FranzXaver converts a SVG file but uses Batik internally
- https://github.com/codecentric/javafxsvg converts a SVG file but uses Batik internally
