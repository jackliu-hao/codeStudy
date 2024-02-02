/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
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
/*     */ public class SegmentOptions
/*     */ {
/*     */   private static final int DEFLATE_HINT = 32;
/*     */   private static final int HAVE_ALL_CODE_FLAGS = 4;
/*     */   private static final int HAVE_CLASS_FLAGS_HI = 512;
/*     */   private static final int HAVE_CODE_FLAGS_HI = 1024;
/*     */   private static final int HAVE_CP_NUMBERS = 2;
/*     */   private static final int HAVE_FIELD_FLAGS_HI = 1024;
/*     */   private static final int HAVE_FILE_HEADERS = 16;
/*     */   private static final int HAVE_FILE_MODTIME = 64;
/*     */   private static final int HAVE_FILE_OPTIONS = 128;
/*     */   private static final int HAVE_FILE_SIZE_HI = 256;
/*     */   private static final int HAVE_METHOD_FLAGS_HI = 2048;
/*     */   private static final int HAVE_SPECIAL_FORMATS = 1;
/*     */   private static final int UNUSED = -8184;
/*     */   private final int options;
/*     */   
/*     */   public SegmentOptions(int options) throws Pack200Exception {
/*  69 */     if ((options & 0xFFFFE008) != 0) {
/*  70 */       throw new Pack200Exception("Some unused flags are non-zero");
/*     */     }
/*  72 */     this.options = options;
/*     */   }
/*     */   
/*     */   public boolean hasAllCodeFlags() {
/*  76 */     return ((this.options & 0x4) != 0);
/*     */   }
/*     */   
/*     */   public boolean hasArchiveFileCounts() {
/*  80 */     return ((this.options & 0x10) != 0);
/*     */   }
/*     */   
/*     */   public boolean hasClassFlagsHi() {
/*  84 */     return ((this.options & 0x200) != 0);
/*     */   }
/*     */   
/*     */   public boolean hasCodeFlagsHi() {
/*  88 */     return ((this.options & 0x400) != 0);
/*     */   }
/*     */   
/*     */   public boolean hasCPNumberCounts() {
/*  92 */     return ((this.options & 0x2) != 0);
/*     */   }
/*     */   
/*     */   public boolean hasFieldFlagsHi() {
/*  96 */     return ((this.options & 0x400) != 0);
/*     */   }
/*     */   
/*     */   public boolean hasFileModtime() {
/* 100 */     return ((this.options & 0x40) != 0);
/*     */   }
/*     */   
/*     */   public boolean hasFileOptions() {
/* 104 */     return ((this.options & 0x80) != 0);
/*     */   }
/*     */   
/*     */   public boolean hasFileSizeHi() {
/* 108 */     return ((this.options & 0x100) != 0);
/*     */   }
/*     */   
/*     */   public boolean hasMethodFlagsHi() {
/* 112 */     return ((this.options & 0x800) != 0);
/*     */   }
/*     */   
/*     */   public boolean hasSpecialFormats() {
/* 116 */     return ((this.options & 0x1) != 0);
/*     */   }
/*     */   
/*     */   public boolean shouldDeflate() {
/* 120 */     return ((this.options & 0x20) != 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\SegmentOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */