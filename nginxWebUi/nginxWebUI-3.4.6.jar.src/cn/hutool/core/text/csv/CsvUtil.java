/*     */ package cn.hutool.core.text.csv;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CsvUtil
/*     */ {
/*     */   public static CsvReader getReader(CsvReadConfig config) {
/*  25 */     return new CsvReader(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CsvReader getReader() {
/*  34 */     return new CsvReader();
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
/*     */   public static CsvReader getReader(Reader reader, CsvReadConfig config) {
/*  46 */     return new CsvReader(reader, config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CsvReader getReader(Reader reader) {
/*  57 */     return getReader(reader, null);
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
/*     */   
/*     */   public static CsvWriter getWriter(String filePath, Charset charset) {
/*  70 */     return new CsvWriter(filePath, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CsvWriter getWriter(File file, Charset charset) {
/*  81 */     return new CsvWriter(file, charset);
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
/*     */   public static CsvWriter getWriter(String filePath, Charset charset, boolean isAppend) {
/*  93 */     return new CsvWriter(filePath, charset, isAppend);
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
/*     */   public static CsvWriter getWriter(File file, Charset charset, boolean isAppend) {
/* 105 */     return new CsvWriter(file, charset, isAppend);
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
/*     */   
/*     */   public static CsvWriter getWriter(File file, Charset charset, boolean isAppend, CsvWriteConfig config) {
/* 118 */     return new CsvWriter(file, charset, isAppend, config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CsvWriter getWriter(Writer writer) {
/* 128 */     return new CsvWriter(writer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CsvWriter getWriter(Writer writer, CsvWriteConfig config) {
/* 139 */     return new CsvWriter(writer, config);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\csv\CsvUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */