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
   // $FF: synthetic field
   static Class class$freemarker$debug$DebugModel;
   // $FF: synthetic field
   static Class class$java$lang$String;
   // $FF: synthetic field
   static Class array$Ljava$lang$String;

   static {
      try {
         $method_get_0 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("get", Integer.TYPE);
         $method_get_1 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("get", Integer.TYPE, Integer.TYPE);
         $method_get_2 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("get", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
         $method_get_3 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("get", array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
         $method_getAsBoolean_4 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("getAsBoolean");
         $method_getAsDate_5 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("getAsDate");
         $method_getAsNumber_6 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("getAsNumber");
         $method_getAsString_7 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("getAsString");
         $method_getCollection_8 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("getCollection");
         $method_getDateType_9 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("getDateType");
         $method_getModelTypes_10 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("getModelTypes");
         $method_keys_11 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("keys");
         $method_size_12 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = class$("freemarker.debug.DebugModel"))).getMethod("size");
      } catch (NoSuchMethodException var0) {
         throw new NoSuchMethodError("stub class initialization failed");
      }
   }

   public RmiDebugModelImpl_Stub(RemoteRef var1) {
      super(var1);
   }

   // $FF: synthetic method
   static Class class$(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   public DebugModel get(int var1) throws TemplateModelException, RemoteException {
      try {
         Object var2 = super.ref.invoke(this, $method_get_0, new Object[]{new Integer(var1)}, -8133058733457407300L);
         return (DebugModel)var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (TemplateModelException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new UnexpectedException("undeclared checked exception", var6);
      }
   }

   public DebugModel[] get(int var1, int var2) throws TemplateModelException, RemoteException {
      try {
         Object var3 = super.ref.invoke(this, $method_get_1, new Object[]{new Integer(var1), new Integer(var2)}, 2963274088089045739L);
         return (DebugModel[])var3;
      } catch (RuntimeException var4) {
         throw var4;
      } catch (RemoteException var5) {
         throw var5;
      } catch (TemplateModelException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new UnexpectedException("undeclared checked exception", var7);
      }
   }

   public DebugModel get(String var1) throws TemplateModelException, RemoteException {
      try {
         Object var2 = super.ref.invoke(this, $method_get_2, new Object[]{var1}, -724507235264020332L);
         return (DebugModel)var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (TemplateModelException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new UnexpectedException("undeclared checked exception", var6);
      }
   }

   public DebugModel[] get(String[] var1) throws TemplateModelException, RemoteException {
      try {
         Object var2 = super.ref.invoke(this, $method_get_3, new Object[]{var1}, -5400820492225333337L);
         return (DebugModel[])var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (TemplateModelException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new UnexpectedException("undeclared checked exception", var6);
      }
   }

   public boolean getAsBoolean() throws TemplateModelException, RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_getAsBoolean_4, (Object[])null, 315270873791227726L);
         return (Boolean)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (TemplateModelException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public Date getAsDate() throws TemplateModelException, RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_getAsDate_5, (Object[])null, -6762406881188215033L);
         return (Date)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (TemplateModelException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public Number getAsNumber() throws TemplateModelException, RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_getAsNumber_6, (Object[])null, -6188010426576701549L);
         return (Number)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (TemplateModelException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public String getAsString() throws TemplateModelException, RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_getAsString_7, (Object[])null, -5749749291031241731L);
         return (String)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (TemplateModelException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public DebugModel[] getCollection() throws TemplateModelException, RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_getCollection_8, (Object[])null, -1992223977663617938L);
         return (DebugModel[])var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (TemplateModelException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public int getDateType() throws TemplateModelException, RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_getDateType_9, (Object[])null, -3242981404503740604L);
         return (Integer)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (TemplateModelException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public int getModelTypes() throws RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_getModelTypes_10, (Object[])null, -3673171458095957561L);
         return (Integer)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new UnexpectedException("undeclared checked exception", var4);
      }
   }

   public String[] keys() throws TemplateModelException, RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_keys_11, (Object[])null, 563174456558742983L);
         return (String[])var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (TemplateModelException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public int size() throws TemplateModelException, RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_size_12, (Object[])null, 4495240443643581991L);
         return (Integer)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (TemplateModelException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }
}
