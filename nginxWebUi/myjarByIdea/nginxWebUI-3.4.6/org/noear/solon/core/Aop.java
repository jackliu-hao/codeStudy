package org.noear.solon.core;

import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.noear.solon.Utils;

public class Aop {
   private static final AopContext ac = new AopContext();

   public static AopContext context() {
      return ac;
   }

   public static BeanWrap wrap(Class<?> type, Object bean) {
      return ac.wrap(type, bean);
   }

   public static BeanWrap wrapAndPut(Class<?> type) {
      return wrapAndPut(type, (Object)null);
   }

   public static BeanWrap wrapAndPut(Class<?> type, Object bean) {
      return ac.wrapAndPut(type, bean);
   }

   public static boolean has(Object nameOrType) {
      return ac.hasWrap(nameOrType);
   }

   public static <T> T get(String name) {
      return ac.getBean(name);
   }

   public static <T> T get(Class<T> type) {
      return ac.getBean(type);
   }

   public static <T> T getOrNew(Class<T> type) {
      return ac.getBeanOrNew(type);
   }

   public static void getAsyn(String name, Consumer<BeanWrap> callback) {
      ac.getWrapAsyn(name, callback);
   }

   public static void getAsyn(Class<?> type, Consumer<BeanWrap> callback) {
      ac.getWrapAsyn(type, callback);
   }

   public static <T> T inject(T bean) {
      ac.beanInject(bean);
      return bean;
   }

   public static <T> T inject(T bean, Properties propS) {
      return Utils.injectProperties(bean, propS);
   }

   public static void beanOnloaded(Consumer<AopContext> fun) {
      ac.beanOnloaded(fun);
   }

   public static void beanOnloaded(int index, Consumer<AopContext> fun) {
      ac.beanOnloaded(index, fun);
   }

   public static void beanForeach(BiConsumer<String, BeanWrap> action) {
      ac.beanForeach(action);
   }

   public static void beanForeach(Consumer<BeanWrap> action) {
      ac.beanForeach(action);
   }

   public static List<BeanWrap> beanFind(BiPredicate<String, BeanWrap> filter) {
      return ac.beanFind(filter);
   }

   public static List<BeanWrap> beanFind(Predicate<BeanWrap> filter) {
      return ac.beanFind(filter);
   }
}
