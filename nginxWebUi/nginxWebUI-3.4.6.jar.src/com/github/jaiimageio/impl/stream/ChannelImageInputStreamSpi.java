/*     */ package com.github.jaiimageio.impl.stream;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.PackageUtil;
/*     */ import com.github.jaiimageio.stream.FileChannelImageInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.util.Locale;
/*     */ import javax.imageio.spi.ImageInputStreamSpi;
/*     */ import javax.imageio.stream.FileCacheImageInputStream;
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
/*     */ public class ChannelImageInputStreamSpi
/*     */   extends ImageInputStreamSpi
/*     */ {
/*     */   public ChannelImageInputStreamSpi() {
/*  65 */     super(PackageUtil.getVendor(), 
/*  66 */         PackageUtil.getVersion(), ReadableByteChannel.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ImageInputStream createInputStreamInstance(Object input, boolean useCache, File cacheDir) throws IOException {
/*  75 */     if (input == null || !(input instanceof ReadableByteChannel))
/*     */     {
/*  77 */       throw new IllegalArgumentException("XXX");
/*     */     }
/*     */     
/*  80 */     ImageInputStream stream = null;
/*     */     
/*  82 */     if (input instanceof FileChannel) {
/*  83 */       FileChannelImageInputStream fileChannelImageInputStream = new FileChannelImageInputStream((FileChannel)input);
/*     */     } else {
/*     */       
/*  86 */       InputStream inStream = Channels.newInputStream((ReadableByteChannel)input);
/*     */       
/*  88 */       if (useCache) {
/*     */         try {
/*  90 */           stream = new FileCacheImageInputStream(inStream, cacheDir);
/*     */         }
/*  92 */         catch (IOException iOException) {}
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*  97 */       if (stream == null) {
/*  98 */         stream = new MemoryCacheImageInputStream(inStream);
/*     */       }
/*     */     } 
/*     */     
/* 102 */     return stream;
/*     */   }
/*     */   
/*     */   public String getDescription(Locale locale) {
/* 106 */     return "NIO Channel ImageInputStream";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\stream\ChannelImageInputStreamSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */