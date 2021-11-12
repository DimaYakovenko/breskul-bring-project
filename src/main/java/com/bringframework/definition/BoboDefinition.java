package com.bringframework.definition;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"boboName", "boboClass"})
public class BoboDefinition {
    private String boboName;
    private Class<?> boboClass;
    private String initMethodName;

    public static BoboDefinition of(Class<?> boboClass, String boboName) {
        return BoboDefinition.builder().boboClass(boboClass).boboName(boboName).build();
    }
}
