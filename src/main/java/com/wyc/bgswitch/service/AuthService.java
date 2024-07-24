package com.wyc.bgswitch.service;

import com.wyc.bgswitch.config.lock.RedisLockPrefix;
import com.wyc.bgswitch.exception.InvalidCredentialException;
import com.wyc.bgswitch.exception.UsernameConflictException;
import com.wyc.bgswitch.lock.LockManager;
import com.wyc.bgswitch.redis.entity.Auth;
import com.wyc.bgswitch.redis.repository.AuthRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wyc
 */

@Service
public class AuthService implements UserDetailsService {
    private final AuthRepository authRepository;
    private final LockManager lockManager;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(AuthRepository authRepository, LockManager lockManager, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.lockManager = lockManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String password) throws HttpClientErrorException.Conflict {
        if (!username.startsWith("weapp-") && (username.trim().length() < 8 || username.trim().length() > 12)) {
            throw new InvalidCredentialException("Invalid username %s.".formatted(username));
        }
        // todo: validate username: nums and letters only
        LockManager.MultiLockBuilder.MultiLock lock = lockManager.useBuilder()
                .obtain(RedisLockPrefix.LOCK_PREFIX_USER).of(username)
                .build();
        lock.lock();
        try {
            List<Auth> l = authRepository.findByUsername(username);
            if (l == null || l.size() == 0) {
                // 找不到才创建
                authRepository.save(new Auth(username, passwordEncoder.encode(password), List.of(Auth.ROLES.USER)));
            } else {
                throw new UsernameConflictException("Username %s is unavailable.".formatted(username));
//                throw new HttpClientErrorException(HttpStatusCode.valueOf(409), "Username %s is unavailable.".formatted(username));
            }
        } finally {
            lock.unLock();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Auth> l = authRepository.findByUsername(username);
        if (l == null || l.isEmpty()) {
            throw new UsernameNotFoundException("Username %s not found.".formatted(username));
        }
        return new MyUserDetails(l.get(0));
    }

    public Boolean userExists(String username) {
        List<Auth> l = authRepository.findByUsername(username);
        return l != null && l.size() == 1;
    }

    private record MyUserDetails(Auth auth) implements UserDetails {
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return auth.getRoles().stream().map(Authority::new).collect(Collectors.toList());
        }

        @Override
        public String getPassword() {
            return auth.getPassword();
        }

        @Override
        public String getUsername() {
            return auth.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        private record Authority(Auth.ROLES role) implements GrantedAuthority {
            @Override
            public String getAuthority() {
                return Auth.ROLES.PREFIX + role.name();
            }
        }
    }
}
