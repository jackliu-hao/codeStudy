package cn.hutool.core.map;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class CaseInsensitiveTreeMap<K, V> extends CaseInsensitiveMap<K, V> {
   private static final long serialVersionUID = 4043263744224569870L;

   public CaseInsensitiveTreeMap() {
      this((Comparator)null);
   }

   public CaseInsensitiveTreeMap(Map<? extends K, ? extends V> m) {
      this();
      this.putAll(m);
   }

   public CaseInsensitiveTreeMap(SortedMap<? extends K, ? extends V> m) {
      super((Map)(new TreeMap(m)));
   }

   public CaseInsensitiveTreeMap(Comparator<? super K> comparator) {
      super((Map)(new TreeMap(comparator)));
   }
}
