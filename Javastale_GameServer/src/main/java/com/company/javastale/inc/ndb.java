
/**
 * Created by Leystryku on 01.02.2016.
 */


package com.company.javastale.inc;

import java.io.IOException;
import java.sql.*;


/**
 * Created by Leystryku on 01.02.2016.
 */

public class ndb
{


    private Connection connect = null;

    public ndb(String serverip, int port, String username, String password, String database) throws Exception
    {

        try
        {

            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://" + serverip + ":" + port + "/" + database + "?" + "user=" + username + "&password=" + password + "&useSSL=true");

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public ResultSet Query(String query) throws Exception
    {
        try
        {
            Statement statement = connect.createStatement();

            boolean gotresultset = statement.execute(query);

            if(gotresultset)
            {
                return statement.getResultSet();
            }

            return null;

        }

        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public PreparedStatement GetStatement(String query) throws Exception
    {
        try
        {
            PreparedStatement statement = connect.prepareStatement(query);

            return statement;

        }

        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }


}
