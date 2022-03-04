package com.github.afarentino.getfitdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import java.nio.file.Files;


@SpringBootApplication
public class GetfitdbApplication implements ApplicationRunner {
	private static final Logger logger = LoggerFactory.getLogger(GetfitdbApplication.class);
	private static Path currentWorkingDir = Paths.get("").toAbsolutePath();
	private static final String fileSeparator = File.separator;
	private static final String url = "jdbc:sqlite:" + currentWorkingDir + fileSeparator + "getfit.db";

	@Autowired
	private ApplicationContext ctx;

	private static String getFileName(ApplicationArguments args) {
		if ( args.containsOption("inFile") ) {
			List<String> values = args.getOptionValues("inFile");
			if (values.size() > 1) {
				throw new IllegalStateException("Only one inFile supported at this time");
			}
			return values.get(0);
		}
		logger.error("Input file required: Specify a file to use using an --inFile= argument");
		return null;
	}

	/**
	 * Create a new Database if needed
	 *
	 * @see: https://www.sqlitetutorial.net/sqlite-java/
	 */
	private static void createNewDatabase() {
		try ( Connection conn = DriverManager.getConnection(url)) {
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				logger.info("The driver name is " + meta.getDriverName());
				logger.info("A new records.db SQLite database has been created.");
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
		}
	}

	private static void createNewTable() {
		//TODO: Try to use different data types
		String sql = """
				CREATE TABLE IF NOT EXISTS records (
				Start TEXT,
				Distance TEXT,
				ZoneTime TEXT,
				ElapsedTime TEXT,
				CaloriesBurned TEXT,
				AvgHeartRate TEXT,
				MaxHeartRate TEXT,
				Notes TEXT);""";

		try (Connection conn = DriverManager.getConnection(url);
			 Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	private static void insertIntoTable(String fileName) throws SQLException {
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

		Path path = Path.of(fileName);

		AtomicBoolean isFirst = new AtomicBoolean(true);
		AtomicReference<Connection> rollbackCon = null;
		try (Connection con = DriverManager.getConnection(url);
			 PreparedStatement pstmt = con.prepareStatement(sql);
			 Stream<String> lines = Files.lines(path)) {
			con.setAutoCommit(false);
			lines.forEach(s ->
			{
				if (isFirst.get() == true) {
					isFirst.set(false);
				} else {
					String[] values = s.split(",");
					try {
						pstmt.setString(1, values[0]);
						pstmt.setString(2, values[1]);
						pstmt.setString(3, values[2]);
						pstmt.setString(4, values[3]);
						pstmt.setString(5, values[4]);
						pstmt.setString(6, values[5]);
						pstmt.setString(7, values[6]);
						if (values.length == 8) {
							pstmt.setString(8, values[7]);
						} else {
							pstmt.setString( 8, "");
						}
						pstmt.executeUpdate();
						con.commit();
					} catch (SQLException e) {
						logger.error(e.getMessage());
						logger.error("Transaction is being rolled back");
						rollbackCon.set(con);
					}
				}
			});
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
			if (rollbackCon.get() != null) {
				rollbackCon.get().rollback();
			}
		}
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		String fileName = getFileName(args);
		if (fileName == null) {
			logger.info("Server mode enabled. No CSV file specified");
			return; // inFile missing startup in server mode as no additional csv file needs to be imported
		}
		if (fileName.endsWith(".csv") == false) {
			logger.error("Please specify a file ending with .csv " + fileName);
			SpringApplication.exit(this.ctx);
		}

		// Connect to or create the records database
		File csv = new File(fileName);
		if (csv != null && csv.exists() ) {
			logger.info("CSV used is: " + csv.getName());
			createNewDatabase();
			createNewTable();
			insertIntoTable(fileName);
		} else {
			logger.error("CSV file does not exist.");
			SpringApplication.exit(this.ctx);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(GetfitdbApplication.class, args);
	}
}