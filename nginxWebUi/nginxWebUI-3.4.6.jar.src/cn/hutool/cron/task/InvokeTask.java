/*    */ package cn.hutool.cron.task;
/*    */ 
/*    */ import cn.hutool.core.exceptions.UtilException;
/*    */ import cn.hutool.core.util.ClassLoaderUtil;
/*    */ import cn.hutool.core.util.ClassUtil;
/*    */ import cn.hutool.core.util.ReflectUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.cron.CronException;
/*    */ import java.lang.reflect.Method;
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
/*    */ public class InvokeTask
/*    */   implements Task
/*    */ {
/*    */   private final Object obj;
/*    */   private final Method method;
/*    */   
/*    */   public InvokeTask(String classNameWithMethodName) {
/* 30 */     int splitIndex = classNameWithMethodName.lastIndexOf('#');
/* 31 */     if (splitIndex <= 0) {
/* 32 */       splitIndex = classNameWithMethodName.lastIndexOf('.');
/*    */     }
/* 34 */     if (splitIndex <= 0) {
/* 35 */       throw new UtilException("Invalid classNameWithMethodName [{}]!", new Object[] { classNameWithMethodName });
/*    */     }
/*    */ 
/*    */     
/* 39 */     String className = classNameWithMethodName.substring(0, splitIndex);
/* 40 */     if (StrUtil.isBlank(className)) {
/* 41 */       throw new IllegalArgumentException("Class name is blank !");
/*    */     }
/* 43 */     Class<?> clazz = ClassLoaderUtil.loadClass(className);
/* 44 */     if (null == clazz) {
/* 45 */       throw new IllegalArgumentException("Load class with name of [" + className + "] fail !");
/*    */     }
/* 47 */     this.obj = ReflectUtil.newInstanceIfPossible(clazz);
/*    */ 
/*    */     
/* 50 */     String methodName = classNameWithMethodName.substring(splitIndex + 1);
/* 51 */     if (StrUtil.isBlank(methodName)) {
/* 52 */       throw new IllegalArgumentException("Method name is blank !");
/*    */     }
/* 54 */     this.method = ClassUtil.getPublicMethod(clazz, methodName, new Class[0]);
/* 55 */     if (null == this.method) {
/* 56 */       throw new IllegalArgumentException("No method with name of [" + methodName + "] !");
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute() {
/*    */     try {
/* 63 */       ReflectUtil.invoke(this.obj, this.method, new Object[0]);
/* 64 */     } catch (UtilException e) {
/* 65 */       throw new CronException(e.getCause());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\task\InvokeTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */