package org.xnio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

final class XnioFileChannel extends FileChannel {
   private final FileChannel delegate;

   XnioFileChannel(FileChannel delegate) {
      this.delegate = delegate;
   }

   public int read(ByteBuffer dst) throws IOException {
      return this.delegate.read(dst);
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      return this.delegate.read(dsts, offset, length);
   }

   public int write(ByteBuffer src) throws IOException {
      return this.delegate.write(src);
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.delegate.write(srcs, offset, length);
   }

   public long position() throws IOException {
      return this.delegate.position();
   }

   public FileChannel position(long newPosition) throws IOException {
      return this.delegate.position(newPosition);
   }

   public long size() throws IOException {
      return this.delegate.size();
   }

   public FileChannel truncate(long size) throws IOException {
      return this.delegate.truncate(size);
   }

   public void force(boolean metaData) throws IOException {
      this.delegate.force(metaData);
   }

   public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
      return target instanceof StreamSinkChannel ? ((StreamSinkChannel)target).transferFrom(this.delegate, position, count) : this.delegate.transferTo(position, count, target);
   }

   public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
      return src instanceof StreamSourceChannel ? ((StreamSourceChannel)src).transferTo(position, count, this.delegate) : this.delegate.transferFrom(src, position, count);
   }

   public int read(ByteBuffer dst, long position) throws IOException {
      return this.delegate.read(dst, position);
   }

   public int write(ByteBuffer src, long position) throws IOException {
      return this.delegate.write(src, position);
   }

   public MappedByteBuffer map(FileChannel.MapMode mode, long position, long size) throws IOException {
      return this.delegate.map(mode, position, size);
   }

   public FileLock lock(long position, long size, boolean shared) throws IOException {
      return this.delegate.lock(position, size, shared);
   }

   public FileLock tryLock(long position, long size, boolean shared) throws IOException {
      return this.delegate.tryLock(position, size, shared);
   }

   public void implCloseChannel() throws IOException {
      this.delegate.close();
   }

   FileChannel getDelegate() {
      return this.delegate;
   }
}
