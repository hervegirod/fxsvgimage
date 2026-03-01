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
package com.steadystate.css.parser.media;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.MediaList;

import com.steadystate.css.dom.CSSMediaRuleImpl;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.MediaListImpl;
import com.steadystate.css.dom.Property;
import com.steadystate.css.parser.AbstractSACParserTestUtils;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.CSSVersion;
import com.steadystate.css.parser.LexicalUnitImpl;
import com.steadystate.css.parser.ParserConfiguration;
import com.steadystate.css.parser.SACMediaListImpl;
import com.steadystate.css.parser.SACParser;
import org.junit.BeforeClass;

/**
 * @author rbri
 */
public class CSS3MediaTest extends AbstractSACParserTestUtils {
   
   @BeforeClass
   public static void setUpClass() {
      ParserConfiguration.getInstance().setVersion(CSSVersion.CSS3);
   }

   @Override
   protected SACParser sacParser() {
      return CSSOMParser.newParser();
   }   

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void mediaConditions() throws Exception {
      final String css = "@media screen and (max-width: 30em) { h1 { color: red } }";
      final CSSStyleSheet sheet = parse(css);
      final CSSRule cssRule = sheet.getCssRules().item(0);
      Assert.assertTrue(cssRule instanceof CSSMediaRuleImpl);
      Assert.assertTrue(((CSSMediaRuleImpl) cssRule).getMedia() instanceof MediaListImpl);
      
      final MediaList mediaList = ((CSSMediaRuleImpl) cssRule).getMedia();
      Assert.assertEquals("screen and (max-width: 30em)", mediaList.getMediaText());
      Assert.assertEquals(1, mediaList.getLength());
      
      final MediaListImpl mediaListImpl = (MediaListImpl) mediaList;
      Assert.assertEquals("screen", mediaListImpl.mediaQuery(0).getMedia());
      Assert.assertEquals("screen and (max-width: 30em)", mediaListImpl.mediaQuery(0).toString());
      Assert.assertEquals(1, mediaListImpl.mediaQuery(0).getProperties().size());
      Assert.assertEquals("max-width: 30em", mediaListImpl.mediaQuery(0).getProperties().get(0).toString());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void withoutValue() throws Exception {
      final String css = "@media screen and (color) { h1 { color: red } }";
      final CSSStyleSheet sheet = parse(css);
      final CSSRule cssRule = sheet.getCssRules().item(0);
      Assert.assertTrue(cssRule instanceof CSSMediaRuleImpl);
      Assert.assertTrue(((CSSMediaRuleImpl) cssRule).getMedia() instanceof MediaListImpl);
      
      final MediaList mediaList = ((CSSMediaRuleImpl) cssRule).getMedia();
      Assert.assertEquals("screen and (color)", mediaList.getMediaText());
      Assert.assertEquals(1, mediaList.getLength());
      
      final MediaListImpl mediaListImpl = (MediaListImpl) mediaList;
      Assert.assertEquals("screen", mediaListImpl.mediaQuery(0).getMedia());
      Assert.assertEquals("screen and (color)", mediaListImpl.mediaQuery(0).toString());
      Assert.assertEquals(1, mediaListImpl.mediaQuery(0).getProperties().size());
      Assert.assertEquals("color", mediaListImpl.mediaQuery(0).getProperties().get(0).toString());
   }

   /**
    * see http://keithclark.co.uk/articles/moving-ie-specific-css-into-media-blocks/media-tests/
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void mediaBlockHacks() throws Exception {
      // TODO have to check this in more detail
      String css = "@media\\0 { h1 { color: red } }";
      CSSStyleSheet sheet = parse(css, 0, 0, 0);
      Assert.assertEquals(1, sheet.getCssRules().getLength());
      
      css = "@media\\9 { h1 { color: red } }";
      sheet = parse(css, 0, 0, 0);
      Assert.assertEquals(1, sheet.getCssRules().getLength());
      
      css = "@media screen\\0 { h1 { color: red } }";
      sheet = parse(css, 0, 0, 0);
      Assert.assertEquals(1, sheet.getCssRules().getLength());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void only() throws Exception {
      final String css = "@media only screen and (color) { h1 { color: red } }";
      final CSSStyleSheet sheet = parse(css);
      final CSSRule cssRule = sheet.getCssRules().item(0);
      Assert.assertTrue(cssRule instanceof CSSMediaRuleImpl);
      Assert.assertTrue(((CSSMediaRuleImpl) cssRule).getMedia() instanceof MediaListImpl);
      
      final MediaList mediaList = ((CSSMediaRuleImpl) cssRule).getMedia();
      Assert.assertEquals("only screen and (color)", mediaList.getMediaText());
      Assert.assertEquals(1, mediaList.getLength());
      
      final MediaListImpl mediaListImpl = (MediaListImpl) mediaList;
      Assert.assertEquals("screen", mediaListImpl.mediaQuery(0).getMedia());
      Assert.assertEquals("only screen and (color)", mediaListImpl.mediaQuery(0).toString());
      Assert.assertFalse(mediaListImpl.mediaQuery(0).isNot());
      Assert.assertTrue(mediaListImpl.mediaQuery(0).isOnly());
      Assert.assertEquals(1, mediaListImpl.mediaQuery(0).getProperties().size());
      Assert.assertEquals("color", mediaListImpl.mediaQuery(0).getProperties().get(0).toString());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void not() throws Exception {
      final String css = "@media not screen and (color) { h1 { color: red } }";
      final CSSStyleSheet sheet = parse(css);
      final CSSRule cssRule = sheet.getCssRules().item(0);
      Assert.assertTrue(cssRule instanceof CSSMediaRuleImpl);
      Assert.assertTrue(((CSSMediaRuleImpl) cssRule).getMedia() instanceof MediaListImpl);
      
      final MediaList mediaList = ((CSSMediaRuleImpl) cssRule).getMedia();
      Assert.assertEquals("not screen and (color)", mediaList.getMediaText());
      Assert.assertEquals(1, mediaList.getLength());
      
      final MediaListImpl mediaListImpl = (MediaListImpl) mediaList;
      Assert.assertEquals("screen", mediaListImpl.mediaQuery(0).getMedia());
      Assert.assertEquals("not screen and (color)", mediaListImpl.mediaQuery(0).toString());
      Assert.assertTrue(mediaListImpl.mediaQuery(0).isNot());
      Assert.assertFalse(mediaListImpl.mediaQuery(0).isOnly());
      Assert.assertEquals(1, mediaListImpl.mediaQuery(0).getProperties().size());
      Assert.assertEquals("color", mediaListImpl.mediaQuery(0).getProperties().get(0).toString());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dpi() throws Exception {
      final String css = "@media not screen and (min-resolution: 300dpi) { h1 { color: red } }";
      final CSSStyleSheet sheet = parse(css);
      final CSSRule cssRule = sheet.getCssRules().item(0);
      Assert.assertTrue(cssRule instanceof CSSMediaRuleImpl);
      Assert.assertTrue(((CSSMediaRuleImpl) cssRule).getMedia() instanceof MediaListImpl);
      
      final MediaList mediaList = ((CSSMediaRuleImpl) cssRule).getMedia();
      Assert.assertEquals("not screen and (min-resolution: 300dpi)", mediaList.getMediaText());
      Assert.assertEquals(1, mediaList.getLength());
      
      final MediaListImpl mediaListImpl = (MediaListImpl) mediaList;
      Assert.assertEquals("screen", mediaListImpl.mediaQuery(0).getMedia());
      Assert.assertEquals("not screen and (min-resolution: 300dpi)", mediaListImpl.mediaQuery(0).toString());
      Assert.assertEquals(1, mediaListImpl.mediaQuery(0).getProperties().size());
      
      final Property prop = mediaListImpl.mediaQuery(0).getProperties().get(0);
      Assert.assertEquals("min-resolution: 300dpi", prop.toString());
      Assert.assertEquals("min-resolution", prop.getName());
      final CSSValueImpl valueImpl = (CSSValueImpl) prop.getValue();
      final LexicalUnit unitImpl = (LexicalUnitImpl) valueImpl.getValue();
      Assert.assertEquals(LexicalUnit.SAC_DIMENSION, unitImpl.getLexicalUnitType());
      Assert.assertEquals(300f, unitImpl.getFloatValue(), 0.000001);
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dpcm() throws Exception {
      final String css = "@media not screen and (min-resolution: 11.8dpcm) { h1 { color: red } }";
      final CSSStyleSheet sheet = parse(css);
      final CSSRule cssRule = sheet.getCssRules().item(0);
      Assert.assertTrue(cssRule instanceof CSSMediaRuleImpl);
      Assert.assertTrue(((CSSMediaRuleImpl) cssRule).getMedia() instanceof MediaListImpl);
      
      final MediaList mediaList = ((CSSMediaRuleImpl) cssRule).getMedia();
      Assert.assertEquals("not screen and (min-resolution: 11.8dpcm)", mediaList.getMediaText());
      Assert.assertEquals(1, mediaList.getLength());
      
      final MediaListImpl mediaListImpl = (MediaListImpl) mediaList;
      Assert.assertEquals("screen", mediaListImpl.mediaQuery(0).getMedia());
      Assert.assertEquals("not screen and (min-resolution: 11.8dpcm)", mediaListImpl.mediaQuery(0).toString());
      Assert.assertEquals(1, mediaListImpl.mediaQuery(0).getProperties().size());
      
      final Property prop = mediaListImpl.mediaQuery(0).getProperties().get(0);
      Assert.assertEquals("min-resolution: 11.8dpcm", prop.toString());
      Assert.assertEquals("min-resolution", prop.getName());
      final CSSValueImpl valueImpl = (CSSValueImpl) prop.getValue();
      final LexicalUnit unitImpl = (LexicalUnitImpl) valueImpl.getValue();
      Assert.assertEquals(LexicalUnit.SAC_DIMENSION, unitImpl.getLexicalUnitType());
      Assert.assertEquals(11.8f, unitImpl.getFloatValue(), 0.000001);
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void onlyAndAnd() throws Exception {
      final String css
         = "@media only screen and (max-width: 735px) and (max-device-width: 768px) { h1 { color: red } }";
      final CSSStyleSheet sheet = parse(css);
      final CSSRule cssRule = sheet.getCssRules().item(0);
      Assert.assertTrue(cssRule instanceof CSSMediaRuleImpl);
      Assert.assertTrue(((CSSMediaRuleImpl) cssRule).getMedia() instanceof MediaListImpl);
      
      final MediaListImpl mediaListImpl = (MediaListImpl) ((CSSMediaRuleImpl) cssRule).getMedia();
      Assert.assertEquals("only screen and (max-width: 735px) and (max-device-width: 768px)",
         mediaListImpl.getMediaText());
      Assert.assertEquals(1, mediaListImpl.getLength());
      Assert.assertEquals("screen", mediaListImpl.mediaQuery(0).getMedia());
      Assert.assertEquals("only screen and (max-width: 735px) and (max-device-width: 768px)",
         mediaListImpl.mediaQuery(0).toString());
      Assert.assertEquals(2, mediaListImpl.mediaQuery(0).getProperties().size());
      
      Property prop = mediaListImpl.mediaQuery(0).getProperties().get(0);
      Assert.assertEquals("max-width: 735px", prop.toString());
      Assert.assertEquals("max-width", prop.getName());
      CSSValueImpl valueImpl = (CSSValueImpl) prop.getValue();
      LexicalUnit unitImpl = (LexicalUnitImpl) valueImpl.getValue();
      Assert.assertEquals(LexicalUnit.SAC_PIXEL, unitImpl.getLexicalUnitType());
      Assert.assertEquals(735, unitImpl.getIntegerValue(), 0.000001);
      
      prop = mediaListImpl.mediaQuery(0).getProperties().get(1);
      Assert.assertEquals("max-device-width: 768px", prop.toString());
      Assert.assertEquals("max-device-width", prop.getName());
      valueImpl = (CSSValueImpl) prop.getValue();
      unitImpl = (LexicalUnitImpl) valueImpl.getValue();
      Assert.assertEquals(LexicalUnit.SAC_PIXEL, unitImpl.getLexicalUnitType());
      Assert.assertEquals(768, unitImpl.getIntegerValue(), 0.000001);
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void allShorthand() throws Exception {
      // all and (color)
      final String css = "@media (color) { h1 { color: red } }";
      final CSSStyleSheet sheet = parse(css);
      Assert.assertEquals(1, sheet.getCssRules().getLength());
      
      Assert.assertEquals(1, sheet.getMedia().getLength());
      Assert.assertEquals("all", sheet.getMedia().toString());
      
      final CSSRule cssRule = sheet.getCssRules().item(0);
      Assert.assertTrue(cssRule instanceof CSSMediaRuleImpl);
      Assert.assertTrue(((CSSMediaRuleImpl) cssRule).getMedia() instanceof MediaListImpl);
      
      final MediaListImpl mediaListImpl = (MediaListImpl) ((CSSMediaRuleImpl) cssRule).getMedia();
      Assert.assertEquals("all and (color)", mediaListImpl.getMediaText());
      Assert.assertEquals(1, mediaListImpl.getLength());
      Assert.assertEquals("all", mediaListImpl.mediaQuery(0).getMedia());
      Assert.assertEquals("all and (color)", mediaListImpl.mediaQuery(0).toString());
      Assert.assertEquals(1, mediaListImpl.mediaQuery(0).getProperties().size());
      Assert.assertEquals("color", mediaListImpl.mediaQuery(0).getProperties().get(0).toString());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void parseMedia() throws Exception {
      final String css = "speech and (min-device-width: 800px)";
      final SACMediaList mediaList = parseMedia(css, 0, 0, 0);
      
      Assert.assertEquals(1, mediaList.getLength());
      Assert.assertEquals("speech", mediaList.toString());
      
      final MediaQuery mediaQuery = ((SACMediaListImpl) mediaList).mediaQuery(0);
      Assert.assertEquals("speech", mediaQuery.getMedia());
      final List<Property> properties = mediaQuery.getProperties();
      Assert.assertEquals(1, properties.size());
      Assert.assertEquals("min-device-width: 800px", properties.get(0).toString());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void parseMediaComplexRule() throws Exception {
      final String css = "only screen and (max-width: 735px) and (max-device-width: 768px), print,"
         + "aural and (device-aspect-ratio: 16/9)";
      final SACMediaList mediaList = parseMedia(css, 0, 0, 0);
      
      Assert.assertEquals(3, mediaList.getLength());
      Assert.assertEquals("screen, print, aural", mediaList.toString());
      
      MediaQuery mediaQuery = ((SACMediaListImpl) mediaList).mediaQuery(0);
      Assert.assertEquals("screen", mediaQuery.getMedia());
      List<Property> properties = mediaQuery.getProperties();
      Assert.assertEquals(2, properties.size());
      Assert.assertEquals("max-width: 735px", properties.get(0).toString());
      Assert.assertEquals("max-device-width: 768px", properties.get(1).toString());
      
      mediaQuery = ((SACMediaListImpl) mediaList).mediaQuery(1);
      Assert.assertEquals("print", mediaQuery.getMedia());
      properties = mediaQuery.getProperties();
      Assert.assertEquals(0, properties.size());
      
      mediaQuery = ((SACMediaListImpl) mediaList).mediaQuery(2);
      Assert.assertEquals("aural", mediaQuery.getMedia());
      properties = mediaQuery.getProperties();
      Assert.assertEquals("device-aspect-ratio: 16 / 9", properties.get(0).toString());
   }
}
