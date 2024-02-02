/*    */ package org.noear.solon.web.servlet;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.servlet.MultipartConfigElement;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.Part;
/*    */ import org.noear.solon.boot.ServerProps;
/*    */ import org.noear.solon.core.handle.UploadedFile;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class MultipartUtil
/*    */ {
/*    */   public static void buildParamsAndFiles(SolonServletContext context) throws IOException, ServletException {
/* 21 */     HttpServletRequest request = (HttpServletRequest)context.request();
/*    */     
/* 23 */     long maxBodySize = (ServerProps.request_maxBodySize == 0) ? -1L : ServerProps.request_maxBodySize;
/* 24 */     long maxFileSize = (ServerProps.request_maxFileSize == 0) ? -1L : ServerProps.request_maxFileSize;
/*    */ 
/*    */     
/* 27 */     MultipartConfigElement configElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"), maxFileSize, maxBodySize, 0);
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 32 */     request.setAttribute("org.eclipse.jetty.multipartConfig", configElement);
/*    */     
/* 34 */     for (Part part : request.getParts()) {
/* 35 */       if (isFile(part)) {
/* 36 */         doBuildFiles(context, part);
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   private static void doBuildFiles(SolonServletContext context, Part part) throws IOException {
/* 42 */     List<UploadedFile> list = context._fileMap.get(part.getName());
/* 43 */     if (list == null) {
/* 44 */       list = new ArrayList<>();
/* 45 */       context._fileMap.put(part.getName(), list);
/*    */     } 
/*    */     
/* 48 */     UploadedFile f1 = new UploadedFile();
/* 49 */     f1.contentType = part.getContentType();
/* 50 */     f1.contentSize = part.getSize();
/* 51 */     f1.content = part.getInputStream();
/*    */     
/* 53 */     f1.name = part.getSubmittedFileName();
/* 54 */     int idx = f1.name.lastIndexOf(".");
/* 55 */     if (idx > 0) {
/* 56 */       f1.extension = f1.name.substring(idx + 1);
/*    */     }
/*    */     
/* 59 */     list.add(f1);
/*    */   }
/*    */   
/*    */   private static boolean isField(Part filePart) {
/* 63 */     return (filePart.getSubmittedFileName() == null);
/*    */   }
/*    */   
/*    */   private static boolean isFile(Part filePart) {
/* 67 */     return !isField(filePart);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\servlet\MultipartUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */