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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.girod.javafx.svgimage.xml.ClippingFactory;
import org.girod.javafx.svgimage.xml.FilterSpec;
import org.girod.javafx.svgimage.xml.LengthParser;
import org.girod.javafx.svgimage.xml.ParserUtils;
import org.girod.javafx.svgimage.xml.PercentParser;
import org.girod.javafx.svgimage.xml.SVGParsingException;
import org.girod.javafx.svgimage.xml.SVGShapeBuilder;
import org.girod.javafx.svgimage.xml.SVGStyleBuilder;
import org.girod.javafx.svgimage.xml.SVGTags;
import org.girod.javafx.svgimage.xml.Styles;
import org.girod.javafx.svgimage.xml.Viewport;
import org.girod.javafx.svgimage.xml.XMLNode;
import org.girod.javafx.svgimage.xml.XMLRoot;
import org.girod.javafx.svgimage.xml.XMLTreeHandler;
import org.xml.sax.SAXException;

/**
 * This class allows to load a svg file and convert it to an Image or a JavaFX tree.
 *
 * @version 0.4
 */
public class SVGLoader implements SVGTags {
   private static final Pattern TRANSFORM_PAT = Pattern.compile("\\w+\\((.*)\\)");
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
                  node = buildTSpanGroup(childNode);
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
                  buildLinearGradient(childNode);
                  break;
               }
            case RADIAL_GRADIENT:
               if (acceptDefs) {
                  buildRadialGradient(childNode);
                  break;
               }
            case FILTER:
               buildFilter(childNode);
               break;
         }
         if (node != null) {
            if (node instanceof Shape) {
               setShapeStyle((Shape) node, childNode);
            }

            setOpacity(node, childNode);
            setFilter(node, childNode);
            setTransform(node, childNode);

            group.getChildren().add(node);
         }
      }
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
               width = xmlNode.getAttributeValueAsDouble(tok.nextToken(), 0);
               height = xmlNode.getAttributeValueAsDouble(tok.nextToken(), 0);
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

   private Group buildTSpanGroup(XMLNode xmlNode) {
      Group group = new Group();
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         Node node = null;
         String name = childNode.getName();
         switch (name) {
            case TSPAN:
               node = SVGShapeBuilder.buildText(childNode, viewport);
               break;
         }
         if (node != null) {
            group.getChildren().add(node);
         }
      }

      return group;
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
            case FE_MERGE:
               SVGShapeBuilder.buildFEMerge(spec, childNode);
               break;
            case FE_SPECULAR_LIGHTING:
               SVGShapeBuilder.buildFESpecularLighting(spec, childNode, viewport);
               break;
         }
      }
   }

   private void buildRadialGradient(XMLNode xmlNode) {
      String id = null;
      Double fx = null;
      Double fy = null;
      Double cx = null;
      Double cy = null;
      Double r = null;
      Transform transform = null;
      String href = null;

      Iterator<String> it = xmlNode.getAttributes().keySet().iterator();
      while (it.hasNext()) {
         String attrname = it.next();
         switch (attrname) {
            case ID:
               id = xmlNode.getAttributeValue(attrname);
               break;
            case XLINK_HREF:
               href = xmlNode.getAttributeValue(attrname);
               if (href.startsWith("#")) {
                  href = href.substring(1);
               } else {
                  href = null;
               }
            case GRADIENT_UNITS:
               String gradientUnits = xmlNode.getAttributeValue(attrname);
               if (!gradientUnits.equals(USERSPACE_ON_USE)) {
                  return;
               }
               break;
            case FX:
               fx = PercentParser.parseValue(xmlNode, attrname);
               break;
            case FY:
               fy = PercentParser.parseValue(xmlNode, attrname);
               break;
            case CX:
               cx = PercentParser.parseValue(xmlNode, attrname);
               break;
            case CY:
               cy = PercentParser.parseValue(xmlNode, attrname);
               break;
            case R:
               r = PercentParser.parseValue(xmlNode, attrname);
               break;
            case GRADIENT_TRANSFORM:
               transform = extractTransform(xmlNode.getAttributeValue(attrname));
               break;
            default:
               break;
         }
      }

      GradientSpec spec = new GradientSpec();
      if (id != null) {
         gradientSpecs.put(id, spec);
      }
      List<GradientSpec.Stop> specstops = buildStops(spec, xmlNode, RADIAL_GRADIENT);
      if (specstops.isEmpty() && href != null && gradientSpecs.containsKey(href)) {
         specstops = gradientSpecs.get(href).getStops();
      }
      List<Stop> stops = convertStops(specstops);

      if (id != null && cx != null && cy != null && r != null) {
         double fDistance = 0.0;
         double fAngle = 0.0;

         if (transform != null && transform instanceof Affine) {
            double tempCx = cx;
            double tempCy = cy;
            double tempR = r;

            Affine affine = (Affine) transform;
            cx = tempCx * affine.getMxx() + tempCy * affine.getMxy() + affine.getTx();
            cy = tempCx * affine.getMyx() + tempCy * affine.getMyy() + affine.getTy();

            r = Math.sqrt(tempR * affine.getMxx() * tempR * affine.getMxx() + tempR * affine.getMyx() * tempR * affine.getMyx());

            if (fx != null && fy != null) {
               double tempFx = fx;
               double tempFy = fy;
               fx = tempFx * affine.getMxx() + tempFy * affine.getMxy() + affine.getTx();
               fy = tempFx * affine.getMyx() + tempFy * affine.getMyy() + affine.getTy();
            } else {
               fAngle = Math.asin(affine.getMyx()) * 180.0 / Math.PI;
               fDistance = Math.sqrt((cx - tempCx) * (cx - tempCx) + (cy - tempCy) * (cy - tempCy));
            }
         }

         if (fx != null && fy != null) {
            fDistance = Math.sqrt((fx - cx) * (fx - cx) + (fy - cy) * (fy - cy)) / r;
            fAngle = Math.atan2(cy - fy, cx - fx) * 180.0 / Math.PI;
         }

         RadialGradient gradient = new RadialGradient(fAngle, fDistance, cx, cy, r, true, CycleMethod.NO_CYCLE, stops);
         gradients.put(id, gradient);
      }
   }

   private void buildLinearGradient(XMLNode xmlNode) {
      String id = null;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      Transform transform = null;
      String href = null;

      Iterator<String> it = xmlNode.getAttributes().keySet().iterator();
      while (it.hasNext()) {
         String attrname = it.next();
         switch (attrname) {
            case ID:
               id = xmlNode.getAttributeValue(attrname);
               break;
            case XLINK_HREF:
               href = xmlNode.getAttributeValue(attrname);
               if (href.startsWith("#")) {
                  href = href.substring(1);
               } else {
                  href = null;
               }
               break;
            case GRADIENT_UNITS:
               String gradientUnits = xmlNode.getAttributeValue(attrname);
               if (!gradientUnits.equals(USERSPACE_ON_USE)) {
                  return;
               }
               break;
            case X1:
               x1 = xmlNode.getAttributeValueAsDouble(attrname);
               break;
            case Y1:
               y1 = xmlNode.getAttributeValueAsDouble(attrname);
               break;
            case X2:
               x2 = xmlNode.getAttributeValueAsDouble(attrname);
               break;
            case Y2:
               y2 = xmlNode.getAttributeValueAsDouble(attrname);
               break;
            case GRADIENT_TRANSFORM:
               transform = extractTransform(xmlNode.getAttributeValue(attrname));
               break;
            default:
               break;
         }
      }

      GradientSpec spec = new GradientSpec();
      if (id != null) {
         gradientSpecs.put(id, spec);
      }
      List<GradientSpec.Stop> specstops = buildStops(spec, xmlNode, LINEAR_GRADIENT);
      if (specstops.isEmpty() && href != null && gradientSpecs.containsKey(href)) {
         specstops = gradientSpecs.get(href).getStops();
      }
      List<Stop> stops = convertStops(specstops);

      if (id != null && x1 != 0 && y1 != 0 && x2 != 0 && y2 != 0) {
         if (transform != null && transform instanceof Affine) {
            double x1d = x1;
            double y1d = y1;
            double x2d = x2;
            double y2d = y2;
            Affine affine = (Affine) transform;
            x1 = x1d * affine.getMxx() + y1d * affine.getMxy() + affine.getTx();
            y1 = x1d * affine.getMyx() + y1d * affine.getMyy() + affine.getTy();
            x2 = x2d * affine.getMxx() + y2d * affine.getMxy() + affine.getTx();
            y2 = x2d * affine.getMyx() + y2d * affine.getMyy() + affine.getTy();
         }

         LinearGradient gradient = new LinearGradient(x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, stops);
         gradients.put(id, gradient);
      }
   }

   private List<Stop> convertStops(List<GradientSpec.Stop> specstops) {
      List<Stop> stops = new ArrayList<>();
      Iterator<GradientSpec.Stop> it = specstops.iterator();
      while (it.hasNext()) {
         GradientSpec.Stop theStop = it.next();
         Stop stop = new Stop(theStop.offset, theStop.color);
         stops.add(stop);
      }
      return stops;
   }

   private List<GradientSpec.Stop> buildStops(GradientSpec spec, XMLNode xmlNode, String kindOfGradient) {
      List<GradientSpec.Stop> stops = new ArrayList<>();
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         if (!childNode.getName().equals(STOP)) {
            continue;
         }
         double offset = 0d;
         String color = null;
         double opacity = 1.0;

         Iterator<String> it2 = childNode.getAttributes().keySet().iterator();
         while (it2.hasNext()) {
            String attrname = it2.next();
            switch (attrname) {
               case OFFSET:
                  offset = PercentParser.parseValue(childNode, attrname);
                  break;
               case STYLE:
                  String style = childNode.getAttributeValue(attrname);
                  StringTokenizer tokenizer = new StringTokenizer(style, ";");
                  while (tokenizer.hasMoreTokens()) {
                     String item = tokenizer.nextToken().trim();
                     if (item.startsWith("stop-color")) {
                        color = item.substring(11);
                     } else if (item.startsWith("stop-opacity")) {
                        opacity = ParserUtils.parseDoubleProtected(item.substring(13));
                     } else {
                     }
                  }
                  break;
               default:
                  break;
            }
         }

         if (color != null) {
            Color colour = Color.web(color, opacity);
            GradientSpec.Stop stop = spec.addStop(offset, opacity, colour);
            stops.add(stop);
         }
      }

      return stops;
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
         Transform transform = extractTransform(transforms);
         if (transform != null) {
            node.getTransforms().add(transform);
         }
      }
   }

   private List<Double> getArguments(String transformTxt) {
      List<Double> args = new ArrayList<>();
      Matcher m = TRANSFORM_PAT.matcher(transformTxt);
      if (m.matches()) {
         String content = m.group(1);
         String remaining = content;
         while (true) {
            int index = remaining.indexOf(' ');
            if (index == -1) {
               index = remaining.indexOf(',');
            }
            if (index == -1) {
               ParserUtils.parseDoubleArgument(args, remaining);
               break;
            } else {
               String beginning = remaining.substring(0, index);
               ParserUtils.parseDoubleArgument(args, beginning);
               remaining = remaining.substring(index + 1);
            }
         }
      } else {
         return null;
      }
      return args;
   }

   private Transform extractTransform(String transforms) {
      Transform transform = null;

      StringTokenizer tokenizer = new StringTokenizer(transforms, ")");

      while (tokenizer.hasMoreTokens()) {
         String transformTxt = tokenizer.nextToken() + ")";
         if (transformTxt.startsWith("translate(")) {
            List<Double> args = getArguments(transformTxt);
            if (args.size() == 2) {
               transform = Transform.translate(args.get(0), args.get(1));
            }
         } else if (transformTxt.startsWith("scale(")) {
            List<Double> args = getArguments(transformTxt);
            if (args.size() == 2) {
               transform = Transform.scale(args.get(0), args.get(1));
            }
         } else if (transformTxt.startsWith("rotate(")) {
            List<Double> args = getArguments(transformTxt);
            if (args.size() == 3) {
               transform = Transform.rotate(args.get(0), args.get(1), args.get(2));
            }
         } else if (transformTxt.startsWith("skewX(")) {
            List<Double> args = getArguments(transformTxt);
            if (args.size() == 1) {
               transform = Transform.shear(args.get(0), 1);
            }
         } else if (transformTxt.startsWith("skewY(")) {
            List<Double> args = getArguments(transformTxt);
            if (args.size() == 1) {
               transform = Transform.shear(1, args.get(0));
            }
         } else if (transformTxt.startsWith("matrix(")) {
            List<Double> args = getArguments(transformTxt);
            if (args.size() == 6) {
               transform = Transform.affine(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5));
            }
         }
      }

      return transform;
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
         double opacity = xmlNode.getAttributeValueAsDouble(OPACITY);
         node.setOpacity(opacity);
      }
   }

   private Effect expressFilter(Node node, String value) {
      Effect effect = ParserUtils.expressFilter(filterSpecs, node, value);
      return effect;
   }

   private Paint expressPaint(String value) {
      Paint paint = null;
      if (!value.equals("none")) {
         if (value.startsWith("url(#")) {
            String id = value.substring(5, value.length() - 1);
            paint = gradients.get(id);
         } else {
            paint = Color.web(value);
         }
      }

      return paint;
   }

   private void setStyleClass(Shape shape, String styleClasses) {
      StringTokenizer tok = new StringTokenizer(styleClasses, " ");
      while (tok.hasMoreTokens()) {
         String styleClass = tok.nextToken();
         shape.getStyleClass().add(styleClass);
         if (svgStyle != null && svgStyle.hasRule(styleClass)) {
            Styles.Rule rule = svgStyle.getRule(styleClass);
            rule.apply(shape);
         }
      }
   }

   private void setClipPath(Shape shape, String spec) {
      if (spec.startsWith("url(#")) {
         String clipID = spec.substring(5, spec.length() - 1);
         if (clippingFactory.hasClip(clipID)) {
            Shape clipShape = clippingFactory.createClip(clipID, viewport);
            if (clipShape != null) {
               shape.setClip(clipShape);
            }
         }
      }
   }

   private void setShapeStyle(Shape shape, XMLNode xmlNode) {
      if (xmlNode.hasAttribute(FILL)) {
         shape.setFill(expressPaint(xmlNode.getAttributeValue(FILL)));
      }

      if (xmlNode.hasAttribute(STROKE)) {
         shape.setStroke(expressPaint(xmlNode.getAttributeValue(STROKE)));
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

      if (xmlNode.hasAttribute(CLASS)) {
         String styleClasses = xmlNode.getAttributeValue(CLASS);
         setStyleClass(shape, styleClasses);
      }

      if (xmlNode.hasAttribute(CLIP_PATH)) {
         String content = xmlNode.getAttributeValue(CLIP_PATH);
         setClipPath(shape, content);
      }

      if (xmlNode.hasAttribute(STYLE)) {
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
                  setClipPath(shape, styleValue);
                  break;
               case FILL:
                  shape.setFill(expressPaint(styleValue));
                  break;
               case STROKE:
                  shape.setStroke(expressPaint(styleValue));
                  break;
               case STROKE_WIDTH:
                  double strokeWidth = LengthParser.parseLength(styleValue);
                  shape.setStrokeWidth(strokeWidth);
                  break;
               case STROKE_DASHARRAY:
                  applyDash(shape, styleValue);
                  break;
               case STROKE_DASHOFFSET:
                  double offset = LengthParser.parseLength(styleValue);
                  shape.setStrokeDashOffset(offset);
                  break;
               case STROKE_LINECAP:
                  applyLineCap(shape, styleValue);
                  break;
               case STROKE_MITERLIMIT:
                  applyMiterLimit(shape, styleValue);
                  break;
               case STROKE_LINEJOIN:
                  applyLineJoin(shape, styleValue);
                  break;
               case OPACITY:
                  try {
                     double opacity = Double.parseDouble(styleValue);
                     shape.setOpacity(opacity);
                  } catch (NumberFormatException e) {
                  }
                  break;
               case FILTER:
                  if (effectsSupported) {
                     Effect effect = expressFilter(shape, styleValue);
                     if (effect != null) {
                        shape.setEffect(effect);
                     }
                  }
                  break;
               default:
                  break;
            }
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
      StringTokenizer tokenizer = new StringTokenizer(styleValue, " ,");
      while (tokenizer.hasMoreTokens()) {
         String value = tokenizer.nextToken();
         array.add(ParserUtils.parseDoubleProtected(value, true, viewport));
      }
   }
}
