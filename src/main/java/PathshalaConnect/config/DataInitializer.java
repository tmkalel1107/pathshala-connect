package PathshalaConnect.config;

import PathshalaConnect.entity.*;
import PathshalaConnect.repository.*;
import PathshalaConnect.service.DonationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedDatabase(
            UserRepository userRepository,
            SchoolRepository schoolRepository,
            NeedRepository needRepository,
            DonationRepository donationRepository,
            ProgressUpdateRepository progressRepository,
            DonationService donationService) {

        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // 1. Seed Admin User
            User admin = userRepository.findByEmail("admin@pathshala.com").orElse(null);
            if (admin == null) {
                admin = new User();
                admin.setFullName("Platform Admin (प्लॅटफॉर्म प्रशासक)");
                admin.setEmail("admin@pathshala.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ADMIN");
                admin.setPhone("9999999999");
                admin.setOrganization("Pathshala Connect Admin Team");
                admin.setActive(true);
                admin.setCreatedAt(LocalDateTime.now().minusMonths(6));
                userRepository.save(admin);
            }

            // 2. Seed Donor User
            User donor = userRepository.findByEmail("donor@pathshala.com").orElse(null);
            if (donor == null) {
                donor = new User();
                donor.setFullName("अनिल जोशी (Anil Joshi)");
                donor.setEmail("donor@pathshala.com");
                donor.setPassword(encoder.encode("donor123"));
                donor.setRole("DONOR");
                donor.setPhone("9850011223");
                donor.setOrganization("जोशी फाऊंडेशन (Joshi Foundation)");
                donor.setActive(true);
                donor.setCreatedAt(LocalDateTime.now().minusMonths(3));
                userRepository.save(donor);
            }

            // 3. Seed Demo School 1
            User schoolOwner1 = userRepository.findByEmail("school@pathshala.com").orElse(null);
            if (schoolOwner1 == null) {
                schoolOwner1 = new User();
                schoolOwner1.setFullName("सचिन पाटील (मुख्याध्यापक)");
                schoolOwner1.setEmail("school@pathshala.com");
                schoolOwner1.setPassword(encoder.encode("school123"));
                schoolOwner1.setRole("SCHOOL");
                schoolOwner1.setPhone("9822123456");
                schoolOwner1.setOrganization("जि. प. प्राथमिक शाळा मुळशी");
                schoolOwner1.setActive(true);
                schoolOwner1.setCreatedAt(LocalDateTime.now().minusMonths(2));
                userRepository.save(schoolOwner1);

                School s1 = new School();
                s1.setName("जिल्हा परिषद प्राथमिक शाळा, मुळशी");
                s1.setDistrict("पुणे");
                s1.setVillageCity("मुळशी");
                s1.setAddress("मु. पो. मुळशी, ता. मुळशी, जि. पुणे, महाराष्ट्र");
                s1.setPincode("412108");
                s1.setPrincipalName("सचिन बाळकृष्ण पाटील");
                s1.setEmail(schoolOwner1.getEmail());
                s1.setContact("9822123456");
                s1.setDescription("आमच्या शाळेत ग्रामीण भागातील १५० हून अधिक विद्यार्थी शिकत आहेत. डिजिटल शिक्षण, आधुनिक ग्रंथालय आणि सुरक्षित पिण्याच्या पाण्यासाठी समाजाचे सहकार्य अपेक्षित आहे.");
                s1.setStatus("VERIFIED");
                s1.setOwner(schoolOwner1);
                s1.setCreatedAt(LocalDateTime.now().minusMonths(2));
                schoolRepository.save(s1);

                // Need 1 (Digital)
                Need n1 = new Need();
                n1.setTitle("विद्यार्थ्यांसाठी १० संगणक संच");
                n1.setCategory("Digital education");
                n1.setDescription("विद्यार्थ्यांना संगणक साक्षर करण्यासाठी आणि कोडिंग/डिजिटल कौशल्ये शिकवण्यासाठी कॉम्प्युटर लॅबची उभारणी करायची आहे.");
                n1.setTargetAmount(150000);
                n1.setReceivedAmount(65000);
                n1.setStatus("OPEN");
                n1.setPriority("URGENT");
                n1.setDeadline("2026-12-31");
                n1.setSchool(s1);
                n1.setCreatedAt(LocalDateTime.now().minusDays(20));
                needRepository.save(n1);

                // Need 2 (Books - Funded)
                Need n2 = new Need();
                n2.setTitle("ग्रंथालयासाठी पुस्तके व कपाटे");
                n2.setCategory("Books");
                n2.setDescription("मराठी व इंग्रजी भाषेतील ज्ञानवर्धक पुस्तके, संदर्भ ग्रंथ आणि वाचनालयासाठी सुरक्षित कपाटांची आवश्यकता.");
                n2.setTargetAmount(35000);
                n2.setReceivedAmount(35000);
                n2.setStatus("FUNDED");
                n2.setPriority("NORMAL");
                n2.setDeadline("2026-09-30");
                n2.setSchool(s1);
                n2.setCreatedAt(LocalDateTime.now().minusDays(40));
                needRepository.save(n2);

                // Need 3 (Water)
                Need n3 = new Need();
                n3.setTitle("पिण्याच्या पाण्यासाठी वॉटर प्युरिफायर (RO Plant)");
                n3.setCategory("Drinking water");
                n3.setDescription("विद्यार्थ्यांच्या आरोग्यासाठी शाळेमध्ये स्वच्छ पिण्याच्या पाण्यासाठी आरओ फिल्टर सिस्टीम बसवणे आवश्यक आहे.");
                n3.setTargetAmount(45000);
                n3.setReceivedAmount(15000);
                n3.setStatus("OPEN");
                n3.setPriority("HIGH");
                n3.setDeadline("2026-11-15");
                n3.setSchool(s1);
                n3.setCreatedAt(LocalDateTime.now().minusDays(10));
                needRepository.save(n3);

                // Progress 1
                ProgressUpdate p1 = new ProgressUpdate();
                p1.setTitle("ग्रंथालय कपाट आणि ५०० पुस्तकांची खरेदी पूर्ण");
                p1.setDescription("देणगीदारांच्या योगदानातून ५०० नवीन पुस्तके आणि २ मोठी लोखंडी कपाटे खरेदी करण्यात आली आहेत. विद्यार्थ्यांमध्ये वाचनाची आवड निर्माण होत आहे.");
                p1.setProgressPercent(100);
                p1.setImageUrl("https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=600");
                p1.setSchool(s1);
                p1.setNeed(n2);
                p1.setCreatedAt(LocalDateTime.now().minusDays(15));
                progressRepository.save(p1);

                // Progress 2
                ProgressUpdate p2 = new ProgressUpdate();
                p2.setTitle("संगणक कक्षाची वायरिंग व नेटवर्किंग पूर्ण");
                p2.setDescription("कॉम्प्युटर लॅबसाठी आवश्यक असलेले वीज वायरिंग आणि इंटरनेट नेटवर्किंगचे काम पूर्ण झाले असून लवकरच संगणक संच स्थापित केले जातील.");
                p2.setProgressPercent(60);
                p2.setImageUrl("https://images.unsplash.com/photo-1509062522246-3755977927d7?w=600");
                p2.setSchool(s1);
                p2.setNeed(n1);
                p2.setCreatedAt(LocalDateTime.now().minusDays(5));
                progressRepository.save(p2);

                // Donation from demo donor
                donationService.save(donor, n1, 25000, "विद्यार्थ्यांच्या उज्ज्वल भविष्यासाठी लहानशी मदत! - जोशी फाऊंडेशन", "DEMO_UPI");
                donationService.save(donor, n2, 35000, "ग्रामीण भागातील ग्रंथालय चळवळीला प्रोत्साहन.", "DEMO_NETBANKING");
            }

            // 4. Seed Demo School 2 (Satara)
            if (schoolRepository.findAll().size() < 2) {
                User schoolOwner2 = new User();
                schoolOwner2.setFullName("सुनील कदम (मुख्याध्यापक)");
                schoolOwner2.setEmail("satara.school@pathshala.com");
                schoolOwner2.setPassword(encoder.encode("school123"));
                schoolOwner2.setRole("SCHOOL");
                schoolOwner2.setPhone("9422987654");
                schoolOwner2.setOrganization("छत्रपती शिवाजी विद्यालय वाई");
                schoolOwner2.setActive(true);
                schoolOwner2.setCreatedAt(LocalDateTime.now().minusMonths(1));
                userRepository.save(schoolOwner2);

                School s2 = new School();
                s2.setName("छत्रपती शिवाजी विद्यालय, वाई");
                s2.setDistrict("सातारा");
                s2.setVillageCity("वाई");
                s2.setAddress("वाई-महाबळेश्वर रोड, ता. वाई, जि. सातारा");
                s2.setPincode("412803");
                s2.setPrincipalName("सुनील विठ्ठल कदम");
                s2.setEmail(schoolOwner2.getEmail());
                s2.setContact("9422987654");
                s2.setDescription("शाळेतील २०० विद्यार्थ्यांना विज्ञानातील प्रयोगांची आवड निर्माण व्हावी यासाठी सुसज्ज विज्ञान प्रयोगशाळा उभारण्याचे आमचे ध्येय आहे.");
                s2.setStatus("VERIFIED");
                s2.setOwner(schoolOwner2);
                s2.setCreatedAt(LocalDateTime.now().minusMonths(1));
                schoolRepository.save(s2);

                Need n4 = new Need();
                n4.setTitle("विज्ञान प्रयोगशाळा साहित्य व मायक्रोस्कोप");
                n4.setCategory("Classroom equipment");
                n4.setDescription("भौतिकशास्त्र, रसायनशास्त्र आणि जीवशास्त्रातील प्रत्यक्ष प्रयोगांसाठी आधुनिक उपकरणे, टेस्ट ट्यूब्स आणि मायक्रोस्कोपची आवश्यकता आहे.");
                n4.setTargetAmount(80000);
                n4.setReceivedAmount(30000);
                n4.setStatus("OPEN");
                n4.setPriority("HIGH");
                n4.setDeadline("2026-10-31");
                n4.setSchool(s2);
                n4.setCreatedAt(LocalDateTime.now().minusDays(12));
                needRepository.save(n4);

                donationService.save(donor, n4, 15000, "विज्ञान शिक्षणासाठी शुभेच्छा.", "DEMO_UPI");
            }

            // 5. Seed a Pending School for Admin verification demo
            if (schoolRepository.findByStatus("PENDING").isEmpty()) {
                User schoolOwner3 = new User();
                schoolOwner3.setFullName("प्रमोद गायकवाड");
                schoolOwner3.setEmail("nashik.school@pathshala.com");
                schoolOwner3.setPassword(encoder.encode("school123"));
                schoolOwner3.setRole("SCHOOL");
                schoolOwner3.setPhone("9890123456");
                schoolOwner3.setOrganization("ज्ञानदीप प्राथमिक शाळा दिंडोरी");
                schoolOwner3.setActive(true);
                schoolOwner3.setCreatedAt(LocalDateTime.now().minusDays(3));
                userRepository.save(schoolOwner3);

                School s3 = new School();
                s3.setName("ज्ञानदीप प्राथमिक शाळा, दिंडोरी");
                s3.setDistrict("नाशिक");
                s3.setVillageCity("दिंडोरी");
                s3.setAddress("मु. पो. दिंडोरी, जि. नाशिक");
                s3.setPincode("422202");
                s3.setPrincipalName("प्रमोद गायकवाड");
                s3.setEmail(schoolOwner3.getEmail());
                s3.setContact("9890123456");
                s3.setDescription("शाळेतील स्वच्छतागृह आणि खेळाच्या मैदानाच्या विकासासाठी दात्यांच्या सहकार्याची आवश्यकता.");
                s3.setStatus("PENDING");
                s3.setOwner(schoolOwner3);
                s3.setCreatedAt(LocalDateTime.now().minusDays(3));
                schoolRepository.save(s3);
            }
        };
    }
}