package org.noear.solon.serialization;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

public class StringSerializerRender implements Render {
   StringSerializer serializer;
   boolean typed;

   public StringSerializerRender(boolean typed, StringSerializer serializer) {
      this.typed = typed;
      this.serializer = serializer;
   }

   public String getName() {
      return this.getClass().getSimpleName() + "#" + this.serializer.getClass().getSimpleName();
   }

   public String renderAndReturn(Object data, Context ctx) throws Throwable {
      return this.serializer.serialize(data);
   }

   public void render(Object obj, Context ctx) throws Throwable {
      String txt = null;
      if (this.typed) {
         txt = this.serializer.serialize(obj);
      } else {
         if (obj == null) {
            return;
         }

         if (obj instanceof Throwable) {
            throw (Throwable)obj;
         }

         if (obj instanceof String) {
            txt = (String)obj;
         } else {
            txt = this.serializer.serialize(obj);
         }
      }

      ctx.attrSet("output", txt);
      this.output(ctx, obj, txt);
   }

   protected void output(Context ctx, Object obj, String txt) {
      if (obj instanceof String && !ctx.accept().contains("/json")) {
         ctx.output(txt);
      } else {
         ctx.outputAsJson(txt);
      }

   }
}
