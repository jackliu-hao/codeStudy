/*     */ package cn.hutool.core.net.multipart;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.map.multi.ListValueMap;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultipartFormData
/*     */ {
/*  23 */   private final ListValueMap<String, String> requestParameters = new ListValueMap();
/*     */   
/*  25 */   private final ListValueMap<String, UploadFile> requestFiles = new ListValueMap();
/*     */ 
/*     */ 
/*     */   
/*     */   private final UploadSetting setting;
/*     */ 
/*     */   
/*     */   private boolean loaded;
/*     */ 
/*     */ 
/*     */   
/*     */   public MultipartFormData() {
/*  37 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultipartFormData(UploadSetting uploadSetting) {
/*  46 */     this.setting = (uploadSetting == null) ? new UploadSetting() : uploadSetting;
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
/*     */   public void parseRequestStream(InputStream inputStream, Charset charset) throws IOException {
/*  58 */     setLoaded();
/*     */     
/*  60 */     MultipartRequestInputStream input = new MultipartRequestInputStream(inputStream);
/*  61 */     input.readBoundary();
/*     */     while (true) {
/*  63 */       UploadFileHeader header = input.readDataHeader(charset);
/*  64 */       if (header == null) {
/*     */         break;
/*     */       }
/*     */       
/*  68 */       if (header.isFile == true) {
/*     */         
/*  70 */         String fileName = header.fileName;
/*  71 */         if (fileName.length() > 0 && header.contentType.contains("application/x-macbinary")) {
/*  72 */           input.skipBytes(128L);
/*     */         }
/*  74 */         UploadFile newFile = new UploadFile(header, this.setting);
/*  75 */         if (newFile.processStream(input)) {
/*  76 */           putFile(header.formFieldName, newFile);
/*     */         }
/*     */       } else {
/*     */         
/*  80 */         putParameter(header.formFieldName, input.readString(charset));
/*     */       } 
/*     */       
/*  83 */       input.skipBytes(1L);
/*  84 */       input.mark(1);
/*     */ 
/*     */       
/*  87 */       int nextByte = input.read();
/*  88 */       if (nextByte == -1 || nextByte == 45) {
/*  89 */         input.reset();
/*     */         break;
/*     */       } 
/*  92 */       input.reset();
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
/*     */   public String getParam(String paramName) {
/* 104 */     List<String> values = getListParam(paramName);
/* 105 */     if (CollUtil.isNotEmpty(values)) {
/* 106 */       return values.get(0);
/*     */     }
/* 108 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getParamNames() {
/* 115 */     return this.requestParameters.keySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getArrayParam(String paramName) {
/* 125 */     List<String> listParam = getListParam(paramName);
/* 126 */     if (null != listParam) {
/* 127 */       return listParam.<String>toArray(new String[0]);
/*     */     }
/* 129 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getListParam(String paramName) {
/* 140 */     return (List<String>)this.requestParameters.get(paramName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String[]> getParamMap() {
/* 149 */     return Convert.toMap(String.class, String[].class, getParamListMap());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListValueMap<String, String> getParamListMap() {
/* 158 */     return this.requestParameters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UploadFile getFile(String paramName) {
/* 169 */     UploadFile[] values = getFiles(paramName);
/* 170 */     if (values != null && values.length > 0) {
/* 171 */       return values[0];
/*     */     }
/* 173 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UploadFile[] getFiles(String paramName) {
/* 184 */     List<UploadFile> fileList = getFileList(paramName);
/* 185 */     if (null != fileList) {
/* 186 */       return fileList.<UploadFile>toArray(new UploadFile[0]);
/*     */     }
/* 188 */     return null;
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
/*     */   public List<UploadFile> getFileList(String paramName) {
/* 200 */     return (List<UploadFile>)this.requestFiles.get(paramName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getFileParamNames() {
/* 209 */     return this.requestFiles.keySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, UploadFile[]> getFileMap() {
/* 218 */     return Convert.toMap(String.class, UploadFile[].class, getFileListValueMap());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListValueMap<String, UploadFile> getFileListValueMap() {
/* 227 */     return this.requestFiles;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLoaded() {
/* 237 */     return this.loaded;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void putFile(String name, UploadFile uploadFile) {
/* 248 */     this.requestFiles.putValue(name, uploadFile);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void putParameter(String name, String value) {
/* 258 */     this.requestParameters.putValue(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setLoaded() throws IOException {
/* 267 */     if (this.loaded == true) {
/* 268 */       throw new IOException("Multi-part request already parsed.");
/*     */     }
/* 270 */     this.loaded = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\multipart\MultipartFormData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */