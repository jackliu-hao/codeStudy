package cn.hutool.poi.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.exceptions.POIException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WorkbookUtil {
   public static Workbook createBook(String excelFilePath) {
      return createBook(excelFilePath, false);
   }

   public static Workbook createBook(String excelFilePath, boolean readOnly) {
      return createBook(FileUtil.file(excelFilePath), (String)null, readOnly);
   }

   public static Workbook createBook(File excelFile) {
      return createBook(excelFile, false);
   }

   public static Workbook createBook(File excelFile, boolean readOnly) {
      return createBook(excelFile, (String)null, readOnly);
   }

   public static Workbook createBookForWriter(File excelFile) {
      if (null == excelFile) {
         return createBook(true);
      } else {
         return excelFile.exists() ? createBook((InputStream)FileUtil.getInputStream(excelFile)) : createBook(StrUtil.endWithIgnoreCase(excelFile.getName(), ".xlsx"));
      }
   }

   public static Workbook createBook(File excelFile, String password) {
      return createBook(excelFile, password, false);
   }

   public static Workbook createBook(File excelFile, String password, boolean readOnly) {
      try {
         return WorkbookFactory.create(excelFile, password, readOnly);
      } catch (Exception var4) {
         throw new POIException(var4);
      }
   }

   public static Workbook createBook(InputStream in) {
      return createBook((InputStream)in, (String)null);
   }

   public static Workbook createBook(InputStream in, String password) {
      Workbook var2;
      try {
         var2 = WorkbookFactory.create(IoUtil.toMarkSupportStream(in), password);
      } catch (Exception var6) {
         throw new POIException(var6);
      } finally {
         IoUtil.close(in);
      }

      return var2;
   }

   public static Workbook createBook(boolean isXlsx) {
      try {
         return WorkbookFactory.create(isXlsx);
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public static SXSSFWorkbook createSXSSFBook(String excelFilePath) {
      return createSXSSFBook(excelFilePath, false);
   }

   public static SXSSFWorkbook createSXSSFBook(String excelFilePath, boolean readOnly) {
      return createSXSSFBook(FileUtil.file(excelFilePath), (String)null, readOnly);
   }

   public static SXSSFWorkbook createSXSSFBook(File excelFile) {
      return createSXSSFBook(excelFile, false);
   }

   public static SXSSFWorkbook createSXSSFBook(File excelFile, boolean readOnly) {
      return createSXSSFBook(excelFile, (String)null, readOnly);
   }

   public static SXSSFWorkbook createSXSSFBook(File excelFile, String password) {
      return createSXSSFBook(excelFile, password, false);
   }

   public static SXSSFWorkbook createSXSSFBook(File excelFile, String password, boolean readOnly) {
      return toSXSSFBook(createBook(excelFile, password, readOnly));
   }

   public static SXSSFWorkbook createSXSSFBook(InputStream in) {
      return createSXSSFBook((InputStream)in, (String)null);
   }

   public static SXSSFWorkbook createSXSSFBook(InputStream in, String password) {
      return toSXSSFBook(createBook(in, password));
   }

   public static SXSSFWorkbook createSXSSFBook() {
      return new SXSSFWorkbook();
   }

   public static SXSSFWorkbook createSXSSFBook(int rowAccessWindowSize) {
      return new SXSSFWorkbook(rowAccessWindowSize);
   }

   public static SXSSFWorkbook createSXSSFBook(int rowAccessWindowSize, boolean compressTmpFiles, boolean useSharedStringsTable) {
      return new SXSSFWorkbook((XSSFWorkbook)null, rowAccessWindowSize, compressTmpFiles, useSharedStringsTable);
   }

   public static void writeBook(Workbook book, OutputStream out) throws IORuntimeException {
      try {
         book.write(out);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static Sheet getOrCreateSheet(Workbook book, String sheetName) {
      if (null == book) {
         return null;
      } else {
         sheetName = StrUtil.isBlank(sheetName) ? "sheet1" : sheetName;
         Sheet sheet = book.getSheet(sheetName);
         if (null == sheet) {
            sheet = book.createSheet(sheetName);
         }

         return sheet;
      }
   }

   public static Sheet getOrCreateSheet(Workbook book, int sheetIndex) {
      Sheet sheet = null;

      try {
         sheet = book.getSheetAt(sheetIndex);
      } catch (IllegalArgumentException var4) {
      }

      if (null == sheet) {
         sheet = book.createSheet();
      }

      return sheet;
   }

   public static boolean isEmpty(Sheet sheet) {
      return null == sheet || sheet.getLastRowNum() == 0 && sheet.getPhysicalNumberOfRows() == 0;
   }

   private static SXSSFWorkbook toSXSSFBook(Workbook book) {
      if (book instanceof SXSSFWorkbook) {
         return (SXSSFWorkbook)book;
      } else if (book instanceof XSSFWorkbook) {
         return new SXSSFWorkbook((XSSFWorkbook)book);
      } else {
         throw new POIException("The input is not a [xlsx] format.");
      }
   }
}
