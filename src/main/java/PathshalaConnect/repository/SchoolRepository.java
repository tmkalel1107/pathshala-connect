package PathshalaConnect.repository;

import PathshalaConnect.entity.School;
import PathshalaConnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

    List<School> findByStatus(String status);

    List<School> findByStatusOrderByCreatedAtDesc(String status);

    List<School> findByOwner(User owner);

    long countByStatus(String status);

    List<School> findByStatusAndDistrictIgnoreCase(String status, String district);

    @Query("SELECT s FROM School s WHERE s.status = 'VERIFIED' AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(s.district) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(COALESCE(s.villageCity, '')) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<School> searchVerified(@Param("q") String q);

    @Query("SELECT DISTINCT s.district FROM School s WHERE s.status = 'VERIFIED' AND s.district IS NOT NULL ORDER BY s.district")
    List<String> findDistinctDistricts();
}