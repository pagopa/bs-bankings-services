package it.pagopa.bs.web.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import lombok.SneakyThrows;

public class ToJsonStringTypeHandler implements TypeHandler<Object> {

    @Override
    public Object getResult(ResultSet arg0, String i) throws SQLException {
        return null; // unused for reading
    }

    @Override
    public Object getResult(ResultSet resultSet, int i) throws SQLException {
        return null; // unused for reading
    }

    @Override
    public Object getResult(CallableStatement callableStatement, int i) throws SQLException {
        return null; // unused for reading
    }

    @Override
    @SneakyThrows
    public void setParameter(PreparedStatement preparedStatement, int i, Object value, JdbcType jdbcType) throws SQLException {
        if(value instanceof Integer) {
            preparedStatement.setInt(i, (Integer)value);
        } else {
            preparedStatement.setString(i, String.valueOf(value));
        }
    }
}
