CREATE TABLE Story (
    id SERIAL NOT NULL PRIMARY KEY,
    version_major SMALLINT, -- breaking changes
    version_minor SMALLINT, -- optional additions
    version_patch SMALLINT, -- cosmetic changes
    about TEXT,
    language VARCHAR(2),
    -- tags separate table?
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);