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
import org.w3c.css.sac.Condition;

import com.steadystate.css.format.CSSFormat;

/**
 * Test cases for {@link SubstringAttributeConditionImpl}.
 */
public class SubstringAttributeConditionImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withoutValue() throws Exception {
        final SubstringAttributeConditionImpl ac = new SubstringAttributeConditionImpl("test", null, false);
        Assert.assertNull(ac.getNamespaceURI());
        Assert.assertEquals(Condition.SAC_ATTRIBUTE_CONDITION, ac.getConditionType());
        Assert.assertEquals("test", ac.getLocalName());
        Assert.assertNull(ac.getValue());
        Assert.assertFalse(ac.getSpecified());

        Assert.assertEquals("[test]", ac.toString());

        Assert.assertEquals("[test]", ac.getCssText(null));
        Assert.assertEquals("[test]", ac.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void emptyValue() throws Exception {
        final SubstringAttributeConditionImpl ac = new SubstringAttributeConditionImpl("test", "", false);
        Assert.assertNull(ac.getNamespaceURI());
        Assert.assertEquals(Condition.SAC_ATTRIBUTE_CONDITION, ac.getConditionType());
        Assert.assertEquals("test", ac.getLocalName());
        Assert.assertEquals("", ac.getValue());
        Assert.assertFalse(ac.getSpecified());

        Assert.assertEquals("[test*=\"\"]", ac.toString());

        Assert.assertEquals("[test*=\"\"]", ac.getCssText(null));
        Assert.assertEquals("[test*=\"\"]", ac.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withValue() throws Exception {
        final SubstringAttributeConditionImpl ac = new SubstringAttributeConditionImpl("test", "value", false);
        Assert.assertNull(ac.getNamespaceURI());
        Assert.assertEquals(Condition.SAC_ATTRIBUTE_CONDITION, ac.getConditionType());
        Assert.assertEquals("test", ac.getLocalName());
        Assert.assertEquals("value", ac.getValue());
        Assert.assertFalse(ac.getSpecified());

        Assert.assertEquals("[test*=\"value\"]", ac.toString());

        Assert.assertEquals("[test*=\"value\"]", ac.getCssText(null));
        Assert.assertEquals("[test*=\"value\"]", ac.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withoutValueAndSpecified() throws Exception {
        final SubstringAttributeConditionImpl ac = new SubstringAttributeConditionImpl("test", null, true);
        Assert.assertNull(ac.getNamespaceURI());
        Assert.assertEquals(Condition.SAC_ATTRIBUTE_CONDITION, ac.getConditionType());
        Assert.assertEquals("test", ac.getLocalName());
        Assert.assertNull(ac.getValue());
        Assert.assertTrue(ac.getSpecified());

        Assert.assertEquals("[test]", ac.toString());

        Assert.assertEquals("[test]", ac.getCssText(null));
        Assert.assertEquals("[test]", ac.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void emptyValueAndSpecified() throws Exception {
        final SubstringAttributeConditionImpl ac = new SubstringAttributeConditionImpl("test", "", true);
        Assert.assertNull(ac.getNamespaceURI());
        Assert.assertEquals(Condition.SAC_ATTRIBUTE_CONDITION, ac.getConditionType());
        Assert.assertEquals("test", ac.getLocalName());
        Assert.assertEquals("", ac.getValue());
        Assert.assertTrue(ac.getSpecified());

        Assert.assertEquals("[test*=\"\"]", ac.toString());

        Assert.assertEquals("[test*=\"\"]", ac.getCssText(null));
        Assert.assertEquals("[test*=\"\"]", ac.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withValueAndSpecified() throws Exception {
        final SubstringAttributeConditionImpl ac = new SubstringAttributeConditionImpl("test", "value", true);
        Assert.assertNull(ac.getNamespaceURI());
        Assert.assertEquals(Condition.SAC_ATTRIBUTE_CONDITION, ac.getConditionType());
        Assert.assertEquals("test", ac.getLocalName());
        Assert.assertEquals("value", ac.getValue());
        Assert.assertTrue(ac.getSpecified());

        Assert.assertEquals("[test*=\"value\"]", ac.toString());

        Assert.assertEquals("[test*=\"value\"]", ac.getCssText(null));
        Assert.assertEquals("[test*=\"value\"]", ac.getCssText(new CSSFormat()));
    }
}
