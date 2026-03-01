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

package com.steadystate.css.sac;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

/**
 * Extension of the SelectorFactory interface.
 * This was added to support the locator parameter to
 * inform about the code position.
 */
public interface SelectorFactoryExt extends SelectorFactory {

    ElementSelector createElementSelector(String namespaceURI, String tagName, Locator locator)
            throws CSSException;

    ElementSelector createPseudoElementSelector(String namespaceURI, String pseudoName,
            Locator locator, boolean doubleColon)
            throws CSSException;

    ElementSelector createSyntheticElementSelector() throws CSSException;

    SiblingSelector createGeneralAdjacentSelector(short nodeType, Selector child, SimpleSelector directAdjacent)
            throws CSSException;
}
