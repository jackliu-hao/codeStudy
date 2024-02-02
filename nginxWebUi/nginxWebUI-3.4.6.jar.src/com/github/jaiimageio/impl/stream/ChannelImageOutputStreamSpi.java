/*     */ package com.github.jaiimageio.impl.stream;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.PackageUtil;
/*     */ import com.github.jaiimageio.stream.FileChannelImageOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.NonReadableChannelException;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.Locale;
/*     */ import javax.imageio.spi.ImageOutputStreamSpi;
/*     */ import javax.imageio.stream.FileCacheImageOutputStream;
/*     */ import javax.imageio.stream.ImageOutputStream;
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
/*     */ public class ChannelImageOutputStreamSpi
/*     */   extends ImageOutputStreamSpi
/*     */ {
/*     */   public ChannelImageOutputStreamSpi() {
/*  66 */     super(PackageUtil.getVendor(), 
/*  67 */         PackageUtil.getVersion(), WritableByteChannel.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ImageOutputStream createOutputStreamInstance(Object output, boolean useCache, File cacheDir) throws IOException {
/*     */     FileChannelImageOutputStream fileChannelImageOutputStream;
/*     */     MemoryCacheImageOutputStream memoryCacheImageOutputStream;
/*  76 */     if (output == null || !(output instanceof WritableByteChannel))
/*     */     {
/*  78 */       throw new IllegalArgumentException("Cannot create ImageOutputStream from " + output
/*     */           
/*  80 */           .getClass().getName());
/*     */     }
/*     */     
/*  83 */     ImageOutputStream stream = null;
/*     */     
/*  85 */     if (output instanceof FileChannel) {
/*  86 */       FileChannel channel = (FileChannel)output;
/*     */       
/*     */       try {
/*  89 */         channel.map(FileChannel.MapMode.READ_ONLY, channel
/*  90 */             .position(), 1L);
/*     */         
/*  92 */         fileChannelImageOutputStream = new FileChannelImageOutputStream((FileChannel)output);
/*  93 */       } catch (NonReadableChannelException nonReadableChannelException) {}
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  98 */     if (fileChannelImageOutputStream == null) {
/*     */       FileCacheImageOutputStream fileCacheImageOutputStream;
/* 100 */       OutputStream outStream = Channels.newOutputStream((WritableByteChannel)output);
/*     */       
/* 102 */       if (useCache) {
/*     */         try {
/* 104 */           fileCacheImageOutputStream = new FileCacheImageOutputStream(outStream, cacheDir);
/*     */         }
/* 106 */         catch (IOException iOException) {}
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 111 */       if (fileCacheImageOutputStream == null) {
/* 112 */         memoryCacheImageOutputStream = new MemoryCacheImageOutputStream(outStream);
/*     */       }
/*     */     } 
/*     */     
/* 116 */     return memoryCacheImageOutputStream;
/*     */   }
/*     */   
/*     */   public String getDescription(Locale locale) {
/* 120 */     return "NIO Channel ImageOutputStream";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\stream\ChannelImageOutputStreamSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */