package com.bringframework.definition;

import com.bringframework.util.BoboDefinitionUtil;
import lombok.*;

import java.lang.reflect.Constructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"boboName", "boboClass"})
public class BoboDefinition {
    private String boboName;
    private Class<?> boboClass;
    private String initMethodName;
    private String configurationBoboName;
    private String configurationMethodName;
    private Constructor<?> constructor;
    private Class<?>[] parameterTypes;

    public static BoboDefinition of(Class<?> boboClass, String boboName) {
        return BoboDefinitionUtil.buildDefinition(boboClass, boboName);
    }
}
