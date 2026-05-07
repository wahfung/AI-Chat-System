-- Set client encoding to UTF-8
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Initialize database with UTF-8 encoding
ALTER DATABASE ai_chat CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    avatar VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create conversations table
CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create chat_messages table
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation_id (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert demo users
INSERT INTO users (id, username, password, nickname, avatar, created_at)
VALUES
    (1, 'admin', '123456', '管理员', '/default-avatar.png', NOW()),
    (2, 'demo', '123456', '演示用户', '/default-avatar.png', NOW())
ON DUPLICATE KEY UPDATE username=username;

-- Insert sample conversation
INSERT INTO conversations (id, title, user_id, created_at, updated_at)
VALUES (1, '欢迎使用AI助手', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE title=title;

-- Insert sample messages
INSERT INTO chat_messages (id, conversation_id, role, content, created_at)
VALUES
    (1, 1, 'USER', '你好，请介绍一下你自己', NOW()),
    (2, 1, 'ASSISTANT', '你好！我是一个基于DeepSeek模型的AI助手。我可以帮助你回答问题、进行对话、提供建议等。有什么我可以帮助你的吗？', NOW())
ON DUPLICATE KEY UPDATE content=content;
