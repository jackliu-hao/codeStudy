/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.net.SocketAddress;
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
/*     */ public final class SocketAddressBuffer
/*     */ {
/*     */   private SocketAddress sourceAddress;
/*     */   private SocketAddress destinationAddress;
/*     */   
/*     */   public SocketAddress getSourceAddress() {
/*  45 */     return this.sourceAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getSourceAddress(Class<A> type) {
/*  55 */     return type.isInstance(this.sourceAddress) ? type.cast(this.sourceAddress) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSourceAddress(SocketAddress sourceAddress) {
/*  64 */     this.sourceAddress = sourceAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SocketAddress getDestinationAddress() {
/*  73 */     return this.destinationAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getDestinationAddress(Class<A> type) {
/*  83 */     return type.isInstance(this.destinationAddress) ? type.cast(this.destinationAddress) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDestinationAddress(SocketAddress destinationAddress) {
/*  92 */     this.destinationAddress = destinationAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/*  99 */     this.sourceAddress = null;
/* 100 */     this.destinationAddress = null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\SocketAddressBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */