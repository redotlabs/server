-- Rename core tables
ALTER TABLE customers RENAME TO redot_apps;
ALTER TABLE customer_inquiries RENAME TO redot_app_inquiries;

-- Rename foreign-key columns referencing customers
ALTER TABLE cms_members RENAME COLUMN customer_id TO redot_app_id;
ALTER TABLE domains RENAME COLUMN customer_id TO redot_app_id;
ALTER TABLE style_info RENAME COLUMN customer_id TO redot_app_id;
ALTER TABLE site_settings RENAME COLUMN customer_id TO redot_app_id;
ALTER TABLE redot_app_inquiries RENAME COLUMN customer_id TO redot_app_id;

-- Update constraints to the new naming
ALTER TABLE cms_members RENAME CONSTRAINT uk_cms_members_customer_email TO uk_cms_members_redot_app_email;
ALTER TABLE cms_members RENAME CONSTRAINT fk_cms_members_customer TO fk_cms_members_redot_app;
ALTER TABLE redot_apps RENAME CONSTRAINT fk_customers_owner TO fk_redot_apps_owner;
ALTER TABLE domains RENAME CONSTRAINT fk_domains_customer TO fk_domains_redot_app;
ALTER TABLE style_info RENAME CONSTRAINT fk_style_info_customer TO fk_style_info_redot_app;
ALTER TABLE site_settings RENAME CONSTRAINT fk_site_settings_customer TO fk_site_settings_redot_app;
ALTER TABLE redot_app_inquiries RENAME CONSTRAINT fk_customer_inquiries_customer TO fk_redot_app_inquiries_redot_app;
ALTER TABLE redot_app_inquiries RENAME CONSTRAINT fk_customer_inquiries_assignee TO fk_redot_app_inquiries_assignee;
