CREATE TABLE notification (
    id SERIAL PRIMARY KEY,
    destination TEXT NOT NULL,
    content TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    subject TEXT NOT NULL
); -- semicolon added here