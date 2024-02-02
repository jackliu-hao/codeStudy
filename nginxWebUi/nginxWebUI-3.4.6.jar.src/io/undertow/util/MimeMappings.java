/*     */ package io.undertow.util;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MimeMappings
/*     */ {
/*     */   private final Map<String, String> mappings;
/*     */   public static final Map<String, String> DEFAULT_MIME_MAPPINGS;
/*     */   
/*     */   static {
/*  38 */     Map<String, String> defaultMappings = new HashMap<>(101);
/*  39 */     defaultMappings.put("txt", "text/plain");
/*  40 */     defaultMappings.put("css", "text/css");
/*  41 */     defaultMappings.put("html", "text/html");
/*  42 */     defaultMappings.put("htm", "text/html");
/*  43 */     defaultMappings.put("gif", "image/gif");
/*  44 */     defaultMappings.put("jpg", "image/jpeg");
/*  45 */     defaultMappings.put("jpe", "image/jpeg");
/*  46 */     defaultMappings.put("jpeg", "image/jpeg");
/*  47 */     defaultMappings.put("bmp", "image/bmp");
/*  48 */     defaultMappings.put("js", "application/javascript");
/*  49 */     defaultMappings.put("png", "image/png");
/*  50 */     defaultMappings.put("java", "text/plain");
/*  51 */     defaultMappings.put("body", "text/html");
/*  52 */     defaultMappings.put("rtx", "text/richtext");
/*  53 */     defaultMappings.put("tsv", "text/tab-separated-values");
/*  54 */     defaultMappings.put("etx", "text/x-setext");
/*  55 */     defaultMappings.put("json", "application/json");
/*  56 */     defaultMappings.put("class", "application/java");
/*  57 */     defaultMappings.put("csh", "application/x-csh");
/*  58 */     defaultMappings.put("sh", "application/x-sh");
/*  59 */     defaultMappings.put("tcl", "application/x-tcl");
/*  60 */     defaultMappings.put("tex", "application/x-tex");
/*  61 */     defaultMappings.put("texinfo", "application/x-texinfo");
/*  62 */     defaultMappings.put("texi", "application/x-texinfo");
/*  63 */     defaultMappings.put("t", "application/x-troff");
/*  64 */     defaultMappings.put("tr", "application/x-troff");
/*  65 */     defaultMappings.put("roff", "application/x-troff");
/*  66 */     defaultMappings.put("man", "application/x-troff-man");
/*  67 */     defaultMappings.put("me", "application/x-troff-me");
/*  68 */     defaultMappings.put("ms", "application/x-wais-source");
/*  69 */     defaultMappings.put("src", "application/x-wais-source");
/*  70 */     defaultMappings.put("zip", "application/zip");
/*  71 */     defaultMappings.put("bcpio", "application/x-bcpio");
/*  72 */     defaultMappings.put("cpio", "application/x-cpio");
/*  73 */     defaultMappings.put("gtar", "application/x-gtar");
/*  74 */     defaultMappings.put("shar", "application/x-shar");
/*  75 */     defaultMappings.put("sv4cpio", "application/x-sv4cpio");
/*  76 */     defaultMappings.put("sv4crc", "application/x-sv4crc");
/*  77 */     defaultMappings.put("tar", "application/x-tar");
/*  78 */     defaultMappings.put("ustar", "application/x-ustar");
/*  79 */     defaultMappings.put("dvi", "application/x-dvi");
/*  80 */     defaultMappings.put("hdf", "application/x-hdf");
/*  81 */     defaultMappings.put("latex", "application/x-latex");
/*  82 */     defaultMappings.put("bin", "application/octet-stream");
/*  83 */     defaultMappings.put("oda", "application/oda");
/*  84 */     defaultMappings.put("pdf", "application/pdf");
/*  85 */     defaultMappings.put("ps", "application/postscript");
/*  86 */     defaultMappings.put("eps", "application/postscript");
/*  87 */     defaultMappings.put("ai", "application/postscript");
/*  88 */     defaultMappings.put("rtf", "application/rtf");
/*  89 */     defaultMappings.put("nc", "application/x-netcdf");
/*  90 */     defaultMappings.put("cdf", "application/x-netcdf");
/*  91 */     defaultMappings.put("cer", "application/x-x509-ca-cert");
/*  92 */     defaultMappings.put("exe", "application/octet-stream");
/*  93 */     defaultMappings.put("gz", "application/x-gzip");
/*  94 */     defaultMappings.put("Z", "application/x-compress");
/*  95 */     defaultMappings.put("z", "application/x-compress");
/*  96 */     defaultMappings.put("hqx", "application/mac-binhex40");
/*  97 */     defaultMappings.put("mif", "application/x-mif");
/*  98 */     defaultMappings.put("ico", "image/x-icon");
/*  99 */     defaultMappings.put("ief", "image/ief");
/* 100 */     defaultMappings.put("tiff", "image/tiff");
/* 101 */     defaultMappings.put("tif", "image/tiff");
/* 102 */     defaultMappings.put("ras", "image/x-cmu-raster");
/* 103 */     defaultMappings.put("pnm", "image/x-portable-anymap");
/* 104 */     defaultMappings.put("pbm", "image/x-portable-bitmap");
/* 105 */     defaultMappings.put("pgm", "image/x-portable-graymap");
/* 106 */     defaultMappings.put("ppm", "image/x-portable-pixmap");
/* 107 */     defaultMappings.put("rgb", "image/x-rgb");
/* 108 */     defaultMappings.put("xbm", "image/x-xbitmap");
/* 109 */     defaultMappings.put("xpm", "image/x-xpixmap");
/* 110 */     defaultMappings.put("xwd", "image/x-xwindowdump");
/* 111 */     defaultMappings.put("au", "audio/basic");
/* 112 */     defaultMappings.put("snd", "audio/basic");
/* 113 */     defaultMappings.put("aif", "audio/x-aiff");
/* 114 */     defaultMappings.put("aiff", "audio/x-aiff");
/* 115 */     defaultMappings.put("aifc", "audio/x-aiff");
/* 116 */     defaultMappings.put("wav", "audio/x-wav");
/* 117 */     defaultMappings.put("mp3", "audio/mpeg");
/* 118 */     defaultMappings.put("mpeg", "video/mpeg");
/* 119 */     defaultMappings.put("mpg", "video/mpeg");
/* 120 */     defaultMappings.put("mpe", "video/mpeg");
/* 121 */     defaultMappings.put("qt", "video/quicktime");
/* 122 */     defaultMappings.put("mov", "video/quicktime");
/* 123 */     defaultMappings.put("avi", "video/x-msvideo");
/* 124 */     defaultMappings.put("movie", "video/x-sgi-movie");
/* 125 */     defaultMappings.put("avx", "video/x-rad-screenplay");
/* 126 */     defaultMappings.put("wrl", "x-world/x-vrml");
/* 127 */     defaultMappings.put("mpv2", "video/mpeg2");
/* 128 */     defaultMappings.put("jnlp", "application/x-java-jnlp-file");
/*     */     
/* 130 */     defaultMappings.put("eot", "application/vnd.ms-fontobject");
/* 131 */     defaultMappings.put("woff", "application/font-woff");
/* 132 */     defaultMappings.put("woff2", "application/font-woff2");
/* 133 */     defaultMappings.put("ttf", "application/x-font-ttf");
/* 134 */     defaultMappings.put("otf", "application/x-font-opentype");
/* 135 */     defaultMappings.put("sfnt", "application/font-sfnt");
/*     */ 
/*     */ 
/*     */     
/* 139 */     defaultMappings.put("xml", "application/xml");
/* 140 */     defaultMappings.put("xhtml", "application/xhtml+xml");
/* 141 */     defaultMappings.put("xsl", "application/xml");
/* 142 */     defaultMappings.put("svg", "image/svg+xml");
/* 143 */     defaultMappings.put("svgz", "image/svg+xml");
/* 144 */     defaultMappings.put("wbmp", "image/vnd.wap.wbmp");
/* 145 */     defaultMappings.put("wml", "text/vnd.wap.wml");
/* 146 */     defaultMappings.put("wmlc", "application/vnd.wap.wmlc");
/* 147 */     defaultMappings.put("wmls", "text/vnd.wap.wmlscript");
/* 148 */     defaultMappings.put("wmlscriptc", "application/vnd.wap.wmlscriptc");
/*     */     
/* 150 */     DEFAULT_MIME_MAPPINGS = Collections.unmodifiableMap(defaultMappings);
/*     */   }
/*     */   
/* 153 */   public static final MimeMappings DEFAULT = builder().build();
/*     */   
/*     */   MimeMappings(Map<String, String> mappings) {
/* 156 */     this.mappings = mappings;
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/* 160 */     return new Builder(true);
/*     */   }
/*     */   
/*     */   public static Builder builder(boolean includeDefault) {
/* 164 */     return new Builder(includeDefault);
/*     */   }
/*     */   
/*     */   public static class Builder {
/* 168 */     private final Map<String, String> mappings = new HashMap<>();
/*     */ 
/*     */     
/*     */     private Builder(boolean includeDefault) {
/* 172 */       if (includeDefault) {
/* 173 */         this.mappings.putAll(MimeMappings.DEFAULT_MIME_MAPPINGS);
/*     */       }
/*     */     }
/*     */     
/*     */     public Builder addMapping(String extension, String contentType) {
/* 178 */       this.mappings.put(extension.toLowerCase(Locale.ENGLISH), contentType);
/* 179 */       return this;
/*     */     }
/*     */     
/*     */     public MimeMappings build() {
/* 183 */       return new MimeMappings(this.mappings);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getMimeType(String extension) {
/* 188 */     return this.mappings.get(extension.toLowerCase(Locale.ENGLISH));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\MimeMappings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */