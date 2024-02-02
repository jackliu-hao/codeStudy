/*     */ package cn.hutool.system;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JavaInfo
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  14 */   private final String JAVA_VERSION = SystemUtil.get("java.version", false);
/*  15 */   private final float JAVA_VERSION_FLOAT = getJavaVersionAsFloat();
/*  16 */   private final int JAVA_VERSION_INT = getJavaVersionAsInt();
/*  17 */   private final String JAVA_VENDOR = SystemUtil.get("java.vendor", false);
/*  18 */   private final String JAVA_VENDOR_URL = SystemUtil.get("java.vendor.url", false);
/*     */   
/*  20 */   private final boolean IS_JAVA_1_8 = getJavaVersionMatches("1.8");
/*  21 */   private final boolean IS_JAVA_9 = getJavaVersionMatches("9");
/*  22 */   private final boolean IS_JAVA_10 = getJavaVersionMatches("10");
/*  23 */   private final boolean IS_JAVA_11 = getJavaVersionMatches("11");
/*  24 */   private final boolean IS_JAVA_12 = getJavaVersionMatches("12");
/*  25 */   private final boolean IS_JAVA_13 = getJavaVersionMatches("13");
/*  26 */   private final boolean IS_JAVA_14 = getJavaVersionMatches("14");
/*  27 */   private final boolean IS_JAVA_15 = getJavaVersionMatches("15");
/*  28 */   private final boolean IS_JAVA_16 = getJavaVersionMatches("16");
/*  29 */   private final boolean IS_JAVA_17 = getJavaVersionMatches("17");
/*  30 */   private final boolean IS_JAVA_18 = getJavaVersionMatches("18");
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
/*     */   public final String getVersion() {
/*  43 */     return this.JAVA_VERSION;
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
/*     */   public final float getVersionFloat() {
/*  60 */     return this.JAVA_VERSION_FLOAT;
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
/*     */   public final int getVersionInt() {
/*  79 */     return this.JAVA_VERSION_INT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private float getJavaVersionAsFloat() {
/*  88 */     if (this.JAVA_VERSION == null) {
/*  89 */       return 0.0F;
/*     */     }
/*     */     
/*  92 */     String str = this.JAVA_VERSION;
/*     */     
/*  94 */     str = ReUtil.get("^[0-9]{1,2}(\\.[0-9]{1,2})?", str, 0);
/*     */     
/*  96 */     return Float.parseFloat(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getJavaVersionAsInt() {
/* 105 */     if (this.JAVA_VERSION == null) {
/* 106 */       return 0;
/*     */     }
/*     */     
/* 109 */     String javaVersion = ReUtil.get("^[0-9]{1,2}(\\.[0-9]{1,2}){0,2}", this.JAVA_VERSION, 0);
/*     */     
/* 111 */     String[] split = javaVersion.split("\\.");
/* 112 */     String result = ArrayUtil.join((Object[])split, "");
/*     */ 
/*     */     
/* 115 */     if (split[0].length() > 1) {
/* 116 */       result = (result + "0000").substring(0, 4);
/*     */     }
/*     */     
/* 119 */     return Integer.parseInt(result);
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
/*     */   public final String getVendor() {
/* 132 */     return this.JAVA_VENDOR;
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
/*     */   public final String getVendorURL() {
/* 145 */     return this.JAVA_VENDOR_URL;
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
/*     */   @Deprecated
/*     */   public final boolean isJava1_1() {
/* 158 */     return false;
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
/*     */   @Deprecated
/*     */   public final boolean isJava1_2() {
/* 171 */     return false;
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
/*     */   @Deprecated
/*     */   public final boolean isJava1_3() {
/* 184 */     return false;
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
/*     */   @Deprecated
/*     */   public final boolean isJava1_4() {
/* 197 */     return false;
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
/*     */   @Deprecated
/*     */   public final boolean isJava1_5() {
/* 210 */     return false;
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
/*     */   @Deprecated
/*     */   public final boolean isJava1_6() {
/* 223 */     return false;
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
/*     */   @Deprecated
/*     */   public final boolean isJava1_7() {
/* 236 */     return false;
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
/*     */   public final boolean isJava1_8() {
/* 248 */     return this.IS_JAVA_1_8;
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
/*     */   public final boolean isJava9() {
/* 260 */     return this.IS_JAVA_9;
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
/*     */   public final boolean isJava10() {
/* 272 */     return this.IS_JAVA_10;
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
/*     */   public final boolean isJava11() {
/* 284 */     return this.IS_JAVA_11;
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
/*     */   public final boolean isJava12() {
/* 296 */     return this.IS_JAVA_12;
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
/*     */   public final boolean isJava13() {
/* 308 */     return this.IS_JAVA_13;
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
/*     */   public final boolean isJava14() {
/* 321 */     return this.IS_JAVA_14;
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
/*     */   public final boolean isJava15() {
/* 333 */     return this.IS_JAVA_15;
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
/*     */   public final boolean isJava16() {
/* 345 */     return this.IS_JAVA_16;
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
/*     */   public final boolean isJava17() {
/* 357 */     return this.IS_JAVA_17;
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
/*     */   public final boolean isJava18() {
/* 369 */     return this.IS_JAVA_18;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean getJavaVersionMatches(String versionPrefix) {
/* 379 */     if (this.JAVA_VERSION == null) {
/* 380 */       return false;
/*     */     }
/*     */     
/* 383 */     return this.JAVA_VERSION.startsWith(versionPrefix);
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
/*     */   public final boolean isJavaVersionAtLeast(float requiredVersion) {
/* 402 */     return (getVersionFloat() >= requiredVersion);
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
/*     */   public final boolean isJavaVersionAtLeast(int requiredVersion) {
/* 421 */     return (getVersionInt() >= requiredVersion);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 431 */     StringBuilder builder = new StringBuilder();
/*     */     
/* 433 */     SystemUtil.append(builder, "Java Version:    ", getVersion());
/* 434 */     SystemUtil.append(builder, "Java Vendor:     ", getVendor());
/* 435 */     SystemUtil.append(builder, "Java Vendor URL: ", getVendorURL());
/*     */     
/* 437 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\JavaInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */