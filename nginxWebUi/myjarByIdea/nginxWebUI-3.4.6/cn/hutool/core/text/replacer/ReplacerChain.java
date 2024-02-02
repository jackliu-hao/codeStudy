package cn.hutool.core.text.replacer;

import cn.hutool.core.lang.Chain;
import cn.hutool.core.text.StrBuilder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ReplacerChain extends StrReplacer implements Chain<StrReplacer, ReplacerChain> {
   private static final long serialVersionUID = 1L;
   private final List<StrReplacer> replacers = new LinkedList();

   public ReplacerChain(StrReplacer... strReplacers) {
      StrReplacer[] var2 = strReplacers;
      int var3 = strReplacers.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         StrReplacer strReplacer = var2[var4];
         this.addChain(strReplacer);
      }

   }

   public Iterator<StrReplacer> iterator() {
      return this.replacers.iterator();
   }

   public ReplacerChain addChain(StrReplacer element) {
      this.replacers.add(element);
      return this;
   }

   protected int replace(CharSequence str, int pos, StrBuilder out) {
      int consumed = 0;
      Iterator var5 = this.replacers.iterator();

      do {
         if (!var5.hasNext()) {
            return consumed;
         }

         StrReplacer strReplacer = (StrReplacer)var5.next();
         consumed = strReplacer.replace(str, pos, out);
      } while(0 == consumed);

      return consumed;
   }
}
