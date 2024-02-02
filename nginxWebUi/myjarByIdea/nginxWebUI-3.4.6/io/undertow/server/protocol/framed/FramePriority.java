package io.undertow.server.protocol.framed;

import java.util.Deque;
import java.util.List;

public interface FramePriority<C extends AbstractFramedChannel<C, R, S>, R extends AbstractFramedStreamSourceChannel<C, R, S>, S extends AbstractFramedStreamSinkChannel<C, R, S>> {
   boolean insertFrame(S var1, List<S> var2);

   void frameAdded(S var1, List<S> var2, Deque<S> var3);
}
