package com.example.ion_nuxt_back.service;
// model
import com.example.ion_nuxt_back.dto.users.request.editUserReqDTO;
import com.example.ion_nuxt_back.dto.users.response.UserAssetDTO;
import com.example.ion_nuxt_back.model.User;
// DTO.request
import com.example.ion_nuxt_back.dto.users.request.LogInUserReqDTO;
import com.example.ion_nuxt_back.dto.users.request.RegisterUserReqDTO;
// DTO.response
import com.example.ion_nuxt_back.dto.users.response.LogInUserResDTO;
import com.example.ion_nuxt_back.dto.users.response.UserInfoResDTO;
// common
import com.example.ion_nuxt_back.common.JwtSecret;
import com.example.ion_nuxt_back.common.ApiResponse;
// repository
import com.example.ion_nuxt_back.repository.UserRepository;
// springframework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
// util
import java.util.*;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private MongoTemplate mongoTemplate;

    // POST 新增使用者 註冊
    public ResponseEntity<ApiResponse<?>> registerUser( RegisterUserReqDTO request ) {
        try {
            String account = request.getAccount();
            String password = request.getPassword();
            // conditional: 帳號跟密碼 空值
            if ((account == null || account.trim().isEmpty()) || ( password == null || password.trim().isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("resource_is_Empty", 1004));
            }
            //  conditional: 檢查帳號是否重複
            if (userRepository.findByUuid(account).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("duplicate_account", 1005));
            }
            // user token 產生
            String accessToken = JwtSecret.generateAccessToken(account);
            String refreshToken = JwtSecret.generateRefreshToken(account);

            // 建立新使用者
            User user = new User();
            user.setAccount(account);
            user.setPassword(passwordEncoder.encode(password));
            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            user.setCreateTime(new Date());
            user.setNick("");
            user.setInfoImg("");
            user.setUuid(UUID.randomUUID().toString()); // 生成 UUID
            user.setRole("guest");
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    // POST 登入
    public ResponseEntity<ApiResponse<?>> logInUser( LogInUserReqDTO request ) {
        try {
            String account = request.getAccount();
            String password = request.getPassword();
            // conditional: 帳號值 判斷是有存在
            Optional<User> optionalUser = userRepository.findByAccount(account);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("account_error", 1007));
            }

            // conditional: 密碼值 判斷是否正確
            User userOptionalUser = optionalUser.get();
            if (!passwordEncoder.matches(password, userOptionalUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("password_error", 1006));
            }
            // 回傳 token
            LogInUserResDTO logInUserResDTO = new LogInUserResDTO(
                    userOptionalUser.getUuid(),
                    userOptionalUser.getAccessToken(),
                    userOptionalUser.getRefreshToken()
            );
            return ResponseEntity.ok(ApiResponse.success(logInUserResDTO));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    // GET 使用者info
    public ResponseEntity<ApiResponse<?>> getUserInfo( String uuid ) {
        try {
            // conditional: uuid值 判斷是有存在
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
                    userOptionalUser.getRefreshToken(),
                    userOptionalUser.getNick(),
                    userOptionalUser.getInfoImg()
            );
            return ResponseEntity.ok(ApiResponse.success(userInfoDTO));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    // POST 修改使用者
    public ResponseEntity<ApiResponse<?>> editUser(
            editUserReqDTO request,
            String userUUID
            ) {
        try {
            System.out.println(userUUID);
            String nick = request.getNick();
            String infoImg = request.getInfoImg();
            if (( userUUID == null || userUUID.trim().isEmpty())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("resource_is_Empty", 1004));
            }
            // conditional: uuid值 判斷是有存在
            Optional<User> optionalUser = userRepository.findByUuid(userUUID);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("account_error", 1007));
            }

            // === 用 MongoTemplate 做部份欄位更新 ===
            Query query = Query.query(Criteria.where("uuid").is(userUUID));

            // 如果 nick / infoImg 在 User 頂層，這樣寫：
            Update update = new Update()
                    .set("nick", nick)
                    .set("infoImg", infoImg);

            mongoTemplate.updateFirst(query, update, User.class);

            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    // GET 使用者info
    public ResponseEntity<ApiResponse<?>> getUserAsset( ) {
        try {
            // conditional: Role值 判斷是有存在
            Optional<User> optionalUser = userRepository.findByRole("admin");
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Role_is_Empty", 1001));
            }

            User userOptionalUser = optionalUser.get();
            UserAssetDTO userImgDTO = new UserAssetDTO(
                    userOptionalUser.getNick(),
                    userOptionalUser.getInfoImg()
            );
            return ResponseEntity.ok(ApiResponse.success(userImgDTO));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }
}

