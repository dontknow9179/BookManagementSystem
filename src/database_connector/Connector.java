package database_connector;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class Connector {
    public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://localhost:3306/BOOK_STORE_MANAGEMENT?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    public static final String USER = "root";
    public static final String PASS = "sherlock221B";
    public static Connection conn;//连接数据库
    public static Statement stmt;//创建执行语句

    public static void main(String args[]){
        Login login_ui = new Login();
        login_ui.createIt();
    }
}
