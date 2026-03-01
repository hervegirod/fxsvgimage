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

import com.steadystate.css.format.CSSFormat;

/**
 * Testcases for {@link PseudoClassConditionImpl}.
 */
public class PseudoClassConditionImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withoutValue() throws Exception {
        final PseudoClassConditionImpl c = new PseudoClassConditionImpl(null);
        Assert.assertNull(c.getNamespaceURI());
        Assert.assertNull(c.getLocalName());
        Assert.assertNull(c.getValue());
        Assert.assertTrue(c.getSpecified());  // TODO is this correct?

        Assert.assertNull(c.toString());

        Assert.assertNull(c.getCssText(null));
        Assert.assertNull(c.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void emptyValue() throws Exception {
        final PseudoClassConditionImpl c = new PseudoClassConditionImpl("");
        Assert.assertNull(c.getNamespaceURI());
        Assert.assertNull(c.getLocalName());
        Assert.assertEquals("", c.getValue());
        Assert.assertTrue(c.getSpecified());

        Assert.assertEquals(":", c.toString());

        Assert.assertEquals(":", c.getCssText(null));
        Assert.assertEquals(":", c.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withValue() throws Exception {
        final PseudoClassConditionImpl c = new PseudoClassConditionImpl("value");
        Assert.assertNull(c.getNamespaceURI());
        Assert.assertNull(c.getLocalName());
        Assert.assertEquals("value", c.getValue());
        Assert.assertTrue(c.getSpecified());

        Assert.assertEquals(":value", c.toString());

        Assert.assertEquals(":value", c.getCssText(null));
        Assert.assertEquals(":value", c.getCssText(new CSSFormat()));
    }
}
