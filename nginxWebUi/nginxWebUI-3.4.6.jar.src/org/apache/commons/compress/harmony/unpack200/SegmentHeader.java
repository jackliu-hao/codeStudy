/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.compress.harmony.pack200.BHSDCodec;
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
/*     */ public class SegmentHeader
/*     */ {
/*     */   private int archiveMajor;
/*     */   private int archiveMinor;
/*     */   private long archiveModtime;
/*     */   private long archiveSize;
/*     */   private int attributeDefinitionCount;
/*     */   private InputStream bandHeadersInputStream;
/*     */   private int bandHeadersSize;
/*     */   private int classCount;
/*     */   private int cpClassCount;
/*     */   private int cpDescriptorCount;
/*     */   private int cpDoubleCount;
/*     */   private int cpFieldCount;
/*     */   private int cpFloatCount;
/*     */   private int cpIMethodCount;
/*     */   private int cpIntCount;
/*     */   private int cpLongCount;
/*     */   private int cpMethodCount;
/*     */   private int cpSignatureCount;
/*     */   private int cpStringCount;
/*     */   private int cpUTF8Count;
/*     */   private int defaultClassMajorVersion;
/*     */   private int defaultClassMinorVersion;
/*     */   private int innerClassCount;
/*     */   private int numberOfFiles;
/*     */   private int segmentsRemaining;
/*     */   private SegmentOptions options;
/*     */   private final Segment segment;
/*  90 */   private static final int[] magic = new int[] { 202, 254, 208, 13 };
/*     */   
/*     */   public SegmentHeader(Segment segment) {
/*  93 */     this.segment = segment;
/*     */   }
/*     */   private int archiveSizeOffset;
/*     */   public int getArchiveSizeOffset() {
/*  97 */     return this.archiveSizeOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void read(InputStream in) throws IOException, Pack200Exception, Error, Pack200Exception {
/* 104 */     int[] word = decodeScalar("archive_magic_word", in, Codec.BYTE1, magic.length);
/* 105 */     for (int m = 0; m < magic.length; m++) {
/* 106 */       if (word[m] != magic[m]) {
/* 107 */         throw new Error("Bad header");
/*     */       }
/*     */     } 
/* 110 */     setArchiveMinorVersion(decodeScalar("archive_minver", in, Codec.UNSIGNED5));
/* 111 */     setArchiveMajorVersion(decodeScalar("archive_majver", in, Codec.UNSIGNED5));
/* 112 */     this.options = new SegmentOptions(decodeScalar("archive_options", in, Codec.UNSIGNED5));
/* 113 */     parseArchiveFileCounts(in);
/* 114 */     parseArchiveSpecialCounts(in);
/* 115 */     parseCpCounts(in);
/* 116 */     parseClassCounts(in);
/*     */     
/* 118 */     if (getBandHeadersSize() > 0) {
/* 119 */       byte[] bandHeaders = new byte[getBandHeadersSize()];
/* 120 */       readFully(in, bandHeaders);
/* 121 */       setBandHeadersData(bandHeaders);
/*     */     } 
/*     */     
/* 124 */     this.archiveSizeOffset -= in.available();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unpack() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setArchiveMinorVersion(int version) throws Pack200Exception {
/* 138 */     if (version != 7) {
/* 139 */       throw new Pack200Exception("Invalid segment minor version");
/*     */     }
/* 141 */     this.archiveMinor = version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setArchiveMajorVersion(int version) throws Pack200Exception {
/* 151 */     if (version != 150) {
/* 152 */       throw new Pack200Exception("Invalid segment major version: " + version);
/*     */     }
/* 154 */     this.archiveMajor = version;
/*     */   }
/*     */   
/*     */   public long getArchiveModtime() {
/* 158 */     return this.archiveModtime;
/*     */   }
/*     */   
/*     */   public int getAttributeDefinitionCount() {
/* 162 */     return this.attributeDefinitionCount;
/*     */   }
/*     */   
/*     */   public int getClassCount() {
/* 166 */     return this.classCount;
/*     */   }
/*     */   
/*     */   public int getCpClassCount() {
/* 170 */     return this.cpClassCount;
/*     */   }
/*     */   
/*     */   public int getCpDescriptorCount() {
/* 174 */     return this.cpDescriptorCount;
/*     */   }
/*     */   
/*     */   public int getCpDoubleCount() {
/* 178 */     return this.cpDoubleCount;
/*     */   }
/*     */   
/*     */   public int getCpFieldCount() {
/* 182 */     return this.cpFieldCount;
/*     */   }
/*     */   
/*     */   public int getCpFloatCount() {
/* 186 */     return this.cpFloatCount;
/*     */   }
/*     */   
/*     */   public int getCpIMethodCount() {
/* 190 */     return this.cpIMethodCount;
/*     */   }
/*     */   
/*     */   public int getCpIntCount() {
/* 194 */     return this.cpIntCount;
/*     */   }
/*     */   
/*     */   public int getCpLongCount() {
/* 198 */     return this.cpLongCount;
/*     */   }
/*     */   
/*     */   public int getCpMethodCount() {
/* 202 */     return this.cpMethodCount;
/*     */   }
/*     */   
/*     */   public int getCpSignatureCount() {
/* 206 */     return this.cpSignatureCount;
/*     */   }
/*     */   
/*     */   public int getCpStringCount() {
/* 210 */     return this.cpStringCount;
/*     */   }
/*     */   
/*     */   public int getCpUTF8Count() {
/* 214 */     return this.cpUTF8Count;
/*     */   }
/*     */   
/*     */   public int getDefaultClassMajorVersion() {
/* 218 */     return this.defaultClassMajorVersion;
/*     */   }
/*     */   
/*     */   public int getDefaultClassMinorVersion() {
/* 222 */     return this.defaultClassMinorVersion;
/*     */   }
/*     */   
/*     */   public int getInnerClassCount() {
/* 226 */     return this.innerClassCount;
/*     */   }
/*     */   
/*     */   public long getArchiveSize() {
/* 230 */     return this.archiveSize;
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
/*     */   public InputStream getBandHeadersInputStream() {
/* 243 */     if (this.bandHeadersInputStream == null) {
/* 244 */       this.bandHeadersInputStream = new ByteArrayInputStream(new byte[0]);
/*     */     }
/* 246 */     return this.bandHeadersInputStream;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNumberOfFiles() {
/* 251 */     return this.numberOfFiles;
/*     */   }
/*     */   
/*     */   public int getSegmentsRemaining() {
/* 255 */     return this.segmentsRemaining;
/*     */   }
/*     */   
/*     */   public SegmentOptions getOptions() {
/* 259 */     return this.options;
/*     */   }
/*     */   
/*     */   private void parseArchiveFileCounts(InputStream in) throws IOException, Pack200Exception {
/* 263 */     if (this.options.hasArchiveFileCounts()) {
/* 264 */       setArchiveSize(decodeScalar("archive_size_hi", in, Codec.UNSIGNED5) << 32L | 
/* 265 */           decodeScalar("archive_size_lo", in, Codec.UNSIGNED5));
/* 266 */       this.archiveSizeOffset = in.available();
/* 267 */       setSegmentsRemaining(decodeScalar("archive_next_count", in, Codec.UNSIGNED5));
/* 268 */       setArchiveModtime(decodeScalar("archive_modtime", in, Codec.UNSIGNED5));
/* 269 */       this.numberOfFiles = decodeScalar("file_count", in, Codec.UNSIGNED5);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void parseArchiveSpecialCounts(InputStream in) throws IOException, Pack200Exception {
/* 274 */     if (getOptions().hasSpecialFormats()) {
/* 275 */       this.bandHeadersSize = decodeScalar("band_headers_size", in, Codec.UNSIGNED5);
/* 276 */       setAttributeDefinitionCount(decodeScalar("attr_definition_count", in, Codec.UNSIGNED5));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void parseClassCounts(InputStream in) throws IOException, Pack200Exception {
/* 281 */     this.innerClassCount = decodeScalar("ic_count", in, Codec.UNSIGNED5);
/* 282 */     this.defaultClassMinorVersion = decodeScalar("default_class_minver", in, Codec.UNSIGNED5);
/* 283 */     this.defaultClassMajorVersion = decodeScalar("default_class_majver", in, Codec.UNSIGNED5);
/* 284 */     this.classCount = decodeScalar("class_count", in, Codec.UNSIGNED5);
/*     */   }
/*     */   
/*     */   private void parseCpCounts(InputStream in) throws IOException, Pack200Exception {
/* 288 */     this.cpUTF8Count = decodeScalar("cp_Utf8_count", in, Codec.UNSIGNED5);
/* 289 */     if (getOptions().hasCPNumberCounts()) {
/* 290 */       this.cpIntCount = decodeScalar("cp_Int_count", in, Codec.UNSIGNED5);
/* 291 */       this.cpFloatCount = decodeScalar("cp_Float_count", in, Codec.UNSIGNED5);
/* 292 */       this.cpLongCount = decodeScalar("cp_Long_count", in, Codec.UNSIGNED5);
/* 293 */       this.cpDoubleCount = decodeScalar("cp_Double_count", in, Codec.UNSIGNED5);
/*     */     } 
/* 295 */     this.cpStringCount = decodeScalar("cp_String_count", in, Codec.UNSIGNED5);
/* 296 */     this.cpClassCount = decodeScalar("cp_Class_count", in, Codec.UNSIGNED5);
/* 297 */     this.cpSignatureCount = decodeScalar("cp_Signature_count", in, Codec.UNSIGNED5);
/* 298 */     this.cpDescriptorCount = decodeScalar("cp_Descr_count", in, Codec.UNSIGNED5);
/* 299 */     this.cpFieldCount = decodeScalar("cp_Field_count", in, Codec.UNSIGNED5);
/* 300 */     this.cpMethodCount = decodeScalar("cp_Method_count", in, Codec.UNSIGNED5);
/* 301 */     this.cpIMethodCount = decodeScalar("cp_Imethod_count", in, Codec.UNSIGNED5);
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
/*     */   private int[] decodeScalar(String name, InputStream in, BHSDCodec codec, int n) throws IOException, Pack200Exception {
/* 317 */     this.segment.log(2, "Parsed #" + name + " (" + n + ")");
/* 318 */     return codec.decodeInts(n, in);
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
/*     */   private int decodeScalar(String name, InputStream in, BHSDCodec codec) throws IOException, Pack200Exception {
/* 333 */     int ret = codec.decode(in);
/* 334 */     this.segment.log(2, "Parsed #" + name + " as " + ret);
/* 335 */     return ret;
/*     */   }
/*     */   
/*     */   public void setArchiveModtime(long archiveModtime) {
/* 339 */     this.archiveModtime = archiveModtime;
/*     */   }
/*     */   
/*     */   public void setArchiveSize(long archiveSize) {
/* 343 */     this.archiveSize = archiveSize;
/*     */   }
/*     */   
/*     */   private void setAttributeDefinitionCount(long valuie) {
/* 347 */     this.attributeDefinitionCount = (int)valuie;
/*     */   }
/*     */   
/*     */   private void setBandHeadersData(byte[] bandHeaders) {
/* 351 */     this.bandHeadersInputStream = new ByteArrayInputStream(bandHeaders);
/*     */   }
/*     */   
/*     */   public void setSegmentsRemaining(long value) {
/* 355 */     this.segmentsRemaining = (int)value;
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
/*     */   private void readFully(InputStream in, byte[] data) throws IOException, Pack200Exception {
/* 368 */     int total = in.read(data);
/* 369 */     if (total == -1) {
/* 370 */       throw new EOFException("Failed to read any data from input stream");
/*     */     }
/* 372 */     while (total < data.length) {
/* 373 */       int delta = in.read(data, total, data.length - total);
/* 374 */       if (delta == -1) {
/* 375 */         throw new EOFException("Failed to read some data from input stream");
/*     */       }
/* 377 */       total += delta;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getBandHeadersSize() {
/* 382 */     return this.bandHeadersSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\SegmentHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */