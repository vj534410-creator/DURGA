-- ====================================================================
-- DURGA Safety & Support Platform — Seed Data
-- ====================================================================

-- 1. ROLES
INSERT INTO roles (id, name, description) VALUES
('ROLE_USER', 'Standard User', 'Victims, witnesses, or citizens reporting incidents or tracking cases'),
('ROLE_POLICE_OFFICER', 'Police Investigating Officer', 'Authorized police personnel reviewing and investigating assigned cases'),
('ROLE_SENIOR_OFFICER', 'Senior Police Superintendent', 'Supervisory authority overseeing jurisdictional complaints'),
('ROLE_AUTHORITY_REVIEWER', 'State Forensic & Legal Auditor', 'Independent oversight and audit compliance staff'),
('ROLE_ADMIN', 'Central Platform Administrator', 'System management, emergency directory management, and user auditing')
ON CONFLICT (id) DO NOTHING;

-- 2. COMPLAINT CATEGORIES
INSERT INTO complaint_categories (id, name_en, name_gu, name_hi, severity_level) VALUES
('CYBER_CRIME', 'Online Harassment & Cyber Stalking', 'ઓનલાઇન પજવણી અને સાયબર સ્ટોકિંગ', 'ऑनलाइन उत्पीड़न और साइबर स्टॉकिंग', 'HIGH'),
('DOMESTIC_VIOLENCE', 'Domestic Violence & Abuse', 'ઘરેલું હિંસા અને અત્યાચાર', 'घरेलू हिंसा और दुर्व्यવહાર', 'CRITICAL'),
('STALKING', 'Physical Stalking & Threat', 'પીછો કરવો અને શારીરિક ધમકી', 'पीछा करना और शारीरिक धमकी', 'HIGH'),
('WORKPLACE', 'Workplace & Institutional Harassment', 'કાર્યસ્થળ અથવા સંસ્થાકીય પજવણી', 'कार्यस्थल या संस्थागत उत्पीड़न', 'MEDIUM'),
('CHILD_DISTRESS', 'Child Protection & Distress', 'બાળ સુરક્ષા અને કટોકટી', 'बाल सुरक्षा और संकट', 'CRITICAL'),
('PUBLIC_HARASSMENT', 'Public Transit / Street Harassment', 'જાહેર સ્થળે અથવા મુસાફરીમાં છેડતી', 'सार्वजनिक स्थान / सड़क पर छेड़छाड़', 'HIGH')
ON CONFLICT (id) DO NOTHING;

-- 3. SAFETY & EMERGENCY RESOURCES (Verified Official Helplines in India)
INSERT INTO help_categories (id, name, icon_name) VALUES
('CAT_POLICE', 'Police & Dispatch', 'shield'),
('CAT_WOMEN', 'Women Safety & Shelter', 'favorite'),
('CAT_CHILD', 'Child Protection', 'child_care'),
('CAT_CYBER', 'Cyber Crime & Fraud', 'laptop'),
('CAT_HEALTH', 'Medical Ambulance', 'local_hospital'),
('CAT_LEGAL', 'Legal Aid & Counsel', 'gavel')
ON CONFLICT (id) DO NOTHING;

INSERT INTO safety_resources (id, category_id, name, phone_number, region, description, website_url, is_toll_free, is_active) VALUES
('RES_112', 'CAT_POLICE', 'National Emergency Response Support System (ERSS)', '112', 'All India', 'Single emergency helpline for Police, Fire, and Ambulance.', 'https://112.gov.in', TRUE, TRUE),
('RES_1091', 'CAT_WOMEN', 'Women in Distress Emergency Helpline', '1091', 'National', '24x7 toll-free emergency response helpline for women facing violence or harassment.', 'https://ncw.nic.in', TRUE, TRUE),
('RES_181', 'CAT_WOMEN', 'Women Domestic Violence Helpline (Abhayam / WHL)', '181', 'State / National', 'Immediate rescue, legal aid, and counseling for women.', 'https://wcd.nic.in', TRUE, TRUE),
('RES_1098', 'CAT_CHILD', 'CHILDLINE India (National Child Helpline)', '1098', 'All India', '24x7 emergency helpline for children in need of protection or care.', 'https://childlineindia.org', TRUE, TRUE),
('RES_1930', 'CAT_CYBER', 'National Cybercrime Reporting Helpline', '1930', 'All India (MHA)', 'Reporting financial scams, UPI fraud, and online abuse.', 'https://cybercrime.gov.in', TRUE, TRUE),
('RES_108', 'CAT_HEALTH', 'Emergency Medical & Ambulance Service', '108', 'National / State', 'Free 24x7 emergency medical transport and trauma hospital care.', 'https://nhm.gov.in', TRUE, TRUE)
ON CONFLICT (id) DO NOTHING;
