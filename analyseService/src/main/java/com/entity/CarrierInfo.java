package com.entity;

/**
 * @Description TODO
 * @Author wangliqiang
 * @Date 2019/6/3 14:16
 */
public class CarrierInfo {

    private String carrier; // 运营商
    private Long count; // 数量
    private String groupfield; // 分组

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getGroupfield() {
        return groupfield;
    }

    public void setGroupfield(String groupfield) {
        this.groupfield = groupfield;
    }
}
