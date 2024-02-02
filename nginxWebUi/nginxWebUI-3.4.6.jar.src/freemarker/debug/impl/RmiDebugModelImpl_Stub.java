package freemarker.debug.impl;

import freemarker.debug.DebugModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.util.Date;

public final class RmiDebugModelImpl_Stub extends RemoteStub implements DebugModel, Remote {
  private static final long serialVersionUID = 2L;
  
  private static Method $method_get_0;
  
  private static Method $method_get_1;
  
  private static Method $method_get_2;
  
  private static Method $method_get_3;
  
  private static Method $method_getAsBoolean_4;
  
  private static Method $method_getAsDate_5;
  
  private static Method $method_getAsNumber_6;
  
  private static Method $method_getAsString_7;
  
  private static Method $method_getCollection_8;
  
  private static Method $method_getDateType_9;
  
  private static Method $method_getModelTypes_10;
  
  private static Method $method_keys_11;
  
  private static Method $method_size_12;
  
  static Class array$Ljava$lang$String;
  
  static {
    try {
      $method_get_0 = DebugModel.class.getMethod("get", new Class[] { int.class });
      $method_get_1 = DebugModel.class.getMethod("get", new Class[] { int.class, int.class });
      $method_get_2 = DebugModel.class.getMethod("get", new Class[] { String.class });
      $method_get_3 = DebugModel.class.getMethod("get", new Class[] { (array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")) });
      $method_getAsBoolean_4 = DebugModel.class.getMethod("getAsBoolean", new Class[0]);
      $method_getAsDate_5 = DebugModel.class.getMethod("getAsDate", new Class[0]);
      $method_getAsNumber_6 = DebugModel.class.getMethod("getAsNumber", new Class[0]);
      $method_getAsString_7 = DebugModel.class.getMethod("getAsString", new Class[0]);
      $method_getCollection_8 = DebugModel.class.getMethod("getCollection", new Class[0]);
      $method_getDateType_9 = DebugModel.class.getMethod("getDateType", new Class[0]);
      $method_getModelTypes_10 = DebugModel.class.getMethod("getModelTypes", new Class[0]);
      $method_keys_11 = DebugModel.class.getMethod("keys", new Class[0]);
      $method_size_12 = DebugModel.class.getMethod("size", new Class[0]);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError("stub class initialization failed");
    } 
  }
  
  public RmiDebugModelImpl_Stub(RemoteRef paramRemoteRef) {
    super(paramRemoteRef);
  }
  
  public DebugModel get(int paramInt) throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_get_0, new Object[] { new Integer(paramInt) }, -8133058733457407300L);
      return (DebugModel)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public DebugModel[] get(int paramInt1, int paramInt2) throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_get_1, new Object[] { new Integer(paramInt1), new Integer(paramInt2) }, 2963274088089045739L);
      return (DebugModel[])object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public DebugModel get(String paramString) throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_get_2, new Object[] { paramString }, -724507235264020332L);
      return (DebugModel)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public DebugModel[] get(String[] paramArrayOfString) throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_get_3, new Object[] { paramArrayOfString }, -5400820492225333337L);
      return (DebugModel[])object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public boolean getAsBoolean() throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_getAsBoolean_4, null, 315270873791227726L);
      return ((Boolean)object).booleanValue();
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Date getAsDate() throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_getAsDate_5, null, -6762406881188215033L);
      return (Date)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Number getAsNumber() throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_getAsNumber_6, null, -6188010426576701549L);
      return (Number)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public String getAsString() throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_getAsString_7, null, -5749749291031241731L);
      return (String)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public DebugModel[] getCollection() throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_getCollection_8, null, -1992223977663617938L);
      return (DebugModel[])object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public int getDateType() throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_getDateType_9, null, -3242981404503740604L);
      return ((Integer)object).intValue();
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public int getModelTypes() throws RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_getModelTypes_10, null, -3673171458095957561L);
      return ((Integer)object).intValue();
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public String[] keys() throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_keys_11, null, 563174456558742983L);
      return (String[])object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public int size() throws TemplateModelException, RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_size_12, null, 4495240443643581991L);
      return ((Integer)object).intValue();
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (TemplateModelException templateModelException) {
      throw templateModelException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\impl\RmiDebugModelImpl_Stub.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */