/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.util.Iterator;
/*     */ import javax.imageio.ImageReader;
/*     */ import javax.imageio.ImageWriteParam;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.spi.IIORegistry;
/*     */ import javax.imageio.spi.ImageReaderSpi;
/*     */ import javax.imageio.spi.ServiceRegistry;
/*     */ import javax.imageio.stream.MemoryCacheImageInputStream;
/*     */ import javax.imageio.stream.MemoryCacheImageOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFJPEGCompressor
/*     */   extends TIFFBaseJPEGCompressor
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   static final int CHROMA_SUBSAMPLING = 2;
/*     */   
/*     */   private static class JPEGSPIFilter
/*     */     implements ServiceRegistry.Filter
/*     */   {
/*     */     public boolean filter(Object provider) {
/*  82 */       ImageReaderSpi readerSPI = (ImageReaderSpi)provider;
/*     */       
/*  84 */       if (readerSPI != null) {
/*     */         
/*  86 */         String streamMetadataName = readerSPI.getNativeStreamMetadataFormatName();
/*  87 */         if (streamMetadataName != null) {
/*  88 */           return streamMetadataName.equals("javax_imageio_jpeg_stream_1.0");
/*     */         }
/*  90 */         return false;
/*     */       } 
/*     */ 
/*     */       
/*  94 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ImageReader getJPEGTablesReader() {
/* 102 */     ImageReader jpegReader = null;
/*     */     
/*     */     try {
/* 105 */       IIORegistry registry = IIORegistry.getDefaultInstance();
/*     */       
/* 107 */       Class<?> imageReaderClass = Class.forName("javax.imageio.spi.ImageReaderSpi");
/*     */       
/* 109 */       Iterator<?> readerSPIs = registry.getServiceProviders(imageReaderClass, new JPEGSPIFilter(), true);
/*     */ 
/*     */       
/* 112 */       if (readerSPIs.hasNext()) {
/*     */         
/* 114 */         ImageReaderSpi jpegReaderSPI = (ImageReaderSpi)readerSPIs.next();
/* 115 */         jpegReader = jpegReaderSPI.createReaderInstance();
/*     */       } 
/* 117 */     } catch (Exception exception) {}
/*     */ 
/*     */ 
/*     */     
/* 121 */     return jpegReader;
/*     */   }
/*     */   
/*     */   public TIFFJPEGCompressor(ImageWriteParam param) {
/* 125 */     super("JPEG", 7, false, param);
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
/*     */   public void setMetadata(IIOMetadata metadata) {
/* 142 */     super.setMetadata(metadata);
/*     */     
/* 144 */     if (metadata instanceof TIFFImageMetadata) {
/* 145 */       TIFFImageMetadata tim = (TIFFImageMetadata)metadata;
/* 146 */       TIFFIFD rootIFD = tim.getRootIFD();
/* 147 */       BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
/*     */ 
/*     */       
/* 150 */       TIFFField f = tim.getTIFFField(277);
/* 151 */       int numBands = f.getAsInt(0);
/*     */       
/* 153 */       if (numBands == 1) {
/*     */ 
/*     */         
/* 156 */         rootIFD.removeTIFFField(530);
/* 157 */         rootIFD.removeTIFFField(531);
/* 158 */         rootIFD.removeTIFFField(532);
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 164 */         TIFFField YCbCrSubSamplingField = new TIFFField(base.getTag(530), 3, 2, new char[] { '\002', '\002' });
/*     */ 
/*     */         
/* 167 */         rootIFD.addTIFFField(YCbCrSubSamplingField);
/*     */ 
/*     */ 
/*     */         
/* 171 */         TIFFField YCbCrPositioningField = new TIFFField(base.getTag(531), 3, 1, new char[] { '\001' });
/*     */ 
/*     */ 
/*     */         
/* 175 */         rootIFD.addTIFFField(YCbCrPositioningField);
/*     */ 
/*     */ 
/*     */         
/* 179 */         TIFFField referenceBlackWhiteField = new TIFFField(base.getTag(532), 5, 6, new long[][] { { 0L, 1L }, { 255L, 1L }, { 128L, 1L }, { 255L, 1L }, { 128L, 1L }, { 255L, 1L } });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 186 */         rootIFD.addTIFFField(referenceBlackWhiteField);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 198 */       TIFFField JPEGTablesField = tim.getTIFFField(347);
/*     */ 
/*     */       
/* 201 */       if (JPEGTablesField != null)
/*     */       {
/*     */ 
/*     */         
/* 205 */         initJPEGWriter(true, false);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 210 */       if (JPEGTablesField != null && this.JPEGWriter != null) {
/*     */ 
/*     */ 
/*     */         
/* 214 */         this.writeAbbreviatedStream = true;
/*     */ 
/*     */         
/* 217 */         if (JPEGTablesField.getCount() > 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 223 */           byte[] tables = JPEGTablesField.getAsBytes();
/*     */ 
/*     */           
/* 226 */           ByteArrayInputStream bais = new ByteArrayInputStream(tables);
/*     */           
/* 228 */           MemoryCacheImageInputStream iis = new MemoryCacheImageInputStream(bais);
/*     */ 
/*     */ 
/*     */           
/* 232 */           ImageReader jpegReader = getJPEGTablesReader();
/* 233 */           jpegReader.setInput(iis);
/*     */ 
/*     */           
/*     */           try {
/* 237 */             this.JPEGStreamMetadata = jpegReader.getStreamMetadata();
/* 238 */           } catch (Exception e) {
/*     */             
/* 240 */             this.JPEGStreamMetadata = null;
/*     */           } finally {
/* 242 */             jpegReader.reset();
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 247 */         if (this.JPEGStreamMetadata == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 253 */           this
/* 254 */             .JPEGStreamMetadata = this.JPEGWriter.getDefaultStreamMetadata(this.JPEGParam);
/*     */ 
/*     */           
/* 257 */           ByteArrayOutputStream tableByteStream = new ByteArrayOutputStream();
/*     */           
/* 259 */           MemoryCacheImageOutputStream tableStream = new MemoryCacheImageOutputStream(tableByteStream);
/*     */ 
/*     */ 
/*     */           
/* 263 */           this.JPEGWriter.setOutput(tableStream);
/*     */           try {
/* 265 */             this.JPEGWriter.prepareWriteSequence(this.JPEGStreamMetadata);
/* 266 */             tableStream.flush();
/* 267 */             this.JPEGWriter.endWriteSequence();
/*     */ 
/*     */             
/* 270 */             byte[] tables = tableByteStream.toByteArray();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 276 */             JPEGTablesField = new TIFFField(base.getTag(347), 7, tables.length, tables);
/*     */ 
/*     */ 
/*     */             
/* 280 */             rootIFD.addTIFFField(JPEGTablesField);
/* 281 */           } catch (Exception e) {
/*     */             
/* 283 */             rootIFD.removeTIFFField(347);
/* 284 */             this.writeAbbreviatedStream = false;
/*     */           } 
/*     */         } 
/*     */       } else {
/*     */         
/* 289 */         rootIFD.removeTIFFField(347);
/*     */ 
/*     */         
/* 292 */         initJPEGWriter(false, false);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFJPEGCompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */