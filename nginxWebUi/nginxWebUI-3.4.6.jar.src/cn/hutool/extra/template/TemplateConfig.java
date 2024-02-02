/*     */ package cn.hutool.extra.template;
/*     */ 
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TemplateConfig
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 2933113779920339523L;
/*  18 */   public static final TemplateConfig DEFAULT = new TemplateConfig();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Charset charset;
/*     */ 
/*     */ 
/*     */   
/*     */   private String path;
/*     */ 
/*     */ 
/*     */   
/*     */   private ResourceMode resourceMode;
/*     */ 
/*     */ 
/*     */   
/*     */   private Class<? extends TemplateEngine> customEngine;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateConfig() {
/*  41 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateConfig(String path) {
/*  50 */     this(path, ResourceMode.STRING);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateConfig(String path, ResourceMode resourceMode) {
/*  60 */     this(CharsetUtil.CHARSET_UTF_8, path, resourceMode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateConfig(Charset charset, String path, ResourceMode resourceMode) {
/*  71 */     this.charset = charset;
/*  72 */     this.path = path;
/*  73 */     this.resourceMode = resourceMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/*  82 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCharsetStr() {
/*  92 */     if (null == this.charset) {
/*  93 */       return null;
/*     */     }
/*  95 */     return this.charset.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCharset(Charset charset) {
/* 104 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPath() {
/* 113 */     return this.path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPath(String path) {
/* 122 */     this.path = path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResourceMode getResourceMode() {
/* 131 */     return this.resourceMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setResourceMode(ResourceMode resourceMode) {
/* 140 */     this.resourceMode = resourceMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<? extends TemplateEngine> getCustomEngine() {
/* 150 */     return this.customEngine;
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
/*     */   public TemplateConfig setCustomEngine(Class<? extends TemplateEngine> customEngine) {
/* 162 */     this.customEngine = customEngine;
/* 163 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum ResourceMode
/*     */   {
/* 175 */     CLASSPATH,
/*     */ 
/*     */ 
/*     */     
/* 179 */     FILE,
/*     */ 
/*     */ 
/*     */     
/* 183 */     WEB_ROOT,
/*     */ 
/*     */ 
/*     */     
/* 187 */     STRING,
/*     */ 
/*     */ 
/*     */     
/* 191 */     COMPOSITE;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 196 */     if (this == o) {
/* 197 */       return true;
/*     */     }
/* 199 */     if (o == null || getClass() != o.getClass()) {
/* 200 */       return false;
/*     */     }
/* 202 */     TemplateConfig that = (TemplateConfig)o;
/* 203 */     return (Objects.equals(this.charset, that.charset) && 
/* 204 */       Objects.equals(this.path, that.path) && this.resourceMode == that.resourceMode && 
/*     */       
/* 206 */       Objects.equals(this.customEngine, that.customEngine));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 211 */     return Objects.hash(new Object[] { this.charset, this.path, this.resourceMode, this.customEngine });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\TemplateConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */