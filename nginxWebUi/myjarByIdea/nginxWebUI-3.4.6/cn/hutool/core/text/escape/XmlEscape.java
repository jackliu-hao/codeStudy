package cn.hutool.core.text.escape;

import cn.hutool.core.text.replacer.LookupReplacer;
import cn.hutool.core.text.replacer.ReplacerChain;

public class XmlEscape extends ReplacerChain {
   private static final long serialVersionUID = 1L;
   protected static final String[][] BASIC_ESCAPE = new String[][]{{"\"", "&quot;"}, {"&", "&amp;"}, {"<", "&lt;"}, {">", "&gt;"}};

   public XmlEscape() {
      super();
      this.addChain(new LookupReplacer(BASIC_ESCAPE));
   }
}
