package Server;

import com.google.gson.Gson;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{

    public static int serverPort=40000;
    public static String serverAddress;
    public static int OnlienClientNum=0;
    public static JFrame f=new JFrame("服务器");
    public static JButton exit=new JButton("退出服务器");
    public static JTextField onlineClient=new JTextField();
    public static JLabel label=new JLabel("当前在线人数:");
    public static int beginClientsNum=0;    //初始化后用户数量
    public static int endClientsNum=0;      //当前用户数量
    public static ArrayList<ClientInformation> clientInformations=new ArrayList<ClientInformation>();   //保存用户的信息
    public static ConectionMap<ClientInformation,PrintStream> conectionMap=new ConectionMap<ClientInformation, PrintStream>(); //保存用户所对应的socket
    public static JTextArea clientMessageArea=new JTextArea(20,30);
    public static JTextArea onlineClientArea=new JTextArea(10,30);
    public static JTextArea writeArea=new JTextArea(10,20);
    public static JButton sendButton=new JButton("发送");
    public static JButton clearButton=new JButton("清除用户聊天记录");
    public static String hostStr;
    private static void initial()     //初始化操作,加载所有用户的信息
    {
        onlineClientArea.setEditable(false);
        clientMessageArea.setEditable(false);
        onlineClient.setEditable(false);
        onlineClientArea.setFont(new Font("",Font.BOLD,16));
        clientMessageArea.setFont(new Font("",Font.BOLD,16));
        onlineClient.setText("0");
        onlineClientArea.setText("在线用户情况:\n");
        onlineClientArea.setText("id\tname\n");
        clientMessageArea.setText("用户聊天情况:\n");

        clearButton.addActionListener((e)->{
            clientMessageArea.setText("用户聊天情况:\n");
        });
        exit.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {       //服务器退出，通知客户端退出
                for(PrintStream ps:conectionMap.valueSet())
                {
                    ps.println(Sign.ServerExit);
                }
                System.exit(0);
            }
        });
        Box topBox=Box.createHorizontalBox();
        topBox.add(label);
        topBox.add(Box.createHorizontalStrut(10));
        topBox.add(onlineClient);
        topBox.add(clearButton);
        topBox.add(exit);

        JScrollPane clientMessageAreaJsp=new JScrollPane(clientMessageArea);
        JScrollPane clientOnlineAreaJsp=new JScrollPane(onlineClientArea);
        Box messageBox=Box.createHorizontalBox();
        messageBox.add(clientOnlineAreaJsp);
        messageBox.add(clientMessageAreaJsp);

        JScrollPane writeAreaJsp=new JScrollPane(writeArea);
        sendButton.addActionListener(new ActionListener() {                 //服务器发送消息
            @Override
            public void actionPerformed(ActionEvent e) {
                String message=writeArea.getText();
                for(PrintStream tps:conectionMap.valueSet())
                {
                    tps.println(Sign.FromServerMessage+message);

                }
                writeArea.setText("");
            }
        });
        Box writeBox=Box.createHorizontalBox();
        writeBox.add(writeAreaJsp);
        writeBox.add(Box.createHorizontalStrut(20));
        writeBox.add(sendButton);


        f.addWindowListener(new WindowAdapter() {           //服务器退出，通知客户端退出
            @Override
            public void windowClosing(WindowEvent e) {
                for(PrintStream ps:conectionMap.valueSet())
                {
                    ps.println(Sign.ServerExit);
                }
                System.exit(0);
            }
        });

        f.add(topBox,BorderLayout.NORTH);
        f.add(messageBox,BorderLayout.CENTER);
        f.add(writeBox,BorderLayout.SOUTH);
        f.setLocation(200,300);
        f.pack();
        f.setVisible(true);
        try
        {
            hostStr=InetAddress.getLocalHost().toString();
            serverAddress= InetAddress.getLocalHost().toString();
            File file=new File(".","clientInformation.dat");
            if(!file.exists())
            {
                file.createNewFile();
                return;
            }
            FileInputStream fis=new FileInputStream(file);
            ObjectInputStream ois=new ObjectInputStream(fis);
            try
            {
                ClientInformation c;
                while ((c=(ClientInformation)ois.readObject())!=null)
                {
                    clientInformations.add(c);
                    beginClientsNum++;
                    endClientsNum++;
                }
            }
            catch (EOFException eof)
            {
                System.out.println("初始化完毕!");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                fis.close();
                ois.close();
            }
        }
        catch (IOException ic)
        {
            ic.printStackTrace();
        }
    }
    public static void saveClientInformation()          //保存用户的信息
    {
        try
        {
            File file=new File(".","clientInformation.dat");
            if(!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream fos=new FileOutputStream(file);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            for(int i=beginClientsNum;i<clientInformations.size();i++)
            {
                oos.writeObject(clientInformations.get(i));
            }
        }
        catch (IOException ie)
        {
            ie.printStackTrace();
        }
    }

    public static void updateOnlineClient()                 //跟新在线用户情况
    {
        onlineClientArea.setText("在线用户情况:\n");
        for(ClientInformation c:conectionMap.keySet())
        {
            onlineClientArea.append(c.getId()+"\t"+c.getName()+"\n");
        }
    }

    public static void sendAllUsersInformations(PrintStream ps)
    {
        Gson gson=new Gson();
        String s = gson.toJson(Server.clientInformations);
        ps.println(Sign.SendObject+s);
    }
    public static void main(String[] args)
            throws IOException
    {
        initial();
        try {
            ServerSocket ss=new ServerSocket(serverPort);
            while(true)
            {
                Socket socket=ss.accept();
                new ServerThread(socket).start();
            }
        }
        catch (BindException bex)
        {
            JOptionPane.showMessageDialog(null,"请检查是否重复启动服务器！","服务器端口被占领",JOptionPane.OK_OPTION);
            System.exit(0);
        }
    }
}