# fxsvgimage
This library allows to convert a SVG file to a JavaFX Node tree or an Image. See https://fxsvgimage.sourceforge.io/ for a more
complete documentation.

Note that contrary to other existing libraries, this library has no external dependencies (including Batik)

## Using fxsvgimage as a maven/gradle dependency

fxsvgimage is not yet available in maven central. Until it is you can still use a release as a maven or gradle dependency
through [![Release](https://jitpack.io/v/hervegirod/fxsvgimage.svg)](https://jitpack.io/#hervegirod/fxsvgimage)

For maven do the following:
1. Add jitpack to your list of repositories i.e. in the `<repositories>` section add:
```xml
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
```
2. add the latest release of fxsvgimage just as you would add a normal dependency e.g:

**Core only** (SVG loading/conversion, no CSS stylesheet support):
```xml
    <dependency>
      <groupId>com.github.hervegirod</groupId>
      <artifactId>fxsvgimage</artifactId>
      <version>1.6</version>
    </dependency>
```

**Core + CSS parser** (adds CSS stylesheet handling):
```xml
    <dependency>
      <groupId>com.github.hervegirod</groupId>
      <artifactId>fxsvgimage</artifactId>
      <version>1.6</version>
    </dependency>
    <dependency>
      <groupId>com.github.hervegirod</groupId>
      <artifactId>fxsvgimage</artifactId>
      <version>1.6</version>
      <classifier>cssparser</classifier>
    </dependency>
```

For gradle it would be:
```groovy
repositories {
  mavenCentral()
  maven { url 'https://jitpack.io' }
}
dependencies {
  // Core only
  implementation 'com.github.hervegirod:fxsvgimage:1.6'

  // Add this for CSS stylesheet support
  implementation 'com.github.hervegirod:fxsvgimage:1.6:cssparser'
}
```
See https://jitpack.io/ for info on the syntax for other build systems such as sbt, leiningen etc.

# History
See [HISTORY.md](HISTORY.md)

# Usage
This tools allows to allows to convert a SVG file to a JavaFX Node tree or an Image. Just calls one of the static `load` methods, for example:
~~~~
SVGImage img = SVGLoader.load(<my SVG file>);
~~~~
The `SVGImage` class is a `Group`.

It also allows to convert a JavaFX Node tree to a SVG file. For example:
~~~~
      ConverterParameters params = new ConverterParameters();
      params.width = 150;
      params.height = 150;
      SVGConverter converter = new SVGConverter();
      converter.convert(<my JavaFX root>, params, <my SVG file>);
~~~~

# Supported SVG constructs
This library support:
- clip paths
- linear gradients
- radial gradients
- filters
- rect, circle, ellipse, path, polygon, polyline, line, image, text, tspan
- use, symbol
- fill, stroke, style, class, transform attributes
- animations (partial)

# Limitations
- Radial gradients which use absolute values for their definitions do not work correctly for the moment

# Other libraries
The following libraries also convert a SVG file to a JavaFX Node tree:
- https://github.com/afester/FranzXaver converts a SVG file but uses Batik internally
- https://github.com/codecentric/javafxsvg converts a SVG file but uses Batik internally
