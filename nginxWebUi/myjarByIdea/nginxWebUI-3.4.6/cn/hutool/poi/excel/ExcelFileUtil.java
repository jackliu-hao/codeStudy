package cn.hutool.poi.excel;

import cn.hutool.core.io.IORuntimeException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.FileMagic;

public class ExcelFileUtil {
   public static boolean isXls(InputStream in) {
      return FileMagic.OLE2 == getFileMagic(in);
   }

   public static boolean isXlsx(InputStream in) {
      return FileMagic.OOXML == getFileMagic(in);
   }

   public static boolean isXlsx(File file) {
      try {
         return FileMagic.valueOf(file) == FileMagic.OOXML;
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   private static FileMagic getFileMagic(InputStream in) {
      in = FileMagic.prepareToCheckMagic(in);

      try {
         FileMagic magic = FileMagic.valueOf(in);
         return magic;
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }
}
