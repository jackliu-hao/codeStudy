package org.apache.maven.model.merge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.model.Activation;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.PatternSet;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class ModelMerger {
   public void merge(Model target, Model source, boolean sourceDominant, Map<?, ?> hints) {
      if (target == null) {
         throw new IllegalArgumentException("target missing");
      } else if (source != null) {
         Map<Object, Object> context = new HashMap();
         if (hints != null) {
            context.putAll(hints);
         }

         this.mergeModel(target, source, sourceDominant, context);
      }
   }

   protected void mergeModel(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeModelBase(target, source, sourceDominant, context);
      this.mergeModel_ModelVersion(target, source, sourceDominant, context);
      this.mergeModel_Parent(target, source, sourceDominant, context);
      this.mergeModel_GroupId(target, source, sourceDominant, context);
      this.mergeModel_ArtifactId(target, source, sourceDominant, context);
      this.mergeModel_Version(target, source, sourceDominant, context);
      this.mergeModel_Packaging(target, source, sourceDominant, context);
      this.mergeModel_Name(target, source, sourceDominant, context);
      this.mergeModel_Description(target, source, sourceDominant, context);
      this.mergeModel_Url(target, source, sourceDominant, context);
      this.mergeModel_InceptionYear(target, source, sourceDominant, context);
      this.mergeModel_Organization(target, source, sourceDominant, context);
      this.mergeModel_Licenses(target, source, sourceDominant, context);
      this.mergeModel_MailingLists(target, source, sourceDominant, context);
      this.mergeModel_Developers(target, source, sourceDominant, context);
      this.mergeModel_Contributors(target, source, sourceDominant, context);
      this.mergeModel_IssueManagement(target, source, sourceDominant, context);
      this.mergeModel_Scm(target, source, sourceDominant, context);
      this.mergeModel_CiManagement(target, source, sourceDominant, context);
      this.mergeModel_Prerequisites(target, source, sourceDominant, context);
      this.mergeModel_Build(target, source, sourceDominant, context);
      this.mergeModel_Profiles(target, source, sourceDominant, context);
   }

   protected void mergeModel_ModelVersion(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getModelVersion();
      if (src != null && (sourceDominant || target.getModelVersion() == null)) {
         target.setModelVersion(src);
         target.setLocation("modelVersion", source.getLocation("modelVersion"));
      }

   }

   protected void mergeModel_Parent(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      Parent src = source.getParent();
      if (source.getParent() != null) {
         Parent tgt = target.getParent();
         if (tgt == null) {
            tgt = new Parent();
            target.setParent(tgt);
         }

         this.mergeParent(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeModel_GroupId(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getGroupId();
      if (src != null && (sourceDominant || target.getGroupId() == null)) {
         target.setGroupId(src);
         target.setLocation("groupId", source.getLocation("groupId"));
      }

   }

   protected void mergeModel_ArtifactId(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getArtifactId();
      if (src != null && (sourceDominant || target.getArtifactId() == null)) {
         target.setArtifactId(src);
         target.setLocation("artifactId", source.getLocation("artifactId"));
      }

   }

   protected void mergeModel_Version(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getVersion();
      if (src != null && (sourceDominant || target.getVersion() == null)) {
         target.setVersion(src);
         target.setLocation("version", source.getLocation("version"));
      }

   }

   protected void mergeModel_Packaging(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getPackaging();
      if (src != null && (sourceDominant || target.getPackaging() == null)) {
         target.setPackaging(src);
         target.setLocation("packaging", source.getLocation("packaging"));
      }

   }

   protected void mergeModel_Name(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getName();
      if (src != null && (sourceDominant || target.getName() == null)) {
         target.setName(src);
         target.setLocation("name", source.getLocation("name"));
      }

   }

   protected void mergeModel_Description(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getDescription();
      if (src != null && (sourceDominant || target.getDescription() == null)) {
         target.setDescription(src);
         target.setLocation("description", source.getLocation("description"));
      }

   }

   protected void mergeModel_Url(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getUrl();
      if (src != null && (sourceDominant || target.getUrl() == null)) {
         target.setUrl(src);
         target.setLocation("url", source.getLocation("url"));
      }

   }

   protected void mergeModel_InceptionYear(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getInceptionYear();
      if (src != null && (sourceDominant || target.getInceptionYear() == null)) {
         target.setInceptionYear(src);
         target.setLocation("inceptionYear", source.getLocation("inceptionYear"));
      }

   }

   protected void mergeModel_Organization(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      Organization src = source.getOrganization();
      if (source.getOrganization() != null) {
         Organization tgt = target.getOrganization();
         if (tgt == null) {
            tgt = new Organization();
            target.setOrganization(tgt);
         }

         this.mergeOrganization(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeModel_Licenses(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      List<License> src = source.getLicenses();
      if (!src.isEmpty()) {
         List<License> tgt = target.getLicenses();
         Map<Object, License> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         License element;
         Object key;
         while(i$.hasNext()) {
            element = (License)i$.next();
            key = this.getLicenseKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setLicenses(new ArrayList(merged.values()));
                  return;
               }

               element = (License)i$.next();
               key = this.getLicenseKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeModel_MailingLists(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      List<MailingList> src = source.getMailingLists();
      if (!src.isEmpty()) {
         List<MailingList> tgt = target.getMailingLists();
         Map<Object, MailingList> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         MailingList element;
         Object key;
         while(i$.hasNext()) {
            element = (MailingList)i$.next();
            key = this.getMailingListKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setMailingLists(new ArrayList(merged.values()));
                  return;
               }

               element = (MailingList)i$.next();
               key = this.getMailingListKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeModel_Developers(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      List<Developer> src = source.getDevelopers();
      if (!src.isEmpty()) {
         List<Developer> tgt = target.getDevelopers();
         Map<Object, Developer> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Developer element;
         Object key;
         while(i$.hasNext()) {
            element = (Developer)i$.next();
            key = this.getDeveloperKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setDevelopers(new ArrayList(merged.values()));
                  return;
               }

               element = (Developer)i$.next();
               key = this.getDeveloperKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeModel_Contributors(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      List<Contributor> src = source.getContributors();
      if (!src.isEmpty()) {
         List<Contributor> tgt = target.getContributors();
         Map<Object, Contributor> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Contributor element;
         Object key;
         while(i$.hasNext()) {
            element = (Contributor)i$.next();
            key = this.getContributorKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setContributors(new ArrayList(merged.values()));
                  return;
               }

               element = (Contributor)i$.next();
               key = this.getContributorKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeModel_IssueManagement(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      IssueManagement src = source.getIssueManagement();
      if (source.getIssueManagement() != null) {
         IssueManagement tgt = target.getIssueManagement();
         if (tgt == null) {
            tgt = new IssueManagement();
            target.setIssueManagement(tgt);
         }

         this.mergeIssueManagement(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeModel_Scm(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      Scm src = source.getScm();
      if (source.getScm() != null) {
         Scm tgt = target.getScm();
         if (tgt == null) {
            tgt = new Scm();
            target.setScm(tgt);
         }

         this.mergeScm(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeModel_CiManagement(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      CiManagement src = source.getCiManagement();
      if (source.getCiManagement() != null) {
         CiManagement tgt = target.getCiManagement();
         if (tgt == null) {
            tgt = new CiManagement();
            target.setCiManagement(tgt);
         }

         this.mergeCiManagement(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeModel_Prerequisites(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      Prerequisites src = source.getPrerequisites();
      if (source.getPrerequisites() != null) {
         Prerequisites tgt = target.getPrerequisites();
         if (tgt == null) {
            tgt = new Prerequisites();
            target.setPrerequisites(tgt);
         }

         this.mergePrerequisites(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeModel_Build(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      Build src = source.getBuild();
      if (source.getBuild() != null) {
         Build tgt = target.getBuild();
         if (tgt == null) {
            tgt = new Build();
            target.setBuild(tgt);
         }

         this.mergeBuild(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeModel_Profiles(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
      List<Profile> src = source.getProfiles();
      if (!src.isEmpty()) {
         List<Profile> tgt = target.getProfiles();
         Map<Object, Profile> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Profile element;
         Object key;
         while(i$.hasNext()) {
            element = (Profile)i$.next();
            key = this.getProfileKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setProfiles(new ArrayList(merged.values()));
                  return;
               }

               element = (Profile)i$.next();
               key = this.getProfileKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeModelBase(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeModelBase_DistributionManagement(target, source, sourceDominant, context);
      this.mergeModelBase_Modules(target, source, sourceDominant, context);
      this.mergeModelBase_Repositories(target, source, sourceDominant, context);
      this.mergeModelBase_PluginRepositories(target, source, sourceDominant, context);
      this.mergeModelBase_Dependencies(target, source, sourceDominant, context);
      this.mergeModelBase_Reporting(target, source, sourceDominant, context);
      this.mergeModelBase_DependencyManagement(target, source, sourceDominant, context);
      this.mergeModelBase_Properties(target, source, sourceDominant, context);
   }

   protected void mergeModelBase_Modules(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
      List<String> src = source.getModules();
      if (!src.isEmpty()) {
         List<String> tgt = target.getModules();
         List<String> merged = new ArrayList(tgt.size() + src.size());
         merged.addAll(tgt);
         merged.addAll(src);
         target.setModules(merged);
      }

   }

   protected void mergeModelBase_Dependencies(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
      List<Dependency> src = source.getDependencies();
      if (!src.isEmpty()) {
         List<Dependency> tgt = target.getDependencies();
         Map<Object, Dependency> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Dependency element;
         Object key;
         while(i$.hasNext()) {
            element = (Dependency)i$.next();
            key = this.getDependencyKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setDependencies(new ArrayList(merged.values()));
                  return;
               }

               element = (Dependency)i$.next();
               key = this.getDependencyKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeModelBase_Repositories(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
      List<Repository> src = source.getRepositories();
      if (!src.isEmpty()) {
         List<Repository> tgt = target.getRepositories();
         Map<Object, Repository> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Repository element;
         Object key;
         while(i$.hasNext()) {
            element = (Repository)i$.next();
            key = this.getRepositoryKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setRepositories(new ArrayList(merged.values()));
                  return;
               }

               element = (Repository)i$.next();
               key = this.getRepositoryKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeModelBase_PluginRepositories(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
      List<Repository> src = source.getPluginRepositories();
      if (!src.isEmpty()) {
         List<Repository> tgt = target.getPluginRepositories();
         Map<Object, Repository> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Repository element;
         Object key;
         while(i$.hasNext()) {
            element = (Repository)i$.next();
            key = this.getRepositoryKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setPluginRepositories(new ArrayList(merged.values()));
                  return;
               }

               element = (Repository)i$.next();
               key = this.getRepositoryKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeModelBase_DistributionManagement(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
      DistributionManagement src = source.getDistributionManagement();
      if (source.getDistributionManagement() != null) {
         DistributionManagement tgt = target.getDistributionManagement();
         if (tgt == null) {
            tgt = new DistributionManagement();
            target.setDistributionManagement(tgt);
         }

         this.mergeDistributionManagement(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeModelBase_Reporting(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
      Reporting src = source.getReporting();
      if (source.getReporting() != null) {
         Reporting tgt = target.getReporting();
         if (tgt == null) {
            tgt = new Reporting();
            target.setReporting(tgt);
         }

         this.mergeReporting(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeModelBase_DependencyManagement(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
      DependencyManagement src = source.getDependencyManagement();
      if (source.getDependencyManagement() != null) {
         DependencyManagement tgt = target.getDependencyManagement();
         if (tgt == null) {
            tgt = new DependencyManagement();
            target.setDependencyManagement(tgt);
         }

         this.mergeDependencyManagement(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeModelBase_Properties(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
      Properties merged = new Properties();
      if (sourceDominant) {
         merged.putAll(target.getProperties());
         merged.putAll(source.getProperties());
      } else {
         merged.putAll(source.getProperties());
         merged.putAll(target.getProperties());
      }

      target.setProperties(merged);
      target.setLocation("properties", InputLocation.merge(target.getLocation("properties"), source.getLocation("properties"), sourceDominant));
   }

   protected void mergeDistributionManagement(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeDistributionManagement_Repository(target, source, sourceDominant, context);
      this.mergeDistributionManagement_SnapshotRepository(target, source, sourceDominant, context);
      this.mergeDistributionManagement_Site(target, source, sourceDominant, context);
      this.mergeDistributionManagement_Status(target, source, sourceDominant, context);
      this.mergeDistributionManagement_DownloadUrl(target, source, sourceDominant, context);
   }

   protected void mergeDistributionManagement_Repository(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
      DeploymentRepository src = source.getRepository();
      if (src != null) {
         DeploymentRepository tgt = target.getRepository();
         if (tgt == null) {
            tgt = new DeploymentRepository();
            target.setRepository(tgt);
         }

         this.mergeDeploymentRepository(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeDistributionManagement_SnapshotRepository(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
      DeploymentRepository src = source.getSnapshotRepository();
      if (src != null) {
         DeploymentRepository tgt = target.getSnapshotRepository();
         if (tgt == null) {
            tgt = new DeploymentRepository();
            target.setSnapshotRepository(tgt);
         }

         this.mergeDeploymentRepository(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeDistributionManagement_Site(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
      Site src = source.getSite();
      if (src != null) {
         Site tgt = target.getSite();
         if (tgt == null) {
            tgt = new Site();
            target.setSite(tgt);
         }

         this.mergeSite(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeDistributionManagement_Status(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getStatus();
      if (src != null && (sourceDominant || target.getStatus() == null)) {
         target.setStatus(src);
         target.setLocation("status", source.getLocation("status"));
      }

   }

   protected void mergeDistributionManagement_DownloadUrl(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getDownloadUrl();
      if (src != null && (sourceDominant || target.getDownloadUrl() == null)) {
         target.setDownloadUrl(src);
         target.setLocation("downloadUrl", source.getLocation("downloadUrl"));
      }

   }

   protected void mergeRelocation(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeRelocation_GroupId(target, source, sourceDominant, context);
      this.mergeRelocation_ArtifactId(target, source, sourceDominant, context);
      this.mergeRelocation_Version(target, source, sourceDominant, context);
      this.mergeRelocation_Message(target, source, sourceDominant, context);
   }

   protected void mergeRelocation_GroupId(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getGroupId();
      if (src != null && (sourceDominant || target.getGroupId() == null)) {
         target.setGroupId(src);
         target.setLocation("groupId", source.getLocation("groupId"));
      }

   }

   protected void mergeRelocation_ArtifactId(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getArtifactId();
      if (src != null && (sourceDominant || target.getArtifactId() == null)) {
         target.setArtifactId(src);
         target.setLocation("artifactId", source.getLocation("artifactId"));
      }

   }

   protected void mergeRelocation_Version(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getVersion();
      if (src != null && (sourceDominant || target.getVersion() == null)) {
         target.setVersion(src);
         target.setLocation("version", source.getLocation("version"));
      }

   }

   protected void mergeRelocation_Message(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getMessage();
      if (src != null && (sourceDominant || target.getMessage() == null)) {
         target.setMessage(src);
         target.setLocation("message", source.getLocation("message"));
      }

   }

   protected void mergeDeploymentRepository(DeploymentRepository target, DeploymentRepository source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeRepository(target, source, sourceDominant, context);
      this.mergeDeploymentRepository_UniqueVersion(target, source, sourceDominant, context);
   }

   protected void mergeDeploymentRepository_UniqueVersion(DeploymentRepository target, DeploymentRepository source, boolean sourceDominant, Map<Object, Object> context) {
      if (sourceDominant) {
         target.setUniqueVersion(source.isUniqueVersion());
         target.setLocation("uniqueVersion", source.getLocation("uniqueVersion"));
      }

   }

   protected void mergeSite(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeSite_Id(target, source, sourceDominant, context);
      this.mergeSite_Name(target, source, sourceDominant, context);
      this.mergeSite_Url(target, source, sourceDominant, context);
   }

   protected void mergeSite_Id(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getId();
      if (src != null && (sourceDominant || target.getId() == null)) {
         target.setId(src);
         target.setLocation("id", source.getLocation("id"));
      }

   }

   protected void mergeSite_Name(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getName();
      if (src != null && (sourceDominant || target.getName() == null)) {
         target.setName(src);
         target.setLocation("name", source.getLocation("name"));
      }

   }

   protected void mergeSite_Url(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getUrl();
      if (src != null && (sourceDominant || target.getUrl() == null)) {
         target.setUrl(src);
         target.setLocation("url", source.getLocation("url"));
      }

   }

   protected void mergeRepository(Repository target, Repository source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeRepositoryBase(target, source, sourceDominant, context);
      this.mergeRepository_Releases(target, source, sourceDominant, context);
      this.mergeRepository_Snapshots(target, source, sourceDominant, context);
   }

   protected void mergeRepository_Releases(Repository target, Repository source, boolean sourceDominant, Map<Object, Object> context) {
      RepositoryPolicy src = source.getReleases();
      if (src != null) {
         RepositoryPolicy tgt = target.getReleases();
         if (tgt == null) {
            tgt = new RepositoryPolicy();
            target.setReleases(tgt);
         }

         this.mergeRepositoryPolicy(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeRepository_Snapshots(Repository target, Repository source, boolean sourceDominant, Map<Object, Object> context) {
      RepositoryPolicy src = source.getSnapshots();
      if (src != null) {
         RepositoryPolicy tgt = target.getSnapshots();
         if (tgt == null) {
            tgt = new RepositoryPolicy();
            target.setSnapshots(tgt);
         }

         this.mergeRepositoryPolicy(tgt, src, sourceDominant, context);
      }

   }

   protected void mergeRepositoryBase(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeRepositoryBase_Id(target, source, sourceDominant, context);
      this.mergeRepositoryBase_Name(target, source, sourceDominant, context);
      this.mergeRepositoryBase_Url(target, source, sourceDominant, context);
      this.mergeRepositoryBase_Layout(target, source, sourceDominant, context);
   }

   protected void mergeRepositoryBase_Id(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getId();
      if (src != null && (sourceDominant || target.getId() == null)) {
         target.setId(src);
         target.setLocation("id", source.getLocation("id"));
      }

   }

   protected void mergeRepositoryBase_Url(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getUrl();
      if (src != null && (sourceDominant || target.getUrl() == null)) {
         target.setUrl(src);
         target.setLocation("url", source.getLocation("url"));
      }

   }

   protected void mergeRepositoryBase_Name(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getName();
      if (src != null && (sourceDominant || target.getName() == null)) {
         target.setName(src);
         target.setLocation("name", source.getLocation("name"));
      }

   }

   protected void mergeRepositoryBase_Layout(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getLayout();
      if (src != null && (sourceDominant || target.getLayout() == null)) {
         target.setLayout(src);
         target.setLocation("layout", source.getLocation("layout"));
      }

   }

   protected void mergeRepositoryPolicy(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeRepositoryPolicy_Enabled(target, source, sourceDominant, context);
      this.mergeRepositoryPolicy_UpdatePolicy(target, source, sourceDominant, context);
      this.mergeRepositoryPolicy_ChecksumPolicy(target, source, sourceDominant, context);
   }

   protected void mergeRepositoryPolicy_Enabled(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getEnabled();
      if (src != null && (sourceDominant || target.getEnabled() == null)) {
         target.setEnabled(src);
         target.setLocation("enabled", source.getLocation("enabled"));
      }

   }

   protected void mergeRepositoryPolicy_UpdatePolicy(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getUpdatePolicy();
      if (src != null && (sourceDominant || target.getUpdatePolicy() == null)) {
         target.setUpdatePolicy(src);
         target.setLocation("updatePolicy", source.getLocation("updatePolicy"));
      }

   }

   protected void mergeRepositoryPolicy_ChecksumPolicy(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getChecksumPolicy();
      if (src != null && (sourceDominant || target.getChecksumPolicy() == null)) {
         target.setChecksumPolicy(src);
         target.setLocation("checksumPolicy", source.getLocation("checksumPolicy"));
      }

   }

   protected void mergeDependency(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeDependency_GroupId(target, source, sourceDominant, context);
      this.mergeDependency_ArtifactId(target, source, sourceDominant, context);
      this.mergeDependency_Version(target, source, sourceDominant, context);
      this.mergeDependency_Type(target, source, sourceDominant, context);
      this.mergeDependency_Classifier(target, source, sourceDominant, context);
      this.mergeDependency_Scope(target, source, sourceDominant, context);
      this.mergeDependency_SystemPath(target, source, sourceDominant, context);
      this.mergeDependency_Optional(target, source, sourceDominant, context);
      this.mergeDependency_Exclusions(target, source, sourceDominant, context);
   }

   protected void mergeDependency_GroupId(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getGroupId();
      if (src != null && (sourceDominant || target.getGroupId() == null)) {
         target.setGroupId(src);
         target.setLocation("groupId", source.getLocation("groupId"));
      }

   }

   protected void mergeDependency_ArtifactId(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getArtifactId();
      if (src != null && (sourceDominant || target.getArtifactId() == null)) {
         target.setArtifactId(src);
         target.setLocation("artifactId", source.getLocation("artifactId"));
      }

   }

   protected void mergeDependency_Version(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getVersion();
      if (src != null && (sourceDominant || target.getVersion() == null)) {
         target.setVersion(src);
         target.setLocation("version", source.getLocation("version"));
      }

   }

   protected void mergeDependency_Type(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getType();
      if (src != null && (sourceDominant || target.getType() == null)) {
         target.setType(src);
         target.setLocation("type", source.getLocation("type"));
      }

   }

   protected void mergeDependency_Classifier(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getClassifier();
      if (src != null && (sourceDominant || target.getClassifier() == null)) {
         target.setClassifier(src);
         target.setLocation("classifier", source.getLocation("classifier"));
      }

   }

   protected void mergeDependency_Scope(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getScope();
      if (src != null && (sourceDominant || target.getScope() == null)) {
         target.setScope(src);
         target.setLocation("scope", source.getLocation("scope"));
      }

   }

   protected void mergeDependency_SystemPath(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getSystemPath();
      if (src != null && (sourceDominant || target.getSystemPath() == null)) {
         target.setSystemPath(src);
         target.setLocation("systemPath", source.getLocation("systemPath"));
      }

   }

   protected void mergeDependency_Optional(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getOptional();
      if (src != null && (sourceDominant || target.getOptional() == null)) {
         target.setOptional(src);
         target.setLocation("optional", source.getLocation("optional"));
      }

   }

   protected void mergeDependency_Exclusions(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
      List<Exclusion> src = source.getExclusions();
      if (!src.isEmpty()) {
         List<Exclusion> tgt = target.getExclusions();
         Map<Object, Exclusion> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Exclusion element;
         Object key;
         while(i$.hasNext()) {
            element = (Exclusion)i$.next();
            key = this.getExclusionKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setExclusions(new ArrayList(merged.values()));
                  return;
               }

               element = (Exclusion)i$.next();
               key = this.getExclusionKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeExclusion(Exclusion target, Exclusion source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeExclusion_GroupId(target, source, sourceDominant, context);
      this.mergeExclusion_ArtifactId(target, source, sourceDominant, context);
   }

   protected void mergeExclusion_GroupId(Exclusion target, Exclusion source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getGroupId();
      if (src != null && (sourceDominant || target.getGroupId() == null)) {
         target.setGroupId(src);
         target.setLocation("groupId", source.getLocation("groupId"));
      }

   }

   protected void mergeExclusion_ArtifactId(Exclusion target, Exclusion source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getArtifactId();
      if (src != null && (sourceDominant || target.getArtifactId() == null)) {
         target.setArtifactId(src);
         target.setLocation("artifactId", source.getLocation("artifactId"));
      }

   }

   protected void mergeReporting(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeReporting_OutputDirectory(target, source, sourceDominant, context);
      this.mergeReporting_ExcludeDefaults(target, source, sourceDominant, context);
      this.mergeReporting_Plugins(target, source, sourceDominant, context);
   }

   protected void mergeReporting_OutputDirectory(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getOutputDirectory();
      if (src != null && (sourceDominant || target.getOutputDirectory() == null)) {
         target.setOutputDirectory(src);
         target.setLocation("outputDirectory", source.getLocation("outputDirectory"));
      }

   }

   protected void mergeReporting_ExcludeDefaults(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getExcludeDefaults();
      if (src != null && (sourceDominant || target.getExcludeDefaults() == null)) {
         target.setExcludeDefaults(src);
         target.setLocation("excludeDefaults", source.getLocation("excludeDefaults"));
      }

   }

   protected void mergeReporting_Plugins(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
      List<ReportPlugin> src = source.getPlugins();
      if (!src.isEmpty()) {
         List<ReportPlugin> tgt = target.getPlugins();
         Map<Object, ReportPlugin> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         ReportPlugin element;
         Object key;
         while(i$.hasNext()) {
            element = (ReportPlugin)i$.next();
            key = this.getReportPluginKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setPlugins(new ArrayList(merged.values()));
                  return;
               }

               element = (ReportPlugin)i$.next();
               key = this.getReportPluginKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeReportPlugin(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeReportPlugin_Inherited(target, source, sourceDominant, context);
      this.mergeReportPlugin_Configuration(target, source, sourceDominant, context);
      this.mergeReportPlugin_GroupId(target, source, sourceDominant, context);
      this.mergeReportPlugin_ArtifactId(target, source, sourceDominant, context);
      this.mergeReportPlugin_Version(target, source, sourceDominant, context);
      this.mergeReportPlugin_ReportSets(target, source, sourceDominant, context);
   }

   protected void mergeReportPlugin_GroupId(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getGroupId();
      if (src != null && (sourceDominant || target.getGroupId() == null)) {
         target.setGroupId(src);
         target.setLocation("groupId", source.getLocation("groupId"));
      }

   }

   protected void mergeReportPlugin_ArtifactId(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getArtifactId();
      if (src != null && (sourceDominant || target.getArtifactId() == null)) {
         target.setArtifactId(src);
         target.setLocation("artifactId", source.getLocation("artifactId"));
      }

   }

   protected void mergeReportPlugin_Version(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getVersion();
      if (src != null && (sourceDominant || target.getVersion() == null)) {
         target.setVersion(src);
         target.setLocation("version", source.getLocation("version"));
      }

   }

   protected void mergeReportPlugin_Inherited(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getInherited();
      if (src != null && (sourceDominant || target.getInherited() == null)) {
         target.setInherited(src);
         target.setLocation("inherited", source.getLocation("inherited"));
      }

   }

   protected void mergeReportPlugin_Configuration(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
      Xpp3Dom src = (Xpp3Dom)source.getConfiguration();
      if (src != null) {
         Xpp3Dom tgt = (Xpp3Dom)target.getConfiguration();
         if (!sourceDominant && tgt != null) {
            tgt = Xpp3Dom.mergeXpp3Dom(tgt, src);
         } else {
            tgt = Xpp3Dom.mergeXpp3Dom(new Xpp3Dom(src), tgt);
         }

         target.setConfiguration(tgt);
      }

   }

   protected void mergeReportPlugin_ReportSets(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
      List<ReportSet> src = source.getReportSets();
      if (!src.isEmpty()) {
         List<ReportSet> tgt = target.getReportSets();
         Map<Object, ReportSet> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         ReportSet element;
         Object key;
         while(i$.hasNext()) {
            element = (ReportSet)i$.next();
            key = this.getReportSetKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setReportSets(new ArrayList(merged.values()));
                  return;
               }

               element = (ReportSet)i$.next();
               key = this.getReportSetKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeDependencyManagement(DependencyManagement target, DependencyManagement source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeDependencyManagement_Dependencies(target, source, sourceDominant, context);
   }

   protected void mergeDependencyManagement_Dependencies(DependencyManagement target, DependencyManagement source, boolean sourceDominant, Map<Object, Object> context) {
      List<Dependency> src = source.getDependencies();
      if (!src.isEmpty()) {
         List<Dependency> tgt = target.getDependencies();
         Map<Object, Dependency> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Dependency element;
         Object key;
         while(i$.hasNext()) {
            element = (Dependency)i$.next();
            key = this.getDependencyKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setDependencies(new ArrayList(merged.values()));
                  return;
               }

               element = (Dependency)i$.next();
               key = this.getDependencyKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeParent(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeParent_GroupId(target, source, sourceDominant, context);
      this.mergeParent_ArtifactId(target, source, sourceDominant, context);
      this.mergeParent_Version(target, source, sourceDominant, context);
      this.mergeParent_RelativePath(target, source, sourceDominant, context);
   }

   protected void mergeParent_GroupId(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getGroupId();
      if (src != null && (sourceDominant || target.getGroupId() == null)) {
         target.setGroupId(src);
         target.setLocation("groupId", source.getLocation("groupId"));
      }

   }

   protected void mergeParent_ArtifactId(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getArtifactId();
      if (src != null && (sourceDominant || target.getArtifactId() == null)) {
         target.setArtifactId(src);
         target.setLocation("artifactId", source.getLocation("artifactId"));
      }

   }

   protected void mergeParent_Version(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getVersion();
      if (src != null && (sourceDominant || target.getVersion() == null)) {
         target.setVersion(src);
         target.setLocation("version", source.getLocation("version"));
      }

   }

   protected void mergeParent_RelativePath(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getRelativePath();
      if (src != null && (sourceDominant || target.getRelativePath() == null)) {
         target.setRelativePath(src);
         target.setLocation("relativePath", source.getLocation("relativePath"));
      }

   }

   protected void mergeOrganization(Organization target, Organization source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeOrganization_Name(target, source, sourceDominant, context);
      this.mergeOrganization_Url(target, source, sourceDominant, context);
   }

   protected void mergeOrganization_Name(Organization target, Organization source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getName();
      if (src != null && (sourceDominant || target.getName() == null)) {
         target.setName(src);
         target.setLocation("name", source.getLocation("name"));
      }

   }

   protected void mergeOrganization_Url(Organization target, Organization source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getUrl();
      if (src != null && (sourceDominant || target.getUrl() == null)) {
         target.setUrl(src);
         target.setLocation("url", source.getLocation("url"));
      }

   }

   protected void mergeLicense(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeLicense_Name(target, source, sourceDominant, context);
      this.mergeLicense_Url(target, source, sourceDominant, context);
      this.mergeLicense_Distribution(target, source, sourceDominant, context);
      this.mergeLicense_Comments(target, source, sourceDominant, context);
   }

   protected void mergeLicense_Name(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getName();
      if (src != null && (sourceDominant || target.getName() == null)) {
         target.setName(src);
         target.setLocation("name", source.getLocation("name"));
      }

   }

   protected void mergeLicense_Url(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getUrl();
      if (src != null && (sourceDominant || target.getUrl() == null)) {
         target.setUrl(src);
         target.setLocation("url", source.getLocation("url"));
      }

   }

   protected void mergeLicense_Distribution(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getDistribution();
      if (src != null && (sourceDominant || target.getDistribution() == null)) {
         target.setDistribution(src);
         target.setLocation("distribution", source.getLocation("distribution"));
      }

   }

   protected void mergeLicense_Comments(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getComments();
      if (src != null && (sourceDominant || target.getComments() == null)) {
         target.setComments(src);
         target.setLocation("comments", source.getLocation("comments"));
      }

   }

   protected void mergeMailingList(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeMailingList_Name(target, source, sourceDominant, context);
      this.mergeMailingList_Subscribe(target, source, sourceDominant, context);
      this.mergeMailingList_Unsubscribe(target, source, sourceDominant, context);
      this.mergeMailingList_Post(target, source, sourceDominant, context);
      this.mergeMailingList_OtherArchives(target, source, sourceDominant, context);
   }

   protected void mergeMailingList_Name(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getName();
      if (src != null && (sourceDominant || target.getName() == null)) {
         target.setName(src);
         target.setLocation("name", source.getLocation("name"));
      }

   }

   protected void mergeMailingList_Subscribe(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getSubscribe();
      if (src != null && (sourceDominant || target.getSubscribe() == null)) {
         target.setSubscribe(src);
         target.setLocation("subscribe", source.getLocation("subscribe"));
      }

   }

   protected void mergeMailingList_Unsubscribe(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getUnsubscribe();
      if (src != null && (sourceDominant || target.getUnsubscribe() == null)) {
         target.setUnsubscribe(src);
         target.setLocation("unsubscribe", source.getLocation("unsubscribe"));
      }

   }

   protected void mergeMailingList_Post(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getPost();
      if (src != null && (sourceDominant || target.getPost() == null)) {
         target.setPost(src);
         target.setLocation("post", source.getLocation("post"));
      }

   }

   protected void mergeMailingList_Archive(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getArchive();
      if (src != null && (sourceDominant || target.getArchive() == null)) {
         target.setArchive(src);
         target.setLocation("archive", source.getLocation("archive"));
      }

   }

   protected void mergeMailingList_OtherArchives(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
      List<String> src = source.getOtherArchives();
      if (!src.isEmpty()) {
         List<String> tgt = target.getOtherArchives();
         List<String> merged = new ArrayList(tgt.size() + src.size());
         merged.addAll(tgt);
         merged.addAll(src);
         target.setOtherArchives(merged);
      }

   }

   protected void mergeDeveloper(Developer target, Developer source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeContributor(target, source, sourceDominant, context);
      this.mergeDeveloper_Id(target, source, sourceDominant, context);
   }

   protected void mergeDeveloper_Id(Developer target, Developer source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getId();
      if (src != null && (sourceDominant || target.getId() == null)) {
         target.setId(src);
         target.setLocation("id", source.getLocation("id"));
      }

   }

   protected void mergeContributor(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeContributor_Name(target, source, sourceDominant, context);
      this.mergeContributor_Email(target, source, sourceDominant, context);
      this.mergeContributor_Url(target, source, sourceDominant, context);
      this.mergeContributor_Organization(target, source, sourceDominant, context);
      this.mergeContributor_OrganizationUrl(target, source, sourceDominant, context);
      this.mergeContributor_Timezone(target, source, sourceDominant, context);
      this.mergeContributor_Roles(target, source, sourceDominant, context);
      this.mergeContributor_Properties(target, source, sourceDominant, context);
   }

   protected void mergeContributor_Name(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getName();
      if (src != null && (sourceDominant || target.getName() == null)) {
         target.setName(src);
         target.setLocation("name", source.getLocation("name"));
      }

   }

   protected void mergeContributor_Email(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getEmail();
      if (src != null && (sourceDominant || target.getEmail() == null)) {
         target.setEmail(src);
         target.setLocation("email", source.getLocation("email"));
      }

   }

   protected void mergeContributor_Url(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getUrl();
      if (src != null && (sourceDominant || target.getUrl() == null)) {
         target.setUrl(src);
         target.setLocation("url", source.getLocation("url"));
      }

   }

   protected void mergeContributor_Organization(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getOrganization();
      if (src != null && (sourceDominant || target.getOrganization() == null)) {
         target.setOrganization(src);
         target.setLocation("organization", source.getLocation("organization"));
      }

   }

   protected void mergeContributor_OrganizationUrl(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getOrganizationUrl();
      if (src != null && (sourceDominant || target.getOrganizationUrl() == null)) {
         target.setOrganizationUrl(src);
         target.setLocation("organizationUrl", source.getLocation("organizationUrl"));
      }

   }

   protected void mergeContributor_Timezone(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getTimezone();
      if (src != null && (sourceDominant || target.getTimezone() == null)) {
         target.setTimezone(src);
         target.setLocation("timezone", source.getLocation("timezone"));
      }

   }

   protected void mergeContributor_Roles(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
      List<String> src = source.getRoles();
      if (!src.isEmpty()) {
         List<String> tgt = target.getRoles();
         List<String> merged = new ArrayList(tgt.size() + src.size());
         merged.addAll(tgt);
         merged.addAll(src);
         target.setRoles(merged);
      }

   }

   protected void mergeContributor_Properties(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
      Properties merged = new Properties();
      if (sourceDominant) {
         merged.putAll(target.getProperties());
         merged.putAll(source.getProperties());
      } else {
         merged.putAll(source.getProperties());
         merged.putAll(target.getProperties());
      }

      target.setProperties(merged);
      target.setLocation("properties", InputLocation.merge(target.getLocation("properties"), source.getLocation("properties"), sourceDominant));
   }

   protected void mergeIssueManagement(IssueManagement target, IssueManagement source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeIssueManagement_Url(target, source, sourceDominant, context);
      this.mergeIssueManagement_System(target, source, sourceDominant, context);
   }

   protected void mergeIssueManagement_System(IssueManagement target, IssueManagement source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getSystem();
      if (src != null && (sourceDominant || target.getSystem() == null)) {
         target.setSystem(src);
         target.setLocation("system", source.getLocation("system"));
      }

   }

   protected void mergeIssueManagement_Url(IssueManagement target, IssueManagement source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getUrl();
      if (src != null && (sourceDominant || target.getUrl() == null)) {
         target.setUrl(src);
         target.setLocation("url", source.getLocation("url"));
      }

   }

   protected void mergeScm(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeScm_Url(target, source, sourceDominant, context);
      this.mergeScm_Connection(target, source, sourceDominant, context);
      this.mergeScm_DeveloperConnection(target, source, sourceDominant, context);
      this.mergeScm_Tag(target, source, sourceDominant, context);
   }

   protected void mergeScm_Url(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getUrl();
      if (src != null && (sourceDominant || target.getUrl() == null)) {
         target.setUrl(src);
         target.setLocation("url", source.getLocation("url"));
      }

   }

   protected void mergeScm_Connection(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getConnection();
      if (src != null && (sourceDominant || target.getConnection() == null)) {
         target.setConnection(src);
         target.setLocation("connection", source.getLocation("connection"));
      }

   }

   protected void mergeScm_DeveloperConnection(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getDeveloperConnection();
      if (src != null && (sourceDominant || target.getDeveloperConnection() == null)) {
         target.setDeveloperConnection(src);
         target.setLocation("developerConnection", source.getLocation("developerConnection"));
      }

   }

   protected void mergeScm_Tag(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getTag();
      if (src != null && (sourceDominant || target.getTag() == null)) {
         target.setTag(src);
         target.setLocation("tag", source.getLocation("tag"));
      }

   }

   protected void mergeCiManagement(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeCiManagement_System(target, source, sourceDominant, context);
      this.mergeCiManagement_Url(target, source, sourceDominant, context);
      this.mergeCiManagement_Notifiers(target, source, sourceDominant, context);
   }

   protected void mergeCiManagement_System(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getSystem();
      if (src != null && (sourceDominant || target.getSystem() == null)) {
         target.setSystem(src);
         target.setLocation("system", source.getLocation("system"));
      }

   }

   protected void mergeCiManagement_Url(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getUrl();
      if (src != null && (sourceDominant || target.getUrl() == null)) {
         target.setUrl(src);
         target.setLocation("url", source.getLocation("url"));
      }

   }

   protected void mergeCiManagement_Notifiers(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
      List<Notifier> src = source.getNotifiers();
      if (!src.isEmpty()) {
         List<Notifier> tgt = target.getNotifiers();
         Map<Object, Notifier> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Notifier element;
         Object key;
         while(i$.hasNext()) {
            element = (Notifier)i$.next();
            key = this.getNotifierKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setNotifiers(new ArrayList(merged.values()));
                  return;
               }

               element = (Notifier)i$.next();
               key = this.getNotifierKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeNotifier(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeNotifier_Type(target, source, sourceDominant, context);
      this.mergeNotifier_Address(target, source, sourceDominant, context);
      this.mergeNotifier_Configuration(target, source, sourceDominant, context);
      this.mergeNotifier_SendOnError(target, source, sourceDominant, context);
      this.mergeNotifier_SendOnFailure(target, source, sourceDominant, context);
      this.mergeNotifier_SendOnSuccess(target, source, sourceDominant, context);
      this.mergeNotifier_SendOnWarning(target, source, sourceDominant, context);
   }

   protected void mergeNotifier_Type(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getType();
      if (src != null && (sourceDominant || target.getType() == null)) {
         target.setType(src);
      }

   }

   protected void mergeNotifier_Address(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getAddress();
      if (src != null && (sourceDominant || target.getAddress() == null)) {
         target.setAddress(src);
      }

   }

   protected void mergeNotifier_Configuration(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
      Properties merged = new Properties();
      if (sourceDominant) {
         merged.putAll(target.getConfiguration());
         merged.putAll(source.getConfiguration());
      } else {
         merged.putAll(source.getConfiguration());
         merged.putAll(target.getConfiguration());
      }

      target.setConfiguration(merged);
   }

   protected void mergeNotifier_SendOnError(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
      if (sourceDominant) {
         target.setSendOnError(source.isSendOnError());
      }

   }

   protected void mergeNotifier_SendOnFailure(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
      if (sourceDominant) {
         target.setSendOnFailure(source.isSendOnFailure());
      }

   }

   protected void mergeNotifier_SendOnSuccess(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
      if (sourceDominant) {
         target.setSendOnSuccess(source.isSendOnSuccess());
      }

   }

   protected void mergeNotifier_SendOnWarning(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
      if (sourceDominant) {
         target.setSendOnWarning(source.isSendOnWarning());
      }

   }

   protected void mergePrerequisites(Prerequisites target, Prerequisites source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergePrerequisites_Maven(target, source, sourceDominant, context);
   }

   protected void mergePrerequisites_Maven(Prerequisites target, Prerequisites source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getMaven();
      if (src != null && (sourceDominant || target.getMaven() == null)) {
         target.setMaven(src);
         target.setLocation("maven", source.getLocation("maven"));
      }

   }

   protected void mergeBuild(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeBuildBase(target, source, sourceDominant, context);
      this.mergeBuild_SourceDirectory(target, source, sourceDominant, context);
      this.mergeBuild_ScriptSourceDirectory(target, source, sourceDominant, context);
      this.mergeBuild_TestSourceDirectory(target, source, sourceDominant, context);
      this.mergeBuild_OutputDirectory(target, source, sourceDominant, context);
      this.mergeBuild_TestOutputDirectory(target, source, sourceDominant, context);
      this.mergeBuild_Extensions(target, source, sourceDominant, context);
   }

   protected void mergeBuild_SourceDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getSourceDirectory();
      if (src != null && (sourceDominant || target.getSourceDirectory() == null)) {
         target.setSourceDirectory(src);
         target.setLocation("sourceDirectory", source.getLocation("sourceDirectory"));
      }

   }

   protected void mergeBuild_ScriptSourceDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getScriptSourceDirectory();
      if (src != null && (sourceDominant || target.getScriptSourceDirectory() == null)) {
         target.setScriptSourceDirectory(src);
         target.setLocation("scriptSourceDirectory", source.getLocation("scriptSourceDirectory"));
      }

   }

   protected void mergeBuild_TestSourceDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getTestSourceDirectory();
      if (src != null && (sourceDominant || target.getTestSourceDirectory() == null)) {
         target.setTestSourceDirectory(src);
         target.setLocation("testSourceDirectory", source.getLocation("testSourceDirectory"));
      }

   }

   protected void mergeBuild_OutputDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getOutputDirectory();
      if (src != null && (sourceDominant || target.getOutputDirectory() == null)) {
         target.setOutputDirectory(src);
         target.setLocation("outputDirectory", source.getLocation("outputDirectory"));
      }

   }

   protected void mergeBuild_TestOutputDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getTestOutputDirectory();
      if (src != null && (sourceDominant || target.getTestOutputDirectory() == null)) {
         target.setTestOutputDirectory(src);
         target.setLocation("testOutputDirectory", source.getLocation("testOutputDirectory"));
      }

   }

   protected void mergeBuild_Extensions(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
      List<Extension> src = source.getExtensions();
      if (!src.isEmpty()) {
         List<Extension> tgt = target.getExtensions();
         Map<Object, Extension> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Extension element;
         Object key;
         while(i$.hasNext()) {
            element = (Extension)i$.next();
            key = this.getExtensionKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setExtensions(new ArrayList(merged.values()));
                  return;
               }

               element = (Extension)i$.next();
               key = this.getExtensionKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeExtension(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeExtension_GroupId(target, source, sourceDominant, context);
      this.mergeExtension_ArtifactId(target, source, sourceDominant, context);
      this.mergeExtension_Version(target, source, sourceDominant, context);
   }

   protected void mergeExtension_GroupId(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getGroupId();
      if (src != null && (sourceDominant || target.getGroupId() == null)) {
         target.setGroupId(src);
         target.setLocation("groupId", source.getLocation("groupId"));
      }

   }

   protected void mergeExtension_ArtifactId(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getArtifactId();
      if (src != null && (sourceDominant || target.getArtifactId() == null)) {
         target.setArtifactId(src);
         target.setLocation("artifactId", source.getLocation("artifactId"));
      }

   }

   protected void mergeExtension_Version(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getVersion();
      if (src != null && (sourceDominant || target.getVersion() == null)) {
         target.setVersion(src);
         target.setLocation("version", source.getLocation("version"));
      }

   }

   protected void mergeBuildBase(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergePluginConfiguration(target, source, sourceDominant, context);
      this.mergeBuildBase_DefaultGoal(target, source, sourceDominant, context);
      this.mergeBuildBase_FinalName(target, source, sourceDominant, context);
      this.mergeBuildBase_Directory(target, source, sourceDominant, context);
      this.mergeBuildBase_Resources(target, source, sourceDominant, context);
      this.mergeBuildBase_TestResources(target, source, sourceDominant, context);
      this.mergeBuildBase_Filters(target, source, sourceDominant, context);
   }

   protected void mergeBuildBase_DefaultGoal(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getDefaultGoal();
      if (src != null && (sourceDominant || target.getDefaultGoal() == null)) {
         target.setDefaultGoal(src);
         target.setLocation("defaultGoal", source.getLocation("defaultGoal"));
      }

   }

   protected void mergeBuildBase_Directory(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getDirectory();
      if (src != null && (sourceDominant || target.getDirectory() == null)) {
         target.setDirectory(src);
         target.setLocation("directory", source.getLocation("directory"));
      }

   }

   protected void mergeBuildBase_FinalName(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getFinalName();
      if (src != null && (sourceDominant || target.getFinalName() == null)) {
         target.setFinalName(src);
         target.setLocation("finalName", source.getLocation("finalName"));
      }

   }

   protected void mergeBuildBase_Filters(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
      List<String> src = source.getFilters();
      if (!src.isEmpty()) {
         List<String> tgt = target.getFilters();
         List<String> merged = new ArrayList(tgt.size() + src.size());
         merged.addAll(tgt);
         merged.addAll(src);
         target.setFilters(merged);
      }

   }

   protected void mergeBuildBase_Resources(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
      List<Resource> src = source.getResources();
      if (!src.isEmpty()) {
         List<Resource> tgt = target.getResources();
         Map<Object, Resource> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Resource element;
         Object key;
         while(i$.hasNext()) {
            element = (Resource)i$.next();
            key = this.getResourceKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setResources(new ArrayList(merged.values()));
                  return;
               }

               element = (Resource)i$.next();
               key = this.getResourceKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeBuildBase_TestResources(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
      List<Resource> src = source.getTestResources();
      if (!src.isEmpty()) {
         List<Resource> tgt = target.getTestResources();
         Map<Object, Resource> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Resource element;
         Object key;
         while(i$.hasNext()) {
            element = (Resource)i$.next();
            key = this.getResourceKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setTestResources(new ArrayList(merged.values()));
                  return;
               }

               element = (Resource)i$.next();
               key = this.getResourceKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergePluginConfiguration(PluginConfiguration target, PluginConfiguration source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergePluginContainer(target, source, sourceDominant, context);
      this.mergePluginConfiguration_PluginManagement(target, source, sourceDominant, context);
   }

   protected void mergePluginConfiguration_PluginManagement(PluginConfiguration target, PluginConfiguration source, boolean sourceDominant, Map<Object, Object> context) {
      PluginManagement src = source.getPluginManagement();
      if (source.getPluginManagement() != null) {
         PluginManagement tgt = target.getPluginManagement();
         if (tgt == null) {
            tgt = new PluginManagement();
            target.setPluginManagement(tgt);
         }

         this.mergePluginManagement(tgt, src, sourceDominant, context);
      }

   }

   protected void mergePluginContainer(PluginContainer target, PluginContainer source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergePluginContainer_Plugins(target, source, sourceDominant, context);
   }

   protected void mergePluginContainer_Plugins(PluginContainer target, PluginContainer source, boolean sourceDominant, Map<Object, Object> context) {
      List<Plugin> src = source.getPlugins();
      if (!src.isEmpty()) {
         List<Plugin> tgt = target.getPlugins();
         Map<Object, Plugin> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Plugin element;
         Object key;
         while(i$.hasNext()) {
            element = (Plugin)i$.next();
            key = this.getPluginKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setPlugins(new ArrayList(merged.values()));
                  return;
               }

               element = (Plugin)i$.next();
               key = this.getPluginKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergePluginManagement(PluginManagement target, PluginManagement source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergePluginContainer(target, source, sourceDominant, context);
   }

   protected void mergePlugin(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeConfigurationContainer(target, source, sourceDominant, context);
      this.mergePlugin_GroupId(target, source, sourceDominant, context);
      this.mergePlugin_ArtifactId(target, source, sourceDominant, context);
      this.mergePlugin_Version(target, source, sourceDominant, context);
      this.mergePlugin_Extensions(target, source, sourceDominant, context);
      this.mergePlugin_Dependencies(target, source, sourceDominant, context);
      this.mergePlugin_Executions(target, source, sourceDominant, context);
   }

   protected void mergePlugin_GroupId(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getGroupId();
      if (src != null && (sourceDominant || target.getGroupId() == null)) {
         target.setGroupId(src);
         target.setLocation("groupId", source.getLocation("groupId"));
      }

   }

   protected void mergePlugin_ArtifactId(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getArtifactId();
      if (src != null && (sourceDominant || target.getArtifactId() == null)) {
         target.setArtifactId(src);
         target.setLocation("artifactId", source.getLocation("artifactId"));
      }

   }

   protected void mergePlugin_Version(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getVersion();
      if (src != null && (sourceDominant || target.getVersion() == null)) {
         target.setVersion(src);
         target.setLocation("version", source.getLocation("version"));
      }

   }

   protected void mergePlugin_Extensions(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getExtensions();
      if (src != null && (sourceDominant || target.getExtensions() == null)) {
         target.setExtensions(src);
         target.setLocation("extensions", source.getLocation("extensions"));
      }

   }

   protected void mergePlugin_Dependencies(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
      List<Dependency> src = source.getDependencies();
      if (!src.isEmpty()) {
         List<Dependency> tgt = target.getDependencies();
         Map<Object, Dependency> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         Dependency element;
         Object key;
         while(i$.hasNext()) {
            element = (Dependency)i$.next();
            key = this.getDependencyKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setDependencies(new ArrayList(merged.values()));
                  return;
               }

               element = (Dependency)i$.next();
               key = this.getDependencyKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergePlugin_Executions(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
      List<PluginExecution> src = source.getExecutions();
      if (!src.isEmpty()) {
         List<PluginExecution> tgt = target.getExecutions();
         Map<Object, PluginExecution> merged = new LinkedHashMap((src.size() + tgt.size()) * 2);
         Iterator i$ = tgt.iterator();

         PluginExecution element;
         Object key;
         while(i$.hasNext()) {
            element = (PluginExecution)i$.next();
            key = this.getPluginExecutionKey(element);
            merged.put(key, element);
         }

         i$ = src.iterator();

         while(true) {
            do {
               if (!i$.hasNext()) {
                  target.setExecutions(new ArrayList(merged.values()));
                  return;
               }

               element = (PluginExecution)i$.next();
               key = this.getPluginExecutionKey(element);
            } while(!sourceDominant && merged.containsKey(key));

            merged.put(key, element);
         }
      }
   }

   protected void mergeConfigurationContainer(ConfigurationContainer target, ConfigurationContainer source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeConfigurationContainer_Inherited(target, source, sourceDominant, context);
      this.mergeConfigurationContainer_Configuration(target, source, sourceDominant, context);
   }

   protected void mergeConfigurationContainer_Inherited(ConfigurationContainer target, ConfigurationContainer source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getInherited();
      if (src != null && (sourceDominant || target.getInherited() == null)) {
         target.setInherited(src);
         target.setLocation("inherited", source.getLocation("inherited"));
      }

   }

   protected void mergeConfigurationContainer_Configuration(ConfigurationContainer target, ConfigurationContainer source, boolean sourceDominant, Map<Object, Object> context) {
      Xpp3Dom src = (Xpp3Dom)source.getConfiguration();
      if (src != null) {
         Xpp3Dom tgt = (Xpp3Dom)target.getConfiguration();
         if (!sourceDominant && tgt != null) {
            tgt = Xpp3Dom.mergeXpp3Dom(tgt, src);
         } else {
            tgt = Xpp3Dom.mergeXpp3Dom(new Xpp3Dom(src), tgt);
         }

         target.setConfiguration(tgt);
      }

   }

   protected void mergePluginExecution(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeConfigurationContainer(target, source, sourceDominant, context);
      this.mergePluginExecution_Id(target, source, sourceDominant, context);
      this.mergePluginExecution_Phase(target, source, sourceDominant, context);
      this.mergePluginExecution_Goals(target, source, sourceDominant, context);
   }

   protected void mergePluginExecution_Id(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getId();
      if (src != null && (sourceDominant || target.getId() == null)) {
         target.setId(src);
         target.setLocation("id", source.getLocation("id"));
      }

   }

   protected void mergePluginExecution_Phase(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getPhase();
      if (src != null && (sourceDominant || target.getPhase() == null)) {
         target.setPhase(src);
         target.setLocation("phase", source.getLocation("phase"));
      }

   }

   protected void mergePluginExecution_Goals(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
      List<String> src = source.getGoals();
      if (!src.isEmpty()) {
         List<String> tgt = target.getGoals();
         List<String> merged = new ArrayList(tgt.size() + src.size());
         merged.addAll(tgt);
         merged.addAll(src);
         target.setGoals(merged);
      }

   }

   protected void mergeResource(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeFileSet(target, source, sourceDominant, context);
      this.mergeResource_TargetPath(target, source, sourceDominant, context);
      this.mergeResource_Filtering(target, source, sourceDominant, context);
      this.mergeResource_MergeId(target, source, sourceDominant, context);
   }

   protected void mergeResource_TargetPath(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getTargetPath();
      if (src != null && (sourceDominant || target.getTargetPath() == null)) {
         target.setTargetPath(src);
         target.setLocation("targetPath", source.getLocation("targetPath"));
      }

   }

   protected void mergeResource_Filtering(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getFiltering();
      if (src != null && (sourceDominant || target.getFiltering() == null)) {
         target.setFiltering(src);
         target.setLocation("filtering", source.getLocation("filtering"));
      }

   }

   protected void mergeResource_MergeId(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getMergeId();
      if (src != null && (sourceDominant || target.getMergeId() == null)) {
         target.setMergeId(src);
      }

   }

   protected void mergeFileSet(FileSet target, FileSet source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergePatternSet(target, source, sourceDominant, context);
      this.mergeFileSet_Directory(target, source, sourceDominant, context);
   }

   protected void mergeFileSet_Directory(FileSet target, FileSet source, boolean sourceDominant, Map<Object, Object> context) {
      String src = source.getDirectory();
      if (src != null && (sourceDominant || target.getDirectory() == null)) {
         target.setDirectory(src);
         target.setLocation("directory", source.getLocation("directory"));
      }

   }

   protected void mergePatternSet(PatternSet target, PatternSet source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergePatternSet_Includes(target, source, sourceDominant, context);
      this.mergePatternSet_Excludes(target, source, sourceDominant, context);
   }

   protected void mergePatternSet_Includes(PatternSet target, PatternSet source, boolean sourceDominant, Map<Object, Object> context) {
      List<String> src = source.getIncludes();
      if (!src.isEmpty()) {
         List<String> tgt = target.getIncludes();
         List<String> merged = new ArrayList(tgt.size() + src.size());
         merged.addAll(tgt);
         merged.addAll(src);
         target.setIncludes(merged);
      }

   }

   protected void mergePatternSet_Excludes(PatternSet target, PatternSet source, boolean sourceDominant, Map<Object, Object> context) {
      List<String> src = source.getExcludes();
      if (!src.isEmpty()) {
         List<String> tgt = target.getExcludes();
         List<String> merged = new ArrayList(tgt.size() + src.size());
         merged.addAll(tgt);
         merged.addAll(src);
         target.setExcludes(merged);
      }

   }

   protected void mergeProfile(Profile target, Profile source, boolean sourceDominant, Map<Object, Object> context) {
      this.mergeModelBase(target, source, sourceDominant, context);
   }

   protected void mergeActivation(Activation target, Activation source, boolean sourceDominant, Map<Object, Object> context) {
   }

   protected Object getDependencyKey(Dependency dependency) {
      return dependency;
   }

   protected Object getPluginKey(Plugin object) {
      return object;
   }

   protected Object getPluginExecutionKey(PluginExecution object) {
      return object;
   }

   protected Object getReportPluginKey(ReportPlugin object) {
      return object;
   }

   protected Object getReportSetKey(ReportSet object) {
      return object;
   }

   protected Object getLicenseKey(License object) {
      return object;
   }

   protected Object getMailingListKey(MailingList object) {
      return object;
   }

   protected Object getDeveloperKey(Developer object) {
      return object;
   }

   protected Object getContributorKey(Contributor object) {
      return object;
   }

   protected Object getProfileKey(Profile object) {
      return object;
   }

   protected Object getRepositoryKey(Repository object) {
      return this.getRepositoryBaseKey(object);
   }

   protected Object getRepositoryBaseKey(RepositoryBase object) {
      return object;
   }

   protected Object getNotifierKey(Notifier object) {
      return object;
   }

   protected Object getResourceKey(Resource object) {
      return object;
   }

   protected Object getExtensionKey(Extension object) {
      return object;
   }

   protected Object getExclusionKey(Exclusion object) {
      return object;
   }
}
