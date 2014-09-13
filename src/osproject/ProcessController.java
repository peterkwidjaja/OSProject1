/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package osproject;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Peter
 */
public class ProcessController {
    LinkedList<ProcessBlock>[] readyList = new LinkedList[3];
    ProcessBlock running;
    ResourceBlock[] resources;
    
    public ProcessController(){
        resources = new ResourceBlock[4];
        for(int i=1;i<=4;i++){
            resources[i-1] = new ResourceBlock("R"+i,i);
        }
        for(int i=0;i<3;i++){
            readyList[i] = new LinkedList<>();
        }
        running = null;
        create("init",0);
        //System.out.println("init");
    }
    public void create(String PID, int priority){
        if(priority<0 || priority>2)
            throw new RuntimeException();
        ProcessBlock newProcess = new ProcessBlock(PID,"ready",priority);
        if(!PID.equals("init")){
            running.addChild(newProcess);
            newProcess.setParent(running);
        }
        readyList[priority].add(newProcess);
        newProcess.setList(readyList[priority]);
        scheduler();
    }
    public void destroy(String PID){
        ProcessBlock temp = findBlock(PID, running);
        if(temp==null){
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
        if(pointer.getResourcesSize()>0){
            ListIterator<ResourceNode> rsc = pointer.getResourcesList();
            while(rsc.hasNext()){
                ResourceNode tempNode = rsc.next();
                ResourceBlock tempBlock = tempNode.getResource();
                tempBlock.setStatus(tempBlock.getStatus()+tempNode.getSize());
            }
        }
        LinkedList<ProcessBlock> temp = pointer.getList();
        if(temp!=null)
            temp.remove(pointer);
        pointer.delChild();
        pointer.getParent().delChild(pointer);
    }
    private void updateResources(){
        for(int i=1;i<=4;i++){
            ResourceBlock temp = resources[i-1];
            boolean flag = true;
            while(temp.getBlockedSize()>0 && flag){  
                ProcessNode nextProcessNode = temp.getList().peekFirst();
                if(nextProcessNode.getRequestSize()<=temp.getStatus()){
                    ProcessBlock nextProcess = nextProcessNode.getProcess();
                    temp.getList().removeFirst();
                    nextProcess.setStatus("ready");
                    nextProcess.setList(readyList[nextProcess.getPriority()]);
                    nextProcess.addResource(temp, nextProcessNode.getRequestSize());
                    readyList[nextProcess.getPriority()].add(nextProcess);
                }
                else
                    flag = false;
            }
        }
    }
    
    public void request(String name, int size){
        int nameBlock = Integer.parseInt(name.substring(1));
        if(nameBlock<1 || nameBlock>4 || name.charAt(0)!='R' || size>nameBlock)
            throw new RuntimeException();
        ResourceBlock temp = resources[nameBlock-1];
        if(temp.isFree(size)){
            temp.setStatus(temp.getStatus()-size);
            running.addResource(temp,size);
            System.out.println("FREE");
        }
        else{
            running.setStatus("blocked");
            running.setList(temp.getList());
            readyList[running.getPriority()].remove(running);
            temp.addBlocked(running,size);
        }
        scheduler();
    }
    public void release(String name, int size){
        int nameBlock = Integer.parseInt(name.substring(1));
        
        if(nameBlock<1 || nameBlock>4 || name.charAt(0)!='R' || size>nameBlock)
            throw new RuntimeException();
        
        ResourceBlock temp = resources[nameBlock-1];
        ListIterator<ResourceNode> list = running.getResourcesList();
        while(list.hasNext()){
            ResourceNode rscNode = list.next();
            if(rscNode.getResource()==temp){
                
                if(rscNode.getSize()<size)
                    throw new RuntimeException();
                
                rscNode.setSize(rscNode.getSize()-size);
                if(rscNode.getSize()<=0) list.remove();
                temp.setStatus(temp.getStatus()+size);
                break;
            }
        }
        if(temp.getBlockedSize()>0){
            ProcessNode nextProcessNode = temp.getList().peekFirst();
            if(nextProcessNode.getRequestSize()<=temp.getStatus()){
                ProcessBlock nextProcess = nextProcessNode.getProcess();
                temp.removeBlocked(nextProcess);
                nextProcess.setStatus("ready");
                nextProcess.setList(readyList[nextProcess.getPriority()]);
                nextProcess.addResource(temp, nextProcessNode.getRequestSize());
                readyList[nextProcess.getPriority()].add(nextProcess);
            }
        }
        scheduler();
    }
    public void timeout(){
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
