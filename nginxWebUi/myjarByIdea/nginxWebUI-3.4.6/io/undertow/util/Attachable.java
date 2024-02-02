package io.undertow.util;

import java.util.List;

public interface Attachable {
   <T> T getAttachment(AttachmentKey<T> var1);

   <T> List<T> getAttachmentList(AttachmentKey<? extends List<T>> var1);

   <T> T putAttachment(AttachmentKey<T> var1, T var2);

   <T> T removeAttachment(AttachmentKey<T> var1);

   <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> var1, T var2);
}
