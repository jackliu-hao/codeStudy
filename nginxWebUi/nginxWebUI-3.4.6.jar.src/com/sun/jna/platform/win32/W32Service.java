/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.io.Closeable;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class W32Service
/*     */   implements Closeable
/*     */ {
/*  56 */   Winsvc.SC_HANDLE _handle = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public W32Service(Winsvc.SC_HANDLE handle) {
/*  66 */     this._handle = handle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*  74 */     if (this._handle != null) {
/*  75 */       if (!Advapi32.INSTANCE.CloseServiceHandle(this._handle)) {
/*  76 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */       }
/*  78 */       this._handle = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addShutdownPrivilegeToProcess() {
/*  83 */     WinNT.HANDLEByReference hToken = new WinNT.HANDLEByReference();
/*  84 */     WinNT.LUID luid = new WinNT.LUID();
/*  85 */     Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 32, hToken);
/*     */     
/*  87 */     Advapi32.INSTANCE.LookupPrivilegeValue("", "SeShutdownPrivilege", luid);
/*  88 */     WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(1);
/*  89 */     tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(luid, new WinDef.DWORD(2L));
/*  90 */     Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.getValue(), false, tp, tp.size(), null, new IntByReference());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFailureActions(List<Winsvc.SC_ACTION> actions, int resetPeriod, String rebootMsg, String command) {
/* 101 */     Winsvc.SERVICE_FAILURE_ACTIONS.ByReference actionStruct = new Winsvc.SERVICE_FAILURE_ACTIONS.ByReference();
/* 102 */     actionStruct.dwResetPeriod = resetPeriod;
/* 103 */     actionStruct.lpRebootMsg = rebootMsg;
/* 104 */     actionStruct.lpCommand = command;
/* 105 */     actionStruct.cActions = actions.size();
/*     */     
/* 107 */     actionStruct.lpsaActions = new Winsvc.SC_ACTION.ByReference();
/* 108 */     Winsvc.SC_ACTION[] actionArray = (Winsvc.SC_ACTION[])actionStruct.lpsaActions.toArray(actions.size());
/* 109 */     boolean hasShutdownPrivilege = false;
/* 110 */     int i = 0;
/* 111 */     for (Winsvc.SC_ACTION action : actions) {
/* 112 */       if (!hasShutdownPrivilege && action.type == 2) {
/* 113 */         addShutdownPrivilegeToProcess();
/* 114 */         hasShutdownPrivilege = true;
/*     */       } 
/* 116 */       (actionArray[i]).type = action.type;
/* 117 */       (actionArray[i]).delay = action.delay;
/* 118 */       i++;
/*     */     } 
/*     */     
/* 121 */     if (!Advapi32.INSTANCE.ChangeServiceConfig2(this._handle, 2, actionStruct))
/*     */     {
/* 123 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */   }
/*     */   
/*     */   private Pointer queryServiceConfig2(int type) {
/* 128 */     IntByReference bufferSize = new IntByReference();
/* 129 */     Advapi32.INSTANCE.QueryServiceConfig2(this._handle, type, Pointer.NULL, 0, bufferSize);
/*     */     
/* 131 */     Memory memory = new Memory(bufferSize.getValue());
/*     */     
/* 133 */     if (!Advapi32.INSTANCE.QueryServiceConfig2(this._handle, type, (Pointer)memory, bufferSize.getValue(), new IntByReference()))
/*     */     {
/* 135 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */     
/* 138 */     return (Pointer)memory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Winsvc.SERVICE_FAILURE_ACTIONS getFailureActions() {
/* 147 */     Pointer buffer = queryServiceConfig2(2);
/* 148 */     Winsvc.SERVICE_FAILURE_ACTIONS result = new Winsvc.SERVICE_FAILURE_ACTIONS(buffer);
/* 149 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFailureActionsFlag(boolean flagValue) {
/* 158 */     Winsvc.SERVICE_FAILURE_ACTIONS_FLAG flag = new Winsvc.SERVICE_FAILURE_ACTIONS_FLAG();
/* 159 */     flag.fFailureActionsOnNonCrashFailures = flagValue ? 1 : 0;
/*     */     
/* 161 */     if (!Advapi32.INSTANCE.ChangeServiceConfig2(this._handle, 4, flag))
/*     */     {
/* 163 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getFailureActionsFlag() {
/* 173 */     Pointer buffer = queryServiceConfig2(4);
/* 174 */     Winsvc.SERVICE_FAILURE_ACTIONS_FLAG result = new Winsvc.SERVICE_FAILURE_ACTIONS_FLAG(buffer);
/* 175 */     return (result.fFailureActionsOnNonCrashFailures != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Winsvc.SERVICE_STATUS_PROCESS queryStatus() {
/* 185 */     IntByReference size = new IntByReference();
/*     */     
/* 187 */     Advapi32.INSTANCE.QueryServiceStatusEx(this._handle, 0, null, 0, size);
/*     */ 
/*     */     
/* 190 */     Winsvc.SERVICE_STATUS_PROCESS status = new Winsvc.SERVICE_STATUS_PROCESS(size.getValue());
/* 191 */     if (!Advapi32.INSTANCE.QueryServiceStatusEx(this._handle, 0, status, status
/* 192 */         .size(), size)) {
/* 193 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */     
/* 196 */     return status;
/*     */   }
/*     */   
/*     */   public void startService() {
/* 200 */     waitForNonPendingState();
/*     */     
/* 202 */     if ((queryStatus()).dwCurrentState == 4) {
/*     */       return;
/*     */     }
/* 205 */     if (!Advapi32.INSTANCE.StartService(this._handle, 0, null)) {
/* 206 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/* 208 */     waitForNonPendingState();
/* 209 */     if ((queryStatus()).dwCurrentState != 4) {
/* 210 */       throw new RuntimeException("Unable to start the service");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stopService() {
/* 218 */     stopService(30000L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stopService(long timeout) {
/* 227 */     long startTime = System.currentTimeMillis();
/* 228 */     waitForNonPendingState();
/*     */     
/* 230 */     if ((queryStatus()).dwCurrentState == 1) {
/*     */       return;
/*     */     }
/* 233 */     Winsvc.SERVICE_STATUS status = new Winsvc.SERVICE_STATUS();
/* 234 */     if (!Advapi32.INSTANCE.ControlService(this._handle, 1, status)) {
/* 235 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 240 */     while (status.dwCurrentState != 1) {
/* 241 */       long msRemainingBeforeTimeout = timeout - System.currentTimeMillis() - startTime;
/*     */       
/* 243 */       if (msRemainingBeforeTimeout < 0L) {
/* 244 */         throw new RuntimeException(String.format("Service stop exceeded timeout time of %d ms", new Object[] { Long.valueOf(timeout) }));
/*     */       }
/*     */       
/* 247 */       long dwWaitTime = Math.min(sanitizeWaitTime(status.dwWaitHint), msRemainingBeforeTimeout);
/*     */       
/*     */       try {
/* 250 */         Thread.sleep(dwWaitTime);
/* 251 */       } catch (InterruptedException e) {
/* 252 */         throw new RuntimeException(e);
/*     */       } 
/* 254 */       if (!Advapi32.INSTANCE.QueryServiceStatus(this._handle, status)) {
/* 255 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void continueService() {
/* 264 */     waitForNonPendingState();
/*     */     
/* 266 */     if ((queryStatus()).dwCurrentState == 4) {
/*     */       return;
/*     */     }
/* 269 */     if (!Advapi32.INSTANCE.ControlService(this._handle, 3, new Winsvc.SERVICE_STATUS()))
/*     */     {
/* 271 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/* 273 */     waitForNonPendingState();
/* 274 */     if ((queryStatus()).dwCurrentState != 4) {
/* 275 */       throw new RuntimeException("Unable to continue the service");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void pauseService() {
/* 283 */     waitForNonPendingState();
/*     */     
/* 285 */     if ((queryStatus()).dwCurrentState == 7) {
/*     */       return;
/*     */     }
/* 288 */     if (!Advapi32.INSTANCE.ControlService(this._handle, 2, new Winsvc.SERVICE_STATUS()))
/*     */     {
/* 290 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/* 292 */     waitForNonPendingState();
/* 293 */     if ((queryStatus()).dwCurrentState != 7) {
/* 294 */       throw new RuntimeException("Unable to pause the service");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int sanitizeWaitTime(int dwWaitHint) {
/* 303 */     int dwWaitTime = dwWaitHint / 10;
/*     */     
/* 305 */     if (dwWaitTime < 1000) {
/* 306 */       dwWaitTime = 1000;
/* 307 */     } else if (dwWaitTime > 10000) {
/* 308 */       dwWaitTime = 10000;
/*     */     } 
/* 310 */     return dwWaitTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void waitForNonPendingState() {
/* 318 */     Winsvc.SERVICE_STATUS_PROCESS status = queryStatus();
/*     */     
/* 320 */     int previousCheckPoint = status.dwCheckPoint;
/* 321 */     int checkpointStartTickCount = Kernel32.INSTANCE.GetTickCount();
/*     */     
/* 323 */     while (isPendingState(status.dwCurrentState)) {
/*     */ 
/*     */       
/* 326 */       if (status.dwCheckPoint != previousCheckPoint) {
/* 327 */         previousCheckPoint = status.dwCheckPoint;
/* 328 */         checkpointStartTickCount = Kernel32.INSTANCE.GetTickCount();
/*     */       } 
/*     */ 
/*     */       
/* 332 */       if (Kernel32.INSTANCE.GetTickCount() - checkpointStartTickCount > status.dwWaitHint) {
/* 333 */         throw new RuntimeException("Timeout waiting for service to change to a non-pending state.");
/*     */       }
/*     */       
/* 336 */       int dwWaitTime = sanitizeWaitTime(status.dwWaitHint);
/*     */       
/*     */       try {
/* 339 */         Thread.sleep(dwWaitTime);
/* 340 */       } catch (InterruptedException e) {
/* 341 */         throw new RuntimeException(e);
/*     */       } 
/*     */       
/* 344 */       status = queryStatus();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isPendingState(int state) {
/* 349 */     switch (state) {
/*     */       case 2:
/*     */       case 3:
/*     */       case 5:
/*     */       case 6:
/* 354 */         return true;
/*     */     } 
/* 356 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Winsvc.SC_HANDLE getHandle() {
/* 366 */     return this._handle;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Winsvc.ENUM_SERVICE_STATUS[] enumDependentServices(int dwServiceState) {
/* 394 */     IntByReference pcbBytesNeeded = new IntByReference(0);
/* 395 */     IntByReference lpServicesReturned = new IntByReference(0);
/* 396 */     Advapi32.INSTANCE.EnumDependentServices(this._handle, dwServiceState, Pointer.NULL, 0, pcbBytesNeeded, lpServicesReturned);
/* 397 */     int lastError = Kernel32.INSTANCE.GetLastError();
/* 398 */     if (lastError != 234) {
/* 399 */       throw new Win32Exception(lastError);
/*     */     }
/* 401 */     Memory buffer = new Memory(pcbBytesNeeded.getValue());
/* 402 */     boolean result = Advapi32.INSTANCE.EnumDependentServices(this._handle, dwServiceState, (Pointer)buffer, (int)buffer.size(), pcbBytesNeeded, lpServicesReturned);
/* 403 */     if (!result) {
/* 404 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/* 406 */     if (lpServicesReturned.getValue() == 0) {
/* 407 */       return new Winsvc.ENUM_SERVICE_STATUS[0];
/*     */     }
/* 409 */     Winsvc.ENUM_SERVICE_STATUS status = (Winsvc.ENUM_SERVICE_STATUS)Structure.newInstance(Winsvc.ENUM_SERVICE_STATUS.class, (Pointer)buffer);
/* 410 */     status.read();
/* 411 */     return (Winsvc.ENUM_SERVICE_STATUS[])status.toArray(lpServicesReturned.getValue());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\W32Service.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */