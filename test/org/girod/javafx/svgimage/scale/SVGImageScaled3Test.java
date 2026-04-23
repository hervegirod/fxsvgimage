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
package org.girod.javafx.svgimage.scale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.net.URL;
import javafx.scene.image.Image;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

/**
 * Unit tests for generating scaled images.
 *
 * @version 1.7.1
 */
public class SVGImageScaled3Test {
   private static double DELTA = 0.001d;

   public SVGImageScaled3Test() {
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
    * Test of generating a scaled image.
    */
   @Test
   public void testScaledNewImage() {
      System.out.println("SVGImageScaledTest : testScaledNewImage");
      URL url = this.getClass().getResource("rect50.svg");
      SVGImage result = SVGLoader.load(url);
      Image img = result.toImage();
      assertNotNull("Image must exist", img);
      double width = img.getWidth();
      double height = img.getHeight();
      assertEquals("width", 50, width, DELTA);
      assertEquals("height", 50, height, DELTA);
      
      SVGImage result2 = result.scale(2d, true);
      assertFalse("Must be a new image", result == result2);
      img = result2.toImage();
      assertNotNull("Image must exist", img);
      width = img.getWidth();
      height = img.getHeight();
      assertEquals("width", 100, width, DELTA);
      assertEquals("height", 100, height, DELTA);      
   }
   
   /**
    * Test of scaling a existing image.
    */
   @Test
   public void testScaledExistingImage() {
      System.out.println("SVGImageScaledTest : testScaledExistingImage");
      URL url = this.getClass().getResource("rect50.svg");
      SVGImage result = SVGLoader.load(url);
      Image img = result.toImage();
      assertNotNull("Image must exist", img);
      double width = img.getWidth();
      double height = img.getHeight();
      assertEquals("width", 50, width, DELTA);
      assertEquals("height", 50, height, DELTA);
      
      SVGImage result2 = result.scale(2d, false);
      assertTrue("Must be the same image", result == result2);
      img = result2.toImage();
      assertNotNull("Image must exist", img);
      width = img.getWidth();
      height = img.getHeight();
      assertEquals("width", 100, width, DELTA);
      assertEquals("height", 100, height, DELTA);    
   }   
   
   /**
    * Test of scaling a existing image.
    */
   @Test
   public void testScaledExistingImage2() {
      System.out.println("SVGImageScaledTest : testScaledExistingImage2");
      URL url = this.getClass().getResource("rect50.svg");
      SVGImage result = SVGLoader.load(url);
      Image img = result.toImage();
      assertNotNull("Image must exist", img);
      double width = img.getWidth();
      double height = img.getHeight();
      assertEquals("width", 50, width, DELTA);
      assertEquals("height", 50, height, DELTA);
      
      SVGImage result2 = result.scale(2d);
      assertTrue("Must be the same image", result == result2);
      img = result2.toImage();
      assertNotNull("Image must exist", img);
      width = img.getWidth();
      height = img.getHeight();
      assertEquals("width", 100, width, DELTA);
      assertEquals("height", 100, height, DELTA);      
   }    
   
   /**
    * Test of generating a scaled image.
    */
   @Test
   public void testScaledExistingImage3() {
      System.out.println("SVGImageScaledTest : testScaledExistingImage3");
      URL url = this.getClass().getResource("rect50.svg");
      SVGImage result = SVGLoader.load(url);
      result = result.scale(0.5d);
      Image img = result.toImage();
      assertNotNull("Image must exist", img);
      double width = img.getWidth();
      double height = img.getHeight();
      assertEquals("width", 25, width, DELTA);
      assertEquals("height", 25, height, DELTA);
   }   
}
