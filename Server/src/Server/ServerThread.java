package Server;

import com.google.gson.Gson;
import com.sun.security.ntlm.Client;
import javafx.scene.shape.Line;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;

public class ServerThread extends Thread
{
    private Socket socket;
    private PrintStream ps=null;
    private BufferedReader bf=null;
    ServerThread(Socket socket)
    {
        this.socket=socket;
    }
    public void run()
    {
        try
        {
            ps = new PrintStream(socket.getOutputStream());
            bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
               sign: while ((line = bf.readLine()) != null)
                {
                    if (line.startsWith(Sign.login))          //用户登录
                    {
                        String realMessage = getRealMessage(line,Sign.login);          //解密信息
                        String id = realMessage.split(Sign.SplitSign)[0];
                        String password = realMessage.split(Sign.SplitSign)[1];
                        boolean pass = false;
                        for (ClientInformation c : Server.clientInformations)
                        {
                            if (c.passwordEquals(password) && c.getId().equals(id))             //判断密码帐号是否正确
                            {
                                pass = true;
                                for(ClientInformation tc:Server.conectionMap.keySet())      //判断该帐号是否有人登录
                                {
                                    if(c.equals(tc))
                                    {
                                        Server.conectionMap.removeByKey(c).println(Sign.RepeatOnline);  //给客户端发送重复登录的信息
                                        Server.OnlienClientNum--;
                                        break ;
                                    }
                                }
                                c.setOnline(true);                  //设置该用户上线的标志
                                Gson gson=new Gson();
                                String onlineUserStr =gson.toJson(c);
                                Server.conectionMap.put(c, ps);
                                ps.println(Sign.Pass);                  //给用户发送登录成功的信息
                                ps.println(onlineUserStr);              //给登录用户发送他的基本信息
                                Server.sendAllUsersInformations(ps);
                                Server.updateOnlineClient();            //更新在线用户情况
                                Server.OnlienClientNum++;
                                Server.onlineClient.setText(String.valueOf(Server.OnlienClientNum));    //修改在线人数
                                try
                                {
                                    Thread.sleep(500);
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                                for(PrintStream tps:Server.conectionMap.valueSet())
                                {
                                    if(!tps.equals(ps))
                                    {
                                        tps.println(Sign.OneUserIsOnline + onlineUserStr);        //给所有用户发送有新的用户上线的消息
                                    }
                                }
                                break;
                            }
                        }
                        if (!pass)
                        {
                            ps.println(Sign.UnPass);
                        }
                    }
                    else if (line.startsWith(Sign.Register))           //注册用户
                    {
                        String realMessage = getRealMessage(line,Sign.Register);
                        String name = realMessage.split(Sign.SplitSign)[0];
                        String password = realMessage.split(Sign.SplitSign)[1];
                        String id = "" + (++Server.endClientsNum);
                        ClientInformation NewClient = new ClientInformation(id, name, password);
                        Server.clientInformations.add(NewClient);
                        Server.saveClientInformation();
                        ps.println(id);
                    }
                    else if(line.startsWith(Sign.ClientExit))          //用户离线
                    {
                        String realMessage=getRealMessage(line,Sign.ClientExit);
                        Gson gson=new Gson();
                        ClientInformation offOnlineUser=gson.fromJson(realMessage,ClientInformation.class);
                        try
                        {
                            if(bf!=null)
                                bf.close();
                            if(ps!=null)
                                ps.close();
                        }
                        catch (IOException ioex)
                        {
                            ioex.printStackTrace();
                        }
                        socket.close();
                        Server.OnlienClientNum--;
                        Server.onlineClient.setText(String.valueOf(Server.OnlienClientNum));
                        Server.conectionMap.removeByKey(offOnlineUser);

                        for(ClientInformation c:Server.clientInformations)
                        {
                            if(c.equals(offOnlineUser))
                            {
                                c.setOnline(false);
                            }
                        }
                        offOnlineUser.setOnline(false);
                        String offOnlineUserStr=gson.toJson(offOnlineUser);

                        for(PrintStream tps:Server.conectionMap.valueSet())
                        {
                            tps.println(Sign.OneUserOffOnline+offOnlineUserStr);      //给所有用户发送有用户下线的消息
                        }
                        Server.updateOnlineClient();
                        break sign;
                    }
                    else if (line.startsWith(Sign.SendPublicMessage))       //发送公共信息
                    {
                        String realMessage = getRealMessage(line,Sign.SendPublicMessage);
                        String senderName=Server.conectionMap.getKeyByValue(ps).getName();
                        for (PrintStream p : Server.conectionMap.valueSet())
                        {
                            p.println(Sign.SendPublicMessage+senderName + ": " + realMessage);
                        }
                        Server.clientMessageArea.append("("+senderName+")"+"对所有人说："+realMessage+"\n");
                    }
                    else if (line.startsWith(Sign.SendPrivateMessage))     //发送私人信息
                    {
                        String realMessage = getRealMessage(line,Sign.SendPrivateMessage);
                        String receiverID = realMessage.split(Sign.SplitSign)[0];
                        String message = realMessage.split(Sign.SplitSign)[1];
                        ClientInformation sender = Server.conectionMap.getKeyByValue(ps);
                        ClientInformation receiver=null;
                        for (ClientInformation c : Server.conectionMap.keySet())
                        {
                            if (c.getId() .equals(receiverID))
                            {
                                receiver=c;
                                Server.conectionMap.get(c).println(Sign.SendPrivateMessage+ receiverID+Sign.SplitSign+ message);
                                break;
                            }
                        }
                        Server.clientMessageArea.append(sender.getName()+"悄悄的对"+receiver.getName()+"说:"+message+"\n");
                    }
                }
        }
        catch (IOException ioe)
        {
            try
            {
                if(bf!=null)
                    bf.close();
                if(ps!=null)
                    ps.close();
                if(socket!=null)
                    socket.close();
            }
            catch (IOException ex)
            {
                ioe.printStackTrace();
            }
        }
    }
    public static String getRealMessage(String line,String cmd)
    {
        String realMessage=line.substring(cmd.length(),line.length());
        return realMessage;
    }
}
