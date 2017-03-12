CREATE TABLE Story (
    id SERIAL NOT NULL PRIMARY KEY,
    version_major TINYINT, -- breaking changes
    version_minor TINYINT, -- optional additions
    version_patch TINYINT, -- cosmetic changes
    about TEXT,
    language VARCHAR(2),
    -- tags separate table?
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);