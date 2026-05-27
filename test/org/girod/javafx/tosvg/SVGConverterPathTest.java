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
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for a SVG converter to convert a path.
 *
 * @version 1.7.3
 */
public class SVGConverterPathTest {

   public SVGConverterPathTest() {
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
    * Test of generating a svg from a path.
    */
   @Test
   public void testConvertPath() throws Exception {
      System.out.println("SVGConverterPathTest : testConvertPath");
      PathConverterUtils utils = new PathConverterUtils();
      File file = File.createTempFile("tosvg", ".svg");
      utils.convert(file);
      URL url = SVGConverterPathTest.class.getResource("path.svg");

      XMLComparator comp = new XMLComparator(url, file);
      assertTrue("Converted path svg must be equal", comp.compare());
      file.delete();
   }

   public static class PathConverterUtils extends AbstractSVGConverterUtils {
      @Override
      protected Node getContent() {
         Path path = new Path();

         MoveTo moveTo = new MoveTo();
         moveTo.setX(0.0f);
         moveTo.setY(0.0f);
         path.getElements().add(moveTo);

         HLineTo hLineTo = new HLineTo();
         hLineTo.setX(70.0f);
         path.getElements().add(hLineTo);

         ArcTo arcTo = new ArcTo();
         arcTo.setX(50.0f);
         arcTo.setY(50.0f);
         arcTo.setRadiusX(50.0f);
         arcTo.setRadiusY(50.0f);
         path.getElements().add(arcTo);

         return path;
      }

   }
}
