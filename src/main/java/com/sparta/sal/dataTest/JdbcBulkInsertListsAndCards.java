/*
package com.sparta.sal.dataTest;

import java.sql.*;
import java.util.ArrayList;

// 중복 코드를 최소화하고 효율성을 높인 예시입니다.
public class JdbcBulkInsertListsAndCards {
    private static final String URL = "jdbc:mysql://localhost:3306/test";
    private static final String USER = "root";
    private static final String PASSWORD = "gjsl12399!";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);
            Long boardId = 2L;

            insertLists(conn, boardId);
            insertCards(conn, boardId);

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Long checkOrInsertBoard(Connection conn) throws SQLException {
        String checkBoardSQL = "SELECT id FROM boards WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkBoardSQL)) {
            pstmt.setLong(1, 1L); // Check for a specific board ID
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("id"); // Existing board ID
            } else {
                String insertBoardSQL = "INSERT INTO boards (board_title) VALUES (?)";
                try (PreparedStatement insertBoardPstmt = conn.prepareStatement(insertBoardSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insertBoardPstmt.setString(1, "Your title Name");
                    insertBoardPstmt.executeUpdate();

                    try (ResultSet generatedKeys = insertBoardPstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getLong(1); // New board ID
                        }
                    }
                }
            }
        }
        return null; // Fallback
    }

    private static void insertLists(Connection conn, Long boardId) throws SQLException {
        String insertListSQL = "INSERT INTO list (title, sequence, board_id) VALUES (?, ?, ?)";
        try (PreparedStatement listPstmt = conn.prepareStatement(insertListSQL)) {
            for (int i = 1; i <= 10; i++) {
                listPstmt.setString(1, "List " + i);
                listPstmt.setInt(2, i);
                listPstmt.setLong(3, boardId);
                listPstmt.addBatch();
            }
            listPstmt.executeBatch();
        }
    }

    private static void insertCards(Connection conn, Long boardId) throws SQLException {
        String insertCardSQL = "INSERT INTO card (card_title, card_explain, deadline, list_id, is_deleted) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement cardPstmt = conn.prepareStatement(insertCardSQL)) {
            int batchSize = 10000; // 한 번에 삽입할 카드 수
            int totalCards = 1000000; // 총 카드 수
            int iterations = totalCards / batchSize; // 반복 횟수
            int count = 0;

            String getListIdSQL = "SELECT id FROM list WHERE board_id = ?";
            try (PreparedStatement getListIdPstmt = conn.prepareStatement(getListIdSQL)) {
                getListIdPstmt.setLong(1, boardId);
                try (ResultSet resultSet = getListIdPstmt.executeQuery()) {
                    // ArrayList 사용하여 리스트 ID 저장
                    ArrayList<Long> listIds = new ArrayList<>();
                    while (resultSet.next()) {
                        listIds.add(resultSet.getLong("id"));
                    }

                    // 카드 삽입
                    for (int i = 0; i < iterations; i++) {
                        for (int j = 0; j < batchSize; j++) {
                            long listId = listIds.get(j % listIds.size()); // 리스트 ID를 순환하여 사용
                            cardPstmt.setString(1, "Card Title " + (count + 1));
                            cardPstmt.setString(2, "Card Description " + (count + 1));
                            cardPstmt.setTimestamp(3, java.sql.Timestamp.valueOf("2024-12-31 00:00:00"));
                            cardPstmt.setLong(4, listId);
                            cardPstmt.setBoolean(5, false);

                            cardPstmt.addBatch();
                            count++;

                            if (count % batchSize == 0) {
                                cardPstmt.executeBatch();
                            }
                        }
                    }
                }
            }
            cardPstmt.executeBatch(); // 남아 있는 카드 삽입
        }
    }
}
*/
