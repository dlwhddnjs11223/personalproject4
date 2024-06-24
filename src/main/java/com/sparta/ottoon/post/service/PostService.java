package com.sparta.ottoon.post.service;

import com.sparta.ottoon.post.dto.PostRequestDto;
import com.sparta.ottoon.post.dto.PostResponseDto;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.entity.UserStatus;
import com.sparta.ottoon.post.repository.PostRepository;
import com.sparta.ottoon.auth.repository.UserRepository;
import com.sparta.ottoon.common.exception.CustomException;
import com.sparta.ottoon.common.exception.ErrorCode;
import com.sparta.ottoon.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponseDto save(PostRequestDto postRequestDto){
        Long logInUserId = getLogInUserId();
        User user = getUserById(logInUserId);
        Post post = postRequestDto.toEntity();
        post.updateUser(user);
        post = postRepository.save(post);
        return PostResponseDto.toDto("게시글 등록 완료", 200, post);
    }

    public PostResponseDto findById(long postId){
        Post post = findPostById(postId);
        return PostResponseDto.toDto("부분 게시글 조회 완료", 200, post);
    }

    @Transactional
    public List<PostResponseDto> getAll(){
        List<Post> list = postRepository.findAllByOrderByCreatedAtDesc();
        return list
                .stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> PostResponseDto.toDto("전체 게시글 조회 완료", 200, post))
                .toList();
    }

    @Transactional
    public PostResponseDto update(long postId, PostRequestDto postRequestDto){
        Post post = findPostById(postId);
        Long logInUserId = getLogInUserId();
        User user = getUserById(logInUserId);
        // 본인 계정 혹은 관리자 계정이면 게시글 수정 가능
        if (logInUserId.equals(post.getUser().getId()) || user.getStatus().equals(UserStatus.ADMIN)){
            //if (logInUserId.equals(post.getUser().getId())){
            post.update(postRequestDto.getContents());
            return PostResponseDto.toDto("게시글 수정 완료", 200, post);
        } else {
            throw new CustomException(ErrorCode.BAD_AUTH_PUT);
        }
    }

    @Transactional
    public PostResponseDto delete(long postId){
        Post post = findPostById(postId);
        Long logInUserId = getLogInUserId();
        if (logInUserId.equals(post.getUser().getId())) {
            postRepository.delete(post);
            return PostResponseDto.toDeleteResponse("게시글 삭제 완료", 200);
        } else {
            throw new CustomException(ErrorCode.BAD_AUTH_DELETE);
        }
    }

    private Post findPostById(long postId){
        return postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.BAD_POST_ID));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Long getLogInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user.getId();
    }

}
