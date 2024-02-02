package org.antlr.v4.runtime.misc;

import java.lang.reflect.Method;

/** @deprecated */
public class TestRig {
   public static void main(String[] args) {
      try {
         Class<?> testRigClass = Class.forName("org.antlr.v4.gui.TestRig");
         System.err.println("Warning: TestRig moved to org.antlr.v4.gui.TestRig; calling automatically");

         try {
            Method mainMethod = testRigClass.getMethod("main", String[].class);
            mainMethod.invoke((Object)null, args);
         } catch (Exception var3) {
            System.err.println("Problems calling org.antlr.v4.gui.TestRig.main(args)");
         }
      } catch (ClassNotFoundException var4) {
         System.err.println("Use of TestRig now requires the use of the tool jar, antlr-4.X-complete.jar");
         System.err.println("Maven users need group ID org.antlr and artifact ID antlr4");
      }

   }
}
