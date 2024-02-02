package cn.hutool.poi.word;

import java.io.File;

public class WordUtil {
   public static Word07Writer getWriter() {
      return new Word07Writer();
   }

   public static Word07Writer getWriter(File destFile) {
      return new Word07Writer(destFile);
   }
}
