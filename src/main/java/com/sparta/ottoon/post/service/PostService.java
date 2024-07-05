package com.sparta.ottoon.post.service;

import com.querydsl.core.Tuple;
import com.sparta.ottoon.post.dto.PostRequestDto;
import com.sparta.ottoon.post.dto.PostResponseDto;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.entity.UserStatus;
import com.sparta.ottoon.post.repository.PostCustomRepositoryImpl;
import com.sparta.ottoon.post.repository.PostRepository;
import com.sparta.ottoon.auth.repository.UserRepository;
import com.sparta.ottoon.common.exception.CustomException;
import com.sparta.ottoon.common.exception.ErrorCode;
import com.sparta.ottoon.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final PostCustomRepositoryImpl postCustomRepositoryImpl;

    @Transactional
    public PostResponseDto save(PostRequestDto postRequestDto) {
        Long logInUserId = getLogInUserId();
        User user = getUserById(logInUserId);
        Post post = postRequestDto.toEntity();
        post.updateUser(user);
        post = postRepository.save(post);

        return PostResponseDto.toDto("게시글 등록 완료", 200, post);
    }

    @Transactional(readOnly = true)
    public PostResponseDto findById(long postId) {
        Post post = findPostById(postId);

        return PostResponseDto.toDto("부분 게시글 조회 완료", 200, post);
    }

    @Transactional(readOnly = true)

    public List<PostResponseDto> getAll(int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "isTop").and(Sort.by(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(page, 5, sort);
        Page<PostResponseDto> postPage = postRepository.findAll(pageable).map(post -> PostResponseDto.toDto("전체 게시글 조회 완료", 200, post));


        return postPage
                .stream()
                .toList();
    }

    @Transactional
    public PostResponseDto update(long postId, PostRequestDto postRequestDto) {
        Post post = findPostById(postId);
        Long logInUserId = getLogInUserId();
        User user = getUserById(logInUserId);

        // 본인 계정 혹은 관리자 계정이면 게시글 수정 가능
        if (logInUserId.equals(post.getUser().getId()) || user.getStatus().equals(UserStatus.ADMIN)) {
            post.update(postRequestDto.getContents());

            return PostResponseDto.toDto("게시글 수정 완료", 200, post);
        } else {

            throw new CustomException(ErrorCode.BAD_AUTH_PUT);

        }
    }

    @Transactional
    public void delete(long postId) {
        Post post = findPostById(postId);
        Long logInUserId = getLogInUserId();
        User user = getUserById(logInUserId);

        // 본인 계정 혹은 관리자 계정이면 게시글 삭제 가능
        if (logInUserId.equals(post.getUser().getId()) || user.getStatus().equals(UserStatus.ADMIN)) {
            postRepository.delete(post);
            PostResponseDto.toDeleteResponse("게시글 삭제 완료", 200);
        } else {
            throw new CustomException(ErrorCode.BAD_AUTH_DELETE);
        }
    }

    private Post findPostById(long postId) {
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

    public Page<PostResponseDto> findLikePost(UserDetails userDetails, int page) {
        Pageable pageable = PageRequest.of(page - 1, 5);

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        Page<Post> postList = postCustomRepositoryImpl.findAllILike(user.getId(), pageable);

        Page<PostResponseDto> postResponseDtos = postList
                .map(post -> PostResponseDto.toDto("좋아요 한 게시글 조회 완료", 200, post));

        return postResponseDtos;
    }


    public Page<PostResponseDto> findPostsFollow(UserDetails userDetails, int page) {
        Pageable pageable = PageRequest.of(page-1, 5);

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        Page<Post> postList = postCustomRepositoryImpl.findPostsFollow(user.getId(), pageable);

        Page<PostResponseDto> postResponseDtos =
                postList
                        .map(post -> PostResponseDto.toDto("팔로우한 유저의 게시글 조회 완료", 200, post));
        return postResponseDtos;
    }
}
