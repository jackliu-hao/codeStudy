/*    */ package org.apache.commons.compress.archivers.jar;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*    */ import org.apache.commons.compress.archivers.zip.JarMarker;
/*    */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
/*    */ import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
/*    */ import org.apache.commons.compress.archivers.zip.ZipExtraField;
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
/*    */ public class JarArchiveOutputStream
/*    */   extends ZipArchiveOutputStream
/*    */ {
/*    */   private boolean jarMarkerAdded;
/*    */   
/*    */   public JarArchiveOutputStream(OutputStream out) {
/* 41 */     super(out);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JarArchiveOutputStream(OutputStream out, String encoding) {
/* 52 */     super(out);
/* 53 */     setEncoding(encoding);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void putArchiveEntry(ArchiveEntry ze) throws IOException {
/* 59 */     if (!this.jarMarkerAdded) {
/* 60 */       ((ZipArchiveEntry)ze).addAsFirstExtraField((ZipExtraField)JarMarker.getInstance());
/* 61 */       this.jarMarkerAdded = true;
/*    */     } 
/* 63 */     super.putArchiveEntry(ze);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\jar\JarArchiveOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */