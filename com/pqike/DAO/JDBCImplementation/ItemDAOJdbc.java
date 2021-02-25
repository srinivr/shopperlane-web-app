/*
 * Author: Srinivas
 */
package com.pqike.DAO.JDBCImplementation;

import com.pqike.DAO.DAOFactory;
import com.pqike.DAO.DAOException;
import com.pqike.DAO.DAOUtil;
import com.pqike.DAO.Interfaces.ItemDAO;
import com.pqike.DAO.Interfaces.ItemDAOHelper;
import com.pqike.bean.AppItem;
import com.pqike.bean.AppMoreDetail;
import com.pqike.bean.AppSimilarItem;
import com.pqike.bean.Item;
import com.pqike.model.ClerkSessionStore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public abstract class ItemDAOJdbc implements ItemDAO {

    private static final String BILLDESCRIPTION = "bill_description";
    private static final String BRAND = "brand";
    private static final String CLERKID = "clerk_id";
    private static final String COSTPRICE = "cost_price";
    private static final String DESCRIPTION = "description";
    private static final String DISCOUNT = "discount";
    private static final String ID = "id";
    private static final String INSTOCK = "in_stock";
    private static final String LASTOPERATIONTIME = "last_operation_time";
    private static final String MARKEDPRICE = "marked_price";
    private static final String MERCHANT = "merchant";
    private static final String MODELNUMBER = "model_number";
    private static final String NAME = "name";
    private static final String OFFLINERESERVE = "offline_reserve";
    private static final String REWARD = "reward";
    private static final String SELLINGPRICE = "selling_price";
    private static final String SKUID = "sku_id";
    private static final String SUPPLIERID = "supplier_id";
    private static final String THRESHOLD = "threshold";
    private static final DecimalFormat dft = new DecimalFormat();

    private static final String VIEWITEMSCOUNT = "select count(id) from `%s`.`%s` %s";
    private static final String VIEWITEMSMINIMAL = "select id, name, cost_price, marked_price, selling_price, discount, in_stock %s from `%s`.`%s` %s limit %s, %s";
    private static final String VIEWSIMILARITEMSKEY = "select id %s from `%s`.`%s` where merchant = (select merchant from `%s`.`%s` where id = ?) and model_number = (select model_number from `%s`.`%s` where id = ?)";
    private static final String VIEWMOREDETAILS = "select name, description %s from `%s`.`%s` where id = ?";
    private static final String VIEWCARTITEMS = "select id, name, cost_price, marked_price, selling_price, discount, in_stock %s from `%s`.`%s` where id = ?";

    @Override
    public List<Integer> add(List<Item> items, List<Integer> taxIds, Integer offerGroup) throws DAOException {
        if (items == null) {
            throw new IllegalArgumentException("Null item passed");
        }
        DAOFactory cdf = DAOFactory.getInstance(getDB());
        Connection con = null;
        PreparedStatement pt = null;
        ItemDAOHelper itmdao;
        try {
            con = cdf.getConnection();
            con.setAutoCommit(false);
            pt = DAOUtil.emptyPreparedStatement(con, true, getInsert());
            itmdao = DAOFactory.getItemHelperDAO();
            for (Item item : items) {
                if (hasNull(item)) {
                    con.rollback();
                    throw new IllegalArgumentException("Item has null value");
                }
                Object[] args = makeArgs(item, false);
                DAOUtil.setValues(pt, args);
                if (!itmdao.add(con, pt, item, getTable(), taxIds, offerGroup)) {
                    con.rollback();
                    throw new DAOException(items + " has not been added");
                }
            }
            con.commit();
            ResultSet rs = pt.getGeneratedKeys();
            if (rs.next()) {
                List<Integer> returnIds = new ArrayList<>();
                returnIds.add(rs.getInt(1));
                while (rs.next()) {
                    returnIds.add(rs.getInt(1));
                }
                return returnIds;
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DAOUtil.close(con, pt);
        }

    }

    @Override
    public Integer edit(Item itm, List<Integer> taxIds, Integer offerGroup) throws DAOException {
        if (itm == null) {
            throw new IllegalArgumentException("Null item passed");
        }
        if (itm.hasNull()) {
            throw new IllegalArgumentException(itm + " has null value");
        }
        DAOFactory cdf = DAOFactory.getInstance(getDB());
        Connection con = null;
        PreparedStatement pt = null;
        ItemDAOHelper itmdao = DAOFactory.getItemHelperDAO();
        try {
            con = cdf.getConnection();
            con.setAutoCommit(false);
            Integer oldSkuId = itmdao.viewSkuId(con, itm.getId(), itm.getMerchant(), getTable());
            if (oldSkuId == null) {
                throw new DAOException(itm + " does not have a matching sku id");
            }
            Object[] args = makeArgs(itm, false);
            Object[] argsNew = new Object[args.length + 2]; //To include the id and merchant in the where clause
            System.arraycopy(args, 0, argsNew, 0, args.length);
            argsNew[argsNew.length - 2] = itm.getId();
            argsNew[argsNew.length - 1] = itm.getMerchant();
            pt = DAOUtil.prepareStatement(con, false, getUpdate(), argsNew);
            itmdao.update(con, pt, itm, oldSkuId, getTable(), taxIds, offerGroup);
            con.commit();
            return itm.getId();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DAOUtil.close(con, pt);
        }
    }

    @Override
    public Item fetch(Integer id, Integer clerkId) throws DAOException {
        if (id == null) {
            throw new IllegalArgumentException("Null Id passed");
        }
        if (clerkId == null) {
            throw new IllegalArgumentException("Null clerkId passed");
        }
        Integer merchant = ClerkSessionStore.getClerkSessionStore().getClerkMerchant(clerkId);
        DAOFactory cdf = DAOFactory.getInstance(getDB());
        Connection con = null;
        PreparedStatement pt = null;
        ResultSet rs = null;
        try {
            con = cdf.getConnection();
            Object[] args = {
                id,
                merchant
            };
            pt = DAOUtil.prepareStatement(con, false, getFetch(), args);
            rs = pt.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DAOUtil.close(con, pt, rs);
        }
    }

    @Override
    public List<Item> view(Integer clerkId, String sortColumn, String sortOrder, String keyword, String modelNumber, String skuIdString, Date beginDate, Date endDate, Integer discountMin, Integer discountMax, Integer costPriceMin, Integer costPriceMax, Integer markedPriceMin, Integer markedPriceMax, Integer sellingPriceMin, Integer sellingPriceMax, Integer pageNumber, Integer numResults) throws DAOException {
        DAOFactory cdf = DAOFactory.getInstance(getDB());
        Connection con = null;
        ResultSet rs = null;
        try {
            con = cdf.getConnection();
            ItemDAOHelper itmdao = DAOFactory.getItemHelperDAO();
            rs = itmdao.view(con, getTable(), clerkId, sortColumn, sortOrder, keyword, modelNumber, skuIdString, beginDate, endDate, discountMin, discountMax, costPriceMin, costPriceMax, markedPriceMin, markedPriceMax, sellingPriceMin, sellingPriceMax, pageNumber, numResults);
            List<Item> items = new ArrayList<>();
            while (rs.next()) {
                items.add(map(rs));
            }
            return items;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DAOUtil.close(con, rs);
        }
    }

    @Override
    public Integer viewCount(Integer clerkId, String sortColumn, String sortOrder, String keyword, String modelNumber, String skuIdString, Date beginDate, Date endDate, Integer discountMin, Integer discountMax, Integer costPriceMin, Integer costPriceMax, Integer markedPriceMin, Integer markedPriceMax, Integer sellingPriceMin, Integer sellingPriceMax) throws DAOException {
        DAOFactory cdf = DAOFactory.getInstance(getDB());
        Connection con = null;
        ResultSet rs = null;
        try {
            con = cdf.getConnection();
            ItemDAOHelper itmdao = DAOFactory.getItemHelperDAO();
            return itmdao.viewCount(con, getTable(), clerkId, sortColumn, sortOrder, keyword, modelNumber, skuIdString, beginDate, endDate, discountMin, discountMax, costPriceMin, costPriceMax, markedPriceMin, markedPriceMax, sellingPriceMin, sellingPriceMax);
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DAOUtil.close(con, rs);
        }
    }

    @Override
    public boolean delete(Integer id, Integer clerkId) throws DAOException {
        DAOFactory cdf = DAOFactory.getInstance(getDB());
        Connection con = null;
        try {
            con = cdf.getConnection();
            con.setAutoCommit(false);
            ItemDAOHelper itmdao = DAOFactory.getItemHelperDAO();

            itmdao.delete(con, id, clerkId, getTable());
            con.commit();
            return true;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DAOUtil.close(con);
        }
    }

    @Override
    public List<AppItem> viewItems(int from, int limit, Integer merchant) {
        DAOFactory cdf = DAOFactory.getInstance(getDB());
        Connection con = null;
        try {
            con = cdf.getConnection();
            return viewBatchItems(con, getDB(), getTable(), from, limit, merchant);
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DAOUtil.close(con);
        }
    }

    @Override
    public int viewClientItemsCount(Integer merchant) {
        DAOFactory cdf = DAOFactory.getInstance(getDB());
        Connection con = null;
        try {
            con = cdf.getConnection();
            return viewAppItemsCount(con, getDB(), getTable(), merchant);
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DAOUtil.close(con);
        }
    }

    @Override
    public AppSimilarItem similarItems(Integer id) {
        String key = getSimilarItemKey();
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }
        if (key == null) {
            return null;
        }
        if (key.trim().length() == 0) {
            throw new IllegalArgumentException("Key is empty. Pass null for no key");
        }
        Connection con = null;
        PreparedStatement pt = null;
        ResultSet rs = null;
        try {
            String query = String.format(VIEWSIMILARITEMSKEY, "," + key, getDB(), getTable(), getDB(), getTable(), getDB(), getTable());
            con = DAOFactory.getInstance(getDB()).getConnection();
            pt = DAOUtil.prepareStatement(con, false, query, new Object[]{id, id});
            rs = pt.executeQuery();
            List<Integer> ids = new ArrayList<Integer>();
            List<String> keys = new ArrayList<String>();
            while (rs.next()) {
                ids.add(rs.getInt(ID));
                keys.add(rs.getString(key));
            }
            return new AppSimilarItem(key, ids, keys);
        } catch (SQLException ex) {
            Logger.getLogger(ItemDAOJdbc.class.getName()).log(Level.SEVERE, null, ex);
            throw new DAOException(ex);
        } finally {
            DAOUtil.close(con, rs, pt);
        }
    }

    @Override
    public AppMoreDetail getMoreDetails(Integer id) {
        Connection con = null;
        PreparedStatement pt = null;
        ResultSet rs = null;
        try {
            con = DAOFactory.getInstance(getDB()).getConnection();
            pt = DAOUtil.prepareStatement(con, false, String.format(VIEWMOREDETAILS, (getMoreDetailKeys() == null) ? "" : "," + getMoreDetailKeys(), getDB(), getTable()), new Object[]{id});
            rs = pt.executeQuery();
            if (rs.next()) {
                AppMoreDetail itm = new AppMoreDetail();
                itm.setName(rs.getString(NAME));
                itm.setDescription(rs.getString(DESCRIPTION));
                itm = setMoreDetailsToItem(rs, itm);
                return itm;
            }
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(ItemDAOJdbc.class.getName()).log(Level.SEVERE, null, ex);
            throw new DAOException(ex);
        } finally {
            DAOUtil.close(con, pt, rs);
        }
    }
    
    @Override
    public AppItem viewSingleItem(Integer id) {
        Connection con = null;
        PreparedStatement pt = null;
        ResultSet rs = null;
        try {
            con = DAOFactory.getInstance(getDB()).getConnection();
            String temp = getSimilarItemKey();
            if(temp != null && temp.trim().length() > 1)
                temp = ", " + temp;
            else
                temp = "";
            System.out.println("Query is " + String.format(VIEWCARTITEMS, temp, getDB(), getTable()));
            pt = DAOUtil.prepareStatement(con, false, String.format(VIEWCARTITEMS, temp, getDB(), getTable()), new Object[]{id});
            rs = pt.executeQuery();           
            if(rs.next()) {
                return makeMinimal(rs, true, false);
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DAOUtil.close(con, pt, rs);
        }
    }

    private List<AppItem> viewBatchItems(Connection con, String db, String table, int from, int limit, Integer merchant) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (db == null || "".equals(db)) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (table == null || "".equals(table)) {
            throw new IllegalArgumentException("Connection is null");
        }
        String merchantQuery = "";
        if(merchant != null)
            merchantQuery = " where merchant=" + merchant +" ";
        PreparedStatement pt = null;
        ResultSet rs = null;
        try {
            pt = DAOUtil.prepareStatement(con, false, String.format(VIEWITEMSMINIMAL, getMinimalItemKeyQuery(), db, table, merchantQuery, from, limit), new Object[]{});
            rs = pt.executeQuery();
            List<AppItem> itms = new ArrayList<AppItem>();
            while (rs.next()) {
                itms.add(makeMinimal(rs));
            }
            return itms;
        } finally {
            DAOUtil.close(pt, rs);
        }
    }

    private int viewAppItemsCount(Connection con, String db, String table, Integer merchant) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (db == null || "".equals(db)) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (table == null || "".equals(table)) {
            throw new IllegalArgumentException("Connection is null");
        }
        String merchantQuery = "";
        if(merchant != null)
            merchantQuery = " where merchant=" + merchant +" ";
        PreparedStatement pt = null;
        ResultSet rs = null;
        try {
            System.out.println("ItemDAOJdbc viewAppitemsCount Query is " + String.format(VIEWITEMSCOUNT, db, table, merchantQuery));
            pt = DAOUtil.prepareStatement(con, false, String.format(VIEWITEMSCOUNT, db, table, merchantQuery), new Object[]{});
            rs = pt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DAOUtil.close(pt, rs);
        }
    }

    private AppItem makeMinimal(ResultSet rs) throws SQLException {
        return makeMinimal(rs, false, true);
    }

    private AppItem makeMinimal(ResultSet rs, boolean count, boolean minimal) throws SQLException {
        AppItem mItm = new AppItem();
        Integer discount = rs.getInt(DISCOUNT);
        mItm.setId(rs.getString(ID));
        mItm.setBrand(rs.getString(NAME));
        if (count) {
            if (rs.getInt(INSTOCK) >= 10) {
                mItm.setInStock("10");
            } else {
                mItm.setInStock(rs.getInt(INSTOCK)+"");
            }

        } else {
            if (rs.getInt(INSTOCK) > 0) {
                mItm.setInStock("In Stock");
            } else {
                mItm.setInStock("Sold out");
            }
        }
        if (minimal) {
            mItm.setKey(setMinimalItemKeyValue(rs));
        } else {
            mItm.setKey(setSimilarItemKeyValue(rs));
        }
        Integer costPrice = (int) (rs.getInt(COSTPRICE) / 100.0 + 0.5); //mrp
        Integer markedPrice = (int) (rs.getInt(MARKEDPRICE) / 100.0 + 0.5); //Seller price
        if (markedPrice < costPrice) {
            mItm.setMrp(costPrice + "");
        } else { //mrp  equals cost price
            mItm.setMrp(null);
        }
        if (discount <= 0) {
            mItm.setDiscount(null);
            mItm.setSellerPrice(null);
        } else {
            mItm.setDiscount(dft.format(discount / 100.0) + "% off");
            mItm.setSellerPrice(markedPrice + "");
        }
        mItm.setFinalPrice((int) (rs.getInt(SELLINGPRICE) / 100.0 + 0.5) + "");
        return mItm;
    }

    private Object[] makeArgs(Item itm, boolean includeId) {
        Object[] specificArgs = specificArgs(itm);
        Integer sellingPrice = (int) (itm.getMarkedPrice() * ((10000.0 - itm.getDiscount()) / 10000.0) + 0.5);
        Object[] generalArgs = {
            itm.getBrand(),
            itm.getName(),
            itm.getDescription(),
            sellingPrice,
            itm.getDiscount(),
            itm.getMerchant(),
            itm.getInStock(),
            itm.getSkuId(),
            itm.getClerkId(),
            itm.getSupplierId(),
            itm.getCostPrice(),
            itm.getMarkedPrice(),
            itm.getThreshold(),
            itm.getModelNumber(),
            itm.isVisible(),
            itm.isReward(),
            itm.getOfflineReserve(),
            itm.getBillDescription(),};
        Object[] args = new Object[generalArgs.length + specificArgs.length];
        System.arraycopy(generalArgs, 0, args, 0, generalArgs.length);
        for (int i = generalArgs.length; i < args.length; i++) {
            args[i] = specificArgs[i - generalArgs.length];
        }
        if (includeId) {
            Object[] argsNew = new Object[args.length + 1];
            argsNew[0] = itm.getId();
            for (int i = 1; i < argsNew.length; i++) {
                argsNew[i] = args[i - 1];
            }
            return argsNew;
        }
        return args;
    }

    public Item map(ResultSet rs) throws SQLException {
        Item itm = specificItem();
        itm.setBillDescription(rs.getString(BILLDESCRIPTION));
        itm.setBrand(rs.getString(BRAND));
        itm.setClerkId(rs.getInt(CLERKID));
        itm.setCostPrice(rs.getInt(COSTPRICE));
        itm.setDescription(rs.getString(DESCRIPTION));
        itm.setDiscount(rs.getInt(DISCOUNT));
        itm.setId(rs.getInt(ID));
        itm.setInStock(rs.getInt(INSTOCK));
        itm.setLastModifiedTime(rs.getTimestamp(LASTOPERATIONTIME));
        itm.setMarkedPrice(rs.getInt(MARKEDPRICE));
        itm.setMerchant(rs.getInt(MERCHANT));
        itm.setModelNumber(rs.getString(MODELNUMBER));
        itm.setName(rs.getString(NAME));
        itm.setOfflineReserve(rs.getInt(OFFLINERESERVE));
        itm.setReward(rs.getBoolean(REWARD));
        itm.setSellingPrice(rs.getInt(SELLINGPRICE));
        itm.setSkuId(rs.getInt(SKUID));
        itm.setSupplierId(rs.getInt(SUPPLIERID));
        itm.setThreshold(rs.getInt(THRESHOLD));
        return mapSpecific(rs, itm);

    }

    public abstract String getInsert();

    public abstract String getTable();

    public abstract String getDB();

    public abstract String getUpdate();

    public abstract Object[] specificArgs(Item itm);

    public abstract boolean hasNull(Item item);

    public abstract Item mapSpecific(ResultSet rs, Item itm) throws SQLException;

    public abstract Item specificItem();

    public abstract String getFetch();

    public abstract String getMinimalItemKeyQuery();

    public abstract String getSimilarItemKey();

    public abstract String getMoreDetailKeys(); // CAN BE BUILT FROM getMoreDetailKeyList() but to avoid regenerating key for each it is hardcoded.

    public abstract List<String> getMoreDetailKeyList();

    public AppMoreDetail setMoreDetailsToItem(ResultSet rs, AppMoreDetail itm) throws SQLException {
        if (getMoreDetailKeyList() == null) {
            return itm;
        }
        itm.setKeys(getMoreDetailKeyList());
        List<String> valueList = new ArrayList<String>();
        for (String key : getMoreDetailKeyList()) {
            valueList.add(rs.getString(key));
        }
        itm.setValues(valueList);
        return itm;
    }

    public abstract String setMinimalItemKeyValue(ResultSet rs) throws SQLException;
    public abstract String setSimilarItemKeyValue(ResultSet rs) throws SQLException;
}
