/*
Copyright (c) 2026 Hervé Girod
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
package org.girod.javafx.svgimage.fromjfx;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javafx.scene.Node;

/**
 * The JavaFX converter.
 *
 * @param <D> the converter delegate
 * @version 1.8
 */
public interface JFXConverter<D extends ConverterDelegate> {

   /**
    * Set the default converter parameters.
    *
    * @param defaultParams the default converter parameters
    */
   public void setDefaultParameters(ConverterParameters defaultParams);

   /**
    * Return the converter delegate.
    *
    * @return the converter delegate
    */
   public D getConverterDelegate();

   /**
    * Return the root Node.
    *
    * @return the root Node
    */
   public Node getRoot();

   /**
    * Return the Affine transform on the root node.
    *
    * @return the Affine transform
    */
   public default AffineTransform getTransform() {
      return getTransform(getRoot());
   }

   /**
    * Return the Affine transform on a node.
    *
    * @param node the node
    * @return the Affine transform
    */
   public AffineTransform getTransform(Node node);

   /**
    * Return the translation on the root node.
    *
    * @return the translation on the root node
    */
   public default AffineTransform getTranslation() {
      return getTranslation(getRoot());
   }

   /**
    * Return the translation on a node.
    *
    * @param node the node
    * @return the translation
    */
   public AffineTransform getTranslation(Node node);

   /**
    * Return the viewbox for the root node.
    *
    * @return the viewbox
    */
   public default Rectangle2D getViewbox() {
      return getViewbox(getRoot());
   }

   /**
    * Return the viewbox for a node.
    *
    * @param node the node
    * @return the compute of the viewbox for the node
    */
   public Rectangle2D getViewbox(Node node);

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @throws JFXConverterException if the conversion fails
    */
   public void convert(Node root) throws JFXConverterException;

   /**
    * Convert a JavaFX Node hierarchy.
    *
    * @param root the root Node
    * @param params the parameters
    * @throws JFXConverterException if the conversion fails
    */
   public void convert(Node root, ConverterParameters params) throws JFXConverterException;
}
