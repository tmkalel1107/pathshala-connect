package PathshalaConnect.repository;

import PathshalaConnect.entity.Donation;
import PathshalaConnect.entity.Need;
import PathshalaConnect.entity.School;
import PathshalaConnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByDonor(User donor);

    List<Donation> findByDonorOrderByDonationDateDesc(User donor);

    List<Donation> findBySchool(School school);

    List<Donation> findBySchoolOrderByDonationDateDesc(School school);

    List<Donation> findByNeed(Need need);

    List<Donation> findTop10ByOrderByIdDesc();

    List<Donation> findAllByOrderByDonationDateDesc();

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d")
    Double sumTotalDonations();

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.donor = :donor")
    Double sumDonationsByDonor(@Param("donor") User donor);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.school = :school")
    Double sumDonationsBySchool(@Param("school") School school);

    @Query("SELECT COUNT(DISTINCT d.school.id) FROM Donation d WHERE d.donor = :donor")
    Long countDistinctSchoolsByDonor(@Param("donor") User donor);
}