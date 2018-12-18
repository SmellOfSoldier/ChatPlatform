
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import Server.Server;
import Server.Sign;

public class RegisterWindow                 //注册界面
{
    public static Socket registerSocket;
    private static BufferedReader br=null;
    private static PrintStream ps=null;
    public static JFrame f=new JFrame("注册页面");
    private static JTextField nameField=new JTextField(50);
    private static JPasswordField passWordField=new JPasswordField();
    private static JTextField confirmPasswordField=new JTextField();
    private static JLabel nameLabel=new JLabel("昵称");
    private static JLabel passwordLabel=new JLabel("密码");
    private static JTextField idLabel=new JTextField("你的登录帐号会由系统为您提供");
    private static JButton register=new JButton("注册");
    private static JTextField tips=new JTextField("请输入您的昵称与密码");
    public static void registe()
    {
        f.setLocation(100,100);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                nameField.setText("");
                passWordField.setText("");
                f.setVisible(false);
            }
        });

        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    String password = String.valueOf(passWordField.getPassword());
                    if(name.equals("")||password.equals(""))
                    {
                        tips.setText("昵称、密码不能为空");
                    }
                   else {
                        InetSocketAddress address=new InetSocketAddress(InetAddress.getLocalHost(),40000);
                        try {
                            registerSocket.connect(address,50000);
                            ps=new PrintStream( registerSocket.getOutputStream());
                            br=new BufferedReader(new InputStreamReader( registerSocket.getInputStream()));
                        }
                        catch (IOException io)
                        {
                            JOptionPane.showMessageDialog(f,  "连接超时！请检查服务器是否开启", "提示",JOptionPane.ERROR_MESSAGE);

                        }
                        if(registerSocket.isBound())
                        {
                            ps.println(Sign.Register + name + Sign.SplitSign + password);
                            String id =br.readLine();
                            JOptionPane.showMessageDialog(f,  "请记住你的账号为："+id, "你的账号",JOptionPane.OK_OPTION);
                            ps.close();
                            br.close();
                            registerSocket.close();
                            nameField.setText("");
                            passWordField.setText("");
                            f.setVisible(false);
                        }
                    }
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
                catch (NullPointerException ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        f.setResizable(false);
        idLabel.setBackground(new Color(0xB4C0E9));
        idLabel.setEditable(false);
        idLabel.setFont(new Font("",Font.BOLD,15));
        tips.setEditable(false);
        Box enterBox=Box.createVerticalBox();
        Box nameBox=Box.createHorizontalBox();
        Box passWordBox=Box.createHorizontalBox();
        nameBox.add(nameLabel);
        nameBox.add(Box.createHorizontalStrut(10));
        nameBox.add(nameField);
        passWordBox.add(passwordLabel);
        passWordBox.add(Box.createHorizontalStrut(10));
        passWordBox.add(passWordField);
        enterBox.add(tips);
        enterBox.add(Box.createVerticalStrut(10));
        enterBox.add(nameBox);
        enterBox.add(Box.createVerticalStrut(10));
        enterBox.add(passWordBox);
        enterBox.add(Box.createVerticalStrut(10));
        enterBox.add(idLabel);
        Box box=Box.createHorizontalBox();
        box.add(enterBox);
        box.add(Box.createHorizontalStrut(10));
        box.add(register);
        f.add(box);
        f.setLocation(400,200);
        f.pack();
        f.setVisible(true);
    }
}
