/*
 * Author: Srinivas
 */

package com.pqike.DAO.Interfaces;

import com.pqike.DAO.DAOException;
import com.pqike.bean.AppItem;
import com.pqike.bean.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author User
 */

//Used for handling transaction across mutiple tables related to Items. ItemDAO focuses only on item tables.
public interface ItemDAOHelper {
    public ResultSet view(Connection con, String table, Integer clerkId, String sortColumn, String sortOrder, String keyword, String modelNumber, String skuIdString, Date beginDate, Date endDate, Integer discountMin, Integer discountMax, Integer costPriceMin, Integer costPriceMax, Integer markedPriceMin, Integer markedPriceMax, Integer sellingPriceMin, Integer sellingPriceMax, Integer pageNumber, Integer numResults) throws DAOException;
    public Integer viewCount(Connection con, String table, Integer clerkId, String sortColumn, String sortOrder, String keyword, String modelNumber, String skuIdString, Date beginDate, Date endDate, Integer discountMin, Integer discountMax, Integer costPriceMin, Integer costPriceMax, Integer markedPriceMin, Integer markedPriceMax, Integer sellingPriceMin, Integer sellingPriceMax) throws DAOException;
    public Boolean add(Connection con, PreparedStatement pt, Item item, String tableName, List<Integer> taxIds, Integer offerGroup) throws SQLException;
    public Integer viewSkuId(Connection con, Integer itemId, Integer merchant, String tableName) throws SQLException;
    public void update(Connection con, PreparedStatement pt, Item item, Integer oldSkuId, String tableName, List<Integer> taxIds, Integer offerGroup) throws SQLException;
    public void delete(Connection con, Integer id, Integer merchant, String tableName) throws SQLException;
}
