/*
Copyright (c) 2026 Hervé Girod
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
package org.girod.javafx.svgimage.xml.parsers.xmltree;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A File utilities class.
 *
 * @version 1.7.1
 */
public class FileUtils {
   private static final Pattern PAT_SUBPROTOCOL = Pattern.compile("(\\w+):.*");

   /**
    * Return an URL by its relative path from a parent URL.
    *
    * @param parentURL the parent URL
    * @param path the path of the child relative to the parent
    *
    * @return the child URL
    */
   public static URL getChildURL(URL parentURL, String path) {
      try {
         path = replaceEscapedSequences(path);
         File pathFile = new File(path);
         if (pathFile.isAbsolute()) {
            URL url = pathFile.toURI().toURL();
            return url;
         } else if ((parentURL != null) && (parentURL.getFile() != null)) {
            String protocol = parentURL.getProtocol();
            String host = parentURL.getHost();
            int port = parentURL.getPort();
            String file = parentURL.getFile();
            URL childURL = null;
            String childFile = new File(new File(file), path).getPath();
            childFile = replaceEscapedSequences(childFile);
            childFile = childFile.replace('\\', '\u002F').replace("\u002F.\u002F", "\u002F");
            childFile = getCorrectedPath(childFile);
            if (childFile != null) {
               if (childFile.endsWith(".jar") || childFile.endsWith(".apk") || childFile.endsWith(".dex")) {
                  if (childFile.startsWith("file:")) {
                     childFile = "jar:" + childFile.substring(5);
                  }
               } else if (childFile.endsWith(".zip")) {
                  if (childFile.startsWith("file:")) {
                     childFile = "zip:" + childFile.substring(5);
                  }
               } else if (childFile.endsWith(".gzip")) {
                  if (childFile.startsWith("file:")) {
                     childFile = "gzip:" + childFile.substring(5);
                  }
               }

               childFile = encodeURL(childFile);
               childURL = new URL(protocol, host, port, childFile);
            }
            return childURL;
         } else {
            return null;
         }
      } catch (MalformedURLException ex) {
         return null;
      }
   }

   private static String getCorrectedPath(String path) {
      Matcher m = PAT_SUBPROTOCOL.matcher(path);
      if (path.charAt(0) != '/') {
         if (!m.matches()) {
            path = "/" + path;
         } else if (m.matches()) {
            String group = m.group(1);
            path = "/" + path;
         }
      }
      return path;
   }

   /**
    * Filter a path name for an URL, replacing spaces characters by their percent encoding equivalent.
    *
    * @param path the path
    * @return the path after replacing spaces characters by their percent encoding equivalent
    */
   public static String encodeURL(String path) {
      if (!path.contains(" ")) {
         return path;
      } else {
         StringBuilder buf = new StringBuilder();
         int offset = 0;
         while (true) {
            int index = path.indexOf(' ', offset);
            if (index != -1) {
               buf.append(path.substring(offset, index));
               buf.append("%20");
               offset = index + 1;
            } else {
               buf.append(path.substring(offset));
               break;
            }
         }
         return buf.toString();
      }
   }

   /**
    * Replace the percent-encoding escape sequences in a URL path by their equivalent characters.
    *
    * @param path the path
    * @return the path after replacing the percent-encoding escape sequences by their equivalent characters
    */
   private static String replaceEscapedSequences(String path) {
      if (!path.contains("%")) {
         return path;
      } else {
         StringBuilder buf = new StringBuilder();
         int offset = 0;
         while (true) {
            int index = path.indexOf('%', offset);
            if (index != -1) {
               buf.append(path.substring(offset, index));
               char c = (char) (int) Integer.decode("0x" + path.substring(index + 1, index + 3));
               buf.append(c);
               offset = index + 3;
            } else {
               buf.append(path.substring(offset));
               break;
            }
         }
         return buf.toString();
      }
   }
   
   /**
    * Return the parent URL of a given URL.
    *
    * @param url the URL
    *
    * @return the parent URL
    */
   public static URL getParentURL(URL url) {
      if (url == null) {
         return null;
      }
      try {
         if (url.getFile() != null) {
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();
            String file = url.getFile();
            file = replaceEscapedSequences(file);
            File parentFile = new File(file).getParentFile();
            if (parentFile != null) {
               String parentFilePath = new File(file).getParentFile().getPath();
               parentFilePath = parentFilePath.replace('\\', '\u002F');
               parentFilePath = getCorrectedPath(parentFilePath);
               if (!parentFilePath.endsWith("/")) {
                  parentFilePath += "/";
               }
               URL parentURL = new URL(protocol, host, port, parentFilePath);
               return parentURL;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } catch (MalformedURLException ex) {
         return null;
      }
   }   

   public static boolean exists(URL url) {
      String path = url.getFile();
      File file = new File(path);
      return file.exists() && file.isFile();
   }
}
