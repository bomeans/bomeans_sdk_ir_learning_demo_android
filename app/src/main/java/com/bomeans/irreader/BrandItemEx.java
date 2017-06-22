package com.bomeans.irreader;

import com.bomeans.IRKit.BrandItem;

/**
 * Created by ray on 2017/6/16.
 */



public class BrandItemEx extends BrandItem {

    public BrandItemEx(BrandItem brand) {
        this.brandId = brand.brandId;
        this.name = brand.name;
        this.locationName = brand.locationName;
    }

    @Override
    public String toString() {
        if (this.name.equalsIgnoreCase(this.locationName)) {
            return this.name;
        } else {
            return String.format("%s(%s)", this.name, this.locationName);
        }
    }
}
