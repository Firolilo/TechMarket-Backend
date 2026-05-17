package com.techmarket.techmarket.ambassadors.api.mobile;

import com.techmarket.techmarket.ambassadors.api.mobile.response.ActivityItemResponse;
import com.techmarket.techmarket.ambassadors.api.mobile.response.DailyActivityResponse;
import com.techmarket.techmarket.ambassadors.api.mobile.response.DashboardStatsResponse;
import com.techmarket.techmarket.ambassadors.api.mobile.response.ReferredBusinessResponse;
import com.techmarket.techmarket.ambassadors.application.service.DashboardApplicationService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ambassadors/me")
public class DashboardController {

    private final DashboardApplicationService service;

    public DashboardController(DashboardApplicationService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public DashboardStatsResponse getStats(Authentication auth) {
        return service.getStats(userIdFrom(auth));
    }

    @GetMapping("/activity")
    public List<ActivityItemResponse> getActivity(
            Authentication auth, @RequestParam(defaultValue = "10") int limit) {
        return service.getRecentActivity(userIdFrom(auth), limit);
    }

    @GetMapping("/weekly-activity")
    public List<DailyActivityResponse> getWeeklyActivity(
            Authentication auth,
            @RequestParam(required = false, defaultValue = "semana") String periodo) {
        return service.getWeeklyActivity(userIdFrom(auth), periodo);
    }

    @GetMapping("/referred-businesses")
    public List<ReferredBusinessResponse> getReferredBusinesses(Authentication auth) {
        return service.getReferredBusinesses(userIdFrom(auth));
    }

    private UUID userIdFrom(Authentication auth) {
        return UUID.fromString((String) auth.getPrincipal());
    }
}
