package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import java.util.Date;

public class COMLateBindingObject extends COMBindingBaseObject {
   public COMLateBindingObject(IDispatch iDispatch) {
      super(iDispatch);
   }

   public COMLateBindingObject(Guid.CLSID clsid, boolean useActiveInstance) {
      super(clsid, useActiveInstance);
   }

   public COMLateBindingObject(String progId, boolean useActiveInstance) throws COMException {
      super(progId, useActiveInstance);
   }

   protected IDispatch getAutomationProperty(String propertyName) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(2, result, propertyName);
      return (IDispatch)result.getValue();
   }

   protected IDispatch getAutomationProperty(String propertyName, Variant.VARIANT value) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(2, result, propertyName, value);
      return (IDispatch)result.getValue();
   }

   /** @deprecated */
   @Deprecated
   protected IDispatch getAutomationProperty(String propertyName, COMLateBindingObject comObject) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(2, result, propertyName);
      return (IDispatch)result.getValue();
   }

   /** @deprecated */
   @Deprecated
   protected IDispatch getAutomationProperty(String propertyName, COMLateBindingObject comObject, Variant.VARIANT value) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(2, result, propertyName, value);
      return (IDispatch)result.getValue();
   }

   /** @deprecated */
   @Deprecated
   protected IDispatch getAutomationProperty(String propertyName, IDispatch iDispatch) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(2, result, propertyName);
      return (IDispatch)result.getValue();
   }

   protected boolean getBooleanProperty(String propertyName) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(2, result, propertyName);
      return result.booleanValue();
   }

   protected Date getDateProperty(String propertyName) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(2, result, propertyName);
      return result.dateValue();
   }

   protected int getIntProperty(String propertyName) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(2, result, propertyName);
      return result.intValue();
   }

   protected short getShortProperty(String propertyName) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(2, result, propertyName);
      return result.shortValue();
   }

   protected String getStringProperty(String propertyName) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(2, result, propertyName);
      String res = result.stringValue();
      OleAuto.INSTANCE.VariantClear(result);
      return res;
   }

   protected Variant.VARIANT invoke(String methodName) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(1, result, methodName);
      return result;
   }

   protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(1, result, methodName, arg);
      return result;
   }

   protected Variant.VARIANT invoke(String methodName, Variant.VARIANT[] args) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(1, result, methodName, args);
      return result;
   }

   protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2) {
      return this.invoke(methodName, new Variant.VARIANT[]{arg1, arg2});
   }

   protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3) {
      return this.invoke(methodName, new Variant.VARIANT[]{arg1, arg2, arg3});
   }

   protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3, Variant.VARIANT arg4) {
      return this.invoke(methodName, new Variant.VARIANT[]{arg1, arg2, arg3, arg4});
   }

   /** @deprecated */
   @Deprecated
   protected void invokeNoReply(String methodName, IDispatch dispatch) {
      this.oleMethod(1, (Variant.VARIANT.ByReference)null, dispatch, methodName);
   }

   /** @deprecated */
   @Deprecated
   protected void invokeNoReply(String methodName, COMLateBindingObject comObject) {
      this.oleMethod(1, (Variant.VARIANT.ByReference)null, comObject.getIDispatch(), methodName);
   }

   protected void invokeNoReply(String methodName, Variant.VARIANT arg) {
      this.oleMethod(1, (Variant.VARIANT.ByReference)null, methodName, arg);
   }

   /** @deprecated */
   @Deprecated
   protected void invokeNoReply(String methodName, IDispatch dispatch, Variant.VARIANT arg) {
      this.oleMethod(1, (Variant.VARIANT.ByReference)null, dispatch, methodName, arg);
   }

   /** @deprecated */
   @Deprecated
   protected void invokeNoReply(String methodName, IDispatch dispatch, Variant.VARIANT arg1, Variant.VARIANT arg2) {
      this.oleMethod(1, (Variant.VARIANT.ByReference)null, dispatch, methodName, new Variant.VARIANT[]{arg1, arg2});
   }

   /** @deprecated */
   @Deprecated
   protected void invokeNoReply(String methodName, COMLateBindingObject comObject, Variant.VARIANT arg1, Variant.VARIANT arg2) {
      this.oleMethod(1, (Variant.VARIANT.ByReference)null, comObject.getIDispatch(), methodName, new Variant.VARIANT[]{arg1, arg2});
   }

   /** @deprecated */
   protected void invokeNoReply(String methodName, COMLateBindingObject comObject, Variant.VARIANT arg) {
      this.oleMethod(1, (Variant.VARIANT.ByReference)null, comObject.getIDispatch(), methodName, arg);
   }

   /** @deprecated */
   @Deprecated
   protected void invokeNoReply(String methodName, IDispatch dispatch, Variant.VARIANT[] args) {
      this.oleMethod(1, (Variant.VARIANT.ByReference)null, dispatch, methodName, args);
   }

   protected void invokeNoReply(String methodName) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(1, result, methodName);
   }

   protected void invokeNoReply(String methodName, Variant.VARIANT[] args) {
      Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
      this.oleMethod(1, result, methodName, args);
   }

   protected void invokeNoReply(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2) {
      this.invokeNoReply(methodName, new Variant.VARIANT[]{arg1, arg2});
   }

   protected void invokeNoReply(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3) {
      this.invokeNoReply(methodName, new Variant.VARIANT[]{arg1, arg2, arg3});
   }

   protected void invokeNoReply(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3, Variant.VARIANT arg4) {
      this.invokeNoReply(methodName, new Variant.VARIANT[]{arg1, arg2, arg3, arg4});
   }

   protected void setProperty(String propertyName, boolean value) {
      this.oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
   }

   protected void setProperty(String propertyName, Date value) {
      this.oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
   }

   protected void setProperty(String propertyName, Dispatch value) {
      this.oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
   }

   /** @deprecated */
   @Deprecated
   protected void setProperty(String propertyName, IDispatch value) {
      this.oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
   }

   protected void setProperty(String propertyName, int value) {
      this.oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
   }

   protected void setProperty(String propertyName, short value) {
      this.oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
   }

   protected void setProperty(String propertyName, String value) {
      this.oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
   }

   protected void setProperty(String propertyName, Variant.VARIANT value) {
      this.oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, value);
   }

   /** @deprecated */
   @Deprecated
   protected void setProperty(String propertyName, IDispatch iDispatch, Variant.VARIANT value) {
      this.oleMethod(4, (Variant.VARIANT.ByReference)null, iDispatch, propertyName, value);
   }

   /** @deprecated */
   @Deprecated
   protected void setProperty(String propertyName, COMLateBindingObject comObject, Variant.VARIANT value) {
      this.oleMethod(4, (Variant.VARIANT.ByReference)null, comObject.getIDispatch(), propertyName, value);
   }

   public Variant.VARIANT toVariant() {
      return new Variant.VARIANT(this.getIDispatch());
   }
}
