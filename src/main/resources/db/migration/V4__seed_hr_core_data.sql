-- Seed data for the first HR core vertical slice. Safe to re-run because all inserts are keyed.
INSERT INTO roles (name, description)
VALUES
    ('HR_MANAGER', 'Quản lý nghiệp vụ nhân sự'),
    ('MANAGER', 'Quản lý trực tiếp'),
    ('EMPLOYEE', 'Nhân viên self-service')
ON CONFLICT (name) DO NOTHING;

INSERT INTO departments (code, name, description)
VALUES
    ('ENG', 'Engineering', 'Phát triển và vận hành sản phẩm'),
    ('PEOPLE', 'Human Resources', 'Quản trị nhân sự và văn hóa'),
    ('FIN', 'Finance', 'Tài chính và kế toán')
ON CONFLICT (code) DO NOTHING;

INSERT INTO positions (code, name, description)
VALUES
    ('ENG-BE', 'Backend Developer', 'Phát triển dịch vụ và API'),
    ('ENG-FE', 'Frontend Developer', 'Phát triển giao diện sản phẩm'),
    ('HR-01', 'HR Specialist', 'Vận hành nghiệp vụ nhân sự'),
    ('FIN-01', 'Finance Officer', 'Xử lý nghiệp vụ tài chính')
ON CONFLICT (code) DO NOTHING;

INSERT INTO employees (employee_code, full_name, email, phone, hire_date, status, department_id, position_id)
SELECT seed.employee_code,
       seed.full_name,
       seed.email,
       seed.phone,
       seed.hire_date,
       seed.status,
       department.id,
       position.id
FROM (VALUES
    ('EMP001', 'Nguyễn Minh Anh', 'minh.anh@company.local', '0901000001', DATE '2025-01-06', 'ACTIVE', 'PEOPLE', 'HR-01'),
    ('EMP002', 'Trần Hoàng Nam', 'hoang.nam@company.local', '0901000002', DATE '2025-02-10', 'ACTIVE', 'ENG', 'ENG-BE'),
    ('EMP003', 'Lê Thu Hà', 'thu.ha@company.local', '0901000003', DATE '2025-03-03', 'ACTIVE', 'ENG', 'ENG-FE')
) AS seed(employee_code, full_name, email, phone, hire_date, status, department_code, position_code)
JOIN departments department ON department.code = seed.department_code
JOIN positions position ON position.code = seed.position_code
ON CONFLICT (employee_code) DO NOTHING;

UPDATE users
SET employee_id = employees.id
FROM employees
WHERE users.username = 'hradmin'
  AND employees.employee_code = 'EMP001'
  AND users.employee_id IS NULL;

UPDATE users
SET employee_id = employees.id
FROM employees
WHERE users.username = 'employee'
  AND employees.employee_code = 'EMP002'
  AND users.employee_id IS NULL;

INSERT INTO user_roles (user_id, role_id)
SELECT users.id, roles.id
FROM users
JOIN roles ON roles.name = 'EMPLOYEE'
WHERE users.username = 'employee'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO leave_types (code, name, default_days, status)
VALUES
    ('ANNUAL', 'Nghỉ phép năm', 12, 'ACTIVE'),
    ('SICK', 'Nghỉ ốm', 30, 'ACTIVE'),
    ('UNPAID', 'Nghỉ không lương', 0, 'ACTIVE'),
    ('PERSONAL', 'Nghỉ việc riêng', 3, 'ACTIVE')
ON CONFLICT (code) DO NOTHING;
