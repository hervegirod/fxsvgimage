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
package org.girod.javafx.svgimage;

import org.girod.javafx.svgimage.xml.GradientSpec;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.girod.javafx.svgimage.xml.ClippingFactory;
import org.girod.javafx.svgimage.xml.FilterSpec;
import org.girod.javafx.svgimage.xml.LengthParser;
import org.girod.javafx.svgimage.xml.ParserUtils;
import org.girod.javafx.svgimage.xml.SVGParsingException;
import org.girod.javafx.svgimage.xml.SVGShapeBuilder;
import org.girod.javafx.svgimage.xml.SVGStyleBuilder;
import org.girod.javafx.svgimage.xml.SVGTags;
import org.girod.javafx.svgimage.xml.SpanGroup;
import org.girod.javafx.svgimage.xml.Styles;
import org.girod.javafx.svgimage.xml.Viewport;
import org.girod.javafx.svgimage.xml.XMLNode;
import org.girod.javafx.svgimage.xml.XMLRoot;
import org.girod.javafx.svgimage.xml.XMLTreeHandler;
import org.xml.sax.SAXException;

/**
 * This class allows to load a svg file and convert it to an Image or a JavaFX tree.
 *
 * @version 0.5.1
 */
public class SVGLoader implements SVGTags {
   private final URL url;
   private final SVGImage root;
   private Viewport viewport = null;
   private Styles svgStyle = null;
   private final ClippingFactory clippingFactory = new ClippingFactory();
   private final Map<String, GradientSpec> gradientSpecs = new HashMap<>();
   private final Map<String, FilterSpec> filterSpecs = new HashMap<>();
   private final Map<String, Paint> gradients = new HashMap<>();
   private boolean effectsSupported = false;

   private SVGLoader(URL url) {
      this.url = url;
      this.root = new SVGImage();
   }

   /**
    * Load a svg File.
    *
    * @param file the file
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage load(File file) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }
   }

   /**
    * Load a svg URL.
    *
    * @param url the URL
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage load(URL url) throws SVGParsingException {
      SVGLoader loader = new SVGLoader(url);
      SVGImage img = loader.loadImpl();
      return img;
   }

   /**
    * Load a svg File, and set the styleSheets of the associated JavaFX Node.
    *
    * @param file the file
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage load(File file, String styleSheets) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url, styleSheets);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }
   }

   /**
    * Load a svg URL, and set the styleSheets of the associated JavaFX Node.
    *
    * @param url the URL
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage load(URL url, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      return load(url, params);
   }

   /**
    * Load a svg File, and scale the associated JavaFX Node.
    *
    * @param file the file
    * @param scale the scale
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage loadScaled(File file, double scale) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return loadScaled(url, scale);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }
   }

   /**
    * Load a svg URL, and scale the associated JavaFX Node.
    *
    * @param url the URL
    * @param scale the scale
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage loadScaled(URL url, double scale) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.scale = scale;
      return load(url, params);
   }

   /**
    * Load a svg File, and scale the associated JavaFX Node.
    *
    * @param file the file
    * @param width the resulting width
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage load(File file, double width) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url, width);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }
   }

   /**
    * Load a svg URL, and set the resulting width the associated JavaFX Node.
    *
    * @param url the URL
    * @param width the resulting width
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage load(URL url, double width) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.width = width;
      return load(url, params);
   }

   /**
    * Load a svg File, set the styleSheets and set the resulting width of the associated JavaFX Node.
    *
    * @param file the File
    * @param width the resulting width
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage load(File file, double width, String styleSheets) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url, width, styleSheets);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }
   }

   /**
    * Load a svg URL, set the styleSheets and set the resulting width of the associated JavaFX Node.
    *
    * @param url the URL
    * @param width the resulting width
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage load(URL url, double width, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      params.width = width;
      return load(url, params);
   }

   /**
    * Load a svg File, set the styleSheets and scale of the associated JavaFX Node.
    *
    * @param file the File
    * @param scale the scale
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage loadScaled(File file, double scale, String styleSheets) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return loadScaled(url, scale, styleSheets);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }

   }

   /**
    * Load a svg URL, set the styleSheets and scale of the associated JavaFX Node.
    *
    * @param url the URL
    * @param scale the scale
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage loadScaled(URL url, double scale, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      params.scale = scale;
      return load(url, params);
   }

   /**
    * Load a svg URL, and set the parameters of the associated JavaFX Node.
    *
    * @param url the URL
    * @param params the parameters
    * @return the SVGImage
    * @throws SVGParsingException
    */
   public static SVGImage load(URL url, LoaderParameters params) throws SVGParsingException {
      SVGLoader loader = new SVGLoader(url);
      SVGImage img = loader.loadImpl();
      if (params.styleSheets != null) {
         img.getStylesheets().add(params.styleSheets);
      }
      if (params.scale > 0) {
         double initialWidth = img.getLayoutBounds().getWidth();
         double initialHeight = img.getLayoutBounds().getHeight();
         img.setScaleX(params.scale);
         img.setScaleY(params.scale);
         img.setTranslateX(-initialWidth / 2 + initialWidth * params.scale / 2);
         img.setTranslateY(-initialHeight / 2);
      } else if (params.width > 0) {
         double initialWidth = img.getLayoutBounds().getWidth();
         double initialHeight = img.getLayoutBounds().getHeight();
         double scaleX = params.width / initialWidth;
         img.setScaleX(scaleX);
         img.setScaleY(scaleX);
         img.setTranslateX(-initialWidth / 2 + initialWidth * scaleX / 2);
         img.setTranslateY(-initialHeight / 2);
      }
      return img;
   }

