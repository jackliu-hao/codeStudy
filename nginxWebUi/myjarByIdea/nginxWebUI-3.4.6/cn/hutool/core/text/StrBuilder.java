package cn.hutool.core.text;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import java.io.Serializable;
import java.util.Arrays;

public class StrBuilder implements CharSequence, Appendable, Serializable {
   private static final long serialVersionUID = 6341229705927508451L;
   public static final int DEFAULT_CAPACITY = 16;
   private char[] value;
   private int position;

   public static StrBuilder create() {
      return new StrBuilder();
   }

   public static StrBuilder create(int initialCapacity) {
      return new StrBuilder(initialCapacity);
   }

   public static StrBuilder create(CharSequence... strs) {
      return new StrBuilder(strs);
   }

   public StrBuilder() {
      this(16);
   }

   public StrBuilder(int initialCapacity) {
      this.value = new char[initialCapacity];
   }

   public StrBuilder(CharSequence... strs) {
      this(ArrayUtil.isEmpty((Object[])strs) ? 16 : totalLength(strs) + 16);
      CharSequence[] var2 = strs;
      int var3 = strs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         CharSequence str = var2[var4];
         this.append(str);
      }

   }

   public StrBuilder append(Object obj) {
      return this.insert(this.position, obj);
   }

   public StrBuilder append(char c) {
      return this.insert(this.position, c);
   }

   public StrBuilder append(char[] src) {
      return ArrayUtil.isEmpty((char[])src) ? this : this.append((char[])src, 0, src.length);
   }

   public StrBuilder append(char[] src, int srcPos, int length) {
      return this.insert(this.position, src, srcPos, length);
   }

   public StrBuilder append(CharSequence csq) {
      return this.insert(this.position, csq);
   }

   public StrBuilder append(CharSequence csq, int start, int end) {
      return this.insert(this.position, csq, start, end);
   }

   public StrBuilder insert(int index, Object obj) {
      return obj instanceof CharSequence ? this.insert(index, (CharSequence)obj) : this.insert(index, (CharSequence)Convert.toStr(obj));
   }

   public StrBuilder insert(int index, char c) {
      if (index < 0) {
         index += this.position;
      }

      if (index < 0) {
         throw new StringIndexOutOfBoundsException(index);
      } else {
         this.moveDataAfterIndex(index, 1);
         this.value[index] = c;
         this.position = Math.max(this.position, index) + 1;
         return this;
      }
   }

   public StrBuilder insert(int index, char[] src) {
      return ArrayUtil.isEmpty((char[])src) ? this : this.insert(index, (char[])src, 0, src.length);
   }

   public StrBuilder insert(int index, char[] src, int srcPos, int length) {
      if (!ArrayUtil.isEmpty((char[])src) && srcPos <= src.length && length > 0) {
         if (index < 0) {
            index += this.position;
         }

         if (index < 0) {
            throw new StringIndexOutOfBoundsException(index);
         } else {
            if (srcPos < 0) {
               srcPos = 0;
            } else if (srcPos + length > src.length) {
               length = src.length - srcPos;
            }

            this.moveDataAfterIndex(index, length);
            System.arraycopy(src, srcPos, this.value, index, length);
            this.position = Math.max(this.position, index) + length;
            return this;
         }
      } else {
         return this;
      }
   }

   public StrBuilder insert(int index, CharSequence csq) {
      if (index < 0) {
         index += this.position;
      }

      if (index < 0) {
         throw new StringIndexOutOfBoundsException(index);
      } else {
         if (null == csq) {
            csq = "";
         }

         int len = ((CharSequence)csq).length();
         this.moveDataAfterIndex(index, ((CharSequence)csq).length());
         if (csq instanceof String) {
            ((String)csq).getChars(0, len, this.value, index);
         } else if (csq instanceof StringBuilder) {
            ((StringBuilder)csq).getChars(0, len, this.value, index);
         } else if (csq instanceof StringBuffer) {
            ((StringBuffer)csq).getChars(0, len, this.value, index);
         } else if (csq instanceof StrBuilder) {
            ((StrBuilder)csq).getChars(0, len, this.value, index);
         } else {
            int i = 0;

            for(int j = this.position; i < len; ++j) {
               this.value[j] = ((CharSequence)csq).charAt(i);
               ++i;
            }
         }

         this.position = Math.max(this.position, index) + len;
         return this;
      }
   }

   public StrBuilder insert(int index, CharSequence csq, int start, int end) {
      if (csq == null) {
         csq = "null";
      }

      int csqLen = ((CharSequence)csq).length();
      if (start > csqLen) {
         return this;
      } else {
         if (start < 0) {
            start = 0;
         }

         if (end > csqLen) {
            end = csqLen;
         }

         if (start >= end) {
            return this;
         } else {
            if (index < 0) {
               index += this.position;
            }

            if (index < 0) {
               throw new StringIndexOutOfBoundsException(index);
            } else {
               int length = end - start;
               this.moveDataAfterIndex(index, length);
               int i = start;

               for(int j = this.position; i < end; ++j) {
                  this.value[j] = ((CharSequence)csq).charAt(i);
                  ++i;
               }

               this.position = Math.max(this.position, index) + length;
               return this;
            }
         }
      }
   }

   public StrBuilder getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      if (srcBegin < 0) {
         srcBegin = 0;
      }

      if (srcEnd < 0) {
         srcEnd = 0;
      } else if (srcEnd > this.position) {
         srcEnd = this.position;
      }

      if (srcBegin > srcEnd) {
         throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
      } else {
         System.arraycopy(this.value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
         return this;
      }
   }

   public boolean hasContent() {
      return this.position > 0;
   }

   public boolean isEmpty() {
      return this.position == 0;
   }

   public StrBuilder clear() {
      return this.reset();
   }

   public StrBuilder reset() {
      this.position = 0;
      return this;
   }

   public StrBuilder delTo(int newPosition) {
      if (newPosition < 0) {
         newPosition = 0;
      }

      return this.del(newPosition, this.position);
   }

   public StrBuilder del(int start, int end) throws StringIndexOutOfBoundsException {
      if (start < 0) {
         start = 0;
      }

      if (end >= this.position) {
         this.position = start;
         return this;
      } else {
         if (end < 0) {
            end = 0;
         }

         int len = end - start;
         if (len > 0) {
            System.arraycopy(this.value, start + len, this.value, start, this.position - end);
            this.position -= len;
         } else if (len < 0) {
            throw new StringIndexOutOfBoundsException("Start is greater than End.");
         }

         return this;
      }
   }

   public String toString(boolean isReset) {
      if (this.position > 0) {
         String s = new String(this.value, 0, this.position);
         if (isReset) {
            this.reset();
         }

         return s;
      } else {
         return "";
      }
   }

   public String toStringAndReset() {
      return this.toString(true);
   }

   public String toString() {
      return this.toString(false);
   }

   public int length() {
      return this.position;
   }

   public char charAt(int index) {
      if (index < 0) {
         index += this.position;
      }

      if (index >= 0 && index <= this.position) {
         return this.value[index];
      } else {
         throw new StringIndexOutOfBoundsException(index);
      }
   }

   public CharSequence subSequence(int start, int end) {
      return this.subString(start, end);
   }

   public String subString(int start) {
      return this.subString(start, this.position);
   }

   public String subString(int start, int end) {
      return new String(this.value, start, end - start);
   }

   private void moveDataAfterIndex(int index, int length) {
      this.ensureCapacity(Math.max(this.position, index) + length);
      if (index < this.position) {
         System.arraycopy(this.value, index, this.value, index + length, this.position - index);
      } else if (index > this.position) {
         Arrays.fill(this.value, this.position, index, ' ');
      }

   }

   private void ensureCapacity(int minimumCapacity) {
      if (minimumCapacity - this.value.length > 0) {
         this.expandCapacity(minimumCapacity);
      }

   }

   private void expandCapacity(int minimumCapacity) {
      int newCapacity = (this.value.length << 1) + 2;
      if (newCapacity - minimumCapacity < 0) {
         newCapacity = minimumCapacity;
      }

      if (newCapacity < 0) {
         throw new OutOfMemoryError("Capacity is too long and max than Integer.MAX");
      } else {
         this.value = Arrays.copyOf(this.value, newCapacity);
      }
   }

   private static int totalLength(CharSequence... strs) {
      int totalLength = 0;
      CharSequence[] var2 = strs;
      int var3 = strs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         CharSequence str = var2[var4];
         totalLength += null == str ? 0 : str.length();
      }

      return totalLength;
   }
}
