/*     */ package org.apache.commons.compress.archivers.dump;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.EnumSet;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ public class DumpArchiveEntry
/*     */   implements ArchiveEntry
/*     */ {
/*     */   private String name;
/* 183 */   private TYPE type = TYPE.UNKNOWN;
/*     */   private int mode;
/* 185 */   private Set<PERMISSION> permissions = Collections.emptySet();
/*     */   
/*     */   private long size;
/*     */   
/*     */   private long atime;
/*     */   
/*     */   private long mtime;
/*     */   
/*     */   private int uid;
/*     */   private int gid;
/* 195 */   private final DumpArchiveSummary summary = null;
/*     */ 
/*     */   
/* 198 */   private final TapeSegmentHeader header = new TapeSegmentHeader();
/*     */   
/*     */   private String simpleName;
/*     */   
/*     */   private String originalName;
/*     */   
/*     */   private int volume;
/*     */   
/*     */   private long offset;
/*     */   
/*     */   private int ino;
/*     */   
/*     */   private int nlink;
/*     */   
/*     */   private long ctime;
/*     */   
/*     */   private int generation;
/*     */   
/*     */   private boolean isDeleted;
/*     */ 
/*     */   
/*     */   public DumpArchiveEntry() {}
/*     */ 
/*     */   
/*     */   public DumpArchiveEntry(String name, String simpleName) {
/* 223 */     setName(name);
/* 224 */     this.simpleName = simpleName;
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
/*     */   protected DumpArchiveEntry(String name, String simpleName, int ino, TYPE type) {
/* 237 */     setType(type);
/* 238 */     setName(name);
/* 239 */     this.simpleName = simpleName;
/* 240 */     this.ino = ino;
/* 241 */     this.offset = 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSimpleName() {
/* 249 */     return this.simpleName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setSimpleName(String simpleName) {
/* 257 */     this.simpleName = simpleName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIno() {
/* 265 */     return this.header.getIno();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNlink() {
/* 273 */     return this.nlink;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNlink(int nlink) {
/* 281 */     this.nlink = nlink;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getCreationTime() {
/* 289 */     return new Date(this.ctime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCreationTime(Date ctime) {
/* 297 */     this.ctime = ctime.getTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getGeneration() {
/* 305 */     return this.generation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGeneration(int generation) {
/* 313 */     this.generation = generation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDeleted() {
/* 321 */     return this.isDeleted;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDeleted(boolean isDeleted) {
/* 329 */     this.isDeleted = isDeleted;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getOffset() {
/* 337 */     return this.offset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOffset(long offset) {
/* 345 */     this.offset = offset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getVolume() {
/* 353 */     return this.volume;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVolume(int volume) {
/* 361 */     this.volume = volume;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DumpArchiveConstants.SEGMENT_TYPE getHeaderType() {
/* 369 */     return this.header.getType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHeaderCount() {
/* 377 */     return this.header.getCount();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHeaderHoles() {
/* 385 */     return this.header.getHoles();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSparseRecord(int idx) {
/* 394 */     return ((this.header.getCdata(idx) & 0x1) == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 399 */     return this.ino;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 404 */     if (o == this) {
/* 405 */       return true;
/*     */     }
/* 407 */     if (o == null || !o.getClass().equals(getClass())) {
/* 408 */       return false;
/*     */     }
/*     */     
/* 411 */     DumpArchiveEntry rhs = (DumpArchiveEntry)o;
/*     */     
/* 413 */     if (rhs.header == null) {
/* 414 */       return false;
/*     */     }
/*     */     
/* 417 */     if (this.ino != rhs.ino) {
/* 418 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 422 */     if ((this.summary == null && rhs.summary != null) || (this.summary != null && 
/* 423 */       !this.summary.equals(rhs.summary))) {
/* 424 */       return false;
/*     */     }
/*     */     
/* 427 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 432 */     return getName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static DumpArchiveEntry parse(byte[] buffer) {
/* 442 */     DumpArchiveEntry entry = new DumpArchiveEntry();
/* 443 */     TapeSegmentHeader header = entry.header;
/*     */     
/* 445 */     header.type = DumpArchiveConstants.SEGMENT_TYPE.find(DumpArchiveUtil.convert32(buffer, 0));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 451 */     header.volume = DumpArchiveUtil.convert32(buffer, 12);
/*     */     
/* 453 */     entry.ino = header.ino = DumpArchiveUtil.convert32(buffer, 20);
/*     */ 
/*     */ 
/*     */     
/* 457 */     int m = DumpArchiveUtil.convert16(buffer, 32);
/*     */ 
/*     */     
/* 460 */     entry.setType(TYPE.find(m >> 12 & 0xF));
/*     */ 
/*     */     
/* 463 */     entry.setMode(m);
/*     */     
/* 465 */     entry.nlink = DumpArchiveUtil.convert16(buffer, 34);
/*     */     
/* 467 */     entry.setSize(DumpArchiveUtil.convert64(buffer, 40));
/*     */ 
/*     */     
/* 470 */     long t = 1000L * DumpArchiveUtil.convert32(buffer, 48) + (DumpArchiveUtil.convert32(buffer, 52) / 1000);
/* 471 */     entry.setAccessTime(new Date(t));
/*     */     
/* 473 */     t = 1000L * DumpArchiveUtil.convert32(buffer, 56) + (DumpArchiveUtil.convert32(buffer, 60) / 1000);
/* 474 */     entry.setLastModifiedDate(new Date(t));
/*     */     
/* 476 */     t = 1000L * DumpArchiveUtil.convert32(buffer, 64) + (DumpArchiveUtil.convert32(buffer, 68) / 1000);
/* 477 */     entry.ctime = t;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 483 */     entry.generation = DumpArchiveUtil.convert32(buffer, 140);
/* 484 */     entry.setUserId(DumpArchiveUtil.convert32(buffer, 144));
/* 485 */     entry.setGroupId(DumpArchiveUtil.convert32(buffer, 148));
/*     */     
/* 487 */     header.count = DumpArchiveUtil.convert32(buffer, 160);
/*     */     
/* 489 */     header.holes = 0;
/*     */     
/* 491 */     for (int i = 0; i < 512 && i < header.count; i++) {
/* 492 */       if (buffer[164 + i] == 0) {
/* 493 */         header.holes++;
/*     */       }
/*     */     } 
/*     */     
/* 497 */     System.arraycopy(buffer, 164, header.cdata, 0, 512);
/*     */     
/* 499 */     entry.volume = header.getVolume();
/*     */ 
/*     */     
/* 502 */     return entry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void update(byte[] buffer) {
/* 509 */     this.header.volume = DumpArchiveUtil.convert32(buffer, 16);
/* 510 */     this.header.count = DumpArchiveUtil.convert32(buffer, 160);
/*     */     
/* 512 */     this.header.holes = 0;
/*     */     
/* 514 */     for (int i = 0; i < 512 && i < this.header.count; i++) {
/* 515 */       if (buffer[164 + i] == 0) {
/* 516 */         this.header.holes++;
/*     */       }
/*     */     } 
/*     */     
/* 520 */     System.arraycopy(buffer, 164, this.header.cdata, 0, 512);
/*     */   }
/*     */ 
/*     */   
/*     */   static class TapeSegmentHeader
/*     */   {
/*     */     private DumpArchiveConstants.SEGMENT_TYPE type;
/*     */     
/*     */     private int volume;
/*     */     
/*     */     private int ino;
/*     */     private int count;
/*     */     private int holes;
/* 533 */     private final byte[] cdata = new byte[512];
/*     */     
/*     */     public DumpArchiveConstants.SEGMENT_TYPE getType() {
/* 536 */       return this.type;
/*     */     }
/*     */     
/*     */     public int getVolume() {
/* 540 */       return this.volume;
/*     */     }
/*     */     
/*     */     public int getIno() {
/* 544 */       return this.ino;
/*     */     }
/*     */     
/*     */     void setIno(int ino) {
/* 548 */       this.ino = ino;
/*     */     }
/*     */     
/*     */     public int getCount() {
/* 552 */       return this.count;
/*     */     }
/*     */     
/*     */     public int getHoles() {
/* 556 */       return this.holes;
/*     */     }
/*     */     
/*     */     public int getCdata(int idx) {
/* 560 */       return this.cdata[idx];
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
/*     */   public String getName() {
/* 573 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getOriginalName() {
/* 581 */     return this.originalName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setName(String name) {
/* 589 */     this.originalName = name;
/* 590 */     if (name != null) {
/* 591 */       if (isDirectory() && !name.endsWith("/")) {
/* 592 */         name = name + "/";
/*     */       }
/* 594 */       if (name.startsWith("./")) {
/* 595 */         name = name.substring(2);
/*     */       }
/*     */     } 
/* 598 */     this.name = name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getLastModifiedDate() {
/* 607 */     return new Date(this.mtime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/* 616 */     return (this.type == TYPE.DIRECTORY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFile() {
/* 624 */     return (this.type == TYPE.FILE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSocket() {
/* 632 */     return (this.type == TYPE.SOCKET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isChrDev() {
/* 640 */     return (this.type == TYPE.CHRDEV);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBlkDev() {
/* 648 */     return (this.type == TYPE.BLKDEV);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFifo() {
/* 656 */     return (this.type == TYPE.FIFO);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TYPE getType() {
/* 664 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setType(TYPE type) {
/* 672 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMode() {
/* 680 */     return this.mode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMode(int mode) {
/* 688 */     this.mode = mode & 0xFFF;
/* 689 */     this.permissions = PERMISSION.find(mode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<PERMISSION> getPermissions() {
/* 697 */     return this.permissions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getSize() {
/* 706 */     return isDirectory() ? -1L : this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long getEntrySize() {
/* 713 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSize(long size) {
/* 721 */     this.size = size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLastModifiedDate(Date mtime) {
/* 729 */     this.mtime = mtime.getTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getAccessTime() {
/* 737 */     return new Date(this.atime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAccessTime(Date atime) {
/* 745 */     this.atime = atime.getTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getUserId() {
/* 753 */     return this.uid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUserId(int uid) {
/* 761 */     this.uid = uid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getGroupId() {
/* 769 */     return this.gid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGroupId(int gid) {
/* 777 */     this.gid = gid;
/*     */   }
/*     */   
/*     */   public enum TYPE {
/* 781 */     WHITEOUT(14),
/* 782 */     SOCKET(12),
/* 783 */     LINK(10),
/* 784 */     FILE(8),
/* 785 */     BLKDEV(6),
/* 786 */     DIRECTORY(4),
/* 787 */     CHRDEV(2),
/* 788 */     FIFO(1),
/* 789 */     UNKNOWN(15);
/*     */     
/*     */     private final int code;
/*     */     
/*     */     TYPE(int code) {
/* 794 */       this.code = code;
/*     */     }
/*     */     
/*     */     public static TYPE find(int code) {
/* 798 */       TYPE type = UNKNOWN;
/*     */       
/* 800 */       for (TYPE t : values()) {
/* 801 */         if (code == t.code) {
/* 802 */           type = t;
/*     */         }
/*     */       } 
/*     */       
/* 806 */       return type;
/*     */     }
/*     */   }
/*     */   
/*     */   public enum PERMISSION {
/* 811 */     SETUID(2048),
/* 812 */     SETGUI(1024),
/* 813 */     STICKY(512),
/* 814 */     USER_READ(256),
/* 815 */     USER_WRITE(128),
/* 816 */     USER_EXEC(64),
/* 817 */     GROUP_READ(32),
/* 818 */     GROUP_WRITE(16),
/* 819 */     GROUP_EXEC(8),
/* 820 */     WORLD_READ(4),
/* 821 */     WORLD_WRITE(2),
/* 822 */     WORLD_EXEC(1);
/*     */     
/*     */     private final int code;
/*     */     
/*     */     PERMISSION(int code) {
/* 827 */       this.code = code;
/*     */     }
/*     */     
/*     */     public static Set<PERMISSION> find(int code) {
/* 831 */       Set<PERMISSION> set = new HashSet<>();
/*     */       
/* 833 */       for (PERMISSION p : values()) {
/* 834 */         if ((code & p.code) == p.code) {
/* 835 */           set.add(p);
/*     */         }
/*     */       } 
/*     */       
/* 839 */       if (set.isEmpty()) {
/* 840 */         return Collections.emptySet();
/*     */       }
/*     */       
/* 843 */       return EnumSet.copyOf(set);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\dump\DumpArchiveEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */