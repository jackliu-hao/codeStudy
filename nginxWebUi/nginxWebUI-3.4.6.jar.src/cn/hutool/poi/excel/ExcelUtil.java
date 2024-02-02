/*     */ package cn.hutool.poi.excel;
/*     */ 
/*     */ import cn.hutool.core.exceptions.DependencyException;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.poi.excel.cell.CellLocation;
/*     */ import cn.hutool.poi.excel.sax.ExcelSaxReader;
/*     */ import cn.hutool.poi.excel.sax.ExcelSaxUtil;
/*     */ import cn.hutool.poi.excel.sax.handler.RowHandler;
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
/*     */ public class ExcelUtil
/*     */ {
/*     */   public static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";
/*     */   public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
/*     */   
/*     */   public static void readBySax(String path, int rid, RowHandler rowHandler) {
/*  46 */     readBySax(FileUtil.file(path), rid, rowHandler);
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
/*     */   public static void readBySax(String path, String idOrRid, RowHandler rowHandler) {
/*  58 */     readBySax(FileUtil.file(path), idOrRid, rowHandler);
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
/*     */   public static void readBySax(File file, int rid, RowHandler rowHandler) {
/*  70 */     ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(file), rowHandler);
/*  71 */     reader.read(file, rid);
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
/*     */   public static void readBySax(File file, String idOrRidOrSheetName, RowHandler rowHandler) {
/*  83 */     ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(file), rowHandler);
/*  84 */     reader.read(file, idOrRidOrSheetName);
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
/*     */   public static void readBySax(InputStream in, int rid, RowHandler rowHandler) {
/*  96 */     in = IoUtil.toMarkSupportStream(in);
/*  97 */     ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(in), rowHandler);
/*  98 */     reader.read(in, rid);
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
/*     */   public static void readBySax(InputStream in, String idOrRidOrSheetName, RowHandler rowHandler) {
/* 110 */     in = IoUtil.toMarkSupportStream(in);
/* 111 */     ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(in), rowHandler);
/* 112 */     reader.read(in, idOrRidOrSheetName);
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
/*     */ 
/*     */   
/*     */   public static ExcelReader getReader(String bookFilePath) {
/* 127 */     return getReader(bookFilePath, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExcelReader getReader(File bookFile) {
/* 138 */     return getReader(bookFile, 0);
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
/*     */   public static ExcelReader getReader(String bookFilePath, int sheetIndex) {
/*     */     try {
/* 151 */       return new ExcelReader(bookFilePath, sheetIndex);
/* 152 */     } catch (NoClassDefFoundError e) {
/* 153 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
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
/*     */   public static ExcelReader getReader(String bookFilePath, String sheetName) {
/*     */     try {
/* 167 */       return new ExcelReader(bookFilePath, sheetName);
/* 168 */     } catch (NoClassDefFoundError e) {
/* 169 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExcelReader getReader(File bookFile, int sheetIndex) {
/*     */     try {
/* 182 */       return new ExcelReader(bookFile, sheetIndex);
/* 183 */     } catch (NoClassDefFoundError e) {
/* 184 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExcelReader getReader(File bookFile, String sheetName) {
/*     */     try {
/* 197 */       return new ExcelReader(bookFile, sheetName);
/* 198 */     } catch (NoClassDefFoundError e) {
/* 199 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExcelReader getReader(InputStream bookStream) {
/* 211 */     return getReader(bookStream, 0);
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
/*     */   public static ExcelReader getReader(InputStream bookStream, int sheetIndex) {
/*     */     try {
/* 224 */       return new ExcelReader(bookStream, sheetIndex);
/* 225 */     } catch (NoClassDefFoundError e) {
/* 226 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
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
/*     */   public static ExcelReader getReader(InputStream bookStream, String sheetName) {
/*     */     try {
/* 240 */       return new ExcelReader(bookStream, sheetName);
/* 241 */     } catch (NoClassDefFoundError e) {
/* 242 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
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
/*     */   
/*     */   public static ExcelWriter getWriter() {
/*     */     try {
/* 258 */       return new ExcelWriter();
/* 259 */     } catch (NoClassDefFoundError e) {
/* 260 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
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
/*     */   public static ExcelWriter getWriter(boolean isXlsx) {
/*     */     try {
/* 275 */       return new ExcelWriter(isXlsx);
/* 276 */     } catch (NoClassDefFoundError e) {
/* 277 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExcelWriter getWriter(String destFilePath) {
/*     */     try {
/* 289 */       return new ExcelWriter(destFilePath);
/* 290 */     } catch (NoClassDefFoundError e) {
/* 291 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExcelWriter getWriterWithSheet(String sheetName) {
/*     */     try {
/* 304 */       return new ExcelWriter((File)null, sheetName);
/* 305 */     } catch (NoClassDefFoundError e) {
/* 306 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExcelWriter getWriter(File destFile) {
/*     */     try {
/* 318 */       return new ExcelWriter(destFile);
/* 319 */     } catch (NoClassDefFoundError e) {
/* 320 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExcelWriter getWriter(String destFilePath, String sheetName) {
/*     */     try {
/* 333 */       return new ExcelWriter(destFilePath, sheetName);
/* 334 */     } catch (NoClassDefFoundError e) {
/* 335 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExcelWriter getWriter(File destFile, String sheetName) {
/*     */     try {
/* 348 */       return new ExcelWriter(destFile, sheetName);
/* 349 */     } catch (NoClassDefFoundError e) {
/* 350 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
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
/*     */   
/*     */   public static BigExcelWriter getBigWriter() {
/*     */     try {
/* 366 */       return new BigExcelWriter();
/* 367 */     } catch (NoClassDefFoundError e) {
/* 368 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
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
/*     */   public static BigExcelWriter getBigWriter(int rowAccessWindowSize) {
/*     */     try {
/* 383 */       return new BigExcelWriter(rowAccessWindowSize);
/* 384 */     } catch (NoClassDefFoundError e) {
/* 385 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigExcelWriter getBigWriter(String destFilePath) {
/*     */     try {
/* 397 */       return new BigExcelWriter(destFilePath);
/* 398 */     } catch (NoClassDefFoundError e) {
/* 399 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigExcelWriter getBigWriter(File destFile) {
/*     */     try {
/* 411 */       return new BigExcelWriter(destFile);
/* 412 */     } catch (NoClassDefFoundError e) {
/* 413 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigExcelWriter getBigWriter(String destFilePath, String sheetName) {
/*     */     try {
/* 426 */       return new BigExcelWriter(destFilePath, sheetName);
/* 427 */     } catch (NoClassDefFoundError e) {
/* 428 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigExcelWriter getBigWriter(File destFile, String sheetName) {
/*     */     try {
/* 441 */       return new BigExcelWriter(destFile, sheetName);
/* 442 */     } catch (NoClassDefFoundError e) {
/* 443 */       throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(e.getCause(), e), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String indexToColName(int index) {
/* 455 */     if (index < 0) {
/* 456 */       return null;
/*     */     }
/* 458 */     StringBuilder colName = StrUtil.builder();
/*     */     while (true) {
/* 460 */       if (colName.length() > 0) {
/* 461 */         index--;
/*     */       }
/* 463 */       int remainder = index % 26;
/* 464 */       colName.append((char)(remainder + 65));
/* 465 */       index = (index - remainder) / 26;
/* 466 */       if (index <= 0) {
/* 467 */         return colName.reverse().toString();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int colNameToIndex(String colName) {
/* 478 */     int length = colName.length();
/*     */     
/* 480 */     int index = -1;
/* 481 */     for (int i = 0; i < length; i++) {
/* 482 */       char c = Character.toUpperCase(colName.charAt(i));
/* 483 */       if (Character.isDigit(c)) {
/*     */         break;
/*     */       }
/* 486 */       index = (index + 1) * 26 + c - 65;
/*     */     } 
/* 488 */     return index;
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
/*     */   public static CellLocation toLocation(String locationRef) {
/* 500 */     int x = colNameToIndex(locationRef);
/* 501 */     int y = ReUtil.getFirstNumber(locationRef).intValue() - 1;
/* 502 */     return new CellLocation(x, y);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\ExcelUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */