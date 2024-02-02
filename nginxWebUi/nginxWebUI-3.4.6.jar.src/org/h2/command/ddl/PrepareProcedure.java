/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.command.Prepared;
/*    */ import org.h2.engine.Procedure;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Parameter;
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
/*    */ public class PrepareProcedure
/*    */   extends DefineCommand
/*    */ {
/*    */   private String procedureName;
/*    */   private Prepared prepared;
/*    */   
/*    */   public PrepareProcedure(SessionLocal paramSessionLocal) {
/* 26 */     super(paramSessionLocal);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void checkParameters() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public long update() {
/* 36 */     Procedure procedure = new Procedure(this.procedureName, this.prepared);
/* 37 */     this.prepared.setParameterList(this.parameters);
/* 38 */     this.prepared.setPrepareAlways(this.prepareAlways);
/* 39 */     this.prepared.prepare();
/* 40 */     this.session.addProcedure(procedure);
/* 41 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setProcedureName(String paramString) {
/* 45 */     this.procedureName = paramString;
/*    */   }
/*    */   
/*    */   public void setPrepared(Prepared paramPrepared) {
/* 49 */     this.prepared = paramPrepared;
/*    */   }
/*    */ 
/*    */   
/*    */   public ArrayList<Parameter> getParameters() {
/* 54 */     return new ArrayList<>(0);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 59 */     return 51;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\PrepareProcedure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */