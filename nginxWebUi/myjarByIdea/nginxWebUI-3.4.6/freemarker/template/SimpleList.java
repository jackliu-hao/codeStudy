package freemarker.template;

import java.util.Collection;
import java.util.List;

/** @deprecated */
@Deprecated
public class SimpleList extends SimpleSequence {
   public SimpleList() {
   }

   public SimpleList(List list) {
      super((Collection)list);
   }
}
