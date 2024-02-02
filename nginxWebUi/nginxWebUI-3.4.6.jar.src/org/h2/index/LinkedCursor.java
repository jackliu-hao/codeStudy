/*    */ package org.h2.index;
/*    */ 
/*    */ import java.sql.PreparedStatement;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import org.h2.engine.Session;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.result.Row;
/*    */ import org.h2.result.SearchRow;
/*    */ import org.h2.table.TableLink;
/*    */ import org.h2.value.ValueToObjectConverter2;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LinkedCursor
/*    */   implements Cursor
/*    */ {
/*    */   private final TableLink tableLink;
/*    */   private final PreparedStatement prep;
/*    */   private final String sql;
/*    */   private final SessionLocal session;
/*    */   private final ResultSet rs;
/*    */   private Row current;
/*    */   
/*    */   LinkedCursor(TableLink paramTableLink, ResultSet paramResultSet, SessionLocal paramSessionLocal, String paramString, PreparedStatement paramPreparedStatement) {
/* 32 */     this.session = paramSessionLocal;
/* 33 */     this.tableLink = paramTableLink;
/* 34 */     this.rs = paramResultSet;
/* 35 */     this.sql = paramString;
/* 36 */     this.prep = paramPreparedStatement;
/*    */   }
/*    */ 
/*    */   
/*    */   public Row get() {
/* 41 */     return this.current;
/*    */   }
/*    */ 
/*    */   
/*    */   public SearchRow getSearchRow() {
/* 46 */     return (SearchRow)this.current;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean next() {
/*    */     try {
/* 52 */       boolean bool = this.rs.next();
/* 53 */       if (!bool) {
/* 54 */         this.rs.close();
/* 55 */         this.tableLink.reusePreparedStatement(this.prep, this.sql);
/* 56 */         this.current = null;
/* 57 */         return false;
/*    */       } 
/* 59 */     } catch (SQLException sQLException) {
/* 60 */       throw DbException.convert(sQLException);
/*    */     } 
/* 62 */     this.current = this.tableLink.getTemplateRow();
/* 63 */     for (byte b = 0; b < this.current.getColumnCount(); b++) {
/* 64 */       this.current.setValue(b, ValueToObjectConverter2.readValue((Session)this.session, this.rs, b + 1, this.tableLink
/* 65 */             .getColumn(b).getType().getValueType()));
/*    */     }
/* 67 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean previous() {
/* 72 */     throw DbException.getInternalError(toString());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\LinkedCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */