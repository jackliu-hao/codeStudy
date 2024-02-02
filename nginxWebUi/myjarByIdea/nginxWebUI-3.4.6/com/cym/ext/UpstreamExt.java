package com.cym.ext;

import com.cym.model.Upstream;
import com.cym.model.UpstreamServer;
import java.util.List;

public class UpstreamExt {
   Upstream upstream;
   List<UpstreamServer> upstreamServerList;
   String serverStr;
   String paramJson;

   public String getParamJson() {
      return this.paramJson;
   }

   public void setParamJson(String paramJson) {
      this.paramJson = paramJson;
   }

   public String getServerStr() {
      return this.serverStr;
   }

   public void setServerStr(String serverStr) {
      this.serverStr = serverStr;
   }

   public Upstream getUpstream() {
      return this.upstream;
   }

   public void setUpstream(Upstream upstream) {
      this.upstream = upstream;
   }

   public List<UpstreamServer> getUpstreamServerList() {
      return this.upstreamServerList;
   }

   public void setUpstreamServerList(List<UpstreamServer> upstreamServerList) {
      this.upstreamServerList = upstreamServerList;
   }
}
