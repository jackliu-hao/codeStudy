/*    */ package cn.hutool.system;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JvmSpecInfo
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 11 */   private final String JAVA_VM_SPECIFICATION_NAME = SystemUtil.get("java.vm.specification.name", false);
/* 12 */   private final String JAVA_VM_SPECIFICATION_VERSION = SystemUtil.get("java.vm.specification.version", false);
/* 13 */   private final String JAVA_VM_SPECIFICATION_VENDOR = SystemUtil.get("java.vm.specification.vendor", false);
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
/* 26 */     return this.JAVA_VM_SPECIFICATION_NAME;
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
/* 40 */     return this.JAVA_VM_SPECIFICATION_VERSION;
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
/* 54 */     return this.JAVA_VM_SPECIFICATION_VENDOR;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String toString() {
/* 64 */     StringBuilder builder = new StringBuilder();
/*    */     
/* 66 */     SystemUtil.append(builder, "JavaVM Spec. Name:    ", getName());
/* 67 */     SystemUtil.append(builder, "JavaVM Spec. Version: ", getVersion());
/* 68 */     SystemUtil.append(builder, "JavaVM Spec. Vendor:  ", getVendor());
/*    */     
/* 70 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\JvmSpecInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */