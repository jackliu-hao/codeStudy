/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
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
/*     */   extends BandSet
/*     */ {
/*     */   public SegmentHeader() {
/*  32 */     super(1, null);
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
/*  56 */     this.band_headers = new IntList();
/*     */     
/*  58 */     this.have_all_code_flags = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  67 */     this.have_file_modtime = true;
/*  68 */     this.have_file_options = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  77 */     this.majverCounter = new Counter();
/*     */   }
/*     */   private static final int[] magic = new int[] { 202, 254, 208, 13 }; private static final int archive_minver = 7; private static final int archive_majver = 150; private int archive_options; private int cp_Utf8_count; private int cp_Int_count; private int cp_Float_count; private int cp_Long_count; private int cp_Double_count; private int cp_String_count; private int cp_Class_count; private int cp_Signature_count; private int cp_Descr_count; private int cp_Field_count; private int cp_Method_count;
/*     */   private int cp_Imethod_count;
/*     */   private int attribute_definition_count;
/*     */   
/*     */   public void pack(OutputStream out) throws IOException, Pack200Exception {
/*  84 */     out.write(encodeScalar(magic, Codec.BYTE1));
/*  85 */     out.write(encodeScalar(7, Codec.UNSIGNED5));
/*  86 */     out.write(encodeScalar(150, Codec.UNSIGNED5));
/*  87 */     calculateArchiveOptions();
/*  88 */     out.write(encodeScalar(this.archive_options, Codec.UNSIGNED5));
/*  89 */     writeArchiveFileCounts(out);
/*  90 */     writeArchiveSpecialCounts(out);
/*  91 */     writeCpCounts(out);
/*  92 */     writeClassCounts(out);
/*  93 */     if (this.band_headers.size() > 0)
/*  94 */       out.write(encodeScalar(this.band_headers.toArray(), Codec.BYTE1)); 
/*     */   }
/*     */   private final IntList band_headers; private boolean have_all_code_flags; private int archive_size_hi; private int archive_size_lo; private int archive_next_count; private int archive_modtime; private int file_count; private boolean deflate_hint; private final boolean have_file_modtime = true; private final boolean have_file_options = true; private boolean have_file_size_hi; private boolean have_class_flags_hi; private boolean have_field_flags_hi; private boolean have_method_flags_hi; private boolean have_code_flags_hi; private int ic_count; private int class_count; private final Counter majverCounter;
/*     */   
/*     */   private void calculateArchiveOptions() {
/*  99 */     if (this.attribute_definition_count > 0 || this.band_headers.size() > 0) {
/* 100 */       this.archive_options |= 0x1;
/*     */     }
/* 102 */     if (this.cp_Int_count > 0 || this.cp_Float_count > 0 || this.cp_Long_count > 0 || this.cp_Double_count > 0) {
/* 103 */       this.archive_options |= 0x2;
/*     */     }
/* 105 */     if (this.have_all_code_flags) {
/* 106 */       this.archive_options |= 0x4;
/*     */     }
/* 108 */     if (this.file_count > 0) {
/* 109 */       this.archive_options |= 0x10;
/*     */     }
/* 111 */     if (this.deflate_hint) {
/* 112 */       this.archive_options |= 0x20;
/*     */     }
/*     */     
/* 115 */     this.archive_options |= 0x40;
/*     */ 
/*     */     
/* 118 */     this.archive_options |= 0x80;
/*     */     
/* 120 */     if (this.have_file_size_hi) {
/* 121 */       this.archive_options |= 0x100;
/*     */     }
/* 123 */     if (this.have_class_flags_hi) {
/* 124 */       this.archive_options |= 0x200;
/*     */     }
/* 126 */     if (this.have_field_flags_hi) {
/* 127 */       this.archive_options |= 0x400;
/*     */     }
/* 129 */     if (this.have_method_flags_hi) {
/* 130 */       this.archive_options |= 0x800;
/*     */     }
/* 132 */     if (this.have_code_flags_hi) {
/* 133 */       this.archive_options |= 0x1000;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCp_Utf8_count(int count) {
/* 138 */     this.cp_Utf8_count = count;
/*     */   }
/*     */   
/*     */   public void setCp_Int_count(int count) {
/* 142 */     this.cp_Int_count = count;
/*     */   }
/*     */   
/*     */   public void setCp_Float_count(int count) {
/* 146 */     this.cp_Float_count = count;
/*     */   }
/*     */   
/*     */   public void setCp_Long_count(int count) {
/* 150 */     this.cp_Long_count = count;
/*     */   }
/*     */   
/*     */   public void setCp_Double_count(int count) {
/* 154 */     this.cp_Double_count = count;
/*     */   }
/*     */   
/*     */   public void setCp_String_count(int count) {
/* 158 */     this.cp_String_count = count;
/*     */   }
/*     */   
/*     */   public void setCp_Class_count(int count) {
/* 162 */     this.cp_Class_count = count;
/*     */   }
/*     */   
/*     */   public void setCp_Signature_count(int count) {
/* 166 */     this.cp_Signature_count = count;
/*     */   }
/*     */   
/*     */   public void setCp_Descr_count(int count) {
/* 170 */     this.cp_Descr_count = count;
/*     */   }
/*     */   
/*     */   public void setCp_Field_count(int count) {
/* 174 */     this.cp_Field_count = count;
/*     */   }
/*     */   
/*     */   public void setCp_Method_count(int count) {
/* 178 */     this.cp_Method_count = count;
/*     */   }
/*     */   
/*     */   public void setCp_Imethod_count(int count) {
/* 182 */     this.cp_Imethod_count = count;
/*     */   }
/*     */   
/*     */   public void setAttribute_definition_count(int attribute_definition_count) {
/* 186 */     this.attribute_definition_count = attribute_definition_count;
/*     */   }
/*     */   
/*     */   public void setHave_all_code_flags(boolean have_all_code_flags) {
/* 190 */     this.have_all_code_flags = have_all_code_flags;
/*     */   }
/*     */   
/*     */   public int getArchive_modtime() {
/* 194 */     return this.archive_modtime;
/*     */   }
/*     */   
/*     */   public void setFile_count(int file_count) {
/* 198 */     this.file_count = file_count;
/*     */   }
/*     */   
/*     */   public void setDeflate_hint(boolean deflate_hint) {
/* 202 */     this.deflate_hint = deflate_hint;
/*     */   }
/*     */   
/*     */   public void setHave_class_flags_hi(boolean have_class_flags_hi) {
/* 206 */     this.have_class_flags_hi = have_class_flags_hi;
/*     */   }
/*     */   
/*     */   public void setHave_field_flags_hi(boolean have_field_flags_hi) {
/* 210 */     this.have_field_flags_hi = have_field_flags_hi;
/*     */   }
/*     */   
/*     */   public void setHave_method_flags_hi(boolean have_method_flags_hi) {
/* 214 */     this.have_method_flags_hi = have_method_flags_hi;
/*     */   }
/*     */   
/*     */   public void setHave_code_flags_hi(boolean have_code_flags_hi) {
/* 218 */     this.have_code_flags_hi = have_code_flags_hi;
/*     */   }
/*     */   
/*     */   public boolean have_class_flags_hi() {
/* 222 */     return this.have_class_flags_hi;
/*     */   }
/*     */   
/*     */   public boolean have_field_flags_hi() {
/* 226 */     return this.have_field_flags_hi;
/*     */   }
/*     */   
/*     */   public boolean have_method_flags_hi() {
/* 230 */     return this.have_method_flags_hi;
/*     */   }
/*     */   
/*     */   public boolean have_code_flags_hi() {
/* 234 */     return this.have_code_flags_hi;
/*     */   }
/*     */   
/*     */   public void setIc_count(int ic_count) {
/* 238 */     this.ic_count = ic_count;
/*     */   }
/*     */   
/*     */   public void setClass_count(int class_count) {
/* 242 */     this.class_count = class_count;
/*     */   }
/*     */   
/*     */   private void writeCpCounts(OutputStream out) throws IOException, Pack200Exception {
/* 246 */     out.write(encodeScalar(this.cp_Utf8_count, Codec.UNSIGNED5));
/* 247 */     if ((this.archive_options & 0x2) != 0) {
/* 248 */       out.write(encodeScalar(this.cp_Int_count, Codec.UNSIGNED5));
/* 249 */       out.write(encodeScalar(this.cp_Float_count, Codec.UNSIGNED5));
/* 250 */       out.write(encodeScalar(this.cp_Long_count, Codec.UNSIGNED5));
/* 251 */       out.write(encodeScalar(this.cp_Double_count, Codec.UNSIGNED5));
/*     */     } 
/* 253 */     out.write(encodeScalar(this.cp_String_count, Codec.UNSIGNED5));
/* 254 */     out.write(encodeScalar(this.cp_Class_count, Codec.UNSIGNED5));
/* 255 */     out.write(encodeScalar(this.cp_Signature_count, Codec.UNSIGNED5));
/* 256 */     out.write(encodeScalar(this.cp_Descr_count, Codec.UNSIGNED5));
/* 257 */     out.write(encodeScalar(this.cp_Field_count, Codec.UNSIGNED5));
/* 258 */     out.write(encodeScalar(this.cp_Method_count, Codec.UNSIGNED5));
/* 259 */     out.write(encodeScalar(this.cp_Imethod_count, Codec.UNSIGNED5));
/*     */   }
/*     */   
/*     */   private void writeClassCounts(OutputStream out) throws IOException, Pack200Exception {
/* 263 */     int default_class_minver = 0;
/* 264 */     int default_class_majver = this.majverCounter.getMostCommon();
/* 265 */     out.write(encodeScalar(this.ic_count, Codec.UNSIGNED5));
/* 266 */     out.write(encodeScalar(0, Codec.UNSIGNED5));
/* 267 */     out.write(encodeScalar(default_class_majver, Codec.UNSIGNED5));
/* 268 */     out.write(encodeScalar(this.class_count, Codec.UNSIGNED5));
/*     */   }
/*     */   
/*     */   private void writeArchiveSpecialCounts(OutputStream out) throws IOException, Pack200Exception {
/* 272 */     if ((this.archive_options & 0x1) > 0) {
/* 273 */       out.write(encodeScalar(this.band_headers.size(), Codec.UNSIGNED5));
/* 274 */       out.write(encodeScalar(this.attribute_definition_count, Codec.UNSIGNED5));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeArchiveFileCounts(OutputStream out) throws IOException, Pack200Exception {
/* 279 */     if ((this.archive_options & 0x10) > 0) {
/* 280 */       out.write(encodeScalar(this.archive_size_hi, Codec.UNSIGNED5));
/* 281 */       out.write(encodeScalar(this.archive_size_lo, Codec.UNSIGNED5));
/* 282 */       out.write(encodeScalar(this.archive_next_count, Codec.UNSIGNED5));
/* 283 */       out.write(encodeScalar(this.archive_modtime, Codec.UNSIGNED5));
/* 284 */       out.write(encodeScalar(this.file_count, Codec.UNSIGNED5));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addMajorVersion(int major) {
/* 289 */     this.majverCounter.add(major);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class Counter
/*     */   {
/* 297 */     private final int[] objs = new int[8];
/* 298 */     private final int[] counts = new int[8];
/*     */     private int length;
/*     */     
/*     */     public void add(int obj) {
/* 302 */       boolean found = false;
/* 303 */       for (int i = 0; i < this.length; i++) {
/* 304 */         if (this.objs[i] == obj) {
/* 305 */           this.counts[i] = this.counts[i] + 1;
/* 306 */           found = true;
/*     */         } 
/*     */       } 
/* 309 */       if (!found) {
/* 310 */         this.objs[this.length] = obj;
/* 311 */         this.counts[this.length] = 1;
/* 312 */         this.length++;
/* 313 */         if (this.length > this.objs.length - 1) {
/* 314 */           Object[] newArray = new Object[this.objs.length + 8];
/* 315 */           System.arraycopy(this.objs, 0, newArray, 0, this.length);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     public int getMostCommon() {
/* 321 */       int returnIndex = 0;
/* 322 */       for (int i = 0; i < this.length; i++) {
/* 323 */         if (this.counts[i] > this.counts[returnIndex]) {
/* 324 */           returnIndex = i;
/*     */         }
/*     */       } 
/* 327 */       return this.objs[returnIndex];
/*     */     }
/*     */     private Counter() {} }
/*     */   
/*     */   public int getDefaultMajorVersion() {
/* 332 */     return this.majverCounter.getMostCommon();
/*     */   }
/*     */   
/*     */   public boolean have_file_size_hi() {
/* 336 */     return this.have_file_size_hi;
/*     */   }
/*     */   
/*     */   public boolean have_file_modtime() {
/* 340 */     return true;
/*     */   }
/*     */   
/*     */   public boolean have_file_options() {
/* 344 */     return true;
/*     */   }
/*     */   
/*     */   public boolean have_all_code_flags() {
/* 348 */     return this.have_all_code_flags;
/*     */   }
/*     */   
/*     */   public void appendBandCodingSpecifier(int specifier) {
/* 352 */     this.band_headers.add(specifier);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\SegmentHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */