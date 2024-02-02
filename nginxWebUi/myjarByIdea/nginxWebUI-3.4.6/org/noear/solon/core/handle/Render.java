package org.noear.solon.core.handle;

public interface Render {
   default String getName() {
      return this.getClass().getSimpleName();
   }

   default String renderAndReturn(Object data, Context ctx) throws Throwable {
      return null;
   }

   void render(Object data, Context ctx) throws Throwable;
}
