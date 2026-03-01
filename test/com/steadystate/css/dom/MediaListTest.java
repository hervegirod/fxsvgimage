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

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.stylesheets.MediaList;

import com.steadystate.css.format.CSSFormat;

/**
/**
 * Unit tests for {@link MediaListImpl}.
 *
 * @author rbri
 */
public class MediaListTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void defaultConstructor() throws Exception {
        final MediaList ml = new MediaListImpl();
        Assert.assertEquals("", ml.toString());
        Assert.assertEquals("", ml.getMediaText());
        Assert.assertEquals(0, ml.getLength());
        Assert.assertNull(ml.item(0));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getMediaText() throws Exception {
        final MediaList ml = new MediaListImpl();
        Assert.assertEquals("", ml.getMediaText());

        ml.appendMedium("newMedium");
        Assert.assertEquals("newMedium", ml.getMediaText());

        ml.appendMedium("anotherMedium");
        Assert.assertEquals("newMedium, anotherMedium", ml.getMediaText());

        ml.appendMedium("lastMedium");
        Assert.assertEquals("newMedium, anotherMedium, lastMedium", ml.getMediaText());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getLength() throws Exception {
        final MediaList ml = new MediaListImpl();
        Assert.assertEquals(0, ml.getLength());

        ml.appendMedium("newMedium");
        Assert.assertEquals(1, ml.getLength());

        ml.appendMedium("anotherMedium");
        Assert.assertEquals(2, ml.getLength());

        ml.appendMedium("lastMedium");
        Assert.assertEquals(3, ml.getLength());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void item() throws Exception {
        final MediaList ml = new MediaListImpl();
        Assert.assertEquals(null, ml.item(-1));
        Assert.assertEquals(null, ml.item(0));
        Assert.assertEquals(null, ml.item(1));
        Assert.assertEquals(null, ml.item(10));

        ml.appendMedium("newMedium");
        Assert.assertEquals(null, ml.item(-1));
        Assert.assertEquals("newMedium", ml.item(0));
        Assert.assertEquals(null, ml.item(1));
        Assert.assertEquals(null, ml.item(10));

        ml.appendMedium("anotherMedium");
        Assert.assertEquals(null, ml.item(-1));
        Assert.assertEquals("newMedium", ml.item(0));
        Assert.assertEquals("anotherMedium", ml.item(1));
        Assert.assertEquals(null, ml.item(2));
        Assert.assertEquals(null, ml.item(10));

        ml.appendMedium("lastMedium");
        Assert.assertEquals(null, ml.item(-1));
        Assert.assertEquals("newMedium", ml.item(0));
        Assert.assertEquals("anotherMedium", ml.item(1));
        Assert.assertEquals("lastMedium", ml.item(2));
        Assert.assertEquals(null, ml.item(3));
        Assert.assertEquals(null, ml.item(10));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void deleteMedium() throws Exception {
        MediaList ml = new MediaListImpl();
        ml.appendMedium("newMedium");
        ml.appendMedium("anotherMedium");
        ml.appendMedium("lastMedium");
        Assert.assertEquals(3, ml.getLength());
        Assert.assertEquals("newMedium, anotherMedium, lastMedium", ml.getMediaText());

        ml.deleteMedium("newMedium");
        Assert.assertEquals(2, ml.getLength());
        Assert.assertEquals("anotherMedium, lastMedium", ml.getMediaText());

        ml = new MediaListImpl();
        ml.appendMedium("newMedium");
        ml.appendMedium("anotherMedium");
        ml.appendMedium("lastMedium");
        Assert.assertEquals(3, ml.getLength());
        Assert.assertEquals("newMedium, anotherMedium, lastMedium", ml.getMediaText());

        ml.deleteMedium("anotherMedium");
        Assert.assertEquals(2, ml.getLength());
        Assert.assertEquals("newMedium, lastMedium", ml.getMediaText());

        ml = new MediaListImpl();
        ml.appendMedium("newMedium");
        ml.appendMedium("anotherMedium");
        ml.appendMedium("lastMedium");
        Assert.assertEquals(3, ml.getLength());
        Assert.assertEquals("newMedium, anotherMedium, lastMedium", ml.getMediaText());

        ml.deleteMedium("lastMeDIUM");
        Assert.assertEquals(2, ml.getLength());
        Assert.assertEquals("newMedium, anotherMedium", ml.getMediaText());

        try {
            ml.deleteMedium("unknown");
            Assert.fail("DOMException expected");
        }
        catch (final DOMException e) {
            Assert.assertEquals(DOMException.NOT_FOUND_ERR, e.code);
        }
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void setMedia() throws Exception {
        final MediaListImpl ml = new MediaListImpl();
        Assert.assertEquals(0, ml.getLength());
        Assert.assertEquals("", ml.getMediaText());

        ml.setMedia(Arrays.asList("newMedium", "anotherMedium", "lastMedium"));
        Assert.assertEquals(3, ml.getLength());
        Assert.assertEquals("newMedium, anotherMedium, lastMedium", ml.getMediaText());

        ml.setMedia(Arrays.asList("somethingElse"));
        Assert.assertEquals(1, ml.getLength());
        Assert.assertEquals("somethingElse", ml.getMediaText());

        ml.setMedia(new ArrayList<String>());
        Assert.assertEquals(0, ml.getLength());
        Assert.assertEquals("", ml.getMediaText());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void setMediaText() throws Exception {
        final MediaList ml = new MediaListImpl();

        ml.setMediaText("MyMediaText");
        Assert.assertEquals("MyMediaText", ml.toString());
        Assert.assertEquals("MyMediaText", ml.getMediaText());
        Assert.assertEquals(1, ml.getLength());
        Assert.assertEquals("MyMediaText", ml.item(0));
        Assert.assertNull(ml.item(1));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getMediaTextFormated() throws Exception {
        final MediaListImpl ml = new MediaListImpl();
        ml.appendMedium("newMedium");

        Assert.assertEquals("newMedium", ml.getMediaText());
        Assert.assertEquals("newMedium", ml.getMediaText(null));
        Assert.assertEquals("newMedium", ml.getMediaText(new CSSFormat()));
    }
}
