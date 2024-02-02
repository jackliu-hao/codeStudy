/*     */ package cn.hutool.core.net.multipart;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.StrUtil;
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
/*     */ public class UploadFileHeader
/*     */ {
/*     */   String formFieldName;
/*     */   String formFileName;
/*     */   String path;
/*     */   String fileName;
/*     */   boolean isFile;
/*     */   String contentType;
/*     */   String mimeType;
/*     */   String mimeSubtype;
/*     */   String contentDisposition;
/*     */   
/*     */   UploadFileHeader(String dataHeader) {
/*  28 */     processHeaderString(dataHeader);
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
/*     */   public boolean isFile() {
/*  40 */     return this.isFile;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFormFieldName() {
/*  49 */     return this.formFieldName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFormFileName() {
/*  58 */     return this.formFileName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFileName() {
/*  67 */     return this.fileName;
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
/*     */   public String getContentType() {
/*  79 */     return this.contentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMimeType() {
/*  88 */     return this.mimeType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMimeSubtype() {
/*  97 */     return this.mimeSubtype;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getContentDisposition() {
/* 106 */     return this.contentDisposition;
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
/*     */   private String getDataFieldValue(String dataHeader, String fieldName) {
/* 119 */     String value = null;
/* 120 */     String token = StrUtil.format("{}=\"", new Object[] { fieldName });
/* 121 */     int pos = dataHeader.indexOf(token);
/* 122 */     if (pos > 0) {
/* 123 */       int start = pos + token.length();
/* 124 */       int end = dataHeader.indexOf('"', start);
/* 125 */       if (start > 0 && end > 0) {
/* 126 */         value = dataHeader.substring(start, end);
/*     */       }
/*     */     } 
/* 129 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getContentType(String dataHeader) {
/* 139 */     String token = "Content-Type:";
/* 140 */     int start = dataHeader.indexOf(token);
/* 141 */     if (start == -1) {
/* 142 */       return "";
/*     */     }
/* 144 */     start += token.length();
/* 145 */     return dataHeader.substring(start);
/*     */   }
/*     */   
/*     */   private String getContentDisposition(String dataHeader) {
/* 149 */     int start = dataHeader.indexOf(':') + 1;
/* 150 */     int end = dataHeader.indexOf(';');
/* 151 */     return dataHeader.substring(start, end);
/*     */   }
/*     */   
/*     */   private String getMimeType(String ContentType) {
/* 155 */     int pos = ContentType.indexOf('/');
/* 156 */     if (pos == -1) {
/* 157 */       return ContentType;
/*     */     }
/* 159 */     return ContentType.substring(1, pos);
/*     */   }
/*     */   
/*     */   private String getMimeSubtype(String ContentType) {
/* 163 */     int start = ContentType.indexOf('/');
/* 164 */     if (start == -1) {
/* 165 */       return ContentType;
/*     */     }
/* 167 */     start++;
/* 168 */     return ContentType.substring(start);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void processHeaderString(String dataHeader) {
/* 177 */     this.isFile = (dataHeader.indexOf("filename") > 0);
/* 178 */     this.formFieldName = getDataFieldValue(dataHeader, "name");
/* 179 */     if (this.isFile) {
/* 180 */       this.formFileName = getDataFieldValue(dataHeader, "filename");
/* 181 */       if (this.formFileName == null) {
/*     */         return;
/*     */       }
/* 184 */       if (this.formFileName.length() == 0) {
/* 185 */         this.path = "";
/* 186 */         this.fileName = "";
/*     */       } 
/* 188 */       int ls = FileUtil.lastIndexOfSeparator(this.formFileName);
/* 189 */       if (ls == -1) {
/* 190 */         this.path = "";
/* 191 */         this.fileName = this.formFileName;
/*     */       } else {
/* 193 */         this.path = this.formFileName.substring(0, ls);
/* 194 */         this.fileName = this.formFileName.substring(ls);
/*     */       } 
/* 196 */       if (this.fileName.length() > 0) {
/* 197 */         this.contentType = getContentType(dataHeader);
/* 198 */         this.mimeType = getMimeType(this.contentType);
/* 199 */         this.mimeSubtype = getMimeSubtype(this.contentType);
/* 200 */         this.contentDisposition = getContentDisposition(dataHeader);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\multipart\UploadFileHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */