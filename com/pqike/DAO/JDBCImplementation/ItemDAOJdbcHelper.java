/*
 * Author: Srinivas
 */
package com.pqike.DAO.JDBCImplementation;

import com.pqike.DAO.DAOException;
import com.pqike.DAO.DAOFactory;
import com.pqike.DAO.DAOUtil;
import com.pqike.DAO.Interfaces.ItemDAOHelper;
import com.pqike.DAO.Interfaces.ItemOfferGroupMapDAO;
import com.pqike.DAO.Interfaces.ItemTaxMapDAO;
import com.pqike.DAO.Interfaces.SkuCategoryMapDAO;
import com.pqike.bean.Item;
import com.pqike.bean.ItemOfferGroupMap;
import com.pqike.bean.ItemTaxMap;
import com.pqike.bean.SkuCategoryMap;
import com.pqike.model.ClerkSessionStore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author User
 */
public class ItemDAOJdbcHelper implements ItemDAOHelper {

    private static final String VIEWQUERY_1 = "select * from billpulp.`%s` where merchant='%s'";
    private static final String VIEWQUERY_2 = " %s %s limit ";
    private static final String VIEWCOUNTQUERY_1 = "select count(id) from billpulp.`%s` where merchant='%s'";
    private static final String VIEWCOUNTQUERY_2 = " %s %s ";
    private static final String VIEWSKUID = "Select sku_id from billpulp.`%s` where id=? and merchant=?";
    private static final String DELETE = "delete from billpulp.`%s` where id=? and merchant=?"; 
    
    @Override
    public ResultSet view(Connection con, String table, Integer clerkId, String sortColumn, String sortOrder, String keyword, String modelNumber, String skuIdString, Date beginDate, Date endDate, Integer discountMin, Integer discountMax, Integer costPriceMin, Integer costPriceMax, Integer markedPriceMin, Integer markedPriceMax, Integer sellingPriceMin, Integer sellingPriceMax, Integer pageNumber, Integer numResults) throws DAOException {
        if (con == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (table == null) {
            throw new IllegalArgumentException("Table Name is null");
        }
        if (clerkId == null) {
            throw new IllegalArgumentException("ClerkId is null");
        }
        if(pageNumber == null)
            throw new IllegalArgumentException("Page Number is null");
        if(numResults == null)
            throw new IllegalArgumentException("Number of results is null");
        System.out.println("ItemDAOJdbcHelper");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Integer _begin = pageNumber * numResults;
        String role = ClerkSessionStore.getClerkSessionStore().getClerkRole(clerkId);
        Integer merchant = ClerkSessionStore.getClerkSessionStore().getClerkMerchant(clerkId);
        System.out.println("Shirt-ViewItems clerk, merchant" + clerkId + "," + merchant);
        String _query = String.format(VIEWQUERY_1, table, merchant) ;
        StringBuilder _builder = new StringBuilder();
        String query;
        Integer skuId;
        try {
            skuId = Integer.parseInt(skuIdString);
        } catch (NumberFormatException e) {
            skuId = null;
        }
        if (skuId != null) {
            //query = String.format(_query, String.format("and sku_id='%s'", skuId), "");
            _builder.append(String.format("and sku_id='%s'", skuId));
        }
        /*else*/ if (keyword != null && keyword != "") {
            System.out.println("Keyword is " + keyword);
            //String temp = "and (name like '%"+ keyword +"%' or description like '%"+ keyword +"%')";
            //query = String.format(_query, temp, "");
            _builder.append("and (name like '%" + keyword + "%' or description like '%" + keyword + "%')");
        }
        if (modelNumber != null && modelNumber != "") {
            _builder.append(" and model_number like '%" + modelNumber + "%'");
        }
        //else
        //{
        if (beginDate != null) {
            String _date;
            try {
                _date = formatter.format(beginDate);
                _builder.append(String.format(" and last_operation_time >= '%s'", _date));
            } catch (Exception e) {
            }

        }
        if (endDate != null) {
            String _date;
            try {
                _date = formatter.format(endDate);
                _builder.append(String.format(" and last_operation_time <= '%s'", _date));
            } catch (Exception e) {
            }
        }
        if (discountMin != null) {
            _builder.append(String.format(" and discount >= '%s'", discountMin));
        }
        if (discountMax != null) {
            _builder.append(String.format(" and discount <= '%s'", discountMax));
        }
        if (sellingPriceMin != null) {
            _builder.append(String.format(" and selling_price >= '%s'", sellingPriceMin));
        }
        if (sellingPriceMax != null) {
            _builder.append(String.format(" and selling_price <= '%s'", sellingPriceMax));
        }
        if (costPriceMin != null) {
            _builder.append(String.format(" and cost_price >= '%s'", costPriceMin));
        }
        if (costPriceMax != null) {
            _builder.append(String.format(" and cost_price <= '%s'", costPriceMax));
        }
        if (markedPriceMin != null) {
            _builder.append(String.format(" and marked_price >= '%s'", markedPriceMin));
        }
        if (markedPriceMax != null) {
            _builder.append(String.format(" and marked_price <= '%s'", markedPriceMax));
        }
        String _orderCondition;
        if ("Ascending".equals(sortColumn)) {
            _orderCondition = String.format(" order by '%s' '%s'", sortColumn, "asc");
        } else {
            _orderCondition = String.format(" order by '%s' '%s'", sortColumn, "desc");
        }
        query = String.format(VIEWQUERY_2, _builder.toString(), _orderCondition);
        query = _query + query + + _begin + "," + numResults + " ;";
        //}
        System.out.println("ViewItemQuery is " + query);
        Statement st = null;
        ResultSet rs;
        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DAOUtil.close(st);
        }
    }

