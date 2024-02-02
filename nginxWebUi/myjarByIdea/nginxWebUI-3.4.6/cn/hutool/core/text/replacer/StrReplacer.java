package cn.hutool.core.text.replacer;

import cn.hutool.core.lang.Replacer;
import cn.hutool.core.text.StrBuilder;
import java.io.Serializable;

public abstract class StrReplacer implements Replacer<CharSequence>, Serializable {
   private static final long serialVersionUID = 1L;

   protected abstract int replace(CharSequence var1, int var2, StrBuilder var3);

   public CharSequence replace(CharSequence t) {
      int len = t.length();
      StrBuilder builder = StrBuilder.create(len);

      int consumed;
      for(int pos = 0; pos < len; pos += consumed) {
         consumed = this.replace(t, pos, builder);
         if (0 == consumed) {
            builder.append(t.charAt(pos));
            ++pos;
         }
      }

      return builder;
   }
}
