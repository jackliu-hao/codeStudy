/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.util.IDispatch;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.io.Closeable;
/*     */ import java.util.Iterator;
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
/*     */ public class IComEnumVariantIterator
/*     */   implements Iterable<Variant.VARIANT>, Iterator<Variant.VARIANT>, Closeable
/*     */ {
/*     */   private Variant.VARIANT nextValue;
/*     */   private EnumVariant backingIteration;
/*     */   
/*     */   public static IComEnumVariantIterator wrap(IDispatch dispatch) {
/*  62 */     PointerByReference pbr = new PointerByReference();
/*  63 */     IUnknown unknwn = (IUnknown)dispatch.getProperty(IUnknown.class, OaIdl.DISPID_NEWENUM, new Object[0]);
/*  64 */     unknwn.QueryInterface(EnumVariant.REFIID, pbr);
/*     */     
/*  66 */     unknwn.Release();
/*  67 */     EnumVariant variant = new EnumVariant(pbr.getValue());
/*  68 */     return new IComEnumVariantIterator(variant);
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
/*     */   public IComEnumVariantIterator(EnumVariant backingIteration) {
/*  84 */     this.backingIteration = backingIteration;
/*  85 */     retrieveNext();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/*  90 */     return (this.nextValue != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Variant.VARIANT next() {
/*  95 */     Variant.VARIANT current = this.nextValue;
/*  96 */     retrieveNext();
/*  97 */     return current;
/*     */   }
/*     */   
/*     */   private void retrieveNext() {
/* 101 */     if (this.backingIteration == null) {
/*     */       return;
/*     */     }
/* 104 */     Variant.VARIANT[] variants = this.backingIteration.Next(1);
/* 105 */     if (variants.length == 0) {
/* 106 */       close();
/*     */     } else {
/* 108 */       this.nextValue = variants[0];
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 114 */     if (this.backingIteration != null) {
/* 115 */       this.nextValue = null;
/* 116 */       this.backingIteration.Release();
/* 117 */       this.backingIteration = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 123 */     close();
/* 124 */     super.finalize();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Variant.VARIANT> iterator() {
/* 129 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove() {
/* 134 */     throw new UnsupportedOperationException("remove");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\IComEnumVariantIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */