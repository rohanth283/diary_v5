-- Insert test user only if it doesn't exist
INSERT INTO users (username, password) 
SELECT 'user', '$2a$10$gbjIonMW.cY5bbbjkpPpnO3KHcALyazpwpqNPu9QnkA9ifIWImT3O'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'user'); 