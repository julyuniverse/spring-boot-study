package com.springbootstudy.sociallogin.util;

import com.springbootstudy.sociallogin.dto.AccountDto;
import com.springbootstudy.sociallogin.entity.Account;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public class ConvertUtils {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public static AccountDto convertAccountDto(Account account) {
        return new AccountDto(account.getAccountId(), account.getEmail(), account.getFirstName(), account.getLastName());
    }
}
