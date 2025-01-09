package com.andwis.travel_with_anna.api.country;

import java.util.List;

public interface EntityResponse<T> {
    List<T> getEntities();
}
