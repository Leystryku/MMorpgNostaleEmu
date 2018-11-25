package com.company.javastale.inc;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.net.*;
import com.company.javastale.inc.ndb;

public class Main
{

    public static ndb database = null;

    //add encryptgamepacket/decryptgamepacket pls


    public static byte[] DecryptGamePacket(int sessionid, byte[] msg)
    {


        int sessionkey = (sessionid&0xFF);
        int sessionnum = (sessionid>>6)&0xFF&0x80000003;

        byte[] crypted = new byte[msg.length];

        for(int i=0;i<msg.length;i++)
        {

            crypted[i] = (byte)((msg[i]^(byte)0xC3) - (byte)sessionkey + (byte)64);
        }

        return crypted;
    }


    public static byte[] EncryptGamePacket(int sessionid, byte[] msg)
    {


        int sessionkey = (sessionid&0xFF);
        int sessionnum = (sessionid>>6)&0xFF&0x80000003;

        byte[] crypted = new byte[msg.length];

        for(int i=0;i<msg.length;i++)
        {

            crypted[i] = (byte)((msg[i]^(byte)0xC3) - (byte)sessionkey + (byte)64);
        }

        return crypted;
    }

    public static void TCP_GameServer()
    {
        try {
            ServerSocket tcp_socket = new ServerSocket(27015);

            while(true)
            {

                Socket client = tcp_socket.accept();

                String ip = client.getInetAddress().toString();
                int port = client.getPort();


                byte[] buffer = new byte[0xFFFF];

                int receivedbytes = client.getInputStream().read(buffer, 0, buffer.length);

                byte[] msg = new byte[receivedbytes];
                System.arraycopy(buffer, 0, msg, 0, receivedbytes);

                System.out.printf("GAMESERVER PKG FROM: %s __ %d\n%s\n", ip, port, new String(DecryptGamePacket(0, msg)));



/*
                String decryptpls = "0B 9C 93 96 8C 8B A0 8C 8B 9E 8D 8B 83 14 E0 FF";

                for(String s : decryptpls.split(Pattern.quote(" "))) {
                    int number = Integer.parseInt(s, 16);
                    client.getOutputStream().write(number);
                }
*/
                String decryptpls2 = "05 9C 93 96 8C 8B 83 14 10 09 B3 9A 9E 94 9A 8D 96 99 86 FE 14 14 15 18 14 15 16 B1 64 43 D8 35 C3 AC 32 53 25 32 53 25 15 81 15 15 14 37 77 34 3D 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 14 83 14 E0 FF 05 9C 93 96 8C 8B 83 15 10 09 B3 9A 86 8C 8B 8D 86 94 8A FE 14 14 14 1D 91 41 61 CA 17 84 38 45 93 84 46 3B A7 36 6B 36 C4 38 4B 93 84 BA 1C 41 19 15 14 36 54 93 43 A7 63 43 77 73 43 75 D3 43 75 C3 43 75 B3 43 59 D3 43 D3 25 32 53 25 32 53 25 32 53 90 25 32 53 25 32 51 41 4E FF 05 9C 93 96 8C 8B 83 16 10 08 94 90 9D 9E 93 AB C5 D6 FE 14 15 14 17 14 14 15 12 53 56 35 3C 32 53 25 32 53 25 15 11 51 51 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 53 25 32 51 41 4E FF 0A 9C 93 96 8C 8B A0 9A 91 9B F5 FF";

                for(String s : decryptpls2.split(Pattern.quote(" "))) {
                    int number = Integer.parseInt(s, 16);
                    client.getOutputStream().write(number);
                }

                client.getOutputStream().flush();


            }

        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args)
    {


        new Thread(Main::TCP_GameServer).start();


        /*
        String decryptpls = "EE 01 B5 F9 75 1B FE F3 F3 FB 02 01 FE 00 FF F3 F2 F5 67 EE 01 B5 09 6B 1D F1 06 EC 0D FD 69 05 67";

        String[] strArr = decryptpls.split(Pattern.quote(" "));
        int curshit = 0;
        byte[] shit = new byte[strArr.length];

        for(String s : strArr)
        {
            int number = Integer.parseInt(s, 16);
            shit[curshit++] =  (byte)number;
        }

        System.out.printf("ENC: %s\n", new String(DecryptGamePacket( shit)));
*/

    }
}
