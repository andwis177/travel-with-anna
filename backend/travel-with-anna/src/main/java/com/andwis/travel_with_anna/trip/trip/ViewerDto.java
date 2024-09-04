package com.andwis.travel_with_anna.trip.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewerDto {
    private Long viewerId;
    private String userName;
    private byte[] cover;


}
