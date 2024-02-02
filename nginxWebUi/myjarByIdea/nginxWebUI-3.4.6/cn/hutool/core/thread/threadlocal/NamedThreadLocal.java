package cn.hutool.core.thread.threadlocal;

public class NamedThreadLocal<T> extends ThreadLocal<T> {
   private final String name;

   public NamedThreadLocal(String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }
}
