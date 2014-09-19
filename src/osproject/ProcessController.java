/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package osproject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Peter
 */
public class ProcessController {
    LinkedList<ProcessBlock>[] readyList = new LinkedList[3];
    ProcessBlock init;
    ProcessBlock running;
    ResourceBlock[] resources;
    HashMap<String, ProcessBlock> hashProcess = new HashMap<String, ProcessBlock>();
    
    public ProcessController(){
        resources = new ResourceBlock[4];
        for(int i=1;i<=4;i++){
            resources[i-1] = new ResourceBlock("R"+i,i);
        }
        for(int i=0;i<3;i++){
            readyList[i] = new LinkedList<ProcessBlock>();
        }
        running = null;
        create("init",0);
    }
    public void create(String PID, int priority){
        if((priority==0&&!PID.equals("init")) || priority<0 || priority>2 || hashProcess.containsKey(PID))
            throw new RuntimeException();
        ProcessBlock newProcess = new ProcessBlock(PID,"ready",priority);
        if(!PID.equals("init")){
            running.addChild(newProcess);
            newProcess.setParent(running);
        }
        else{
            init = newProcess;
        }
        readyList[priority].add(newProcess);
        newProcess.setReadyList(readyList[priority]);
        hashProcess.put(PID, newProcess);
        scheduler();
    }
    public void destroy(String PID){
        if(PID.equalsIgnoreCase("init"))
            throw new RuntimeException();
        ProcessBlock temp = findBlock(PID, running);
        if(temp==null){ //throw error if process is not found under the child of the current running process
            throw new RuntimeException();
        }
        killTree(temp);
        if(running==temp)
            running=null;
        updateResources();
        scheduler();
    }
    private ProcessBlock findBlock(String PID, ProcessBlock block){
        if(block.getID().equals(PID)){
            return block;
        }
        ListIterator<ProcessBlock> list = block.getChild();
        ProcessBlock temp;
        while(list.hasNext()){
            temp = findBlock(PID, list.next());
            if(temp!=null){
                return temp;
            }
        }
        return null;
    }
    private void killTree(ProcessBlock pointer){       
        ListIterator<ProcessBlock> child = pointer.getChild();
        while(child.hasNext()){          
            killTree(child.next());
        }
        
        //release all resources held by the process
        if(pointer.getResourcesSize()>0){
            ListIterator<ResourceNode> rsc = pointer.getResourcesList();
            while(rsc.hasNext()){
                ResourceNode tempNode = rsc.next();
                ResourceBlock tempBlock = tempNode.getResource();
                tempBlock.setStatus(tempBlock.getStatus()+tempNode.getSize()); //add the number of empty unit(s) in the resource
            }
        }
        
        //Update pointers
        LinkedList temp = pointer.getList();
        if(temp!=null){
            temp.remove(pointer);
        }
        hashProcess.remove(pointer.getID());
        pointer.delChild();
    }
    private void updateResources(){
        for(int i=1;i<=4;i++){
            ResourceBlock temp = resources[i-1];
            boolean flag = true;
            while(temp.getBlockedSize()>0 && flag && temp.getStatus()>0){  
                ProcessNode nextProcessNode = temp.getList().peekFirst();
                if(nextProcessNode.getRequestSize()<=temp.getStatus()){
                    ProcessBlock nextProcess = nextProcessNode.getProcess();
                    temp.getList().removeFirst();
                    temp.setStatus(temp.getStatus()-nextProcessNode.getRequestSize());
                    nextProcess.setStatus("ready");
                    nextProcess.setReadyList(readyList[nextProcess.getPriority()]);
                    nextProcess.addResource(temp, nextProcessNode.getRequestSize());
                    readyList[nextProcess.getPriority()].add(nextProcess);
                }
                else
                    flag = false;
            }
        }
    }
    
    public void request(String name, int size){
        if(running.getID().equals("init"))
            throw new RuntimeException();
        
        int nameBlock = Integer.parseInt(name.substring(1));
        
        if(nameBlock<1 || nameBlock>4 || name.charAt(0)!='R' || size>nameBlock)
            throw new RuntimeException();
        
        //check for resource redundancy
        //Return error if trying to request same resource but over the size limit
        if(running.getResourcesSize()>0){
            ListIterator<ResourceNode> rscIterator = running.getResourcesList();
            while(rscIterator.hasNext()){
                ResourceNode tempNode = rscIterator.next();
                if(tempNode.getID().equalsIgnoreCase(name)){
                    if(tempNode.getSize()+size > nameBlock)
                        throw new RuntimeException();
                }
            }
        }
        
        ResourceBlock temp = resources[nameBlock-1];
        if(temp.isFree(size)){
            temp.setStatus(temp.getStatus()-size);
            running.addResource(temp,size);
        }
        else{
            running.setStatus("blocked");
            running.setBlockedList(temp.getList());
            readyList[running.getPriority()].remove(running);
            temp.addBlocked(running,size);
        }
        scheduler();
    }
    
    public void release(String name, int size){
        if(running.getID().equals("init"))
            throw new RuntimeException();
        
        int nameBlock = Integer.parseInt(name.substring(1));
        
        if(nameBlock<1 || nameBlock>4 || name.charAt(0)!='R' || size>nameBlock)
            throw new RuntimeException();
        
        boolean found = false;
        ResourceBlock temp = resources[nameBlock-1];
        ListIterator<ResourceNode> list = running.getResourcesList();
        //Find the resource in the process
        while(list.hasNext()){
            ResourceNode rscNode = list.next();
            if(rscNode.getResource()==temp){
                found = true;
                
                if(rscNode.getSize()<size) //throw error if size to release is larger than what exists
                    throw new RuntimeException();
                
                rscNode.setSize(rscNode.getSize()-size);
                if(rscNode.getSize()==0) list.remove(); //if the size is 0
                temp.setStatus(temp.getStatus()+size);
                break;
            }
        }
        if(!found) throw new RuntimeException(); //detect if the resourceID does not exist in the process
        
        if(temp.getBlockedSize()>0){ //If there's another process waiting after the deletion
            ProcessNode nextProcessNode = temp.getList().peekFirst();
            if(nextProcessNode.getRequestSize()<=temp.getStatus()){
                ProcessBlock nextProcess = nextProcessNode.getProcess();
                temp.removeFirstBlocked();
                temp.setStatus(temp.getStatus()-nextProcessNode.getRequestSize());
                nextProcess.setStatus("ready");
                nextProcess.setReadyList(readyList[nextProcess.getPriority()]);
                nextProcess.addResource(temp, nextProcessNode.getRequestSize());
                readyList[nextProcess.getPriority()].add(nextProcess);
            }
        }
        scheduler();
    }
    public void timeout(){
        if(running.getID().equals("init"))
            throw new RuntimeException();
        
        readyList[running.getPriority()].remove(running);
        running.setStatus("ready");
        readyList[running.getPriority()].add(running);
        scheduler();
    }
    private void scheduler(){
        ProcessBlock highest = findHighest();
        if(running == null || running.getPriority() < highest.getPriority() || !running.getStatus().equals("running")){
            highest.setStatus("running");
            running = highest;
        }
        System.out.println(running.getID());
    }
    private ProcessBlock findHighest(){
        for(int i=2; i>=0; i--){
            if(!readyList[i].isEmpty())
                return readyList[i].peek();
        }
        return null;
    }
}
