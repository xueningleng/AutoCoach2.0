package com.example.autocoach20.Activities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import java.sql.*;


public class DBOperations {
    Statement stmt = null;
    ResultSet rs = null;
    Connection conn = null;

    public DBOperations(){

    }
   /* public static void main(String args[])throws Exception{
        DBOperations db = new DBOperations();
        db.createTable("tripRecord");
        //db.getAutoKey("tripRecord");
        db.writeSpeed("tripRecord" ,"user1", 124,1,62);
        db.fetch("tripRecord","speed");
    }
*/


    public void createTable(String tableName)throws SQLException{
        try {
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/autocoach";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "password");
            //System.out.println("Connected to database ");
            Statement stmt = conn.createStatement();
            String query = "CREATE TABLE "+tableName+"(" +
                    "ID INT NOT NULL, " +
                    "userName VARCHAR (20) NOT NULL, " +
                    "userId INT NOT NULL, " +
                    "tripId INT NOT NULL, " +
                    "speed DECIMAL, " +
                    "PRIMARY KEY (ID));";
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
