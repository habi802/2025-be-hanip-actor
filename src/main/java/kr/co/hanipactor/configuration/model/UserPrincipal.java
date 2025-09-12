package kr.co.hanipactor.configuration.model;


import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class UserPrincipal implements UserDetails, OAuth2User {
    private final Collection<? extends GrantedAuthority> authorities;
    private final JwtUser jwtUser;

    public UserPrincipal(JwtUser jwtUser) {
        this.jwtUser = jwtUser;
        EnumUserRole role = jwtUser.getRole();
        String roleName = String.format("ROLE_%s", role.name());
        log.info("roleName: {}", roleName);
        this.authorities = List.of(new SimpleGrantedAuthority(roleName));
    }

    public Long getSignedUserId() {
        return jwtUser.getSignedUserId();
    }

    @Override
    public String getPassword() { return null; }

    @Override
    public String getUsername() { return "oauth2"; }

    @Override
    public String getName() { return "oauth2"; }

    @Override
    public Map<String, Object> getAttributes() { return Map.of(); }
}