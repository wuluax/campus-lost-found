CREATE TABLE IF NOT EXISTS slf_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type TINYINT NOT NULL,
  description TEXT,
  category VARCHAR(64),
  location VARCHAR(128),
  event_time DATETIME,
  status TINYINT DEFAULT 0,
  publisher_id BIGINT NOT NULL,
  contact_phone VARCHAR(32),
  cover_image VARCHAR(512),
  images_json TEXT,
  create_time DATETIME,
  update_time DATETIME
);

CREATE TABLE IF NOT EXISTS slf_clue (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  item_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  create_time DATETIME
);

CREATE TABLE IF NOT EXISTS slf_message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  from_user_id BIGINT NOT NULL,
  to_user_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  create_time DATETIME
);
