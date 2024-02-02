/*     */ package cn.hutool.core.text.csv;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CsvReadConfig
/*     */   extends CsvConfig<CsvReadConfig>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 5396453565371560052L;
/*  15 */   protected long headerLineNo = -1L;
/*     */   
/*     */   protected boolean skipEmptyRows = true;
/*     */   
/*     */   protected boolean errorOnDifferentFieldCount;
/*     */   
/*     */   protected long beginLineNo;
/*     */   
/*  23 */   protected long endLineNo = 9223372036854775806L;
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean trimField;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CsvReadConfig defaultConfig() {
/*  33 */     return new CsvReadConfig();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReadConfig setContainsHeader(boolean containsHeader) {
/*  45 */     return setHeaderLineNo(containsHeader ? this.beginLineNo : -1L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReadConfig setHeaderLineNo(long headerLineNo) {
/*  56 */     this.headerLineNo = headerLineNo;
/*  57 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReadConfig setSkipEmptyRows(boolean skipEmptyRows) {
/*  67 */     this.skipEmptyRows = skipEmptyRows;
/*  68 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReadConfig setErrorOnDifferentFieldCount(boolean errorOnDifferentFieldCount) {
/*  78 */     this.errorOnDifferentFieldCount = errorOnDifferentFieldCount;
/*  79 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReadConfig setBeginLineNo(long beginLineNo) {
/*  90 */     this.beginLineNo = beginLineNo;
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReadConfig setEndLineNo(long endLineNo) {
/* 102 */     this.endLineNo = endLineNo;
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReadConfig setTrimField(boolean trimField) {
/* 115 */     this.trimField = trimField;
/* 116 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\csv\CsvReadConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */