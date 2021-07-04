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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.girod.javafx.svgimage.xml.PercentParser;
import org.girod.javafx.svgimage.xml.LengthParser;
import org.girod.javafx.svgimage.xml.XMLNode;
import org.girod.javafx.svgimage.xml.XMLRoot;
import org.girod.javafx.svgimage.xml.XMLTreeHandler;
import org.xml.sax.SAXException;

/**
 * This class allows to load a svg file and convert it to an Image or a JavaFX tree.
 *
 * @version 0.2
 */
public class SVGLoader {
   private static final Pattern TRANSFORM = Pattern.compile("\\w+\\((.*)\\)");
   private final URL url;
   private final SVGImage root;
   private final Map<String, GradientSpec> gradientSpecs = new HashMap<>();
   private final Map<String, Paint> gradients = new HashMap<>();
   private final Map<String, Shape> clips = new HashMap<>();

   private SVGLoader(URL url) {
      this.url = url;
      this.root = new SVGImage();
   }

   /**
    * Load a svg File.
    *
    * @param file the file
    * @return the SVGImage
    * @throws IOException
    */
   public static SVGImage load(File file) throws IOException {
      URL url = file.toURI().toURL();
      return load(url);
   }

   /**
    * Load a svg URL.
    *
    * @param url the URL
    * @return the SVGImage
    * @throws IOException
    */
   public static SVGImage load(URL url) throws IOException {
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
    * @throws IOException
    */
   public static SVGImage load(File file, String styleSheets) throws IOException {
      URL url = file.toURI().toURL();
      return load(url, styleSheets);
   }

   /**
    * Load a svg URL, and set the styleSheets of the associated JavaFX Node.
    *
    * @param url the URL
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws IOException
    */
   public static SVGImage load(URL url, String styleSheets) throws IOException {
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
    * @throws IOException
    */
   public static SVGImage loadScaled(File file, double scale) throws IOException {
      URL url = file.toURI().toURL();
      return loadScaled(url, scale);
   }

   /**
    * Load a svg URL, and scale the associated JavaFX Node.
    *
    * @param url the URL
    * @param scale the scale
    * @return the SVGImage
    * @throws IOException
    */
   public static SVGImage loadScaled(URL url, double scale) throws IOException {
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
    * @throws IOException
    */
   public static SVGImage load(File file, double width) throws IOException {
      URL url = file.toURI().toURL();
      return load(url, width);
   }

   /**
    * Load a svg URL, and set the resulting width the associated JavaFX Node.
    *
    * @param url the URL
    * @param width the resulting width
    * @return the SVGImage
    * @throws IOException
    */
   public static SVGImage load(URL url, double width) throws IOException {
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
    * @throws IOException
    */
   public static SVGImage load(File file, double width, String styleSheets) throws IOException {
      URL url = file.toURI().toURL();
      return load(url, width, styleSheets);
   }

   /**
    * Load a svg URL, set the styleSheets and set the resulting width of the associated JavaFX Node.
    *
    * @param url the URL
    * @param width the resulting width
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws IOException
    */
   public static SVGImage load(URL url, double width, String styleSheets) throws IOException {
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
    * @throws IOException
    */
   public static SVGImage loadScaled(File file, double scale, String styleSheets) throws IOException {
      URL url = file.toURI().toURL();
      return loadScaled(url, scale, styleSheets);
   }

   /**
    * Load a svg URL, set the styleSheets and scale of the associated JavaFX Node.
    *
    * @param url the URL
    * @param scale the scale
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws IOException
    */
   public static SVGImage loadScaled(URL url, double scale, String styleSheets) throws IOException {
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
    * @throws IOException
    */
   public static SVGImage load(URL url, LoaderParameters params) throws IOException {
      SVGLoader loader = new SVGLoader(url);
      SVGImage img = loader.loadImpl();
      if (params.styleSheets != null) {
         img.getStylesheets().add(params.styleSheets);
      }
      if (params.scale > 0) {
         img.setScaleX(params.scale);
         img.setScaleY(params.scale);
      } else if (params.width > 0) {
         double initialWidth = img.getLayoutBounds().getWidth();
         double scaleX = params.width / initialWidth;
         img.setScaleX(scaleX);
         img.setScaleY(scaleX);
      }

      return img;
   }

   private SVGImage loadImpl() throws IOException {
      SAXParserFactory saxfactory = SAXParserFactory.newInstance();
      try {
         SAXParser parser = saxfactory.newSAXParser();
         XMLTreeHandler handler = new XMLTreeHandler();
         parser.parse(url.openStream(), handler);
         return walk(handler.getRoot());
      } catch (ParserConfigurationException | SAXException ex) {
         return null;
      }
   }

   private SVGImage walk(XMLRoot xmlRoot) {
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
            case "rect":
               node = buildRect(childNode);
               break;
            case "circle":
               node = buildCircle(childNode);
               break;
            case "ellipse":
               node = buildEllipse(childNode);
               break;
            case "path":
               node = buildPath(childNode);
               break;
            case "polygon":
               node = buildPolygon(childNode);
               break;
            case "line":
               node = buildLine(childNode);
               break;
            case "polyline":
               node = buildPolyline(childNode);
               break;
            case "text":
               node = buildText(childNode);
               if (node == null) {
                  node = buildTSpanGroup(childNode);
               }
               break;
            case "image":
               node = buildImage(childNode);
               break;
            case "svg":
            case "g":
               node = buildGroup(childNode);
               break;
            case "defs":
               if (!acceptDefs) {
                  buildDefs(childNode);
                  break;
               }
            case "clipPath":
               if (acceptDefs) {
                  buildClipPath(childNode);
                  break;
               }
            case "linearGradient":
               if (acceptDefs) {
                  buildLinearGradient(childNode);
                  break;
               }
            case "radialGradient":
               if (acceptDefs) {
                  buildRadialGradient(childNode);
                  break;
               }
         }
         if (node != null) {
            if (node instanceof Shape) {
               setShapeStyle((Shape) node, childNode);
            }

            setOpacity(node, childNode);
            setTransform(node, childNode);

            group.getChildren().add(node);
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
            case "tspan":
               node = buildText(childNode);
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
            case "id":
               id = xmlNode.getAttributeValue(attrname);
               break;
            case "xlink:href":
               href = xmlNode.getAttributeValue(attrname);
               if (href.startsWith("#")) {
                  href = href.substring(1);
               } else {
                  href = null;
               }
            case "gradientUnits":
               String gradientUnits = xmlNode.getAttributeValue(attrname);
               if (!gradientUnits.equals("userSpaceOnUse")) {
                  return;
               }
               break;
            case "fx":
               fx = PercentParser.parseValue(xmlNode, attrname);
               break;
            case "fy":
               fy = PercentParser.parseValue(xmlNode, attrname);
               break;
            case "cx":
               cx = PercentParser.parseValue(xmlNode, attrname);
               break;
            case "cy":
               cy = PercentParser.parseValue(xmlNode, attrname);
               break;
            case "r":
               r = PercentParser.parseValue(xmlNode, attrname);
               break;
            case "gradientTransform":
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
      List<GradientSpec.Stop> specstops = buildStops(spec, xmlNode, "radialGradient");
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
            case "id":
               id = xmlNode.getAttributeValue(attrname);
               break;
            case "xlink:href":
               href = xmlNode.getAttributeValue(attrname);
               if (href.startsWith("#")) {
                  href = href.substring(1);
               } else {
                  href = null;
               }
               break;
            case "gradientUnits":
               String gradientUnits = xmlNode.getAttributeValue(attrname);
               if (!gradientUnits.equals("userSpaceOnUse")) {
                  return;
               }
               break;
            case "x1":
               x1 = xmlNode.getAttributeValueAsDouble(attrname);
               break;
            case "y1":
               y1 = xmlNode.getAttributeValueAsDouble(attrname);
               break;
            case "x2":
               x2 = xmlNode.getAttributeValueAsDouble(attrname);
               break;
            case "y2":
               y2 = xmlNode.getAttributeValueAsDouble(attrname);
               break;
            case "gradientTransform":
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
      List<GradientSpec.Stop> specstops = buildStops(spec, xmlNode, "linearGradient");
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

   private double parseDoubleProtected(String valueS) {
      try {
         return Double.parseDouble(valueS);
      } catch (NumberFormatException e) {
         return 0;
      }
   }

   private List<GradientSpec.Stop> buildStops(GradientSpec spec, XMLNode xmlNode, String kindOfGradient) {
      List<GradientSpec.Stop> stops = new ArrayList<>();
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         if (!childNode.getName().equals("stop")) {
            continue;
         }
         double offset = 0d;
         String color = null;
         double opacity = 1.0;

         Iterator<String> it2 = childNode.getAttributes().keySet().iterator();
         while (it2.hasNext()) {
            String attrname = it2.next();
            switch (attrname) {
               case "offset":
                  offset = PercentParser.parseValue(childNode, attrname);
                  break;
               case "style":
                  String style = childNode.getAttributeValue(attrname);
                  StringTokenizer tokenizer = new StringTokenizer(style, ";");
                  while (tokenizer.hasMoreTokens()) {
                     String item = tokenizer.nextToken().trim();
                     if (item.startsWith("stop-color")) {
                        color = item.substring(11);
                     } else if (item.startsWith("stop-opacity")) {
                        opacity = parseDoubleProtected(item.substring(13));
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

   private Shape buildRect(XMLNode xmlNode) {
      double x = 0.0;
      double y = 0.0;

      if (xmlNode.hasAttribute("x")) {
         x = xmlNode.getAttributeValueAsDouble("x");
      }
      if (xmlNode.hasAttribute("y")) {
         y = xmlNode.getAttributeValueAsDouble("y");
      }
      Rectangle rect = new Rectangle(x, y, xmlNode.getAttributeValueAsDouble("width", 0), xmlNode.getAttributeValueAsDouble("height", 0));
      return rect;
   }

   private Shape buildCircle(XMLNode xmlNode) {
      Circle circle = new Circle(xmlNode.getAttributeValueAsDouble("cx", 0), xmlNode.getAttributeValueAsDouble("cy", 0), xmlNode.getAttributeValueAsDouble("r", 0));

      return circle;
   }

   private Shape buildEllipse(XMLNode xmlNode) {
      Ellipse ellipse = new Ellipse(xmlNode.getAttributeValueAsDouble("cx", 0),
         xmlNode.getAttributeValueAsDouble("cy", 0),
         xmlNode.getAttributeValueAsDouble("rx", 0),
         xmlNode.getAttributeValueAsDouble("ry", 0));

      return ellipse;
   }

   private Shape buildPath(XMLNode xmlNode) {
      SVGPath path = new SVGPath();
      path.setContent(xmlNode.getAttributeValue("d"));

      return path;
   }

   private Shape buildPolygon(XMLNode node) {
      String pointsAttribute = node.getAttributeValue("points");
      Polygon polygon = new Polygon();

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");
      while (tokenizer.hasMoreTokens()) {
         String point = tokenizer.nextToken();

         StringTokenizer tokenizer2 = new StringTokenizer(point, ",");
         Double x = Double.valueOf(tokenizer2.nextToken());
         Double y = Double.valueOf(tokenizer2.nextToken());

         polygon.getPoints().add(x);
         polygon.getPoints().add(y);
      }

      return polygon;
   }

   private Shape buildLine(XMLNode xmlNode) {
      if (xmlNode.hasAttribute("x1") && xmlNode.hasAttribute("y1") && xmlNode.hasAttribute("x2") && xmlNode.hasAttribute("y2")) {
         double x1 = xmlNode.getAttributeValueAsDouble("x1");
         double y1 = xmlNode.getAttributeValueAsDouble("y1");
         double x2 = xmlNode.getAttributeValueAsDouble("x2");
         double y2 = xmlNode.getAttributeValueAsDouble("y2");

         return new Line(x1, y1, x2, y2);
      } else {
         return null;
      }
   }

   private Shape buildPolyline(XMLNode xmlNode) {
      Polyline polyline = new Polyline();
      String pointsAttribute = xmlNode.getAttributeValue("points");

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");
      while (tokenizer.hasMoreTokens()) {
         String points = tokenizer.nextToken();
         StringTokenizer tokenizer2 = new StringTokenizer(points, ",");
         double x = parseDoubleProtected(tokenizer2.nextToken());
         double y = parseDoubleProtected(tokenizer2.nextToken());
         polyline.getPoints().add(x);
         polyline.getPoints().add(y);
      }

      return polyline;
   }

   private void buildClipPath(XMLNode xmlNode) {
      if (xmlNode.hasAttribute("id")) {
         String id = xmlNode.getAttributeValue("id");
         Shape theShape = null;
         Iterator<XMLNode> it = xmlNode.getChildren().iterator();
         while (it.hasNext()) {
            XMLNode childNode = it.next();
            Shape shape = null;
            String name = childNode.getName();
            switch (name) {
               case "circle":
                  shape = buildCircle(childNode);
                  break;
               case "path":
                  shape = buildPath(childNode);
                  break;
               case "ellipse":
                  shape = buildEllipse(childNode);
                  break;
               case "rect":
                  shape = buildRect(childNode);
                  break;
            }
            if (theShape == null) {
               theShape = shape;
            } else {
               theShape = Shape.union(theShape, shape);
            }
         }
         if (theShape != null) {
            clips.put(id, theShape);
         }
      }
   }

   private Text buildText(XMLNode xmlNode) {
      Font font = null;
      if (xmlNode.hasAttribute("font-family") && xmlNode.hasAttribute("font-size")) {
         font = Font.font(xmlNode.getAttributeValue("font-family").replace("'", ""),
            xmlNode.getAttributeValueAsDouble("font-size"));
      }

      String cdata = xmlNode.getCDATA();
      if (cdata != null) {
         Text text = new Text(cdata);
         if (font != null) {
            text.setFont(font);
         }

         return text;
      } else {
         return null;
      }
   }

   private ImageView buildImage(XMLNode xmlNode) {
      double width = xmlNode.getAttributeValueAsDouble("width", 0);
      double height = xmlNode.getAttributeValueAsDouble("height", 0);
      String hrefAttribute = xmlNode.getAttributeValue("href");

      URL imageUrl = null;
      try {
         imageUrl = new URL(hrefAttribute);
      } catch (MalformedURLException ex) {
         try {
            imageUrl = new URL(url, hrefAttribute);
         } catch (MalformedURLException ex1) {
         }
      }
      if (imageUrl != null) {
         Image image = new Image(imageUrl.toString(), width, height, true, true);
         return new ImageView(image);
      }

      return null;
   }

   private void setTransform(Node node, XMLNode xmlNode) {
      if (xmlNode.hasAttribute("transform")) {
         String transforms = xmlNode.getAttributeValue("transform");
         Transform transform = extractTransform(transforms);
         node.getTransforms().add(transform);
      }
   }

   private List<Double> getArguments(String transformTxt) {
      List<Double> args = new ArrayList<>();
      Matcher m = TRANSFORM.matcher(transformTxt);
      if (m.matches()) {
         String content = m.group(1);
         String remaining = content;
         while (true) {
            int index = remaining.indexOf(' ');
            if (index == -1) {
               index = remaining.indexOf(',');
            }
            if (index == -1) {
               try {
                  double value = Double.parseDouble(remaining);
                  args.add(value);
               } catch (NumberFormatException e) {
               }
               break;
            } else {
               try {
                  String beginning = remaining.substring(0, index);
                  double value = Double.parseDouble(beginning);
                  args.add(value);
               } catch (NumberFormatException e) {
               }
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
               transform = Transform.rotate(args.get(0), args.get(1), args.get(3));
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

   private void setOpacity(Node node, XMLNode xmlNode) {
      if (xmlNode.hasAttribute("opacity")) {
         double opacity = xmlNode.getAttributeValueAsDouble("opacity");
         node.setOpacity(opacity);
      }
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
         String tk = tok.nextToken();
         shape.getStyleClass().add(tk);
      }
   }

   private void setShapeStyle(Shape shape, XMLNode xmlNode) {
      if (xmlNode.hasAttribute("fill")) {
         shape.setFill(expressPaint(xmlNode.getAttributeValue("fill")));
      }

      if (xmlNode.hasAttribute("stroke")) {
         shape.setFill(expressPaint(xmlNode.getAttributeValue("stroke")));
      }

      if (xmlNode.hasAttribute("stroke-width")) {
         double strokeWidth = xmlNode.getAttributeValueAsDouble("stroke-width", 1);
         shape.setStrokeWidth(strokeWidth);
      }

      if (xmlNode.hasAttribute("class")) {
         String styleClasses = xmlNode.getAttributeValue("class");
         setStyleClass(shape, styleClasses);
      }

      if (xmlNode.hasAttribute("style")) {
         String styles = xmlNode.getAttributeValue("style");
         StringTokenizer tokenizer = new StringTokenizer(styles, ";");
         while (tokenizer.hasMoreTokens()) {
            String style = tokenizer.nextToken();

            StringTokenizer tokenizer2 = new StringTokenizer(style, ":");
            String styleName = tokenizer2.nextToken().trim();
            String styleValue = null;
            if (tokenizer.hasMoreTokens()) {
               styleValue = tokenizer2.nextToken().trim();
            }
            if (styleValue == null) {
               continue;
            }

            switch (styleName) {
               case "clip-path":
                  if (styleValue.startsWith("url(#")) {
                     String clipID = styleValue.substring(5, styleValue.length() - 1);
                     if (clips.containsKey(clipID)) {
                        shape.setClip(clips.get(clipID));
                     }
                  }
                  break;
               case "fill":
                  shape.setFill(expressPaint(styleValue));
                  break;
               case "stroke":
                  shape.setStroke(expressPaint(styleValue));
                  break;
               case "stroke-width":
                  double strokeWidth = LengthParser.parseLength(styleValue);
                  shape.setStrokeWidth(strokeWidth);
                  break;
               case "stroke-linecap":
                  StrokeLineCap linecap = StrokeLineCap.BUTT;
                  if (styleValue.equals("round")) {
                     linecap = StrokeLineCap.ROUND;
                  } else if (styleValue.equals("square")) {
                     linecap = StrokeLineCap.SQUARE;
                  } else if (!styleValue.equals("butt")) {
                  }

                  shape.setStrokeLineCap(linecap);
                  break;
               case "stroke-miterlimit":
                  try {
                     double miterLimit = Double.parseDouble(styleValue);
                     shape.setStrokeMiterLimit(miterLimit);
                  } catch (NumberFormatException e) {
                  }
                  break;
               case "stroke-linejoin":
                  StrokeLineJoin linejoin = StrokeLineJoin.MITER;
                  if (styleValue.equals("bevel")) {
                     linejoin = StrokeLineJoin.BEVEL;
                  } else if (styleValue.equals("round")) {
                     linejoin = StrokeLineJoin.ROUND;
                  } else if (!styleValue.equals("miter")) {
                  }

                  shape.setStrokeLineJoin(linejoin);
                  break;
               case "opacity":
                  try {
                     double opacity = Double.parseDouble(styleValue);
                     shape.setOpacity(opacity);
                  } catch (NumberFormatException e) {
                  }
                  break;
               default:
                  break;
            }
         }
      }
   }
}
