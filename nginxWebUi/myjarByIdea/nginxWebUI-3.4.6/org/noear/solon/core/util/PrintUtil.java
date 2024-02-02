package org.noear.solon.core.util;

import java.io.File;

public class PrintUtil {
   public static final String ANSI_RESET = "\u001b[0m";
   public static final String ANSI_BLACK = "\u001b[30m";
   public static final String ANSI_RED = "\u001b[31m";
   public static final String ANSI_GREEN = "\u001b[32m";
   public static final String ANSI_YELLOW = "\u001b[33m";
   public static final String ANSI_BLUE = "\u001b[34m";
   public static final String ANSI_PURPLE = "\u001b[35m";
   public static final String ANSI_CYAN = "\u001b[36m";
   public static final String ANSI_WHITE = "\u001b[37m";
   public static final boolean IS_WINDOWS;

   public static void blackln(Object txt) {
      colorln("\u001b[30m", txt);
   }

   public static void redln(Object txt) {
      colorln("\u001b[31m", txt);
   }

   public static void blueln(Object txt) {
      colorln("\u001b[34m", txt);
   }

   public static void greenln(Object txt) {
      colorln("\u001b[32m", txt);
   }

   public static void purpleln(Object txt) {
      colorln("\u001b[35m", txt);
   }

   public static void yellowln(Object txt) {
      colorln("\u001b[33m", txt);
   }

   public static void colorln(String color, Object s) {
      if (IS_WINDOWS) {
         System.out.println(s);
      } else {
         System.out.println(color + s);
         System.out.print("\u001b[0m");
      }

   }

   public static void debug(Object content) {
      System.out.print("[Solon] ");
      blueln(content);
   }

   public static void debug(String label, Object content) {
      System.out.print("[Solon] ");
      blueln(label + ": " + content);
   }

   public static void info(Object content) {
      System.out.println("[Solon] " + content);
   }

   public static void info(String label, Object content) {
      System.out.print("[Solon] ");
      greenln(label + ": " + content);
   }

   public static void warn(Object content) {
      System.out.print("[Solon] ");
      yellowln(content);
   }

   public static void warn(String label, Object content) {
      System.out.print("[Solon] ");
      yellowln(label + ": " + content);
   }

   public static void error(Object content) {
      System.out.print("[Solon] ");
      redln(content);
   }

   public static void error(String label, Object content) {
      System.out.print("[Solon] ");
      redln(label + ": " + content);
   }

   static {
      IS_WINDOWS = File.separatorChar == '\\';
   }
}
