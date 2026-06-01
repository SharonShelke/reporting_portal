-- Migration script to add security question fields to the users table
ALTER TABLE users 
ADD COLUMN security_answer1 VARCHAR(255) DEFAULT NULL,
ADD COLUMN security_answer2 VARCHAR(255) DEFAULT NULL,
ADD COLUMN security_answer3 VARCHAR(255) DEFAULT NULL;

-- Migration script to move healing crusade sponsorship to partnership reports
ALTER TABLE zone_weekly_reports DROP COLUMN IF EXISTS healing_crusade_sponsorship;
ALTER TABLE partnership_reports ADD COLUMN healing_crusade_sponsorship DECIMAL(15,2) DEFAULT 0;

-- Alter statements to add the 11 new fields/questions
-- ALTER zone_weekly_reports (Zonal Report)
ALTER TABLE zone_weekly_reports 
ADD COLUMN participation_pray_with_me TEXT DEFAULT NULL,
ADD COLUMN total_registration_hslhs INT DEFAULT 0,
ADD COLUMN herald_conference VARCHAR(100) DEFAULT NULL;

-- ALTER testimonial_reports (Testimonies)
ALTER TABLE testimonial_reports 
ADD COLUMN pray_with_me_testimonies TEXT DEFAULT NULL,
ADD COLUMN translation_testimonies TEXT DEFAULT NULL,
ADD COLUMN partnership_testimonies TEXT DEFAULT NULL;

-- ALTER outreach_reports (Outreach Report)
ALTER TABLE outreach_reports 
ADD COLUMN httn_magazine_testimonies_outreaches TEXT DEFAULT NULL;

-- ALTER magazine_reports (Magazine)
ALTER TABLE magazine_reports 
ADD COLUMN monthly_minimum_order INT DEFAULT 0,
ADD COLUMN amount_paid_magazine DECIMAL(12,2) DEFAULT 0.00;

-- ALTER partnership_reports (Partnership Report)
ALTER TABLE partnership_reports 
ADD COLUMN group_pastors_milestones TEXT DEFAULT NULL,
ADD COLUMN sponsored_teenspiration_kidsspiration TEXT DEFAULT NULL;
