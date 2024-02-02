module com.zaxxer.hikari {
  requires java.base;
  requires java.sql;
  requires java.management;
  requires java.naming;
  requires org.slf4j;
  requires org.hibernate.orm.core;
  requires simpleclient;
  requires metrics.core;
  requires metrics.healthchecks;
  requires micrometer.core;
  
  exports com.zaxxer.hikari;
  exports com.zaxxer.hikari.hibernate;
  exports com.zaxxer.hikari.metrics;
  exports com.zaxxer.hikari.metrics.dropwizard;
  exports com.zaxxer.hikari.metrics.micrometer;
  exports com.zaxxer.hikari.metrics.prometheus;
  exports com.zaxxer.hikari.pool;
  exports com.zaxxer.hikari.util;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\META-INF\versions\11\module-info.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */