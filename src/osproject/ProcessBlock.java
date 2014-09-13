/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package osproject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Peter
 */
public class ProcessBlock {
    private final String PID;
    //resource
    private String status;
    private LinkedList<ProcessBlock> list;
    private ProcessBlock parent;
    private LinkedList<ProcessBlock> child;
    private int priority;
    private LinkedList<ResourceNode> otherResources;
    public ProcessBlock(String PID, String status, int priority){
        this.PID = PID;
        this.priority = priority;
        this.status = status;
        parent = null;
        child = new LinkedList<>();
        otherResources = new LinkedList<>();
    }
    //Mutators
    public void setParent(ProcessBlock parent){
        this.parent = parent;
    }
    public void addChild(ProcessBlock child){
        this.child.add(child);
    }
    public void delChild(){
        this.child.clear();
    }
    public void delChild(ProcessBlock child){
        this.child.remove(child);
    }
    public void setStatus(String newStatus){
        this.status = newStatus;
    }
    public void setPriority(int newPriority){
        this.priority = newPriority;
    }
    public void setList(LinkedList list){
        this.list = list;
    }
    
    //Accessors
    public ProcessBlock getParent(){
        return parent;
    }
    public String getID(){
        return PID;
    }
    public String getStatus(){
        return status;
    }
    public LinkedList getList(){
        return list;
    }
    public int getPriority(){
        return priority;
    }
    public ListIterator<ProcessBlock> getChild(){
        return child.listIterator(0);
    }
    
    public void addResource(ResourceBlock newResource, int size){
        ResourceNode newNode = new ResourceNode(newResource, size);
        otherResources.add(newNode);
    }
    public int getResourcesSize(){
        return otherResources.size();
    }
    public ListIterator<ResourceNode> getResourcesList(){
        return otherResources.listIterator(0);
    }
}
