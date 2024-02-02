/*    */ package oshi.software.common;
/*    */ 
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.software.os.OSFileStore;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public abstract class AbstractOSFileStore
/*    */   implements OSFileStore
/*    */ {
/*    */   private String name;
/*    */   private String volume;
/*    */   private String label;
/*    */   private String mount;
/*    */   private String options;
/*    */   private String uuid;
/*    */   
/*    */   protected AbstractOSFileStore(String name, String volume, String label, String mount, String options, String uuid) {
/* 40 */     this.name = name;
/* 41 */     this.volume = volume;
/* 42 */     this.label = label;
/* 43 */     this.mount = mount;
/* 44 */     this.options = options;
/* 45 */     this.uuid = uuid;
/*    */   }
/*    */ 
/*    */   
/*    */   protected AbstractOSFileStore() {}
/*    */ 
/*    */   
/*    */   public String getName() {
/* 53 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getVolume() {
/* 58 */     return this.volume;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getLabel() {
/* 63 */     return this.label;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMount() {
/* 68 */     return this.mount;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getOptions() {
/* 73 */     return this.options;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUUID() {
/* 78 */     return this.uuid;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 83 */     return "OSFileStore [name=" + getName() + ", volume=" + getVolume() + ", label=" + getLabel() + ", logicalVolume=" + 
/* 84 */       getLogicalVolume() + ", mount=" + getMount() + ", description=" + 
/* 85 */       getDescription() + ", fsType=" + getType() + ", options=\"" + getOptions() + "\", uuid=" + getUUID() + ", freeSpace=" + 
/* 86 */       getFreeSpace() + ", usableSpace=" + getUsableSpace() + ", totalSpace=" + 
/* 87 */       getTotalSpace() + ", freeInodes=" + getFreeInodes() + ", totalInodes=" + getTotalInodes() + "]";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\common\AbstractOSFileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */