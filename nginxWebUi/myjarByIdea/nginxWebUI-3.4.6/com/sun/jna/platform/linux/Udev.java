package com.sun.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.PointerType;

public interface Udev extends Library {
   Udev INSTANCE = (Udev)Native.load("udev", Udev.class);

   UdevContext udev_new();

   UdevContext udev_ref(UdevContext var1);

   UdevContext udev_unref(UdevContext var1);

   UdevDevice udev_device_new_from_syspath(UdevContext var1, String var2);

   UdevEnumerate udev_enumerate_new(UdevContext var1);

   UdevEnumerate udev_enumerate_ref(UdevEnumerate var1);

   UdevEnumerate udev_enumerate_unref(UdevEnumerate var1);

   int udev_enumerate_add_match_subsystem(UdevEnumerate var1, String var2);

   int udev_enumerate_scan_devices(UdevEnumerate var1);

   UdevListEntry udev_enumerate_get_list_entry(UdevEnumerate var1);

   UdevListEntry udev_list_entry_get_next(UdevListEntry var1);

   String udev_list_entry_get_name(UdevListEntry var1);

   UdevDevice udev_device_ref(UdevDevice var1);

   UdevDevice udev_device_unref(UdevDevice var1);

   UdevDevice udev_device_get_parent(UdevDevice var1);

   UdevDevice udev_device_get_parent_with_subsystem_devtype(UdevDevice var1, String var2, String var3);

   String udev_device_get_syspath(UdevDevice var1);

   String udev_device_get_sysname(UdevDevice var1);

   String udev_device_get_devnode(UdevDevice var1);

   String udev_device_get_devtype(UdevDevice var1);

   String udev_device_get_subsystem(UdevDevice var1);

   String udev_device_get_sysattr_value(UdevDevice var1, String var2);

   String udev_device_get_property_value(UdevDevice var1, String var2);

   public static class UdevDevice extends PointerType {
      public UdevDevice ref() {
         return Udev.INSTANCE.udev_device_ref(this);
      }

      public void unref() {
         Udev.INSTANCE.udev_device_unref(this);
      }

      public UdevDevice getParent() {
         return Udev.INSTANCE.udev_device_get_parent(this);
      }

      public UdevDevice getParentWithSubsystemDevtype(String subsystem, String devtype) {
         return Udev.INSTANCE.udev_device_get_parent_with_subsystem_devtype(this, subsystem, devtype);
      }

      public String getSyspath() {
         return Udev.INSTANCE.udev_device_get_syspath(this);
      }

      public String getSysname() {
         return Udev.INSTANCE.udev_device_get_syspath(this);
      }

      public String getDevnode() {
         return Udev.INSTANCE.udev_device_get_devnode(this);
      }

      public String getDevtype() {
         return Udev.INSTANCE.udev_device_get_devtype(this);
      }

      public String getSubsystem() {
         return Udev.INSTANCE.udev_device_get_subsystem(this);
      }

      public String getSysattrValue(String sysattr) {
         return Udev.INSTANCE.udev_device_get_sysattr_value(this, sysattr);
      }

      public String getPropertyValue(String key) {
         return Udev.INSTANCE.udev_device_get_property_value(this, key);
      }
   }

   public static class UdevListEntry extends PointerType {
      public UdevListEntry getNext() {
         return Udev.INSTANCE.udev_list_entry_get_next(this);
      }

      public String getName() {
         return Udev.INSTANCE.udev_list_entry_get_name(this);
      }
   }

   public static class UdevEnumerate extends PointerType {
      public UdevEnumerate ref() {
         return Udev.INSTANCE.udev_enumerate_ref(this);
      }

      public void unref() {
         Udev.INSTANCE.udev_enumerate_unref(this);
      }

      public int addMatchSubsystem(String subsystem) {
         return Udev.INSTANCE.udev_enumerate_add_match_subsystem(this, subsystem);
      }

      public int scanDevices() {
         return Udev.INSTANCE.udev_enumerate_scan_devices(this);
      }

      public UdevListEntry getListEntry() {
         return Udev.INSTANCE.udev_enumerate_get_list_entry(this);
      }
   }

   public static class UdevContext extends PointerType {
      UdevContext ref() {
         return Udev.INSTANCE.udev_ref(this);
      }

      public void unref() {
         Udev.INSTANCE.udev_unref(this);
      }

      public UdevEnumerate enumerateNew() {
         return Udev.INSTANCE.udev_enumerate_new(this);
      }

      public UdevDevice deviceNewFromSyspath(String syspath) {
         return Udev.INSTANCE.udev_device_new_from_syspath(this, syspath);
      }
   }
}
