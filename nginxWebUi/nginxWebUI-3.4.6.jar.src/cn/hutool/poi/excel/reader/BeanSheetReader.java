/*    */ package cn.hutool.poi.excel.reader;
/*    */ 
/*    */ import cn.hutool.core.bean.BeanUtil;
/*    */ import cn.hutool.core.bean.copier.CopyOptions;
/*    */ import cn.hutool.poi.excel.cell.CellEditor;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.apache.poi.ss.usermodel.Sheet;
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
/*    */ public class BeanSheetReader<T>
/*    */   implements SheetReader<List<T>>
/*    */ {
/*    */   private final Class<T> beanClass;
/*    */   private final MapSheetReader mapSheetReader;
/*    */   
/*    */   public BeanSheetReader(int headerRowIndex, int startRowIndex, int endRowIndex, Class<T> beanClass) {
/* 32 */     this.mapSheetReader = new MapSheetReader(headerRowIndex, startRowIndex, endRowIndex);
/* 33 */     this.beanClass = beanClass;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public List<T> read(Sheet sheet) {
/* 39 */     List<Map<String, Object>> mapList = this.mapSheetReader.read(sheet);
/* 40 */     if (Map.class.isAssignableFrom(this.beanClass)) {
/* 41 */       return (List)mapList;
/*    */     }
/*    */     
/* 44 */     List<T> beanList = new ArrayList<>(mapList.size());
/* 45 */     CopyOptions copyOptions = CopyOptions.create().setIgnoreError(true);
/* 46 */     for (Map<String, Object> map : mapList) {
/* 47 */       beanList.add((T)BeanUtil.toBean(map, this.beanClass, copyOptions));
/*    */     }
/* 49 */     return beanList;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setCellEditor(CellEditor cellEditor) {
/* 59 */     this.mapSheetReader.setCellEditor(cellEditor);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setIgnoreEmptyRow(boolean ignoreEmptyRow) {
/* 68 */     this.mapSheetReader.setIgnoreEmptyRow(ignoreEmptyRow);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setHeaderAlias(Map<String, String> headerAlias) {
/* 77 */     this.mapSheetReader.setHeaderAlias(headerAlias);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void addHeaderAlias(String header, String alias) {
/* 87 */     this.mapSheetReader.addHeaderAlias(header, alias);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\reader\BeanSheetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */