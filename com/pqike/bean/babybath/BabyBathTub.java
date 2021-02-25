/*
 * Author: Srinivas
 * POJO with hasNull method. Having it this way enables useful abstractions in 
   DAO layer
 */
package com.pqike.bean.babybath;

import com.pqike.bean.Item;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author User
 */
public class BabyBathTub extends Item implements Serializable {

    private Short length;
    private Short width;
    private Short height;
    private String color;

    
    public Short getLength() {
        return length;
    }

    public void setLength(Short length) {
        this.length = length;
    }

    public Short getWidth() {
        return width;
    }

    public void setWidth(Short width) {
        this.width = width;
    }

    public Short getHeight() {
        return height;
    }

    public void setHeight(Short height) {
        this.height = height;
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
        hash = 47 * hash + Objects.hashCode(this.length);
        hash = 47 * hash + Objects.hashCode(this.width);
        hash = 47 * hash + Objects.hashCode(this.height);
        hash = 47 * hash + Objects.hashCode(this.color);
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
        
        final BabyBathTub other = (BabyBathTub) obj;
        if (!Objects.equals(this.length, other.length)) {
            return false;
        }
        
        if (!Objects.equals(this.width, other.width)) {
            return false;
        }
        if (!Objects.equals(this.height, other.height)) {
            return false;
        }
        if (!Objects.equals(this.color, other.color)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BabyBathTub{" + super.toString()+ ", length=" + length + ", width=" + width + ", height=" + height + ", color=" + color + '}';
    }
    
    
    
    public boolean hasNull(){
        if(length == null) return true;
        if(width == null) return true;
        if(height == null) return true;
        if(color == null) return true;
        return super.hasNull();
    }

}
