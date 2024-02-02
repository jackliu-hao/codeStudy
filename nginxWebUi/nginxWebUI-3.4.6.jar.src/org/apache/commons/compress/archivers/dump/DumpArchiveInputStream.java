/*     */ package org.apache.commons.compress.archivers.dump;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.PriorityQueue;
/*     */ import java.util.Queue;
/*     */ import java.util.Stack;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveException;
/*     */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*     */ import org.apache.commons.compress.utils.IOUtils;
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
/*     */ public class DumpArchiveInputStream
/*     */   extends ArchiveInputStream
/*     */ {
/*     */   private final DumpArchiveSummary summary;
/*     */   private DumpArchiveEntry active;
/*     */   private boolean isClosed;
/*     */   private boolean hasHitEOF;
/*     */   private long entrySize;
/*     */   private long entryOffset;
/*     */   private int readIdx;
/*  59 */   private final byte[] readBuf = new byte[1024];
/*     */   
/*     */   private byte[] blockBuffer;
/*     */   
/*     */   private int recordOffset;
/*     */   private long filepos;
/*     */   protected TapeInputStream raw;
/*  66 */   private final Map<Integer, Dirent> names = new HashMap<>();
/*     */ 
/*     */   
/*  69 */   private final Map<Integer, DumpArchiveEntry> pending = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Queue<DumpArchiveEntry> queue;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ZipEncoding zipEncoding;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final String encoding;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DumpArchiveInputStream(InputStream is) throws ArchiveException {
/*  90 */     this(is, null);
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
/*     */ 
/*     */   
/*     */   public DumpArchiveInputStream(InputStream is, String encoding) throws ArchiveException {
/* 104 */     this.raw = new TapeInputStream(is);
/* 105 */     this.hasHitEOF = false;
/* 106 */     this.encoding = encoding;
/* 107 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*     */ 
/*     */     
/*     */     try {
/* 111 */       byte[] headerBytes = this.raw.readRecord();
/*     */       
/* 113 */       if (!DumpArchiveUtil.verify(headerBytes)) {
/* 114 */         throw new UnrecognizedFormatException();
/*     */       }
/*     */ 
/*     */       
/* 118 */       this.summary = new DumpArchiveSummary(headerBytes, this.zipEncoding);
/*     */ 
/*     */       
/* 121 */       this.raw.resetBlockSize(this.summary.getNTRec(), this.summary.isCompressed());
/*     */ 
/*     */       
/* 124 */       this.blockBuffer = new byte[4096];
/*     */ 
/*     */       
/* 127 */       readCLRI();
/* 128 */       readBITS();
/* 129 */     } catch (IOException ex) {
/* 130 */       throw new ArchiveException(ex.getMessage(), ex);
/*     */     } 
/*     */ 
/*     */     
/* 134 */     Dirent root = new Dirent(2, 2, 4, ".");
/* 135 */     this.names.put(Integer.valueOf(2), root);
/*     */ 
/*     */ 
/*     */     
/* 139 */     this.queue = new PriorityQueue<>(10, (p, q) -> 
/*     */         
/* 141 */         (p.getOriginalName() == null || q.getOriginalName() == null) ? Integer.MAX_VALUE : p.getOriginalName().compareTo(q.getOriginalName()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getCount() {
/* 152 */     return (int)getBytesRead();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRead() {
/* 157 */     return this.raw.getBytesRead();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DumpArchiveSummary getSummary() {
/* 165 */     return this.summary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readCLRI() throws IOException {
/* 172 */     byte[] buffer = this.raw.readRecord();
/*     */     
/* 174 */     if (!DumpArchiveUtil.verify(buffer)) {
/* 175 */       throw new InvalidFormatException();
/*     */     }
/*     */     
/* 178 */     this.active = DumpArchiveEntry.parse(buffer);
/*     */     
/* 180 */     if (DumpArchiveConstants.SEGMENT_TYPE.CLRI != this.active.getHeaderType()) {
/* 181 */       throw new InvalidFormatException();
/*     */     }
/*     */ 
/*     */     
/* 185 */     if (this.raw.skip(1024L * this.active.getHeaderCount()) == -1L)
/*     */     {
/* 187 */       throw new EOFException();
/*     */     }
/* 189 */     this.readIdx = this.active.getHeaderCount();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readBITS() throws IOException {
/* 196 */     byte[] buffer = this.raw.readRecord();
/*     */     
/* 198 */     if (!DumpArchiveUtil.verify(buffer)) {
/* 199 */       throw new InvalidFormatException();
/*     */     }
/*     */     
/* 202 */     this.active = DumpArchiveEntry.parse(buffer);
/*     */     
/* 204 */     if (DumpArchiveConstants.SEGMENT_TYPE.BITS != this.active.getHeaderType()) {
/* 205 */       throw new InvalidFormatException();
/*     */     }
/*     */ 
/*     */     
/* 209 */     if (this.raw.skip(1024L * this.active.getHeaderCount()) == -1L)
/*     */     {
/* 211 */       throw new EOFException();
/*     */     }
/* 213 */     this.readIdx = this.active.getHeaderCount();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DumpArchiveEntry getNextDumpEntry() throws IOException {
/* 222 */     return getNextEntry();
/*     */   }
/*     */ 
/*     */   
/*     */   public DumpArchiveEntry getNextEntry() throws IOException {
/* 227 */     DumpArchiveEntry entry = null;
/* 228 */     String path = null;
/*     */ 
/*     */     
/* 231 */     if (!this.queue.isEmpty()) {
/* 232 */       return this.queue.remove();
/*     */     }
/*     */     
/* 235 */     while (entry == null) {
/* 236 */       if (this.hasHitEOF) {
/* 237 */         return null;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 244 */       while (this.readIdx < this.active.getHeaderCount()) {
/* 245 */         if (!this.active.isSparseRecord(this.readIdx++) && this.raw
/* 246 */           .skip(1024L) == -1L) {
/* 247 */           throw new EOFException();
/*     */         }
/*     */       } 
/*     */       
/* 251 */       this.readIdx = 0;
/* 252 */       this.filepos = this.raw.getBytesRead();
/*     */       
/* 254 */       byte[] headerBytes = this.raw.readRecord();
/*     */       
/* 256 */       if (!DumpArchiveUtil.verify(headerBytes)) {
/* 257 */         throw new InvalidFormatException();
/*     */       }
/*     */       
/* 260 */       this.active = DumpArchiveEntry.parse(headerBytes);
/*     */ 
/*     */       
/* 263 */       while (DumpArchiveConstants.SEGMENT_TYPE.ADDR == this.active.getHeaderType()) {
/* 264 */         if (this.raw.skip(1024L * (this.active
/* 265 */             .getHeaderCount() - this.active
/* 266 */             .getHeaderHoles())) == -1L) {
/* 267 */           throw new EOFException();
/*     */         }
/*     */         
/* 270 */         this.filepos = this.raw.getBytesRead();
/* 271 */         headerBytes = this.raw.readRecord();
/*     */         
/* 273 */         if (!DumpArchiveUtil.verify(headerBytes)) {
/* 274 */           throw new InvalidFormatException();
/*     */         }
/*     */         
/* 277 */         this.active = DumpArchiveEntry.parse(headerBytes);
/*     */       } 
/*     */ 
/*     */       
/* 281 */       if (DumpArchiveConstants.SEGMENT_TYPE.END == this.active.getHeaderType()) {
/* 282 */         this.hasHitEOF = true;
/*     */         
/* 284 */         return null;
/*     */       } 
/*     */       
/* 287 */       entry = this.active;
/*     */       
/* 289 */       if (entry.isDirectory()) {
/* 290 */         readDirectoryEntry(this.active);
/*     */ 
/*     */         
/* 293 */         this.entryOffset = 0L;
/* 294 */         this.entrySize = 0L;
/* 295 */         this.readIdx = this.active.getHeaderCount();
/*     */       } else {
/* 297 */         this.entryOffset = 0L;
/* 298 */         this.entrySize = this.active.getEntrySize();
/* 299 */         this.readIdx = 0;
/*     */       } 
/*     */       
/* 302 */       this.recordOffset = this.readBuf.length;
/*     */       
/* 304 */       path = getPath(entry);
/*     */       
/* 306 */       if (path == null) {
/* 307 */         entry = null;
/*     */       }
/*     */     } 
/*     */     
/* 311 */     entry.setName(path);
/* 312 */     entry.setSimpleName(((Dirent)this.names.get(Integer.valueOf(entry.getIno()))).getName());
/* 313 */     entry.setOffset(this.filepos);
/*     */     
/* 315 */     return entry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readDirectoryEntry(DumpArchiveEntry entry) throws IOException {
/* 323 */     long size = entry.getEntrySize();
/* 324 */     boolean first = true;
/*     */     
/* 326 */     while (first || DumpArchiveConstants.SEGMENT_TYPE.ADDR == entry
/* 327 */       .getHeaderType()) {
/*     */       
/* 329 */       if (!first) {
/* 330 */         this.raw.readRecord();
/*     */       }
/*     */       
/* 333 */       if (!this.names.containsKey(Integer.valueOf(entry.getIno())) && DumpArchiveConstants.SEGMENT_TYPE.INODE == entry
/* 334 */         .getHeaderType()) {
/* 335 */         this.pending.put(Integer.valueOf(entry.getIno()), entry);
/*     */       }
/*     */       
/* 338 */       int datalen = 1024 * entry.getHeaderCount();
/*     */       
/* 340 */       if (this.blockBuffer.length < datalen) {
/* 341 */         this.blockBuffer = IOUtils.readRange(this.raw, datalen);
/* 342 */         if (this.blockBuffer.length != datalen) {
/* 343 */           throw new EOFException();
/*     */         }
/* 345 */       } else if (this.raw.read(this.blockBuffer, 0, datalen) != datalen) {
/* 346 */         throw new EOFException();
/*     */       } 
/*     */       
/* 349 */       int reclen = 0;
/*     */       int i;
/* 351 */       for (i = 0; i < datalen - 8 && i < size - 8L; 
/* 352 */         i += reclen) {
/* 353 */         int ino = DumpArchiveUtil.convert32(this.blockBuffer, i);
/* 354 */         reclen = DumpArchiveUtil.convert16(this.blockBuffer, i + 4);
/*     */         
/* 356 */         byte type = this.blockBuffer[i + 6];
/*     */         
/* 358 */         String name = DumpArchiveUtil.decode(this.zipEncoding, this.blockBuffer, i + 8, this.blockBuffer[i + 7]);
/*     */         
/* 360 */         if (!".".equals(name) && !"..".equals(name)) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 365 */           Dirent d = new Dirent(ino, entry.getIno(), type, name);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 374 */           this.names.put(Integer.valueOf(ino), d);
/*     */ 
/*     */           
/* 377 */           for (Map.Entry<Integer, DumpArchiveEntry> e : this.pending.entrySet()) {
/* 378 */             String path = getPath(e.getValue());
/*     */             
/* 380 */             if (path != null) {
/* 381 */               ((DumpArchiveEntry)e.getValue()).setName(path);
/* 382 */               ((DumpArchiveEntry)e.getValue())
/* 383 */                 .setSimpleName(((Dirent)this.names.get(e.getKey())).getName());
/* 384 */               this.queue.add(e.getValue());
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 390 */           for (DumpArchiveEntry e : this.queue) {
/* 391 */             this.pending.remove(Integer.valueOf(e.getIno()));
/*     */           }
/*     */         } 
/*     */       } 
/* 395 */       byte[] peekBytes = this.raw.peek();
/*     */       
/* 397 */       if (!DumpArchiveUtil.verify(peekBytes)) {
/* 398 */         throw new InvalidFormatException();
/*     */       }
/*     */       
/* 401 */       entry = DumpArchiveEntry.parse(peekBytes);
/* 402 */       first = false;
/* 403 */       size -= 1024L;
/*     */     } 
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
/*     */   private String getPath(DumpArchiveEntry entry) {
/* 416 */     Stack<String> elements = new Stack<>();
/* 417 */     Dirent dirent = null;
/*     */     int i;
/* 419 */     for (i = entry.getIno();; i = dirent.getParentIno()) {
/* 420 */       if (!this.names.containsKey(Integer.valueOf(i))) {
/* 421 */         elements.clear();
/*     */         
/*     */         break;
/*     */       } 
/* 425 */       dirent = this.names.get(Integer.valueOf(i));
/* 426 */       elements.push(dirent.getName());
/*     */       
/* 428 */       if (dirent.getIno() == dirent.getParentIno()) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 434 */     if (elements.isEmpty()) {
/* 435 */       this.pending.put(Integer.valueOf(entry.getIno()), entry);
/*     */       
/* 437 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 441 */     StringBuilder sb = new StringBuilder(elements.pop());
/*     */     
/* 443 */     while (!elements.isEmpty()) {
/* 444 */       sb.append('/');
/* 445 */       sb.append(elements.pop());
/*     */     } 
/*     */     
/* 448 */     return sb.toString();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] buf, int off, int len) throws IOException {
/* 466 */     if (len == 0) {
/* 467 */       return 0;
/*     */     }
/* 469 */     int totalRead = 0;
/*     */     
/* 471 */     if (this.hasHitEOF || this.isClosed || this.entryOffset >= this.entrySize) {
/* 472 */       return -1;
/*     */     }
/*     */     
/* 475 */     if (this.active == null) {
/* 476 */       throw new IllegalStateException("No current dump entry");
/*     */     }
/*     */     
/* 479 */     if (len + this.entryOffset > this.entrySize) {
/* 480 */       len = (int)(this.entrySize - this.entryOffset);
/*     */     }
/*     */     
/* 483 */     while (len > 0) {
/* 484 */       int sz = (len > this.readBuf.length - this.recordOffset) ? (this.readBuf.length - this.recordOffset) : len;
/*     */ 
/*     */ 
/*     */       
/* 488 */       if (this.recordOffset + sz <= this.readBuf.length) {
/* 489 */         System.arraycopy(this.readBuf, this.recordOffset, buf, off, sz);
/* 490 */         totalRead += sz;
/* 491 */         this.recordOffset += sz;
/* 492 */         len -= sz;
/* 493 */         off += sz;
/*     */       } 
/*     */ 
/*     */       
/* 497 */       if (len > 0) {
/* 498 */         if (this.readIdx >= 512) {
/* 499 */           byte[] headerBytes = this.raw.readRecord();
/*     */           
/* 501 */           if (!DumpArchiveUtil.verify(headerBytes)) {
/* 502 */             throw new InvalidFormatException();
/*     */           }
/*     */           
/* 505 */           this.active = DumpArchiveEntry.parse(headerBytes);
/* 506 */           this.readIdx = 0;
/*     */         } 
/*     */         
/* 509 */         if (!this.active.isSparseRecord(this.readIdx++)) {
/* 510 */           int r = this.raw.read(this.readBuf, 0, this.readBuf.length);
/* 511 */           if (r != this.readBuf.length) {
/* 512 */             throw new EOFException();
/*     */           }
/*     */         } else {
/* 515 */           Arrays.fill(this.readBuf, (byte)0);
/*     */         } 
/*     */         
/* 518 */         this.recordOffset = 0;
/*     */       } 
/*     */     } 
/*     */     
/* 522 */     this.entryOffset += totalRead;
/*     */     
/* 524 */     return totalRead;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 532 */     if (!this.isClosed) {
/* 533 */       this.isClosed = true;
/* 534 */       this.raw.close();
/*     */     } 
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
/*     */   
/*     */   public static boolean matches(byte[] buffer, int length) {
/* 548 */     if (length < 32) {
/* 549 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 553 */     if (length >= 1024) {
/* 554 */       return DumpArchiveUtil.verify(buffer);
/*     */     }
/*     */ 
/*     */     
/* 558 */     return (60012 == DumpArchiveUtil.convert32(buffer, 24));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\dump\DumpArchiveInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */