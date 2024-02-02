/*     */ package com.github.jaiimageio.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriter;
/*     */ import java.util.Locale;
/*     */ import javax.imageio.ImageWriteParam;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFImageWriteParam
/*     */   extends ImageWriteParam
/*     */ {
/* 166 */   TIFFCompressor compressor = null;
/*     */   
/* 168 */   TIFFColorConverter colorConverter = null;
/*     */ 
/*     */ 
/*     */   
/*     */   int photometricInterpretation;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean appendedCompressionType = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFImageWriteParam(Locale locale) {
/* 181 */     super(locale);
/* 182 */     this.canWriteCompressed = true;
/* 183 */     this.canWriteTiles = true;
/* 184 */     this.compressionTypes = TIFFImageWriter.TIFFCompressionTypes;
/*     */   }
/*     */   
/*     */   public boolean isCompressionLossless() {
/* 188 */     if (getCompressionMode() != 2) {
/* 189 */       throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
/*     */     }
/*     */ 
/*     */     
/* 193 */     if (this.compressionType == null) {
/* 194 */       throw new IllegalStateException("No compression type set!");
/*     */     }
/*     */     
/* 197 */     if (this.compressor != null && this.compressionType
/* 198 */       .equals(this.compressor.getCompressionType())) {
/* 199 */       return this.compressor.isCompressionLossless();
/*     */     }
/*     */     
/* 202 */     for (int i = 0; i < this.compressionTypes.length; i++) {
/* 203 */       if (this.compressionType.equals(this.compressionTypes[i])) {
/* 204 */         return TIFFImageWriter.isCompressionLossless[i];
/*     */       }
/*     */     } 
/*     */     
/* 208 */     return false;
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
/*     */   public void setTIFFCompressor(TIFFCompressor compressor) {
/* 252 */     if (getCompressionMode() != 2) {
/* 253 */       throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
/*     */     }
/*     */ 
/*     */     
/* 257 */     this.compressor = compressor;
/*     */     
/* 259 */     if (this.appendedCompressionType) {
/*     */       
/* 261 */       int len = this.compressionTypes.length - 1;
/* 262 */       String[] types = new String[len];
/* 263 */       System.arraycopy(this.compressionTypes, 0, types, 0, len);
/* 264 */       this.compressionTypes = types;
/* 265 */       this.appendedCompressionType = false;
/*     */     } 
/*     */     
/* 268 */     if (compressor != null) {
/*     */       
/* 270 */       String compressorType = compressor.getCompressionType();
/* 271 */       int len = this.compressionTypes.length;
/* 272 */       boolean appendCompressionType = true;
/* 273 */       for (int i = 0; i < len; i++) {
/* 274 */         if (compressorType.equals(this.compressionTypes[i])) {
/* 275 */           appendCompressionType = false;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 280 */       if (appendCompressionType) {
/*     */         
/* 282 */         String[] types = new String[len + 1];
/* 283 */         System.arraycopy(this.compressionTypes, 0, types, 0, len);
/* 284 */         types[len] = compressorType;
/* 285 */         this.compressionTypes = types;
/* 286 */         this.appendedCompressionType = true;
/*     */       } 
/*     */     } 
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
/*     */   public TIFFCompressor getTIFFCompressor() {
/* 306 */     if (getCompressionMode() != 2) {
/* 307 */       throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
/*     */     }
/*     */     
/* 310 */     return this.compressor;
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
/*     */   public void setColorConverter(TIFFColorConverter colorConverter, int photometricInterpretation) {
/* 330 */     this.colorConverter = colorConverter;
/* 331 */     this.photometricInterpretation = photometricInterpretation;
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
/*     */   public TIFFColorConverter getColorConverter() {
/* 345 */     return this.colorConverter;
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
/*     */   public int getPhotometricInterpretation() {
/* 362 */     if (this.colorConverter == null) {
/* 363 */       throw new IllegalStateException("Color converter not set!");
/*     */     }
/* 365 */     return this.photometricInterpretation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unsetColorConverter() {
/* 375 */     this.colorConverter = null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\TIFFImageWriteParam.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */