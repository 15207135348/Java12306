package com.yy.other.constant;

public enum TrainType {


    C("城际列车", true, true),
    D("动车组", true, true),
    G("高速列车", true, true),
    Z("直达列车", true, true),
    T("特快列车", true, true),
    K("快速列车", true, true),
    L("临时旅客列车", true, true),
    A("局管内临时旅客列车", true, true),
    Y("旅游列车", true, true),
    O("普快列车", true, false);


    private String desc;
    private boolean hasAirConditioning;
    private boolean isNewAirConditioning;


    TrainType(String desc, boolean hasAirConditioning, boolean isNewAirConditioning) {
        this.desc = desc;
        this.hasAirConditioning = hasAirConditioning;
        this.isNewAirConditioning = isNewAirConditioning;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isHasAirConditioning() {
        return hasAirConditioning;
    }

    public void setHasAirConditioning(boolean hasAirConditioning) {
        this.hasAirConditioning = hasAirConditioning;
    }

    public boolean isNewAirConditioning() {
        return isNewAirConditioning;
    }

    public void setNewAirConditioning(boolean newAirConditioning) {
        isNewAirConditioning = newAirConditioning;
    }

    public static TrainType getTrainType(String trainCode){
        if (trainCode.startsWith("C")){
            return TrainType.C;
        }
        if (trainCode.startsWith("D")){
            return TrainType.D;
        }
        if (trainCode.startsWith("G")){
            return TrainType.G;
        }
        if (trainCode.startsWith("Z")){
            return TrainType.Z;
        }
        if (trainCode.startsWith("T")){
            return TrainType.T;
        }
        if (trainCode.startsWith("K")){
            return TrainType.K;
        }
        if (trainCode.startsWith("L")){
            return TrainType.L;
        }
        if (trainCode.startsWith("A")){
            return TrainType.A;
        }
        if (trainCode.startsWith("Y")){
            return TrainType.Y;
        }
        return TrainType.O;
    }
}
