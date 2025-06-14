/*
Copyright (c) 2025, Hervé Girod
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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.net.URL;
import javafx.scene.image.Image;

/**
 * Unit tests for generating scaled images.
 *
 * @since 1.3
 */
public class SVGImageScaledParamsTest {
   private static double DELTA = 0.001d;

   public SVGImageScaledParamsTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }
   
   /**
    * Test of generating a non scaled image.
    */
   @Test
   public void testNoScaledImage() {
      System.out.println("SVGImageScaledParamsTest : testNoScaledImage");
      URL url = this.getClass().getResource("logo_Simplified.svg");
      SVGImage result = SVGLoader.load(url);
      Image img = result.toImage();
      assertNotNull("Image must exist", img);
      double width = img.getWidth();
      double height = img.getHeight();
      assertEquals("width", 113, width, DELTA);
      assertEquals("height", 113, height, DELTA);
   }   

   /**
    * Test of generating a non scaled image.
    */
   @Test
   public void testScaledParamsImage() {
      System.out.println("SVGImageScaledParamsTest : testScaledParamsImage");
      URL url = this.getClass().getResource("logo_Simplified.svg");
      LoaderParameters params = new LoaderParameters();
      params.width = 300;
      SVGImage result = SVGLoader.load(url, params);
      Image img = result.toImage();
      assertNotNull("Image must exist", img);
      double width = img.getWidth();
      double height = img.getHeight();
      assertEquals("width", 224, width, DELTA);
      assertEquals("height", 224, height, DELTA);
   }
}
