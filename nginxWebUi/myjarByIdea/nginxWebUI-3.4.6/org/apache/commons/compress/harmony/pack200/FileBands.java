package org.apache.commons.compress.harmony.pack200;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import org.objectweb.asm.ClassReader;

public class FileBands extends BandSet {
   private final CPUTF8[] fileName;
   private int[] file_name;
   private final int[] file_modtime;
   private final long[] file_size;
   private final int[] file_options;
   private final byte[][] file_bits;
   private final List fileList;
   private final PackingOptions options;
   private final CpBands cpBands;

   public FileBands(CpBands cpBands, SegmentHeader segmentHeader, PackingOptions options, Archive.SegmentUnit segmentUnit, int effort) {
      super(effort, segmentHeader);
      this.fileList = segmentUnit.getFileList();
      this.options = options;
      this.cpBands = cpBands;
      int size = this.fileList.size();
      this.fileName = new CPUTF8[size];
      this.file_modtime = new int[size];
      this.file_size = new long[size];
      this.file_options = new int[size];
      int totalSize = 0;
      this.file_bits = new byte[size][];
      int archiveModtime = segmentHeader.getArchive_modtime();
      Set classNames = new HashSet();
      Iterator iterator = segmentUnit.getClassList().iterator();

      while(iterator.hasNext()) {
         ClassReader reader = (ClassReader)iterator.next();
         classNames.add(reader.getClassName());
      }

      CPUTF8 emptyString = cpBands.getCPUtf8("");
      int latestModtime = Integer.MIN_VALUE;
      boolean isLatest = !"keep".equals(options.getModificationTime());

      int i;
      for(i = 0; i < size; ++i) {
         Archive.PackingFile packingFile = (Archive.PackingFile)this.fileList.get(i);
         String name = packingFile.getName();
         int[] var10000;
         if (name.endsWith(".class") && !options.isPassFile(name)) {
            var10000 = this.file_options;
            var10000[i] |= 2;
            if (classNames.contains(name.substring(0, name.length() - 6))) {
               this.fileName[i] = emptyString;
            } else {
               this.fileName[i] = cpBands.getCPUtf8(name);
            }
         } else {
            this.fileName[i] = cpBands.getCPUtf8(name);
         }

         if (options.isKeepDeflateHint() && packingFile.isDefalteHint()) {
            var10000 = this.file_options;
            var10000[i] |= 1;
         }

         byte[] bytes = packingFile.getContents();
         this.file_size[i] = (long)bytes.length;
         totalSize = (int)((long)totalSize + this.file_size[i]);
         long modtime = (packingFile.getModtime() + (long)TimeZone.getDefault().getRawOffset()) / 1000L;
         this.file_modtime[i] = (int)(modtime - (long)archiveModtime);
         if (isLatest && latestModtime < this.file_modtime[i]) {
            latestModtime = this.file_modtime[i];
         }

         this.file_bits[i] = packingFile.getContents();
      }

      if (isLatest) {
         for(i = 0; i < size; ++i) {
            this.file_modtime[i] = latestModtime;
         }
      }

   }

   public void finaliseBands() {
      this.file_name = new int[this.fileName.length];

      for(int i = 0; i < this.file_name.length; ++i) {
         if (this.fileName[i].equals(this.cpBands.getCPUtf8(""))) {
            Archive.PackingFile packingFile = (Archive.PackingFile)this.fileList.get(i);
            String name = packingFile.getName();
            if (this.options.isPassFile(name)) {
               this.fileName[i] = this.cpBands.getCPUtf8(name);
               int[] var10000 = this.file_options;
               var10000[i] &= -3;
            }
         }

         this.file_name[i] = this.fileName[i].getIndex();
      }

   }

   public void pack(OutputStream out) throws IOException, Pack200Exception {
      PackingUtils.log("Writing file bands...");
      byte[] encodedBand = this.encodeBandInt("file_name", this.file_name, Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from file_name[" + this.file_name.length + "]");
      encodedBand = this.encodeFlags("file_size", this.file_size, Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader.have_file_size_hi());
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from file_size[" + this.file_size.length + "]");
      if (this.segmentHeader.have_file_modtime()) {
         encodedBand = this.encodeBandInt("file_modtime", this.file_modtime, Codec.DELTA5);
         out.write(encodedBand);
         PackingUtils.log("Wrote " + encodedBand.length + " bytes from file_modtime[" + this.file_modtime.length + "]");
      }

      if (this.segmentHeader.have_file_options()) {
         encodedBand = this.encodeBandInt("file_options", this.file_options, Codec.UNSIGNED5);
         out.write(encodedBand);
         PackingUtils.log("Wrote " + encodedBand.length + " bytes from file_options[" + this.file_options.length + "]");
      }

      encodedBand = this.encodeBandInt("file_bits", this.flatten(this.file_bits), Codec.BYTE1);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from file_bits[" + this.file_bits.length + "]");
   }

   private int[] flatten(byte[][] bytes) {
      int total = 0;

      for(int i = 0; i < bytes.length; ++i) {
         total += bytes[i].length;
      }

      int[] band = new int[total];
      int index = 0;

      for(int i = 0; i < bytes.length; ++i) {
         for(int j = 0; j < bytes[i].length; ++j) {
            band[index++] = bytes[i][j] & 255;
         }
      }

      return band;
   }
}
