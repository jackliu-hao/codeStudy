/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.compress.harmony.pack200.Codec;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileBands
/*     */   extends BandSet
/*     */ {
/*     */   private byte[][] fileBits;
/*     */   private int[] fileModtime;
/*     */   private String[] fileName;
/*     */   private int[] fileOptions;
/*     */   private long[] fileSize;
/*     */   private final String[] cpUTF8;
/*     */   private InputStream in;
/*     */   
/*     */   public FileBands(Segment segment) {
/*  51 */     super(segment);
/*  52 */     this.cpUTF8 = segment.getCpBands().getCpUTF8();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void read(InputStream in) throws IOException, Pack200Exception {
/*  62 */     int numberOfFiles = this.header.getNumberOfFiles();
/*  63 */     SegmentOptions options = this.header.getOptions();
/*     */     
/*  65 */     this.fileName = parseReferences("file_name", in, Codec.UNSIGNED5, numberOfFiles, this.cpUTF8);
/*  66 */     this.fileSize = parseFlags("file_size", in, numberOfFiles, Codec.UNSIGNED5, options.hasFileSizeHi());
/*  67 */     if (options.hasFileModtime()) {
/*  68 */       this.fileModtime = decodeBandInt("file_modtime", in, Codec.DELTA5, numberOfFiles);
/*     */     } else {
/*  70 */       this.fileModtime = new int[numberOfFiles];
/*     */     } 
/*  72 */     if (options.hasFileOptions()) {
/*  73 */       this.fileOptions = decodeBandInt("file_options", in, Codec.UNSIGNED5, numberOfFiles);
/*     */     } else {
/*  75 */       this.fileOptions = new int[numberOfFiles];
/*     */     } 
/*  77 */     this.in = in;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void processFileBits() throws IOException, Pack200Exception {
/*  84 */     int numberOfFiles = this.header.getNumberOfFiles();
/*  85 */     this.fileBits = new byte[numberOfFiles][];
/*  86 */     for (int i = 0; i < numberOfFiles; i++) {
/*  87 */       int size = (int)this.fileSize[i];
/*     */ 
/*     */       
/*  90 */       this.fileBits[i] = new byte[size];
/*  91 */       int read = this.in.read(this.fileBits[i]);
/*  92 */       if (size != 0 && read < size) {
/*  93 */         throw new Pack200Exception("Expected to read " + size + " bytes but read " + read);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void unpack() {}
/*     */ 
/*     */   
/*     */   public byte[][] getFileBits() {
/* 104 */     return this.fileBits;
/*     */   }
/*     */   
/*     */   public int[] getFileModtime() {
/* 108 */     return this.fileModtime;
/*     */   }
/*     */   
/*     */   public String[] getFileName() {
/* 112 */     return this.fileName;
/*     */   }
/*     */   
/*     */   public int[] getFileOptions() {
/* 116 */     return this.fileOptions;
/*     */   }
/*     */   
/*     */   public long[] getFileSize() {
/* 120 */     return this.fileSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\FileBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */