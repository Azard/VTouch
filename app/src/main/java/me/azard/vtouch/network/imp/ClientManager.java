package me.azard.vtouch.network.imp;

/**
 * Created by Elega on 2014/7/9.
 */
public class ClientManager {
    private Long client[]=new Long[2];
    //cid:client的ID号
    public boolean addClient(long cid){
        if (client[0]==null){
            client[0]=cid;
            return true;
        }
        else if(client[1]==null){
            client[1]=cid;
            return true;
        }
        return false;
    }
    public boolean removeClient(long cid){
        if (client[0]==null) return false;
        if (client[0]==cid){
            client[0]=null;
            return true;
        }
        else if(client[1]==cid){
            client[1]=null;
            return true;
        }
        else return false;
    }
    //ID:第i个设备
    public boolean isConnected(int id){
        if (client[id]!=null) return true;
        else return false;
    }

    public Long getCid(int id){
        return client[id];
    }

    public Integer getId(long cid) {
        if (client[0] == cid) return 0;
        if (client[1] == cid) return 1;
        else return null;
    }

    public void clear(){
        client[0]=null;
        client[1]=null;
    }
}
