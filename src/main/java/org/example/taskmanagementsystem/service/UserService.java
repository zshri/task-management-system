package org.example.taskmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.UserNotFoundException;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.model.dto.ResponseUserDto;
import org.example.taskmanagementsystem.repository.UserRepository;
import org.example.taskmanagementsystem.util.UserMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с пользователями.
 * Предоставляет методы для получения информации о пользователях,
 * включая постраничное получение списка пользователей и профиль пользователя.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    /**
     * Получает страницу с пользователями.
     *
     * @param page номер страницы
     * @param size размер страницы
     * @return страница с пользователями в формате DTO
     * @throws RuntimeException если произошла ошибка при получении пользователей
     */
    public Page<ResponseUserDto> getUsersPage(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.findAll(pageable);

            List<ResponseUserDto> responseUserDTOList = userPage.getContent().stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());

            return new PageImpl<>(responseUserDTOList, pageable, userPage.getTotalElements());
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching users", e);
        }
    }

    /**
     * Получает профиль пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return DTO профиля пользователя
     * @throws UserNotFoundException если пользователь не найден
     * @throws Exception если произошла ошибка при получении профиля пользователя
     */
    public ResponseUserDto getUserProfile(Long userId) throws UserNotFoundException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
            log.info("User profile: {}", user);
            return userMapper.toDto(user);
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("An error occurred while fetching user profile", e);
            throw e;
        }

    }

}
