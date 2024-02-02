/*    */ package org.h2.index;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.result.Row;
/*    */ import org.h2.result.SearchRow;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MetaCursor
/*    */   implements Cursor
/*    */ {
/*    */   private Row current;
/*    */   private final ArrayList<Row> rows;
/*    */   private int index;
/*    */   
/*    */   MetaCursor(ArrayList<Row> paramArrayList) {
/* 24 */     this.rows = paramArrayList;
/*    */   }
/*    */ 
/*    */   
/*    */   public Row get() {
/* 29 */     return this.current;
/*    */   }
/*    */ 
/*    */   
/*    */   public SearchRow getSearchRow() {
/* 34 */     return (SearchRow)this.current;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean next() {
/* 39 */     this.current = (this.index >= this.rows.size()) ? null : this.rows.get(this.index++);
/* 40 */     return (this.current != null);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean previous() {
/* 45 */     throw DbException.getInternalError(toString());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\MetaCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */