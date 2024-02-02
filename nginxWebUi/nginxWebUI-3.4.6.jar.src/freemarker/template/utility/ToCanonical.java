/*    */ package freemarker.template.utility;
/*    */ 
/*    */ import freemarker.template.Configuration;
/*    */ import freemarker.template.Template;
/*    */ import java.io.File;
/*    */ import java.io.FileWriter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class ToCanonical
/*    */ {
/* 37 */   static Configuration config = Configuration.getDefaultConfiguration();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public static void main(String[] args) {
/* 44 */     config.setWhitespaceStripping(false);
/* 45 */     if (args.length == 0) {
/* 46 */       usage();
/*    */     }
/* 48 */     for (int i = 0; i < args.length; i++) {
/* 49 */       File f = new File(args[i]);
/* 50 */       if (!f.exists()) {
/* 51 */         System.err.println("File " + f + " doesn't exist.");
/*    */       }
/*    */       try {
/* 54 */         convertFile(f);
/* 55 */       } catch (Exception e) {
/* 56 */         System.err.println("Error converting file: " + f);
/* 57 */         e.printStackTrace();
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   static void convertFile(File f) throws IOException {
/* 63 */     File fullPath = f.getAbsoluteFile();
/* 64 */     File dir = fullPath.getParentFile();
/* 65 */     String filename = fullPath.getName();
/* 66 */     File convertedFile = new File(dir, filename + ".canonical");
/* 67 */     config.setDirectoryForTemplateLoading(dir);
/* 68 */     Template template = config.getTemplate(filename);
/* 69 */     try (FileWriter output = new FileWriter(convertedFile)) {
/* 70 */       template.dump(output);
/*    */     } 
/*    */   }
/*    */   
/*    */   static void usage() {
/* 75 */     System.err.println("Usage: java freemarker.template.utility.ToCanonical <filename(s)>");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\ToCanonical.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */