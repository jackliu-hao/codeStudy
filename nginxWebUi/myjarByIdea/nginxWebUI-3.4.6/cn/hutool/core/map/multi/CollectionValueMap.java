package cn.hutool.core.map.multi;

import cn.hutool.core.lang.func.Func0;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CollectionValueMap<K, V> extends AbsCollValueMap<K, V, Collection<V>> {
   private static final long serialVersionUID = 9012989578038102983L;
   private final Func0<Collection<V>> collectionCreateFunc;

   public CollectionValueMap() {
      this(16);
   }

   public CollectionValueMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public CollectionValueMap(Map<? extends K, ? extends Collection<V>> m) {
      this(0.75F, m);
   }

   public CollectionValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m) {
      this(loadFactor, m, ArrayList::new);
   }

   public CollectionValueMap(int initialCapacity, float loadFactor) {
      this(initialCapacity, loadFactor, ArrayList::new);
   }

   public CollectionValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m, Func0<Collection<V>> collectionCreateFunc) {
      this(m.size(), loadFactor, collectionCreateFunc);
      this.putAll(m);
   }

   public CollectionValueMap(int initialCapacity, float loadFactor, Func0<Collection<V>> collectionCreateFunc) {
      super(new HashMap(initialCapacity, loadFactor));
      this.collectionCreateFunc = collectionCreateFunc;
   }

   protected Collection<V> createCollection() {
      return (Collection)this.collectionCreateFunc.callWithRuntimeException();
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "<init>":
            if (lambda.getImplMethodKind() == 8 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("java/util/ArrayList") && lambda.getImplMethodSignature().equals("()V")) {
               return ArrayList::new;
            } else if (lambda.getImplMethodKind() == 8 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("java/util/ArrayList") && lambda.getImplMethodSignature().equals("()V")) {
               return ArrayList::new;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
