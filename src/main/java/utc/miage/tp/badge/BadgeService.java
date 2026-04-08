package utc.miage.tp.badge;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BadgeService {

  private final BadgeRepository badgeRepository;

  public BadgeService(BadgeRepository badgeRepository) {
    this.badgeRepository = badgeRepository;
  }

  @Transactional(readOnly = true)
  public List<Badge> getAll() {
    return badgeRepository.findAll();
  }
}
