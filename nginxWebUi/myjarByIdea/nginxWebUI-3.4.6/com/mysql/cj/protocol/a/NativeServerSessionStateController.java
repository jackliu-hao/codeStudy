package com.mysql.cj.protocol.a;

import com.mysql.cj.protocol.ServerSessionStateController;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NativeServerSessionStateController implements ServerSessionStateController {
   private NativeServerSessionStateChanges sessionStateChanges;
   private List<WeakReference<ServerSessionStateController.SessionStateChangesListener>> listeners;

   public void setSessionStateChanges(ServerSessionStateController.ServerSessionStateChanges changes) {
      this.sessionStateChanges = (NativeServerSessionStateChanges)changes;
      if (this.listeners != null) {
         Iterator var2 = this.listeners.iterator();

         while(var2.hasNext()) {
            WeakReference<ServerSessionStateController.SessionStateChangesListener> wr = (WeakReference)var2.next();
            ServerSessionStateController.SessionStateChangesListener l = (ServerSessionStateController.SessionStateChangesListener)wr.get();
            if (l != null) {
               l.handleSessionStateChanges(changes);
            } else {
               this.listeners.remove(wr);
            }
         }
      }

   }

   public NativeServerSessionStateChanges getSessionStateChanges() {
      return this.sessionStateChanges;
   }

   public void addSessionStateChangesListener(ServerSessionStateController.SessionStateChangesListener l) {
      if (this.listeners == null) {
         this.listeners = new ArrayList();
      }

      Iterator var2 = this.listeners.iterator();

      WeakReference wr;
      do {
         if (!var2.hasNext()) {
            this.listeners.add(new WeakReference(l));
            return;
         }

         wr = (WeakReference)var2.next();
      } while(!l.equals(wr.get()));

   }

   public void removeSessionStateChangesListener(ServerSessionStateController.SessionStateChangesListener listener) {
      if (this.listeners != null) {
         Iterator var2 = this.listeners.iterator();

         WeakReference wr;
         ServerSessionStateController.SessionStateChangesListener l;
         do {
            if (!var2.hasNext()) {
               return;
            }

            wr = (WeakReference)var2.next();
            l = (ServerSessionStateController.SessionStateChangesListener)wr.get();
         } while(l != null && !l.equals(listener));

         this.listeners.remove(wr);
      }

   }

   public static class NativeServerSessionStateChanges implements ServerSessionStateController.ServerSessionStateChanges {
      private List<ServerSessionStateController.SessionStateChange> sessionStateChanges = new ArrayList();

      public List<ServerSessionStateController.SessionStateChange> getSessionStateChangesList() {
         return this.sessionStateChanges;
      }

      public NativeServerSessionStateChanges init(NativePacketPayload buf, String encoding) {
         int totalLen = (int)buf.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
         int start = buf.getPosition();

         for(int end = start + totalLen; totalLen > 0 && end > start; start = buf.getPosition()) {
            int type = (int)buf.readInteger(NativeConstants.IntegerDataType.INT1);
            NativePacketPayload b = new NativePacketPayload(buf.readBytes(NativeConstants.StringSelfDataType.STRING_LENENC));
            switch (type) {
               case 0:
                  this.sessionStateChanges.add((new ServerSessionStateController.SessionStateChange(type)).addValue(b.readString(NativeConstants.StringSelfDataType.STRING_LENENC, encoding)).addValue(b.readString(NativeConstants.StringSelfDataType.STRING_LENENC, encoding)));
                  break;
               case 1:
               case 4:
               case 5:
                  this.sessionStateChanges.add((new ServerSessionStateController.SessionStateChange(type)).addValue(b.readString(NativeConstants.StringSelfDataType.STRING_LENENC, encoding)));
                  break;
               case 2:
               default:
                  this.sessionStateChanges.add((new ServerSessionStateController.SessionStateChange(type)).addValue(b.readString(NativeConstants.StringLengthDataType.STRING_FIXED, encoding, b.getPayloadLength())));
                  break;
               case 3:
                  b.readInteger(NativeConstants.IntegerDataType.INT1);
                  this.sessionStateChanges.add((new ServerSessionStateController.SessionStateChange(type)).addValue(b.readString(NativeConstants.StringSelfDataType.STRING_LENENC, encoding)));
            }
         }

         return this;
      }
   }
}
