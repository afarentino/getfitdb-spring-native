package com.github.afarentino.getfitdb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.github.afarentino.getfitdb.model.ExerciseRecord;


public class RecordService {
    private final JdbcTemplate template;

    public RecordService(JdbcTemplate template) {
        this.template = template;
    }

    private final RowMapper<ExerciseRecord> rowMapper = (rs, rowNum) -> new ExerciseRecord(
            rs.getString("Start"),
            rs.getDouble("Distance"),
            rs.getDouble( "ZoneTime"),
            rs.getInt("ElapsedTime"),
            rs.getInt("CaloriesBurned"),
            rs.getInt("AvgHeartRate"),
            rs.getInt("MaxHeartRate"),
            rs.getString("Notes")
    );

    public List<ExerciseRecord> findAll() {
        String findAllRecords = """
                select * from records
                """;

        return template.query(findAllRecords, rowMapper);
    }

}
