/*     */ package org.h2.store.fs.zip;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.channels.NonWritableChannelException;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import org.h2.store.fs.FakeFileChannel;
/*     */ import org.h2.store.fs.FileBase;
/*     */ import org.h2.util.IOUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class FileZip
/*     */   extends FileBase
/*     */ {
/*  27 */   private static final byte[] SKIP_BUFFER = new byte[1024];
/*     */   
/*     */   private final ZipFile file;
/*     */   private final ZipEntry entry;
/*     */   private long pos;
/*     */   private InputStream in;
/*     */   private long inPos;
/*     */   private final long length;
/*     */   private boolean skipUsingRead;
/*     */   
/*     */   FileZip(ZipFile paramZipFile, ZipEntry paramZipEntry) {
/*  38 */     this.file = paramZipFile;
/*  39 */     this.entry = paramZipEntry;
/*  40 */     this.length = paramZipEntry.getSize();
/*     */   }
/*     */ 
/*     */   
/*     */   public long position() {
/*  45 */     return this.pos;
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() {
/*  50 */     return this.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer paramByteBuffer) throws IOException {
/*  55 */     seek();
/*  56 */     int i = this.in.read(paramByteBuffer.array(), paramByteBuffer.arrayOffset() + paramByteBuffer.position(), paramByteBuffer
/*  57 */         .remaining());
/*  58 */     if (i > 0) {
/*  59 */       paramByteBuffer.position(paramByteBuffer.position() + i);
/*  60 */       this.pos += i;
/*  61 */       this.inPos += i;
/*     */     } 
/*  63 */     return i;
/*     */   }
/*     */   
/*     */   private void seek() throws IOException {
/*  67 */     if (this.inPos > this.pos) {
/*  68 */       if (this.in != null) {
/*  69 */         this.in.close();
/*     */       }
/*  71 */       this.in = null;
/*     */     } 
/*  73 */     if (this.in == null) {
/*  74 */       this.in = this.file.getInputStream(this.entry);
/*  75 */       this.inPos = 0L;
/*     */     } 
/*  77 */     if (this.inPos < this.pos) {
/*  78 */       long l = this.pos - this.inPos;
/*  79 */       if (!this.skipUsingRead) {
/*     */         try {
/*  81 */           IOUtils.skipFully(this.in, l);
/*  82 */         } catch (NullPointerException nullPointerException) {
/*     */           
/*  84 */           this.skipUsingRead = true;
/*     */         } 
/*     */       }
/*  87 */       if (this.skipUsingRead) {
/*  88 */         while (l > 0L) {
/*  89 */           int i = (int)Math.min(SKIP_BUFFER.length, l);
/*  90 */           i = this.in.read(SKIP_BUFFER, 0, i);
/*  91 */           l -= i;
/*     */         } 
/*     */       }
/*  94 */       this.inPos = this.pos;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel position(long paramLong) {
/* 100 */     this.pos = paramLong;
/* 101 */     return (FileChannel)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel truncate(long paramLong) throws IOException {
/* 106 */     throw new IOException("File is read-only");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void force(boolean paramBoolean) throws IOException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/* 116 */     throw new NonWritableChannelException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/* 122 */     if (paramBoolean) {
/* 123 */       return new FileLock((FileChannel)FakeFileChannel.INSTANCE, paramLong1, paramLong2, paramBoolean)
/*     */         {
/*     */           public boolean isValid()
/*     */           {
/* 127 */             return true;
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public void release() throws IOException {}
/*     */         };
/*     */     }
/* 135 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void implCloseChannel() throws IOException {
/* 140 */     if (this.in != null) {
/* 141 */       this.in.close();
/* 142 */       this.in = null;
/*     */     } 
/* 144 */     this.file.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\zip\FileZip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */