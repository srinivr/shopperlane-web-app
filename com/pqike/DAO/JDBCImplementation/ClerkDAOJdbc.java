/*
 * Author: Srinivas
 */
package com.pqike.DAO.JDBCImplementation;

import com.pqike.DAO.DAOFactory;
import com.pqike.DAO.DAOUtil;
import com.pqike.bean.Clerk;
import com.pqike.bean.ClerkSession;
import com.pqike.exception.IllegalDataStateException;
import com.pqike.exception.InvalidPasswordException;
import com.pqike.model.ClerkSessionStore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */

//WHY NO INSTANCE METHOD ? EVERYTHING is STAtIC :O :O
public class ClerkDAOJdbc {

    private static final String DB = "billpulp";
    private static final String INSERT = "Insert into billpulp.clerks(merchant_id,password, email, role, first_name, last_name) values (?,?,?,?,?,?)";
    private static final String FETCHPASSWORDWITHMERCHANT = "select password from billpulp.clerks where id = ? and merchant_id = ?";
    private static final String LOGIN = "Select password, role, merchant_id from billpulp.clerks where id=?";
    private static final String UPDATE = "update billpulp.clerks set password = ?, email = ?, role = ?, first_name = ?, last_name = ? where merchant_id = ? and id = ?";
    private static final String VIEWCOUNT = "Select count(id) as count from clerks where merchant_id = ?";
    private static final String PAGINATEDVIEW = "select id, email, role, first_name, last_name, merchant_id from billpulp.clerks where merchant_id =? limit ?, ?";
    private static final String FETCH = "select email, role, first_name, last_name, merchant_id from billpulp.clerks where id=? and merchant_id=?";
    private static final String DELETE = "delete from billpulp.clerks where id=? and merchant_id=?";
    private static final String SELECTROLE = "Select name from billpulp.roles where id = ?";
    /*Generate password hash*/

    private static String hashPassword(String password) {
        return password;
    }

    /*Login*/
    public static boolean login(Integer clerkId, String password, String sessionId) throws SQLException, ClassNotFoundException {
        DAOFactory DF = DAOFactory.getInstance(DB);
        boolean returnFlag = false;
        password = hashPassword(password);
        Connection con = null;
        PreparedStatement passwordPt = null;
        PreparedStatement rolePt = null;
        ResultSet passwordRs = null;
        ResultSet roleRs = null;
        try {
            con = DF.getConnection();
            Object[] args = {
                clerkId
            };
            passwordPt = DAOUtil.prepareStatement(con, false, LOGIN, args);
            passwordRs = passwordPt.executeQuery();
            if (passwordRs.next()) {
                if (password.equals(passwordRs.getString("password"))) {
                    args = new Object[]{
                        passwordRs.getObject("role")
                    };
                    rolePt = DAOUtil.prepareStatement(con, false, SELECTROLE, args);
                    roleRs = rolePt.executeQuery();
                    if (roleRs.next()) {
                        String role = roleRs.getString("name");
                        Integer merchant = passwordRs.getInt("merchant_id");
                        System.out.println("in clerk login()");
                        ClerkSession clrkSess = new ClerkSession();
                        clrkSess.setClerkId(clerkId);
                        clrkSess.setMerchant(merchant);
                        clrkSess.setRole(role);
                        clrkSess.setMerchant(merchant);
                        clrkSess.setSessionId(sessionId);
                        //ClerkSession.commitDb();
                        System.out.println("in clerk login() - clerkSession commited");
                        ClerkSessionStore.getClerkSessionStore().addClerkSession(clrkSess);
                        returnFlag = true;

                    } else {
                        returnFlag = false; //Change to throw clerkRoleUndefinedException
                        //throw new clerkRoleUndefinedException;
                    }
                } else {
                    returnFlag = false;
                }
            } else {
                returnFlag = false;
            }
            return returnFlag;
        } finally {
            DAOUtil.close(con, passwordPt, rolePt, passwordRs, roleRs);
        }
    }

    public static boolean logout(int clerkId, String sessionId) throws SQLException, ClassNotFoundException {
        //delete from redis
        return true;
        /*if (clerkSession.removeFromDb(clerkId, sessionId)) {
         ClerkSessionStore.getClerkSessionStore().removeClerkSession(clerkId);
         return true;
         } else {
         return false;
         }*/
    }

    public void addClerk(Clerk clrk) throws SQLException, InvalidPasswordException, ClassNotFoundException //throw WEAKPASSWORDEXception if password.length<6
    {
        DAOFactory DF = DAOFactory.getInstance(DB);
        Connection con = null;
        PreparedStatement pt = null;
        try {
            con = DF.getConnection();
            Object[] args = {
                clrk.getMerchantId(),
                clrk.getPassword(),
                clrk.getEmail(),
                clrk.getRole(),
                clrk.getFirstName(),
                clrk.getLastName()
            };
            pt = DAOUtil.prepareStatement(con, false, INSERT, args);
            pt.executeUpdate();
        } finally {
            DAOUtil.close(con, pt);
        }

    }
    /*Edit clerk*/

    public void editClerk(Clerk clrk) throws SQLException, InvalidPasswordException, IllegalDataStateException, ClassNotFoundException //throw WEAKPASSWORDEXception if password.length<6
    {
        DAOFactory DF = DAOFactory.getInstance(DB);
        Connection con = null;
        PreparedStatement passwordPt = null;
        PreparedStatement updatePt = null;
        ResultSet rs = null;
        try {
            con = DF.getConnection();
            Object[] args = {
                clrk.getClerkId(),
                clrk.getMerchantId()
            };
            passwordPt = DAOUtil.prepareStatement(con, false, FETCHPASSWORDWITHMERCHANT, args);
            rs = passwordPt.executeQuery();
            if (rs.next()) {

                /*if (!validatePassword(this.password)) {
                 throw new InvalidPasswordException();
                 }*/
                String _oldPassword = (clrk.getPassword() == null || !validatePassword(clrk.getPassword())) ? rs.getString("password") : clrk.getPassword();

                args = new Object[]{
                    _oldPassword,
                    clrk.getEmail(),
                    clrk.getRole(),
                    clrk.getFirstName(),
                    clrk.getLastName(),
                    clrk.getMerchantId(),
                    clrk.getClerkId()
                };
                updatePt = DAOUtil.prepareStatement(con, false, UPDATE, args);
                updatePt.executeUpdate();

            } else {
                throw new IllegalDataStateException("Clerks. Clerks without password");
            }
        } finally {
            DAOUtil.close(con, passwordPt, updatePt, rs);
        }
    }

    public static Integer viewClerksCount(Integer clerkId) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement pt = null;
        ResultSet rs = null;
        Integer merchant = ClerkSessionStore.getClerkSessionStore().getClerkMerchant(clerkId);
        DAOFactory DF = DAOFactory.getInstance(DB);
        try {
            con = DF.getConnection();
            Object[] args = {
                merchant
            };
            pt = DAOUtil.prepareStatement(con, false, VIEWCOUNT, args);
            rs = pt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            } else {
                return 0;
            }
        } finally {
            DAOUtil.close(con, pt, rs);
        }

    }

    public static List<Clerk> viewClerks(Integer clerkId, Integer numResults, Integer pageNumber) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement pt = null;
        ResultSet rs = null;
        try {
            List<Clerk> _clerks = new ArrayList<Clerk>();
            Integer _begin = numResults * pageNumber;
            String role = ClerkSessionStore.getClerkSessionStore().getClerkRole(clerkId);
            Integer merchant = ClerkSessionStore.getClerkSessionStore().getClerkMerchant(clerkId);
            con = DAOFactory.getInstance("billpulp").getConnection();
            Object[] args = {
                merchant,
                _begin,
                numResults
            };
            pt = DAOUtil.prepareStatement(con, false, PAGINATEDVIEW, args);
            rs = pt.executeQuery();
            while (rs.next()) {
                _clerks.add(map(rs));
            }
            return _clerks;
        } finally {
            DAOUtil.close(con, pt, rs);
        }

    }

    public static Clerk fetchClerk(Integer id, Integer clerkId) throws SQLException, ClassNotFoundException {
        String role = ClerkSessionStore.getClerkSessionStore().getClerkRole(clerkId);
        Integer merchant = ClerkSessionStore.getClerkSessionStore().getClerkMerchant(clerkId);
        DAOFactory DF = DAOFactory.getInstance(DB);
        Connection con = null;
        PreparedStatement pt = null;
        ResultSet rs = null;
        try {
            con = DF.getConnection();
            Object[] args = {
                id,
                merchant
            };
            pt = DAOUtil.prepareStatement(con, false, FETCH, args);
            rs = pt.executeQuery();
            if (rs.next()) {
                return map(rs);
            } else {
                return null;
            }
        } finally {
            DAOUtil.close(con, rs, pt);
        }

    }

    public static void deleteClerk(Integer id, Integer clerkId) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement pt = null;
        DAOFactory DF = DAOFactory.getInstance(DB);
        String role = ClerkSessionStore.getClerkSessionStore().getClerkRole(clerkId);
        Integer merchant = ClerkSessionStore.getClerkSessionStore().getClerkMerchant(clerkId);
        try {
            con = DF.getConnection();
            Object[] args = {
                id,
                merchant
            };
            pt = DAOUtil.prepareStatement(con, false, DELETE, args);
            pt.executeUpdate();
        } finally {
            DAOUtil.close(con, pt);
        }
    }

    private boolean validatePassword(String password) {
        if (password.length() > 6) {
            return true;
        } else {
            return false;
        }
    }

    private static Clerk map(ResultSet rs) throws SQLException {
        Clerk clrk = new Clerk();
        clrk.setClerkId(rs.getInt("id"));
        clrk.setEmail(rs.getString("email"));
        clrk.setFirstName(rs.getString("first_name"));
        clrk.setLastName(rs.getString("last_name"));
        clrk.setMerchantId(rs.getInt("merchant_id"));
        clrk.setRole(rs.getInt("role") + "");
        return clrk;
    }
}
