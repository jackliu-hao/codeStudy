/*     */ package org.h2.store.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FakeFileChannel
/*     */   extends FileChannel
/*     */ {
/*  24 */   public static final FakeFileChannel INSTANCE = new FakeFileChannel();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void implCloseChannel() throws IOException {
/*  30 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileLock lock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/*  35 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public MappedByteBuffer map(FileChannel.MapMode paramMapMode, long paramLong1, long paramLong2) throws IOException {
/*  40 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public long position() throws IOException {
/*  45 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel position(long paramLong) throws IOException {
/*  50 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer paramByteBuffer) throws IOException {
/*  55 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/*  60 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
/*  65 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() throws IOException {
/*  70 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2) throws IOException {
/*  75 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel) throws IOException {
/*  80 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel truncate(long paramLong) throws IOException {
/*  85 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/*  90 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/*  95 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 100 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
/* 105 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void force(boolean paramBoolean) throws IOException {
/* 110 */     throw new IOException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\FakeFileChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */