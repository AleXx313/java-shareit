package ru.practicum.shareit.util.valid;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<StartBeforeEndDateValid, BookItemRequestDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookItemRequestDto requestDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = requestDto.getStart();
        LocalDateTime end = requestDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
