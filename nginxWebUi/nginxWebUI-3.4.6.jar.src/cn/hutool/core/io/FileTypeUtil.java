/*     */ package cn.hutool.core.io;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentSkipListMap;
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
/*     */ public class FileTypeUtil
/*     */ {
/*     */   private static final Map<String, String> FILE_TYPE_MAP;
/*     */   
/*     */   static {
/*  26 */     FILE_TYPE_MAP = new ConcurrentSkipListMap<>((s1, s2) -> {
/*     */           int len1 = s1.length();
/*     */ 
/*     */           
/*     */           int len2 = s2.length();
/*     */ 
/*     */           
/*     */           return (len1 == len2) ? s1.compareTo(s2) : (len2 - len1);
/*     */         });
/*     */     
/*  36 */     FILE_TYPE_MAP.put("ffd8ff", "jpg");
/*  37 */     FILE_TYPE_MAP.put("89504e47", "png");
/*  38 */     FILE_TYPE_MAP.put("4749463837", "gif");
/*  39 */     FILE_TYPE_MAP.put("4749463839", "gif");
/*  40 */     FILE_TYPE_MAP.put("49492a00227105008037", "tif");
/*  41 */     FILE_TYPE_MAP.put("424d228c010000000000", "bmp");
/*  42 */     FILE_TYPE_MAP.put("424d8240090000000000", "bmp");
/*  43 */     FILE_TYPE_MAP.put("424d8e1b030000000000", "bmp");
/*  44 */     FILE_TYPE_MAP.put("41433130313500000000", "dwg");
/*  45 */     FILE_TYPE_MAP.put("7b5c727466315c616e73", "rtf");
/*  46 */     FILE_TYPE_MAP.put("38425053000100000000", "psd");
/*  47 */     FILE_TYPE_MAP.put("46726f6d3a203d3f6762", "eml");
/*  48 */     FILE_TYPE_MAP.put("5374616E64617264204A", "mdb");
/*  49 */     FILE_TYPE_MAP.put("252150532D41646F6265", "ps");
/*  50 */     FILE_TYPE_MAP.put("255044462d312e", "pdf");
/*  51 */     FILE_TYPE_MAP.put("2e524d46000000120001", "rmvb");
/*  52 */     FILE_TYPE_MAP.put("464c5601050000000900", "flv");
/*  53 */     FILE_TYPE_MAP.put("0000001C66747970", "mp4");
/*  54 */     FILE_TYPE_MAP.put("00000020667479706", "mp4");
/*  55 */     FILE_TYPE_MAP.put("00000018667479706D70", "mp4");
/*  56 */     FILE_TYPE_MAP.put("49443303000000002176", "mp3");
/*  57 */     FILE_TYPE_MAP.put("000001ba210001000180", "mpg");
/*  58 */     FILE_TYPE_MAP.put("3026b2758e66cf11a6d9", "wmv");
/*  59 */     FILE_TYPE_MAP.put("52494646e27807005741", "wav");
/*  60 */     FILE_TYPE_MAP.put("52494646d07d60074156", "avi");
/*  61 */     FILE_TYPE_MAP.put("4d546864000000060001", "mid");
/*  62 */     FILE_TYPE_MAP.put("526172211a0700cf9073", "rar");
/*  63 */     FILE_TYPE_MAP.put("235468697320636f6e66", "ini");
/*  64 */     FILE_TYPE_MAP.put("504B03040a0000000000", "jar");
/*  65 */     FILE_TYPE_MAP.put("504B0304140008000800", "jar");
/*     */     
/*  67 */     FILE_TYPE_MAP.put("d0cf11e0a1b11ae10", "xls");
/*  68 */     FILE_TYPE_MAP.put("504B0304", "zip");
/*  69 */     FILE_TYPE_MAP.put("4d5a9000030000000400", "exe");
/*  70 */     FILE_TYPE_MAP.put("3c25402070616765206c", "jsp");
/*  71 */     FILE_TYPE_MAP.put("4d616e69666573742d56", "mf");
/*  72 */     FILE_TYPE_MAP.put("7061636b616765207765", "java");
/*  73 */     FILE_TYPE_MAP.put("406563686f206f66660d", "bat");
/*  74 */     FILE_TYPE_MAP.put("1f8b0800000000000000", "gz");
/*  75 */     FILE_TYPE_MAP.put("cafebabe0000002e0041", "class");
/*  76 */     FILE_TYPE_MAP.put("49545346030000006000", "chm");
/*  77 */     FILE_TYPE_MAP.put("04000000010000001300", "mxp");
/*  78 */     FILE_TYPE_MAP.put("6431303a637265617465", "torrent");
/*  79 */     FILE_TYPE_MAP.put("6D6F6F76", "mov");
/*  80 */     FILE_TYPE_MAP.put("FF575043", "wpd");
/*  81 */     FILE_TYPE_MAP.put("CFAD12FEC5FD746F", "dbx");
/*  82 */     FILE_TYPE_MAP.put("2142444E", "pst");
/*  83 */     FILE_TYPE_MAP.put("AC9EBD8F", "qdf");
/*  84 */     FILE_TYPE_MAP.put("E3828596", "pwl");
/*  85 */     FILE_TYPE_MAP.put("2E7261FD", "ram");
/*     */     
/*  87 */     FILE_TYPE_MAP.put("52494646", "webp");
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
/*     */   public static String putFileType(String fileStreamHexHead, String extName) {
/*  99 */     return FILE_TYPE_MAP.put(fileStreamHexHead, extName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String removeFileType(String fileStreamHexHead) {
/* 109 */     return FILE_TYPE_MAP.remove(fileStreamHexHead);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getType(String fileStreamHexHead) {
/* 119 */     for (Map.Entry<String, String> fileTypeEntry : FILE_TYPE_MAP.entrySet()) {
/* 120 */       if (StrUtil.startWithIgnoreCase(fileStreamHexHead, fileTypeEntry.getKey())) {
/* 121 */         return fileTypeEntry.getValue();
/*     */       }
/*     */     } 
/* 124 */     return null;
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
/*     */   public static String getType(InputStream in) throws IORuntimeException {
/* 137 */     return getType(IoUtil.readHex28Upper(in));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getType(InputStream in, String filename) {
/* 158 */     String typeName = getType(in);
/*     */     
/* 160 */     if (null == typeName) {
/*     */       
/* 162 */       typeName = FileUtil.extName(filename);
/* 163 */     } else if ("xls".equals(typeName)) {
/*     */       
/* 165 */       String extName = FileUtil.extName(filename);
/* 166 */       if ("doc".equalsIgnoreCase(extName)) {
/* 167 */         typeName = "doc";
/* 168 */       } else if ("msi".equalsIgnoreCase(extName)) {
/* 169 */         typeName = "msi";
/*     */       } 
/* 171 */     } else if ("zip".equals(typeName)) {
/*     */       
/* 173 */       String extName = FileUtil.extName(filename);
/* 174 */       if ("docx".equalsIgnoreCase(extName)) {
/* 175 */         typeName = "docx";
/* 176 */       } else if ("xlsx".equalsIgnoreCase(extName)) {
/* 177 */         typeName = "xlsx";
/* 178 */       } else if ("pptx".equalsIgnoreCase(extName)) {
/* 179 */         typeName = "pptx";
/* 180 */       } else if ("jar".equalsIgnoreCase(extName)) {
/* 181 */         typeName = "jar";
/* 182 */       } else if ("war".equalsIgnoreCase(extName)) {
/* 183 */         typeName = "war";
/* 184 */       } else if ("ofd".equalsIgnoreCase(extName)) {
/* 185 */         typeName = "ofd";
/*     */       } 
/* 187 */     } else if ("jar".equals(typeName)) {
/*     */       
/* 189 */       String extName = FileUtil.extName(filename);
/* 190 */       if ("xlsx".equalsIgnoreCase(extName)) {
/* 191 */         typeName = "xlsx";
/* 192 */       } else if ("docx".equalsIgnoreCase(extName)) {
/*     */         
/* 194 */         typeName = "docx";
/* 195 */       } else if ("pptx".equalsIgnoreCase(extName)) {
/*     */         
/* 197 */         typeName = "pptx";
/*     */       } 
/*     */     } 
/* 200 */     return typeName;
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
/*     */   public static String getType(File file) throws IORuntimeException {
/* 217 */     FileInputStream in = null;
/*     */     try {
/* 219 */       in = IoUtil.toStream(file);
/* 220 */       return getType(in, file.getName());
/*     */     } finally {
/* 222 */       IoUtil.close(in);
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
/*     */   public static String getTypeByPath(String path) throws IORuntimeException {
/* 234 */     return getType(FileUtil.file(path));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\FileTypeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */