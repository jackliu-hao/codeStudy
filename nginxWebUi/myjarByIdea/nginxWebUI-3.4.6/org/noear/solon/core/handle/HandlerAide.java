package org.noear.solon.core.handle;

import java.util.ArrayList;
import java.util.List;

public class HandlerAide {
   protected List<Handler> befores = new ArrayList();
   protected List<Handler> afters = new ArrayList();

   public void before(Handler handler) {
      this.befores.add(handler);
   }

   public void after(Handler handler) {
      this.afters.add(handler);
   }
}
