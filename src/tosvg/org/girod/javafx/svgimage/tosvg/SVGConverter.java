/*
Copyright (c) 2022, 2025 Hervé Girod
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
package org.girod.javafx.svgimage.tosvg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.girod.javafx.svgimage.tosvg.converters.ConverterDelegate;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;
import org.girod.javafx.svgimage.tosvg.xml.XMLNodeUtilities;
import org.girod.javafx.svgimage.tosvg.xml.XMLRoot;

/**
 * The JavaFX to SVg converter.
 *
 * @version 1.2
 */
public class SVGConverter {
   private ConverterDelegate delegate = null;
   private ConverterParameters params = null;

   /**
    * Create a SVG converter.
    */
   public SVGConverter() {
      delegate = new ConverterDelegate();
   }

   /**
    * Return the ConverterDelegate.
    *
    * @return the ConverterDelegate
    */
   public ConverterDelegate getConverterDelegate() {
      return delegate;
   }

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @param file the file
    * @throws IOException if writing the SVG fails
    */
   public void convert(Node root, File file) throws IOException {
      convert(root, file, new ConverterParameters());
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
      if (params.width > 0) {
         width = params.width;
         xmlRoot.addAttribute("width", params.width);
      } else {
         width = root.getBoundsInLocal().getWidth();
         xmlRoot.addAttribute("width", width);
      }
      if (params.height > 0) {
         height = params.height;
         xmlRoot.addAttribute("height", params.height);
      } else {
         height = root.getBoundsInLocal().getHeight();
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
    * @param file the file
    * @param params the conversion parameters
    * @throws IOException if writing the SVG fails
    */
   public void convert(Node root, File file, ConverterParameters params) throws IOException {
      delegate.setSVGFile(file);
      this.params = params;
      XMLRoot xmlRoot = new XMLRoot("svg");
      addRootAttributes(root, xmlRoot, params);
      delegate.convertRoot(root, xmlRoot, params);

      XMLNodeUtilities.print(xmlRoot, 2, file);
   }

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @param url the url
    * @throws IOException if writing the SVG fails
    */
   public void convert(Node root, URL url) throws IOException {
      convert(root, url, new ConverterParameters());
   }

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @param url the url
    * @param params the conversion parameters
    * @throws IOException if writing the SVG fails
    */
   public void convert(Node root, URL url, ConverterParameters params) throws IOException {
      delegate.setSVGFile(new File(url.getFile()));
      this.params = params;
      XMLRoot xmlRoot = new XMLRoot("svg");
      addRootAttributes(root, xmlRoot, params);
      delegate.convertRoot(root, xmlRoot, params);

      XMLNodeUtilities.print(xmlRoot, 2, url);
   }
}
