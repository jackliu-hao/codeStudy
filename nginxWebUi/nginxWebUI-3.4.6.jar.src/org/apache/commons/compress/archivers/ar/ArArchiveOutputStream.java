/*     */ package org.apache.commons.compress.archivers.ar;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.Path;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*     */ import org.apache.commons.compress.utils.ArchiveUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ArArchiveOutputStream
/*     */   extends ArchiveOutputStream
/*     */ {
/*     */   public static final int LONGFILE_ERROR = 0;
/*     */   public static final int LONGFILE_BSD = 1;
/*     */   private final OutputStream out;
/*     */   private long entryOffset;
/*     */   private ArArchiveEntry prevEntry;
/*     */   private boolean haveUnclosedEntry;
/*  48 */   private int longFileMode = 0;
/*     */   
/*     */   private boolean finished;
/*     */ 
/*     */   
/*     */   public ArArchiveOutputStream(OutputStream pOut) {
/*  54 */     this.out = pOut;
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
/*     */   public void setLongFileMode(int longFileMode) {
/*  66 */     this.longFileMode = longFileMode;
/*     */   }
/*     */   
/*     */   private void writeArchiveHeader() throws IOException {
/*  70 */     byte[] header = ArchiveUtils.toAsciiBytes("!<arch>\n");
/*  71 */     this.out.write(header);
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeArchiveEntry() throws IOException {
/*  76 */     if (this.finished) {
/*  77 */       throw new IOException("Stream has already been finished");
/*     */     }
/*  79 */     if (this.prevEntry == null || !this.haveUnclosedEntry) {
/*  80 */       throw new IOException("No current entry to close");
/*     */     }
/*  82 */     if (this.entryOffset % 2L != 0L) {
/*  83 */       this.out.write(10);
/*     */     }
/*  85 */     this.haveUnclosedEntry = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void putArchiveEntry(ArchiveEntry pEntry) throws IOException {
/*  90 */     if (this.finished) {
/*  91 */       throw new IOException("Stream has already been finished");
/*     */     }
/*     */     
/*  94 */     ArArchiveEntry pArEntry = (ArArchiveEntry)pEntry;
/*  95 */     if (this.prevEntry == null) {
/*  96 */       writeArchiveHeader();
/*     */     } else {
/*  98 */       if (this.prevEntry.getLength() != this.entryOffset) {
/*  99 */         throw new IOException("Length does not match entry (" + this.prevEntry.getLength() + " != " + this.entryOffset);
/*     */       }
/*     */       
/* 102 */       if (this.haveUnclosedEntry) {
/* 103 */         closeArchiveEntry();
/*     */       }
/*     */     } 
/*     */     
/* 107 */     this.prevEntry = pArEntry;
/*     */     
/* 109 */     writeEntryHeader(pArEntry);
/*     */     
/* 111 */     this.entryOffset = 0L;
/* 112 */     this.haveUnclosedEntry = true;
/*     */   }
/*     */   
/*     */   private long fill(long pOffset, long pNewOffset, char pFill) throws IOException {
/* 116 */     long diff = pNewOffset - pOffset;
/*     */     
/* 118 */     if (diff > 0L) {
/* 119 */       for (int i = 0; i < diff; i++) {
/* 120 */         write(pFill);
/*     */       }
/*     */     }
/*     */     
/* 124 */     return pNewOffset;
/*     */   }
/*     */   
/*     */   private long write(String data) throws IOException {
/* 128 */     byte[] bytes = data.getBytes(StandardCharsets.US_ASCII);
/* 129 */     write(bytes);
/* 130 */     return bytes.length;
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeEntryHeader(ArArchiveEntry pEntry) throws IOException {
/* 135 */     long offset = 0L;
/* 136 */     boolean mustAppendName = false;
/*     */     
/* 138 */     String n = pEntry.getName();
/* 139 */     int nLength = n.length();
/* 140 */     if (0 == this.longFileMode && nLength > 16) {
/* 141 */       throw new IOException("File name too long, > 16 chars: " + n);
/*     */     }
/* 143 */     if (1 == this.longFileMode && (nLength > 16 || n
/* 144 */       .contains(" "))) {
/* 145 */       mustAppendName = true;
/* 146 */       offset += write("#1/" + 
/* 147 */           String.valueOf(nLength));
/*     */     } else {
/* 149 */       offset += write(n);
/*     */     } 
/*     */     
/* 152 */     offset = fill(offset, 16L, ' ');
/* 153 */     String m = "" + pEntry.getLastModified();
/* 154 */     if (m.length() > 12) {
/* 155 */       throw new IOException("Last modified too long");
/*     */     }
/* 157 */     offset += write(m);
/*     */     
/* 159 */     offset = fill(offset, 28L, ' ');
/* 160 */     String u = "" + pEntry.getUserId();
/* 161 */     if (u.length() > 6) {
/* 162 */       throw new IOException("User id too long");
/*     */     }
/* 164 */     offset += write(u);
/*     */     
/* 166 */     offset = fill(offset, 34L, ' ');
/* 167 */     String g = "" + pEntry.getGroupId();
/* 168 */     if (g.length() > 6) {
/* 169 */       throw new IOException("Group id too long");
/*     */     }
/* 171 */     offset += write(g);
/*     */     
/* 173 */     offset = fill(offset, 40L, ' ');
/* 174 */     String fm = "" + Integer.toString(pEntry.getMode(), 8);
/* 175 */     if (fm.length() > 8) {
/* 176 */       throw new IOException("Filemode too long");
/*     */     }
/* 178 */     offset += write(fm);
/*     */     
/* 180 */     offset = fill(offset, 48L, ' ');
/*     */     
/* 182 */     String s = String.valueOf(pEntry.getLength() + (mustAppendName ? nLength : 0L));
/*     */     
/* 184 */     if (s.length() > 10) {
/* 185 */       throw new IOException("Size too long");
/*     */     }
/* 187 */     offset += write(s);
/*     */     
/* 189 */     offset = fill(offset, 58L, ' ');
/*     */     
/* 191 */     offset += write("`\n");
/*     */     
/* 193 */     if (mustAppendName) {
/* 194 */       offset += write(n);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 201 */     this.out.write(b, off, len);
/* 202 */     count(len);
/* 203 */     this.entryOffset += len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 212 */       if (!this.finished) {
/* 213 */         finish();
/*     */       }
/*     */     } finally {
/* 216 */       this.out.close();
/* 217 */       this.prevEntry = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
/* 224 */     if (this.finished) {
/* 225 */       throw new IOException("Stream has already been finished");
/*     */     }
/* 227 */     return new ArArchiveEntry(inputFile, entryName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArchiveEntry createArchiveEntry(Path inputPath, String entryName, LinkOption... options) throws IOException {
/* 237 */     if (this.finished) {
/* 238 */       throw new IOException("Stream has already been finished");
/*     */     }
/* 240 */     return new ArArchiveEntry(inputPath, entryName, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public void finish() throws IOException {
/* 245 */     if (this.haveUnclosedEntry) {
/* 246 */       throw new IOException("This archive contains unclosed entries.");
/*     */     }
/* 248 */     if (this.finished) {
/* 249 */       throw new IOException("This archive has already been finished");
/*     */     }
/* 251 */     this.finished = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\ar\ArArchiveOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */