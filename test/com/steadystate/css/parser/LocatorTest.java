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

package com.steadystate.css.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.Parser;
import org.w3c.dom.css.CSSCharsetRule;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSPageRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSUnknownRule;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.stylesheets.MediaList;

import com.steadystate.css.dom.CSSOMObject;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.Property;
import com.steadystate.css.userdata.UserDataConstants;

/**
 * Testcases for correct error location reporting.
 */
public class LocatorTest {

    private static final String CHARSET_RULE = "@charset \"utf-8\";\n";
    private static final String SIMPLE_IMPORT_RULE =
        "@import url('./import.css');\n";
    private static final String IMPORT_RULE =
        "@import url('./import.css') screen, projection;\n";
    private static final String UNKNOWN_AT_RULE = "@foo bar;\n";
    private static final String FONT_FACE_RULE = "@font-face {\n"
        + "  font-family: \"Robson Celtic\";\n"
        + "  src: url('http://site/fonts/rob-celt')\n"
        + "}\n";
    private static final String PAGE_RULE = "@page :left {\n"
        + "  margin: 3cm\n"
        + "}\n";
    private static final String MEDIA_RULE_START = "@media handheld {\n";
    private static final String STYLE_RULE =
        "h1, h2, .foo, h3#bar {\n"
        + "  font-weight: bold;\n"
        + "  padding-left: -1px;\n"
        + "  border-right: medium solid #00f\n"
        + "}\n";

    @Test
    public void locationsCSS1() {
        final String cssCode = SIMPLE_IMPORT_RULE
            + UNKNOWN_AT_RULE
            + STYLE_RULE;
        final Map<Character, List<Integer[]>> positions =
            new Hashtable<Character, List<Integer[]>>();
        final List<Integer[]> rPos = new ArrayList<Integer[]>();
        rPos.add(new Integer[] {1, 1});
        rPos.add(new Integer[] {2, 1});
        rPos.add(new Integer[] {3, 1});
        positions.put('R', rPos);
        final List<Integer[]> pPos = new ArrayList<Integer[]>();
        pPos.add(new Integer[] {4, 3});
        pPos.add(new Integer[] {5, 3});
        pPos.add(new Integer[] {6, 3});
        positions.put('P', pPos);
        final List<Integer[]> vPos = new ArrayList<Integer[]>();
        vPos.add(new Integer[] {4, 16});
        vPos.add(new Integer[] {5, 17});
        vPos.add(new Integer[] {6, 17});
        vPos.add(new Integer[] {6, 17});
        vPos.add(new Integer[] {6, 24});
        vPos.add(new Integer[] {6, 30});
        positions.put('V', vPos);

        locations(new SACParserCSS1(), cssCode, positions);
    }

    @Test
    public void locationsCSS2() {
        final String cssCode = CHARSET_RULE
            + IMPORT_RULE
            + UNKNOWN_AT_RULE
            + PAGE_RULE
            + FONT_FACE_RULE
            + MEDIA_RULE_START
            + STYLE_RULE
            + "}\n";
        final Map<Character, List<Integer[]>> positions =
            new Hashtable<Character, List<Integer[]>>();
        final List<Integer[]> rPos = new ArrayList<Integer[]>();
        rPos.add(new Integer[] {1, 1});
        rPos.add(new Integer[] {2, 1});
        rPos.add(new Integer[] {3, 1});
        rPos.add(new Integer[] {4, 1});
        rPos.add(new Integer[] {7, 1});
        rPos.add(new Integer[] {11, 1});
        rPos.add(new Integer[] {12, 1});
        positions.put('R', rPos);
        final List<Integer[]> mPos = new ArrayList<Integer[]>();
        mPos.add(new Integer[] {2, 29});
        mPos.add(new Integer[] {11, 16});
        positions.put('M', mPos);
        final List<Integer[]> pPos = new ArrayList<Integer[]>();
        pPos.add(new Integer[] {5, 3});
        pPos.add(new Integer[] {8, 3});
        pPos.add(new Integer[] {9, 3});
        pPos.add(new Integer[] {13, 3});
        pPos.add(new Integer[] {14, 3});
        pPos.add(new Integer[] {15, 3});
        positions.put('P', pPos);
        final List<Integer[]> vPos = new ArrayList<Integer[]>();
        vPos.add(new Integer[] {5, 11});
        vPos.add(new Integer[] {8, 16});
        vPos.add(new Integer[] {9, 8});
        vPos.add(new Integer[] {13, 16});
        vPos.add(new Integer[] {14, 17});
        vPos.add(new Integer[] {15, 17});
        vPos.add(new Integer[] {15, 17});
        vPos.add(new Integer[] {15, 24});
        vPos.add(new Integer[] {15, 30});
        positions.put('V', vPos);
        locations(new SACParserCSS2(), cssCode, positions);
    }

