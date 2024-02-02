package org.apache.commons.compress.archivers.zip;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.compress.utils.MultiReadOnlySeekableByteChannel;

public class ZipSplitReadOnlySeekableByteChannel extends MultiReadOnlySeekableByteChannel {
   private static final int ZIP_SPLIT_SIGNATURE_LENGTH = 4;
   private final ByteBuffer zipSplitSignatureByteBuffer = ByteBuffer.allocate(4);

   public ZipSplitReadOnlySeekableByteChannel(List<SeekableByteChannel> channels) throws IOException {
      super(channels);
      this.assertSplitSignature(channels);
   }

   private void assertSplitSignature(List<SeekableByteChannel> channels) throws IOException {
      SeekableByteChannel channel = (SeekableByteChannel)channels.get(0);
      channel.position(0L);
      this.zipSplitSignatureByteBuffer.rewind();
      channel.read(this.zipSplitSignatureByteBuffer);
      ZipLong signature = new ZipLong(this.zipSplitSignatureByteBuffer.array());
      if (!signature.equals(ZipLong.DD_SIG)) {
         channel.position(0L);
         throw new IOException("The first zip split segment does not begin with split zip file signature");
      } else {
         channel.position(0L);
      }
   }

   public static SeekableByteChannel forOrderedSeekableByteChannels(SeekableByteChannel... channels) throws IOException {
      return (SeekableByteChannel)(((SeekableByteChannel[])Objects.requireNonNull(channels, "channels must not be null")).length == 1 ? channels[0] : new ZipSplitReadOnlySeekableByteChannel(Arrays.asList(channels)));
   }

   public static SeekableByteChannel forOrderedSeekableByteChannels(SeekableByteChannel lastSegmentChannel, Iterable<SeekableByteChannel> channels) throws IOException {
      Objects.requireNonNull(channels, "channels");
      Objects.requireNonNull(lastSegmentChannel, "lastSegmentChannel");
      List<SeekableByteChannel> channelsList = new ArrayList();
      Iterator var3 = channels.iterator();

      while(var3.hasNext()) {
         SeekableByteChannel channel = (SeekableByteChannel)var3.next();
         channelsList.add(channel);
      }

      channelsList.add(lastSegmentChannel);
      return forOrderedSeekableByteChannels((SeekableByteChannel[])channelsList.toArray(new SeekableByteChannel[0]));
   }

   public static SeekableByteChannel buildFromLastSplitSegment(File lastSegmentFile) throws IOException {
      String extension = FileNameUtils.getExtension(lastSegmentFile.getCanonicalPath());
      if (!extension.equalsIgnoreCase("zip")) {
         throw new IllegalArgumentException("The extension of last zip split segment should be .zip");
      } else {
         File parent = lastSegmentFile.getParentFile();
         String fileBaseName = FileNameUtils.getBaseName(lastSegmentFile.getCanonicalPath());
         ArrayList<File> splitZipSegments = new ArrayList();
         Pattern pattern = Pattern.compile(Pattern.quote(fileBaseName) + ".[zZ][0-9]+");
         File[] children = parent.listFiles();
         if (children != null) {
            File[] var7 = children;
            int var8 = children.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               File file = var7[var9];
               if (pattern.matcher(file.getName()).matches()) {
                  splitZipSegments.add(file);
               }
            }
         }

         splitZipSegments.sort(new ZipSplitSegmentComparator());
         return forFiles(lastSegmentFile, splitZipSegments);
      }
   }

   public static SeekableByteChannel forFiles(File... files) throws IOException {
      List<SeekableByteChannel> channels = new ArrayList();
      File[] var2 = (File[])Objects.requireNonNull(files, "files must not be null");
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         File f = var2[var4];
         channels.add(Files.newByteChannel(f.toPath(), StandardOpenOption.READ));
      }

      return (SeekableByteChannel)(channels.size() == 1 ? (SeekableByteChannel)channels.get(0) : new ZipSplitReadOnlySeekableByteChannel(channels));
   }

   public static SeekableByteChannel forFiles(File lastSegmentFile, Iterable<File> files) throws IOException {
      Objects.requireNonNull(files, "files");
      Objects.requireNonNull(lastSegmentFile, "lastSegmentFile");
      List<File> filesList = new ArrayList();
      Iterator var3 = files.iterator();

      while(var3.hasNext()) {
         File f = (File)var3.next();
         filesList.add(f);
      }

      filesList.add(lastSegmentFile);
      return forFiles((File[])filesList.toArray(new File[0]));
   }

   private static class ZipSplitSegmentComparator implements Comparator<File>, Serializable {
      private static final long serialVersionUID = 20200123L;

      private ZipSplitSegmentComparator() {
      }

      public int compare(File file1, File file2) {
         String extension1 = FileNameUtils.getExtension(file1.getPath());
         String extension2 = FileNameUtils.getExtension(file2.getPath());
         if (!extension1.startsWith("z")) {
            return -1;
         } else if (!extension2.startsWith("z")) {
            return 1;
         } else {
            Integer splitSegmentNumber1 = Integer.parseInt(extension1.substring(1));
            Integer splitSegmentNumber2 = Integer.parseInt(extension2.substring(1));
            return splitSegmentNumber1.compareTo(splitSegmentNumber2);
         }
      }

      // $FF: synthetic method
      ZipSplitSegmentComparator(Object x0) {
         this();
      }
   }
}
