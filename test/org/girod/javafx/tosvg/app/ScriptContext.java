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

import java.io.File;

/**
 * The script context.
 *
 * @since 1.0
 */
public class ScriptContext {
   private final File scriptFile;
   private final File scriptDir;

   /**
    * Constructor.
    *
    * @param scriptFile the script file
    */
   public ScriptContext(File scriptFile) {
      this.scriptFile = scriptFile;
      this.scriptDir = scriptFile.getParentFile();
   }

   /**
    * Return the script directory.
    *
    * @return the script directory
    */
   public File getScriptDirectory() {
      return scriptDir;
   }

   /**
    * Return the script file.
    *
    * @return the script file
    */
   public File getScriptFile() {
      return scriptFile;
   }

   /**
    * Return the absolute path of a file defined relative to the script file.
    *
    * @param path the relative path
    * @return the absolute path
    */
   public File getPath(String path) {
      File file = new File(scriptDir, path);
      return file;
   }

   /**
    * Return the absolute path of a file defined relative to the script file.
    *
    * @param path the relative path
    * @return the absolute path
    */
   public String getCSS(String path) {
      File file = new File(scriptDir, path);
      String ref = "file:///" + file.getAbsolutePath().replace("\\", "/");
      return ref;
   }
}
