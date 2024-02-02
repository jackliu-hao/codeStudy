/*     */ package cn.hutool.setting.profile;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.setting.Setting;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Profile
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -4189955219454008744L;
/*     */   public static final String DEFAULT_PROFILE = "default";
/*     */   private String profile;
/*     */   private Charset charset;
/*     */   private boolean useVar;
/*  39 */   private final Map<String, Setting> settingMap = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Profile() {
/*  46 */     this("default");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Profile(String profile) {
/*  55 */     this(profile, Setting.DEFAULT_CHARSET, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Profile(String profile, Charset charset, boolean useVar) {
/*  66 */     this.profile = profile;
/*  67 */     this.charset = charset;
/*  68 */     this.useVar = useVar;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting getSetting(String name) {
/*  79 */     String nameForProfile = fixNameForProfile(name);
/*  80 */     Setting setting = this.settingMap.get(nameForProfile);
/*  81 */     if (null == setting) {
/*  82 */       setting = new Setting(nameForProfile, this.charset, this.useVar);
/*  83 */       this.settingMap.put(nameForProfile, setting);
/*     */     } 
/*  85 */     return setting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Profile setProfile(String profile) {
/*  95 */     this.profile = profile;
/*  96 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Profile setCharset(Charset charset) {
/* 106 */     this.charset = charset;
/* 107 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Profile setUseVar(boolean useVar) {
/* 117 */     this.useVar = useVar;
/* 118 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Profile clear() {
/* 127 */     this.settingMap.clear();
/* 128 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String fixNameForProfile(String name) {
/* 139 */     Assert.notBlank(name, "Setting name must be not blank !", new Object[0]);
/* 140 */     String actralProfile = StrUtil.nullToEmpty(this.profile);
/* 141 */     if (false == name.contains(".")) {
/* 142 */       return StrUtil.format("{}/{}.setting", new Object[] { actralProfile, name });
/*     */     }
/* 144 */     return StrUtil.format("{}/{}", new Object[] { actralProfile, name });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\setting\profile\Profile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */