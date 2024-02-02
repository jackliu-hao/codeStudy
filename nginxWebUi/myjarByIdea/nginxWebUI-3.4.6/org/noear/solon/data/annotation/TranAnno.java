package org.noear.solon.data.annotation;

import java.lang.annotation.Annotation;
import org.noear.solon.data.tran.TranIsolation;
import org.noear.solon.data.tran.TranPolicy;

public class TranAnno implements Tran {
   private TranPolicy _policy;
   private TranIsolation _isolation;
   private boolean _readOnly;

   public TranAnno() {
      this._policy = TranPolicy.required;
      this._isolation = TranIsolation.unspecified;
      this._readOnly = false;
   }

   public TranPolicy policy() {
      return this._policy;
   }

   public TranAnno policy(TranPolicy policy) {
      this._policy = policy;
      return this;
   }

   public TranIsolation isolation() {
      return this._isolation;
   }

   public TranAnno isolation(TranIsolation isolation) {
      this._isolation = isolation;
      return this;
   }

   public boolean readOnly() {
      return this._readOnly;
   }

   public TranAnno readOnly(boolean readOnly) {
      this._readOnly = readOnly;
      return this;
   }

   public Class<? extends Annotation> annotationType() {
      return Tran.class;
   }
}
