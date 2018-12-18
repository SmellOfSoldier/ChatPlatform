package Server;

import java.io.Serializable;

public class ClientInformation implements Serializable
{
    private String id;
    private String password;
    private String name;
    private boolean isOnline=false;
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
    public void setOnline(boolean flag){isOnline=flag;}
    public boolean isOnline(){return isOnline;}
}
