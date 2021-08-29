package com.tenniscourts.reservations;

import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ReservationService.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private ScheduleRepository scheduleRepository;


    @InjectMocks
    ReservationService reservationService;

    @Test
    public void getRefundValueFullRefund() {

        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(2);

        schedule.setStartDateTime(startDateTime);

        Assert.assertEquals(reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()), new BigDecimal(10));

    }

    @Test
    public void getRefundValueTwentyFivePercent() {

        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(1L);

        schedule.setStartDateTime(startDateTime);

        Assert.assertEquals(new BigDecimal(2.50) , reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()).stripTrailingZeros() );

    }

    @Test
    public void getRefundValueFiftyPercent() {

        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(6L);

        schedule.setStartDateTime(startDateTime);

        Assert.assertEquals(new BigDecimal(5.0), reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()).stripTrailingZeros() );

    }

    @Test
    public void getRefundValueSeventyFiftyPercent() {

        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(20L);

        schedule.setStartDateTime(startDateTime);

        Assert.assertEquals(new BigDecimal(7.50) , reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()).stripTrailingZeros());

    }



}