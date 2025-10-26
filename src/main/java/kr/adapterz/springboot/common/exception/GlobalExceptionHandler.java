package kr.adapterz.springboot.common.exception;

import kr.adapterz.springboot.post.exception.PostNotFoundException;
import kr.adapterz.springboot.user.exception.EmailAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<String> handleUnauthenticated() {
        return ResponseEntity.status(401).body("인증이 필요합니다.");
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExists() {
        return ResponseEntity.status(409).body("이미 존재하는 이메일입니다.");
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFound() {
        return ResponseEntity.status(404).body("게시글을 찾을 수 없습니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("요청 형식이 잘못되었습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("예상치 못한 서버 오류 발생", ex);
        return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
    }

}
