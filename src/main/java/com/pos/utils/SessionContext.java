package com.pos.utils;

import com.pos.models.User;

public final class SessionContext {
    private static User currentUser;

    private SessionContext() {
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        SessionContext.currentUser = currentUser;
    }
}
