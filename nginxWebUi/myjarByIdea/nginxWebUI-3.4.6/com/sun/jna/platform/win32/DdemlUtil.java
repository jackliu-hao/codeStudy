package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.win32.W32APIOptions;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DdemlUtil {
   public interface IDdeConnectionList extends Closeable {
      Ddeml.HCONVLIST getHandle();

      IDdeConnection queryNextServer(IDdeConnection var1);

      void close();
   }

   public interface IDdeClient extends Closeable {
      Integer getInstanceIdentitifier();

      void initialize(int var1) throws DdemlException;

      Ddeml.HSZ createStringHandle(String var1) throws DdemlException;

      String queryString(Ddeml.HSZ var1) throws DdemlException;

      boolean freeStringHandle(Ddeml.HSZ var1);

      boolean keepStringHandle(Ddeml.HSZ var1);

      void nameService(Ddeml.HSZ var1, int var2) throws DdemlException;

      void nameService(String var1, int var2) throws DdemlException;

      int getLastError();

      IDdeConnection connect(Ddeml.HSZ var1, Ddeml.HSZ var2, Ddeml.CONVCONTEXT var3);

      IDdeConnection connect(String var1, String var2, Ddeml.CONVCONTEXT var3);

      Ddeml.HDDEDATA createDataHandle(Pointer var1, int var2, int var3, Ddeml.HSZ var4, int var5, int var6);

      void freeDataHandle(Ddeml.HDDEDATA var1);

      Ddeml.HDDEDATA addData(Ddeml.HDDEDATA var1, Pointer var2, int var3, int var4);

      int getData(Ddeml.HDDEDATA var1, Pointer var2, int var3, int var4);

      Pointer accessData(Ddeml.HDDEDATA var1, WinDef.DWORDByReference var2);

      void unaccessData(Ddeml.HDDEDATA var1);

      void postAdvise(Ddeml.HSZ var1, Ddeml.HSZ var2);

      void postAdvise(String var1, String var2);

      void abandonTransactions();

      IDdeConnectionList connectList(Ddeml.HSZ var1, Ddeml.HSZ var2, IDdeConnectionList var3, Ddeml.CONVCONTEXT var4);

      IDdeConnectionList connectList(String var1, String var2, IDdeConnectionList var3, Ddeml.CONVCONTEXT var4);

      boolean enableCallback(int var1);

      boolean uninitialize();

      IDdeConnection wrap(Ddeml.HCONV var1);

      void registerAdvstartHandler(AdvstartHandler var1);

      void unregisterAdvstartHandler(AdvstartHandler var1);

      void registerAdvstopHandler(AdvstopHandler var1);

      void unregisterAdvstopHandler(AdvstopHandler var1);

      void registerConnectHandler(ConnectHandler var1);

      void unregisterConnectHandler(ConnectHandler var1);

      void registerAdvReqHandler(AdvreqHandler var1);

      void unregisterAdvReqHandler(AdvreqHandler var1);

      void registerRequestHandler(RequestHandler var1);

      void unregisterRequestHandler(RequestHandler var1);

      void registerWildconnectHandler(WildconnectHandler var1);

      void unregisterWildconnectHandler(WildconnectHandler var1);

      void registerAdvdataHandler(AdvdataHandler var1);

      void unregisterAdvdataHandler(AdvdataHandler var1);

      void registerExecuteHandler(ExecuteHandler var1);

      void unregisterExecuteHandler(ExecuteHandler var1);

      void registerPokeHandler(PokeHandler var1);

      void unregisterPokeHandler(PokeHandler var1);

      void registerConnectConfirmHandler(ConnectConfirmHandler var1);

      void unregisterConnectConfirmHandler(ConnectConfirmHandler var1);

      void registerDisconnectHandler(DisconnectHandler var1);

      void unregisterDisconnectHandler(DisconnectHandler var1);

      void registerErrorHandler(ErrorHandler var1);

      void unregisterErrorHandler(ErrorHandler var1);

      void registerRegisterHandler(RegisterHandler var1);

      void unregisterRegisterHandler(RegisterHandler var1);

      void registerXactCompleteHandler(XactCompleteHandler var1);

      void unregisterXactCompleteHandler(XactCompleteHandler var1);

      void registerUnregisterHandler(UnregisterHandler var1);

      void unregisterUnregisterHandler(UnregisterHandler var1);

      void registerMonitorHandler(MonitorHandler var1);

      void unregisterMonitorHandler(MonitorHandler var1);
   }

   public interface IDdeConnection extends Closeable {
      Ddeml.HCONV getConv();

      void execute(String var1, int var2, WinDef.DWORDByReference var3, BaseTSD.DWORD_PTR var4);

      void poke(Pointer var1, int var2, Ddeml.HSZ var3, int var4, int var5, WinDef.DWORDByReference var6, BaseTSD.DWORD_PTR var7);

      void poke(Pointer var1, int var2, String var3, int var4, int var5, WinDef.DWORDByReference var6, BaseTSD.DWORD_PTR var7);

      Ddeml.HDDEDATA request(Ddeml.HSZ var1, int var2, int var3, WinDef.DWORDByReference var4, BaseTSD.DWORD_PTR var5);

      Ddeml.HDDEDATA request(String var1, int var2, int var3, WinDef.DWORDByReference var4, BaseTSD.DWORD_PTR var5);

      Ddeml.HDDEDATA clientTransaction(Pointer var1, int var2, Ddeml.HSZ var3, int var4, int var5, int var6, WinDef.DWORDByReference var7, BaseTSD.DWORD_PTR var8);

      Ddeml.HDDEDATA clientTransaction(Pointer var1, int var2, String var3, int var4, int var5, int var6, WinDef.DWORDByReference var7, BaseTSD.DWORD_PTR var8);

      void advstart(Ddeml.HSZ var1, int var2, int var3, WinDef.DWORDByReference var4, BaseTSD.DWORD_PTR var5);

      void advstart(String var1, int var2, int var3, WinDef.DWORDByReference var4, BaseTSD.DWORD_PTR var5);

      void advstop(Ddeml.HSZ var1, int var2, int var3, WinDef.DWORDByReference var4, BaseTSD.DWORD_PTR var5);

      void advstop(String var1, int var2, int var3, WinDef.DWORDByReference var4, BaseTSD.DWORD_PTR var5);

      void abandonTransaction(int var1);

      void abandonTransactions();

      void impersonateClient();

      void close();

      void reconnect();

      boolean enableCallback(int var1);

      void setUserHandle(int var1, BaseTSD.DWORD_PTR var2) throws DdemlException;

      Ddeml.CONVINFO queryConvInfo(int var1) throws DdemlException;
   }

   public static class DdemlException extends RuntimeException {
      private static final Map<Integer, String> ERROR_CODE_MAP;
      private final int errorCode;

      public static DdemlException create(int errorCode) {
         String errorName = (String)ERROR_CODE_MAP.get(errorCode);
         return new DdemlException(errorCode, String.format("%s (Code: 0x%X)", errorName != null ? errorName : "", errorCode));
      }

      public DdemlException(int errorCode, String message) {
         super(message);
         this.errorCode = errorCode;
      }

      public int getErrorCode() {
         return this.errorCode;
      }

      static {
         Map<Integer, String> errorCodeMapBuilder = new HashMap();
         Field[] var1 = Ddeml.class.getFields();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Field f = var1[var3];
            String name = f.getName();
            if (name.startsWith("DMLERR_") && !name.equals("DMLERR_FIRST") && !name.equals("DMLERR_LAST")) {
               try {
                  errorCodeMapBuilder.put(f.getInt((Object)null), name);
               } catch (IllegalArgumentException var7) {
                  throw new RuntimeException(var7);
               } catch (IllegalAccessException var8) {
                  throw new RuntimeException(var8);
               }
            }
         }

         ERROR_CODE_MAP = Collections.unmodifiableMap(errorCodeMapBuilder);
      }
   }

   public static class DdeAdapter implements Ddeml.DdeCallback {
      private static final Logger LOG = Logger.getLogger(DdeAdapter.class.getName());
      private int idInst;
      private final List<AdvstartHandler> advstartHandler = new CopyOnWriteArrayList();
      private final List<AdvstopHandler> advstopHandler = new CopyOnWriteArrayList();
      private final List<ConnectHandler> connectHandler = new CopyOnWriteArrayList();
      private final List<AdvreqHandler> advReqHandler = new CopyOnWriteArrayList();
      private final List<RequestHandler> requestHandler = new CopyOnWriteArrayList();
      private final List<WildconnectHandler> wildconnectHandler = new CopyOnWriteArrayList();
      private final List<AdvdataHandler> advdataHandler = new CopyOnWriteArrayList();
      private final List<ExecuteHandler> executeHandler = new CopyOnWriteArrayList();
      private final List<PokeHandler> pokeHandler = new CopyOnWriteArrayList();
      private final List<ConnectConfirmHandler> connectConfirmHandler = new CopyOnWriteArrayList();
      private final List<DisconnectHandler> disconnectHandler = new CopyOnWriteArrayList();
      private final List<ErrorHandler> errorHandler = new CopyOnWriteArrayList();
      private final List<RegisterHandler> registerHandler = new CopyOnWriteArrayList();
      private final List<XactCompleteHandler> xactCompleteHandler = new CopyOnWriteArrayList();
      private final List<UnregisterHandler> unregisterHandler = new CopyOnWriteArrayList();
      private final List<MonitorHandler> monitorHandler = new CopyOnWriteArrayList();

      public void setInstanceIdentifier(int idInst) {
         this.idInst = idInst;
      }

      public WinDef.PVOID ddeCallback(int wType, int wFmt, Ddeml.HCONV hConv, Ddeml.HSZ hsz1, Ddeml.HSZ hsz2, Ddeml.HDDEDATA hData, BaseTSD.ULONG_PTR lData1, BaseTSD.ULONG_PTR lData2) {
         String transactionTypeName = null;

         try {
            boolean booleanResult;
            Ddeml.HDDEDATA data;
            Ddeml.CONVCONTEXT convcontext;
            int intResult;
            switch (wType) {
               case 4144:
                  booleanResult = this.onAdvstart(wType, wFmt, hConv, hsz1, hsz2);
                  return new WinDef.PVOID(Pointer.createConstant((new WinDef.BOOL(booleanResult)).intValue()));
               case 4194:
                  convcontext = null;
                  if (lData1.toPointer() != null) {
                     convcontext = new Ddeml.CONVCONTEXT(new Pointer(lData1.longValue()));
                  }

                  booleanResult = this.onConnect(wType, hsz1, hsz2, convcontext, lData2 != null && lData2.intValue() != 0);
                  return new WinDef.PVOID(Pointer.createConstant((new WinDef.BOOL(booleanResult)).intValue()));
               case 8226:
                  int count = lData1.intValue() & '\uffff';
                  data = this.onAdvreq(wType, wFmt, hConv, hsz1, hsz2, count);
                  if (data == null) {
                     return new WinDef.PVOID();
                  }

                  return new WinDef.PVOID(data.getPointer());
               case 8368:
                  data = this.onRequest(wType, wFmt, hConv, hsz1, hsz2);
                  if (data == null) {
                     return new WinDef.PVOID();
                  }

                  return new WinDef.PVOID(data.getPointer());
               case 8418:
                  convcontext = null;
                  if (lData1.toPointer() != null) {
                     convcontext = new Ddeml.CONVCONTEXT(new Pointer(lData1.longValue()));
                  }

                  Ddeml.HSZPAIR[] hszPairs = this.onWildconnect(wType, hsz1, hsz2, convcontext, lData2 != null && lData2.intValue() != 0);
                  if (hszPairs != null && hszPairs.length != 0) {
                     int size = 0;
                     Ddeml.HSZPAIR[] var17 = hszPairs;
                     int var18 = hszPairs.length;

                     for(int var19 = 0; var19 < var18; ++var19) {
                        Ddeml.HSZPAIR hp = var17[var19];
                        hp.write();
                        size += hp.size();
                     }

                     data = Ddeml.INSTANCE.DdeCreateDataHandle(this.idInst, hszPairs[0].getPointer(), size, 0, (Ddeml.HSZ)null, wFmt, 0);
                     return new WinDef.PVOID(data.getPointer());
                  }

                  return new WinDef.PVOID();
               case 16400:
                  intResult = this.onAdvdata(wType, wFmt, hConv, hsz1, hsz2, hData);
                  return new WinDef.PVOID(Pointer.createConstant(intResult));
               case 16464:
                  intResult = this.onExecute(wType, hConv, hsz1, hData);
                  Ddeml.INSTANCE.DdeFreeDataHandle(hData);
                  return new WinDef.PVOID(Pointer.createConstant(intResult));
               case 16528:
                  intResult = this.onPoke(wType, wFmt, hConv, hsz1, hsz2, hData);
                  return new WinDef.PVOID(Pointer.createConstant(intResult));
               case 32770:
                  this.onError(wType, hConv, (int)(lData2.longValue() & 65535L));
                  break;
               case 32832:
                  this.onAdvstop(wType, wFmt, hConv, hsz1, hsz2);
                  break;
               case 32882:
                  this.onConnectConfirm(wType, hConv, hsz1, hsz2, lData2 != null && lData2.intValue() != 0);
                  break;
               case 32896:
                  this.onXactComplete(wType, wFmt, hConv, hsz1, hsz2, hData, lData1, lData2);
                  break;
               case 32930:
                  this.onRegister(wType, hsz1, hsz2);
                  break;
               case 32962:
                  this.onDisconnect(wType, hConv, lData2 != null && lData2.intValue() != 0);
                  break;
               case 32978:
                  this.onUnregister(wType, hsz1, hsz2);
                  break;
               case 33010:
                  this.onMonitor(wType, hData, lData2.intValue());
                  break;
               default:
                  LOG.log(Level.FINE, String.format("Not implemented Operation - Transaction type: 0x%X (%s)", wType, transactionTypeName));
            }
         } catch (BlockException var21) {
            return new WinDef.PVOID(Pointer.createConstant(-1));
         } catch (Throwable var22) {
            LOG.log(Level.WARNING, "Exception in DDECallback", var22);
         }

         return new WinDef.PVOID();
      }

      public void registerAdvstartHandler(AdvstartHandler handler) {
         this.advstartHandler.add(handler);
      }

      public void unregisterAdvstartHandler(AdvstartHandler handler) {
         this.advstartHandler.remove(handler);
      }

      private boolean onAdvstart(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
         boolean oneHandlerTrue = false;
         Iterator var7 = this.advstartHandler.iterator();

         while(var7.hasNext()) {
            AdvstartHandler handler = (AdvstartHandler)var7.next();
            if (handler.onAdvstart(transactionType, dataFormat, hconv, topic, item)) {
               oneHandlerTrue = true;
            }
         }

         return oneHandlerTrue;
      }

      public void registerAdvstopHandler(AdvstopHandler handler) {
         this.advstopHandler.add(handler);
      }

      public void unregisterAdvstopHandler(AdvstopHandler handler) {
         this.advstopHandler.remove(handler);
      }

      private void onAdvstop(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
         Iterator var6 = this.advstopHandler.iterator();

         while(var6.hasNext()) {
            AdvstopHandler handler = (AdvstopHandler)var6.next();
            handler.onAdvstop(transactionType, dataFormat, hconv, topic, item);
         }

      }

      public void registerConnectHandler(ConnectHandler handler) {
         this.connectHandler.add(handler);
      }

      public void unregisterConnectHandler(ConnectHandler handler) {
         this.connectHandler.remove(handler);
      }

      private boolean onConnect(int transactionType, Ddeml.HSZ topic, Ddeml.HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
         boolean oneHandlerTrue = false;
         Iterator var7 = this.connectHandler.iterator();

         while(var7.hasNext()) {
            ConnectHandler handler = (ConnectHandler)var7.next();
            if (handler.onConnect(transactionType, topic, service, convcontext, sameInstance)) {
               oneHandlerTrue = true;
            }
         }

         return oneHandlerTrue;
      }

      public void registerAdvReqHandler(AdvreqHandler handler) {
         this.advReqHandler.add(handler);
      }

      public void unregisterAdvReqHandler(AdvreqHandler handler) {
         this.advReqHandler.remove(handler);
      }

      private Ddeml.HDDEDATA onAdvreq(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item, int count) {
         Iterator var7 = this.advReqHandler.iterator();

         Ddeml.HDDEDATA result;
         do {
            if (!var7.hasNext()) {
               return null;
            }

            AdvreqHandler handler = (AdvreqHandler)var7.next();
            result = handler.onAdvreq(transactionType, dataFormat, hconv, topic, item, count);
         } while(result == null);

         return result;
      }

      public void registerRequestHandler(RequestHandler handler) {
         this.requestHandler.add(handler);
      }

      public void unregisterRequestHandler(RequestHandler handler) {
         this.requestHandler.remove(handler);
      }

      private Ddeml.HDDEDATA onRequest(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
         Iterator var6 = this.requestHandler.iterator();

         Ddeml.HDDEDATA result;
         do {
            if (!var6.hasNext()) {
               return null;
            }

            RequestHandler handler = (RequestHandler)var6.next();
            result = handler.onRequest(transactionType, dataFormat, hconv, topic, item);
         } while(result == null);

         return result;
      }

      public void registerWildconnectHandler(WildconnectHandler handler) {
         this.wildconnectHandler.add(handler);
      }

      public void unregisterWildconnectHandler(WildconnectHandler handler) {
         this.wildconnectHandler.remove(handler);
      }

      private Ddeml.HSZPAIR[] onWildconnect(int transactionType, Ddeml.HSZ topic, Ddeml.HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
         List<Ddeml.HSZPAIR> hszpairs = new ArrayList(1);
         Iterator var7 = this.wildconnectHandler.iterator();

         while(var7.hasNext()) {
            WildconnectHandler handler = (WildconnectHandler)var7.next();
            hszpairs.addAll(handler.onWildconnect(transactionType, topic, service, convcontext, sameInstance));
         }

         return (Ddeml.HSZPAIR[])hszpairs.toArray(new Ddeml.HSZPAIR[0]);
      }

      public void registerAdvdataHandler(AdvdataHandler handler) {
         this.advdataHandler.add(handler);
      }

      public void unregisterAdvdataHandler(AdvdataHandler handler) {
         this.advdataHandler.remove(handler);
      }

      private int onAdvdata(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item, Ddeml.HDDEDATA hdata) {
         Iterator var7 = this.advdataHandler.iterator();

         int result;
         do {
            if (!var7.hasNext()) {
               return 0;
            }

            AdvdataHandler handler = (AdvdataHandler)var7.next();
            result = handler.onAdvdata(transactionType, dataFormat, hconv, topic, item, hdata);
         } while(result == 0);

         return result;
      }

      public void registerExecuteHandler(ExecuteHandler handler) {
         this.executeHandler.add(handler);
      }

      public void unregisterExecuteHandler(ExecuteHandler handler) {
         this.executeHandler.remove(handler);
      }

      private int onExecute(int transactionType, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HDDEDATA commandString) {
         Iterator var5 = this.executeHandler.iterator();

         int result;
         do {
            if (!var5.hasNext()) {
               return 0;
            }

            ExecuteHandler handler = (ExecuteHandler)var5.next();
            result = handler.onExecute(transactionType, hconv, topic, commandString);
         } while(result == 0);

         return result;
      }

      public void registerPokeHandler(PokeHandler handler) {
         this.pokeHandler.add(handler);
      }

      public void unregisterPokeHandler(PokeHandler handler) {
         this.pokeHandler.remove(handler);
      }

      private int onPoke(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item, Ddeml.HDDEDATA hdata) {
         Iterator var7 = this.pokeHandler.iterator();

         int result;
         do {
            if (!var7.hasNext()) {
               return 0;
            }

            PokeHandler handler = (PokeHandler)var7.next();
            result = handler.onPoke(transactionType, dataFormat, hconv, topic, item, hdata);
         } while(result == 0);

         return result;
      }

      public void registerConnectConfirmHandler(ConnectConfirmHandler handler) {
         this.connectConfirmHandler.add(handler);
      }

      public void unregisterConnectConfirmHandler(ConnectConfirmHandler handler) {
         this.connectConfirmHandler.remove(handler);
      }

      private void onConnectConfirm(int transactionType, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ service, boolean sameInstance) {
         Iterator var6 = this.connectConfirmHandler.iterator();

         while(var6.hasNext()) {
            ConnectConfirmHandler handler = (ConnectConfirmHandler)var6.next();
            handler.onConnectConfirm(transactionType, hconv, topic, service, sameInstance);
         }

      }

      public void registerDisconnectHandler(DisconnectHandler handler) {
         this.disconnectHandler.add(handler);
      }

      public void unregisterDisconnectHandler(DisconnectHandler handler) {
         this.disconnectHandler.remove(handler);
      }

      private void onDisconnect(int transactionType, Ddeml.HCONV hconv, boolean sameInstance) {
         Iterator var4 = this.disconnectHandler.iterator();

         while(var4.hasNext()) {
            DisconnectHandler handler = (DisconnectHandler)var4.next();
            handler.onDisconnect(transactionType, hconv, sameInstance);
         }

      }

      public void registerErrorHandler(ErrorHandler handler) {
         this.errorHandler.add(handler);
      }

      public void unregisterErrorHandler(ErrorHandler handler) {
         this.errorHandler.remove(handler);
      }

      private void onError(int transactionType, Ddeml.HCONV hconv, int errorCode) {
         Iterator var4 = this.errorHandler.iterator();

         while(var4.hasNext()) {
            ErrorHandler handler = (ErrorHandler)var4.next();
            handler.onError(transactionType, hconv, errorCode);
         }

      }

      public void registerRegisterHandler(RegisterHandler handler) {
         this.registerHandler.add(handler);
      }

      public void unregisterRegisterHandler(RegisterHandler handler) {
         this.registerHandler.remove(handler);
      }

      private void onRegister(int transactionType, Ddeml.HSZ baseServiceName, Ddeml.HSZ instanceSpecificServiceName) {
         Iterator var4 = this.registerHandler.iterator();

         while(var4.hasNext()) {
            RegisterHandler handler = (RegisterHandler)var4.next();
            handler.onRegister(transactionType, baseServiceName, instanceSpecificServiceName);
         }

      }

      public void registerXactCompleteHandler(XactCompleteHandler handler) {
         this.xactCompleteHandler.add(handler);
      }

      public void xactCompleteXactCompleteHandler(XactCompleteHandler handler) {
         this.xactCompleteHandler.remove(handler);
      }

      private void onXactComplete(int transactionType, int dataFormat, Ddeml.HCONV hConv, Ddeml.HSZ topic, Ddeml.HSZ item, Ddeml.HDDEDATA hdata, BaseTSD.ULONG_PTR transactionIdentifier, BaseTSD.ULONG_PTR statusFlag) {
         Iterator var9 = this.xactCompleteHandler.iterator();

         while(var9.hasNext()) {
            XactCompleteHandler handler = (XactCompleteHandler)var9.next();
            handler.onXactComplete(transactionType, dataFormat, hConv, topic, item, hdata, transactionIdentifier, statusFlag);
         }

      }

      public void registerUnregisterHandler(UnregisterHandler handler) {
         this.unregisterHandler.add(handler);
      }

      public void unregisterUnregisterHandler(UnregisterHandler handler) {
         this.unregisterHandler.remove(handler);
      }

      private void onUnregister(int transactionType, Ddeml.HSZ baseServiceName, Ddeml.HSZ instanceSpecificServiceName) {
         Iterator var4 = this.unregisterHandler.iterator();

         while(var4.hasNext()) {
            UnregisterHandler handler = (UnregisterHandler)var4.next();
            handler.onUnregister(transactionType, baseServiceName, instanceSpecificServiceName);
         }

      }

      public void registerMonitorHandler(MonitorHandler handler) {
         this.monitorHandler.add(handler);
      }

      public void unregisterMonitorHandler(MonitorHandler handler) {
         this.monitorHandler.remove(handler);
      }

      private void onMonitor(int transactionType, Ddeml.HDDEDATA hdata, int dwData2) {
         Iterator var4 = this.monitorHandler.iterator();

         while(var4.hasNext()) {
            MonitorHandler handler = (MonitorHandler)var4.next();
            handler.onMonitor(transactionType, hdata, dwData2);
         }

      }

      public static class BlockException extends RuntimeException {
      }
   }

   public interface MonitorHandler {
      void onMonitor(int var1, Ddeml.HDDEDATA var2, int var3);
   }

   public interface PokeHandler {
      int onPoke(int var1, int var2, Ddeml.HCONV var3, Ddeml.HSZ var4, Ddeml.HSZ var5, Ddeml.HDDEDATA var6);
   }

   public interface ExecuteHandler {
      int onExecute(int var1, Ddeml.HCONV var2, Ddeml.HSZ var3, Ddeml.HDDEDATA var4);
   }

   public interface UnregisterHandler {
      void onUnregister(int var1, Ddeml.HSZ var2, Ddeml.HSZ var3);
   }

   public interface XactCompleteHandler {
      void onXactComplete(int var1, int var2, Ddeml.HCONV var3, Ddeml.HSZ var4, Ddeml.HSZ var5, Ddeml.HDDEDATA var6, BaseTSD.ULONG_PTR var7, BaseTSD.ULONG_PTR var8);
   }

   public interface RegisterHandler {
      void onRegister(int var1, Ddeml.HSZ var2, Ddeml.HSZ var3);
   }

   public interface ErrorHandler {
      void onError(int var1, Ddeml.HCONV var2, int var3);
   }

   public interface DisconnectHandler {
      void onDisconnect(int var1, Ddeml.HCONV var2, boolean var3);
   }

   public interface ConnectConfirmHandler {
      void onConnectConfirm(int var1, Ddeml.HCONV var2, Ddeml.HSZ var3, Ddeml.HSZ var4, boolean var5);
   }

   public interface AdvdataHandler {
      int onAdvdata(int var1, int var2, Ddeml.HCONV var3, Ddeml.HSZ var4, Ddeml.HSZ var5, Ddeml.HDDEDATA var6);
   }

   public interface WildconnectHandler {
      List<Ddeml.HSZPAIR> onWildconnect(int var1, Ddeml.HSZ var2, Ddeml.HSZ var3, Ddeml.CONVCONTEXT var4, boolean var5);
   }

   public interface RequestHandler {
      Ddeml.HDDEDATA onRequest(int var1, int var2, Ddeml.HCONV var3, Ddeml.HSZ var4, Ddeml.HSZ var5);
   }

   public interface AdvreqHandler {
      Ddeml.HDDEDATA onAdvreq(int var1, int var2, Ddeml.HCONV var3, Ddeml.HSZ var4, Ddeml.HSZ var5, int var6);
   }

   public interface ConnectHandler {
      boolean onConnect(int var1, Ddeml.HSZ var2, Ddeml.HSZ var3, Ddeml.CONVCONTEXT var4, boolean var5);
   }

   public interface AdvstopHandler {
      void onAdvstop(int var1, int var2, Ddeml.HCONV var3, Ddeml.HSZ var4, Ddeml.HSZ var5);
   }

   public interface AdvstartHandler {
      boolean onAdvstart(int var1, int var2, Ddeml.HCONV var3, Ddeml.HSZ var4, Ddeml.HSZ var5);
   }

   public static class DdeClient implements IDdeClient {
      private Integer idInst;
      private final DdeAdapter ddeAdapter = new DdeAdapter();

      public Integer getInstanceIdentitifier() {
         return this.idInst;
      }

      public void initialize(int afCmd) throws DdemlException {
         WinDef.DWORDByReference pidInst = new WinDef.DWORDByReference();
         Integer result = Ddeml.INSTANCE.DdeInitialize(pidInst, this.ddeAdapter, afCmd, 0);
         if (result != 0) {
            throw DdemlUtil.DdemlException.create(result);
         } else {
            this.idInst = pidInst.getValue().intValue();
            if (this.ddeAdapter instanceof DdeAdapter) {
               this.ddeAdapter.setInstanceIdentifier(this.idInst);
            }

         }
      }

      public Ddeml.HSZ createStringHandle(String value) throws DdemlException {
         if (value == null) {
            return null;
         } else {
            short codePage;
            if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
               codePage = 1200;
            } else {
               codePage = 1004;
            }

            Ddeml.HSZ handle = Ddeml.INSTANCE.DdeCreateStringHandle(this.idInst, value, codePage);
            if (handle == null) {
               throw DdemlUtil.DdemlException.create(this.getLastError());
            } else {
               return handle;
            }
         }
      }

      public void nameService(Ddeml.HSZ name, int afCmd) throws DdemlException {
         Ddeml.HDDEDATA handle = Ddeml.INSTANCE.DdeNameService(this.idInst, name, new Ddeml.HSZ(), afCmd);
         if (handle == null) {
            throw DdemlUtil.DdemlException.create(this.getLastError());
         }
      }

      public void nameService(String name, int afCmd) throws DdemlException {
         Ddeml.HSZ nameHSZ = null;

         try {
            nameHSZ = this.createStringHandle(name);
            this.nameService(nameHSZ, afCmd);
         } finally {
            this.freeStringHandle(nameHSZ);
         }

      }

      public int getLastError() {
         return Ddeml.INSTANCE.DdeGetLastError(this.idInst);
      }

      public IDdeConnection connect(Ddeml.HSZ service, Ddeml.HSZ topic, Ddeml.CONVCONTEXT convcontext) {
         Ddeml.HCONV hconv = Ddeml.INSTANCE.DdeConnect(this.idInst, service, topic, convcontext);
         if (hconv == null) {
            throw DdemlUtil.DdemlException.create(this.getLastError());
         } else {
            return new DdeConnection(this, hconv);
         }
      }

      public IDdeConnection connect(String service, String topic, Ddeml.CONVCONTEXT convcontext) {
         Ddeml.HSZ serviceHSZ = null;
         Ddeml.HSZ topicHSZ = null;

         IDdeConnection var6;
         try {
            serviceHSZ = this.createStringHandle(service);
            topicHSZ = this.createStringHandle(topic);
            var6 = this.connect(serviceHSZ, topicHSZ, convcontext);
         } finally {
            this.freeStringHandle(topicHSZ);
            this.freeStringHandle(serviceHSZ);
         }

         return var6;
      }

      public String queryString(Ddeml.HSZ value) throws DdemlException {
         short codePage;
         byte byteWidth;
         if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
            codePage = 1200;
            byteWidth = 2;
         } else {
            codePage = 1004;
            byteWidth = 1;
         }

         Memory buffer = new Memory((long)(257 * byteWidth));

         String var6;
         try {
            Ddeml.INSTANCE.DdeQueryString(this.idInst, value, buffer, 256, codePage);
            if (W32APIOptions.DEFAULT_OPTIONS != W32APIOptions.UNICODE_OPTIONS) {
               var6 = buffer.getString(0L);
               return var6;
            }

            var6 = buffer.getWideString(0L);
         } finally {
            buffer.valid();
         }

         return var6;
      }

      public Ddeml.HDDEDATA createDataHandle(Pointer pSrc, int cb, int cbOff, Ddeml.HSZ hszItem, int wFmt, int afCmd) {
         Ddeml.HDDEDATA returnData = Ddeml.INSTANCE.DdeCreateDataHandle(this.idInst, pSrc, cb, cbOff, hszItem, wFmt, afCmd);
         if (returnData == null) {
            throw DdemlUtil.DdemlException.create(this.getLastError());
         } else {
            return returnData;
         }
      }

      public void freeDataHandle(Ddeml.HDDEDATA hData) {
         boolean result = Ddeml.INSTANCE.DdeFreeDataHandle(hData);
         if (!result) {
            throw DdemlUtil.DdemlException.create(this.getLastError());
         }
      }

      public Ddeml.HDDEDATA addData(Ddeml.HDDEDATA hData, Pointer pSrc, int cb, int cbOff) {
         Ddeml.HDDEDATA newHandle = Ddeml.INSTANCE.DdeAddData(hData, pSrc, cb, cbOff);
         if (newHandle == null) {
            throw DdemlUtil.DdemlException.create(this.getLastError());
         } else {
            return newHandle;
         }
      }

      public int getData(Ddeml.HDDEDATA hData, Pointer pDst, int cbMax, int cbOff) {
         int result = Ddeml.INSTANCE.DdeGetData(hData, pDst, cbMax, cbOff);
         int errorCode = this.getLastError();
         if (errorCode != 0) {
            throw DdemlUtil.DdemlException.create(errorCode);
         } else {
            return result;
         }
      }

      public Pointer accessData(Ddeml.HDDEDATA hData, WinDef.DWORDByReference pcbDataSize) {
         Pointer result = Ddeml.INSTANCE.DdeAccessData(hData, pcbDataSize);
         if (result == null) {
            throw DdemlUtil.DdemlException.create(this.getLastError());
         } else {
            return result;
         }
      }

      public void unaccessData(Ddeml.HDDEDATA hData) {
         boolean result = Ddeml.INSTANCE.DdeUnaccessData(hData);
         if (!result) {
            throw DdemlUtil.DdemlException.create(this.getLastError());
         }
      }

      public void postAdvise(Ddeml.HSZ hszTopic, Ddeml.HSZ hszItem) {
         boolean result = Ddeml.INSTANCE.DdePostAdvise(this.idInst, hszTopic, hszItem);
         if (!result) {
            throw DdemlUtil.DdemlException.create(this.getLastError());
         }
      }

      public void postAdvise(String topic, String item) {
         Ddeml.HSZ itemHSZ = null;
         Ddeml.HSZ topicHSZ = null;

         try {
            topicHSZ = this.createStringHandle(topic);
            itemHSZ = this.createStringHandle(item);
            this.postAdvise(topicHSZ, itemHSZ);
         } finally {
            this.freeStringHandle(topicHSZ);
            this.freeStringHandle(itemHSZ);
         }

      }

      public boolean freeStringHandle(Ddeml.HSZ value) {
         return value == null ? true : Ddeml.INSTANCE.DdeFreeStringHandle(this.idInst, value);
      }

      public boolean keepStringHandle(Ddeml.HSZ value) {
         return Ddeml.INSTANCE.DdeKeepStringHandle(this.idInst, value);
      }

      public void abandonTransactions() {
         boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(this.idInst, (Ddeml.HCONV)null, 0);
         if (!result) {
            throw DdemlUtil.DdemlException.create(this.getLastError());
         }
      }

      public IDdeConnectionList connectList(Ddeml.HSZ service, Ddeml.HSZ topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
         Ddeml.HCONVLIST convlist = Ddeml.INSTANCE.DdeConnectList(this.idInst, service, topic, existingList != null ? existingList.getHandle() : null, ctx);
         if (convlist == null) {
            throw DdemlUtil.DdemlException.create(this.getLastError());
         } else {
            return new DdeConnectionList(this, convlist);
         }
      }

      public IDdeConnectionList connectList(String service, String topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
         Ddeml.HSZ serviceHSZ = null;
         Ddeml.HSZ topicHSZ = null;

         IDdeConnectionList var7;
         try {
            serviceHSZ = this.createStringHandle(service);
            topicHSZ = this.createStringHandle(topic);
            var7 = this.connectList(serviceHSZ, topicHSZ, existingList, ctx);
         } finally {
            this.freeStringHandle(topicHSZ);
            this.freeStringHandle(serviceHSZ);
         }

         return var7;
      }

      public boolean enableCallback(int wCmd) {
         boolean result = Ddeml.INSTANCE.DdeEnableCallback(this.idInst, (Ddeml.HCONV)null, wCmd);
         if (!result && wCmd != 2) {
            int errorCode = this.getLastError();
            if (errorCode != 0) {
               throw DdemlUtil.DdemlException.create(this.getLastError());
            }
         }

         return result;
      }

      public boolean uninitialize() {
         return Ddeml.INSTANCE.DdeUninitialize(this.idInst);
      }

      public void close() {
         this.uninitialize();
      }

      public IDdeConnection wrap(Ddeml.HCONV hconv) {
         return new DdeConnection(this, hconv);
      }

      public void unregisterDisconnectHandler(DisconnectHandler handler) {
         this.ddeAdapter.unregisterDisconnectHandler(handler);
      }

      public void registerAdvstartHandler(AdvstartHandler handler) {
         this.ddeAdapter.registerAdvstartHandler(handler);
      }

      public void unregisterAdvstartHandler(AdvstartHandler handler) {
         this.ddeAdapter.unregisterAdvstartHandler(handler);
      }

      public void registerAdvstopHandler(AdvstopHandler handler) {
         this.ddeAdapter.registerAdvstopHandler(handler);
      }

      public void unregisterAdvstopHandler(AdvstopHandler handler) {
         this.ddeAdapter.unregisterAdvstopHandler(handler);
      }

      public void registerConnectHandler(ConnectHandler handler) {
         this.ddeAdapter.registerConnectHandler(handler);
      }

      public void unregisterConnectHandler(ConnectHandler handler) {
         this.ddeAdapter.unregisterConnectHandler(handler);
      }

      public void registerAdvReqHandler(AdvreqHandler handler) {
         this.ddeAdapter.registerAdvReqHandler(handler);
      }

      public void unregisterAdvReqHandler(AdvreqHandler handler) {
         this.ddeAdapter.unregisterAdvReqHandler(handler);
      }

      public void registerRequestHandler(RequestHandler handler) {
         this.ddeAdapter.registerRequestHandler(handler);
      }

      public void unregisterRequestHandler(RequestHandler handler) {
         this.ddeAdapter.unregisterRequestHandler(handler);
      }

      public void registerWildconnectHandler(WildconnectHandler handler) {
         this.ddeAdapter.registerWildconnectHandler(handler);
      }

      public void unregisterWildconnectHandler(WildconnectHandler handler) {
         this.ddeAdapter.unregisterWildconnectHandler(handler);
      }

      public void registerAdvdataHandler(AdvdataHandler handler) {
         this.ddeAdapter.registerAdvdataHandler(handler);
      }

      public void unregisterAdvdataHandler(AdvdataHandler handler) {
         this.ddeAdapter.unregisterAdvdataHandler(handler);
      }

      public void registerExecuteHandler(ExecuteHandler handler) {
         this.ddeAdapter.registerExecuteHandler(handler);
      }

      public void unregisterExecuteHandler(ExecuteHandler handler) {
         this.ddeAdapter.unregisterExecuteHandler(handler);
      }

      public void registerPokeHandler(PokeHandler handler) {
         this.ddeAdapter.registerPokeHandler(handler);
      }

      public void unregisterPokeHandler(PokeHandler handler) {
         this.ddeAdapter.unregisterPokeHandler(handler);
      }

      public void registerConnectConfirmHandler(ConnectConfirmHandler handler) {
         this.ddeAdapter.registerConnectConfirmHandler(handler);
      }

      public void unregisterConnectConfirmHandler(ConnectConfirmHandler handler) {
         this.ddeAdapter.unregisterConnectConfirmHandler(handler);
      }

      public void registerDisconnectHandler(DisconnectHandler handler) {
         this.ddeAdapter.registerDisconnectHandler(handler);
      }

      public void registerErrorHandler(ErrorHandler handler) {
         this.ddeAdapter.registerErrorHandler(handler);
      }

      public void unregisterErrorHandler(ErrorHandler handler) {
         this.ddeAdapter.unregisterErrorHandler(handler);
      }

      public void registerRegisterHandler(RegisterHandler handler) {
         this.ddeAdapter.registerRegisterHandler(handler);
      }

      public void unregisterRegisterHandler(RegisterHandler handler) {
         this.ddeAdapter.unregisterRegisterHandler(handler);
      }

      public void registerXactCompleteHandler(XactCompleteHandler handler) {
         this.ddeAdapter.registerXactCompleteHandler(handler);
      }

      public void unregisterXactCompleteHandler(XactCompleteHandler handler) {
         this.ddeAdapter.xactCompleteXactCompleteHandler(handler);
      }

      public void registerUnregisterHandler(UnregisterHandler handler) {
         this.ddeAdapter.registerUnregisterHandler(handler);
      }

      public void unregisterUnregisterHandler(UnregisterHandler handler) {
         this.ddeAdapter.unregisterUnregisterHandler(handler);
      }

      public void registerMonitorHandler(MonitorHandler handler) {
         this.ddeAdapter.registerMonitorHandler(handler);
      }

      public void unregisterMonitorHandler(MonitorHandler handler) {
         this.ddeAdapter.unregisterMonitorHandler(handler);
      }
   }

   public static class DdeConnectionList implements IDdeConnectionList {
      private final IDdeClient client;
      private final Ddeml.HCONVLIST convList;

      public DdeConnectionList(IDdeClient client, Ddeml.HCONVLIST convList) {
         this.convList = convList;
         this.client = client;
      }

      public Ddeml.HCONVLIST getHandle() {
         return this.convList;
      }

      public IDdeConnection queryNextServer(IDdeConnection prevConnection) {
         Ddeml.HCONV conv = Ddeml.INSTANCE.DdeQueryNextServer(this.convList, prevConnection != null ? prevConnection.getConv() : null);
         return conv != null ? new DdeConnection(this.client, conv) : null;
      }

      public void close() {
         boolean result = Ddeml.INSTANCE.DdeDisconnectList(this.convList);
         if (!result) {
            throw DdemlUtil.DdemlException.create(this.client.getLastError());
         }
      }
   }

   public static class DdeConnection implements IDdeConnection {
      private Ddeml.HCONV conv;
      private final IDdeClient client;

      public DdeConnection(IDdeClient client, Ddeml.HCONV conv) {
         this.conv = conv;
         this.client = client;
      }

      public Ddeml.HCONV getConv() {
         return this.conv;
      }

      public void abandonTransaction(int transactionId) {
         boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(this.client.getInstanceIdentitifier(), this.conv, transactionId);
         if (!result) {
            throw DdemlUtil.DdemlException.create(this.client.getLastError());
         }
      }

      public void abandonTransactions() {
         boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(this.client.getInstanceIdentitifier(), this.conv, 0);
         if (!result) {
            throw DdemlUtil.DdemlException.create(this.client.getLastError());
         }
      }

      public Ddeml.HDDEDATA clientTransaction(Pointer data, int dataLength, Ddeml.HSZ item, int wFmt, int transaction, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
         if (timeout == -1 && result == null) {
            result = new WinDef.DWORDByReference();
         }

         Ddeml.HDDEDATA returnData = Ddeml.INSTANCE.DdeClientTransaction(data, dataLength, this.conv, item, wFmt, transaction, timeout, result);
         if (returnData == null) {
            throw DdemlUtil.DdemlException.create(this.client.getLastError());
         } else {
            if (userHandle != null) {
               if (timeout != -1) {
                  this.setUserHandle(-1, userHandle);
               } else {
                  this.setUserHandle(result.getValue().intValue(), userHandle);
               }
            }

            return returnData;
         }
      }

      public Ddeml.HDDEDATA clientTransaction(Pointer data, int dataLength, String item, int wFmt, int transaction, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
         Ddeml.HSZ itemHSZ = null;

         Ddeml.HDDEDATA var10;
         try {
            itemHSZ = this.client.createStringHandle(item);
            var10 = this.clientTransaction(data, dataLength, itemHSZ, wFmt, transaction, timeout, result, userHandle);
         } finally {
            this.client.freeStringHandle(itemHSZ);
         }

         return var10;
      }

      public void poke(Pointer data, int dataLength, Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
         this.clientTransaction(data, dataLength, (Ddeml.HSZ)item, wFmt, 16528, timeout, result, userHandle);
      }

      public void poke(Pointer data, int dataLength, String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
         Ddeml.HSZ itemHSZ = null;

         try {
            itemHSZ = this.client.createStringHandle(item);
            this.poke(data, dataLength, itemHSZ, wFmt, timeout, result, userHandle);
         } finally {
            this.client.freeStringHandle(itemHSZ);
         }

      }

      public Ddeml.HDDEDATA request(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
         return this.clientTransaction(Pointer.NULL, 0, (Ddeml.HSZ)item, wFmt, 8368, timeout, result, userHandle);
      }

      public Ddeml.HDDEDATA request(String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
         Ddeml.HSZ itemHSZ = null;

         Ddeml.HDDEDATA var7;
         try {
            itemHSZ = this.client.createStringHandle(item);
            var7 = this.request(itemHSZ, wFmt, timeout, result, userHandle);
         } finally {
            this.client.freeStringHandle(itemHSZ);
         }

         return var7;
      }

      public void execute(String executeString, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
         Memory mem = new Memory((long)(executeString.length() * 2 + 2));
         mem.setWideString(0L, executeString);
         this.clientTransaction(mem, (int)mem.size(), (Ddeml.HSZ)((Ddeml.HSZ)null), 0, 16464, timeout, result, userHandle);
      }

      public void advstart(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
         this.clientTransaction(Pointer.NULL, 0, (Ddeml.HSZ)item, wFmt, 4144, timeout, result, userHandle);
      }

      public void advstart(String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
         Ddeml.HSZ itemHSZ = null;

         try {
            itemHSZ = this.client.createStringHandle(item);
            this.advstart(itemHSZ, wFmt, timeout, result, userHandle);
         } finally {
            this.client.freeStringHandle(itemHSZ);
         }

      }

      public void advstop(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
         this.clientTransaction(Pointer.NULL, 0, (Ddeml.HSZ)item, wFmt, 32832, timeout, result, userHandle);
      }

      public void advstop(String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
         Ddeml.HSZ itemHSZ = null;

         try {
            itemHSZ = this.client.createStringHandle(item);
            this.advstop(itemHSZ, wFmt, timeout, result, userHandle);
         } finally {
            this.client.freeStringHandle(itemHSZ);
         }

      }

      public void impersonateClient() {
         boolean result = Ddeml.INSTANCE.DdeImpersonateClient(this.conv);
         if (!result) {
            throw DdemlUtil.DdemlException.create(this.client.getLastError());
         }
      }

      public void close() {
         boolean result = Ddeml.INSTANCE.DdeDisconnect(this.conv);
         if (!result) {
            throw DdemlUtil.DdemlException.create(this.client.getLastError());
         }
      }

      public void reconnect() {
         Ddeml.HCONV newConv = Ddeml.INSTANCE.DdeReconnect(this.conv);
         if (newConv != null) {
            this.conv = newConv;
         } else {
            throw DdemlUtil.DdemlException.create(this.client.getLastError());
         }
      }

      public boolean enableCallback(int wCmd) {
         boolean result = Ddeml.INSTANCE.DdeEnableCallback(this.client.getInstanceIdentitifier(), this.conv, wCmd);
         if (!result && wCmd == 2) {
            throw DdemlUtil.DdemlException.create(this.client.getLastError());
         } else {
            return result;
         }
      }

      public void setUserHandle(int id, BaseTSD.DWORD_PTR hUser) throws DdemlException {
         boolean result = Ddeml.INSTANCE.DdeSetUserHandle(this.conv, id, hUser);
         if (!result) {
            throw DdemlUtil.DdemlException.create(this.client.getLastError());
         }
      }

      public Ddeml.CONVINFO queryConvInfo(int idTransaction) throws DdemlException {
         Ddeml.CONVINFO convInfo = new Ddeml.CONVINFO();
         convInfo.cb = convInfo.size();
         convInfo.ConvCtxt.cb = convInfo.ConvCtxt.size();
         convInfo.write();
         int result = Ddeml.INSTANCE.DdeQueryConvInfo(this.conv, idTransaction, convInfo);
         if (result == 0) {
            throw DdemlUtil.DdemlException.create(this.client.getLastError());
         } else {
            return convInfo;
         }
      }
   }

   private static class MessageLoopWrapper implements InvocationHandler {
      private final Object delegate;
      private final User32Util.MessageLoopThread loopThread;

      public MessageLoopWrapper(User32Util.MessageLoopThread thread, Object delegate) {
         this.loopThread = thread;
         this.delegate = delegate;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         try {
            Object result = method.invoke(this.delegate, args);
            Class<?> wrapClass = null;
            if (result instanceof IDdeConnection) {
               wrapClass = IDdeConnection.class;
            } else if (result instanceof IDdeConnectionList) {
               wrapClass = IDdeConnectionList.class;
            } else if (result instanceof IDdeClient) {
               wrapClass = IDdeClient.class;
            }

            if (wrapClass != null && method.getReturnType().isAssignableFrom(wrapClass)) {
               result = this.wrap(result, wrapClass);
            }

            return result;
         } catch (InvocationTargetException var6) {
            Throwable cause = var6.getCause();
            if (cause instanceof Exception) {
               throw (Exception)cause;
            } else {
               throw var6;
            }
         }
      }

      private <V> V wrap(V delegate, Class clazz) {
         V messageLoopHandler = Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[]{clazz}, this.loopThread.new Handler(delegate));
         V clientDelegate = Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[]{clazz}, new MessageLoopWrapper(this.loopThread, messageLoopHandler));
         return clientDelegate;
      }
   }

   public static class StandaloneDdeClient implements IDdeClient, Closeable {
      private final User32Util.MessageLoopThread messageLoop = new User32Util.MessageLoopThread();
      private final IDdeClient ddeClient = new DdeClient();
      private final IDdeClient clientDelegate;

      public StandaloneDdeClient() {
         IDdeClient messageLoopHandler = (IDdeClient)Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[]{IDdeClient.class}, this.messageLoop.new Handler(this.ddeClient));
         this.clientDelegate = (IDdeClient)Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[]{IDdeClient.class}, new MessageLoopWrapper(this.messageLoop, messageLoopHandler));
         this.messageLoop.setDaemon(true);
         this.messageLoop.start();
      }

      public Integer getInstanceIdentitifier() {
         return this.ddeClient.getInstanceIdentitifier();
      }

      public void initialize(int afCmd) throws DdemlException {
         this.clientDelegate.initialize(afCmd);
      }

      public Ddeml.HSZ createStringHandle(String value) throws DdemlException {
         return this.clientDelegate.createStringHandle(value);
      }

      public void nameService(Ddeml.HSZ name, int afCmd) throws DdemlException {
         this.clientDelegate.nameService(name, afCmd);
      }

      public int getLastError() {
         return this.clientDelegate.getLastError();
      }

      public IDdeConnection connect(Ddeml.HSZ service, Ddeml.HSZ topic, Ddeml.CONVCONTEXT convcontext) {
         return this.clientDelegate.connect(service, topic, convcontext);
      }

      public String queryString(Ddeml.HSZ value) throws DdemlException {
         return this.clientDelegate.queryString(value);
      }

      public Ddeml.HDDEDATA createDataHandle(Pointer pSrc, int cb, int cbOff, Ddeml.HSZ hszItem, int wFmt, int afCmd) {
         return this.clientDelegate.createDataHandle(pSrc, cb, cbOff, hszItem, wFmt, afCmd);
      }

      public void freeDataHandle(Ddeml.HDDEDATA hData) {
         this.clientDelegate.freeDataHandle(hData);
      }

      public Ddeml.HDDEDATA addData(Ddeml.HDDEDATA hData, Pointer pSrc, int cb, int cbOff) {
         return this.clientDelegate.addData(hData, pSrc, cb, cbOff);
      }

      public int getData(Ddeml.HDDEDATA hData, Pointer pDst, int cbMax, int cbOff) {
         return this.clientDelegate.getData(hData, pDst, cbMax, cbOff);
      }

      public Pointer accessData(Ddeml.HDDEDATA hData, WinDef.DWORDByReference pcbDataSize) {
         return this.clientDelegate.accessData(hData, pcbDataSize);
      }

      public void unaccessData(Ddeml.HDDEDATA hData) {
         this.clientDelegate.unaccessData(hData);
      }

      public void postAdvise(Ddeml.HSZ hszTopic, Ddeml.HSZ hszItem) {
         this.clientDelegate.postAdvise(hszTopic, hszItem);
      }

      public void close() throws IOException {
         this.clientDelegate.uninitialize();
         this.messageLoop.exit();
      }

      public boolean freeStringHandle(Ddeml.HSZ value) {
         return this.clientDelegate.freeStringHandle(value);
      }

      public boolean keepStringHandle(Ddeml.HSZ value) {
         return this.clientDelegate.keepStringHandle(value);
      }

      public void abandonTransactions() {
         this.clientDelegate.abandonTransactions();
      }

      public IDdeConnectionList connectList(Ddeml.HSZ service, Ddeml.HSZ topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
         return this.clientDelegate.connectList(service, topic, existingList, ctx);
      }

      public boolean enableCallback(int wCmd) {
         return this.clientDelegate.enableCallback(wCmd);
      }

      public IDdeConnection wrap(Ddeml.HCONV conv) {
         return this.clientDelegate.wrap(conv);
      }

      public IDdeConnection connect(String service, String topic, Ddeml.CONVCONTEXT convcontext) {
         return this.clientDelegate.connect(service, topic, convcontext);
      }

      public boolean uninitialize() {
         return this.clientDelegate.uninitialize();
      }

      public void postAdvise(String hszTopic, String hszItem) {
         this.clientDelegate.postAdvise(hszTopic, hszItem);
      }

      public IDdeConnectionList connectList(String service, String topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
         return this.clientDelegate.connectList(service, topic, existingList, ctx);
      }

      public void nameService(String name, int afCmd) throws DdemlException {
         this.clientDelegate.nameService(name, afCmd);
      }

      public void registerAdvstartHandler(AdvstartHandler handler) {
         this.clientDelegate.registerAdvstartHandler(handler);
      }

      public void unregisterAdvstartHandler(AdvstartHandler handler) {
         this.clientDelegate.unregisterAdvstartHandler(handler);
      }

      public void registerAdvstopHandler(AdvstopHandler handler) {
         this.clientDelegate.registerAdvstopHandler(handler);
      }

      public void unregisterAdvstopHandler(AdvstopHandler handler) {
         this.clientDelegate.unregisterAdvstopHandler(handler);
      }

      public void registerConnectHandler(ConnectHandler handler) {
         this.clientDelegate.registerConnectHandler(handler);
      }

      public void unregisterConnectHandler(ConnectHandler handler) {
         this.clientDelegate.unregisterConnectHandler(handler);
      }

      public void registerAdvReqHandler(AdvreqHandler handler) {
         this.clientDelegate.registerAdvReqHandler(handler);
      }

      public void unregisterAdvReqHandler(AdvreqHandler handler) {
         this.clientDelegate.unregisterAdvReqHandler(handler);
      }

      public void registerRequestHandler(RequestHandler handler) {
         this.clientDelegate.registerRequestHandler(handler);
      }

      public void unregisterRequestHandler(RequestHandler handler) {
         this.clientDelegate.unregisterRequestHandler(handler);
      }

      public void registerWildconnectHandler(WildconnectHandler handler) {
         this.clientDelegate.registerWildconnectHandler(handler);
      }

      public void unregisterWildconnectHandler(WildconnectHandler handler) {
         this.clientDelegate.unregisterWildconnectHandler(handler);
      }

      public void registerAdvdataHandler(AdvdataHandler handler) {
         this.clientDelegate.registerAdvdataHandler(handler);
      }

      public void unregisterAdvdataHandler(AdvdataHandler handler) {
         this.clientDelegate.unregisterAdvdataHandler(handler);
      }

      public void registerExecuteHandler(ExecuteHandler handler) {
         this.clientDelegate.registerExecuteHandler(handler);
      }

      public void unregisterExecuteHandler(ExecuteHandler handler) {
         this.clientDelegate.unregisterExecuteHandler(handler);
      }

      public void registerPokeHandler(PokeHandler handler) {
         this.clientDelegate.registerPokeHandler(handler);
      }

      public void unregisterPokeHandler(PokeHandler handler) {
         this.clientDelegate.unregisterPokeHandler(handler);
      }

      public void registerConnectConfirmHandler(ConnectConfirmHandler handler) {
         this.clientDelegate.registerConnectConfirmHandler(handler);
      }

      public void unregisterConnectConfirmHandler(ConnectConfirmHandler handler) {
         this.clientDelegate.unregisterConnectConfirmHandler(handler);
      }

      public void registerDisconnectHandler(DisconnectHandler handler) {
         this.clientDelegate.registerDisconnectHandler(handler);
      }

      public void unregisterDisconnectHandler(DisconnectHandler handler) {
         this.clientDelegate.unregisterDisconnectHandler(handler);
      }

      public void registerErrorHandler(ErrorHandler handler) {
         this.clientDelegate.registerErrorHandler(handler);
      }

      public void unregisterErrorHandler(ErrorHandler handler) {
         this.clientDelegate.unregisterErrorHandler(handler);
      }

      public void registerRegisterHandler(RegisterHandler handler) {
         this.clientDelegate.registerRegisterHandler(handler);
      }

      public void unregisterRegisterHandler(RegisterHandler handler) {
         this.clientDelegate.unregisterRegisterHandler(handler);
      }

      public void registerXactCompleteHandler(XactCompleteHandler handler) {
         this.clientDelegate.registerXactCompleteHandler(handler);
      }

      public void unregisterXactCompleteHandler(XactCompleteHandler handler) {
         this.clientDelegate.unregisterXactCompleteHandler(handler);
      }

      public void registerUnregisterHandler(UnregisterHandler handler) {
         this.clientDelegate.registerUnregisterHandler(handler);
      }

      public void unregisterUnregisterHandler(UnregisterHandler handler) {
         this.clientDelegate.unregisterUnregisterHandler(handler);
      }

      public void registerMonitorHandler(MonitorHandler handler) {
         this.clientDelegate.registerMonitorHandler(handler);
      }

      public void unregisterMonitorHandler(MonitorHandler handler) {
         this.clientDelegate.unregisterMonitorHandler(handler);
      }
   }
}
