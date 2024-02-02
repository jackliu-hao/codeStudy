package cn.hutool.core.text.escape;

import cn.hutool.core.text.replacer.LookupReplacer;

public class Html4Unescape extends XmlUnescape {
   private static final long serialVersionUID = 1L;
   protected static final String[][] ISO8859_1_UNESCAPE;
   protected static final String[][] HTML40_EXTENDED_UNESCAPE;

   public Html4Unescape() {
      this.addChain(new LookupReplacer(ISO8859_1_UNESCAPE));
      this.addChain(new LookupReplacer(HTML40_EXTENDED_UNESCAPE));
   }

   static {
      ISO8859_1_UNESCAPE = InternalEscapeUtil.invert(Html4Escape.ISO8859_1_ESCAPE);
      HTML40_EXTENDED_UNESCAPE = InternalEscapeUtil.invert(Html4Escape.HTML40_EXTENDED_ESCAPE);
   }
}
