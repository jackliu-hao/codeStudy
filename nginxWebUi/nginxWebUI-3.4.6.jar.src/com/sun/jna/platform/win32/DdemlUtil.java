/*      */ package com.sun.jna.platform.win32;
/*      */ 
/*      */ import com.sun.jna.Memory;
/*      */ import com.sun.jna.Pointer;
/*      */ import com.sun.jna.win32.W32APIOptions;
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationHandler;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class DdemlUtil
/*      */ {
/*      */   public static class StandaloneDdeClient
/*      */     implements IDdeClient, Closeable
/*      */   {
/*   66 */     private final User32Util.MessageLoopThread messageLoop = new User32Util.MessageLoopThread();
/*      */     private final DdemlUtil.IDdeClient ddeClient;
/*      */     private final DdemlUtil.IDdeClient clientDelegate;
/*      */     
/*      */     public StandaloneDdeClient() {
/*   71 */       this.ddeClient = new DdemlUtil.DdeClient();
/*   72 */       (new Class[1])[0] = DdemlUtil.IDdeClient.class; this.messageLoop.getClass(); DdemlUtil.IDdeClient messageLoopHandler = (DdemlUtil.IDdeClient)Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[1], new User32Util.MessageLoopThread.Handler(this.messageLoop, this.ddeClient));
/*      */ 
/*      */       
/*   75 */       this.clientDelegate = (DdemlUtil.IDdeClient)Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[] { DdemlUtil.IDdeClient.class }, new DdemlUtil.MessageLoopWrapper(this.messageLoop, messageLoopHandler));
/*      */ 
/*      */       
/*   78 */       this.messageLoop.setDaemon(true);
/*   79 */       this.messageLoop.start();
/*      */     }
/*      */     
/*      */     public Integer getInstanceIdentitifier() {
/*   83 */       return this.ddeClient.getInstanceIdentitifier();
/*      */     }
/*      */     
/*      */     public void initialize(int afCmd) throws DdemlUtil.DdemlException {
/*   87 */       this.clientDelegate.initialize(afCmd);
/*      */     }
/*      */     
/*      */     public Ddeml.HSZ createStringHandle(String value) throws DdemlUtil.DdemlException {
/*   91 */       return this.clientDelegate.createStringHandle(value);
/*      */     }
/*      */     
/*      */     public void nameService(Ddeml.HSZ name, int afCmd) throws DdemlUtil.DdemlException {
/*   95 */       this.clientDelegate.nameService(name, afCmd);
/*      */     }
/*      */     
/*      */     public int getLastError() {
/*   99 */       return this.clientDelegate.getLastError();
/*      */     }
/*      */     
/*      */     public DdemlUtil.IDdeConnection connect(Ddeml.HSZ service, Ddeml.HSZ topic, Ddeml.CONVCONTEXT convcontext) {
/*  103 */       return this.clientDelegate.connect(service, topic, convcontext);
/*      */     }
/*      */     
/*      */     public String queryString(Ddeml.HSZ value) throws DdemlUtil.DdemlException {
/*  107 */       return this.clientDelegate.queryString(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public Ddeml.HDDEDATA createDataHandle(Pointer pSrc, int cb, int cbOff, Ddeml.HSZ hszItem, int wFmt, int afCmd) {
/*  112 */       return this.clientDelegate.createDataHandle(pSrc, cb, cbOff, hszItem, wFmt, afCmd);
/*      */     }
/*      */ 
/*      */     
/*      */     public void freeDataHandle(Ddeml.HDDEDATA hData) {
/*  117 */       this.clientDelegate.freeDataHandle(hData);
/*      */     }
/*      */ 
/*      */     
/*      */     public Ddeml.HDDEDATA addData(Ddeml.HDDEDATA hData, Pointer pSrc, int cb, int cbOff) {
/*  122 */       return this.clientDelegate.addData(hData, pSrc, cb, cbOff);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getData(Ddeml.HDDEDATA hData, Pointer pDst, int cbMax, int cbOff) {
/*  127 */       return this.clientDelegate.getData(hData, pDst, cbMax, cbOff);
/*      */     }
/*      */ 
/*      */     
/*      */     public Pointer accessData(Ddeml.HDDEDATA hData, WinDef.DWORDByReference pcbDataSize) {
/*  132 */       return this.clientDelegate.accessData(hData, pcbDataSize);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unaccessData(Ddeml.HDDEDATA hData) {
/*  137 */       this.clientDelegate.unaccessData(hData);
/*      */     }
/*      */ 
/*      */     
/*      */     public void postAdvise(Ddeml.HSZ hszTopic, Ddeml.HSZ hszItem) {
/*  142 */       this.clientDelegate.postAdvise(hszTopic, hszItem);
/*      */     }
/*      */     
/*      */     public void close() throws IOException {
/*  146 */       this.clientDelegate.uninitialize();
/*  147 */       this.messageLoop.exit();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean freeStringHandle(Ddeml.HSZ value) {
/*  152 */       return this.clientDelegate.freeStringHandle(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean keepStringHandle(Ddeml.HSZ value) {
/*  157 */       return this.clientDelegate.keepStringHandle(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void abandonTransactions() {
/*  162 */       this.clientDelegate.abandonTransactions();
/*      */     }
/*      */ 
/*      */     
/*      */     public DdemlUtil.IDdeConnectionList connectList(Ddeml.HSZ service, Ddeml.HSZ topic, DdemlUtil.IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
/*  167 */       return this.clientDelegate.connectList(service, topic, existingList, ctx);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean enableCallback(int wCmd) {
/*  172 */       return this.clientDelegate.enableCallback(wCmd);
/*      */     }
/*      */ 
/*      */     
/*      */     public DdemlUtil.IDdeConnection wrap(Ddeml.HCONV conv) {
/*  177 */       return this.clientDelegate.wrap(conv);
/*      */     }
/*      */ 
/*      */     
/*      */     public DdemlUtil.IDdeConnection connect(String service, String topic, Ddeml.CONVCONTEXT convcontext) {
/*  182 */       return this.clientDelegate.connect(service, topic, convcontext);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean uninitialize() {
/*  187 */       return this.clientDelegate.uninitialize();
/*      */     }
/*      */ 
/*      */     
/*      */     public void postAdvise(String hszTopic, String hszItem) {
/*  192 */       this.clientDelegate.postAdvise(hszTopic, hszItem);
/*      */     }
/*      */ 
/*      */     
/*      */     public DdemlUtil.IDdeConnectionList connectList(String service, String topic, DdemlUtil.IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
/*  197 */       return this.clientDelegate.connectList(service, topic, existingList, ctx);
/*      */     }
/*      */ 
/*      */     
/*      */     public void nameService(String name, int afCmd) throws DdemlUtil.DdemlException {
/*  202 */       this.clientDelegate.nameService(name, afCmd);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerAdvstartHandler(DdemlUtil.AdvstartHandler handler) {
/*  207 */       this.clientDelegate.registerAdvstartHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterAdvstartHandler(DdemlUtil.AdvstartHandler handler) {
/*  212 */       this.clientDelegate.unregisterAdvstartHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerAdvstopHandler(DdemlUtil.AdvstopHandler handler) {
/*  217 */       this.clientDelegate.registerAdvstopHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterAdvstopHandler(DdemlUtil.AdvstopHandler handler) {
/*  222 */       this.clientDelegate.unregisterAdvstopHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerConnectHandler(DdemlUtil.ConnectHandler handler) {
/*  227 */       this.clientDelegate.registerConnectHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterConnectHandler(DdemlUtil.ConnectHandler handler) {
/*  232 */       this.clientDelegate.unregisterConnectHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerAdvReqHandler(DdemlUtil.AdvreqHandler handler) {
/*  237 */       this.clientDelegate.registerAdvReqHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterAdvReqHandler(DdemlUtil.AdvreqHandler handler) {
/*  242 */       this.clientDelegate.unregisterAdvReqHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerRequestHandler(DdemlUtil.RequestHandler handler) {
/*  247 */       this.clientDelegate.registerRequestHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterRequestHandler(DdemlUtil.RequestHandler handler) {
/*  252 */       this.clientDelegate.unregisterRequestHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerWildconnectHandler(DdemlUtil.WildconnectHandler handler) {
/*  257 */       this.clientDelegate.registerWildconnectHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterWildconnectHandler(DdemlUtil.WildconnectHandler handler) {
/*  262 */       this.clientDelegate.unregisterWildconnectHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerAdvdataHandler(DdemlUtil.AdvdataHandler handler) {
/*  267 */       this.clientDelegate.registerAdvdataHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterAdvdataHandler(DdemlUtil.AdvdataHandler handler) {
/*  272 */       this.clientDelegate.unregisterAdvdataHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerExecuteHandler(DdemlUtil.ExecuteHandler handler) {
/*  277 */       this.clientDelegate.registerExecuteHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterExecuteHandler(DdemlUtil.ExecuteHandler handler) {
/*  282 */       this.clientDelegate.unregisterExecuteHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerPokeHandler(DdemlUtil.PokeHandler handler) {
/*  287 */       this.clientDelegate.registerPokeHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterPokeHandler(DdemlUtil.PokeHandler handler) {
/*  292 */       this.clientDelegate.unregisterPokeHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler handler) {
/*  297 */       this.clientDelegate.registerConnectConfirmHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler handler) {
/*  302 */       this.clientDelegate.unregisterConnectConfirmHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerDisconnectHandler(DdemlUtil.DisconnectHandler handler) {
/*  307 */       this.clientDelegate.registerDisconnectHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterDisconnectHandler(DdemlUtil.DisconnectHandler handler) {
/*  312 */       this.clientDelegate.unregisterDisconnectHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerErrorHandler(DdemlUtil.ErrorHandler handler) {
/*  317 */       this.clientDelegate.registerErrorHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterErrorHandler(DdemlUtil.ErrorHandler handler) {
/*  322 */       this.clientDelegate.unregisterErrorHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerRegisterHandler(DdemlUtil.RegisterHandler handler) {
/*  327 */       this.clientDelegate.registerRegisterHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterRegisterHandler(DdemlUtil.RegisterHandler handler) {
/*  332 */       this.clientDelegate.unregisterRegisterHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerXactCompleteHandler(DdemlUtil.XactCompleteHandler handler) {
/*  337 */       this.clientDelegate.registerXactCompleteHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterXactCompleteHandler(DdemlUtil.XactCompleteHandler handler) {
/*  342 */       this.clientDelegate.unregisterXactCompleteHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerUnregisterHandler(DdemlUtil.UnregisterHandler handler) {
/*  347 */       this.clientDelegate.registerUnregisterHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterUnregisterHandler(DdemlUtil.UnregisterHandler handler) {
/*  352 */       this.clientDelegate.unregisterUnregisterHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void registerMonitorHandler(DdemlUtil.MonitorHandler handler) {
/*  357 */       this.clientDelegate.registerMonitorHandler(handler);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterMonitorHandler(DdemlUtil.MonitorHandler handler) {
/*  362 */       this.clientDelegate.unregisterMonitorHandler(handler);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class MessageLoopWrapper implements InvocationHandler {
/*      */     private final Object delegate;
/*      */     private final User32Util.MessageLoopThread loopThread;
/*      */     
/*      */     public MessageLoopWrapper(User32Util.MessageLoopThread thread, Object delegate) {
/*  371 */       this.loopThread = thread;
/*  372 */       this.delegate = delegate;
/*      */     }
/*      */     
/*      */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*      */       try {
/*  377 */         Object result = method.invoke(this.delegate, args);
/*  378 */         Class<?> wrapClass = null;
/*  379 */         if (result instanceof DdemlUtil.IDdeConnection) {
/*  380 */           wrapClass = DdemlUtil.IDdeConnection.class;
/*  381 */         } else if (result instanceof DdemlUtil.IDdeConnectionList) {
/*  382 */           wrapClass = DdemlUtil.IDdeConnectionList.class;
/*  383 */         } else if (result instanceof DdemlUtil.IDdeClient) {
/*  384 */           wrapClass = DdemlUtil.IDdeClient.class;
/*      */         } 
/*  386 */         if (wrapClass != null && method.getReturnType().isAssignableFrom(wrapClass)) {
/*  387 */           result = wrap(result, wrapClass);
/*      */         }
/*  389 */         return result;
/*  390 */       } catch (InvocationTargetException ex) {
/*  391 */         Throwable cause = ex.getCause();
/*  392 */         if (cause instanceof Exception) {
/*  393 */           throw (Exception)cause;
/*      */         }
/*  395 */         throw ex;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private <V> V wrap(V delegate, Class clazz) {
/*  401 */       (new Class[1])[0] = clazz; this.loopThread.getClass(); V messageLoopHandler = (V)Proxy.newProxyInstance(DdemlUtil.StandaloneDdeClient.class.getClassLoader(), new Class[1], new User32Util.MessageLoopThread.Handler(this.loopThread, delegate));
/*      */ 
/*      */       
/*  404 */       V clientDelegate = (V)Proxy.newProxyInstance(DdemlUtil.StandaloneDdeClient.class.getClassLoader(), new Class[] { clazz }, new MessageLoopWrapper(this.loopThread, messageLoopHandler));
/*      */ 
/*      */       
/*  407 */       return clientDelegate;
/*      */     }
/*      */   }
/*      */   
/*      */   public static class DdeConnection implements IDdeConnection {
/*      */     private Ddeml.HCONV conv;
/*      */     private final DdemlUtil.IDdeClient client;
/*      */     
/*      */     public DdeConnection(DdemlUtil.IDdeClient client, Ddeml.HCONV conv) {
/*  416 */       this.conv = conv;
/*  417 */       this.client = client;
/*      */     }
/*      */     
/*      */     public Ddeml.HCONV getConv() {
/*  421 */       return this.conv;
/*      */     }
/*      */ 
/*      */     
/*      */     public void abandonTransaction(int transactionId) {
/*  426 */       boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(this.client.getInstanceIdentitifier().intValue(), this.conv, transactionId);
/*  427 */       if (!result) {
/*  428 */         throw DdemlUtil.DdemlException.create(this.client.getLastError());
/*      */       }
/*      */     }
/*      */     
/*      */     public void abandonTransactions() {
/*  433 */       boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(this.client.getInstanceIdentitifier().intValue(), this.conv, 0);
/*  434 */       if (!result) {
/*  435 */         throw DdemlUtil.DdemlException.create(this.client.getLastError());
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public Ddeml.HDDEDATA clientTransaction(Pointer data, int dataLength, Ddeml.HSZ item, int wFmt, int transaction, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
/*  441 */       if (timeout == -1 && result == null) {
/*  442 */         result = new WinDef.DWORDByReference();
/*      */       }
/*  444 */       Ddeml.HDDEDATA returnData = Ddeml.INSTANCE.DdeClientTransaction(data, dataLength, this.conv, item, wFmt, transaction, timeout, result);
/*  445 */       if (returnData == null) {
/*  446 */         throw DdemlUtil.DdemlException.create(this.client.getLastError());
/*      */       }
/*  448 */       if (userHandle != null) {
/*  449 */         if (timeout != -1) {
/*  450 */           setUserHandle(-1, userHandle);
/*      */         } else {
/*  452 */           setUserHandle(result.getValue().intValue(), userHandle);
/*      */         } 
/*      */       }
/*  455 */       return returnData;
/*      */     }
/*      */     
/*      */     public Ddeml.HDDEDATA clientTransaction(Pointer data, int dataLength, String item, int wFmt, int transaction, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
/*  459 */       Ddeml.HSZ itemHSZ = null;
/*      */       try {
/*  461 */         itemHSZ = this.client.createStringHandle(item);
/*  462 */         return clientTransaction(data, dataLength, itemHSZ, wFmt, transaction, timeout, result, userHandle);
/*      */       } finally {
/*  464 */         this.client.freeStringHandle(itemHSZ);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void poke(Pointer data, int dataLength, Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
/*  470 */       clientTransaction(data, dataLength, item, wFmt, 16528, timeout, result, userHandle);
/*      */     }
/*      */     
/*      */     public void poke(Pointer data, int dataLength, String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
/*  474 */       Ddeml.HSZ itemHSZ = null;
/*      */       try {
/*  476 */         itemHSZ = this.client.createStringHandle(item);
/*  477 */         poke(data, dataLength, itemHSZ, wFmt, timeout, result, userHandle);
/*      */       } finally {
/*  479 */         this.client.freeStringHandle(itemHSZ);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public Ddeml.HDDEDATA request(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
/*  485 */       return clientTransaction(Pointer.NULL, 0, item, wFmt, 8368, timeout, result, userHandle);
/*      */     }
/*      */     
/*      */     public Ddeml.HDDEDATA request(String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
/*  489 */       Ddeml.HSZ itemHSZ = null;
/*      */       try {
/*  491 */         itemHSZ = this.client.createStringHandle(item);
/*  492 */         return request(itemHSZ, wFmt, timeout, result, userHandle);
/*      */       } finally {
/*  494 */         this.client.freeStringHandle(itemHSZ);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void execute(String executeString, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
/*  500 */       Memory mem = new Memory((executeString.length() * 2 + 2));
/*  501 */       mem.setWideString(0L, executeString);
/*  502 */       clientTransaction((Pointer)mem, (int)mem.size(), (Ddeml.HSZ)null, 0, 16464, timeout, result, userHandle);
/*      */     }
/*      */ 
/*      */     
/*      */     public void advstart(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
/*  507 */       clientTransaction(Pointer.NULL, 0, item, wFmt, 4144, timeout, result, userHandle);
/*      */     }
/*      */     
/*      */     public void advstart(String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
/*  511 */       Ddeml.HSZ itemHSZ = null;
/*      */       try {
/*  513 */         itemHSZ = this.client.createStringHandle(item);
/*  514 */         advstart(itemHSZ, wFmt, timeout, result, userHandle);
/*      */       } finally {
/*  516 */         this.client.freeStringHandle(itemHSZ);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void advstop(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
/*  522 */       clientTransaction(Pointer.NULL, 0, item, wFmt, 32832, timeout, result, userHandle);
/*      */     }
/*      */     
/*      */     public void advstop(String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
/*  526 */       Ddeml.HSZ itemHSZ = null;
/*      */       try {
/*  528 */         itemHSZ = this.client.createStringHandle(item);
/*  529 */         advstop(itemHSZ, wFmt, timeout, result, userHandle);
/*      */       } finally {
/*  531 */         this.client.freeStringHandle(itemHSZ);
/*      */       } 
/*      */     }
/*      */     
/*      */     public void impersonateClient() {
/*  536 */       boolean result = Ddeml.INSTANCE.DdeImpersonateClient(this.conv);
/*  537 */       if (!result) {
/*  538 */         throw DdemlUtil.DdemlException.create(this.client.getLastError());
/*      */       }
/*      */     }
/*      */     
/*      */     public void close() {
/*  543 */       boolean result = Ddeml.INSTANCE.DdeDisconnect(this.conv);
/*  544 */       if (!result) {
/*  545 */         throw DdemlUtil.DdemlException.create(this.client.getLastError());
/*      */       }
/*      */     }
/*      */     
/*      */     public void reconnect() {
/*  550 */       Ddeml.HCONV newConv = Ddeml.INSTANCE.DdeReconnect(this.conv);
/*  551 */       if (newConv != null) {
/*  552 */         this.conv = newConv;
/*      */       } else {
/*  554 */         throw DdemlUtil.DdemlException.create(this.client.getLastError());
/*      */       } 
/*      */     }
/*      */     
/*      */     public boolean enableCallback(int wCmd) {
/*  559 */       boolean result = Ddeml.INSTANCE.DdeEnableCallback(this.client.getInstanceIdentitifier().intValue(), this.conv, wCmd);
/*  560 */       if (!result && wCmd == 2) {
/*  561 */         throw DdemlUtil.DdemlException.create(this.client.getLastError());
/*      */       }
/*  563 */       return result;
/*      */     }
/*      */     
/*      */     public void setUserHandle(int id, BaseTSD.DWORD_PTR hUser) throws DdemlUtil.DdemlException {
/*  567 */       boolean result = Ddeml.INSTANCE.DdeSetUserHandle(this.conv, id, hUser);
/*  568 */       if (!result) {
/*  569 */         throw DdemlUtil.DdemlException.create(this.client.getLastError());
/*      */       }
/*      */     }
/*      */     
/*      */     public Ddeml.CONVINFO queryConvInfo(int idTransaction) throws DdemlUtil.DdemlException {
/*  574 */       Ddeml.CONVINFO convInfo = new Ddeml.CONVINFO();
/*  575 */       convInfo.cb = convInfo.size();
/*  576 */       convInfo.ConvCtxt.cb = convInfo.ConvCtxt.size();
/*  577 */       convInfo.write();
/*  578 */       int result = Ddeml.INSTANCE.DdeQueryConvInfo(this.conv, idTransaction, convInfo);
/*  579 */       if (result == 0) {
/*  580 */         throw DdemlUtil.DdemlException.create(this.client.getLastError());
/*      */       }
/*  582 */       return convInfo;
/*      */     }
/*      */   }
/*      */   
/*      */   public static class DdeConnectionList
/*      */     implements IDdeConnectionList {
/*      */     private final DdemlUtil.IDdeClient client;
/*      */     private final Ddeml.HCONVLIST convList;
/*      */     
/*      */     public DdeConnectionList(DdemlUtil.IDdeClient client, Ddeml.HCONVLIST convList) {
/*  592 */       this.convList = convList;
/*  593 */       this.client = client;
/*      */     }
/*      */ 
/*      */     
/*      */     public Ddeml.HCONVLIST getHandle() {
/*  598 */       return this.convList;
/*      */     }
/*      */ 
/*      */     
/*      */     public DdemlUtil.IDdeConnection queryNextServer(DdemlUtil.IDdeConnection prevConnection) {
/*  603 */       Ddeml.HCONV conv = Ddeml.INSTANCE.DdeQueryNextServer(this.convList, (prevConnection != null) ? prevConnection
/*      */           
/*  605 */           .getConv() : null);
/*  606 */       if (conv != null) {
/*  607 */         return new DdemlUtil.DdeConnection(this.client, conv);
/*      */       }
/*  609 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void close() {
/*  615 */       boolean result = Ddeml.INSTANCE.DdeDisconnectList(this.convList);
/*  616 */       if (!result)
/*  617 */         throw DdemlUtil.DdemlException.create(this.client.getLastError()); 
/*      */     }
/*      */   }
/*      */   
/*      */   public static class DdeClient
/*      */     implements IDdeClient {
/*      */     private Integer idInst;
/*  624 */     private final DdemlUtil.DdeAdapter ddeAdapter = new DdemlUtil.DdeAdapter();
/*      */     
/*      */     public Integer getInstanceIdentitifier() {
/*  627 */       return this.idInst;
/*      */     }
/*      */     
/*      */     public void initialize(int afCmd) throws DdemlUtil.DdemlException {
/*  631 */       WinDef.DWORDByReference pidInst = new WinDef.DWORDByReference();
/*  632 */       Integer result = Integer.valueOf(Ddeml.INSTANCE.DdeInitialize(pidInst, this.ddeAdapter, afCmd, 0));
/*  633 */       if (result.intValue() != 0) {
/*  634 */         throw DdemlUtil.DdemlException.create(result.intValue());
/*      */       }
/*  636 */       this.idInst = Integer.valueOf(pidInst.getValue().intValue());
/*  637 */       if (this.ddeAdapter instanceof DdemlUtil.DdeAdapter)
/*  638 */         this.ddeAdapter.setInstanceIdentifier(this.idInst.intValue()); 
/*      */     }
/*      */     
/*      */     public Ddeml.HSZ createStringHandle(String value) throws DdemlUtil.DdemlException {
/*      */       int codePage;
/*  643 */       if (value == null) {
/*  644 */         return null;
/*      */       }
/*      */       
/*  647 */       if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
/*  648 */         codePage = 1200;
/*      */       } else {
/*  650 */         codePage = 1004;
/*      */       } 
/*  652 */       Ddeml.HSZ handle = Ddeml.INSTANCE.DdeCreateStringHandle(this.idInst.intValue(), value, codePage);
/*  653 */       if (handle == null) {
/*  654 */         throw DdemlUtil.DdemlException.create(getLastError());
/*      */       }
/*  656 */       return handle;
/*      */     }
/*      */     
/*      */     public void nameService(Ddeml.HSZ name, int afCmd) throws DdemlUtil.DdemlException {
/*  660 */       Ddeml.HDDEDATA handle = Ddeml.INSTANCE.DdeNameService(this.idInst.intValue(), name, new Ddeml.HSZ(), afCmd);
/*  661 */       if (handle == null) {
/*  662 */         throw DdemlUtil.DdemlException.create(getLastError());
/*      */       }
/*      */     }
/*      */     
/*      */     public void nameService(String name, int afCmd) throws DdemlUtil.DdemlException {
/*  667 */       Ddeml.HSZ nameHSZ = null;
/*      */       try {
/*  669 */         nameHSZ = createStringHandle(name);
/*  670 */         nameService(nameHSZ, afCmd);
/*      */       } finally {
/*  672 */         freeStringHandle(nameHSZ);
/*      */       } 
/*      */     }
/*      */     
/*      */     public int getLastError() {
/*  677 */       return Ddeml.INSTANCE.DdeGetLastError(this.idInst.intValue());
/*      */     }
/*      */     
/*      */     public DdemlUtil.IDdeConnection connect(Ddeml.HSZ service, Ddeml.HSZ topic, Ddeml.CONVCONTEXT convcontext) {
/*  681 */       Ddeml.HCONV hconv = Ddeml.INSTANCE.DdeConnect(this.idInst.intValue(), service, topic, convcontext);
/*  682 */       if (hconv == null) {
/*  683 */         throw DdemlUtil.DdemlException.create(getLastError());
/*      */       }
/*  685 */       return new DdemlUtil.DdeConnection(this, hconv);
/*      */     }
/*      */     
/*      */     public DdemlUtil.IDdeConnection connect(String service, String topic, Ddeml.CONVCONTEXT convcontext) {
/*  689 */       Ddeml.HSZ serviceHSZ = null;
/*  690 */       Ddeml.HSZ topicHSZ = null;
/*      */       try {
/*  692 */         serviceHSZ = createStringHandle(service);
/*  693 */         topicHSZ = createStringHandle(topic);
/*  694 */         return connect(serviceHSZ, topicHSZ, convcontext);
/*      */       } finally {
/*  696 */         freeStringHandle(topicHSZ);
/*  697 */         freeStringHandle(serviceHSZ);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public String queryString(Ddeml.HSZ value) throws DdemlUtil.DdemlException {
/*      */       int codePage, byteWidth;
/*  704 */       if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
/*  705 */         codePage = 1200;
/*  706 */         byteWidth = 2;
/*      */       } else {
/*  708 */         codePage = 1004;
/*  709 */         byteWidth = 1;
/*      */       } 
/*  711 */       Memory buffer = new Memory((257 * byteWidth));
/*      */       try {
/*  713 */         int length = Ddeml.INSTANCE.DdeQueryString(this.idInst.intValue(), value, (Pointer)buffer, 256, codePage);
/*  714 */         if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
/*  715 */           return buffer.getWideString(0L);
/*      */         }
/*  717 */         return buffer.getString(0L);
/*      */       } finally {
/*      */         
/*  720 */         buffer.valid();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public Ddeml.HDDEDATA createDataHandle(Pointer pSrc, int cb, int cbOff, Ddeml.HSZ hszItem, int wFmt, int afCmd) {
/*  726 */       Ddeml.HDDEDATA returnData = Ddeml.INSTANCE.DdeCreateDataHandle(this.idInst.intValue(), pSrc, cb, cbOff, hszItem, wFmt, afCmd);
/*  727 */       if (returnData == null) {
/*  728 */         throw DdemlUtil.DdemlException.create(getLastError());
/*      */       }
/*  730 */       return returnData;
/*      */     }
/*      */     
/*      */     public void freeDataHandle(Ddeml.HDDEDATA hData) {
/*  734 */       boolean result = Ddeml.INSTANCE.DdeFreeDataHandle(hData);
/*  735 */       if (!result) {
/*  736 */         throw DdemlUtil.DdemlException.create(getLastError());
/*      */       }
/*      */     }
/*      */     
/*      */     public Ddeml.HDDEDATA addData(Ddeml.HDDEDATA hData, Pointer pSrc, int cb, int cbOff) {
/*  741 */       Ddeml.HDDEDATA newHandle = Ddeml.INSTANCE.DdeAddData(hData, pSrc, cb, cbOff);
/*  742 */       if (newHandle == null) {
/*  743 */         throw DdemlUtil.DdemlException.create(getLastError());
/*      */       }
/*  745 */       return newHandle;
/*      */     }
/*      */     
/*      */     public int getData(Ddeml.HDDEDATA hData, Pointer pDst, int cbMax, int cbOff) {
/*  749 */       int result = Ddeml.INSTANCE.DdeGetData(hData, pDst, cbMax, cbOff);
/*  750 */       int errorCode = getLastError();
/*  751 */       if (errorCode != 0) {
/*  752 */         throw DdemlUtil.DdemlException.create(errorCode);
/*      */       }
/*  754 */       return result;
/*      */     }
/*      */     
/*      */     public Pointer accessData(Ddeml.HDDEDATA hData, WinDef.DWORDByReference pcbDataSize) {
/*  758 */       Pointer result = Ddeml.INSTANCE.DdeAccessData(hData, pcbDataSize);
/*  759 */       if (result == null) {
/*  760 */         throw DdemlUtil.DdemlException.create(getLastError());
/*      */       }
/*  762 */       return result;
/*      */     }
/*      */     
/*      */     public void unaccessData(Ddeml.HDDEDATA hData) {
/*  766 */       boolean result = Ddeml.INSTANCE.DdeUnaccessData(hData);
/*  767 */       if (!result) {
/*  768 */         throw DdemlUtil.DdemlException.create(getLastError());
/*      */       }
/*      */     }
/*      */     
/*      */     public void postAdvise(Ddeml.HSZ hszTopic, Ddeml.HSZ hszItem) {
/*  773 */       boolean result = Ddeml.INSTANCE.DdePostAdvise(this.idInst.intValue(), hszTopic, hszItem);
/*  774 */       if (!result) {
/*  775 */         throw DdemlUtil.DdemlException.create(getLastError());
/*      */       }
/*      */     }
/*      */     
/*      */     public void postAdvise(String topic, String item) {
/*  780 */       Ddeml.HSZ itemHSZ = null;
/*  781 */       Ddeml.HSZ topicHSZ = null;
/*      */       try {
/*  783 */         topicHSZ = createStringHandle(topic);
/*  784 */         itemHSZ = createStringHandle(item);
/*  785 */         postAdvise(topicHSZ, itemHSZ);
/*      */       } finally {
/*  787 */         freeStringHandle(topicHSZ);
/*  788 */         freeStringHandle(itemHSZ);
/*      */       } 
/*      */     }
/*      */     
/*      */     public boolean freeStringHandle(Ddeml.HSZ value) {
/*  793 */       if (value == null) {
/*  794 */         return true;
/*      */       }
/*  796 */       return Ddeml.INSTANCE.DdeFreeStringHandle(this.idInst.intValue(), value);
/*      */     }
/*      */     
/*      */     public boolean keepStringHandle(Ddeml.HSZ value) {
/*  800 */       return Ddeml.INSTANCE.DdeKeepStringHandle(this.idInst.intValue(), value);
/*      */     }
/*      */     
/*      */     public void abandonTransactions() {
/*  804 */       boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(this.idInst.intValue(), null, 0);
/*  805 */       if (!result) {
/*  806 */         throw DdemlUtil.DdemlException.create(getLastError());
/*      */       }
/*      */     }
/*      */     
/*      */     public DdemlUtil.IDdeConnectionList connectList(Ddeml.HSZ service, Ddeml.HSZ topic, DdemlUtil.IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
/*  811 */       Ddeml.HCONVLIST convlist = Ddeml.INSTANCE.DdeConnectList(this.idInst.intValue(), service, topic, (existingList != null) ? existingList.getHandle() : null, ctx);
/*  812 */       if (convlist == null) {
/*  813 */         throw DdemlUtil.DdemlException.create(getLastError());
/*      */       }
/*  815 */       return new DdemlUtil.DdeConnectionList(this, convlist);
/*      */     }
/*      */ 
/*      */     
/*      */     public DdemlUtil.IDdeConnectionList connectList(String service, String topic, DdemlUtil.IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
/*  820 */       Ddeml.HSZ serviceHSZ = null;
/*  821 */       Ddeml.HSZ topicHSZ = null;
/*      */       try {
/*  823 */         serviceHSZ = createStringHandle(service);
/*  824 */         topicHSZ = createStringHandle(topic);
/*  825 */         return connectList(serviceHSZ, topicHSZ, existingList, ctx);
/*      */       } finally {
/*  827 */         freeStringHandle(topicHSZ);
/*  828 */         freeStringHandle(serviceHSZ);
/*      */       } 
/*      */     }
/*      */     
/*      */     public boolean enableCallback(int wCmd) {
/*  833 */       boolean result = Ddeml.INSTANCE.DdeEnableCallback(this.idInst.intValue(), null, wCmd);
/*  834 */       if (!result && wCmd != 2) {
/*  835 */         int errorCode = getLastError();
/*  836 */         if (errorCode != 0) {
/*  837 */           throw DdemlUtil.DdemlException.create(getLastError());
/*      */         }
/*      */       } 
/*  840 */       return result;
/*      */     }
/*      */     
/*      */     public boolean uninitialize() {
/*  844 */       return Ddeml.INSTANCE.DdeUninitialize(this.idInst.intValue());
/*      */     }
/*      */     
/*      */     public void close() {
/*  848 */       uninitialize();
/*      */     }
/*      */     
/*      */     public DdemlUtil.IDdeConnection wrap(Ddeml.HCONV hconv) {
/*  852 */       return new DdemlUtil.DdeConnection(this, hconv);
/*      */     }
/*      */ 
/*      */     
/*      */     public void unregisterDisconnectHandler(DdemlUtil.DisconnectHandler handler) {
/*  857 */       this.ddeAdapter.unregisterDisconnectHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerAdvstartHandler(DdemlUtil.AdvstartHandler handler) {
/*  861 */       this.ddeAdapter.registerAdvstartHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterAdvstartHandler(DdemlUtil.AdvstartHandler handler) {
/*  865 */       this.ddeAdapter.unregisterAdvstartHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerAdvstopHandler(DdemlUtil.AdvstopHandler handler) {
/*  869 */       this.ddeAdapter.registerAdvstopHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterAdvstopHandler(DdemlUtil.AdvstopHandler handler) {
/*  873 */       this.ddeAdapter.unregisterAdvstopHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerConnectHandler(DdemlUtil.ConnectHandler handler) {
/*  877 */       this.ddeAdapter.registerConnectHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterConnectHandler(DdemlUtil.ConnectHandler handler) {
/*  881 */       this.ddeAdapter.unregisterConnectHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerAdvReqHandler(DdemlUtil.AdvreqHandler handler) {
/*  885 */       this.ddeAdapter.registerAdvReqHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterAdvReqHandler(DdemlUtil.AdvreqHandler handler) {
/*  889 */       this.ddeAdapter.unregisterAdvReqHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerRequestHandler(DdemlUtil.RequestHandler handler) {
/*  893 */       this.ddeAdapter.registerRequestHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterRequestHandler(DdemlUtil.RequestHandler handler) {
/*  897 */       this.ddeAdapter.unregisterRequestHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerWildconnectHandler(DdemlUtil.WildconnectHandler handler) {
/*  901 */       this.ddeAdapter.registerWildconnectHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterWildconnectHandler(DdemlUtil.WildconnectHandler handler) {
/*  905 */       this.ddeAdapter.unregisterWildconnectHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerAdvdataHandler(DdemlUtil.AdvdataHandler handler) {
/*  909 */       this.ddeAdapter.registerAdvdataHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterAdvdataHandler(DdemlUtil.AdvdataHandler handler) {
/*  913 */       this.ddeAdapter.unregisterAdvdataHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerExecuteHandler(DdemlUtil.ExecuteHandler handler) {
/*  917 */       this.ddeAdapter.registerExecuteHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterExecuteHandler(DdemlUtil.ExecuteHandler handler) {
/*  921 */       this.ddeAdapter.unregisterExecuteHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerPokeHandler(DdemlUtil.PokeHandler handler) {
/*  925 */       this.ddeAdapter.registerPokeHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterPokeHandler(DdemlUtil.PokeHandler handler) {
/*  929 */       this.ddeAdapter.unregisterPokeHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler handler) {
/*  933 */       this.ddeAdapter.registerConnectConfirmHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler handler) {
/*  937 */       this.ddeAdapter.unregisterConnectConfirmHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerDisconnectHandler(DdemlUtil.DisconnectHandler handler) {
/*  941 */       this.ddeAdapter.registerDisconnectHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerErrorHandler(DdemlUtil.ErrorHandler handler) {
/*  945 */       this.ddeAdapter.registerErrorHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterErrorHandler(DdemlUtil.ErrorHandler handler) {
/*  949 */       this.ddeAdapter.unregisterErrorHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerRegisterHandler(DdemlUtil.RegisterHandler handler) {
/*  953 */       this.ddeAdapter.registerRegisterHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterRegisterHandler(DdemlUtil.RegisterHandler handler) {
/*  957 */       this.ddeAdapter.unregisterRegisterHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerXactCompleteHandler(DdemlUtil.XactCompleteHandler handler) {
/*  961 */       this.ddeAdapter.registerXactCompleteHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterXactCompleteHandler(DdemlUtil.XactCompleteHandler handler) {
/*  965 */       this.ddeAdapter.xactCompleteXactCompleteHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerUnregisterHandler(DdemlUtil.UnregisterHandler handler) {
/*  969 */       this.ddeAdapter.registerUnregisterHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterUnregisterHandler(DdemlUtil.UnregisterHandler handler) {
/*  973 */       this.ddeAdapter.unregisterUnregisterHandler(handler);
/*      */     }
/*      */     
/*      */     public void registerMonitorHandler(DdemlUtil.MonitorHandler handler) {
/*  977 */       this.ddeAdapter.registerMonitorHandler(handler);
/*      */     }
/*      */     
/*      */     public void unregisterMonitorHandler(DdemlUtil.MonitorHandler handler) {
/*  981 */       this.ddeAdapter.unregisterMonitorHandler(handler);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface AdvstartHandler
/*      */   {
/*      */     boolean onAdvstart(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface AdvstopHandler
/*      */   {
/*      */     void onAdvstop(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface ConnectHandler
/*      */   {
/*      */     boolean onConnect(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.CONVCONTEXT param1CONVCONTEXT, boolean param1Boolean);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface AdvreqHandler
/*      */   {
/*      */     Ddeml.HDDEDATA onAdvreq(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, int param1Int3);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface RequestHandler
/*      */   {
/*      */     Ddeml.HDDEDATA onRequest(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface WildconnectHandler
/*      */   {
/*      */     List<Ddeml.HSZPAIR> onWildconnect(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.CONVCONTEXT param1CONVCONTEXT, boolean param1Boolean);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface AdvdataHandler
/*      */   {
/*      */     int onAdvdata(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.HDDEDATA param1HDDEDATA);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface ConnectConfirmHandler
/*      */   {
/*      */     void onConnectConfirm(int param1Int, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, boolean param1Boolean);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface DisconnectHandler
/*      */   {
/*      */     void onDisconnect(int param1Int, Ddeml.HCONV param1HCONV, boolean param1Boolean);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface ErrorHandler
/*      */   {
/*      */     void onError(int param1Int1, Ddeml.HCONV param1HCONV, int param1Int2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface RegisterHandler
/*      */   {
/*      */     void onRegister(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface XactCompleteHandler
/*      */   {
/*      */     void onXactComplete(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.HDDEDATA param1HDDEDATA, BaseTSD.ULONG_PTR param1ULONG_PTR1, BaseTSD.ULONG_PTR param1ULONG_PTR2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface UnregisterHandler
/*      */   {
/*      */     void onUnregister(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface ExecuteHandler
/*      */   {
/*      */     int onExecute(int param1Int, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ, Ddeml.HDDEDATA param1HDDEDATA);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface PokeHandler
/*      */   {
/*      */     int onPoke(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.HDDEDATA param1HDDEDATA);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface MonitorHandler
/*      */   {
/*      */     void onMonitor(int param1Int1, Ddeml.HDDEDATA param1HDDEDATA, int param1Int2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class DdeAdapter
/*      */     implements Ddeml.DdeCallback
/*      */   {
/*      */     public static class BlockException
/*      */       extends RuntimeException {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1535 */     private static final Logger LOG = Logger.getLogger(DdeAdapter.class.getName());
/*      */     
/*      */     private int idInst;
/*      */     
/*      */     public void setInstanceIdentifier(int idInst) {
/* 1540 */       this.idInst = idInst;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.PVOID ddeCallback(int wType, int wFmt, Ddeml.HCONV hConv, Ddeml.HSZ hsz1, Ddeml.HSZ hsz2, Ddeml.HDDEDATA hData, BaseTSD.ULONG_PTR lData1, BaseTSD.ULONG_PTR lData2) {
/* 1548 */       String transactionTypeName = null; 
/*      */       try { boolean booleanResult; Ddeml.HDDEDATA data; Ddeml.CONVCONTEXT convcontext; int intResult, count; Ddeml.HSZPAIR[] hszPairs; int size;
/* 1550 */         switch (wType)
/*      */         { case 4144:
/* 1552 */             booleanResult = onAdvstart(wType, wFmt, hConv, hsz1, hsz2);
/* 1553 */             return new WinDef.PVOID(Pointer.createConstant((new WinDef.BOOL(booleanResult)).intValue()));
/*      */           case 4194:
/* 1555 */             convcontext = null;
/* 1556 */             if (lData1.toPointer() != null) {
/* 1557 */               convcontext = new Ddeml.CONVCONTEXT(new Pointer(lData1.longValue()));
/*      */             }
/* 1559 */             booleanResult = onConnect(wType, hsz1, hsz2, convcontext, (lData2 != null && lData2.intValue() != 0));
/* 1560 */             return new WinDef.PVOID(Pointer.createConstant((new WinDef.BOOL(booleanResult)).intValue()));
/*      */           case 8226:
/* 1562 */             count = lData1.intValue() & 0xFFFF;
/* 1563 */             data = onAdvreq(wType, wFmt, hConv, hsz1, hsz2, count);
/* 1564 */             if (data == null) {
/* 1565 */               return new WinDef.PVOID();
/*      */             }
/* 1567 */             return new WinDef.PVOID(data.getPointer());
/*      */           
/*      */           case 8368:
/* 1570 */             data = onRequest(wType, wFmt, hConv, hsz1, hsz2);
/* 1571 */             if (data == null) {
/* 1572 */               return new WinDef.PVOID();
/*      */             }
/* 1574 */             return new WinDef.PVOID(data.getPointer());
/*      */           
/*      */           case 8418:
/* 1577 */             convcontext = null;
/* 1578 */             if (lData1.toPointer() != null) {
/* 1579 */               convcontext = new Ddeml.CONVCONTEXT(new Pointer(lData1.longValue()));
/*      */             }
/* 1581 */             hszPairs = onWildconnect(wType, hsz1, hsz2, convcontext, (lData2 != null && lData2.intValue() != 0));
/* 1582 */             if (hszPairs == null || hszPairs.length == 0) {
/* 1583 */               return new WinDef.PVOID();
/*      */             }
/* 1585 */             size = 0;
/* 1586 */             for (Ddeml.HSZPAIR hp : hszPairs) {
/* 1587 */               hp.write();
/* 1588 */               size += hp.size();
/*      */             } 
/* 1590 */             data = Ddeml.INSTANCE.DdeCreateDataHandle(this.idInst, hszPairs[0]
/* 1591 */                 .getPointer(), size, 0, null, wFmt, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1597 */             return new WinDef.PVOID(data.getPointer());
/*      */           case 16400:
/* 1599 */             intResult = onAdvdata(wType, wFmt, hConv, hsz1, hsz2, hData);
/* 1600 */             return new WinDef.PVOID(Pointer.createConstant(intResult));
/*      */           case 16464:
/* 1602 */             intResult = onExecute(wType, hConv, hsz1, hData);
/* 1603 */             Ddeml.INSTANCE.DdeFreeDataHandle(hData);
/* 1604 */             return new WinDef.PVOID(Pointer.createConstant(intResult));
/*      */           case 16528:
/* 1606 */             intResult = onPoke(wType, wFmt, hConv, hsz1, hsz2, hData);
/* 1607 */             return new WinDef.PVOID(Pointer.createConstant(intResult));
/*      */           case 32832:
/* 1609 */             onAdvstop(wType, wFmt, hConv, hsz1, hsz2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1640 */             return new WinDef.PVOID();case 32882: onConnectConfirm(wType, hConv, hsz1, hsz2, (lData2 != null && lData2.intValue() != 0)); return new WinDef.PVOID();case 32962: onDisconnect(wType, hConv, (lData2 != null && lData2.intValue() != 0)); return new WinDef.PVOID();case 32770: onError(wType, hConv, (int)(lData2.longValue() & 0xFFFFL)); return new WinDef.PVOID();case 32930: onRegister(wType, hsz1, hsz2); return new WinDef.PVOID();case 32896: onXactComplete(wType, wFmt, hConv, hsz1, hsz2, hData, lData1, lData2); return new WinDef.PVOID();case 32978: onUnregister(wType, hsz1, hsz2); return new WinDef.PVOID();case 33010: onMonitor(wType, hData, lData2.intValue()); return new WinDef.PVOID(); }  LOG.log(Level.FINE, String.format("Not implemented Operation - Transaction type: 0x%X (%s)", new Object[] { Integer.valueOf(wType), transactionTypeName })); } catch (BlockException ex) { return new WinDef.PVOID(Pointer.createConstant(-1)); } catch (Throwable ex) { LOG.log(Level.WARNING, "Exception in DDECallback", ex); }  return new WinDef.PVOID();
/*      */     }
/*      */     
/* 1643 */     private final List<DdemlUtil.AdvstartHandler> advstartHandler = new CopyOnWriteArrayList<DdemlUtil.AdvstartHandler>();
/*      */     
/*      */     public void registerAdvstartHandler(DdemlUtil.AdvstartHandler handler) {
/* 1646 */       this.advstartHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterAdvstartHandler(DdemlUtil.AdvstartHandler handler) {
/* 1650 */       this.advstartHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private boolean onAdvstart(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
/* 1654 */       boolean oneHandlerTrue = false;
/* 1655 */       for (DdemlUtil.AdvstartHandler handler : this.advstartHandler) {
/* 1656 */         if (handler.onAdvstart(transactionType, dataFormat, hconv, topic, item)) {
/* 1657 */           oneHandlerTrue = true;
/*      */         }
/*      */       } 
/* 1660 */       return oneHandlerTrue;
/*      */     }
/*      */     
/* 1663 */     private final List<DdemlUtil.AdvstopHandler> advstopHandler = new CopyOnWriteArrayList<DdemlUtil.AdvstopHandler>();
/*      */     
/*      */     public void registerAdvstopHandler(DdemlUtil.AdvstopHandler handler) {
/* 1666 */       this.advstopHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterAdvstopHandler(DdemlUtil.AdvstopHandler handler) {
/* 1670 */       this.advstopHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private void onAdvstop(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
/* 1674 */       for (DdemlUtil.AdvstopHandler handler : this.advstopHandler) {
/* 1675 */         handler.onAdvstop(transactionType, dataFormat, hconv, topic, item);
/*      */       }
/*      */     }
/*      */     
/* 1679 */     private final List<DdemlUtil.ConnectHandler> connectHandler = new CopyOnWriteArrayList<DdemlUtil.ConnectHandler>();
/*      */     
/*      */     public void registerConnectHandler(DdemlUtil.ConnectHandler handler) {
/* 1682 */       this.connectHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterConnectHandler(DdemlUtil.ConnectHandler handler) {
/* 1686 */       this.connectHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private boolean onConnect(int transactionType, Ddeml.HSZ topic, Ddeml.HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
/* 1690 */       boolean oneHandlerTrue = false;
/* 1691 */       for (DdemlUtil.ConnectHandler handler : this.connectHandler) {
/* 1692 */         if (handler.onConnect(transactionType, topic, service, convcontext, sameInstance)) {
/* 1693 */           oneHandlerTrue = true;
/*      */         }
/*      */       } 
/* 1696 */       return oneHandlerTrue;
/*      */     }
/*      */     
/* 1699 */     private final List<DdemlUtil.AdvreqHandler> advReqHandler = new CopyOnWriteArrayList<DdemlUtil.AdvreqHandler>();
/*      */     
/*      */     public void registerAdvReqHandler(DdemlUtil.AdvreqHandler handler) {
/* 1702 */       this.advReqHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterAdvReqHandler(DdemlUtil.AdvreqHandler handler) {
/* 1706 */       this.advReqHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private Ddeml.HDDEDATA onAdvreq(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item, int count) {
/* 1710 */       for (DdemlUtil.AdvreqHandler handler : this.advReqHandler) {
/* 1711 */         Ddeml.HDDEDATA result = handler.onAdvreq(transactionType, dataFormat, hconv, topic, item, count);
/* 1712 */         if (result != null) {
/* 1713 */           return result;
/*      */         }
/*      */       } 
/* 1716 */       return null;
/*      */     }
/*      */     
/* 1719 */     private final List<DdemlUtil.RequestHandler> requestHandler = new CopyOnWriteArrayList<DdemlUtil.RequestHandler>();
/*      */     
/*      */     public void registerRequestHandler(DdemlUtil.RequestHandler handler) {
/* 1722 */       this.requestHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterRequestHandler(DdemlUtil.RequestHandler handler) {
/* 1726 */       this.requestHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private Ddeml.HDDEDATA onRequest(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
/* 1730 */       for (DdemlUtil.RequestHandler handler : this.requestHandler) {
/* 1731 */         Ddeml.HDDEDATA result = handler.onRequest(transactionType, dataFormat, hconv, topic, item);
/* 1732 */         if (result != null) {
/* 1733 */           return result;
/*      */         }
/*      */       } 
/* 1736 */       return null;
/*      */     }
/*      */     
/* 1739 */     private final List<DdemlUtil.WildconnectHandler> wildconnectHandler = new CopyOnWriteArrayList<DdemlUtil.WildconnectHandler>();
/*      */     
/*      */     public void registerWildconnectHandler(DdemlUtil.WildconnectHandler handler) {
/* 1742 */       this.wildconnectHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterWildconnectHandler(DdemlUtil.WildconnectHandler handler) {
/* 1746 */       this.wildconnectHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private Ddeml.HSZPAIR[] onWildconnect(int transactionType, Ddeml.HSZ topic, Ddeml.HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
/* 1750 */       List<Ddeml.HSZPAIR> hszpairs = new ArrayList<Ddeml.HSZPAIR>(1);
/* 1751 */       for (DdemlUtil.WildconnectHandler handler : this.wildconnectHandler) {
/* 1752 */         hszpairs.addAll(handler.onWildconnect(transactionType, topic, service, convcontext, sameInstance));
/*      */       }
/* 1754 */       return hszpairs.<Ddeml.HSZPAIR>toArray(new Ddeml.HSZPAIR[0]);
/*      */     }
/*      */ 
/*      */     
/* 1758 */     private final List<DdemlUtil.AdvdataHandler> advdataHandler = new CopyOnWriteArrayList<DdemlUtil.AdvdataHandler>();
/*      */     
/*      */     public void registerAdvdataHandler(DdemlUtil.AdvdataHandler handler) {
/* 1761 */       this.advdataHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterAdvdataHandler(DdemlUtil.AdvdataHandler handler) {
/* 1765 */       this.advdataHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private int onAdvdata(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item, Ddeml.HDDEDATA hdata) {
/* 1769 */       for (DdemlUtil.AdvdataHandler handler : this.advdataHandler) {
/* 1770 */         int result = handler.onAdvdata(transactionType, dataFormat, hconv, topic, item, hdata);
/* 1771 */         if (result != 0) {
/* 1772 */           return result;
/*      */         }
/*      */       } 
/* 1775 */       return 0;
/*      */     }
/*      */     
/* 1778 */     private final List<DdemlUtil.ExecuteHandler> executeHandler = new CopyOnWriteArrayList<DdemlUtil.ExecuteHandler>();
/*      */     
/*      */     public void registerExecuteHandler(DdemlUtil.ExecuteHandler handler) {
/* 1781 */       this.executeHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterExecuteHandler(DdemlUtil.ExecuteHandler handler) {
/* 1785 */       this.executeHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private int onExecute(int transactionType, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HDDEDATA commandString) {
/* 1789 */       for (DdemlUtil.ExecuteHandler handler : this.executeHandler) {
/* 1790 */         int result = handler.onExecute(transactionType, hconv, topic, commandString);
/* 1791 */         if (result != 0) {
/* 1792 */           return result;
/*      */         }
/*      */       } 
/* 1795 */       return 0;
/*      */     }
/*      */     
/* 1798 */     private final List<DdemlUtil.PokeHandler> pokeHandler = new CopyOnWriteArrayList<DdemlUtil.PokeHandler>();
/*      */     
/*      */     public void registerPokeHandler(DdemlUtil.PokeHandler handler) {
/* 1801 */       this.pokeHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterPokeHandler(DdemlUtil.PokeHandler handler) {
/* 1805 */       this.pokeHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private int onPoke(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item, Ddeml.HDDEDATA hdata) {
/* 1809 */       for (DdemlUtil.PokeHandler handler : this.pokeHandler) {
/* 1810 */         int result = handler.onPoke(transactionType, dataFormat, hconv, topic, item, hdata);
/* 1811 */         if (result != 0) {
/* 1812 */           return result;
/*      */         }
/*      */       } 
/* 1815 */       return 0;
/*      */     }
/*      */     
/* 1818 */     private final List<DdemlUtil.ConnectConfirmHandler> connectConfirmHandler = new CopyOnWriteArrayList<DdemlUtil.ConnectConfirmHandler>();
/*      */     
/*      */     public void registerConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler handler) {
/* 1821 */       this.connectConfirmHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler handler) {
/* 1825 */       this.connectConfirmHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private void onConnectConfirm(int transactionType, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ service, boolean sameInstance) {
/* 1829 */       for (DdemlUtil.ConnectConfirmHandler handler : this.connectConfirmHandler) {
/* 1830 */         handler.onConnectConfirm(transactionType, hconv, topic, service, sameInstance);
/*      */       }
/*      */     }
/*      */     
/* 1834 */     private final List<DdemlUtil.DisconnectHandler> disconnectHandler = new CopyOnWriteArrayList<DdemlUtil.DisconnectHandler>();
/*      */     
/*      */     public void registerDisconnectHandler(DdemlUtil.DisconnectHandler handler) {
/* 1837 */       this.disconnectHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterDisconnectHandler(DdemlUtil.DisconnectHandler handler) {
/* 1841 */       this.disconnectHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private void onDisconnect(int transactionType, Ddeml.HCONV hconv, boolean sameInstance) {
/* 1845 */       for (DdemlUtil.DisconnectHandler handler : this.disconnectHandler) {
/* 1846 */         handler.onDisconnect(transactionType, hconv, sameInstance);
/*      */       }
/*      */     }
/*      */     
/* 1850 */     private final List<DdemlUtil.ErrorHandler> errorHandler = new CopyOnWriteArrayList<DdemlUtil.ErrorHandler>();
/*      */     
/*      */     public void registerErrorHandler(DdemlUtil.ErrorHandler handler) {
/* 1853 */       this.errorHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterErrorHandler(DdemlUtil.ErrorHandler handler) {
/* 1857 */       this.errorHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private void onError(int transactionType, Ddeml.HCONV hconv, int errorCode) {
/* 1861 */       for (DdemlUtil.ErrorHandler handler : this.errorHandler) {
/* 1862 */         handler.onError(transactionType, hconv, errorCode);
/*      */       }
/*      */     }
/*      */     
/* 1866 */     private final List<DdemlUtil.RegisterHandler> registerHandler = new CopyOnWriteArrayList<DdemlUtil.RegisterHandler>();
/*      */     
/*      */     public void registerRegisterHandler(DdemlUtil.RegisterHandler handler) {
/* 1869 */       this.registerHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterRegisterHandler(DdemlUtil.RegisterHandler handler) {
/* 1873 */       this.registerHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private void onRegister(int transactionType, Ddeml.HSZ baseServiceName, Ddeml.HSZ instanceSpecificServiceName) {
/* 1877 */       for (DdemlUtil.RegisterHandler handler : this.registerHandler) {
/* 1878 */         handler.onRegister(transactionType, baseServiceName, instanceSpecificServiceName);
/*      */       }
/*      */     }
/*      */     
/* 1882 */     private final List<DdemlUtil.XactCompleteHandler> xactCompleteHandler = new CopyOnWriteArrayList<DdemlUtil.XactCompleteHandler>();
/*      */     
/*      */     public void registerXactCompleteHandler(DdemlUtil.XactCompleteHandler handler) {
/* 1885 */       this.xactCompleteHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void xactCompleteXactCompleteHandler(DdemlUtil.XactCompleteHandler handler) {
/* 1889 */       this.xactCompleteHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private void onXactComplete(int transactionType, int dataFormat, Ddeml.HCONV hConv, Ddeml.HSZ topic, Ddeml.HSZ item, Ddeml.HDDEDATA hdata, BaseTSD.ULONG_PTR transactionIdentifier, BaseTSD.ULONG_PTR statusFlag) {
/* 1893 */       for (DdemlUtil.XactCompleteHandler handler : this.xactCompleteHandler) {
/* 1894 */         handler.onXactComplete(transactionType, dataFormat, hConv, topic, item, hdata, transactionIdentifier, statusFlag);
/*      */       }
/*      */     }
/*      */     
/* 1898 */     private final List<DdemlUtil.UnregisterHandler> unregisterHandler = new CopyOnWriteArrayList<DdemlUtil.UnregisterHandler>();
/*      */     
/*      */     public void registerUnregisterHandler(DdemlUtil.UnregisterHandler handler) {
/* 1901 */       this.unregisterHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterUnregisterHandler(DdemlUtil.UnregisterHandler handler) {
/* 1905 */       this.unregisterHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private void onUnregister(int transactionType, Ddeml.HSZ baseServiceName, Ddeml.HSZ instanceSpecificServiceName) {
/* 1909 */       for (DdemlUtil.UnregisterHandler handler : this.unregisterHandler) {
/* 1910 */         handler.onUnregister(transactionType, baseServiceName, instanceSpecificServiceName);
/*      */       }
/*      */     }
/*      */     
/* 1914 */     private final List<DdemlUtil.MonitorHandler> monitorHandler = new CopyOnWriteArrayList<DdemlUtil.MonitorHandler>();
/*      */     
/*      */     public void registerMonitorHandler(DdemlUtil.MonitorHandler handler) {
/* 1917 */       this.monitorHandler.add(handler);
/*      */     }
/*      */     
/*      */     public void unregisterMonitorHandler(DdemlUtil.MonitorHandler handler) {
/* 1921 */       this.monitorHandler.remove(handler);
/*      */     }
/*      */     
/*      */     private void onMonitor(int transactionType, Ddeml.HDDEDATA hdata, int dwData2) {
/* 1925 */       for (DdemlUtil.MonitorHandler handler : this.monitorHandler) {
/* 1926 */         handler.onMonitor(transactionType, hdata, dwData2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static class DdemlException
/*      */     extends RuntimeException
/*      */   {
/*      */     private static final Map<Integer, String> ERROR_CODE_MAP;
/*      */     private final int errorCode;
/*      */     
/*      */     static {
/* 1939 */       Map<Integer, String> errorCodeMapBuilder = new HashMap<Integer, String>();
/* 1940 */       for (Field f : Ddeml.class.getFields()) {
/* 1941 */         String name = f.getName();
/* 1942 */         if (name.startsWith("DMLERR_") && !name.equals("DMLERR_FIRST") && !name.equals("DMLERR_LAST")) {
/*      */           try {
/* 1944 */             errorCodeMapBuilder.put(Integer.valueOf(f.getInt(null)), name);
/* 1945 */           } catch (IllegalArgumentException ex) {
/* 1946 */             throw new RuntimeException(ex);
/* 1947 */           } catch (IllegalAccessException ex) {
/* 1948 */             throw new RuntimeException(ex);
/*      */           } 
/*      */         }
/*      */       } 
/* 1952 */       ERROR_CODE_MAP = Collections.unmodifiableMap(errorCodeMapBuilder);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static DdemlException create(int errorCode) {
/* 1958 */       String errorName = ERROR_CODE_MAP.get(Integer.valueOf(errorCode));
/* 1959 */       return new DdemlException(errorCode, String.format("%s (Code: 0x%X)", new Object[] { (errorName != null) ? errorName : "", 
/*      */               
/* 1961 */               Integer.valueOf(errorCode) }));
/*      */     }
/*      */     
/*      */     public DdemlException(int errorCode, String message) {
/* 1965 */       super(message);
/* 1966 */       this.errorCode = errorCode;
/*      */     }
/*      */     
/*      */     public int getErrorCode() {
/* 1970 */       return this.errorCode;
/*      */     }
/*      */   }
/*      */   
/*      */   public static interface IDdeConnection extends Closeable {
/*      */     Ddeml.HCONV getConv();
/*      */     
/*      */     void execute(String param1String, int param1Int, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
/*      */     
/*      */     void poke(Pointer param1Pointer, int param1Int1, Ddeml.HSZ param1HSZ, int param1Int2, int param1Int3, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
/*      */     
/*      */     void poke(Pointer param1Pointer, int param1Int1, String param1String, int param1Int2, int param1Int3, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
/*      */     
/*      */     Ddeml.HDDEDATA request(Ddeml.HSZ param1HSZ, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
/*      */     
/*      */     Ddeml.HDDEDATA request(String param1String, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
/*      */     
/*      */     Ddeml.HDDEDATA clientTransaction(Pointer param1Pointer, int param1Int1, Ddeml.HSZ param1HSZ, int param1Int2, int param1Int3, int param1Int4, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
/*      */     
/*      */     Ddeml.HDDEDATA clientTransaction(Pointer param1Pointer, int param1Int1, String param1String, int param1Int2, int param1Int3, int param1Int4, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
/*      */     
/*      */     void advstart(Ddeml.HSZ param1HSZ, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
/*      */     
/*      */     void advstart(String param1String, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
/*      */     
/*      */     void advstop(Ddeml.HSZ param1HSZ, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
/*      */     
/*      */     void advstop(String param1String, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
/*      */     
/*      */     void abandonTransaction(int param1Int);
/*      */     
/*      */     void abandonTransactions();
/*      */     
/*      */     void impersonateClient();
/*      */     
/*      */     void close();
/*      */     
/*      */     void reconnect();
/*      */     
/*      */     boolean enableCallback(int param1Int);
/*      */     
/*      */     void setUserHandle(int param1Int, BaseTSD.DWORD_PTR param1DWORD_PTR) throws DdemlUtil.DdemlException;
/*      */     
/*      */     Ddeml.CONVINFO queryConvInfo(int param1Int) throws DdemlUtil.DdemlException;
/*      */   }
/*      */   
/*      */   public static interface IDdeClient extends Closeable {
/*      */     Integer getInstanceIdentitifier();
/*      */     
/*      */     void initialize(int param1Int) throws DdemlUtil.DdemlException;
/*      */     
/*      */     Ddeml.HSZ createStringHandle(String param1String) throws DdemlUtil.DdemlException;
/*      */     
/*      */     String queryString(Ddeml.HSZ param1HSZ) throws DdemlUtil.DdemlException;
/*      */     
/*      */     boolean freeStringHandle(Ddeml.HSZ param1HSZ);
/*      */     
/*      */     boolean keepStringHandle(Ddeml.HSZ param1HSZ);
/*      */     
/*      */     void nameService(Ddeml.HSZ param1HSZ, int param1Int) throws DdemlUtil.DdemlException;
/*      */     
/*      */     void nameService(String param1String, int param1Int) throws DdemlUtil.DdemlException;
/*      */     
/*      */     int getLastError();
/*      */     
/*      */     DdemlUtil.IDdeConnection connect(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.CONVCONTEXT param1CONVCONTEXT);
/*      */     
/*      */     DdemlUtil.IDdeConnection connect(String param1String1, String param1String2, Ddeml.CONVCONTEXT param1CONVCONTEXT);
/*      */     
/*      */     Ddeml.HDDEDATA createDataHandle(Pointer param1Pointer, int param1Int1, int param1Int2, Ddeml.HSZ param1HSZ, int param1Int3, int param1Int4);
/*      */     
/*      */     void freeDataHandle(Ddeml.HDDEDATA param1HDDEDATA);
/*      */     
/*      */     Ddeml.HDDEDATA addData(Ddeml.HDDEDATA param1HDDEDATA, Pointer param1Pointer, int param1Int1, int param1Int2);
/*      */     
/*      */     int getData(Ddeml.HDDEDATA param1HDDEDATA, Pointer param1Pointer, int param1Int1, int param1Int2);
/*      */     
/*      */     Pointer accessData(Ddeml.HDDEDATA param1HDDEDATA, WinDef.DWORDByReference param1DWORDByReference);
/*      */     
/*      */     void unaccessData(Ddeml.HDDEDATA param1HDDEDATA);
/*      */     
/*      */     void postAdvise(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
/*      */     
/*      */     void postAdvise(String param1String1, String param1String2);
/*      */     
/*      */     void abandonTransactions();
/*      */     
/*      */     DdemlUtil.IDdeConnectionList connectList(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, DdemlUtil.IDdeConnectionList param1IDdeConnectionList, Ddeml.CONVCONTEXT param1CONVCONTEXT);
/*      */     
/*      */     DdemlUtil.IDdeConnectionList connectList(String param1String1, String param1String2, DdemlUtil.IDdeConnectionList param1IDdeConnectionList, Ddeml.CONVCONTEXT param1CONVCONTEXT);
/*      */     
/*      */     boolean enableCallback(int param1Int);
/*      */     
/*      */     boolean uninitialize();
/*      */     
/*      */     DdemlUtil.IDdeConnection wrap(Ddeml.HCONV param1HCONV);
/*      */     
/*      */     void registerAdvstartHandler(DdemlUtil.AdvstartHandler param1AdvstartHandler);
/*      */     
/*      */     void unregisterAdvstartHandler(DdemlUtil.AdvstartHandler param1AdvstartHandler);
/*      */     
/*      */     void registerAdvstopHandler(DdemlUtil.AdvstopHandler param1AdvstopHandler);
/*      */     
/*      */     void unregisterAdvstopHandler(DdemlUtil.AdvstopHandler param1AdvstopHandler);
/*      */     
/*      */     void registerConnectHandler(DdemlUtil.ConnectHandler param1ConnectHandler);
/*      */     
/*      */     void unregisterConnectHandler(DdemlUtil.ConnectHandler param1ConnectHandler);
/*      */     
/*      */     void registerAdvReqHandler(DdemlUtil.AdvreqHandler param1AdvreqHandler);
/*      */     
/*      */     void unregisterAdvReqHandler(DdemlUtil.AdvreqHandler param1AdvreqHandler);
/*      */     
/*      */     void registerRequestHandler(DdemlUtil.RequestHandler param1RequestHandler);
/*      */     
/*      */     void unregisterRequestHandler(DdemlUtil.RequestHandler param1RequestHandler);
/*      */     
/*      */     void registerWildconnectHandler(DdemlUtil.WildconnectHandler param1WildconnectHandler);
/*      */     
/*      */     void unregisterWildconnectHandler(DdemlUtil.WildconnectHandler param1WildconnectHandler);
/*      */     
/*      */     void registerAdvdataHandler(DdemlUtil.AdvdataHandler param1AdvdataHandler);
/*      */     
/*      */     void unregisterAdvdataHandler(DdemlUtil.AdvdataHandler param1AdvdataHandler);
/*      */     
/*      */     void registerExecuteHandler(DdemlUtil.ExecuteHandler param1ExecuteHandler);
/*      */     
/*      */     void unregisterExecuteHandler(DdemlUtil.ExecuteHandler param1ExecuteHandler);
/*      */     
/*      */     void registerPokeHandler(DdemlUtil.PokeHandler param1PokeHandler);
/*      */     
/*      */     void unregisterPokeHandler(DdemlUtil.PokeHandler param1PokeHandler);
/*      */     
/*      */     void registerConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler param1ConnectConfirmHandler);
/*      */     
/*      */     void unregisterConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler param1ConnectConfirmHandler);
/*      */     
/*      */     void registerDisconnectHandler(DdemlUtil.DisconnectHandler param1DisconnectHandler);
/*      */     
/*      */     void unregisterDisconnectHandler(DdemlUtil.DisconnectHandler param1DisconnectHandler);
/*      */     
/*      */     void registerErrorHandler(DdemlUtil.ErrorHandler param1ErrorHandler);
/*      */     
/*      */     void unregisterErrorHandler(DdemlUtil.ErrorHandler param1ErrorHandler);
/*      */     
/*      */     void registerRegisterHandler(DdemlUtil.RegisterHandler param1RegisterHandler);
/*      */     
/*      */     void unregisterRegisterHandler(DdemlUtil.RegisterHandler param1RegisterHandler);
/*      */     
/*      */     void registerXactCompleteHandler(DdemlUtil.XactCompleteHandler param1XactCompleteHandler);
/*      */     
/*      */     void unregisterXactCompleteHandler(DdemlUtil.XactCompleteHandler param1XactCompleteHandler);
/*      */     
/*      */     void registerUnregisterHandler(DdemlUtil.UnregisterHandler param1UnregisterHandler);
/*      */     
/*      */     void unregisterUnregisterHandler(DdemlUtil.UnregisterHandler param1UnregisterHandler);
/*      */     
/*      */     void registerMonitorHandler(DdemlUtil.MonitorHandler param1MonitorHandler);
/*      */     
/*      */     void unregisterMonitorHandler(DdemlUtil.MonitorHandler param1MonitorHandler);
/*      */   }
/*      */   
/*      */   public static interface IDdeConnectionList extends Closeable {
/*      */     Ddeml.HCONVLIST getHandle();
/*      */     
/*      */     DdemlUtil.IDdeConnection queryNextServer(DdemlUtil.IDdeConnection param1IDdeConnection);
/*      */     
/*      */     void close();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\DdemlUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */