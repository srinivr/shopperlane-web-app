/*
 * Author: Srinivas
 */
package com.pqike.servlet;

import com.google.gson.Gson;
import com.pqike.Utility.ItemFactory;
import com.pqike.bean.AppItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author User
 */
@WebServlet("/AppHost")
public class AppHostServlet extends HttpServlet {

    private static final String CATEGORY = "cat";
    private static String TYPE = "type";
    private static String BATCH = "bat";
    private static String COUNT = "count";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String category = request.getParameter(CATEGORY);
        String type = request.getParameter(TYPE);
        String batchString = request.getParameter(BATCH);
        String isCountString = request.getParameter(COUNT);
        Integer batch = null;
        Integer merchant = null;
        boolean isCount = true;
        try {
            batch = Integer.parseInt(batchString);
        } catch (NumberFormatException e) {

        }
        try {
            isCount = Boolean.parseBoolean(isCountString);
        } catch (Exception e) {

        }
        try{
            merchant = Integer.parseInt(request.getParameter("merchant"));
        }
        catch(NumberFormatException ex){
            
        }
        if (category == null || type == null || batch == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        PrintWriter out = null;
        try {
            out = response.getWriter();
            if (isCount) {
                int count = ItemFactory.viewItemBatchCount(category, type, merchant);
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                out.println("{");
                out.println("\"count\":");
                out.println("\""+count+"\"");
                out.println("}");
            } else {
                List<AppItem> itms = ItemFactory.viewItemBatch(category, type, batch, merchant);
                System.out.println(itms);
                Gson gson = new Gson();
                String items = gson.toJson(itms);
                try {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    out = response.getWriter();
                    out.println(items);
                    System.out.println("Items are " + items);
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(this.getClass().getName() + " " + e);
        }
        finally{
            if(out != null)
                out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
