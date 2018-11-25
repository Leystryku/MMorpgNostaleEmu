package com.company.javastale.inc;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.net.*;

public class Main
{

    public static int sessionid = 0;
    public static ndb database = null;

    //Session is used by client when not logged in

    public static byte[] EncryptSessionMessage(byte[] msg)
    {
        byte[] crypted = new byte[msg.length+2];

        for(int i=0;i<msg.length;i++)
        {
            crypted[i] = (byte)((msg[i] + 0xF)^0xC3);;
        }

        crypted[msg.length] = 0x19;
        crypted[msg.length+1] = 0x2F;

        return crypted;
    }

    public static byte[] DecryptSessionMessage(byte[] msg)
    {
        byte[] crypted = new byte[msg.length];

        for(int i=0;i<msg.length;i++)
        {
            crypted[i] = (byte)((msg[i] - 0xF)^0xC3);
        }

        return crypted;
    }

    //login message is used by the login server

    public static byte[] EncryptLoginMessage(byte[] msg)
    {
        byte[] crypted = new byte[msg.length+2];

        for(int i=0;i<msg.length;i++)
        {
            crypted[i] = (byte)((msg[i] + 0xF));;
        }

        crypted[msg.length] = 0x19;
        crypted[msg.length+1] = 0x2F;

        return crypted;
    }

    public static byte[] DecryptLoginMessage(byte[] msg)
    {
        byte[] crypted = new byte[msg.length];

        for(int i=0;i<msg.length;i++)
        {
            crypted[i] = (byte)((msg[i] - 0xF));
        }

        return crypted;
    }

    public static String DecryptPassword(String pass)
    {


        int skip_bytes = pass.length() % 4;

        if (skip_bytes!=0)
        {
            pass = pass.substring(skip_bytes, pass.length());
        }
        else {
            pass = pass.substring(4, pass.length());
        }

        byte[] passbuff = new byte[pass.length()];

        int cnt = 0;

        for(int i=1;i<pass.length()-1;i+=4)
        {
            byte[] shit = new byte[2];
            shit[0] = (byte)pass.charAt(i);
            shit[1] = (byte)pass.charAt(i+2);

            passbuff[cnt++] = (byte)Integer.parseInt(new String(shit), 16);

        }


        return new String(passbuff, 0, cnt);
    }

    //add encryptgamepacket/decryptgamepacket pls


    public static void TCP_LoginServer()
    {
        try {
            ServerSocket tcp_socket = new ServerSocket(4001);

            while(true)
            {

                Socket client = tcp_socket.accept();

                String ip = client.getInetAddress().toString();
                int port = client.getPort();


                byte[] buffer = new byte[0xFFFF];

                int receivedbytes = client.getInputStream().read(buffer, 0, buffer.length);

                byte[] msg = new byte[receivedbytes];
                System.arraycopy(buffer, 0, msg, 0, receivedbytes);

                String ssession = new String(DecryptSessionMessage(msg));

                String[] splitted = ssession.split(" ");

                sessionid = Integer.parseInt(splitted[1]);

                String password = DecryptPassword(splitted[3]);
                System.out.printf("LOGIN PKG FROM: %s __ %d __ ID: %d __ PASS: %s\n%s\n", ip, port, sessionid, password, new String(DecryptSessionMessage(msg)));


                ResultSet worked;

                try
                {
                    PreparedStatement ps = database.GetStatement("SELECT * from ndb_players WHERE nickname=? AND password=?");
                    ps.setString(1, splitted[2]);
                    ps.setString(2, password);

                    worked = ps.executeQuery();

                    if(!worked.next())
                    {
                        String blocklogin = "fail Fehler: Deine ID oder dein Passwort ist falsch.\rZu viele fehlgeschlagene Versuche führen zu einer temporären Sperre.";
                        client.getOutputStream().write(EncryptLoginMessage(blocklogin.getBytes("ISO-8859-1")));
                        client.getOutputStream().flush();
                        client.close();

                        System.out.printf("PASS: %s\n", password);
                        continue;
                    }

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    continue;
                }

                try
                {
                    PreparedStatement ps = database.GetStatement("UPDATE ndb_players SET `sessionid`=? WHERE nickname=?");
                    ps.setString(1, splitted[1]);
                    ps.setString(2, splitted[2]);

                    ps.executeUpdate();


                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    continue;
                }

                String serverlist = "NsTeST 56759 37.4.8.57:27015:3:1.1.Hitler";

                client.getOutputStream().write(EncryptLoginMessage(serverlist.getBytes("ISO-8859-1")));
                client.getOutputStream().flush();
                client.close();

            }

        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {


        new Thread(Main::TCP_LoginServer).start();


        try
        {
            database = new ndb("127.0.0.1",3306,"root","root", "ndb");

            database.Query("CREATE TABLE IF NOT EXISTS ndb_players (  nickname VARCHAR(50) PRIMARY KEY, password VARCHAR(50), sessionid INT(32))");
            //database.Query("INSERT INTO ndb_players (`nickname`, `password`, `sessionid`) VALUES("Leystryku", "lollol", 0)");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }



    }
}
