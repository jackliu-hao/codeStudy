/*    */ package com.github.odiszapc.nginxparser.antlr;
/*    */ 
/*    */ import com.github.odiszapc.nginxparser.NgxConfig;
/*    */ import org.antlr.v4.runtime.misc.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NginxListenerImpl
/*    */   extends NginxBaseListener
/*    */ {
/*    */   private NgxConfig result;
/*    */   
/*    */   public NgxConfig getResult() {
/* 16 */     return this.result;
/*    */   }
/*    */ 
/*    */   
/*    */   public void enterConfig(@NotNull NginxParser.ConfigContext ctx) {
/* 21 */     this.result = ctx.ret;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\antlr\NginxListenerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */