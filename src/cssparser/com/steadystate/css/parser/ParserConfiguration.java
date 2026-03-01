/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.steadystate.css.parser;

/**
 *
 * @author scdsahv
 */
public class ParserConfiguration {
   private static ParserConfiguration config = null;
   private short version = -1;
   
   private ParserConfiguration() {      
   }
   
   public static ParserConfiguration getInstance() {
      if (config == null) {
         config = new ParserConfiguration();
      }
      return config;
   }
   
   public void setVersion(short version) {
      this.version = version;
   }   
   
   public short getVersion() {
      return version;
   }
}
