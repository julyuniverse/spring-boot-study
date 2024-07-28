package com.springbootstudy.sociallogin.repository;

import com.springbootstudy.sociallogin.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface AccountRepository extends JpaRepository<Account, Integer> {
}
