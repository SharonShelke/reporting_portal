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
ADD COLUMN sponsored_teenspiration_kidspiration TEXT DEFAULT NULL;

-- ALTER magazine_reports (New Magazines Report Updates)
ALTER TABLE magazine_reports
ADD COLUMN adult_copies INT DEFAULT 0,
ADD COLUMN adult_languages VARCHAR(255) DEFAULT NULL,
ADD COLUMN teens_copies INT DEFAULT 0,
ADD COLUMN teens_languages VARCHAR(255) DEFAULT NULL,
ADD COLUMN kids_copies INT DEFAULT 0,
ADD COLUMN kids_languages VARCHAR(255) DEFAULT NULL,
ADD COLUMN monthly_copies_ordered INT DEFAULT 0,
ADD COLUMN praise_reports TEXT DEFAULT NULL;

-- Rename meeting columns in zone_weekly_reports to reflect Executive Minister
ALTER TABLE zone_weekly_reports RENAME COLUMN zonal_pastor_directors_meeting TO zonal_pastor_executive_ministers_meeting;
ALTER TABLE zone_weekly_reports RENAME COLUMN zonal_manager_directors_meeting TO zonal_manager_executive_ministers_meeting;

-- Alter partnership_reports columns to allow both text and numbers
ALTER TABLE partnership_reports MODIFY COLUMN zonal_partnership TEXT DEFAULT NULL;
ALTER TABLE partnership_reports MODIFY COLUMN groups_partnership TEXT DEFAULT NULL;
ALTER TABLE partnership_reports MODIFY COLUMN churches_partnership TEXT DEFAULT NULL;
ALTER TABLE partnership_reports MODIFY COLUMN cell_partnership TEXT DEFAULT NULL;

-- Add sponsored Teenspiration and Kidspiration fields
ALTER TABLE partnership_reports ADD COLUMN sponsored_teenspiration TEXT DEFAULT NULL;
ALTER TABLE partnership_reports ADD COLUMN sponsored_kidspiration TEXT DEFAULT NULL;

-- Add salvation, healing, and others testimonies columns to testimonial_reports
ALTER TABLE testimonial_reports 
ADD COLUMN salvation_testimonies TEXT DEFAULT NULL,
ADD COLUMN healing_testimonies TEXT DEFAULT NULL,
ADD COLUMN others_testimonies TEXT DEFAULT NULL;

-- Add dates_received and outreach_locations to magazine_reports
ALTER TABLE magazine_reports 
ADD COLUMN dates_received VARCHAR(255) DEFAULT NULL,
ADD COLUMN outreach_locations TEXT DEFAULT NULL;

-- Add new outreach fields to outreach_reports
ALTER TABLE outreach_reports 
ADD COLUMN magazines_used INT DEFAULT 0,
ADD COLUMN people_involved INT DEFAULT 0,
ADD COLUMN total_attendance INT DEFAULT 0,
ADD COLUMN souls_saved INT DEFAULT 0,
ADD COLUMN outreach_testimonies TEXT DEFAULT NULL,
ADD COLUMN follow_up_plan TEXT DEFAULT NULL;



