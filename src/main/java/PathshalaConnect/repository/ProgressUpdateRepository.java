package PathshalaConnect.repository;

import PathshalaConnect.entity.ProgressUpdate;
import PathshalaConnect.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressUpdateRepository extends JpaRepository<ProgressUpdate, Long> {

    List<ProgressUpdate> findBySchoolOrderByIdDesc(School school);

    List<ProgressUpdate> findBySchoolOrderByCreatedAtDesc(School school);

    List<ProgressUpdate> findBySchoolId(Long schoolId);

    List<ProgressUpdate> findBySchoolIdOrderByCreatedAtDesc(Long schoolId);

    List<ProgressUpdate> findTop10ByOrderByCreatedAtDesc();
}