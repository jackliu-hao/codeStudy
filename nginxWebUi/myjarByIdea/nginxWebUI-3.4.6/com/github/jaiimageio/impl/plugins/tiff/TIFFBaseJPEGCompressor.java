package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.w3c.dom.Node;

public abstract class TIFFBaseJPEGCompressor extends TIFFCompressor {
   private static final boolean DEBUG = false;
   protected static final String STREAM_METADATA_NAME = "javax_imageio_jpeg_stream_1.0";
   protected static final String IMAGE_METADATA_NAME = "javax_imageio_jpeg_image_1.0";
   private ImageWriteParam param = null;
   protected JPEGImageWriteParam JPEGParam = null;
   protected ImageWriter JPEGWriter = null;
   protected boolean writeAbbreviatedStream = false;
   protected IIOMetadata JPEGStreamMetadata = null;
   private IIOMetadata JPEGImageMetadata = null;
   private boolean usingCodecLib;
   private IIOByteArrayOutputStream baos;

   private static void pruneNodes(Node tree, boolean pruneTables) {
      if (tree == null) {
         throw new IllegalArgumentException("tree == null!");
      } else if (!tree.getNodeName().equals("javax_imageio_jpeg_image_1.0")) {
         throw new IllegalArgumentException("root node name is not javax_imageio_jpeg_image_1.0!");
      } else {
         List wantedNodes = new ArrayList();
         wantedNodes.addAll(Arrays.asList("JPEGvariety", "markerSequence", "sof", "componentSpec", "sos", "scanComponentSpec"));
         if (!pruneTables) {
            wantedNodes.add("dht");
            wantedNodes.add("dhtable");
            wantedNodes.add("dqt");
            wantedNodes.add("dqtable");
         }

         IIOMetadataNode iioTree = (IIOMetadataNode)tree;
         List nodes = getAllNodes(iioTree, (List)null);
         int numNodes = nodes.size();

         for(int i = 0; i < numNodes; ++i) {
            Node node = (Node)nodes.get(i);
            if (!wantedNodes.contains(node.getNodeName())) {
               node.getParentNode().removeChild(node);
            }
         }

      }
   }

   private static List getAllNodes(IIOMetadataNode root, List nodes) {
      if (nodes == null) {
         nodes = new ArrayList();
      }

      if (root.hasChildNodes()) {
         for(Node sibling = root.getFirstChild(); sibling != null; sibling = sibling.getNextSibling()) {
            ((List)nodes).add(sibling);
            nodes = getAllNodes((IIOMetadataNode)sibling, (List)nodes);
         }
      }

      return (List)nodes;
   }

   public TIFFBaseJPEGCompressor(String compressionType, int compressionTagValue, boolean isCompressionLossless, ImageWriteParam param) {
      super(compressionType, compressionTagValue, isCompressionLossless);
      this.param = param;
   }

   protected void initJPEGWriter(boolean supportsStreamMetadata, boolean supportsImageMetadata) {
      if (this.JPEGWriter != null && (supportsStreamMetadata || supportsImageMetadata)) {
         ImageWriterSpi spi = this.JPEGWriter.getOriginatingProvider();
         String imName;
         if (supportsStreamMetadata) {
            imName = spi.getNativeStreamMetadataFormatName();
            if (imName == null || !imName.equals("javax_imageio_jpeg_stream_1.0")) {
               this.JPEGWriter = null;
            }
         }

         if (this.JPEGWriter != null && supportsImageMetadata) {
            imName = spi.getNativeImageMetadataFormatName();
            if (imName == null || !imName.equals("javax_imageio_jpeg_image_1.0")) {
               this.JPEGWriter = null;
            }
         }
      }

      if (this.JPEGWriter == null) {
         Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");

         label70: {
            ImageWriter writer;
            while(true) {
               if (!iter.hasNext()) {
                  break label70;
               }

               writer = (ImageWriter)iter.next();
               if (!supportsStreamMetadata && !supportsImageMetadata) {
                  break;
               }

               ImageWriterSpi spi = writer.getOriginatingProvider();
               String imName;
               if (supportsStreamMetadata) {
                  imName = spi.getNativeStreamMetadataFormatName();
                  if (imName == null || !imName.equals("javax_imageio_jpeg_stream_1.0")) {
                     continue;
                  }
               }

               if (!supportsImageMetadata) {
                  break;
               }

               imName = spi.getNativeImageMetadataFormatName();
               if (imName != null && imName.equals("javax_imageio_jpeg_image_1.0")) {
                  break;
               }
            }

            this.JPEGWriter = writer;
         }

         if (this.JPEGWriter == null) {
            throw new IllegalStateException("No appropriate JPEG writers found!");
         }
      }

      this.usingCodecLib = this.JPEGWriter.getClass().getName().startsWith("com.sun.media");
      if (this.JPEGParam == null) {
         if (this.param != null && this.param instanceof JPEGImageWriteParam) {
            this.JPEGParam = (JPEGImageWriteParam)this.param;
         } else {
            this.JPEGParam = new JPEGImageWriteParam(this.writer != null ? this.writer.getLocale() : null);
            if (this.param.getCompressionMode() == 2) {
               this.JPEGParam.setCompressionMode(2);
               this.JPEGParam.setCompressionQuality(this.param.getCompressionQuality());
            }
         }
      }

   }

