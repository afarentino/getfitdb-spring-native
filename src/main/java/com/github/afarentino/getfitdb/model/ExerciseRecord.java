package com.github.afarentino.getfitdb.model;

/**
 * DTO (data transfer object used to represent the date we need to persist to a database table)
 *
 * Note: Due to specifics of the JPA specs immutable Java ExerciseRecord
 * cannot be used as the Entity class used to persist data to a
 * relational database.
 *
 * An entity must follow these requirements:
 * 1) The Entity class needs to be non-final
 * 2) The Entity class needs to have a no-arg constructor that is either public or protected
 * 3) Then entity attributes must be non-final.
 */
public record ExerciseRecord(String start,
                             Double distance,
                             Double zoneTime,
                             Integer elapsedTime,
                             Integer caloriesBurned,
                             Integer avgHeartRate,
                             Integer maxHeartRate,
                             String notes) { }



