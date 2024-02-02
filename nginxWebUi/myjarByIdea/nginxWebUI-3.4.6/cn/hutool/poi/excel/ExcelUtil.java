package cn.hutool.poi.excel;

import cn.hutool.core.exceptions.DependencyException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.cell.CellLocation;
import cn.hutool.poi.excel.sax.ExcelSaxReader;
import cn.hutool.poi.excel.sax.ExcelSaxUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import java.io.File;
import java.io.InputStream;

public class ExcelUtil {
   public static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";
   public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

   public static void readBySax(String path, int rid, RowHandler rowHandler) {
      readBySax(FileUtil.file(path), rid, rowHandler);
   }

   public static void readBySax(String path, String idOrRid, RowHandler rowHandler) {
      readBySax(FileUtil.file(path), idOrRid, rowHandler);
   }

   public static void readBySax(File file, int rid, RowHandler rowHandler) {
      ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(file), rowHandler);
      reader.read(file, rid);
   }

   public static void readBySax(File file, String idOrRidOrSheetName, RowHandler rowHandler) {
      ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(file), rowHandler);
      reader.read(file, idOrRidOrSheetName);
   }

   public static void readBySax(InputStream in, int rid, RowHandler rowHandler) {
      in = IoUtil.toMarkSupportStream(in);
      ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(in), rowHandler);
      reader.read(in, rid);
   }

   public static void readBySax(InputStream in, String idOrRidOrSheetName, RowHandler rowHandler) {
      in = IoUtil.toMarkSupportStream(in);
      ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(in), rowHandler);
      reader.read(in, idOrRidOrSheetName);
   }

   public static ExcelReader getReader(String bookFilePath) {
      return getReader((String)bookFilePath, 0);
   }

   public static ExcelReader getReader(File bookFile) {
      return getReader((File)bookFile, 0);
   }

   public static ExcelReader getReader(String bookFilePath, int sheetIndex) {
      try {
         return new ExcelReader(bookFilePath, sheetIndex);
      } catch (NoClassDefFoundError var3) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var3.getCause(), (Object)var3), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelReader getReader(String bookFilePath, String sheetName) {
      try {
         return new ExcelReader(bookFilePath, sheetName);
      } catch (NoClassDefFoundError var3) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var3.getCause(), (Object)var3), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelReader getReader(File bookFile, int sheetIndex) {
      try {
         return new ExcelReader(bookFile, sheetIndex);
      } catch (NoClassDefFoundError var3) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var3.getCause(), (Object)var3), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelReader getReader(File bookFile, String sheetName) {
      try {
         return new ExcelReader(bookFile, sheetName);
      } catch (NoClassDefFoundError var3) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var3.getCause(), (Object)var3), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelReader getReader(InputStream bookStream) {
      return getReader((InputStream)bookStream, 0);
   }

   public static ExcelReader getReader(InputStream bookStream, int sheetIndex) {
      try {
         return new ExcelReader(bookStream, sheetIndex);
      } catch (NoClassDefFoundError var3) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var3.getCause(), (Object)var3), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelReader getReader(InputStream bookStream, String sheetName) {
      try {
         return new ExcelReader(bookStream, sheetName);
      } catch (NoClassDefFoundError var3) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var3.getCause(), (Object)var3), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelWriter getWriter() {
      try {
         return new ExcelWriter();
      } catch (NoClassDefFoundError var1) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var1.getCause(), (Object)var1), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelWriter getWriter(boolean isXlsx) {
      try {
         return new ExcelWriter(isXlsx);
      } catch (NoClassDefFoundError var2) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var2.getCause(), (Object)var2), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelWriter getWriter(String destFilePath) {
      try {
         return new ExcelWriter(destFilePath);
      } catch (NoClassDefFoundError var2) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var2.getCause(), (Object)var2), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelWriter getWriterWithSheet(String sheetName) {
      try {
         return new ExcelWriter((File)null, sheetName);
      } catch (NoClassDefFoundError var2) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var2.getCause(), (Object)var2), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelWriter getWriter(File destFile) {
      try {
         return new ExcelWriter(destFile);
      } catch (NoClassDefFoundError var2) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var2.getCause(), (Object)var2), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelWriter getWriter(String destFilePath, String sheetName) {
      try {
         return new ExcelWriter(destFilePath, sheetName);
      } catch (NoClassDefFoundError var3) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var3.getCause(), (Object)var3), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static ExcelWriter getWriter(File destFile, String sheetName) {
      try {
         return new ExcelWriter(destFile, sheetName);
      } catch (NoClassDefFoundError var3) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var3.getCause(), (Object)var3), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static BigExcelWriter getBigWriter() {
      try {
         return new BigExcelWriter();
      } catch (NoClassDefFoundError var1) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var1.getCause(), (Object)var1), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static BigExcelWriter getBigWriter(int rowAccessWindowSize) {
      try {
         return new BigExcelWriter(rowAccessWindowSize);
      } catch (NoClassDefFoundError var2) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var2.getCause(), (Object)var2), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static BigExcelWriter getBigWriter(String destFilePath) {
      try {
         return new BigExcelWriter(destFilePath);
      } catch (NoClassDefFoundError var2) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var2.getCause(), (Object)var2), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static BigExcelWriter getBigWriter(File destFile) {
      try {
         return new BigExcelWriter(destFile);
      } catch (NoClassDefFoundError var2) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var2.getCause(), (Object)var2), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static BigExcelWriter getBigWriter(String destFilePath, String sheetName) {
      try {
         return new BigExcelWriter(destFilePath, sheetName);
      } catch (NoClassDefFoundError var3) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var3.getCause(), (Object)var3), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static BigExcelWriter getBigWriter(File destFile, String sheetName) {
      try {
         return new BigExcelWriter(destFile, sheetName);
      } catch (NoClassDefFoundError var3) {
         throw new DependencyException((Throwable)ObjectUtil.defaultIfNull(var3.getCause(), (Object)var3), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }

   public static String indexToColName(int index) {
      if (index < 0) {
         return null;
      } else {
         StringBuilder colName = StrUtil.builder();

         do {
            if (colName.length() > 0) {
               --index;
            }

            int remainder = index % 26;
            colName.append((char)(remainder + 65));
            index = (index - remainder) / 26;
         } while(index > 0);

         return colName.reverse().toString();
      }
   }

   public static int colNameToIndex(String colName) {
      int length = colName.length();
      int index = -1;

      for(int i = 0; i < length; ++i) {
         char c = Character.toUpperCase(colName.charAt(i));
         if (Character.isDigit(c)) {
            break;
         }

         index = (index + 1) * 26 + c - 65;
      }

      return index;
   }

   public static CellLocation toLocation(String locationRef) {
      int x = colNameToIndex(locationRef);
      int y = ReUtil.getFirstNumber(locationRef) - 1;
      return new CellLocation(x, y);
   }
}
