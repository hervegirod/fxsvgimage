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
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.girod.javafx.svgimage.xml.ClippingFactory;
import org.girod.javafx.svgimage.xml.FilterSpec;
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
 * @version 0.5.5
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
   private Map<String, XMLNode> namedNodes = new HashMap<>();
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

   private void addNamedNode(XMLNode xmlNode, Node node) {
      if (node != null && xmlNode.hasAttribute(ID)) {
         String id = xmlNode.getAttributeValue(ID);
         namedNodes.put(id, xmlNode);
      }
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
               node = SVGShapeBuilder.buildRect(childNode, null, viewport);
               addNamedNode(childNode, node);
               break;
            case CIRCLE:
               node = SVGShapeBuilder.buildCircle(childNode, null, viewport);
               addNamedNode(childNode, node);
               break;
            case ELLIPSE:
               node = SVGShapeBuilder.buildEllipse(childNode, null, viewport);
               addNamedNode(childNode, node);
               break;
            case PATH:
               node = SVGShapeBuilder.buildPath(childNode, null, viewport);
               addNamedNode(childNode, node);
               break;
            case POLYGON:
               node = SVGShapeBuilder.buildPolygon(childNode, null, viewport);
               addNamedNode(childNode, node);
               break;
            case LINE:
               node = SVGShapeBuilder.buildLine(childNode, null, viewport);
               addNamedNode(childNode, node);
               break;
            case POLYLINE:
               node = SVGShapeBuilder.buildPolyline(childNode, null, viewport);
               addNamedNode(childNode, node);
               break;
            case USE:
               node = SVGShapeBuilder.buildUse(childNode, namedNodes, gradients, svgStyle, effectsSupported, filterSpecs, null, viewport);
               break;
            case TEXT:
               node = SVGShapeBuilder.buildText(childNode, null, viewport);
               if (node == null) {
                  spanGroup = SVGShapeBuilder.buildTSpanGroup(childNode, null, viewport);
                  addNamedNode(childNode, spanGroup.getTextGroup());
               } else {
                  addNamedNode(childNode, node);
               }
               break;
            case IMAGE:
               node = SVGShapeBuilder.buildImage(childNode, url, null, viewport);
               addNamedNode(childNode, node);
               break;
            case SVG:
               parseViewport(childNode);
               node = buildGroup(childNode);
               break;
            case G:
               node = buildGroup(childNode);
               addNamedNode(childNode, node);
               break;
            case DEFS:
               if (!acceptDefs) {
                  buildDefs(childNode);
                  break;
               }
            case CLIP_PATH_SPEC:
               buildClipPath(childNode);
               break;
            case LINEAR_GRADIENT:
               if (acceptDefs) {
                  SVGShapeBuilder.buildLinearGradient(gradientSpecs, gradients, childNode, viewport);
                  break;
               }
            case RADIAL_GRADIENT:
               if (acceptDefs) {
                  SVGShapeBuilder.buildRadialGradient(gradientSpecs, gradients, childNode, viewport);
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
      ParserUtils.setOpacity(node, xmlNode);
      setFilter(node, xmlNode);
      ParserUtils.setTransform(node, xmlNode, viewport);
   }

   private void parseViewport(XMLNode xmlNode) {
      if (viewport == null) {
         double width = 0;
         double height = 0;
         if (xmlNode.hasAttribute(WIDTH) && xmlNode.hasAttribute(HEIGHT)) {
            width = xmlNode.getLengthValue(WIDTH, 0);
            height = xmlNode.getLengthValue(HEIGHT, 0);
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

   private void setFilter(Node node, XMLNode xmlNode) {
      if (effectsSupported && xmlNode.hasAttribute(FILTER)) {
         Effect effect = expressFilter(node, xmlNode.getAttributeValue(FILTER));
         if (effect != null) {
            node.setEffect(effect);
         }
      }
   }

   private Effect expressFilter(Node node, String value) {
      Effect effect = ParserUtils.expressFilter(filterSpecs, node, value);
      return effect;
   }

   private void setNodeStyle(Node node, XMLNode xmlNode) {
      SVGStyleBuilder.setNodeStyle(node, gradients, xmlNode, clippingFactory, svgStyle,
         effectsSupported, filterSpecs, viewport);
   }
}
