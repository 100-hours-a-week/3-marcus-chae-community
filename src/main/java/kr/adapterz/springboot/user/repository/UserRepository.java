package kr.adapterz.springboot.user.repository;

import jakarta.validation.constraints.Email;
import kr.adapterz.springboot.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(@Email String email);

    boolean existsByEmail(@Email String email);
}