/*    */ package org.noear.solon.boot.jlhttp;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.noear.solon.boot.ServerProps;
/*    */ import org.noear.solon.boot.io.LimitedInputStream;
/*    */ import org.noear.solon.core.handle.UploadedFile;
/*    */ 
/*    */ class MultipartUtil
/*    */ {
/*    */   public static void buildParamsAndFiles(JlHttpContext context) throws IOException {
/* 16 */     HTTPServer.Request request = (HTTPServer.Request)context.request();
/* 17 */     HTTPServer.MultipartIterator parts = new HTTPServer.MultipartIterator(request);
/*    */     
/* 19 */     while (parts.hasNext()) {
/* 20 */       HTTPServer.MultipartIterator.Part part = parts.next();
/*    */       
/* 22 */       if (!isFile(part)) {
/* 23 */         context.paramSet(part.name, part.getString()); continue;
/*    */       } 
/* 25 */       doBuildFiles(context, part);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private static void doBuildFiles(JlHttpContext context, HTTPServer.MultipartIterator.Part part) throws IOException {
/* 31 */     List<UploadedFile> list = context._fileMap.get(part.getName());
/* 32 */     if (list == null) {
/* 33 */       list = new ArrayList<>();
/* 34 */       context._fileMap.put(part.getName(), list);
/*    */     } 
/*    */     
/* 37 */     UploadedFile f1 = new UploadedFile();
/* 38 */     f1.contentType = part.getHeaders().get("Content-Type");
/* 39 */     f1.content = read((InputStream)new LimitedInputStream(part.getBody(), ServerProps.request_maxFileSize));
/* 40 */     f1.contentSize = f1.content.available();
/* 41 */     f1.name = part.getFilename();
/* 42 */     int idx = f1.name.lastIndexOf(".");
/* 43 */     if (idx > 0) {
/* 44 */       f1.extension = f1.name.substring(idx + 1);
/*    */     }
/*    */     
/* 47 */     list.add(f1);
/*    */   }
/*    */   
/*    */   private static boolean isField(HTTPServer.MultipartIterator.Part filePart) {
/* 51 */     return (filePart.getFilename() == null);
/*    */   }
/*    */   
/*    */   private static boolean isFile(HTTPServer.MultipartIterator.Part filePart) {
/* 55 */     return !isField(filePart);
/*    */   }
/*    */   
/*    */   private static ByteArrayInputStream read(InputStream input) throws IOException {
/* 59 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/*    */     
/* 61 */     byte[] buffer = new byte[4096];
/* 62 */     int n = 0;
/* 63 */     while (-1 != (n = input.read(buffer))) {
/* 64 */       output.write(buffer, 0, n);
/*    */     }
/* 66 */     return new ByteArrayInputStream(output.toByteArray());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boot\jlhttp\MultipartUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */