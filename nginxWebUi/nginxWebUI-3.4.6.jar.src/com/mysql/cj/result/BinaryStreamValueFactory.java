/*    */ package com.mysql.cj.result;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.InputStream;
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
/*    */ public class BinaryStreamValueFactory
/*    */   extends DefaultValueFactory<InputStream>
/*    */ {
/*    */   public BinaryStreamValueFactory(PropertySet pset) {
/* 43 */     super(pset);
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream createFromBytes(byte[] bytes, int offset, int length, Field f) {
/* 48 */     return new ByteArrayInputStream(bytes, offset, length);
/*    */   }
/*    */   
/*    */   public String getTargetTypeName() {
/* 52 */     return InputStream.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\BinaryStreamValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */