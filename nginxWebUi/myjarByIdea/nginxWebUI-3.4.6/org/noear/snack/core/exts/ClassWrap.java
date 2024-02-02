package org.noear.snack.core.exts;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ClassWrap {
   private static Map<Class<?>, ClassWrap> cached = new ConcurrentHashMap();
   private final Class<?> _clz;
   private final Collection<FieldWrap> _fieldAllWraps;
   private boolean _recordable;
   private Constructor _recordConstructor;
   private Parameter[] _recordParams;

   public static ClassWrap get(Class<?> clz) {
      ClassWrap cw = (ClassWrap)cached.get(clz);
      if (cw == null) {
         cw = new ClassWrap(clz);
         ClassWrap l = (ClassWrap)cached.putIfAbsent(clz, cw);
         if (l != null) {
            cw = l;
         }
      }

      return cw;
   }

   protected ClassWrap(Class<?> clz) {
      this._clz = clz;
      this._recordable = true;
      Map<String, FieldWrap> map = new LinkedHashMap();
      this.scanAllFields(clz, map::containsKey, map::put);
      this._fieldAllWraps = map.values();
      if (this._fieldAllWraps.size() == 0) {
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

   public Collection<FieldWrap> fieldAllWraps() {
      return this._fieldAllWraps;
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

   private void scanAllFields(Class<?> clz, Predicate<String> checker, BiConsumer<String, FieldWrap> consumer) {
      if (clz != null) {
         Field[] var4 = clz.getDeclaredFields();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Field f = var4[var6];
            int mod = f.getModifiers();
            if (!Modifier.isStatic(mod) && !Modifier.isTransient(mod) && !checker.test(f.getName())) {
               this._recordable &= Modifier.isFinal(mod);
               consumer.accept(f.getName(), new FieldWrap(clz, f, Modifier.isFinal(mod)));
            }
         }

         Class<?> sup = clz.getSuperclass();
         if (sup != Object.class) {
            this.scanAllFields(sup, checker, consumer);
         }

      }
   }
}
