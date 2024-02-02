/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.io.Serializable;
/*    */ import java.util.Comparator;
/*    */ import java.util.List;
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
/*    */ public class VersionComparator
/*    */   implements Comparator<String>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 8083701245147495562L;
/* 25 */   public static final VersionComparator INSTANCE = new VersionComparator();
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
/*    */   public int compare(String version1, String version2) {
/* 53 */     if (ObjectUtil.equal(version1, version2)) {
/* 54 */       return 0;
/*    */     }
/* 56 */     if (version1 == null && version2 == null)
/* 57 */       return 0; 
/* 58 */     if (version1 == null)
/* 59 */       return -1; 
/* 60 */     if (version2 == null) {
/* 61 */       return 1;
/*    */     }
/*    */     
/* 64 */     List<String> v1s = StrUtil.split(version1, '.');
/* 65 */     List<String> v2s = StrUtil.split(version2, '.');
/*    */     
/* 67 */     int diff = 0;
/* 68 */     int minLength = Math.min(v1s.size(), v2s.size());
/*    */ 
/*    */     
/* 71 */     for (int i = 0; i < minLength; i++) {
/* 72 */       String v1 = v1s.get(i);
/* 73 */       String v2 = v2s.get(i);
/*    */       
/* 75 */       diff = v1.length() - v2.length();
/* 76 */       if (0 == diff) {
/* 77 */         diff = v1.compareTo(v2);
/*    */       }
/* 79 */       if (diff != 0) {
/*    */         break;
/*    */       }
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 86 */     return (diff != 0) ? diff : (v1s.size() - v2s.size());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\VersionComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */