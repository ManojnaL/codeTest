package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationService {

    @Autowired
    private final ReservationRepository reservationRepository;

    @Autowired
    private final ReservationMapper reservationMapper;

    @Autowired
    private final GuestRepository guestRepository;

    @Autowired
    private final ScheduleRepository scheduleRepository;

    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {

        Long scheduleID = createReservationRequestDTO.getScheduleId();
        Optional<Guest> guest = guestRepository.findById(createReservationRequestDTO.getGuestId());
        Optional<Schedule> schedule = scheduleRepository.findById(scheduleID);

        if(guest.isPresent() && schedule.isPresent()){

            this.validateSchedule(scheduleID);

            Reservation reservation = new Reservation();

            reservation.setReservationStatus(ReservationStatus.READY_TO_PLAY);
            reservation.setGuest(guest.get());
            reservation.setSchedule(schedule.get());
            reservation.setValue(BigDecimal.TEN);
            reservation.setRefundValue(BigDecimal.TEN);

            try {
                reservationRepository.save(reservation);
            }catch(Exception ex) {
                throw new EntityNotFoundException("Error occurred in saving into reservation database."+ex.getMessage());
            }
            schedule.get().addReservation(reservation);

            try{
                scheduleRepository.save(schedule.get());
            }catch(Exception ex) {
                throw new EntityNotFoundException("Error occurred in saving into schedule database."+ex.getMessage());
            }

            return reservationMapper.map(reservation);
        }

        String errorMessage = (!guest.isPresent())? "The GuestID is not valid" : "The scheduleID is not valid";
        throw new UnsupportedOperationException(errorMessage);
    }

    private void validateSchedule(Long scheduleID) {

        List<Reservation> scheduledLst = reservationRepository.findBySchedule_Id(scheduleID);
        if(!scheduledLst.isEmpty()) {
            scheduledLst.sort(Comparator.comparing(Reservation::getDateUpdate));
            Reservation lastestReservation = scheduledLst.get(scheduledLst.size() - 1);

            if (!ReservationStatus.READY_TO_PLAY.equals(lastestReservation.getReservationStatus())) {
                throw new IllegalArgumentException("This schedule is no longer available. Please choose a different slot.");
            }
        }
    }

    @SneakyThrows
    public ReservationDTO findReservation(Long reservationId) throws Exception {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    public ReservationDTO cancelReservation(Long reservationId) throws Exception {
        return reservationMapper.map(this.cancel(reservationId));
    }

    @SneakyThrows
    private Reservation cancel(Long reservationId) throws Exception{
        return reservationRepository.findById(reservationId).map(reservation -> {
            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

        }).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);
        try{
            return reservationRepository.save(reservation);
        }catch(Exception ex) {
            throw new EntityNotFoundException("Error occurred in saving into database."+ex.getMessage());
        }
    }

    private void validateCancellation(Reservation reservation) {
        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        BigDecimal refund = BigDecimal.ZERO;

        if (hours >= 24) {
            return reservation.getValue();
        }
        else if(hours >= 12){
            refund = reservation.getValue().multiply(new BigDecimal(0.75));
            return refund;
        }
        else if(hours >= 2){
            refund = reservation.getValue().multiply(new BigDecimal(0.5));
            return refund;
        }
        else{
            refund = reservation.getValue().multiply(new BigDecimal(0.25));
            return refund;
        }

    }

    /*TODO: This method actually not fully working, find a way to fix the issue when it's throwing the error:
            "Cannot reschedule to the same slot.*/
    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) throws Exception {

        Reservation previousReservation = cancel(previousReservationId);

        if (scheduleId.equals(previousReservation.getSchedule().getId())) {
            throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);

        try{
            reservationRepository.save(previousReservation);
        }catch(Exception ex) {
            throw new EntityNotFoundException("Error occurred in saving into database."+ex.getMessage());
        }

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId())
                .scheduleId(scheduleId)
                .build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;

    }
}
