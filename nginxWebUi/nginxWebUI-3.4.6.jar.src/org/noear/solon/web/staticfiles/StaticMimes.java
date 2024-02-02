/*     */ package org.noear.solon.web.staticfiles;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StaticMimes
/*     */ {
/*  15 */   static final Map<String, String> mimeMap = new HashMap<>();
/*     */   
/*     */   static {
/*  18 */     mimeMap.put(".abs", "audio/x-mpeg");
/*  19 */     mimeMap.put(".ai", "application/postscript");
/*  20 */     mimeMap.put(".aif", "audio/x-aiff");
/*  21 */     mimeMap.put(".aifc", "audio/x-aiff");
/*  22 */     mimeMap.put(".aiff", "audio/x-aiff");
/*  23 */     mimeMap.put(".aim", "application/x-aim");
/*  24 */     mimeMap.put(".art", "image/x-jg");
/*  25 */     mimeMap.put(".asf", "video/x-ms-asf");
/*  26 */     mimeMap.put(".asx", "video/x-ms-asf");
/*  27 */     mimeMap.put(".au", "audio/basic");
/*  28 */     mimeMap.put(".avi", "video/x-msvideo");
/*  29 */     mimeMap.put(".avx", "video/x-rad-screenplay");
/*  30 */     mimeMap.put(".bcpio", "application/x-bcpio");
/*  31 */     mimeMap.put(".bin", "application/octet-stream");
/*  32 */     mimeMap.put(".bmp", "image/bmp");
/*  33 */     mimeMap.put(".body", "text/html");
/*  34 */     mimeMap.put(".cdf", "application/x-cdf");
/*  35 */     mimeMap.put(".cer", "application/pkix-cert");
/*  36 */     mimeMap.put(".class", "application/java");
/*  37 */     mimeMap.put(".cpio", "application/x-cpio");
/*  38 */     mimeMap.put(".csh", "application/x-csh");
/*  39 */     mimeMap.put(".css", "text/css");
/*  40 */     mimeMap.put(".dib", "image/bmp");
/*  41 */     mimeMap.put(".doc", "application/msword");
/*  42 */     mimeMap.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
/*  43 */     mimeMap.put(".dtd", "application/xml-dtd");
/*  44 */     mimeMap.put(".dv", "video/x-dv");
/*  45 */     mimeMap.put(".dvi", "application/x-dvi");
/*  46 */     mimeMap.put(".eot", "application/vnd.ms-fontobject");
/*  47 */     mimeMap.put(".eps", "application/postscript");
/*  48 */     mimeMap.put(".etx", "text/x-setext");
/*  49 */     mimeMap.put(".exe", "application/octet-stream");
/*  50 */     mimeMap.put(".gif", "image/gif");
/*  51 */     mimeMap.put(".gtar", "application/x-gtar");
/*  52 */     mimeMap.put(".gz", "application/x-gzip");
/*  53 */     mimeMap.put(".hdf", "application/x-hdf");
/*  54 */     mimeMap.put(".hqx", "application/mac-binhex40");
/*  55 */     mimeMap.put(".htc", "text/x-component");
/*  56 */     mimeMap.put(".htm", "text/html");
/*  57 */     mimeMap.put(".html", "text/html");
/*  58 */     mimeMap.put(".ief", "image/ief");
/*  59 */     mimeMap.put(".ico", "image/x-icon");
/*  60 */     mimeMap.put(".jad", "text/vnd.sun.j2me.app-descriptor");
/*  61 */     mimeMap.put(".jar", "application/java-archive");
/*  62 */     mimeMap.put(".java", "text/x-java-source");
/*  63 */     mimeMap.put(".jnlp", "application/x-java-jnlp-file");
/*  64 */     mimeMap.put(".jpe", "image/jpeg");
/*  65 */     mimeMap.put(".jpeg", "image/jpeg");
/*  66 */     mimeMap.put(".jpg", "image/jpeg");
/*  67 */     mimeMap.put(".js", "application/javascript");
/*  68 */     mimeMap.put(".jsf", "text/plain");
/*  69 */     mimeMap.put(".json", "application/json");
/*  70 */     mimeMap.put(".jspf", "text/plain");
/*  71 */     mimeMap.put(".kar", "audio/midi");
/*  72 */     mimeMap.put(".latex", "application/x-latex");
/*  73 */     mimeMap.put(".m3u", "audio/x-mpegurl");
/*  74 */     mimeMap.put(".mac", "image/x-macpaint");
/*  75 */     mimeMap.put(".man", "text/troff");
/*  76 */     mimeMap.put(".mathml", "application/mathml+xml");
/*  77 */     mimeMap.put(".me", "text/troff");
/*  78 */     mimeMap.put(".mid", "audio/midi");
/*  79 */     mimeMap.put(".midi", "audio/midi");
/*  80 */     mimeMap.put(".mif", "application/x-mif");
/*  81 */     mimeMap.put(".mov", "video/quicktime");
/*  82 */     mimeMap.put(".movie", "video/x-sgi-movie");
/*  83 */     mimeMap.put(".mp1", "audio/mpeg");
/*  84 */     mimeMap.put(".mp2", "audio/mpeg");
/*  85 */     mimeMap.put(".mp3", "audio/mpeg");
/*  86 */     mimeMap.put(".mp4", "video/mp4");
/*  87 */     mimeMap.put(".mpa", "audio/mpeg");
/*  88 */     mimeMap.put(".mpe", "video/mpeg");
/*  89 */     mimeMap.put(".mpeg", "video/mpeg");
/*  90 */     mimeMap.put(".mpega", "audio/x-mpeg");
/*  91 */     mimeMap.put(".mpg", "video/mpeg");
/*  92 */     mimeMap.put(".mpv2", "video/mpeg2");
/*  93 */     mimeMap.put(".ms", "application/x-wais-source");
/*  94 */     mimeMap.put(".nc", "application/x-netcdf");
/*  95 */     mimeMap.put(".oda", "application/oda");
/*  96 */     mimeMap.put(".odb", "application/vnd.oasis.opendocument.database");
/*  97 */     mimeMap.put(".odc", "application/vnd.oasis.opendocument.chart");
/*  98 */     mimeMap.put(".odf", "application/vnd.oasis.opendocument.formula");
/*  99 */     mimeMap.put(".odg", "application/vnd.oasis.opendocument.graphics");
/* 100 */     mimeMap.put(".odi", "application/vnd.oasis.opendocument.image");
/* 101 */     mimeMap.put(".odm", "application/vnd.oasis.opendocument.text-master");
/* 102 */     mimeMap.put(".odp", "application/vnd.oasis.opendocument.presentation");
/* 103 */     mimeMap.put(".ods", "application/vnd.oasis.opendocument.spreadsheet");
/* 104 */     mimeMap.put(".odt", "application/vnd.oasis.opendocument.text");
/* 105 */     mimeMap.put(".otg", "application/vnd.oasis.opendocument.graphics-template");
/* 106 */     mimeMap.put(".oth", "application/vnd.oasis.opendocument.text-web");
/* 107 */     mimeMap.put(".otp", "application/vnd.oasis.opendocument.presentation-template");
/* 108 */     mimeMap.put(".ots", "application/vnd.oasis.opendocument.spreadsheet-template ");
/* 109 */     mimeMap.put(".ott", "application/vnd.oasis.opendocument.text-template");
/* 110 */     mimeMap.put(".ogx", "application/ogg");
/* 111 */     mimeMap.put(".ogv", "video/ogg");
/* 112 */     mimeMap.put(".oga", "audio/ogg");
/* 113 */     mimeMap.put(".ogg", "audio/ogg");
/* 114 */     mimeMap.put(".otf", "application/x-font-opentype");
/* 115 */     mimeMap.put(".spx", "audio/ogg");
/* 116 */     mimeMap.put(".flac", "audio/flac");
/* 117 */     mimeMap.put(".anx", "application/annodex");
/* 118 */     mimeMap.put(".axa", "audio/annodex");
/* 119 */     mimeMap.put(".axv", "video/annodex");
/* 120 */     mimeMap.put(".xspf", "application/xspf+xml");
/* 121 */     mimeMap.put(".pbm", "image/x-portable-bitmap");
/* 122 */     mimeMap.put(".pct", "image/pict");
/* 123 */     mimeMap.put(".pdf", "application/pdf");
/* 124 */     mimeMap.put(".pgm", "image/x-portable-graymap");
/* 125 */     mimeMap.put(".pic", "image/pict");
/* 126 */     mimeMap.put(".pict", "image/pict");
/* 127 */     mimeMap.put(".pls", "audio/x-scpls");
/* 128 */     mimeMap.put(".png", "image/png");
/* 129 */     mimeMap.put(".pnm", "image/x-portable-anymap");
/* 130 */     mimeMap.put(".pnt", "image/x-macpaint");
/* 131 */     mimeMap.put(".ppm", "image/x-portable-pixmap");
/* 132 */     mimeMap.put(".ppt", "application/vnd.ms-powerpoint");
/* 133 */     mimeMap.put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
/* 134 */     mimeMap.put(".pps", "application/vnd.ms-powerpoint");
/* 135 */     mimeMap.put(".ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
/* 136 */     mimeMap.put(".ps", "application/postscript");
/* 137 */     mimeMap.put(".psd", "image/vnd.adobe.photoshop");
/* 138 */     mimeMap.put(".qt", "video/quicktime");
/* 139 */     mimeMap.put(".qti", "image/x-quicktime");
/* 140 */     mimeMap.put(".qtif", "image/x-quicktime");
/* 141 */     mimeMap.put(".ras", "image/x-cmu-raster");
/* 142 */     mimeMap.put(".rar", "application/octet-stream");
/* 143 */     mimeMap.put(".rdf", "application/rdf+xml");
/* 144 */     mimeMap.put(".rgb", "image/x-rgb");
/* 145 */     mimeMap.put(".rm", "application/vnd.rn-realmedia");
/* 146 */     mimeMap.put(".rmi", "audio/mid");
/* 147 */     mimeMap.put(".roff", "text/troff");
/* 148 */     mimeMap.put(".rtf", "application/rtf");
/* 149 */     mimeMap.put(".rtx", "text/richtext");
/* 150 */     mimeMap.put(".sfnt", "application/font-sfnt");
/* 151 */     mimeMap.put(".sh", "application/x-sh");
/* 152 */     mimeMap.put(".shar", "application/x-shar");
/* 153 */     mimeMap.put(".sit", "application/x-stuffit");
/* 154 */     mimeMap.put(".snd", "audio/basic");
/* 155 */     mimeMap.put(".src", "application/x-wais-source");
/* 156 */     mimeMap.put(".sv4cpio", "application/x-sv4cpio");
/* 157 */     mimeMap.put(".sv4crc", "application/x-sv4crc");
/* 158 */     mimeMap.put(".svg", "image/svg+xml");
/* 159 */     mimeMap.put(".svgz", "image/svg+xml");
/* 160 */     mimeMap.put(".swf", "application/x-shockwave-flash");
/* 161 */     mimeMap.put(".t", "text/troff");
/* 162 */     mimeMap.put(".tar", "application/x-tar");
/* 163 */     mimeMap.put(".tcl", "application/x-tcl");
/* 164 */     mimeMap.put(".tex", "application/x-tex");
/* 165 */     mimeMap.put(".texi", "application/x-texinfo");
/* 166 */     mimeMap.put(".texinfo", "application/x-texinfo");
/* 167 */     mimeMap.put(".tgz", "application/x-compressed");
/* 168 */     mimeMap.put(".tif", "image/tiff");
/* 169 */     mimeMap.put(".tiff", "image/tiff");
/* 170 */     mimeMap.put(".tr", "text/troff");
/* 171 */     mimeMap.put(".tsv", "text/tab-separated-values");
/* 172 */     mimeMap.put(".ttf", "application/x-font-ttf");
/* 173 */     mimeMap.put(".txt", "text/plain");
/* 174 */     mimeMap.put(".ulw", "audio/basic");
/* 175 */     mimeMap.put(".ustar", "application/x-ustar");
/* 176 */     mimeMap.put(".vxml", "application/voicexml+xml");
/* 177 */     mimeMap.put(".xbm", "image/x-xbitmap");
/* 178 */     mimeMap.put(".xht", "application/xhtml+xml");
/* 179 */     mimeMap.put(".xhtml", "application/xhtml+xml");
/* 180 */     mimeMap.put(".xls", "application/vnd.ms-excel");
/* 181 */     mimeMap.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
/* 182 */     mimeMap.put(".xml", "application/xml");
/* 183 */     mimeMap.put(".xpm", "image/x-xpixmap");
/* 184 */     mimeMap.put(".xsl", "application/xml");
/* 185 */     mimeMap.put(".xslt", "application/xslt+xml");
/* 186 */     mimeMap.put(".xul", "application/vnd.mozilla.xul+xml");
/* 187 */     mimeMap.put(".xwd", "image/x-xwindowdump");
/* 188 */     mimeMap.put(".vsd", "application/vnd.visio");
/* 189 */     mimeMap.put(".wav", "audio/x-wav");
/* 190 */     mimeMap.put(".wbmp", "image/vnd.wap.wbmp");
/* 191 */     mimeMap.put(".wml", "text/vnd.wap.wml");
/* 192 */     mimeMap.put(".wmlc", "application/vnd.wap.wmlc");
/* 193 */     mimeMap.put(".wmls", "text/vnd.wap.wmlsc");
/* 194 */     mimeMap.put(".wmlscriptc", "application/vnd.wap.wmlscriptc");
/* 195 */     mimeMap.put(".wma", "audio/x-ms-wma");
/* 196 */     mimeMap.put(".wmv", "video/x-ms-wmv");
/* 197 */     mimeMap.put(".woff", "application/font-woff");
/* 198 */     mimeMap.put(".woff2", "application/font-woff2");
/* 199 */     mimeMap.put(".wrl", "model/vrml");
/* 200 */     mimeMap.put(".wspolicy", "application/wspolicy+xml");
/* 201 */     mimeMap.put(".z", "application/x-compress");
/* 202 */     mimeMap.put(".zip", "application/zip");
/* 203 */     mimeMap.put(".7z", "application/x-7z-compressed");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized String add(String extension, String conentType) {
/* 210 */     return mimeMap.put(extension, conentType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized String findByExt(String ext) {
/* 217 */     return mimeMap.get(ext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized String findByFileName(String fileName) {
/* 224 */     String ext = resolveExt(fileName);
/*     */     
/* 226 */     return findByExt(ext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, String> getMap() {
/* 233 */     return Collections.unmodifiableMap(mimeMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String resolveExt(String fileName) {
/* 240 */     String ext = "";
/* 241 */     int pos = fileName.lastIndexOf('#');
/* 242 */     if (pos > 0) {
/* 243 */       fileName = fileName.substring(0, pos - 1);
/*     */     }
/*     */     
/* 246 */     pos = fileName.lastIndexOf('.');
/* 247 */     pos = Math.max(pos, fileName.lastIndexOf('/'));
/* 248 */     pos = Math.max(pos, fileName.lastIndexOf('?'));
/* 249 */     if (pos != -1 && fileName.charAt(pos) == '.') {
/* 250 */       ext = fileName.substring(pos).toLowerCase();
/*     */     }
/*     */     
/* 253 */     return ext;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\staticfiles\StaticMimes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */