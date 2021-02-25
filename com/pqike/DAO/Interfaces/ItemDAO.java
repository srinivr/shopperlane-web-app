/*
 * Author: Srinivas
 */

package com.pqike.DAO.Interfaces;

import com.pqike.DAO.DAOException;
import com.pqike.bean.AppItem;
import com.pqike.bean.AppMoreDetail;
import com.pqike.bean.AppSimilarItem;
import com.pqike.bean.Item;
import java.util.Date;
import java.util.List;

/**
 *
 * @author User
 */
public interface ItemDAO {
    
    //Create
    public List<Integer> add(List<Item> items, List<Integer> taxIds, Integer offerGroup) throws DAOException;
    //View
    public Item fetch(Integer id, Integer merchant) throws DAOException;
    //Update
    public Integer edit(Item itm, List<Integer> taxIds, Integer offerGroup) throws DAOException;
    //Delete
    public boolean delete(Integer id, Integer clerkId) throws DAOException;
    

    public List<Item> view(Integer clerkId, String sortColumn, String sortOrder, String keyword, String modelNumber, String skuIdString, Date beginDate, Date endDate, Integer discountMin, Integer discountMax, Integer costPriceMin, Integer costPriceMax, Integer markedPriceMin, Integer markedPriceMax, Integer sellingPriceMin, Integer sellingPriceMax, Integer pageNumber, Integer numResults) throws DAOException;
    public Integer viewCount(Integer clerkId, String sortColumn, String sortOrder, String keyword, String modelNumber, String skuIdString, Date beginDate, Date endDate, Integer discountMin, Integer discountMax, Integer costPriceMin, Integer costPriceMax, Integer markedPriceMin, Integer markedPriceMax, Integer sellingPriceMin, Integer sellingPriceMax) throws DAOException;
    public AppSimilarItem similarItems(Integer id) throws DAOException; //return null for types where model number for schema will be unique
    public AppMoreDetail getMoreDetails(Integer id);
    //public List<Item> view(int from, int till);
    public List<AppItem> viewItems(int from, int limit, Integer merchant);
    public AppItem viewSingleItem(Integer id);
    public int viewClientItemsCount(Integer merchant);
}
