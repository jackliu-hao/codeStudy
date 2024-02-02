package com.cym.service;

import com.cym.sqlhelper.utils.SqlHelper;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class LocationService {
   @Inject
   SqlHelper sqlHelper;
}
