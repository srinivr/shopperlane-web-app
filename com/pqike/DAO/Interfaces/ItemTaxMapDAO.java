/*
 * Author: Srinivas
 */

package com.pqike.DAO.Interfaces;

import com.pqike.DAO.DAOException;
import com.pqike.bean.ItemTaxMap;
import java.sql.Connection;

/**
 *
 * @author User
 */
public interface ItemTaxMapDAO{    
    public void add(Connection con, ItemTaxMap item) throws DAOException;
    public void delete(Connection con, Integer skuId, Integer merchant) throws DAOException;
}
