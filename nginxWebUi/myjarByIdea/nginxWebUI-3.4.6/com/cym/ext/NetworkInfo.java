package com.cym.ext;

public class NetworkInfo {
   Double send;
   Double receive;
   String time;

   public String getTime() {
      return this.time;
   }

   public void setTime(String time) {
      this.time = time;
   }

   public Double getSend() {
      return this.send;
   }

   public void setSend(Double send) {
      this.send = send;
   }

   public Double getReceive() {
      return this.receive;
   }

   public void setReceive(Double receive) {
      this.receive = receive;
   }
}
