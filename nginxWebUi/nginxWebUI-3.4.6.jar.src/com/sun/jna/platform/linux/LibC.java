/*     */ package com.sun.jna.platform.linux;
/*     */ 
/*     */ import com.sun.jna.Library;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.NativeLong;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.Structure.FieldOrder;
/*     */ import com.sun.jna.platform.unix.LibCAPI;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
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
/*     */ public interface LibC
/*     */   extends LibCAPI, Library
/*     */ {
/*     */   public static final String NAME = "c";
/*     */   
/*     */   int sysinfo(Sysinfo paramSysinfo);
/*     */   
/*     */   int statvfs(String paramString, Statvfs paramStatvfs);
/*     */   
/*  43 */   public static final LibC INSTANCE = (LibC)Native.load("c", LibC.class);
/*     */   
/*     */   @FieldOrder({"uptime", "loads", "totalram", "freeram", "sharedram", "bufferram", "totalswap", "freeswap", "procs", "totalhigh", "freehigh", "mem_unit", "_f"})
/*     */   public static class Sysinfo
/*     */     extends Structure {
/*  48 */     private static final int PADDING_SIZE = 20 - 2 * NativeLong.SIZE - 4;
/*     */     
/*     */     public NativeLong uptime;
/*     */     
/*  52 */     public NativeLong[] loads = new NativeLong[3];
/*     */     
/*     */     public NativeLong totalram;
/*     */     public NativeLong freeram;
/*     */     public NativeLong sharedram;
/*     */     public NativeLong bufferram;
/*     */     public NativeLong totalswap;
/*     */     public NativeLong freeswap;
/*     */     public short procs;
/*     */     public NativeLong totalhigh;
/*     */     public NativeLong freehigh;
/*     */     public int mem_unit;
/*  64 */     public byte[] _f = new byte[PADDING_SIZE];
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
/*     */     protected List<Field> getFieldList() {
/*  76 */       List<Field> fields = new ArrayList<Field>(super.getFieldList());
/*  77 */       if (PADDING_SIZE == 0) {
/*  78 */         Iterator<Field> fieldIterator = fields.iterator();
/*  79 */         while (fieldIterator.hasNext()) {
/*  80 */           Field field = fieldIterator.next();
/*  81 */           if ("_f".equals(field.getName())) {
/*  82 */             fieldIterator.remove();
/*     */           }
/*     */         } 
/*     */       } 
/*  86 */       return fields;
/*     */     }
/*     */ 
/*     */     
/*     */     protected List<String> getFieldOrder() {
/*  91 */       List<String> fieldOrder = new ArrayList<String>(super.getFieldOrder());
/*  92 */       if (PADDING_SIZE == 0) {
/*  93 */         fieldOrder.remove("_f");
/*     */       }
/*  95 */       return fieldOrder;
/*     */     }
/*     */   }
/*     */   
/*     */   @FieldOrder({"f_bsize", "f_frsize", "f_blocks", "f_bfree", "f_bavail", "f_files", "f_ffree", "f_favail", "f_fsid", "_f_unused", "f_flag", "f_namemax", "_f_spare"})
/*     */   public static class Statvfs
/*     */     extends Structure
/*     */   {
/*     */     public NativeLong f_bsize;
/*     */     public NativeLong f_frsize;
/*     */     public NativeLong f_blocks;
/*     */     public NativeLong f_bfree;
/*     */     public NativeLong f_bavail;
/*     */     public NativeLong f_files;
/*     */     public NativeLong f_ffree;
/*     */     public NativeLong f_favail;
/*     */     public NativeLong f_fsid;
/*     */     public int _f_unused;
/*     */     public NativeLong f_flag;
/*     */     public NativeLong f_namemax;
/* 115 */     public int[] _f_spare = new int[6];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected List<Field> getFieldList() {
/* 125 */       List<Field> fields = new ArrayList<Field>(super.getFieldList());
/* 126 */       if (NativeLong.SIZE > 4) {
/* 127 */         Iterator<Field> fieldIterator = fields.iterator();
/* 128 */         while (fieldIterator.hasNext()) {
/* 129 */           Field field = fieldIterator.next();
/* 130 */           if ("_f_unused".equals(field.getName())) {
/* 131 */             fieldIterator.remove();
/*     */           }
/*     */         } 
/*     */       } 
/* 135 */       return fields;
/*     */     }
/*     */ 
/*     */     
/*     */     protected List<String> getFieldOrder() {
/* 140 */       List<String> fieldOrder = new ArrayList<String>(super.getFieldOrder());
/* 141 */       if (NativeLong.SIZE > 4) {
/* 142 */         fieldOrder.remove("_f_unused");
/*     */       }
/* 144 */       return fieldOrder;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\linux\LibC.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */