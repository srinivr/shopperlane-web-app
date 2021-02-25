/*
 * Author: Srinivas
 * Load JSP/HTML based dispatcher classes based on URL Parameters using 
   'reflection'
 * and open the template in the editor.
 */

package com.pqike.Utility;

import com.pqike.DAO.DAOException;
import com.pqike.bean.Item;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class JSPHTMLDispatcher {
    private static final String PREFIX = "com.pqike.Utility.HtmlGeneratorUtility";
    private static final String SUFFIX = "HtmlHelper";
    
    public static String getHtml(String category, String type, Item itm){
        return getHtml(category, type, itm, true);
    }
    
    public static String getHtml(String category, String type){
        System.out.println("Called");
        System.out.println("Response is " + getHtml(category, type, null, false));
        return getHtml(category, type, null, false);
    }
    
    private static String getHtml(String category, String type, Item itm, boolean nullCheck){
        System.out.println("Jspdisptach - check 1");
        if(nullCheck)
        {
            if(itm == null)
                throw new DAOException("Null Item");
        }
        System.out.println("Jspdisptach - check 2");
        try {
            System.out.println("Loading " + PREFIX + "." + category.replaceAll(" ", "") + "." + type.replaceAll(" ", "") + SUFFIX);
            Class helper = Class.forName(PREFIX + "." + category.replaceAll(" ", "") + "." + type.replaceAll(" ", "") + SUFFIX);
            Object obj = helper.newInstance();
            Method m;
            System.out.println("Jspdisptach - check 3");
            if(itm != null) {
                System.out.println("Jspdisptach - check 4");
                m = helper.getDeclaredMethod("getHtml", Item.class);
                return (String) m.invoke(obj,itm);
            }
            else{
                System.out.println("Before invoking method");
                m = helper.getDeclaredMethod("getHtml");
                System.out.println("After invoking method");
                return (String) m.invoke(obj);
            }
            
        }
        catch (ClassNotFoundException ex) {
            System.out.print("Jspdisptach " + ex);
            Logger.getLogger(JSPHTMLDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            System.out.print("Jspdisptach " + ex);
            Logger.getLogger(JSPHTMLDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            System.out.print("Jspdisptach " + ex);
            Logger.getLogger(JSPHTMLDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            System.out.print("Jspdisptach " + ex);
            Logger.getLogger(JSPHTMLDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            System.out.print("Jspdisptach " + ex);
            Logger.getLogger(JSPHTMLDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            System.out.print("Jspdisptach " + ex);
            Logger.getLogger(JSPHTMLDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            System.out.print("Jspdisptach " + ex + " " + ex.getCause() + " " + ex.getMessage());
            Logger.getLogger(JSPHTMLDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
