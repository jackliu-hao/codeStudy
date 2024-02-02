package cn.hutool.core.text.escape;

import cn.hutool.core.text.replacer.LookupReplacer;
import cn.hutool.core.text.replacer.ReplacerChain;

public class XmlUnescape extends ReplacerChain {
   private static final long serialVersionUID = 1L;
   protected static final String[][] BASIC_UNESCAPE;
   protected static final String[][] OTHER_UNESCAPE;

   public XmlUnescape() {
      super();
      this.addChain(new LookupReplacer(BASIC_UNESCAPE));
      this.addChain(new NumericEntityUnescaper());
      this.addChain(new LookupReplacer(OTHER_UNESCAPE));
   }

   static {
      BASIC_UNESCAPE = InternalEscapeUtil.invert(XmlEscape.BASIC_ESCAPE);
      OTHER_UNESCAPE = new String[][]{{"&apos;", "'"}};
   }
}
