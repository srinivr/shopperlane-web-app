/*
 * Author: Srinivas
 */

package com.pqike.Utility.HtmlGeneratorUtility.BabyBath;

import com.pqike.Utility.HtmlGeneratorUtility.IHtmlHelper;
import com.pqike.bean.Item;

/**
 *
 * @author User
 */
public final class BabyBathnetHtmlHelper implements IHtmlHelper{
    
    @Override
    public String getHtml(Item itm) {
        return getHtml(false, itm);
    }

    @Override
    public String getHtml() {
        return getHtml(true, null);
    }
    
    private String getHtml(boolean isAdd, Item itm) {
        return "";
    }
}
