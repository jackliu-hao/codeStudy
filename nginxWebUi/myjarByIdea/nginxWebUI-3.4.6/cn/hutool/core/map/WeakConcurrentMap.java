package cn.hutool.core.map;

import cn.hutool.core.util.ReferenceUtil;
import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WeakConcurrentMap<K, V> extends ReferenceConcurrentMap<K, V> {
   public WeakConcurrentMap() {
      this(new ConcurrentHashMap());
   }

   public WeakConcurrentMap(ConcurrentMap<Reference<K>, V> raw) {
      super(raw, ReferenceUtil.ReferenceType.WEAK);
   }
}
