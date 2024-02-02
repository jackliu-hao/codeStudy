/*     */ package org.apache.commons.compress.archivers.arj;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class MainHeader
/*     */ {
/*     */   int archiverVersionNumber;
/*     */   int minVersionToExtract;
/*     */   int hostOS;
/*     */   int arjFlags;
/*     */   int securityVersion;
/*     */   int fileType;
/*     */   int reserved;
/*     */   int dateTimeCreated;
/*     */   int dateTimeModified;
/*     */   long archiveSize;
/*     */   int securityEnvelopeFilePosition;
/*     */   int fileSpecPosition;
/*     */   int securityEnvelopeLength;
/*     */   int encryptionVersion;
/*     */   int lastChapter;
/*     */   int arjProtectionFactor;
/*     */   int arjFlags2;
/*     */   String name;
/*     */   String comment;
/*     */   byte[] extendedHeaderBytes;
/*     */   
/*     */   static class Flags
/*     */   {
/*     */     static final int GARBLED = 1;
/*     */     static final int OLD_SECURED_NEW_ANSI_PAGE = 2;
/*     */     static final int VOLUME = 4;
/*     */     static final int ARJPROT = 8;
/*     */     static final int PATHSYM = 16;
/*     */     static final int BACKUP = 32;
/*     */     static final int SECURED = 64;
/*     */     static final int ALTNAME = 128;
/*     */   }
/*     */   
/*     */   static class HostOS
/*     */   {
/*     */     static final int MS_DOS = 0;
/*     */     static final int PRIMOS = 1;
/*     */     static final int UNIX = 2;
/*     */     static final int AMIGA = 3;
/*     */     static final int MAC_OS = 4;
/*     */     static final int OS2 = 5;
/*     */     static final int APPLE_GS = 6;
/*     */     static final int ATARI_ST = 7;
/*     */     static final int NeXT = 8;
/*     */     static final int VAX_VMS = 9;
/*     */     static final int WIN95 = 10;
/*     */     static final int WIN32 = 11;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  72 */     StringBuilder builder = new StringBuilder();
/*  73 */     builder.append("MainHeader [archiverVersionNumber=");
/*  74 */     builder.append(this.archiverVersionNumber);
/*  75 */     builder.append(", minVersionToExtract=");
/*  76 */     builder.append(this.minVersionToExtract);
/*  77 */     builder.append(", hostOS=");
/*  78 */     builder.append(this.hostOS);
/*  79 */     builder.append(", arjFlags=");
/*  80 */     builder.append(this.arjFlags);
/*  81 */     builder.append(", securityVersion=");
/*  82 */     builder.append(this.securityVersion);
/*  83 */     builder.append(", fileType=");
/*  84 */     builder.append(this.fileType);
/*  85 */     builder.append(", reserved=");
/*  86 */     builder.append(this.reserved);
/*  87 */     builder.append(", dateTimeCreated=");
/*  88 */     builder.append(this.dateTimeCreated);
/*  89 */     builder.append(", dateTimeModified=");
/*  90 */     builder.append(this.dateTimeModified);
/*  91 */     builder.append(", archiveSize=");
/*  92 */     builder.append(this.archiveSize);
/*  93 */     builder.append(", securityEnvelopeFilePosition=");
/*  94 */     builder.append(this.securityEnvelopeFilePosition);
/*  95 */     builder.append(", fileSpecPosition=");
/*  96 */     builder.append(this.fileSpecPosition);
/*  97 */     builder.append(", securityEnvelopeLength=");
/*  98 */     builder.append(this.securityEnvelopeLength);
/*  99 */     builder.append(", encryptionVersion=");
/* 100 */     builder.append(this.encryptionVersion);
/* 101 */     builder.append(", lastChapter=");
/* 102 */     builder.append(this.lastChapter);
/* 103 */     builder.append(", arjProtectionFactor=");
/* 104 */     builder.append(this.arjProtectionFactor);
/* 105 */     builder.append(", arjFlags2=");
/* 106 */     builder.append(this.arjFlags2);
/* 107 */     builder.append(", name=");
/* 108 */     builder.append(this.name);
/* 109 */     builder.append(", comment=");
/* 110 */     builder.append(this.comment);
/* 111 */     builder.append(", extendedHeaderBytes=");
/* 112 */     builder.append(Arrays.toString(this.extendedHeaderBytes));
/* 113 */     builder.append("]");
/* 114 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\arj\MainHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */