package org.apache.commons.compress.archivers.sevenz;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import org.apache.commons.compress.MemoryLimitException;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class SevenZFile implements Closeable {
   static final int SIGNATURE_HEADER_SIZE = 32;
   private static final String DEFAULT_FILE_NAME = "unknown archive";
   private final String fileName;
   private SeekableByteChannel channel;
   private final Archive archive;
   private int currentEntryIndex;
   private int currentFolderIndex;
   private InputStream currentFolderInputStream;
   private byte[] password;
   private final SevenZFileOptions options;
   private long compressedBytesReadFromCurrentEntry;
   private long uncompressedBytesReadFromCurrentEntry;
   private final ArrayList<InputStream> deferredBlockStreams;
   static final byte[] sevenZSignature = new byte[]{55, 122, -68, -81, 39, 28};
   private static final CharsetEncoder PASSWORD_ENCODER;

   public SevenZFile(File fileName, char[] password) throws IOException {
      this(fileName, password, SevenZFileOptions.DEFAULT);
   }

   public SevenZFile(File fileName, char[] password, SevenZFileOptions options) throws IOException {
      this(Files.newByteChannel(fileName.toPath(), EnumSet.of(StandardOpenOption.READ)), fileName.getAbsolutePath(), utf16Decode(password), true, options);
   }

   /** @deprecated */
   @Deprecated
   public SevenZFile(File fileName, byte[] password) throws IOException {
      this(Files.newByteChannel(fileName.toPath(), EnumSet.of(StandardOpenOption.READ)), fileName.getAbsolutePath(), password, true, SevenZFileOptions.DEFAULT);
   }

   public SevenZFile(SeekableByteChannel channel) throws IOException {
      this(channel, SevenZFileOptions.DEFAULT);
   }

   public SevenZFile(SeekableByteChannel channel, SevenZFileOptions options) throws IOException {
      this(channel, "unknown archive", (char[])null, options);
   }

   public SevenZFile(SeekableByteChannel channel, char[] password) throws IOException {
      this(channel, password, SevenZFileOptions.DEFAULT);
   }

   public SevenZFile(SeekableByteChannel channel, char[] password, SevenZFileOptions options) throws IOException {
      this(channel, "unknown archive", password, options);
   }

   public SevenZFile(SeekableByteChannel channel, String fileName, char[] password) throws IOException {
      this(channel, fileName, password, SevenZFileOptions.DEFAULT);
   }

   public SevenZFile(SeekableByteChannel channel, String fileName, char[] password, SevenZFileOptions options) throws IOException {
      this(channel, fileName, utf16Decode(password), false, options);
   }

   public SevenZFile(SeekableByteChannel channel, String fileName) throws IOException {
      this(channel, fileName, SevenZFileOptions.DEFAULT);
   }

   public SevenZFile(SeekableByteChannel channel, String fileName, SevenZFileOptions options) throws IOException {
      this(channel, fileName, (byte[])null, false, options);
   }

   /** @deprecated */
   @Deprecated
   public SevenZFile(SeekableByteChannel channel, byte[] password) throws IOException {
      this(channel, "unknown archive", password);
   }

   /** @deprecated */
   @Deprecated
   public SevenZFile(SeekableByteChannel channel, String fileName, byte[] password) throws IOException {
      this(channel, fileName, password, false, SevenZFileOptions.DEFAULT);
   }

   private SevenZFile(SeekableByteChannel channel, String filename, byte[] password, boolean closeOnError, SevenZFileOptions options) throws IOException {
      this.currentEntryIndex = -1;
      this.currentFolderIndex = -1;
      this.deferredBlockStreams = new ArrayList();
      boolean succeeded = false;
      this.channel = channel;
      this.fileName = filename;
      this.options = options;

      try {
         this.archive = this.readHeaders(password);
         if (password != null) {
            this.password = Arrays.copyOf(password, password.length);
         } else {
            this.password = null;
         }

         succeeded = true;
      } finally {
         if (!succeeded && closeOnError) {
            this.channel.close();
         }

      }

   }

   public SevenZFile(File fileName) throws IOException {
      this(fileName, SevenZFileOptions.DEFAULT);
   }

   public SevenZFile(File fileName, SevenZFileOptions options) throws IOException {
      this((File)fileName, (char[])null, (SevenZFileOptions)options);
   }

   public void close() throws IOException {
      if (this.channel != null) {
         try {
            this.channel.close();
         } finally {
            this.channel = null;
            if (this.password != null) {
               Arrays.fill(this.password, (byte)0);
            }

            this.password = null;
         }
      }

   }

   public SevenZArchiveEntry getNextEntry() throws IOException {
      if (this.currentEntryIndex >= this.archive.files.length - 1) {
         return null;
      } else {
         ++this.currentEntryIndex;
         SevenZArchiveEntry entry = this.archive.files[this.currentEntryIndex];
         if (entry.getName() == null && this.options.getUseDefaultNameForUnnamedEntries()) {
            entry.setName(this.getDefaultName());
         }

         this.buildDecodingStream(this.currentEntryIndex, false);
         this.uncompressedBytesReadFromCurrentEntry = this.compressedBytesReadFromCurrentEntry = 0L;
         return entry;
      }
   }

   public Iterable<SevenZArchiveEntry> getEntries() {
      return new ArrayList(Arrays.asList(this.archive.files));
   }

   private Archive readHeaders(byte[] password) throws IOException {
      ByteBuffer buf = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
      this.readFully(buf);
      byte[] signature = new byte[6];
      buf.get(signature);
      if (!Arrays.equals(signature, sevenZSignature)) {
         throw new IOException("Bad 7z signature");
      } else {
         byte archiveVersionMajor = buf.get();
         byte archiveVersionMinor = buf.get();
         if (archiveVersionMajor != 0) {
            throw new IOException(String.format("Unsupported 7z version (%d,%d)", archiveVersionMajor, archiveVersionMinor));
         } else {
            boolean headerLooksValid = false;
            long startHeaderCrc = 4294967295L & (long)buf.getInt();
            if (startHeaderCrc == 0L) {
               long currentPosition = this.channel.position();
               ByteBuffer peekBuf = ByteBuffer.allocate(20);
               this.readFully(peekBuf);
               this.channel.position(currentPosition);

               while(peekBuf.hasRemaining()) {
                  if (peekBuf.get() != 0) {
                     headerLooksValid = true;
                     break;
                  }
               }
            } else {
               headerLooksValid = true;
            }

            if (headerLooksValid) {
               StartHeader startHeader = this.readStartHeader(startHeaderCrc);
               return this.initializeArchive(startHeader, password, true);
            } else if (this.options.getTryToRecoverBrokenArchives()) {
               return this.tryToLocateEndHeader(password);
            } else {
               throw new IOException("archive seems to be invalid.\nYou may want to retry and enable the tryToRecoverBrokenArchives if the archive could be a multi volume archive that has been closed prematurely.");
            }
         }
      }
   }

   private Archive tryToLocateEndHeader(byte[] password) throws IOException {
      ByteBuffer nidBuf = ByteBuffer.allocate(1);
      long searchLimit = 1048576L;
      long previousDataSize = this.channel.position() + 20L;
      long minPos;
      if (this.channel.position() + 1048576L > this.channel.size()) {
         minPos = this.channel.position();
      } else {
         minPos = this.channel.size() - 1048576L;
      }

      long pos = this.channel.size() - 1L;

      while(true) {
         byte nid;
         do {
            if (pos <= minPos) {
               throw new IOException("Start header corrupt and unable to guess end header");
            }

            --pos;
            this.channel.position(pos);
            nidBuf.rewind();
            if (this.channel.read(nidBuf) < 1) {
               throw new EOFException();
            }

            nid = nidBuf.array()[0];
         } while(nid != 23 && nid != 1);

         try {
            StartHeader startHeader = new StartHeader();
            startHeader.nextHeaderOffset = pos - previousDataSize;
            startHeader.nextHeaderSize = this.channel.size() - pos;
            Archive result = this.initializeArchive(startHeader, password, false);
            if (result.packSizes.length > 0 && result.files.length > 0) {
               return result;
            }
         } catch (Exception var14) {
         }
      }
   }

   private Archive initializeArchive(StartHeader startHeader, byte[] password, boolean verifyCrc) throws IOException {
      assertFitsIntoNonNegativeInt("nextHeaderSize", startHeader.nextHeaderSize);
      int nextHeaderSizeInt = (int)startHeader.nextHeaderSize;
      this.channel.position(32L + startHeader.nextHeaderOffset);
      ByteBuffer buf = ByteBuffer.allocate(nextHeaderSizeInt).order(ByteOrder.LITTLE_ENDIAN);
      this.readFully(buf);
      if (verifyCrc) {
         CRC32 crc = new CRC32();
         crc.update(buf.array());
         if (startHeader.nextHeaderCrc != crc.getValue()) {
            throw new IOException("NextHeader CRC mismatch");
         }
      }

      Archive archive = new Archive();
      int nid = getUnsignedByte(buf);
      if (nid == 23) {
         buf = this.readEncodedHeader(buf, archive, password);
         archive = new Archive();
         nid = getUnsignedByte(buf);
      }

      if (nid != 1) {
         throw new IOException("Broken or unsupported archive: no Header");
      } else {
         this.readHeader(buf, archive);
         archive.subStreamsInfo = null;
         return archive;
      }
   }

   private StartHeader readStartHeader(long startHeaderCrc) throws IOException {
      StartHeader startHeader = new StartHeader();
      DataInputStream dataInputStream = new DataInputStream(new CRC32VerifyingInputStream(new BoundedSeekableByteChannelInputStream(this.channel, 20L), 20L, startHeaderCrc));
      Throwable var5 = null;

      StartHeader var8;
      try {
         startHeader.nextHeaderOffset = Long.reverseBytes(dataInputStream.readLong());
         if (startHeader.nextHeaderOffset < 0L || startHeader.nextHeaderOffset + 32L > this.channel.size()) {
            throw new IOException("nextHeaderOffset is out of bounds");
         }

         startHeader.nextHeaderSize = Long.reverseBytes(dataInputStream.readLong());
         long nextHeaderEnd = startHeader.nextHeaderOffset + startHeader.nextHeaderSize;
         if (nextHeaderEnd < startHeader.nextHeaderOffset || nextHeaderEnd + 32L > this.channel.size()) {
            throw new IOException("nextHeaderSize is out of bounds");
         }

         startHeader.nextHeaderCrc = 4294967295L & (long)Integer.reverseBytes(dataInputStream.readInt());
         var8 = startHeader;
      } catch (Throwable var17) {
         var5 = var17;
         throw var17;
      } finally {
         if (dataInputStream != null) {
            if (var5 != null) {
               try {
                  dataInputStream.close();
               } catch (Throwable var16) {
                  var5.addSuppressed(var16);
               }
            } else {
               dataInputStream.close();
            }
         }

      }

      return var8;
   }

   private void readHeader(ByteBuffer header, Archive archive) throws IOException {
      int pos = header.position();
      ArchiveStatistics stats = this.sanityCheckAndCollectStatistics(header);
      stats.assertValidity(this.options.getMaxMemoryLimitInKb());
      header.position(pos);
      int nid = getUnsignedByte(header);
      if (nid == 2) {
         this.readArchiveProperties(header);
         nid = getUnsignedByte(header);
      }

      if (nid == 3) {
         throw new IOException("Additional streams unsupported");
      } else {
         if (nid == 4) {
            this.readStreamsInfo(header, archive);
            nid = getUnsignedByte(header);
         }

         if (nid == 5) {
            this.readFilesInfo(header, archive);
            nid = getUnsignedByte(header);
         }

      }
   }

   private ArchiveStatistics sanityCheckAndCollectStatistics(ByteBuffer header) throws IOException {
      ArchiveStatistics stats = new ArchiveStatistics();
      int nid = getUnsignedByte(header);
      if (nid == 2) {
         this.sanityCheckArchiveProperties(header);
         nid = getUnsignedByte(header);
      }

      if (nid == 3) {
         throw new IOException("Additional streams unsupported");
      } else {
         if (nid == 4) {
            this.sanityCheckStreamsInfo(header, stats);
            nid = getUnsignedByte(header);
         }

         if (nid == 5) {
            this.sanityCheckFilesInfo(header, stats);
            nid = getUnsignedByte(header);
         }

         if (nid != 0) {
            throw new IOException("Badly terminated header, found " + nid);
         } else {
            return stats;
         }
      }
   }

   private void readArchiveProperties(ByteBuffer input) throws IOException {
      for(int nid = getUnsignedByte(input); nid != 0; nid = getUnsignedByte(input)) {
         long propertySize = readUint64(input);
         byte[] property = new byte[(int)propertySize];
         get(input, property);
      }

   }

   private void sanityCheckArchiveProperties(ByteBuffer header) throws IOException {
      for(int nid = getUnsignedByte(header); nid != 0; nid = getUnsignedByte(header)) {
         int propertySize = assertFitsIntoNonNegativeInt("propertySize", readUint64(header));
         if (skipBytesFully(header, (long)propertySize) < (long)propertySize) {
            throw new IOException("invalid property size");
         }
      }

   }

   private ByteBuffer readEncodedHeader(ByteBuffer header, Archive archive, byte[] password) throws IOException {
      int pos = header.position();
      ArchiveStatistics stats = new ArchiveStatistics();
      this.sanityCheckStreamsInfo(header, stats);
      stats.assertValidity(this.options.getMaxMemoryLimitInKb());
      header.position(pos);
      this.readStreamsInfo(header, archive);
      if (archive.folders != null && archive.folders.length != 0) {
         if (archive.packSizes != null && archive.packSizes.length != 0) {
            Folder folder = archive.folders[0];
            int firstPackStreamIndex = false;
            long folderOffset = 32L + archive.packPos + 0L;
            this.channel.position(folderOffset);
            InputStream inputStreamStack = new BoundedSeekableByteChannelInputStream(this.channel, archive.packSizes[0]);

            Coder coder;
            for(Iterator var11 = folder.getOrderedCoders().iterator(); var11.hasNext(); inputStreamStack = Coders.addDecoder(this.fileName, (InputStream)inputStreamStack, folder.getUnpackSizeForCoder(coder), coder, password, this.options.getMaxMemoryLimitInKb())) {
               coder = (Coder)var11.next();
               if (coder.numInStreams != 1L || coder.numOutStreams != 1L) {
                  throw new IOException("Multi input/output stream coders are not yet supported");
               }
            }

            if (folder.hasCrc) {
               inputStreamStack = new CRC32VerifyingInputStream((InputStream)inputStreamStack, folder.getUnpackSize(), folder.crc);
            }

            int unpackSize = assertFitsIntoNonNegativeInt("unpackSize", folder.getUnpackSize());
            byte[] nextHeader = IOUtils.readRange((InputStream)inputStreamStack, unpackSize);
            if (nextHeader.length < unpackSize) {
               throw new IOException("premature end of stream");
            } else {
               ((InputStream)inputStreamStack).close();
               return ByteBuffer.wrap(nextHeader).order(ByteOrder.LITTLE_ENDIAN);
            }
         } else {
            throw new IOException("no packed streams, can't read encoded header");
         }
      } else {
         throw new IOException("no folders, can't read encoded header");
      }
   }

   private void sanityCheckStreamsInfo(ByteBuffer header, ArchiveStatistics stats) throws IOException {
      int nid = getUnsignedByte(header);
      if (nid == 6) {
         this.sanityCheckPackInfo(header, stats);
         nid = getUnsignedByte(header);
      }

      if (nid == 7) {
         this.sanityCheckUnpackInfo(header, stats);
         nid = getUnsignedByte(header);
      }

      if (nid == 8) {
         this.sanityCheckSubStreamsInfo(header, stats);
         nid = getUnsignedByte(header);
      }

      if (nid != 0) {
         throw new IOException("Badly terminated StreamsInfo");
      }
   }

   private void readStreamsInfo(ByteBuffer header, Archive archive) throws IOException {
      int nid = getUnsignedByte(header);
      if (nid == 6) {
         this.readPackInfo(header, archive);
         nid = getUnsignedByte(header);
      }

      if (nid == 7) {
         this.readUnpackInfo(header, archive);
         nid = getUnsignedByte(header);
      } else {
         archive.folders = Folder.EMPTY_FOLDER_ARRAY;
      }

      if (nid == 8) {
         this.readSubStreamsInfo(header, archive);
         nid = getUnsignedByte(header);
      }

   }

   private void sanityCheckPackInfo(ByteBuffer header, ArchiveStatistics stats) throws IOException {
      long packPos = readUint64(header);
      if (packPos >= 0L && 32L + packPos <= this.channel.size() && 32L + packPos >= 0L) {
         long numPackStreams = readUint64(header);
         stats.numberOfPackedStreams = assertFitsIntoNonNegativeInt("numPackStreams", numPackStreams);
         int nid = getUnsignedByte(header);
         if (nid == 9) {
            long totalPackSizes = 0L;
            int i = 0;

            while(true) {
               if (i >= stats.numberOfPackedStreams) {
                  nid = getUnsignedByte(header);
                  break;
               }

               long packSize = readUint64(header);
               totalPackSizes += packSize;
               long endOfPackStreams = 32L + packPos + totalPackSizes;
               if (packSize < 0L || endOfPackStreams > this.channel.size() || endOfPackStreams < packPos) {
                  throw new IOException("packSize (" + packSize + ") is out of range");
               }

               ++i;
            }
         }

         if (nid == 10) {
            int crcsDefined = this.readAllOrBits(header, stats.numberOfPackedStreams).cardinality();
            if (skipBytesFully(header, (long)(4 * crcsDefined)) < (long)(4 * crcsDefined)) {
               throw new IOException("invalid number of CRCs in PackInfo");
            }

            nid = getUnsignedByte(header);
         }

         if (nid != 0) {
            throw new IOException("Badly terminated PackInfo (" + nid + ")");
         }
      } else {
         throw new IOException("packPos (" + packPos + ") is out of range");
      }
   }

   private void readPackInfo(ByteBuffer header, Archive archive) throws IOException {
      archive.packPos = readUint64(header);
      int numPackStreamsInt = (int)readUint64(header);
      int nid = getUnsignedByte(header);
      int i;
      if (nid == 9) {
         archive.packSizes = new long[numPackStreamsInt];

         for(i = 0; i < archive.packSizes.length; ++i) {
            archive.packSizes[i] = readUint64(header);
         }

         nid = getUnsignedByte(header);
      }

      if (nid == 10) {
         archive.packCrcsDefined = this.readAllOrBits(header, numPackStreamsInt);
         archive.packCrcs = new long[numPackStreamsInt];

         for(i = 0; i < numPackStreamsInt; ++i) {
            if (archive.packCrcsDefined.get(i)) {
               archive.packCrcs[i] = 4294967295L & (long)getInt(header);
            }
         }

         nid = getUnsignedByte(header);
      }

   }

   private void sanityCheckUnpackInfo(ByteBuffer header, ArchiveStatistics stats) throws IOException {
      int nid = getUnsignedByte(header);
      if (nid != 11) {
         throw new IOException("Expected kFolder, got " + nid);
      } else {
         long numFolders = readUint64(header);
         stats.numberOfFolders = assertFitsIntoNonNegativeInt("numFolders", numFolders);
         int external = getUnsignedByte(header);
         if (external != 0) {
            throw new IOException("External unsupported");
         } else {
            List<Integer> numberOfOutputStreamsPerFolder = new LinkedList();

            for(int i = 0; i < stats.numberOfFolders; ++i) {
               numberOfOutputStreamsPerFolder.add(this.sanityCheckFolder(header, stats));
            }

            long totalNumberOfBindPairs = stats.numberOfOutStreams - (long)stats.numberOfFolders;
            long packedStreamsRequiredByFolders = stats.numberOfInStreams - totalNumberOfBindPairs;
            if (packedStreamsRequiredByFolders < (long)stats.numberOfPackedStreams) {
               throw new IOException("archive doesn't contain enough packed streams");
            } else {
               nid = getUnsignedByte(header);
               if (nid != 12) {
                  throw new IOException("Expected kCodersUnpackSize, got " + nid);
               } else {
                  Iterator var12 = numberOfOutputStreamsPerFolder.iterator();

                  while(var12.hasNext()) {
                     int numberOfOutputStreams = (Integer)var12.next();

                     for(int i = 0; i < numberOfOutputStreams; ++i) {
                        long unpackSize = readUint64(header);
                        if (unpackSize < 0L) {
                           throw new IllegalArgumentException("negative unpackSize");
                        }
                     }
                  }

                  nid = getUnsignedByte(header);
                  if (nid == 10) {
                     stats.folderHasCrc = this.readAllOrBits(header, stats.numberOfFolders);
                     int crcsDefined = stats.folderHasCrc.cardinality();
                     if (skipBytesFully(header, (long)(4 * crcsDefined)) < (long)(4 * crcsDefined)) {
                        throw new IOException("invalid number of CRCs in UnpackInfo");
                     }

                     nid = getUnsignedByte(header);
                  }

                  if (nid != 0) {
                     throw new IOException("Badly terminated UnpackInfo");
                  }
               }
            }
         }
      }
   }

   private void readUnpackInfo(ByteBuffer header, Archive archive) throws IOException {
      int nid = getUnsignedByte(header);
      int numFoldersInt = (int)readUint64(header);
      Folder[] folders = new Folder[numFoldersInt];
      archive.folders = folders;
      getUnsignedByte(header);

      for(int i = 0; i < numFoldersInt; ++i) {
         folders[i] = this.readFolder(header);
      }

      nid = getUnsignedByte(header);
      Folder[] var11 = folders;
      int i = folders.length;

      for(int var8 = 0; var8 < i; ++var8) {
         Folder folder = var11[var8];
         assertFitsIntoNonNegativeInt("totalOutputStreams", folder.totalOutputStreams);
         folder.unpackSizes = new long[(int)folder.totalOutputStreams];

         for(int i = 0; (long)i < folder.totalOutputStreams; ++i) {
            folder.unpackSizes[i] = readUint64(header);
         }
      }

      nid = getUnsignedByte(header);
      if (nid == 10) {
         BitSet crcsDefined = this.readAllOrBits(header, numFoldersInt);

         for(i = 0; i < numFoldersInt; ++i) {
            if (crcsDefined.get(i)) {
               folders[i].hasCrc = true;
               folders[i].crc = 4294967295L & (long)getInt(header);
            } else {
               folders[i].hasCrc = false;
            }
         }

         nid = getUnsignedByte(header);
      }

   }

   private void sanityCheckSubStreamsInfo(ByteBuffer header, ArchiveStatistics stats) throws IOException {
      int nid = getUnsignedByte(header);
      List<Integer> numUnpackSubStreamsPerFolder = new LinkedList();
      int numDigests;
      if (nid == 13) {
         for(numDigests = 0; numDigests < stats.numberOfFolders; ++numDigests) {
            numUnpackSubStreamsPerFolder.add(assertFitsIntoNonNegativeInt("numStreams", readUint64(header)));
         }

         stats.numberOfUnpackSubStreams = (Long)numUnpackSubStreamsPerFolder.stream().collect(Collectors.summingLong(Integer::longValue));
         nid = getUnsignedByte(header);
      } else {
         stats.numberOfUnpackSubStreams = (long)stats.numberOfFolders;
      }

      assertFitsIntoNonNegativeInt("totalUnpackStreams", stats.numberOfUnpackSubStreams);
      int missingCrcs;
      if (nid == 9) {
         Iterator var10 = numUnpackSubStreamsPerFolder.iterator();

         label78:
         while(true) {
            do {
               if (!var10.hasNext()) {
                  nid = getUnsignedByte(header);
                  break label78;
               }

               missingCrcs = (Integer)var10.next();
            } while(missingCrcs == 0);

            for(int i = 0; i < missingCrcs - 1; ++i) {
               long size = readUint64(header);
               if (size < 0L) {
                  throw new IOException("negative unpackSize");
               }
            }
         }
      }

      numDigests = 0;
      if (numUnpackSubStreamsPerFolder.isEmpty()) {
         numDigests = stats.folderHasCrc == null ? stats.numberOfFolders : stats.numberOfFolders - stats.folderHasCrc.cardinality();
      } else {
         missingCrcs = 0;
         Iterator var11 = numUnpackSubStreamsPerFolder.iterator();

         label62:
         while(true) {
            int numUnpackSubStreams;
            do {
               if (!var11.hasNext()) {
                  break label62;
               }

               numUnpackSubStreams = (Integer)var11.next();
            } while(numUnpackSubStreams == 1 && stats.folderHasCrc != null && stats.folderHasCrc.get(missingCrcs++));

            numDigests += numUnpackSubStreams;
         }
      }

      if (nid == 10) {
         assertFitsIntoNonNegativeInt("numDigests", (long)numDigests);
         missingCrcs = this.readAllOrBits(header, numDigests).cardinality();
         if (skipBytesFully(header, (long)(4 * missingCrcs)) < (long)(4 * missingCrcs)) {
            throw new IOException("invalid number of missing CRCs in SubStreamInfo");
         }

         nid = getUnsignedByte(header);
      }

      if (nid != 0) {
         throw new IOException("Badly terminated SubStreamsInfo");
      }
   }

   private void readSubStreamsInfo(ByteBuffer header, Archive archive) throws IOException {
      Folder[] var3 = archive.folders;
      int var4 = var3.length;

      int nid;
      for(nid = 0; nid < var4; ++nid) {
         Folder folder = var3[nid];
         folder.numUnpackSubStreams = 1;
      }

      long unpackStreamsCount = (long)archive.folders.length;
      nid = getUnsignedByte(header);
      int nextUnpackStream;
      if (nid == 13) {
         unpackStreamsCount = 0L;
         Folder[] var20 = archive.folders;
         int var7 = var20.length;

         for(nextUnpackStream = 0; nextUnpackStream < var7; ++nextUnpackStream) {
            Folder folder = var20[nextUnpackStream];
            long numStreams = readUint64(header);
            folder.numUnpackSubStreams = (int)numStreams;
            unpackStreamsCount += numStreams;
         }

         nid = getUnsignedByte(header);
      }

      int totalUnpackStreams = (int)unpackStreamsCount;
      SubStreamsInfo subStreamsInfo = new SubStreamsInfo();
      subStreamsInfo.unpackSizes = new long[totalUnpackStreams];
      subStreamsInfo.hasCrc = new BitSet(totalUnpackStreams);
      subStreamsInfo.crcs = new long[totalUnpackStreams];
      nextUnpackStream = 0;
      Folder[] var23 = archive.folders;
      int var25 = var23.length;

      int var11;
      int i;
      for(var11 = 0; var11 < var25; ++var11) {
         Folder folder = var23[var11];
         if (folder.numUnpackSubStreams != 0) {
            long sum = 0L;
            if (nid == 9) {
               for(i = 0; i < folder.numUnpackSubStreams - 1; ++i) {
                  long size = readUint64(header);
                  subStreamsInfo.unpackSizes[nextUnpackStream++] = size;
                  sum += size;
               }
            }

            if (sum > folder.getUnpackSize()) {
               throw new IOException("sum of unpack sizes of folder exceeds total unpack size");
            }

            subStreamsInfo.unpackSizes[nextUnpackStream++] = folder.getUnpackSize() - sum;
         }
      }

      if (nid == 9) {
         nid = getUnsignedByte(header);
      }

      int numDigests = 0;
      Folder[] var26 = archive.folders;
      var11 = var26.length;

      int nextCrc;
      for(nextCrc = 0; nextCrc < var11; ++nextCrc) {
         Folder folder = var26[nextCrc];
         if (folder.numUnpackSubStreams != 1 || !folder.hasCrc) {
            numDigests += folder.numUnpackSubStreams;
         }
      }

      if (nid == 10) {
         BitSet hasMissingCrc = this.readAllOrBits(header, numDigests);
         long[] missingCrcs = new long[numDigests];

         for(nextCrc = 0; nextCrc < numDigests; ++nextCrc) {
            if (hasMissingCrc.get(nextCrc)) {
               missingCrcs[nextCrc] = 4294967295L & (long)getInt(header);
            }
         }

         nextCrc = 0;
         int nextMissingCrc = 0;
         Folder[] var14 = archive.folders;
         i = var14.length;

         for(int var32 = 0; var32 < i; ++var32) {
            Folder folder = var14[var32];
            if (folder.numUnpackSubStreams == 1 && folder.hasCrc) {
               subStreamsInfo.hasCrc.set(nextCrc, true);
               subStreamsInfo.crcs[nextCrc] = folder.crc;
               ++nextCrc;
            } else {
               for(int i = 0; i < folder.numUnpackSubStreams; ++i) {
                  subStreamsInfo.hasCrc.set(nextCrc, hasMissingCrc.get(nextMissingCrc));
                  subStreamsInfo.crcs[nextCrc] = missingCrcs[nextMissingCrc];
                  ++nextCrc;
                  ++nextMissingCrc;
               }
            }
         }

         nid = getUnsignedByte(header);
      }

      archive.subStreamsInfo = subStreamsInfo;
   }

   private int sanityCheckFolder(ByteBuffer header, ArchiveStatistics stats) throws IOException {
      int numCoders = assertFitsIntoNonNegativeInt("numCoders", readUint64(header));
      if (numCoders == 0) {
         throw new IOException("Folder without coders");
      } else {
         stats.numberOfCoders = stats.numberOfCoders + (long)numCoders;
         long totalOutStreams = 0L;
         long totalInStreams = 0L;

         int numBindPairs;
         int numPackedStreams;
         for(numBindPairs = 0; numBindPairs < numCoders; ++numBindPairs) {
            int bits = getUnsignedByte(header);
            numPackedStreams = bits & 15;
            get(header, new byte[numPackedStreams]);
            boolean isSimple = (bits & 16) == 0;
            boolean hasAttributes = (bits & 32) != 0;
            boolean moreAlternativeMethods = (bits & 128) != 0;
            if (moreAlternativeMethods) {
               throw new IOException("Alternative methods are unsupported, please report. The reference implementation doesn't support them either.");
            }

            if (isSimple) {
               ++totalInStreams;
               ++totalOutStreams;
            } else {
               totalInStreams += (long)assertFitsIntoNonNegativeInt("numInStreams", readUint64(header));
               totalOutStreams += (long)assertFitsIntoNonNegativeInt("numOutStreams", readUint64(header));
            }

            if (hasAttributes) {
               int propertiesSize = assertFitsIntoNonNegativeInt("propertiesSize", readUint64(header));
               if (skipBytesFully(header, (long)propertiesSize) < (long)propertiesSize) {
                  throw new IOException("invalid propertiesSize in folder");
               }
            }
         }

         assertFitsIntoNonNegativeInt("totalInStreams", totalInStreams);
         assertFitsIntoNonNegativeInt("totalOutStreams", totalOutStreams);
         stats.numberOfOutStreams = stats.numberOfOutStreams + totalOutStreams;
         stats.numberOfInStreams = stats.numberOfInStreams + totalInStreams;
         if (totalOutStreams == 0L) {
            throw new IOException("Total output streams can't be 0");
         } else {
            numBindPairs = assertFitsIntoNonNegativeInt("numBindPairs", totalOutStreams - 1L);
            if (totalInStreams < (long)numBindPairs) {
               throw new IOException("Total input streams can't be less than the number of bind pairs");
            } else {
               BitSet inStreamsBound = new BitSet((int)totalInStreams);

               int i;
               int packedStreamIndex;
               for(numPackedStreams = 0; numPackedStreams < numBindPairs; ++numPackedStreams) {
                  i = assertFitsIntoNonNegativeInt("inIndex", readUint64(header));
                  if (totalInStreams <= (long)i) {
                     throw new IOException("inIndex is bigger than number of inStreams");
                  }

                  inStreamsBound.set(i);
                  packedStreamIndex = assertFitsIntoNonNegativeInt("outIndex", readUint64(header));
                  if (totalOutStreams <= (long)packedStreamIndex) {
                     throw new IOException("outIndex is bigger than number of outStreams");
                  }
               }

               numPackedStreams = assertFitsIntoNonNegativeInt("numPackedStreams", totalInStreams - (long)numBindPairs);
               if (numPackedStreams == 1) {
                  if (inStreamsBound.nextClearBit(0) == -1) {
                     throw new IOException("Couldn't find stream's bind pair index");
                  }
               } else {
                  for(i = 0; i < numPackedStreams; ++i) {
                     packedStreamIndex = assertFitsIntoNonNegativeInt("packedStreamIndex", readUint64(header));
                     if ((long)packedStreamIndex >= totalInStreams) {
                        throw new IOException("packedStreamIndex is bigger than number of totalInStreams");
                     }
                  }
               }

               return (int)totalOutStreams;
            }
         }
      }
   }

   private Folder readFolder(ByteBuffer header) throws IOException {
      Folder folder = new Folder();
      long numCoders = readUint64(header);
      Coder[] coders = new Coder[(int)numCoders];
      long totalInStreams = 0L;
      long totalOutStreams = 0L;

      for(int i = 0; i < coders.length; ++i) {
         coders[i] = new Coder();
         int bits = getUnsignedByte(header);
         int idSize = bits & 15;
         boolean isSimple = (bits & 16) == 0;
         boolean hasAttributes = (bits & 32) != 0;
         boolean moreAlternativeMethods = (bits & 128) != 0;
         coders[i].decompressionMethodId = new byte[idSize];
         get(header, coders[i].decompressionMethodId);
         if (isSimple) {
            coders[i].numInStreams = 1L;
            coders[i].numOutStreams = 1L;
         } else {
            coders[i].numInStreams = readUint64(header);
            coders[i].numOutStreams = readUint64(header);
         }

         totalInStreams += coders[i].numInStreams;
         totalOutStreams += coders[i].numOutStreams;
         if (hasAttributes) {
            long propertiesSize = readUint64(header);
            coders[i].properties = new byte[(int)propertiesSize];
            get(header, coders[i].properties);
         }

         if (moreAlternativeMethods) {
            throw new IOException("Alternative methods are unsupported, please report. The reference implementation doesn't support them either.");
         }
      }

      folder.coders = coders;
      folder.totalInputStreams = totalInStreams;
      folder.totalOutputStreams = totalOutStreams;
      long numBindPairs = totalOutStreams - 1L;
      BindPair[] bindPairs = new BindPair[(int)numBindPairs];

      for(int i = 0; i < bindPairs.length; ++i) {
         bindPairs[i] = new BindPair();
         bindPairs[i].inIndex = readUint64(header);
         bindPairs[i].outIndex = readUint64(header);
      }

      folder.bindPairs = bindPairs;
      long numPackedStreams = totalInStreams - numBindPairs;
      long[] packedStreams = new long[(int)numPackedStreams];
      int i;
      if (numPackedStreams == 1L) {
         for(i = 0; i < (int)totalInStreams && folder.findBindPairForInStream(i) >= 0; ++i) {
         }

         packedStreams[0] = (long)i;
      } else {
         for(i = 0; i < (int)numPackedStreams; ++i) {
            packedStreams[i] = readUint64(header);
         }
      }

      folder.packedStreams = packedStreams;
      return folder;
   }

   private BitSet readAllOrBits(ByteBuffer header, int size) throws IOException {
      int areAllDefined = getUnsignedByte(header);
      BitSet bits;
      if (areAllDefined != 0) {
         bits = new BitSet(size);

         for(int i = 0; i < size; ++i) {
            bits.set(i, true);
         }
      } else {
         bits = this.readBits(header, size);
      }

      return bits;
   }

   private BitSet readBits(ByteBuffer header, int size) throws IOException {
      BitSet bits = new BitSet(size);
      int mask = 0;
      int cache = 0;

      for(int i = 0; i < size; ++i) {
         if (mask == 0) {
            mask = 128;
            cache = getUnsignedByte(header);
         }

         bits.set(i, (cache & mask) != 0);
         mask >>>= 1;
      }

      return bits;
   }

   private void sanityCheckFilesInfo(ByteBuffer header, ArchiveStatistics stats) throws IOException {
      stats.numberOfEntries = assertFitsIntoNonNegativeInt("numFiles", readUint64(header));
      int emptyStreams = -1;

      while(true) {
         int propertyType = getUnsignedByte(header);
         if (propertyType == 0) {
            stats.numberOfEntriesWithStream = stats.numberOfEntries - (emptyStreams > 0 ? emptyStreams : 0);
            return;
         }

         long size = readUint64(header);
         int attributesDefined;
         int namesLength;
         switch (propertyType) {
            case 14:
               emptyStreams = this.readBits(header, stats.numberOfEntries).cardinality();
               break;
            case 15:
               if (emptyStreams == -1) {
                  throw new IOException("Header format error: kEmptyStream must appear before kEmptyFile");
               }

               this.readBits(header, emptyStreams);
               break;
            case 16:
               if (emptyStreams == -1) {
                  throw new IOException("Header format error: kEmptyStream must appear before kAnti");
               }

               this.readBits(header, emptyStreams);
               break;
            case 17:
               attributesDefined = getUnsignedByte(header);
               if (attributesDefined != 0) {
                  throw new IOException("Not implemented");
               }

               namesLength = assertFitsIntoNonNegativeInt("file names length", size - 1L);
               if ((namesLength & 1) != 0) {
                  throw new IOException("File names length invalid");
               }

               int filesSeen = 0;
               int i = 0;

               for(; i < namesLength; i += 2) {
                  char c = getChar(header);
                  if (c == 0) {
                     ++filesSeen;
                  }
               }

               if (filesSeen != stats.numberOfEntries) {
                  throw new IOException("Invalid number of file names (" + filesSeen + " instead of " + stats.numberOfEntries + ")");
               }
               break;
            case 18:
               attributesDefined = this.readAllOrBits(header, stats.numberOfEntries).cardinality();
               namesLength = getUnsignedByte(header);
               if (namesLength != 0) {
                  throw new IOException("Not implemented");
               }

               if (skipBytesFully(header, (long)(8 * attributesDefined)) < (long)(8 * attributesDefined)) {
                  throw new IOException("invalid creation dates size");
               }
               break;
            case 19:
               attributesDefined = this.readAllOrBits(header, stats.numberOfEntries).cardinality();
               namesLength = getUnsignedByte(header);
               if (namesLength != 0) {
                  throw new IOException("Not implemented");
               }

               if (skipBytesFully(header, (long)(8 * attributesDefined)) < (long)(8 * attributesDefined)) {
                  throw new IOException("invalid access dates size");
               }
               break;
            case 20:
               attributesDefined = this.readAllOrBits(header, stats.numberOfEntries).cardinality();
               namesLength = getUnsignedByte(header);
               if (namesLength != 0) {
                  throw new IOException("Not implemented");
               }

               if (skipBytesFully(header, (long)(8 * attributesDefined)) < (long)(8 * attributesDefined)) {
                  throw new IOException("invalid modification dates size");
               }
               break;
            case 21:
               attributesDefined = this.readAllOrBits(header, stats.numberOfEntries).cardinality();
               namesLength = getUnsignedByte(header);
               if (namesLength != 0) {
                  throw new IOException("Not implemented");
               }

               if (skipBytesFully(header, (long)(4 * attributesDefined)) < (long)(4 * attributesDefined)) {
                  throw new IOException("invalid windows attributes size");
               }
               break;
            case 22:
            case 23:
            default:
               if (skipBytesFully(header, size) < size) {
                  throw new IOException("Incomplete property of type " + propertyType);
               }
               break;
            case 24:
               throw new IOException("kStartPos is unsupported, please report");
            case 25:
               if (skipBytesFully(header, size) < size) {
                  throw new IOException("Incomplete kDummy property");
               }
         }
      }
   }

   private void readFilesInfo(ByteBuffer header, Archive archive) throws IOException {
      int numFilesInt = (int)readUint64(header);
      Map<Integer, SevenZArchiveEntry> fileMap = new HashMap();
      BitSet isEmptyStream = null;
      BitSet isEmptyFile = null;
      BitSet isAnti = null;

      int i;
      int nextName;
      int nextFile;
      label156:
      do {
         label144:
         while(true) {
            int nonEmptyFileCounter = getUnsignedByte(header);
            if (nonEmptyFileCounter == 0) {
               nonEmptyFileCounter = 0;
               int emptyFileCounter = 0;

               for(int i = 0; i < numFilesInt; ++i) {
                  SevenZArchiveEntry entryAtIndex = (SevenZArchiveEntry)fileMap.get(i);
                  if (entryAtIndex != null) {
                     entryAtIndex.setHasStream(isEmptyStream == null || !isEmptyStream.get(i));
                     if (entryAtIndex.hasStream()) {
                        if (archive.subStreamsInfo == null) {
                           throw new IOException("Archive contains file with streams but no subStreamsInfo");
                        }

                        entryAtIndex.setDirectory(false);
                        entryAtIndex.setAntiItem(false);
                        entryAtIndex.setHasCrc(archive.subStreamsInfo.hasCrc.get(nonEmptyFileCounter));
                        entryAtIndex.setCrcValue(archive.subStreamsInfo.crcs[nonEmptyFileCounter]);
                        entryAtIndex.setSize(archive.subStreamsInfo.unpackSizes[nonEmptyFileCounter]);
                        if (entryAtIndex.getSize() < 0L) {
                           throw new IOException("broken archive, entry with negative size");
                        }

                        ++nonEmptyFileCounter;
                     } else {
                        entryAtIndex.setDirectory(isEmptyFile == null || !isEmptyFile.get(emptyFileCounter));
                        entryAtIndex.setAntiItem(isAnti != null && isAnti.get(emptyFileCounter));
                        entryAtIndex.setHasCrc(false);
                        entryAtIndex.setSize(0L);
                        ++emptyFileCounter;
                     }
                  }
               }

               List<SevenZArchiveEntry> entries = new ArrayList();
               Iterator var20 = fileMap.values().iterator();

               while(var20.hasNext()) {
                  SevenZArchiveEntry e = (SevenZArchiveEntry)var20.next();
                  if (e != null) {
                     entries.add(e);
                  }
               }

               archive.files = (SevenZArchiveEntry[])entries.toArray(SevenZArchiveEntry.EMPTY_SEVEN_Z_ARCHIVE_ENTRY_ARRAY);
               this.calculateStreamMap(archive);
               return;
            }

            long size = readUint64(header);
            BitSet attributesDefined;
            SevenZArchiveEntry entryAtIndex;
            switch (nonEmptyFileCounter) {
               case 14:
                  isEmptyStream = this.readBits(header, numFilesInt);
                  break;
               case 15:
                  isEmptyFile = this.readBits(header, isEmptyStream.cardinality());
                  break;
               case 16:
                  isAnti = this.readBits(header, isEmptyStream.cardinality());
                  break;
               case 17:
                  getUnsignedByte(header);
                  byte[] names = new byte[(int)(size - 1L)];
                  i = names.length;
                  get(header, names);
                  nextFile = 0;
                  nextName = 0;

                  for(int i = 0; i < i; i += 2) {
                     if (names[i] == 0 && names[i + 1] == 0) {
                        this.checkEntryIsInitialized(fileMap, nextFile);
                        ((SevenZArchiveEntry)fileMap.get(nextFile)).setName(new String(names, nextName, i - nextName, StandardCharsets.UTF_16LE));
                        nextName = i + 2;
                        ++nextFile;
                     }
                  }
                  continue label156;
               case 18:
                  attributesDefined = this.readAllOrBits(header, numFilesInt);
                  getUnsignedByte(header);
                  i = 0;

                  while(true) {
                     if (i >= numFilesInt) {
                        continue label144;
                     }

                     this.checkEntryIsInitialized(fileMap, i);
                     entryAtIndex = (SevenZArchiveEntry)fileMap.get(i);
                     entryAtIndex.setHasCreationDate(attributesDefined.get(i));
                     if (entryAtIndex.getHasCreationDate()) {
                        entryAtIndex.setCreationDate(getLong(header));
                     }

                     ++i;
                  }
               case 19:
                  attributesDefined = this.readAllOrBits(header, numFilesInt);
                  getUnsignedByte(header);
                  i = 0;

                  while(true) {
                     if (i >= numFilesInt) {
                        continue label144;
                     }

                     this.checkEntryIsInitialized(fileMap, i);
                     entryAtIndex = (SevenZArchiveEntry)fileMap.get(i);
                     entryAtIndex.setHasAccessDate(attributesDefined.get(i));
                     if (entryAtIndex.getHasAccessDate()) {
                        entryAtIndex.setAccessDate(getLong(header));
                     }

                     ++i;
                  }
               case 20:
                  attributesDefined = this.readAllOrBits(header, numFilesInt);
                  getUnsignedByte(header);
                  i = 0;

                  while(true) {
                     if (i >= numFilesInt) {
                        continue label144;
                     }

                     this.checkEntryIsInitialized(fileMap, i);
                     entryAtIndex = (SevenZArchiveEntry)fileMap.get(i);
                     entryAtIndex.setHasLastModifiedDate(attributesDefined.get(i));
                     if (entryAtIndex.getHasLastModifiedDate()) {
                        entryAtIndex.setLastModifiedDate(getLong(header));
                     }

                     ++i;
                  }
               case 21:
                  attributesDefined = this.readAllOrBits(header, numFilesInt);
                  getUnsignedByte(header);
                  i = 0;

                  while(true) {
                     if (i >= numFilesInt) {
                        continue label144;
                     }

                     this.checkEntryIsInitialized(fileMap, i);
                     entryAtIndex = (SevenZArchiveEntry)fileMap.get(i);
                     entryAtIndex.setHasWindowsAttributes(attributesDefined.get(i));
                     if (entryAtIndex.getHasWindowsAttributes()) {
                        entryAtIndex.setWindowsAttributes(getInt(header));
                     }

                     ++i;
                  }
               case 22:
               case 23:
               case 24:
               default:
                  skipBytesFully(header, size);
                  break;
               case 25:
                  skipBytesFully(header, size);
            }
         }
      } while(nextName == i && nextFile == numFilesInt);

      throw new IOException("Error parsing file names");
   }

   private void checkEntryIsInitialized(Map<Integer, SevenZArchiveEntry> archiveEntries, int index) {
      if (archiveEntries.get(index) == null) {
         archiveEntries.put(index, new SevenZArchiveEntry());
      }

   }

   private void calculateStreamMap(Archive archive) throws IOException {
      StreamMap streamMap = new StreamMap();
      int nextFolderPackStreamIndex = 0;
      int numFolders = archive.folders != null ? archive.folders.length : 0;
      streamMap.folderFirstPackStreamIndex = new int[numFolders];

      for(int i = 0; i < numFolders; ++i) {
         streamMap.folderFirstPackStreamIndex[i] = nextFolderPackStreamIndex;
         nextFolderPackStreamIndex += archive.folders[i].packedStreams.length;
      }

      long nextPackStreamOffset = 0L;
      int numPackSizes = archive.packSizes.length;
      streamMap.packStreamOffsets = new long[numPackSizes];

      int nextFolderIndex;
      for(nextFolderIndex = 0; nextFolderIndex < numPackSizes; ++nextFolderIndex) {
         streamMap.packStreamOffsets[nextFolderIndex] = nextPackStreamOffset;
         nextPackStreamOffset += archive.packSizes[nextFolderIndex];
      }

      streamMap.folderFirstFileIndex = new int[numFolders];
      streamMap.fileFolderIndex = new int[archive.files.length];
      nextFolderIndex = 0;
      int nextFolderUnpackStreamIndex = 0;

      for(int i = 0; i < archive.files.length; ++i) {
         if (!archive.files[i].hasStream() && nextFolderUnpackStreamIndex == 0) {
            streamMap.fileFolderIndex[i] = -1;
         } else {
            if (nextFolderUnpackStreamIndex == 0) {
               while(nextFolderIndex < archive.folders.length) {
                  streamMap.folderFirstFileIndex[nextFolderIndex] = i;
                  if (archive.folders[nextFolderIndex].numUnpackSubStreams > 0) {
                     break;
                  }

                  ++nextFolderIndex;
               }

               if (nextFolderIndex >= archive.folders.length) {
                  throw new IOException("Too few folders in archive");
               }
            }

            streamMap.fileFolderIndex[i] = nextFolderIndex;
            if (archive.files[i].hasStream()) {
               ++nextFolderUnpackStreamIndex;
               if (nextFolderUnpackStreamIndex >= archive.folders[nextFolderIndex].numUnpackSubStreams) {
                  ++nextFolderIndex;
                  nextFolderUnpackStreamIndex = 0;
               }
            }
         }
      }

      archive.streamMap = streamMap;
   }

   private void buildDecodingStream(int entryIndex, boolean isRandomAccess) throws IOException {
      if (this.archive.streamMap == null) {
         throw new IOException("Archive doesn't contain stream information to read entries");
      } else {
         int folderIndex = this.archive.streamMap.fileFolderIndex[entryIndex];
         if (folderIndex < 0) {
            this.deferredBlockStreams.clear();
         } else {
            SevenZArchiveEntry file = this.archive.files[entryIndex];
            boolean isInSameFolder = false;
            if (this.currentFolderIndex == folderIndex) {
               if (entryIndex > 0) {
                  file.setContentMethods(this.archive.files[entryIndex - 1].getContentMethods());
               }

               if (isRandomAccess && file.getContentMethods() == null) {
                  int folderFirstFileIndex = this.archive.streamMap.folderFirstFileIndex[folderIndex];
                  SevenZArchiveEntry folderFirstFile = this.archive.files[folderFirstFileIndex];
                  file.setContentMethods(folderFirstFile.getContentMethods());
               }

               isInSameFolder = true;
            } else {
               this.currentFolderIndex = folderIndex;
               this.reopenFolderInputStream(folderIndex, file);
            }

            boolean haveSkippedEntries = false;
            if (isRandomAccess) {
               haveSkippedEntries = this.skipEntriesWhenNeeded(entryIndex, isInSameFolder, folderIndex);
            }

            if (!isRandomAccess || this.currentEntryIndex != entryIndex || haveSkippedEntries) {
               InputStream fileStream = new BoundedInputStream(this.currentFolderInputStream, file.getSize());
               if (file.getHasCrc()) {
                  fileStream = new CRC32VerifyingInputStream((InputStream)fileStream, file.getSize(), file.getCrcValue());
               }

               this.deferredBlockStreams.add(fileStream);
            }
         }
      }
   }

   private void reopenFolderInputStream(int folderIndex, SevenZArchiveEntry file) throws IOException {
      this.deferredBlockStreams.clear();
      if (this.currentFolderInputStream != null) {
         this.currentFolderInputStream.close();
         this.currentFolderInputStream = null;
      }

      Folder folder = this.archive.folders[folderIndex];
      int firstPackStreamIndex = this.archive.streamMap.folderFirstPackStreamIndex[folderIndex];
      long folderOffset = 32L + this.archive.packPos + this.archive.streamMap.packStreamOffsets[firstPackStreamIndex];
      this.currentFolderInputStream = this.buildDecoderStack(folder, folderOffset, firstPackStreamIndex, file);
   }

   private boolean skipEntriesWhenNeeded(int entryIndex, boolean isInSameFolder, int folderIndex) throws IOException {
      SevenZArchiveEntry file = this.archive.files[entryIndex];
      if (this.currentEntryIndex == entryIndex && !this.hasCurrentEntryBeenRead()) {
         return false;
      } else {
         int filesToSkipStartIndex = this.archive.streamMap.folderFirstFileIndex[this.currentFolderIndex];
         if (isInSameFolder) {
            if (this.currentEntryIndex < entryIndex) {
               filesToSkipStartIndex = this.currentEntryIndex + 1;
            } else {
               this.reopenFolderInputStream(folderIndex, file);
            }
         }

         for(int i = filesToSkipStartIndex; i < entryIndex; ++i) {
            SevenZArchiveEntry fileToSkip = this.archive.files[i];
            InputStream fileStreamToSkip = new BoundedInputStream(this.currentFolderInputStream, fileToSkip.getSize());
            if (fileToSkip.getHasCrc()) {
               fileStreamToSkip = new CRC32VerifyingInputStream((InputStream)fileStreamToSkip, fileToSkip.getSize(), fileToSkip.getCrcValue());
            }

            this.deferredBlockStreams.add(fileStreamToSkip);
            fileToSkip.setContentMethods(file.getContentMethods());
         }

         return true;
      }
   }

   private boolean hasCurrentEntryBeenRead() {
      boolean hasCurrentEntryBeenRead = false;
      if (!this.deferredBlockStreams.isEmpty()) {
         InputStream currentEntryInputStream = (InputStream)this.deferredBlockStreams.get(this.deferredBlockStreams.size() - 1);
         if (currentEntryInputStream instanceof CRC32VerifyingInputStream) {
            hasCurrentEntryBeenRead = ((CRC32VerifyingInputStream)currentEntryInputStream).getBytesRemaining() != this.archive.files[this.currentEntryIndex].getSize();
         }

         if (currentEntryInputStream instanceof BoundedInputStream) {
            hasCurrentEntryBeenRead = ((BoundedInputStream)currentEntryInputStream).getBytesRemaining() != this.archive.files[this.currentEntryIndex].getSize();
         }
      }

      return hasCurrentEntryBeenRead;
   }

   private InputStream buildDecoderStack(Folder folder, long folderOffset, int firstPackStreamIndex, SevenZArchiveEntry entry) throws IOException {
      this.channel.position(folderOffset);
      InputStream inputStreamStack = new FilterInputStream(new BufferedInputStream(new BoundedSeekableByteChannelInputStream(this.channel, this.archive.packSizes[firstPackStreamIndex]))) {
         public int read() throws IOException {
            int r = this.in.read();
            if (r >= 0) {
               this.count(1);
            }

            return r;
         }

         public int read(byte[] b) throws IOException {
            return this.read(b, 0, b.length);
         }

         public int read(byte[] b, int off, int len) throws IOException {
            if (len == 0) {
               return 0;
            } else {
               int r = this.in.read(b, off, len);
               if (r >= 0) {
                  this.count(r);
               }

               return r;
            }
         }

         private void count(int c) {
            SevenZFile.this.compressedBytesReadFromCurrentEntry = SevenZFile.this.compressedBytesReadFromCurrentEntry + (long)c;
         }
      };
      LinkedList<SevenZMethodConfiguration> methods = new LinkedList();
      Iterator var8 = folder.getOrderedCoders().iterator();

      while(var8.hasNext()) {
         Coder coder = (Coder)var8.next();
         if (coder.numInStreams != 1L || coder.numOutStreams != 1L) {
            throw new IOException("Multi input/output stream coders are not yet supported");
         }

         SevenZMethod method = SevenZMethod.byId(coder.decompressionMethodId);
         inputStreamStack = Coders.addDecoder(this.fileName, (InputStream)inputStreamStack, folder.getUnpackSizeForCoder(coder), coder, this.password, this.options.getMaxMemoryLimitInKb());
         methods.addFirst(new SevenZMethodConfiguration(method, Coders.findByMethod(method).getOptionsFromCoder(coder, (InputStream)inputStreamStack)));
      }

      entry.setContentMethods(methods);
      if (folder.hasCrc) {
         return new CRC32VerifyingInputStream((InputStream)inputStreamStack, folder.getUnpackSize(), folder.crc);
      } else {
         return (InputStream)inputStreamStack;
      }
   }

   public int read() throws IOException {
      int b = this.getCurrentStream().read();
      if (b >= 0) {
         ++this.uncompressedBytesReadFromCurrentEntry;
      }

      return b;
   }

   private InputStream getCurrentStream() throws IOException {
      if (this.archive.files[this.currentEntryIndex].getSize() == 0L) {
         return new ByteArrayInputStream(ByteUtils.EMPTY_BYTE_ARRAY);
      } else if (this.deferredBlockStreams.isEmpty()) {
         throw new IllegalStateException("No current 7z entry (call getNextEntry() first).");
      } else {
         for(; this.deferredBlockStreams.size() > 1; this.compressedBytesReadFromCurrentEntry = 0L) {
            InputStream stream = (InputStream)this.deferredBlockStreams.remove(0);
            Throwable var2 = null;

            try {
               IOUtils.skip(stream, Long.MAX_VALUE);
            } catch (Throwable var11) {
               var2 = var11;
               throw var11;
            } finally {
               if (stream != null) {
                  if (var2 != null) {
                     try {
                        stream.close();
                     } catch (Throwable var10) {
                        var2.addSuppressed(var10);
                     }
                  } else {
                     stream.close();
                  }
               }

            }
         }

         return (InputStream)this.deferredBlockStreams.get(0);
      }
   }

   public InputStream getInputStream(SevenZArchiveEntry entry) throws IOException {
      int entryIndex = -1;

      for(int i = 0; i < this.archive.files.length; ++i) {
         if (entry == this.archive.files[i]) {
            entryIndex = i;
            break;
         }
      }

      if (entryIndex < 0) {
         throw new IllegalArgumentException("Can not find " + entry.getName() + " in " + this.fileName);
      } else {
         this.buildDecodingStream(entryIndex, true);
         this.currentEntryIndex = entryIndex;
         this.currentFolderIndex = this.archive.streamMap.fileFolderIndex[entryIndex];
         return this.getCurrentStream();
      }
   }

   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else {
         int cnt = this.getCurrentStream().read(b, off, len);
         if (cnt > 0) {
            this.uncompressedBytesReadFromCurrentEntry += (long)cnt;
         }

         return cnt;
      }
   }

   public InputStreamStatistics getStatisticsForCurrentEntry() {
      return new InputStreamStatistics() {
         public long getCompressedCount() {
            return SevenZFile.this.compressedBytesReadFromCurrentEntry;
         }

         public long getUncompressedCount() {
            return SevenZFile.this.uncompressedBytesReadFromCurrentEntry;
         }
      };
   }

   private static long readUint64(ByteBuffer in) throws IOException {
      long firstByte = (long)getUnsignedByte(in);
      int mask = 128;
      long value = 0L;

      for(int i = 0; i < 8; ++i) {
         if ((firstByte & (long)mask) == 0L) {
            return value | (firstByte & (long)(mask - 1)) << 8 * i;
         }

         long nextByte = (long)getUnsignedByte(in);
         value |= nextByte << 8 * i;
         mask >>>= 1;
      }

      return value;
   }

   private static char getChar(ByteBuffer buf) throws IOException {
      if (buf.remaining() < 2) {
         throw new EOFException();
      } else {
         return buf.getChar();
      }
   }

   private static int getInt(ByteBuffer buf) throws IOException {
      if (buf.remaining() < 4) {
         throw new EOFException();
      } else {
         return buf.getInt();
      }
   }

   private static long getLong(ByteBuffer buf) throws IOException {
      if (buf.remaining() < 8) {
         throw new EOFException();
      } else {
         return buf.getLong();
      }
   }

   private static void get(ByteBuffer buf, byte[] to) throws IOException {
      if (buf.remaining() < to.length) {
         throw new EOFException();
      } else {
         buf.get(to);
      }
   }

   private static int getUnsignedByte(ByteBuffer buf) throws IOException {
      if (!buf.hasRemaining()) {
         throw new EOFException();
      } else {
         return buf.get() & 255;
      }
   }

   public static boolean matches(byte[] signature, int length) {
      if (length < sevenZSignature.length) {
         return false;
      } else {
         for(int i = 0; i < sevenZSignature.length; ++i) {
            if (signature[i] != sevenZSignature[i]) {
               return false;
            }
         }

         return true;
      }
   }

   private static long skipBytesFully(ByteBuffer input, long bytesToSkip) throws IOException {
      if (bytesToSkip < 1L) {
         return 0L;
      } else {
         int current = input.position();
         int maxSkip = input.remaining();
         if ((long)maxSkip < bytesToSkip) {
            bytesToSkip = (long)maxSkip;
         }

         input.position(current + (int)bytesToSkip);
         return bytesToSkip;
      }
   }

   private void readFully(ByteBuffer buf) throws IOException {
      buf.rewind();
      IOUtils.readFully((ReadableByteChannel)this.channel, (ByteBuffer)buf);
      buf.flip();
   }

   public String toString() {
      return this.archive.toString();
   }

   public String getDefaultName() {
      if (!"unknown archive".equals(this.fileName) && this.fileName != null) {
         String lastSegment = (new File(this.fileName)).getName();
         int dotPos = lastSegment.lastIndexOf(".");
         return dotPos > 0 ? lastSegment.substring(0, dotPos) : lastSegment + "~";
      } else {
         return null;
      }
   }

   private static byte[] utf16Decode(char[] chars) throws IOException {
      if (chars == null) {
         return null;
      } else {
         ByteBuffer encoded = PASSWORD_ENCODER.encode(CharBuffer.wrap(chars));
         if (encoded.hasArray()) {
            return encoded.array();
         } else {
            byte[] e = new byte[encoded.remaining()];
            encoded.get(e);
            return e;
         }
      }
   }

   private static int assertFitsIntoNonNegativeInt(String what, long value) throws IOException {
      if (value <= 2147483647L && value >= 0L) {
         return (int)value;
      } else {
         throw new IOException("Cannot handle " + what + " " + value);
      }
   }

   static {
      PASSWORD_ENCODER = StandardCharsets.UTF_16LE.newEncoder();
   }

   private static class ArchiveStatistics {
      private int numberOfPackedStreams;
      private long numberOfCoders;
      private long numberOfOutStreams;
      private long numberOfInStreams;
      private long numberOfUnpackSubStreams;
      private int numberOfFolders;
      private BitSet folderHasCrc;
      private int numberOfEntries;
      private int numberOfEntriesWithStream;

      private ArchiveStatistics() {
      }

      public String toString() {
         return "Archive with " + this.numberOfEntries + " entries in " + this.numberOfFolders + " folders. Estimated size " + this.estimateSize() / 1024L + " kB.";
      }

      long estimateSize() {
         long lowerBound = 16L * (long)this.numberOfPackedStreams + (long)(this.numberOfPackedStreams / 8) + (long)this.numberOfFolders * this.folderSize() + this.numberOfCoders * this.coderSize() + (this.numberOfOutStreams - (long)this.numberOfFolders) * this.bindPairSize() + 8L * (this.numberOfInStreams - this.numberOfOutStreams + (long)this.numberOfFolders) + 8L * this.numberOfOutStreams + (long)this.numberOfEntries * this.entrySize() + this.streamMapSize();
         return 2L * lowerBound;
      }

      void assertValidity(int maxMemoryLimitInKb) throws IOException {
         if (this.numberOfEntriesWithStream > 0 && this.numberOfFolders == 0) {
            throw new IOException("archive with entries but no folders");
         } else if ((long)this.numberOfEntriesWithStream > this.numberOfUnpackSubStreams) {
            throw new IOException("archive doesn't contain enough substreams for entries");
         } else {
            long memoryNeededInKb = this.estimateSize() / 1024L;
            if ((long)maxMemoryLimitInKb < memoryNeededInKb) {
               throw new MemoryLimitException(memoryNeededInKb, maxMemoryLimitInKb);
            }
         }
      }

      private long folderSize() {
         return 30L;
      }

      private long coderSize() {
         return 22L;
      }

      private long bindPairSize() {
         return 16L;
      }

      private long entrySize() {
         return 100L;
      }

      private long streamMapSize() {
         return (long)(8 * this.numberOfFolders + 8 * this.numberOfPackedStreams + 4 * this.numberOfEntries);
      }

      // $FF: synthetic method
      ArchiveStatistics(Object x0) {
         this();
      }
   }
}
