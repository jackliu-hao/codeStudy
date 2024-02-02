package com.cym.ext;

import com.cym.model.Location;
import com.cym.model.Server;
import java.util.List;

public class ServerExt {
   Server server;
   List<Location> locationList;
   String locationStr;
   String paramJson;

   public String getParamJson() {
      return this.paramJson;
   }

   public void setParamJson(String paramJson) {
      this.paramJson = paramJson;
   }

   public String getLocationStr() {
      return this.locationStr;
   }

   public void setLocationStr(String locationStr) {
      this.locationStr = locationStr;
   }

   public Server getServer() {
      return this.server;
   }

   public void setServer(Server server) {
      this.server = server;
   }

   public List<Location> getLocationList() {
      return this.locationList;
   }

   public void setLocationList(List<Location> locationList) {
      this.locationList = locationList;
   }
}
