# Pathshala Connect – पाठशाळा कनेक्ट
> **"शाळांना सक्षम करणारे, समाजाला जोडणारे व्यासपीठ"**  
> *Connecting Schools with Support*

---

## 📌 प्रकल्प विहंगावलोकन / Project Overview

**Pathshala Connect (पाठशाळा कनेक्ट)** हा एक पूर्ण-स्टॅक (Full-Stack) वेब अनुप्रयोग आहे जो शैक्षणिक गरजा असलेल्या शाळांना (विशेषतः ग्रामीण व गरजू भागातील) आणि त्यांना साहाय्य करू इच्छिणाऱ्या देणगीदारांना थेट आणि पारदर्शकपणे जोडतो.

- **मराठी-प्रथम (Marathi-First) रचना**: संपूर्ण अनुप्रयोग प्राथमिक भाषेत मराठीत उपलब्ध आहे आणि एका क्लिकवर इंग्रजी (English) मध्ये सहज स्विच करता येतो.
- **तीन मुख्य भूमिका (Three User Roles)**:
  1. 🏫 **शाळा / शाळा प्रतिनिधी (SCHOOL)**
  2. 🤝 **देणगीदार (DONOR)**
  3. 🛡️ **प्लॅटफॉर्म प्रशासक (ADMIN)**
- **पारदर्शकता (100% Transparency)**: शाळांच्या शैक्षणिक गरजा, निधी संकलन, देणगीदारांचे योगदान आणि कामाचे प्रगती अपडेट्स (फोटो व टक्केवारीसह) सार्वजनिकरीत्या उपलब्ध.

---

## 🛠️ तंत्रज्ञान स्टॅक / Technology Stack

- **Backend**: Java 17, Spring Boot 3.3.5
- **Persistence / ORM**: Spring Data JPA / Hibernate 6, MySQL 8
- **Security / Hashing**: Session Authentication + Spring Security Crypto (BCrypt password hashing) + Role-based `AuthInterceptor`
- **Frontend / Templates**: Thymeleaf, HTML5, Modern Responsive CSS3, JavaScript (No React / Angular / Node.js)
- **Internationalization (i18n)**: Spring ResourceBundles (`messages_mr.properties`, `messages.properties`) + `CookieLocaleResolver` + `LocaleChangeInterceptor` (`?lang=mr` / `?lang=en`)
- **Build Tool**: Apache Maven

---

## 🚀 ॲप्लिकेशन सुरू कसे करावे / How to Run

### १. पूर्वतयारी (Prerequisites)
- **Java 17 JDK** इन्स्टॉल असावे (`java -version`).
- **Apache Maven 3.8+** इन्स्टॉल असावे (`mvn -version`).
- **MySQL 8.0 Server** स्थानिक सेवेवर (localhost:3306) कार्यरत असावे.

### २. MySQL डेटाबेस कॉन्फिगरेशन
डेटाबेस मॅन्युअली तयार करण्याची आवश्यकता नाही (`createDatabaseIfNotExist=true` मुळे डेटाबेस आपोआप तयार होतो).

तुमचा MySQL पासवर्ड बदलण्यासाठी दोन पर्याय उपलब्ध आहेत:

#### पर्याय अ: `src/main/resources/application.properties` फाईलमध्ये पासवर्ड टाका:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```
*(टीप: सध्या डीफॉल्ट पासवर्ड `Tushar@5436` सेट केलेला आहे)*

#### पर्याय ब: पर्यावरण व्हेरिएबल (Environment Variable) द्वारे सेट करा:
```powershell
$env:MYSQL_PASSWORD="YOUR_MYSQL_PASSWORD"
```

### ३. ॲप्लिकेशन चालवा (Run Command)
प्रकल्पाच्या रूट फोल्डरमधून टर्मिनल उघडा आणि पुढील कमांड चालवा:

```powershell
mvn spring-boot:run
```
किंवा Windows बॅच फाईलवर डबल-क्लिक करा:
```powershell
.\run.bat
```

### ४. ब्राऊजरमध्ये उघडा
ॲप्लिकेशन सुरू झाल्यावर ब्राऊजरमध्ये खालील URL उघडा:
👉 **http://localhost:8080**

---

## 🔑 डेमो लॉगिन माहिती / Demo Credentials

प्रकल्प पहिल्यांदा सुरू होताच चाचणीसाठी खालील डेमो खाती स्वयंचलितरित्या तयार केली जातात:

| भूमिका (Role) | ई-मेल (Email) | पासवर्ड (Password) | वर्णन |
|---|---|---|---|
| 🛡️ **Admin** | `admin@pathshala.com` | `admin123` | शाळा पडताळणी, वापरकर्ते व्यवस्थापन, ऑडिट |
| 🏫 **School** | `school@pathshala.com` | `school123` | जि. प. प्राथमिक शाळा मुळशी (Verified) |
| 🤝 **Donor** | `donor@pathshala.com` | `donor123` | अनिल जोशी (जोशी फाऊंडेशन) |

---

## 🧭 मुख्य पृष्ठे आणि वैशिष्ट्ये / Pages & Features

1. **मुख्यपृष्ठ (`/`)**:
   - आकर्षक Light Education Theme, सांख्यिकी कार्ड्स (Verified Schools, Active Needs, Total Donations, Transparency).
   - "कसे कार्य करते?" (How it works in 4 steps).
   - पडताळलेल्या शाळा व सक्रिय गरजांची पूर्वनोंदणी.
2. **नोंदणी पृष्ठ (`/register`)**:
   - "तुम्ही कोणत्या प्रकारे सहभागी होऊ इच्छिता?" या प्रश्नासह 🏫 शाळा किंवा 🤝 देणगीदार निवड.
   - शाळा निवडल्यास: शाळेचे नाव, मुख्याध्यापक नाव, जिल्हा, गाव/शहर, पिनकोड, पत्ता व वर्णन ही क्षेत्रे आपोआप उघडतात.
   - पासवर्ड किमान ६ अक्षरे आणि पासवर्ड पडताळणी (Confirm password matching).
3. **लॉगिन पृष्ठ (`/login`)**:
   - सुरक्षित BCrypt प्रमाणीकरण.
   - भूमिकेनुसार आपोआप योग्य डॅशबोर्डवर रिडायरेक्ट (School -> School Dashboard, Donor -> Donor Dashboard, Admin -> Admin Dashboard).
4. **शाळा निर्देशिका (`/schools`)**:
   - कीवर्ड शोध (Search by school name, village, or district).
   - जिल्हा ड्रॉपडाउन फिल्टर (Filter by district).
5. **शाळा तपशील (`/schools/{id}`)**:
   - शाळेची संपूर्ण माहिती, मुख्याध्यापक संपर्क, पत्ता.
   - शैक्षणिक गरजांची यादी, निधी प्रगती बार (Progress bar).
   - कामाचे फोटो आणि प्रगती अपडेट्स (Timeline).
6. **शाळा डॅशबोर्ड (`/dashboard` - Role: SCHOOL)**:
   - गरज पोस्ट करा (`/school/post-need`) - संगणक, पुस्तके, स्वच्छतागृह, पिण्याचे पाणी इत्यादी १० श्रेणी.
   - प्रगती अपडेट पोस्ट करा (`/school/post-progress`) - टक्केवारी व फोटो URL सह.
   - मिळालेल्या देणग्यांचे विवरण आणि पावती संदर्भ क्रमांक.
   - शाळा प्रोफाइल संपादन (`/school/profile`).
7. **देणगीदार डॅशबोर्ड (`/dashboard` - Role: DONOR)**:
   - एकूण केलेले योगदान, मदत केलेल्या शाळांची संख्या.
   - संपूर्ण देणगी इतिहास आणि ट्रान्झॅक्शन आयडी.
   - शिफारस केलेल्या सक्रिय गरजा.
8. **देणगी पृष्ठ (`/donate/{id}`)**:
   - जलद रक्कम बटणे (₹५००, ₹१,०००, ₹२,५००, ₹५,०००) किंवा कस्टम रक्कम.
   - संदेश आणि सुरक्षित डेमो पेमेंट पद्धती निवड.
9. **पारदर्शकता पृष्ठ (`/transparency`)**:
   - एकूण जमा निधी, एकूण शाळा, सक्रिय गरजा आणि अलीकडील देणगी नोंदींचा सार्वजनिक प्रवाह.
10. **ॲडमिन डॅशबोर्ड (`/dashboard` - Role: ADMIN)**:
    - शाळा पडताळणी (One-click Verify / Reject).
    - वापरकर्ते नियंत्रण (Active / Inactive toggle).
    - देणगी ऑडिट लेजर (Audit ledger).
11. **कस्टम एरर पेजेस**:
    - `error/403.html` (प्रवेश मर्यादित - Unauthorized role access).
    - `error/404.html` (पान सापडले नाही - Not found).
    - `error/500.html` (सर्व्हर त्रुटी - Internal server error).

---

## 🔒 सुरक्षा आणि प्रवेश नियंत्रण (Security & Access Control)

- **`AuthInterceptor`**:
  - अनधिकृत वापरकर्त्यांना सुरक्षितपणे `/login?msg=auth_required` वर रिडायरेक्ट केले जाते.
  - SCHOOL वापरकर्त्यांना DONOR किंवा ADMIN पृष्ठांवर प्रवेश दिला जात नाही (४०३ Forbidden).
  - DONOR वापरकर्त्यांना SCHOOL व्यवस्थापन किंवा ADMIN पृष्ठांवर प्रवेश दिला जात नाही (४०३ Forbidden).
  - सामान्य वापरकर्त्यांना ADMIN पॅनेलमध्ये प्रवेशास पूर्ण बंदी.
- **पासवर्ड हॅशिंग**: सर्व पासवर्ड्स Spring Security Crypto च्या `BCryptPasswordEncoder` द्वारे सुरक्षित हॅश करूनच डेटाबेसमध्ये साठवले जातात.

---

## 📦 प्रकल्प ZIP निर्मिती / Project Archive

हा प्रकल्प एका स्वतंत्र `pathshala-connect.zip` मध्ये पॅक केलेला असून तो कोणत्याही संगणकावर सहज अनझिप करून चालवता येतो.
