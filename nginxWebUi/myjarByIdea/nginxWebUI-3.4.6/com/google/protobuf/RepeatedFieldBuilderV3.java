package com.google.protobuf;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RepeatedFieldBuilderV3<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder> implements AbstractMessage.BuilderParent {
   private AbstractMessage.BuilderParent parent;
   private List<MType> messages;
   private boolean isMessagesListMutable;
   private List<SingleFieldBuilderV3<MType, BType, IType>> builders;
   private boolean isClean;
   private MessageExternalList<MType, BType, IType> externalMessageList;
   private BuilderExternalList<MType, BType, IType> externalBuilderList;
   private MessageOrBuilderExternalList<MType, BType, IType> externalMessageOrBuilderList;

   public RepeatedFieldBuilderV3(List<MType> messages, boolean isMessagesListMutable, AbstractMessage.BuilderParent parent, boolean isClean) {
      this.messages = messages;
      this.isMessagesListMutable = isMessagesListMutable;
      this.parent = parent;
      this.isClean = isClean;
   }

   public void dispose() {
      this.parent = null;
   }

   private void ensureMutableMessageList() {
      if (!this.isMessagesListMutable) {
         this.messages = new ArrayList(this.messages);
         this.isMessagesListMutable = true;
      }

   }

   private void ensureBuilders() {
      if (this.builders == null) {
         this.builders = new ArrayList(this.messages.size());

         for(int i = 0; i < this.messages.size(); ++i) {
            this.builders.add((Object)null);
         }
      }

   }

   public int getCount() {
      return this.messages.size();
   }

   public boolean isEmpty() {
      return this.messages.isEmpty();
   }

   public MType getMessage(int index) {
      return this.getMessage(index, false);
   }

   private MType getMessage(int index, boolean forBuild) {
      if (this.builders == null) {
         return (AbstractMessage)this.messages.get(index);
      } else {
         SingleFieldBuilderV3<MType, BType, IType> builder = (SingleFieldBuilderV3)this.builders.get(index);
         if (builder == null) {
            return (AbstractMessage)this.messages.get(index);
         } else {
            return forBuild ? builder.build() : builder.getMessage();
         }
      }
   }

   public BType getBuilder(int index) {
      this.ensureBuilders();
      SingleFieldBuilderV3<MType, BType, IType> builder = (SingleFieldBuilderV3)this.builders.get(index);
      if (builder == null) {
         MType message = (AbstractMessage)this.messages.get(index);
         builder = new SingleFieldBuilderV3(message, this, this.isClean);
         this.builders.set(index, builder);
      }

      return builder.getBuilder();
   }

   public IType getMessageOrBuilder(int index) {
      if (this.builders == null) {
         return (MessageOrBuilder)this.messages.get(index);
      } else {
         SingleFieldBuilderV3<MType, BType, IType> builder = (SingleFieldBuilderV3)this.builders.get(index);
         return builder == null ? (MessageOrBuilder)this.messages.get(index) : builder.getMessageOrBuilder();
      }
   }

   public RepeatedFieldBuilderV3<MType, BType, IType> setMessage(int index, MType message) {
      Internal.checkNotNull(message);
      this.ensureMutableMessageList();
      this.messages.set(index, message);
      if (this.builders != null) {
         SingleFieldBuilderV3<MType, BType, IType> entry = (SingleFieldBuilderV3)this.builders.set(index, (Object)null);
         if (entry != null) {
            entry.dispose();
         }
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   public RepeatedFieldBuilderV3<MType, BType, IType> addMessage(MType message) {
      Internal.checkNotNull(message);
      this.ensureMutableMessageList();
      this.messages.add(message);
      if (this.builders != null) {
         this.builders.add((Object)null);
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   public RepeatedFieldBuilderV3<MType, BType, IType> addMessage(int index, MType message) {
      Internal.checkNotNull(message);
      this.ensureMutableMessageList();
      this.messages.add(index, message);
      if (this.builders != null) {
         this.builders.add(index, (Object)null);
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   public RepeatedFieldBuilderV3<MType, BType, IType> addAllMessages(Iterable<? extends MType> values) {
      Iterator var2 = values.iterator();

      while(var2.hasNext()) {
         MType value = (AbstractMessage)var2.next();
         Internal.checkNotNull(value);
      }

      int size = -1;
      if (values instanceof Collection) {
         Collection<MType> collection = (Collection)values;
         if (collection.size() == 0) {
            return this;
         }

         size = collection.size();
      }

      this.ensureMutableMessageList();
      if (size >= 0 && this.messages instanceof ArrayList) {
         ((ArrayList)this.messages).ensureCapacity(this.messages.size() + size);
      }

      Iterator var7 = values.iterator();

      while(var7.hasNext()) {
         MType value = (AbstractMessage)var7.next();
         this.addMessage(value);
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   public BType addBuilder(MType message) {
      this.ensureMutableMessageList();
      this.ensureBuilders();
      SingleFieldBuilderV3<MType, BType, IType> builder = new SingleFieldBuilderV3(message, this, this.isClean);
      this.messages.add((Object)null);
      this.builders.add(builder);
      this.onChanged();
      this.incrementModCounts();
      return builder.getBuilder();
   }

   public BType addBuilder(int index, MType message) {
      this.ensureMutableMessageList();
      this.ensureBuilders();
      SingleFieldBuilderV3<MType, BType, IType> builder = new SingleFieldBuilderV3(message, this, this.isClean);
      this.messages.add(index, (Object)null);
      this.builders.add(index, builder);
      this.onChanged();
      this.incrementModCounts();
      return builder.getBuilder();
   }

   public void remove(int index) {
      this.ensureMutableMessageList();
      this.messages.remove(index);
      if (this.builders != null) {
         SingleFieldBuilderV3<MType, BType, IType> entry = (SingleFieldBuilderV3)this.builders.remove(index);
         if (entry != null) {
            entry.dispose();
         }
      }

      this.onChanged();
      this.incrementModCounts();
   }

   public void clear() {
      this.messages = Collections.emptyList();
      this.isMessagesListMutable = false;
      if (this.builders != null) {
         Iterator var1 = this.builders.iterator();

         while(var1.hasNext()) {
            SingleFieldBuilderV3<MType, BType, IType> entry = (SingleFieldBuilderV3)var1.next();
            if (entry != null) {
               entry.dispose();
            }
         }

         this.builders = null;
      }

      this.onChanged();
      this.incrementModCounts();
   }

   public List<MType> build() {
      this.isClean = true;
      if (!this.isMessagesListMutable && this.builders == null) {
         return this.messages;
      } else {
         boolean allMessagesInSync = true;
         int i;
         if (!this.isMessagesListMutable) {
            for(i = 0; i < this.messages.size(); ++i) {
               Message message = (Message)this.messages.get(i);
               SingleFieldBuilderV3<MType, BType, IType> builder = (SingleFieldBuilderV3)this.builders.get(i);
               if (builder != null && builder.build() != message) {
                  allMessagesInSync = false;
                  break;
               }
            }

            if (allMessagesInSync) {
               return this.messages;
            }
         }

         this.ensureMutableMessageList();

         for(i = 0; i < this.messages.size(); ++i) {
            this.messages.set(i, this.getMessage(i, true));
         }

         this.messages = Collections.unmodifiableList(this.messages);
         this.isMessagesListMutable = false;
         return this.messages;
      }
   }

   public List<MType> getMessageList() {
      if (this.externalMessageList == null) {
         this.externalMessageList = new MessageExternalList(this);
      }

      return this.externalMessageList;
   }

   public List<BType> getBuilderList() {
      if (this.externalBuilderList == null) {
         this.externalBuilderList = new BuilderExternalList(this);
      }

      return this.externalBuilderList;
   }

   public List<IType> getMessageOrBuilderList() {
      if (this.externalMessageOrBuilderList == null) {
         this.externalMessageOrBuilderList = new MessageOrBuilderExternalList(this);
      }

      return this.externalMessageOrBuilderList;
   }

   private void onChanged() {
      if (this.isClean && this.parent != null) {
         this.parent.markDirty();
         this.isClean = false;
      }

   }

   public void markDirty() {
      this.onChanged();
   }

   private void incrementModCounts() {
      if (this.externalMessageList != null) {
         this.externalMessageList.incrementModCount();
      }

      if (this.externalBuilderList != null) {
         this.externalBuilderList.incrementModCount();
      }

      if (this.externalMessageOrBuilderList != null) {
         this.externalMessageOrBuilderList.incrementModCount();
      }

   }

   private static class MessageOrBuilderExternalList<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder> extends AbstractList<IType> implements List<IType> {
      RepeatedFieldBuilderV3<MType, BType, IType> builder;

      MessageOrBuilderExternalList(RepeatedFieldBuilderV3<MType, BType, IType> builder) {
         this.builder = builder;
      }

      public int size() {
         return this.builder.getCount();
      }

      public IType get(int index) {
         return this.builder.getMessageOrBuilder(index);
      }

      void incrementModCount() {
         ++this.modCount;
      }
   }

   private static class BuilderExternalList<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder> extends AbstractList<BType> implements List<BType> {
      RepeatedFieldBuilderV3<MType, BType, IType> builder;

      BuilderExternalList(RepeatedFieldBuilderV3<MType, BType, IType> builder) {
         this.builder = builder;
      }

      public int size() {
         return this.builder.getCount();
      }

      public BType get(int index) {
         return this.builder.getBuilder(index);
      }

      void incrementModCount() {
         ++this.modCount;
      }
   }

   private static class MessageExternalList<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder> extends AbstractList<MType> implements List<MType> {
      RepeatedFieldBuilderV3<MType, BType, IType> builder;

      MessageExternalList(RepeatedFieldBuilderV3<MType, BType, IType> builder) {
         this.builder = builder;
      }

      public int size() {
         return this.builder.getCount();
      }

      public MType get(int index) {
         return this.builder.getMessage(index);
      }

      void incrementModCount() {
         ++this.modCount;
      }
   }
}
