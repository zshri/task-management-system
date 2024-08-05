package org.example.taskmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.UserNotFoundException;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.model.dto.ResponseUserDto;
import org.example.taskmanagementsystem.repository.UserRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;

    public Page<ResponseUserDto> getUsersPage(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.findAll(pageable);

            List<ResponseUserDto> responseUserDTOList = userPage.getContent().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return new PageImpl<>(responseUserDTOList, pageable, userPage.getTotalElements());
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching users", e);
        }
    }

    public ResponseUserDto convertToDto(User user) {
        ResponseUserDto responseUserDto = new ResponseUserDto();
        responseUserDto.setId(user.getId());
        responseUserDto.setName(user.getUsername());
        return responseUserDto;
    }

    public ResponseUserDto getUserProfile(Long userId) throws UserNotFoundException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
            log.info("User profile: {}", user);
            return convertToDto(user);
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("An error occurred while fetching user profile", e);
            throw e;
        }

    }


}
