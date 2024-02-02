/*    */ package cn.hutool.aop.aspects;
/*    */ 
/*    */ import cn.hutool.core.date.TimeInterval;
/*    */ import cn.hutool.core.lang.Console;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TimeIntervalAspect
/*    */   extends SimpleAspect
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 16 */   private final TimeInterval interval = new TimeInterval();
/*    */ 
/*    */   
/*    */   public boolean before(Object target, Method method, Object[] args) {
/* 20 */     this.interval.start();
/* 21 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean after(Object target, Method method, Object[] args, Object returnVal) {
/* 26 */     Console.log("Method [{}.{}] execute spend [{}]ms return value [{}]", new Object[] { target
/* 27 */           .getClass().getName(), method
/* 28 */           .getName(), 
/* 29 */           Long.valueOf(this.interval.intervalMs()), returnVal });
/*    */     
/* 31 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\aop\aspects\TimeIntervalAspect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */