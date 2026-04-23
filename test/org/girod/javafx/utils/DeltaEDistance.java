/*------------------------------------------------------------------------------
 * Copyright (C) 2025, 2026 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.girod.javafx.utils;

import java.awt.Color;

/**
 * This class computes the distance between two colors. This is the CIELAB ΔE* distance.
 *
 * @since 1.7.1
 */
public class DeltaEDistance {
   private static final double XYZ_WHITE_REFERENCE_X = 95.047;
   private static final double XYZ_WHITE_REFERENCE_Y = 100;
   private static final double XYZ_WHITE_REFERENCE_Z = 108.883;
   private static final double XYZ_EPSILON = 0.008856;
   private static final double XYZ_KAPPA = 903.3;

   private DeltaEDistance() {
   }
   
   public static void main(String[] args) {
      double deltaE = deltaE(Color.BLUE, Color.RED);
      System.out.println("deltaE(Color.BLUE, Color.RED) = " + deltaE);
      deltaE = deltaE(Color.BLUE, Color.CYAN);
      System.out.println("deltaE(Color.BLUE, Color.CYAN) = " + deltaE);
      deltaE = deltaE(Color.GRAY, Color.LIGHT_GRAY);
      System.out.println("deltaE(Color.GRAY, Color.LIGHT_GRAY) = " + deltaE);      
   }

   /**
    * Computes the deltaE distance between two colors.
    *
    * @param c1 the first color
    * @param c2 the second color
    * @return the distance
    */
   public static double deltaE(Color c1, Color c2) {
      // see https://en.wikipedia.org/wiki/Color_difference#CIELAB_%CE%94E*
      // see https://stackoverflow.com/questions/79492413/how-calculate-distance-between-two-rgb-colors
      double[] outLab1 = new double[3];
      RGBToLAB(c1, outLab1);

      double[] outLab2 = new double[3];
      RGBToLAB(c2, outLab2);

      double diff_l = outLab1[0] - outLab2[0];
      double diff_a = outLab1[1] - outLab2[1];
      double diff_b = outLab1[2] - outLab2[2];
      return Math.sqrt(diff_l * diff_l + diff_a * diff_a + diff_b * diff_b);
   }

   private static void RGBToLAB(Color color, double[] outLab) {
      RGBToXYZ(color.getRed(), color.getGreen(), color.getBlue(), outLab);
      XYZToLAB(outLab[0], outLab[1], outLab[2], outLab);
   }

   private static void RGBToXYZ(int r, int g, int b, double[] outXyz) {
      double sr = r / 255.0;
      sr = sr < 0.04045 ? sr / 12.92 : Math.pow((sr + 0.055) / 1.055, 2.4);
      double sg = g / 255.0;
      sg = sg < 0.04045 ? sg / 12.92 : Math.pow((sg + 0.055) / 1.055, 2.4);
      double sb = b / 255.0;
      sb = sb < 0.04045 ? sb / 12.92 : Math.pow((sb + 0.055) / 1.055, 2.4);
      outXyz[0] = 100 * (sr * 0.4124 + sg * 0.3576 + sb * 0.1805);
      outXyz[1] = 100 * (sr * 0.2126 + sg * 0.7152 + sb * 0.0722);
      outXyz[2] = 100 * (sr * 0.0193 + sg * 0.1192 + sb * 0.9505);
   }

   private static void XYZToLAB(double x, double y, double z, double[] outLab) {
      x = pivotXyzComponent(x / XYZ_WHITE_REFERENCE_X);
      y = pivotXyzComponent(y / XYZ_WHITE_REFERENCE_Y);
      z = pivotXyzComponent(z / XYZ_WHITE_REFERENCE_Z);
      outLab[0] = Math.max(0, 116 * y - 16);
      outLab[1] = 500 * (x - y);
      outLab[2] = 200 * (y - z);
   }

   private static double pivotXyzComponent(double component) {
      return component > XYZ_EPSILON ? Math.pow(component, 1 / 3.0) : (XYZ_KAPPA * component + 16) / 116;
   }
}
