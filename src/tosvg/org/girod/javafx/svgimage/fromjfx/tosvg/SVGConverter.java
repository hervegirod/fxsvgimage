/*
Copyright (c) 2022, 2025, 2026 Hervé Girod
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
package org.girod.javafx.svgimage.fromjfx.tosvg;

import org.girod.javafx.svgimage.fromjfx.ConverterParameters;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.girod.javafx.svgimage.fromjfx.AbstractJFXConverter;
import org.girod.javafx.svgimage.fromjfx.JFXConverterException;
import org.girod.javafx.svgimage.fromjfx.tosvg.converters.SVGConverterDelegate;
import org.girod.javafx.svgimage.fromjfx.tosvg.xml.XMLNode;
import org.girod.javafx.svgimage.fromjfx.tosvg.xml.XMLNodeUtilities;
import org.girod.javafx.svgimage.fromjfx.tosvg.xml.XMLRoot;

/**
 * The JavaFX to SVG converter.
 *
 * @version 1.8
 */
public class SVGConverter extends AbstractJFXConverter<SVGConverterDelegate> {
   private File file = null;

   /**
    * Create a SVG converter.
    */
   public SVGConverter() {
      super();
      this.delegate = new SVGConverterDelegate(defaultParams);
   }

   /**
    * Create a SVG converter.
    *
    * @param defaultParams the default parameters
    */
   public SVGConverter(ConverterParameters defaultParams) {
      super();
      this.delegate = new SVGConverterDelegate(defaultParams);
   }

   /**
    * Set the output file.
    *
    * @param file the output file
    */
   public void setFile(File file) {
      this.file = file;
   }

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @throws JFXConverterException if writing the SVG fails
    */
   @Override
   public void convert(Node root) throws JFXConverterException {
      if (file == null) {
         throw new JFXConverterException("Output SVG file is null");
      }
      convert(root, file, defaultParams);
   }

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @param file the file
    * @throws JFXConverterException if writing the SVG fails
    */
   public void convert(Node root, File file) throws JFXConverterException {
      convert(root, file, defaultParams);
   }

   private String encodeBackground(Color color) {
      int r = (int) (255 * color.getRed());
      int g = (int) (255 * color.getGreen());
      int b = (int) (255 * color.getBlue());
      return "rgb(" + r + "," + g + "," + b + ")";
   }

   private void addRootAttributes(Node root, XMLRoot xmlRoot, ConverterParameters params) {
      double width;
      double height;
      Bounds bounds = null;
      if (params.width > 0) {
         width = params.width;
         xmlRoot.addAttribute("width", params.width);
      } else {
         bounds = root.getBoundsInLocal();
         width = bounds.getMaxX();
         if (params.insets > 0) {
            width += params.insets * 2;
         }
         xmlRoot.addAttribute("width", width);
      }
      if (params.height > 0) {
         height = params.height;
         xmlRoot.addAttribute("height", params.height);
      } else {
         if (bounds == null) {
            bounds = root.getBoundsInLocal();
         }
         height = bounds.getMaxY();
         if (params.insets > 0) {
            height += params.insets * 2;
         }
         xmlRoot.addAttribute("height", height);
      }
      if (params.hasViewbox) {
         String viewBox = "0 0 " + width + " " + height;
         xmlRoot.addAttribute("viewBox", viewBox);
      }
      if (params.background != null) {
         XMLNode rectNode = new XMLNode("rect");
         rectNode.addAttribute("width", "100%");
         rectNode.addAttribute("height", "100%");
         String background = encodeBackground(params.background);
         rectNode.addAttribute("fill", background);
         xmlRoot.addChild(rectNode);
      }
   }

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @param params the conversion parameters
    * @throws JFXConverterException if writing the SVG fails
    */
   @Override
   public void convert(Node root, ConverterParameters params) throws JFXConverterException {
      if (file == null) {
         throw new JFXConverterException("Output SVG file is null");
      }
      convert(root, file, params);
   }

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @param file the file
    * @param params the conversion parameters
    * @throws JFXConverterException if writing the SVG fails
    */
   public void convert(Node root, File file, ConverterParameters params) throws JFXConverterException {
      delegate.setSVGFile(file);
      if (params == null) {
         params = defaultParams;
      }
      XMLRoot xmlRoot = new XMLRoot("svg");
      addRootAttributes(root, xmlRoot, params);
      delegate.convertRoot(root, xmlRoot, params);

      try {
         XMLNodeUtilities.print(xmlRoot, 2, file);
      } catch (IOException e) {
         throw new JFXConverterException(e);
      }
   }

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @param url the url
    * @throws JFXConverterException if writing the SVG fails
    */
   public void convert(Node root, URL url) throws JFXConverterException {
      convert(root, url, defaultParams);
   }

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @param url the url
    * @param params the conversion parameters
    * @throws JFXConverterException if writing the SVG fails
    */
   public void convert(Node root, URL url, ConverterParameters params) throws JFXConverterException {
      delegate.setSVGFile(new File(url.getFile()));
      if (params == null) {
         params = defaultParams;
      }
      XMLRoot xmlRoot = new XMLRoot("svg");
      addRootAttributes(root, xmlRoot, params);
      delegate.convertRoot(root, xmlRoot, params);

      try {
         XMLNodeUtilities.print(xmlRoot, 2, url);
      } catch (IOException ex) {
         throw new JFXConverterException(ex);
      }
   }
}
