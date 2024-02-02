package org.antlr.v4.runtime;

public class RuntimeMetaData {
   public static final String VERSION = "4.5.3";

   public static String getRuntimeVersion() {
      return "4.5.3";
   }

   public static void checkVersion(String generatingToolVersion, String compileTimeVersion) {
      String runtimeVersion = "4.5.3";
      boolean runtimeConflictsWithGeneratingTool = false;
      boolean runtimeConflictsWithCompileTimeTool = false;
      if (generatingToolVersion != null) {
         runtimeConflictsWithGeneratingTool = !runtimeVersion.equals(generatingToolVersion) && !getMajorMinorVersion(runtimeVersion).equals(getMajorMinorVersion(generatingToolVersion));
      }

      runtimeConflictsWithCompileTimeTool = !runtimeVersion.equals(compileTimeVersion) && !getMajorMinorVersion(runtimeVersion).equals(getMajorMinorVersion(compileTimeVersion));
      if (runtimeConflictsWithGeneratingTool) {
         System.err.printf("ANTLR Tool version %s used for code generation does not match the current runtime version %s", generatingToolVersion, runtimeVersion);
      }

      if (runtimeConflictsWithCompileTimeTool) {
         System.err.printf("ANTLR Runtime version %s used for parser compilation does not match the current runtime version %s", compileTimeVersion, runtimeVersion);
      }

   }

   public static String getMajorMinorVersion(String version) {
      int firstDot = version.indexOf(46);
      int secondDot = firstDot >= 0 ? version.indexOf(46, firstDot + 1) : -1;
      int firstDash = version.indexOf(45);
      int referenceLength = version.length();
      if (secondDot >= 0) {
         referenceLength = Math.min(referenceLength, secondDot);
      }

      if (firstDash >= 0) {
         referenceLength = Math.min(referenceLength, firstDash);
      }

      return version.substring(0, referenceLength);
   }
}
