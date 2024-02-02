package com.mysql.cj;

public class ServerVersion implements Comparable<ServerVersion> {
   private String completeVersion;
   private Integer major;
   private Integer minor;
   private Integer subminor;

   public ServerVersion(String completeVersion, int major, int minor, int subminor) {
      this.completeVersion = completeVersion;
      this.major = major;
      this.minor = minor;
      this.subminor = subminor;
   }

   public ServerVersion(int major, int minor, int subminor) {
      this((String)null, major, minor, subminor);
   }

   public int getMajor() {
      return this.major;
   }

   public int getMinor() {
      return this.minor;
   }

   public int getSubminor() {
      return this.subminor;
   }

   public String toString() {
      return this.completeVersion != null ? this.completeVersion : String.format("%d.%d.%d", this.major, this.minor, this.subminor);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && ServerVersion.class.isAssignableFrom(obj.getClass())) {
         ServerVersion another = (ServerVersion)obj;
         return this.getMajor() == another.getMajor() && this.getMinor() == another.getMinor() && this.getSubminor() == another.getSubminor();
      } else {
         return false;
      }
   }

   public int hashCode() {
      int hash = 23;
      hash += 19 * hash + this.major;
      hash += 19 * hash + this.minor;
      hash += 19 * hash + this.subminor;
      return hash;
   }

   public int compareTo(ServerVersion other) {
      int c;
      if ((c = this.major.compareTo(other.getMajor())) != 0) {
         return c;
      } else {
         return (c = this.minor.compareTo(other.getMinor())) != 0 ? c : this.subminor.compareTo(other.getSubminor());
      }
   }

   public boolean meetsMinimum(ServerVersion min) {
      return this.compareTo(min) >= 0;
   }

   public static ServerVersion parseVersion(String versionString) {
      int point = versionString.indexOf(46);
      if (point != -1) {
         try {
            int serverMajorVersion = Integer.parseInt(versionString.substring(0, point));
            String remaining = versionString.substring(point + 1, versionString.length());
            point = remaining.indexOf(46);
            if (point != -1) {
               int serverMinorVersion = Integer.parseInt(remaining.substring(0, point));
               remaining = remaining.substring(point + 1, remaining.length());

               int pos;
               for(pos = 0; pos < remaining.length() && remaining.charAt(pos) >= '0' && remaining.charAt(pos) <= '9'; ++pos) {
               }

               int serverSubminorVersion = Integer.parseInt(remaining.substring(0, pos));
               return new ServerVersion(versionString, serverMajorVersion, serverMinorVersion, serverSubminorVersion);
            }
         } catch (NumberFormatException var7) {
         }
      }

      return new ServerVersion(0, 0, 0);
   }
}
