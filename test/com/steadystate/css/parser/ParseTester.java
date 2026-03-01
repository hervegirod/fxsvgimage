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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.helpers.ParserFactory;

import com.steadystate.css.util.Output;

/**
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @author sdanig
 * @author rbri
 */
public class ParseTester extends HandlerBase {

    private static final String PARSER = "com.steadystate.css.parser.SACParserCSS21";

    private int propertyCounter_ = 0;
    private Output output_;

    public static void main(final String[] args) {
        final File css = new File("src/test/resources/test.css");
        // System.out.println(css.getAbsolutePath());

        new ParseTester().testParsing(css);
    }

    public ParseTester() {
        final Writer w = new OutputStreamWriter(System.out);
        output_ = new Output(w, "    ");
    }

    public void testParsing(final File cssFile) {
        try {
            System.setProperty("org.w3c.css.sac.parser", PARSER);
            final ParserFactory factory = new ParserFactory();
            final Parser parser = factory.makeParser();
            parser.setDocumentHandler(this);

            final Reader r = new FileReader(cssFile);

            final InputSource is = new InputSource(r);
            parser.parseStyleSheet(is);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startDocument(final InputSource source) throws CSSException {
        println("startDocument");
    }

    @Override
    public void endDocument(final InputSource source) throws CSSException {
        println("endDocument");
    }

    @Override
    public void comment(final String text) throws CSSException {
    }

    @Override
    public void ignorableAtRule(final String atRule, final Locator locator) throws CSSException {
        println(atRule);
    }

    @Override
    public void namespaceDeclaration(final String prefix, final String uri) throws CSSException {
    }

    @Override
    public void importStyle(final String uri,
            final SACMediaList media, final String defaultNamespaceURI, final Locator locator) throws CSSException {
        print("@import url(" + uri + ")");
        if (media.getLength() > 0) {
            println(" " + media.toString() + ";");
        }
        else {
            println(";");
        }
    }

    @Override
    public void startMedia(final SACMediaList media, final Locator locator) throws CSSException {
        println("@media " + media.toString() + " {");
        output_.indent();
    }

    @Override
    public void endMedia(final SACMediaList media) throws CSSException {
        output_.unindent();
        println("}");
    }

    @Override
    public void startPage(final String name, final String pseudoPage, final Locator locator) throws CSSException {
        print("@page");
        if (name != null) {
            print(" " + name);
        }
        if (pseudoPage != null) {
            println(" " + pseudoPage);
        }
        println(" {");
        propertyCounter_ = 0;
        output_.indent();
    }

    @Override
    public void endPage(final String name, final String pseudoPage) throws CSSException {
        println("");
        output_.unindent();
        println("}");
    }

    @Override
    public void startFontFace(final Locator locator) throws CSSException {
        println("@font-face {");
        propertyCounter_ = 0;
        output_.indent();
    }

    @Override
    public void endFontFace() throws CSSException {
        println("");
        output_.unindent();
        println("}");
    }

    @Override
    public void startSelector(final SelectorList selectors, final Locator locator) throws CSSException {
        println(selectors.toString() + " {");
        propertyCounter_ = 0;
        output_.indent();
    }

    @Override
    public void endSelector(final SelectorList selectors) throws CSSException {
        println("");
        output_.unindent();
        println("}");
    }

    @Override
    public void property(final String name,
            final LexicalUnit value, final boolean important, final Locator locator) throws CSSException {
        if (propertyCounter_++ > 0) {
            println(";");
        }
        print(name + ":");

        // Iterate through the chain of lexical units
        LexicalUnit nextVal = value;
        while (nextVal != null) {
            //print(" " + ((LexicalUnitImpl) nextVal).toDebugString());
            nextVal = nextVal.getNextLexicalUnit();
        }

        // Is it important?
        if (important) {
            print(" !important");
        }
    }

    private void print(final String message) {
        try {
            output_.print(message);
            output_.flush();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void println(final String message) {
        try {
            output_.println(message);
            output_.flush();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
