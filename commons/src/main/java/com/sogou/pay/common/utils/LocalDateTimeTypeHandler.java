package com.sogou.pay.common.utils;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.time.LocalDateTime;

@MappedTypes(LocalDateTime.class)
public class LocalDateTimeTypeHandler implements TypeHandler<LocalDateTime> {
  @Override
  public void setParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType)
    throws SQLException {
    if (null != parameter) {
      ps.setTimestamp(i, Timestamp.valueOf(parameter));
    } else {
      ps.setTimestamp(i, null);
    }
  }

  @Override
  public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
    Timestamp ts = rs.getTimestamp(columnName);
    if (null == ts) return null;
    return ts.toLocalDateTime();
  }

  @Override
  public LocalDateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
    return cs.getTimestamp(columnIndex).toLocalDateTime();
  }

  @Override
  public LocalDateTime getResult(ResultSet rs, int columnIndex) throws SQLException {
    Timestamp ts = rs.getTimestamp(columnIndex);
    if (null == ts) return null;
    return ts.toLocalDateTime();
  }
}
