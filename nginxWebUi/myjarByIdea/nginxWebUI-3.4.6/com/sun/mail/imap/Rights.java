package com.sun.mail.imap;

import java.util.Vector;

public class Rights implements Cloneable {
   private boolean[] rights = new boolean[128];

   public Rights() {
   }

   public Rights(Rights rights) {
      System.arraycopy(rights.rights, 0, this.rights, 0, this.rights.length);
   }

   public Rights(String rights) {
      for(int i = 0; i < rights.length(); ++i) {
         this.add(Rights.Right.getInstance(rights.charAt(i)));
      }

   }

   public Rights(Right right) {
      this.rights[right.right] = true;
   }

   public void add(Right right) {
      this.rights[right.right] = true;
   }

   public void add(Rights rights) {
      for(int i = 0; i < rights.rights.length; ++i) {
         if (rights.rights[i]) {
            this.rights[i] = true;
         }
      }

   }

   public void remove(Right right) {
      this.rights[right.right] = false;
   }

   public void remove(Rights rights) {
      for(int i = 0; i < rights.rights.length; ++i) {
         if (rights.rights[i]) {
            this.rights[i] = false;
         }
      }

   }

   public boolean contains(Right right) {
      return this.rights[right.right];
   }

   public boolean contains(Rights rights) {
      for(int i = 0; i < rights.rights.length; ++i) {
         if (rights.rights[i] && !this.rights[i]) {
            return false;
         }
      }

      return true;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof Rights)) {
         return false;
      } else {
         Rights rights = (Rights)obj;

         for(int i = 0; i < rights.rights.length; ++i) {
            if (rights.rights[i] != this.rights[i]) {
               return false;
            }
         }

         return true;
      }
   }

   public int hashCode() {
      int hash = 0;

      for(int i = 0; i < this.rights.length; ++i) {
         if (this.rights[i]) {
            ++hash;
         }
      }

      return hash;
   }

   public Right[] getRights() {
      Vector v = new Vector();

      for(int i = 0; i < this.rights.length; ++i) {
         if (this.rights[i]) {
            v.addElement(Rights.Right.getInstance((char)i));
         }
      }

      Right[] rights = new Right[v.size()];
      v.copyInto(rights);
      return rights;
   }

   public Object clone() {
      Rights r = null;

      try {
         r = (Rights)super.clone();
         r.rights = new boolean[128];
         System.arraycopy(this.rights, 0, r.rights, 0, this.rights.length);
      } catch (CloneNotSupportedException var3) {
      }

      return r;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();

      for(int i = 0; i < this.rights.length; ++i) {
         if (this.rights[i]) {
            sb.append((char)i);
         }
      }

      return sb.toString();
   }

   public static final class Right {
      private static Right[] cache = new Right[128];
      public static final Right LOOKUP = getInstance('l');
      public static final Right READ = getInstance('r');
      public static final Right KEEP_SEEN = getInstance('s');
      public static final Right WRITE = getInstance('w');
      public static final Right INSERT = getInstance('i');
      public static final Right POST = getInstance('p');
      public static final Right CREATE = getInstance('c');
      public static final Right DELETE = getInstance('d');
      public static final Right ADMINISTER = getInstance('a');
      char right;

      private Right(char right) {
         if (right >= 128) {
            throw new IllegalArgumentException("Right must be ASCII");
         } else {
            this.right = right;
         }
      }

      public static synchronized Right getInstance(char right) {
         if (right >= 128) {
            throw new IllegalArgumentException("Right must be ASCII");
         } else {
            if (cache[right] == null) {
               cache[right] = new Right(right);
            }

            return cache[right];
         }
      }

      public String toString() {
         return String.valueOf(this.right);
      }
   }
}
