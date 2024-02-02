/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.io.Closeable;
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
/*     */ public class W32ServiceManager
/*     */   implements Closeable
/*     */ {
/*  40 */   Winsvc.SC_HANDLE _handle = null;
/*  41 */   String _machineName = null;
/*  42 */   String _databaseName = null;
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
/*     */   public W32ServiceManager() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public W32ServiceManager(int permissions) {
/*  64 */     open(permissions);
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
/*     */   public W32ServiceManager(String machineName, String databaseName) {
/*  83 */     this._machineName = machineName;
/*  84 */     this._databaseName = databaseName;
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
/*     */   public W32ServiceManager(String machineName, String databaseName, int permissions) {
/* 105 */     this._machineName = machineName;
/* 106 */     this._databaseName = databaseName;
/* 107 */     open(permissions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void open(int permissions) {
/* 116 */     close();
/*     */     
/* 118 */     this._handle = Advapi32.INSTANCE.OpenSCManager(this._machineName, this._databaseName, permissions);
/*     */ 
/*     */     
/* 121 */     if (this._handle == null) {
/* 122 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 131 */     if (this._handle != null) {
/* 132 */       if (!Advapi32.INSTANCE.CloseServiceHandle(this._handle)) {
/* 133 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */       }
/* 135 */       this._handle = null;
/*     */     } 
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
/*     */   public W32Service openService(String serviceName, int permissions) {
/* 148 */     Winsvc.SC_HANDLE serviceHandle = Advapi32.INSTANCE.OpenService(this._handle, serviceName, permissions);
/*     */ 
/*     */     
/* 151 */     if (serviceHandle == null) {
/* 152 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */     
/* 155 */     return new W32Service(serviceHandle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Winsvc.SC_HANDLE getHandle() {
/* 164 */     return this._handle;
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
/*     */   public Winsvc.ENUM_SERVICE_STATUS_PROCESS[] enumServicesStatusExProcess(int dwServiceType, int dwServiceState, String groupName) {
/* 221 */     IntByReference pcbBytesNeeded = new IntByReference(0);
/* 222 */     IntByReference lpServicesReturned = new IntByReference(0);
/* 223 */     IntByReference lpResumeHandle = new IntByReference(0);
/* 224 */     Advapi32.INSTANCE.EnumServicesStatusEx(this._handle, 0, dwServiceType, dwServiceState, Pointer.NULL, 0, pcbBytesNeeded, lpServicesReturned, lpResumeHandle, groupName);
/* 225 */     int lastError = Kernel32.INSTANCE.GetLastError();
/* 226 */     if (lastError != 234) {
/* 227 */       throw new Win32Exception(lastError);
/*     */     }
/* 229 */     Memory buffer = new Memory(pcbBytesNeeded.getValue());
/* 230 */     boolean result = Advapi32.INSTANCE.EnumServicesStatusEx(this._handle, 0, dwServiceType, dwServiceState, (Pointer)buffer, (int)buffer.size(), pcbBytesNeeded, lpServicesReturned, lpResumeHandle, groupName);
/* 231 */     if (!result) {
/* 232 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/* 234 */     if (lpServicesReturned.getValue() == 0) {
/* 235 */       return new Winsvc.ENUM_SERVICE_STATUS_PROCESS[0];
/*     */     }
/* 237 */     Winsvc.ENUM_SERVICE_STATUS_PROCESS status = (Winsvc.ENUM_SERVICE_STATUS_PROCESS)Structure.newInstance(Winsvc.ENUM_SERVICE_STATUS_PROCESS.class, (Pointer)buffer);
/* 238 */     status.read();
/* 239 */     return (Winsvc.ENUM_SERVICE_STATUS_PROCESS[])status.toArray(lpServicesReturned.getValue());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\W32ServiceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */