package Server;

import com.google.gson.Gson;
import netscape.javascript.JSObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{

    public static void main(String[] args)
    {
       try
       {
           ServerSocket serverSocket=new ServerSocket(3000);
           ClientInformation clientInformation=new ClientInformation("1","djf","123");
           Gson gson=new Gson();
           Socket socket=null;
           while(true) {
                socket = serverSocket.accept();
               break;
           }
               BufferedWriter bf=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
               ArrayList<ClientInformation> c=new ArrayList<ClientInformation>();
               c.add(new ClientInformation("1","djf","123"));
               c.add(new ClientInformation("2","abc","1234"));
               c.add(new ClientInformation("3","djf","12345"));
               c.add(new ClientInformation("4","lkj","123456"));
               String s1=gson.toJson(c);
               System.out.println(s1);
               bf.write(s1);
               bf.newLine();
               bf.flush();

       }
       catch (IOException ioe)
       {
           ioe.printStackTrace();
       }
    }
}
