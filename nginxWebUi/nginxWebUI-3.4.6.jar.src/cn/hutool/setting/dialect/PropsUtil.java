/*    */ package cn.hutool.setting.dialect;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import cn.hutool.core.io.resource.NoResourceException;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
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
/*    */ public class PropsUtil
/*    */ {
/* 22 */   private static final Map<String, Props> propsMap = new ConcurrentHashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Props get(String name) {
/* 32 */     return propsMap.computeIfAbsent(name, filePath -> {
/*    */           String extName = FileUtil.extName(filePath);
/*    */           if (StrUtil.isEmpty(extName)) {
/*    */             filePath = filePath + "." + "properties";
/*    */           }
/*    */           return new Props(filePath);
/*    */         });
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
/*    */   public static Props getFirstFound(String... names) {
/* 50 */     for (String name : names) {
/*    */       try {
/* 52 */         return get(name);
/* 53 */       } catch (NoResourceException noResourceException) {}
/*    */     } 
/*    */ 
/*    */     
/* 57 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Props getSystemProps() {
/* 67 */     return new Props(System.getProperties());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\setting\dialect\PropsUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */