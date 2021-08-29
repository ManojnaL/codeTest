package com.tenniscourts.guests;


import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface GuestMapper {

    Guest mapDtoToGuest(GuestDTO source);

    GuestDTO mapGuestToDTO(Guest source);

    List<GuestDTO> mapList(List<Guest> source);

}
