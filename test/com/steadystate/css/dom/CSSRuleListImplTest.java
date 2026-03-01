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

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.format.CSSFormat;
import com.steadystate.css.parser.CSSOMParser;

/**
/**
 * Unit tests for {@link CSSPageRuleImpl}.
 *
 * @author rbri
 */
public class CSSRuleListImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void defaultConstructor() throws Exception {
        final CSSRuleListImpl rl = new CSSRuleListImpl();
        Assert.assertEquals("", rl.toString());
        Assert.assertTrue(rl.getRules().isEmpty());
        Assert.assertEquals(0, rl.getLength());
        Assert.assertNull(rl.item(0));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getLength() throws Exception {
        final CSSRuleListImpl rl = new CSSRuleListImpl();
        Assert.assertEquals(0, rl.getLength());

        rl.add(new CSSFontFaceRuleImpl());
        Assert.assertEquals(1, rl.getLength());

        rl.add(new CSSFontFaceRuleImpl());
        Assert.assertEquals(2, rl.getLength());

        rl.add(new CSSFontFaceRuleImpl());
        Assert.assertEquals(3, rl.getLength());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void item() throws Exception {
        final CSSRuleListImpl rl = new CSSRuleListImpl();
        Assert.assertEquals(null, rl.item(-1));
        Assert.assertEquals(null, rl.item(0));
        Assert.assertEquals(null, rl.item(1));
        Assert.assertEquals(null, rl.item(10));

        rl.add(new CSSFontFaceRuleImpl());
        Assert.assertEquals(null, rl.item(-1));
        Assert.assertEquals("@font-face {}", rl.item(0).toString());
        Assert.assertEquals(null, rl.item(1));
        Assert.assertEquals(null, rl.item(10));

        rl.add(new CSSCharsetRuleImpl());
        Assert.assertEquals(null, rl.item(-1));
        Assert.assertEquals("@font-face {}", rl.item(0).toString());
        Assert.assertEquals("@charset \"\";", rl.item(1).toString());
        Assert.assertEquals(null, rl.item(2));
        Assert.assertEquals(null, rl.item(10));

        rl.add(new CSSImportRuleImpl());
        Assert.assertEquals(null, rl.item(-1));
        Assert.assertEquals("@font-face {}", rl.item(0).toString());
        Assert.assertEquals("@charset \"\";", rl.item(1).toString());
        Assert.assertEquals("@import;", rl.item(2).toString());
        Assert.assertEquals(null, rl.item(3));
        Assert.assertEquals(null, rl.item(10));
    }

    private CSSRuleListImpl parseRuleList(final String rules) throws Exception {
        final InputSource is = new InputSource(new StringReader(rules));
        final CSSStyleSheet ss = new CSSOMParser().parseStyleSheet(is, null, null);

        return (CSSRuleListImpl) ss.getCssRules();
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssTextFormated() throws Exception {
        final CSSRuleListImpl value = parseRuleList("h1 {} h2 {color:green}");

        Assert.assertEquals("h1 { }\r\nh2 { color: green }", value.getCssText());
        Assert.assertEquals("h1 { }\r\nh2 { color: green }", value.getCssText(null));
        Assert.assertEquals("h1 { }\r\nh2 { color: green }", value.getCssText(new CSSFormat()));
    }
}
