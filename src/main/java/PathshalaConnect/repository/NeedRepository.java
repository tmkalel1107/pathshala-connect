package PathshalaConnect.repository;

import PathshalaConnect.entity.Need;
import PathshalaConnect.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeedRepository extends JpaRepository<Need, Long> {

    List<Need> findBySchool(School school);

    List<Need> findBySchoolId(Long schoolId);

    List<Need> findBySchoolOrderByCreatedAtDesc(School school);

    List<Need> findByStatus(String status);

    List<Need> findByStatusOrderByCreatedAtDesc(String status);

    List<Need> findTop10ByOrderByIdDesc();

    List<Need> findTop10ByStatusOrderByIdDesc(String status);

    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(n.targetAmount), 0) FROM Need n")
    Double sumTargetAmount();

    @Query("SELECT COALESCE(SUM(n.receivedAmount), 0) FROM Need n")
    Double sumReceivedAmount();
}