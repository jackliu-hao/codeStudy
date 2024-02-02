/*    */ package cn.hutool.core.net;
/*    */ 
/*    */ import cn.hutool.core.map.BiMap;
/*    */ import java.util.HashMap;
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
/*    */ public class MaskBit
/*    */ {
/* 19 */   private static final BiMap<Integer, String> MASK_BIT_MAP = new BiMap(new HashMap<>(32)); static {
/* 20 */     MASK_BIT_MAP.put(Integer.valueOf(1), "128.0.0.0");
/* 21 */     MASK_BIT_MAP.put(Integer.valueOf(2), "192.0.0.0");
/* 22 */     MASK_BIT_MAP.put(Integer.valueOf(3), "224.0.0.0");
/* 23 */     MASK_BIT_MAP.put(Integer.valueOf(4), "240.0.0.0");
/* 24 */     MASK_BIT_MAP.put(Integer.valueOf(5), "248.0.0.0");
/* 25 */     MASK_BIT_MAP.put(Integer.valueOf(6), "252.0.0.0");
/* 26 */     MASK_BIT_MAP.put(Integer.valueOf(7), "254.0.0.0");
/* 27 */     MASK_BIT_MAP.put(Integer.valueOf(8), "255.0.0.0");
/* 28 */     MASK_BIT_MAP.put(Integer.valueOf(9), "255.128.0.0");
/* 29 */     MASK_BIT_MAP.put(Integer.valueOf(10), "255.192.0.0");
/* 30 */     MASK_BIT_MAP.put(Integer.valueOf(11), "255.224.0.0");
/* 31 */     MASK_BIT_MAP.put(Integer.valueOf(12), "255.240.0.0");
/* 32 */     MASK_BIT_MAP.put(Integer.valueOf(13), "255.248.0.0");
/* 33 */     MASK_BIT_MAP.put(Integer.valueOf(14), "255.252.0.0");
/* 34 */     MASK_BIT_MAP.put(Integer.valueOf(15), "255.254.0.0");
/* 35 */     MASK_BIT_MAP.put(Integer.valueOf(16), "255.255.0.0");
/* 36 */     MASK_BIT_MAP.put(Integer.valueOf(17), "255.255.128.0");
/* 37 */     MASK_BIT_MAP.put(Integer.valueOf(18), "255.255.192.0");
/* 38 */     MASK_BIT_MAP.put(Integer.valueOf(19), "255.255.224.0");
/* 39 */     MASK_BIT_MAP.put(Integer.valueOf(20), "255.255.240.0");
/* 40 */     MASK_BIT_MAP.put(Integer.valueOf(21), "255.255.248.0");
/* 41 */     MASK_BIT_MAP.put(Integer.valueOf(22), "255.255.252.0");
/* 42 */     MASK_BIT_MAP.put(Integer.valueOf(23), "255.255.254.0");
/* 43 */     MASK_BIT_MAP.put(Integer.valueOf(24), "255.255.255.0");
/* 44 */     MASK_BIT_MAP.put(Integer.valueOf(25), "255.255.255.128");
/* 45 */     MASK_BIT_MAP.put(Integer.valueOf(26), "255.255.255.192");
/* 46 */     MASK_BIT_MAP.put(Integer.valueOf(27), "255.255.255.224");
/* 47 */     MASK_BIT_MAP.put(Integer.valueOf(28), "255.255.255.240");
/* 48 */     MASK_BIT_MAP.put(Integer.valueOf(29), "255.255.255.248");
/* 49 */     MASK_BIT_MAP.put(Integer.valueOf(30), "255.255.255.252");
/* 50 */     MASK_BIT_MAP.put(Integer.valueOf(31), "255.255.255.254");
/* 51 */     MASK_BIT_MAP.put(Integer.valueOf(32), "255.255.255.255");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String get(int maskBit) {
/* 61 */     return (String)MASK_BIT_MAP.get(Integer.valueOf(maskBit));
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
/*    */   public static Integer getMaskBit(String mask) {
/* 73 */     return (Integer)MASK_BIT_MAP.getKey(mask);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\MaskBit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */