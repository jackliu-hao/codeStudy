/*     */ package com.sun.jna.platform.win32.COM.tlb;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.TypeLibUtil;
/*     */ import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
/*     */ import com.sun.jna.platform.win32.COM.tlb.imp.TlbCmdlineArgs;
/*     */ import com.sun.jna.platform.win32.COM.tlb.imp.TlbCoClass;
/*     */ import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
/*     */ import com.sun.jna.platform.win32.COM.tlb.imp.TlbDispInterface;
/*     */ import com.sun.jna.platform.win32.COM.tlb.imp.TlbEnum;
/*     */ import com.sun.jna.platform.win32.COM.tlb.imp.TlbInterface;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
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
/*     */ public class TlbImp
/*     */   implements TlbConst
/*     */ {
/*     */   private TypeLibUtil typeLibUtil;
/*     */   private File comRootDir;
/*     */   private File outputDir;
/*     */   private TlbCmdlineArgs cmdlineArgs;
/*     */   
/*     */   public static void main(String[] args) {
/*  67 */     new TlbImp(args);
/*     */   }
/*     */   
/*     */   public TlbImp(String[] args) {
/*  71 */     this.cmdlineArgs = new TlbCmdlineArgs(args);
/*     */     
/*  73 */     if (this.cmdlineArgs.isTlbId()) {
/*  74 */       String clsid = this.cmdlineArgs.getRequiredParam("tlb.id");
/*     */       
/*  76 */       int majorVersion = this.cmdlineArgs.getIntParam("tlb.major.version");
/*     */       
/*  78 */       int minorVersion = this.cmdlineArgs.getIntParam("tlb.minor.version");
/*     */ 
/*     */ 
/*     */       
/*  82 */       this.typeLibUtil = new TypeLibUtil(clsid, majorVersion, minorVersion);
/*     */       
/*  84 */       startCOM2Java();
/*  85 */     } else if (this.cmdlineArgs.isTlbFile()) {
/*  86 */       String file = this.cmdlineArgs.getRequiredParam("tlb.file");
/*     */ 
/*     */       
/*  89 */       this.typeLibUtil = new TypeLibUtil(file);
/*  90 */       startCOM2Java();
/*     */     } else {
/*  92 */       this.cmdlineArgs.showCmdHelp();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void startCOM2Java() {
/*     */     try {
/* 101 */       createDir();
/*     */       
/* 103 */       String bindingMode = this.cmdlineArgs.getBindingMode();
/*     */       
/* 105 */       int typeInfoCount = this.typeLibUtil.getTypeInfoCount();
/* 106 */       for (int i = 0; i < typeInfoCount; i++) {
/* 107 */         OaIdl.TYPEKIND typekind = this.typeLibUtil.getTypeInfoType(i);
/*     */         
/* 109 */         if (typekind.value == 0) {
/* 110 */           createCOMEnum(i, getPackageName(), this.typeLibUtil);
/* 111 */         } else if (typekind.value == 1) {
/* 112 */           logInfo("'TKIND_RECORD' objects are currently not supported!");
/* 113 */         } else if (typekind.value == 2) {
/* 114 */           logInfo("'TKIND_MODULE' objects are currently not supported!");
/* 115 */         } else if (typekind.value == 3) {
/* 116 */           createCOMInterface(i, getPackageName(), this.typeLibUtil);
/*     */         }
/* 118 */         else if (typekind.value == 4) {
/* 119 */           createCOMDispInterface(i, getPackageName(), this.typeLibUtil);
/*     */         }
/* 121 */         else if (typekind.value == 5) {
/* 122 */           createCOMCoClass(i, getPackageName(), this.typeLibUtil, bindingMode);
/*     */         }
/* 124 */         else if (typekind.value == 6) {
/* 125 */           logInfo("'TKIND_ALIAS' objects are currently not supported!");
/* 126 */         } else if (typekind.value == 7) {
/* 127 */           logInfo("'TKIND_UNION' objects are currently not supported!");
/*     */         } 
/*     */       } 
/*     */       
/* 131 */       logInfo(typeInfoCount + " files sucessfully written to: " + this.comRootDir
/* 132 */           .toString());
/* 133 */     } catch (Exception e) {
/* 134 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void createDir() throws FileNotFoundException {
/* 139 */     String _outputDir = this.cmdlineArgs.getParam("output.dir");
/*     */     
/* 141 */     String path = "_jnaCOM_" + System.currentTimeMillis() + "\\myPackage\\" + this.typeLibUtil.getName().toLowerCase() + "\\";
/*     */     
/* 143 */     if (_outputDir != null) {
/* 144 */       this.comRootDir = new File(_outputDir + "\\" + path);
/*     */     } else {
/* 146 */       String tmp = System.getProperty("java.io.tmpdir");
/* 147 */       this.comRootDir = new File(tmp + "\\" + path);
/*     */     } 
/*     */     
/* 150 */     if (this.comRootDir.exists()) {
/* 151 */       this.comRootDir.delete();
/*     */     }
/* 153 */     if (this.comRootDir.mkdirs()) {
/* 154 */       logInfo("Output directory sucessfully created.");
/*     */     } else {
/* 156 */       throw new FileNotFoundException("Output directory NOT sucessfully created to: " + this.comRootDir
/*     */           
/* 158 */           .toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private String getPackageName() {
/* 163 */     return "myPackage." + this.typeLibUtil.getName().toLowerCase();
/*     */   }
/*     */   
/*     */   private void writeTextFile(String filename, String str) throws IOException {
/* 167 */     String file = this.comRootDir + File.separator + filename;
/* 168 */     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
/*     */     
/* 170 */     bos.write(str.getBytes());
/* 171 */     bos.close();
/*     */   }
/*     */   
/*     */   private void writeTlbClass(TlbBase tlbBase) throws IOException {
/* 175 */     StringBuffer classBuffer = tlbBase.getClassBuffer();
/* 176 */     writeTextFile(tlbBase.getFilename(), classBuffer.toString());
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
/*     */   private void createCOMEnum(int index, String packagename, TypeLibUtil typeLibUtil) throws IOException {
/* 190 */     TlbEnum tlbEnum = new TlbEnum(index, packagename, typeLibUtil);
/* 191 */     writeTlbClass((TlbBase)tlbEnum);
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
/*     */   private void createCOMInterface(int index, String packagename, TypeLibUtil typeLibUtil) throws IOException {
/* 205 */     TlbInterface tlbInterface = new TlbInterface(index, packagename, typeLibUtil);
/*     */     
/* 207 */     writeTlbClass((TlbBase)tlbInterface);
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
/*     */   private void createCOMDispInterface(int index, String packagename, TypeLibUtil typeLibUtil) throws IOException {
/* 221 */     TlbDispInterface tlbDispatch = new TlbDispInterface(index, packagename, typeLibUtil);
/*     */     
/* 223 */     writeTlbClass((TlbBase)tlbDispatch);
/*     */   }
/*     */ 
/*     */   
/*     */   private void createCOMCoClass(int index, String packagename, TypeLibUtil typeLibUtil, String bindingMode) throws IOException {
/* 228 */     TlbCoClass tlbCoClass = new TlbCoClass(index, getPackageName(), typeLibUtil, bindingMode);
/*     */     
/* 230 */     writeTlbClass((TlbBase)tlbCoClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void logInfo(String msg) {
/* 240 */     System.out.println(msg);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\TlbImp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */