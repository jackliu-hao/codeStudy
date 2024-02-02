package ch.qos.logback.core.spi;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.util.COWArrayList;
import java.util.Iterator;

public class AppenderAttachableImpl<E> implements AppenderAttachable<E> {
   private final COWArrayList<Appender<E>> appenderList = new COWArrayList(new Appender[0]);
   static final long START = System.currentTimeMillis();

   public void addAppender(Appender<E> newAppender) {
      if (newAppender == null) {
         throw new IllegalArgumentException("Null argument disallowed");
      } else {
         this.appenderList.addIfAbsent(newAppender);
      }
   }

   public int appendLoopOnAppenders(E e) {
      int size = 0;
      Appender<E>[] appenderArray = (Appender[])this.appenderList.asTypedArray();
      int len = appenderArray.length;

      for(int i = 0; i < len; ++i) {
         appenderArray[i].doAppend(e);
         ++size;
      }

      return size;
   }

   public Iterator<Appender<E>> iteratorForAppenders() {
      return this.appenderList.iterator();
   }

   public Appender<E> getAppender(String name) {
      if (name == null) {
         return null;
      } else {
         Iterator var2 = this.appenderList.iterator();

         Appender appender;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            appender = (Appender)var2.next();
         } while(!name.equals(appender.getName()));

         return appender;
      }
   }

   public boolean isAttached(Appender<E> appender) {
      if (appender == null) {
         return false;
      } else {
         Iterator var2 = this.appenderList.iterator();

         Appender a;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            a = (Appender)var2.next();
         } while(a != appender);

         return true;
      }
   }

   public void detachAndStopAllAppenders() {
      Iterator var1 = this.appenderList.iterator();

      while(var1.hasNext()) {
         Appender<E> a = (Appender)var1.next();
         a.stop();
      }

      this.appenderList.clear();
   }

   public boolean detachAppender(Appender<E> appender) {
      if (appender == null) {
         return false;
      } else {
         boolean result = this.appenderList.remove(appender);
         return result;
      }
   }

   public boolean detachAppender(String name) {
      if (name == null) {
         return false;
      } else {
         boolean removed = false;
         Iterator var3 = this.appenderList.iterator();

         while(var3.hasNext()) {
            Appender<E> a = (Appender)var3.next();
            if (name.equals(a.getName())) {
               removed = this.appenderList.remove(a);
               break;
            }
         }

         return removed;
      }
   }
}