    @Test
    public void locationsCSS21() {
        final String cssCode = CHARSET_RULE
            + IMPORT_RULE
            + UNKNOWN_AT_RULE
            + PAGE_RULE
            + FONT_FACE_RULE
            + MEDIA_RULE_START
            + STYLE_RULE
            + "}\n";
        final Map<Character, List<Integer[]>> positions =
            new Hashtable<Character, List<Integer[]>>();
        final List<Integer[]> rPos = new ArrayList<Integer[]>();
        rPos.add(new Integer[] {1, 1});
        rPos.add(new Integer[] {2, 1});
        rPos.add(new Integer[] {3, 1});
        rPos.add(new Integer[] {4, 1});
        rPos.add(new Integer[] {7, 1});
        rPos.add(new Integer[] {11, 1});
        rPos.add(new Integer[] {12, 1});
        positions.put('R', rPos);
        final List<Integer[]> mPos = new ArrayList<Integer[]>();
        mPos.add(new Integer[] {2, 29});
        mPos.add(new Integer[] {11, 8});
        positions.put('M', mPos);
        final List<Integer[]> pPos = new ArrayList<Integer[]>();
        pPos.add(new Integer[] {5, 3});
        pPos.add(new Integer[] {13, 3});
        pPos.add(new Integer[] {14, 3});
        pPos.add(new Integer[] {15, 3});
        positions.put('P', pPos);
        final List<Integer[]> vPos = new ArrayList<Integer[]>();
        vPos.add(new Integer[] {5, 11});
        vPos.add(new Integer[] {13, 16});
        vPos.add(new Integer[] {14, 17});
        vPos.add(new Integer[] {15, 17});
        vPos.add(new Integer[] {15, 17});
        vPos.add(new Integer[] {15, 24});
        vPos.add(new Integer[] {15, 30});
        positions.put('V', vPos);

        locations(new SACParserCSS21(), cssCode, positions);
    }

    @Test
    public void locationsCSS3() {
        final String cssCode = CHARSET_RULE
            + IMPORT_RULE
            + UNKNOWN_AT_RULE
            + PAGE_RULE
            + FONT_FACE_RULE
            + MEDIA_RULE_START
            + STYLE_RULE
            + "}\n";
        final Map<Character, List<Integer[]>> positions =
            new Hashtable<Character, List<Integer[]>>();
        final List<Integer[]> rPos = new ArrayList<Integer[]>();
        rPos.add(new Integer[] {1, 1});
        rPos.add(new Integer[] {2, 1});
        rPos.add(new Integer[] {3, 1});
        rPos.add(new Integer[] {4, 1});
        rPos.add(new Integer[] {7, 1});
        rPos.add(new Integer[] {11, 1});
        rPos.add(new Integer[] {12, 1});
        positions.put('R', rPos);
        final List<Integer[]> mPos = new ArrayList<Integer[]>();
        mPos.add(new Integer[] {2, 29});
        mPos.add(new Integer[] {11, 8});
        positions.put('M', mPos);
        final List<Integer[]> pPos = new ArrayList<Integer[]>();
        pPos.add(new Integer[] {5, 3});
        pPos.add(new Integer[] {8, 3});
        pPos.add(new Integer[] {9, 3});
        pPos.add(new Integer[] {13, 3});
        pPos.add(new Integer[] {14, 3});
        pPos.add(new Integer[] {15, 3});
        positions.put('P', pPos);
        final List<Integer[]> vPos = new ArrayList<Integer[]>();
        vPos.add(new Integer[] {5, 11});
        vPos.add(new Integer[] {8, 16});
        vPos.add(new Integer[] {9, 8});
        vPos.add(new Integer[] {13, 16});
        vPos.add(new Integer[] {14, 17});
        vPos.add(new Integer[] {15, 17});
        vPos.add(new Integer[] {15, 17});
        vPos.add(new Integer[] {15, 24});
        vPos.add(new Integer[] {15, 30});
        positions.put('V', vPos);

        locations(new SACParserCSS3(), cssCode, positions);
    }

