package com.bringframework.definition;

import com.bringframework.util.BoboDefinitionUtil;
import lombok.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * A BoboDefinition describes a bobo instance (bobo metadata), which has bobo name, bobo type,
 * constructor, and further information needed for creating and configuring bobo instance
 *
 * @author Andrii Bobrov
 * @author Mykhailo Pysarenko
 * @since 3 november 2021
 */
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
    private Method configurationMethod;
    private Constructor<?> constructor;

    public static BoboDefinition of(Class<?> boboClass, String boboName) {
        return BoboDefinitionUtil.buildDefinition(boboClass, boboName);
    }
}
