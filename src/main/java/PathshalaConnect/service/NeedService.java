package PathshalaConnect.service;

import PathshalaConnect.entity.Need;
import PathshalaConnect.entity.School;
import PathshalaConnect.repository.NeedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NeedService {

    private final NeedRepository needRepository;

    public NeedService(NeedRepository needRepository) {
        this.needRepository = needRepository;
    }

    @Transactional(readOnly = true)
    public List<Need> getAllNeeds() {
        return needRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Need get(Long id) {
        if (id == null) return null;
        return needRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<Need> getNeedById(Long id) {
        return needRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Need> latest() {
        return needRepository.findTop10ByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public List<Need> latestOpen() {
        return needRepository.findTop10ByStatusOrderByIdDesc("OPEN");
    }

    @Transactional(readOnly = true)
    public List<Need> bySchool(School school) {
        if (school == null) return List.of();
        return needRepository.findBySchoolOrderByCreatedAtDesc(school);
    }

    @Transactional(readOnly = true)
    public List<Need> getNeedsBySchoolId(Long schoolId) {
        if (schoolId == null) return List.of();
        return needRepository.findBySchoolId(schoolId);
    }

    public Need save(Need need) {
        return needRepository.save(need);
    }

    public Need saveNeed(Need need) {
        return needRepository.save(need);
    }

    public void deleteNeed(Long id) {
        needRepository.deleteById(id);
    }

    public void delete(Long id) {
        deleteNeed(id);
    }

    public Need toggleStatus(Long id) {
        Need need = get(id);
        if (need != null) {
            if ("OPEN".equals(need.getStatus())) {
                need.setStatus("CLOSED");
            } else {
                need.setStatus("OPEN");
            }
            return needRepository.save(need);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public long countOpen() {
        return needRepository.countByStatus("OPEN");
    }

    @Transactional(readOnly = true)
    public long countTotal() {
        return needRepository.count();
    }

    @Transactional(readOnly = true)
    public double getTotalTargetAmount() {
        Double sum = needRepository.sumTargetAmount();
        return sum != null ? sum : 0.0;
    }

    @Transactional(readOnly = true)
    public double getTotalReceivedAmount() {
        Double sum = needRepository.sumReceivedAmount();
        return sum != null ? sum : 0.0;
    }
}