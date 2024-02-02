package org.apache.commons.compress.utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class MultiReadOnlySeekableByteChannel implements SeekableByteChannel {
   private final List<SeekableByteChannel> channels;
   private long globalPosition;
   private int currentChannelIdx;

   public MultiReadOnlySeekableByteChannel(List<SeekableByteChannel> channels) {
      this.channels = Collections.unmodifiableList(new ArrayList((Collection)Objects.requireNonNull(channels, "channels must not be null")));
   }

   public synchronized int read(ByteBuffer dst) throws IOException {
      if (!this.isOpen()) {
         throw new ClosedChannelException();
      } else if (!dst.hasRemaining()) {
         return 0;
      } else {
         int totalBytesRead = 0;

         while(dst.hasRemaining() && this.currentChannelIdx < this.channels.size()) {
            SeekableByteChannel currentChannel = (SeekableByteChannel)this.channels.get(this.currentChannelIdx);
            int newBytesRead = currentChannel.read(dst);
            if (newBytesRead == -1) {
               ++this.currentChannelIdx;
            } else {
               if (currentChannel.position() >= currentChannel.size()) {
                  ++this.currentChannelIdx;
               }

               totalBytesRead += newBytesRead;
            }
         }

         if (totalBytesRead > 0) {
            this.globalPosition += (long)totalBytesRead;
            return totalBytesRead;
         } else {
            return -1;
         }
      }
   }

   public void close() throws IOException {
      IOException first = null;
      Iterator var2 = this.channels.iterator();

      while(var2.hasNext()) {
         SeekableByteChannel ch = (SeekableByteChannel)var2.next();

         try {
            ch.close();
         } catch (IOException var5) {
            if (first == null) {
               first = var5;
            }
         }
      }

      if (first != null) {
         throw new IOException("failed to close wrapped channel", first);
      }
   }

   public boolean isOpen() {
      Iterator var1 = this.channels.iterator();

      SeekableByteChannel ch;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         ch = (SeekableByteChannel)var1.next();
      } while(ch.isOpen());

      return false;
   }

   public long position() {
      return this.globalPosition;
   }

   public synchronized SeekableByteChannel position(long channelNumber, long relativeOffset) throws IOException {
      if (!this.isOpen()) {
         throw new ClosedChannelException();
      } else {
         long globalPosition = relativeOffset;

         for(int i = 0; (long)i < channelNumber; ++i) {
            globalPosition += ((SeekableByteChannel)this.channels.get(i)).size();
         }

         return this.position(globalPosition);
      }
   }

   public long size() throws IOException {
      if (!this.isOpen()) {
         throw new ClosedChannelException();
      } else {
         long acc = 0L;

         SeekableByteChannel ch;
         for(Iterator var3 = this.channels.iterator(); var3.hasNext(); acc += ch.size()) {
            ch = (SeekableByteChannel)var3.next();
         }

         return acc;
      }
   }

   public SeekableByteChannel truncate(long size) {
      throw new NonWritableChannelException();
   }

   public int write(ByteBuffer src) {
      throw new NonWritableChannelException();
   }

   public synchronized SeekableByteChannel position(long newPosition) throws IOException {
      if (newPosition < 0L) {
         throw new IOException("Negative position: " + newPosition);
      } else if (!this.isOpen()) {
         throw new ClosedChannelException();
      } else {
         this.globalPosition = newPosition;
         long pos = newPosition;

         for(int i = 0; i < this.channels.size(); ++i) {
            SeekableByteChannel currentChannel = (SeekableByteChannel)this.channels.get(i);
            long size = currentChannel.size();
            long newChannelPos;
            if (pos == -1L) {
               newChannelPos = 0L;
            } else if (pos <= size) {
               this.currentChannelIdx = i;
               long tmp = pos;
               pos = -1L;
               newChannelPos = tmp;
            } else {
               pos -= size;
               newChannelPos = size;
            }

            currentChannel.position(newChannelPos);
         }

         return this;
      }
   }

   public static SeekableByteChannel forSeekableByteChannels(SeekableByteChannel... channels) {
      return (SeekableByteChannel)(((SeekableByteChannel[])Objects.requireNonNull(channels, "channels must not be null")).length == 1 ? channels[0] : new MultiReadOnlySeekableByteChannel(Arrays.asList(channels)));
   }

   public static SeekableByteChannel forFiles(File... files) throws IOException {
      List<SeekableByteChannel> channels = new ArrayList();
      File[] var2 = (File[])Objects.requireNonNull(files, "files must not be null");
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         File f = var2[var4];
         channels.add(Files.newByteChannel(f.toPath(), StandardOpenOption.READ));
      }

      return (SeekableByteChannel)(channels.size() == 1 ? (SeekableByteChannel)channels.get(0) : new MultiReadOnlySeekableByteChannel(channels));
   }
}
