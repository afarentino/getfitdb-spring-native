package com.github.afarentino.getfitdb;

import com.github.afarentino.getfitdb.model.ExerciseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
public class RecordService implements RecordRepository {
    private static final Logger logger = LoggerFactory.getLogger(RecordService.class);

    private static Path currentWorkingDir = Paths.get("").toAbsolutePath();
    private static final String fileSeparator = File.separator;
    public static final String url = "jdbc:sqlite:" + currentWorkingDir + fileSeparator + "getfit.db";


    @Autowired
    JdbcTemplate template;

    @Autowired
    public RecordService(JdbcTemplate template) {
        this.template = template;
    }

    private final RowMapper<ExerciseRecord> rowMapper = (rs, rowNum) -> new ExerciseRecord(
            rs.getString("Start"),
            rs.getDouble("Distance"),
            rs.getDouble( "ZoneTime"),
            rs.getInt("ElapsedTime"),
            rs.getInt("CaloriesBurned"),
            rs.getInt("AvgHeartRate"),
            rs.getInt("MaxHeartRate"),
            rs.getString("Notes")
    );

    public List<ExerciseRecord> findAll() {
        String findAllRecords = """
                select * from records
                """;

        return template.query(findAllRecords, rowMapper);
    }


    /**
     * Create a new Database if needed
     * @see: https://www.sqlitetutorial.net/sqlite-java/
     */
    public static void createNewDatabase() {
        try ( Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                logger.info("The driver name is " + meta.getDriverName());
                logger.info("Connected to records.db SQLite database.");
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
    }

    public static boolean tableExists(String tableName) {
        String sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?";

        boolean exists = false;
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement pstmt = con.prepareStatement(sql))
        {
            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name").toLowerCase();
                exists = tableName.toLowerCase().contains(name) ? true : false;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return exists;
    }

    public static void dropTable() {
        String sql = "DROP TABLE IF EXISTS records;";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static void createNewTable() {
        // TODO: Externalize sql using application properties
        // allows schema to change without changing code.
        String sql = """
				CREATE TABLE IF NOT EXISTS records (
				Start TEXT PRIMARY KEY NOT NULL,
				Distance DOUBLE,
				ZoneTime DOUBLE,
				ElapsedTime INTEGER,
				CaloriesBurned INTEGER,
				AvgHeartRate INTEGER,
				MaxHeartRate INTEGER,
				Notes TEXT);""";

        if (tableExists("records")) {
            dropTable();
        }

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static boolean rowExists(Connection con, String primaryKey) throws SQLException {
        String sql = "SELECT COUNT(*) FROM records WHERE Start = ?;";
        int exists = 0;
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, "'" + primaryKey + "'");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                exists = rs.getInt(1);
            }
        }
        return (exists == 1);
    }

    public static void updateRow(Connection con, String[] values) throws SQLException {
        String sql = """
 				UPDATE records
 				SET Distance = ?, ZoneTime = ?, ElapsedTime = ?, CaloriesBurned = ?, 
 				AvgHeartRate = ?, MaxHeartRate = ?, Notes = ? 
 				WHERE Start = ?;""";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(8, values[0]);

            if (values[1].isEmpty()) {
                pstmt.setNull(1, Types.NULL);
            } else {
                pstmt.setDouble(1, Double.parseDouble(values[1]));
            }

            if (values[2].isEmpty()) {
                pstmt.setNull(2, Types.NULL);
            } else {
                pstmt.setDouble(2, Double.parseDouble(values[2]));
            }

            if (values[3].isEmpty()) {
                pstmt.setNull(3, Types.NULL);
            } else {
                pstmt.setInt(3, Integer.parseInt(values[3]));
            }

            if (values[4].isEmpty()) {
                pstmt.setNull(4, Types.NULL);
            } else {
                pstmt.setInt(4, Integer.parseInt(values[4]));
            }

            if (values[5].isEmpty()) {
                pstmt.setNull(5, Types.NULL);
            } else {
                pstmt.setInt(5, Integer.parseInt(values[5]));
            }

            if (values[6].isEmpty()) {
                pstmt.setNull(6, Types.NULL);
            } else {
                pstmt.setInt(6, Integer.parseInt(values[6]));
            }

            // NOTES are optional so handle them accordingly
            if (values.length == 8) {
                pstmt.setString(7, values[7]);
            } else {
                pstmt.setString(7, "");
            }
            pstmt.executeUpdate();
        }
    }

    public static void addRow(Connection con, String[] values) throws SQLException {
        String sql = """
				INSERT INTO records(
					Start,
					Distance,
					ZoneTime,
					ElapsedTime,
					CaloriesBurned,
					AvgHeartRate,
					MaxHeartRate,
					Notes) VALUES (?,?,?,?,?,?,?,?);""";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, values[0]);

            if (values[1].isEmpty()) {
                pstmt.setNull(2, Types.NULL);
            } else {
                pstmt.setDouble(2, Double.parseDouble(values[1]));
            }

            if (values[2].isEmpty()) {
                pstmt.setNull(3, Types.NULL);
            } else {
                pstmt.setDouble(3, Double.parseDouble(values[2]));
            }

            if (values[3].isEmpty()) {
                pstmt.setNull(4, Types.NULL);
            } else {
                pstmt.setInt(4, Integer.parseInt(values[3]));
            }

            if (values[4].isEmpty()) {
                pstmt.setNull(5, Types.NULL);
            } else {
                pstmt.setInt(5, Integer.parseInt(values[4]));
            }

            if (values[5].isEmpty()) {
                pstmt.setNull(6, Types.NULL);
            } else {
                pstmt.setInt(6, Integer.parseInt(values[5]));
            }

            if (values[6].isEmpty()) {
                pstmt.setNull(7, Types.NULL);
            } else {
                pstmt.setInt(7, Integer.parseInt(values[6]));
            }

            // NOTES are optional so handle them accordingly
            if (values.length == 8) {
                pstmt.setString(8, values[7]);
            } else {
                pstmt.setString(8, "");
            }
            pstmt.executeUpdate();
        }
    }

    public static void updateTable(String fileName) throws SQLException {
        Path path = Path.of(fileName);
        AtomicBoolean isFirst = new AtomicBoolean(true);
        AtomicReference<Connection> rollbackCon = new AtomicReference(null);

        try ( Connection con = DriverManager.getConnection(url);
              Stream<String> lines = Files.lines(path)) {

            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            con.setAutoCommit(false);

            lines.forEach(s ->
            {
                if (isFirst.get() == true) {
                    isFirst.set(false);
                } else {
                    String[] values = s.split(",");
                    try {
                        if (rowExists(con, values[0]) == true) {
                            updateRow(con, values);
                        } else {
                            addRow(con, values);
                        }
                    } catch (SQLException e) {
                        logger.error(e.getMessage());
                        logger.error("Transaction will be rolled back");
                        rollbackCon.set(con);
                    }
                }
            });
            con.commit();
            logger.info("Changes found in  " + fileName + " added to records");
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
            if (rollbackCon.get() != null) {
                rollbackCon.get().rollback();
            }
        }
    }

}
