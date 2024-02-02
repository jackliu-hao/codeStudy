/*     */ package oshi.software.os.windows;
/*     */ 
/*     */ import java.util.List;
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
/*     */ 
/*     */ @ThreadSafe
/*     */ public class WindowsOSFileStore
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
/*     */   public WindowsOSFileStore(String name, String volume, String label, String mount, String options, String uuid, String logicalVolume, String description, String fsType, long freeSpace, long usableSpace, long totalSpace, long freeInodes, long totalInodes) {
/*  48 */     super(name, volume, label, mount, options, uuid);
/*  49 */     this.logicalVolume = logicalVolume;
/*  50 */     this.description = description;
/*  51 */     this.fsType = fsType;
/*  52 */     this.freeSpace = freeSpace;
/*  53 */     this.usableSpace = usableSpace;
/*  54 */     this.totalSpace = totalSpace;
/*  55 */     this.freeInodes = freeInodes;
/*  56 */     this.totalInodes = totalInodes;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getLogicalVolume() {
/*  61 */     return this.logicalVolume;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescription() {
/*  66 */     return this.description;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getType() {
/*  71 */     return this.fsType;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getFreeSpace() {
/*  76 */     return this.freeSpace;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUsableSpace() {
/*  81 */     return this.usableSpace;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTotalSpace() {
/*  86 */     return this.totalSpace;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getFreeInodes() {
/*  91 */     return this.freeInodes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTotalInodes() {
/*  96 */     return this.totalInodes;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 102 */     List<OSFileStore> volumes = WindowsFileSystem.getLocalVolumes(getVolume());
/* 103 */     if (volumes.isEmpty()) {
/*     */       
/* 105 */       String nameToMatch = (getMount().length() < 2) ? null : getMount().substring(0, 2);
/* 106 */       volumes = WindowsFileSystem.getWmiVolumes(nameToMatch, false);
/*     */     } 
/* 108 */     for (OSFileStore fileStore : volumes) {
/* 109 */       if (getVolume().equals(fileStore.getVolume()) && getMount().equals(fileStore.getMount())) {
/* 110 */         this.logicalVolume = fileStore.getLogicalVolume();
/* 111 */         this.description = fileStore.getDescription();
/* 112 */         this.fsType = fileStore.getType();
/* 113 */         this.freeSpace = fileStore.getFreeSpace();
/* 114 */         this.usableSpace = fileStore.getUsableSpace();
/* 115 */         this.totalSpace = fileStore.getTotalSpace();
/* 116 */         this.freeInodes = fileStore.getFreeInodes();
/* 117 */         this.totalInodes = fileStore.getTotalInodes();
/* 118 */         return true;
/*     */       } 
/*     */     } 
/* 121 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\windows\WindowsOSFileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */