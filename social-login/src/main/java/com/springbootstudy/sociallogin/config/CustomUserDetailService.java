package com.springbootstudy.sociallogin.config;

import com.springbootstudy.sociallogin.entity.Account;
import com.springbootstudy.sociallogin.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return createUserDetails(accountRepository.findByAccountId(Integer.parseInt(username)));
    }

    /**
     * db에 account 값이 존재한다면 UserDetails 객체로 만들어서 반환
     *
     * @author Lee Taesung
     * @since 1.0
     */
    private UserDetails createUserDetails(Account account) {
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(account.getAuthority());

        return new User(Integer.toString(account.getAccountId()), account.getPassword(), Collections.singleton(grantedAuthority));
    }
}
