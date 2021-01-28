package com.example.autocoach20.Activities;


import com.example.autocoach20.Activities.Model.Trip;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;


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
    //General Operations
    public boolean checkTableExist(String table) throws ClassNotFoundException, SQLException {
        try {
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet res = meta.getTables(null, null, table,
                    new String[]{"TABLE"});
            while (res.next()) {
                //System.out.println(res.getString("TABLE_NAME"));
                return true;
            }
        }catch(SQLException e){
        }
        return false;
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
    //Record table operations
    public void createRecordTable(){
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
    public void insertRecord(int tripId, int speed) {
        try {
            int nextKey = getAutoKey("trips");
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            Statement stmt = conn.createStatement();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println(timestamp);
            String query = " insert into records (recordId, tripId, time, speed)" + " values (?, ?, ?, ?);";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, nextKey);
            preparedStmt.setInt(2, tripId);
            preparedStmt.setTimestamp(3, timestamp);
            preparedStmt.setInt(4, speed);
            preparedStmt.execute();
            conn.close();
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
    //User table operations
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
    public void updateUser(String uid, int gender, int age)throws Exception {
        try{
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            System.out.println("UserID: " +uid);

            String sql = "update users set gender = ? where userId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,gender);
            stmt.setString(2, uid);
            stmt.executeUpdate();
            String sql2 = "update users set age = ? where userId = ?";
            PreparedStatement stmt2 = conn.prepareStatement(sql2);
            stmt2.setInt(1,age);
            stmt2.setString(2, uid);
            stmt2.executeUpdate();
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
    //Trip table operations
    public void createTripTable(){
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
    public void updateTrip(int tripId, long tripEndTime){
        try{
            if (tripId<0) tripId = getAutoKey("users");
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            String sql = "update trips set endTime = ? where tripId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1,tripEndTime);
            stmt.setInt(2,tripId);
            stmt.executeUpdate();
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
    public Trip fetchCurrentTripData(){
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
            int tripId = getAutoKey("trips")-1;
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

    public void writeSpeed(String table, String userName, int userId, int tripId, int speed)throws Exception {

        int autoIncKeyFromFunc = getAutoKey(table);
        try {
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            System.out.println("Connected to database ");
            Statement stmt = conn.createStatement();
            String query = " insert into " + table + "(id, userName, userId, tripId, speed)" + " values (?, ?, ?, ?, ?);";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, autoIncKeyFromFunc);
            preparedStmt.setString(2, userName);
            preparedStmt.setInt(3, userId);
            preparedStmt.setInt(4, tripId);
            preparedStmt.setInt(5, speed);
            preparedStmt.execute();
            conn.close();
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
}