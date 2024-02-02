/*    */ package io.undertow.security.idm;
/*    */ 
/*    */ import org.ietf.jgss.GSSContext;
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
/*    */ public class GSSContextCredential
/*    */   implements Credential
/*    */ {
/*    */   private final GSSContext gssContext;
/*    */   
/*    */   public GSSContextCredential(GSSContext gssContext) {
/* 32 */     this.gssContext = gssContext;
/*    */   }
/*    */   
/*    */   public GSSContext getGssContext() {
/* 36 */     return this.gssContext;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\idm\GSSContextCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */