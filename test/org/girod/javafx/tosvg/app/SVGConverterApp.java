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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * A simple application which allows to convert using a Script file.
 *
 * @since 1.0
 */
public class SVGConverterApp extends JFrame {
   private JTextField titleTf = null;
   private JButton convertButton = null;
   private JButton selectContentButton = null;
   private ScriptWrapper wrapper = null;

   public SVGConverterApp() {
      super("Converter Test");
      createContent();
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

   private void createContent() {
      this.setSize(400, 100);
      Container pane = this.getContentPane();
      pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

      // options panel
      JPanel optionsPanel = new JPanel();
      optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
      optionsPanel.add(Box.createHorizontalStrut(5));
      JLabel label = new JLabel("Title");
      optionsPanel.add(label);
      optionsPanel.add(Box.createHorizontalStrut(10));
      titleTf = new JTextField(10);
      titleTf.setText("Default Title");
      optionsPanel.add(titleTf);
      optionsPanel.add(Box.createHorizontalStrut(5));
      optionsPanel.add(Box.createHorizontalGlue());

      pane.add(optionsPanel);
      pane.add(Box.createVerticalStrut(5));

      // conversion panel
      JPanel conversionPanel = new JPanel();
      conversionPanel.setLayout(new BoxLayout(conversionPanel, BoxLayout.X_AXIS));
      selectContentButton = new JButton("Select Script");
      selectContentButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            selectContent();
         }
      });
      conversionPanel.add(Box.createHorizontalStrut(5));
      conversionPanel.add(selectContentButton);
      conversionPanel.add(Box.createHorizontalStrut(40));

      convertButton = new JButton("Convert");
      convertButton.setEnabled(false);
      convertButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            convert();
         }
      });
      conversionPanel.add(convertButton);
      conversionPanel.add(Box.createHorizontalStrut(5));
      conversionPanel.add(Box.createHorizontalGlue());
      pane.add(conversionPanel);
      pane.add(Box.createVerticalStrut(5));
      pane.add(Box.createVerticalGlue());
   }

   private void selectContent() {
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
      chooser.setDialogTitle("Select Groovy Script");
      chooser.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            String name = f.getName();
            return f.isDirectory() || name.endsWith(".groovy");
         }

         @Override
         public String getDescription() {
            return "Groovy Scripts";
         }
      });
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setDialogType(JFileChooser.OPEN_DIALOG);
      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
         File file = chooser.getSelectedFile();
         try {
            getScript(file);
            convertButton.setEnabled(true);
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }

   private void convert() {
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
      chooser.setDialogTitle("Select output file");
      chooser.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            String name = f.getName();
            return f.isDirectory() || name.endsWith(".svg");
         }

         @Override
         public String getDescription() {
            return "svg files";
         }
      });
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
         File file = chooser.getSelectedFile();
         if (file != null) {
            String name = file.getName();
            int index = name.lastIndexOf('.');
            if (index == -1) {
               name = name + ".svg";
               file = new File(file.getParentFile(), name);
            }
            convert(file, titleTf.getText());
         }
      }
   }

   private ScriptWrapper getScript(File file) throws Exception {
      wrapper = new ScriptWrapper(file);
      wrapper.createScript(this.getClass().getClassLoader());
      return wrapper;
   }

   private void convert(File file, String title) {
      new JFXPanel();
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
            try {
               if (wrapper != null) {
                  Node node = wrapper.executeScript();
                  if (node != null) {
                     convertToSVG(node, file, title);
                  }
               }
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }
      });
   }

   private void convertToSVG(Node node, File file, String title) throws Exception {
      SVGDriverAppUtils utils = new SVGDriverAppUtils();
      utils.convert(node, file, title);
   }

   public static void main(String[] args) {
      SVGConverterApp app = new SVGConverterApp();
      app.setVisible(true);
   }
}
