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

import javafx.scene.Parent;
import javafx.scene.SubScene;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

/**
 * A converter which convert a subScene.
 *
 * @since 1.0
 */
public class SubSceneConverter extends AbstractConverter {
   private SubScene subScene = null;

   public SubSceneConverter(ConverterDelegate delegate, SubScene subScene, XMLNode xmlParent) {
      super(delegate, subScene, xmlParent);
      this.subScene = subScene;
   }

   /**
    * Return the SubScene.
    *
    * @return the SubScene
    */
   public SubScene getSubScene() {
      return subScene;
   }

   /**
    * Return the converter SubScene root Node.
    *
    * @return the converter SubScene root Node
    */
   @Override
   public Parent getParent() {
      return subScene.getRoot();
   }

   @Override
   public void applyStyle(XMLNode node, String clipID) {
      if (clipID != null) {
         StringBuilder buf = new StringBuilder();
         String style = buf.toString();
         node.addAttribute("style", style);
      }      
   }

   /**
    * Return a g node.
    *
    * @return the node
    */
   @Override
   public XMLNode convert() {
      XMLNode xmlGroup = new XMLNode("g");
      xmlParent.addChild(xmlGroup);
      return xmlGroup;
   }
}
