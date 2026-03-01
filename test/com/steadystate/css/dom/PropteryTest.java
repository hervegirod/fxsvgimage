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

import com.steadystate.css.parser.LexicalUnitImpl;

/**
/**
 * Unit tests for {@link Property}.
 *
 * @author rbri
 */
public class PropteryTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void defaultConstructor() throws Exception {
        final Property prop = new Property();
        Assert.assertEquals("null", prop.toString());
        Assert.assertNull(prop.getName());
        Assert.assertNull(prop.getValue());
        Assert.assertFalse(prop.isImportant());
        Assert.assertTrue(prop.getUserDataMap().isEmpty());

        prop.setName("MyName");
        Assert.assertEquals("MyName", prop.toString());
        Assert.assertEquals("MyName", prop.getName());
        Assert.assertNull(prop.getValue());
        Assert.assertFalse(prop.isImportant());
        Assert.assertTrue(prop.getUserDataMap().isEmpty());

        LexicalUnit lu = LexicalUnitImpl.createString(null, "MyValue");
        prop.setValue(new CSSValueImpl(lu, true));
        Assert.assertEquals("MyName: \"MyValue\"", prop.toString());
        Assert.assertEquals("MyName", prop.getName());
        Assert.assertEquals("\"MyValue\"", prop.getValue().toString());
        Assert.assertFalse(prop.isImportant());
        Assert.assertTrue(prop.getUserDataMap().isEmpty());

        lu = LexicalUnitImpl.createPixel(null, 11f);
        prop.setValue(new CSSValueImpl(lu, true));
        Assert.assertEquals("MyName: 11px", prop.toString());
        Assert.assertEquals("MyName", prop.getName());
        Assert.assertEquals("11px", prop.getValue().toString());
        Assert.assertFalse(prop.isImportant());
        Assert.assertTrue(prop.getUserDataMap().isEmpty());

        prop.setImportant(true);
        Assert.assertEquals("MyName: 11px !important", prop.toString());
        Assert.assertEquals("MyName", prop.getName());
        Assert.assertEquals("11px", prop.getValue().toString());
        Assert.assertTrue(prop.isImportant());
        Assert.assertTrue(prop.getUserDataMap().isEmpty());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void defaultConstructorNoValueImportant() throws Exception {
        final Property prop = new Property();
        prop.setName("MyName");
        prop.setImportant(true);
        Assert.assertEquals("MyName !important", prop.toString());
        Assert.assertEquals("MyName", prop.getName());
        Assert.assertNull(prop.getValue());
        Assert.assertTrue(prop.isImportant());
        Assert.assertTrue(prop.getUserDataMap().isEmpty());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void constructWithParams() throws Exception {
        final LexicalUnit lu = LexicalUnitImpl.createCentimeter(null, 13.2f);
        final Property prop = new Property("MyName", new CSSValueImpl(lu, true), false);
        Assert.assertEquals("MyName: 13.2cm", prop.toString());
        Assert.assertEquals("MyName", prop.getName());
        Assert.assertEquals("13.2cm", prop.getValue().toString());
        Assert.assertFalse(prop.isImportant());
        Assert.assertTrue(prop.getUserDataMap().isEmpty());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void equals() throws Exception {
        LexicalUnit lu = LexicalUnitImpl.createCentimeter(null, 13.2f);
        final Property prop = new Property("MyName", new CSSValueImpl(lu, true), false);

        lu = LexicalUnitImpl.createCentimeter(null, 13.2f);
        Property prop2 = new Property("MyName", new CSSValueImpl(lu, true), false);

        Assert.assertEquals(prop, prop2);

        // different name
        lu = LexicalUnitImpl.createCentimeter(null, 13.2f);
        prop2 = new Property("MyName2", new CSSValueImpl(lu, true), false);
        Assert.assertFalse(prop.equals(prop2));

        // different value
        lu = LexicalUnitImpl.createMillimeter(null, 13.2f);
        prop2 = new Property("MyName", new CSSValueImpl(lu, true), false);
        Assert.assertFalse(prop.equals(prop2));

        // different importance
        lu = LexicalUnitImpl.createCentimeter(null, 13.2f);
        prop2 = new Property("MyName", new CSSValueImpl(lu, true), true);
        Assert.assertFalse(prop.equals(prop2));
    }
}
