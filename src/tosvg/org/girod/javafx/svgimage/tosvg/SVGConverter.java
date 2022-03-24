/*
Copyright (c) 2022, Hervé Girod
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
import org.girod.javafx.svgimage.tosvg.converters.ConverterDelegate;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;
import org.girod.javafx.svgimage.tosvg.xml.XMLNodeUtilities;
import org.girod.javafx.svgimage.tosvg.xml.XMLRoot;

/**
 *
 * @since 1.0
 */
public class SVGConverter {
   private ConverterDelegate delegate = null;

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
    */
   public void convert(Node root, File file) throws IOException {
      convert(root, file, new ConverterParameters());
   }

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @param file the file
    * @param params the conversion parameters
    */
   public void convert(Node root, File file, ConverterParameters params) throws IOException {
      delegate.setSVGFile(file);
      XMLRoot xmlRoot = new XMLRoot("svg");
      if (params.width > 0) {
         xmlRoot.addAttribute("width", params.width);
      } else {
         double width = root.getBoundsInLocal().getWidth();
         xmlRoot.addAttribute("width", width);
      }
      if (params.height > 0) {
         xmlRoot.addAttribute("height", params.height);
      } else {
         double height = root.getBoundsInLocal().getHeight();
         xmlRoot.addAttribute("height", height);
      }
      delegate.convertRoot(root, xmlRoot);

      XMLNodeUtilities.print(xmlRoot, 2, file);
   }

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @param url the url
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
    */
   public void convert(Node root, URL url, ConverterParameters params) throws IOException {
      delegate.setSVGFile(new File(url.getFile()));
      XMLRoot xmlRoot = new XMLRoot("svg");
      if (params.width > 0) {
         xmlRoot.addAttribute("width", params.width);
      }
      if (params.height > 0) {
         xmlRoot.addAttribute("height", params.height);
      }    
      delegate.convertRoot(root, xmlRoot);

      XMLNodeUtilities.print(xmlRoot, 2, url);
   }
}
