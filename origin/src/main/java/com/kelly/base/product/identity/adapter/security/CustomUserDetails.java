package com.kelly.base.product.identity.adapter.security;

import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.domain.account.AccountStatus;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
@ToString
public class  CustomUserDetails extends User {
    private final long accountId;   // accountId ( Entity )

    /**
     * 생성자
     *
     * @param account <code>Account</code> entity
     * @author kelly
     */
    public CustomUserDetails(final Account account) {
        // accountNonExpired, credentialsNonExpired 는 사용하지 않음 -> true
        super(
                account.getLoginId(), account.getPassword(),
                account.getStatus() == AccountStatus.ACTIVE,
                true,
                true,
                account.getStatus() != AccountStatus.LOCKED,
                List.of(new SimpleGrantedAuthority(account.getRole().getAuthority()))
        );

        this.accountId = account.getId();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
