/*    */ package cn.hutool.core.lang.hash;
/*    */ 
/*    */ import cn.hutool.core.exceptions.UtilException;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.security.MessageDigest;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class KetamaHash
/*    */   implements Hash64<String>, Hash32<String>
/*    */ {
/*    */   public long hash64(String key) {
/* 19 */     byte[] bKey = md5(key);
/* 20 */     return (bKey[3] & 0xFF) << 24L | (bKey[2] & 0xFF) << 16L | (bKey[1] & 0xFF) << 8L | (bKey[0] & 0xFF);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int hash32(String key) {
/* 28 */     return (int)(hash64(key) & 0xFFFFFFFFL);
/*    */   }
/*    */ 
/*    */   
/*    */   public Number hash(String key) {
/* 33 */     return Long.valueOf(hash64(key));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static byte[] md5(String key) {
/*    */     MessageDigest md5;
/*    */     try {
/* 45 */       md5 = MessageDigest.getInstance("MD5");
/* 46 */     } catch (NoSuchAlgorithmException e) {
/* 47 */       throw new UtilException("MD5 algorithm not suooport!", e);
/*    */     } 
/* 49 */     return md5.digest(StrUtil.utf8Bytes(key));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\hash\KetamaHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */