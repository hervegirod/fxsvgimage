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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

/**
 * This class is a <code>HBox</code> which hold Text children and layout its size accordingly.
 *
 * @since 1.3
 */
public class TextHBox extends HBox {
   private final Dimension2D size;
   private Font font;
   private double[] areaWidths;
   private final List<Text> textNodes = new ArrayList<>();

   private Dimension2D getTextSize(String cdata, Font font) {
      Text text2 = new Text(cdata);
      this.font = font;
      text2.setFont(font);
      Group group = new Group(text2);
      Scene scene = new Scene(group); // we don't keep the scene, u it is necessary for the applyCSS to eturn a valid result

      areaWidths = new double[cdata.length()];
      for (int i = 0; i < cdata.length(); i++) {
         String letter = cdata.substring(i, i + 1);
         Text text = new Text(letter);
         text.setFont(font);
         group.getChildren().add(text);
         text.applyCss();
         double width = text.getLayoutBounds().getWidth();
         areaWidths[i] = width;
      }
      text2.applyCss();
      double width = text2.getLayoutBounds().getWidth();
      double height = text2.getLayoutBounds().getHeight();

      return new Dimension2D(width, height);
   }

   /**
    * Return the text Font.
    *
    * @return the text Font
    */
   public Font getFont() {
      return font;
   }

   /**
    * Return the text width.
    *
    * @return the text width
    */
   public double getTextWidth() {
      return size.getWidth();
   }

   /**
    * Return the text children.
    *
    * @return the the text children
    */
   public List<Text> getTextChildren() {
      return textNodes;
   }

   /**
    * Constructor.
    *
    * @param cdata the text
    * @param font the font
    */
   public TextHBox(String cdata, Font font) {
      super();
      setSpacing(0d);
      setPrefSize(0d, 0d);
      setMaxSize(0d, 0d);
      size = getTextSize(cdata, font);
      ObservableList<Node> list = getChildren();
      for (int i = 0; i < cdata.length(); i++) {
         String letter = cdata.substring(i, i + 1);
         Text text = new Text(letter);
         text.setFontSmoothingType(FontSmoothingType.LCD);
         HBox.setMargin(text, Insets.EMPTY);
         HBox.setHgrow(text, Priority.SOMETIMES);
         if (font != null) {
            text.setFont(font);
         }
         list.add(text);
         textNodes.add(text);
      }
   }

   /**
    * Set the fill paint for all text nodes.
    *
    * @param fill the fill paint
    */
   public void setFill(Paint fill) {
      Iterator<Text> it = textNodes.iterator();
      while (it.hasNext()) {
         Text text = it.next();
         text.setFill(fill);
      }
   }
   
   /**
    * Apply text decoration to all text nodes.
    *
    * @param value the text-decoration value
    */
   public void setTextDecoration(String value) {
      Iterator<Text> it = textNodes.iterator();
      while (it.hasNext()) {
         Text text = it.next();
         SVGShapeBuilder.applyTextDecoration(text, value);
      }
   }   
   
   /**
    * Apply text anchor alignment to all text nodes.
    *
    * @param value the text-anchor value
    */
   public void setTextAnchor(String value) {
      Iterator<Text> it = textNodes.iterator();
      while (it.hasNext()) {
         Text text = it.next();
         SVGShapeBuilder.applyTextAnchor(text, value);
      }
   }    

   @Override
   protected double computePrefHeight(double width) {
      return size.getHeight();
   }

   @Override
   protected double computePrefWidth(double height) {
      return size.getWidth();
   }

   @Override
   protected double computeMinHeight(double width) {
      return size.getHeight();
   }

   @Override
   protected double computeMinWidth(double height) {
      return size.getWidth();
   }

   @Override
   protected void layoutChildren() {
      List<Node> children = getChildren();
      double x = 0;
      for (int i = 0; i < children.size(); i++) {
         children.get(i).setLayoutX(x);
         x += areaWidths[i];
      }
   }
}
