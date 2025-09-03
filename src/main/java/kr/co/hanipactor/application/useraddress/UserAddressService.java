package kr.co.hanipactor.application.useraddress;

import kr.co.hanipactor.application.useraddress.model.UserAddressPostReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAddressService {
    private final UserAddressMapper userAddressMapper;
    private final UserAddressRepository userAddressRepository;

    public void userAddressSave(UserAddressPostReq req) {
        userAddressMapper.save(req);
    }
}