    @Override
    public Integer viewCount(Connection con, String table, Integer clerkId, String sortColumn, String sortOrder, String keyword, String modelNumber, String skuIdString, Date beginDate, Date endDate, Integer discountMin, Integer discountMax, Integer costPriceMin, Integer costPriceMax, Integer markedPriceMin, Integer markedPriceMax, Integer sellingPriceMin, Integer sellingPriceMax) throws DAOException {
        if (con == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (table == null) {
            throw new IllegalArgumentException("Table Name is null");
        }
        if (clerkId == null) {
            throw new IllegalArgumentException("ClerkId is null");
        }
        System.out.println("Shirt-ViewItems");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Integer merchant = ClerkSessionStore.getClerkSessionStore().getClerkMerchant(clerkId);
        System.out.println("Shirt-ViewItems clerk, merchant" + clerkId + "," + merchant);
        String _query = String.format(VIEWCOUNTQUERY_1, table, merchant);
        StringBuilder _builder = new StringBuilder();
        String query;
        Integer skuId;
        try {
            skuId = Integer.parseInt(skuIdString);
        } catch (NumberFormatException e) {
            skuId = null;
        }
        if (skuId != null) {
            //query = String.format(_query, String.format("and sku_id='%s'", skuId), "");
            _builder.append(String.format("and sku_id='%s'", skuId));
        }
        /*else*/ if (keyword != null && keyword != "") {
            System.out.println("Keyword is " + keyword);
            //String temp = "and (name like '%"+ keyword +"%' or description like '%"+ keyword +"%')";
            //query = String.format(_query, temp, "");
            _builder.append("and (name like '%" + keyword + "%' or description like '%" + keyword + "%')");
        }
        if (modelNumber != null && modelNumber != "") {
            _builder.append(" and model_number like '%" + modelNumber + "%'");
        }
        //else
        //{
        if (beginDate != null) {
            String _date;
            try {
                _date = formatter.format(beginDate);
                _builder.append(String.format(" and last_operation_time >= '%s'", _date));
            } catch (Exception e) {
            }

        }
        if (endDate != null) {
            String _date;
            try {
                _date = formatter.format(endDate);
                _builder.append(String.format(" and last_operation_time <= '%s'", _date));
            } catch (Exception e) {
            }
        }
        if (discountMin != null) {
            _builder.append(String.format(" and discount >= '%s'", discountMin));
        }
        if (discountMax != null) {
            _builder.append(String.format(" and discount <= '%s'", discountMax));
        }
        if (sellingPriceMin != null) {
            _builder.append(String.format(" and selling_price >= '%s'", sellingPriceMin));
        }
        if (sellingPriceMax != null) {
            _builder.append(String.format(" and selling_price <= '%s'", sellingPriceMax));
        }
        if (costPriceMin != null) {
            _builder.append(String.format(" and cost_price >= '%s'", costPriceMin));
        }
        if (costPriceMax != null) {
            _builder.append(String.format(" and cost_price <= '%s'", costPriceMax));
        }
        if (markedPriceMin != null) {
            _builder.append(String.format(" and marked_price >= '%s'", markedPriceMin));
        }
        if (markedPriceMax != null) {
            _builder.append(String.format(" and marked_price <= '%s'", markedPriceMax));
        }
        String _orderCondition;
        _orderCondition = "";
        if ("Ascending".equals(sortColumn)) {
            _orderCondition = String.format(" order by '%s' '%s'", sortColumn, "asc");
        } else if ("Descending".equals(sortColumn)) {
            _orderCondition = String.format(" order by '%s' '%s'", sortColumn, "desc");
        }
        query = _query + String.format(VIEWCOUNTQUERY_2, _builder.toString(), _orderCondition);
        //}
        System.out.println("ViewItemQuery is " + query);
        Statement st = null;
        ResultSet rs = null;
        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            if(rs.next())
                return rs.getInt(1);
            return null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DAOUtil.close(st);
        }
    }

