/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.Collections;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class DefaultServletConfig
/*    */ {
/* 37 */   private static final String[] DEFAULT_ALLOWED_EXTENSIONS = new String[] { "js", "css", "png", "jpg", "gif", "html", "htm", "txt", "pdf" };
/* 38 */   private static final String[] DEFAULT_DISALLOWED_EXTENSIONS = new String[] { "class", "jar", "war", "zip", "xml" };
/*    */   
/*    */   private final boolean defaultAllowed;
/*    */   private final Set<String> allowed;
/*    */   private final Set<String> disallowed;
/*    */   
/*    */   public DefaultServletConfig(boolean defaultAllowed, Set<String> exceptions) {
/* 45 */     this.defaultAllowed = defaultAllowed;
/* 46 */     if (defaultAllowed) {
/* 47 */       this.disallowed = Collections.unmodifiableSet(new HashSet<>(exceptions));
/* 48 */       this.allowed = null;
/*    */     } else {
/* 50 */       this.allowed = Collections.unmodifiableSet(new HashSet<>(exceptions));
/* 51 */       this.disallowed = null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public DefaultServletConfig(boolean defaultAllowed) {
/* 56 */     this.defaultAllowed = defaultAllowed;
/* 57 */     this.allowed = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(DEFAULT_ALLOWED_EXTENSIONS)));
/* 58 */     this.disallowed = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(DEFAULT_DISALLOWED_EXTENSIONS)));
/*    */   }
/*    */   
/*    */   public DefaultServletConfig() {
/* 62 */     this.defaultAllowed = false;
/* 63 */     this.allowed = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(DEFAULT_ALLOWED_EXTENSIONS)));
/* 64 */     this.disallowed = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(DEFAULT_DISALLOWED_EXTENSIONS)));
/*    */   }
/*    */   
/*    */   public boolean isDefaultAllowed() {
/* 68 */     return this.defaultAllowed;
/*    */   }
/*    */   
/*    */   public Set<String> getAllowed() {
/* 72 */     return this.allowed;
/*    */   }
/*    */   
/*    */   public Set<String> getDisallowed() {
/* 76 */     return this.disallowed;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\DefaultServletConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */