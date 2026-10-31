-- =============================================================================
-- FULL DATABASE SCHEMA & INITIAL DATA MIGRATION
-- Database: PostgreSQL (Neon)
-- Target Schema: loveworld_reports
-- =============================================================================

-- 1. Create Schema
CREATE SCHEMA IF NOT EXISTS loveworld_reports;

-- 2. Users Table
CREATE TABLE IF NOT EXISTS loveworld_reports.users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    role VARCHAR(50),
    region VARCHAR(255),
    status VARCHAR(30) DEFAULT 'active',
    kingchat_login_count INT DEFAULT 0,
    kingschat_id VARCHAR(255) UNIQUE,
    phone VARCHAR(50),
    invite_token VARCHAR(255),
    joined_date DATE DEFAULT CURRENT_DATE,
    otp_code VARCHAR(10),
    otp_expiry TIMESTAMP,
    security_question VARCHAR(255),
    security_answer VARCHAR(255),
    security_answer1 VARCHAR(255),
    security_answer2 VARCHAR(255),
    security_answer3 VARCHAR(255),
    admin_approval_notification_sent BOOLEAN DEFAULT FALSE,
    report_count INT DEFAULT 0
);

-- 3. Zone Weekly Reports (Zonal Master Reports)
CREATE TABLE IF NOT EXISTS loveworld_reports.zone_weekly_reports (
    id BIGSERIAL PRIMARY KEY,
    submitted_by VARCHAR(150) NOT NULL,
    submitter_email VARCHAR(255) NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_date DATE NOT NULL,
    submitted_time TIME NOT NULL,
    week_start_date DATE NOT NULL,
    zone_name VARCHAR(150) NOT NULL,
    zonal_manager VARCHAR(150) NOT NULL,
    zonal_pastor_executive_ministers_meeting VARCHAR(50) NOT NULL,
    zonal_manager_executive_ministers_meeting VARCHAR(50) NOT NULL,
    zonal_manager_strategy_meeting VARCHAR(50) NOT NULL,
    testimony_clarification_concern TEXT,
    region_name VARCHAR(150),
    status VARCHAR(30) DEFAULT 'PENDING',
    admin_note TEXT,
    participation_pray_with_me TEXT,
    total_registration_hslhs INT DEFAULT 0,
    herald_conference VARCHAR(100),
    dates_received VARCHAR(50)
);

-- 4. Partnership Reports
CREATE TABLE IF NOT EXISTS loveworld_reports.partnership_reports (
    id BIGSERIAL PRIMARY KEY,
    submitted_date DATE,
    submitter_email VARCHAR(255),
    zone_name VARCHAR(255),
    arms TEXT,
    total_remittance NUMERIC(12,2),
    zonal_partnership TEXT,
    zonal_partnership_details TEXT,
    groups_partnership TEXT,
    churches_partnership TEXT,
    cell_partnership TEXT,
    healing_crusade_sponsorship NUMERIC(12,2) DEFAULT 0.00,
    notes TEXT,
    status VARCHAR(30) DEFAULT 'PENDING',
    admin_note TEXT,
    pop_media_url TEXT,
    trumpets_blown INT DEFAULT 0,
    great_shouts INT DEFAULT 0,
    testimonies_submitted INT DEFAULT 0,
    group_pastors_milestones TEXT,
    sponsored_teenspiration_kidspiration TEXT,
    sponsored_teenspiration TEXT,
    sponsored_kidspiration TEXT,
    new_partners_recruited INT DEFAULT 0,
    remittance_purpose TEXT
);

-- 5. Testimonial Reports
CREATE TABLE IF NOT EXISTS loveworld_reports.testimonial_reports (
    id BIGSERIAL PRIMARY KEY,
    submitted_date DATE,
    submitter_email VARCHAR(255),
    zone_name VARCHAR(255),
    testimony TEXT,
    testimonies_count INT DEFAULT 0,
    before_images INT DEFAULT 0,
    after_images INT DEFAULT 0,
    documents INT DEFAULT 0,
    status VARCHAR(30) DEFAULT 'PENDING',
    admin_note TEXT,
    pray_with_me_testimonies TEXT,
    translation_testimonies TEXT,
    partnership_testimonies TEXT,
    salvation_testimonies TEXT,
    healing_testimonies TEXT,
    others_testimonies TEXT,
    member_name VARCHAR(255),
    category_details VARCHAR(255)
);

-- 6. Outreach Reports
CREATE TABLE IF NOT EXISTS loveworld_reports.outreach_reports (
    id BIGSERIAL PRIMARY KEY,
    submitted_date DATE,
    submitter_email VARCHAR(255),
    zone_name VARCHAR(255),
    category VARCHAR(150),
    locations TEXT,
    story TEXT,
    media_count INT DEFAULT 0,
    status VARCHAR(30) DEFAULT 'PENDING',
    admin_note TEXT,
    httn_magazine_testimonies_outreaches TEXT,
    magazines_used INT DEFAULT 0,
    people_involved INT DEFAULT 0,
    total_attendance INT DEFAULT 0,
    souls_saved INT DEFAULT 0,
    outreach_testimonies TEXT,
    follow_up_plan TEXT,
    healing_translations_achieved INT DEFAULT 0,
    healing_outreaches_held INT DEFAULT 0,
    healing_outreach_locations TEXT,
    healing_media_submitted INT DEFAULT 0
);

-- 7. Magazine Reports
CREATE TABLE IF NOT EXISTS loveworld_reports.magazine_reports (
    id BIGSERIAL PRIMARY KEY,
    submitted_date DATE,
    submitter_email VARCHAR(255),
    zone_name VARCHAR(255),
    language VARCHAR(50),
    ordered_copies INT DEFAULT 0,
    received_copies INT DEFAULT 0,
    receipt_status VARCHAR(20),
    reason TEXT,
    challenges_faced TEXT,
    healing_outreaches INT DEFAULT 0,
    status VARCHAR(30) DEFAULT 'PENDING',
    is_adult BOOLEAN DEFAULT FALSE,
    is_teevolution BOOLEAN DEFAULT FALSE,
    is_kids_magazine BOOLEAN DEFAULT FALSE,
    admin_note TEXT,
    monthly_minimum_order INT DEFAULT 0,
    monthly_minimum_order_amount_paid NUMERIC(12,2) DEFAULT 0.00,
    cumulative_sponsored_amount_paid NUMERIC(12,2) DEFAULT 0.00,
    proof_of_payment INT DEFAULT 0,
    adult_copies INT DEFAULT 0,
    adult_languages VARCHAR(255),
    teens_copies INT DEFAULT 0,
    teens_languages VARCHAR(255),
    kids_copies INT DEFAULT 0,
    kids_languages VARCHAR(255),
    monthly_copies_ordered INT DEFAULT 0,
    praise_reports TEXT,
    dates_received VARCHAR(255)
);

-- 8. Magazine Types
CREATE TABLE IF NOT EXISTS loveworld_reports.magazine_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    price DOUBLE PRECISION,
    description TEXT
);

-- 9. Magazine Orders
CREATE TABLE IF NOT EXISTS loveworld_reports.magazine_orders (
    id BIGSERIAL PRIMARY KEY,
    zone VARCHAR(255),
    magazine_type VARCHAR(255),
    quantity INT,
    total_amount DOUBLE PRECISION,
    status VARCHAR(50),
    ordered_by VARCHAR(255),
    ordered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_address TEXT,
    country VARCHAR(100),
    state_province VARCHAR(100),
    postal_code VARCHAR(50),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    shipping_cost DOUBLE PRECISION,
    tax_amount DOUBLE PRECISION,
    weight DOUBLE PRECISION,
    invoice_sent_at TIMESTAMP,
    payment_confirmed_at TIMESTAMP,
    production_start_at TIMESTAMP,
    production_deadline TIMESTAMP,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    feedback_received BOOLEAN DEFAULT FALSE,
    delay_report TEXT
);

-- 10. Payments
CREATE TABLE IF NOT EXISTS loveworld_reports.payments (
    id BIGSERIAL PRIMARY KEY,
    proof_url TEXT,
    status VARCHAR(50),
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_id BIGINT REFERENCES loveworld_reports.magazine_orders(id) ON DELETE SET NULL,
    method VARCHAR(50),
    transaction_id VARCHAR(255),
    amount DOUBLE PRECISION
);

-- 11. Testimonies
CREATE TABLE IF NOT EXISTS loveworld_reports.testimonies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT REFERENCES loveworld_reports.users(id) ON DELETE SET NULL,
    category VARCHAR(100) NOT NULL,
    media_type VARCHAR(50),
    user_avatar_url TEXT,
    country VARCHAR(100),
    state VARCHAR(100),
    status VARCHAR(50) DEFAULT 'PENDING',
    is_featured BOOLEAN DEFAULT FALSE,
    featured_type VARCHAR(50),
    likes_count INT DEFAULT 0,
    views_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 12. Testimony Media URLs
CREATE TABLE IF NOT EXISTS loveworld_reports.testimony_media_urls (
    testimony_id BIGINT NOT NULL REFERENCES loveworld_reports.testimonies(id) ON DELETE CASCADE,
    url TEXT
);

-- 13. Comments
CREATE TABLE IF NOT EXISTS loveworld_reports.comments (
    id BIGSERIAL PRIMARY KEY,
    testimony_id BIGINT REFERENCES loveworld_reports.testimonies(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES loveworld_reports.users(id) ON DELETE SET NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 14. Prayer Wall
CREATE TABLE IF NOT EXISTS loveworld_reports.prayer_wall (
    id BIGSERIAL PRIMARY KEY,
    testimony_id BIGINT REFERENCES loveworld_reports.testimonies(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES loveworld_reports.users(id) ON DELETE SET NULL,
    prayer TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 15. Testimony Likes
CREATE TABLE IF NOT EXISTS loveworld_reports.testimony_likes (
    id BIGSERIAL PRIMARY KEY,
    testimony_id BIGINT REFERENCES loveworld_reports.testimonies(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES loveworld_reports.users(id) ON DELETE CASCADE,
    CONSTRAINT uk_testimony_user UNIQUE (testimony_id, user_id)
);

-- 16. Notifications
CREATE TABLE IF NOT EXISTS loveworld_reports.notifications (
    id BIGSERIAL PRIMARY KEY,
    message TEXT,
    target_role VARCHAR(50),
    user_email VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 17. Audit Logs
CREATE TABLE IF NOT EXISTS loveworld_reports.audit_logs (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(255),
    user_id BIGINT,
    action VARCHAR(255),
    module VARCHAR(255),
    prev TEXT,
    updated TEXT,
    details TEXT
);

-- 18. Report Backups
CREATE TABLE IF NOT EXISTS loveworld_reports.report_backups (
    id BIGSERIAL PRIMARY KEY,
    original_id BIGINT,
    report_type VARCHAR(100),
    report_data TEXT,
    deleted_by VARCHAR(255),
    deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- INITIAL DATA INSERTION
-- =============================================================================

-- Insert registered users (Admins, Zonal Managers, Global Partnership Manager)
-- Password hash corresponds to 'admin123' (bcrypt)
INSERT INTO loveworld_reports.users 
(first_name, last_name, email, password, role, region, status, joined_date, report_count)
VALUES
('System', 'Administrator', 'admin@loveworld.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'admin', 'Global', 'active', '2026-03-24', 0),
('Sharon', 'Shelke', 'sharonshelke1@gmail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'admin', 'Global', 'active', CURRENT_DATE, 0),
('sharon', '', 'sharon@loveworld.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'zonal', 'Global', 'active', '2026-03-23', 0),
('sister', '', 'upasana@loveworld.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'zonal', 'Global', 'active', '2026-03-24', 0),
('sis', 'Nissi', 'nissi@loveworld.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'zonal', 'Global', 'active', '2026-03-24', 0),
('Global', 'Partnership Manager', 'global@loveworld.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'global', 'Global', 'active', '2026-03-26', 0),
('Zonal', 'Partnership Manager', 'zonal@loveworld.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'zonal', 'North America', 'active', '2026-03-26', 0)
ON CONFLICT (email) DO UPDATE SET
  status = 'active',
  role = EXCLUDED.role;

