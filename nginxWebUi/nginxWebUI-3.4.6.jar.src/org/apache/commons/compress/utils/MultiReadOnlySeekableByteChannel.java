/*     */ package org.apache.commons.compress.utils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.NonWritableChannelException;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ 
/*     */ public class MultiReadOnlySeekableByteChannel
/*     */   implements SeekableByteChannel
/*     */ {
/*     */   private final List<SeekableByteChannel> channels;
/*     */   private long globalPosition;
/*     */   private int currentChannelIdx;
/*     */   
/*     */   public MultiReadOnlySeekableByteChannel(List<SeekableByteChannel> channels) {
/*  58 */     this.channels = Collections.unmodifiableList(new ArrayList<>(
/*  59 */           Objects.<Collection<? extends SeekableByteChannel>>requireNonNull(channels, "channels must not be null")));
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized int read(ByteBuffer dst) throws IOException {
/*  64 */     if (!isOpen()) {
/*  65 */       throw new ClosedChannelException();
/*     */     }
/*  67 */     if (!dst.hasRemaining()) {
/*  68 */       return 0;
/*     */     }
/*     */     
/*  71 */     int totalBytesRead = 0;
/*  72 */     while (dst.hasRemaining() && this.currentChannelIdx < this.channels.size()) {
/*  73 */       SeekableByteChannel currentChannel = this.channels.get(this.currentChannelIdx);
/*  74 */       int newBytesRead = currentChannel.read(dst);
/*  75 */       if (newBytesRead == -1) {
/*     */         
/*  77 */         this.currentChannelIdx++;
/*     */         continue;
/*     */       } 
/*  80 */       if (currentChannel.position() >= currentChannel.size())
/*     */       {
/*  82 */         this.currentChannelIdx++;
/*     */       }
/*  84 */       totalBytesRead += newBytesRead;
/*     */     } 
/*  86 */     if (totalBytesRead > 0) {
/*  87 */       this.globalPosition += totalBytesRead;
/*  88 */       return totalBytesRead;
/*     */     } 
/*  90 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  95 */     IOException first = null;
/*  96 */     for (SeekableByteChannel ch : this.channels) {
/*     */       try {
/*  98 */         ch.close();
/*  99 */       } catch (IOException ex) {
/* 100 */         if (first == null) {
/* 101 */           first = ex;
/*     */         }
/*     */       } 
/*     */     } 
/* 105 */     if (first != null) {
/* 106 */       throw new IOException("failed to close wrapped channel", first);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 112 */     for (SeekableByteChannel ch : this.channels) {
/* 113 */       if (!ch.isOpen()) {
/* 114 */         return false;
/*     */       }
/*     */     } 
/* 117 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long position() {
/* 129 */     return this.globalPosition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized SeekableByteChannel position(long channelNumber, long relativeOffset) throws IOException {
/* 141 */     if (!isOpen()) {
/* 142 */       throw new ClosedChannelException();
/*     */     }
/* 144 */     long globalPosition = relativeOffset;
/* 145 */     for (int i = 0; i < channelNumber; i++) {
/* 146 */       globalPosition += ((SeekableByteChannel)this.channels.get(i)).size();
/*     */     }
/*     */     
/* 149 */     return position(globalPosition);
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() throws IOException {
/* 154 */     if (!isOpen()) {
/* 155 */       throw new ClosedChannelException();
/*     */     }
/* 157 */     long acc = 0L;
/* 158 */     for (SeekableByteChannel ch : this.channels) {
/* 159 */       acc += ch.size();
/*     */     }
/* 161 */     return acc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SeekableByteChannel truncate(long size) {
/* 169 */     throw new NonWritableChannelException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) {
/* 177 */     throw new NonWritableChannelException();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized SeekableByteChannel position(long newPosition) throws IOException {
/* 182 */     if (newPosition < 0L) {
/* 183 */       throw new IOException("Negative position: " + newPosition);
/*     */     }
/* 185 */     if (!isOpen()) {
/* 186 */       throw new ClosedChannelException();
/*     */     }
/*     */     
/* 189 */     this.globalPosition = newPosition;
/*     */     
/* 191 */     long pos = newPosition;
/*     */     
/* 193 */     for (int i = 0; i < this.channels.size(); i++) {
/* 194 */       long newChannelPos; SeekableByteChannel currentChannel = this.channels.get(i);
/* 195 */       long size = currentChannel.size();
/*     */ 
/*     */       
/* 198 */       if (pos == -1L) {
/*     */ 
/*     */         
/* 201 */         newChannelPos = 0L;
/* 202 */       } else if (pos <= size) {
/*     */         
/* 204 */         this.currentChannelIdx = i;
/* 205 */         long tmp = pos;
/* 206 */         pos = -1L;
/* 207 */         newChannelPos = tmp;
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 212 */         pos -= size;
/* 213 */         newChannelPos = size;
/*     */       } 
/*     */       
/* 216 */       currentChannel.position(newChannelPos);
/*     */     } 
/* 218 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SeekableByteChannel forSeekableByteChannels(SeekableByteChannel... channels) {
/* 229 */     if (((SeekableByteChannel[])Objects.requireNonNull((T)channels, "channels must not be null")).length == 1) {
/* 230 */       return channels[0];
/*     */     }
/* 232 */     return new MultiReadOnlySeekableByteChannel(Arrays.asList(channels));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SeekableByteChannel forFiles(File... files) throws IOException {
/* 244 */     List<SeekableByteChannel> channels = new ArrayList<>();
/* 245 */     for (File f : (File[])Objects.<File[]>requireNonNull(files, "files must not be null")) {
/* 246 */       channels.add(Files.newByteChannel(f.toPath(), new OpenOption[] { StandardOpenOption.READ }));
/*     */     } 
/* 248 */     if (channels.size() == 1) {
/* 249 */       return channels.get(0);
/*     */     }
/* 251 */     return new MultiReadOnlySeekableByteChannel(channels);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\MultiReadOnlySeekableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */