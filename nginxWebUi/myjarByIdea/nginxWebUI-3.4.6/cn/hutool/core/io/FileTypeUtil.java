package cn.hutool.core.io;

import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class FileTypeUtil {
   private static final Map<String, String> FILE_TYPE_MAP = new ConcurrentSkipListMap((s1, s2) -> {
      int len1 = s1.length();
      int len2 = s2.length();
      return len1 == len2 ? s1.compareTo(s2) : len2 - len1;
   });

   public static String putFileType(String fileStreamHexHead, String extName) {
      return (String)FILE_TYPE_MAP.put(fileStreamHexHead, extName);
   }

   public static String removeFileType(String fileStreamHexHead) {
      return (String)FILE_TYPE_MAP.remove(fileStreamHexHead);
   }

   public static String getType(String fileStreamHexHead) {
      Iterator var1 = FILE_TYPE_MAP.entrySet().iterator();

      Map.Entry fileTypeEntry;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         fileTypeEntry = (Map.Entry)var1.next();
      } while(!StrUtil.startWithIgnoreCase(fileStreamHexHead, (CharSequence)fileTypeEntry.getKey()));

      return (String)fileTypeEntry.getValue();
   }

   public static String getType(InputStream in) throws IORuntimeException {
      return getType(IoUtil.readHex28Upper(in));
   }

   public static String getType(InputStream in, String filename) {
      String typeName = getType(in);
      if (null == typeName) {
         typeName = FileUtil.extName(filename);
      } else {
         String extName;
         if ("xls".equals(typeName)) {
            extName = FileUtil.extName(filename);
            if ("doc".equalsIgnoreCase(extName)) {
               typeName = "doc";
            } else if ("msi".equalsIgnoreCase(extName)) {
               typeName = "msi";
            }
         } else if ("zip".equals(typeName)) {
            extName = FileUtil.extName(filename);
            if ("docx".equalsIgnoreCase(extName)) {
               typeName = "docx";
            } else if ("xlsx".equalsIgnoreCase(extName)) {
               typeName = "xlsx";
            } else if ("pptx".equalsIgnoreCase(extName)) {
               typeName = "pptx";
            } else if ("jar".equalsIgnoreCase(extName)) {
               typeName = "jar";
            } else if ("war".equalsIgnoreCase(extName)) {
               typeName = "war";
            } else if ("ofd".equalsIgnoreCase(extName)) {
               typeName = "ofd";
            }
         } else if ("jar".equals(typeName)) {
            extName = FileUtil.extName(filename);
            if ("xlsx".equalsIgnoreCase(extName)) {
               typeName = "xlsx";
            } else if ("docx".equalsIgnoreCase(extName)) {
               typeName = "docx";
            } else if ("pptx".equalsIgnoreCase(extName)) {
               typeName = "pptx";
            }
         }
      }

      return typeName;
   }

   public static String getType(File file) throws IORuntimeException {
      FileInputStream in = null;

      String var2;
      try {
         in = IoUtil.toStream(file);
         var2 = getType(in, file.getName());
      } finally {
         IoUtil.close(in);
      }

      return var2;
   }

   public static String getTypeByPath(String path) throws IORuntimeException {
      return getType(FileUtil.file(path));
   }

   static {
      FILE_TYPE_MAP.put("ffd8ff", "jpg");
      FILE_TYPE_MAP.put("89504e47", "png");
      FILE_TYPE_MAP.put("4749463837", "gif");
      FILE_TYPE_MAP.put("4749463839", "gif");
      FILE_TYPE_MAP.put("49492a00227105008037", "tif");
      FILE_TYPE_MAP.put("424d228c010000000000", "bmp");
      FILE_TYPE_MAP.put("424d8240090000000000", "bmp");
      FILE_TYPE_MAP.put("424d8e1b030000000000", "bmp");
      FILE_TYPE_MAP.put("41433130313500000000", "dwg");
      FILE_TYPE_MAP.put("7b5c727466315c616e73", "rtf");
      FILE_TYPE_MAP.put("38425053000100000000", "psd");
      FILE_TYPE_MAP.put("46726f6d3a203d3f6762", "eml");
      FILE_TYPE_MAP.put("5374616E64617264204A", "mdb");
      FILE_TYPE_MAP.put("252150532D41646F6265", "ps");
      FILE_TYPE_MAP.put("255044462d312e", "pdf");
      FILE_TYPE_MAP.put("2e524d46000000120001", "rmvb");
      FILE_TYPE_MAP.put("464c5601050000000900", "flv");
      FILE_TYPE_MAP.put("0000001C66747970", "mp4");
      FILE_TYPE_MAP.put("00000020667479706", "mp4");
      FILE_TYPE_MAP.put("00000018667479706D70", "mp4");
      FILE_TYPE_MAP.put("49443303000000002176", "mp3");
      FILE_TYPE_MAP.put("000001ba210001000180", "mpg");
      FILE_TYPE_MAP.put("3026b2758e66cf11a6d9", "wmv");
      FILE_TYPE_MAP.put("52494646e27807005741", "wav");
      FILE_TYPE_MAP.put("52494646d07d60074156", "avi");
      FILE_TYPE_MAP.put("4d546864000000060001", "mid");
      FILE_TYPE_MAP.put("526172211a0700cf9073", "rar");
      FILE_TYPE_MAP.put("235468697320636f6e66", "ini");
      FILE_TYPE_MAP.put("504B03040a0000000000", "jar");
      FILE_TYPE_MAP.put("504B0304140008000800", "jar");
      FILE_TYPE_MAP.put("d0cf11e0a1b11ae10", "xls");
      FILE_TYPE_MAP.put("504B0304", "zip");
      FILE_TYPE_MAP.put("4d5a9000030000000400", "exe");
      FILE_TYPE_MAP.put("3c25402070616765206c", "jsp");
      FILE_TYPE_MAP.put("4d616e69666573742d56", "mf");
      FILE_TYPE_MAP.put("7061636b616765207765", "java");
      FILE_TYPE_MAP.put("406563686f206f66660d", "bat");
      FILE_TYPE_MAP.put("1f8b0800000000000000", "gz");
      FILE_TYPE_MAP.put("cafebabe0000002e0041", "class");
      FILE_TYPE_MAP.put("49545346030000006000", "chm");
      FILE_TYPE_MAP.put("04000000010000001300", "mxp");
      FILE_TYPE_MAP.put("6431303a637265617465", "torrent");
      FILE_TYPE_MAP.put("6D6F6F76", "mov");
      FILE_TYPE_MAP.put("FF575043", "wpd");
      FILE_TYPE_MAP.put("CFAD12FEC5FD746F", "dbx");
      FILE_TYPE_MAP.put("2142444E", "pst");
      FILE_TYPE_MAP.put("AC9EBD8F", "qdf");
      FILE_TYPE_MAP.put("E3828596", "pwl");
      FILE_TYPE_MAP.put("2E7261FD", "ram");
      FILE_TYPE_MAP.put("52494646", "webp");
   }
}