    @Test
    public void locationsCSSmobileOKbasic1() {
        final String cssCode = IMPORT_RULE
            + UNKNOWN_AT_RULE
            + PAGE_RULE
            + FONT_FACE_RULE
            + MEDIA_RULE_START
            + STYLE_RULE
            + "}\n";
        final Map<Character, List<Integer[]>> positions =
            new Hashtable<Character, List<Integer[]>>();
        final List<Integer[]> rPos = new ArrayList<Integer[]>();
        rPos.add(new Integer[] {1, 1});
        rPos.add(new Integer[] {2, 1});
        rPos.add(new Integer[] {3, 1});
        rPos.add(new Integer[] {6, 1});
        rPos.add(new Integer[] {10, 1});
        rPos.add(new Integer[] {11, 1});
        rPos.add(new Integer[] {12, 1});
        positions.put('R', rPos);
        final List<Integer[]> mPos = new ArrayList<Integer[]>();
        mPos.add(new Integer[] {1, 29});
        mPos.add(new Integer[] {10, 16});
        positions.put('M', mPos);
        final List<Integer[]> pPos = new ArrayList<Integer[]>();
        pPos.add(new Integer[] {12, 3});
        pPos.add(new Integer[] {13, 3});
        pPos.add(new Integer[] {14, 3});
        positions.put('P', pPos);
        final List<Integer[]> vPos = new ArrayList<Integer[]>();
        vPos.add(new Integer[] {12, 16});
        vPos.add(new Integer[] {13, 17});
        vPos.add(new Integer[] {14, 17});
        vPos.add(new Integer[] {14, 17});
        vPos.add(new Integer[] {14, 24});
        vPos.add(new Integer[] {14, 30});
        positions.put('V', vPos);

        locations(new SACParserCSSmobileOKBasic1(), cssCode, positions);
    }

    private void locations(final Parser sacParser, final String cssCode,
            final Map<Character, List<Integer[]>> positions) {
        final Reader r = new StringReader(cssCode);
        final InputSource source = new InputSource(r);
        final CSSOMParser cssomParser = new CSSOMParser(sacParser);
        final Map<Character, Integer> counts = new Hashtable<Character, Integer>();
        counts.put('R', 0);
        counts.put('M', 0);
        counts.put('P', 0);
        counts.put('V', 0);
        try {
            final CSSStyleSheet cssStyleSheet =
                cssomParser.parseStyleSheet(source, null, null);
            final CSSRuleList cssRules = cssStyleSheet.getCssRules();
            cssRules(cssRules, positions, counts);
        }
        catch (final IOException e) {
            Assert.assertFalse(e.getLocalizedMessage(), true);
        }
    }

    private void cssRules(final CSSRuleList cssRules, final Map<Character, List<Integer[]>> positions,
            final Map<Character, Integer> counts) {
        for (int i = 0; i < cssRules.getLength(); i++) {
            cssRule(cssRules.item(i), positions, counts);
        }
    }

