/*
 * Author: Srinivas
 */
package com.pqike.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author User
 */
//Should not let to create an Item object. Item has to considered to be a template.
public abstract class Item implements Serializable {

    private Integer id;
    private String brand;
    private String name;
    private String description;
    private Integer sellingPrice;
    private Integer discount;
    private Integer merchant;
    private Integer inStock;
    private Integer skuId;
    private Date lastModifiedTime;
    private Integer clerkId;
    private Integer supplierId;
    private Integer costPrice;
    private Integer markedPrice;
    private Integer threshold;
    private String modelNumber;
    private Boolean visible;
    private Boolean reward;
    private Integer offlineReserve;
    private String billDescription;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Integer sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }


    public Integer getMerchant() {
        return merchant;
    }

    public void setMerchant(Integer merchant) {
        this.merchant = merchant;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Integer getClerkId() {
        return clerkId;
    }

    public void setClerkId(Integer clerkId) {
        this.clerkId = clerkId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Integer costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getMarkedPrice() {
        return markedPrice;
    }

    public void setMarkedPrice(Integer markedPrice) {
        this.markedPrice = markedPrice;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean isReward() {
        return reward;
    }

    public void setReward(Boolean reward) {
        this.reward = reward;
    }

    public Integer getOfflineReserve() {
        return offlineReserve;
    }

    public void setOfflineReserve(Integer offlineReserve) {
        this.offlineReserve = offlineReserve;
    }

    public String getBillDescription() {
        return billDescription;
    }

    public void setBillDescription(String billDescription) {
        this.billDescription = billDescription;
    }

    @Override
    public String toString() {
        return "Item{" + "id=" + id + ", name=" + name + ", description=" + description + ", sellingPrice=" + sellingPrice + ", discount=" + discount + ", merchant=" + merchant + ", inStock=" + inStock + ", skuId=" + skuId + ", lastModifiedTime=" + lastModifiedTime + ", clerkId=" + clerkId + ", supplierId=" + supplierId + ", costPrice=" + costPrice + ", markedPrice=" + markedPrice + ", threshold=" + threshold + ", modelNumber=" + modelNumber + ", visible=" + visible + ", reward=" + reward + ", offlineReserve=" + offlineReserve + ", billDescription=" + billDescription + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + Objects.hashCode(this.description);
        hash = 47 * hash + Objects.hashCode(this.sellingPrice);
        hash = 47 * hash + Objects.hashCode(this.discount);
        hash = 47 * hash + Objects.hashCode(this.merchant);
        hash = 47 * hash + Objects.hashCode(this.inStock);
        hash = 47 * hash + Objects.hashCode(this.skuId);
        hash = 47 * hash + Objects.hashCode(this.lastModifiedTime);
        hash = 47 * hash + Objects.hashCode(this.clerkId);
        hash = 47 * hash + Objects.hashCode(this.supplierId);
        hash = 47 * hash + Objects.hashCode(this.costPrice);
        hash = 47 * hash + Objects.hashCode(this.markedPrice);
        hash = 47 * hash + Objects.hashCode(this.threshold);
        hash = 47 * hash + Objects.hashCode(this.modelNumber);
        hash = 47 * hash + Objects.hashCode(this.visible);
        hash = 47 * hash + Objects.hashCode(this.reward);
        hash = 47 * hash + Objects.hashCode(this.offlineReserve);
        hash = 47 * hash + Objects.hashCode(this.billDescription);
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
        final Item other = (Item) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.sellingPrice, other.sellingPrice)) {
            return false;
        }
        if (!Objects.equals(this.discount, other.discount)) {
            return false;
        }
        if (!Objects.equals(this.merchant, other.merchant)) {
            return false;
        }
        if (!Objects.equals(this.inStock, other.inStock)) {
            return false;
        }
        if (!Objects.equals(this.skuId, other.skuId)) {
            return false;
        }
        if (!Objects.equals(this.lastModifiedTime, other.lastModifiedTime)) {
            return false;
        }
        if (!Objects.equals(this.clerkId, other.clerkId)) {
            return false;
        }
        if (!Objects.equals(this.supplierId, other.supplierId)) {
            return false;
        }
        if (!Objects.equals(this.costPrice, other.costPrice)) {
            return false;
        }
        if (!Objects.equals(this.markedPrice, other.markedPrice)) {
            return false;
        }
        if (!Objects.equals(this.threshold, other.threshold)) {
            return false;
        }
        if (!Objects.equals(this.modelNumber, other.modelNumber)) {
            return false;
        }
        if (!Objects.equals(this.visible, other.visible)) {
            return false;
        }
        if (!Objects.equals(this.reward, other.reward)) {
            return false;
        }
        if (!Objects.equals(this.offlineReserve, other.offlineReserve)) {
            return false;
        }
        if (!Objects.equals(this.billDescription, other.billDescription)) {
            return false;
        }
        return true;
    }

    public boolean hasNull() {
        if (id == null) {
            return true;
        }
        if (name == null) {
            return true;
        }
        if (description == null) {
            return true;
        }
        if (sellingPrice == null) {
            return true;
        }
        if (discount == null) {
            return true;
        }
        if (merchant == null) {
            return true;
        }
        if (inStock == null) {
            return true;
        }
        if (skuId == null) {
            return true;
        }
        if (lastModifiedTime == null) {
            return true;
        }
        if (clerkId == null) {
            return true;
        }
        if (supplierId == null) {
            return true;
        }
        if (costPrice == null) {
            return true;
        }
        if (markedPrice == null) {
            return true;
        }
        if (threshold == null) {
            return true;
        }
        if (modelNumber == null) {
            return true;
        }
        if (visible == null) {
            return true;
        }
        if (reward == null) {
            return true;
        }
        if (offlineReserve == null) {
            return true;
        }
        if (billDescription == null) {
            return true;
        }
        return false;
    }

    public void copy(Item itm){
        if(itm == null)
            throw new IllegalArgumentException("Null item received");
        this.billDescription = itm.billDescription;
        this.clerkId = itm.clerkId;
        this.costPrice = itm.clerkId;
        this.description = itm.description;
        this.discount = itm.discount;
        this.id = itm.id;
        this.inStock = itm.inStock;
        this.lastModifiedTime = itm.lastModifiedTime;
        this.markedPrice = itm.markedPrice;
        this.merchant = itm.merchant;
        this.modelNumber = itm.modelNumber;
        this.name = itm.name;
        this.offlineReserve = itm.offlineReserve;
        this.reward = itm.reward;
        this.sellingPrice = itm.sellingPrice;
        this.skuId = itm.skuId;
        this.supplierId = itm.supplierId;
        this.threshold = itm.threshold;
        this.visible = itm.visible;
    }
}
