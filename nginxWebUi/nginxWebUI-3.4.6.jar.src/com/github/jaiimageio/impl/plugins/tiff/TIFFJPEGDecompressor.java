/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.imageio.ImageReadParam;
/*     */ import javax.imageio.ImageReader;
/*     */ import javax.imageio.stream.ImageInputStream;
/*     */ import javax.imageio.stream.MemoryCacheImageInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFJPEGDecompressor
/*     */   extends TIFFDecompressor
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   protected static final int SOI = 216;
/*     */   protected static final int EOI = 217;
/*  72 */   protected ImageReader JPEGReader = null;
/*     */   
/*     */   protected ImageReadParam JPEGParam;
/*     */   protected boolean hasJPEGTables = false;
/*  76 */   protected byte[] tables = null;
/*     */   
/*  78 */   private byte[] data = new byte[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beginDecoding() {
/* 125 */     if (this.JPEGReader == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 140 */       Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("jpeg");
/*     */       
/* 142 */       if (!iter.hasNext())
/*     */       {
/* 144 */         throw new IllegalStateException("No JPEG readers found!");
/*     */       }
/*     */ 
/*     */       
/* 148 */       this.JPEGReader = iter.next();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 153 */       this.JPEGParam = this.JPEGReader.getDefaultReadParam();
/*     */     } 
/*     */ 
/*     */     
/* 157 */     TIFFImageMetadata tmetadata = (TIFFImageMetadata)this.metadata;
/*     */     
/* 159 */     TIFFField f = tmetadata.getTIFFField(347);
/*     */     
/* 161 */     if (f != null) {
/* 162 */       this.hasJPEGTables = true;
/* 163 */       this.tables = f.getAsBytes();
/*     */     } else {
/* 165 */       this.hasJPEGTables = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
/*     */     ImageInputStream is;
/* 174 */     this.stream.seek(this.offset);
/*     */ 
/*     */ 
/*     */     
/* 178 */     if (this.hasJPEGTables) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 183 */       int dataLength = this.tables.length + this.byteCount;
/* 184 */       if (this.data.length < dataLength) {
/* 185 */         this.data = new byte[dataLength];
/*     */       }
/*     */ 
/*     */       
/* 189 */       int dataOffset = this.tables.length;
/* 190 */       for (int i = this.tables.length - 2; i > 0; i--) {
/* 191 */         if ((this.tables[i] & 0xFF) == 255 && (this.tables[i + 1] & 0xFF) == 217) {
/*     */           
/* 193 */           dataOffset = i;
/*     */           break;
/*     */         } 
/*     */       } 
/* 197 */       System.arraycopy(this.tables, 0, this.data, 0, dataOffset);
/*     */ 
/*     */       
/* 200 */       byte byte1 = (byte)this.stream.read();
/* 201 */       byte byte2 = (byte)this.stream.read();
/* 202 */       if ((byte1 & 0xFF) != 255 || (byte2 & 0xFF) != 216) {
/* 203 */         this.data[dataOffset++] = byte1;
/* 204 */         this.data[dataOffset++] = byte2;
/*     */       } 
/*     */ 
/*     */       
/* 208 */       this.stream.readFully(this.data, dataOffset, this.byteCount - 2);
/*     */ 
/*     */       
/* 211 */       ByteArrayInputStream bais = new ByteArrayInputStream(this.data);
/* 212 */       is = new MemoryCacheImageInputStream(bais);
/*     */     }
/*     */     else {
/*     */       
/* 216 */       is = this.stream;
/*     */     } 
/*     */ 
/*     */     
/* 220 */     this.JPEGReader.setInput(is, false, true);
/*     */ 
/*     */     
/* 223 */     this.JPEGParam.setDestination(this.rawImage);
/*     */ 
/*     */     
/* 226 */     this.JPEGReader.read(0, this.JPEGParam);
/*     */   }
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 230 */     super.finalize();
/* 231 */     this.JPEGReader.dispose();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFJPEGDecompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */