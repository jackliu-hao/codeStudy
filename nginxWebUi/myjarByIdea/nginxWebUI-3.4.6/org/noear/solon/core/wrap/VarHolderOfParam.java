package org.noear.solon.core.wrap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.VarHolder;

public class VarHolderOfParam implements VarHolder {
   private final Parameter p;
   private final ParameterizedType genericType;
   private final AopContext ctx;
   protected Object val;
   protected boolean done;
   protected Runnable onDone;

   public VarHolderOfParam(AopContext ctx, Parameter p, Runnable onDone) {
      this.ctx = ctx;
      this.p = p;
      this.onDone = onDone;
      Type tmp = p.getParameterizedType();
      if (tmp instanceof ParameterizedType) {
         this.genericType = (ParameterizedType)tmp;
      } else {
         this.genericType = null;
      }

   }

   public AopContext context() {
      return this.ctx;
   }

   public ParameterizedType getGenericType() {
      return this.genericType;
   }

   public boolean isField() {
      return false;
   }

   public Class<?> getType() {
      return this.p.getType();
   }

   public Annotation[] getAnnoS() {
      return this.p.getAnnotations();
   }

   public void setValue(Object val) {
      this.val = val;
      this.done = true;
      if (this.onDone != null) {
         this.onDone.run();
      }

   }

   public Object getValue() {
      return this.val;
   }

   public boolean isDone() {
      return this.done;
   }
}
