package com.cym.ext;

import com.cym.model.Location;
import com.cym.model.Upstream;

public class LocationExt {
   Location location;
   Upstream upstream;

   public Location getLocation() {
      return this.location;
   }

   public void setLocation(Location location) {
      this.location = location;
   }

   public Upstream getUpstream() {
      return this.upstream;
   }

   public void setUpstream(Upstream upstream) {
      this.upstream = upstream;
   }
}
