package gyc.admin.controller;

import java.util.List;

import gyc.admin.dto.ConsumerActivityRow;
import gyc.admin.repository.ConsumerActivityViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Read-only. Backs the "Consumer Activity" mirror panel — the reverse-direction fold. */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    private final ConsumerActivityViewRepository repository;

    @Autowired
    public DashboardController(ConsumerActivityViewRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/consumer-activity")
    public List<ConsumerActivityRow> consumerActivity() {
        return repository.findAllByOrderByRecordedAtDesc().stream().map(ConsumerActivityRow::from).toList();
    }
}
