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

import com.steadystate.css.format.CSSFormat;
import com.steadystate.css.parser.LexicalUnitImpl;

/**
/**
 * Unit tests for {@link CounterImpl}.
 *
 * @author rbri
 */
public class CounterImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void defaultConstructor() throws Exception {
        final CounterImpl counter = new CounterImpl();
        Assert.assertEquals("counter(null)", counter.toString());
        Assert.assertNull(counter.getIdentifier());
        Assert.assertNull(counter.getSeparator());
        Assert.assertNull(counter.getListStyle());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void constructByLU() throws Exception {
        final LexicalUnit counterLu = LexicalUnitImpl.createString(null, "ident");

        CounterImpl counter = new CounterImpl(false, counterLu);
        Assert.assertEquals("counter(ident)", counter.toString());
        Assert.assertEquals("ident", counter.getIdentifier());
        Assert.assertNull(counter.getSeparator());
        Assert.assertNull(counter.getListStyle());

        counter = new CounterImpl(true, counterLu);
        Assert.assertEquals("counter(ident)", counter.toString());
        Assert.assertEquals("ident", counter.getIdentifier());
        Assert.assertNull(counter.getSeparator());
        Assert.assertNull(counter.getListStyle());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void constructByLUSeparator() throws Exception {
        final LexicalUnit counterLu = LexicalUnitImpl.createString(null, "ident");
        LexicalUnit lu = LexicalUnitImpl.createComma(counterLu);
        lu = LexicalUnitImpl.createString(lu, "sep");

        CounterImpl counter = new CounterImpl(false, counterLu);
        Assert.assertEquals("counter(ident, sep)", counter.toString());
        Assert.assertEquals("ident", counter.getIdentifier());
        Assert.assertNull(counter.getSeparator());
        Assert.assertEquals("sep", counter.getListStyle());

        counter = new CounterImpl(true, counterLu);
        Assert.assertEquals("counters(ident, \"sep\")", counter.toString());
        Assert.assertEquals("ident", counter.getIdentifier());
        Assert.assertEquals("sep", counter.getSeparator());
        Assert.assertNull(counter.getListStyle());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void constructByLUSeparatorList() throws Exception {
        final LexicalUnit counterLu = LexicalUnitImpl.createString(null, "ident");
        LexicalUnit lu = LexicalUnitImpl.createComma(counterLu);
        lu = LexicalUnitImpl.createString(lu, "sep");
        lu = LexicalUnitImpl.createComma(lu);
        lu = LexicalUnitImpl.createString(lu, "list");

        final CounterImpl counter = new CounterImpl(true, counterLu);
        Assert.assertEquals("counters(ident, \"sep\", list)", counter.toString());
        Assert.assertEquals("ident", counter.getIdentifier());
        Assert.assertEquals("sep", counter.getSeparator());
        Assert.assertEquals("list", counter.getListStyle());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void getCssText() throws Exception {
        final LexicalUnit counterLu = LexicalUnitImpl.createString(null, "ident");
        LexicalUnit lu = LexicalUnitImpl.createComma(counterLu);
        lu = LexicalUnitImpl.createString(lu, "sep");
        lu = LexicalUnitImpl.createComma(lu);
        lu = LexicalUnitImpl.createString(lu, "list");

        final CounterImpl counter = new CounterImpl(true, counterLu);

        Assert.assertEquals("counters(ident, \"sep\", list)", counter.getCssText());
        Assert.assertEquals("counters(ident, \"sep\", list)", counter.getCssText(null));
        Assert.assertEquals("counters(ident, \"sep\", list)", counter.getCssText(new CSSFormat()));
    }
}
