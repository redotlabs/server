-- Safe rename of core tables
DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.tables WHERE table_name='customers'
        ) THEN
            ALTER TABLE customers RENAME TO redot_apps;
        END IF;
    END $$;

DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.tables WHERE table_name='customer_inquiries'
        ) THEN
            ALTER TABLE customer_inquiries RENAME TO redot_app_inquiries;
        END IF;
    END $$;



-- Safe rename of foreign key columns
DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name='cms_members' AND column_name='customer_id'
        ) THEN
            ALTER TABLE cms_members RENAME COLUMN customer_id TO redot_app_id;
        END IF;
    END $$;

DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name='domains' AND column_name='customer_id'
        ) THEN
            ALTER TABLE domains RENAME COLUMN customer_id TO redot_app_id;
        END IF;
    END $$;

DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name='style_info' AND column_name='customer_id'
        ) THEN
            ALTER TABLE style_info RENAME COLUMN customer_id TO redot_app_id;
        END IF;
    END $$;

DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name='site_settings' AND column_name='customer_id'
        ) THEN
            ALTER TABLE site_settings RENAME COLUMN customer_id TO redot_app_id;
        END IF;
    END $$;

DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name='redot_app_inquiries' AND column_name='customer_id'
        ) THEN
            ALTER TABLE redot_app_inquiries RENAME COLUMN customer_id TO redot_app_id;
        END IF;
    END $$;



-- Safe rename of constraints
DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name='uk_cms_members_customer_email'
        ) THEN
            ALTER TABLE cms_members
                RENAME CONSTRAINT uk_cms_members_customer_email
                    TO uk_cms_members_redot_app_email;
        END IF;
    END $$;


DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name='fk_cms_members_customer'
        ) THEN
            ALTER TABLE cms_members
                RENAME CONSTRAINT fk_cms_members_customer
                    TO fk_cms_members_redot_app;
        END IF;
    END $$;


DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name='fk_customers_owner'
        ) THEN
            ALTER TABLE redot_apps
                RENAME CONSTRAINT fk_customers_owner
                    TO fk_redot_apps_owner;
        END IF;
    END $$;


DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name='fk_domains_customer'
        ) THEN
            ALTER TABLE domains
                RENAME CONSTRAINT fk_domains_customer
                    TO fk_domains_redot_app;
        END IF;
    END $$;


DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name='fk_style_info_customer'
        ) THEN
            ALTER TABLE style_info
                RENAME CONSTRAINT fk_style_info_customer
                    TO fk_style_info_redot_app;
        END IF;
    END $$;


DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name='fk_site_settings_customer'
        ) THEN
            ALTER TABLE site_settings
                RENAME CONSTRAINT fk_site_settings_customer
                    TO fk_site_settings_redot_app;
        END IF;
    END $$;


DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name='fk_customer_inquiries_customer'
        ) THEN
            ALTER TABLE redot_app_inquiries
                RENAME CONSTRAINT fk_customer_inquiries_customer
                    TO fk_redot_app_inquiries_redot_app;
        END IF;
    END $$;


DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name='fk_customer_inquiries_assignee'
        ) THEN
            ALTER TABLE redot_app_inquiries
                RENAME CONSTRAINT fk_customer_inquiries_assignee
                    TO fk_redot_app_inquiries_assignee;
        END IF;
    END $$;
