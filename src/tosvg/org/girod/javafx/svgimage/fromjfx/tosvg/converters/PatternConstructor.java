/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.javafx.svgimage.fromjfx.tosvg.converters;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import org.girod.javafx.svgimage.fromjfx.tosvg.xml.XMLNode;

/**
 * Builds SVG pattern elements.
 *
 * @since 1.7.3
 */
public class PatternConstructor {
   private int patternID = 0;

   /**
    * Create a clip constructor.
    */
   public PatternConstructor() {
   }

   /**
    * Return the pattern id.
    *
    * @return the pattern id
    */
   public String getGradientID() {
      return "pattern_" + patternID;
   }

   /**
    * Create an SVG pattern element for the provided ImagePattern.
    *
    * @param pattern the ImagePattern
    * @return the SVG pattern element
    */
   public XMLNode createPattern(ImagePattern pattern) {
      Image image = pattern.getImage();
      XMLNode patternNode = new XMLNode("pattern");
      int width = (int)pattern.getWidth();
      int height = (int)pattern.getHeight();
      int x = (int)pattern.getX();
      int y = (int)pattern.getY();
      patternNode.addAttribute("width", width);
      patternNode.addAttribute("height", height);
      patternNode.addAttribute("viewBox", x + " " + y + " " + (width + x) + " " + (height + y));
      XMLNode imageNode = new XMLNode("image");
      imageNode.addAttribute("width", width);
      imageNode.addAttribute("height", height);
      imageNode.addAttribute("x", x);
      imageNode.addAttribute("y", y);
      patternID++;
      return patternNode;
   }
}
