/*
 * Author: Srinivas
 */

package com.pqike.DAO;

/**
 *
 * @author User
 */
public class DAOException extends RuntimeException {
    
    public DAOException(String message){
        super(message);
    }
    public DAOException(Throwable cause){
        super(cause);
    }
    public DAOException(String message, Throwable cause){
        super(message, cause);
    }
}
