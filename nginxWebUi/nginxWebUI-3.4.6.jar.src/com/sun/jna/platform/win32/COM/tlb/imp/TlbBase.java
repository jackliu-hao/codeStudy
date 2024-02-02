/*     */ package com.sun.jna.platform.win32.COM.tlb.imp;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.TypeInfoUtil;
/*     */ import com.sun.jna.platform.win32.COM.TypeLibUtil;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public abstract class TlbBase
/*     */ {
/*     */   public static final String CR = "\n";
/*     */   public static final String CRCR = "\n\n";
/*     */   public static final String TAB = "\t";
/*     */   public static final String TABTAB = "\t\t";
/*     */   protected TypeLibUtil typeLibUtil;
/*     */   protected TypeInfoUtil typeInfoUtil;
/*     */   protected int index;
/*     */   protected StringBuffer templateBuffer;
/*     */   protected StringBuffer classBuffer;
/*  73 */   protected String content = "";
/*     */   
/*  75 */   protected String filename = "DefaultFilename";
/*     */   
/*  77 */   protected String name = "DefaultName";
/*     */ 
/*     */   
/*  80 */   public static String[] IUNKNOWN_METHODS = new String[] { "QueryInterface", "AddRef", "Release" };
/*     */ 
/*     */ 
/*     */   
/*  84 */   public static String[] IDISPATCH_METHODS = new String[] { "GetTypeInfoCount", "GetTypeInfo", "GetIDsOfNames", "Invoke" };
/*     */ 
/*     */   
/*  87 */   protected String bindingMode = "dispid";
/*     */   
/*     */   public TlbBase(int index, TypeLibUtil typeLibUtil, TypeInfoUtil typeInfoUtil) {
/*  90 */     this(index, typeLibUtil, typeInfoUtil, "dispid");
/*     */   }
/*     */   
/*     */   public TlbBase(int index, TypeLibUtil typeLibUtil, TypeInfoUtil typeInfoUtil, String bindingMode) {
/*  94 */     this.index = index;
/*  95 */     this.typeLibUtil = typeLibUtil;
/*  96 */     this.typeInfoUtil = typeInfoUtil;
/*  97 */     this.bindingMode = bindingMode;
/*     */     
/*  99 */     String filename = getClassTemplate();
/*     */     try {
/* 101 */       readTemplateFile(filename);
/* 102 */       this.classBuffer = this.templateBuffer;
/* 103 */     } catch (IOException e) {
/* 104 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logError(String msg) {
/* 115 */     log("ERROR", msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logInfo(String msg) {
/* 125 */     log("INFO", msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuffer getClassBuffer() {
/* 134 */     return this.classBuffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void createContent(String content) {
/* 144 */     replaceVariable("content", content);
/*     */   }
/*     */   
/*     */   public void setFilename(String filename) {
/* 148 */     if (!filename.endsWith("java"))
/* 149 */       filename = filename + ".java"; 
/* 150 */     this.filename = filename;
/*     */   }
/*     */   
/*     */   public String getFilename() {
/* 154 */     return this.filename;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 158 */     return this.name;
/*     */   }
/*     */   
/*     */   public void setName(String name) {
/* 162 */     this.name = name;
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
/*     */   protected void log(String level, String msg) {
/* 174 */     String _msg = level + " " + getTime() + " : " + msg;
/* 175 */     System.out.println(_msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getTime() {
/* 184 */     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
/* 185 */     return sdf.format(new Date());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract String getClassTemplate();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void readTemplateFile(String filename) throws IOException {
/* 204 */     this.templateBuffer = new StringBuffer();
/* 205 */     BufferedReader reader = null;
/*     */     
/*     */     try {
/* 208 */       InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
/* 209 */       reader = new BufferedReader(new InputStreamReader(is));
/* 210 */       String line = null;
/* 211 */       while ((line = reader.readLine()) != null)
/* 212 */         this.templateBuffer.append(line + "\n"); 
/*     */     } finally {
/* 214 */       if (reader != null) {
/* 215 */         reader.close();
/*     */       }
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
/*     */   protected void replaceVariable(String name, String value) {
/* 228 */     if (value == null) {
/* 229 */       value = "";
/*     */     }
/* 231 */     Pattern pattern = Pattern.compile("\\$\\{" + name + "\\}");
/* 232 */     Matcher matcher = pattern.matcher(this.classBuffer);
/* 233 */     String replacement = value;
/* 234 */     String result = "";
/*     */     
/* 236 */     while (matcher.find()) {
/* 237 */       result = matcher.replaceAll(replacement);
/*     */     }
/*     */     
/* 240 */     if (result.length() > 0)
/* 241 */       this.classBuffer = new StringBuffer(result); 
/*     */   }
/*     */   
/*     */   protected void createPackageName(String packagename) {
/* 245 */     replaceVariable("packagename", packagename);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void createClassName(String name) {
/* 255 */     replaceVariable("classname", name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isReservedMethod(String method) {
/*     */     int i;
/* 266 */     for (i = 0; i < IUNKNOWN_METHODS.length; i++) {
/* 267 */       if (IUNKNOWN_METHODS[i].equalsIgnoreCase(method)) {
/* 268 */         return true;
/*     */       }
/*     */     } 
/* 271 */     for (i = 0; i < IDISPATCH_METHODS.length; i++) {
/* 272 */       if (IDISPATCH_METHODS[i].equalsIgnoreCase(method)) {
/* 273 */         return true;
/*     */       }
/*     */     } 
/* 276 */     return false;
/*     */   }
/*     */   
/*     */   protected boolean isVTableMode() {
/* 280 */     if (this.bindingMode.equalsIgnoreCase("vtable")) {
/* 281 */       return true;
/*     */     }
/* 283 */     return false;
/*     */   }
/*     */   
/*     */   protected boolean isDispIdMode() {
/* 287 */     if (this.bindingMode.equalsIgnoreCase("dispid")) {
/* 288 */       return true;
/*     */     }
/* 290 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */