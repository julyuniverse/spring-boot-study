package com.springbootstudy.sociallogin.repository;

import com.springbootstudy.sociallogin.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface AccountRepository extends JpaRepository<Account, Integer> {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<Account> findByAccountIdAndIsActive(int accountId, boolean isActive);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<Account> findByUserIdentifier(String userIdentifier);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Account findByAccountId(int accountId);
}
