package cn.hutool.poi.excel.sax;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.exceptions.POIException;
import java.io.File;
import java.io.InputStream;

public interface ExcelSaxReader<T> {
   String RID_PREFIX = "rId";
   String SHEET_NAME_PREFIX = "sheetName:";

   T read(File var1, String var2) throws POIException;

   T read(InputStream var1, String var2) throws POIException;

   default T read(String path) throws POIException {
      return this.read(FileUtil.file(path));
   }

   default T read(File file) throws POIException {
      return this.read((File)file, -1);
   }

   default T read(InputStream in) throws POIException {
      return this.read((InputStream)in, -1);
   }

   default T read(String path, int idOrRidOrSheetName) throws POIException {
      return this.read(FileUtil.file(path), idOrRidOrSheetName);
   }

   default T read(String path, String idOrRidOrSheetName) throws POIException {
      return this.read(FileUtil.file(path), idOrRidOrSheetName);
   }

   default T read(File file, int rid) throws POIException {
      return this.read(file, String.valueOf(rid));
   }

   default T read(InputStream in, int rid) throws POIException {
      return this.read(in, String.valueOf(rid));
   }
}
