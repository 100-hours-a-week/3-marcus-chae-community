package kr.adapterz.springboot.common.exception;

import kr.adapterz.springboot.auth.exception.InvalidCredentialsException;
import kr.adapterz.springboot.auth.exception.UnauthenticatedException;
import kr.adapterz.springboot.auth.exception.UnauthorizedException;
import kr.adapterz.springboot.comment.exception.CommentNotFoundException;
import kr.adapterz.springboot.post.exception.PostNotFoundException;
import kr.adapterz.springboot.user.exception.EmailAlreadyExistsException;
import kr.adapterz.springboot.user.exception.InvalidPasswordException;
import kr.adapterz.springboot.user.exception.NicknameAlreadyExistsException;
import kr.adapterz.springboot.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<String> handleUnauthenticated() {
        return ResponseEntity.status(401).body("인증이 필요합니다.");
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials() {
        return ResponseEntity.status(401).body("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized() {
        return ResponseEntity.status(403).body("권한이 없습니다.");
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExists() {
        return ResponseEntity.status(409).body("이미 존재하는 이메일입니다.");
    }

    @ExceptionHandler(NicknameAlreadyExistsException.class)
    public ResponseEntity<String> handleNicknameAlreadyExists() {
        return ResponseEntity.status(409).body("이미 존재하는 닉네임입니다.");
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFound() {
        return ResponseEntity.status(404).body("게시글을 찾을 수 없습니다.");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound() {
        return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFound() {
        return ResponseEntity.status(404).body("댓글을 찾을 수 없습니다.");
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPassword() {
        return ResponseEntity.status(400).body("기존 비밀번호가 일치하지 않습니다.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("'%s' 메서드는 지원하지 않습니다.", ex.getMethod());
        return ResponseEntity.status(405).body(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("요청 본문 파싱 오류: {}", ex.getMessage());
        return ResponseEntity.badRequest().body("요청 본문이 올바르지 않습니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        // validation의 필드 에러 메시지 전부 그대로 전달
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String typeName = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "알 수 없음";
        String message = String.format("잘못된 요청입니다. '%s' 파라미터는 %s 타입이어야 합니다.", paramName, typeName);
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("예상치 못한 서버 오류 발생", ex);
        return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
    }

}
