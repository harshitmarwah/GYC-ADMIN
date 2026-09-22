package gyc.admin.controller;

import java.util.List;

import gyc.admin.dto.ConfigUpdateRequest;
import gyc.admin.dto.RecordRequest;
import gyc.admin.dto.RecordResponse;
import gyc.admin.dto.SuspendUserRequest;
import gyc.admin.service.RecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** All synchronous, blocking REST — Admin's whole story end to end. */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class RecordController {

    private final RecordService recordService;

    @Autowired
    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping("/records")
    public ResponseEntity<RecordResponse> createRecord(@Valid @RequestBody RecordRequest request) {
        var saved = recordService.createRecord(request.title(), request.body());
        return ResponseEntity.status(HttpStatus.CREATED).body(RecordResponse.from(saved));
    }

    @PutMapping("/records/{id}")
    public RecordResponse updateRecord(@PathVariable Long id, @Valid @RequestBody RecordRequest request) {
        var saved = recordService.updateRecord(id, request.title(), request.body());
        return RecordResponse.from(saved);
    }

    @GetMapping("/records")
    public List<RecordResponse> listRecords() {
        return recordService.listRecords().stream().map(RecordResponse::from).toList();
    }

    @PostMapping("/users/{id}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable("id") String userId,
                                             @Valid @RequestBody SuspendUserRequest request) {
        recordService.suspendUser(userId, request.suspended(), request.reason());
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/config")
    public ResponseEntity<Void> updateConfig(@Valid @RequestBody ConfigUpdateRequest request) {
        recordService.updateConfig(request.key(), request.value());
        return ResponseEntity.ok().build();
    }
}
