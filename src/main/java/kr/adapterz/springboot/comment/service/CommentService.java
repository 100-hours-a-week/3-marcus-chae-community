package kr.adapterz.springboot.comment.service;

import kr.adapterz.springboot.auth.exception.UnauthorizedException;
import kr.adapterz.springboot.comment.dto.CommentCreateRequest;
import kr.adapterz.springboot.comment.dto.CommentResponse;
import kr.adapterz.springboot.comment.dto.CommentUpdateRequest;
import kr.adapterz.springboot.comment.entity.Comment;
import kr.adapterz.springboot.comment.exception.CommentNotFoundException;
import kr.adapterz.springboot.comment.repository.CommentRepository;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.post.exception.PostNotFoundException;
import kr.adapterz.springboot.post.repository.PostRepository;
import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.exception.UserNotFoundException;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 게시글에 달린 댓글 조회. 추후 페이지네이션 기능 추가 요망
     *
     * @param postId
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    /**
     * 특정 게시글의 댓글 수 조회
     *
     * @param postId
     *
     * @return
     */
    @Transactional(readOnly = true)
    public long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    /**
     * 여러 게시글의 댓글 수를 배치로 조회 (N+1 문제 방지)
     *
     * @param postIds
     *
     * @return 게시글 ID를 키로, 댓글 수를 값으로 하는 Map
     */
    @Transactional(readOnly = true)
    public Map<Long, Long> findCommentCountsByPostIds(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Map.of();
        }

        return commentRepository.countByPostIdIn(postIds).stream()
                .collect(Collectors.toMap(
                        CommentRepository.CommentCountProjection::getPostId,
                        CommentRepository.CommentCountProjection::getCount
                ));
    }

    @Transactional
    public CommentResponse create(Long authorId, Long targetPostId, CommentCreateRequest request) {
        User author = userRepository.findById(authorId).orElseThrow(UserNotFoundException::new);
        Post targetPost = postRepository.findById(targetPostId).orElseThrow(PostNotFoundException::new);

        Comment comment = new Comment(author, targetPost, request.content());
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.from(savedComment);
    }

    @Transactional
    public CommentResponse update(Long requestorId, Long commentId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        validateCommentOwnership(comment, requestorId);

        comment.setContent(request.content());
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.from(savedComment);
    }

    @Transactional
    public void delete(Long requestorId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        validateCommentOwnership(comment, requestorId);

        commentRepository.deleteById(commentId);
    }

    /**
     * 댓글 소유자와 요청자가 일치하는지 검증
     *
     * @param comment 검증 대상 댓글
     * @param requestorId 요청자 ID
     * @throws UnauthorizedException 소유자가 아닌 경우
     */
    private void validateCommentOwnership(Comment comment, Long requestorId) {
        if (!Objects.equals(comment.getAuthor().getId(), requestorId)) {
            throw new UnauthorizedException();
        }
    }
}
