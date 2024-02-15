package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.Location;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class EventMapper {
    @Autowired
    private CommentMapper commentMapper;

    public Event newEventDtoToEvent(NewEventDto eventDto, User user, Category category) {
        return Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .category(category)
                .state(EventState.PENDING)
                .createdOn(LocalDateTime.now())
                .eventDate(eventDto.getEventDate())
                .publishedOn(null)
                .latitude(eventDto.getLocation().getLat())
                .longitude(eventDto.getLocation().getLon())
                .initiator(user)
                .participantLimit(eventDto.getParticipantLimit())
                .paid(Objects.requireNonNullElse(eventDto.getPaid(), false))
                .requestModeration(eventDto.getRequestModeration())
                .build();
    }

    public EventFullDto eventToEventFullDto(Event event, Long confirmedRequests, Long views, Long numberOfComments) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.INSTANCE.categoryToCategoryDto(event.getCategory()))
                .confirmedRequests(Objects.requireNonNullElse(confirmedRequests, 0L))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.INSTANCE.userToUserShortDtoResponse(event.getInitiator()))
                .location(new Location(event.getLatitude(), event.getLongitude()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(Objects.requireNonNullElse(views, 0L))
                .numberOfComments(numberOfComments)
                .build();
    }

    public EventShortDto eventToEventShortDto(Event event, Long confirmedRequests, Long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.INSTANCE.categoryToCategoryDto(event.getCategory()))
                .confirmedRequests(Objects.requireNonNullElse(confirmedRequests, 0L))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.INSTANCE.userToUserShortDtoResponse(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(Objects.requireNonNullElse(views, 0L))
                .build();
    }

    @Mapping(source = "category", target = "category")
    @Mapping(source = "eventDto.location.lat", target = "latitude")
    @Mapping(source = "eventDto.location.lon", target = "longitude")
    @Mapping(ignore = true, target = "id")
    public abstract void updateFromDto(@MappingTarget Event event, UpdateEventUserRequest eventDto, Category category);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "eventDto.location.lat", target = "latitude")
    @Mapping(source = "eventDto.location.lon", target = "longitude")
    @Mapping(ignore = true, target = "id")
    public abstract void updateFromDto(@MappingTarget Event event, UpdateEventAdminRequest eventDto, Category category);
}