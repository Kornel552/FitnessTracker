package pl.wsb.fitnesstracker.user.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserDto;
import pl.wsb.fitnesstracker.user.api.UserProvider;
import pl.wsb.fitnesstracker.user.api.UserService;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;
    private final UserProvider userProvider;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {

        User user = userMapper.toUser(userDto);

        User savedUser = userService.createUser(user);

        return userMapper.toUserDto(savedUser);
    }

    @GetMapping
    public List<UserDto> getUsers() {

        return userProvider.findAllUsers().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {

        User user = userProvider.getUser(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return userMapper.toUserDto(user);
    }

    @GetMapping("/search")
    public UserDto getUserByEmail(@RequestParam String email) {

        User user = userProvider.getUserByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return userMapper.toUserDto(user);
    }
}