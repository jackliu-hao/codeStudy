package io.undertow.server.protocol.framed;

import java.util.Deque;
import java.util.List;

public interface FramePriority<C extends AbstractFramedChannel<C, R, S>, R extends AbstractFramedStreamSourceChannel<C, R, S>, S extends AbstractFramedStreamSinkChannel<C, R, S>> {
  boolean insertFrame(S paramS, List<S> paramList);
  
  void frameAdded(S paramS, List<S> paramList, Deque<S> paramDeque);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\framed\FramePriority.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */