package com.bomeans.irreader;

import com.bomeans.IRKit.TypeItem;

/**
 * Created by ray on 2017/6/19.
 */

public class TypeItemEx extends TypeItem {

    public TypeItemEx(TypeItem type) {
        this.typeId = type.typeId;
        this.name = type.name;
        this.locationName = type.locationName;
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
