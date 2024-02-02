package io.undertow.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class QValueParser {
   private QValueParser() {
   }

   public static List<List<QValueResult>> parse(List<String> headers) {
      List<QValueResult> found = new ArrayList();
      QValueResult current = null;
      Iterator var3 = headers.iterator();

      while(true) {
         while(true) {
            String header;
            int l;
            int stringStart;
            do {
               if (!var3.hasNext()) {
                  Collections.sort(found, Collections.reverseOrder());
                  String currentQValue = null;
                  List<List<QValueResult>> values = new ArrayList();
                  List<QValueResult> currentSet = null;

                  QValueResult val;
                  for(Iterator var12 = found.iterator(); var12.hasNext(); currentSet.add(val)) {
                     val = (QValueResult)var12.next();
                     if (!val.qvalue.equals(currentQValue)) {
                        currentQValue = val.qvalue;
                        currentSet = new ArrayList();
                        values.add(currentSet);
                     }
                  }

                  return values;
               }

               header = (String)var3.next();
               l = header.length();
               stringStart = 0;

               for(int i = 0; i < l; ++i) {
                  char c = header.charAt(i);
                  switch (c) {
                     case ' ':
                        if (stringStart != i) {
                           if (current != null && i - stringStart > 2 && header.charAt(stringStart) == 'q' && header.charAt(stringStart + 1) == '=') {
                              current.qvalue = header.substring(stringStart + 2, i);
                           } else {
                              current = handleNewEncoding(found, header, stringStart, i);
                           }
                        }

                        stringStart = i + 1;
                        break;
                     case ',':
                        if (current != null && i - stringStart > 2 && header.charAt(stringStart) == 'q' && header.charAt(stringStart + 1) == '=') {
                           current.qvalue = header.substring(stringStart + 2, i);
                           current = null;
                        } else if (stringStart != i) {
                           current = handleNewEncoding(found, header, stringStart, i);
                        }

                        stringStart = i + 1;
                        break;
                     case ';':
                        if (stringStart != i) {
                           current = handleNewEncoding(found, header, stringStart, i);
                           stringStart = i + 1;
                        }
                  }
               }
            } while(stringStart == l);

            if (current != null && l - stringStart > 2 && header.charAt(stringStart) == 'q' && header.charAt(stringStart + 1) == '=') {
               current.qvalue = header.substring(stringStart + 2, l);
            } else {
               current = handleNewEncoding(found, header, stringStart, l);
            }
         }
      }
   }

   private static QValueResult handleNewEncoding(List<QValueResult> found, String header, int stringStart, int i) {
      QValueResult current = new QValueResult();
      current.value = header.substring(stringStart, i);
      found.add(current);
      return current;
   }

   public static class QValueResult implements Comparable<QValueResult> {
      private String value;
      private String qvalue = "1";

      public String getValue() {
         return this.value;
      }

      public String getQvalue() {
         return this.qvalue;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof QValueResult)) {
            return false;
         } else {
            QValueResult that = (QValueResult)o;
            if (this.getValue() != null) {
               if (this.getValue().equals(that.getValue())) {
                  return this.getQvalue() != null ? this.getQvalue().equals(that.getQvalue()) : that.getQvalue() == null;
               }
            } else if (that.getValue() == null) {
               return this.getQvalue() != null ? this.getQvalue().equals(that.getQvalue()) : that.getQvalue() == null;
            }

            return false;
         }
      }

      public int hashCode() {
         int result = this.getValue() != null ? this.getValue().hashCode() : 0;
         result = 31 * result + (this.getQvalue() != null ? this.getQvalue().hashCode() : 0);
         return result;
      }

      public int compareTo(QValueResult other) {
         String t = this.qvalue;
         String o = other.qvalue;
         if (t == null && o == null) {
            return 0;
         } else if (o == null) {
            return 1;
         } else if (t == null) {
            return -1;
         } else {
            int tl = t.length();
            int ol = o.length();

            for(int i = 0; i < 5; ++i) {
               if (tl == i || ol == i) {
                  return ol - tl;
               }

               if (i != 1) {
                  int tc = t.charAt(i);
                  int oc = o.charAt(i);
                  int res = tc - oc;
                  if (res != 0) {
                     return res;
                  }
               }
            }

            return 0;
         }
      }

      public boolean isQValueZero() {
         if (this.qvalue == null) {
            return false;
         } else {
            int length = Math.min(5, this.qvalue.length());
            boolean zero = true;

            for(int j = 0; j < length; ++j) {
               if (j != 1 && this.qvalue.charAt(j) != '0') {
                  zero = false;
                  break;
               }
            }

            return zero;
         }
      }
   }
}
