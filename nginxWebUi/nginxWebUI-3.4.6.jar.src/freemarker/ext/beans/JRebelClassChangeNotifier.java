/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import java.lang.ref.WeakReference;
/*    */ import org.zeroturnaround.javarebel.ClassEventListener;
/*    */ import org.zeroturnaround.javarebel.ReloaderFactory;
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
/*    */ class JRebelClassChangeNotifier
/*    */   implements ClassChangeNotifier
/*    */ {
/*    */   static void testAvailability() {
/* 30 */     ReloaderFactory.getInstance();
/*    */   }
/*    */ 
/*    */   
/*    */   public void subscribe(ClassIntrospector classIntrospector) {
/* 35 */     ReloaderFactory.getInstance().addClassReloadListener(new ClassIntrospectorCacheInvalidator(classIntrospector));
/*    */   }
/*    */   
/*    */   private static class ClassIntrospectorCacheInvalidator
/*    */     implements ClassEventListener
/*    */   {
/*    */     private final WeakReference ref;
/*    */     
/*    */     ClassIntrospectorCacheInvalidator(ClassIntrospector w) {
/* 44 */       this.ref = new WeakReference<>(w);
/*    */     }
/*    */ 
/*    */     
/*    */     public void onClassEvent(int eventType, Class<?> pClass) {
/* 49 */       ClassIntrospector ci = this.ref.get();
/* 50 */       if (ci == null) {
/* 51 */         ReloaderFactory.getInstance().removeClassReloadListener(this);
/* 52 */       } else if (eventType == 1) {
/* 53 */         ci.remove(pClass);
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\JRebelClassChangeNotifier.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */