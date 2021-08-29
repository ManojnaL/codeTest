package com.tenniscourts.guests;


import com.tenniscourts.config.BaseRestController;
import com.tenniscourts.reservations.CreateReservationRequestDTO;
import com.tenniscourts.reservations.ReservationDTO;
import com.tenniscourts.reservations.ReservationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestControllerAdvice
@RestController
@RequestMapping("/api/guest/")
public class GuestController extends BaseRestController {

    @Autowired
    private final GuestService guestService;

    @ApiOperation(value = "Book a reservation.")
    @ApiResponse(code = 201, message = "Your reservation is confirmed.")
    @PostMapping
    public ResponseEntity<Void> createGuest(@RequestBody GuestDTO guest) {
        return ResponseEntity.created(locationByEntity(guestService.createGuest(guest).getId())).build();
    }

    @ApiOperation(value = "Fetches Guest details with the provided ID.")
    @ApiResponse(code = 200, message = "")
    @GetMapping("id/{id}")
    public ResponseEntity<GuestDTO> findGuestById(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(guestService.findGuestById(id));
    }

    @ApiOperation(value = "Fetches Guest details with the provided Name.")
    @ApiResponse(code = 200, message = "")
    @GetMapping("name/{name}")
    public ResponseEntity<List<GuestDTO>> findGuestByName(@PathVariable String name) throws Exception {
        return ResponseEntity.ok(guestService.findGuestByName(name));
    }

    @ApiOperation(value = "Fetches all the guest details.")
    @ApiResponse(code = 200, message = "")
    @GetMapping
    public ResponseEntity<List<GuestDTO>> findAllGuest() throws Exception {
        return ResponseEntity.ok(guestService.findAllGuest());
    }

    @ApiOperation(value = "Updates Guest's name.")
    @ApiResponse(code = 200, message = "")
    @PutMapping
    public ResponseEntity<GuestDTO> updateGuestName(@RequestBody GuestDTO guestDTO) throws Exception {
        return ResponseEntity.ok(guestService.updateGuestName(guestDTO));
    }

    @ApiOperation(value = "Deletes Guest with the provided ID.")
    @ApiResponse(code = 200, message = "")
    @DeleteMapping("{id}")
    public ResponseEntity<GuestDTO> deleteGuest(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(guestService.deleteGuest(id));
    }


}
