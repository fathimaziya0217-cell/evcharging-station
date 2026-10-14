package com.evcharging.security;

import com.evcharging.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {

    public static Optional<CustomUserDetails> getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails);
        }
        return Optional.empty();
    }

    public static Optional<Long> getCurrentUserId() {
        return getCurrentUserDetails().map(CustomUserDetails::getId);
    }

    public static Optional<User> getCurrentUser() {
        return getCurrentUserDetails().map(CustomUserDetails::getUser);
    }

    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String && "anonymousUser".equals(auth.getPrincipal()));
    }

    public static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
