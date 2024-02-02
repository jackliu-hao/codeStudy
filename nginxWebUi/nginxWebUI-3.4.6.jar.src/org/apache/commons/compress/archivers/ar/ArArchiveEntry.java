/*     */ package org.apache.commons.compress.archivers.ar;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Date;
/*     */ import java.util.Objects;
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
/*     */ public class ArArchiveEntry
/*     */   implements ArchiveEntry
/*     */ {
/*     */   public static final String HEADER = "!<arch>\n";
/*     */   public static final String TRAILER = "`\n";
/*     */   private final String name;
/*     */   private final int userId;
/*     */   private final int groupId;
/*     */   private final int mode;
/*     */   private static final int DEFAULT_MODE = 33188;
/*     */   private final long lastModified;
/*     */   private final long length;
/*     */   
/*     */   public ArArchiveEntry(String name, long length) {
/*  90 */     this(name, length, 0, 0, 33188, 
/*  91 */         System.currentTimeMillis() / 1000L);
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
/*     */   public ArArchiveEntry(String name, long length, int userId, int groupId, int mode, long lastModified) {
/* 106 */     this.name = name;
/* 107 */     if (length < 0L) {
/* 108 */       throw new IllegalArgumentException("length must not be negative");
/*     */     }
/* 110 */     this.length = length;
/* 111 */     this.userId = userId;
/* 112 */     this.groupId = groupId;
/* 113 */     this.mode = mode;
/* 114 */     this.lastModified = lastModified;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArArchiveEntry(File inputFile, String entryName) {
/* 124 */     this(entryName, inputFile.isFile() ? inputFile.length() : 0L, 0, 0, 33188, inputFile
/* 125 */         .lastModified() / 1000L);
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
/*     */   public ArArchiveEntry(Path inputPath, String entryName, LinkOption... options) throws IOException {
/* 137 */     this(entryName, Files.isRegularFile(inputPath, options) ? Files.size(inputPath) : 0L, 0, 0, 33188, 
/* 138 */         Files.getLastModifiedTime(inputPath, options).toMillis() / 1000L);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSize() {
/* 143 */     return getLength();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 148 */     return this.name;
/*     */   }
/*     */   
/*     */   public int getUserId() {
/* 152 */     return this.userId;
/*     */   }
/*     */   
/*     */   public int getGroupId() {
/* 156 */     return this.groupId;
/*     */   }
/*     */   
/*     */   public int getMode() {
/* 160 */     return this.mode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLastModified() {
/* 168 */     return this.lastModified;
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getLastModifiedDate() {
/* 173 */     return new Date(1000L * getLastModified());
/*     */   }
/*     */   
/*     */   public long getLength() {
/* 177 */     return this.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/* 182 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 187 */     return Objects.hash(new Object[] { this.name });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 192 */     if (this == obj) {
/* 193 */       return true;
/*     */     }
/* 195 */     if (obj == null || getClass() != obj.getClass()) {
/* 196 */       return false;
/*     */     }
/* 198 */     ArArchiveEntry other = (ArArchiveEntry)obj;
/* 199 */     if (this.name == null) {
/* 200 */       return (other.name == null);
/*     */     }
/* 202 */     return this.name.equals(other.name);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\ar\ArArchiveEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */