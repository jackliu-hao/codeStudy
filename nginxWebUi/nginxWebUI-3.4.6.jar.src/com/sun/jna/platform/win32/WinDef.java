/*      */ package com.sun.jna.platform.win32;
/*      */ 
/*      */ import com.sun.jna.IntegerType;
/*      */ import com.sun.jna.Native;
/*      */ import com.sun.jna.Pointer;
/*      */ import com.sun.jna.PointerType;
/*      */ import com.sun.jna.Structure;
/*      */ import com.sun.jna.Structure.FieldOrder;
/*      */ import java.awt.Rectangle;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public interface WinDef
/*      */ {
/*      */   public static final int MAX_PATH = 260;
/*      */   
/*      */   public static class WORD
/*      */     extends IntegerType
/*      */     implements Comparable<WORD>
/*      */   {
/*      */     public static final int SIZE = 2;
/*      */     
/*      */     public WORD() {
/*   62 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WORD(long value) {
/*   72 */       super(2, value, true);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(WORD other) {
/*   77 */       return compare(this, other);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class WORDByReference
/*      */     extends com.sun.jna.ptr.ByReference
/*      */   {
/*      */     public WORDByReference() {
/*   90 */       this(new WinDef.WORD(0L));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WORDByReference(WinDef.WORD value) {
/*   99 */       super(2);
/*  100 */       setValue(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(WinDef.WORD value) {
/*  109 */       getPointer().setShort(0L, value.shortValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD getValue() {
/*  118 */       return new WinDef.WORD(getPointer().getShort(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class DWORD
/*      */     extends IntegerType
/*      */     implements Comparable<DWORD>
/*      */   {
/*      */     public static final int SIZE = 4;
/*      */ 
/*      */ 
/*      */     
/*      */     public DWORD() {
/*  134 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public DWORD(long value) {
/*  144 */       super(4, value, true);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD getLow() {
/*  153 */       return new WinDef.WORD(longValue() & 0xFFFFL);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD getHigh() {
/*  162 */       return new WinDef.WORD(longValue() >> 16L & 0xFFFFL);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(DWORD other) {
/*  167 */       return compare(this, other);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class DWORDByReference
/*      */     extends com.sun.jna.ptr.ByReference
/*      */   {
/*      */     public DWORDByReference() {
/*  180 */       this(new WinDef.DWORD(0L));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public DWORDByReference(WinDef.DWORD value) {
/*  189 */       super(4);
/*  190 */       setValue(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(WinDef.DWORD value) {
/*  199 */       getPointer().setInt(0L, value.intValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD getValue() {
/*  208 */       return new WinDef.DWORD(getPointer().getInt(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class LONG
/*      */     extends IntegerType
/*      */     implements Comparable<LONG>
/*      */   {
/*  218 */     public static final int SIZE = Native.LONG_SIZE;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public LONG() {
/*  224 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public LONG(long value) {
/*  233 */       super(SIZE, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(LONG other) {
/*  238 */       return compare(this, other);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class LONGByReference
/*      */     extends com.sun.jna.ptr.ByReference
/*      */   {
/*      */     public LONGByReference() {
/*  251 */       this(new WinDef.LONG(0L));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public LONGByReference(WinDef.LONG value) {
/*  260 */       super(WinDef.LONG.SIZE);
/*  261 */       setValue(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(WinDef.LONG value) {
/*  270 */       getPointer().setInt(0L, value.intValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.LONG getValue() {
/*  279 */       return new WinDef.LONG(getPointer().getInt(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class LONGLONG
/*      */     extends IntegerType
/*      */     implements Comparable<LONGLONG>
/*      */   {
/*  289 */     public static final int SIZE = Native.LONG_SIZE * 2;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public LONGLONG() {
/*  295 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public LONGLONG(long value) {
/*  304 */       super(8, value, false);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(LONGLONG other) {
/*  309 */       return compare(this, other);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class LONGLONGByReference
/*      */     extends com.sun.jna.ptr.ByReference
/*      */   {
/*      */     public LONGLONGByReference() {
/*  322 */       this(new WinDef.LONGLONG(0L));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public LONGLONGByReference(WinDef.LONGLONG value) {
/*  331 */       super(WinDef.LONGLONG.SIZE);
/*  332 */       setValue(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(WinDef.LONGLONG value) {
/*  341 */       getPointer().setLong(0L, value.longValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.LONGLONG getValue() {
/*  350 */       return new WinDef.LONGLONG(getPointer().getLong(0L));
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
/*      */   public static class HDC
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HDC() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HDC(Pointer p) {
/*  373 */       super(p);
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
/*      */   public static class HICON
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HICON() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HICON(WinNT.HANDLE handle) {
/*  398 */       this(handle.getPointer());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HICON(Pointer p) {
/*  408 */       super(p);
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
/*      */   public static class HCURSOR
/*      */     extends HICON
/*      */   {
/*      */     public HCURSOR() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HCURSOR(Pointer p) {
/*  431 */       super(p);
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
/*      */   public static class HMENU
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HMENU() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HMENU(Pointer p) {
/*  454 */       super(p);
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
/*      */   public static class HPEN
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HPEN() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HPEN(Pointer p) {
/*  477 */       super(p);
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
/*      */   public static class HRSRC
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HRSRC() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HRSRC(Pointer p) {
/*  500 */       super(p);
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
/*      */   public static class HPALETTE
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HPALETTE() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HPALETTE(Pointer p) {
/*  523 */       super(p);
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
/*      */   public static class HBITMAP
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HBITMAP() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HBITMAP(Pointer p) {
/*  546 */       super(p);
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
/*      */   public static class HRGN
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HRGN() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HRGN(Pointer p) {
/*  569 */       super(p);
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
/*      */   public static class HWND
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HWND() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HWND(Pointer p) {
/*  592 */       super(p);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class HINSTANCE
/*      */     extends WinNT.HANDLE {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class HMODULE
/*      */     extends HINSTANCE {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class HFONT
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HFONT() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HFONT(Pointer p) {
/*  629 */       super(p);
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
/*      */   public static class HKL
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HKL() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HKL(Pointer p) {
/*  652 */       super(p);
/*      */     }
/*      */     
/*      */     public HKL(int i) {
/*  656 */       super(Pointer.createConstant(i));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getLanguageIdentifier() {
/*  663 */       return (int)(Pointer.nativeValue(getPointer()) & 0xFFFFL);
/*      */     }
/*      */     
/*      */     public int getDeviceHandle() {
/*  667 */       return (int)(Pointer.nativeValue(getPointer()) >> 16L & 0xFFFFL);
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/*  672 */       return String.format("%08x", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())) });
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class LPARAM
/*      */     extends BaseTSD.LONG_PTR
/*      */   {
/*      */     public LPARAM() {
/*  685 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public LPARAM(long value) {
/*  695 */       super(value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class LRESULT
/*      */     extends BaseTSD.LONG_PTR
/*      */   {
/*      */     public LRESULT() {
/*  708 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public LRESULT(long value) {
/*  718 */       super(value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class INT_PTR
/*      */     extends IntegerType
/*      */   {
/*      */     public INT_PTR() {
/*  729 */       super(Native.POINTER_SIZE);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public INT_PTR(long value) {
/*  739 */       super(Native.POINTER_SIZE, value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Pointer toPointer() {
/*  748 */       return Pointer.createConstant(longValue());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class UINT_PTR
/*      */     extends IntegerType
/*      */   {
/*      */     public UINT_PTR() {
/*  761 */       super(Native.POINTER_SIZE);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public UINT_PTR(long value) {
/*  771 */       super(Native.POINTER_SIZE, value, true);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Pointer toPointer() {
/*  780 */       return Pointer.createConstant(longValue());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class WPARAM
/*      */     extends UINT_PTR
/*      */   {
/*      */     public WPARAM() {
/*  793 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WPARAM(long value) {
/*  803 */       super(value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"left", "top", "right", "bottom"})
/*      */   public static class RECT
/*      */     extends Structure
/*      */   {
/*      */     public int left;
/*      */ 
/*      */ 
/*      */     
/*      */     public int top;
/*      */ 
/*      */ 
/*      */     
/*      */     public int right;
/*      */ 
/*      */     
/*      */     public int bottom;
/*      */ 
/*      */ 
/*      */     
/*      */     public Rectangle toRectangle() {
/*  830 */       return new Rectangle(this.left, this.top, this.right - this.left, this.bottom - this.top);
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/*  835 */       return "[(" + this.left + "," + this.top + ")(" + this.right + "," + this.bottom + ")]";
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class ULONG
/*      */     extends IntegerType
/*      */     implements Comparable<ULONG>
/*      */   {
/*  845 */     public static final int SIZE = Native.LONG_SIZE;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ULONG() {
/*  851 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ULONG(long value) {
/*  861 */       super(SIZE, value, true);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(ULONG other) {
/*  866 */       return compare(this, other);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class ULONGByReference
/*      */     extends com.sun.jna.ptr.ByReference
/*      */   {
/*      */     public ULONGByReference() {
/*  879 */       this(new WinDef.ULONG(0L));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ULONGByReference(WinDef.ULONG value) {
/*  888 */       super(WinDef.ULONG.SIZE);
/*  889 */       setValue(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(WinDef.ULONG value) {
/*  898 */       getPointer().setInt(0L, value.intValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.ULONG getValue() {
/*  907 */       return new WinDef.ULONG(getPointer().getInt(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class ULONGLONG
/*      */     extends IntegerType
/*      */     implements Comparable<ULONGLONG>
/*      */   {
/*      */     public static final int SIZE = 8;
/*      */ 
/*      */ 
/*      */     
/*      */     public ULONGLONG() {
/*  923 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ULONGLONG(long value) {
/*  932 */       super(8, value, true);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(ULONGLONG other) {
/*  937 */       return compare(this, other);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class ULONGLONGByReference
/*      */     extends com.sun.jna.ptr.ByReference
/*      */   {
/*      */     public ULONGLONGByReference() {
/*  950 */       this(new WinDef.ULONGLONG(0L));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ULONGLONGByReference(WinDef.ULONGLONG value) {
/*  959 */       super(8);
/*  960 */       setValue(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(WinDef.ULONGLONG value) {
/*  969 */       getPointer().setLong(0L, value.longValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.ULONGLONG getValue() {
/*  978 */       return new WinDef.ULONGLONG(getPointer().getLong(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class DWORDLONG
/*      */     extends IntegerType
/*      */     implements Comparable<DWORDLONG>
/*      */   {
/*      */     public static final int SIZE = 8;
/*      */ 
/*      */ 
/*      */     
/*      */     public DWORDLONG() {
/*  994 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public DWORDLONG(long value) {
/* 1004 */       super(8, value, true);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(DWORDLONG other) {
/* 1009 */       return compare(this, other);
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
/*      */   public static class HBRUSH
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HBRUSH() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HBRUSH(Pointer p) {
/* 1032 */       super(p);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class ATOM
/*      */     extends WORD
/*      */   {
/*      */     public ATOM() {
/* 1045 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ATOM(long value) {
/* 1055 */       super(value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class PVOID
/*      */     extends PointerType
/*      */   {
/*      */     public PVOID() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public PVOID(Pointer pointer) {
/* 1074 */       super(pointer);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class LPVOID
/*      */     extends PointerType
/*      */   {
/*      */     public LPVOID() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public LPVOID(Pointer p) {
/* 1095 */       super(p);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"x", "y"})
/*      */   public static class POINT
/*      */     extends Structure
/*      */   {
/*      */     public int x;
/*      */     public int y;
/*      */     
/*      */     public static class ByReference
/*      */       extends POINT
/*      */       implements Structure.ByReference
/*      */     {
/*      */       public ByReference() {}
/*      */       
/*      */       public ByReference(Pointer memory) {
/* 1114 */         super(memory);
/*      */       }
/*      */       
/*      */       public ByReference(int x, int y) {
/* 1118 */         super(x, y);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static class ByValue
/*      */       extends POINT
/*      */       implements Structure.ByValue
/*      */     {
/*      */       public ByValue() {}
/*      */ 
/*      */       
/*      */       public ByValue(Pointer memory) {
/* 1132 */         super(memory);
/*      */       }
/*      */       
/*      */       public ByValue(int x, int y) {
/* 1136 */         super(x, y);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public POINT() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public POINT(Pointer memory) {
/* 1159 */       super(memory);
/* 1160 */       read();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public POINT(int x, int y) {
/* 1172 */       this.x = x;
/* 1173 */       this.y = y;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class USHORT
/*      */     extends IntegerType
/*      */     implements Comparable<USHORT>
/*      */   {
/*      */     public static final int SIZE = 2;
/*      */ 
/*      */ 
/*      */     
/*      */     public USHORT() {
/* 1189 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public USHORT(long value) {
/* 1199 */       super(2, value, true);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(USHORT other) {
/* 1204 */       return compare(this, other);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class USHORTByReference
/*      */     extends com.sun.jna.ptr.ByReference
/*      */   {
/*      */     public USHORTByReference() {
/* 1217 */       this(new WinDef.USHORT(0L));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public USHORTByReference(WinDef.USHORT value) {
/* 1226 */       super(2);
/* 1227 */       setValue(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public USHORTByReference(short value) {
/* 1236 */       super(2);
/* 1237 */       setValue(new WinDef.USHORT(value));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(WinDef.USHORT value) {
/* 1246 */       getPointer().setShort(0L, value.shortValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.USHORT getValue() {
/* 1255 */       return new WinDef.USHORT(getPointer().getShort(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class SHORT
/*      */     extends IntegerType
/*      */     implements Comparable<SHORT>
/*      */   {
/*      */     public static final int SIZE = 2;
/*      */ 
/*      */ 
/*      */     
/*      */     public SHORT() {
/* 1271 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public SHORT(long value) {
/* 1281 */       super(2, value, false);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(SHORT other) {
/* 1286 */       return compare(this, other);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class UINT
/*      */     extends IntegerType
/*      */     implements Comparable<UINT>
/*      */   {
/*      */     public static final int SIZE = 4;
/*      */ 
/*      */ 
/*      */     
/*      */     public UINT() {
/* 1302 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public UINT(long value) {
/* 1312 */       super(4, value, true);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(UINT other) {
/* 1317 */       return compare(this, other);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class UINTByReference
/*      */     extends com.sun.jna.ptr.ByReference
/*      */   {
/*      */     public UINTByReference() {
/* 1330 */       this(new WinDef.UINT(0L));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public UINTByReference(WinDef.UINT value) {
/* 1339 */       super(4);
/* 1340 */       setValue(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(WinDef.UINT value) {
/* 1349 */       getPointer().setInt(0L, value.intValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.UINT getValue() {
/* 1358 */       return new WinDef.UINT(getPointer().getInt(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class SCODE
/*      */     extends ULONG
/*      */   {
/*      */     public SCODE() {
/* 1371 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public SCODE(long value) {
/* 1381 */       super(value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class SCODEByReference
/*      */     extends com.sun.jna.ptr.ByReference
/*      */   {
/*      */     public SCODEByReference() {
/* 1394 */       this(new WinDef.SCODE(0L));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public SCODEByReference(WinDef.SCODE value) {
/* 1403 */       super(WinDef.SCODE.SIZE);
/* 1404 */       setValue(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(WinDef.SCODE value) {
/* 1413 */       getPointer().setInt(0L, value.intValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.SCODE getValue() {
/* 1422 */       return new WinDef.SCODE(getPointer().getInt(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class LCID
/*      */     extends DWORD
/*      */   {
/*      */     public LCID() {
/* 1435 */       super(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public LCID(long value) {
/* 1444 */       super(value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class BOOL
/*      */     extends IntegerType
/*      */     implements Comparable<BOOL>
/*      */   {
/*      */     public static final int SIZE = 4;
/*      */ 
/*      */ 
/*      */     
/*      */     public BOOL() {
/* 1460 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BOOL(boolean value) {
/* 1469 */       this(value ? 1L : 0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BOOL(long value) {
/* 1478 */       super(4, value, false);
/* 1479 */       assert value == 0L || value == 1L;
/*      */     }
/*      */     
/*      */     public boolean booleanValue() {
/* 1483 */       if (intValue() > 0) {
/* 1484 */         return true;
/*      */       }
/* 1486 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1492 */       return Boolean.toString(booleanValue());
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(BOOL other) {
/* 1497 */       return compare(this, other);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static int compare(BOOL v1, BOOL v2) {
/* 1514 */       if (v1 == v2)
/* 1515 */         return 0; 
/* 1516 */       if (v1 == null)
/* 1517 */         return 1; 
/* 1518 */       if (v2 == null) {
/* 1519 */         return -1;
/*      */       }
/* 1521 */       return compare(v1.booleanValue(), v2.booleanValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static int compare(BOOL v1, boolean v2) {
/* 1537 */       if (v1 == null) {
/* 1538 */         return 1;
/*      */       }
/* 1540 */       return compare(v1.booleanValue(), v2);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static int compare(boolean v1, boolean v2) {
/* 1546 */       if (v1 == v2)
/* 1547 */         return 0; 
/* 1548 */       if (v1) {
/* 1549 */         return 1;
/*      */       }
/* 1551 */       return -1;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class BOOLByReference
/*      */     extends com.sun.jna.ptr.ByReference
/*      */   {
/*      */     public BOOLByReference() {
/* 1565 */       this(new WinDef.BOOL(0L));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BOOLByReference(WinDef.BOOL value) {
/* 1574 */       super(4);
/* 1575 */       setValue(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(WinDef.BOOL value) {
/* 1584 */       getPointer().setInt(0L, value.intValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.BOOL getValue() {
/* 1593 */       return new WinDef.BOOL(getPointer().getInt(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class UCHAR
/*      */     extends IntegerType
/*      */     implements Comparable<UCHAR>
/*      */   {
/*      */     public static final int SIZE = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     public UCHAR() {
/* 1609 */       this(0L);
/*      */     }
/*      */     
/*      */     public UCHAR(char ch) {
/* 1613 */       this((ch & 0xFF));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public UCHAR(long value) {
/* 1622 */       super(1, value, true);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(UCHAR other) {
/* 1627 */       return compare(this, other);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class BYTE
/*      */     extends UCHAR
/*      */   {
/*      */     public BYTE() {
/* 1640 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BYTE(long value) {
/* 1649 */       super(value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class CHAR
/*      */     extends IntegerType
/*      */     implements Comparable<CHAR>
/*      */   {
/*      */     public static final int SIZE = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     public CHAR() {
/* 1665 */       this(0L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public CHAR(byte ch) {
/* 1674 */       this((ch & 0xFF));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public CHAR(long value) {
/* 1683 */       super(1, value, false);
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(CHAR other) {
/* 1688 */       return compare(this, other);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class CHARByReference
/*      */     extends com.sun.jna.ptr.ByReference
/*      */   {
/*      */     public CHARByReference() {
/* 1701 */       this(new WinDef.CHAR(0L));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public CHARByReference(WinDef.CHAR value) {
/* 1710 */       super(1);
/* 1711 */       setValue(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(WinDef.CHAR value) {
/* 1720 */       getPointer().setByte(0L, value.byteValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.CHAR getValue() {
/* 1729 */       return new WinDef.CHAR(getPointer().getByte(0L));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class HGLRC
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HGLRC() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HGLRC(Pointer p) {
/* 1751 */       super(p);
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
/*      */   public static class HGLRCByReference
/*      */     extends WinNT.HANDLEByReference
/*      */   {
/*      */     public HGLRCByReference() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HGLRCByReference(WinDef.HGLRC h) {
/* 1774 */       super(h);
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\WinDef.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */