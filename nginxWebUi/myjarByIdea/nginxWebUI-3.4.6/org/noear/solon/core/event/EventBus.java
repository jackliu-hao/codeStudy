package org.noear.solon.core.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.noear.solon.Solon;
import org.noear.solon.Utils;

public final class EventBus {
   private static Map<Object, HH> sThrow = new HashMap();
   private static Map<Object, HH> sOther = new HashMap();

   public static void pushAsyn(Object event) {
      if (event != null) {
         Utils.pools.submit(() -> {
            push0(event);
         });
      }

   }

   public static void push(Object event) {
      if (event != null) {
         push0(event);
      }

   }

   private static void push0(Object event) {
      if (event instanceof Throwable) {
         if (Solon.app() == null || Solon.app().enableErrorAutoprint()) {
            ((Throwable)event).printStackTrace();
         }

         push1(sThrow.values(), event, false);
      } else {
         push1(sOther.values(), event, true);
      }

   }

   private static void push1(Collection<HH> hhs, Object event, boolean thrown) {
      Iterator var3 = hhs.iterator();

      while(true) {
         HH h1;
         do {
            if (!var3.hasNext()) {
               return;
            }

            h1 = (HH)var3.next();
         } while(!h1.t.isInstance(event));

         try {
            h1.l.onEvent(event);
         } catch (Throwable var6) {
            if (thrown) {
               push(var6);
            } else {
               var6.printStackTrace();
            }
         }
      }
   }

   public static synchronized <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
      if (Throwable.class.isAssignableFrom(eventType)) {
         sThrow.putIfAbsent(listener, new HH(eventType, listener));
         if (Solon.app() != null) {
            Solon.app().enableErrorAutoprint(false);
         }
      } else {
         sOther.putIfAbsent(listener, new HH(eventType, listener));
      }

   }

   public static synchronized <T> void unsubscribe(EventListener<T> listener) {
      sThrow.remove(listener);
      sOther.remove(listener);
   }

   static class HH {
      protected Class<?> t;
      protected EventListener l;

      public HH(Class<?> type, EventListener listener) {
         this.t = type;
         this.l = listener;
      }
   }
}