   private SVGImage loadImpl() throws SVGParsingException {
      if (Platform.isFxApplicationThread()) {
         try {
            return loadImplInJFX();
         } catch (SVGParsingException ex) {
            throw ex;
         } catch (Exception ex) {
            throw new SVGParsingException(ex);
         }
      } else {
         // the next instruction is only there to initialize the JavaFX platform
         new JFXPanel();
         FutureTask<SVGImage> future = new FutureTask<>(new Callable<SVGImage>() {
            @Override
            public SVGImage call() throws Exception {
               SVGImage img = loadImplInJFX();
               return img;
            }
         });
         Platform.runLater(future);
         try {
            return future.get();
         } catch (InterruptedException ex) {
            return null;
         } catch (ExecutionException ex) {
            Throwable th = ex.getCause();
            if (th instanceof SVGParsingException) {
               throw (SVGParsingException) th;
            } else {
               throw new SVGParsingException(ex.getCause());
            }
         }
      }
   }

   private SVGImage loadImplInJFX() throws IOException {
      effectsSupported = Platform.isSupported(ConditionalFeature.EFFECT);
      SAXParserFactory saxfactory = SAXParserFactory.newInstance();
      try {
         // see https://stackoverflow.com/questions/10257576/how-to-ignore-inline-dtd-when-parsing-xml-file-in-java
         saxfactory.setFeature("http://xml.org/sax/features/resolve-dtd-uris", false);
         saxfactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
         saxfactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
         SAXParser parser = saxfactory.newSAXParser();
         XMLTreeHandler handler = new XMLTreeHandler();
         parser.parse(url.openStream(), handler);
         return walk(handler.getRoot());
      } catch (ParserConfigurationException | SAXException ex) {
         return null;
      }
   }

   private SVGImage walk(XMLRoot xmlRoot) {
      String name = xmlRoot.getName();
      if (name.equals(SVG)) {
         parseViewport(xmlRoot);
      }
      buildNode(xmlRoot, root);
      return root;
   }

   private void buildNode(XMLNode xmlNode, Group group) {
      buildNode(xmlNode, group, false);
   }

