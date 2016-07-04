/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.common.utils;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月4日;
//-------------------------------------------------------
public class FileUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

  public static String getStringFromFile(String path) {
    try {
      return StringUtils.join(Files.readAllLines(Paths.get(path)), null);
    } catch (Exception e) {
      LOGGER.error(String.format("[getStringFromFile]path=%s", path), e);
      return null;
    }
  }

}
