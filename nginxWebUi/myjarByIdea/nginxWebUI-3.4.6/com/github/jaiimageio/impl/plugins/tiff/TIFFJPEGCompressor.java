package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class TIFFJPEGCompressor extends TIFFBaseJPEGCompressor {
   private static final boolean DEBUG = false;
   static final int CHROMA_SUBSAMPLING = 2;

   private static ImageReader getJPEGTablesReader() {
      ImageReader jpegReader = null;

      try {
         IIORegistry registry = IIORegistry.getDefaultInstance();
         Class imageReaderClass = Class.forName("javax.imageio.spi.ImageReaderSpi");
         Iterator readerSPIs = registry.getServiceProviders(imageReaderClass, new JPEGSPIFilter(), true);
         if (readerSPIs.hasNext()) {
            ImageReaderSpi jpegReaderSPI = (ImageReaderSpi)readerSPIs.next();
            jpegReader = jpegReaderSPI.createReaderInstance();
         }
      } catch (Exception var5) {
      }

      return jpegReader;
   }

   public TIFFJPEGCompressor(ImageWriteParam param) {
      super("JPEG", 7, false, param);
   }

   public void setMetadata(IIOMetadata metadata) {
      super.setMetadata(metadata);
      if (metadata instanceof TIFFImageMetadata) {
         TIFFImageMetadata tim = (TIFFImageMetadata)metadata;
         TIFFIFD rootIFD = tim.getRootIFD();
         BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
         TIFFField f = tim.getTIFFField(277);
         int numBands = f.getAsInt(0);
         TIFFField JPEGTablesField;
         if (numBands == 1) {
            rootIFD.removeTIFFField(530);
            rootIFD.removeTIFFField(531);
            rootIFD.removeTIFFField(532);
         } else {
            JPEGTablesField = new TIFFField(base.getTag(530), 3, 2, new char[]{'\u0002', '\u0002'});
            rootIFD.addTIFFField(JPEGTablesField);
            TIFFField YCbCrPositioningField = new TIFFField(base.getTag(531), 3, 1, new char[]{'\u0001'});
            rootIFD.addTIFFField(YCbCrPositioningField);
            TIFFField referenceBlackWhiteField = new TIFFField(base.getTag(532), 5, 6, new long[][]{{0L, 1L}, {255L, 1L}, {128L, 1L}, {255L, 1L}, {128L, 1L}, {255L, 1L}});
            rootIFD.addTIFFField(referenceBlackWhiteField);
         }

         JPEGTablesField = tim.getTIFFField(347);
         if (JPEGTablesField != null) {
            this.initJPEGWriter(true, false);
         }

         if (JPEGTablesField != null && this.JPEGWriter != null) {
            this.writeAbbreviatedStream = true;
            if (JPEGTablesField.getCount() > 0) {
               byte[] tables = JPEGTablesField.getAsBytes();
               ByteArrayInputStream bais = new ByteArrayInputStream(tables);
               MemoryCacheImageInputStream iis = new MemoryCacheImageInputStream(bais);
               ImageReader jpegReader = getJPEGTablesReader();
               jpegReader.setInput(iis);

               try {
                  this.JPEGStreamMetadata = jpegReader.getStreamMetadata();
               } catch (Exception var18) {
                  this.JPEGStreamMetadata = null;
               } finally {
                  jpegReader.reset();
               }
            }

            if (this.JPEGStreamMetadata == null) {
               this.JPEGStreamMetadata = this.JPEGWriter.getDefaultStreamMetadata(this.JPEGParam);
               ByteArrayOutputStream tableByteStream = new ByteArrayOutputStream();
               MemoryCacheImageOutputStream tableStream = new MemoryCacheImageOutputStream(tableByteStream);
               this.JPEGWriter.setOutput(tableStream);

               try {
                  this.JPEGWriter.prepareWriteSequence(this.JPEGStreamMetadata);
                  tableStream.flush();
                  this.JPEGWriter.endWriteSequence();
                  byte[] tables = tableByteStream.toByteArray();
                  JPEGTablesField = new TIFFField(base.getTag(347), 7, tables.length, tables);
                  rootIFD.addTIFFField(JPEGTablesField);
               } catch (Exception var17) {
                  rootIFD.removeTIFFField(347);
                  this.writeAbbreviatedStream = false;
               }
            }
         } else {
            rootIFD.removeTIFFField(347);
            this.initJPEGWriter(false, false);
         }
      }

   }

   private static class JPEGSPIFilter implements ServiceRegistry.Filter {
      JPEGSPIFilter() {
      }

      public boolean filter(Object provider) {
         ImageReaderSpi readerSPI = (ImageReaderSpi)provider;
         if (readerSPI != null) {
            String streamMetadataName = readerSPI.getNativeStreamMetadataFormatName();
            return streamMetadataName != null ? streamMetadataName.equals("javax_imageio_jpeg_stream_1.0") : false;
         } else {
            return false;
         }
      }
   }
}
