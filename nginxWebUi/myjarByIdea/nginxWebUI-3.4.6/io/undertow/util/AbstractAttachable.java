package io.undertow.util;

import io.undertow.UndertowMessages;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAttachable implements Attachable {
   private Map<AttachmentKey<?>, Object> attachments;

   public <T> T getAttachment(AttachmentKey<T> key) {
      return key != null && this.attachments != null ? this.attachments.get(key) : null;
   }

   public <T> List<T> getAttachmentList(AttachmentKey<? extends List<T>> key) {
      if (key != null && this.attachments != null) {
         List<T> list = (List)this.attachments.get(key);
         return list == null ? Collections.emptyList() : list;
      } else {
         return Collections.emptyList();
      }
   }

   public <T> T putAttachment(AttachmentKey<T> key, T value) {
      if (key == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("key");
      } else {
         if (this.attachments == null) {
            this.attachments = this.createAttachmentMap();
         }

         return this.attachments.put(key, value);
      }
   }

   protected Map<AttachmentKey<?>, Object> createAttachmentMap() {
      return new IdentityHashMap(5);
   }

   public <T> T removeAttachment(AttachmentKey<T> key) {
      return key != null && this.attachments != null ? this.attachments.remove(key) : null;
   }

   public <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> key, T value) {
      if (key != null) {
         if (this.attachments == null) {
            this.attachments = this.createAttachmentMap();
         }

         Map<AttachmentKey<?>, Object> attachments = this.attachments;
         AttachmentList<T> list = (AttachmentList)attachments.get(key);
         if (list == null) {
            AttachmentList<T> newList = new AttachmentList(((ListAttachmentKey)key).getValueClass());
            attachments.put(key, newList);
            newList.add(value);
         } else {
            list.add(value);
         }
      }

   }
}
