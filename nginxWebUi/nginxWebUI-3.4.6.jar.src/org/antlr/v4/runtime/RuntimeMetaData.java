/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RuntimeMetaData
/*     */ {
/*     */   public static final String VERSION = "4.5.3";
/*     */   
/*     */   public static String getRuntimeVersion() {
/* 107 */     return "4.5.3";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkVersion(String generatingToolVersion, String compileTimeVersion) {
/* 169 */     String runtimeVersion = "4.5.3";
/* 170 */     boolean runtimeConflictsWithGeneratingTool = false;
/* 171 */     boolean runtimeConflictsWithCompileTimeTool = false;
/*     */     
/* 173 */     if (generatingToolVersion != null) {
/* 174 */       runtimeConflictsWithGeneratingTool = (!runtimeVersion.equals(generatingToolVersion) && !getMajorMinorVersion(runtimeVersion).equals(getMajorMinorVersion(generatingToolVersion)));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 179 */     runtimeConflictsWithCompileTimeTool = (!runtimeVersion.equals(compileTimeVersion) && !getMajorMinorVersion(runtimeVersion).equals(getMajorMinorVersion(compileTimeVersion)));
/*     */ 
/*     */ 
/*     */     
/* 183 */     if (runtimeConflictsWithGeneratingTool) {
/* 184 */       System.err.printf("ANTLR Tool version %s used for code generation does not match the current runtime version %s", new Object[] { generatingToolVersion, runtimeVersion });
/*     */     }
/*     */     
/* 187 */     if (runtimeConflictsWithCompileTimeTool) {
/* 188 */       System.err.printf("ANTLR Runtime version %s used for parser compilation does not match the current runtime version %s", new Object[] { compileTimeVersion, runtimeVersion });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMajorMinorVersion(String version) {
/* 203 */     int firstDot = version.indexOf('.');
/* 204 */     int secondDot = (firstDot >= 0) ? version.indexOf('.', firstDot + 1) : -1;
/* 205 */     int firstDash = version.indexOf('-');
/* 206 */     int referenceLength = version.length();
/* 207 */     if (secondDot >= 0) {
/* 208 */       referenceLength = Math.min(referenceLength, secondDot);
/*     */     }
/*     */     
/* 211 */     if (firstDash >= 0) {
/* 212 */       referenceLength = Math.min(referenceLength, firstDash);
/*     */     }
/*     */     
/* 215 */     return version.substring(0, referenceLength);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\RuntimeMetaData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */