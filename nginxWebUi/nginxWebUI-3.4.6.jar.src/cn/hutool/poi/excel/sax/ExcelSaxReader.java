/*     */ package cn.hutool.poi.excel.sax;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.poi.exceptions.POIException;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface ExcelSaxReader<T>
/*     */ {
/*     */   public static final String RID_PREFIX = "rId";
/*     */   public static final String SHEET_NAME_PREFIX = "sheetName:";
/*     */   
/*     */   T read(File paramFile, String paramString) throws POIException;
/*     */   
/*     */   T read(InputStream paramInputStream, String paramString) throws POIException;
/*     */   
/*     */   default T read(String path) throws POIException {
/*  51 */     return read(FileUtil.file(path));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default T read(File file) throws POIException {
/*  62 */     return read(file, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default T read(InputStream in) throws POIException {
/*  73 */     return read(in, -1);
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
/*     */   default T read(String path, int idOrRidOrSheetName) throws POIException {
/*  85 */     return read(FileUtil.file(path), idOrRidOrSheetName);
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
/*     */   default T read(String path, String idOrRidOrSheetName) throws POIException {
/*  97 */     return read(FileUtil.file(path), idOrRidOrSheetName);
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
/*     */   default T read(File file, int rid) throws POIException {
/* 109 */     return read(file, String.valueOf(rid));
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
/*     */   default T read(InputStream in, int rid) throws POIException {
/* 121 */     return read(in, String.valueOf(rid));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\ExcelSaxReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */