package com.feicuiedu.videonews.bombapi.entity;

import java.util.Date;

/**
 * Bomb数据库中的通用字段
 * 作者：yuanchao on 2016/8/16 0016 11:51
 * 邮箱：yuanchao@feicuiedu.com
 */
public abstract class BaseEntity {

    // 唯一Id，由Bomb自动生成
    private String objectId;

    // 创建时间，由Bomb自动生成
    private Date createdAt;

    // 修改时间，由Bomb自动生成
    private Date updatedAt;

    public String getObjectId() {
        return objectId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
