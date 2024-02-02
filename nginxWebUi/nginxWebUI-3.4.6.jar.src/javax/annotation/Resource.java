/*    */ package javax.annotation;
/*    */ 
/*    */ import java.lang.annotation.ElementType;
/*    */ import java.lang.annotation.Repeatable;
/*    */ import java.lang.annotation.Retention;
/*    */ import java.lang.annotation.RetentionPolicy;
/*    */ import java.lang.annotation.Target;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
/*    */ @Retention(RetentionPolicy.RUNTIME)
/*    */ @Repeatable(Resources.class)
/*    */ public @interface Resource
/*    */ {
/*    */   String name() default "";
/*    */   
/*    */   String lookup() default "";
/*    */   
/*    */   Class<?> type() default Object.class;
/*    */   
/*    */   AuthenticationType authenticationType() default AuthenticationType.CONTAINER;
/*    */   
/*    */   boolean shareable() default true;
/*    */   
/*    */   String mappedName() default "";
/*    */   
/*    */   String description() default "";
/*    */   
/*    */   public enum AuthenticationType
/*    */   {
/* 79 */     CONTAINER,
/* 80 */     APPLICATION;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\annotation\Resource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */