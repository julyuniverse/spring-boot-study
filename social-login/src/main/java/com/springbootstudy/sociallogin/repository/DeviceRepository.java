package com.springbootstudy.sociallogin.repository;

import com.springbootstudy.sociallogin.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface DeviceRepository extends JpaRepository<Device, Integer> {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<Device> findByUuid(String uuid);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Device findByDeviceId(int deviceId);
}