   private void buildNode(XMLNode xmlNode, Group group, boolean acceptDefs) {
      if (group == null) {
         group = new Group();
      }
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         Node node = null;
         SpanGroup spanGroup = null;
         String name = childNode.getName();
         switch (name) {
            case STYLE:
               manageSVGStyle(childNode);
               break;
            case RECT:
               node = SVGShapeBuilder.buildRect(childNode, viewport);
               break;
            case CIRCLE:
               node = SVGShapeBuilder.buildCircle(childNode, viewport);
               break;
            case ELLIPSE:
               node = SVGShapeBuilder.buildEllipse(childNode, viewport);
               break;
            case PATH:
               node = SVGShapeBuilder.buildPath(childNode, viewport);
               break;
            case POLYGON:
               node = SVGShapeBuilder.buildPolygon(childNode, viewport);
               break;
            case LINE:
               node = SVGShapeBuilder.buildLine(childNode, viewport);
               break;
            case POLYLINE:
               node = SVGShapeBuilder.buildPolyline(childNode, viewport);
               break;
            case TEXT:
               node = SVGShapeBuilder.buildText(childNode, viewport);
               if (node == null) {
                  spanGroup = buildTSpanGroup(childNode);
               }
               break;
            case IMAGE:
               node = SVGShapeBuilder.buildImage(childNode, url, viewport);
               break;
            case SVG:
               parseViewport(childNode);
               node = buildGroup(childNode);
               break;
            case G:
               node = buildGroup(childNode);
               break;
            case DEFS:
               if (!acceptDefs) {
                  buildDefs(childNode);
                  break;
               }
            case CLIP_PATH:
               if (acceptDefs) {
                  buildClipPath(childNode);
                  break;
               }
            case LINEAR_GRADIENT:
               if (acceptDefs) {
                  SVGShapeBuilder.buildLinearGradient(gradientSpecs, gradients, childNode);
                  break;
               }
            case RADIAL_GRADIENT:
               if (acceptDefs) {
                  SVGShapeBuilder.buildRadialGradient(gradientSpecs, gradients, childNode);
                  break;
               }
            case FILTER:
               buildFilter(childNode);
               break;
         }
         if (node != null) {
            addStyles(node, childNode);
            group.getChildren().add(node);
         } else if (spanGroup != null) {
            Map<String, String> theStylesMap = ParserUtils.getStyles(childNode);
            Iterator<SpanGroup.TSpan> it2 = spanGroup.getSpans().iterator();
            SpanGroup.TSpan previous = null;
            while (it2.hasNext()) {
               SpanGroup.TSpan tspan = it2.next();
               Text tspanText = tspan.text;
               String theStyles = ParserUtils.mergeStyles(theStylesMap, tspan.node);
               tspan.node.addAttribute(STYLE, theStyles);
               addStyles(tspanText, tspan.node);
               if (tspan.node.hasAttribute(BASELINE_SHIFT)) {
                  // http://www.svgbasics.com/font_effects_italic.html
                  // https://stackoverflow.com/questions/50295199/javafx-subscript-and-superscript-text-in-textflow
                  String shiftValue = tspan.node.getAttributeValue(BASELINE_SHIFT);
                  ParserUtils.setBaselineShift(tspanText, shiftValue);
               }
               // https://vanseodesign.com/web-design/svg-text-tspan-element/
               if (!ParserUtils.hasXPosition(tspan.node) && previous != null) {
                  double width = previous.text.getLayoutBounds().getWidth();
                  tspanText.setLayoutX(width + previous.text.getLayoutX());
               }
               previous = tspan;
            }
            group.getChildren().add(spanGroup.getTextGroup());
         }
      }
   }

   private void addStyles(Node node, XMLNode xmlNode) {
      setNodeStyle(node, xmlNode);
      setOpacity(node, xmlNode);
      setFilter(node, xmlNode);
      setTransform(node, xmlNode);
   }

   private void parseViewport(XMLNode xmlNode) {
      if (viewport == null) {
         double width = 0;
         double height = 0;
         if (xmlNode.hasAttribute(WIDTH) && xmlNode.hasAttribute(HEIGHT)) {
            width = xmlNode.getAttributeValueAsDouble(WIDTH, 0);
            height = xmlNode.getAttributeValueAsDouble(HEIGHT, 0);
         } else if (xmlNode.hasAttribute(VIEWBOX)) {
            String box = xmlNode.getAttributeValue(VIEWBOX);
            StringTokenizer tok = new StringTokenizer(box, " ,");
            if (tok.countTokens() >= 4) {
               tok.nextToken();
               tok.nextToken();
               width = ParserUtils.parseDoubleProtected(tok.nextToken());
               height = ParserUtils.parseDoubleProtected(tok.nextToken());
            }
         }
         viewport = new Viewport(width, height);
      }
   }

   private void manageSVGStyle(XMLNode xmlNode) {
      if (svgStyle == null) {
         String cdata = xmlNode.getCDATA();
         if (cdata != null) {
            svgStyle = SVGStyleBuilder.parseStyle(cdata, viewport);
         }
      }
   }

   private void buildDefs(XMLNode xmlNode) {
      buildNode(xmlNode, null, true);
   }

   private SpanGroup buildTSpanGroup(XMLNode xmlNode) {
      Group group = new Group();
      SpanGroup spanGroup = new SpanGroup(group);
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         Text text = null;
         String name = childNode.getName();
         switch (name) {
            case TSPAN:
               text = SVGShapeBuilder.buildText(childNode, viewport);
               break;
         }
         if (text != null) {
            group.getChildren().add(text);
            spanGroup.addTSpan(childNode, text);
         }
      }

      return spanGroup;
   }

   private Group buildGroup(XMLNode xmlNode) {
      Group group = new Group();
      buildNode(xmlNode, group);

      return group;
   }

   private void buildFilter(XMLNode xmlNode) {
      if (xmlNode.hasAttribute(ID)) {
         String id = xmlNode.getAttributeValue(ID);
         FilterSpec spec = new FilterSpec();
         filterSpecs.put(id, spec);
         buildFilterEffects(spec, xmlNode);
      }
   }

   private void buildFilterEffects(FilterSpec spec, XMLNode xmlNode) {
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         String name = childNode.getName();
         switch (name) {
            case FE_GAUSSIAN_BLUR:
               SVGShapeBuilder.buildFEGaussianBlur(spec, childNode);
               break;
            case FE_DROP_SHADOW:
               SVGShapeBuilder.buildFEDropShadow(spec, childNode, viewport);
               break;
            case FE_FLOOD:
               SVGShapeBuilder.buildFEFlood(spec, childNode, viewport);
               break;
            case FE_IMAGE:
               SVGShapeBuilder.buildFEImage(spec, url, childNode, viewport);
               break;
            case FE_OFFSET:
               SVGShapeBuilder.buildFEOffset(spec, childNode, viewport);
               break;
            case FE_COMPOSITE:
               SVGShapeBuilder.buildFEComposite(spec, childNode);
            case FE_MERGE:
               SVGShapeBuilder.buildFEMerge(spec, childNode);
               break;
            case FE_SPECULAR_LIGHTING:
               SVGShapeBuilder.buildFESpecularLighting(spec, childNode, viewport);
               break;
            case FE_DIFFUSE_LIGHTING:
               SVGShapeBuilder.buildFEDiffuseLighting(spec, childNode, viewport);
               break;
         }
      }
   }

   private void buildClipPath(XMLNode xmlNode) {
      if (xmlNode.hasAttribute(ID)) {
         String id = xmlNode.getAttributeValue(ID);
         clippingFactory.addClipSpec(id, xmlNode);
      }
   }

   private void setTransform(Node node, XMLNode xmlNode) {
      if (xmlNode.hasAttribute(TRANSFORM)) {
         String transforms = xmlNode.getAttributeValue(TRANSFORM);
         Transform transform = ParserUtils.extractTransform(transforms);
         if (transform != null) {
            node.getTransforms().add(transform);
         }
      }
   }

   private void setFilter(Node node, XMLNode xmlNode) {
      if (effectsSupported && xmlNode.hasAttribute(FILTER)) {
         Effect effect = expressFilter(node, xmlNode.getAttributeValue(FILTER));
         if (effect != null) {
            node.setEffect(effect);
         }
      }
   }

   private void setOpacity(Node node, XMLNode xmlNode) {
      if (xmlNode.hasAttribute(OPACITY)) {
         String opacityS = xmlNode.getAttributeValue(OPACITY);
         double opacity = ParserUtils.parseOpacity(opacityS);
         if (opacity >= 0) {
            node.setOpacity(opacity);
         }
      }
      if (xmlNode.hasAttribute(FILL_OPACITY) && node instanceof Shape) {
         String fillOpacityS = xmlNode.getAttributeValue(FILL_OPACITY);
         double fillOpacity = ParserUtils.parseOpacity(fillOpacityS);
         if (fillOpacity >= 0) {
            ParserUtils.setFillOpacity(node, fillOpacity);
         }
      }
   }

   private Effect expressFilter(Node node, String value) {
      Effect effect = ParserUtils.expressFilter(filterSpecs, node, value);
      return effect;
   }

   private void setStyleClass(Node node, String styleClasses) {
      StringTokenizer tok = new StringTokenizer(styleClasses, " ");
      while (tok.hasMoreTokens()) {
         String styleClass = tok.nextToken();
         node.getStyleClass().add(styleClass);
         if (svgStyle != null && svgStyle.hasRule(styleClass)) {
            Styles.Rule rule = svgStyle.getRule(styleClass);
            rule.apply(node);
         }
      }
   }

   private void setClipPath(Node node, String spec) {
      if (spec.startsWith("url(#")) {
         String clipID = ParserUtils.getURL(spec);
         if (clippingFactory.hasClip(clipID)) {
            Shape clipShape = clippingFactory.createClip(clipID, viewport);
            if (clipShape != null) {
               node.setClip(clipShape);
            }
         }
      }
   }

   private void setNodeStyle(Node node, XMLNode xmlNode) {
      if (node instanceof Shape) {
         Shape shape = (Shape) node;
         if (xmlNode.hasAttribute(FILL)) {
            shape.setFill(ParserUtils.expressPaint(gradients, xmlNode.getAttributeValue(FILL)));
         }

         if (xmlNode.hasAttribute(STROKE)) {
            shape.setStroke(ParserUtils.expressPaint(gradients, xmlNode.getAttributeValue(STROKE)));
         }

         if (xmlNode.hasAttribute(STROKE_WIDTH)) {
            double strokeWidth = xmlNode.getAttributeValueAsDouble(STROKE_WIDTH, 1);
            shape.setStrokeWidth(strokeWidth);
         }

         if (xmlNode.hasAttribute(STROKE_DASHARRAY)) {
            String dashArray = xmlNode.getAttributeValue(STROKE_DASHARRAY);
            applyDash(shape, dashArray);
         }

         if (xmlNode.hasAttribute(STROKE_DASHOFFSET)) {
            String dashOffset = xmlNode.getAttributeValue(STROKE_DASHOFFSET);
            double offset = LengthParser.parseLength(dashOffset);
            shape.setStrokeDashOffset(offset);
         }

         if (xmlNode.hasAttribute(STROKE_LINEJOIN)) {
            String lineJoin = xmlNode.getAttributeValue(STROKE_LINEJOIN);
            applyLineJoin(shape, lineJoin);
         }

         if (xmlNode.hasAttribute(STROKE_LINECAP)) {
            String lineCap = xmlNode.getAttributeValue(STROKE_LINECAP);
            applyLineCap(shape, lineCap);
         }

         if (xmlNode.hasAttribute(STROKE_MITERLIMIT)) {
            String miterLimit = xmlNode.getAttributeValue(STROKE_MITERLIMIT);
            applyMiterLimit(shape, miterLimit);
         }
      }

      if (xmlNode.hasAttribute(CLASS)) {
         String styleClasses = xmlNode.getAttributeValue(CLASS);
         setStyleClass(node, styleClasses);
      }

      if (xmlNode.hasAttribute(CLIP_PATH)) {
         String content = xmlNode.getAttributeValue(CLIP_PATH);
         setClipPath(node, content);
      }

      if (xmlNode.hasAttribute(STYLE)) {
         FontWeight fontWeight = FontWeight.NORMAL;
         FontPosture fontPosture = FontPosture.REGULAR;
         double fontSize = 12d;
         String fontFamily = null;
         String styles = xmlNode.getAttributeValue(STYLE);
         StringTokenizer tokenizer = new StringTokenizer(styles, ";");
         while (tokenizer.hasMoreTokens()) {
            String style = tokenizer.nextToken();

            StringTokenizer tokenizer2 = new StringTokenizer(style, ":");
            String styleName = tokenizer2.nextToken().trim();
            String styleValue = null;
            if (tokenizer2.hasMoreTokens()) {
               styleValue = tokenizer2.nextToken().trim();
            }
            if (styleValue == null) {
               continue;
            }

            switch (styleName) {
               case CLIP_PATH:
                  setClipPath(node, styleValue);
                  break;
               case FONT_FAMILY:
                  if (node instanceof Text) {
                     fontFamily = styleValue.replace("'", "");
                  }
                  break;
               case FONT_WEIGHT:
                  if (node instanceof Text) {
                     fontWeight = SVGShapeBuilder.getFontWeight(styleValue);
                  }
                  break;
               case TEXT_DECORATION:
                  if (node instanceof Text) {
                     SVGShapeBuilder.applyTextDecoration((Text) node, styleValue);
                  }
                  break;
               case FONT_STYLE:
                  if (node instanceof Text) {
                     fontPosture = SVGShapeBuilder.applyFontPosture((Text) node, styleValue);
                  }
                  break;
               case FONT_SIZE:
                  if (node instanceof Text) {
                     fontSize = ParserUtils.parseFontSize(styleValue);
                  }
                  break;
               case FILL:
                  if (node instanceof Shape) {
                     ((Shape) node).setFill(ParserUtils.expressPaint(gradients, styleValue));
                  }
                  break;
               case STROKE:
                  if (node instanceof Shape) {
                     ((Shape) node).setStroke(ParserUtils.expressPaint(gradients, styleValue));
                  }
                  break;
               case STROKE_WIDTH:
                  if (node instanceof Shape) {
                     double strokeWidth = LengthParser.parseLength(styleValue);
                     ((Shape) node).setStrokeWidth(strokeWidth);
                  }
                  break;
               case STROKE_DASHARRAY:
                  if (node instanceof Shape) {
                     applyDash(((Shape) node), styleValue);
                  }
                  break;
               case STROKE_DASHOFFSET:
                  if (node instanceof Shape) {
                     double offset = LengthParser.parseLength(styleValue);
                     ((Shape) node).setStrokeDashOffset(offset);
                  }
                  break;
               case STROKE_LINECAP:
                  if (node instanceof Shape) {
                     applyLineCap(((Shape) node), styleValue);
                  }
                  break;
               case STROKE_MITERLIMIT:
                  if (node instanceof Shape) {
                     applyMiterLimit(((Shape) node), styleValue);
                  }
                  break;
               case STROKE_LINEJOIN:
                  if (node instanceof Shape) {
                     applyLineJoin(((Shape) node), styleValue);
                  }
                  break;
               case OPACITY: {
                  double opacity = ParserUtils.parseOpacity(styleValue);
                  if (opacity >= 0) {
                     node.setOpacity(opacity);
                  }
               }
               break;
               case FILL_OPACITY: {
                  if (node instanceof Shape) {
                     double fillOpacity = ParserUtils.parseOpacity(styleValue);
                     if (fillOpacity >= 0) {
                        ParserUtils.setFillOpacity(node, fillOpacity);
                     }
                  }
               }
               break;
               case FILTER:
                  if (effectsSupported) {
                     Effect effect = expressFilter(node, styleValue);
                     if (effect != null) {
                        node.setEffect(effect);
                     }
                  }
                  break;
               default:
                  break;
            }
         }
         if (node instanceof Text) {
            Font font = Font.font(fontFamily, fontWeight, fontPosture, fontSize);
            ((Text) node).setFont(font);
         }
      }
   }

   private void applyMiterLimit(Shape shape, String styleValue) {
      try {
         double miterLimit = Double.parseDouble(styleValue);
         shape.setStrokeMiterLimit(miterLimit);
      } catch (NumberFormatException e) {
      }
   }

   private void applyLineCap(Shape shape, String styleValue) {
      StrokeLineCap linecap = StrokeLineCap.BUTT;
      if (styleValue.equals("round")) {
         linecap = StrokeLineCap.ROUND;
      } else if (styleValue.equals("square")) {
         linecap = StrokeLineCap.SQUARE;
      } else if (!styleValue.equals("butt")) {
         linecap = StrokeLineCap.BUTT;
      }
      shape.setStrokeLineCap(linecap);
   }

   private void applyLineJoin(Shape shape, String styleValue) {
      StrokeLineJoin linejoin = StrokeLineJoin.MITER;
      if (styleValue.equals("bevel")) {
         linejoin = StrokeLineJoin.BEVEL;
      } else if (styleValue.equals("round")) {
         linejoin = StrokeLineJoin.ROUND;
      } else if (!styleValue.equals("miter")) {
         linejoin = StrokeLineJoin.MITER;
      }
      shape.setStrokeLineJoin(linejoin);
   }

   private void applyDash(Shape shape, String styleValue) {
      ObservableList<Double> array = shape.getStrokeDashArray();
      List<Double> list = ParserUtils.parseDashArray(styleValue, viewport);
      if (list != null) {
         for (int i = 0; i < list.size(); i++) {
            array.add(list.get(i));
         }
      }
   }
}
