/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import org.objectweb.asm.ClassReader;
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
/*     */   private final CPUTF8[] fileName;
/*     */   private int[] file_name;
/*     */   private final int[] file_modtime;
/*     */   private final long[] file_size;
/*     */   private final int[] file_options;
/*     */   private final byte[][] file_bits;
/*     */   private final List fileList;
/*     */   private final PackingOptions options;
/*     */   private final CpBands cpBands;
/*     */   
/*     */   public FileBands(CpBands cpBands, SegmentHeader segmentHeader, PackingOptions options, Archive.SegmentUnit segmentUnit, int effort) {
/*  49 */     super(effort, segmentHeader);
/*  50 */     this.fileList = segmentUnit.getFileList();
/*  51 */     this.options = options;
/*  52 */     this.cpBands = cpBands;
/*  53 */     int size = this.fileList.size();
/*  54 */     this.fileName = new CPUTF8[size];
/*  55 */     this.file_modtime = new int[size];
/*  56 */     this.file_size = new long[size];
/*  57 */     this.file_options = new int[size];
/*  58 */     int totalSize = 0;
/*  59 */     this.file_bits = new byte[size][];
/*  60 */     int archiveModtime = segmentHeader.getArchive_modtime();
/*     */     
/*  62 */     Set<String> classNames = new HashSet();
/*  63 */     for (Iterator<ClassReader> iterator = segmentUnit.getClassList().iterator(); iterator.hasNext(); ) {
/*  64 */       ClassReader reader = iterator.next();
/*  65 */       classNames.add(reader.getClassName());
/*     */     } 
/*  67 */     CPUTF8 emptyString = cpBands.getCPUtf8("");
/*     */     
/*  69 */     int latestModtime = Integer.MIN_VALUE;
/*  70 */     boolean isLatest = !"keep".equals(options.getModificationTime());
/*  71 */     for (int i = 0; i < size; i++) {
/*  72 */       Archive.PackingFile packingFile = this.fileList.get(i);
/*  73 */       String name = packingFile.getName();
/*  74 */       if (name.endsWith(".class") && !options.isPassFile(name)) {
/*  75 */         this.file_options[i] = this.file_options[i] | 0x2;
/*  76 */         if (classNames.contains(name.substring(0, name.length() - 6))) {
/*  77 */           this.fileName[i] = emptyString;
/*     */         } else {
/*  79 */           this.fileName[i] = cpBands.getCPUtf8(name);
/*     */         } 
/*     */       } else {
/*  82 */         this.fileName[i] = cpBands.getCPUtf8(name);
/*     */       } 
/*     */       
/*  85 */       if (options.isKeepDeflateHint() && packingFile.isDefalteHint()) {
/*  86 */         this.file_options[i] = this.file_options[i] | 0x1;
/*     */       }
/*  88 */       byte[] bytes = packingFile.getContents();
/*  89 */       this.file_size[i] = bytes.length;
/*  90 */       totalSize = (int)(totalSize + this.file_size[i]);
/*     */ 
/*     */       
/*  93 */       long modtime = (packingFile.getModtime() + TimeZone.getDefault().getRawOffset()) / 1000L;
/*  94 */       this.file_modtime[i] = (int)(modtime - archiveModtime);
/*  95 */       if (isLatest && latestModtime < this.file_modtime[i]) {
/*  96 */         latestModtime = this.file_modtime[i];
/*     */       }
/*     */       
/*  99 */       this.file_bits[i] = packingFile.getContents();
/*     */     } 
/*     */     
/* 102 */     if (isLatest) {
/* 103 */       for (int index = 0; index < size; index++) {
/* 104 */         this.file_modtime[index] = latestModtime;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finaliseBands() {
/* 114 */     this.file_name = new int[this.fileName.length];
/* 115 */     for (int i = 0; i < this.file_name.length; i++) {
/* 116 */       if (this.fileName[i].equals(this.cpBands.getCPUtf8(""))) {
/* 117 */         Archive.PackingFile packingFile = this.fileList.get(i);
/* 118 */         String name = packingFile.getName();
/* 119 */         if (this.options.isPassFile(name)) {
/* 120 */           this.fileName[i] = this.cpBands.getCPUtf8(name);
/* 121 */           this.file_options[i] = this.file_options[i] & 0xFFFFFFFD;
/*     */         } 
/*     */       } 
/* 124 */       this.file_name[i] = this.fileName[i].getIndex();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void pack(OutputStream out) throws IOException, Pack200Exception {
/* 130 */     PackingUtils.log("Writing file bands...");
/* 131 */     byte[] encodedBand = encodeBandInt("file_name", this.file_name, Codec.UNSIGNED5);
/* 132 */     out.write(encodedBand);
/* 133 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from file_name[" + this.file_name.length + "]");
/*     */     
/* 135 */     encodedBand = encodeFlags("file_size", this.file_size, Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader
/* 136 */         .have_file_size_hi());
/* 137 */     out.write(encodedBand);
/* 138 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from file_size[" + this.file_size.length + "]");
/*     */     
/* 140 */     if (this.segmentHeader.have_file_modtime()) {
/* 141 */       encodedBand = encodeBandInt("file_modtime", this.file_modtime, Codec.DELTA5);
/* 142 */       out.write(encodedBand);
/* 143 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from file_modtime[" + this.file_modtime.length + "]");
/*     */     } 
/* 145 */     if (this.segmentHeader.have_file_options()) {
/* 146 */       encodedBand = encodeBandInt("file_options", this.file_options, Codec.UNSIGNED5);
/* 147 */       out.write(encodedBand);
/* 148 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from file_options[" + this.file_options.length + "]");
/*     */     } 
/*     */     
/* 151 */     encodedBand = encodeBandInt("file_bits", flatten(this.file_bits), Codec.BYTE1);
/* 152 */     out.write(encodedBand);
/* 153 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from file_bits[" + this.file_bits.length + "]");
/*     */   }
/*     */   
/*     */   private int[] flatten(byte[][] bytes) {
/* 157 */     int total = 0;
/* 158 */     for (int i = 0; i < bytes.length; i++) {
/* 159 */       total += (bytes[i]).length;
/*     */     }
/* 161 */     int[] band = new int[total];
/* 162 */     int index = 0;
/* 163 */     for (int j = 0; j < bytes.length; j++) {
/* 164 */       for (int k = 0; k < (bytes[j]).length; k++) {
/* 165 */         band[index++] = bytes[j][k] & 0xFF;
/*     */       }
/*     */     } 
/* 168 */     return band;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\FileBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */