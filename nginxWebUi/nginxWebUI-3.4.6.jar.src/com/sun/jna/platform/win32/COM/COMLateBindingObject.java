/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class COMLateBindingObject
/*     */   extends COMBindingBaseObject
/*     */ {
/*     */   public COMLateBindingObject(IDispatch iDispatch) {
/*  47 */     super(iDispatch);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public COMLateBindingObject(Guid.CLSID clsid, boolean useActiveInstance) {
/*  59 */     super(clsid, useActiveInstance);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public COMLateBindingObject(String progId, boolean useActiveInstance) throws COMException {
/*  74 */     super(progId, useActiveInstance);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IDispatch getAutomationProperty(String propertyName) {
/*  85 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/*  86 */     oleMethod(2, result, propertyName);
/*     */     
/*  88 */     return (IDispatch)result.getValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IDispatch getAutomationProperty(String propertyName, Variant.VARIANT value) {
/* 101 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 102 */     oleMethod(2, result, propertyName, value);
/*     */     
/* 104 */     return (IDispatch)result.getValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected IDispatch getAutomationProperty(String propertyName, COMLateBindingObject comObject) {
/* 120 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 121 */     oleMethod(2, result, propertyName);
/*     */     
/* 123 */     return (IDispatch)result.getValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected IDispatch getAutomationProperty(String propertyName, COMLateBindingObject comObject, Variant.VARIANT value) {
/* 141 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 142 */     oleMethod(2, result, propertyName, value);
/*     */     
/* 144 */     return (IDispatch)result.getValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected IDispatch getAutomationProperty(String propertyName, IDispatch iDispatch) {
/* 160 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 161 */     oleMethod(2, result, propertyName);
/*     */     
/* 163 */     return (IDispatch)result.getValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getBooleanProperty(String propertyName) {
/* 174 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 175 */     oleMethod(2, result, propertyName);
/*     */     
/* 177 */     return result.booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Date getDateProperty(String propertyName) {
/* 188 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 189 */     oleMethod(2, result, propertyName);
/*     */     
/* 191 */     return result.dateValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getIntProperty(String propertyName) {
/* 202 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 203 */     oleMethod(2, result, propertyName);
/*     */     
/* 205 */     return result.intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected short getShortProperty(String propertyName) {
/* 216 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 217 */     oleMethod(2, result, propertyName);
/*     */     
/* 219 */     return result.shortValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getStringProperty(String propertyName) {
/* 230 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 231 */     oleMethod(2, result, propertyName);
/*     */     
/* 233 */     String res = result.stringValue();
/*     */     
/* 235 */     OleAuto.INSTANCE.VariantClear((Variant.VARIANT)result);
/*     */     
/* 237 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Variant.VARIANT invoke(String methodName) {
/* 248 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 249 */     oleMethod(1, result, methodName);
/*     */     
/* 251 */     return (Variant.VARIANT)result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg) {
/* 264 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 265 */     oleMethod(1, result, methodName, arg);
/*     */     
/* 267 */     return (Variant.VARIANT)result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Variant.VARIANT invoke(String methodName, Variant.VARIANT[] args) {
/* 280 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 281 */     oleMethod(1, result, methodName, args);
/*     */     
/* 283 */     return (Variant.VARIANT)result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2) {
/* 298 */     return invoke(methodName, new Variant.VARIANT[] { arg1, arg2 });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3) {
/* 316 */     return invoke(methodName, new Variant.VARIANT[] { arg1, arg2, arg3 });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3, Variant.VARIANT arg4) {
/* 336 */     return invoke(methodName, new Variant.VARIANT[] { arg1, arg2, arg3, arg4 });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void invokeNoReply(String methodName, IDispatch dispatch) {
/* 344 */     oleMethod(1, (Variant.VARIANT.ByReference)null, dispatch, methodName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void invokeNoReply(String methodName, COMLateBindingObject comObject) {
/* 352 */     oleMethod(1, (Variant.VARIANT.ByReference)null, comObject.getIDispatch(), methodName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void invokeNoReply(String methodName, Variant.VARIANT arg) {
/* 365 */     oleMethod(1, (Variant.VARIANT.ByReference)null, methodName, arg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void invokeNoReply(String methodName, IDispatch dispatch, Variant.VARIANT arg) {
/* 374 */     oleMethod(1, (Variant.VARIANT.ByReference)null, dispatch, methodName, arg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void invokeNoReply(String methodName, IDispatch dispatch, Variant.VARIANT arg1, Variant.VARIANT arg2) {
/* 383 */     oleMethod(1, (Variant.VARIANT.ByReference)null, dispatch, methodName, new Variant.VARIANT[] { arg1, arg2 });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void invokeNoReply(String methodName, COMLateBindingObject comObject, Variant.VARIANT arg1, Variant.VARIANT arg2) {
/* 393 */     oleMethod(1, (Variant.VARIANT.ByReference)null, comObject.getIDispatch(), methodName, new Variant.VARIANT[] { arg1, arg2 });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void invokeNoReply(String methodName, COMLateBindingObject comObject, Variant.VARIANT arg) {
/* 402 */     oleMethod(1, (Variant.VARIANT.ByReference)null, comObject.getIDispatch(), methodName, arg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void invokeNoReply(String methodName, IDispatch dispatch, Variant.VARIANT[] args) {
/* 413 */     oleMethod(1, (Variant.VARIANT.ByReference)null, dispatch, methodName, args);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void invokeNoReply(String methodName) {
/* 424 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 425 */     oleMethod(1, result, methodName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void invokeNoReply(String methodName, Variant.VARIANT[] args) {
/* 437 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/* 438 */     oleMethod(1, result, methodName, args);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void invokeNoReply(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2) {
/* 452 */     invokeNoReply(methodName, new Variant.VARIANT[] { arg1, arg2 });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void invokeNoReply(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3) {
/* 469 */     invokeNoReply(methodName, new Variant.VARIANT[] { arg1, arg2, arg3 });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void invokeNoReply(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3, Variant.VARIANT arg4) {
/* 488 */     invokeNoReply(methodName, new Variant.VARIANT[] { arg1, arg2, arg3, arg4 });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setProperty(String propertyName, boolean value) {
/* 500 */     oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setProperty(String propertyName, Date value) {
/* 512 */     oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setProperty(String propertyName, Dispatch value) {
/* 524 */     oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void setProperty(String propertyName, IDispatch value) {
/* 532 */     oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setProperty(String propertyName, int value) {
/* 544 */     oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setProperty(String propertyName, short value) {
/* 556 */     oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setProperty(String propertyName, String value) {
/* 568 */     oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, new Variant.VARIANT(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setProperty(String propertyName, Variant.VARIANT value) {
/* 580 */     oleMethod(4, (Variant.VARIANT.ByReference)null, propertyName, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void setProperty(String propertyName, IDispatch iDispatch, Variant.VARIANT value) {
/* 589 */     oleMethod(4, (Variant.VARIANT.ByReference)null, iDispatch, propertyName, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void setProperty(String propertyName, COMLateBindingObject comObject, Variant.VARIANT value) {
/* 599 */     oleMethod(4, (Variant.VARIANT.ByReference)null, comObject
/* 600 */         .getIDispatch(), propertyName, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Variant.VARIANT toVariant() {
/* 609 */     return new Variant.VARIANT(getIDispatch());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\COMLateBindingObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */