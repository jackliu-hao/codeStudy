package com.github.jaiimageio.impl.plugins.gif;

import com.github.jaiimageio.impl.common.LZWCompressor;
import com.github.jaiimageio.impl.common.PaletteBuilder;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GIFImageWriter extends ImageWriter {
   private static final boolean DEBUG = false;
   static final String STANDARD_METADATA_NAME = "javax_imageio_1.0";
   static final String STREAM_METADATA_NAME = "javax_imageio_gif_stream_1.0";
   static final String IMAGE_METADATA_NAME = "javax_imageio_gif_image_1.0";
   private ImageOutputStream stream = null;
   private boolean isWritingSequence = false;
   private boolean wroteSequenceHeader = false;
   private GIFWritableStreamMetadata theStreamMetadata = null;
   private int imageIndex = 0;

   private static int getNumBits(int value) throws IOException {
      byte numBits;
      switch (value) {
         case 2:
            numBits = 1;
            break;
         case 4:
            numBits = 2;
            break;
         case 8:
            numBits = 3;
            break;
         case 16:
            numBits = 4;
            break;
         case 32:
            numBits = 5;
            break;
         case 64:
            numBits = 6;
            break;
         case 128:
            numBits = 7;
            break;
         case 256:
            numBits = 8;
            break;
         default:
            throw new IOException("Bad palette length: " + value + "!");
      }

      return numBits;
   }

   private static void computeRegions(Rectangle sourceBounds, Dimension destSize, ImageWriteParam p) {
      int periodX = 1;
      int periodY = 1;
      if (p != null) {
         int[] sourceBands = p.getSourceBands();
         if (sourceBands != null && (sourceBands.length != 1 || sourceBands[0] != 0)) {
            throw new IllegalArgumentException("Cannot sub-band image!");
         }

         Rectangle sourceRegion = p.getSourceRegion();
         if (sourceRegion != null) {
            sourceRegion = sourceRegion.intersection(sourceBounds);
            sourceBounds.setBounds(sourceRegion);
         }

         int gridX = p.getSubsamplingXOffset();
         int gridY = p.getSubsamplingYOffset();
         sourceBounds.x += gridX;
         sourceBounds.y += gridY;
         sourceBounds.width -= gridX;
         sourceBounds.height -= gridY;
         periodX = p.getSourceXSubsampling();
         periodY = p.getSourceYSubsampling();
      }

      destSize.setSize((sourceBounds.width + periodX - 1) / periodX, (sourceBounds.height + periodY - 1) / periodY);
      if (destSize.width <= 0 || destSize.height <= 0) {
         throw new IllegalArgumentException("Empty source region!");
      }
   }

   private static byte[] createColorTable(ColorModel colorModel, SampleModel sampleModel) {
      byte[] colorTable;
      int colorTableLength;
      int i;
      if (colorModel instanceof IndexColorModel) {
         IndexColorModel icm = (IndexColorModel)colorModel;
         colorTableLength = icm.getMapSize();
         i = getGifPaletteSize(colorTableLength);
         byte[] reds = new byte[i];
         byte[] greens = new byte[i];
         byte[] blues = new byte[i];
         icm.getReds(reds);
         icm.getGreens(greens);
         icm.getBlues(blues);

         int idx;
         for(idx = colorTableLength; idx < i; ++idx) {
            reds[idx] = reds[0];
            greens[idx] = greens[0];
            blues[idx] = blues[0];
         }

         colorTable = new byte[3 * i];
         idx = 0;

         for(int i = 0; i < i; ++i) {
            colorTable[idx++] = reds[i];
            colorTable[idx++] = greens[i];
            colorTable[idx++] = blues[i];
         }
      } else if (sampleModel.getNumBands() == 1) {
         int numBits = sampleModel.getSampleSize()[0];
         if (numBits > 8) {
            numBits = 8;
         }

         colorTableLength = 3 * (1 << numBits);
         colorTable = new byte[colorTableLength];

         for(i = 0; i < colorTableLength; ++i) {
            colorTable[i] = (byte)(i / 3);
         }
      } else {
         colorTable = null;
      }

      return colorTable;
   }

   private static int getGifPaletteSize(int x) {
      if (x <= 2) {
         return 2;
      } else {
         --x;
         x |= x >> 1;
         x |= x >> 2;
         x |= x >> 4;
         x |= x >> 8;
         x |= x >> 16;
         return x + 1;
      }
   }

   public GIFImageWriter(GIFImageWriterSpi originatingProvider) {
      super(originatingProvider);
   }

   public boolean canWriteSequence() {
      return true;
   }

   private void convertMetadata(String metadataFormatName, IIOMetadata inData, IIOMetadata outData) {
      String formatName = null;
      String nativeFormatName = inData.getNativeMetadataFormatName();
      if (nativeFormatName != null && nativeFormatName.equals(metadataFormatName)) {
         formatName = metadataFormatName;
      } else {
         String[] extraFormatNames = inData.getExtraMetadataFormatNames();
         if (extraFormatNames != null) {
            for(int i = 0; i < extraFormatNames.length; ++i) {
               if (extraFormatNames[i].equals(metadataFormatName)) {
                  formatName = metadataFormatName;
                  break;
               }
            }
         }
      }

      if (formatName == null && inData.isStandardMetadataFormatSupported()) {
         formatName = "javax_imageio_1.0";
      }

      if (formatName != null) {
         try {
            Node root = inData.getAsTree(formatName);
            outData.mergeTree(formatName, root);
         } catch (IIOInvalidTreeException var8) {
         }
      }

   }

   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
      if (inData == null) {
         throw new IllegalArgumentException("inData == null!");
      } else {
         IIOMetadata sm = this.getDefaultStreamMetadata(param);
         this.convertMetadata("javax_imageio_gif_stream_1.0", inData, sm);
         return sm;
      }
   }

   public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
      if (inData == null) {
         throw new IllegalArgumentException("inData == null!");
      } else if (imageType == null) {
         throw new IllegalArgumentException("imageType == null!");
      } else {
         GIFWritableImageMetadata im = (GIFWritableImageMetadata)this.getDefaultImageMetadata(imageType, param);
         boolean isProgressive = im.interlaceFlag;
         this.convertMetadata("javax_imageio_gif_image_1.0", inData, im);
         if (param != null && param.canWriteProgressive() && param.getProgressiveMode() != 3) {
            im.interlaceFlag = isProgressive;
         }

         return im;
      }
   }

   public void endWriteSequence() throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException("output == null!");
      } else if (!this.isWritingSequence) {
         throw new IllegalStateException("prepareWriteSequence() was not invoked!");
      } else {
         this.writeTrailer();
         this.resetLocal();
      }
   }

   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
      GIFWritableImageMetadata imageMetadata = new GIFWritableImageMetadata();
      SampleModel sampleModel = imageType.getSampleModel();
      Rectangle sourceBounds = new Rectangle(sampleModel.getWidth(), sampleModel.getHeight());
      Dimension destSize = new Dimension();
      computeRegions(sourceBounds, destSize, param);
      imageMetadata.imageWidth = destSize.width;
      imageMetadata.imageHeight = destSize.height;
      if (param != null && param.canWriteProgressive() && param.getProgressiveMode() == 0) {
         imageMetadata.interlaceFlag = false;
      } else {
         imageMetadata.interlaceFlag = true;
      }

      ColorModel colorModel = imageType.getColorModel();
      imageMetadata.localColorTable = createColorTable(colorModel, sampleModel);
      if (colorModel instanceof IndexColorModel) {
         int transparentIndex = ((IndexColorModel)colorModel).getTransparentPixel();
         if (transparentIndex != -1) {
            imageMetadata.transparentColorFlag = true;
            imageMetadata.transparentColorIndex = transparentIndex;
         }
      }

      return imageMetadata;
   }

   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
      GIFWritableStreamMetadata streamMetadata = new GIFWritableStreamMetadata();
      streamMetadata.version = "89a";
      return streamMetadata;
   }

   public ImageWriteParam getDefaultWriteParam() {
      return new GIFImageWriteParam(this.getLocale());
   }

   public void prepareWriteSequence(IIOMetadata streamMetadata) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException("Output is not set.");
      } else {
         this.resetLocal();
         if (streamMetadata == null) {
            this.theStreamMetadata = (GIFWritableStreamMetadata)this.getDefaultStreamMetadata((ImageWriteParam)null);
         } else {
            this.theStreamMetadata = new GIFWritableStreamMetadata();
            this.convertMetadata("javax_imageio_gif_stream_1.0", streamMetadata, this.theStreamMetadata);
         }

         this.isWritingSequence = true;
      }
   }

   public void reset() {
      super.reset();
      this.resetLocal();
   }

   private void resetLocal() {
      this.isWritingSequence = false;
      this.wroteSequenceHeader = false;
      this.theStreamMetadata = null;
      this.imageIndex = 0;
   }

   public void setOutput(Object output) {
      super.setOutput(output);
      if (output != null) {
         if (!(output instanceof ImageOutputStream)) {
            throw new IllegalArgumentException("output is not an ImageOutputStream");
         }

         this.stream = (ImageOutputStream)output;
         this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      } else {
         this.stream = null;
      }

   }

   public void write(IIOMetadata sm, IIOImage iioimage, ImageWriteParam p) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException("output == null!");
      } else if (iioimage == null) {
         throw new IllegalArgumentException("iioimage == null!");
      } else if (iioimage.hasRaster()) {
         throw new UnsupportedOperationException("canWriteRasters() == false!");
      } else {
         this.resetLocal();
         GIFWritableStreamMetadata streamMetadata;
         if (sm == null) {
            streamMetadata = (GIFWritableStreamMetadata)this.getDefaultStreamMetadata(p);
         } else {
            streamMetadata = (GIFWritableStreamMetadata)this.convertStreamMetadata(sm, p);
         }

         this.write(true, true, streamMetadata, iioimage, p);
      }
   }

   public void writeToSequence(IIOImage image, ImageWriteParam param) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException("output == null!");
      } else if (image == null) {
         throw new IllegalArgumentException("image == null!");
      } else if (image.hasRaster()) {
         throw new UnsupportedOperationException("canWriteRasters() == false!");
      } else if (!this.isWritingSequence) {
         throw new IllegalStateException("prepareWriteSequence() was not invoked!");
      } else {
         this.write(!this.wroteSequenceHeader, false, this.theStreamMetadata, image, param);
         if (!this.wroteSequenceHeader) {
            this.wroteSequenceHeader = true;
         }

         ++this.imageIndex;
      }
   }

   private boolean needToCreateIndex(RenderedImage image) {
      SampleModel sampleModel = image.getSampleModel();
      ColorModel colorModel = image.getColorModel();
      return sampleModel.getNumBands() != 1 || sampleModel.getSampleSize()[0] > 8 || colorModel.getComponentSize()[0] > 8;
   }

   private void write(boolean writeHeader, boolean writeTrailer, IIOMetadata sm, IIOImage iioimage, ImageWriteParam p) throws IOException {
      this.clearAbortRequest();
      RenderedImage image = iioimage.getRenderedImage();
      if (this.needToCreateIndex(image)) {
         image = PaletteBuilder.createIndexedImage(image);
         iioimage.setRenderedImage(image);
      }

      ColorModel colorModel = image.getColorModel();
      SampleModel sampleModel = image.getSampleModel();
      Rectangle sourceBounds = new Rectangle(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
      Dimension destSize = new Dimension();
      computeRegions(sourceBounds, destSize, p);
      GIFWritableImageMetadata imageMetadata = null;
      IndexColorModel icm;
      if (iioimage.getMetadata() != null) {
         imageMetadata = new GIFWritableImageMetadata();
         this.convertMetadata("javax_imageio_gif_image_1.0", iioimage.getMetadata(), imageMetadata);
         if (imageMetadata.localColorTable == null) {
            imageMetadata.localColorTable = createColorTable(colorModel, sampleModel);
            if (colorModel instanceof IndexColorModel) {
               icm = (IndexColorModel)colorModel;
               int index = icm.getTransparentPixel();
               imageMetadata.transparentColorFlag = index != -1;
               if (imageMetadata.transparentColorFlag) {
                  imageMetadata.transparentColorIndex = index;
               }
            }
         }
      }

      icm = null;
      byte[] globalColorTable;
      if (writeHeader) {
         if (sm == null) {
            throw new IllegalArgumentException("Cannot write null header!");
         }

         GIFWritableStreamMetadata streamMetadata = (GIFWritableStreamMetadata)sm;
         if (streamMetadata.version == null) {
            streamMetadata.version = "89a";
         }

         if (streamMetadata.logicalScreenWidth == -1) {
            streamMetadata.logicalScreenWidth = destSize.width;
         }

         if (streamMetadata.logicalScreenHeight == -1) {
            streamMetadata.logicalScreenHeight = destSize.height;
         }

         if (streamMetadata.colorResolution == -1) {
            streamMetadata.colorResolution = colorModel != null ? colorModel.getComponentSize()[0] : sampleModel.getSampleSize()[0];
         }

         if (streamMetadata.globalColorTable == null) {
            if (this.isWritingSequence && imageMetadata != null && imageMetadata.localColorTable != null) {
               streamMetadata.globalColorTable = imageMetadata.localColorTable;
            } else if (imageMetadata == null || imageMetadata.localColorTable == null) {
               streamMetadata.globalColorTable = createColorTable(colorModel, sampleModel);
            }
         }

         globalColorTable = streamMetadata.globalColorTable;
         int bitsPerPixel;
         if (globalColorTable != null) {
            bitsPerPixel = getNumBits(globalColorTable.length / 3);
         } else if (imageMetadata != null && imageMetadata.localColorTable != null) {
            bitsPerPixel = getNumBits(imageMetadata.localColorTable.length / 3);
         } else {
            bitsPerPixel = sampleModel.getSampleSize(0);
         }

         this.writeHeader(streamMetadata, bitsPerPixel);
      } else {
         if (!this.isWritingSequence) {
            throw new IllegalArgumentException("Must write header for single image!");
         }

         globalColorTable = this.theStreamMetadata.globalColorTable;
      }

      this.writeImage(iioimage.getRenderedImage(), imageMetadata, p, globalColorTable, sourceBounds, destSize);
      if (writeTrailer) {
         this.writeTrailer();
      }

   }

   private void writeImage(RenderedImage image, GIFWritableImageMetadata imageMetadata, ImageWriteParam param, byte[] globalColorTable, Rectangle sourceBounds, Dimension destSize) throws IOException {
      ColorModel colorModel = image.getColorModel();
      SampleModel sampleModel = image.getSampleModel();
      boolean writeGraphicsControlExtension;
      if (imageMetadata == null) {
         imageMetadata = (GIFWritableImageMetadata)this.getDefaultImageMetadata(new ImageTypeSpecifier(image), param);
         writeGraphicsControlExtension = imageMetadata.transparentColorFlag;
      } else {
         NodeList list = null;

         try {
            IIOMetadataNode root = (IIOMetadataNode)imageMetadata.getAsTree("javax_imageio_gif_image_1.0");
            list = root.getElementsByTagName("GraphicControlExtension");
         } catch (IllegalArgumentException var12) {
         }

         writeGraphicsControlExtension = list != null && list.getLength() > 0;
         if (param != null && param.canWriteProgressive()) {
            if (param.getProgressiveMode() == 0) {
               imageMetadata.interlaceFlag = false;
            } else if (param.getProgressiveMode() == 1) {
               imageMetadata.interlaceFlag = true;
            }
         }
      }

      if (Arrays.equals(globalColorTable, imageMetadata.localColorTable)) {
         imageMetadata.localColorTable = null;
      }

      imageMetadata.imageWidth = destSize.width;
      imageMetadata.imageHeight = destSize.height;
      if (writeGraphicsControlExtension) {
         this.writeGraphicControlExtension(imageMetadata);
      }

      this.writePlainTextExtension(imageMetadata);
      this.writeApplicationExtension(imageMetadata);
      this.writeCommentExtension(imageMetadata);
      int bitsPerPixel = getNumBits(imageMetadata.localColorTable == null ? (globalColorTable == null ? sampleModel.getSampleSize(0) : globalColorTable.length / 3) : imageMetadata.localColorTable.length / 3);
      this.writeImageDescriptor(imageMetadata, bitsPerPixel);
      this.writeRasterData(image, sourceBounds, destSize, param, imageMetadata.interlaceFlag);
   }

   private void writeRows(RenderedImage image, LZWCompressor compressor, int sx, int sdx, int sy, int sdy, int sw, int dy, int ddy, int dw, int dh, int numRowsWritten, int progressReportRowPeriod) throws IOException {
      int[] sbuf = new int[sw];
      byte[] dbuf = new byte[dw];
      Raster raster = image.getNumXTiles() == 1 && image.getNumYTiles() == 1 ? image.getTile(0, 0) : image.getData();

      for(int y = dy; y < dh; y += ddy) {
         if (numRowsWritten % progressReportRowPeriod == 0) {
            if (this.abortRequested()) {
               this.processWriteAborted();
               return;
            }

            this.processImageProgress((float)numRowsWritten * 100.0F / (float)dh);
         }

         raster.getSamples(sx, sy, sw, 1, 0, sbuf);
         int i = 0;

         for(int j = 0; i < dw; j += sdx) {
            dbuf[i] = (byte)sbuf[j];
            ++i;
         }

         compressor.compress(dbuf, 0, dw);
         ++numRowsWritten;
         sy += sdy;
      }

   }

   private void writeRowsOpt(byte[] data, int offset, int lineStride, LZWCompressor compressor, int dy, int ddy, int dw, int dh, int numRowsWritten, int progressReportRowPeriod) throws IOException {
      offset += dy * lineStride;
      lineStride *= ddy;

      for(int y = dy; y < dh; y += ddy) {
         if (numRowsWritten % progressReportRowPeriod == 0) {
            if (this.abortRequested()) {
               this.processWriteAborted();
               return;
            }

            this.processImageProgress((float)numRowsWritten * 100.0F / (float)dh);
         }

         compressor.compress(data, offset, dw);
         ++numRowsWritten;
         offset += lineStride;
      }

   }

   private void writeRasterData(RenderedImage image, Rectangle sourceBounds, Dimension destSize, ImageWriteParam param, boolean interlaceFlag) throws IOException {
      int sourceXOffset = sourceBounds.x;
      int sourceYOffset = sourceBounds.y;
      int sourceWidth = sourceBounds.width;
      int sourceHeight = sourceBounds.height;
      int destWidth = destSize.width;
      int destHeight = destSize.height;
      int periodX;
      int periodY;
      if (param == null) {
         periodX = 1;
         periodY = 1;
      } else {
         periodX = param.getSourceXSubsampling();
         periodY = param.getSourceYSubsampling();
      }

      SampleModel sampleModel = image.getSampleModel();
      int bitsPerPixel = sampleModel.getSampleSize()[0];
      int initCodeSize = bitsPerPixel;
      if (bitsPerPixel == 1) {
         initCodeSize = bitsPerPixel + 1;
      }

      this.stream.write(initCodeSize);
      LZWCompressor compressor = new LZWCompressor(this.stream, initCodeSize, false);
      boolean isOptimizedCase = periodX == 1 && periodY == 1 && sampleModel instanceof ComponentSampleModel && image.getNumXTiles() == 1 && image.getNumYTiles() == 1 && image.getTile(0, 0).getDataBuffer() instanceof DataBufferByte;
      int numRowsWritten = 0;
      int progressReportRowPeriod = Math.max(destHeight / 20, 1);
      this.processImageStarted(this.imageIndex);
      Raster tile;
      byte[] data;
      ComponentSampleModel csm;
      int offset;
      int lineStride;
      if (interlaceFlag) {
         if (isOptimizedCase) {
            tile = image.getTile(0, 0);
            data = ((DataBufferByte)tile.getDataBuffer()).getData();
            csm = (ComponentSampleModel)tile.getSampleModel();
            offset = csm.getOffset(sourceXOffset - tile.getSampleModelTranslateX(), sourceYOffset - tile.getSampleModelTranslateY(), 0);
            lineStride = csm.getScanlineStride();
            this.writeRowsOpt(data, offset, lineStride, compressor, 0, 8, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
            if (this.abortRequested()) {
               return;
            }

            numRowsWritten += destHeight / 8;
            this.writeRowsOpt(data, offset, lineStride, compressor, 4, 8, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
            if (this.abortRequested()) {
               return;
            }

            numRowsWritten += (destHeight - 4) / 8;
            this.writeRowsOpt(data, offset, lineStride, compressor, 2, 4, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
            if (this.abortRequested()) {
               return;
            }

            numRowsWritten += (destHeight - 2) / 4;
            this.writeRowsOpt(data, offset, lineStride, compressor, 1, 2, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
         } else {
            this.writeRows(image, compressor, sourceXOffset, periodX, sourceYOffset, 8 * periodY, sourceWidth, 0, 8, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
            if (this.abortRequested()) {
               return;
            }

            numRowsWritten += destHeight / 8;
            this.writeRows(image, compressor, sourceXOffset, periodX, sourceYOffset + 4 * periodY, 8 * periodY, sourceWidth, 4, 8, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
            if (this.abortRequested()) {
               return;
            }

            numRowsWritten += (destHeight - 4) / 8;
            this.writeRows(image, compressor, sourceXOffset, periodX, sourceYOffset + 2 * periodY, 4 * periodY, sourceWidth, 2, 4, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
            if (this.abortRequested()) {
               return;
            }

            numRowsWritten += (destHeight - 2) / 4;
            this.writeRows(image, compressor, sourceXOffset, periodX, sourceYOffset + periodY, 2 * periodY, sourceWidth, 1, 2, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
         }
      } else if (isOptimizedCase) {
         tile = image.getTile(0, 0);
         data = ((DataBufferByte)tile.getDataBuffer()).getData();
         csm = (ComponentSampleModel)tile.getSampleModel();
         offset = csm.getOffset(sourceXOffset - tile.getSampleModelTranslateX(), sourceYOffset - tile.getSampleModelTranslateY(), 0);
         lineStride = csm.getScanlineStride();
         this.writeRowsOpt(data, offset, lineStride, compressor, 0, 1, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
      } else {
         this.writeRows(image, compressor, sourceXOffset, periodX, sourceYOffset, periodY, sourceWidth, 0, 1, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
      }

      if (!this.abortRequested()) {
         this.processImageProgress(100.0F);
         compressor.flush();
         this.stream.write(0);
         this.processImageComplete();
      }
   }

   private void writeHeader(String version, int logicalScreenWidth, int logicalScreenHeight, int colorResolution, int pixelAspectRatio, int backgroundColorIndex, boolean sortFlag, int bitsPerPixel, byte[] globalColorTable) throws IOException {
      try {
         this.stream.writeBytes("GIF" + version);
         this.stream.writeShort((short)logicalScreenWidth);
         this.stream.writeShort((short)logicalScreenHeight);
         int packedFields = globalColorTable != null ? 128 : 0;
         packedFields |= (colorResolution - 1 & 7) << 4;
         if (sortFlag) {
            packedFields |= 8;
         }

         packedFields |= bitsPerPixel - 1;
         this.stream.write(packedFields);
         this.stream.write(backgroundColorIndex);
         this.stream.write(pixelAspectRatio);
         if (globalColorTable != null) {
            this.stream.write(globalColorTable);
         }

      } catch (IOException var11) {
         throw new IIOException("I/O error writing header!", var11);
      }
   }

   private void writeHeader(IIOMetadata streamMetadata, int bitsPerPixel) throws IOException {
      GIFWritableStreamMetadata sm;
      if (streamMetadata instanceof GIFWritableStreamMetadata) {
         sm = (GIFWritableStreamMetadata)streamMetadata;
      } else {
         sm = new GIFWritableStreamMetadata();
         Node root = streamMetadata.getAsTree("javax_imageio_gif_stream_1.0");
         sm.setFromTree("javax_imageio_gif_stream_1.0", root);
      }

      this.writeHeader(sm.version, sm.logicalScreenWidth, sm.logicalScreenHeight, sm.colorResolution, sm.pixelAspectRatio, sm.backgroundColorIndex, sm.sortFlag, bitsPerPixel, sm.globalColorTable);
   }

   private void writeGraphicControlExtension(int disposalMethod, boolean userInputFlag, boolean transparentColorFlag, int delayTime, int transparentColorIndex) throws IOException {
      try {
         this.stream.write(33);
         this.stream.write(249);
         this.stream.write(4);
         int packedFields = (disposalMethod & 3) << 2;
         if (userInputFlag) {
            packedFields |= 2;
         }

         if (transparentColorFlag) {
            packedFields |= 1;
         }

         this.stream.write(packedFields);
         this.stream.writeShort((short)delayTime);
         this.stream.write(transparentColorIndex);
         this.stream.write(0);
      } catch (IOException var7) {
         throw new IIOException("I/O error writing Graphic Control Extension!", var7);
      }
   }

   private void writeGraphicControlExtension(GIFWritableImageMetadata im) throws IOException {
      this.writeGraphicControlExtension(im.disposalMethod, im.userInputFlag, im.transparentColorFlag, im.delayTime, im.transparentColorIndex);
   }

   private void writeBlocks(byte[] data) throws IOException {
      int len;
      if (data != null && data.length > 0) {
         for(int offset = 0; offset < data.length; offset += len) {
            len = Math.min(data.length - offset, 255);
            this.stream.write(len);
            this.stream.write(data, offset, len);
         }
      }

   }

   private void writePlainTextExtension(GIFWritableImageMetadata im) throws IOException {
      if (im.hasPlainTextExtension) {
         try {
            this.stream.write(33);
            this.stream.write(1);
            this.stream.write(12);
            this.stream.writeShort(im.textGridLeft);
            this.stream.writeShort(im.textGridTop);
            this.stream.writeShort(im.textGridWidth);
            this.stream.writeShort(im.textGridHeight);
            this.stream.write(im.characterCellWidth);
            this.stream.write(im.characterCellHeight);
            this.stream.write(im.textForegroundColor);
            this.stream.write(im.textBackgroundColor);
            this.writeBlocks(im.text);
            this.stream.write(0);
         } catch (IOException var3) {
            throw new IIOException("I/O error writing Plain Text Extension!", var3);
         }
      }

   }

   private void writeApplicationExtension(GIFWritableImageMetadata im) throws IOException {
      if (im.applicationIDs != null) {
         Iterator iterIDs = im.applicationIDs.iterator();
         Iterator iterCodes = im.authenticationCodes.iterator();
         Iterator iterData = im.applicationData.iterator();

         while(iterIDs.hasNext()) {
            try {
               this.stream.write(33);
               this.stream.write(255);
               this.stream.write(11);
               this.stream.write((byte[])((byte[])iterIDs.next()), 0, 8);
               this.stream.write((byte[])((byte[])iterCodes.next()), 0, 3);
               this.writeBlocks((byte[])((byte[])iterData.next()));
               this.stream.write(0);
            } catch (IOException var6) {
               throw new IIOException("I/O error writing Application Extension!", var6);
            }
         }
      }

   }

   private void writeCommentExtension(GIFWritableImageMetadata im) throws IOException {
      if (im.comments != null) {
         try {
            Iterator iter = im.comments.iterator();

            while(iter.hasNext()) {
               this.stream.write(33);
               this.stream.write(254);
               this.writeBlocks((byte[])((byte[])iter.next()));
               this.stream.write(0);
            }
         } catch (IOException var3) {
            throw new IIOException("I/O error writing Comment Extension!", var3);
         }
      }

   }

   private void writeImageDescriptor(int imageLeftPosition, int imageTopPosition, int imageWidth, int imageHeight, boolean interlaceFlag, boolean sortFlag, int bitsPerPixel, byte[] localColorTable) throws IOException {
      try {
         this.stream.write(44);
         this.stream.writeShort((short)imageLeftPosition);
         this.stream.writeShort((short)imageTopPosition);
         this.stream.writeShort((short)imageWidth);
         this.stream.writeShort((short)imageHeight);
         int packedFields = localColorTable != null ? 128 : 0;
         if (interlaceFlag) {
            packedFields |= 64;
         }

         if (sortFlag) {
            packedFields |= 8;
         }

         packedFields |= bitsPerPixel - 1;
         this.stream.write(packedFields);
         if (localColorTable != null) {
            this.stream.write(localColorTable);
         }

      } catch (IOException var10) {
         throw new IIOException("I/O error writing Image Descriptor!", var10);
      }
   }

   private void writeImageDescriptor(GIFWritableImageMetadata imageMetadata, int bitsPerPixel) throws IOException {
      this.writeImageDescriptor(imageMetadata.imageLeftPosition, imageMetadata.imageTopPosition, imageMetadata.imageWidth, imageMetadata.imageHeight, imageMetadata.interlaceFlag, imageMetadata.sortFlag, bitsPerPixel, imageMetadata.localColorTable);
   }

   private void writeTrailer() throws IOException {
      this.stream.write(59);
   }
}
