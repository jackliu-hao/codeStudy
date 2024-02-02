/*    */ package cn.hutool.system;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JavaSpecInfo
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 11 */   private final String JAVA_SPECIFICATION_NAME = SystemUtil.get("java.specification.name", false);
/* 12 */   private final String JAVA_SPECIFICATION_VERSION = SystemUtil.get("java.specification.version", false);
/* 13 */   private final String JAVA_SPECIFICATION_VENDOR = SystemUtil.get("java.specification.vendor", false);
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
/* 26 */     return this.JAVA_SPECIFICATION_NAME;
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
/*    */   
/*    */   public final String getVersion() {
/* 41 */     return this.JAVA_SPECIFICATION_VERSION;
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
/* 55 */     return this.JAVA_SPECIFICATION_VENDOR;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String toString() {
/* 65 */     StringBuilder builder = new StringBuilder();
/*    */     
/* 67 */     SystemUtil.append(builder, "Java Spec. Name:    ", getName());
/* 68 */     SystemUtil.append(builder, "Java Spec. Version: ", getVersion());
/* 69 */     SystemUtil.append(builder, "Java Spec. Vendor:  ", getVendor());
/*    */     
/* 71 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\JavaSpecInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */