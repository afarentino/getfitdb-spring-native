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
import java.util.List;

@SpringBootApplication
public class GetfitdbApplication implements ApplicationRunner {
	private static final Logger logger = LoggerFactory.getLogger(GetfitdbApplication.class);

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

		// Construct our TextFileService
		File csv = new File(fileName);
		if (csv != null && csv.exists() ) {
			logger.info("CSV used is: " + csv.getName());
		} else {
			logger.error("CSV file does not exist.");
			SpringApplication.exit(this.ctx);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(GetfitdbApplication.class, args);
	}
}
