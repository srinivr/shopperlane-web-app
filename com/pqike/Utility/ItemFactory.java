/*
 * Author: Srinivas
 * Call approriate DAO class methods based on 'reflection'.
 * Loads classes based on URL parameters
 */
package com.pqike.Utility;

import com.pqike.DAO.DAOException;
import com.pqike.bean.AppItem;
import com.pqike.bean.AppMoreDetail;
import com.pqike.bean.AppSimilarItem;
import com.pqike.bean.Item;
import com.pqike.model.ClerkSessionStore;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class ItemFactory {

    private static final int BATCHSIZE = 50;
    private static final String DAOClassPrefix = "com.pqike.DAO.JDBCImplementation";
    private static final String DAOClassSuffix = "DAOJdbc";
    private static final String BeanClassPrefix = "com.pqike.bean";
    //private static final String BeanClassSuffix = "DAOJdbc";

    private static Class getDAOClass(String category, String type) throws ClassNotFoundException {
        category = category.replaceAll(" ", "");
        category = category.toLowerCase();
        type = type.replaceAll(" ", "");
        String className = DAOClassPrefix + "." + category + "." + type + DAOClassSuffix;
        System.out.println("Class name is " + className);
        return Class.forName(className);

    }

    public static List<Integer> add(Integer clerkId, String category, String type, Map<String, String> values) {
        System.out.println("in add");
        List<Item> itms = itemListFromRequest(clerkId, category, type, values);
        System.out.println("got list item from req");
        if(itms == null)
            throw new DAOException("Corrupt values received");
        else
            System.out.println("List size " + itms.size());
        List<Integer> taxIds = new ArrayList<>();
        Integer taxId = null;
        try {
            taxId = Integer.parseInt(values.get("tax"));
            System.out.println("taxId is " + taxId);
            taxIds.add(taxId);
        } catch (Exception e) {

        }
        Integer offerGroupId = null;
        try {
            offerGroupId = Integer.parseInt(values.get("offergroup"));
            System.out.println("OfferGroup Id is "+ offerGroupId);
        } catch (Exception e) {

        }
        try{
            Class cls = getDAOClass(category, type);
            System.out.println("got dao class");
            Object obj = cls.newInstance();
            System.out.println("created instance");
            Method m = cls.getDeclaredMethod("add", List.class, List.class, Integer.class);
            System.out.println("method got");
            List<Integer> ids = (List<Integer>) m.invoke(obj, itms, taxIds, offerGroupId);
            System.out.println("method invoked - result size, result " + ids.size() + ", " + ids);
            return ids;
        } catch (ClassNotFoundException ex) {
            System.out.println("Item Factory " + ex);
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex.getCause());
            System.out.println(ex.getCause());
            System.out.println(".......");
            ex.printStackTrace();
        }
        catch(DAOException ex){
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }

    public static Integer edit(Integer clerkId, String category, String type, Map<String, String> values) throws DAOException {
        System.out.println("in edit");
        List<Item> itms = itemListFromRequest(clerkId, category, type, values);
        System.out.println("got list item from req");
        if(itms == null)
            throw new DAOException("Corrupt values received");
        else
            System.out.println("List size " + itms.size());
        List<Integer> taxIds = new ArrayList<>();
        Integer taxId = null;
        try {
            taxId = Integer.parseInt(values.get("tax"));
            System.out.println("taxId is " + taxId);
            taxIds.add(taxId);
        } catch (Exception e) {

        }
        Integer offerGroupId = null;
        try {
            offerGroupId = Integer.parseInt(values.get("offergroup"));
            System.out.println("OfferGroup Id is "+ offerGroupId);
        } catch (Exception e) {

        }
        try{
            Class cls = getDAOClass(category, type);
            System.out.println("got dao class");
            Object obj = cls.newInstance();
            System.out.println("created instance");
            Method m = cls.getDeclaredMethod("edit", Item.class, List.class, Integer.class);
            System.out.println("method got");
            Integer id = (Integer) m.invoke(obj, itms.get(0), taxIds, offerGroupId);
            System.out.println("method invoked - result size, result " + id);
            return id;
        } 
        catch(Exception e){
            throw new DAOException(e);
        }
//        catch (ClassNotFoundException ex) {
//            System.out.println("Item Factory " + ex);
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex.getCause());
//            System.out.println(ex.getCause());
//            System.out.println(".......");
//            ex.printStackTrace();
//        }
        
//        return null;
    }
    
    public static Item fetchItem(Integer id, Integer clerkId, String category, String type) {
        try {
            System.out.println("Item Factory - fetch Item id,ClerkId, category, type" + id +", " + clerkId +", " + category +", " + type);
            Class cls = getDAOClass(category, type);
            Object obj = cls.newInstance();
            Method m = cls.getDeclaredMethod("fetch", Integer.class, Integer.class);
            return (Item) m.invoke(obj, id, clerkId);
        } catch (InstantiationException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static List<Item> viewItems(String category, String type, Integer clerkId, String sortColumn, String sortOrder, String keyword, String modelNumber, String skuIdString, Date beginDate, Date endDate, Integer discountMin, Integer discountMax, Integer costPriceMin, Integer costPriceMax, Integer markedPriceMin, Integer markedPriceMax, Integer sellingPriceMin, Integer sellingPriceMax, Integer pageNumber, Integer numResults) {
        try {
            Class cls = getDAOClass(category, type);
            Object obj = cls.newInstance();
            Method m = cls.getDeclaredMethod("view", Integer.class, String.class, String.class, String.class, String.class, String.class, Date.class, Date.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class);
            return (List<Item>) m.invoke(obj, clerkId, sortColumn, sortOrder, keyword, modelNumber, skuIdString, beginDate, endDate, discountMin, discountMax, costPriceMin, costPriceMax, markedPriceMin, markedPriceMax, sellingPriceMin, sellingPriceMax, pageNumber, numResults);
        } catch (InstantiationException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static Integer viewItemsCount(String category, String type, Integer clerkId, String sortColumn, String sortOrder, String keyword, String modelNumber, String skuIdString, Date beginDate, Date endDate, Integer discountMin, Integer discountMax, Integer costPriceMin, Integer costPriceMax, Integer markedPriceMin, Integer markedPriceMax, Integer sellingPriceMin, Integer sellingPriceMax) {
        try {
            Class cls = getDAOClass(category, type);
            Object obj = cls.newInstance();
            Method m = cls.getDeclaredMethod("viewCount", Integer.class, String.class, String.class, String.class, String.class, String.class, Date.class, Date.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class);
            return (Integer) m.invoke(obj, clerkId, sortColumn, sortOrder, keyword, modelNumber, skuIdString, beginDate, endDate, discountMin, discountMax, costPriceMin, costPriceMax, markedPriceMin, markedPriceMax, sellingPriceMin, sellingPriceMax);
        }catch(Exception ex){
            System.out.println("ItemFactory - viewCount " + ex);
        }
//        catch (InstantiationException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return null;
    }
    
    
    
    public static boolean delete(Integer id, Integer clerkId, String category, String type) {
        try {
            Class cls = getDAOClass(category, type);
            Object obj = cls.newInstance();
            Method m = cls.getDeclaredMethod("delete", Integer.class, Integer.class);
            return (boolean) m.invoke(obj, id, clerkId);
        } 
        catch(Exception ex){
            System.out.println(ex);
        }
//        catch (InstantiationException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return false;
    }
    
    
    public static Item makeSpecificItem(String category, String type) {
        return makeSpecificItem(category, type, 1).get(0);
    }

    private static List<Item> makeSpecificItem(String category, String type, int count) {
        if (count < 1) {
            throw new IllegalArgumentException("count less than 1");
        }
        System.out.println("ItemFacotry - Category " + category);
        System.out.println("ItemFacotry - Type " + type);
        category = category.replaceAll(" ", "");
        category = category.toLowerCase();
        type = type.replaceAll(" ", "");
        try {
            String className = BeanClassPrefix + "." + category + "." + type;
            Class cls = Class.forName(className);
            List<Item> itms = new ArrayList<Item>();
            for (int i = 0; i < count; i++) {
                itms.add((Item) cls.newInstance());
            }
            return itms;
        } catch (Exception ex) {
            System.out.println(ex.getCause()+" and ex is " + ex);
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new DAOException(ex);
        }
    }

    private static List<Item> itemListFromRequest(Integer clerkId, String category, String type, Map<String, String> values) {
        System.out.println("In item list from request");
        Set<String> params = values.keySet();
        System.out.println("got params");
        boolean hasSize = false;
        int count;
        for (String param : params) {
            if (param.equalsIgnoreCase("size")) {
                hasSize = true;
                break;
            }
        }
        params = null;
        String[] sizes = {};
        if (!hasSize) {
            count = 1;

        } else {
            sizes = values.get("size").split("-");
            count = sizes.length;
        }
        System.out.println("count is "+count);
        DecimalFormat ft = new DecimalFormat("##.##");
        List<Item> itms = makeSpecificItem(category, type, count);
        Integer merchant = (ClerkSessionStore.getClerkSessionStore()).getClerkMerchant(clerkId);
        try {
            Item itm = itms.get(0);
            System.out.println("got first item, setting other fields");
            itm.setBillDescription(values.get("billdescription"));
            itm.setClerkId(clerkId);
            itm.setCostPrice(Integer.parseInt(ft.format(Double.parseDouble(values.get("costprice")) * 100.0) + ""));
            itm.setDescription(values.get("description"));
            itm.setDiscount(Integer.parseInt(ft.format(Double.parseDouble(values.get("discount")) * 100.0) + ""));
            Integer id = null;
            try{
                id = Integer.parseInt(values.get("id"));
            }
            catch(NumberFormatException e){
                
            }
            if(id != null)
                itm.setId(id);
            else
                itm.setId(-1);
            itm.setInStock(Integer.parseInt(values.get("instock")));
            itm.setLastModifiedTime(new Date());
            itm.setMarkedPrice(Integer.parseInt(ft.format(Double.parseDouble(values.get("markedprice")) * 100.0) + ""));
            itm.setMerchant(merchant);
            itm.setModelNumber(values.get("model"));
            itm.setBrand(values.get("brand"));
            itm.setName(values.get("title"));
            itm.setOfflineReserve(Integer.parseInt(values.get("offlinereserve")));
            itm.setReward(false);
            itm.setSellingPrice(0);
            itm.setSkuId(Integer.parseInt(values.get("sku")));
            itm.setSupplierId(Integer.parseInt(values.get("supplierid")));
            itm.setThreshold(Integer.parseInt(values.get("minstock")));
            itm.setVisible(true);
            Class cl = itm.getClass();
            System.out.println("class is "+ cl);
            Field[] flds = cl.getDeclaredFields();
            Field sizeField = null;
            for (Field fld : flds) {
                System.out.println("working on "+ fld.getName());
                fld.setAccessible(true);
                String name = fld.getName();
                String val;
                if (hasSize && name.equals("size")) {
                    sizeField = fld;
                    val = sizes[0];
                    fld.set(itm, val);
                    continue;
                }
                val = values.get(name);
                if (val == null) {
                    throw new DAOException("Required item has a null value");
                }
                System.out.println("After checking param val "+ val);
                String typ = fld.getType().getName();
                System.out.println(fld.getName()+" ka type is "+ typ);
                if (typ.contains("String")) {
                    fld.set(itm, val);
                } else if (typ.contains("Short")) {
                    fld.set(itm, Short.parseShort(val));
                } else if (typ.contains("Long")) {
                    fld.set(itm, Long.parseLong(val));
                } else if (typ.contains("Double")) {
                    fld.set(itm, Double.parseDouble(val));
                } else if (typ.contains("Integer")) {
                    fld.set(itm, Integer.parseInt(val));
                } else if (typ.contains("Boolean")) {
                    fld.set(itm, Boolean.parseBoolean(val));
                } else {
                    throw new DAOException("Unchecked Type. System error");
                }
                System.out.println("after setting field");
            }
            //Copy all other values. Only one instance of that value.            
            System.out.println("Before looping");
            for (int i = 1; i < count; i++) { //except first item
                itms.get(i).copy(itm);
                if (sizeField != null) {
                    sizeField.set(sizeField, sizes[i]);
                }
                itms.get(i).setSkuId(itms.get(i - 1).getSkuId() + 1);
                System.out.println("in loop " + i);
            }
            System.out.println("returning items");
            return itms;
        } catch(Exception ex){
            Logger.getLogger(ItemFactory.class+"").log(Level.SEVERE, ex.getMessage());
            throw new DAOException(ex);
        }
    }

    public static List<AppItem> viewItemBatch(String category, String type, Integer batchNumber, Integer merchant) {
        System.out.println("in viewItemBatch");
        if(category == null || type == null || batchNumber == null || "".equals(category) || "".equals(type))
            throw new IllegalArgumentException("Either category, type or batchNumber is null");
        int from = BATCHSIZE * (batchNumber-1);
        try{
            Class cls = getDAOClass(category, type);
            System.out.println("got dao class");
            Object obj = cls.newInstance();
            System.out.println("created instance");
            Method m = cls.getDeclaredMethod("viewItems", Integer.TYPE, Integer.TYPE, Integer.class);
            System.out.println("method got");
            List<AppItem> itms = (List<AppItem>) m.invoke(obj, from, BATCHSIZE, merchant);
            System.out.println("ItemFactory, viewItems - method invoked - result size, result " + itms.size() + ", " + itms);
            return itms;
        } catch (ClassNotFoundException ex) {
            System.out.println("Item Factory " + ex);
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            System.out.println("Item Factory " + ex);
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            System.out.println("Item Factory " + ex);
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            System.out.println("Item Factory " + ex);
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            System.out.println("Item Factory " + ex);
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            System.out.println("Item Factory " + ex);
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex.getCause());
            System.out.println(ex.getCause());
            System.out.println(".......");
            ex.printStackTrace();
        }
        catch(DAOException ex){
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }
    public static Integer viewItemBatchCount(String category, String type, Integer merchant){
        try {
            Class cls = getDAOClass(category, type);
            Object obj = cls.newInstance();
            Method m = cls.getDeclaredMethod("viewClientItemsCount", Integer.class);
            return (int) m.invoke(obj, merchant);
        } catch (InstantiationException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static AppSimilarItem similarItems(Integer id, String category, String type) throws DAOException{
        
        try {
            Class cls = getDAOClass(category, type);
            Object obj = cls.newInstance();
            Method m = cls.getDeclaredMethod("similarItems", Integer.class);
            return (AppSimilarItem) m.invoke(obj, id);
        } catch (Exception ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new DAOException(ex);
        }
//        catch (IllegalAccessException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }
    
    public static AppMoreDetail getMoreDetails(Integer id, String category, String type) throws DAOException{
        
        try {
            Class cls = getDAOClass(category, type);
            Object obj = cls.newInstance();
            Method m = cls.getDeclaredMethod("getMoreDetails", Integer.class);
            return (AppMoreDetail) m.invoke(obj, id);
        } catch (Exception ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new DAOException(ex);
        }
    }
    
    public static AppItem getCartItem(Integer id, String category, String type) throws DAOException {
        try{
            Class cls = getDAOClass(category, type);
            Object obj  = cls.newInstance();
            Method m = cls.getDeclaredMethod("viewSingleItem", Integer.class);
            return (AppItem) m.invoke(obj, id);
        }
        catch(Exception ex){
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new DAOException(ex);
        }
    }
    
}
