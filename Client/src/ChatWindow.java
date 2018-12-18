
import Server.ClientInformation;
import Server.Server;
import Server.Sign;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

public class ChatWindow
{
    public static JFrame f =new JFrame("帐号："+Client.getMe().getId()+"          "+"昵称："+Client.getMe().getName());
    private static JButton send =new JButton("发送");
    private static JTextArea writeArea=new JTextArea(10,50);
    public static JTextArea messageArea=new JTextArea(10,50);
    public static JList<String> onlineUserList=new JList<String>();
    public static JList<String> offOnlineUserList=new JList<String>();
    public static JLabel onlineUsersLabel=new JLabel();
    public static JLabel offOnlineUserLabel=new JLabel();
    public static JTextArea sendMessageTips=new JTextArea(10,20);
    public static String receiverId=null;
    public static String receiverName=null;
    private static void initial()
    {
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Gson gson=new Gson();
                String offOlineUserStr=gson.toJson(Client.getMe());
                Client.ps.println(Sign.ClientExit+offOlineUserStr);
                System.exit(0);
            }
        });
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String message=writeArea.getText();
                if(receiverId==null)                //发送公共消息
                {
                    Client.ps.println(Sign.SendPublicMessage + message);
                }
                else                                //发送私人消息
                {
                    Client.ps.println(Sign.SendPrivateMessage + receiverId +Sign.SplitSign+message);
                    ChatWindow.messageArea.append("你悄悄的对"+receiverName+"说："+message+"\n");
                }
                writeArea.setText("");
            }
        });
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setBackground(new Color(0xD5D7DA));
        Box writeBox=Box.createHorizontalBox();
        JScrollPane messageAreaJsp=new JScrollPane(messageArea);
        JScrollPane writeAreaJsp=new JScrollPane(writeArea);
        writeBox.add(writeAreaJsp);
        writeBox.add(Box.createHorizontalStrut(40));
        writeBox.add(send);

        onlineUsersLabel.setText("在线用户列表（"+Client.onlineUsers.size()+"）");
        onlineUsersLabel.setFont(new Font("",Font.BOLD,18));
        onlineUsersLabel.setBackground(new Color(0x69CC45));
        onlineUserList.setBackground(new Color(0x8FC581));
        offOnlineUserLabel.setText("离线用户列表（"+Client.offOnlineUsers.size()+"）");
        offOnlineUserLabel.setFont(new Font("",Font.BOLD,18));
        offOnlineUserLabel.setBackground(new Color(0xAFB2B6));
        offOnlineUserList.setBackground(new Color(0x83808B));
        JScrollPane onlineUserListJsp=new JScrollPane(onlineUserList);
        JScrollPane offOnlineUserListJsp=new JScrollPane(offOnlineUserList);
        onlineUserList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                List<String> selectList=onlineUserList.getSelectedValuesList();
                String s=selectList.get(0);
                if(s.equals("发送公共消息"))
                {
                    receiverId=null;
                }
                else {
                    receiverId = getIdFromUserList(selectList.get(0));
                    receiverName=getNameFromUserList(selectList.get(0));
                }
            }
        });
        sendMessageTips.setEditable(false);
        sendMessageTips.setLineWrap(true);
        sendMessageTips.setFont(new Font("",Font.BOLD,12));
        sendMessageTips.append("发送信息提示：\n");
        sendMessageTips.append("①如果你要发送公共信息，用鼠标点击“发送公共信息即可。”\n");
        sendMessageTips.append("②如果你要发送私人信息，用鼠标点击你要发送的用户。\n");
        sendMessageTips.append("系统默认发送公共信息。\n");
        Box userListBox=Box.createVerticalBox();
        userListBox.add(sendMessageTips);
        userListBox.add(onlineUsersLabel);
        userListBox.add(onlineUserListJsp);
        userListBox.add(offOnlineUserLabel);
        userListBox.add(offOnlineUserListJsp);
        updateUserList();

        f.add(messageAreaJsp,BorderLayout.CENTER);
        f.add(writeBox,BorderLayout.SOUTH);
        f.add(userListBox,BorderLayout.EAST);
        f.setLocation(400,400);
        //f.setPreferredSize(new Dimension(400,500));
        f.pack();

    }
    public static String getIdFromUserList(String s)
    {
        char[] ts=s.toCharArray();
        int from=0;
        for(;ts[from]!='(';from++);
        int end=from;
        for(;ts[end]!=')';end++);
        return s.substring(from+1,end);
    }
    public static String getNameFromUserList(String s)
    {
        char[] ts=s.toCharArray();
        int end=0;
        for(;ts[end]!='(';end++)
        {
        }
        return s.substring(0,end);
    }
    public static void openChatWindow()
    {
        initial();
        new LisentThread().start();
        f.setVisible(true);
    }
    public static void updateUserList()                 //更新用户列表信息
    {
        Vector<String> onlineUserStrVector=new Vector<String>();
        Vector<String> offOnlineUserStrVector=new Vector<String>();
        onlineUserStrVector.add("发送公共消息");
        if(!Client.onlineUsers.isEmpty())
        {
            for (ClientInformation c : Client.onlineUsers)
            {
                onlineUserStrVector.add(c.getName() + "(" + c.getId() + ")");
            }
        }
        if(!Client.offOnlineUsers.isEmpty())
        {
            for(ClientInformation c:Client.offOnlineUsers)
            {
                offOnlineUserStrVector.add(c.getName() + "(" + c.getId() + ")");
            }
        }
        ChatWindow.onlineUserList.setListData(onlineUserStrVector);
        ChatWindow.offOnlineUserList.setListData(offOnlineUserStrVector);
        ChatWindow.onlineUsersLabel.setText("在线用户列表（"+Client.onlineUsers.size()+"）");
        ChatWindow.offOnlineUserLabel.setText("离线用户列表（"+Client.offOnlineUsers.size()+"）");
    }
}

class LisentThread extends Thread            //监听来自服务器的消息
{
    public void run()
    {
        while(true)
        {
            String line=null;
            try {
                while ((line=Client.br.readLine())!=null)
                {

                    if(line.startsWith(Sign.SendPublicMessage))                 //收到公共消息
                    {
                        String realMessage= Client.getRealMessage(line, Sign.SendPublicMessage);
                        ChatWindow.messageArea.append(realMessage+"\n");
                    }
                    else if(line.startsWith(Sign.SendPrivateMessage))           //收到私人消息
                    {
                        String realMessage=Client.getRealMessage(line,Sign.SendPrivateMessage);
                        String senderId=realMessage.split(Sign.SplitSign)[0];
                        String message=realMessage.split(Sign.SplitSign)[1];
                        for(ClientInformation c:Client.onlineUsers)
                        {
                            if(c.getId().equals(senderId))
                            {
                                ChatWindow.messageArea.append(c.getName()+"("+senderId+")悄悄的对你说："+message+"\n");
                            }
                        }
                    }
                    else if(line.startsWith(Sign.FromServerMessage))           //收到来自服务器的消息
                    {
                        String realMessage=Client.getRealMessage(line,Sign.FromServerMessage);
                        ChatWindow.messageArea.append("来自服务器的消息："+realMessage+"\n");
                    }
                    else if(line.startsWith(Sign.SendObject))                  //收到服务器发送过来的其他在线用户
                    {
                        String realMessage=Client.getRealMessage(line,Sign.SendObject);
                        Gson gson=new Gson();
                        ClientInformation [] allUser=gson.fromJson(realMessage,ClientInformation[].class);
                        for(int i=0;i<allUser.length;i++)
                        {
                            if(allUser[i].isOnline())
                                Client.onlineUsers.add(allUser[i]);
                            else
                                Client.offOnlineUsers.add(allUser[i]);
                        }
                        ChatWindow.updateUserList();
                    }
                    else if(line.startsWith(Sign.OneUserIsOnline))              //接收到一个用户登录
                    {
                        String realMessage=Client.getRealMessage(line,Sign.OneUserIsOnline);
                        Gson gson=new Gson();
                        ClientInformation newOnlineUser=gson.fromJson(realMessage,ClientInformation.class);
                        Client.offOnlineUsers.remove(newOnlineUser);
                        Client.onlineUsers.add(newOnlineUser);
                        ChatWindow.updateUserList();
                    }
                    else if(line.startsWith(Sign.OneUserOffOnline))             //接受到一个用户离线
                    {
                        String realMessage=Client.getRealMessage(line,Sign.OneUserOffOnline);
                        Gson gson=new Gson();
                        ClientInformation newOffOnlineUser=gson.fromJson(realMessage,ClientInformation.class);
                        Client.onlineUsers.remove(newOffOnlineUser);
                        Client.offOnlineUsers.add(newOffOnlineUser);
                        ChatWindow.updateUserList();
                    }
                    else if(line.startsWith(Sign.RepeatOnline))
                    {
                        try
                        {
                            Client.ps.close();
                            Client.br.close();
                            Client.socket.close();
                            JOptionPane.showMessageDialog(ChatWindow.f,  "你的账号已经被其他用户登录", "提示",JOptionPane.ERROR_MESSAGE);
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                        finally
                        {
                            System.exit(0);
                        }
                    }
                    else if(line.startsWith(Sign.ServerExit))                   //服务器关闭
                    {
                        ChatWindow.messageArea.setText("服务器已经断开连接.......客户端即将关闭！");
                        Thread.sleep(3*1000);
                        System.exit(0);
                    }
                }
            }
            catch (EOFException eof)
            {

            }
            catch (IOException ioe)
            {

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}