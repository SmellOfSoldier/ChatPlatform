import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;

public class SwindJDialog
{

    public static int value=0;
    public static void main(String[] args)
            throws IOException
    {

        JFrame f=new JFrame();
        String s="123";
        Integer a=1;
        ClientInformation c=new ClientInformation("1","2","3");
        DefaultListModel defaultListModel=new DefaultListModel();
        JList list=new JList(defaultListModel);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2)
                {
                    System.out.println();;
                }
            }
        });
        defaultListModel.addElement(s);
        defaultListModel.addElement(a);
        defaultListModel.addElement(c.getName());

        f.add(list);
        f.setVisible(true);


    }
}
