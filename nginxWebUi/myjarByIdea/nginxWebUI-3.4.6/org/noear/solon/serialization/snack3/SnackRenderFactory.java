package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

public class SnackRenderFactory extends SnackRenderFactoryBase {
   public static final SnackRenderFactory global = new SnackRenderFactory();
   private final Options config = Options.def();

   private SnackRenderFactory() {
   }

   public Render create() {
      return new StringSerializerRender(false, new SnackSerializer(this.config));
   }

   protected Options config() {
      return this.config;
   }

   public void setFeatures(Feature... features) {
      this.config.setFeatures(features);
   }
}
