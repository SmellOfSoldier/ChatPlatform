import Server.ClientInformation;
import Server.Sign;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Client
{
    private static ClientInformation Me=new ClientInformation();
    protected static List<ClientInformation> onlineUsers= Collections.synchronizedList(new ArrayList<ClientInformation>());
    protected static List<ClientInformation> offOnlineUsers=Collections.synchronizedList(new ArrayList<ClientInformation>()) ;
    protected static Socket socket=new Socket();
    protected static BufferedReader br=null;
    protected static PrintStream ps=null;
    private Client(){}
    public static String getRealMessage(String line,String cmd)
    {
        String realMessage=line.substring(cmd.length(),line.length());
        return realMessage;
    }
    public static void setMe(ClientInformation c)
    {
        Me=c;
    }
    public static ClientInformation getMe()
    {
        return Me;
    }
    public static void main(String[] args)
            throws IOException
    {
        LoginWindow.loginServer();
    }
}
