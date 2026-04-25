package com.architecturedays.day015.antes;

public interface EntityRepository {
    <T> T save(T entity);
}
