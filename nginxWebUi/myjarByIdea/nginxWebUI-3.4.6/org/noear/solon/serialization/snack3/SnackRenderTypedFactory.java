package org.noear.solon.serialization.snack3;

import org.noear.snack.core.NodeEncoder;
import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

public class SnackRenderTypedFactory extends SnackRenderFactoryBase {
   public static final SnackRenderTypedFactory global = new SnackRenderTypedFactory();
   private final Options config = Options.serialize();

   private SnackRenderTypedFactory() {
   }

   public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
      this.config.addEncoder(clz, encoder);
   }

   public Render create() {
      return new StringSerializerRender(true, new SnackSerializer(this.config));
   }

   protected Options config() {
      return this.config;
   }
}
