/*    */ package cn.hutool.core.compiler;
/*    */ 
/*    */ import cn.hutool.core.io.file.FileNameUtil;
/*    */ import cn.hutool.core.util.ZipUtil;
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.zip.ZipEntry;
/*    */ import java.util.zip.ZipFile;
/*    */ import javax.tools.JavaFileObject;
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
/*    */ public class JavaFileObjectUtil
/*    */ {
/*    */   public static List<JavaFileObject> getJavaFileObjects(File file) {
/* 27 */     List<JavaFileObject> result = new ArrayList<>();
/* 28 */     String fileName = file.getName();
/*    */     
/* 30 */     if (isJavaFile(fileName)) {
/* 31 */       result.add(new JavaSourceFileObject(file.toURI()));
/* 32 */     } else if (isJarOrZipFile(fileName)) {
/* 33 */       result.addAll(getJavaFileObjectByZipOrJarFile(file));
/*    */     } 
/* 35 */     return result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isJarOrZipFile(String fileName) {
/* 45 */     return FileNameUtil.isType(fileName, new String[] { "jar", "zip" });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isJavaFile(String fileName) {
/* 55 */     return FileNameUtil.isType(fileName, new String[] { "java" });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static List<JavaFileObject> getJavaFileObjectByZipOrJarFile(File file) {
/* 65 */     List<JavaFileObject> collection = new ArrayList<>();
/* 66 */     ZipFile zipFile = ZipUtil.toZipFile(file, null);
/* 67 */     ZipUtil.read(zipFile, zipEntry -> {
/*    */           String name = zipEntry.getName();
/*    */           if (isJavaFile(name)) {
/*    */             collection.add(new JavaSourceFileObject(name, ZipUtil.getStream(zipFile, zipEntry)));
/*    */           }
/*    */         });
/* 73 */     return collection;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compiler\JavaFileObjectUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */