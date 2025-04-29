/*
Copyright (c) 2025 Hervé Girod
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
package org.girod.javafx.svgimage.xml.builders;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Utilities for builders.
 *
 * @since 1.3
 */
public class BuilderUtils {
   private BuilderUtils() {
   }

   /**
    * Removes new lines from a text.
    *
    * @param text the text
    * @return the text without new lines
    */
   public static String removeNewLines(String text) {
      if (text == null) {
         return null;
      }
      text = text.trim();
      return text.replace("\n", "").replaceAll("\t", "");
   }

   /**
    *
    * Computes the width of a text.
    *
    * @param text the text
    * @return the width
    */
   public static double getTextWidth(Text text) {
      Text text2 = new Text(text.getText());
      text2.setFont(text.getFont());
      Group group = new Group(text2);
      Scene scene = new Scene(group); // we need to create a scene for the applyCSS to have a result
      text2.applyCss();
      double width = text2.getLayoutBounds().getWidth();
      return width;
   }

   /**
    *
    * Computes the width of a text or a TextHBox.
    *
    * @param node the text or TextHBox
    * @return the width
    */
   public static double getTextWidth(Node node) {
      double width;
      if (node instanceof Text) {
         width = getTextWidth((Text) node);
      } else if (node instanceof TextHBox) {
         width = ((TextHBox) node).getTextWidth();
      } else {
         width = 0d;
      }
      return width;
   }

   /**
    *
    * Computes the Font of a text or a TextHBox.
    *
    * @param node the text or TextHBox
    * @return the Font
    */
   public static Font getTextFont(Node node) {
      Font font;
      if (node instanceof Text) {
         font = ((Text) node).getFont();
      } else if (node instanceof TextHBox) {
         font = ((TextHBox) node).getFont();
      } else {
         font = Font.font(12d);
      }
      return font;
   }

   /**
    *
    * Computes the X position of a text or a TextHBox.
    *
    * @param node the text or TextHBox
    * @return the x position
    */   
   public static double getTextX(Node node) {
      double x;
      if (node instanceof Text) {
         x = ((Text) node).getX();
      } else if (node instanceof TextHBox) {
         x = ((HBox) node).getLayoutX();
      } else {
         x = 0d;
      }
      return x;
   }

   /**
    *
    * Computes the Y position of a text or a TextHBox.
    *
    * @param node the text or TextHBox
    * @return the y position
    */     
   public static double getTextY(Node node) {
      double y;
      if (node instanceof Text) {
         y = ((Text) node).getY();
      } else if (node instanceof TextHBox) {
         y = ((HBox) node).getLayoutY();
      } else {
         y = 0d;
      }
      return y;
   }
}
