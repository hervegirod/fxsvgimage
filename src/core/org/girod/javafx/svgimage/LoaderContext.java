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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Paint;
import org.girod.javafx.svgimage.xml.ClippingFactory;
import org.girod.javafx.svgimage.xml.FilterSpec;
import org.girod.javafx.svgimage.xml.GradientSpec;
import org.girod.javafx.svgimage.xml.Styles;
import org.girod.javafx.svgimage.xml.SymbolSpec;
import org.girod.javafx.svgimage.xml.Viewport;
import org.girod.javafx.svgimage.xml.XMLNode;

/**
 * The context of a {@link SVGLoader}.
 *
 * @version 0.6
 */
public class LoaderContext {
   /**
    * The resulting group.
    */
   public final SVGImage root;
   /**
    * The loader parameters.
    */
   public final LoaderParameters params;
   /**
    * The viewport.
    */
   public Viewport viewport = null;
   /**
    * The overall "styles" element if it exists.
    */
   public Styles svgStyle = null;
   /**
    * The SVG file url.
    */
   public final URL url;
   /**
    * The clipping factory.
    */
   public final ClippingFactory clippingFactory = new ClippingFactory();
   /**
    * The gradients specifications.
    */
   public final Map<String, GradientSpec> gradientSpecs = new HashMap<>();
   /**
    * The filters specifications.
    */
   public final Map<String, FilterSpec> filterSpecs = new HashMap<>();
   /**
    * The gradients.
    */
   public final Map<String, Paint> gradients = new HashMap<>();
   private Map<String, XMLNode> namedNodes = new HashMap<>();
   private Map<String, SymbolSpec> symbols = new HashMap<>();
   /**
    * True if the effects are supported.
    */
   public boolean effectsSupported = false;

   public LoaderContext(SVGImage root, LoaderParameters params, URL url) {
      this.root = root;
      this.params = params;
      this.url = url;
   }

   /**
    * Add a named node.
    *
    * @param id the node id
    * @param xmlNode the node
    */
   public void addNamedNode(String id, XMLNode xmlNode) {
      namedNodes.put(id, xmlNode);
   }

   /**
    * Return true if there is a node with a specified id.
    *
    * @param id the node id
    * @return true if there is a node with the specified id
    */
   public boolean hasReifiedNamedNode(String id) {
      return namedNodes.containsKey(id);
   }

   /**
    * Return true if there is a node with a specified id.
    *
    * @param id the node id
    * @return true if there is a node with the specified id
    */
   public boolean hasNamedNode(String id) {
      return namedNodes.containsKey(id) || symbols.containsKey(id);
   }

   /**
    * Return the node of a specified id.
    *
    * @param id the node id
    * @return the node
    */
   public XMLNode getReifiedNamedNode(String id) {
      return namedNodes.get(id);
   }

   /**
    * Return the node of a specified id.
    *
    * @param id the node id
    * @return the node
    */
   public XMLNode getNamedNode(String id) {
      if (namedNodes.containsKey(id)) {
         return namedNodes.get(id);
      } else {
         return symbols.get(id).getXMLNode();
      }
   }

   /**
    * Add a symbol.
    *
    * @param id the symbols id
    * @param symbol the symbol
    */
   public void addSymbol(String id, SymbolSpec symbol) {
      symbols.put(id, symbol);
   }

   /**
    * Return true if there is a symbol with a specified id.
    *
    * @param id the symbols id
    * @return true if there is a symbol with the specified id
    */
   public boolean hasSymbol(String id) {
      return symbols.containsKey(id);
   }

   /**
    * Return the symbol of a specified id.
    *
    * @param id the symbols id
    * @return the symbols
    */
   public SymbolSpec getSymbol(String id) {
      return symbols.get(id);
   }
}
