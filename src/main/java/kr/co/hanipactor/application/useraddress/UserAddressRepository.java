package kr.co.hanipactor.application.useraddress;

import kr.co.hanipactor.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
}
