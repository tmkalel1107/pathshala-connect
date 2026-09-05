package PathshalaConnect.service;

import PathshalaConnect.entity.Donation;
import PathshalaConnect.entity.Need;
import PathshalaConnect.entity.School;
import PathshalaConnect.entity.User;
import PathshalaConnect.repository.DonationRepository;
import PathshalaConnect.repository.NeedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DonationService {

    private final DonationRepository donationRepository;
    private final NeedRepository needRepository;

    public DonationService(DonationRepository donationRepository, NeedRepository needRepository) {
        this.donationRepository = donationRepository;
        this.needRepository = needRepository;
    }

    public Donation save(User donor, Need need, double amount, String message) {
        return save(donor, need, amount, message, "DEMO_UPI");
    }

    public Donation save(User donor, Need need, double amount, String message, String paymentMethod) {
        Donation donation = new Donation();
        donation.setDonor(donor);
        donation.setNeed(need);
        donation.setSchool(need != null ? need.getSchool() : null);
        donation.setAmount(amount);
        donation.setMessage(message != null ? message.trim() : "");
        donation.setStatus("COMPLETED");
        donation.setPaymentMethod(paymentMethod != null ? paymentMethod : "DEMO_UPI");
        donation.setTransactionRef("TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        donation.setDonationDate(LocalDateTime.now());

        if (need != null) {
            need.setReceivedAmount(need.getReceivedAmount() + amount);
            if (need.getReceivedAmount() >= need.getTargetAmount()) {
                need.setStatus("FUNDED");
            }
            needRepository.save(need);
        }

        return donationRepository.save(donation);
    }

    @Transactional(readOnly = true)
    public List<Donation> mine(User donor) {
        if (donor == null) return List.of();
        return donationRepository.findByDonorOrderByDonationDateDesc(donor);
    }

    @Transactional(readOnly = true)
    public List<Donation> bySchool(School school) {
        if (school == null) return List.of();
        return donationRepository.findBySchoolOrderByDonationDateDesc(school);
    }

    @Transactional(readOnly = true)
    public List<Donation> latest() {
        return donationRepository.findTop10ByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public List<Donation> all() {
        return donationRepository.findAllByOrderByDonationDateDesc();
    }

    @Transactional(readOnly = true)
    public double getTotalDonationsAmount() {
        Double sum = donationRepository.sumTotalDonations();
        return sum != null ? sum : 0.0;
    }

    @Transactional(readOnly = true)
    public double getTotalByDonor(User donor) {
        if (donor == null) return 0.0;
        Double sum = donationRepository.sumDonationsByDonor(donor);
        return sum != null ? sum : 0.0;
    }

    @Transactional(readOnly = true)
    public double getTotalBySchool(School school) {
        if (school == null) return 0.0;
        Double sum = donationRepository.sumDonationsBySchool(school);
        return sum != null ? sum : 0.0;
    }

    @Transactional(readOnly = true)
    public long countSupportedSchools(User donor) {
        if (donor == null) return 0;
        Long count = donationRepository.countDistinctSchoolsByDonor(donor);
        return count != null ? count : 0;
    }

    @Transactional(readOnly = true)
    public long countTotalDonations() {
        return donationRepository.count();
    }
}