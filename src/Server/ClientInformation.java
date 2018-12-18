package Server;

import java.io.Serializable;

public class ClientInformation implements Serializable
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
    public boolean passwordEquals(String password){return this.password==password;}
    public String getname(){return name;}
    public String getid(){return id;}
    public boolean equals(ClientInformation c){return c.getid()==this.id;}
    public int hashCode(){return Integer.valueOf(id);}

}
