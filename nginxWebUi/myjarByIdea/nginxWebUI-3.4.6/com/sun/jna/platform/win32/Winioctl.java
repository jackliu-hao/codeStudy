package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface Winioctl {
   int FILE_DEVICE_BEEP = 1;
   int FILE_DEVICE_CD_ROM = 2;
   int FILE_DEVICE_CD_ROM_FILE_SYSTEM = 3;
   int FILE_DEVICE_CONTROLLER = 4;
   int FILE_DEVICE_DATALINK = 5;
   int FILE_DEVICE_DFS = 6;
   int FILE_DEVICE_DISK = 7;
   int FILE_DEVICE_DISK_FILE_SYSTEM = 8;
   int FILE_DEVICE_FILE_SYSTEM = 9;
   int FILE_DEVICE_INPORT_PORT = 10;
   int FILE_DEVICE_KEYBOARD = 11;
   int FILE_DEVICE_MAILSLOT = 12;
   int FILE_DEVICE_MIDI_IN = 13;
   int FILE_DEVICE_MIDI_OUT = 14;
   int FILE_DEVICE_MOUSE = 15;
   int FILE_DEVICE_MULTI_UNC_PROVIDER = 16;
   int FILE_DEVICE_NAMED_PIPE = 17;
   int FILE_DEVICE_NETWORK = 18;
   int FILE_DEVICE_NETWORK_BROWSER = 19;
   int FILE_DEVICE_NETWORK_FILE_SYSTEM = 20;
   int FILE_DEVICE_NULL = 21;
   int FILE_DEVICE_PARALLEL_PORT = 22;
   int FILE_DEVICE_PHYSICAL_NETCARD = 23;
   int FILE_DEVICE_PRINTER = 24;
   int FILE_DEVICE_SCANNER = 25;
   int FILE_DEVICE_SERIAL_MOUSE_PORT = 26;
   int FILE_DEVICE_SERIAL_PORT = 27;
   int FILE_DEVICE_SCREEN = 28;
   int FILE_DEVICE_SOUND = 29;
   int FILE_DEVICE_STREAMS = 30;
   int FILE_DEVICE_TAPE = 31;
   int FILE_DEVICE_TAPE_FILE_SYSTEM = 32;
   int FILE_DEVICE_TRANSPORT = 33;
   int FILE_DEVICE_UNKNOWN = 34;
   int FILE_DEVICE_VIDEO = 35;
   int FILE_DEVICE_VIRTUAL_DISK = 36;
   int FILE_DEVICE_WAVE_IN = 37;
   int FILE_DEVICE_WAVE_OUT = 38;
   int FILE_DEVICE_8042_PORT = 39;
   int FILE_DEVICE_NETWORK_REDIRECTOR = 40;
   int FILE_DEVICE_BATTERY = 41;
   int FILE_DEVICE_BUS_EXTENDER = 42;
   int FILE_DEVICE_MODEM = 43;
   int FILE_DEVICE_VDM = 44;
   int FILE_DEVICE_MASS_STORAGE = 45;
   int FILE_DEVICE_SMB = 46;
   int FILE_DEVICE_KS = 47;
   int FILE_DEVICE_CHANGER = 48;
   int FILE_DEVICE_SMARTCARD = 49;
   int FILE_DEVICE_ACPI = 50;
   int FILE_DEVICE_DVD = 51;
   int FILE_DEVICE_FULLSCREEN_VIDEO = 52;
   int FILE_DEVICE_DFS_FILE_SYSTEM = 53;
   int FILE_DEVICE_DFS_VOLUME = 54;
   int FILE_DEVICE_SERENUM = 55;
   int FILE_DEVICE_TERMSRV = 56;
   int FILE_DEVICE_KSEC = 57;
   int FILE_DEVICE_FIPS = 58;
   int FILE_DEVICE_INFINIBAND = 59;
   int FILE_DEVICE_VMBUS = 62;
   int FILE_DEVICE_CRYPT_PROVIDER = 63;
   int FILE_DEVICE_WPD = 64;
   int FILE_DEVICE_BLUETOOTH = 65;
   int FILE_DEVICE_MT_COMPOSITE = 66;
   int FILE_DEVICE_MT_TRANSPORT = 67;
   int FILE_DEVICE_BIOMETRIC = 68;
   int FILE_DEVICE_PMI = 69;
   int FILE_DEVICE_EHSTOR = 70;
   int FILE_DEVICE_DEVAPI = 71;
   int FILE_DEVICE_GPIO = 72;
   int FILE_DEVICE_USBEX = 73;
   int FILE_DEVICE_CONSOLE = 80;
   int FILE_DEVICE_NFP = 81;
   int FILE_DEVICE_SYSENV = 82;
   int FILE_DEVICE_VIRTUAL_BLOCK = 83;
   int FILE_DEVICE_POINT_OF_SERVICE = 84;
   int FSCTL_GET_COMPRESSION = 15;
   int FSCTL_SET_COMPRESSION = 16;
   int FSCTL_SET_REPARSE_POINT = 41;
   int FSCTL_GET_REPARSE_POINT = 42;
   int FSCTL_DELETE_REPARSE_POINT = 43;
   int METHOD_BUFFERED = 0;
   int METHOD_IN_DIRECT = 1;
   int METHOD_OUT_DIRECT = 2;
   int METHOD_NEITHER = 3;
   int FILE_ANY_ACCESS = 0;
   int FILE_SPECIAL_ACCESS = 0;
   int FILE_READ_ACCESS = 1;
   int FILE_WRITE_ACCESS = 2;
   int IOCTL_STORAGE_GET_DEVICE_NUMBER = 2953344;

   @Structure.FieldOrder({"DeviceType", "DeviceNumber", "PartitionNumber"})
   public static class STORAGE_DEVICE_NUMBER extends Structure {
      public int DeviceType;
      public int DeviceNumber;
      public int PartitionNumber;

      public STORAGE_DEVICE_NUMBER() {
      }

      public STORAGE_DEVICE_NUMBER(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends STORAGE_DEVICE_NUMBER implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }
}
