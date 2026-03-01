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

package com.steadystate.css.parser.selectors;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.Selector;

import com.steadystate.css.format.CSSFormat;

/**
 * Testcases for {@link ChildSelectorImpl}.
 * @author rbri
 */
public class ChildSelectorImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withoutParentSimple() throws Exception {
        final ChildSelectorImpl s = new ChildSelectorImpl(null, null);
        Assert.assertEquals(Selector.SAC_CHILD_SELECTOR, s.getSelectorType());
        Assert.assertNull(s.getAncestorSelector());
        Assert.assertNull(s.getSimpleSelector());

        Assert.assertEquals(" > ", s.toString());

        Assert.assertEquals(" > ", s.getCssText(null));
        Assert.assertEquals(" > ", s.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void parentOnly() throws Exception {
        final ElementSelectorImpl parent = new ElementSelectorImpl("p");
        final ChildSelectorImpl s = new ChildSelectorImpl(parent, null);
        Assert.assertEquals(Selector.SAC_CHILD_SELECTOR, s.getSelectorType());
        Assert.assertEquals(parent, s.getAncestorSelector());
        Assert.assertNull(s.getSimpleSelector());

        Assert.assertEquals("p > ", s.toString());

        Assert.assertEquals("p > ", s.getCssText(null));
        Assert.assertEquals("p > ", s.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void simpleOnly() throws Exception {
        final ElementSelectorImpl simple = new ElementSelectorImpl("c");
        final ChildSelectorImpl s = new ChildSelectorImpl(null, simple);
        Assert.assertEquals(Selector.SAC_CHILD_SELECTOR, s.getSelectorType());
        Assert.assertNull(s.getAncestorSelector());
        Assert.assertEquals(simple, s.getSimpleSelector());

        Assert.assertEquals(" > c", s.toString());

        Assert.assertEquals(" > c", s.getCssText(null));
        Assert.assertEquals(" > c", s.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void both() throws Exception {
        final ElementSelectorImpl parent = new ElementSelectorImpl("p");
        final ElementSelectorImpl simple = new ElementSelectorImpl("c");
        final ChildSelectorImpl s = new ChildSelectorImpl(parent, simple);
        Assert.assertEquals(Selector.SAC_CHILD_SELECTOR, s.getSelectorType());
        Assert.assertEquals(parent, s.getAncestorSelector());
        Assert.assertEquals(simple, s.getSimpleSelector());

        Assert.assertEquals("p > c", s.toString());

        Assert.assertEquals("p > c", s.getCssText(null));
        Assert.assertEquals("p > c", s.getCssText(new CSSFormat()));
    }
}
