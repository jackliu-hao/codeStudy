package io.undertow.util;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class HeaderMap implements Iterable<HeaderValues> {
   private Object[] table = new Object[16];
   private int size;
   private Collection<HttpString> headerNames;

   private HeaderValues getEntry(HttpString headerName) {
      if (headerName == null) {
         return null;
      } else {
         int hc = headerName.hashCode();
         int idx = hc & this.table.length - 1;
         Object o = this.table[idx];
         if (o == null) {
            return null;
         } else {
            HeaderValues headerValues;
            if (o instanceof HeaderValues) {
               headerValues = (HeaderValues)o;
               return !headerName.equals(headerValues.key) ? null : headerValues;
            } else {
               HeaderValues[] row = (HeaderValues[])((HeaderValues[])o);

               for(int i = 0; i < row.length; ++i) {
                  headerValues = row[i];
                  if (headerValues != null && headerName.equals(headerValues.key)) {
                     return headerValues;
                  }
               }

               return null;
            }
         }
      }
   }

   private HeaderValues getEntry(String headerName) {
      if (headerName == null) {
         return null;
      } else {
         int hc = HttpString.hashCodeOf(headerName);
         int idx = hc & this.table.length - 1;
         Object o = this.table[idx];
         if (o == null) {
            return null;
         } else {
            HeaderValues headerValues;
            if (o instanceof HeaderValues) {
               headerValues = (HeaderValues)o;
               return !headerValues.key.equalToString(headerName) ? null : headerValues;
            } else {
               HeaderValues[] row = (HeaderValues[])((HeaderValues[])o);

               for(int i = 0; i < row.length; ++i) {
                  headerValues = row[i];
                  if (headerValues != null && headerValues.key.equalToString(headerName)) {
                     return headerValues;
                  }
               }

               return null;
            }
         }
      }
   }

   private HeaderValues removeEntry(HttpString headerName) {
      if (headerName == null) {
         return null;
      } else {
         int hc = headerName.hashCode();
         Object[] table = this.table;
         int idx = hc & table.length - 1;
         Object o = table[idx];
         if (o == null) {
            return null;
         } else {
            HeaderValues headerValues;
            if (o instanceof HeaderValues) {
               headerValues = (HeaderValues)o;
               if (!headerName.equals(headerValues.key)) {
                  return null;
               } else {
                  table[idx] = null;
                  --this.size;
                  return headerValues;
               }
            } else {
               HeaderValues[] row = (HeaderValues[])((HeaderValues[])o);

               for(int i = 0; i < row.length; ++i) {
                  headerValues = row[i];
                  if (headerValues != null && headerName.equals(headerValues.key)) {
                     row[i] = null;
                     --this.size;
                     return headerValues;
                  }
               }

               return null;
            }
         }
      }
   }

   private HeaderValues removeEntry(String headerName) {
      if (headerName == null) {
         return null;
      } else {
         int hc = HttpString.hashCodeOf(headerName);
         Object[] table = this.table;
         int idx = hc & table.length - 1;
         Object o = table[idx];
         if (o == null) {
            return null;
         } else {
            HeaderValues headerValues;
            if (o instanceof HeaderValues) {
               headerValues = (HeaderValues)o;
               if (!headerValues.key.equalToString(headerName)) {
                  return null;
               } else {
                  table[idx] = null;
                  --this.size;
                  return headerValues;
               }
            } else {
               HeaderValues[] row = (HeaderValues[])((HeaderValues[])o);

               for(int i = 0; i < row.length; ++i) {
                  headerValues = row[i];
                  if (headerValues != null && headerValues.key.equalToString(headerName)) {
                     row[i] = null;
                     --this.size;
                     return headerValues;
                  }
               }

               return null;
            }
         }
      }
   }

   private void resize() {
      int oldLen = this.table.length;
      if (oldLen != 1073741824) {
         assert Integer.bitCount(oldLen) == 1;

         Object[] newTable = Arrays.copyOf(this.table, oldLen << 1);
         this.table = newTable;

         for(int i = 0; i < oldLen; ++i) {
            if (newTable[i] != null) {
               if (newTable[i] instanceof HeaderValues) {
                  HeaderValues e = (HeaderValues)newTable[i];
                  if ((e.key.hashCode() & oldLen) != 0) {
                     newTable[i] = null;
                     newTable[i + oldLen] = e;
                  }
               } else {
                  HeaderValues[] oldRow = (HeaderValues[])((HeaderValues[])newTable[i]);
                  HeaderValues[] newRow = (HeaderValues[])oldRow.clone();
                  int rowLen = oldRow.length;
                  newTable[i + oldLen] = newRow;

                  for(int j = 0; j < rowLen; ++j) {
                     HeaderValues item = oldRow[j];
                     if (item != null) {
                        if ((item.key.hashCode() & oldLen) != 0) {
                           oldRow[j] = null;
                        } else {
                           newRow[j] = null;
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private HeaderValues getOrCreateEntry(HttpString headerName) {
      if (headerName == null) {
         return null;
      } else {
         int hc = headerName.hashCode();
         Object[] table = this.table;
         int length = table.length;
         int idx = hc & length - 1;
         Object o = table[idx];
         if (o == null) {
            if (this.size >= length >> 1) {
               this.resize();
               return this.getOrCreateEntry(headerName);
            } else {
               HeaderValues headerValues = new HeaderValues(headerName);
               table[idx] = headerValues;
               ++this.size;
               return headerValues;
            }
         } else {
            return this.getOrCreateNonEmpty(headerName, table, length, idx, o);
         }
      }
   }

   private HeaderValues getOrCreateNonEmpty(HttpString headerName, Object[] table, int length, int idx, Object o) {
      HeaderValues headerValues;
      HeaderValues[] row;
      if (o instanceof HeaderValues) {
         headerValues = (HeaderValues)o;
         if (!headerName.equals(headerValues.key)) {
            if (this.size >= length >> 1) {
               this.resize();
               return this.getOrCreateEntry(headerName);
            } else {
               ++this.size;
               row = new HeaderValues[]{headerValues, new HeaderValues(headerName), null, null};
               table[idx] = row;
               return row[1];
            }
         } else {
            return headerValues;
         }
      } else {
         row = (HeaderValues[])((HeaderValues[])o);
         int empty = -1;

         for(int i = 0; i < row.length; ++i) {
            headerValues = row[i];
            if (headerValues != null) {
               if (headerName.equals(headerValues.key)) {
                  return headerValues;
               }
            } else if (empty == -1) {
               empty = i;
            }
         }

         if (this.size >= length >> 1) {
            this.resize();
            return this.getOrCreateEntry(headerName);
         } else {
            ++this.size;
            headerValues = new HeaderValues(headerName);
            if (empty != -1) {
               row[empty] = headerValues;
            } else {
               if (row.length >= 16) {
                  throw new SecurityException("Excessive collisions");
               }

               HeaderValues[] newRow = (HeaderValues[])Arrays.copyOf(row, row.length + 3);
               newRow[row.length] = headerValues;
               table[idx] = newRow;
            }

            return headerValues;
         }
      }
   }

   public HeaderValues get(HttpString headerName) {
      return this.getEntry(headerName);
   }

   public HeaderValues get(String headerName) {
      return this.getEntry(headerName);
   }

   public String getFirst(HttpString headerName) {
      HeaderValues headerValues = this.getEntry(headerName);
      return headerValues == null ? null : headerValues.getFirst();
   }

   public String getFirst(String headerName) {
      HeaderValues headerValues = this.getEntry(headerName);
      return headerValues == null ? null : headerValues.getFirst();
   }

   public String get(HttpString headerName, int index) throws IndexOutOfBoundsException {
      if (headerName == null) {
         return null;
      } else {
         HeaderValues headerValues = this.getEntry(headerName);
         return headerValues == null ? null : headerValues.get(index);
      }
   }

   public String get(String headerName, int index) throws IndexOutOfBoundsException {
      if (headerName == null) {
         return null;
      } else {
         HeaderValues headerValues = this.getEntry(headerName);
         return headerValues == null ? null : headerValues.get(index);
      }
   }

   public String getLast(HttpString headerName) {
      if (headerName == null) {
         return null;
      } else {
         HeaderValues headerValues = this.getEntry(headerName);
         return headerValues == null ? null : headerValues.getLast();
      }
   }

   public String getLast(String headerName) {
      if (headerName == null) {
         return null;
      } else {
         HeaderValues headerValues = this.getEntry(headerName);
         return headerValues == null ? null : headerValues.getLast();
      }
   }

   public int count(HttpString headerName) {
      if (headerName == null) {
         return 0;
      } else {
         HeaderValues headerValues = this.getEntry(headerName);
         return headerValues == null ? 0 : headerValues.size();
      }
   }

   public int count(String headerName) {
      if (headerName == null) {
         return 0;
      } else {
         HeaderValues headerValues = this.getEntry(headerName);
         return headerValues == null ? 0 : headerValues.size();
      }
   }

   public int size() {
      return this.size;
   }

   public long fastIterate() {
      Object[] table = this.table;
      int len = table.length;

      for(int ri = 0; ri < len; ++ri) {
         Object item = table[ri];
         if (item != null) {
            if (item instanceof HeaderValues) {
               return (long)ri << 32;
            }

            HeaderValues[] row = (HeaderValues[])((HeaderValues[])item);
            int ci = 0;

            for(int rowLen = row.length; ci < rowLen; ++ci) {
               if (row[ci] != null) {
                  return (long)ri << 32 | (long)ci & 4294967295L;
               }
            }
         }
      }

      return -1L;
   }

   public long fastIterateNonEmpty() {
      Object[] table = this.table;
      int len = table.length;

      for(int ri = 0; ri < len; ++ri) {
         Object item = table[ri];
         if (item != null) {
            if (item instanceof HeaderValues) {
               if (!((HeaderValues)item).isEmpty()) {
                  return (long)ri << 32;
               }
            } else {
               HeaderValues[] row = (HeaderValues[])((HeaderValues[])item);
               int ci = 0;

               for(int rowLen = row.length; ci < rowLen; ++ci) {
                  if (row[ci] != null && !row[ci].isEmpty()) {
                     return (long)ri << 32 | (long)ci & 4294967295L;
                  }
               }
            }
         }
      }

      return -1L;
   }

   public long fiNext(long cookie) {
      if (cookie == -1L) {
         return -1L;
      } else {
         Object[] table = this.table;
         int len = table.length;
         int ri = (int)(cookie >> 32);
         int ci = (int)cookie;
         Object item = table[ri];
         HeaderValues[] row;
         int rowLen;
         if (item instanceof HeaderValues[]) {
            row = (HeaderValues[])((HeaderValues[])item);
            rowLen = row.length;
            ++ci;
            if (ci >= rowLen) {
               ++ri;
               ci = 0;
            } else if (row[ci] != null) {
               return (long)ri << 32 | (long)ci & 4294967295L;
            }
         } else {
            ++ri;
            ci = 0;
         }

         while(ri < len) {
            item = table[ri];
            if (item instanceof HeaderValues) {
               return (long)ri << 32;
            }

            if (item instanceof HeaderValues[]) {
               row = (HeaderValues[])((HeaderValues[])item);

               for(rowLen = row.length; ci < rowLen; ++ci) {
                  if (row[ci] != null) {
                     return (long)ri << 32 | (long)ci & 4294967295L;
                  }
               }
            }

            ci = 0;
            ++ri;
         }

         return -1L;
      }
   }

   public long fiNextNonEmpty(long cookie) {
      if (cookie == -1L) {
         return -1L;
      } else {
         Object[] table = this.table;
         int len = table.length;
         int ri = (int)(cookie >> 32);
         int ci = (int)cookie;
         Object item = table[ri];
         HeaderValues[] row;
         int rowLen;
         if (item instanceof HeaderValues[]) {
            row = (HeaderValues[])((HeaderValues[])item);
            rowLen = row.length;
            ++ci;
            if (ci >= rowLen) {
               ++ri;
               ci = 0;
            } else if (row[ci] != null && !row[ci].isEmpty()) {
               return (long)ri << 32 | (long)ci & 4294967295L;
            }
         } else {
            ++ri;
            ci = 0;
         }

         while(ri < len) {
            item = table[ri];
            if (item instanceof HeaderValues && !((HeaderValues)item).isEmpty()) {
               return (long)ri << 32;
            }

            if (item instanceof HeaderValues[]) {
               row = (HeaderValues[])((HeaderValues[])item);

               for(rowLen = row.length; ci < rowLen; ++ci) {
                  if (row[ci] != null && !row[ci].isEmpty()) {
                     return (long)ri << 32 | (long)ci & 4294967295L;
                  }
               }
            }

            ci = 0;
            ++ri;
         }

         return -1L;
      }
   }

   public HeaderValues fiCurrent(long cookie) {
      try {
         Object[] table = this.table;
         int ri = (int)(cookie >> 32);
         int ci = (int)cookie;
         Object item = table[ri];
         if (item instanceof HeaderValues[]) {
            return ((HeaderValues[])((HeaderValues[])item))[ci];
         } else if (ci == 0) {
            return (HeaderValues)item;
         } else {
            throw new NoSuchElementException();
         }
      } catch (RuntimeException var7) {
         throw new NoSuchElementException();
      }
   }

   public Iterable<String> eachValue(HttpString headerName) {
      if (headerName == null) {
         return Collections.emptyList();
      } else {
         HeaderValues entry = this.getEntry(headerName);
         return (Iterable)(entry == null ? Collections.emptyList() : entry);
      }
   }

   public Iterator<HeaderValues> iterator() {
      return new Iterator<HeaderValues>() {
         final Object[] table;
         boolean consumed;
         int ri;
         int ci;

         {
            this.table = HeaderMap.this.table;
         }

         private HeaderValues _next() {
            while(this.ri < this.table.length) {
               Object o = this.table[this.ri];
               if (o == null) {
                  ++this.ri;
                  this.ci = 0;
                  this.consumed = false;
               } else if (o instanceof HeaderValues) {
                  if (this.ci <= 0 && !this.consumed) {
                     return (HeaderValues)o;
                  }

                  ++this.ri;
                  this.ci = 0;
                  this.consumed = false;
               } else {
                  HeaderValues[] row = (HeaderValues[])((HeaderValues[])o);
                  int len = row.length;
                  if (this.ci >= len) {
                     ++this.ri;
                     this.ci = 0;
                     this.consumed = false;
                  } else if (this.consumed) {
                     ++this.ci;
                     this.consumed = false;
                  } else {
                     HeaderValues headerValues = row[this.ci];
                     if (headerValues != null) {
                        return headerValues;
                     }

                     ++this.ci;
                  }
               }
            }

            return null;
         }

         public boolean hasNext() {
            return this._next() != null;
         }

         public HeaderValues next() {
            HeaderValues next = this._next();
            if (next == null) {
               throw new NoSuchElementException();
            } else {
               this.consumed = true;
               return next;
            }
         }

         public void remove() {
         }
      };
   }

   public Collection<HttpString> getHeaderNames() {
      return this.headerNames != null ? this.headerNames : (this.headerNames = new AbstractCollection<HttpString>() {
         public boolean contains(Object o) {
            return o instanceof HttpString && HeaderMap.this.getEntry((HttpString)o) != null;
         }

         public boolean add(HttpString httpString) {
            HeaderMap.this.getOrCreateEntry(httpString);
            return true;
         }

         public boolean remove(Object o) {
            if (!(o instanceof HttpString)) {
               return false;
            } else {
               HttpString s = (HttpString)o;
               HeaderValues entry = HeaderMap.this.getEntry(s);
               if (entry == null) {
                  return false;
               } else {
                  entry.clear();
                  return true;
               }
            }
         }

         public void clear() {
            HeaderMap.this.clear();
         }

         public Iterator<HttpString> iterator() {
            final Iterator<HeaderValues> iterator = HeaderMap.this.iterator();
            return new Iterator<HttpString>() {
               public boolean hasNext() {
                  return iterator.hasNext();
               }

               public HttpString next() {
                  return ((HeaderValues)iterator.next()).getHeaderName();
               }

               public void remove() {
                  iterator.remove();
               }
            };
         }

         public int size() {
            return HeaderMap.this.size();
         }
      });
   }

   public HeaderMap add(HttpString headerName, String headerValue) {
      this.addLast(headerName, headerValue);
      return this;
   }

   public HeaderMap addFirst(HttpString headerName, String headerValue) {
      if (headerName == null) {
         throw new IllegalArgumentException("headerName is null");
      } else if (headerValue == null) {
         return this;
      } else {
         this.getOrCreateEntry(headerName).addFirst(headerValue);
         return this;
      }
   }

   public HeaderMap addLast(HttpString headerName, String headerValue) {
      if (headerName == null) {
         throw new IllegalArgumentException("headerName is null");
      } else if (headerValue == null) {
         return this;
      } else {
         this.getOrCreateEntry(headerName).addLast(headerValue);
         return this;
      }
   }

   public HeaderMap add(HttpString headerName, long headerValue) {
      this.add(headerName, Long.toString(headerValue));
      return this;
   }

   public HeaderMap addAll(HttpString headerName, Collection<String> headerValues) {
      if (headerName == null) {
         throw new IllegalArgumentException("headerName is null");
      } else if (headerValues != null && !headerValues.isEmpty()) {
         this.getOrCreateEntry(headerName).addAll(headerValues);
         return this;
      } else {
         return this;
      }
   }

   public HeaderMap put(HttpString headerName, String headerValue) {
      if (headerName == null) {
         throw new IllegalArgumentException("headerName is null");
      } else if (headerValue == null) {
         this.remove(headerName);
         return this;
      } else {
         HeaderValues headerValues = this.getOrCreateEntry(headerName);
         headerValues.clear();
         headerValues.add(headerValue);
         return this;
      }
   }

   public HeaderMap put(HttpString headerName, long headerValue) {
      if (headerName == null) {
         throw new IllegalArgumentException("headerName is null");
      } else {
         HeaderValues entry = this.getOrCreateEntry(headerName);
         entry.clear();
         entry.add(Long.toString(headerValue));
         return this;
      }
   }

   public HeaderMap putAll(HttpString headerName, Collection<String> headerValues) {
      if (headerName == null) {
         throw new IllegalArgumentException("headerName is null");
      } else if (headerValues != null && !headerValues.isEmpty()) {
         HeaderValues entry = this.getOrCreateEntry(headerName);
         entry.clear();
         entry.addAll(headerValues);
         return this;
      } else {
         this.remove(headerName);
         return this;
      }
   }

   public HeaderMap clear() {
      Arrays.fill(this.table, (Object)null);
      this.size = 0;
      return this;
   }

   public Collection<String> remove(HttpString headerName) {
      if (headerName == null) {
         return Collections.emptyList();
      } else {
         Collection<String> values = this.removeEntry(headerName);
         return (Collection)(values != null ? values : Collections.emptyList());
      }
   }

   public Collection<String> remove(String headerName) {
      if (headerName == null) {
         return Collections.emptyList();
      } else {
         Collection<String> values = this.removeEntry(headerName);
         return (Collection)(values != null ? values : Collections.emptyList());
      }
   }

   public boolean contains(HttpString headerName) {
      HeaderValues headerValues = this.getEntry(headerName);
      if (headerValues == null) {
         return false;
      } else {
         Object v = headerValues.value;
         if (v instanceof String) {
            return true;
         } else {
            String[] list = (String[])((String[])v);

            for(int i = 0; i < list.length; ++i) {
               if (list[i] != null) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean contains(String headerName) {
      HeaderValues headerValues = this.getEntry(headerName);
      if (headerValues == null) {
         return false;
      } else {
         Object v = headerValues.value;
         if (v instanceof String) {
            return true;
         } else {
            String[] list = (String[])((String[])v);

            for(int i = 0; i < list.length; ++i) {
               if (list[i] != null) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean equals(Object o) {
      return o == this;
   }

   public int hashCode() {
      return super.hashCode();
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("{");
      boolean first = true;
      Iterator var3 = this.getHeaderNames().iterator();

      while(var3.hasNext()) {
         HttpString name = (HttpString)var3.next();
         if (first) {
            first = false;
         } else {
            sb.append(", ");
         }

         sb.append(name);
         sb.append("=[");
         boolean f = true;

         String val;
         for(Iterator var6 = this.get(name).iterator(); var6.hasNext(); sb.append(val)) {
            val = (String)var6.next();
            if (f) {
               f = false;
            } else {
               sb.append(", ");
            }
         }

         sb.append("]");
      }

      sb.append("}");
      return sb.toString();
   }
}
