package com.tenniscourts.guests;


import com.tenniscourts.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class GuestService {

    @Autowired
    private final GuestMapper guestMapper;

    @Autowired
    private final GuestRepository guestRepository;


    public GuestDTO createGuest(GuestDTO guestDTO) {
        try {
            return guestMapper.mapGuestToDTO(guestRepository.saveAndFlush(guestMapper.mapDtoToGuest(guestDTO)));
        }catch(Exception ex) {
            throw new EntityNotFoundException("Error occurred in saving into database."+ex.getMessage());
        }
    }

    @SneakyThrows
    public GuestDTO findGuestById(Long id) {

        return guestRepository.findById(id).map(guestMapper::mapGuestToDTO).orElseThrow(() -> {
            throw new EntityNotFoundException("Guest not found.");
        });

    }

    @SneakyThrows
    public List<GuestDTO> findGuestByName(String name) {

        List<GuestDTO> lstGuestDTO = guestRepository.findByName(name).stream().map(guestMapper::mapGuestToDTO).collect(Collectors.toList());
        if(lstGuestDTO.isEmpty()) {
            throw new EntityNotFoundException("Guest not found.");
       }
        return lstGuestDTO;
    }

    @SneakyThrows
    public List<GuestDTO> findAllGuest() {
        List<GuestDTO> lstGuestDTO = guestRepository.findAll().stream().map(guestMapper::mapGuestToDTO).collect(Collectors.toList());
        if(lstGuestDTO.isEmpty()) {
            throw new EntityNotFoundException("Guest not found.");
        }
        return lstGuestDTO;
    }

    @SneakyThrows
    public GuestDTO updateGuestName(GuestDTO guestDTO) {

        try{
            return guestRepository.findById(guestDTO.getId()).map(guest -> {

                guest.setName(guestDTO.getName());
                guestRepository.save(guest);
                return guestDTO;

            }).orElseThrow(() -> {
                throw new EntityNotFoundException("Guest not found.");
            });
        }catch(Exception ex) {
            throw new EntityNotFoundException("Error occurred in saving into database."+ex.getMessage());
        }

    }

    @SneakyThrows
    public GuestDTO deleteGuest(Long id) {
        try{
            return guestRepository.findById(id).map(guest -> {

                guestRepository.delete(guest);
                return guestMapper.mapGuestToDTO(guest);

            }).orElseThrow(() -> {
                throw new EntityNotFoundException("Guest not found.");
            });
        }catch(Exception ex) {
            throw new EntityNotFoundException("Error occurred in saving into database."+ex.getMessage());
        }

    }

}
