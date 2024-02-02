/*    */ package cn.hutool.poi.excel.sax.handler;
/*    */ 
/*    */ import cn.hutool.core.collection.IterUtil;
/*    */ import cn.hutool.core.collection.ListUtil;
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import java.lang.invoke.SerializedLambda;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public abstract class MapRowHandler
/*    */   extends AbstractRowHandler<Map<String, Object>>
/*    */ {
/*    */   private final int headerRowIndex;
/*    */   List<String> headerList;
/*    */   
/*    */   public MapRowHandler(int headerRowIndex, int startRowIndex, int endRowIndex) {
/* 36 */     super(startRowIndex, endRowIndex);
/* 37 */     this.headerRowIndex = headerRowIndex;
/* 38 */     this.convertFunc = (rowList -> IterUtil.toMap(this.headerList, rowList));
/*    */   }
/*    */ 
/*    */   
/*    */   public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
/* 43 */     if (rowIndex == this.headerRowIndex) {
/* 44 */       this.headerList = ListUtil.unmodifiable(Convert.toList(String.class, rowCells));
/*    */       return;
/*    */     } 
/* 47 */     super.handle(sheetIndex, rowIndex, rowCells);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\handler\MapRowHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */