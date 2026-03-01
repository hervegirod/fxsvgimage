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

import java.io.StringReader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.format.CSSFormat;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.CSSVersion;

/**
/**
 * Unit tests for {@link CSSFontFaceRuleImpl}.
 *
 * @author rbri
 */
public class CSSFontFaceRuleImplTest {

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssText() throws Exception {
        final CSSFontFaceRuleImpl value = parseFontFaceRule("@font-face { font-family: 'Scarface' }");

        Assert.assertEquals("@font-face {font-family: \"Scarface\"}", value.getCssText());
        Assert.assertEquals(CSSRule.FONT_FACE_RULE, value.getType());
        Assert.assertEquals("@font-face {font-family: \"Scarface\"}", value.toString());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void setCssText() throws Exception {
        final CSSFontFaceRuleImpl value = parseFontFaceRule("@font-face { font-family: 'Scarface' }");

        Assert.assertEquals("@font-face {font-family: \"Scarface\"}", value.getCssText());
        Assert.assertEquals(CSSRule.FONT_FACE_RULE, value.getType());
        Assert.assertEquals("@font-face {font-family: \"Scarface\"}", value.toString());

        value.setCssText("@font-face { font-family: 'Ariel'; font-style: 'cute'; }");
        Assert.assertEquals("@font-face {font-family: \"Ariel\"; font-style: \"cute\"}", value.getCssText());
        Assert.assertEquals(CSSRule.FONT_FACE_RULE, value.getType());
        Assert.assertEquals("@font-face {font-family: \"Ariel\"; font-style: \"cute\"}", value.toString());

    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getStyle() throws Exception {
        CSSFontFaceRuleImpl value = parseFontFaceRule("@font-face { font-family: 'Scarface' }");
        Assert.assertEquals("font-family: \"Scarface\"", value.getStyle().toString());

        value = parseFontFaceRule("@font-face { font-style: cute; }");
        Assert.assertEquals("font-style: cute", value.getStyle().toString());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void type() throws Exception {
        final CSSFontFaceRuleImpl value = new CSSFontFaceRuleImpl();

        Assert.assertEquals(CSSRule.FONT_FACE_RULE, value.getType());
        Assert.assertEquals("@font-face {}", value.toString());
    }

    private CSSFontFaceRuleImpl parseFontFaceRule(final String rule) throws Exception {
        final InputSource is = new InputSource(new StringReader(rule));
        final CSSStyleSheet ss = new CSSOMParser(CSSVersion.CSS3).parseStyleSheet(is, null, null);

        final CSSFontFaceRuleImpl value = (CSSFontFaceRuleImpl) ss.getCssRules().item(0);
        return value;
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssTextFormated() throws Exception {
        final CSSFontFaceRuleImpl value = parseFontFaceRule("@font-face { font-family: 'Scarface' }");

        Assert.assertEquals("@font-face {font-family: \"Scarface\"}", value.getCssText());
        Assert.assertEquals("@font-face {font-family: \"Scarface\"}", value.getCssText(null));
        Assert.assertEquals("@font-face {font-family: \"Scarface\"}", value.getCssText(new CSSFormat()));
    }
}
