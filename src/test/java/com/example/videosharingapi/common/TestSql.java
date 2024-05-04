package com.example.videosharingapi.common;

import org.springframework.test.context.jdbc.Sql;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Sql(
        scripts = "/sql/data-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        config = @org.springframework.test.context.jdbc.SqlConfig(commentPrefix = "#")
)
@Sql(
        scripts = "/sql/clean-h2.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS,
        config = @org.springframework.test.context.jdbc.SqlConfig(commentPrefix = "#")
)
public @interface TestSql {
}