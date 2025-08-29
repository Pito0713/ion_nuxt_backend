package com.example.ion_nuxt_back.service;
// model
import com.example.ion_nuxt_back.common.JwtSecret;
import com.example.ion_nuxt_back.dto.users.request.LogInUserReqDTO;
import com.example.ion_nuxt_back.dto.users.request.RefreshTokenReqDTO;
import com.example.ion_nuxt_back.dto.users.request.RegisterUserReqDTO;
import com.example.ion_nuxt_back.dto.users.request.userPasswordChangeReqDTO;
import com.example.ion_nuxt_back.dto.users.response.LogInUserResDTO;
import com.example.ion_nuxt_back.dto.users.response.UserInfoResDTO;
import com.example.ion_nuxt_back.model.User;
import com.example.ion_nuxt_back.repository.UserRepository;
import com.example.ion_nuxt_back.common.ApiResponse;

import io.jsonwebtoken.Claims;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.*;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private MongoTemplate mongoTemplate;

    public ResponseEntity<ApiResponse<?>> registerUser( RegisterUserReqDTO request ) {
        try {
            String account = request.getAccount();
            String password = request.getPassword();
            // conditional: 帳號跟密碼 空值
            if ((account == null || account.trim().isEmpty()) || ( password == null || password.trim().isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("resource_is_Empty", 1004));
            }
            // 檢查帳號是否重複
            if (userRepository.findByUuid(account).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("duplicate_account", 1005));
            }
            // token 產生
            String accessToken = JwtSecret.generateAccessToken(account);
            String refreshToken = JwtSecret.generateRefreshToken(account);
            // 建立新使用者
            User user = new User();
            user.setAccount(account);
            user.setPassword(passwordEncoder.encode(password));
            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            user.setCreateTime(new Date());
            user.setUuid(UUID.randomUUID().toString());
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    public ResponseEntity<ApiResponse<?>> logInUser( LogInUserReqDTO request ) {
        try {
            String account = request.getAccount();
            String password = request.getPassword();
            Optional<User> optionalUser = userRepository.findByAccount(account);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("account_error", 1007));
            }

            User userOptionalUser = optionalUser.get();

            if (!passwordEncoder.matches(password, userOptionalUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("password_error", 1006));
            }
            String accessToken = JwtSecret.generateAccessToken(account);
            String refreshToken = JwtSecret.generateRefreshToken(account);
            userOptionalUser.setAccessToken(accessToken);
            userOptionalUser.setRefreshToken(refreshToken);
            userRepository.save(userOptionalUser);

            LogInUserResDTO logInUserResDTO = new LogInUserResDTO(
                    userOptionalUser.getUuid(),
                    accessToken,
                    refreshToken
            );

            return ResponseEntity.ok(ApiResponse.success(logInUserResDTO));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    public ResponseEntity<ApiResponse<?>> getUserInfo( String uuid ) {
        try {
            Optional<User> optionalUser = userRepository.findByUuid(uuid);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("resource_is_Empty", 1004));
            }
            User userOptionalUser = optionalUser.get();

            UserInfoResDTO userInfoDTO = new UserInfoResDTO(
                    userOptionalUser.getUuid(),
                    userOptionalUser.getAccount(),
                    userOptionalUser.getAccessToken(),
                    userOptionalUser.getRefreshToken()
            );
            return ResponseEntity.ok(ApiResponse.success(userInfoDTO));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    public  ResponseEntity<ApiResponse<?>> userPasswordChange(
            String userToken,
            userPasswordChangeReqDTO request
    ) {
        Claims claims = JwtSecret.parseToken(userToken);
        String account = claims.getSubject();
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("unauthorized", 1002));
        }

        Optional<User> optionalUser = userRepository.findByAccount(account);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("account_error", 1007));
        }
        User userOptionalUser = optionalUser.get();
        if (!passwordEncoder.matches(request.getOldPassword(), userOptionalUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("password_error", 10067));
        }

        if(request.getNewPassword().equals(request.getOldPassword())){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("password_the_same", 10069));
        }

        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("confirm_password_error", 10069));
        }

        String id = userOptionalUser.getId();
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        Update update = new Update()
                .set("password", passwordEncoder.encode(request.getNewPassword()));
        mongoTemplate.updateFirst(query, update, User.class);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    public ResponseEntity<ApiResponse<?>> refreshToken (RefreshTokenReqDTO request) {
        try {
            Claims claims = JwtSecret.parseToken(request.getRefreshToken());
            String account = claims.getSubject();
            if (account == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("unauthorized", 1996));

            }
            Optional<User> optionalUser = userRepository.findByAccount(account);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("account_error", 1997));
            }

            User user = optionalUser.get();
            String newAccessToken = JwtSecret.generateAccessToken(account);
            String newRefreshToken = JwtSecret.generateRefreshToken(account);
            user.setAccessToken(newAccessToken);
            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);

            LogInUserResDTO logInUserResDTO = new LogInUserResDTO(
                    user.getUuid(),
                    newAccessToken,
                    newRefreshToken
            );
            return ResponseEntity.ok(ApiResponse.success(logInUserResDTO));
        } catch ( Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("unauthorized", 1996));
        }
    }
}

