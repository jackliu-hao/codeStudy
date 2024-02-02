/*    */ package cn.hutool.system;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JvmInfo
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 11 */   private final String JAVA_VM_NAME = SystemUtil.get("java.vm.name", false);
/* 12 */   private final String JAVA_VM_VERSION = SystemUtil.get("java.vm.version", false);
/* 13 */   private final String JAVA_VM_VENDOR = SystemUtil.get("java.vm.vendor", false);
/* 14 */   private final String JAVA_VM_INFO = SystemUtil.get("java.vm.info", false);
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
/*    */   public final String getName() {
/* 27 */     return this.JAVA_VM_NAME;
/*    */   }
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
/*    */   public final String getVersion() {
/* 41 */     return this.JAVA_VM_VERSION;
/*    */   }
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
/*    */   public final String getVendor() {
/* 55 */     return this.JAVA_VM_VENDOR;
/*    */   }
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
/*    */   public final String getInfo() {
/* 69 */     return this.JAVA_VM_INFO;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String toString() {
/* 79 */     StringBuilder builder = new StringBuilder();
/*    */     
/* 81 */     SystemUtil.append(builder, "JavaVM Name:    ", getName());
/* 82 */     SystemUtil.append(builder, "JavaVM Version: ", getVersion());
/* 83 */     SystemUtil.append(builder, "JavaVM Vendor:  ", getVendor());
/* 84 */     SystemUtil.append(builder, "JavaVM Info:    ", getInfo());
/*    */     
/* 86 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\JvmInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */