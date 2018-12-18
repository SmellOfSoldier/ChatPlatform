package Client;

import Server.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;


public class Client
{
    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket(InetAddress.getLocalHost(), 3000);
            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ArrayList<ClientInformation> arrayList=new ArrayList<ClientInformation>();
                try
                {
                    String s=null;
                    Gson gson=new Gson();
                    s=br.readLine();
                    arrayList=gson.fromJson(s,arrayList.getClass());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            for(ClientInformation c:arrayList)
            {
                System.out.println(c.getname()+"   "+c.getid());
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
