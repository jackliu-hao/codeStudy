/*     */ package cn.hutool.poi.excel;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.poi.exceptions.POIException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.poi.ss.usermodel.Sheet;
/*     */ import org.apache.poi.ss.usermodel.Workbook;
/*     */ import org.apache.poi.ss.usermodel.WorkbookFactory;
/*     */ import org.apache.poi.xssf.streaming.SXSSFWorkbook;
/*     */ import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
/*     */ public class WorkbookUtil
/*     */ {
/*     */   public static Workbook createBook(String excelFilePath) {
/*  36 */     return createBook(excelFilePath, false);
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
/*     */   public static Workbook createBook(String excelFilePath, boolean readOnly) {
/*  48 */     return createBook(FileUtil.file(excelFilePath), null, readOnly);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Workbook createBook(File excelFile) {
/*  58 */     return createBook(excelFile, false);
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
/*     */   public static Workbook createBook(File excelFile, boolean readOnly) {
/*  70 */     return createBook(excelFile, null, readOnly);
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
/*     */ 
/*     */   
/*     */   public static Workbook createBookForWriter(File excelFile) {
/*  87 */     if (null == excelFile) {
/*  88 */       return createBook(true);
/*     */     }
/*     */     
/*  91 */     if (excelFile.exists()) {
/*  92 */       return createBook(FileUtil.getInputStream(excelFile));
/*     */     }
/*     */     
/*  95 */     return createBook(StrUtil.endWithIgnoreCase(excelFile.getName(), ".xlsx"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Workbook createBook(File excelFile, String password) {
/* 106 */     return createBook(excelFile, password, false);
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
/*     */   public static Workbook createBook(File excelFile, String password, boolean readOnly) {
/*     */     try {
/* 120 */       return WorkbookFactory.create(excelFile, password, readOnly);
/* 121 */     } catch (Exception e) {
/* 122 */       throw new POIException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Workbook createBook(InputStream in) {
/* 133 */     return createBook(in, (String)null);
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
/*     */   public static Workbook createBook(InputStream in, String password) {
/*     */     try {
/* 146 */       return WorkbookFactory.create(IoUtil.toMarkSupportStream(in), password);
/* 147 */     } catch (Exception e) {
/* 148 */       throw new POIException(e);
/*     */     } finally {
/* 150 */       IoUtil.close(in);
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
/*     */   public static Workbook createBook(boolean isXlsx) {
/*     */     try {
/* 163 */       return WorkbookFactory.create(isXlsx);
/* 164 */     } catch (IOException e) {
/* 165 */       throw new IORuntimeException(e);
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
/*     */   public static SXSSFWorkbook createSXSSFBook(String excelFilePath) {
/* 177 */     return createSXSSFBook(excelFilePath, false);
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
/*     */   public static SXSSFWorkbook createSXSSFBook(String excelFilePath, boolean readOnly) {
/* 189 */     return createSXSSFBook(FileUtil.file(excelFilePath), (String)null, readOnly);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SXSSFWorkbook createSXSSFBook(File excelFile) {
/* 200 */     return createSXSSFBook(excelFile, false);
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
/*     */   public static SXSSFWorkbook createSXSSFBook(File excelFile, boolean readOnly) {
/* 212 */     return createSXSSFBook(excelFile, (String)null, readOnly);
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
/*     */   public static SXSSFWorkbook createSXSSFBook(File excelFile, String password) {
/* 225 */     return createSXSSFBook(excelFile, password, false);
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
/*     */   public static SXSSFWorkbook createSXSSFBook(File excelFile, String password, boolean readOnly) {
/* 239 */     return toSXSSFBook(createBook(excelFile, password, readOnly));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SXSSFWorkbook createSXSSFBook(InputStream in) {
/* 250 */     return createSXSSFBook(in, (String)null);
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
/*     */   public static SXSSFWorkbook createSXSSFBook(InputStream in, String password) {
/* 262 */     return toSXSSFBook(createBook(in, password));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SXSSFWorkbook createSXSSFBook() {
/* 272 */     return new SXSSFWorkbook();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SXSSFWorkbook createSXSSFBook(int rowAccessWindowSize) {
/* 283 */     return new SXSSFWorkbook(rowAccessWindowSize);
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
/*     */   public static SXSSFWorkbook createSXSSFBook(int rowAccessWindowSize, boolean compressTmpFiles, boolean useSharedStringsTable) {
/* 296 */     return new SXSSFWorkbook(null, rowAccessWindowSize, compressTmpFiles, useSharedStringsTable);
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
/*     */   public static void writeBook(Workbook book, OutputStream out) throws IORuntimeException {
/*     */     try {
/* 309 */       book.write(out);
/* 310 */     } catch (IOException e) {
/* 311 */       throw new IORuntimeException(e);
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
/*     */   public static Sheet getOrCreateSheet(Workbook book, String sheetName) {
/* 325 */     if (null == book) {
/* 326 */       return null;
/*     */     }
/* 328 */     sheetName = StrUtil.isBlank(sheetName) ? "sheet1" : sheetName;
/* 329 */     Sheet sheet = book.getSheet(sheetName);
/* 330 */     if (null == sheet) {
/* 331 */       sheet = book.createSheet(sheetName);
/*     */     }
/* 333 */     return sheet;
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
/*     */   public static Sheet getOrCreateSheet(Workbook book, int sheetIndex) {
/* 347 */     Sheet sheet = null;
/*     */     try {
/* 349 */       sheet = book.getSheetAt(sheetIndex);
/* 350 */     } catch (IllegalArgumentException illegalArgumentException) {}
/*     */ 
/*     */     
/* 353 */     if (null == sheet) {
/* 354 */       sheet = book.createSheet();
/*     */     }
/* 356 */     return sheet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isEmpty(Sheet sheet) {
/* 367 */     return (null == sheet || (sheet.getLastRowNum() == 0 && sheet.getPhysicalNumberOfRows() == 0));
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
/*     */   private static SXSSFWorkbook toSXSSFBook(Workbook book) {
/* 380 */     if (book instanceof SXSSFWorkbook) {
/* 381 */       return (SXSSFWorkbook)book;
/*     */     }
/* 383 */     if (book instanceof XSSFWorkbook) {
/* 384 */       return new SXSSFWorkbook((XSSFWorkbook)book);
/*     */     }
/* 386 */     throw new POIException("The input is not a [xlsx] format.");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\WorkbookUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */