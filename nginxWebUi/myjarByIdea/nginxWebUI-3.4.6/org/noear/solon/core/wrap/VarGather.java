package org.noear.solon.core.wrap;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.VarHolder;

public class VarGather implements Runnable {
   List<VarHolderOfParam> vars;
   int varSize;
   Consumer<Object[]> done;
   BeanWrap bw;

   public VarGather(BeanWrap bw, int varSize, Consumer<Object[]> done) {
      this.bw = bw;
      this.done = done;
      this.varSize = varSize;
      this.vars = new ArrayList(varSize);
   }

   public VarHolder add(Parameter p) {
      VarHolderOfParam p2 = new VarHolderOfParam(this.bw.context(), p, this);
      this.vars.add(p2);
      return p2;
   }

   public void run() {
      Iterator var1 = this.vars.iterator();

      while(var1.hasNext()) {
         VarHolderOfParam p1 = (VarHolderOfParam)var1.next();
         if (!p1.isDone()) {
            return;
         }
      }

      if (this.vars.size() == this.varSize) {
         List<Object> args = new ArrayList(this.vars.size());
         Iterator var5 = this.vars.iterator();

         while(var5.hasNext()) {
            VarHolderOfParam p1 = (VarHolderOfParam)var5.next();
            args.add(p1.getValue());
         }

         this.done.accept(args.toArray());
      }
   }
}
