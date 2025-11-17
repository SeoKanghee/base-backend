package com.kelly.base.product.identity.repository;

import com.kelly.base.product.identity.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByLoginId(final String loginId);
}
