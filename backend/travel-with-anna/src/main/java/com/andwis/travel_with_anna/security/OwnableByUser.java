package com.andwis.travel_with_anna.security;

import com.andwis.travel_with_anna.user.User;

public interface OwnableByUser {
    User getOwner();
}
