/*    */ package cn.hutool.poi.excel.sax.handler;
/*    */ 
/*    */ import cn.hutool.core.bean.BeanUtil;
/*    */ import cn.hutool.core.collection.IterUtil;
/*    */ import cn.hutool.core.collection.ListUtil;
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.lang.invoke.SerializedLambda;
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
/*    */ public abstract class BeanRowHandler<T>
/*    */   extends AbstractRowHandler<T>
/*    */ {
/*    */   private final int headerRowIndex;
/*    */   List<String> headerList;
/*    */   
/*    */   public BeanRowHandler(int headerRowIndex, int startRowIndex, int endRowIndex, Class<T> clazz) {
/* 38 */     super(startRowIndex, endRowIndex);
/* 39 */     Assert.isTrue((headerRowIndex <= startRowIndex), "Header row must before the start row!", new Object[0]);
/* 40 */     this.headerRowIndex = headerRowIndex;
/* 41 */     this.convertFunc = (rowList -> BeanUtil.toBean(IterUtil.toMap(this.headerList, rowList), clazz));
/*    */   }
/*    */ 
/*    */   
/*    */   public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
/* 46 */     if (rowIndex == this.headerRowIndex) {
/* 47 */       this.headerList = ListUtil.unmodifiable(Convert.toList(String.class, rowCells));
/*    */       return;
/*    */     } 
/* 50 */     super.handle(sheetIndex, rowIndex, rowCells);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\handler\BeanRowHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */