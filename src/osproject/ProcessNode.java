/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package osproject;

import java.util.Objects;

/**
 *
 * @author Peter
 */
public class ProcessNode {
    private final ProcessBlock process;
    private int size;
    public ProcessNode(ProcessBlock process, int size){
        this.process = process;
        this.size = size;
    }
    public int getRequestSize(){
        return size;
    }
    public ProcessBlock getProcess(){
        return process;
    }
    
    public void setRequestSize(int newSize){
        this.size = newSize;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof ProcessBlock){
            ProcessBlock temp = (ProcessBlock) obj;
            return temp.equals(process);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.process);
        return hash;
    }
}
