-- Set client encoding to UTF-8
SET NAMES utf8mb4;

-- Insert demo user (password: 123456)
INSERT IGNORE INTO users (id, username, password, nickname, avatar, created_at)
VALUES (1, 'admin', '123456', '管理员', '/default-avatar.png', NOW());

INSERT IGNORE INTO users (id, username, password, nickname, avatar, created_at)
VALUES (2, 'demo', '123456', '演示用户', '/default-avatar.png', NOW());

-- Insert sample conversation
INSERT IGNORE INTO conversations (id, title, user_id, created_at, updated_at)
VALUES (1, '欢迎使用AI助手', 1, NOW(), NOW());

-- Insert sample messages
INSERT IGNORE INTO chat_messages (id, conversation_id, role, content, created_at)
VALUES (1, 1, 'USER', '你好，请介绍一下你自己', NOW());

INSERT IGNORE INTO chat_messages (id, conversation_id, role, content, created_at)
VALUES (2, 1, 'ASSISTANT', '你好！我是一个基于DeepSeek模型的AI助手。我可以帮助你回答问题、进行对话、提供建议等。有什么我可以帮助你的吗？', NOW());
