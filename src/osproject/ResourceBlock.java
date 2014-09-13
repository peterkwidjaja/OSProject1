/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package osproject;

import java.util.LinkedList;

/**
 *
 * @author Peter
 */
public class ResourceBlock {
    private final String PID;
    private int status;
    private final int capacity;
    private LinkedList<ProcessNode> blockedList;
    public ResourceBlock(String PID, int capacity){
        this.PID = PID;
        this.capacity = capacity;
        this.status = capacity;
        this.blockedList = new LinkedList<>();
    }
    public int getStatus(){
        return status;
    }
    public boolean isFree(int num){
        return status>=num;
    }
    public String getPID(){
        return PID;
    }
    public int getCapacity(){
        return capacity;
    }
    public void setStatus(int newStatus){
        this.status = newStatus;
    }
    public LinkedList<ProcessNode> getList(){
        return blockedList;
    }
    public void addBlocked(ProcessBlock newProcess, int size){
        ProcessNode newNode = new ProcessNode(newProcess, size);
        blockedList.add(newNode);
    }
    public void removeBlocked(ProcessBlock process){
        if(blockedList.remove(process)) System.out.println("remove "+process.getID());
    }
    public int getBlockedSize(){
        return blockedList.size();
    }
}
