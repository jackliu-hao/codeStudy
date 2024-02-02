package cn.hutool.poi.excel.sax;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;

public class Excel07SaxReader implements ExcelSaxReader<Excel07SaxReader> {
   private final SheetDataSaxHandler handler;

   public Excel07SaxReader(RowHandler rowHandler) {
      this.handler = new SheetDataSaxHandler(rowHandler);
   }

   public Excel07SaxReader setRowHandler(RowHandler rowHandler) {
      this.handler.setRowHandler(rowHandler);
      return this;
   }

   public Excel07SaxReader read(File file, int rid) throws POIException {
      return this.read(file, "rId" + rid);
   }

   public Excel07SaxReader read(File file, String idOrRidOrSheetName) throws POIException {
      try {
         OPCPackage open = OPCPackage.open(file, PackageAccess.READ);
         Throwable var4 = null;

         Excel07SaxReader var5;
         try {
            var5 = this.read(open, idOrRidOrSheetName);
         } catch (Throwable var15) {
            var4 = var15;
            throw var15;
         } finally {
            if (open != null) {
               if (var4 != null) {
                  try {
                     open.close();
                  } catch (Throwable var14) {
                     var4.addSuppressed(var14);
                  }
               } else {
                  open.close();
               }
            }

         }

         return var5;
      } catch (IOException | InvalidFormatException var17) {
         throw new POIException(var17);
      }
   }

   public Excel07SaxReader read(InputStream in, int rid) throws POIException {
      return this.read(in, "rId" + rid);
   }

   public Excel07SaxReader read(InputStream in, String idOrRidOrSheetName) throws POIException {
      try {
         OPCPackage opcPackage = OPCPackage.open(in);
         Throwable var4 = null;

         Excel07SaxReader var5;
         try {
            var5 = this.read(opcPackage, idOrRidOrSheetName);
         } catch (Throwable var16) {
            var4 = var16;
            throw var16;
         } finally {
            if (opcPackage != null) {
               if (var4 != null) {
                  try {
                     opcPackage.close();
                  } catch (Throwable var15) {
                     var4.addSuppressed(var15);
                  }
               } else {
                  opcPackage.close();
               }
            }

         }

         return var5;
      } catch (IOException var18) {
         throw new IORuntimeException(var18);
      } catch (InvalidFormatException var19) {
         throw new POIException(var19);
      }
   }

   public Excel07SaxReader read(OPCPackage opcPackage, int rid) throws POIException {
      return this.read(opcPackage, "rId" + rid);
   }

   public Excel07SaxReader read(OPCPackage opcPackage, String idOrRidOrSheetName) throws POIException {
      try {
         return this.read(new XSSFReader(opcPackage), idOrRidOrSheetName);
      } catch (OpenXML4JException var4) {
         throw new POIException(var4);
      } catch (IOException var5) {
         throw new IORuntimeException(var5);
      }
   }

   public Excel07SaxReader read(XSSFReader xssfReader, String idOrRidOrSheetName) throws POIException {
      try {
         this.handler.stylesTable = xssfReader.getStylesTable();
      } catch (InvalidFormatException | IOException var4) {
      }

      this.handler.sharedStrings = (SharedStrings)ReflectUtil.invoke(xssfReader, (String)"getSharedStringsTable");
      return this.readSheets(xssfReader, idOrRidOrSheetName);
   }

   private Excel07SaxReader readSheets(XSSFReader xssfReader, String idOrRidOrSheetName) throws POIException {
      this.handler.sheetIndex = this.getSheetIndex(xssfReader, idOrRidOrSheetName);
      InputStream sheetInputStream = null;

      try {
         if (this.handler.sheetIndex > -1) {
            sheetInputStream = xssfReader.getSheet("rId" + (this.handler.sheetIndex + 1));
            ExcelSaxUtil.readFrom(sheetInputStream, this.handler);
            this.handler.rowHandler.doAfterAllAnalysed();
         } else {
            this.handler.sheetIndex = -1;
            Iterator<InputStream> sheetInputStreams = xssfReader.getSheetsData();

            while(sheetInputStreams.hasNext()) {
               this.handler.index = 0;
               ++this.handler.sheetIndex;
               sheetInputStream = (InputStream)sheetInputStreams.next();
               ExcelSaxUtil.readFrom(sheetInputStream, this.handler);
               this.handler.rowHandler.doAfterAllAnalysed();
            }
         }
      } catch (RuntimeException var9) {
         throw var9;
      } catch (Exception var10) {
         throw new POIException(var10);
      } finally {
         IoUtil.close(sheetInputStream);
      }

      return this;
   }

   private int getSheetIndex(XSSFReader xssfReader, String idOrRidOrSheetName) {
      if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, "rId")) {
         return Integer.parseInt(StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, "rId"));
      } else {
         SheetRidReader ridReader = SheetRidReader.parse(xssfReader);
         Integer rid;
         if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, "sheetName:")) {
            idOrRidOrSheetName = StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, "sheetName:");
            rid = ridReader.getRidByNameBase0(idOrRidOrSheetName);
            if (null != rid) {
               return rid;
            }
         } else {
            rid = ridReader.getRidByNameBase0(idOrRidOrSheetName);
            if (null != rid) {
               return rid;
            }

            try {
               int sheetIndex = Integer.parseInt(idOrRidOrSheetName);
               rid = ridReader.getRidBySheetIdBase0(sheetIndex);
               return (Integer)ObjectUtil.defaultIfNull(rid, (Object)sheetIndex);
            } catch (NumberFormatException var6) {
            }
         }

         throw new IllegalArgumentException("Invalid rId or id or sheetName: " + idOrRidOrSheetName);
      }
   }
}
