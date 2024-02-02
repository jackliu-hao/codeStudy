/*     */ package org.apache.commons.compress.archivers.dump;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Date;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DumpArchiveSummary
/*     */ {
/*     */   private long dumpDate;
/*     */   private long previousDumpDate;
/*     */   private int volume;
/*     */   private String label;
/*     */   private int level;
/*     */   private String filesys;
/*     */   private String devname;
/*     */   private String hostname;
/*     */   private int flags;
/*     */   private int firstrec;
/*     */   private int ntrec;
/*     */   
/*     */   DumpArchiveSummary(byte[] buffer, ZipEncoding encoding) throws IOException {
/*  47 */     this.dumpDate = 1000L * DumpArchiveUtil.convert32(buffer, 4);
/*  48 */     this.previousDumpDate = 1000L * DumpArchiveUtil.convert32(buffer, 8);
/*  49 */     this.volume = DumpArchiveUtil.convert32(buffer, 12);
/*  50 */     this.label = DumpArchiveUtil.decode(encoding, buffer, 676, 16).trim();
/*  51 */     this.level = DumpArchiveUtil.convert32(buffer, 692);
/*  52 */     this.filesys = DumpArchiveUtil.decode(encoding, buffer, 696, 64).trim();
/*  53 */     this.devname = DumpArchiveUtil.decode(encoding, buffer, 760, 64).trim();
/*  54 */     this.hostname = DumpArchiveUtil.decode(encoding, buffer, 824, 64).trim();
/*  55 */     this.flags = DumpArchiveUtil.convert32(buffer, 888);
/*  56 */     this.firstrec = DumpArchiveUtil.convert32(buffer, 892);
/*  57 */     this.ntrec = DumpArchiveUtil.convert32(buffer, 896);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getDumpDate() {
/*  67 */     return new Date(this.dumpDate);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDumpDate(Date dumpDate) {
/*  75 */     this.dumpDate = dumpDate.getTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getPreviousDumpDate() {
/*  83 */     return new Date(this.previousDumpDate);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPreviousDumpDate(Date previousDumpDate) {
/*  91 */     this.previousDumpDate = previousDumpDate.getTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getVolume() {
/*  99 */     return this.volume;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVolume(int volume) {
/* 107 */     this.volume = volume;
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
/*     */   public int getLevel() {
/* 119 */     return this.level;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLevel(int level) {
/* 127 */     this.level = level;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLabel() {
/* 136 */     return this.label;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLabel(String label) {
/* 144 */     this.label = label;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFilesystem() {
/* 152 */     return this.filesys;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFilesystem(String fileSystem) {
/* 160 */     this.filesys = fileSystem;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDevname() {
/* 168 */     return this.devname;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDevname(String devname) {
/* 176 */     this.devname = devname;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHostname() {
/* 184 */     return this.hostname;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHostname(String hostname) {
/* 192 */     this.hostname = hostname;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFlags() {
/* 200 */     return this.flags;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFlags(int flags) {
/* 208 */     this.flags = flags;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFirstRecord() {
/* 216 */     return this.firstrec;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFirstRecord(int firstrec) {
/* 224 */     this.firstrec = firstrec;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNTRec() {
/* 233 */     return this.ntrec;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNTRec(int ntrec) {
/* 241 */     this.ntrec = ntrec;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNewHeader() {
/* 251 */     return ((this.flags & 0x1) == 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNewInode() {
/* 260 */     return ((this.flags & 0x2) == 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCompressed() {
/* 269 */     return ((this.flags & 0x80) == 128);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMetaDataOnly() {
/* 277 */     return ((this.flags & 0x100) == 256);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExtendedAttributes() {
/* 285 */     return ((this.flags & 0x8000) == 32768);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 290 */     int hash = 17;
/*     */     
/* 292 */     if (this.label != null) {
/* 293 */       hash = this.label.hashCode();
/*     */     }
/*     */     
/* 296 */     hash = (int)(hash + 31L * this.dumpDate);
/*     */     
/* 298 */     if (this.hostname != null) {
/* 299 */       hash = 31 * this.hostname.hashCode() + 17;
/*     */     }
/*     */     
/* 302 */     if (this.devname != null) {
/* 303 */       hash = 31 * this.devname.hashCode() + 17;
/*     */     }
/*     */     
/* 306 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 311 */     if (this == o) {
/* 312 */       return true;
/*     */     }
/*     */     
/* 315 */     if (o == null || !o.getClass().equals(getClass())) {
/* 316 */       return false;
/*     */     }
/*     */     
/* 319 */     DumpArchiveSummary rhs = (DumpArchiveSummary)o;
/*     */     
/* 321 */     if (this.dumpDate != rhs.dumpDate) {
/* 322 */       return false;
/*     */     }
/*     */     
/* 325 */     if (getHostname() == null || 
/* 326 */       !getHostname().equals(rhs.getHostname())) {
/* 327 */       return false;
/*     */     }
/*     */     
/* 330 */     return (getDevname() != null && getDevname().equals(rhs.getDevname()));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\dump\DumpArchiveSummary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */