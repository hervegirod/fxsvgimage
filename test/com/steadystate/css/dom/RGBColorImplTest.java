/*
 * Copyright (C) 1999-2020 David Schweinsberg.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.steadystate.css.dom;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

import com.steadystate.css.format.CSSFormat;
import com.steadystate.css.parser.LexicalUnitImpl;

/**
 * Unit tests for {@link RGBColorImpl}.
 *
 * @author rbri
 */
public class RGBColorImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void defaultConstructor() throws Exception {
        final RGBColorImpl rgb = new RGBColorImpl();
        Assert.assertEquals("rgb(null, null, null)", rgb.toString());
        Assert.assertNull(rgb.getRed());
        Assert.assertNull(rgb.getGreen());
        Assert.assertNull(rgb.getBlue());

        LexicalUnit lu = LexicalUnitImpl.createNumber(null, 10);
        rgb.setRed(new CSSValueImpl(lu, true));
        Assert.assertEquals("rgb(10, null, null)", rgb.toString());
        Assert.assertEquals("10", rgb.getRed().getCssText());

        lu = LexicalUnitImpl.createNumber(null, 20);
        rgb.setGreen(new CSSValueImpl(lu, true));
        Assert.assertEquals("rgb(10, 20, null)", rgb.toString());
        Assert.assertEquals("20", rgb.getGreen().getCssText());

        lu = LexicalUnitImpl.createNumber(null, 30);
        rgb.setBlue(new CSSValueImpl(lu, true));
        Assert.assertEquals("rgb(10, 20, 30)", rgb.toString());
        Assert.assertEquals("30", rgb.getBlue().getCssText());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void constructByLU() throws Exception {
        final LexicalUnit rgbLU = LexicalUnitImpl.createNumber(null, 10);
        LexicalUnit lu = LexicalUnitImpl.createComma(rgbLU);
        lu = LexicalUnitImpl.createNumber(lu, 20);
        lu = LexicalUnitImpl.createComma(lu);
        lu = LexicalUnitImpl.createNumber(lu, 30);

        final RGBColorImpl rgb = new RGBColorImpl(rgbLU);
        Assert.assertEquals("rgb(10, 20, 30)", rgb.toString());
        Assert.assertEquals("10", rgb.getRed().getCssText());
        Assert.assertEquals("20", rgb.getGreen().getCssText());
        Assert.assertEquals("30", rgb.getBlue().getCssText());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void constructByLUException() throws Exception {
        LexicalUnit rgbLU = LexicalUnitImpl.createNumber(null, 10);
        LexicalUnit lu = LexicalUnitImpl.createNumber(rgbLU, 20);
        lu = LexicalUnitImpl.createNumber(lu, 30);

        try {
            new RGBColorImpl(rgbLU);
            Assert.fail("DOMException expected");
        }
        catch (final DOMException e) {
            Assert.assertEquals("rgb parameters must be separated by ','.", e.getMessage());
        }

        rgbLU = LexicalUnitImpl.createNumber(null, 10);
        lu = LexicalUnitImpl.createComma(rgbLU);
        lu = LexicalUnitImpl.createNumber(lu, 20);
        lu = LexicalUnitImpl.createNumber(lu, 30);

        try {
            new RGBColorImpl(rgbLU);
            Assert.fail("DOMException expected");
        }
        catch (final DOMException e) {
            Assert.assertEquals("rgb parameters must be separated by ','.", e.getMessage());
        }
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void constructByLUTooManyValuesException() throws Exception {
        final LexicalUnit rgbLU = LexicalUnitImpl.createNumber(null, 10);
        LexicalUnit lu = LexicalUnitImpl.createComma(rgbLU);
        lu = LexicalUnitImpl.createNumber(lu, 20);
        lu = LexicalUnitImpl.createComma(lu);
        lu = LexicalUnitImpl.createNumber(lu, 30);
        lu = LexicalUnitImpl.createComma(lu);

        try {
            new RGBColorImpl(rgbLU);
            Assert.fail("DOMException expected");
        }
        catch (final DOMException e) {
            Assert.assertEquals("Too many parameters for rgb function.", e.getMessage());
        }
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssText() throws Exception {
        final LexicalUnit rgbLu = LexicalUnitImpl.createNumber(null, 10);
        LexicalUnit lu = LexicalUnitImpl.createComma(rgbLu);
        lu = LexicalUnitImpl.createNumber(lu, 20);
        lu = LexicalUnitImpl.createComma(lu);
        lu = LexicalUnitImpl.createNumber(lu, 30);

        final RGBColorImpl rgb = new RGBColorImpl(rgbLu);

        Assert.assertEquals("rgb(10, 20, 30)", rgb.getCssText());
        Assert.assertEquals("rgb(10, 20, 30)", rgb.getCssText(null));
        Assert.assertEquals("rgb(10, 20, 30)", rgb.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssTextAsHex() throws Exception {
        final LexicalUnit rgbLu = LexicalUnitImpl.createNumber(null, 10);
        LexicalUnit lu = LexicalUnitImpl.createComma(rgbLu);
        lu = LexicalUnitImpl.createNumber(lu, 20);
        lu = LexicalUnitImpl.createComma(lu);
        lu = LexicalUnitImpl.createNumber(lu, 5);

        final RGBColorImpl rgb = new RGBColorImpl(rgbLu);
        final CSSFormat format = new CSSFormat();

        format.setRgbAsHex(true);
        Assert.assertEquals("#0a1405", rgb.getCssText(format));

        format.setRgbAsHex(false);
        Assert.assertEquals("rgb(10, 20, 5)", rgb.getCssText(format));
    }
}
