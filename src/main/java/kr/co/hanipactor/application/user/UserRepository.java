package kr.co.hanipactor.application.user;

import kr.co.hanipactor.configuration.security.model.SignInProviderType;
import kr.co.hanipactor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByLoginId(String loginId);
    User findByLoginIdAndProviderType(String loginId, SignInProviderType providerType);
}
