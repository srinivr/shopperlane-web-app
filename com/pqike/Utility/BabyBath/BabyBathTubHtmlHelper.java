/*
 * Author: Srinivas
 */

package com.pqike.Utility.HtmlGeneratorUtility.BabyBath;

import com.pqike.DAO.DAOException;
import com.pqike.Utility.ColorUtility;
import com.pqike.Utility.HtmlGeneratorUtility.IHtmlHelper;
import com.pqike.Utility.HtmlUtility;
import com.pqike.bean.Item;
import com.pqike.bean.babybath.BabyBathTub;

/**
 *
 * @author User
 */
public final class BabyBathTubHtmlHelper implements IHtmlHelper {

    private final static String HTMLCONTENT = ""
            + "<div class='row'>"
            + "    <div class='col-md-10'>"
            + "        </br>"
            + "    </div>"
            + "</div>"
            + "<div class='row'>"
            + "    <div class='col-md-4'>"
            + "        <div class='input-group .col-md-3'>"
            + "            <span class='input-group-addon'> Length&nbsp;</span>"
            + "            <input type='number' name='length' class='form-control' placeholder='In mm. Max 30000' %s >"
            + "        </div>"
            + "    </div>"
            + "    <div class='col-md-3'>"
            + "        <div class='input-group .col-md-3'>"
            + "            <span class='input-group-addon'> Width&nbsp;</span>"
            + "            <input type='number' name='width' class='form-control' placeholder='In mm. Max 30000' %s  >"
            + "        </div>"
            + "    </div>"
            + "    <div class='col-md-3'>"
            + "        <div class='input-group .col-md-3'>"
            + "            <span class='input-group-addon'> Height&nbsp;&nbsp;&nbsp;</span>"
            + "            <input type='number' name='height' class='form-control' placeholder='In mm. Max 30000' %s >"
            + "        </div>"
            + "    </div>						"
            + "</div>"
            + "<div class='row'>"
            + "    <div class='col-md-10'>"
            + "        </br>"
            + "    </div>"
            + "</div>"
            + "<div class='row'>"
            + "    <div class='col-md-4'>"
            + "         <div class='input-group'>"
            + "        <span class='input-group-addon'>Color</span>"
            + "        <select class='form-control' name='color'>"
            + "%s"
            + "        </select>"
            + "     </div>"
            + "    </div>"
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
        BabyBathTub bbt = (BabyBathTub) itm;
        if (!isAdd) {
            if (itm == null) {
                throw new DAOException("Null Item Passed");
            }
        }
        if(isAdd)
            return String.format(HTMLCONTENT, "", "" , "", HtmlUtility.selectOptionsMaker(ColorUtility.getColors()));
        else
            return String.format(HTMLCONTENT, "value='" + bbt.getLength() + "'", "value='" + bbt.getWidth() + "'", "value='" + bbt.getHeight() + "'", HtmlUtility.selectOptionsMaker(ColorUtility.getColors(), bbt.getColor()));
    }

}
