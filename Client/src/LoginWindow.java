
import Server.ClientInformation;
import Server.Server;
import Server.Sign;
import com.google.gson.Gson;
import sun.rmi.runtime.Log;

import javax.swing.*;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class LoginWindow                //登录界面
{
    public static JFrame f=new JFrame("登陆界面");
    public static JTextField  idField=new JTextField(20);
    private static JPasswordField passwordField=new JPasswordField(20);
    private static JButton login=new JButton("登陆");
    private static JButton register=new JButton("注册");
    private static JLabel idlabel=new JLabel("帐号");
    private static JLabel passwordlabel=new JLabel("密码");
    private static JLabel tips=new JLabel();
    private static JTextArea message=new JTextArea();
    public static void loginServer()
    {
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Client.socket.close();
                    System.exit(0);
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        });
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try {
                    String passWord = String.valueOf(passwordField.getPassword());
                    String id = idField.getText();
                    InetSocketAddress address=new InetSocketAddress(InetAddress.getLocalHost(), 40000);
                    try {
                        if(!Client.socket.isBound())
                        {
                            Client.socket.connect(address, 40000);
                            Client.ps = new PrintStream(Client.socket.getOutputStream());
                            Client.br = new BufferedReader(new InputStreamReader(Client.socket.getInputStream()));
                        }
                    }
                    catch (IOException io)
                    {
                        JOptionPane.showMessageDialog(f,  "连接超时！请检查服务器是否开启", "提示",JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                    if(Client.socket.isBound())
                    {
                        Client.ps.println(Sign.login + id + Sign.SplitSign + passWord);
                        String result=null;
                        while((result=Client.br.readLine())==null);
                        if (result.equals(Sign.Pass))
                        {
                            Gson gson=new Gson();
                            String MeStr=Client.br.readLine();                  //接受服务器发来的该用户的基本消息
                            Client.setMe(gson.fromJson(MeStr, ClientInformation.class));
                            tips.setText("密码正确，正在登陆");
                            Thread.sleep(2 * 1000);
                            f.setVisible(false);
                            ChatWindow.openChatWindow();
                        } else {
                            tips.setText("账号或密码有误！");
                        }
                    }
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

        });
        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                RegisterWindow.registerSocket=new Socket();
                RegisterWindow.registe();
            }
        });
        idField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                tips.setText("");
            }
        });
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                tips.setText("");
            }
        });

        message.setEditable(false);

        Box idBox=Box.createHorizontalBox();
        Box passwordBox=Box.createHorizontalBox();
        idBox.add(idlabel);
        idBox.add(Box.createHorizontalStrut(10));
        idBox.add(idField);
        passwordBox.add(passwordlabel);
        passwordBox.add(Box.createHorizontalStrut(10));
        passwordBox.add(passwordField);
        Box enterBox=Box.createVerticalBox();
        tips.setSize(new Dimension(100,50));
        enterBox.add(tips);
        enterBox.add(Box.createVerticalStrut(5));
        enterBox.add(idBox);
        enterBox.add(Box.createVerticalStrut(10));
        enterBox.add(passwordBox);
        Box buttonBox=Box.createHorizontalBox();
        buttonBox.add(login);
        buttonBox.add(Box.createHorizontalStrut(20));
        buttonBox.add(register);
        message.setFont(new Font("Courier",Font.ITALIC,16));
        message.append("欢迎使用聊天系统1.0版本！在使用时请注意一下几点事项\n");
        message.append("①该聊天系统为多人在线聊天系统\n");
        message.append("②您需要您必须要创建一给帐号才能使用该聊天系统，只需创建一次就能永久使用\n");
        message.append("③您可以选择一个用户向他发送单人信息\n");
        message.append("④该系统为测试版，可能有些小bug，如果发现请联系作者：\n");
        message.append("QQ：2251237579       作者：老兵的微笑\n");
        message.append("祝您使用愉快！\n");
        Box box=Box.createHorizontalBox();
        box.add(enterBox);
        box.add( Box.createHorizontalStrut(10));
        box.add(buttonBox);
        f.add(message,BorderLayout.CENTER);
        f.add(box,BorderLayout.SOUTH);
        f.setLocation(750,450);
        f.setResizable(false);
        f.pack();
        f.setVisible(true);
    }
}
