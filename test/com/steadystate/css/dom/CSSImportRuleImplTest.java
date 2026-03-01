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
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.format.CSSFormat;
import com.steadystate.css.parser.CSSOMParser;

/**
/**
 * Unit tests for {@link CSSImportRuleImpl}.
 *
 * @author rbri
 */
public class CSSImportRuleImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssText() throws Exception {
        final CSSImportRuleImpl value = parseImportRule("@import \"ext.css\";");

        Assert.assertEquals("@import url(ext.css);", value.getCssText());
        Assert.assertEquals(CSSRule.IMPORT_RULE, value.getType());
        Assert.assertEquals("@import url(ext.css);", value.toString());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void setCssText() throws Exception {
        final CSSImportRuleImpl value = parseImportRule("@import \"ext.css\";");

        Assert.assertEquals("@import url(ext.css);", value.getCssText());
        Assert.assertEquals(CSSRule.IMPORT_RULE, value.getType());
        Assert.assertEquals("@import url(ext.css);", value.toString());

        value.setCssText("@import url(cool.css);");
        Assert.assertEquals("@import url(cool.css);", value.getCssText());
        Assert.assertEquals(CSSRule.IMPORT_RULE, value.getType());
        Assert.assertEquals("@import url(cool.css);", value.toString());

    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getHref() throws Exception {
        final CSSImportRuleImpl value = parseImportRule("@import \"ext.css\";");
        Assert.assertEquals("ext.css", value.getHref());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void setHref() throws Exception {
        final CSSImportRuleImpl value = parseImportRule("@import \"ext.css\";");
        Assert.assertEquals("ext.css", value.getHref());

        value.setHref("coOl");
        Assert.assertEquals("coOl", value.getHref());
        Assert.assertEquals("@import url(coOl);", value.getCssText());
        Assert.assertEquals("@import url(coOl);", value.toString());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void type() throws Exception {
        final CSSImportRuleImpl value = new CSSImportRuleImpl();

        Assert.assertEquals(CSSRule.IMPORT_RULE, value.getType());
        Assert.assertEquals("@import;", value.toString());
    }

    private CSSImportRuleImpl parseImportRule(final String rule) throws Exception {
        final InputSource is = new InputSource(new StringReader(rule));
        final CSSStyleSheet ss = new CSSOMParser().parseStyleSheet(is, null, null);

        final CSSImportRuleImpl value = (CSSImportRuleImpl) ss.getCssRules().item(0);
        return value;
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssTextFormated() throws Exception {
        final CSSImportRuleImpl value = parseImportRule("@import \"ext.css\";");

        Assert.assertEquals("@import url(ext.css);", value.getCssText());
        Assert.assertEquals("@import url(ext.css);", value.getCssText(null));
        Assert.assertEquals("@import url(ext.css);", value.getCssText(new CSSFormat()));
    }
}
