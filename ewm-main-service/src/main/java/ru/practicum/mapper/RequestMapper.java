package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class RequestMapper {

    @Mapping(source = "participationRequest.event.id", target = "event")
    @Mapping(source = "participationRequest.requester.id", target = "requester")
    public ParticipationRequestDto participationRequestToRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent().getId())
                .requester(participationRequest.getRequester().getId())
                .status(participationRequest.getStatus())
                .build();
    }

    public abstract List<ParticipationRequestDto> participationRequestToRequestDto(List<ParticipationRequest> participationRequest);

    public EventRequestStatusUpdateResult requestsToEventRequestStatusUpdateResult(List<ParticipationRequest> requests) {
        List<ParticipationRequestDto> requestDtos = participationRequestToRequestDto(requests);
        List<ParticipationRequestDto> confirmedRequests = requestDtos.stream()
                .filter(r -> RequestStatus.CONFIRMED.equals(r.getStatus()))
                .collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedRequests = requestDtos.stream()
                .filter(r -> RequestStatus.REJECTED.equals(r.getStatus()))
                .collect(Collectors.toList());
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}
