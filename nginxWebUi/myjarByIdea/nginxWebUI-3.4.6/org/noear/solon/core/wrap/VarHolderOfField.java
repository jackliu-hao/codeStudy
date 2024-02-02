package org.noear.solon.core.wrap;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.VarHolder;

public class VarHolderOfField implements VarHolder {
   protected final FieldWrap fw;
   protected final Object obj;
   protected final AopContext ctx;

   public VarHolderOfField(AopContext ctx, FieldWrap fw, Object obj) {
      this.ctx = ctx;
      this.fw = fw;
      this.obj = obj;
   }

   public AopContext context() {
      return this.ctx;
   }

   public ParameterizedType getGenericType() {
      return this.fw.genericType;
   }

   public boolean isField() {
      return true;
   }

   public Class<?> getType() {
      return this.fw.type;
   }

   public Annotation[] getAnnoS() {
      return this.fw.annoS;
   }

   public void setValue(Object val) {
      this.fw.setValue(this.obj, val, true);
   }
}
