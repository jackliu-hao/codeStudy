package ch.qos.logback.core.helpers;

import ch.qos.logback.core.AppenderBase;

public final class NOPAppender<E> extends AppenderBase<E> {
  protected void append(E eventObject) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\helpers\NOPAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */