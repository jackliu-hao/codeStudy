/*    */ package cn.hutool.poi;
/*    */ 
/*    */ import cn.hutool.core.exceptions.DependencyException;
/*    */ import cn.hutool.core.util.ClassLoaderUtil;
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
/*    */ public class PoiChecker
/*    */ {
/*    */   public static final String NO_POI_ERROR_MSG = "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2";
/*    */   
/*    */   public static void checkPoiImport() {
/*    */     try {
/* 22 */       Class.forName("org.apache.poi.ss.usermodel.Workbook", false, ClassLoaderUtil.getClassLoader());
/* 23 */     } catch (ClassNotFoundException|NoClassDefFoundError e) {
/* 24 */       throw new DependencyException(e, "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\PoiChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */