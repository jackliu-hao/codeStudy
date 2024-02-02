package com.google.protobuf;

interface SchemaFactory {
  <T> Schema<T> createSchema(Class<T> paramClass);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\SchemaFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */