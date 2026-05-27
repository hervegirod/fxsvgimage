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

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Light;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

/**
 * Builds SVG filter elements.
 *
 * @since 1.7.3
 */
public class FilterConstructor {
   private int filterID = 0;

   /**
    * Create a clip constructor.
    */
   public FilterConstructor() {
   }

   /**
    * Return the filter id.
    *
    * @return the filter id
    */
   public String getFilterID() {
      return "filter_" + filterID;
   }

   /**
    * Create an SVG filter element for the provided effect.
    *
    * @param effect the effect
    * @return the SVG filter element
    */
   public XMLNode createFilter(Effect effect) {
      XMLNode filterNode = null;
      if (effect instanceof DropShadow) {
         filterNode = new XMLNode("filter");
         DropShadow dropShadow = (DropShadow) effect;
         createDropShadow(filterNode, dropShadow);
      } else if (effect instanceof GaussianBlur) {
         filterNode = new XMLNode("filter");
         GaussianBlur blur = (GaussianBlur) effect;
         createGaussianBlur(filterNode, blur);
      } else if (effect instanceof Lighting) {
         filterNode = new XMLNode("filter");
         Lighting lighting = (Lighting) effect;
         createLighting(filterNode, lighting);
      }
      if (filterNode != null) {
         filterID++;
         return filterNode;
      } else {
         return null;
      }
   }

   private void createLighting(XMLNode filterNode, Lighting lighting) {
      boolean isDiffuse = isDiffuse(lighting);
      boolean isSpecular = isSpecular(lighting);
      if (isDiffuse && !isSpecular) {
         XMLNode lightingNode = createLighting(filterNode, lighting, true);
         filterNode.addChild(lightingNode);
      } else if (!isDiffuse && isSpecular) {
         XMLNode lightingNode = createLighting(filterNode, lighting, false);
         filterNode.addChild(lightingNode);
      } else if (isDiffuse && isSpecular) {
         XMLNode diffuseLightingNode = createLighting(filterNode, lighting, true);
         diffuseLightingNode.addAttribute("in", "SourceGraphic");
         diffuseLightingNode.addAttribute("result", "diffuse1");
         filterNode.addChild(diffuseLightingNode);
         XMLNode specularLightingNode = createLighting(filterNode, lighting, false);      
         specularLightingNode.addAttribute("in", "SourceGraphic");
         specularLightingNode.addAttribute("result", "diffuse2");     
         filterNode.addChild(specularLightingNode);
         XMLNode mergeNode = new XMLNode("feMerge");
         filterNode.addChild(mergeNode);
         XMLNode mergeChild1 = new XMLNode("feMergeNode");
         mergeChild1.addAttribute("in", "diffuse1");  
         mergeNode.addChild(mergeChild1);
         XMLNode mergeChild2 = new XMLNode("feMergeNode");
         mergeChild2.addAttribute("in", "diffuse2");  
         mergeNode.addChild(mergeChild2);
      }
   }

   private boolean isDiffuse(Lighting lighting) {
      return lighting.getDiffuseConstant() != 0d;
   }

   private boolean isSpecular(Lighting lighting) {
      return lighting.getSpecularConstant() != 0d;
   }

   private XMLNode createLighting(XMLNode filterNode, Lighting lighting, boolean isDiffuse) {
      filterNode.addAttribute("filterUnits", "userSpaceOnUse");
      XMLNode lightingNode;
      if (isDiffuse) {
         lightingNode = new XMLNode("feDiffuseLighting");
         lightingNode.addAttribute("diffuseConstant", lighting.getDiffuseConstant());
      } else {
         lightingNode = new XMLNode("feSpecularLighting");
         lightingNode.addAttribute("specularConstant", lighting.getSpecularConstant());
         lightingNode.addAttribute("specularExponent", lighting.getSpecularExponent());
      }
      lightingNode.addAttribute("surfaceScale", lighting.getSurfaceScale());
      String colorS = Utilities.convertColor(lighting.getLight().getColor());
      Light light = lighting.getLight();
      if (light instanceof Light.Point) {
         XMLNode pointLightNode = new XMLNode("fePointLight");
         lightingNode.addChild(pointLightNode);
         Light.Point pointLight = (Light.Point) light;
         pointLightNode.addAttribute("x", (int) pointLight.getX());
         pointLightNode.addAttribute("y", (int) pointLight.getY());
         pointLightNode.addAttribute("z", (int) pointLight.getZ());
      } else if (light instanceof Light.Distant) {
         XMLNode distantLightNode = new XMLNode("feDistantLight");
         lightingNode.addChild(distantLightNode);
         Light.Distant distantLight = (Light.Distant) light;
         int azimuth = (int) distantLight.getAzimuth();
         int elevation = (int) distantLight.getElevation();
         distantLightNode.addAttribute("azimuth", azimuth);
         distantLightNode.addAttribute("elevation", elevation);
      } else if (light instanceof Light.Spot) {
         XMLNode spotLightNode = new XMLNode("feSpotLight");
         lightingNode.addChild(spotLightNode);
         Light.Spot spotLight = (Light.Spot) light;
         spotLightNode.addAttribute("x", (int) spotLight.getX());
         spotLightNode.addAttribute("y", (int) spotLight.getY());
         spotLightNode.addAttribute("z", (int) spotLight.getZ());
         spotLightNode.addAttribute("pointsAtX", (int) spotLight.getPointsAtX());
         spotLightNode.addAttribute("pointsAtY", (int) spotLight.getPointsAtY());
         spotLightNode.addAttribute("pointsAtY", (int) spotLight.getPointsAtZ());
         spotLightNode.addAttribute("specularExponent", (int) spotLight.getSpecularExponent());
      }
      lightingNode.addAttribute("lighting-color", colorS);
      return lightingNode;
   }

   private void createGaussianBlur(XMLNode filterNode, GaussianBlur blur) {
      filterNode.addAttribute("filterUnits", "userSpaceOnUse");
      XMLNode blurNode = new XMLNode("feGaussianBlur");
      filterNode.addChild(blurNode);
      blurNode.addAttribute("stdDeviation", blur.getRadius());
   }

   private void createDropShadow(XMLNode filterNode, DropShadow dropShadow) {
      filterNode.addAttribute("filterUnits", "userSpaceOnUse");
      XMLNode dropShadowNode = new XMLNode("feDropShadow");
      filterNode.addChild(dropShadowNode);
      dropShadowNode.addAttribute("dx", dropShadow.getOffsetX());
      dropShadowNode.addAttribute("dy", dropShadow.getOffsetY());
      dropShadowNode.addAttribute("stdDeviation", dropShadow.getRadius());
      String colorS = Utilities.convertColor(dropShadow.getColor());
      dropShadowNode.addAttribute("flood-color", colorS);
   }
}
