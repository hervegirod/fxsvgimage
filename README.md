# fxsvgimage
This library allows to convert a SVG file to a JavaFX Node tree or an Image. 

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
 - Support stroke-dasharray and stroke-dashoffset
 - Support the style node
 - Avoid throwing an exception if the svg document contains a DTD with reference to an external http source, on a platform which has no internet access
 - Support the filter element
 - Propagate style attributes from parent nodes to child nodes

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
- rect, circle, ellipse, path, polygon, polyline, line, image, text, tspan
- fill, stroke, style, class, transform attributes

# Limitations
- Radial gradients which use absolute values for their definitions do not work correctly for the moment

# Other libraries
The following libraries also convert a SVG file to a JavaFX Node tree:
- https://github.com/afester/FranzXaver converts a SVG file but uses Batik internally
- https://github.com/codecentric/javafxsvg converts a SVG file but uses Batik internally
