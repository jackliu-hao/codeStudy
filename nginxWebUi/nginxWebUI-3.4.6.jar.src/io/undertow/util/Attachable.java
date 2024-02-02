package io.undertow.util;

import java.util.List;

public interface Attachable {
  <T> T getAttachment(AttachmentKey<T> paramAttachmentKey);
  
  <T> List<T> getAttachmentList(AttachmentKey<? extends List<T>> paramAttachmentKey);
  
  <T> T putAttachment(AttachmentKey<T> paramAttachmentKey, T paramT);
  
  <T> T removeAttachment(AttachmentKey<T> paramAttachmentKey);
  
  <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> paramAttachmentKey, T paramT);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\Attachable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */