/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
/*     */ import java.awt.Point;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ComponentColorModel;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.PixelInterleavedSampleModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.imageio.IIOException;
/*     */ import javax.imageio.IIOImage;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.imageio.ImageWriteParam;
/*     */ import javax.imageio.ImageWriter;
/*     */ import javax.imageio.metadata.IIOInvalidTreeException;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.metadata.IIOMetadataNode;
/*     */ import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
/*     */ import javax.imageio.spi.ImageWriterSpi;
/*     */ import javax.imageio.stream.ImageOutputStream;
/*     */ import javax.imageio.stream.MemoryCacheImageOutputStream;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TIFFBaseJPEGCompressor
/*     */   extends TIFFCompressor
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   protected static final String STREAM_METADATA_NAME = "javax_imageio_jpeg_stream_1.0";
/*     */   protected static final String IMAGE_METADATA_NAME = "javax_imageio_jpeg_image_1.0";
/*  99 */   private ImageWriteParam param = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 105 */   protected JPEGImageWriteParam JPEGParam = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 111 */   protected ImageWriter JPEGWriter = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean writeAbbreviatedStream = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 126 */   protected IIOMetadata JPEGStreamMetadata = null;
/*     */ 
/*     */   
/* 129 */   private IIOMetadata JPEGImageMetadata = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean usingCodecLib;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private IIOByteArrayOutputStream baos;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void pruneNodes(Node tree, boolean pruneTables) {
/* 151 */     if (tree == null) {
/* 152 */       throw new IllegalArgumentException("tree == null!");
/*     */     }
/* 154 */     if (!tree.getNodeName().equals("javax_imageio_jpeg_image_1.0")) {
/* 155 */       throw new IllegalArgumentException("root node name is not javax_imageio_jpeg_image_1.0!");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 163 */     List<String> wantedNodes = new ArrayList();
/* 164 */     wantedNodes.addAll(Arrays.asList(new String[] { "JPEGvariety", "markerSequence", "sof", "componentSpec", "sos", "scanComponentSpec" }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 171 */     if (!pruneTables) {
/* 172 */       wantedNodes.add("dht");
/* 173 */       wantedNodes.add("dhtable");
/* 174 */       wantedNodes.add("dqt");
/* 175 */       wantedNodes.add("dqtable");
/*     */     } 
/*     */     
/* 178 */     IIOMetadataNode iioTree = (IIOMetadataNode)tree;
/*     */     
/* 180 */     List<Node> nodes = getAllNodes(iioTree, (List)null);
/* 181 */     int numNodes = nodes.size();
/*     */     
/* 183 */     for (int i = 0; i < numNodes; i++) {
/* 184 */       Node node = nodes.get(i);
/* 185 */       if (!wantedNodes.contains(node.getNodeName()))
/*     */       {
/*     */ 
/*     */         
/* 189 */         node.getParentNode().removeChild(node);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static List getAllNodes(IIOMetadataNode root, List<Node> nodes) {
/* 195 */     if (nodes == null) nodes = new ArrayList();
/*     */     
/* 197 */     if (root.hasChildNodes()) {
/* 198 */       Node sibling = root.getFirstChild();
/* 199 */       while (sibling != null) {
/* 200 */         nodes.add(sibling);
/* 201 */         nodes = getAllNodes((IIOMetadataNode)sibling, nodes);
/* 202 */         sibling = sibling.getNextSibling();
/*     */       } 
/*     */     } 
/*     */     
/* 206 */     return nodes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFBaseJPEGCompressor(String compressionType, int compressionTagValue, boolean isCompressionLossless, ImageWriteParam param) {
/* 213 */     super(compressionType, compressionTagValue, isCompressionLossless);
/*     */     
/* 215 */     this.param = param;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class IIOByteArrayOutputStream
/*     */     extends ByteArrayOutputStream
/*     */   {
/*     */     IIOByteArrayOutputStream() {}
/*     */ 
/*     */ 
/*     */     
/*     */     IIOByteArrayOutputStream(int size) {
/* 228 */       super(size);
/*     */     }
/*     */ 
/*     */     
/*     */     public synchronized void writeTo(ImageOutputStream ios) throws IOException {
/* 233 */       ios.write(this.buf, 0, this.count);
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
/*     */   protected void initJPEGWriter(boolean supportsStreamMetadata, boolean supportsImageMetadata) {
/* 250 */     if (this.JPEGWriter != null && (supportsStreamMetadata || supportsImageMetadata)) {
/*     */       
/* 252 */       ImageWriterSpi spi = this.JPEGWriter.getOriginatingProvider();
/* 253 */       if (supportsStreamMetadata) {
/* 254 */         String smName = spi.getNativeStreamMetadataFormatName();
/* 255 */         if (smName == null || !smName.equals("javax_imageio_jpeg_stream_1.0")) {
/* 256 */           this.JPEGWriter = null;
/*     */         }
/*     */       } 
/* 259 */       if (this.JPEGWriter != null && supportsImageMetadata) {
/* 260 */         String imName = spi.getNativeImageMetadataFormatName();
/* 261 */         if (imName == null || !imName.equals("javax_imageio_jpeg_image_1.0")) {
/* 262 */           this.JPEGWriter = null;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 268 */     if (this.JPEGWriter == null) {
/* 269 */       Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
/*     */       
/* 271 */       while (iter.hasNext()) {
/*     */         
/* 273 */         ImageWriter writer = iter.next();
/*     */ 
/*     */         
/* 276 */         if (supportsStreamMetadata || supportsImageMetadata) {
/* 277 */           ImageWriterSpi spi = writer.getOriginatingProvider();
/* 278 */           if (supportsStreamMetadata) {
/*     */             
/* 280 */             String smName = spi.getNativeStreamMetadataFormatName();
/* 281 */             if (smName == null || 
/* 282 */               !smName.equals("javax_imageio_jpeg_stream_1.0")) {
/*     */               continue;
/*     */             }
/*     */           } 
/*     */           
/* 287 */           if (supportsImageMetadata) {
/*     */             
/* 289 */             String imName = spi.getNativeImageMetadataFormatName();
/* 290 */             if (imName == null || 
/* 291 */               !imName.equals("javax_imageio_jpeg_image_1.0")) {
/*     */               continue;
/*     */             }
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 299 */         this.JPEGWriter = writer;
/*     */       } 
/*     */ 
/*     */       
/* 303 */       if (this.JPEGWriter == null)
/*     */       {
/* 305 */         throw new IllegalStateException("No appropriate JPEG writers found!");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 310 */     this
/* 311 */       .usingCodecLib = this.JPEGWriter.getClass().getName().startsWith("com.sun.media");
/*     */ 
/*     */ 
/*     */     
/* 315 */     if (this.JPEGParam == null) {
/* 316 */       if (this.param != null && this.param instanceof JPEGImageWriteParam) {
/* 317 */         this.JPEGParam = (JPEGImageWriteParam)this.param;
/*     */       } else {
/* 319 */         this
/*     */           
/* 321 */           .JPEGParam = new JPEGImageWriteParam((this.writer != null) ? this.writer.getLocale() : null);
/* 322 */         if (this.param.getCompressionMode() == 2) {
/*     */           
/* 324 */           this.JPEGParam.setCompressionMode(2);
/* 325 */           this.JPEGParam.setCompressionQuality(this.param.getCompressionQuality());
/*     */         } 
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
/*     */   private IIOMetadata getImageMetadata(boolean pruneTables) throws IIOException {
/* 339 */     if (this.JPEGImageMetadata == null && "javax_imageio_jpeg_image_1.0"
/* 340 */       .equals(this.JPEGWriter.getOriginatingProvider().getNativeImageMetadataFormatName())) {
/* 341 */       TIFFImageWriter tiffWriter = (TIFFImageWriter)this.writer;
/*     */ 
/*     */       
/* 344 */       this
/* 345 */         .JPEGImageMetadata = this.JPEGWriter.getDefaultImageMetadata(tiffWriter.imageType, this.JPEGParam);
/*     */ 
/*     */ 
/*     */       
/* 349 */       Node tree = this.JPEGImageMetadata.getAsTree("javax_imageio_jpeg_image_1.0");
/*     */ 
/*     */       
/*     */       try {
/* 353 */         pruneNodes(tree, pruneTables);
/* 354 */       } catch (IllegalArgumentException e) {
/* 355 */         throw new IIOException("Error pruning unwanted nodes", e);
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 360 */         this.JPEGImageMetadata.setFromTree("javax_imageio_jpeg_image_1.0", tree);
/* 361 */       } catch (IIOInvalidTreeException e) {
/*     */ 
/*     */ 
/*     */         
/* 365 */         throw new IIOException("Cannot set pruned image metadata!", e);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 370 */     return this.JPEGImageMetadata; } public final int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
/*     */     ImageOutputStream ios;
/*     */     long initialStreamPosition;
/*     */     DataBufferByte dbb;
/*     */     int[] offsets;
/*     */     ColorSpace cs;
/*     */     int compDataLength;
/* 377 */     if (this.JPEGWriter == null) {
/* 378 */       throw new IIOException("JPEG writer has not been initialized!");
/*     */     }
/*     */     
/* 381 */     if ((bitsPerSample.length != 3 || bitsPerSample[0] != 8 || bitsPerSample[1] != 8 || bitsPerSample[2] != 8) && (bitsPerSample.length != 1 || bitsPerSample[0] != 8))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 387 */       throw new IIOException("Can only JPEG compress 8- and 24-bit images!");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 394 */     if (this.usingCodecLib && !this.writeAbbreviatedStream) {
/* 395 */       ios = this.stream;
/* 396 */       initialStreamPosition = this.stream.getStreamPosition();
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 403 */       if (this.baos == null) {
/* 404 */         this.baos = new IIOByteArrayOutputStream();
/*     */       } else {
/* 406 */         this.baos.reset();
/*     */       } 
/* 408 */       ios = new MemoryCacheImageOutputStream(this.baos);
/* 409 */       initialStreamPosition = 0L;
/*     */     } 
/* 411 */     this.JPEGWriter.setOutput(ios);
/*     */ 
/*     */ 
/*     */     
/* 415 */     if (off == 0 || this.usingCodecLib) {
/* 416 */       dbb = new DataBufferByte(b, b.length);
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 422 */       int bytesPerSegment = scanlineStride * height;
/* 423 */       byte[] btmp = new byte[bytesPerSegment];
/* 424 */       System.arraycopy(b, off, btmp, 0, bytesPerSegment);
/* 425 */       dbb = new DataBufferByte(btmp, bytesPerSegment);
/* 426 */       off = 0;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 432 */     if (bitsPerSample.length == 3) {
/* 433 */       offsets = new int[] { off, off + 1, off + 2 };
/* 434 */       cs = ColorSpace.getInstance(1000);
/*     */     } else {
/* 436 */       offsets = new int[] { off };
/* 437 */       cs = ColorSpace.getInstance(1003);
/*     */     } 
/*     */ 
/*     */     
/* 441 */     ColorModel cm = new ComponentColorModel(cs, false, false, 1, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 448 */     SampleModel sm = new PixelInterleavedSampleModel(0, width, height, bitsPerSample.length, scanlineStride, offsets);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 457 */     WritableRaster wras = Raster.createWritableRaster(sm, dbb, new Point(0, 0));
/*     */ 
/*     */     
/* 460 */     BufferedImage bi = new BufferedImage(cm, wras, false, null);
/*     */ 
/*     */     
/* 463 */     IIOMetadata imageMetadata = getImageMetadata(this.writeAbbreviatedStream);
/*     */ 
/*     */ 
/*     */     
/* 467 */     if (this.usingCodecLib && !this.writeAbbreviatedStream) {
/*     */       
/* 469 */       this.JPEGWriter.write(null, new IIOImage(bi, null, imageMetadata), this.JPEGParam);
/*     */ 
/*     */ 
/*     */       
/* 473 */       compDataLength = (int)(this.stream.getStreamPosition() - initialStreamPosition);
/*     */     } else {
/* 475 */       if (this.writeAbbreviatedStream) {
/*     */ 
/*     */ 
/*     */         
/* 479 */         this.JPEGWriter.prepareWriteSequence(this.JPEGStreamMetadata);
/* 480 */         ios.flush();
/*     */ 
/*     */         
/* 483 */         this.baos.reset();
/*     */ 
/*     */         
/* 486 */         IIOImage image = new IIOImage(bi, null, imageMetadata);
/* 487 */         this.JPEGWriter.writeToSequence(image, this.JPEGParam);
/* 488 */         this.JPEGWriter.endWriteSequence();
/*     */       } else {
/*     */         
/* 491 */         this.JPEGWriter.write(null, new IIOImage(bi, null, imageMetadata), this.JPEGParam);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 496 */       compDataLength = this.baos.size();
/* 497 */       this.baos.writeTo(this.stream);
/* 498 */       this.baos.reset();
/*     */     } 
/*     */     
/* 501 */     return compDataLength;
/*     */   }
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 505 */     super.finalize();
/* 506 */     if (this.JPEGWriter != null)
/* 507 */       this.JPEGWriter.dispose(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFBaseJPEGCompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */