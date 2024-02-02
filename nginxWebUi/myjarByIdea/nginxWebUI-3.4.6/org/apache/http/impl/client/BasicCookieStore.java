package org.apache.http.impl.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BasicCookieStore implements CookieStore, Serializable {
   private static final long serialVersionUID = -7581093305228232025L;
   private final TreeSet<Cookie> cookies = new TreeSet(new CookieIdentityComparator());
   private transient ReadWriteLock lock = new ReentrantReadWriteLock();

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.lock = new ReentrantReadWriteLock();
   }

   public void addCookie(Cookie cookie) {
      if (cookie != null) {
         this.lock.writeLock().lock();

         try {
            this.cookies.remove(cookie);
            if (!cookie.isExpired(new Date())) {
               this.cookies.add(cookie);
            }
         } finally {
            this.lock.writeLock().unlock();
         }
      }

   }

   public void addCookies(Cookie[] cookies) {
      if (cookies != null) {
         Cookie[] arr$ = cookies;
         int len$ = cookies.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Cookie cookie = arr$[i$];
            this.addCookie(cookie);
         }
      }

   }

   public List<Cookie> getCookies() {
      this.lock.readLock().lock();

      ArrayList var1;
      try {
         var1 = new ArrayList(this.cookies);
      } finally {
         this.lock.readLock().unlock();
      }

      return var1;
   }

   public boolean clearExpired(Date date) {
      if (date == null) {
         return false;
      } else {
         this.lock.writeLock().lock();

         boolean var7;
         try {
            boolean removed = false;
            Iterator<Cookie> it = this.cookies.iterator();

            while(it.hasNext()) {
               if (((Cookie)it.next()).isExpired(date)) {
                  it.remove();
                  removed = true;
               }
            }

            var7 = removed;
         } finally {
            this.lock.writeLock().unlock();
         }

         return var7;
      }
   }

   public void clear() {
      this.lock.writeLock().lock();

      try {
         this.cookies.clear();
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   public String toString() {
      this.lock.readLock().lock();

      String var1;
      try {
         var1 = this.cookies.toString();
      } finally {
         this.lock.readLock().unlock();
      }

      return var1;
   }
}
