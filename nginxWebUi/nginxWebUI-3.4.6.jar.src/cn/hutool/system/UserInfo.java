/*     */ package cn.hutool.system;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UserInfo
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  22 */   private final String USER_NAME = fixPath(SystemUtil.get("user.name", false));
/*  23 */   private final String USER_HOME = fixPath(SystemUtil.get("user.home", false));
/*  24 */   private final String USER_DIR = fixPath(SystemUtil.get("user.dir", false));
/*  25 */   private final String JAVA_IO_TMPDIR = fixPath(SystemUtil.get("java.io.tmpdir", false));
/*  26 */   private final String USER_LANGUAGE = SystemUtil.get("user.language", false);
/*     */   
/*     */   public UserInfo() {
/*  29 */     String userCountry = SystemUtil.get("user.country", false);
/*  30 */     if (null == userCountry) {
/*  31 */       userCountry = SystemUtil.get("user.country", false);
/*     */     }
/*  33 */     this.USER_COUNTRY = userCountry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String USER_COUNTRY;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getName() {
/*  48 */     return this.USER_NAME;
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
/*     */   public final String getHomeDir() {
/*  63 */     return this.USER_HOME;
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
/*     */   public final String getCurrentDir() {
/*  78 */     return this.USER_DIR;
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
/*     */   public final String getTempDir() {
/*  93 */     return this.JAVA_IO_TMPDIR;
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
/*     */   public final String getLanguage() {
/* 107 */     return this.USER_LANGUAGE;
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
/*     */   public final String getCountry() {
/* 121 */     return this.USER_COUNTRY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 131 */     StringBuilder builder = new StringBuilder();
/*     */     
/* 133 */     SystemUtil.append(builder, "User Name:        ", getName());
/* 134 */     SystemUtil.append(builder, "User Home Dir:    ", getHomeDir());
/* 135 */     SystemUtil.append(builder, "User Current Dir: ", getCurrentDir());
/* 136 */     SystemUtil.append(builder, "User Temp Dir:    ", getTempDir());
/* 137 */     SystemUtil.append(builder, "User Language:    ", getLanguage());
/* 138 */     SystemUtil.append(builder, "User Country:     ", getCountry());
/*     */     
/* 140 */     return builder.toString();
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
/*     */   private static String fixPath(String path) {
/* 154 */     return StrUtil.addSuffixIfNot(path, File.separator);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\UserInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */