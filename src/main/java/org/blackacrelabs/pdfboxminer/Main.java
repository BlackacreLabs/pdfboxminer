package org.blackacrelabs.pdfboxminer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.blackacrelabs.pdfboxminer.TextEmitter.Format;

public class Main {

	final static String USAGE = "USAGE: [--format FORMAT] <PDF File>";

	public static void main(String[] args) {
		// Set up command line options
		Options options = new Options();
		Option formatOption = new Option("f", true, "output format");
		formatOption.setLongOpt("format");
		options.addOption(formatOption);

		// Parse command line options
		try {
			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse(options, args);
			// Default format
			Format format = Format.values()[0];
			String stringFormat = cmd.getOptionValue("format");
			if (stringFormat != null) {
				try {
					format = Format.valueOf(stringFormat.toUpperCase());
				} catch (IllegalArgumentException e) {
					System.err.print("Valid formats include: ");
					for (Format validFormat : Format.values()) {
						System.err.print("\"" + validFormat.toString() + "\" ");
					}
					System.err.println();
					System.exit(1);
				}
			}

			// PDF file
			String[] remaining = cmd.getArgs();
			if (remaining.length < 1) {
				System.err.println(USAGE);
				System.exit(1);
			} else {
				try {
					TextEmitter emitter = new TextEmitter();
					PDDocument document = PDDocument.load(remaining[0]);
					emitter.tokenize(document);
					document.close();
					// Print output
					System.out.println(emitter.getOutput(format));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (ParseException exp) {
			System.err.println(exp.getMessage());
			System.exit(1);
		}
	}
}