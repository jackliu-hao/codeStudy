/*    */ package com.sun.jna;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StringArray
/*    */   extends Memory
/*    */   implements Function.PostCallRead
/*    */ {
/*    */   private String encoding;
/* 37 */   private List<NativeString> natives = new ArrayList<NativeString>();
/*    */   private Object[] original;
/*    */   
/*    */   public StringArray(String[] strings) {
/* 41 */     this(strings, false);
/*    */   }
/*    */   
/*    */   public StringArray(String[] strings, boolean wide) {
/* 45 */     this((Object[])strings, wide ? "--WIDE-STRING--" : Native.getDefaultStringEncoding());
/*    */   }
/*    */   
/*    */   public StringArray(String[] strings, String encoding) {
/* 49 */     this((Object[])strings, encoding);
/*    */   }
/*    */   
/*    */   public StringArray(WString[] strings) {
/* 53 */     this((Object[])strings, "--WIDE-STRING--");
/*    */   }
/*    */   private StringArray(Object[] strings, String encoding) {
/* 56 */     super(((strings.length + 1) * Native.POINTER_SIZE));
/* 57 */     this.original = strings;
/* 58 */     this.encoding = encoding;
/* 59 */     for (int i = 0; i < strings.length; i++) {
/* 60 */       Pointer p = null;
/* 61 */       if (strings[i] != null) {
/* 62 */         NativeString ns = new NativeString(strings[i].toString(), encoding);
/* 63 */         this.natives.add(ns);
/* 64 */         p = ns.getPointer();
/*    */       } 
/* 66 */       setPointer((Native.POINTER_SIZE * i), p);
/*    */     } 
/* 68 */     setPointer((Native.POINTER_SIZE * strings.length), null);
/*    */   }
/*    */ 
/*    */   
/*    */   public void read() {
/* 73 */     boolean returnWide = this.original instanceof WString[];
/* 74 */     boolean wide = "--WIDE-STRING--".equals(this.encoding);
/* 75 */     for (int si = 0; si < this.original.length; si++) {
/* 76 */       Pointer p = getPointer((si * Native.POINTER_SIZE));
/* 77 */       Object s = null;
/* 78 */       if (p != null) {
/* 79 */         s = wide ? p.getWideString(0L) : p.getString(0L, this.encoding);
/* 80 */         if (returnWide) s = new WString((String)s); 
/*    */       } 
/* 82 */       this.original[si] = s;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 88 */     boolean wide = "--WIDE-STRING--".equals(this.encoding);
/* 89 */     String s = wide ? "const wchar_t*[]" : "const char*[]";
/* 90 */     s = s + Arrays.<Object>asList(this.original);
/* 91 */     return s;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\StringArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */