/*    */ package cn.hutool.core.util;
/*    */ 
/*    */ import cn.hutool.core.io.FastByteArrayOutputStream;
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.io.Serializable;
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
/*    */ public class SerializeUtil
/*    */ {
/*    */   public static <T> T clone(T obj) {
/* 29 */     if (false == obj instanceof Serializable) {
/* 30 */       return null;
/*    */     }
/* 32 */     return deserialize(serialize(obj));
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
/*    */   public static <T> byte[] serialize(T obj) {
/* 44 */     if (false == obj instanceof Serializable) {
/* 45 */       return null;
/*    */     }
/* 47 */     FastByteArrayOutputStream byteOut = new FastByteArrayOutputStream();
/* 48 */     IoUtil.writeObjects((OutputStream)byteOut, false, new Serializable[] { (Serializable)obj });
/* 49 */     return byteOut.toByteArray();
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
/*    */   
/*    */   public static <T> T deserialize(byte[] bytes) {
/* 65 */     return (T)IoUtil.readObj(new ByteArrayInputStream(bytes));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\SerializeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */