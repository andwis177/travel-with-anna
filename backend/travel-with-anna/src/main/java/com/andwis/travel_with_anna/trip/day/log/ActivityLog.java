package com.andwis.travel_with_anna.trip.day.log;


import com.andwis.travel_with_anna.trip.day.Day;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Size(max = 60)
    @Column(name = "log", length = 60)
    private String log;

    @Size(max = 40)
    @Column(name = "type", length = 40)
    private String type;

    @ManyToOne
    @JoinColumn(name = "day_id")
    private Day day;
}
