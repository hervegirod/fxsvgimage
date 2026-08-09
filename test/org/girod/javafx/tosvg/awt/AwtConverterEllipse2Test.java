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
package org.girod.javafx.tosvg.awt;

import java.io.File;
import java.net.URL;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import org.girod.javafx.svgimage.fromjfx.ConverterParameters;
import org.girod.javafx.utils.ImagesComparator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for an awt converter to convert an ellipse.
 *
 * @since 1.8
 */
public class AwtConverterEllipse2Test {

   public AwtConverterEllipse2Test() {
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
    * Test of generating a png from an ellipse.
    */
   @Test
   public void testConvertEllipse() throws Exception {
      System.out.println("AwtConverterEllipse2Test : testConvertEllipse");
      EllipseConverterUtils utils = new EllipseConverterUtils();
      File file = File.createTempFile("topng", ".png");
      ConverterParameters params = new ConverterParameters();
      params.allowTransformForRoot = true;
      utils.convert(file, params);
      
      URL refURL =  this.getClass().getResource("ellipse2.png");
      ImagesComparator comp = new ImagesComparator();
      ImagesComparator.Params iparams = comp.getParams();
      iparams.deltaERGB = 1.5d;         
      ImagesComparator.Result iresult = comp.compareImages(new File(refURL.getFile()), file);
      assertTrue("Images are equal", iresult.isEquals());      

      file.delete();
   }

   public static class EllipseConverterUtils extends AbstractAwtConverterUtils {
      @Override
      protected Node getContent() {
         Ellipse ellipse = new Ellipse();
         ellipse.setCenterX(100.0f);
         ellipse.setCenterY(100.0f);
         ellipse.setRadiusX(100.0f);
         ellipse.setRadiusY(50.0f);
         ellipse.setStroke(Color.RED);
         ellipse.setFill(Color.GREEN);
         return ellipse;
      }

   }
}
