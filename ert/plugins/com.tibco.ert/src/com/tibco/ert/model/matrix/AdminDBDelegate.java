package com.tibco.ert.model.matrix;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tibco.ert.model.matrix.patch.User;
import com.tibco.matrix.admin.server.services.adminconfiguration.types.DBParameters;

public class AdminDBDelegate {
    private static final String SQL_SELECT_PERMISSION = "select * from PERMISSION";

    private static final String SQL_SELECT_USER = "select u.E_ID, u.NAME, hp.VALUE, hp.SALT from \"USER\" u "
            + "inner join PASSWORDHISTORY_HASHEDPASSWORD ph on u.PASSWORDHISTORY_PASSWORDHIS_ID = ph.ID and ph.PASSWORDHISTORY_HISTORY_IDX = 0" + "inner join HASHEDPASSWORD hp on ph.ELT = hp.E_ID";

    private static final String SQL_UPDATE_USER_PASSWORD = "update HASHEDPASSWORD set VALUE = ?, SALT = ? where "
            + "E_ID = (select ph.ELT from PASSWORDHISTORY_HASHEDPASSWORD ph inner join \"USER\" u on u.PASSWORDHISTORY_PASSWORDHIS_ID = ph.ID and ph.PASSWORDHISTORY_HISTORY_IDX = 0 where u.NAME = ?)";

    private static final String SQL_SELECT_KEYSTORE = "select a.ID, a.NAME, a.DESCRIPTION, s.PASSWORD, k.STOREURL from IKEYSTORAGECONFIGURATION k "
            + "inner join SUBSTITUTABLEOBJECT s on k.PASSWORD_PASSWORD_E_ID = s.E_ID inner join ADMINENTITY a on k.E_CONTAINER = a.ID";

    private static final String SQL_SELECT_KEYSTORE_21 = "select a.ID, a.NAME, a.DESCRIPTION, s.PASSWORD, k.STOREURL from KEYSTORECONFIGURATION k "
            + "inner join OBFUSCATED5XPASSWORD s on k.PASSWORD_PASSWORD_E_ID = s.PASSWORD_E_ID " + "inner join KEYSTORE ks on k.IKEYSTORAGECONFIGURATION_E_ID = ks.KEYSTORECONFIGURATION_KEYST_ID "
            + "inner join ADMINENTITY a on ks.SIMPLEEDITABLEENTITY_ID = a.ID ";

    private static final String SQL_SELECT_LOGSERVICE = "select E_ID,l.NAME,DESTINATION,a1.NAME as JMSNAME,EMSQUEUENAME,a2.NAME as JDBCNAME,FILENAME,MAXSIZE,MAXINDEX from LOGSERVICE l "
            + "inner join ADMINENTITY a1 on l.EMSPARAMID = a1.ID left join ADMINENTITY a2 on l.JDBCPARAMID = a2.ID";

    private static final String SQL_SELECT_LOGSERVICE_MODELLIST = "SELECT m.NAME,LOGSERVICE_MODELLIST_E_ID as E_ID FROM MODELNAME m "
            + "inner join LOGSERVICE l on m.LOGSERVICE_MODELLIST_E_ID = l.E_ID order by LOGSERVICE_MODELLIST_IDX";

    private Connection conn;

    private boolean version21;

    public AdminDBDelegate(DBParameters dbParams, String password) throws Exception {
        String username = dbParams.getUsername();
        String url = dbParams.getUrl();
        String driver = dbParams.getDriver();

        conn = createConnection(driver, url, username, password);
        version21 = isVersion21();
    }

    public User[] getUsers() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(SQL_SELECT_USER);
        List<User> list = new ArrayList<User>();

        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt(1));
            user.setName(rs.getString(2));
            user.setPassword(rs.getBytes(3));
            user.setSalt(rs.getBytes(4));
            list.add(user);
        }

        rs.close();
        stmt.close();
        return list.toArray(new User[list.size()]);
    }

    public int[] updateUserPassword(User[] users) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_USER_PASSWORD);

        for (User user : users) {
            ps.setBytes(1, user.getPassword());
            ps.setBytes(2, user.getSalt());
            ps.setString(3, user.getName());
            ps.addBatch();
        }

        int[] result = ps.executeBatch();
        
        ps.close();
        
        return result;
    }

    public List<Map<String, String>> getAllPermissions() throws SQLException {
        return selectCommand(SQL_SELECT_PERMISSION);
    }

    public List<Map<String, String>> getAllKeystores() throws SQLException {
        if (version21) {
            return selectCommand(SQL_SELECT_KEYSTORE_21);
        }
        return selectCommand(SQL_SELECT_KEYSTORE);
    }

    public List<Map<String, String>> getAllLogServices() throws SQLException {
        return selectCommand(SQL_SELECT_LOGSERVICE);
    }

    public List<Map<String, String>> getModelList() throws SQLException {
        return selectCommand(SQL_SELECT_LOGSERVICE_MODELLIST);
    }

    private List<Map<String, String>> selectCommand(String sqlCommand) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sqlCommand);
        List<Map<String, String>> list = convertToList(rs);

        rs.close();
        stmt.close();
        return list;
    }

    private List<Map<String, String>> convertToList(ResultSet rs) throws SQLException {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();
        while (rs.next()) {
            Map<String, String> record = new HashMap<String, String>();
            for (int i = 1; i <= numColumns; i++) {
                String key = rsmd.getColumnName(i);
                String value = convertNullString(rs.getString(i));
                record.put(key.toUpperCase(), value);
            }
            list.add(record);
        }
        return list;
    }

    private String convertNullString(String value) {
        if (value == null)
            return "";
        return value;
    }

    public void release() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private Connection createConnection(String driver, String url, String user, String password) throws Exception {
        Class.forName(driver);
        conn = DriverManager.getConnection(url, user, password);

        Class[] connectionArgTypes = new Class[] { String.class };
        Object[] connectionArgs = new Object[] { "TIBADB123QQQQQQQQQQQQQQQ" };
        // For TIBCO-branded Merant JDBC driver. Use reflection to avoid requiring
        // the merant jars in class path to compile

        Class connectionClass = conn.getClass();
        Class expectedBase = null;
        try {
            expectedBase = Class.forName("com.merant.datadirect.jdbc.extensions.ExtEmbeddedConnection");
        } catch (ClassNotFoundException ce) {
            // if this class is not there, the driver is not embedded driver
            // so ignore error
        }
        if (expectedBase != null && expectedBase.isAssignableFrom(conn.getClass())) {
            Method m = connectionClass.getMethod("unlock", connectionArgTypes);
            boolean unlocked = ((Boolean) m.invoke(conn, connectionArgs)).booleanValue();
            if (!unlocked)
                throw new Exception("Unable unlock for the driver " + driver);
        }
        return conn;
    }

    private boolean isVersion21() throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM MACHINE");
        int colCount = rs.getMetaData().getColumnCount();

        rs.close();
        stmt.close();

        if (colCount == 7) {
            return true;
        }
        return false;
    }
}
