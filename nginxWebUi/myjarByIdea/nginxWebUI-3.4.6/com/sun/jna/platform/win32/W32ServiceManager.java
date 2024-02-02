package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import java.io.Closeable;

public class W32ServiceManager implements Closeable {
   Winsvc.SC_HANDLE _handle = null;
   String _machineName = null;
   String _databaseName = null;

   public W32ServiceManager() {
   }

   public W32ServiceManager(int permissions) {
      this.open(permissions);
   }

   public W32ServiceManager(String machineName, String databaseName) {
      this._machineName = machineName;
      this._databaseName = databaseName;
   }

   public W32ServiceManager(String machineName, String databaseName, int permissions) {
      this._machineName = machineName;
      this._databaseName = databaseName;
      this.open(permissions);
   }

   public void open(int permissions) {
      this.close();
      this._handle = Advapi32.INSTANCE.OpenSCManager(this._machineName, this._databaseName, permissions);
      if (this._handle == null) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      }
   }

   public void close() {
      if (this._handle != null) {
         if (!Advapi32.INSTANCE.CloseServiceHandle(this._handle)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }

         this._handle = null;
      }

   }

   public W32Service openService(String serviceName, int permissions) {
      Winsvc.SC_HANDLE serviceHandle = Advapi32.INSTANCE.OpenService(this._handle, serviceName, permissions);
      if (serviceHandle == null) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return new W32Service(serviceHandle);
      }
   }

   public Winsvc.SC_HANDLE getHandle() {
      return this._handle;
   }

   public Winsvc.ENUM_SERVICE_STATUS_PROCESS[] enumServicesStatusExProcess(int dwServiceType, int dwServiceState, String groupName) {
      IntByReference pcbBytesNeeded = new IntByReference(0);
      IntByReference lpServicesReturned = new IntByReference(0);
      IntByReference lpResumeHandle = new IntByReference(0);
      Advapi32.INSTANCE.EnumServicesStatusEx(this._handle, 0, dwServiceType, dwServiceState, Pointer.NULL, 0, pcbBytesNeeded, lpServicesReturned, lpResumeHandle, groupName);
      int lastError = Kernel32.INSTANCE.GetLastError();
      if (lastError != 234) {
         throw new Win32Exception(lastError);
      } else {
         Memory buffer = new Memory((long)pcbBytesNeeded.getValue());
         boolean result = Advapi32.INSTANCE.EnumServicesStatusEx(this._handle, 0, dwServiceType, dwServiceState, buffer, (int)buffer.size(), pcbBytesNeeded, lpServicesReturned, lpResumeHandle, groupName);
         if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else if (lpServicesReturned.getValue() == 0) {
            return new Winsvc.ENUM_SERVICE_STATUS_PROCESS[0];
         } else {
            Winsvc.ENUM_SERVICE_STATUS_PROCESS status = (Winsvc.ENUM_SERVICE_STATUS_PROCESS)Structure.newInstance(Winsvc.ENUM_SERVICE_STATUS_PROCESS.class, buffer);
            status.read();
            return (Winsvc.ENUM_SERVICE_STATUS_PROCESS[])((Winsvc.ENUM_SERVICE_STATUS_PROCESS[])status.toArray(lpServicesReturned.getValue()));
         }
      }
   }
}
