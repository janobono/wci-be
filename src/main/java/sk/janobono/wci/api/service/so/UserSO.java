package sk.janobono.wci.api.service.so;

import sk.janobono.wci.common.Authority;
import sk.janobono.wci.dal.domain.User;

import java.util.List;
import java.util.UUID;

public record UserSO(
        UUID id,
        String username,
        String titleBefore,
        String firstName,
        String midName,
        String lastName,
        String titleAfter,
        String email,
        Boolean gdpr,
        Boolean confirmed,
        Boolean enabled,
        List<Authority> authorities
) {
    public static UserSO createUserSO(User user) {
        return new UserSO(
                user.getId(), user.getUsername(), user.getTitleBefore(), user.getFirstName(), user.getMidName(),
                user.getLastName(), user.getTitleAfter(), user.getEmail(), user.getGdpr(), user.getConfirmed(),
                user.getEnabled(), user.getAuthorities()
        );
    }
}
