/*     */ package io.undertow.util;
/*     */ 
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public final class SameSiteNoneIncompatibleClientChecker
/*     */ {
/*  53 */   private static final Pattern IOS_PATTERN = Pattern.compile("\\(iP.+; CPU .*OS (\\d+)[_\\d]*.*\\) AppleWebKit\\/");
/*  54 */   private static final Pattern MACOSX_PATTERN = Pattern.compile("\\(Macintosh;.*Mac OS X (\\d+)_(\\d+)[_\\d]*.*\\) AppleWebKit\\/");
/*  55 */   private static final Pattern SAFARI_PATTERN = Pattern.compile("Version\\/.* Safari\\/");
/*  56 */   private static final Pattern MAC_EMBEDDED_BROWSER_PATTERN = Pattern.compile("^Mozilla\\/[\\.\\d]+ \\(Macintosh;.*Mac OS X [_\\d]+\\) AppleWebKit\\/[\\.\\d]+ \\(KHTML, like Gecko\\)$");
/*  57 */   private static final Pattern CHROMIUM_PATTERN = Pattern.compile("Chrom(e|ium)");
/*  58 */   private static final Pattern CHROMIUM_VERSION_PATTERN = Pattern.compile("Chrom[^ \\/]+\\/(\\d+)[\\.\\d]* ");
/*     */   
/*  60 */   private static final Pattern UC_BROWSER_VERSION_PATTERN = Pattern.compile("UCBrowser\\/(\\d+)\\.(\\d+)\\.(\\d+)[\\.\\d]* ");
/*     */   
/*     */   public static boolean shouldSendSameSiteNone(String useragent) {
/*  63 */     return !isSameSiteNoneIncompatible(useragent);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isSameSiteNoneIncompatible(String useragent) {
/*  68 */     if (useragent == null || useragent.isEmpty()) {
/*  69 */       return false;
/*     */     }
/*     */     
/*  72 */     return (hasWebKitSameSiteBug(useragent) || 
/*  73 */       dropsUnrecognizedSameSiteCookies(useragent));
/*     */   }
/*     */   
/*     */   private static boolean hasWebKitSameSiteBug(String useragent) {
/*  77 */     return (isIosVersion(12, useragent) || (
/*  78 */       isMacosxVersion(10, 14, useragent) && (
/*  79 */       isSafari(useragent) || isMacEmbeddedBrowser(useragent))));
/*     */   }
/*     */   
/*     */   private static boolean dropsUnrecognizedSameSiteCookies(String useragent) {
/*  83 */     if (isUcBrowser(useragent)) {
/*  84 */       return !isUcBrowserVersionAtLeast(12, 13, 2, useragent);
/*     */     }
/*  86 */     return (isChromiumBased(useragent) && 
/*  87 */       isChromiumVersionAtLeast(51, useragent) && 
/*  88 */       !isChromiumVersionAtLeast(67, useragent));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isIosVersion(int major, String useragent) {
/*  94 */     Matcher m = IOS_PATTERN.matcher(useragent);
/*  95 */     if (m.find())
/*     */     {
/*  97 */       return String.valueOf(major).equals(m.group(1));
/*     */     }
/*  99 */     return false;
/*     */   }
/*     */   
/*     */   private static boolean isMacosxVersion(int major, int minor, String useragent) {
/* 103 */     Matcher m = MACOSX_PATTERN.matcher(useragent);
/* 104 */     if (m.find())
/*     */     {
/* 106 */       return (String.valueOf(major).equals(m.group(1)) && 
/* 107 */         String.valueOf(minor).equals(m.group(2)));
/*     */     }
/* 109 */     return false;
/*     */   }
/*     */   
/*     */   private static boolean isSafari(String useragent) {
/* 113 */     return (SAFARI_PATTERN.matcher(useragent).find() && 
/* 114 */       !isChromiumBased(useragent));
/*     */   }
/*     */   
/*     */   private static boolean isMacEmbeddedBrowser(String useragent) {
/* 118 */     return MAC_EMBEDDED_BROWSER_PATTERN.matcher(useragent).find();
/*     */   }
/*     */   
/*     */   private static boolean isChromiumBased(String useragent) {
/* 122 */     return CHROMIUM_PATTERN.matcher(useragent).find();
/*     */   }
/*     */   
/*     */   private static boolean isChromiumVersionAtLeast(int major, String useragent) {
/* 126 */     Matcher m = CHROMIUM_VERSION_PATTERN.matcher(useragent);
/* 127 */     if (m.find()) {
/*     */       
/* 129 */       int version = Integer.parseInt(m.group(1));
/* 130 */       return (version >= major);
/*     */     } 
/* 132 */     return false;
/*     */   }
/*     */   
/*     */   static boolean isUcBrowser(String useragent) {
/* 136 */     return useragent.contains("UCBrowser/");
/*     */   }
/*     */   
/*     */   private static boolean isUcBrowserVersionAtLeast(int major, int minor, int build, String useragent) {
/* 140 */     Matcher m = UC_BROWSER_VERSION_PATTERN.matcher(useragent);
/* 141 */     if (m.find()) {
/*     */       
/* 143 */       int major_version = Integer.parseInt(m.group(1));
/* 144 */       int minor_version = Integer.parseInt(m.group(2));
/* 145 */       int build_version = Integer.parseInt(m.group(3));
/* 146 */       if (major_version != major) {
/* 147 */         return (major_version > major);
/*     */       }
/* 149 */       if (minor_version != minor) {
/* 150 */         return (minor_version > minor);
/*     */       }
/* 152 */       return (build_version >= build);
/*     */     } 
/* 154 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\SameSiteNoneIncompatibleClientChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */