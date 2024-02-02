package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class DistributionManagement implements Serializable, Cloneable, InputLocationTracker {
   private DeploymentRepository repository;
   private DeploymentRepository snapshotRepository;
   private Site site;
   private String downloadUrl;
   private Relocation relocation;
   private String status;
   private Map<Object, InputLocation> locations;

   public DistributionManagement clone() {
      try {
         DistributionManagement copy = (DistributionManagement)super.clone();
         if (this.repository != null) {
            copy.repository = this.repository.clone();
         }

         if (this.snapshotRepository != null) {
            copy.snapshotRepository = this.snapshotRepository.clone();
         }

         if (this.site != null) {
            copy.site = this.site.clone();
         }

         if (this.relocation != null) {
            copy.relocation = this.relocation.clone();
         }

         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getDownloadUrl() {
      return this.downloadUrl;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public Relocation getRelocation() {
      return this.relocation;
   }

   public DeploymentRepository getRepository() {
      return this.repository;
   }

   public Site getSite() {
      return this.site;
   }

   public DeploymentRepository getSnapshotRepository() {
      return this.snapshotRepository;
   }

   public String getStatus() {
      return this.status;
   }

   public void setDownloadUrl(String downloadUrl) {
      this.downloadUrl = downloadUrl;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setRelocation(Relocation relocation) {
      this.relocation = relocation;
   }

   public void setRepository(DeploymentRepository repository) {
      this.repository = repository;
   }

   public void setSite(Site site) {
      this.site = site;
   }

   public void setSnapshotRepository(DeploymentRepository snapshotRepository) {
      this.snapshotRepository = snapshotRepository;
   }

   public void setStatus(String status) {
      this.status = status;
   }
}
