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
package org.girod.javafx.svgimage.tosvg.converters;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

/**
 * A converter which convert Controls.
 *
 * @since 1.0
   */
public class ControlConverter extends RegionConverter {
   private Control control = null;

   /**
    * Constructor.
    *
    * @param delegate the converter delegate
    * @param control the control to convert
    * @param xmlParent the parent xml node
    */
   public ControlConverter(ConverterDelegate delegate, Control control, XMLNode xmlParent) {
      super(delegate, control, xmlParent);
      this.control = control;
   }

   /**
    * Convert the Control.
    *
    * @return the xml node
    */
   @Override
   public XMLNode convert() {
      return super.convert();
   }

   /**
    * Return the additional child Node on this Node.
    * Will return null, except for a {@link javafx.scene.control.Labeled}, where it will return {@link javafx.scene.control.Labeled#getGraphic()}.
    *
    * @return the additional child Node on this Node
    */
   @Override
   public Node getAdditionalNode() {
      if (control instanceof Labeled) {
         return ((Labeled) control).getGraphic();
      } else {
         return null;
      }
   }
}
