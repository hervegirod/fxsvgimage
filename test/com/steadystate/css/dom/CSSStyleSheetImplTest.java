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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.format.CSSFormat;
import com.steadystate.css.parser.CSSOMParser;

/**
/**
 * Unit tests for {@link CSSStyleSheetImpl}.
 *
 * @author rbri
 */
public class CSSStyleSheetImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void insertRule() throws Exception {
        final CSSStyleSheetImpl ss = parseStyleSheet("");

        ss.insertRule(".testStyle { height: 42px; }", 0);
        Assert.assertEquals(".testStyle { height: 42px }", ss.getCssRules().item(0).getCssText());

        ss.insertRule(".testStyle { height: 43px; }", 0);
        Assert.assertEquals(".testStyle { height: 43px }", ss.getCssRules().item(0).getCssText());
        Assert.assertEquals(".testStyle { height: 42px }", ss.getCssRules().item(1).getCssText());

        ss.insertRule(".testStyle { height: 44px; }", 2);
        Assert.assertEquals(".testStyle { height: 43px }", ss.getCssRules().item(0).getCssText());
        Assert.assertEquals(".testStyle { height: 42px }", ss.getCssRules().item(1).getCssText());
        Assert.assertEquals(".testStyle { height: 44px }", ss.getCssRules().item(2).getCssText());

