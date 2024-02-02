/*    */ package cn.hutool.core.text.csv;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Collections;
/*    */ import java.util.Iterator;
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
/*    */ public class CsvData
/*    */   implements Iterable<CsvRow>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final List<String> header;
/*    */   private final List<CsvRow> rows;
/*    */   
/*    */   public CsvData(List<String> header, List<CsvRow> rows) {
/* 26 */     this.header = header;
/* 27 */     this.rows = rows;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getRowCount() {
/* 36 */     return this.rows.size();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<String> getHeader() {
/* 45 */     return Collections.unmodifiableList(this.header);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CsvRow getRow(int index) {
/* 56 */     return this.rows.get(index);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<CsvRow> getRows() {
/* 65 */     return this.rows;
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<CsvRow> iterator() {
/* 70 */     return this.rows.iterator();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 75 */     return "CsvData{header=" + this.header + ", rows=" + this.rows + '}';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\csv\CsvData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */