/*
 * Author: Srinivas
 */
package com.pqike.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class DriverManagerDAOFactory extends DAOFactory {
    private String url;
    private String userName;
    private String password;

    public DriverManagerDAOFactory(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }
    @Override
    public Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url, userName, password);
    }
}
