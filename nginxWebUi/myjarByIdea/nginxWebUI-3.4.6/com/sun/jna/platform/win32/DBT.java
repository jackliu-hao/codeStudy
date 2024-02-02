package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface DBT {
   int DBT_NO_DISK_SPACE = 71;
   int DBT_LOW_DISK_SPACE = 72;
   int DBT_CONFIGMGPRIVATE = 32767;
   int DBT_DEVICEARRIVAL = 32768;
   int DBT_DEVICEQUERYREMOVE = 32769;
   int DBT_DEVICEQUERYREMOVEFAILED = 32770;
   int DBT_DEVICEREMOVEPENDING = 32771;
   int DBT_DEVICEREMOVECOMPLETE = 32772;
   int DBT_DEVNODES_CHANGED = 7;
   int DBT_DEVICETYPESPECIFIC = 32773;
   int DBT_CUSTOMEVENT = 32774;
   Guid.GUID GUID_DEVINTERFACE_USB_DEVICE = new Guid.GUID("{A5DCBF10-6530-11D2-901F-00C04FB951ED}");
   Guid.GUID GUID_DEVINTERFACE_HID = new Guid.GUID("{4D1E55B2-F16F-11CF-88CB-001111000030}");
   Guid.GUID GUID_DEVINTERFACE_VOLUME = new Guid.GUID("{53F5630D-B6BF-11D0-94F2-00A0C91EFB8B}");
   Guid.GUID GUID_DEVINTERFACE_KEYBOARD = new Guid.GUID("{884b96c3-56ef-11d1-bc8c-00a0c91405dd}");
   Guid.GUID GUID_DEVINTERFACE_MOUSE = new Guid.GUID("{378DE44C-56EF-11D1-BC8C-00A0C91405DD}");
   int DBT_DEVTYP_OEM = 0;
   int DBT_DEVTYP_DEVNODE = 1;
   int DBT_DEVTYP_VOLUME = 2;
   int DBT_DEVTYP_PORT = 3;
   int DBT_DEVTYP_NET = 4;
   int DBT_DEVTYP_DEVICEINTERFACE = 5;
   int DBT_DEVTYP_HANDLE = 6;
   int DBTF_MEDIA = 1;
   int DBTF_NET = 2;

   @Structure.FieldOrder({"dbch_size", "dbch_devicetype", "dbch_reserved", "dbch_handle", "dbch_hdevnotify", "dbch_eventguid", "dbch_nameoffset", "dbch_data"})
   public static class DEV_BROADCAST_HANDLE extends Structure {
      public int dbch_size = this.size();
      public int dbch_devicetype;
      public int dbch_reserved;
      public WinNT.HANDLE dbch_handle;
      public WinUser.HDEVNOTIFY dbch_hdevnotify;
      public Guid.GUID dbch_eventguid;
      public WinDef.LONG dbch_nameoffset;
      public byte[] dbch_data;

      public DEV_BROADCAST_HANDLE() {
      }

      public DEV_BROADCAST_HANDLE(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"dbcc_size", "dbcc_devicetype", "dbcc_reserved", "dbcc_classguid", "dbcc_name"})
   public static class DEV_BROADCAST_DEVICEINTERFACE extends Structure {
      public int dbcc_size;
      public int dbcc_devicetype;
      public int dbcc_reserved;
      public Guid.GUID dbcc_classguid;
      public char[] dbcc_name;

      public DEV_BROADCAST_DEVICEINTERFACE() {
         this.dbcc_name = new char[1];
      }

      public DEV_BROADCAST_DEVICEINTERFACE(long pointer) {
         this(new Pointer(pointer));
      }

      public DEV_BROADCAST_DEVICEINTERFACE(Pointer memory) {
         super(memory);
         this.dbcc_name = new char[1];
         this.dbcc_size = (Integer)this.readField("dbcc_size");
         int len = 1 + this.dbcc_size - this.size();
         this.dbcc_name = new char[len];
         this.read();
      }

      public String getDbcc_name() {
         return Native.toString(this.dbcc_name);
      }
   }

   @Structure.FieldOrder({"dbcn_size", "dbcn_devicetype", "dbcn_reserved", "dbcn_resource", "dbcn_flags"})
   public static class DEV_BROADCAST_NET extends Structure {
      public int dbcn_size = this.size();
      public int dbcn_devicetype;
      public int dbcn_reserved;
      public int dbcn_resource;
      public int dbcn_flags;

      public DEV_BROADCAST_NET() {
      }

      public DEV_BROADCAST_NET(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"dbcp_size", "dbcp_devicetype", "dbcp_reserved", "dbcp_name"})
   public static class DEV_BROADCAST_PORT extends Structure {
      public int dbcp_size = this.size();
      public int dbcp_devicetype;
      public int dbcp_reserved;
      public char[] dbcp_name = new char[1];

      public DEV_BROADCAST_PORT() {
      }

      public DEV_BROADCAST_PORT(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"dbcv_size", "dbcv_devicetype", "dbcv_reserved", "dbcv_unitmask", "dbcv_flags"})
   public static class DEV_BROADCAST_VOLUME extends Structure {
      public int dbcv_size = this.size();
      public int dbcv_devicetype;
      public int dbcv_reserved;
      public int dbcv_unitmask;
      public short dbcv_flags;

      public DEV_BROADCAST_VOLUME() {
      }

      public DEV_BROADCAST_VOLUME(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"dbcd_size", "dbcd_devicetype", "dbcd_reserved", "dbcd_devnode"})
   public static class DEV_BROADCAST_DEVNODE extends Structure {
      public int dbcd_size = this.size();
      public int dbcd_devicetype;
      public int dbcd_reserved;
      public int dbcd_devnode;

      public DEV_BROADCAST_DEVNODE() {
      }

      public DEV_BROADCAST_DEVNODE(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"dbco_size", "dbco_devicetype", "dbco_reserved", "dbco_identifier", "dbco_suppfunc"})
   public static class DEV_BROADCAST_OEM extends Structure {
      public int dbco_size = this.size();
      public int dbco_devicetype;
      public int dbco_reserved;
      public int dbco_identifier;
      public int dbco_suppfunc;

      public DEV_BROADCAST_OEM() {
      }

      public DEV_BROADCAST_OEM(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"dbch_size", "dbch_devicetype", "dbch_reserved"})
   public static class DEV_BROADCAST_HDR extends Structure {
      public int dbch_size;
      public int dbch_devicetype;
      public int dbch_reserved;

      public DEV_BROADCAST_HDR() {
         this.dbch_size = this.size();
      }

      public DEV_BROADCAST_HDR(long pointer) {
         this(new Pointer(pointer));
      }

      public DEV_BROADCAST_HDR(Pointer memory) {
         super(memory);
         this.dbch_size = this.size();
         this.read();
      }
   }
}
