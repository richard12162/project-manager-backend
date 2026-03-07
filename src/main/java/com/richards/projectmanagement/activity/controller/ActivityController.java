package com.richards.projectmanagement.activity.controller;

import com.richards.projectmanagement.activity.dto.ActivityLogResponse;
import com.richards.projectmanagement.activity.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/activity")
public class ActivityController {

    private final ActivityLogService activityLogService;

    public ActivityController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public ResponseEntity<List<ActivityLogResponse>> getProjectActivity(
            @PathVariable UUID projectId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(activityLogService.getProjectActivity(projectId, authentication));
    }
}