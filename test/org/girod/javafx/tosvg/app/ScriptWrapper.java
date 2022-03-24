/*
Copyright (c) 2022, Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/fxsvgimage
 */
package org.girod.javafx.tosvg.app;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MissingFieldException;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.Node;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 * The ScriptWrapper class.
 *
 * @since 1.0
 */
public class ScriptWrapper {
   /**
    * We use this Pattern for Groovy compile-time errors, because it seems that it is not possible "easily" to
    * get the line which originates the groovy compilation error without parsing the error message itself.
    */
   private final Pattern exCompileError = Pattern.compile(".*script\\d+\\.groovy:\\s*(\\d+)\\s*:(.*)@.*");
   private GroovyObject groovyObject = null;
   private Script script = null;
   private static final String END = "}\n";
   private String scriptContent = null;
   private int offset = 0;
   private File file = null;
   private static final String DEFAULTCONTENT
      = "class GroovyClass implements Script {\n"
      + "    ScriptContext context;\n";
   public static final String DEFAULTCONTENT1
      = "    public void init(ScriptContext ctx) {\n"
      + "       context = ctx;\n" + "    }\n";
   public static final String BEGIN
      = "import org.girod.javafx.tosvg.app.Script;\n"
      + "import org.girod.javafx.tosvg.app.ScriptContext;\n";
   private static final Pattern IMPORT_PAT = Pattern.compile("\\s*import .*");
   private static final Pattern COMMENT_PAT = Pattern.compile("\\s*//.*");

   public ScriptWrapper(File file) {
      this.file = file;
   }

   /**
    * Execute the script.
    *
    * @return the node
    */
   public Node executeScript() {
      if (script != null) {
         try {
            ScriptContext context = new ScriptContext(file);
            script.init(context);
            return script.getContent();
         } catch (Exception e) {
            logScriptException(e);
            return null;
         }
      } else {
         return null;
      }
   }

   /**
    * Create the script.
    *
    * @param loader the ClassLoader
    * @return the script
    */
   public Script createScript(ClassLoader loader) {
      if (file != null) {
         try {
            URL url = file.toURI().toURL();
            ScriptContent content = new ScriptContent(url);
            StringBuilder buf = new StringBuilder();
            buf.append(BEGIN);
            // add the user import lines
            if (content.hasImports()) {
               buf.append(getContentAsString(content.getImports()));
            }

            buf.append(DEFAULTCONTENT);
            buf.append(DEFAULTCONTENT1);
            // compute the Offset of the first line of Script content in the groovy file relative to the first line of the Script
            // including the content added automatically
            computeOffset(buf.toString(), content);
            // add the user content, including th fields declaration
            buf.append(getContentAsString(content.getContent()));
            buf.append(END);
            scriptContent = buf.toString();
            groovyObject = createGroovyObject(loader);
            script = (Script) groovyObject;
            return script;
         } catch (Exception e) {
            ScriptException scriptException = getCompileException(e);
            System.err.println(scriptException.getMessage());
            return null;
         }
      } else {
         return null;
      }
   }

   /**
    * Called when an Exception is encountered in a Script to manage the Error logging, and detect the lline number for which the error
    * originates.
    *
    * @param e the Exception
    * @return true if the Script processing can continue, false if it should be aborted
    */
   private void logScriptException(Exception e) {
      if (e instanceof MissingMethodException || e instanceof MissingPropertyException || e instanceof MissingFieldException) {
         int lineNumber = ExceptionsUtils.getLineNumber(e);
         if (lineNumber != -1) {
            lineNumber = lineNumber - getOffset();
            String message = e.getMessage();
            if (message == null) {
               message = e.getClass().getName();
            }
            System.err.println("Line " + lineNumber + ": " + e.getMessage());
         } else {
            System.err.println(e.getMessage());
         }
      } else {
         e.printStackTrace();
      }
   }

   /**
    * Return a proper exception from a Grooby compile-time exception, with the correct file and line number.
    *
    * @param e the initial Exception
    * @return the ScriptException
    */
   private ScriptException getCompileException(Exception e) {
      // first we try to get the line number of the exception with the same method as for Groovy runtime exceptions
      // (navigating in the StackTrace)
      // normally we won't be able to get it with compile-time exceptions, but we still do that because it is possible
      // that the alternate method that we will use instead of this use would not work if the "direct" method is working
      int lineNumber = ExceptionsUtils.getLineNumber(e);
      String fileName = "undef";
      String message = e.getMessage();
      if (lineNumber != -1) {
         // we remove the offset from the line number
         lineNumber = lineNumber - getOffset();
         fileName = ExceptionsUtils.getFileName(e);
      }
      // if we were not able to find the line number by navigating in the StackTrace, we will the error message and remove
      // the parts that we don't need
      if (lineNumber == -1) {
         // remove tabulations (\r and \n) from the error message to ease its parsing by the Matcher
         message = removeTabulations(message);
         Matcher m = exCompileError.matcher(message);
         if (m.matches()) {
            fileName = file.getName();
            lineNumber = Integer.parseInt(m.group(1));
            // we remove the offset from the line number
            lineNumber = lineNumber - getOffset();
            // we don't use the whole message because this part will contain a wrong line number
            message = m.group(2);
         }
      }
      ScriptException ex = new ScriptException(e, message, fileName, lineNumber);
      return ex;
   }

