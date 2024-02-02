package org.apache.maven.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Model extends ModelBase implements Serializable, Cloneable {
   private String modelVersion;
   private Parent parent;
   private String groupId;
   private String artifactId;
   private String version;
   private String packaging = "jar";
   private String name;
   private String description;
   private String url;
   private String inceptionYear;
   private Organization organization;
   private List<License> licenses;
   private List<Developer> developers;
   private List<Contributor> contributors;
   private List<MailingList> mailingLists;
   private Prerequisites prerequisites;
   private Scm scm;
   private IssueManagement issueManagement;
   private CiManagement ciManagement;
   private Build build;
   private List<Profile> profiles;
   private String modelEncoding = "UTF-8";
   private File pomFile;

   public void addContributor(Contributor contributor) {
      this.getContributors().add(contributor);
   }

   public void addDeveloper(Developer developer) {
      this.getDevelopers().add(developer);
   }

   public void addLicense(License license) {
      this.getLicenses().add(license);
   }

   public void addMailingList(MailingList mailingList) {
      this.getMailingLists().add(mailingList);
   }

   public void addProfile(Profile profile) {
      this.getProfiles().add(profile);
   }

   public Model clone() {
      try {
         Model copy = (Model)super.clone();
         if (this.parent != null) {
            copy.parent = this.parent.clone();
         }

         if (this.organization != null) {
            copy.organization = this.organization.clone();
         }

         Iterator i$;
         if (this.licenses != null) {
            copy.licenses = new ArrayList();
            i$ = this.licenses.iterator();

            while(i$.hasNext()) {
               License item = (License)i$.next();
               copy.licenses.add(item.clone());
            }
         }

         if (this.developers != null) {
            copy.developers = new ArrayList();
            i$ = this.developers.iterator();

            while(i$.hasNext()) {
               Developer item = (Developer)i$.next();
               copy.developers.add(item.clone());
            }
         }

         if (this.contributors != null) {
            copy.contributors = new ArrayList();
            i$ = this.contributors.iterator();

            while(i$.hasNext()) {
               Contributor item = (Contributor)i$.next();
               copy.contributors.add(item.clone());
            }
         }

         if (this.mailingLists != null) {
            copy.mailingLists = new ArrayList();
            i$ = this.mailingLists.iterator();

            while(i$.hasNext()) {
               MailingList item = (MailingList)i$.next();
               copy.mailingLists.add(item.clone());
            }
         }

         if (this.prerequisites != null) {
            copy.prerequisites = this.prerequisites.clone();
         }

         if (this.scm != null) {
            copy.scm = this.scm.clone();
         }

         if (this.issueManagement != null) {
            copy.issueManagement = this.issueManagement.clone();
         }

         if (this.ciManagement != null) {
            copy.ciManagement = this.ciManagement.clone();
         }

         if (this.build != null) {
            copy.build = this.build.clone();
         }

         if (this.profiles != null) {
            copy.profiles = new ArrayList();
            i$ = this.profiles.iterator();

            while(i$.hasNext()) {
               Profile item = (Profile)i$.next();
               copy.profiles.add(item.clone());
            }
         }

         this.cloneHook(copy);
         return copy;
      } catch (Exception var4) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var4);
      }
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public Build getBuild() {
      return this.build;
   }

   public CiManagement getCiManagement() {
      return this.ciManagement;
   }

   public List<Contributor> getContributors() {
      if (this.contributors == null) {
         this.contributors = new ArrayList();
      }

      return this.contributors;
   }

   public String getDescription() {
      return this.description;
   }

   public List<Developer> getDevelopers() {
      if (this.developers == null) {
         this.developers = new ArrayList();
      }

      return this.developers;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getInceptionYear() {
      return this.inceptionYear;
   }

   public IssueManagement getIssueManagement() {
      return this.issueManagement;
   }

   public List<License> getLicenses() {
      if (this.licenses == null) {
         this.licenses = new ArrayList();
      }

      return this.licenses;
   }

   public List<MailingList> getMailingLists() {
      if (this.mailingLists == null) {
         this.mailingLists = new ArrayList();
      }

      return this.mailingLists;
   }

   public String getModelEncoding() {
      return this.modelEncoding;
   }

   public String getModelVersion() {
      return this.modelVersion;
   }

   public String getName() {
      return this.name;
   }

   public Organization getOrganization() {
      return this.organization;
   }

   public String getPackaging() {
      return this.packaging;
   }

   public Parent getParent() {
      return this.parent;
   }

   public Prerequisites getPrerequisites() {
      return this.prerequisites;
   }

   public List<Profile> getProfiles() {
      if (this.profiles == null) {
         this.profiles = new ArrayList();
      }

      return this.profiles;
   }

   public Scm getScm() {
      return this.scm;
   }

   public String getUrl() {
      return this.url;
   }

   public String getVersion() {
      return this.version;
   }

   public void removeContributor(Contributor contributor) {
      this.getContributors().remove(contributor);
   }

   public void removeDeveloper(Developer developer) {
      this.getDevelopers().remove(developer);
   }

   public void removeLicense(License license) {
      this.getLicenses().remove(license);
   }

   public void removeMailingList(MailingList mailingList) {
      this.getMailingLists().remove(mailingList);
   }

   public void removeProfile(Profile profile) {
      this.getProfiles().remove(profile);
   }

   public void setArtifactId(String artifactId) {
      this.artifactId = artifactId;
   }

   public void setBuild(Build build) {
      this.build = build;
   }

   public void setCiManagement(CiManagement ciManagement) {
      this.ciManagement = ciManagement;
   }

   public void setContributors(List<Contributor> contributors) {
      this.contributors = contributors;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setDevelopers(List<Developer> developers) {
      this.developers = developers;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   public void setInceptionYear(String inceptionYear) {
      this.inceptionYear = inceptionYear;
   }

   public void setIssueManagement(IssueManagement issueManagement) {
      this.issueManagement = issueManagement;
   }

   public void setLicenses(List<License> licenses) {
      this.licenses = licenses;
   }

   public void setMailingLists(List<MailingList> mailingLists) {
      this.mailingLists = mailingLists;
   }

   public void setModelEncoding(String modelEncoding) {
      this.modelEncoding = modelEncoding;
   }

   public void setModelVersion(String modelVersion) {
      this.modelVersion = modelVersion;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setOrganization(Organization organization) {
      this.organization = organization;
   }

   public void setPackaging(String packaging) {
      this.packaging = packaging;
   }

   public void setParent(Parent parent) {
      this.parent = parent;
   }

   public void setPrerequisites(Prerequisites prerequisites) {
      this.prerequisites = prerequisites;
   }

   public void setProfiles(List<Profile> profiles) {
      this.profiles = profiles;
   }

   public void setScm(Scm scm) {
      this.scm = scm;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   private void cloneHook(Model copy) {
      copy.pomFile = this.pomFile;
   }

   public File getPomFile() {
      return this.pomFile;
   }

   public void setPomFile(File pomFile) {
      this.pomFile = pomFile != null ? pomFile.getAbsoluteFile() : null;
   }

   public File getProjectDirectory() {
      return this.pomFile != null ? this.pomFile.getParentFile() : null;
   }

   public String getId() {
      StringBuilder id = new StringBuilder(64);
      id.append(this.getGroupId() == null ? "[inherited]" : this.getGroupId());
      id.append(":");
      id.append(this.getArtifactId());
      id.append(":");
      id.append(this.getPackaging());
      id.append(":");
      id.append(this.getVersion() == null ? "[inherited]" : this.getVersion());
      return id.toString();
   }

   public String toString() {
      return this.getId();
   }
}
