/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.protocol.x.StatementExecuteOk;
/*    */ import java.util.List;
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
/*    */ public class AddResultImpl
/*    */   extends UpdateResult
/*    */   implements AddResult
/*    */ {
/*    */   public AddResultImpl(StatementExecuteOk ok) {
/* 47 */     super(ok);
/*    */   }
/*    */ 
/*    */   
/*    */   public List<String> getGeneratedIds() {
/* 52 */     return this.ok.getGeneratedIds();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\AddResultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */