/*    */ package cn.hutool.poi.excel.sax.handler;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.lang.func.Func1;
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
/*    */ public abstract class AbstractRowHandler<T>
/*    */   implements RowHandler
/*    */ {
/*    */   protected final int startRowIndex;
/*    */   protected final int endRowIndex;
/*    */   protected Func1<List<Object>, T> convertFunc;
/*    */   
/*    */   public AbstractRowHandler(int startRowIndex, int endRowIndex) {
/* 38 */     this.startRowIndex = startRowIndex;
/* 39 */     this.endRowIndex = endRowIndex;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
/* 44 */     Assert.notNull(this.convertFunc);
/* 45 */     if (rowIndex < this.startRowIndex || rowIndex > this.endRowIndex) {
/*    */       return;
/*    */     }
/* 48 */     handleData(sheetIndex, rowIndex, (T)this.convertFunc.callWithRuntimeException(rowCells));
/*    */   }
/*    */   
/*    */   public abstract void handleData(int paramInt, long paramLong, T paramT);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\handler\AbstractRowHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */