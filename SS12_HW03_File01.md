# BÀI TẬP 03: Xây dựng API Spring Boot cho chức năng "Đăng ký mở tài khoản cơ bản"

---

## Ngữ cảnh
Dựa trên tài liệu SRS đã được phê duyệt, đội ngũ Backend cần xây dựng API endpoint cho chức năng **Đăng ký mở tài khoản cơ bản**.

**Input (Request):**  
- fullName  
- phone  
- email  
- citizenId (Số CCCD)  

**Output (Response):**  
- accountId  
- accountNumber  
- status (PENDING/ACTIVE)  

---

## Yêu cầu

### Cấu trúc dự án theo chuẩn MVC
- **Entity**  
- **DTO (Request/Response)**  
- **Repository**  
- **Service (Interface & Impl)**  
- **Controller**

### Chất lượng code
- Có **JavaDoc** giải thích các class/method.  
- Comment tiếng Việt trong các đoạn logic xử lý.  
- Áp dụng **Data Validation**:  
  - `@NotBlank`  
  - `@Email`  
  - Custom validation cho định dạng citizenId 12 số.  

### Bonus Task
- Sinh sơ đồ kiến trúc hệ thống (Architecture Diagram) dạng văn bản hoặc **Mermaid** mô tả luồng đi từ Client → Controller → Service → Database.

---

## Output
- Trả về mã nguồn Java trong **code block**.  
- Sơ đồ kiến trúc dưới dạng **Mermaid**.  
