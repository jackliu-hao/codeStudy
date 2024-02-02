/*     */ package org.apache.commons.compress.archivers.cpio;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.attribute.FileTime;
/*     */ import java.util.Date;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
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
/*     */ public class CpioArchiveEntry
/*     */   implements CpioConstants, ArchiveEntry
/*     */ {
/*     */   private final short fileFormat;
/*     */   private final int headerSize;
/*     */   private final int alignmentBoundary;
/*     */   private long chksum;
/*     */   private long filesize;
/*     */   private long gid;
/*     */   private long inode;
/*     */   private long maj;
/*     */   private long min;
/*     */   private long mode;
/*     */   private long mtime;
/*     */   private String name;
/*     */   private long nlink;
/*     */   private long rmaj;
/*     */   private long rmin;
/*     */   private long uid;
/*     */   
/*     */   public CpioArchiveEntry(short format) {
/* 213 */     switch (format) {
/*     */       case 1:
/* 215 */         this.headerSize = 110;
/* 216 */         this.alignmentBoundary = 4;
/*     */         break;
/*     */       case 2:
/* 219 */         this.headerSize = 110;
/* 220 */         this.alignmentBoundary = 4;
/*     */         break;
/*     */       case 4:
/* 223 */         this.headerSize = 76;
/* 224 */         this.alignmentBoundary = 0;
/*     */         break;
/*     */       case 8:
/* 227 */         this.headerSize = 26;
/* 228 */         this.alignmentBoundary = 2;
/*     */         break;
/*     */       default:
/* 231 */         throw new IllegalArgumentException("Unknown header type");
/*     */     } 
/* 233 */     this.fileFormat = format;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CpioArchiveEntry(String name) {
/* 244 */     this((short)1, name);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CpioArchiveEntry(short format, String name) {
/* 266 */     this(format);
/* 267 */     this.name = name;
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
/*     */   public CpioArchiveEntry(String name, long size) {
/* 280 */     this(name);
/* 281 */     setSize(size);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CpioArchiveEntry(short format, String name, long size) {
/* 306 */     this(format, name);
/* 307 */     setSize(size);
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
/*     */   public CpioArchiveEntry(File inputFile, String entryName) {
/* 321 */     this((short)1, inputFile, entryName);
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
/*     */   public CpioArchiveEntry(Path inputPath, String entryName, LinkOption... options) throws IOException {
/* 338 */     this((short)1, inputPath, entryName, options);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CpioArchiveEntry(short format, File inputFile, String entryName) {
/* 364 */     this(format, entryName, inputFile.isFile() ? inputFile.length() : 0L);
/* 365 */     if (inputFile.isDirectory()) {
/* 366 */       setMode(16384L);
/* 367 */     } else if (inputFile.isFile()) {
/* 368 */       setMode(32768L);
/*     */     } else {
/* 370 */       throw new IllegalArgumentException("Cannot determine type of file " + inputFile
/* 371 */           .getName());
/*     */     } 
/*     */     
/* 374 */     setTime(inputFile.lastModified() / 1000L);
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
/*     */   public CpioArchiveEntry(short format, Path inputPath, String entryName, LinkOption... options) throws IOException {
/* 402 */     this(format, entryName, Files.isRegularFile(inputPath, options) ? Files.size(inputPath) : 0L);
/* 403 */     if (Files.isDirectory(inputPath, options)) {
/* 404 */       setMode(16384L);
/* 405 */     } else if (Files.isRegularFile(inputPath, options)) {
/* 406 */       setMode(32768L);
/*     */     } else {
/* 408 */       throw new IllegalArgumentException("Cannot determine type of file " + inputPath);
/*     */     } 
/*     */     
/* 411 */     setTime(Files.getLastModifiedTime(inputPath, options));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkNewFormat() {
/* 418 */     if ((this.fileFormat & 0x3) == 0) {
/* 419 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkOldFormat() {
/* 427 */     if ((this.fileFormat & 0xC) == 0) {
/* 428 */       throw new UnsupportedOperationException();
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
/*     */   public long getChksum() {
/* 440 */     checkNewFormat();
/* 441 */     return this.chksum & 0xFFFFFFFFL;
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
/*     */   public long getDevice() {
/* 453 */     checkOldFormat();
/* 454 */     return this.min;
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
/*     */   public long getDeviceMaj() {
/* 466 */     checkNewFormat();
/* 467 */     return this.maj;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getDeviceMin() {
/* 477 */     checkNewFormat();
/* 478 */     return this.min;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getSize() {
/* 489 */     return this.filesize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public short getFormat() {
/* 498 */     return this.fileFormat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getGID() {
/* 507 */     return this.gid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHeaderSize() {
/* 516 */     return this.headerSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAlignmentBoundary() {
/* 525 */     return this.alignmentBoundary;
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
/*     */   @Deprecated
/*     */   public int getHeaderPadCount() {
/* 538 */     return getHeaderPadCount((Charset)null);
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
/*     */   public int getHeaderPadCount(Charset charset) {
/* 550 */     if (this.name == null) {
/* 551 */       return 0;
/*     */     }
/* 553 */     if (charset == null) {
/* 554 */       return getHeaderPadCount(this.name.length());
/*     */     }
/* 556 */     return getHeaderPadCount((this.name.getBytes(charset)).length);
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
/*     */   public int getHeaderPadCount(long namesize) {
/* 570 */     if (this.alignmentBoundary == 0) return 0; 
/* 571 */     int size = this.headerSize + 1;
/* 572 */     if (this.name != null) {
/* 573 */       size = (int)(size + namesize);
/*     */     }
/* 575 */     int remain = size % this.alignmentBoundary;
/* 576 */     if (remain > 0) {
/* 577 */       return this.alignmentBoundary - remain;
/*     */     }
/* 579 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDataPadCount() {
/* 588 */     if (this.alignmentBoundary == 0) return 0; 
/* 589 */     long size = this.filesize;
/* 590 */     int remain = (int)(size % this.alignmentBoundary);
/* 591 */     if (remain > 0) {
/* 592 */       return this.alignmentBoundary - remain;
/*     */     }
/* 594 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getInode() {
/* 603 */     return this.inode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMode() {
/* 612 */     return (this.mode == 0L && !"TRAILER!!!".equals(this.name)) ? 32768L : this.mode;
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
/*     */   public String getName() {
/* 624 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getNumberOfLinks() {
/* 633 */     return (this.nlink == 0L) ? (
/* 634 */       isDirectory() ? 2L : 1L) : this.nlink;
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
/*     */   public long getRemoteDevice() {
/* 647 */     checkOldFormat();
/* 648 */     return this.rmin;
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
/*     */   public long getRemoteDeviceMaj() {
/* 660 */     checkNewFormat();
/* 661 */     return this.rmaj;
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
/*     */   public long getRemoteDeviceMin() {
/* 673 */     checkNewFormat();
/* 674 */     return this.rmin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTime() {
/* 683 */     return this.mtime;
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getLastModifiedDate() {
/* 688 */     return new Date(1000L * getTime());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getUID() {
/* 697 */     return this.uid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBlockDevice() {
/* 706 */     return (CpioUtil.fileType(this.mode) == 24576L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCharacterDevice() {
/* 715 */     return (CpioUtil.fileType(this.mode) == 8192L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/* 725 */     return (CpioUtil.fileType(this.mode) == 16384L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNetwork() {
/* 734 */     return (CpioUtil.fileType(this.mode) == 36864L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPipe() {
/* 743 */     return (CpioUtil.fileType(this.mode) == 4096L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRegularFile() {
/* 752 */     return (CpioUtil.fileType(this.mode) == 32768L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSocket() {
/* 761 */     return (CpioUtil.fileType(this.mode) == 49152L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSymbolicLink() {
/* 770 */     return (CpioUtil.fileType(this.mode) == 40960L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setChksum(long chksum) {
/* 781 */     checkNewFormat();
/* 782 */     this.chksum = chksum & 0xFFFFFFFFL;
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
/*     */   public void setDevice(long device) {
/* 795 */     checkOldFormat();
/* 796 */     this.min = device;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDeviceMaj(long maj) {
/* 806 */     checkNewFormat();
/* 807 */     this.maj = maj;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDeviceMin(long min) {
/* 817 */     checkNewFormat();
/* 818 */     this.min = min;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSize(long size) {
/* 828 */     if (size < 0L || size > 4294967295L) {
/* 829 */       throw new IllegalArgumentException("Invalid entry size <" + size + ">");
/*     */     }
/*     */     
/* 832 */     this.filesize = size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGID(long gid) {
/* 842 */     this.gid = gid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInode(long inode) {
/* 852 */     this.inode = inode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMode(long mode) {
/* 862 */     long maskedMode = mode & 0xF000L;
/* 863 */     switch ((int)maskedMode) {
/*     */       case 4096:
/*     */       case 8192:
/*     */       case 16384:
/*     */       case 24576:
/*     */       case 32768:
/*     */       case 36864:
/*     */       case 40960:
/*     */       case 49152:
/*     */         break;
/*     */       default:
/* 874 */         throw new IllegalArgumentException("Unknown mode. Full: " + 
/*     */             
/* 876 */             Long.toHexString(mode) + " Masked: " + 
/* 877 */             Long.toHexString(maskedMode));
/*     */     } 
/*     */     
/* 880 */     this.mode = mode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setName(String name) {
/* 890 */     this.name = name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNumberOfLinks(long nlink) {
/* 900 */     this.nlink = nlink;
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
/*     */   public void setRemoteDevice(long device) {
/* 913 */     checkOldFormat();
/* 914 */     this.rmin = device;
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
/*     */   public void setRemoteDeviceMaj(long rmaj) {
/* 927 */     checkNewFormat();
/* 928 */     this.rmaj = rmaj;
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
/*     */   public void setRemoteDeviceMin(long rmin) {
/* 941 */     checkNewFormat();
/* 942 */     this.rmin = rmin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTime(long time) {
/* 952 */     this.mtime = time;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTime(FileTime time) {
/* 962 */     this.mtime = time.to(TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUID(long uid) {
/* 972 */     this.uid = uid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 980 */     return Objects.hash(new Object[] { this.name });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 988 */     if (this == obj) {
/* 989 */       return true;
/*     */     }
/* 991 */     if (obj == null || getClass() != obj.getClass()) {
/* 992 */       return false;
/*     */     }
/* 994 */     CpioArchiveEntry other = (CpioArchiveEntry)obj;
/* 995 */     if (this.name == null) {
/* 996 */       return (other.name == null);
/*     */     }
/* 998 */     return this.name.equals(other.name);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\cpio\CpioArchiveEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */