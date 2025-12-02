-- Replace legacy CLASSIC theme with DEFAULT and relax check constraint
UPDATE style_info
SET theme = 'DEFAULT'
WHERE theme = 'CLASSIC';

ALTER TABLE style_info
    DROP CONSTRAINT IF EXISTS style_info_theme_check;

ALTER TABLE style_info
    ADD CONSTRAINT style_info_theme_check
        CHECK (theme IN ('MODERN', 'DEFAULT'));
