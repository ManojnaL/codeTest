package com.tenniscourts.guests;

import com.tenniscourts.reservations.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  GuestRepository  extends JpaRepository<Guest, Long> {


    List<Guest> findAllById(Long id);

    List<Guest> findByName(String name);
}
