package com.sparta.ottoon.profile.controller;

import com.sparta.ottoon.profile.dto.ProfileRequestDto;
import com.sparta.ottoon.profile.dto.ProfileResponseDto;
import com.sparta.ottoon.profile.dto.UserPwRequestDto;
import com.sparta.ottoon.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile API", description = "Profile API 입니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class ProfileController {

    private final ProfileService userService;

    @Operation(summary = "getUser", description = "프로필 조회 기능입니다.")
    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponseDto> getUser(@PathVariable String username){
        return ResponseEntity.ok().body(userService.getUser(username));
    }

//    @Operation(summary = "getToptenUser", description = "프로필 조회 기능입니다.")
//    @GetMapping("/topten")
//    public ResponseEntity<ProfileResponseDto> getToptenUser(){
//        return ResponseEntity.ok().body(userService.getToptenUser());
//    }

    @Operation(summary = "updateUser", description = "프로필 수정 기능입니다.")
    @PostMapping("/{userName}")
    public ResponseEntity<ProfileResponseDto> updateUser(@PathVariable String userName,
                                                         @AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody ProfileRequestDto requestDto){
        return ResponseEntity.ok().body(userService.updateUser(userName, userDetails.getUsername(),requestDto));
    }

    @Operation(summary = "updateUserPassword", description = "비밀번호 변경 기능입니다.")
    @PostMapping("/{userName}/password")
    public ResponseEntity<String> updateUserPassword(@PathVariable String userName,
                                                     @AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestBody @Valid UserPwRequestDto requestDto){
        userService.updateUserPassword(userName, userDetails.getUsername(), requestDto);
        return ResponseEntity.ok().body("비밀번호가 정상적으로 변경되었습니다.");
    }

}
