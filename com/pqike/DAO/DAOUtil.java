/*
 * Author: Srinivas
 */
package com.pqike.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class DAOUtil {

    public static PreparedStatement prepareStatement(Connection con, boolean returnGeneratedKeys, String sql, Object... args) throws SQLException {
        PreparedStatement pt = con.prepareStatement(sql,
            returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
        setValues(pt, args);
        return pt;
    }
    public static PreparedStatement emptyPreparedStatement(Connection con, boolean returnGeneratedKeys, String sql, Object... args) throws SQLException {
        PreparedStatement pt = con.prepareStatement(sql,
            returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
        return pt;
    }
    public static void setValues(PreparedStatement pt, Object... args) throws SQLException{
        for(int i=0; i< args.length; i++){
            pt.setObject(i+1, args[i]);
        }
    }
    public static void close(Object... args) throws DAOException {
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            try {
                if (arg instanceof Connection) {
                    ((Connection) arg).close();
                }
                if (arg instanceof PreparedStatement) {
                    ((PreparedStatement) arg).close();
                }
                if (arg instanceof ResultSet) {
                    ((ResultSet) arg).close();
                } else {
                    //throw new DAOException("Attempt to close an Object which is not of type Connection, ResultSet, PreparedStatement");
                }

            } catch (SQLException e) {
                Logger l = Logger.getLogger("DAOUtil");
                l.log(Level.SEVERE, e.getMessage());
            }
        }
    }
}
