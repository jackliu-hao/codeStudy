/*     */ package org.apache.commons.compress.compressors.pack200;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.file.Files;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import org.apache.commons.compress.java.util.jar.Pack200;
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
/*     */ public class Pack200Utils
/*     */ {
/*     */   public static void normalize(File jar) throws IOException {
/*  60 */     normalize(jar, jar, null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void normalize(File jar, Map<String, String> props) throws IOException {
/*  81 */     normalize(jar, jar, props);
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
/*     */   public static void normalize(File from, File to) throws IOException {
/* 106 */     normalize(from, to, null);
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
/*     */   public static void normalize(File from, File to, Map<String, String> props) throws IOException {
/* 130 */     if (props == null) {
/* 131 */       props = new HashMap<>();
/*     */     }
/* 133 */     props.put("pack.segment.limit", "-1");
/* 134 */     File tempFile = File.createTempFile("commons-compress", "pack200normalize");
/*     */     try {
/* 136 */       try(OutputStream fos = Files.newOutputStream(tempFile.toPath(), new java.nio.file.OpenOption[0]); 
/* 137 */           JarFile jarFile = new JarFile(from)) {
/* 138 */         Pack200.Packer packer = Pack200.newPacker();
/* 139 */         packer.properties().putAll(props);
/* 140 */         packer.pack(jarFile, fos);
/*     */       } 
/* 142 */       Pack200.Unpacker unpacker = Pack200.newUnpacker();
/* 143 */       try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(to.toPath(), new java.nio.file.OpenOption[0]))) {
/* 144 */         unpacker.unpack(tempFile, jos);
/*     */       } 
/*     */     } finally {
/* 147 */       if (!tempFile.delete())
/* 148 */         tempFile.deleteOnExit(); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\pack200\Pack200Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */