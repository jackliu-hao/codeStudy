package com.sun.mail.imap;

public class ACL implements Cloneable {
   private String name;
   private Rights rights;

   public ACL(String name) {
      this.name = name;
      this.rights = new Rights();
   }

   public ACL(String name, Rights rights) {
      this.name = name;
      this.rights = rights;
   }

   public String getName() {
      return this.name;
   }

   public void setRights(Rights rights) {
      this.rights = rights;
   }

   public Rights getRights() {
      return this.rights;
   }

   public Object clone() throws CloneNotSupportedException {
      ACL acl = (ACL)super.clone();
      acl.rights = (Rights)this.rights.clone();
      return acl;
   }
}
