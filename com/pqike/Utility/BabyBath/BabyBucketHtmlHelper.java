/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pqike.Utility.HtmlGeneratorUtility.BabyBath;

import com.pqike.DAO.DAOException;
import com.pqike.Utility.ColorUtility;
import com.pqike.Utility.HtmlGeneratorUtility.IHtmlHelper;
import com.pqike.Utility.HtmlUtility;
import com.pqike.bean.Item;
import com.pqike.bean.babybath.BabyBucket;

/**
 *
 * @author User
 */
public class BabyBucketHtmlHelper implements IHtmlHelper {

    private static final String HTMLCONTENT = ""
            + "<div class=\"alert alert-success col-md-10\" role=\"alert\">\n"
            + "    <span class=\"glyphicon glyphicon-exclamation-sign\" aria-hidden=\"true\"></span>\n"
            + "    <span class=\"sr-only\">Note:</span>\n"
            + "    1 litre = 10 deci litre. Example: 1) To enter 13.5 litres, enter 135. 2) To enter 12 litres, enter 120.\n"
            + "</div>\n"
            + "<div class='row'>\n"
            + "    <div class='col-md-5'>\n"
            + "        <div class=\"input-group\">\n"
            + "            <span class='input-group-addon'> Quantity&nbsp;&nbsp;&nbsp;</span>\n"
            + "            <input type='number' name='quantity' class='form-control' placeholder='In deci litre. Max 30000' %s>\n"
            + "        </div>\n"
            + "    </div>\n"
            + "    <div class='col-md-5'>\n"
            + "        <div class=\"input-group\">\n"
            + "            <span class='input-group-addon'> Color&nbsp;&nbsp;&nbsp;</span>\n"
            + "            <select class=\"form-control\" name=\"color\">\n"
            + "                %s"
            + "            </select>\n"
            + "        </div>\n"
            + "    </div>\n"
            + "</div>";

    @Override
    public String getHtml(Item itm) {
        return getHtml(false, itm);
    }

    @Override
    public String getHtml() {
        return getHtml(true, null);
    }

    private String getHtml(boolean isAdd, Item itm) {
        BabyBucket bbt = (BabyBucket) itm;
        if (!isAdd) {
            if (itm == null) {
                throw new DAOException("Null Item Passed");
            }
        }
        if (isAdd) {
            return String.format(HTMLCONTENT, "", HtmlUtility.selectOptionsMaker(ColorUtility.getColors()));
        } else {
            return String.format(HTMLCONTENT, "value='" + bbt.getQuantity() + "'", HtmlUtility.selectOptionsMaker(ColorUtility.getColors(), bbt.getColor()));
        }
    }
}
