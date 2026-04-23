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
package org.girod.javafx.svgimage.css;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.net.URL;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;
import org.girod.javafx.utils.ImagesComparator;
import org.girod.javafx.utils.PaneImageUtil;
import org.girod.javafx.utils.SnapshotUtils;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for a css element
 *
 * @since 1.7.1
 */
public class SVGLoaderCSSTest {
   private static File file = null;

   public SVGLoaderCSSTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
      SnapshotUtils.delete(file);
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of load method, of class SVGLoader.
    */
   @Test
   public void testStyleCSS() throws Exception {
      System.out.println("SVGLoaderCSSTest : testStyleCSS");
      URL url = this.getClass().getResource("circle_css.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);
      PaneImageUtil util = new PaneImageUtil();
      util.createStage(result);
      
      file = File.createTempFile("fxsvgimage", ".png");
      boolean isValid = result.snapshot("png", file); 
      assertTrue("File must exist", isValid);
      
      URL refURL =  this.getClass().getResource("circle3.png");
      ImagesComparator comp = new ImagesComparator();
      ImagesComparator.Params params = comp.getParams();
      params.deltaERGB = 1.5d;        
      ImagesComparator.Result iresult = comp.compareImages(new File(refURL.getFile()), file);
      assertTrue("Images are equal", iresult.isEquals());
      util.hideStage();
   }   
}
