/*
 * Author: Srinivas
 */

package com.pqike.DAO.Interfaces;

import com.pqike.DAO.DAOException;
import com.pqike.bean.ItemOfferGroupMap;

/**
 *
 * @author User
 */
public interface ItemOfferGroupMapDAO {
    public void add(ItemOfferGroupMap itm) throws DAOException;
    public void delete(Integer skuId, Integer merchant) throws DAOException;
}
