/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
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
/*    */ public class DeallocateProcedure
/*    */   extends DefineCommand
/*    */ {
/*    */   private String procedureName;
/*    */   
/*    */   public DeallocateProcedure(SessionLocal paramSessionLocal) {
/* 20 */     super(paramSessionLocal);
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 25 */     this.session.removeProcedure(this.procedureName);
/* 26 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setProcedureName(String paramString) {
/* 30 */     this.procedureName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 35 */     return 35;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DeallocateProcedure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */