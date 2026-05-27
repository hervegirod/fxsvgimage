/*
Copyright (c) 2026, Hervé Girod
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.scene.paint.CycleMethod;
import static javafx.scene.paint.CycleMethod.NO_CYCLE;
import static javafx.scene.paint.CycleMethod.REFLECT;
import static javafx.scene.paint.CycleMethod.REPEAT;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

/**
 * Builds SVG gradient elements from Linear and Radial gradients.
 *
 * @since 1.7.3
 */
public class GradientConstructor {
   private int gradientID = 0;
   private final Map<LinearGradient, Integer> linearGradientToId = new HashMap<>();
   private final Map<RadialGradient, Integer> radialGradientToId = new HashMap<>();

   /**
    * Constructor.
    */
   public GradientConstructor() {
   }

   /**
    * Return the next gradient id.
    *
    * @return the gradient id
    */
   public String getGradientID() {
      return "gradient_" + gradientID;
   }

   /**
    * Create an SVG linearGradient element for the provided LinearGradient paint.
    *
    * @param gradient the LinearGradient paint
    * @return the SVG linearGradient element
    */
   public XMLNode createGradient(LinearGradient gradient) {
      if (linearGradientToId.containsKey(gradient)) {
         gradientID = linearGradientToId.get(gradient);
         return null;
      }
      XMLNode gradientNode = new XMLNode("linearGradient");
      int x1 = (int) (gradient.getStartX() * 100d);
      int y1 = (int) (gradient.getStartY() * 100d);
      int x2 = (int) (gradient.getEndX() * 100d);
      int y2 = (int) (gradient.getEndY() * 100d);
      gradientNode.addAttribute("x1", x1 + "%");
      gradientNode.addAttribute("y1", y1 + "%");
      gradientNode.addAttribute("x2", x2 + "%");
      gradientNode.addAttribute("y2", y2 + "%");
      gradientNode.addAttribute("gradientUnits", "userSpaceOnUse");
      CycleMethod cycleMethod = gradient.getCycleMethod();
      switch (cycleMethod) {
         case NO_CYCLE:
            gradientNode.addAttribute("spreadMethod", "pad");
            break;
         case REFLECT:
            gradientNode.addAttribute("spreadMethod", "reflect");
            break;   
         case REPEAT:
            gradientNode.addAttribute("spreadMethod", "repeat");
            break;              
      }      
      Iterator<Stop> it = gradient.getStops().iterator();
      while (it.hasNext()) {
         Stop stop = it.next();
         XMLNode stopNode = new XMLNode("stop");
         gradientNode.addChild(stopNode);
         int offset = (int) (stop.getOffset() * 100d);
         stopNode.addAttribute("offset", offset + "%");
         String colorS = Utilities.convertColor(stop.getColor());
         stopNode.addAttribute("stop-color", colorS);
      }
      gradientID++;
      linearGradientToId.put(gradient, gradientID);
      return gradientNode;
   }

   /**
    * Create an SVG radialGradient element for the provided LinearGradient paint.
    *
    * @param gradient the RadialGradient paint
    * @return the SVG radialGradient element
    */
   public XMLNode createGradient(RadialGradient gradient) {
      if (radialGradientToId.containsKey(gradient)) {
         gradientID = radialGradientToId.get(gradient);
         return null;
      }
      XMLNode gradientNode = new XMLNode("radialGradient");
      int cx = (int) (gradient.getCenterX());
      int cy = (int) (gradient.getCenterY());
      int radius = (int) (gradient.getRadius());
      int fx = (int) (gradient.getFocusAngle());
      gradientNode.addAttribute("cx", cx);
      gradientNode.addAttribute("cy", cy);
      gradientNode.addAttribute("r", radius);
      gradientNode.addAttribute("fx", fx);
      gradientNode.addAttribute("fy", fx);
      gradientNode.addAttribute("gradientUnits", "userSpaceOnUse");
      CycleMethod cycleMethod = gradient.getCycleMethod();
      switch (cycleMethod) {
         case NO_CYCLE:
            gradientNode.addAttribute("spreadMethod", "pad");
            break;
         case REFLECT:
            gradientNode.addAttribute("spreadMethod", "reflect");
            break;   
         case REPEAT:
            gradientNode.addAttribute("spreadMethod", "repeat");
            break;              
      }
      Iterator<Stop> it = gradient.getStops().iterator();
      while (it.hasNext()) {
         Stop stop = it.next();
         XMLNode stopNode = new XMLNode("stop");
         gradientNode.addChild(stopNode);
         int offset = (int) (stop.getOffset() * 100d);
         stopNode.addAttribute("offset", offset + "%");
         String colorS = Utilities.convertColor(stop.getColor());
         stopNode.addAttribute("stop-color", colorS);
      }
      gradientID++;
      radialGradientToId.put(gradient, gradientID);
      return gradientNode;
   }
}