   /**
    * Remove tabulations from an error message
    */
   private String removeTabulations(String message) {
      message = message.replace("\r", "");
      message = message.replace("\n", "");
      return message;
   }

   /**
    * Compute the offset of the first line of Script content in the groovy file relative to the first line of the Script including the
    * content added automatically. It is useful to crrectly identify the line for an Exception thrown by the Script.
    *
    * @param text the text
    */
   private void computeOffset(String text, ScriptContent content) {
      int _offset = 0;
      StringTokenizer tok = new StringTokenizer(text, "\n");
      _offset += tok.countTokens() - content.getImportsLines();
      setOffset(_offset);
   }

   /**
    * Set the line offset for the Script, useful if the Script associated with the File which contain it is not positioned
    * at the beginning of the File.
    * This can happen if the Script is streamed with a header before the content of the File, or even if it is just part of a larger File.
    *
    * @param offset the line offset for the Script
    */
   private void setOffset(int offset) {
      this.offset = offset;
   }

   private int getOffset() {
      return offset;
   }

   /**
    * Create the Groovy Object.
    *
    * @param decl the declaration
    * @param context the ScriptContext
    * @return the GroovyObject
    */
   private GroovyObject createGroovyObject(ClassLoader loader) throws Exception {
      if (scriptContent != null) {
         // compile the script with the InvokeDynamic optimization
         CompilerConfiguration config = new CompilerConfiguration();
         Map<String, Boolean> optim = config.getOptimizationOptions();
         config.setTargetBytecode(CompilerConfiguration.JDK8);
         optim.put("indy", Boolean.TRUE);
         GroovyClassLoader groovyLoader = new GroovyClassLoader(loader, config);
         Class<?> groovyClass = groovyLoader.parseClass(scriptContent);

         groovyObject = (GroovyObject) groovyClass.newInstance();
         return groovyObject;
      } else {
         return null;
      }
   }

   private String getContentAsString(List<String> content) {
      StringBuilder builder = new StringBuilder();
      Iterator<String> it = content.iterator();
      while (it.hasNext()) {
         builder.append(it.next());
         builder.append(System.getProperty("line.separator"));
      }
      return builder.toString();
   }

   private class ScriptContent {
      private final List<String> imports = new ArrayList<>();
      private final List<String> content = new ArrayList<>();
      private int importsNBLines = 0;

      private ScriptContent(URL scriptURL) {
         try {
            // use buffering, reading one line at a time
            // FileReader always assumes default encoding is OK!
            InputStreamReader stream = new InputStreamReader(scriptURL.openStream());
            try (BufferedReader input = new BufferedReader(stream)) {
               // not declared within while loop
               String line;
               /*
                * readLine is a bit quirky:
                * it returns the content of a line MINUS the newline.
                * it returns null only for the END of the stream.
                * it returns an empty String if two newlines appear in a row.
                */
               boolean beginContent = false;
               while ((line = input.readLine()) != null) {
                  // we check not only the import lines, but also the lines beginning with // in the imports section
                  Matcher m = IMPORT_PAT.matcher(line);
                  Matcher mc = COMMENT_PAT.matcher(line);
                  if (m.matches() || mc.matches() && !beginContent) {
                     imports.add(line);
                     importsNBLines++;
                  } else {
                     /* all this to be sure that declarations are processed correctly, that blanck lines
                      * int the declaration part are correctly counted, and also that we put the declarations
                      * at the right place: we must not put it at the same place as the imports (the position they
                      * have in the user script) because as we add a constructor after the imports, the script would not
                      * compile (it would be before the constructor of the class).
                      *
                      * It means that the variable declarations declared at the beginning of the user Script will be put
                      * just after the "int) method which is added by this class after the class declaration.
                      */
                     if (!beginContent & !line.trim().isEmpty()) {
                        // as we do this only if we find a non empty line which is not an import, we are sure to
                        // consider variable declarations as content (not as import)
                        beginContent = true;
                     }
                     if (!beginContent) {
                        importsNBLines++;
                     } else {
                        content.add(line);
                     }
                  }
               }
            }
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }

      private boolean hasImports() {
         return !imports.isEmpty();
      }

      private List<String> getImports() {
         return imports;
      }

      /**
       * Return the number of import lines, including the empty lines.
       *
       * @return the number of import lines
       */
      private int getImportsLines() {
         return importsNBLines;
      }

      private List<String> getContent() {
         return content;
      }
   }
}
