package com.github.jaiimageio.impl.plugins.wbmp;

import com.github.jaiimageio.impl.common.ImageUtil;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class WBMPImageReader extends ImageReader {
   private ImageInputStream iis = null;
   private boolean gotHeader = false;
   private long imageDataOffset;
   private int width;
   private int height;
   private int wbmpType;
   private WBMPMetadata metadata;

   public WBMPImageReader(ImageReaderSpi originator) {
      super(originator);
   }

   public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
      super.setInput(input, seekForwardOnly, ignoreMetadata);
      this.iis = (ImageInputStream)input;
      this.gotHeader = false;
   }

   public int getNumImages(boolean allowSearch) throws IOException {
      if (this.iis == null) {
         throw new IllegalStateException(I18N.getString("GetNumImages0"));
      } else if (this.seekForwardOnly && allowSearch) {
         throw new IllegalStateException(I18N.getString("GetNumImages1"));
      } else {
         return 1;
      }
   }

   public int getWidth(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      this.readHeader();
      return this.width;
   }

   public int getHeight(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      this.readHeader();
      return this.height;
   }

   public boolean isRandomAccessEasy(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      return true;
   }

   private void checkIndex(int imageIndex) {
      if (imageIndex != 0) {
         throw new IndexOutOfBoundsException(I18N.getString("WBMPImageReader0"));
      }
   }

   public void readHeader() throws IOException {
      if (this.gotHeader) {
         this.iis.seek(this.imageDataOffset);
      } else if (this.iis == null) {
         throw new IllegalStateException(I18N.getString("WBMPImageReader1"));
      } else {
         this.metadata = new WBMPMetadata();
         this.wbmpType = this.iis.readByte();
         byte fixHeaderField = this.iis.readByte();
         if (fixHeaderField == 0 && this.isValidWbmpType(this.wbmpType)) {
            this.metadata.wbmpType = this.wbmpType;
            this.width = ImageUtil.readMultiByteInteger(this.iis);
            this.metadata.width = this.width;
            this.height = ImageUtil.readMultiByteInteger(this.iis);
            this.metadata.height = this.height;
            this.gotHeader = true;
            this.imageDataOffset = this.iis.getStreamPosition();
         } else {
            throw new IIOException(I18N.getString("WBMPImageReader2"));
         }
      }
   }

   public Iterator getImageTypes(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      this.readHeader();
      BufferedImage bi = new BufferedImage(1, 1, 12);
      ArrayList list = new ArrayList(1);
      list.add(new ImageTypeSpecifier(bi));
      return list.iterator();
   }

   public ImageReadParam getDefaultReadParam() {
      return new ImageReadParam();
   }

   public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      if (this.metadata == null) {
         this.readHeader();
      }

      return this.metadata;
   }

   public IIOMetadata getStreamMetadata() throws IOException {
      return null;
   }

   public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
      if (this.iis == null) {
         throw new IllegalStateException(I18N.getString("WBMPImageReader1"));
      } else {
         this.checkIndex(imageIndex);
         this.clearAbortRequest();
         this.processImageStarted(imageIndex);
         if (param == null) {
            param = this.getDefaultReadParam();
         }

         this.readHeader();
         Rectangle sourceRegion = new Rectangle(0, 0, 0, 0);
         Rectangle destinationRegion = new Rectangle(0, 0, 0, 0);
         computeRegions(param, this.width, this.height, param.getDestination(), sourceRegion, destinationRegion);
         int scaleX = param.getSourceXSubsampling();
         int scaleY = param.getSourceYSubsampling();
         int xOffset = param.getSubsamplingXOffset();
         int yOffset = param.getSubsamplingYOffset();
         BufferedImage bi = param.getDestination();
         if (bi == null) {
            bi = new BufferedImage(destinationRegion.x + destinationRegion.width, destinationRegion.y + destinationRegion.height, 12);
         }

         boolean noTransform = destinationRegion.equals(new Rectangle(0, 0, this.width, this.height)) && destinationRegion.equals(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
         WritableRaster tile = bi.getWritableTile(0, 0);
         MultiPixelPackedSampleModel sm = (MultiPixelPackedSampleModel)bi.getSampleModel();
         if (noTransform) {
            if (this.abortRequested()) {
               this.processReadAborted();
               return bi;
            }

            this.iis.read(((DataBufferByte)tile.getDataBuffer()).getData(), 0, this.height * sm.getScanlineStride());
            this.processImageUpdate(bi, 0, 0, this.width, this.height, 1, 1, new int[]{0});
            this.processImageProgress(100.0F);
         } else {
            int len = (this.width + 7) / 8;
            byte[] buf = new byte[len];
            byte[] data = ((DataBufferByte)tile.getDataBuffer()).getData();
            int lineStride = sm.getScanlineStride();
            this.iis.skipBytes(len * sourceRegion.y);
            int skipLength = len * (scaleY - 1);
            int[] srcOff = new int[destinationRegion.width];
            int[] destOff = new int[destinationRegion.width];
            int[] srcPos = new int[destinationRegion.width];
            int[] destPos = new int[destinationRegion.width];
            int j = destinationRegion.x;
            int y = sourceRegion.x;

            int k;
            for(k = 0; j < destinationRegion.x + destinationRegion.width; y += scaleX) {
               srcPos[k] = y >> 3;
               srcOff[k] = 7 - (y & 7);
               destPos[k] = j >> 3;
               destOff[k] = 7 - (j & 7);
               ++j;
               ++k;
            }

            j = 0;
            y = sourceRegion.y;

            for(k = destinationRegion.y * lineStride; j < destinationRegion.height && !this.abortRequested(); y += scaleY) {
               this.iis.read(buf, 0, len);

               for(int i = 0; i < destinationRegion.width; ++i) {
                  int v = buf[srcPos[i]] >> srcOff[i] & 1;
                  data[k + destPos[i]] = (byte)(data[k + destPos[i]] | v << destOff[i]);
               }

               k += lineStride;
               this.iis.skipBytes(skipLength);
               this.processImageUpdate(bi, 0, j, destinationRegion.width, 1, 1, 1, new int[]{0});
               this.processImageProgress(100.0F * (float)j / (float)destinationRegion.height);
               ++j;
            }
         }

         if (this.abortRequested()) {
            this.processReadAborted();
         } else {
            this.processImageComplete();
         }

         return bi;
      }
   }

   public boolean canReadRaster() {
      return true;
   }

   public Raster readRaster(int imageIndex, ImageReadParam param) throws IOException {
      BufferedImage bi = this.read(imageIndex, param);
      return bi.getData();
   }

   public void reset() {
      super.reset();
      this.iis = null;
      this.gotHeader = false;
   }

   boolean isValidWbmpType(int type) {
      return type == 0;
   }
}
