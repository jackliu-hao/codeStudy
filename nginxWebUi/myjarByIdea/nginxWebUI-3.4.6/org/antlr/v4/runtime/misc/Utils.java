package org.antlr.v4.runtime.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Utils {
   public static <T> String join(Iterator<T> iter, String separator) {
      StringBuilder buf = new StringBuilder();

      while(iter.hasNext()) {
         buf.append(iter.next());
         if (iter.hasNext()) {
            buf.append(separator);
         }
      }

      return buf.toString();
   }

   public static <T> String join(T[] array, String separator) {
      StringBuilder builder = new StringBuilder();

      for(int i = 0; i < array.length; ++i) {
         builder.append(array[i]);
         if (i < array.length - 1) {
            builder.append(separator);
         }
      }

      return builder.toString();
   }

   public static int numNonnull(Object[] data) {
      int n = 0;
      if (data == null) {
         return n;
      } else {
         Object[] arr$ = data;
         int len$ = data.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Object o = arr$[i$];
            if (o != null) {
               ++n;
            }
         }

         return n;
      }
   }

   public static <T> void removeAllElements(Collection<T> data, T value) {
      if (data != null) {
         while(data.contains(value)) {
            data.remove(value);
         }

      }
   }

   public static String escapeWhitespace(String s, boolean escapeSpaces) {
      StringBuilder buf = new StringBuilder();
      char[] arr$ = s.toCharArray();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         char c = arr$[i$];
         if (c == ' ' && escapeSpaces) {
            buf.append('·');
         } else if (c == '\t') {
            buf.append("\\t");
         } else if (c == '\n') {
            buf.append("\\n");
         } else if (c == '\r') {
            buf.append("\\r");
         } else {
            buf.append(c);
         }
      }

      return buf.toString();
   }

   public static void writeFile(String fileName, String content) throws IOException {
      writeFile(fileName, content, (String)null);
   }

   public static void writeFile(String fileName, String content, String encoding) throws IOException {
      File f = new File(fileName);
      FileOutputStream fos = new FileOutputStream(f);
      OutputStreamWriter osw;
      if (encoding != null) {
         osw = new OutputStreamWriter(fos, encoding);
      } else {
         osw = new OutputStreamWriter(fos);
      }

      try {
         osw.write(content);
      } finally {
         osw.close();
      }

   }

   public static char[] readFile(String fileName) throws IOException {
      return readFile(fileName, (String)null);
   }

   public static char[] readFile(String fileName, String encoding) throws IOException {
      File f = new File(fileName);
      int size = (int)f.length();
      FileInputStream fis = new FileInputStream(fileName);
      InputStreamReader isr;
      if (encoding != null) {
         isr = new InputStreamReader(fis, encoding);
      } else {
         isr = new InputStreamReader(fis);
      }

      char[] data = null;

      char[] data;
      try {
         data = new char[size];
         int n = isr.read(data);
         if (n < data.length) {
            data = Arrays.copyOf(data, n);
         }
      } finally {
         isr.close();
      }

      return data;
   }

   public static Map<String, Integer> toMap(String[] keys) {
      Map<String, Integer> m = new HashMap();

      for(int i = 0; i < keys.length; ++i) {
         m.put(keys[i], i);
      }

      return m;
   }

   public static char[] toCharArray(IntegerList data) {
      if (data == null) {
         return null;
      } else {
         char[] cdata = new char[data.size()];

         for(int i = 0; i < data.size(); ++i) {
            cdata[i] = (char)data.get(i);
         }

         return cdata;
      }
   }

   public static IntervalSet toSet(BitSet bits) {
      IntervalSet s = new IntervalSet(new int[0]);

      for(int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
         s.add(i);
      }

      return s;
   }
}
