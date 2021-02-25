/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pqike.bean.babybath;

import com.pqike.bean.Item;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author User
 */
public class BabyBucket extends Item implements Serializable{

    private String color;
    private Short quantity;

    public Short getQuantity() {
        return quantity;
    }

    public void setQuantity(Short quantity) {
        this.quantity = quantity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 47 * hash + Objects.hashCode(this.color);
        hash = 47 * hash + Objects.hashCode(this.quantity);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item otherItem = (Item) obj;
        if(!super.equals(otherItem)){
            return false;
        }
        final BabyBucket other = (BabyBucket) obj;
        if (!Objects.equals(this.color, other.color)) {
            return false;
        }
        if (!Objects.equals(this.quantity, other.quantity)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BabyBucket{" + super.toString()+ ", quantity=" + quantity + ", color=" + color + '}';
    }
    
    public boolean hasNull(){
        if(quantity == null) return true;
        if(color == null) return true;
        return super.hasNull();
    }
}
