/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.table.TableView;
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
/*    */ public class AlterView
/*    */   extends DefineCommand
/*    */ {
/*    */   private boolean ifExists;
/*    */   private TableView view;
/*    */   
/*    */   public AlterView(SessionLocal paramSessionLocal) {
/* 23 */     super(paramSessionLocal);
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 27 */     this.ifExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setView(TableView paramTableView) {
/* 31 */     this.view = paramTableView;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 36 */     if (this.view == null && this.ifExists) {
/* 37 */       return 0L;
/*    */     }
/* 39 */     this.session.getUser().checkSchemaOwner(this.view.getSchema());
/* 40 */     DbException dbException = this.view.recompile(this.session, false, true);
/* 41 */     if (dbException != null) {
/* 42 */       throw dbException;
/*    */     }
/* 44 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 49 */     return 20;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */