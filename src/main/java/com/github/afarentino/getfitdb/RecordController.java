package com.github.afarentino.getfitdb;

import com.github.afarentino.getfitdb.model.ExerciseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * In Spring a RestController annotation is equivalent to writing a @Controller class which has
 * @ResponseBody annotated on every single method.
 *
 * Therefore by default RestControllers return JSON/XML, instead of HTML.
 */
@RestController
public class RecordController {
    private final RecordService recordService;

    @Autowired
    public RecordController(RecordService service) {
        this.recordService = service;
    }

    @GetMapping("/records")
    public List<ExerciseRecord> records() {
        return getList();
    }

    private List<ExerciseRecord> getList() {
        ExerciseRecord rec = new ExerciseRecord("1/2/2022",
                1.22,
                1.55,
                    12,
                15,
                122,
                200,
                "This is a note"
                );
        List<ExerciseRecord> list = new ArrayList<>();
        //list.add(rec);

        list = recordService.findAll();
        return list;
    }
}
