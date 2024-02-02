package org.noear.solon.core.wrap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.ConvertUtil;

public class ClassWrap {
   private static Map<Class<?>, ClassWrap> cached = new ConcurrentHashMap();
   private final Class<?> _clz;
   private final Method[] methods;
   private final List<FieldWrap> fieldWraps;
   private final Map<String, FieldWrap> fieldAllWrapsMap;
   private boolean _recordable;
   private Constructor _recordConstructor;
   private Parameter[] _recordParams;

   public static ClassWrap get(Class<?> clz) {
      ClassWrap cw = (ClassWrap)cached.get(clz);
      if (cw == null) {
         synchronized(clz) {
            cw = (ClassWrap)cached.get(clz);
            if (cw == null) {
               cw = new ClassWrap(clz);
               cached.put(clz, cw);
            }
         }
      }

      return cw;
   }

   protected ClassWrap(Class<?> clz) {
      this._clz = clz;
      this._recordable = true;
      this.methods = clz.getDeclaredMethods();
      this.fieldWraps = new ArrayList();
      this.fieldAllWrapsMap = new LinkedHashMap();
      Predicate var10002 = this.fieldAllWrapsMap::containsKey;
      Map var10003 = this.fieldAllWrapsMap;
      this.doScanAllFields(clz, var10002, var10003::put);
      Field[] var2 = clz.getDeclaredFields();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Field f = var2[var4];
         FieldWrap fw = (FieldWrap)this.fieldAllWrapsMap.get(f.getName());
         if (fw != null) {
            this.fieldWraps.add(fw);
         }
      }

      if (this.fieldWraps.size() == 0) {
         this._recordable = false;
      }

      if (this._recordable) {
         this._recordConstructor = clz.getConstructors()[0];
         this._recordParams = this._recordConstructor.getParameters();
      }

   }

   public Class<?> clz() {
      return this._clz;
   }

   public Map<String, FieldWrap> getFieldAllWraps() {
      return Collections.unmodifiableMap(this.fieldAllWrapsMap);
   }

   public FieldWrap getFieldWrap(String field) {
      return (FieldWrap)this.fieldAllWrapsMap.get(field);
   }

   public Method[] getMethods() {
      return this.methods;
   }

   public boolean recordable() {
      return this._recordable;
   }

   public Constructor recordConstructor() {
      return this._recordConstructor;
   }

   public Parameter[] recordParams() {
      return this._recordParams;
   }

   public <T> T newBy(Properties data) {
      try {
         Constructor constructor = this.clz().getConstructor(Properties.class);
         if (constructor != null) {
            return constructor.newInstance(data);
         }
      } catch (Throwable var3) {
      }

      data.getClass();
      return this.newBy(data::getProperty);
   }

   public <T> T newBy(Function<String, String> data) {
      return this.newBy(data, (Context)null);
   }

   public <T> T newBy(Function<String, String> data, Context ctx) {
      try {
         if (this.recordable()) {
            Parameter[] argsP = this.recordParams();
            Object[] argsV = new Object[argsP.length];

            for(int i = 0; i < argsP.length; ++i) {
               Parameter p = argsP[i];
               String key = p.getName();
               String val0 = (String)data.apply(key);
               if (val0 != null) {
                  Object val = ConvertUtil.to(p, p.getType(), key, val0, ctx);
                  argsV[i] = val;
               } else if (p.getType() == UploadedFile.class) {
                  argsV[i] = ctx.file(key);
               } else {
                  argsV[i] = null;
               }
            }

            Object obj = this.recordConstructor().newInstance(argsV);
            return obj;
         } else {
            Object obj = this.clz().newInstance();
            this.doFill(obj, data, ctx);
            return obj;
         }
      } catch (RuntimeException var10) {
         throw var10;
      } catch (Throwable var11) {
         throw new RuntimeException(var11);
      }
   }

   public void fill(Object bean, Function<String, String> data) {
      try {
         this.doFill(bean, data, (Context)null);
      } catch (RuntimeException var4) {
         throw var4;
      } catch (Throwable var5) {
         throw new RuntimeException(var5);
      }
   }

   private void doFill(Object bean, Function<String, String> data, Context ctx) throws Exception {
      Iterator var4 = this.fieldAllWrapsMap.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry<String, FieldWrap> kv = (Map.Entry)var4.next();
         String key = (String)kv.getKey();
         String val0 = (String)data.apply(key);
         FieldWrap fw = (FieldWrap)kv.getValue();
         if (val0 != null) {
            Object val = ConvertUtil.to(fw.field, fw.type, key, val0, ctx);
            fw.setValue(bean, val);
         } else if (ctx != null && fw.type == UploadedFile.class) {
            UploadedFile file1 = ctx.file(key);
            if (file1 != null) {
               fw.setValue(bean, file1);
            }
         }
      }

   }

   private void doScanAllFields(Class<?> clz, Predicate<String> checker, BiConsumer<String, FieldWrap> consumer) {
      if (clz != null) {
         Field[] var4 = clz.getDeclaredFields();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Field f = var4[var6];
            int mod = f.getModifiers();
            if (!Modifier.isStatic(mod) && !Modifier.isTransient(mod) && !checker.test(f.getName())) {
               this._recordable &= Modifier.isFinal(mod);
               consumer.accept(f.getName(), new FieldWrap(this._clz, f, Modifier.isFinal(mod)));
            }
         }

         Class<?> sup = clz.getSuperclass();
         if (sup != Object.class) {
            this.doScanAllFields(sup, checker, consumer);
         }

      }
   }
}
