/*    */ package cn.hutool;
/*    */ 
/*    */ import cn.hutool.core.lang.ConsoleTable;
/*    */ import cn.hutool.core.util.ClassUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.util.Set;
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
/*    */ public class Hutool
/*    */ {
/*    */   public static final String AUTHOR = "Looly";
/*    */   
/*    */   public static Set<Class<?>> getAllUtils() {
/* 57 */     return ClassUtil.scanPackage("cn.hutool", clazz -> 
/* 58 */         (false == clazz.isInterface() && StrUtil.endWith(clazz.getSimpleName(), "Util")));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void printAllUtils() {
/* 65 */     Set<Class<?>> allUtils = getAllUtils();
/* 66 */     ConsoleTable consoleTable = ConsoleTable.create().addHeader(new String[] { "工具类名", "所在包" });
/* 67 */     for (Class<?> clazz : allUtils) {
/* 68 */       consoleTable.addBody(new String[] { clazz.getSimpleName(), clazz.getPackage().getName() });
/*    */     } 
/* 70 */     consoleTable.print();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\Hutool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */