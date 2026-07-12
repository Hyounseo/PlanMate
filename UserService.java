package com.planmate.demo.service;

import com.planmate.demo.domain.User;
import com.planmate.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 내가 주방장(핵심 로직)이다! 라고 선언
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // [기능 1] 회원가입 로직
    @Transactional
    public void registerUser(User user) {
        userRepository.save(user); // DB에 사용자가 입력한 정보를 그대로 저장!
    }

    // [기능 2] 로그인 및 5회 실패 로직
    @Transactional
    public String login(String loginId, String password) {
        // 1. DB에서 아이디로 회원 정보를 찾습니다.
        User user = userRepository.findByLoginId(loginId).orElse(null);

        if (user == null) {
            return "아이디가 존재하지 않습니다.";
        }

        // 2. 이미 계정이 잠긴 상태인지 확인합니다.
        if (user.isLocked()) {
            return "계정이 잠겼습니다. 2차 비밀번호를 입력해주세요.";
        }

        // 3. 비밀번호가 맞는지 확인합니다.
        if (user.getPassword().equals(password)) {
            // 성공! 실패 횟수를 0으로 초기화하고 DB에 저장
            user.setFailCount(0);
            userRepository.save(user);
            return "로그인 성공";
        } else {
            // 실패! 실패 횟수를 1 증가시킵니다. (파이썬에서 count += 1 하던 것과 같습니다)
            user.setFailCount(user.getFailCount() + 1);

            // 만약 5번 틀렸다면 계정을 잠가버립니다.
            if (user.getFailCount() >= 5) {
                user.setLocked(true);
                userRepository.save(user);
                return "5회 오류로 계정이 잠겼습니다. 2차 비밀번호를 입력해주세요.";
            }

            userRepository.save(user);
            return "비밀번호가 틀렸습니다. (현재 " + user.getFailCount() + "회 실패)";
        }
    }

    // [기능 3] 2차 비밀번호로 잠금 해제 로직
    @Transactional
    public String unlockAccount(String loginId, String secondPassword) {
        User user = userRepository.findByLoginId(loginId).orElse(null);

        if (user != null && user.isLocked()) {
            if (user.getSecondPassword().equals(secondPassword)) {
                // 2차 비밀번호가 맞으면 잠금 해제 및 실패 횟수 초기화!
                user.setLocked(false);
                user.setFailCount(0);
                userRepository.save(user);
                return "잠금이 해제되었습니다. 다시 로그인해주세요.";
            } else {
                return "2차 비밀번호도 틀렸습니다.";
            }
        }
        return "잘못된 요청입니다.";
    }
}
