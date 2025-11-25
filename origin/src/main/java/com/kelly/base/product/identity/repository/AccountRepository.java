package com.kelly.base.product.identity.repository;

import com.kelly.base.product.identity.domain.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * account table repository
 *
 * @author 서강희
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    /**
     * login id 로 account 조회
     *
     * @param loginId loginId
     * @return Optional Account
     */
    Optional<Account> findByLoginId(final String loginId);

    /**
     * account 전체 조회
     * <p>
     * hidden 속성의 계정은 제외하고 전체 계정 목록 반환
     *
     * @return Account List
     */
    List<Account> findAllByIsHiddenFalse();

    /**
     * account 전체 조회 (페이지네이션)
     * <p>
     * hidden 속성의 계정은 제외하고 전체 계정 목록 반환
     *
     * @param pageable 페이지네이션 정보
     * @return Page<Account>
     */
    Page<Account> findAllByIsHiddenFalse(Pageable pageable);
}
