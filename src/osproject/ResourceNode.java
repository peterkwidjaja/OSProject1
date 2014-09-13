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
public class ResourceNode {
    private ResourceBlock resource;
    private int size;
    public ResourceNode(ResourceBlock resource, int size){
        this.resource = resource;
        this.size = size;
    }
    public int getSize(){
        return size;
    }
    public ResourceBlock getResource(){
        return resource;
    }
    public void setSize(int newSize){
        this.size = newSize;
    }
    public String getID(){
        return resource.getPID();
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof ResourceBlock){
            ResourceBlock temp = (ResourceBlock) obj;
            return temp==resource;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.resource);
        return hash;
    }
}
