/*    */ package org.apache.commons.compress.archivers.jar;
/*    */ 
/*    */ import java.security.cert.Certificate;
/*    */ import java.util.jar.Attributes;
/*    */ import java.util.jar.JarEntry;
/*    */ import java.util.zip.ZipEntry;
/*    */ import java.util.zip.ZipException;
/*    */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
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
/*    */ public class JarArchiveEntry
/*    */   extends ZipArchiveEntry
/*    */ {
/* 36 */   private final Attributes manifestAttributes = null;
/* 37 */   private final Certificate[] certificates = null;
/*    */   
/*    */   public JarArchiveEntry(ZipEntry entry) throws ZipException {
/* 40 */     super(entry);
/*    */   }
/*    */   
/*    */   public JarArchiveEntry(String name) {
/* 44 */     super(name);
/*    */   }
/*    */   
/*    */   public JarArchiveEntry(ZipArchiveEntry entry) throws ZipException {
/* 48 */     super(entry);
/*    */   }
/*    */   
/*    */   public JarArchiveEntry(JarEntry entry) throws ZipException {
/* 52 */     super(entry);
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
/*    */   @Deprecated
/*    */   public Attributes getManifestAttributes() {
/* 65 */     return this.manifestAttributes;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public Certificate[] getCertificates() {
/* 77 */     if (this.certificates != null) {
/* 78 */       Certificate[] certs = new Certificate[this.certificates.length];
/* 79 */       System.arraycopy(this.certificates, 0, certs, 0, certs.length);
/* 80 */       return certs;
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 87 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\jar\JarArchiveEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */