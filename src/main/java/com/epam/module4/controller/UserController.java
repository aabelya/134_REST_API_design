package com.epam.module4.controller;

import com.epam.module4.controller.validation.OnCreate;
import com.epam.module4.controller.validation.OnManageAccess;
import com.epam.module4.controller.validation.OnUpdate;
import com.epam.module4.domain.Role;
import com.epam.module4.domain.User;
import com.epam.module4.dto.UserDto;
import com.epam.module4.service.DbUserDetailsService;
import com.epam.module4.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    PagedResourcesAssembler<User> pagedResourcesAssembler;

    RepresentationModelAssembler<User, UserDto> modelAssembler = this::toHypermediaDto;

    @GetMapping(value = "/hello")
    @ResponseBody
    public String greeting(HttpServletRequest request) {
        return "Authorized as " + request.getUserPrincipal().getName();
    }

    @PostMapping(path = "/register")
    public String register(HttpServletRequest request, @Validated(OnCreate.class) @ModelAttribute UserDto userDto) throws ServletException {
        if (request.getUserPrincipal() != null) {
            throw new RequestConflictException("can't register while logged in", "logout before registering");
        }
        User user = userService.createUser(userDto);
        request.login(user.getUsername(), userDto.getPassword());
        return "redirect:/users/hello";
    }

    @PostMapping(path = "/create", produces = {"application/hal+json"})
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserDto createUser(HttpServletRequest request, @Validated(OnCreate.class) @ModelAttribute UserDto userDto) {
        Optional.ofNullable(request.getUserPrincipal())
                .map(Principal::getName)
                .ifPresent(createdBy -> userDto.setCreatedBy(createdBy).setUpdatedBy(createdBy));
        return toHypermediaDto(userService.createUser(userDto));
    }

    @GetMapping(produces = {"application/json"})
    @ResponseBody
    public List<UserDto> getAllUsersV1() {
        return userService.getAllUsers().stream().map(this::toDto).toList();
    }

    @GetMapping(produces = {"application/hal+json"})
    @ResponseBody
    public CollectionModel<UserDto> getAllUsersV2(@Min(0) @RequestParam(value = "page", defaultValue = "0") int page,
                                                @Min(1) @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<User> allUsers = userService.getAllUsers(page, size);
        return pagedResourcesAssembler.toModel(allUsers, modelAssembler);
    }

    @GetMapping(value = "/{id}", produces = {"application/hal+json"})
    @ResponseBody
    public UserDto getUser(@PathVariable Integer id) {
        verifyIdMatchesOrHasAnyRole(id, Role.ADMIN, Role.OBSERVER);
        return toHypermediaDto(userService.getUser(id));
    }

    @GetMapping(value = "/by-name/{username}", produces = {"application/hal+json"})
    @ResponseBody
    public UserDto getUser(@Pattern(regexp = UserDto.USERNAME_REGEX) @PathVariable String username) {
        verifyNameMatchesOrHasAnyRole(username, Role.ADMIN, Role.OBSERVER);
        return toHypermediaDto(userService.getUser(username));
    }

    @PutMapping(value = "/{id}", produces = {"application/hal+json"})
    @ResponseBody
    public UserDto updateUser(HttpServletRequest request, @Min(1) @PathVariable Integer id, @Validated(OnUpdate.class) @ModelAttribute UserDto userDto) {
        verifyIdMatchesOrHasAnyRole(id, Role.ADMIN, Role.OBSERVER);
        Optional.ofNullable(request.getUserPrincipal())
                .map(Principal::getName)
                .ifPresent(createdBy -> userDto.setCreatedBy(createdBy).setUpdatedBy(createdBy));
        return toHypermediaDto(userService.updateUser(id, userDto));
    }

    @PutMapping(value = "/access/{id}", produces = {"application/hal+json"})
    @ResponseBody
    public UserDto updateUserAccess(HttpServletRequest request, @Min(1) @PathVariable Integer id, @Validated(OnManageAccess.class) @ModelAttribute UserDto userDto) {
        Optional.ofNullable(request.getUserPrincipal())
                .map(Principal::getName)
                .ifPresent(createdBy -> userDto.setCreatedBy(createdBy).setUpdatedBy(createdBy));
        return toHypermediaDto(userService.updateUser(id, userDto));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public String deleteUser(@Min(1) @PathVariable Integer id) {
        User user = userService.deleteUser(id);
        return "User " + user.getUsername() + " deleted";
    }

    private UserDto toDto(User user) {
        return mapper.convertValue(user, UserDto.class);
    }

    private UserDto toHypermediaDto(User user) {
        UserDto dto = toDto(user);
        dto.add(linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel());
        return dto;
    }

    private void verifyIdMatchesOrHasAnyRole(int id, Role... roles) {
        Function<Authentication, Integer> idExtractor = auth ->
                ((DbUserDetailsService.UserPrincipal) auth.getPrincipal()).getUserId();
        verifyAuthMatchesOrHasAnyRole(auth -> idExtractor.apply(auth).equals(id), roles);
    }

    private void verifyNameMatchesOrHasAnyRole(String username, Role... roles) {
        verifyAuthMatchesOrHasAnyRole(auth -> username.equals(auth.getName()), roles);
    }

    private void verifyAuthMatchesOrHasAnyRole(Predicate<Authentication> authMatch, Role... roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (authMatch.test(auth)) {
            return;
        }
        Set<String> roleList = Arrays.stream(roles).map(Role::name).map(r -> "ROLE_" + r).collect(Collectors.toSet());
        boolean hasRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roleList::contains);
        if (!hasRole) {
            throw new UserOperationNotAuthorizedException("user " + auth.getName() + " is not authorized to perform this operation");
        }
    }

}
