package org.xnio.nio;

import org.xnio.StreamConnection;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;

abstract class AbstractNioStreamConnection extends StreamConnection {
   protected AbstractNioStreamConnection(WorkerThread workerThread) {
      super(workerThread);
   }

   protected void setSourceConduit(StreamSourceConduit conduit) {
      super.setSourceConduit(conduit);
   }

   protected void setSinkConduit(StreamSinkConduit conduit) {
      super.setSinkConduit(conduit);
   }

   protected boolean readClosed() {
      return super.readClosed();
   }

   protected boolean writeClosed() {
      return super.writeClosed();
   }
}
