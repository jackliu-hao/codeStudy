/*    */ package io.undertow.protocols.ssl;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import javax.net.ssl.SNIHostName;
/*    */ import javax.net.ssl.SNIMatcher;
/*    */ import javax.net.ssl.SNIServerName;
/*    */ import javax.net.ssl.SSLContext;
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
/*    */ public class SNIContextMatcher
/*    */ {
/*    */   private final SSLContext defaultContext;
/*    */   private final Map<SNIMatcher, SSLContext> wildcards;
/*    */   private final Map<SNIMatcher, SSLContext> exacts;
/*    */   
/*    */   SNIContextMatcher(SSLContext defaultContext, Map<SNIMatcher, SSLContext> wildcards, Map<SNIMatcher, SSLContext> exacts) {
/* 36 */     this.defaultContext = defaultContext;
/* 37 */     this.wildcards = wildcards;
/* 38 */     this.exacts = exacts;
/*    */   }
/*    */   
/*    */   public SSLContext getContext(List<SNIServerName> servers) {
/* 42 */     for (Map.Entry<SNIMatcher, SSLContext> entry : this.exacts.entrySet()) {
/* 43 */       for (SNIServerName server : servers) {
/* 44 */         if (((SNIMatcher)entry.getKey()).matches(server)) {
/* 45 */           return entry.getValue();
/*    */         }
/*    */       } 
/*    */     } 
/* 49 */     for (Map.Entry<SNIMatcher, SSLContext> entry : this.wildcards.entrySet()) {
/* 50 */       for (SNIServerName server : servers) {
/* 51 */         if (((SNIMatcher)entry.getKey()).matches(server)) {
/* 52 */           return entry.getValue();
/*    */         }
/*    */       } 
/*    */     } 
/* 56 */     return this.defaultContext;
/*    */   }
/*    */   
/*    */   public SSLContext getDefaultContext() {
/* 60 */     return this.defaultContext;
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */   {
/*    */     private SSLContext defaultContext;
/* 66 */     private final Map<SNIMatcher, SSLContext> wildcards = new LinkedHashMap<>();
/* 67 */     private final Map<SNIMatcher, SSLContext> exacts = new LinkedHashMap<>();
/*    */     
/*    */     public SNIContextMatcher build() {
/* 70 */       if (this.defaultContext == null) {
/* 71 */         throw UndertowMessages.MESSAGES.defaultContextCannotBeNull();
/*    */       }
/* 73 */       return new SNIContextMatcher(this.defaultContext, this.wildcards, this.exacts);
/*    */     }
/*    */     
/*    */     public SSLContext getDefaultContext() {
/* 77 */       return this.defaultContext;
/*    */     }
/*    */     
/*    */     public Builder setDefaultContext(SSLContext defaultContext) {
/* 81 */       this.defaultContext = defaultContext;
/* 82 */       return this;
/*    */     }
/*    */     
/*    */     public Builder addMatch(String name, SSLContext context) {
/* 86 */       if (name.contains("*")) {
/* 87 */         this.wildcards.put(SNIHostName.createSNIMatcher(name), context);
/*    */       } else {
/* 89 */         this.exacts.put(SNIHostName.createSNIMatcher(name), context);
/*    */       } 
/* 91 */       return this;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\SNIContextMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */