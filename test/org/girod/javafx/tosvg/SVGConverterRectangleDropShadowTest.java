/*
Copyright (c) 2026 Hervé Girod
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
package org.girod.javafx.tosvg;

import java.io.File;
import java.net.URL;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for a SVG converter to convert a rectangle with a drop shadow effect.
 *
 * @since 1.7.3
 */
public class SVGConverterRectangleDropShadowTest {

   public SVGConverterRectangleDropShadowTest() {
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
    * Test of generating a svg from a rectangle.
    */
   @Test
   public void testConvertRectangle() throws Exception {
      System.out.println("SVGConverterRectangleDropShadowTest : testConvertRectangle");
      RectangleConverterUtils utils = new RectangleConverterUtils();
      File file = File.createTempFile("tosvg", ".svg");
      utils.convert(file);
      URL url = SVGConverterRectangleDropShadowTest.class.getResource("rectangleDropShadow.svg");

      XMLComparator comp = new XMLComparator(url, file);
      assertTrue("Converted rectangle svg must be equal", comp.compare());
      file.delete();
   }

   public static class RectangleConverterUtils extends AbstractSVGConverterUtils {
      @Override
      protected Node getContent() {
         Rectangle rect = new Rectangle(50, 50, 200, 100);
         rect.setFill(Color.GREEN);
         DropShadow dropShadow = new DropShadow();
         dropShadow.setRadius(5.0);
         dropShadow.setOffsetX(3.0);
         dropShadow.setOffsetY(3.0);
         dropShadow.setColor(Color.BLACK);
         rect.setEffect(dropShadow);
         return rect;
      }

   }
}
