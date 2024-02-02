/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
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
/*     */ public class Archive
/*     */ {
/*     */   private InputStream inputStream;
/*     */   private final JarOutputStream outputStream;
/*     */   private boolean removePackFile;
/*  48 */   private int logLevel = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   private FileOutputStream logFile;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean overrideDeflateHint;
/*     */ 
/*     */   
/*     */   private boolean deflateHint;
/*     */ 
/*     */   
/*     */   private String inputFileName;
/*     */ 
/*     */   
/*     */   private String outputFileName;
/*     */ 
/*     */ 
/*     */   
/*     */   public Archive(String inputFile, String outputFile) throws FileNotFoundException, IOException {
/*  70 */     this.inputFileName = inputFile;
/*  71 */     this.outputFileName = outputFile;
/*  72 */     this.inputStream = new FileInputStream(inputFile);
/*  73 */     this.outputStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
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
/*     */   public Archive(InputStream inputStream, JarOutputStream outputStream) throws IOException {
/*  85 */     this.inputStream = inputStream;
/*  86 */     this.outputStream = outputStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unpack() throws Pack200Exception, IOException {
/*  96 */     this.outputStream.setComment("PACK200");
/*     */     try {
/*  98 */       if (!this.inputStream.markSupported()) {
/*  99 */         this.inputStream = new BufferedInputStream(this.inputStream);
/* 100 */         if (!this.inputStream.markSupported()) {
/* 101 */           throw new IllegalStateException();
/*     */         }
/*     */       } 
/* 104 */       this.inputStream.mark(2);
/* 105 */       if ((this.inputStream.read() & 0xFF | (this.inputStream.read() & 0xFF) << 8) == 35615) {
/* 106 */         this.inputStream.reset();
/* 107 */         this.inputStream = new BufferedInputStream(new GZIPInputStream(this.inputStream));
/*     */       } else {
/* 109 */         this.inputStream.reset();
/*     */       } 
/* 111 */       this.inputStream.mark(4);
/* 112 */       int[] magic = { 202, 254, 208, 13 };
/*     */       
/* 114 */       int[] word = new int[4];
/* 115 */       for (int i = 0; i < word.length; i++) {
/* 116 */         word[i] = this.inputStream.read();
/*     */       }
/* 118 */       boolean compressedWithE0 = false;
/* 119 */       for (int m = 0; m < magic.length; m++) {
/* 120 */         if (word[m] != magic[m]) {
/* 121 */           compressedWithE0 = true;
/*     */         }
/*     */       } 
/* 124 */       this.inputStream.reset();
/* 125 */       if (compressedWithE0) {
/*     */         
/* 127 */         JarInputStream jarInputStream = new JarInputStream(this.inputStream);
/*     */         JarEntry jarEntry;
/* 129 */         while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
/* 130 */           this.outputStream.putNextEntry(jarEntry);
/* 131 */           byte[] bytes = new byte[16384];
/* 132 */           int bytesRead = jarInputStream.read(bytes);
/* 133 */           while (bytesRead != -1) {
/* 134 */             this.outputStream.write(bytes, 0, bytesRead);
/* 135 */             bytesRead = jarInputStream.read(bytes);
/*     */           } 
/* 137 */           this.outputStream.closeEntry();
/*     */         } 
/*     */       } else {
/* 140 */         int j = 0;
/* 141 */         while (available(this.inputStream)) {
/* 142 */           j++;
/* 143 */           Segment segment = new Segment();
/* 144 */           segment.setLogLevel(this.logLevel);
/* 145 */           segment.setLogStream((this.logFile != null) ? this.logFile : System.out);
/* 146 */           segment.setPreRead(false);
/*     */           
/* 148 */           if (j == 1) {
/* 149 */             segment.log(2, "Unpacking from " + this.inputFileName + " to " + this.outputFileName);
/*     */           }
/*     */           
/* 152 */           segment.log(2, "Reading segment " + j);
/* 153 */           if (this.overrideDeflateHint) {
/* 154 */             segment.overrideDeflateHint(this.deflateHint);
/*     */           }
/* 156 */           segment.unpack(this.inputStream, this.outputStream);
/* 157 */           this.outputStream.flush();
/*     */           
/* 159 */           if (this.inputStream instanceof FileInputStream) {
/* 160 */             this.inputFileName = ((FileInputStream)this.inputStream).getFD().toString();
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } finally {
/*     */       try {
/* 166 */         this.inputStream.close();
/* 167 */       } catch (Exception exception) {}
/*     */       
/*     */       try {
/* 170 */         this.outputStream.close();
/* 171 */       } catch (Exception exception) {}
/*     */       
/* 173 */       if (this.logFile != null) {
/*     */         try {
/* 175 */           this.logFile.close();
/* 176 */         } catch (Exception exception) {}
/*     */       }
/*     */     } 
/*     */     
/* 180 */     if (this.removePackFile) {
/* 181 */       boolean deleted = false;
/* 182 */       if (this.inputFileName != null) {
/* 183 */         File file = new File(this.inputFileName);
/* 184 */         deleted = file.delete();
/*     */       } 
/* 186 */       if (!deleted) {
/* 187 */         throw new Pack200Exception("Failed to delete the input file.");
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean available(InputStream inputStream) throws IOException {
/* 193 */     inputStream.mark(1);
/* 194 */     int check = inputStream.read();
/* 195 */     inputStream.reset();
/* 196 */     return (check != -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRemovePackFile(boolean removePackFile) {
/* 205 */     this.removePackFile = removePackFile;
/*     */   }
/*     */   
/*     */   public void setVerbose(boolean verbose) {
/* 209 */     if (verbose) {
/* 210 */       this.logLevel = 2;
/* 211 */     } else if (this.logLevel == 2) {
/* 212 */       this.logLevel = 1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setQuiet(boolean quiet) {
/* 217 */     if (quiet) {
/* 218 */       this.logLevel = 0;
/* 219 */     } else if (this.logLevel == 0) {
/* 220 */       this.logLevel = 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setLogFile(String logFileName) throws FileNotFoundException {
/* 225 */     this.logFile = new FileOutputStream(logFileName);
/*     */   }
/*     */   
/*     */   public void setLogFile(String logFileName, boolean append) throws FileNotFoundException {
/* 229 */     this.logFile = new FileOutputStream(logFileName, append);
/*     */   }
/*     */   
/*     */   public void setDeflateHint(boolean deflateHint) {
/* 233 */     this.overrideDeflateHint = true;
/* 234 */     this.deflateHint = deflateHint;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\Archive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */