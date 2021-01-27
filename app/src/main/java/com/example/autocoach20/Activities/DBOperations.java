package com.example.autocoach20.Activities;


import com.example.autocoach20.Activities.Model.Trip;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


public class DBOperations {
    Statement stmt = null;
    ResultSet rs = null;
    Connection conn = null;

    public DBOperations(){

    }
    /*
    public static void main(String args[])throws Exception{
        DBOperations db = new DBOperations();
        // db.dropTable("trips");
        db.dropTable("users");
        //db.createTripTable();
        db.createUserTable();
        //db.createRecordTable();
        //db.getAutoKey("tripRecord");
        // db.writeSpeed("tripRecord" ,"user1", 124,1,62);
        //db.fetch("tripRecord","speed");
    }*/
    public void createRecordTable()throws SQLException{
        try {
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            //System.out.println("Connected to database ");
            Statement stmt = conn.createStatement();
            String query = "CREATE TABLE records(" +
                    "recordId INT NOT NULL, " +
                    "tripId INT NOT NULL, " +
                    "time timestamp NOT NULL, "+
                    "speed INT, "+
                    "PRIMARY KEY (recordId));";
            stmt.execute(query);
            System.out.println("Table Created......");
        }
        catch(Exception e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
        finally{
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }
    }
    public void createUserTable(){
        try {
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            //System.out.println("Connected to database ");
            Statement stmt = conn.createStatement();
            String query = "CREATE TABLE users(" +
                    "userId VARCHAR(255) NOT NULL, " +
                    "userName VARCHAR(255), "+
                    "userEmail VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY (userId));";
            stmt.execute(query);
            System.out.println("Table Users Created......");
        }
        catch(Exception e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
        finally{
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }

    }
    public void insertUser(String uid, String uname, String uemail)throws Exception {
        int autoIncKeyFromFunc = getAutoKey("users");
        try{
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            Statement stmt = conn.createStatement();
            String query =" insert into users (userId, userName, userEmail)"+" values (?, ?, ?);";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString    (1, uid);
            preparedStmt.setString (2, uname);
            preparedStmt.setString (3, uemail);
            preparedStmt.execute();
            conn.close();
        }
        catch(Exception e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
        finally{

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore

                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }

    }

    public void createTripTable()throws SQLException{
        try {
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            //System.out.println("Connected to database ");
            Statement stmt = conn.createStatement();
            String query = "CREATE TABLE trips(" +
                    "tripId INT NOT NULL, " +
                    "userId VARCHAR(255) NOT NULL, " +
                    "startTime BIGINT(8) UNSIGNED, " +
                    "endTime BIGINT(8) UNSIGNED, "+
                    "speed INT, "+
                    "score INT, "+
                    "PRIMARY KEY (tripId));";
            stmt.execute(query);
            System.out.println("Table Trips Created......");
        }
        catch(Exception e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
        finally{
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }
    }
    public int insertTrip(String uid, long tripStartTime, long tripEndTime)throws Exception {
        int autoIncKeyFromFunc = getAutoKey("users");
        try{
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            Statement stmt = conn.createStatement();
            String query =" insert into trips (tripId, userId, startTime, endTime, score)"+" values (?, ?, ?, ?, ?);";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt   (1, autoIncKeyFromFunc);
            preparedStmt.setString (2, uid);
            preparedStmt.setLong (3, tripStartTime);
            preparedStmt.setLong (4, tripEndTime);
            preparedStmt.setInt (5, 60);//basic score
            preparedStmt.execute();
            conn.close();
        }
        catch(Exception e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
        finally{

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore

                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
            return autoIncKeyFromFunc;
        }
    }
    public Trip fetchTripData(int tripId) throws Exception{
        try {
            // int tripId;
            String uid;
            long tripStartTime;
            long tripEndTime;
            int score;

            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            //System.out.println("Connected to database ");
            Statement stmt = conn.createStatement();
            if (stmt.execute("SELECT * FROM trips;")) {
                rs = stmt.getResultSet();
            }
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {//for all rows
                if (tripId == rs.getInt(1)) {
                    uid = rs.getString(2);
                    tripStartTime = rs.getLong(3);
                    tripEndTime = rs.getLong(4);
                    score = rs.getInt(5);
                    Trip t = new Trip(tripId, uid, tripStartTime, tripEndTime, score);
                    return t;
                }else{
                    System.out.println("No trip found");
                }
            }
        }
        catch(Exception e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
        finally{
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore

                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }
        return null;
    }

    public void dropTable(String table) {
        try {
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            //System.out.println("Connected to database ");
            Statement stmt = conn.createStatement();
            String q = "DROP TABLE " + table;
            stmt.executeUpdate(q);
            System.out.println("Table deleted...");
        } catch (Exception e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }
        }
    }
    public void fetch(String table, String field) throws Exception{
        try {
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            //System.out.println("Connected to database ");
            Statement stmt = conn.createStatement();
            if (stmt.execute("SELECT "+field+" FROM "+ table+";")) {
                rs = stmt.getResultSet();
            }
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }
        }
        catch(Exception e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
        finally{
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore

                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }
    }
    public int getAutoKey(String table)throws SQLException{
        int autoIncKeyFromFunc = 0;
        try {
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            //System.out.println("Connected to database ");
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * from " + table +";");

            while (rs.next()) {
                autoIncKeyFromFunc = rs.getInt(1);
            }
            //System.out.println("next key " + autoIncKeyFromFunc);
        }
        catch(Exception e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
        finally{
            if (autoIncKeyFromFunc<=0){
                autoIncKeyFromFunc = 1;
            }
            else{
                autoIncKeyFromFunc++;
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore
                stmt = null;
            }
        }

        //System.out.println("key is "+autoIncKeyFromFunc);
        return autoIncKeyFromFunc;
    }
    public void writeSpeed(String table, String userName, int userId, int tripId, int speed)throws Exception{

        int autoIncKeyFromFunc = getAutoKey(table);
        try{
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            System.out.println("Connected to database ");
            Statement stmt = conn.createStatement();
            String query =" insert into "+table+"(id, userName, userId, tripId, speed)"+" values (?, ?, ?, ?, ?);";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt    (1, autoIncKeyFromFunc);
            preparedStmt.setString (2, userName);
            preparedStmt.setInt    (3, userId);
            preparedStmt.setInt    (4, tripId);
            preparedStmt.setInt    (5, speed);
            preparedStmt.execute();
            conn.close();
        }
        catch(Exception e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
        finally{

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore

                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }

    }

}
