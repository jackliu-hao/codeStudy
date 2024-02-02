/*     */ package org.apache.commons.compress.archivers.arj;
/*     */ 
/*     */ import java.util.Arrays;
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
/*     */ class LocalFileHeader
/*     */ {
/*     */   int archiverVersionNumber;
/*     */   int minVersionToExtract;
/*     */   int hostOS;
/*     */   int arjFlags;
/*     */   int method;
/*     */   int fileType;
/*     */   int reserved;
/*     */   int dateTimeModified;
/*     */   long compressedSize;
/*     */   long originalSize;
/*     */   long originalCrc32;
/*     */   int fileSpecPosition;
/*     */   int fileAccessMode;
/*     */   int firstChapter;
/*     */   int lastChapter;
/*     */   int extendedFilePosition;
/*     */   int dateTimeAccessed;
/*     */   int dateTimeCreated;
/*     */   int originalSizeEvenForVolumes;
/*     */   String name;
/*     */   String comment;
/*     */   byte[][] extendedHeaders;
/*     */   
/*     */   static class Flags
/*     */   {
/*     */     static final int GARBLED = 1;
/*     */     static final int VOLUME = 4;
/*     */     static final int EXTFILE = 8;
/*     */     static final int PATHSYM = 16;
/*     */     static final int BACKUP = 32;
/*     */   }
/*     */   
/*     */   static class FileTypes
/*     */   {
/*     */     static final int BINARY = 0;
/*     */     static final int SEVEN_BIT_TEXT = 1;
/*     */     static final int COMMENT_HEADER = 2;
/*     */     static final int DIRECTORY = 3;
/*     */     static final int VOLUME_LABEL = 4;
/*     */     static final int CHAPTER_LABEL = 5;
/*     */   }
/*     */   
/*     */   static class Methods
/*     */   {
/*     */     static final int STORED = 0;
/*     */     static final int COMPRESSED_MOST = 1;
/*     */     static final int COMPRESSED = 2;
/*     */     static final int COMPRESSED_FASTER = 3;
/*     */     static final int COMPRESSED_FASTEST = 4;
/*     */     static final int NO_DATA_NO_CRC = 8;
/*     */     static final int NO_DATA = 9;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  79 */     StringBuilder builder = new StringBuilder();
/*  80 */     builder.append("LocalFileHeader [archiverVersionNumber=");
/*  81 */     builder.append(this.archiverVersionNumber);
/*  82 */     builder.append(", minVersionToExtract=");
/*  83 */     builder.append(this.minVersionToExtract);
/*  84 */     builder.append(", hostOS=");
/*  85 */     builder.append(this.hostOS);
/*  86 */     builder.append(", arjFlags=");
/*  87 */     builder.append(this.arjFlags);
/*  88 */     builder.append(", method=");
/*  89 */     builder.append(this.method);
/*  90 */     builder.append(", fileType=");
/*  91 */     builder.append(this.fileType);
/*  92 */     builder.append(", reserved=");
/*  93 */     builder.append(this.reserved);
/*  94 */     builder.append(", dateTimeModified=");
/*  95 */     builder.append(this.dateTimeModified);
/*  96 */     builder.append(", compressedSize=");
/*  97 */     builder.append(this.compressedSize);
/*  98 */     builder.append(", originalSize=");
/*  99 */     builder.append(this.originalSize);
/* 100 */     builder.append(", originalCrc32=");
/* 101 */     builder.append(this.originalCrc32);
/* 102 */     builder.append(", fileSpecPosition=");
/* 103 */     builder.append(this.fileSpecPosition);
/* 104 */     builder.append(", fileAccessMode=");
/* 105 */     builder.append(this.fileAccessMode);
/* 106 */     builder.append(", firstChapter=");
/* 107 */     builder.append(this.firstChapter);
/* 108 */     builder.append(", lastChapter=");
/* 109 */     builder.append(this.lastChapter);
/* 110 */     builder.append(", extendedFilePosition=");
/* 111 */     builder.append(this.extendedFilePosition);
/* 112 */     builder.append(", dateTimeAccessed=");
/* 113 */     builder.append(this.dateTimeAccessed);
/* 114 */     builder.append(", dateTimeCreated=");
/* 115 */     builder.append(this.dateTimeCreated);
/* 116 */     builder.append(", originalSizeEvenForVolumes=");
/* 117 */     builder.append(this.originalSizeEvenForVolumes);
/* 118 */     builder.append(", name=");
/* 119 */     builder.append(this.name);
/* 120 */     builder.append(", comment=");
/* 121 */     builder.append(this.comment);
/* 122 */     builder.append(", extendedHeaders=");
/* 123 */     builder.append(Arrays.toString((Object[])this.extendedHeaders));
/* 124 */     builder.append("]");
/* 125 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 130 */     return (this.name == null) ? 0 : this.name.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 135 */     if (this == obj) {
/* 136 */       return true;
/*     */     }
/* 138 */     if (obj == null || getClass() != obj.getClass()) {
/* 139 */       return false;
/*     */     }
/* 141 */     LocalFileHeader other = (LocalFileHeader)obj;
/* 142 */     return (this.archiverVersionNumber == other.archiverVersionNumber && this.minVersionToExtract == other.minVersionToExtract && this.hostOS == other.hostOS && this.arjFlags == other.arjFlags && this.method == other.method && this.fileType == other.fileType && this.reserved == other.reserved && this.dateTimeModified == other.dateTimeModified && this.compressedSize == other.compressedSize && this.originalSize == other.originalSize && this.originalCrc32 == other.originalCrc32 && this.fileSpecPosition == other.fileSpecPosition && this.fileAccessMode == other.fileAccessMode && this.firstChapter == other.firstChapter && this.lastChapter == other.lastChapter && this.extendedFilePosition == other.extendedFilePosition && this.dateTimeAccessed == other.dateTimeAccessed && this.dateTimeCreated == other.dateTimeCreated && this.originalSizeEvenForVolumes == other.originalSizeEvenForVolumes && 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 162 */       Objects.equals(this.name, other.name) && 
/* 163 */       Objects.equals(this.comment, other.comment) && 
/* 164 */       Arrays.deepEquals((Object[])this.extendedHeaders, (Object[])other.extendedHeaders));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\arj\LocalFileHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */