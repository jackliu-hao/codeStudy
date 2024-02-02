/*     */ package org.apache.http.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
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
/*     */ public class VersionInfo
/*     */ {
/*     */   public static final String UNAVAILABLE = "UNAVAILABLE";
/*     */   public static final String VERSION_PROPERTY_FILE = "version.properties";
/*     */   public static final String PROPERTY_MODULE = "info.module";
/*     */   public static final String PROPERTY_RELEASE = "info.release";
/*     */   public static final String PROPERTY_TIMESTAMP = "info.timestamp";
/*     */   private final String infoPackage;
/*     */   private final String infoModule;
/*     */   private final String infoRelease;
/*     */   private final String infoTimestamp;
/*     */   private final String infoClassloader;
/*     */   
/*     */   protected VersionInfo(String pckg, String module, String release, String time, String clsldr) {
/*  91 */     Args.notNull(pckg, "Package identifier");
/*  92 */     this.infoPackage = pckg;
/*  93 */     this.infoModule = (module != null) ? module : "UNAVAILABLE";
/*  94 */     this.infoRelease = (release != null) ? release : "UNAVAILABLE";
/*  95 */     this.infoTimestamp = (time != null) ? time : "UNAVAILABLE";
/*  96 */     this.infoClassloader = (clsldr != null) ? clsldr : "UNAVAILABLE";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getPackage() {
/* 107 */     return this.infoPackage;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getModule() {
/* 117 */     return this.infoModule;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getRelease() {
/* 127 */     return this.infoRelease;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getTimestamp() {
/* 137 */     return this.infoTimestamp;
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
/*     */   public final String getClassloader() {
/* 149 */     return this.infoClassloader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 160 */     StringBuilder sb = new StringBuilder(20 + this.infoPackage.length() + this.infoModule.length() + this.infoRelease.length() + this.infoTimestamp.length() + this.infoClassloader.length());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 165 */     sb.append("VersionInfo(").append(this.infoPackage).append(':').append(this.infoModule);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 170 */     if (!"UNAVAILABLE".equals(this.infoRelease)) {
/* 171 */       sb.append(':').append(this.infoRelease);
/*     */     }
/* 173 */     if (!"UNAVAILABLE".equals(this.infoTimestamp)) {
/* 174 */       sb.append(':').append(this.infoTimestamp);
/*     */     }
/*     */     
/* 177 */     sb.append(')');
/*     */     
/* 179 */     if (!"UNAVAILABLE".equals(this.infoClassloader)) {
/* 180 */       sb.append('@').append(this.infoClassloader);
/*     */     }
/*     */     
/* 183 */     return sb.toString();
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
/*     */   public static VersionInfo[] loadVersionInfo(String[] pckgs, ClassLoader clsldr) {
/* 199 */     Args.notNull(pckgs, "Package identifier array");
/* 200 */     List<VersionInfo> vil = new ArrayList<VersionInfo>(pckgs.length);
/* 201 */     for (String pckg : pckgs) {
/* 202 */       VersionInfo vi = loadVersionInfo(pckg, clsldr);
/* 203 */       if (vi != null) {
/* 204 */         vil.add(vi);
/*     */       }
/*     */     } 
/*     */     
/* 208 */     return vil.<VersionInfo>toArray(new VersionInfo[vil.size()]);
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
/*     */   public static VersionInfo loadVersionInfo(String pckg, ClassLoader clsldr) {
/* 226 */     Args.notNull(pckg, "Package identifier");
/* 227 */     ClassLoader cl = (clsldr != null) ? clsldr : Thread.currentThread().getContextClassLoader();
/*     */     
/* 229 */     Properties vip = null;
/*     */ 
/*     */     
/*     */     try {
/* 233 */       InputStream is = cl.getResourceAsStream(pckg.replace('.', '/') + "/" + "version.properties");
/*     */       
/* 235 */       if (is != null) {
/*     */         try {
/* 237 */           Properties props = new Properties();
/* 238 */           props.load(is);
/* 239 */           vip = props;
/*     */         } finally {
/* 241 */           is.close();
/*     */         } 
/*     */       }
/* 244 */     } catch (IOException ex) {}
/*     */ 
/*     */ 
/*     */     
/* 248 */     VersionInfo result = null;
/* 249 */     if (vip != null) {
/* 250 */       result = fromMap(pckg, vip, cl);
/*     */     }
/*     */     
/* 253 */     return result;
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
/*     */   protected static VersionInfo fromMap(String pckg, Map<?, ?> info, ClassLoader clsldr) {
/* 269 */     Args.notNull(pckg, "Package identifier");
/* 270 */     String module = null;
/* 271 */     String release = null;
/* 272 */     String timestamp = null;
/*     */     
/* 274 */     if (info != null) {
/* 275 */       module = (String)info.get("info.module");
/* 276 */       if (module != null && module.length() < 1) {
/* 277 */         module = null;
/*     */       }
/*     */       
/* 280 */       release = (String)info.get("info.release");
/* 281 */       if (release != null && (release.length() < 1 || release.equals("${pom.version}")))
/*     */       {
/* 283 */         release = null;
/*     */       }
/*     */       
/* 286 */       timestamp = (String)info.get("info.timestamp");
/* 287 */       if (timestamp != null && (timestamp.length() < 1 || timestamp.equals("${mvn.timestamp}")))
/*     */       {
/*     */ 
/*     */         
/* 291 */         timestamp = null;
/*     */       }
/*     */     } 
/*     */     
/* 295 */     String clsldrstr = null;
/* 296 */     if (clsldr != null) {
/* 297 */       clsldrstr = clsldr.toString();
/*     */     }
/*     */     
/* 300 */     return new VersionInfo(pckg, module, release, timestamp, clsldrstr);
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
/*     */   public static String getUserAgent(String name, String pkg, Class<?> cls) {
/* 319 */     VersionInfo vi = loadVersionInfo(pkg, cls.getClassLoader());
/* 320 */     String release = (vi != null) ? vi.getRelease() : "UNAVAILABLE";
/* 321 */     String javaVersion = System.getProperty("java.version");
/*     */     
/* 323 */     return String.format("%s/%s (Java/%s)", new Object[] { name, release, javaVersion });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\VersionInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */