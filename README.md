# fxsvgimage
This library allows to convert a SVG file to a JavaFX Node tree or an Image. See https://fxsvgimage.sourceforge.io/ for a more
complete documentation.

Note that contrary to other existing libraries, this library has no external dependencies (including Batik)

# History
See https://fxsvgimage.sourceforge.io/history.html

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