   private IIOMetadata getImageMetadata(boolean pruneTables) throws IIOException {
      if (this.JPEGImageMetadata == null && "javax_imageio_jpeg_image_1.0".equals(this.JPEGWriter.getOriginatingProvider().getNativeImageMetadataFormatName())) {
         TIFFImageWriter tiffWriter = (TIFFImageWriter)this.writer;
         this.JPEGImageMetadata = this.JPEGWriter.getDefaultImageMetadata(tiffWriter.imageType, this.JPEGParam);
         Node tree = this.JPEGImageMetadata.getAsTree("javax_imageio_jpeg_image_1.0");

         try {
            pruneNodes(tree, pruneTables);
         } catch (IllegalArgumentException var6) {
            throw new IIOException("Error pruning unwanted nodes", var6);
         }

         try {
            this.JPEGImageMetadata.setFromTree("javax_imageio_jpeg_image_1.0", tree);
         } catch (IIOInvalidTreeException var5) {
            throw new IIOException("Cannot set pruned image metadata!", var5);
         }
      }

      return this.JPEGImageMetadata;
   }

   public final int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
      if (this.JPEGWriter == null) {
         throw new IIOException("JPEG writer has not been initialized!");
      } else if ((bitsPerSample.length != 3 || bitsPerSample[0] != 8 || bitsPerSample[1] != 8 || bitsPerSample[2] != 8) && (bitsPerSample.length != 1 || bitsPerSample[0] != 8)) {
         throw new IIOException("Can only JPEG compress 8- and 24-bit images!");
      } else {
         Object ios;
         long initialStreamPosition;
         if (this.usingCodecLib && !this.writeAbbreviatedStream) {
            ios = this.stream;
            initialStreamPosition = this.stream.getStreamPosition();
         } else {
            if (this.baos == null) {
               this.baos = new IIOByteArrayOutputStream();
            } else {
               this.baos.reset();
            }

            ios = new MemoryCacheImageOutputStream(this.baos);
            initialStreamPosition = 0L;
         }

         this.JPEGWriter.setOutput(ios);
         DataBufferByte dbb;
         if (off != 0 && !this.usingCodecLib) {
            int bytesPerSegment = scanlineStride * height;
            byte[] btmp = new byte[bytesPerSegment];
            System.arraycopy(b, off, btmp, 0, bytesPerSegment);
            dbb = new DataBufferByte(btmp, bytesPerSegment);
            off = 0;
         } else {
            dbb = new DataBufferByte(b, b.length);
         }

         int[] offsets;
         ColorSpace cs;
         if (bitsPerSample.length == 3) {
            offsets = new int[]{off, off + 1, off + 2};
            cs = ColorSpace.getInstance(1000);
         } else {
            offsets = new int[]{off};
            cs = ColorSpace.getInstance(1003);
         }

         ColorModel cm = new ComponentColorModel(cs, false, false, 1, 0);
         SampleModel sm = new PixelInterleavedSampleModel(0, width, height, bitsPerSample.length, scanlineStride, offsets);
         WritableRaster wras = Raster.createWritableRaster(sm, dbb, new Point(0, 0));
         BufferedImage bi = new BufferedImage(cm, wras, false, (Hashtable)null);
         IIOMetadata imageMetadata = this.getImageMetadata(this.writeAbbreviatedStream);
         int compDataLength;
         if (this.usingCodecLib && !this.writeAbbreviatedStream) {
            this.JPEGWriter.write((IIOMetadata)null, new IIOImage(bi, (List)null, imageMetadata), this.JPEGParam);
            compDataLength = (int)(this.stream.getStreamPosition() - initialStreamPosition);
         } else {
            if (this.writeAbbreviatedStream) {
               this.JPEGWriter.prepareWriteSequence(this.JPEGStreamMetadata);
               ((ImageOutputStream)ios).flush();
               this.baos.reset();
               IIOImage image = new IIOImage(bi, (List)null, imageMetadata);
               this.JPEGWriter.writeToSequence(image, this.JPEGParam);
               this.JPEGWriter.endWriteSequence();
            } else {
               this.JPEGWriter.write((IIOMetadata)null, new IIOImage(bi, (List)null, imageMetadata), this.JPEGParam);
            }

            compDataLength = this.baos.size();
            this.baos.writeTo(this.stream);
            this.baos.reset();
         }

         return compDataLength;
      }
   }

   protected void finalize() throws Throwable {
      super.finalize();
      if (this.JPEGWriter != null) {
         this.JPEGWriter.dispose();
      }

   }

   private static class IIOByteArrayOutputStream extends ByteArrayOutputStream {
      IIOByteArrayOutputStream() {
      }

      IIOByteArrayOutputStream(int size) {
         super(size);
      }

      public synchronized void writeTo(ImageOutputStream ios) throws IOException {
         ios.write(this.buf, 0, this.count);
      }
   }
}
