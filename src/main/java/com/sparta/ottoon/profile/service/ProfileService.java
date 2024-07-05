package com.sparta.ottoon.profile.service;

import com.sparta.ottoon.auth.entity.PasswordLog;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.entity.UserStatus;
import com.sparta.ottoon.auth.repository.PasswordLogRepository;
import com.sparta.ottoon.auth.repository.UserRepository;
import com.sparta.ottoon.common.exception.CustomException;
import com.sparta.ottoon.common.exception.ErrorCode;
import com.sparta.ottoon.profile.dto.ProfileRequestDto;
import com.sparta.ottoon.profile.dto.ProfileResponseDto;
import com.sparta.ottoon.profile.dto.UserPwRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final PasswordLogRepository passwordLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ProfileResponseDto getUser(String userName) {
        User user = findByUsername(userName);
        return new ProfileResponseDto(user);
    }

    @Transactional
    public ProfileResponseDto updateUser(String userName, String updateUsername, ProfileRequestDto requestDto) {
        User user = findByUsername(userName);
        validateUserPermissions(user, updateUsername);
        user.updateUserInfo(requestDto);
        return new ProfileResponseDto(user);
    }

    @Transactional
    public void updateUserPassword(String userName, String updateUsername, UserPwRequestDto requestDto) {
        User user = findByUsername(userName);
        validateUserPermissions(user, updateUsername);
        //현재 비밀번호와 입력한 비밀번호가 다른지 비교
        if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }
        //현재 비밀번호와 변경하는 비밀번호가 같은지 비교
        if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.DUPLICATE_PASSWORD);
        }
        //현재 passwordLog 에 있는 비밀번호를 List 에 저장
        List<PasswordLog> recentPasswords = passwordLogRepository.findByUserId(user.getId());
        //변경하는 password 가 passwordLog 에 있는 값인지 비교
        for (PasswordLog log : recentPasswords) {
            if (passwordEncoder.matches(requestDto.getNewPassword(), log.getPasswordLog())) {
                throw new CustomException(ErrorCode.LAST3_PASSWORD);
            }
        }
        //이전 비밀번호 log 에 저장하기 위해 encoding 후 save
        PasswordLog oldPasswordLog = new PasswordLog(passwordEncoder.encode(requestDto.getOldPassword()), user);
        passwordLogRepository.save(oldPasswordLog);
        recentPasswords.add(oldPasswordLog);

        //이전 비밀번호 사이즈가 3이 넘어가게되면 가장 오래된 것 삭제
        if (recentPasswords.size() > 3) {
            passwordLogRepository.deleteById(recentPasswords.stream()
                    .sorted(Comparator.comparing(PasswordLog::getCreatedAt))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ErrorCode.FAIL)).getId());
        }
        //새 비밀번호 encoding 해서 저장
        user.updateUserPassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    private User findByUsername(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    //본인 프로필이나 관리자권한인지 확인
    public void validateUserPermissions(User user, String userName){
        User updateUser = findByUsername(userName);
        if(!user.equals(updateUser) && !updateUser.getStatus().equals(UserStatus.ADMIN)){
            throw new CustomException(ErrorCode.USER_FORBIDDEN);
        }
    }


}
