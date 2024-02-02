/*    */ package org.apache.commons.compress.archivers.jar;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*    */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
/*    */ import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JarArchiveInputStream
/*    */   extends ZipArchiveInputStream
/*    */ {
/*    */   public JarArchiveInputStream(InputStream inputStream) {
/* 41 */     super(inputStream);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JarArchiveInputStream(InputStream inputStream, String encoding) {
/* 52 */     super(inputStream, encoding);
/*    */   }
/*    */   
/*    */   public JarArchiveEntry getNextJarEntry() throws IOException {
/* 56 */     ZipArchiveEntry entry = getNextZipEntry();
/* 57 */     return (entry == null) ? null : new JarArchiveEntry(entry);
/*    */   }
/*    */ 
/*    */   
/*    */   public ArchiveEntry getNextEntry() throws IOException {
/* 62 */     return (ArchiveEntry)getNextJarEntry();
/*    */   }
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
/*    */   public static boolean matches(byte[] signature, int length) {
/* 76 */     return ZipArchiveInputStream.matches(signature, length);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\jar\JarArchiveInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */