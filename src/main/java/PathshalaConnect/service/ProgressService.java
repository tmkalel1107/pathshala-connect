package PathshalaConnect.service;

import PathshalaConnect.entity.ProgressUpdate;
import PathshalaConnect.entity.School;
import PathshalaConnect.repository.ProgressUpdateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProgressService {

    private final ProgressUpdateRepository progressUpdateRepository;

    public ProgressService(ProgressUpdateRepository progressUpdateRepository) {
        this.progressUpdateRepository = progressUpdateRepository;
    }

    @Transactional(readOnly = true)
    public List<ProgressUpdate> getAllUpdates() {
        return progressUpdateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ProgressUpdate get(Long id) {
        if (id == null) return null;
        return progressUpdateRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<ProgressUpdate> getUpdateById(Long id) {
        return progressUpdateRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ProgressUpdate> bySchool(School school) {
        if (school == null) return List.of();
        return progressUpdateRepository.findBySchoolOrderByCreatedAtDesc(school);
    }

    @Transactional(readOnly = true)
    public List<ProgressUpdate> getUpdatesBySchoolId(Long schoolId) {
        if (schoolId == null) return List.of();
        return progressUpdateRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId);
    }

    @Transactional(readOnly = true)
    public List<ProgressUpdate> latest() {
        return progressUpdateRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public ProgressUpdate save(ProgressUpdate progressUpdate) {
        if (progressUpdate.getCreatedAt() == null) {
            progressUpdate.setCreatedAt(LocalDateTime.now());
        }
        return progressUpdateRepository.save(progressUpdate);
    }

    public ProgressUpdate saveUpdate(ProgressUpdate progressUpdate) {
        return save(progressUpdate);
    }

    public void deleteUpdate(Long id) {
        progressUpdateRepository.deleteById(id);
    }

    public void delete(Long id) {
        deleteUpdate(id);
    }
}