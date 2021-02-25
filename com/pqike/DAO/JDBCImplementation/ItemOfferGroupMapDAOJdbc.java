/*
 * Author: Srinivas
 */

package com.pqike.DAO.JDBCImplementation;

import com.pqike.DAO.DAOException;
import com.pqike.DAO.DAOFactory;
import com.pqike.DAO.DAOUtil;
import com.pqike.DAO.Interfaces.ItemOfferGroupMapDAO;
import com.pqike.bean.ItemOfferGroupMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class ItemOfferGroupMapDAOJdbc implements ItemOfferGroupMapDAO{
    
    private static final String DB = "billpulp";
    private static final String INSERT = "insert into billpulp.item_offer_group_map values(?,?,?)";
    private static final String DELETE = "delete from billpulp.item_offer_group_map where sku_id = ? and merchant = ?";
    
    @Override
    public void add(ItemOfferGroupMap itm) throws DAOException {
        if(itm == null)
            throw new DAOException("Item is null");
        if(itm.hasNull())
            throw new DAOException("Item has null");
        DAOFactory cdf = DAOFactory.getInstance(DB);
        Connection con = null;
        PreparedStatement pt = null;
        try{
            con = cdf.getConnection();
            Object[] args = {
                itm.getSkuId(),
                itm.getMerchant(),
                itm.getOfferGroupId()
            };
            pt = DAOUtil.prepareStatement(con, false, INSERT, args);
            int aff = pt.executeUpdate();
            if(aff < 0)
                throw new DAOException("No row has been added");
        }
        catch(SQLException e){
            throw new DAOException(e);
        }
        finally{
            DAOUtil.close(con, pt);
        }
    }

    @Override
    public void delete(Integer skuId, Integer merchant) throws DAOException {
        if(skuId == null)
            throw new DAOException("Item id is null");
        if(merchant == null)
            throw new DAOException("Merchant is null");
        DAOFactory cdf = DAOFactory.getInstance(DB);
        Connection con = null;
        PreparedStatement pt = null;
        try{
            con = cdf.getConnection();
            Object[] args = {
                skuId,
                merchant
            };
            pt = DAOUtil.prepareStatement(con, false, DELETE, args);
            pt.executeUpdate();
        }
        catch(SQLException e){
            throw new DAOException(e);
        }
        finally{
            DAOUtil.close(con, pt);
        }
    }
    
}
