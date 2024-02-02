package com.sun.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.LibCAPI;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface LibC extends LibCAPI, Library {
   String NAME = "c";
   LibC INSTANCE = (LibC)Native.load("c", LibC.class);

   int sysinfo(Sysinfo var1);

   int statvfs(String var1, Statvfs var2);

   @Structure.FieldOrder({"f_bsize", "f_frsize", "f_blocks", "f_bfree", "f_bavail", "f_files", "f_ffree", "f_favail", "f_fsid", "_f_unused", "f_flag", "f_namemax", "_f_spare"})
   public static class Statvfs extends Structure {
      public NativeLong f_bsize;
      public NativeLong f_frsize;
      public NativeLong f_blocks;
      public NativeLong f_bfree;
      public NativeLong f_bavail;
      public NativeLong f_files;
      public NativeLong f_ffree;
      public NativeLong f_favail;
      public NativeLong f_fsid;
      public int _f_unused;
      public NativeLong f_flag;
      public NativeLong f_namemax;
      public int[] _f_spare = new int[6];

      protected List<Field> getFieldList() {
         List<Field> fields = new ArrayList(super.getFieldList());
         if (NativeLong.SIZE > 4) {
            Iterator<Field> fieldIterator = fields.iterator();

            while(fieldIterator.hasNext()) {
               Field field = (Field)fieldIterator.next();
               if ("_f_unused".equals(field.getName())) {
                  fieldIterator.remove();
               }
            }
         }

         return fields;
      }

      protected List<String> getFieldOrder() {
         List<String> fieldOrder = new ArrayList(super.getFieldOrder());
         if (NativeLong.SIZE > 4) {
            fieldOrder.remove("_f_unused");
         }

         return fieldOrder;
      }
   }

   @Structure.FieldOrder({"uptime", "loads", "totalram", "freeram", "sharedram", "bufferram", "totalswap", "freeswap", "procs", "totalhigh", "freehigh", "mem_unit", "_f"})
   public static class Sysinfo extends Structure {
      private static final int PADDING_SIZE;
      public NativeLong uptime;
      public NativeLong[] loads = new NativeLong[3];
      public NativeLong totalram;
      public NativeLong freeram;
      public NativeLong sharedram;
      public NativeLong bufferram;
      public NativeLong totalswap;
      public NativeLong freeswap;
      public short procs;
      public NativeLong totalhigh;
      public NativeLong freehigh;
      public int mem_unit;
      public byte[] _f;

      public Sysinfo() {
         this._f = new byte[PADDING_SIZE];
      }

      protected List<Field> getFieldList() {
         List<Field> fields = new ArrayList(super.getFieldList());
         if (PADDING_SIZE == 0) {
            Iterator<Field> fieldIterator = fields.iterator();

            while(fieldIterator.hasNext()) {
               Field field = (Field)fieldIterator.next();
               if ("_f".equals(field.getName())) {
                  fieldIterator.remove();
               }
            }
         }

         return fields;
      }

      protected List<String> getFieldOrder() {
         List<String> fieldOrder = new ArrayList(super.getFieldOrder());
         if (PADDING_SIZE == 0) {
            fieldOrder.remove("_f");
         }

         return fieldOrder;
      }

      static {
         PADDING_SIZE = 20 - 2 * NativeLong.SIZE - 4;
      }
   }
}
