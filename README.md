# fxsvgimage
This library allows to convert a SVG file to a JavaFX Node tree or an Image. 

Note that contrary to other existing libraries, this library has no external dependencies (including Batik)

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
- rect, circle, ellipse, path, polygon, polyline, line, image, text, tspan
- fill, stroke, style, class, transform attributes

