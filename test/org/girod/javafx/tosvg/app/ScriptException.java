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

import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.syntax.SyntaxException;

/**
 * This interface wraps an exception fired by the Scripting engine.
 *
 * @since 1.0
 */
public class ScriptException {
   private ScriptWrapper script = null;
   private String message = null;
   private String filename = null;
   private int lineNumber = -1;
   private Exception exception = null;
   private GroovyRuntimeException runtimeException = null;

   public ScriptException(Exception exception, String message, String filename, int lineNumber) {
      this.exception = exception;
      if (exception instanceof GroovyRuntimeException) {
         runtimeException = (GroovyRuntimeException) exception;
      }
      this.message = message;
      this.filename = filename;
      this.lineNumber = lineNumber;
   }

   /**
    * Set the script wrapper.
    *
    * @param script the script wrapper
    */
   public void setScript(ScriptWrapper script) {
      this.script = script;
   }

   /**
    * Return the script wrapper.
    *
    * @return the script wrapper
    */
   public ScriptWrapper getScript() {
      return script;
   }

   /**
    * Return the exception line number.
    *
    * @return the line number
    */
   public int getLine() {
      if (runtimeException == null) {
         return -1;
      } else {
         if (runtimeException instanceof MultipleCompilationErrorsException) {
            MultipleCompilationErrorsException ex = (MultipleCompilationErrorsException) runtimeException;
            SyntaxException se = ex.getErrorCollector().getSyntaxError(0);
            if (se == null) {
               return -1;
            } else {
               return se.getLine();
            }
         } else {
            return -1;
         }
      }
   }

   public String getMessage() {
      return exception.getMessage();
   }

   public Throwable getThrowable() {
      return exception;
   }
}
