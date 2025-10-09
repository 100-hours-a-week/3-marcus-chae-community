package kr.adapterz.springboot.user.controller;

import kr.adapterz.springboot.entity.User;
import kr.adapterz.springboot.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id) {
        User user = userService.getBasicById(id);
        return "유저 " + id + "번 조회 성공." + "닉네임: " + user.getNickname();
    }
}
