/*     */ package org.apache.commons.compress.archivers.sevenz;
/*     */ 
/*     */ import java.util.Calendar;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Objects;
/*     */ import java.util.TimeZone;
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
/*     */ public class SevenZArchiveEntry
/*     */   implements ArchiveEntry
/*     */ {
/*     */   private String name;
/*     */   private boolean hasStream;
/*     */   private boolean isDirectory;
/*     */   private boolean isAntiItem;
/*     */   private boolean hasCreationDate;
/*     */   private boolean hasLastModifiedDate;
/*     */   private boolean hasAccessDate;
/*     */   private long creationDate;
/*     */   private long lastModifiedDate;
/*     */   private long accessDate;
/*     */   private boolean hasWindowsAttributes;
/*     */   private int windowsAttributes;
/*     */   private boolean hasCrc;
/*     */   private long crc;
/*     */   private long compressedCrc;
/*     */   private long size;
/*     */   private long compressedSize;
/*     */   private Iterable<? extends SevenZMethodConfiguration> contentMethods;
/*  53 */   static final SevenZArchiveEntry[] EMPTY_SEVEN_Z_ARCHIVE_ENTRY_ARRAY = new SevenZArchiveEntry[0];
/*     */ 
/*     */ 
/*     */ 
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
/*  67 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setName(String name) {
/*  76 */     this.name = name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasStream() {
/*  84 */     return this.hasStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHasStream(boolean hasStream) {
/*  92 */     this.hasStream = hasStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/* 102 */     return this.isDirectory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDirectory(boolean isDirectory) {
/* 111 */     this.isDirectory = isDirectory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAntiItem() {
/* 120 */     return this.isAntiItem;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAntiItem(boolean isAntiItem) {
/* 129 */     this.isAntiItem = isAntiItem;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getHasCreationDate() {
/* 137 */     return this.hasCreationDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHasCreationDate(boolean hasCreationDate) {
/* 145 */     this.hasCreationDate = hasCreationDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getCreationDate() {
/* 155 */     if (this.hasCreationDate) {
/* 156 */       return ntfsTimeToJavaTime(this.creationDate);
/*     */     }
/* 158 */     throw new UnsupportedOperationException("The entry doesn't have this timestamp");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCreationDate(long ntfsCreationDate) {
/* 168 */     this.creationDate = ntfsCreationDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCreationDate(Date creationDate) {
/* 176 */     this.hasCreationDate = (creationDate != null);
/* 177 */     if (this.hasCreationDate) {
/* 178 */       this.creationDate = javaTimeToNtfsTime(creationDate);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getHasLastModifiedDate() {
/* 187 */     return this.hasLastModifiedDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHasLastModifiedDate(boolean hasLastModifiedDate) {
/* 196 */     this.hasLastModifiedDate = hasLastModifiedDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getLastModifiedDate() {
/* 207 */     if (this.hasLastModifiedDate) {
/* 208 */       return ntfsTimeToJavaTime(this.lastModifiedDate);
/*     */     }
/* 210 */     throw new UnsupportedOperationException("The entry doesn't have this timestamp");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLastModifiedDate(long ntfsLastModifiedDate) {
/* 220 */     this.lastModifiedDate = ntfsLastModifiedDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLastModifiedDate(Date lastModifiedDate) {
/* 228 */     this.hasLastModifiedDate = (lastModifiedDate != null);
/* 229 */     if (this.hasLastModifiedDate) {
/* 230 */       this.lastModifiedDate = javaTimeToNtfsTime(lastModifiedDate);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getHasAccessDate() {
/* 239 */     return this.hasAccessDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHasAccessDate(boolean hasAcessDate) {
/* 247 */     this.hasAccessDate = hasAcessDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getAccessDate() {
/* 257 */     if (this.hasAccessDate) {
/* 258 */       return ntfsTimeToJavaTime(this.accessDate);
/*     */     }
/* 260 */     throw new UnsupportedOperationException("The entry doesn't have this timestamp");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAccessDate(long ntfsAccessDate) {
/* 270 */     this.accessDate = ntfsAccessDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAccessDate(Date accessDate) {
/* 278 */     this.hasAccessDate = (accessDate != null);
/* 279 */     if (this.hasAccessDate) {
/* 280 */       this.accessDate = javaTimeToNtfsTime(accessDate);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getHasWindowsAttributes() {
/* 289 */     return this.hasWindowsAttributes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHasWindowsAttributes(boolean hasWindowsAttributes) {
/* 297 */     this.hasWindowsAttributes = hasWindowsAttributes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWindowsAttributes() {
/* 305 */     return this.windowsAttributes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWindowsAttributes(int windowsAttributes) {
/* 313 */     this.windowsAttributes = windowsAttributes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getHasCrc() {
/* 323 */     return this.hasCrc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHasCrc(boolean hasCrc) {
/* 331 */     this.hasCrc = hasCrc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getCrc() {
/* 341 */     return (int)this.crc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setCrc(int crc) {
/* 351 */     this.crc = crc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCrcValue() {
/* 360 */     return this.crc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCrcValue(long crc) {
/* 369 */     this.crc = crc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   int getCompressedCrc() {
/* 379 */     return (int)this.compressedCrc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   void setCompressedCrc(int crc) {
/* 389 */     this.compressedCrc = crc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long getCompressedCrcValue() {
/* 398 */     return this.compressedCrc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setCompressedCrcValue(long crc) {
/* 407 */     this.compressedCrc = crc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getSize() {
/* 417 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSize(long size) {
/* 426 */     this.size = size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long getCompressedSize() {
/* 435 */     return this.compressedSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setCompressedSize(long size) {
/* 444 */     this.compressedSize = size;
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
/*     */   public void setContentMethods(Iterable<? extends SevenZMethodConfiguration> methods) {
/* 462 */     if (methods != null) {
/* 463 */       LinkedList<SevenZMethodConfiguration> l = new LinkedList<>();
/* 464 */       for (SevenZMethodConfiguration m : methods) {
/* 465 */         l.addLast(m);
/*     */       }
/* 467 */       this.contentMethods = Collections.unmodifiableList(l);
/*     */     } else {
/* 469 */       this.contentMethods = null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<? extends SevenZMethodConfiguration> getContentMethods() {
/* 488 */     return this.contentMethods;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 493 */     String n = getName();
/* 494 */     return (n == null) ? 0 : n.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 499 */     if (this == obj) {
/* 500 */       return true;
/*     */     }
/* 502 */     if (obj == null || getClass() != obj.getClass()) {
/* 503 */       return false;
/*     */     }
/* 505 */     SevenZArchiveEntry other = (SevenZArchiveEntry)obj;
/* 506 */     return (
/* 507 */       Objects.equals(this.name, other.name) && this.hasStream == other.hasStream && this.isDirectory == other.isDirectory && this.isAntiItem == other.isAntiItem && this.hasCreationDate == other.hasCreationDate && this.hasLastModifiedDate == other.hasLastModifiedDate && this.hasAccessDate == other.hasAccessDate && this.creationDate == other.creationDate && this.lastModifiedDate == other.lastModifiedDate && this.accessDate == other.accessDate && this.hasWindowsAttributes == other.hasWindowsAttributes && this.windowsAttributes == other.windowsAttributes && this.hasCrc == other.hasCrc && this.crc == other.crc && this.compressedCrc == other.compressedCrc && this.size == other.size && this.compressedSize == other.compressedSize && 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 524 */       equalSevenZMethods(this.contentMethods, other.contentMethods));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Date ntfsTimeToJavaTime(long ntfsTime) {
/* 534 */     Calendar ntfsEpoch = Calendar.getInstance();
/* 535 */     ntfsEpoch.setTimeZone(TimeZone.getTimeZone("GMT+0"));
/* 536 */     ntfsEpoch.set(1601, 0, 1, 0, 0, 0);
/* 537 */     ntfsEpoch.set(14, 0);
/* 538 */     long realTime = ntfsEpoch.getTimeInMillis() + ntfsTime / 10000L;
/* 539 */     return new Date(realTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long javaTimeToNtfsTime(Date date) {
/* 548 */     Calendar ntfsEpoch = Calendar.getInstance();
/* 549 */     ntfsEpoch.setTimeZone(TimeZone.getTimeZone("GMT+0"));
/* 550 */     ntfsEpoch.set(1601, 0, 1, 0, 0, 0);
/* 551 */     ntfsEpoch.set(14, 0);
/* 552 */     return (date.getTime() - ntfsEpoch.getTimeInMillis()) * 1000L * 10L;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean equalSevenZMethods(Iterable<? extends SevenZMethodConfiguration> c1, Iterable<? extends SevenZMethodConfiguration> c2) {
/* 557 */     if (c1 == null) {
/* 558 */       return (c2 == null);
/*     */     }
/* 560 */     if (c2 == null) {
/* 561 */       return false;
/*     */     }
/* 563 */     Iterator<? extends SevenZMethodConfiguration> i1 = c1.iterator();
/* 564 */     Iterator<? extends SevenZMethodConfiguration> i2 = c2.iterator();
/* 565 */     while (i1.hasNext()) {
/* 566 */       if (!i2.hasNext()) {
/* 567 */         return false;
/*     */       }
/* 569 */       if (!((SevenZMethodConfiguration)i1.next()).equals(i2.next())) {
/* 570 */         return false;
/*     */       }
/*     */     } 
/* 573 */     return !i2.hasNext();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\SevenZArchiveEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */