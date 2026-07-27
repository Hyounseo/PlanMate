package com.planmate.demo.repository;

import com.planmate.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository를 상속받으면, 저장하고 삭제하는 기본 기능이 자동으로 생깁니다!
public interface UserRepository extends JpaRepository<User, Long> {

    // "DB야, 로그인 아이디(loginId)를 줄 테니까, 그거랑 똑같은 유저 정보 좀 찾아와 줄래?" 라는 명령어입니다.
    Optional<User> findByLoginId(String loginId);
}
