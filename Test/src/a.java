import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.*;
import java.util.List;

public class a
{
    public static JFrame f=new JFrame("测试");
    public static JPanel jPanel=new JPanel();
    public static JTextField  idField=new JTextField(20);
    public static JPasswordField passwordField=new JPasswordField(20);
    public static JTextArea ta=new JTextArea(8,20);
    public static JButton send=new JButton("发送");
    public static void main(String[] args)
    {
        Gson gson=new Gson();
        ConectionMap<ClientInformation,String> conectionMap=new ConectionMap<>();
        ClientInformation c=new ClientInformation("1","djf","123");
        String cStr=gson.toJson(c);
        ClientInformation tc=new ClientInformation("1","djf","123");
        conectionMap.put(c,"djf");
        String s=conectionMap.removeByKey(tc);
        System.out.println(s);

    }

}
class ClientInformation implements Serializable
{
    private String id;
    private String password;
    private String name;
    public ClientInformation(String id,String name,String password)
    {
        this.id=id;
        this.password=password;
        this.name=name;
    }
    public ClientInformation(){}
    public void setPassword(String newPassword){this.password=newPassword;}
    public void setName(String newName){this.name=newName;}
    public boolean passwordEquals(String password){return this.password.equals(password);}
    public String getName(){return name;}
    public String getId(){return id;}

    @Override
    public boolean equals(Object obj) {
        ClientInformation tc=(ClientInformation)obj;
        return id.equals(tc.getId());
    }

    public int hashCode(){return Integer.valueOf(id);}

}

class ConectionMap<k,v>
{

    public Map<k, v> map = Collections.synchronizedMap(new HashMap<k,v>());

    public synchronized v removeByKey(Object tkey)          //删除该对，返回value
    {
        for(Object key:map.keySet())
        {
            boolean flag=key.equals(tkey);
            if(flag)
            {
                v tps=map.get(key);
                map.remove(key);
                return tps;
            }
        }
        return null;
    }
    public synchronized k  removeByValue(PrintStream ps)             //删除该对，返回key
    {
        for(k key:map.keySet())
        {
            if(map.get(key).equals(ps))
            {
                map.remove(key);
                return key;
            }
        }
        return null;
    }
    public synchronized Set<k> keySet()
    {
        return map.keySet();
    }
    public synchronized Set<v> valueSet()
    {
        Set<v> result=new HashSet<v>();
        for(k key:map.keySet())
        {
            result.add(map.get(key));
        }
        return result;
    }
    public synchronized v put(k key,v value)
    {
        for(k kp:map.keySet())
        {
            if(map.get(kp).equals(value))
            {
                throw new RuntimeException("该用户已经注册!无法重新注册");
            }
        }
        return map.put(key,value);
    }
    public synchronized k getKeyByValue(v value)
    {
        for(k key:map.keySet())
        {
            if(map.get(key).equals(value))
            {
                return key;
            }
        }
        return null;
    }
    public synchronized v get(k key)
    {
        for(k tkey:map.keySet())
        {
            if(tkey.equals(key))
            {
                return map.get(key);
            }
        }
        return null;
    }
}