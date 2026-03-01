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

package com.steadystate.css;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.CSSVersion;

/**
 * Tests the CSS DOM implementation by loading a stylesheet and performing a few operations upon it.
 *
 * @author David Schweinsberg
 * @author rbri
 */
public class DomTest {

    @Test
    public void test() throws Exception {
        final String cssText =
            "foo: 1.5; bogus: 3, 2, 1; bar-color: #0FEED0; background: #abc; foreground: rgb( 10, 20, 30 )";

        final CSSOMParser parser = new CSSOMParser(CSSVersion.CSS21);
        final ErrorHandler errorHandler = new ErrorHandler();
        parser.setErrorHandler(errorHandler);

        final InputSource source = new InputSource(new StringReader(cssText));
        final CSSStyleDeclaration style = parser.parseStyleDeclaration(source);

        Assert.assertEquals(0, errorHandler.getErrorCount());
        Assert.assertEquals(0, errorHandler.getFatalErrorCount());
        Assert.assertEquals(0, errorHandler.getWarningCount());

        // Enumerate the properties and retrieve their values
        Assert.assertEquals(5, style.getLength());

        String name = style.item(0);
        Assert.assertEquals("foo : 1.5", name + " : " + style.getPropertyValue(name));
        name = style.item(1);
        Assert.assertEquals("bogus : 3, 2, 1", name + " : " + style.getPropertyValue(name));
        name = style.item(2);
        Assert.assertEquals("bar-color : rgb(15, 238, 208)", name + " : " + style.getPropertyValue(name));
        name = style.item(3);
        Assert.assertEquals("background : rgb(170, 187, 204)", name + " : " + style.getPropertyValue(name));
        name = style.item(4);
        Assert.assertEquals("foreground : rgb(10, 20, 30)", name + " : " + style.getPropertyValue(name));

        // Get the style declaration as a single lump of text
        Assert.assertEquals("foo: 1.5; "
                + "bogus: 3, 2, 1; "
                + "bar-color: rgb(15, 238, 208); "
                + "background: rgb(170, 187, 204); "
                + "foreground: rgb(10, 20, 30)", style.getCssText());

        // Directly set the CSS style declaration
        style.setCssText("alpha: 2; beta: 20px; gamma: 40em; delta: 1mm; epsilon: 24pt");
        Assert.assertEquals("alpha: 2; beta: 20px; gamma: 40em; delta: 1mm; epsilon: 24pt", style.getCssText());

        // Remove some properties, from the middle, beginning, and end
        style.removeProperty("gamma");
        Assert.assertEquals("alpha: 2; beta: 20px; delta: 1mm; epsilon: 24pt", style.getCssText());

        style.removeProperty("alpha");
        Assert.assertEquals("beta: 20px; delta: 1mm; epsilon: 24pt", style.getCssText());

        style.removeProperty("epsilon");
        Assert.assertEquals("beta: 20px; delta: 1mm", style.getCssText());

        // Use the setProperty method to modify an existing property,
        // and add a new one.
        style.setProperty("beta", "40px", null);
        Assert.assertEquals("beta: 40px; delta: 1mm", style.getCssText());

        style.setProperty("omega", "1", "important");
        Assert.assertEquals("beta: 40px; delta: 1mm; omega: 1 !important", style.getCssText());

        // Work with CSSValues
        CSSPrimitiveValue value = (CSSPrimitiveValue) style.getPropertyCSSValue("beta");
        Assert.assertEquals("40px", value.getCssText());
        Assert.assertEquals(40f, value.getFloatValue(CSSPrimitiveValue.CSS_PX), 0.000000f);

        value.setFloatValue(CSSPrimitiveValue.CSS_PX, 100);
        Assert.assertEquals("100", value.getCssText());

        style.setProperty("list-test", "100 200 300", null);
        Assert.assertEquals("beta: 100; delta: 1mm; omega: 1 !important; list-test: 100 200 300", style.getCssText());

        value = (CSSPrimitiveValue) style.getPropertyCSSValue("list-test");
        Assert.assertEquals(CSSValue.CSS_VALUE_LIST, value.getCssValueType());

        final CSSValueList vl = (CSSValueList) style.getPropertyCSSValue("list-test");
        Assert.assertEquals(3, vl.getLength());

        value = (CSSPrimitiveValue) vl.item(0);
        Assert.assertEquals(100, value.getFloatValue(CSSPrimitiveValue.CSS_NUMBER), 0.000000f);

        value = (CSSPrimitiveValue) vl.item(1);
        Assert.assertEquals(200, value.getFloatValue(CSSPrimitiveValue.CSS_NUMBER), 0.000000f);

        value = (CSSPrimitiveValue) vl.item(2);
        Assert.assertEquals(300, value.getFloatValue(CSSPrimitiveValue.CSS_NUMBER), 0.000000f);

        // When a CSSValue is modified, it modifies the declaration
        Assert.assertEquals("beta: 100; delta: 1mm; omega: 1 !important; list-test: 100 200 300", style.getCssText());

        // Using the setCssText method, we can change the type of value
        vl.setCssText("bogus");
        Assert.assertEquals(CSSValue.CSS_PRIMITIVE_VALUE, value.getCssValueType());
        Assert.assertEquals("beta: 100; delta: 1mm; omega: 1 !important; list-test: bogus", style.getCssText());
    }

    @Test
    public void inheritGetStringValue() throws Exception {
        final String cssText = "p { font-size: 2em } p a:link { font-size: inherit }";
        final InputSource source = new InputSource(new StringReader(cssText));
        final CSSOMParser cssomParser = new CSSOMParser();

        final CSSStyleSheet css = cssomParser.parseStyleSheet(source, null, "http://www.example.org/css/style.css");

        final CSSRuleList rules = css.getCssRules();
        Assert.assertEquals(2, rules.getLength());

        Assert.assertEquals("p { font-size: 2em }", rules.item(0).getCssText());
        Assert.assertEquals("p a:link { font-size: inherit }", rules.item(1).getCssText());
    }
}
