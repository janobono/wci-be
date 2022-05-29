package sk.janobono.wci.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wci.api.service.so.UserDataSO;
import sk.janobono.wci.api.service.so.UserProfileSO;
import sk.janobono.wci.api.service.so.UserSO;
import sk.janobono.wci.common.exception.ApplicationExceptionCode;
import sk.janobono.wci.common.Authority;
import sk.janobono.wci.api.service.so.UserSearchCriteriaSO;
import sk.janobono.wci.component.ScDf;
import sk.janobono.wci.dal.domain.User;
import sk.janobono.wci.dal.repository.UserRepository;
import sk.janobono.wci.dal.specification.UserSpecification;
import sk.janobono.wci.util.RandomString;

import java.util.List;
import java.util.UUID;

@Service
public class UserApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserApiService.class);

    private PasswordEncoder passwordEncoder;

    private ScDf scDf;

    private UserRepository userRepository;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder, ScDf scDf) {
        this.passwordEncoder = passwordEncoder;
        this.scDf = scDf;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserSO> getUsers(Pageable pageable) {
        LOGGER.debug("getUsers({})", pageable);
        Page<UserSO> result = userRepository.findAll(pageable).map(UserSO::createUserSO);
        LOGGER.debug("getUsers({})={}", pageable, result);
        return result;
    }

    public Page<UserSO> getUsers(UserSearchCriteriaSO userSearchCriteriaSO, Pageable pageable) {
        LOGGER.debug("getUsers({},{})", userSearchCriteriaSO, pageable);
        Page<UserSO> result = userRepository.findAll(new UserSpecification(scDf, userSearchCriteriaSO), pageable).map(UserSO::createUserSO);
        LOGGER.debug("getUsers({},{})={}", userSearchCriteriaSO, pageable, result);
        return result;
    }

    public UserSO getUser(UUID id) {
        LOGGER.debug("getUser({})", id);
        User user = userRepository.findById(id).orElseThrow(
                () -> ApplicationExceptionCode.USER_NOT_FOUND.exception("User with id {0} not found.", id)
        );
        UserSO result = UserSO.createUserSO(user);
        LOGGER.debug("getUser({})={}", id, result);
        return result;
    }

    @Transactional
    public UserSO addUser(UserDataSO userDataSO) {
        LOGGER.debug("addUser({})", userDataSO);
        if (userRepository.existsByUsername(userDataSO.username().toLowerCase())) {
            throw ApplicationExceptionCode.USER_USERNAME_IS_USED.exception("Username is already taken.");
        }
        if (userRepository.existsByEmail(userDataSO.email().toLowerCase())) {
            throw ApplicationExceptionCode.USER_EMAIL_IS_USED.exception("Email is already taken.");
        }
        User user = new User();
        user.setUsername(userDataSO.username().toLowerCase());
        user.setPassword(passwordEncoder.encode(RandomString.INSTANCE().alphaNumeric(3, 2, 1).generate(6)));
        user.setTitleBefore(userDataSO.titleBefore());
        user.setFirstName(userDataSO.firstName());
        user.setMidName(userDataSO.midName());
        user.setLastName(userDataSO.lastName());
        user.setTitleAfter(userDataSO.titleAfter());
        user.setEmail(userDataSO.email().toLowerCase());
        user.setConfirmed(userDataSO.confirmed());
        user.setEnabled(userDataSO.enabled());
        user.setAuthorities(userDataSO.authorities());
        user = userRepository.save(user);
        UserSO result = UserSO.createUserSO(user);
        LOGGER.debug("addUser({})={}", userDataSO, result);
        return result;
    }

    @Transactional
    public UserSO setUser(UUID id, UserProfileSO userProfileSO) {
        LOGGER.debug("setUser({},{})", id, userProfileSO);
        User user = userRepository.findById(id).orElseThrow(
                () -> ApplicationExceptionCode.USER_NOT_FOUND.exception("User with id {0} not found.", id)
        );
        user.setTitleBefore(userProfileSO.titleBefore());
        user.setFirstName(userProfileSO.firstName());
        user.setMidName(userProfileSO.midName());
        user.setLastName(userProfileSO.lastName());
        user.setTitleAfter(userProfileSO.titleAfter());
        user = userRepository.save(user);
        UserSO result = UserSO.createUserSO(user);
        LOGGER.debug("setUser({},{})={}", id, userProfileSO, result);
        return result;
    }

    @Transactional
    public UserSO setAuthorities(UUID id, List<Authority> authorities) {
        LOGGER.debug("setAuthorities({},{})", id, authorities);
        User user = userRepository.findById(id).orElseThrow(
                () -> ApplicationExceptionCode.USER_NOT_FOUND.exception("User with id {0} not found.", id)
        );
        user.setAuthorities(authorities);
        user = userRepository.save(user);
        UserSO result = UserSO.createUserSO(user);
        LOGGER.debug("setAuthorities({},{})={}", id, authorities, result);
        return result;
    }

    @Transactional
    public UserSO setConfirmed(UUID id, Boolean confirmed) {
        LOGGER.debug("setConfirmed({},{})", id, confirmed);
        User user = userRepository.findById(id).orElseThrow(
                () -> ApplicationExceptionCode.USER_NOT_FOUND.exception("User with id {0} not found.", id)
        );
        user.setConfirmed(confirmed);
        user = userRepository.save(user);
        UserSO result = UserSO.createUserSO(user);
        LOGGER.debug("setConfirmed({},{})={}", id, confirmed, result);
        return result;
    }

    @Transactional
    public UserSO setEnabled(UUID id, Boolean enabled) {
        LOGGER.debug("setEnabled({},{})", id, enabled);
        User user = userRepository.findById(id).orElseThrow(
                () -> ApplicationExceptionCode.USER_NOT_FOUND.exception("User with id {0} not found.", id)
        );
        user.setEnabled(enabled);
        user = userRepository.save(user);
        UserSO result = UserSO.createUserSO(user);
        LOGGER.debug("setEnabled({},{})={}", id, enabled, result);
        return result;
    }

    @Transactional
    public void deleteUser(UUID id) {
        LOGGER.debug("deleteUser({})", id);
        if (!userRepository.existsById(id)) {
            throw ApplicationExceptionCode.USER_NOT_FOUND.exception("User with id {0} not found.", id);
        }
        userRepository.deleteById(id);
    }
}
