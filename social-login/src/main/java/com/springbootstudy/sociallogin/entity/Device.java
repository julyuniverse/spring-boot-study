package com.springbootstudy.sociallogin.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DynamicInsert // insert 시 null인 필드 제외
@DynamicUpdate // update 시 null인 필드 제외
public class Device extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int deviceId;
    private String uuid;
    private String model;
    private String systemName;
    private String systemVersion;
    private Integer accountId;

    @Builder
    public Device(String uuid, String model, String systemName, String systemVersion, Integer accountId) {
        this.uuid = uuid;
        this.model = model;
        this.systemName = systemName;
        this.systemVersion = systemVersion;
        this.accountId = accountId;
    }

    public void updateModel(String model) {
        this.model = model;
    }

    public void updateSystemName(String systemName) {
        this.systemName = systemName;
    }

    public void updateSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public void updateAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}