    private void cssRule(final CSSRule cssRule, final Map<Character, List<Integer[]>> positions,
            final Map<Character, Integer> counts) {
        if (cssRule instanceof CSSOMObject) {
            final Locator locator = getLocator((CSSOMObject) cssRule);
            final Integer[] expected = positions.get('R').get(counts.get('R'));
            final int expectedLine = expected[0];
            final int expectedColumn = expected[1];

            Assert.assertEquals(expectedLine, locator.getLineNumber());
            Assert.assertEquals(expectedColumn, locator.getColumnNumber());
            counts.put('R', counts.get('R') + 1);
        }
        switch (cssRule.getType()) {
            case CSSRule.UNKNOWN_RULE:
                final CSSUnknownRule cssUnknownRule = (CSSUnknownRule) cssRule;
                // TODO
                break;
            case CSSRule.CHARSET_RULE:
                final CSSCharsetRule cssCharsetRule = (CSSCharsetRule) cssRule;
                // TODO
                break;
            case CSSRule.IMPORT_RULE:
                final CSSImportRule cssImportRule = (CSSImportRule) cssRule;
                mediaList(cssImportRule.getMedia(), positions, counts);
                break;
            case CSSRule.MEDIA_RULE:
                final CSSMediaRule cssMediaRule = (CSSMediaRule) cssRule;
                mediaList(cssMediaRule.getMedia(), positions, counts);
                cssRules(cssMediaRule.getCssRules(), positions, counts);
                break;
            case CSSRule.PAGE_RULE:
                final CSSPageRule cssPageRule = (CSSPageRule) cssRule;
                cssStyleDeclaration(cssPageRule.getStyle(), positions, counts);
                break;
            case CSSRule.FONT_FACE_RULE:
                final CSSFontFaceRule cssFontFaceRule = (CSSFontFaceRule) cssRule;
                cssStyleDeclaration(cssFontFaceRule.getStyle(), positions, counts);
                break;
            case CSSRule.STYLE_RULE:
                final CSSStyleRule cssStyleRule = (CSSStyleRule) cssRule;
                cssStyleDeclaration(cssStyleRule.getStyle(), positions, counts);
                break;
            default:
                throw new RuntimeException("Unsupported rule type (" + cssRule.getType() + ")");
        }
    }

    private void cssStyleDeclaration(final CSSStyleDeclaration style,
            final Map<Character, List<Integer[]>> positions,
            final Map<Character, Integer> counts)    {
        if (style instanceof CSSStyleDeclarationImpl) {
            final CSSStyleDeclarationImpl csdi = (CSSStyleDeclarationImpl) style;
            final Iterator<Property> it = csdi.getProperties().iterator();
            while (it.hasNext()) {
                property(it.next(), positions, counts);
            }
        }
    }

    private void mediaList(final MediaList mediaList,
            final Map<Character, List<Integer[]>> positions,
            final Map<Character, Integer> counts) {
        if ((mediaList.getLength() > 0) && (mediaList instanceof CSSOMObject)) {
            final Locator locator = getLocator((CSSOMObject) mediaList);
            final Integer[] expected = positions.get('M').get(counts.get('M'));
            final int expectedLine = expected[0];
            final int expectedColumn = expected[1];

            Assert.assertEquals(expectedLine, locator.getLineNumber());
            Assert.assertEquals(expectedColumn, locator.getColumnNumber());
            counts.put('M', counts.get('M') + 1);
        }
    }

    private void property(final Property property,
            final Map<Character, List<Integer[]>> positions,
            final Map<Character, Integer> counts) {
        final Locator locator = getLocator(property);
        final Integer[] expected = positions.get('P').get(counts.get('P'));
        final int expectedLine = expected[0];
        final int expectedColumn = expected[1];

        Assert.assertEquals(expectedLine, locator.getLineNumber());
        Assert.assertEquals(expectedColumn, locator.getColumnNumber());
        counts.put('P', counts.get('P') + 1);
        cssValue(property.getValue(), positions, counts);
    }

    private void cssValue(final CSSValue cssValue,
            final Map<Character, List<Integer[]>> positions,
            final Map<Character, Integer> counts) {
        if (cssValue instanceof CSSValueImpl) {
            final Locator locator = getLocator((CSSValueImpl) cssValue);
            final Integer[] expected = positions.get('V').get(counts.get('V'));
            final int expectedLine = expected[0];
            final int expectedColumn = expected[1];

            Assert.assertEquals(expectedLine, locator.getLineNumber());
            Assert.assertEquals(expectedColumn, locator.getColumnNumber());
            counts.put('V', counts.get('V') + 1);
        }
        if (cssValue.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            final CSSValueList cssValueList = (CSSValueList) cssValue;
            for (int i = 0; i < cssValueList.getLength(); i++) {
                cssValue(cssValueList.item(i), positions, counts);
            }
        }
    }

    private Locator getLocator(final CSSOMObject cssomObject) {
        return (Locator) cssomObject.getUserData(UserDataConstants.KEY_LOCATOR);
    }
}
