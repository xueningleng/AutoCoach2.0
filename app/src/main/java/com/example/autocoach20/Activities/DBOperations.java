package com.example.autocoach20.Activities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBOperations {
    Statement stmt = null;
    ResultSet rs = null;
    Connection conn = null;

    public DBOperations(){

    }
    public static void main(String args[]){
        DBOperations db = new DBOperations();
        db.connect();
        db.writeSpeed("test",123,1,60);
        db.fetch("speed","speed");
    }

    public void connect() {
        try {
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
        }
        catch(Exception e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
    }

    public void fetch(String table, String field){
        try {
            if (stmt.execute("SELECT "+field+" FROM "+ table)) {
                rs = stmt.getResultSet();
            }
        }
        catch(SQLException e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
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

    public void writeSpeed(String userName, int userId, int tripId, int speed){
        try {
            int autoIncKeyFromFunc = -1;
            rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");

            while(rs.next()) {
                autoIncKeyFromFunc = rs.getInt(1);
            }
            String query =" insert into speed(id, userName, userId, tripId, speed)"+" values (?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt    (1, autoIncKeyFromFunc);
            preparedStmt.setString (2, userName);
            preparedStmt.setInt    (3, userId);
            preparedStmt.setInt    (4, tripId);
            preparedStmt.setInt    (5, speed);
            preparedStmt.execute();
            conn.close();
        }
        catch (Exception e)
        {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
    }

}