        ss.insertRule(".testStyle { height: 45px; }", 2);
        Assert.assertEquals(".testStyle { height: 43px }", ss.getCssRules().item(0).getCssText());
        Assert.assertEquals(".testStyle { height: 42px }", ss.getCssRules().item(1).getCssText());
        Assert.assertEquals(".testStyle { height: 45px }", ss.getCssRules().item(2).getCssText());
        Assert.assertEquals(".testStyle { height: 44px }", ss.getCssRules().item(3).getCssText());
    }

    /**
     * Regression test for bug 2123264.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void insertRuleWithLeadingWhitespace() throws Exception {
        final CSSStyleSheetImpl ss = parseStyleSheet("");

        ss.insertRule(" .testStyleDef { height: 42px; }", 0);
        Assert.assertEquals(".testStyleDef { height: 42px }", ss.getCssRules().item(0).getCssText());

        ss.insertRule("      .testStyleDef { height: 43px;}   ", 0);
        Assert.assertEquals(".testStyleDef { height: 43px }", ss.getCssRules().item(0).getCssText());
        Assert.assertEquals(".testStyleDef { height: 42px }", ss.getCssRules().item(1).getCssText());

        ss.insertRule("\t.testStyleDef { height: 44px; }\r\n", 0);
        Assert.assertEquals(".testStyleDef { height: 44px }", ss.getCssRules().item(0).getCssText());
        Assert.assertEquals(".testStyleDef { height: 43px }", ss.getCssRules().item(1).getCssText());
        Assert.assertEquals(".testStyleDef { height: 42px }", ss.getCssRules().item(2).getCssText());
    }

    /**
     * Regression test for bug 2123264.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void insertRuleWithoutDeclaration() throws Exception {
        final CSSStyleSheet ss = parseStyleSheet("");

        try {
            ss.insertRule(".testStyleDef", 0);
            Assert.fail("DOMException expected");
        }
        catch (final DOMException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().startsWith("Syntax error"));
            Assert.assertEquals(0, ss.getCssRules().getLength());
        }
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void insertRuleReadOnly() throws Exception {
        final CSSStyleSheetImpl ss = parseStyleSheet("");
        ss.setReadOnly(true);

        try {
            ss.insertRule(".testStyleDef", 0);
            Assert.fail("DOMException expected");
        }
        catch (final DOMException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().startsWith("This style sheet is read only"));
            Assert.assertEquals(0, ss.getCssRules().getLength());
        }

    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void insertRuleRuleOrderCharset() throws Exception {
        final CSSStyleSheet ss = parseStyleSheet("");

        ss.insertRule("@charset \"US-ASCII\";", 0);
        try {
            ss.insertRule("@charset \"US-ASCII\";", 0);
            Assert.fail("DOMException expected");
        }
        catch (final DOMException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().startsWith("A charset rule already exists"));
        }
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void insertRuleRuleImport() throws Exception {
        final CSSStyleSheet ss = parseStyleSheet("");

        ss.insertRule("@import \"great.css\";", 0);

        ss.insertRule("@charset \"US-ASCII\";", 0);
        ss.insertRule("@import \"great.css\";", 1);

        try {
            ss.insertRule("testStyleDef { height: 42px }", 0);
            Assert.fail("DOMException expected");
        }
        catch (final DOMException e) {
            Assert.assertTrue(e.getMessage(),
                    e.getMessage().startsWith("Can't insert a rule before the last charset or import rule"));
        }
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void deleteRule() throws Exception {
        final CSSStyleSheet ss = parseStyleSheet("test { height: 42px }");

        ss.deleteRule(0);
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void deleteRuleWrongIndex() throws Exception {
        final CSSStyleSheet ss = parseStyleSheet("");

        try {
            ss.deleteRule(7);
            Assert.fail("DOMException expected");
        }
        catch (final DOMException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().startsWith("Index out of bounds error"));
            Assert.assertEquals(0, ss.getCssRules().getLength());
        }
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void deleteRuleReadOnly() throws Exception {
        final CSSStyleSheetImpl ss = parseStyleSheet("");
        ss.setReadOnly(true);

        try {
            ss.deleteRule(0);
        }
        catch (final DOMException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().startsWith("This style sheet is read only"));
        }

        Assert.assertEquals(0, ss.getCssRules().getLength());
    }

    /**
     * Test serialization.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void serialize() throws Exception {
        final String cssText =
            "h1 {\n"
            + "  font-size: 2em\n"
            + "}\n"
            + "\n"
            + "@media handheld {\n"
            + "  h1 {\n"
            + "    font-size: 1.5em\n"
            + "  }\n"
            + "}";
        final InputSource source = new InputSource(new StringReader(cssText));
        final CSSOMParser cssomParser = new CSSOMParser();
        final CSSStyleSheet css = cssomParser.parseStyleSheet(source, null,
            "http://www.example.org/css/style.css");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(css);
        oos.flush();
        oos.close();

        final byte[] bytes = baos.toByteArray();
        final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        final Object o = ois.readObject();
        Assert.assertEquals(css, o);
    }

    private CSSStyleSheetImpl parseStyleSheet(final String rule) throws Exception {
        final InputSource is = new InputSource(new StringReader(rule));
        final CSSStyleSheet ss = new CSSOMParser().parseStyleSheet(is, null, null);

        return (CSSStyleSheetImpl) ss;
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssTextFormated() throws Exception {
        final CSSStyleSheetImpl value = parseStyleSheet("h1{color:blue}");

        Assert.assertEquals("h1 { color: blue }", value.getCssText());
        Assert.assertEquals("h1 { color: blue }", value.getCssText(null));
        Assert.assertEquals("h1 { color: blue }", value.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssTextFormatedRgbAsHex() throws Exception {
        final CSSStyleSheetImpl value = parseStyleSheet("h1{ color: rgb(1,2,3)}");

        final CSSFormat format = new CSSFormat();

        format.setRgbAsHex(true);
        Assert.assertEquals("h1 { color: #010203 }", value.getCssText(format));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssTextFormatedRgbAsHex2() throws Exception {
        final CSSStyleSheetImpl value = parseStyleSheet("h1 { border: 1px solid rgb(17,2,3) }");

        final CSSFormat format = new CSSFormat();

        format.setRgbAsHex(true);
        Assert.assertEquals("h1 { border: 1px solid #110203 }", value.getCssText(format));
    }
}
