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
 * Testcases for {@link DescendantSelectorImpl}.
 * @author rbri
 */
public class DescendantSelectorImplTest {

    @Test
    public void ancestorSelector() {
        final ElementSelectorImpl parent = new ElementSelectorImpl("p");
        final ElementSelectorImpl descendant = new ElementSelectorImpl("a");
        final DescendantSelectorImpl selector = new DescendantSelectorImpl(parent, descendant);
        Assert.assertEquals(parent, selector.getAncestorSelector());

        Assert.assertEquals("p a", selector.toString());

        Assert.assertEquals("p a", selector.getCssText(null));
        Assert.assertEquals("p a", selector.getCssText(new CSSFormat()));
    }

    @Test
    public void simpleSelector() {
        final ElementSelectorImpl parent = new ElementSelectorImpl("p");
        final ElementSelectorImpl descendant = new ElementSelectorImpl("a");
        final DescendantSelectorImpl selector = new DescendantSelectorImpl(parent, descendant);
        Assert.assertEquals(descendant, selector.getSimpleSelector());

        Assert.assertEquals("p a", selector.toString());

        Assert.assertEquals("p a", selector.getCssText(null));
        Assert.assertEquals("p a", selector.getCssText(new CSSFormat()));
    }

    @Test
    public void selectorType() {
        final ElementSelectorImpl parent = new ElementSelectorImpl("p");
        final ElementSelectorImpl descendant = new ElementSelectorImpl("a");
        final DescendantSelectorImpl selector = new DescendantSelectorImpl(parent, descendant);
        Assert.assertEquals(Selector.SAC_DESCENDANT_SELECTOR, selector.getSelectorType());

        Assert.assertEquals("p a", selector.toString());

        Assert.assertEquals("p a", selector.getCssText(null));
        Assert.assertEquals("p a", selector.getCssText(new CSSFormat()));
    }

    @Test
    public void elementDescendant() {
        final ElementSelectorImpl parent = new ElementSelectorImpl("p");
        final ElementSelectorImpl descendant = new ElementSelectorImpl("a");
        final DescendantSelectorImpl selector = new DescendantSelectorImpl(parent, descendant);
        Assert.assertEquals("p a", selector.toString());

        Assert.assertEquals("p a", selector.getCssText(null));
        Assert.assertEquals("p a", selector.getCssText(new CSSFormat()));
    }

    @Test
    public void pseudoElementDescendant() {
        final ElementSelectorImpl parent = new ElementSelectorImpl("a");
        final PseudoElementSelectorImpl descendant = new PseudoElementSelectorImpl("after");
        final DescendantSelectorImpl selector = new DescendantSelectorImpl(parent, descendant);

        Assert.assertEquals("a:after", selector.toString());

        Assert.assertEquals("a:after", selector.getCssText(null));
        Assert.assertEquals("a:after", selector.getCssText(new CSSFormat()));
    }
}
