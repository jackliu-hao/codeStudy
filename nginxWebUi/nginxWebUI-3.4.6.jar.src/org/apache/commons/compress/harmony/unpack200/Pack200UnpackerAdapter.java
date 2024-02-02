/*    */ package org.apache.commons.compress.harmony.unpack200;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.jar.JarOutputStream;
/*    */ import org.apache.commons.compress.harmony.pack200.Pack200Adapter;
/*    */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*    */ import org.apache.commons.compress.java.util.jar.Pack200;
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
/*    */ public class Pack200UnpackerAdapter
/*    */   extends Pack200Adapter
/*    */   implements Pack200.Unpacker
/*    */ {
/*    */   public void unpack(InputStream in, JarOutputStream out) throws IOException {
/* 44 */     if (in == null || out == null) {
/* 45 */       throw new IllegalArgumentException("Must specify both input and output streams");
/*    */     }
/* 47 */     completed(0.0D);
/*    */     try {
/* 49 */       (new Archive(in, out)).unpack();
/* 50 */     } catch (Pack200Exception e) {
/* 51 */       throw new IOException("Failed to unpack Jar:" + String.valueOf(e));
/*    */     } 
/* 53 */     completed(1.0D);
/* 54 */     in.close();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void unpack(File file, JarOutputStream out) throws IOException {
/* 65 */     if (file == null || out == null) {
/* 66 */       throw new IllegalArgumentException("Must specify both input and output streams");
/*    */     }
/* 68 */     int size = (int)file.length();
/* 69 */     int bufferSize = (size > 0 && size < 8192) ? size : 8192;
/* 70 */     InputStream in = new BufferedInputStream(new FileInputStream(file), bufferSize);
/* 71 */     unpack(in, out);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\Pack200UnpackerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */