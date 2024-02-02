/*    */ package cn.hutool.poi.excel.cell;
/*    */ 
/*    */ import org.apache.poi.ss.usermodel.Cell;
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
/*    */ public class FormulaCellValue
/*    */   implements CellValue<String>, CellSetter
/*    */ {
/*    */   private final String formula;
/*    */   private final Object result;
/*    */   
/*    */   public FormulaCellValue(String formula) {
/* 33 */     this(formula, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FormulaCellValue(String formula, Object result) {
/* 43 */     this.formula = formula;
/* 44 */     this.result = result;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 49 */     return this.formula;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValue(Cell cell) {
/* 54 */     cell.setCellFormula(this.formula);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getResult() {
/* 62 */     return this.result;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     return getResult().toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\FormulaCellValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */