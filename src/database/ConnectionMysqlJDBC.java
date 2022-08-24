package database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 加载驱动运算
 * @author 
 */
public class ConnectionMysqlJDBC {

    private String address;
    private String userName;
    private String password;
    private String dataBaseName;
    private String prot;
    private static final String DRIVER = "com.mysql.jdbc.Driver";

    public ConnectionMysqlJDBC(String address, String userName, String password, String dataBaseName, String prot) {
        this.address = address;
        this.userName = userName;
        this.password = password;
        this.dataBaseName = dataBaseName;
        this.prot = prot;
    }

    public Connection getConn() {
        try {
            Class.forName(DRIVER);// 加载驱动
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionMysqlJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        String URl = "jdbc:mysql://" + address + ":" + prot + "/" + dataBaseName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URl, userName, password);
        } catch (SQLException ex) {
            return  null;
        }
        return conn;
    }

    public void closeAllConnection(Connection con, PreparedStatement ps, ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (ps != null) {
            ps.close();
        }
        if (con != null) {
            con.close();
        }
    }
}
