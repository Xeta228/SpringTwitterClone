CREATE TABLE user_subscriptions (
    channel_id int NOT NULL REFERENCES usr,
    subscriber_id int NOT NULL REFERENCES usr,
    PRIMARY KEY (channel_id, subscriber_id)
)