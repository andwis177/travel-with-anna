package com.andwis.travel_with_anna.trip.expanse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpanseService {
    private final ExpanseRepository expanseRepository;

    public void saveExpanse(Expanse expanse) {
        expanseRepository.save(expanse);
    }
}
