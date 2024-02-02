/*     */ package oshi.software.os.unix.freebsd;
/*     */ 
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.software.common.AbstractOSFileStore;
/*     */ import oshi.software.os.OSFileStore;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public class FreeBsdOSFileStore
/*     */   extends AbstractOSFileStore
/*     */ {
/*     */   private String logicalVolume;
/*     */   private String description;
/*     */   private String fsType;
/*     */   private long freeSpace;
/*     */   private long usableSpace;
/*     */   private long totalSpace;
/*     */   private long freeInodes;
/*     */   private long totalInodes;
/*     */   
/*     */   public FreeBsdOSFileStore(String name, String volume, String label, String mount, String options, String uuid, String logicalVolume, String description, String fsType, long freeSpace, long usableSpace, long totalSpace, long freeInodes, long totalInodes) {
/*  46 */     super(name, volume, label, mount, options, uuid);
/*  47 */     this.logicalVolume = logicalVolume;
/*  48 */     this.description = description;
/*  49 */     this.fsType = fsType;
/*  50 */     this.freeSpace = freeSpace;
/*  51 */     this.usableSpace = usableSpace;
/*  52 */     this.totalSpace = totalSpace;
/*  53 */     this.freeInodes = freeInodes;
/*  54 */     this.totalInodes = totalInodes;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getLogicalVolume() {
/*  59 */     return this.logicalVolume;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescription() {
/*  64 */     return this.description;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getType() {
/*  69 */     return this.fsType;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getFreeSpace() {
/*  74 */     return this.freeSpace;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUsableSpace() {
/*  79 */     return this.usableSpace;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTotalSpace() {
/*  84 */     return this.totalSpace;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getFreeInodes() {
/*  89 */     return this.freeInodes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTotalInodes() {
/*  94 */     return this.totalInodes;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/*  99 */     for (OSFileStore fileStore : (new FreeBsdFileSystem()).getFileStores()) {
/* 100 */       if (getName().equals(fileStore.getName()) && getVolume().equals(fileStore.getVolume()) && 
/* 101 */         getMount().equals(fileStore.getMount())) {
/* 102 */         this.logicalVolume = fileStore.getLogicalVolume();
/* 103 */         this.description = fileStore.getDescription();
/* 104 */         this.fsType = fileStore.getType();
/* 105 */         this.freeSpace = fileStore.getFreeSpace();
/* 106 */         this.usableSpace = fileStore.getUsableSpace();
/* 107 */         this.totalSpace = fileStore.getTotalSpace();
/* 108 */         this.freeInodes = fileStore.getFreeInodes();
/* 109 */         this.totalInodes = fileStore.getTotalInodes();
/* 110 */         return true;
/*     */       } 
/*     */     } 
/* 113 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\freebsd\FreeBsdOSFileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */