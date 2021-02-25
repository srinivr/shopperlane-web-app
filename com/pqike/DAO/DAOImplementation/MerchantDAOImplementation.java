/*
 * Author: Srinivas
 */

package com.pqike.DAO.DAOImplementation;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.pqike.DAO.DAOFactory;
import com.pqike.DAO.DAOUtil;
import com.pqike.DAO.Interfaces.MerchantDAO;
import com.pqike.Utility.PropertiesLoader;
import com.pqike.bean.MerchantView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public class MerchantDAOImplementation implements MerchantDAO{
    private static final String BILLPULP = "billpulp";
    private static final String DB = "billpulp";
    private static final String COLLECTION = "merchants_location";
    private static final PropertiesLoader pldr = PropertiesLoader.getInstance();
    private static final MongoClient mc = new MongoClient(pldr.getProperty("in.shopperlane.mongodb.url", true));
    private static final String MATCHQUERY = "select id, name from merchants where id in (select merchant from `%s`.merchant_departments where merchant in (%s) and site_department = '%s')";
    
    private static MerchantDAOImplementation instance = null;
    
    private MerchantDAOImplementation(){
            
    }
    
    public synchronized static MerchantDAO getInstance(){
        if(instance == null)
            instance = new MerchantDAOImplementation();
        return instance; 
    }
    
    @Override
    public List<Integer> locatedInRadius(Double latitude, Double longitude, double radius) {
        System.out.println("In Located in Radius");
        if(latitude == null || longitude == null)
            throw new IllegalArgumentException("Latitude and Longitude cannot be null");
        if(radius < 0)
            throw new IllegalArgumentException("Radius has to be positive");
        DB db = mc.getDB(BILLPULP);
        DBCollection dbc = db.getCollection(COLLECTION);
        BasicDBList geoCord = new BasicDBList();
        geoCord.add(longitude);
        geoCord.add(latitude);
        
        BasicDBObject geo = new BasicDBObject("$geometry", new BasicDBObject(
        ).append("type", "Point").append("coordinates", geoCord));
        
        BasicDBObject query = new BasicDBObject("loc", new BasicDBObject("$near",
                new BasicDBObject(geo).append("$maxDistance",
                        radius)
        ));
        
        DBCursor mongoCur = dbc.find(query);
        DBObject dbo;
        List<Integer> result = new ArrayList<Integer>();
        Double f;
        while(mongoCur.hasNext()){
            dbo = mongoCur.next();
            f = (Double) dbo.get("id");
            result.add(f.intValue());
        }
        return result;
    }
    
    @Override
    public List<MerchantView> merchantsInDepartment(List<Integer> merchants, String dept){
        if(merchants == null)
            throw new IllegalArgumentException("Merchants is null");
        if(merchants.size() < 1) return null;
        if(dept == null)
            throw new IllegalArgumentException("Department is null");
        Connection con = null;
        PreparedStatement pt = null;
        ResultSet rs = null;
        try{
            con = DAOFactory.getInstance(DB).getConnection();
            StringBuilder args = new StringBuilder();
            args.append(merchants.get(0));
            for(int i=1; i<merchants.size(); i++){
                args.append(",");
                args.append(merchants.get(i));
            }
            System.out.println("Query is " + String.format(MATCHQUERY, DB, args.toString(), dept));
            pt = DAOUtil.prepareStatement(con, false, String.format(MATCHQUERY, DB, args.toString(), dept), new Object[]{});
            rs = pt.executeQuery();
            List<MerchantView> result = new ArrayList<MerchantView>();
            MerchantView mv;
            while(rs.next()){
                mv = new MerchantView();
                mv.setId(rs.getInt(1));
                mv.setTitle(rs.getString(2));
                result.add(mv);
            }
            return result;
        }
        catch(Exception ex){
                System.out.println(this.getClass().getName() +": " + ex);
        }
        finally{
            DAOUtil.close(con, pt, rs);
        }
        return null;
    }
}