    @Override
    public Boolean add(Connection con, PreparedStatement pt, Item item, String tableName, List<Integer> taxIds, Integer offerGroup) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (pt == null) {
            throw new IllegalArgumentException("PreparedStatement is null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        ResultSet rs = null;
        SkuCategoryMapDAO scmDAO = DAOFactory.getSkuCategoryMapDAO();
        ItemTaxMapDAO itmDAO = DAOFactory.getItemTaxMapDAO();
        ItemOfferGroupMapDAO iogmdao = DAOFactory.getItemOfferGroupMapDAO();
        try {
            pt.executeUpdate();
            rs = pt.getGeneratedKeys();

            SkuCategoryMap scm = new SkuCategoryMap();
            scm.setSkuId(item.getSkuId());
            scm.setMerchant(item.getMerchant());
            scm.setTableName(tableName);
            scmDAO.add(con, scm);

            ItemTaxMap itm = new ItemTaxMap();
            ItemOfferGroupMap iogm = new ItemOfferGroupMap();
            if (rs.next()) {
                itm.setSkuId(item.getSkuId());
                itm.setMerchant(item.getMerchant());
                for (Integer tax : taxIds) {
                    itm.setTaxId(tax);
                    itmDAO.add(con, itm);
                }
                iogm.setMerchant(item.getMerchant());
                iogm.setOfferGroupId(offerGroup);
                iogm.setSkuId(item.getSkuId());
                if(offerGroup != null)
                    iogmdao.add(iogm);
                return true;
            } else {
                return false;
            }
        } finally {
            DAOUtil.close(rs);
        }

    }

    @Override
    public Integer viewSkuId(Connection con, Integer itemId, Integer merchant, String tableName) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("item id is null");
        }
        if (merchant == null) {
            throw new IllegalArgumentException("merchant is null");
        }
        PreparedStatement pt = null;
        ResultSet rs = null;
        try {
            Object[] args = {
                itemId,
                merchant
            };
            String tableNameQuery = String.format(VIEWSKUID, tableName);
            System.out.println(tableNameQuery);
            pt = DAOUtil.prepareStatement(con, false, tableNameQuery, args);
            rs = pt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        } finally {
            DAOUtil.close(pt, rs);
        }
    }

    public void update(Connection con, PreparedStatement pt, Item item, Integer oldSkuId, String tableName, List<Integer> taxIds, Integer offerGroup) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (pt == null) {
            throw new IllegalArgumentException("PreparedStatement is null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        SkuCategoryMapDAO scmDAO = DAOFactory.getSkuCategoryMapDAO();
        ItemTaxMapDAO itmDAO = DAOFactory.getItemTaxMapDAO();
        ItemOfferGroupMapDAO igmdao = DAOFactory.getItemOfferGroupMapDAO();
        try {
            SkuCategoryMap scm = new SkuCategoryMap();
            scm.setSkuId(item.getSkuId());
            scm.setMerchant(item.getMerchant());
            scm.setTableName(tableName);
            scmDAO.update(con, oldSkuId, scm);
            int affected = pt.executeUpdate();
            if (affected < 1) {
                throw new DAOException("No row has been updated");
            }
            itmDAO.delete(con, item.getSkuId(), item.getMerchant());
            igmdao.delete(item.getSkuId(), item.getMerchant());
            ItemTaxMap itm = new ItemTaxMap();
            itm.setSkuId(item.getSkuId());
            itm.setMerchant(item.getMerchant());
            for (Integer tax : taxIds) {
                itm.setTaxId(tax);
                itmDAO.add(con, itm);
            }
            if(offerGroup != null) {
                ItemOfferGroupMap iogm = new ItemOfferGroupMap();
                iogm.setMerchant(item.getMerchant());
                iogm.setOfferGroupId(offerGroup);
                iogm.setSkuId(item.getSkuId());
                igmdao.add(iogm);
            }
        } finally {
            
        }
    }

    public void delete(Connection con, Integer id, Integer clerkId, String tableName) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("Table Name is null");
        }
        Integer merchant = ClerkSessionStore.getClerkSessionStore().getClerkMerchant(clerkId);
        Integer skuId = viewSkuId(con, id, merchant, tableName); 
        if (skuId == null) {
            throw new DAOException("No such item exists");
        }
        PreparedStatement pt = null;
        try {
            Object[] args = {
                id,
                merchant
            };
            pt = DAOUtil.prepareStatement(con, false, String.format(DELETE, tableName), args);
            int aff = pt.executeUpdate();
            if(aff<0)
                throw new DAOException("No row has been deleted");
            SkuCategoryMapDAO scmdao = DAOFactory.getSkuCategoryMapDAO();
            scmdao.delete(con, skuId, merchant);
            ItemTaxMapDAO itmdao = DAOFactory.getItemTaxMapDAO();
            ItemOfferGroupMapDAO iogmdao = DAOFactory.getItemOfferGroupMapDAO();
            iogmdao.delete(skuId, merchant);
            itmdao.delete(con, skuId, merchant);
        } finally {
            DAOUtil.close(pt);
        }
    }
}
