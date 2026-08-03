package com.planmate.demo.service;

import com.planmate.demo.domain.User;
import com.planmate.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입
    @Transactional
    public String registerUser(User user) {

        // 아이디 중복 체크
        if (userRepository.findByLoginId(user.getLoginId()).isPresent()) {
            return "이미 존재하는 아이디입니다.";
        }

        userRepository.save(user);

        return "회원가입이 완료되었습니다!";
    }

    // 로그인
    @Transactional
    public String login(
            String loginId,
            String password,
            HttpSession session
    ) {

        User user = userRepository
                .findByLoginId(loginId)
                .orElse(null);

        if (user == null) {
            return "아이디가 존재하지 않습니다.";
        }

        // 계정 잠김 상태
        if (user.isLocked()) {
            return "계정이 잠겼습니다. 2차 비밀번호를 입력해주세요.";
        }

        // 비밀번호 일치
        if (user.getPassword().equals(password)) {

            user.setFailCount(0);
            userRepository.save(user);

            // 로그인한 사용자 정보를 세션에 저장
            session.setAttribute("loggedInUser", user.getLoginId());
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());

            return "로그인 성공";

        } else {

            user.setFailCount(
                    user.getFailCount() + 1
            );

            // 5회 실패 시 계정 잠금
            if (user.getFailCount() >= 5) {

                user.setLocked(true);
                userRepository.save(user);

                return "5회 오류로 계정이 잠겼습니다. 2차 비밀번호를 입력해주세요.";
            }

            userRepository.save(user);

            return "비밀번호가 틀렸습니다. (현재 "
                    + user.getFailCount()
                    + "회 실패)";
        }
    }

    // 2차 비밀번호 인증 → 잠금 해제 후 바로 로그인
    @Transactional
    public String unlockAccount(
            String loginId,
            String secondPassword,
            HttpSession session
    ) {

        User user = userRepository
                .findByLoginId(loginId)
                .orElse(null);

        if (user == null) {
            return "아이디가 존재하지 않습니다.";
        }

        if (!user.isLocked()) {
            return "잠긴 계정이 아닙니다.";
        }

        // 2차 비밀번호 확인
        if (secondPassword.equals(user.getSecondPassword())) {

            // 잠금 해제
            user.setLocked(false);
            user.setFailCount(0);

            userRepository.save(user);

            // 로그인한 사용자 정보를 세션에 저장
            session.setAttribute("loggedInUser", user.getLoginId());
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());

            return "2차 비밀번호 인증 성공. 로그인되었습니다.";

        } else {

            return "2차 비밀번호가 틀렸습니다.";
        }
    }
}
