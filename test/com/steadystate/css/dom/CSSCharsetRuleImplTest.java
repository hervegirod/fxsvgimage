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
 * Unit tests for {@link CSSCharsetRuleImpl}.
 *
 * @author rbri
 */
public class CSSCharsetRuleImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssText() throws Exception {
        final CSSCharsetRuleImpl value = parseCharsetRule("@charset \"utf-8\";");

        Assert.assertEquals("@charset \"utf-8\";", value.getCssText());
        Assert.assertEquals(CSSRule.CHARSET_RULE, value.getType());
        Assert.assertEquals("@charset \"utf-8\";", value.toString());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void setCssText() throws Exception {
        final CSSCharsetRuleImpl value = parseCharsetRule("@charset \"utf-8\";");

        Assert.assertEquals("@charset \"utf-8\";", value.getCssText());
        Assert.assertEquals(CSSRule.CHARSET_RULE, value.getType());
        Assert.assertEquals("@charset \"utf-8\";", value.toString());

        value.setCssText("@charset \"ASCII\";");
        Assert.assertEquals("@charset \"ASCII\";", value.getCssText());
        Assert.assertEquals(CSSRule.CHARSET_RULE, value.getType());
        Assert.assertEquals("@charset \"ASCII\";", value.toString());

    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getSelectorText() throws Exception {
        final CSSCharsetRuleImpl value = parseCharsetRule("@charset \"Deutsch\";");
        Assert.assertEquals("Deutsch", value.getEncoding());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void setEncoding() throws Exception {
        final CSSCharsetRuleImpl value = parseCharsetRule("@charset \"UTF-8\";");
        Assert.assertEquals("UTF-8", value.getEncoding());

        value.setEncoding("EBCDIC");
        Assert.assertEquals("EBCDIC", value.getEncoding());
        Assert.assertEquals("@charset \"EBCDIC\";", value.getCssText());
        Assert.assertEquals("@charset \"EBCDIC\";", value.toString());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void type() throws Exception {
        final CSSCharsetRuleImpl value = new CSSCharsetRuleImpl();

        Assert.assertEquals(CSSRule.CHARSET_RULE, value.getType());
        Assert.assertEquals("@charset \"\";", value.toString());
    }

    private CSSCharsetRuleImpl parseCharsetRule(final String rule) throws Exception {
        final InputSource is = new InputSource(new StringReader(rule));
        final CSSStyleSheet ss = new CSSOMParser().parseStyleSheet(is, null, null);

        final CSSCharsetRuleImpl value = (CSSCharsetRuleImpl) ss.getCssRules().item(0);
        return value;
    }
    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssTextFormated() throws Exception {
        final CSSCharsetRuleImpl value = parseCharsetRule("@charset \"utf-8\";");

        Assert.assertEquals("@charset \"utf-8\";", value.getCssText());
        Assert.assertEquals("@charset \"utf-8\";", value.getCssText(null));
        Assert.assertEquals("@charset \"utf-8\";", value.getCssText(new CSSFormat()));
    }
}
