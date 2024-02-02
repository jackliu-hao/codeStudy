/*    */ package cn.hutool.core.text.csv;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public class CsvWriteConfig
/*    */   extends CsvConfig<CsvWriteConfig>
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 5396453565371560052L;
/*    */   protected boolean alwaysDelimitText;
/* 22 */   protected char[] lineDelimiter = new char[] { '\r', '\n' };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CsvWriteConfig defaultConfig() {
/* 30 */     return new CsvWriteConfig();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CsvWriteConfig setAlwaysDelimitText(boolean alwaysDelimitText) {
/* 40 */     this.alwaysDelimitText = alwaysDelimitText;
/* 41 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CsvWriteConfig setLineDelimiter(char[] lineDelimiter) {
/* 51 */     this.lineDelimiter = lineDelimiter;
/* 52 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\csv\CsvWriteConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */