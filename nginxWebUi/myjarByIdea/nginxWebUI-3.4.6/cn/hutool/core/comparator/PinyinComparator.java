package cn.hutool.core.comparator;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class PinyinComparator implements Comparator<String>, Serializable {
   private static final long serialVersionUID = 1L;
   final Collator collator;

   public PinyinComparator() {
      this.collator = Collator.getInstance(Locale.CHINESE);
   }

   public int compare(String o1, String o2) {
      return this.collator.compare(o1, o2);
   }
}
