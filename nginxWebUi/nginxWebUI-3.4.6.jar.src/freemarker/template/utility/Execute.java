/*     */ package freemarker.template.utility;
/*     */ 
/*     */ import freemarker.template.TemplateMethodModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.util.List;
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
/*     */ public class Execute
/*     */   implements TemplateMethodModel
/*     */ {
/*     */   private static final int OUTPUT_BUFFER_SIZE = 1024;
/*     */   
/*     */   public Object exec(List<String> arguments) throws TemplateModelException {
/*  75 */     StringBuilder aOutputBuffer = new StringBuilder();
/*     */     
/*  77 */     if (arguments.size() < 1) {
/*  78 */       throw new TemplateModelException("Need an argument to execute");
/*     */     }
/*     */     
/*  81 */     String aExecute = arguments.get(0);
/*     */     
/*     */     try {
/*  84 */       Process exec = Runtime.getRuntime().exec(aExecute);
/*     */ 
/*     */       
/*  87 */       try (InputStream execOut = exec.getInputStream()) {
/*  88 */         Reader execReader = new InputStreamReader(execOut);
/*     */         
/*  90 */         char[] buffer = new char[1024];
/*  91 */         int bytes_read = execReader.read(buffer);
/*  92 */         while (bytes_read > 0) {
/*  93 */           aOutputBuffer.append(buffer, 0, bytes_read);
/*  94 */           bytes_read = execReader.read(buffer);
/*     */         } 
/*     */       } 
/*  97 */     } catch (IOException ioe) {
/*  98 */       throw new TemplateModelException(ioe.getMessage());
/*     */     } 
/* 100 */     return aOutputBuffer.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\Execute.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */