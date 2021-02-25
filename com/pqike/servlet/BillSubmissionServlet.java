/*
 * Author: Srinivas
*/
package com.pqike.servlet;

import com.google.gson.*;
import com.pqike.exception.DBException;
import com.pqike.model.Bill;
import com.pqike.model.BillClientTransfer;
import com.pqike.model.BillSubmissionResult;
import com.pqike.model.ClerkSessionStore;
import java.io.*;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Seller/SubmitBill")
public class BillSubmissionServlet extends HttpServlet
{
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		//HttpSession session = request.getSession();
		String clerkIdString = request.getParameter("clerk");
		if(clerkIdString == null)
		{
			//response.sendRedirect("LoginError"); Uncomment
			System.out.println("Stuck on clerkId");
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
		else
		{
			PrintWriter out = response.getWriter();
			String sessionId = request.getParameter("session");
                        Integer clerkId = Integer.parseInt(clerkIdString);
			if(ClerkSessionStore.getClerkSessionStore().validateClerkSession(clerkId, sessionId))
			{
				String jsonData = request.getParameter("json");
				System.out.println("JSON FROM JLABELS => " + jsonData);
				Gson gson = new Gson();
				BillClientTransfer billTransfered = gson.fromJson(jsonData, BillClientTransfer.class);
				Bill bill = billTransfered.getBill();
				
				try
				{
					System.out.println("Calling addItems()..");
					if(bill != null)
						System.out.println(new Gson().toJson(bill));
                                        else{
                                            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                                            return;
                                        }
					if(billTransfered.getBillItems() != null)
						System.out.println(new Gson().toJson(billTransfered.getBillItems()));
					if(billTransfered.getBillPayments() != null)
						System.out.println(new Gson().toJson(billTransfered.getBillPayments()));
					if(billTransfered.getTaxes() != null)
						System.out.println(new Gson().toJson(billTransfered.getTaxes()));
					if(billTransfered.getBillTaxes() != null)
						System.out.println(new Gson().toJson(billTransfered.getBillTaxes()));
					Integer billId = bill.addBills(clerkId, billTransfered.getBillItems(), billTransfered.getBillPayments(), billTransfered.getTaxes(), billTransfered.getBillTaxes());
                                        BillSubmissionResult billResult = new BillSubmissionResult(true, billId, "Bill has been added successfully");
					System.out.println("added");
					out.println(new Gson().toJson(billResult));
					
				}
                                catch(DBException e){
                                    BillSubmissionResult billResult = new BillSubmissionResult(false, null, e.getMessage());
                                    out.println(new Gson().toJson(billResult));
                                }
				catch(SQLException e)
				{
					BillSubmissionResult billResult = new BillSubmissionResult(false, null, "Internal Error. Please contact the administrator and notify the issue. Error Code BPD1.");
					out.println(new Gson().toJson(billResult));
                                        System.out.println(e);
				}
				catch(ClassNotFoundException e)
				{
					BillSubmissionResult billResult = new BillSubmissionResult(false, null, "Internal Error. Please contact the administrator and notify the issue. Error Code BPC2.");
					out.println(new Gson().toJson(billResult));
                                        System.out.println(e);
				}
				catch(Exception e)
				{
					BillSubmissionResult billResult = new BillSubmissionResult(false, null, "Internal Error. Please contact the administrator and notify the issue. Error Code BPG3.");
					out.println(new Gson().toJson(billResult));
                                        System.out.println(e);
				}
			}
			else
			{
				BillSubmissionResult billResult = new BillSubmissionResult(false, null, "You have to be logged in to do that.");
                                out.println(new Gson().toJson(billResult));
			}
		}
	}
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		doPost(request, response);
	}
}