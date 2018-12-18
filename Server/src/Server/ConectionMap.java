package Server;


import java.io.PrintStream;
import java.util.*;

public class ConectionMap<k,v>
{

    public Map<k, v> map = Collections.synchronizedMap(new HashMap<k,v>());

    public synchronized v removeByKey(k c)          //删除该对，返回value
    {
        for(k key:map.keySet())
        {
            boolean flag=c.equals(key);
            if(flag)
            {
                v tps=map.get(key);
                map.remove(key);
                return tps;
            }
        }
        return null;
    }
    public synchronized k  removeByValue(v ps)             //删除该对，返回key
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
