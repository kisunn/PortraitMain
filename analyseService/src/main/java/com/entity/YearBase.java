package com.entity;

/**
 * @Description TODO
 * @Author wangliqiang
 * @Date 2019/6/3 10:51
 */
public class YearBase {
    private String yeartype; // 年代类型
    private Long count; // 数量
    private String groupfield; // 分组字段

    public String getYeartype() {
        return yeartype;
    }

    public void setYeartype(String yeartype) {
        this.yeartype = yeartype;
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
