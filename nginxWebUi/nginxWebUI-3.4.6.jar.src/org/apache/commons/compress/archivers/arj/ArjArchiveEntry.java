/*     */ package org.apache.commons.compress.archivers.arj;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Date;
/*     */ import java.util.regex.Matcher;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.zip.ZipUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ArjArchiveEntry
/*     */   implements ArchiveEntry
/*     */ {
/*     */   private final LocalFileHeader localFileHeader;
/*     */   
/*     */   public ArjArchiveEntry() {
/*  37 */     this.localFileHeader = new LocalFileHeader();
/*     */   }
/*     */   
/*     */   ArjArchiveEntry(LocalFileHeader localFileHeader) {
/*  41 */     this.localFileHeader = localFileHeader;
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
/*  53 */     if ((this.localFileHeader.arjFlags & 0x10) != 0) {
/*  54 */       return this.localFileHeader.name.replaceAll("/", 
/*  55 */           Matcher.quoteReplacement(File.separator));
/*     */     }
/*  57 */     return this.localFileHeader.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getSize() {
/*  67 */     return this.localFileHeader.originalSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/*  76 */     return (this.localFileHeader.fileType == 3);
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
/*     */   public Date getLastModifiedDate() {
/*  97 */     long ts = isHostOsUnix() ? (this.localFileHeader.dateTimeModified * 1000L) : ZipUtil.dosToJavaTime(0xFFFFFFFFL & this.localFileHeader.dateTimeModified);
/*  98 */     return new Date(ts);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMode() {
/* 109 */     return this.localFileHeader.fileAccessMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getUnixMode() {
/* 120 */     return isHostOsUnix() ? getMode() : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHostOs() {
/* 129 */     return this.localFileHeader.hostOS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isHostOsUnix() {
/* 139 */     return (getHostOs() == 2 || getHostOs() == 8);
/*     */   }
/*     */   
/*     */   int getMethod() {
/* 143 */     return this.localFileHeader.method;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 148 */     String name = getName();
/* 149 */     return (name == null) ? 0 : name.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 154 */     if (this == obj) {
/* 155 */       return true;
/*     */     }
/* 157 */     if (obj == null || getClass() != obj.getClass()) {
/* 158 */       return false;
/*     */     }
/* 160 */     ArjArchiveEntry other = (ArjArchiveEntry)obj;
/* 161 */     return this.localFileHeader.equals(other.localFileHeader);
/*     */   }
/*     */   
/*     */   public static class HostOs {
/*     */     public static final int DOS = 0;
/*     */     public static final int PRIMOS = 1;
/*     */     public static final int UNIX = 2;
/*     */     public static final int AMIGA = 3;
/*     */     public static final int MAC_OS = 4;
/*     */     public static final int OS_2 = 5;
/*     */     public static final int APPLE_GS = 6;
/*     */     public static final int ATARI_ST = 7;
/*     */     public static final int NEXT = 8;
/*     */     public static final int VAX_VMS = 9;
/*     */     public static final int WIN95 = 10;
/*     */     public static final int WIN32 = 11;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\arj\ArjArchiveEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */