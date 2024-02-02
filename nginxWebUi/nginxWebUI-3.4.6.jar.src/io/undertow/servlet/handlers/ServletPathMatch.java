/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import javax.servlet.http.MappingMatch;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletPathMatch
/*     */ {
/*     */   private final String matched;
/*     */   private final String remaining;
/*     */   private final boolean requiredWelcomeFileMatch;
/*     */   private final ServletChain servletChain;
/*     */   private final String rewriteLocation;
/*     */   private final Type type;
/*     */   
/*     */   public ServletPathMatch(ServletChain target, String uri, boolean requiredWelcomeFileMatch) {
/*  36 */     this.servletChain = target;
/*  37 */     this.requiredWelcomeFileMatch = requiredWelcomeFileMatch;
/*  38 */     this.type = Type.NORMAL;
/*  39 */     this.rewriteLocation = null;
/*  40 */     if (target.getServletPath() == null) {
/*     */       
/*  42 */       this.matched = uri;
/*  43 */       this.remaining = null;
/*     */     } else {
/*  45 */       this.matched = target.getServletPath();
/*  46 */       if (uri.length() == this.matched.length()) {
/*  47 */         this.remaining = null;
/*     */       } else {
/*  49 */         this.remaining = uri.substring(this.matched.length());
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public ServletPathMatch(ServletChain target, String matched, String remaining, Type type, String rewriteLocation) {
/*  55 */     this.servletChain = target;
/*  56 */     this.matched = matched;
/*  57 */     this.remaining = remaining;
/*  58 */     this.requiredWelcomeFileMatch = false;
/*  59 */     this.type = type;
/*  60 */     this.rewriteLocation = rewriteLocation;
/*     */   }
/*     */   
/*     */   public String getMatched() {
/*  64 */     return this.matched;
/*     */   }
/*     */   
/*     */   public String getRemaining() {
/*  68 */     return this.remaining;
/*     */   }
/*     */   
/*     */   public boolean isRequiredWelcomeFileMatch() {
/*  72 */     return this.requiredWelcomeFileMatch;
/*     */   }
/*     */   
/*     */   public ServletChain getServletChain() {
/*  76 */     return this.servletChain;
/*     */   }
/*     */   
/*     */   public String getRewriteLocation() {
/*  80 */     return this.rewriteLocation;
/*     */   }
/*     */   
/*     */   public Type getType() {
/*  84 */     return this.type;
/*     */   }
/*     */   
/*     */   public String getMatchString() {
/*  88 */     return this.servletChain.getPattern();
/*     */   }
/*     */   
/*     */   public MappingMatch getMappingMatch() {
/*  92 */     return this.servletChain.getMappingMatch();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum Type
/*     */   {
/*  99 */     NORMAL,
/*     */ 
/*     */ 
/*     */     
/* 103 */     REDIRECT,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 108 */     REWRITE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\ServletPathMatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */