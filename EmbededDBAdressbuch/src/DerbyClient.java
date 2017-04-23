import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;


class DerbyClient {
  private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
  private String protocol = "jdbc:derby:";
  private Properties properties = new Properties();
  private String databaseName = "db\\datenbank";
  private String user = "peter";
  private String password = "passwort";
  public Connection connection = null;
  private Statement statement = null;
  public ResultSet resultSet = null;

  public DerbyClient() {
    properties.put("user", user);
    properties.put("password", password);
  }

  /**
    * Mit der Datenbank verbinden
    *
    */
  public void connect() {
    try {
      Class.forName(driver).newInstance();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace(System.err);
    } catch (InstantiationException ie) {
      ie.printStackTrace(System.err);
    } catch (IllegalAccessException iae) {
      iae.printStackTrace(System.err);
    }

    try {
      connection = DriverManager.getConnection(protocol + databaseName,
                                               properties);
      connection.setAutoCommit(false);
    } catch (SQLException sqle) {
      sqle.printStackTrace(System.err);
      System.out.println(sqle.getMessage());
    }
  }

  /**
    * Verbindung überprüfen
    *
    */
  public boolean connected() {
    try {
      return connection.isValid(500);
    } catch (SQLException sqle) {
      return false;
    }
  }

  /**
    * Mit einer existierenden oder einer neuen Datenbank verbinden
    */
  public void createConnect() {
    try {
      Class.forName(driver).newInstance();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace(System.err);
    } catch (InstantiationException ie) {
      ie.printStackTrace(System.err);
    } catch (IllegalAccessException iae) {
      iae.printStackTrace(System.err);
    }

    // mit ij sql.sql aufrufen!!!
    try {
      connection = DriverManager.getConnection(protocol + databaseName +
                                               ";create=true", properties);
      connection.setAutoCommit(false);
    } catch (SQLException sqle) {
      sqle.printStackTrace(System.err);
      System.out.println(sqle.getMessage());
    }
  }

  /**
    * Verbindung trennen
    */
  public void disconnect() {
    try {
      if (resultSet != null) {
        resultSet.close();
        resultSet = null;
      }

      if (statement != null) {
        statement.close();
        statement = null;
      }

      if (connection != null) {
        connection.close();
        connection = null;
      }
    } catch (SQLException sqle) {
      sqle.printStackTrace(System.err);
    }
  }

  /**
    * Einen neuen Datensatz einfügen
    */
  public void insert(Vector data) {
    PreparedStatement psInsert;

    try {
      psInsert = connection.prepareStatement("insert into kontakte values (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?," +
                                             "?, ?, ?, ?, ?, ?, ?, ?)");
      psInsert.setString(1, (String) data.elementAt(0));
      psInsert.setString(2, (String) data.elementAt(1));
      psInsert.setString(3, (String) data.elementAt(2));
      psInsert.setString(4, (String) data.elementAt(3));
      psInsert.setString(5, (String) data.elementAt(4));
      psInsert.setString(6, (String) data.elementAt(5));
      psInsert.setString(7, (String) data.elementAt(6));
      psInsert.setString(8, (String) data.elementAt(7));
      psInsert.setString(9, (String) data.elementAt(8));
      psInsert.setString(10, (String) data.elementAt(9));
      psInsert.setString(11, (String) data.elementAt(10));
      psInsert.setBoolean(12, (Boolean) data.elementAt(11));
      psInsert.setBoolean(13, (Boolean) data.elementAt(12));
      psInsert.setBoolean(14, (Boolean) data.elementAt(13));
      psInsert.setString(15, (String) data.elementAt(14));
      psInsert.setString(16, (String) data.elementAt(15));
      psInsert.executeUpdate();
      connection.commit();
      select();
      resultSet.last();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      System.out.println(sqle.getMessage());
    }
  }

  /**
    * Datenbankabfrage der Daten
    */
  public void select() {
    try {
      PreparedStatement prepairedStatement = (PreparedStatement) connection.prepareStatement("SELECT * FROM kontakte",
                                                                                             ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                                                             ResultSet.CONCUR_UPDATABLE);
      resultSet = prepairedStatement.executeQuery();
      connection.commit();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }
  }

  /**
    * Testmethode
    *
    */
    public static void main(String[] args){
        DerbyClient dc = new DerbyClient();
        dc.createConnect();
        //dc.createTables();
        //dc.insertInto("ort");
        dc.select();
        dc.disconnect();
        System.out.println("Disconnected!"+"Not disconnected!");
    }
}
