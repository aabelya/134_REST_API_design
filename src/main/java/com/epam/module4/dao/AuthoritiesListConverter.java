package com.epam.module4.dao;

import com.epam.module4.domain.Authority;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Converter
public class AuthoritiesListConverter<T> implements AttributeConverter<List<Authority>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<Authority> authorities) {
        return authorities != null ? authorities.stream().distinct().map(Enum::name).collect(Collectors.joining(SPLIT_CHAR)) : "";
    }

    @Override
    public List<Authority> convertToEntityAttribute(String string) {
        return string != null ? Arrays.stream(string.split(SPLIT_CHAR))
                .map(Authority::valueOf).collect(Collectors.toList()) : emptyList();
    }

}

