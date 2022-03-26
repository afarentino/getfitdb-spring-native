package com.github.afarentino.getfitdb;

import com.github.afarentino.getfitdb.model.ExerciseRecord;

import java.util.List;

public interface RecordRepository {
    List<ExerciseRecord> findAll();
}
