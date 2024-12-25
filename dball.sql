-- Create table to show previos states and new changes made

CREATE DATABASE WebPageChanges;

USE WebPageChanges;

CREATE TABLE PageChanges (
    id INT AUTO_INCREMENT PRIMARY KEY,
    change_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    previous_state TEXT,
    new_state TEXT
);
