package chat.utils;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CacheUtil {

    private static HashMap<String, Channel> cacheSC = new HashMap<>();
    private static HashMap<Channel, String> cacheCS = new HashMap<>();

    public static HashMap<String, Channel> getCacheSC() {
        return cacheSC;
    }

    public static HashMap<Channel, String> getCacheCS() {
        return cacheCS;
    }

    //添加缓存
    public static void put(String name,Channel channel){
        cacheSC.put(name,channel);
        cacheCS.put(channel,name);
    }

    //删除缓存
    public static void del(String name){
        Channel channel = cacheSC.get(name);
        if (channel != null){
            cacheSC.remove(name);
            cacheCS.remove(channel);
        }
    }

    public static void del(Channel channel){
        String s = cacheCS.get(channel);
        if (s != null){
            cacheCS.remove(channel);
            cacheCS.remove(s);
        }
    }

    //获取数据
    public static  Channel get(String name){
        return cacheSC.get(name);
    }

    //查询用户在线信息
    public static ArrayList getUsers(){
        ArrayList<String> list = new ArrayList();

        Set<Map.Entry<String,Channel>> ms = cacheSC.entrySet();
        for (Map.Entry entry: ms) {
            list.add((String)entry.getKey());
        }
        return list;
    }

}
