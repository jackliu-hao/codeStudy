package freemarker.ext.beans;

import java.lang.ref.WeakReference;
import org.zeroturnaround.javarebel.ClassEventListener;
import org.zeroturnaround.javarebel.ReloaderFactory;

class JRebelClassChangeNotifier implements ClassChangeNotifier {
   static void testAvailability() {
      ReloaderFactory.getInstance();
   }

   public void subscribe(ClassIntrospector classIntrospector) {
      ReloaderFactory.getInstance().addClassReloadListener(new ClassIntrospectorCacheInvalidator(classIntrospector));
   }

   private static class ClassIntrospectorCacheInvalidator implements ClassEventListener {
      private final WeakReference ref;

      ClassIntrospectorCacheInvalidator(ClassIntrospector w) {
         this.ref = new WeakReference(w);
      }

      public void onClassEvent(int eventType, Class pClass) {
         ClassIntrospector ci = (ClassIntrospector)this.ref.get();
         if (ci == null) {
            ReloaderFactory.getInstance().removeClassReloadListener(this);
         } else if (eventType == 1) {
            ci.remove(pClass);
         }

      }
   }
}
