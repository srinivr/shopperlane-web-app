/*
 * Author: Srinivas
 */

package com.pqike.DAO.Interfaces;

/**
 *
 * @author User
 */
public class ItemDAOQueryConstant {
    public static final String INSERT = "Insert into billpulp.`%s`(name, brand,description, selling_price, discount, image_url_tin, merchant, in_stock, sku_id, last_operation_time, clerk_id, supplier_id, cost_price, marked_price, threshold, model_number, visible, reward, offline_reserve, bill_description,";
    public static final String UPDATE = "Update billpulp.`%s` set name = ?, brand = ?, description = ?, selling_price = ?, discount=?, image_url_tin = ?, merchant = ?, in_stock=?, sku_id = ?, last_operation_time = now(), clerk_id=?, supplier_id = ?, cost_price = ?, marked_price = ?, threshold = ?, model_number = ?, visible = ?, reward = ?, offline_reserve =?, bill_description = ?,";
    public static final String FETCH = "select * from billpulp.`%s` where id=? and merchant=?";
}
