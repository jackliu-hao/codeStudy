package org.wildfly.common.rpc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import org.wildfly.common.Assert;
import org.wildfly.common._private.CommonMessages;

public final class RemoteExceptionCause extends Throwable {
   private static final long serialVersionUID = 7849011228540958997L;
   private static final ClassValue<Function<Throwable, Map<String, String>>> fieldGetterValue = new ClassValue<Function<Throwable, Map<String, String>>>() {
      protected Function<Throwable, Map<String, String>> computeValue(Class<?> type) {
         Field[] fields = type.getFields();
         int length = fields.length;
         int i = 0;

         int j;
         for(j = 0; i < length; ++i) {
            if ((fields[i].getModifiers() & 9) == 1) {
               fields[j++] = fields[i];
            }
         }

         Field[] finalFields;
         if (j < i) {
            finalFields = (Field[])Arrays.copyOf(fields, j);
         } else {
            finalFields = fields;
         }

         if (j == 0) {
            return (t) -> {
               return Collections.emptyMap();
            };
         } else if (j == 1) {
            Field field = finalFields[0];
            return (t) -> {
               try {
                  return Collections.singletonMap(field.getName(), String.valueOf(field.get(t)));
               } catch (IllegalAccessException var3) {
                  throw new IllegalStateException(var3);
               }
            };
         } else {
            return (t) -> {
               Map<String, String> map = new TreeMap();
               Field[] var3 = finalFields;
               int var4 = finalFields.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Field field = var3[var5];

                  try {
                     map.put(field.getName(), String.valueOf(field.get(t)));
                  } catch (IllegalAccessException var8) {
                     throw new IllegalStateException(var8);
                  }
               }

               return Collections.unmodifiableMap(map);
            };
         }
      }
   };
   private static final StackTraceElement[] EMPTY_STACK = new StackTraceElement[0];
   private final String exceptionClassName;
   private final Map<String, String> fields;
   private transient String toString;
   private static final int ST_NULL = 0;
   private static final int ST_NEW_STRING = 1;
   private static final int ST_NEW_STACK_ELEMENT_V8 = 2;
   private static final int ST_NEW_STACK_ELEMENT_V9 = 3;
   private static final int ST_NEW_EXCEPTION_CAUSE = 4;
   private static final int ST_INT8 = 5;
   private static final int ST_INT16 = 6;
   private static final int ST_INT32 = 7;
   private static final int ST_INT_MINI = 32;
   private static final int ST_BACKREF_FAR = 64;
   private static final int ST_BACKREF_NEAR = 128;
   private static final String[] NO_STRINGS = new String[0];
   private static final RemoteExceptionCause[] NO_REMOTE_EXCEPTION_CAUSES = new RemoteExceptionCause[0];

   RemoteExceptionCause(String msg, RemoteExceptionCause cause, String exceptionClassName, Map<String, String> fields, boolean cloneFields) {
      super(msg);
      if (cause != null) {
         this.initCause(cause);
      }

      Assert.checkNotNullParam("exceptionClassName", exceptionClassName);
      this.exceptionClassName = exceptionClassName;
      if (cloneFields) {
         Iterator<Map.Entry<String, String>> iterator = fields.entrySet().iterator();
         if (!iterator.hasNext()) {
            this.fields = Collections.emptyMap();
         } else {
            Map.Entry<String, String> e1 = (Map.Entry)iterator.next();
            String name1 = (String)e1.getKey();
            String value1 = (String)e1.getValue();
            if (name1 == null || value1 == null) {
               throw CommonMessages.msg.cannotContainNullFieldNameOrValue();
            }

            if (!iterator.hasNext()) {
               this.fields = Collections.singletonMap(name1, value1);
            } else {
               Map<String, String> map = new TreeMap();
               map.put(name1, value1);

               do {
                  Map.Entry<String, String> next = (Map.Entry)iterator.next();
                  map.put((String)next.getKey(), (String)next.getValue());
               } while(iterator.hasNext());

               this.fields = Collections.unmodifiableMap(map);
            }
         }
      } else {
         this.fields = fields;
      }

   }

   public RemoteExceptionCause(String msg, String exceptionClassName) {
      this(msg, (RemoteExceptionCause)null, exceptionClassName, Collections.emptyMap(), false);
   }

   public RemoteExceptionCause(String msg, RemoteExceptionCause cause, String exceptionClassName) {
      this(msg, cause, exceptionClassName, Collections.emptyMap(), false);
   }

   public RemoteExceptionCause(String msg, String exceptionClassName, Map<String, String> fields) {
      this(msg, (RemoteExceptionCause)null, exceptionClassName, fields, true);
   }

   public RemoteExceptionCause(String msg, RemoteExceptionCause cause, String exceptionClassName, Map<String, String> fields) {
      this(msg, cause, exceptionClassName, fields, true);
   }

   public static RemoteExceptionCause of(Throwable t) {
      return of(t, new IdentityHashMap());
   }

   private static RemoteExceptionCause of(Throwable t, IdentityHashMap<Throwable, RemoteExceptionCause> seen) {
      if (t == null) {
         return null;
      } else if (t instanceof RemoteExceptionCause) {
         return (RemoteExceptionCause)t;
      } else {
         RemoteExceptionCause existing = (RemoteExceptionCause)seen.get(t);
         if (existing != null) {
            return existing;
         } else {
            RemoteExceptionCause e = new RemoteExceptionCause(t.getMessage(), t.getClass().getName(), (Map)((Function)fieldGetterValue.get(t.getClass())).apply(t));
            e.setStackTrace(t.getStackTrace());
            seen.put(t, e);
            Throwable cause = t.getCause();
            if (cause != null) {
               e.initCause(of(cause, seen));
            }

            Throwable[] var5 = t.getSuppressed();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Throwable throwable = var5[var7];
               e.addSuppressed(of(throwable, seen));
            }

            return e;
         }
      }
   }

   public Throwable toPlainThrowable() {
      Throwable throwable = new Throwable(this.toString(), this.getCause());
      throwable.setStackTrace(this.getStackTrace());
      Throwable[] var2 = this.getSuppressed();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Throwable s = var2[var4];
         throwable.addSuppressed(s);
      }

      return throwable;
   }

   public String getExceptionClassName() {
      return this.exceptionClassName;
   }

   public Set<String> getFieldNames() {
      return this.fields.keySet();
   }

   public String getFieldValue(String fieldName) {
      Assert.checkNotNullParam("fieldName", fieldName);
      return (String)this.fields.get(fieldName);
   }

   public String toString() {
      String toString = this.toString;
      if (toString == null) {
         String message = this.getMessage();
         StringBuilder b = new StringBuilder();
         b.append(message == null ? CommonMessages.msg.remoteException(this.exceptionClassName) : CommonMessages.msg.remoteException(this.exceptionClassName, message));
         Iterator<Map.Entry<String, String>> iterator = this.fields.entrySet().iterator();
         if (iterator.hasNext()) {
            b.append("\n\tPublic fields:");

            do {
               Map.Entry<String, String> entry = (Map.Entry)iterator.next();
               b.append('\n').append('\t').append('\t').append((String)entry.getKey()).append('=').append((String)entry.getValue());
            } while(iterator.hasNext());
         }

         return this.toString = b.toString();
      } else {
         return toString;
      }
   }

   public void writeToStream(DataOutput output) throws IOException {
      Assert.checkNotNullParam("output", output);
      this.writeToStream(output, new IdentityIntMap(), new HashMap(), 0);
   }

   private static int readPackedInt(DataInput is) throws IOException {
      int b = is.readUnsignedByte();
      if ((b & 224) == 32) {
         return b << 27 >> 27;
      } else if (b == 5) {
         return is.readByte();
      } else if (b == 6) {
         return is.readShort();
      } else if (b == 7) {
         return is.readInt();
      } else {
         throw CommonMessages.msg.corruptedStream();
      }
   }

   private static void writePackedInt(DataOutput os, int val) throws IOException {
      if (-16 <= val && val < 16) {
         os.write(32 | val & 31);
      } else if (-128 <= val && val < 128) {
         os.write(5);
         os.write(val);
      } else if (-32768 <= val && val < 32768) {
         os.write(6);
         os.writeShort(val);
      } else {
         os.write(7);
         os.writeInt(val);
      }

   }

   private int writeToStream(DataOutput output, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
      seen.put(this, cnt++);
      output.writeByte(4);
      cnt = this.writeString(output, this.exceptionClassName, seen, stringCache, cnt);
      cnt = this.writeString(output, this.getMessage(), seen, stringCache, cnt);
      cnt = this.writeStackTrace(output, this.getStackTrace(), seen, stringCache, cnt);
      cnt = this.writeFields(output, this.fields, seen, stringCache, cnt);
      cnt = this.writeThrowable(output, this.getCause(), seen, stringCache, cnt);
      Throwable[] suppressed = this.getSuppressed();
      writePackedInt(output, suppressed.length);
      Throwable[] var6 = suppressed;
      int var7 = suppressed.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Throwable t = var6[var8];
         cnt = this.writeThrowable(output, t, seen, stringCache, cnt);
      }

      return cnt;
   }

   private int writeFields(DataOutput output, Map<String, String> fields, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
      writePackedInt(output, fields.size());

      Map.Entry entry;
      for(Iterator var6 = fields.entrySet().iterator(); var6.hasNext(); cnt = this.writeString(output, (String)entry.getValue(), seen, stringCache, cnt)) {
         entry = (Map.Entry)var6.next();
         cnt = this.writeString(output, (String)entry.getKey(), seen, stringCache, cnt);
      }

      return cnt;
   }

   private int writeStackTrace(DataOutput output, StackTraceElement[] stackTrace, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
      int length = stackTrace.length;
      writePackedInt(output, length);
      StackTraceElement[] var7 = stackTrace;
      int var8 = stackTrace.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         StackTraceElement element = var7[var9];
         cnt = this.writeStackElement(output, element, seen, stringCache, cnt);
      }

      return cnt;
   }

   private int writeStackElement(DataOutput output, StackTraceElement element, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
      int idx = seen.get(element, -1);
      int distance = cnt - idx;
      if (idx != -1 && distance <= 16383) {
         if (distance < 127) {
            output.writeByte(128 | distance);
         } else {
            assert distance <= 16383;

            output.writeByte(64 | distance >> 8);
            output.writeByte(distance);
         }

         return cnt;
      } else {
         output.write(2);
         cnt = this.writeString(output, element.getClassName(), seen, stringCache, cnt);
         cnt = this.writeString(output, element.getMethodName(), seen, stringCache, cnt);
         cnt = this.writeString(output, element.getFileName(), seen, stringCache, cnt);
         writePackedInt(output, element.getLineNumber());
         seen.put(element, cnt++);
         return cnt;
      }
   }

   private int writeThrowable(DataOutput output, Throwable throwable, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
      if (throwable == null) {
         output.write(0);
         return cnt;
      } else {
         int idx = seen.get(throwable, -1);
         int distance = cnt - idx;
         if (idx != -1 && distance < 16384) {
            if (distance < 127) {
               output.writeByte(128 | distance);
            } else {
               assert distance <= 16383;

               output.writeByte(64 | distance >> 8);
               output.writeByte(distance);
            }

            return cnt;
         } else {
            RemoteExceptionCause nested;
            if (throwable instanceof RemoteExceptionCause) {
               nested = (RemoteExceptionCause)throwable;
            } else {
               seen.put(throwable, cnt);
               nested = of(throwable);
            }

            return nested.writeToStream(output, seen, stringCache, cnt);
         }
      }
   }

   private int writeString(DataOutput output, String string, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
      if (string == null) {
         output.write(0);
         return cnt;
      } else {
         string = (String)stringCache.computeIfAbsent(string, Function.identity());
         int idx = seen.get(string, -1);
         int distance = cnt - idx;
         if (idx != -1 && distance <= 16383) {
            if (distance < 127) {
               output.writeByte(128 | distance);
            } else {
               assert distance <= 16383;

               output.writeByte(64 | distance >> 8);
               output.writeByte(distance);
            }

            return cnt;
         } else {
            seen.put(string, cnt);
            output.write(1);
            output.writeUTF(string);
            return cnt + 1;
         }
      }
   }

   public static RemoteExceptionCause readFromStream(DataInput input) throws IOException {
      return (RemoteExceptionCause)readObject(input, RemoteExceptionCause.class, new ArrayList(), false);
   }

   private static <T> T readObject(DataInput input, Class<T> expect, ArrayList<Object> cache, boolean allowNull) throws IOException {
      int b = input.readUnsignedByte();
      if (b == 0) {
         if (!allowNull) {
            throw CommonMessages.msg.corruptedStream();
         } else {
            return null;
         }
      } else if (b == 1) {
         if (expect != String.class) {
            throw CommonMessages.msg.corruptedStream();
         } else {
            String str = input.readUTF();
            cache.add(str);
            return expect.cast(str);
         }
      } else {
         int b2;
         if (b == 4) {
            if (expect != RemoteExceptionCause.class) {
               throw CommonMessages.msg.corruptedStream();
            } else {
               b2 = cache.size();
               cache.add((Object)null);
               String exClassName = (String)readObject(input, String.class, cache, false);
               String exMessage = (String)readObject(input, String.class, cache, true);
               int length = readPackedInt(input);
               StackTraceElement[] stackTrace;
               if (length == 0) {
                  stackTrace = EMPTY_STACK;
               } else {
                  stackTrace = new StackTraceElement[length];

                  for(int i = 0; i < length; ++i) {
                     stackTrace[i] = (StackTraceElement)readObject(input, StackTraceElement.class, cache, false);
                  }
               }

               length = readPackedInt(input);
               Object fields;
               if (length == 0) {
                  fields = Collections.emptyMap();
               } else if (length == 1) {
                  fields = Collections.singletonMap((String)readObject(input, String.class, cache, false), (String)readObject(input, String.class, cache, false));
               } else {
                  fields = new HashMap(length);

                  for(int i = 0; i < length; ++i) {
                     ((Map)fields).put((String)readObject(input, String.class, cache, false), (String)readObject(input, String.class, cache, false));
                  }
               }

               RemoteExceptionCause result = new RemoteExceptionCause(exMessage, (RemoteExceptionCause)null, exClassName, (Map)fields, false);
               cache.set(b2, result);
               RemoteExceptionCause causedBy = (RemoteExceptionCause)readObject(input, RemoteExceptionCause.class, cache, true);
               result.initCause(causedBy);
               length = readPackedInt(input);
               result.setStackTrace(stackTrace);

               for(int i = 0; i < length; ++i) {
                  result.addSuppressed((Throwable)readObject(input, RemoteExceptionCause.class, cache, false));
               }

               return expect.cast(result);
            }
         } else {
            StackTraceElement element;
            if (b == 2) {
               if (expect != StackTraceElement.class) {
                  throw CommonMessages.msg.corruptedStream();
               } else {
                  element = new StackTraceElement((String)readObject(input, String.class, cache, false), (String)readObject(input, String.class, cache, false), (String)readObject(input, String.class, cache, true), readPackedInt(input));
                  cache.add(element);
                  return expect.cast(element);
               }
            } else if (b == 3) {
               if (expect != StackTraceElement.class) {
                  throw CommonMessages.msg.corruptedStream();
               } else {
                  readObject(input, String.class, cache, true);
                  readObject(input, String.class, cache, true);
                  readObject(input, String.class, cache, true);
                  element = new StackTraceElement((String)readObject(input, String.class, cache, false), (String)readObject(input, String.class, cache, false), (String)readObject(input, String.class, cache, true), readPackedInt(input));
                  cache.add(element);
                  return expect.cast(element);
               }
            } else if ((b & 128) != 0) {
               b2 = b & 127;
               if (b2 > cache.size()) {
                  throw CommonMessages.msg.corruptedStream();
               } else {
                  Object obj = cache.get(cache.size() - b2);
                  if (expect.isInstance(obj)) {
                     return expect.cast(obj);
                  } else {
                     throw CommonMessages.msg.corruptedStream();
                  }
               }
            } else if ((b & 64) != 0) {
               b2 = input.readUnsignedByte();
               int idx = (b & 63) << 8 | b2;
               if (idx > cache.size()) {
                  throw CommonMessages.msg.corruptedStream();
               } else {
                  Object obj = cache.get(cache.size() - idx);
                  if (expect.isInstance(obj)) {
                     return expect.cast(obj);
                  } else {
                     throw CommonMessages.msg.corruptedStream();
                  }
               }
            } else {
               throw CommonMessages.msg.corruptedStream();
            }
         }
      }
   }

   Object writeReplace() {
      Throwable[] origSuppressed = this.getSuppressed();
      int length = origSuppressed.length;
      RemoteExceptionCause[] suppressed;
      if (length == 0) {
         suppressed = NO_REMOTE_EXCEPTION_CAUSES;
      } else {
         suppressed = new RemoteExceptionCause[length];

         for(int i = 0; i < length; ++i) {
            suppressed[i] = of(origSuppressed[i]);
         }
      }

      int size = this.fields.size();
      String[] fieldArray;
      if (size == 0) {
         fieldArray = NO_STRINGS;
      } else {
         fieldArray = new String[size << 1];
         int i = 0;

         Map.Entry entry;
         for(Iterator var7 = this.fields.entrySet().iterator(); var7.hasNext(); fieldArray[i++] = (String)entry.getValue()) {
            entry = (Map.Entry)var7.next();
            fieldArray[i++] = (String)entry.getKey();
         }
      }

      return new Serialized(this.getMessage(), this.exceptionClassName, of(this.getCause()), suppressed, this.getStackTrace(), fieldArray);
   }

   public RemoteExceptionCause getCause() {
      return (RemoteExceptionCause)super.getCause();
   }

   static final class Serialized implements Serializable {
      private static final long serialVersionUID = -2201431870774913071L;
      final String m;
      final String cn;
      final RemoteExceptionCause c;
      final RemoteExceptionCause[] s;
      final StackTraceElement[] st;
      final String[] f;

      Serialized(String m, String cn, RemoteExceptionCause c, RemoteExceptionCause[] s, StackTraceElement[] st, String[] f) {
         this.m = m;
         this.cn = cn;
         this.c = c;
         this.s = s;
         this.st = st;
         this.f = f;
      }

      Object readResolve() {
         Map fields;
         if (this.f == null) {
            fields = Collections.emptyMap();
         } else {
            int fl = this.f.length;
            if ((fl & 1) != 0) {
               throw CommonMessages.msg.invalidOddFields();
            }

            if (fl == 0) {
               fields = Collections.emptyMap();
            } else if (fl == 2) {
               fields = Collections.singletonMap(this.f[0], this.f[1]);
            } else {
               TreeMap<String, String> map = new TreeMap();

               for(int i = 0; i < fl; i += 2) {
                  map.put(this.f[i], this.f[i + 1]);
               }

               fields = Collections.unmodifiableMap(map);
            }
         }

         RemoteExceptionCause ex = new RemoteExceptionCause(this.m, this.c, this.cn, fields, false);
         ex.setStackTrace(this.st);
         RemoteExceptionCause[] suppressed = this.s;
         if (suppressed != null) {
            RemoteExceptionCause[] var10 = suppressed;
            int var5 = suppressed.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               RemoteExceptionCause c = var10[var6];
               ex.addSuppressed(c);
            }
         }

         return ex;
      }
   }
}
