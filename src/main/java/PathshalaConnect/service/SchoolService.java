package PathshalaConnect.service;

import PathshalaConnect.entity.School;
import PathshalaConnect.entity.User;
import PathshalaConnect.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SchoolService {

    private final SchoolRepository schoolRepository;

    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Transactional(readOnly = true)
    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public School get(Long id) {
        if (id == null) return null;
        return schoolRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<School> getSchoolById(Long id) {
        return schoolRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<School> verified() {
        return schoolRepository.findByStatusOrderByCreatedAtDesc("VERIFIED");
    }

    @Transactional(readOnly = true)
    public List<School> pending() {
        return schoolRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    @Transactional(readOnly = true)
    public List<School> mine(User owner) {
        if (owner == null) return List.of();
        return schoolRepository.findByOwner(owner);
    }

    public School save(School school) {
        return schoolRepository.save(school);
    }

    public School saveSchool(School school) {
        return schoolRepository.save(school);
    }

    public void deleteSchool(Long id) {
        schoolRepository.deleteById(id);
    }

    public void delete(Long id) {
        deleteSchool(id);
    }

    public School updateStatus(Long id, String status) {
        School school = get(id);
        if (school != null) {
            school.setStatus(status);
            return schoolRepository.save(school);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<School> search(String query, String district) {
        boolean hasQuery = query != null && !query.trim().isBlank();
        boolean hasDistrict = district != null && !district.trim().isBlank() && !"ALL".equalsIgnoreCase(district.trim());

        if (hasQuery && hasDistrict) {
            return schoolRepository.searchVerified(query.trim()).stream()
                    .filter(s -> district.trim().equalsIgnoreCase(s.getDistrict()))
                    .toList();
        } else if (hasQuery) {
            return schoolRepository.searchVerified(query.trim());
        } else if (hasDistrict) {
            return schoolRepository.findByStatusAndDistrictIgnoreCase("VERIFIED", district.trim());
        } else {
            return verified();
        }
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctDistricts() {
        return schoolRepository.findDistinctDistricts();
    }

    @Transactional(readOnly = true)
    public long countVerified() {
        return schoolRepository.countByStatus("VERIFIED");
    }

    @Transactional(readOnly = true)
    public long countPending() {
        return schoolRepository.countByStatus("PENDING");
    }

    @Transactional(readOnly = true)
    public long countTotal() {
        return schoolRepository.count();
    }
}