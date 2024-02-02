/*     */ package com.github.jaiimageio.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriter;
/*     */ import java.io.IOException;
/*     */ import javax.imageio.ImageWriter;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.stream.ImageOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TIFFCompressor
/*     */ {
/*     */   protected ImageWriter writer;
/*     */   protected IIOMetadata metadata;
/*     */   protected String compressionType;
/*     */   protected int compressionTagValue;
/*     */   protected boolean isCompressionLossless;
/*     */   protected ImageOutputStream stream;
/*     */   
/*     */   public TIFFCompressor(String compressionType, int compressionTagValue, boolean isCompressionLossless) {
/* 124 */     if (compressionType == null)
/* 125 */       throw new IllegalArgumentException("compressionType == null"); 
/* 126 */     if (compressionTagValue < 1) {
/* 127 */       throw new IllegalArgumentException("compressionTagValue < 1");
/*     */     }
/*     */ 
/*     */     
/* 131 */     this.compressionType = compressionType;
/*     */ 
/*     */ 
/*     */     
/* 135 */     int compressionIndex = -1;
/* 136 */     String[] compressionTypes = TIFFImageWriter.compressionTypes;
/* 137 */     int len = compressionTypes.length;
/* 138 */     for (int i = 0; i < len; i++) {
/* 139 */       if (compressionTypes[i].equals(compressionType)) {
/*     */         
/* 141 */         compressionIndex = i;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 146 */     if (compressionIndex != -1) {
/*     */       
/* 148 */       this.compressionTagValue = TIFFImageWriter.compressionNumbers[compressionIndex];
/*     */       
/* 150 */       this.isCompressionLossless = TIFFImageWriter.isCompressionLossless[compressionIndex];
/*     */     }
/*     */     else {
/*     */       
/* 154 */       this.compressionTagValue = compressionTagValue;
/* 155 */       this.isCompressionLossless = isCompressionLossless;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCompressionType() {
/* 165 */     return this.compressionType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCompressionTagValue() {
/* 175 */     return this.compressionTagValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCompressionLossless() {
/* 184 */     return this.isCompressionLossless;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStream(ImageOutputStream stream) {
/* 195 */     this.stream = stream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ImageOutputStream getStream() {
/* 206 */     return this.stream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriter(ImageWriter writer) {
/* 217 */     this.writer = writer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ImageWriter getWriter() {
/* 228 */     return this.writer;
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
/*     */   public void setMetadata(IIOMetadata metadata) {
/* 240 */     this.metadata = metadata;
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
/*     */   public IIOMetadata getMetadata() {
/* 252 */     return this.metadata;
/*     */   }
/*     */   
/*     */   public abstract int encode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfint, int paramInt4) throws IOException;
/*     */   
/*     */   public void dispose() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\TIFFCompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */