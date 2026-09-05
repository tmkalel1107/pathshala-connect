package PathshalaConnect.controller;

import PathshalaConnect.entity.*;
import PathshalaConnect.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AppController {

    @Value("${pathshala.admin.secret-key:PATHSHALA_ADMIN_2026}")
    private String adminSecretKey;

    private final UserService userService;
    private final SchoolService schoolService;
    private final NeedService needService;
    private final DonationService donationService;
    private final ProgressService progressService;

    public AppController(UserService userService,
                         SchoolService schoolService,
                         NeedService needService,
                         DonationService donationService,
                         ProgressService progressService) {
        this.userService = userService;
        this.schoolService = schoolService;
        this.needService = needService;
        this.donationService = donationService;
        this.progressService = progressService;
    }

    private User currentUser(HttpSession session) {
        if (session == null) return null;
        Long id = (Long) session.getAttribute("uid");
        return id == null ? null : userService.getById(id);
    }

    private boolean hasRole(HttpSession session, String expectedRole) {
        User user = currentUser(session);
        return user != null && expectedRole.equalsIgnoreCase(user.getRole());
    }

    private School ownedSchool(HttpSession session) {
        User user = currentUser(session);
        if (user == null || !"SCHOOL".equalsIgnoreCase(user.getRole())) return null;
        List<School> list = schoolService.mine(user);
        return list.isEmpty() ? null : list.get(0);
    }

    // =========================================================================
    // Public Pages
    // =========================================================================

    @GetMapping("/")
    public String home(Model model) {
        List<School> verifiedSchools = schoolService.verified();
        List<Need> activeNeeds = needService.latestOpen();

        model.addAttribute("schools", verifiedSchools);
        model.addAttribute("needs", activeNeeds.isEmpty() ? needService.latest() : activeNeeds);
        model.addAttribute("totalSchools", verifiedSchools.size());
        model.addAttribute("totalNeeds", needService.countOpen());
        model.addAttribute("totalDonations", donationService.getTotalDonationsAmount());
        model.addAttribute("progressUpdates", progressService.latest());

        return "index";
    }

    @GetMapping("/schools")
    public String schools(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String district,
            Model model) {

        List<School> result = schoolService.search(q, district);
        List<String> districts = schoolService.getDistinctDistricts();

        model.addAttribute("schools", result);
        model.addAttribute("districts", districts);
        model.addAttribute("selectedDistrict", district != null ? district : "ALL");
        model.addAttribute("searchQuery", q != null ? q : "");

        return "schools";
    }

    @GetMapping("/schools/{id}")
    public String schoolDetails(@PathVariable Long id, Model model) {
        School school = schoolService.get(id);
        if (school == null || !"VERIFIED".equals(school.getStatus())) {
            return "redirect:/schools";
        }

        model.addAttribute("school", school);
        model.addAttribute("needs", needService.bySchool(school));
        model.addAttribute("progress", progressService.bySchool(school));
        model.addAttribute("donations", donationService.bySchool(school));
        model.addAttribute("totalReceived", donationService.getTotalBySchool(school));

        return "school-details";
    }

    @GetMapping("/transparency")
    public String transparency(Model model) {
        model.addAttribute("schools", schoolService.verified());
        model.addAttribute("needs", needService.latest());
        model.addAttribute("recentDonations", donationService.latest());
        model.addAttribute("progressUpdates", progressService.latest());
        model.addAttribute("totalDonationAmount", donationService.getTotalDonationsAmount());
        model.addAttribute("totalDonationsCount", donationService.countTotalDonations());
        model.addAttribute("totalSchoolsCount", schoolService.countVerified());
        model.addAttribute("activeNeedsCount", needService.countOpen());

        return "transparency";
    }

    // =========================================================================
    // Authentication (Login, Register, Logout)
    // =========================================================================

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String msg,
            HttpSession session,
            Model model) {

        if (currentUser(session) != null) {
            return "redirect:/dashboard";
        }

        if ("auth_required".equals(msg)) {
            model.addAttribute("errorKey", "flash.authRequired");
        } else if ("logged_out".equals(msg)) {
            model.addAttribute("successKey", "flash.loggedOut");
        }

        return "login";
    }

    @PostMapping("/login")
    public String loginPost(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = userService.login(email != null ? email.trim().toLowerCase() : "", password);
        if (user == null) {
            model.addAttribute("errorKey", "flash.loginError");
            return "login";
        }

        session.setAttribute("uid", user.getId());
        session.setAttribute("name", user.getFullName());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("role", user.getRole());

        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String register(HttpSession session) {
        if (currentUser(session) != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String role,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false) String schoolName,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String villageCity,
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) String principalName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String schoolDescription,
            Model model,
            RedirectAttributes redirectAttributes) {

        String normalizedEmail = email != null ? email.trim().toLowerCase() : "";

        if (userService.existsByEmail(normalizedEmail)) {
            model.addAttribute("errorKey", "flash.emailExists");
            return "register";
        }

        if (!"SCHOOL".equals(role) && !"DONOR".equals(role)) {
            model.addAttribute("errorKey", "flash.invalidRole");
            return "register";
        }

        if (password == null || password.length() < 6 || !password.equals(confirmPassword)) {
            model.addAttribute("errorKey", "flash.pwMismatch");
            return "register";
        }

        if (fullName == null || fullName.trim().isBlank() || phone == null || !phone.trim().matches("\\d{10}")) {
            model.addAttribute("errorKey", "flash.invalidContact");
            return "register";
        }

        if ("SCHOOL".equals(role)) {
            if (schoolName == null || schoolName.trim().isBlank() ||
                district == null || district.trim().isBlank() ||
                address == null || address.trim().isBlank()) {
                model.addAttribute("errorKey", "flash.schoolRequired");
                return "register";
            }
        }

        // Save User
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPassword(password);
        user.setRole(role);
        user.setPhone(phone.trim());
        user.setOrganization(organization != null && !organization.isBlank() ? organization.trim() : "");
        user = userService.save(user);

        // If SCHOOL, save School entity
        if ("SCHOOL".equals(role)) {
            School school = new School();
            school.setName(schoolName.trim());
            school.setDistrict(district.trim());
            school.setVillageCity(villageCity != null ? villageCity.trim() : "");
            school.setPincode(pincode != null ? pincode.trim() : "");
            school.setPrincipalName(principalName != null ? principalName.trim() : fullName.trim());
            school.setAddress(address.trim());
            school.setContact(phone.trim());
            school.setEmail(normalizedEmail);
            school.setDescription(schoolDescription != null ? schoolDescription.trim() : "शाळेचे वर्णन लवकरच अद्ययावत केले जाईल.");
            school.setOwner(user);
            school.setStatus("PENDING");
            schoolService.save(school);
        }

        redirectAttributes.addFlashAttribute("successKey", "flash.regSuccess");
        return "redirect:/login";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?msg=logged_out";
    }

    // =========================================================================
    // Role-Based Dashboards
    // =========================================================================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = currentUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);

        if ("SCHOOL".equals(user.getRole())) {
            School school = ownedSchool(session);
            List<Need> schoolNeeds = school != null ? needService.bySchool(school) : List.of();
            List<Donation> schoolDonations = school != null ? donationService.bySchool(school) : List.of();
            List<ProgressUpdate> schoolUpdates = school != null ? progressService.bySchool(school) : List.of();

            double totalRaised = schoolDonations.stream().mapToDouble(Donation::getAmount).sum();
            long openNeedsCount = schoolNeeds.stream().filter(n -> "OPEN".equals(n.getStatus())).count();

            model.addAttribute("school", school);
            model.addAttribute("mySchools", school != null ? List.of(school) : List.of());
            model.addAttribute("needs", schoolNeeds);
            model.addAttribute("donations", schoolDonations);
            model.addAttribute("updates", schoolUpdates);
            model.addAttribute("totalRaised", totalRaised);
            model.addAttribute("openNeedsCount", openNeedsCount);

            return "school-dashboard";
        }

        if ("DONOR".equals(user.getRole())) {
            List<Donation> myDonations = donationService.mine(user);
            double totalDonated = donationService.getTotalByDonor(user);
            long schoolsSupported = donationService.countSupportedSchools(user);
            List<Need> activeNeeds = needService.latestOpen();

            model.addAttribute("donations", myDonations);
            model.addAttribute("totalDonated", totalDonated);
            model.addAttribute("schoolsSupported", schoolsSupported);
            model.addAttribute("activeNeeds", activeNeeds);

            return "donor-dashboard";
        }

        if ("ADMIN".equals(user.getRole())) {
            model.addAttribute("pendingSchools", schoolService.pending());
            model.addAttribute("verifiedSchools", schoolService.verified());
            model.addAttribute("allSchools", schoolService.getAllSchools());
            model.addAttribute("latestDonations", donationService.latest());
            model.addAttribute("allDonations", donationService.all());
            model.addAttribute("allUsers", userService.getAllUsers());
            model.addAttribute("allNeeds", needService.getAllNeeds());
            model.addAttribute("allUpdates", progressService.getAllUpdates());
            model.addAttribute("totalUsers", userService.countTotalUsers());
            model.addAttribute("totalSchools", schoolService.countTotal());
            model.addAttribute("totalDonors", userService.countByRole("DONOR"));
            model.addAttribute("totalAdmins", userService.countByRole("ADMIN"));
            model.addAttribute("totalDonations", donationService.getTotalDonationsAmount());
            model.addAttribute("totalNeeds", needService.countTotal());
            model.addAttribute("openNeeds", needService.countOpen());

            return "admin-dashboard";
        }

        return "redirect:/login";
    }

    // =========================================================================
    // School Management
    // =========================================================================

    @GetMapping("/school/profile")
    public String schoolProfile(HttpSession session, Model model) {
        if (!hasRole(session, "SCHOOL")) return "redirect:/login";
        School school = ownedSchool(session);
        if (school == null) return "redirect:/dashboard";

        model.addAttribute("school", school);
        return "school-profile";
    }

    @PostMapping("/school/profile")
    public String saveSchoolProfile(
            @RequestParam String name,
            @RequestParam String principalName,
            @RequestParam String contact,
            @RequestParam String district,
            @RequestParam(required = false) String villageCity,
            @RequestParam(required = false) String pincode,
            @RequestParam String address,
            @RequestParam(required = false) String description,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!hasRole(session, "SCHOOL")) return "redirect:/login";
        School school = ownedSchool(session);
        if (school != null) {
            school.setName(name.trim());
            school.setPrincipalName(principalName.trim());
            school.setContact(contact.trim());
            school.setDistrict(district.trim());
            school.setVillageCity(villageCity != null ? villageCity.trim() : "");
            school.setPincode(pincode != null ? pincode.trim() : "");
            school.setAddress(address.trim());
            school.setDescription(description != null ? description.trim() : "");
            schoolService.save(school);
            redirectAttributes.addFlashAttribute("successKey", "flash.profileSaved");
        }
        return "redirect:/school/profile";
    }

    @GetMapping("/school/post-need")
    public String postNeedForm(HttpSession session, Model model) {
        if (!hasRole(session, "SCHOOL")) return "redirect:/login";
        School school = ownedSchool(session);
        if (school == null) return "redirect:/dashboard";

        model.addAttribute("school", school);
        return "post-need";
    }

    @PostMapping("/school/post-need")
    public String postNeed(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam double targetAmount,
            @RequestParam(defaultValue = "NORMAL") String priority,
            @RequestParam(required = false) String deadline,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!hasRole(session, "SCHOOL")) return "redirect:/login";
        School school = ownedSchool(session);
        if (school == null || title.isBlank() || description.isBlank() || targetAmount <= 0) {
            return "redirect:/dashboard";
        }

        Need need = new Need();
        need.setTitle(title.trim());
        need.setCategory(category.trim());
        need.setDescription(description.trim());
        need.setTargetAmount(targetAmount);
        need.setPriority(priority != null ? priority.trim() : "NORMAL");
        need.setDeadline(deadline != null ? deadline.trim() : "");
        need.setSchool(school);
        needService.save(need);

        redirectAttributes.addFlashAttribute("successKey", "flash.needPosted");
        return "redirect:/dashboard";
    }

    @PostMapping("/school/need/{id}/toggle")
    public String toggleNeedStatus(@PathVariable Long id, HttpSession session) {
        if (!hasRole(session, "SCHOOL")) return "redirect:/login";
        needService.toggleStatus(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/school/post-progress")
    public String postProgressForm(HttpSession session, Model model) {
        if (!hasRole(session, "SCHOOL")) return "redirect:/login";
        School school = ownedSchool(session);
        if (school == null) return "redirect:/dashboard";

        model.addAttribute("school", school);
        model.addAttribute("needs", needService.bySchool(school));
        return "post-progress";
    }

    @PostMapping("/school/post-progress")
    public String postProgress(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam int progressPercent,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Long needId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!hasRole(session, "SCHOOL")) return "redirect:/login";
        School school = ownedSchool(session);
        if (school == null || title.isBlank() || description.isBlank()) {
            return "redirect:/dashboard";
        }

        ProgressUpdate update = new ProgressUpdate();
        update.setTitle(title.trim());
        update.setDescription(description.trim());
        update.setProgressPercent(Math.max(0, Math.min(100, progressPercent)));
        update.setImageUrl(imageUrl != null ? imageUrl.trim() : "");
        update.setSchool(school);

        if (needId != null && needId > 0) {
            Need need = needService.get(needId);
            update.setNeed(need);
        }

        progressService.save(update);
        redirectAttributes.addFlashAttribute("successKey", "flash.progressPosted");
        return "redirect:/dashboard";
    }

    // =========================================================================
    // Donor Management & Donations
    // =========================================================================

    @GetMapping("/donate/{id}")
    public String donateForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!hasRole(session, "DONOR")) return "redirect:/login";

        Need need = needService.get(id);
        if (need == null || need.getSchool() == null || !"VERIFIED".equals(need.getSchool().getStatus())) {
            return "redirect:/schools";
        }

        model.addAttribute("need", need);
        model.addAttribute("school", need.getSchool());
        return "donate";
    }

    @PostMapping("/donate/{id}")
    public String donatePost(
            @PathVariable Long id,
            @RequestParam double amount,
            @RequestParam(required = false) String message,
            @RequestParam(defaultValue = "DEMO_UPI") String paymentMethod,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!hasRole(session, "DONOR")) return "redirect:/login";
        User user = currentUser(session);
        Need need = needService.get(id);

        if (need == null || amount <= 0) {
            return "redirect:/dashboard";
        }

        Donation donation = donationService.save(user, need, amount, message, paymentMethod);
        redirectAttributes.addFlashAttribute("successKey", "flash.donationSuccess");

        return "redirect:/dashboard";
    }

    // =========================================================================
    // Admin Actions
    // =========================================================================

    @PostMapping("/admin/schools/{id}/{action}")
    public String manageSchool(@PathVariable Long id, @PathVariable String action, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!hasRole(session, "ADMIN")) return "redirect:/login";

        School school = schoolService.get(id);
        if (school != null) {
            if ("verify".equalsIgnoreCase(action)) {
                school.setStatus("VERIFIED");
                schoolService.save(school);
                redirectAttributes.addFlashAttribute("successKey", "flash.schoolVerified");
            } else if ("reject".equalsIgnoreCase(action)) {
                school.setStatus("REJECTED");
                schoolService.save(school);
                redirectAttributes.addFlashAttribute("successKey", "flash.schoolRejected");
            }
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/create-admin")
    public String createAdmin(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam(required = false) String organization,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!hasRole(session, "ADMIN")) return "redirect:/login";

        String normalizedEmail = email != null ? email.trim().toLowerCase() : "";
        if (userService.existsByEmail(normalizedEmail)) {
            redirectAttributes.addFlashAttribute("errorKey", "flash.emailExists");
            return "redirect:/dashboard#createAdminTab";
        }

        if (password == null || password.length() < 6 || !password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorKey", "flash.pwMismatch");
            return "redirect:/dashboard#createAdminTab";
        }

        User newAdmin = new User();
        newAdmin.setFullName(fullName.trim());
        newAdmin.setEmail(normalizedEmail);
        newAdmin.setPassword(password);
        newAdmin.setRole("ADMIN");
        newAdmin.setPhone(phone != null ? phone.trim() : "");
        newAdmin.setOrganization(organization != null && !organization.isBlank() ? organization.trim() : "Pathshala Connect Administration");
        newAdmin.setActive(true);
        userService.save(newAdmin);

        redirectAttributes.addFlashAttribute("successKey", "flash.adminCreated");
        return "redirect:/dashboard#usersTab";
    }

    @PostMapping("/admin/schools/{id}/delete")
    public String deleteSchool(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!hasRole(session, "ADMIN")) return "redirect:/login";
        School school = schoolService.get(id);
        if (school != null) {
            schoolService.deleteSchool(id);
            redirectAttributes.addFlashAttribute("successKey", "flash.schoolDeleted");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/needs/{id}/toggle-status")
    public String toggleNeedStatus(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!hasRole(session, "ADMIN")) return "redirect:/login";
        Need need = needService.toggleStatus(id);
        if (need != null) {
            redirectAttributes.addFlashAttribute("successKey", "flash.needToggled");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/needs/{id}/delete")
    public String deleteNeed(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!hasRole(session, "ADMIN")) return "redirect:/login";
        Need need = needService.get(id);
        if (need != null) {
            needService.deleteNeed(id);
            redirectAttributes.addFlashAttribute("successKey", "flash.needDeleted");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/progress/{id}/delete")
    public String deleteProgressUpdate(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!hasRole(session, "ADMIN")) return "redirect:/login";
        ProgressUpdate update = progressService.get(id);
        if (update != null) {
            progressService.deleteUpdate(id);
            redirectAttributes.addFlashAttribute("successKey", "flash.updateDeleted");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!hasRole(session, "ADMIN")) return "redirect:/login";
        User current = currentUser(session);
        if (current != null && current.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorKey", "flash.selfDeleteError");
            return "redirect:/dashboard";
        }
        User target = userService.getById(id);
        if (target != null) {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successKey", "flash.userDeleted");
        }
        return "redirect:/dashboard";
    }
}
