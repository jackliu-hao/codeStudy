/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.AbstractList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
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
/*     */ public class RepeatedFieldBuilderV3<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder>
/*     */   implements AbstractMessage.BuilderParent
/*     */ {
/*     */   private AbstractMessage.BuilderParent parent;
/*     */   private List<MType> messages;
/*     */   private boolean isMessagesListMutable;
/*     */   private List<SingleFieldBuilderV3<MType, BType, IType>> builders;
/*     */   private boolean isClean;
/*     */   private MessageExternalList<MType, BType, IType> externalMessageList;
/*     */   private BuilderExternalList<MType, BType, IType> externalBuilderList;
/*     */   private MessageOrBuilderExternalList<MType, BType, IType> externalMessageOrBuilderList;
/*     */   
/*     */   public RepeatedFieldBuilderV3(List<MType> messages, boolean isMessagesListMutable, AbstractMessage.BuilderParent parent, boolean isClean) {
/* 131 */     this.messages = messages;
/* 132 */     this.isMessagesListMutable = isMessagesListMutable;
/* 133 */     this.parent = parent;
/* 134 */     this.isClean = isClean;
/*     */   }
/*     */ 
/*     */   
/*     */   public void dispose() {
/* 139 */     this.parent = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureMutableMessageList() {
/* 147 */     if (!this.isMessagesListMutable) {
/* 148 */       this.messages = new ArrayList<>(this.messages);
/* 149 */       this.isMessagesListMutable = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureBuilders() {
/* 158 */     if (this.builders == null) {
/* 159 */       this.builders = new ArrayList<>(this.messages.size());
/* 160 */       for (int i = 0; i < this.messages.size(); i++) {
/* 161 */         this.builders.add(null);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCount() {
/* 172 */     return this.messages.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 181 */     return this.messages.isEmpty();
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
/*     */   public MType getMessage(int index) {
/* 193 */     return getMessage(index, false);
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
/*     */   private MType getMessage(int index, boolean forBuild) {
/* 207 */     if (this.builders == null)
/*     */     {
/*     */ 
/*     */       
/* 211 */       return this.messages.get(index);
/*     */     }
/*     */     
/* 214 */     SingleFieldBuilderV3<MType, BType, IType> builder = this.builders.get(index);
/* 215 */     if (builder == null)
/*     */     {
/*     */ 
/*     */       
/* 219 */       return this.messages.get(index);
/*     */     }
/*     */     
/* 222 */     return forBuild ? builder.build() : builder.getMessage();
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
/*     */   public BType getBuilder(int index) {
/* 234 */     ensureBuilders();
/* 235 */     SingleFieldBuilderV3<MType, BType, IType> builder = this.builders.get(index);
/* 236 */     if (builder == null) {
/* 237 */       AbstractMessage abstractMessage = (AbstractMessage)this.messages.get(index);
/* 238 */       builder = new SingleFieldBuilderV3<>((MType)abstractMessage, this, this.isClean);
/* 239 */       this.builders.set(index, builder);
/*     */     } 
/* 241 */     return builder.getBuilder();
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
/*     */   public IType getMessageOrBuilder(int index) {
/* 253 */     if (this.builders == null)
/*     */     {
/*     */ 
/*     */       
/* 257 */       return (IType)this.messages.get(index);
/*     */     }
/*     */     
/* 260 */     SingleFieldBuilderV3<MType, BType, IType> builder = this.builders.get(index);
/* 261 */     if (builder == null)
/*     */     {
/*     */ 
/*     */       
/* 265 */       return (IType)this.messages.get(index);
/*     */     }
/*     */     
/* 268 */     return builder.getMessageOrBuilder();
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
/*     */   public RepeatedFieldBuilderV3<MType, BType, IType> setMessage(int index, MType message) {
/* 280 */     Internal.checkNotNull(message);
/* 281 */     ensureMutableMessageList();
/* 282 */     this.messages.set(index, message);
/* 283 */     if (this.builders != null) {
/* 284 */       SingleFieldBuilderV3<MType, BType, IType> entry = this.builders.set(index, null);
/* 285 */       if (entry != null) {
/* 286 */         entry.dispose();
/*     */       }
/*     */     } 
/* 289 */     onChanged();
/* 290 */     incrementModCounts();
/* 291 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RepeatedFieldBuilderV3<MType, BType, IType> addMessage(MType message) {
/* 301 */     Internal.checkNotNull(message);
/* 302 */     ensureMutableMessageList();
/* 303 */     this.messages.add(message);
/* 304 */     if (this.builders != null) {
/* 305 */       this.builders.add(null);
/*     */     }
/* 307 */     onChanged();
/* 308 */     incrementModCounts();
/* 309 */     return this;
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
/*     */   public RepeatedFieldBuilderV3<MType, BType, IType> addMessage(int index, MType message) {
/* 322 */     Internal.checkNotNull(message);
/* 323 */     ensureMutableMessageList();
/* 324 */     this.messages.add(index, message);
/* 325 */     if (this.builders != null) {
/* 326 */       this.builders.add(index, null);
/*     */     }
/* 328 */     onChanged();
/* 329 */     incrementModCounts();
/* 330 */     return this;
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
/*     */   public RepeatedFieldBuilderV3<MType, BType, IType> addAllMessages(Iterable<? extends MType> values) {
/* 342 */     for (AbstractMessage abstractMessage : values) {
/* 343 */       Internal.checkNotNull(abstractMessage);
/*     */     }
/*     */ 
/*     */     
/* 347 */     int size = -1;
/* 348 */     if (values instanceof Collection) {
/*     */       
/* 350 */       Collection<MType> collection = (Collection)values;
/* 351 */       if (collection.size() == 0) {
/* 352 */         return this;
/*     */       }
/* 354 */       size = collection.size();
/*     */     } 
/* 356 */     ensureMutableMessageList();
/*     */     
/* 358 */     if (size >= 0 && this.messages instanceof ArrayList) {
/* 359 */       ((ArrayList)this.messages).ensureCapacity(this.messages.size() + size);
/*     */     }
/*     */     
/* 362 */     for (AbstractMessage abstractMessage : values) {
/* 363 */       addMessage((MType)abstractMessage);
/*     */     }
/*     */     
/* 366 */     onChanged();
/* 367 */     incrementModCounts();
/* 368 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BType addBuilder(MType message) {
/* 378 */     ensureMutableMessageList();
/* 379 */     ensureBuilders();
/* 380 */     SingleFieldBuilderV3<MType, BType, IType> builder = new SingleFieldBuilderV3<>(message, this, this.isClean);
/*     */     
/* 382 */     this.messages.add(null);
/* 383 */     this.builders.add(builder);
/* 384 */     onChanged();
/* 385 */     incrementModCounts();
/* 386 */     return builder.getBuilder();
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
/*     */   public BType addBuilder(int index, MType message) {
/* 398 */     ensureMutableMessageList();
/* 399 */     ensureBuilders();
/* 400 */     SingleFieldBuilderV3<MType, BType, IType> builder = new SingleFieldBuilderV3<>(message, this, this.isClean);
/*     */     
/* 402 */     this.messages.add(index, null);
/* 403 */     this.builders.add(index, builder);
/* 404 */     onChanged();
/* 405 */     incrementModCounts();
/* 406 */     return builder.getBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(int index) {
/* 417 */     ensureMutableMessageList();
/* 418 */     this.messages.remove(index);
/* 419 */     if (this.builders != null) {
/* 420 */       SingleFieldBuilderV3<MType, BType, IType> entry = this.builders.remove(index);
/* 421 */       if (entry != null) {
/* 422 */         entry.dispose();
/*     */       }
/*     */     } 
/* 425 */     onChanged();
/* 426 */     incrementModCounts();
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 431 */     this.messages = Collections.emptyList();
/* 432 */     this.isMessagesListMutable = false;
/* 433 */     if (this.builders != null) {
/* 434 */       for (SingleFieldBuilderV3<MType, BType, IType> entry : this.builders) {
/* 435 */         if (entry != null) {
/* 436 */           entry.dispose();
/*     */         }
/*     */       } 
/* 439 */       this.builders = null;
/*     */     } 
/* 441 */     onChanged();
/* 442 */     incrementModCounts();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<MType> build() {
/* 453 */     this.isClean = true;
/*     */     
/* 455 */     if (!this.isMessagesListMutable && this.builders == null)
/*     */     {
/* 457 */       return this.messages;
/*     */     }
/*     */     
/* 460 */     boolean allMessagesInSync = true;
/* 461 */     if (!this.isMessagesListMutable) {
/*     */ 
/*     */       
/* 464 */       for (int j = 0; j < this.messages.size(); j++) {
/* 465 */         Message message = (Message)this.messages.get(j);
/* 466 */         SingleFieldBuilderV3<MType, BType, IType> builder = this.builders.get(j);
/* 467 */         if (builder != null && 
/* 468 */           builder.build() != message) {
/* 469 */           allMessagesInSync = false;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 474 */       if (allMessagesInSync)
/*     */       {
/* 476 */         return this.messages;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 481 */     ensureMutableMessageList();
/* 482 */     for (int i = 0; i < this.messages.size(); i++) {
/* 483 */       this.messages.set(i, getMessage(i, true));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 488 */     this.messages = Collections.unmodifiableList(this.messages);
/* 489 */     this.isMessagesListMutable = false;
/* 490 */     return this.messages;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<MType> getMessageList() {
/* 500 */     if (this.externalMessageList == null) {
/* 501 */       this.externalMessageList = new MessageExternalList<>(this);
/*     */     }
/* 503 */     return this.externalMessageList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<BType> getBuilderList() {
/* 513 */     if (this.externalBuilderList == null) {
/* 514 */       this.externalBuilderList = new BuilderExternalList<>(this);
/*     */     }
/* 516 */     return this.externalBuilderList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<IType> getMessageOrBuilderList() {
/* 526 */     if (this.externalMessageOrBuilderList == null) {
/* 527 */       this.externalMessageOrBuilderList = new MessageOrBuilderExternalList<>(this);
/*     */     }
/* 529 */     return this.externalMessageOrBuilderList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void onChanged() {
/* 537 */     if (this.isClean && this.parent != null) {
/* 538 */       this.parent.markDirty();
/*     */ 
/*     */       
/* 541 */       this.isClean = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void markDirty() {
/* 547 */     onChanged();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void incrementModCounts() {
/* 555 */     if (this.externalMessageList != null) {
/* 556 */       this.externalMessageList.incrementModCount();
/*     */     }
/* 558 */     if (this.externalBuilderList != null) {
/* 559 */       this.externalBuilderList.incrementModCount();
/*     */     }
/* 561 */     if (this.externalMessageOrBuilderList != null) {
/* 562 */       this.externalMessageOrBuilderList.incrementModCount();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class MessageExternalList<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder>
/*     */     extends AbstractList<MType>
/*     */     implements List<MType>
/*     */   {
/*     */     RepeatedFieldBuilderV3<MType, BType, IType> builder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     MessageExternalList(RepeatedFieldBuilderV3<MType, BType, IType> builder) {
/* 582 */       this.builder = builder;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 587 */       return this.builder.getCount();
/*     */     }
/*     */ 
/*     */     
/*     */     public MType get(int index) {
/* 592 */       return this.builder.getMessage(index);
/*     */     }
/*     */     
/*     */     void incrementModCount() {
/* 596 */       this.modCount++;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class BuilderExternalList<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder>
/*     */     extends AbstractList<BType>
/*     */     implements List<BType>
/*     */   {
/*     */     RepeatedFieldBuilderV3<MType, BType, IType> builder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     BuilderExternalList(RepeatedFieldBuilderV3<MType, BType, IType> builder) {
/* 616 */       this.builder = builder;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 621 */       return this.builder.getCount();
/*     */     }
/*     */ 
/*     */     
/*     */     public BType get(int index) {
/* 626 */       return this.builder.getBuilder(index);
/*     */     }
/*     */     
/*     */     void incrementModCount() {
/* 630 */       this.modCount++;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class MessageOrBuilderExternalList<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder>
/*     */     extends AbstractList<IType>
/*     */     implements List<IType>
/*     */   {
/*     */     RepeatedFieldBuilderV3<MType, BType, IType> builder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     MessageOrBuilderExternalList(RepeatedFieldBuilderV3<MType, BType, IType> builder) {
/* 650 */       this.builder = builder;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 655 */       return this.builder.getCount();
/*     */     }
/*     */ 
/*     */     
/*     */     public IType get(int index) {
/* 660 */       return this.builder.getMessageOrBuilder(index);
/*     */     }
/*     */     
/*     */     void incrementModCount() {
/* 664 */       this.modCount++;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\RepeatedFieldBuilderV3.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */