package org.examples.mrrtvalidtor;

import static java.lang.System.out;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * Validate provided XML against the provided XSDs.
 */
public class MrrtValidator {
	
	
	/**
	 * Validate provided XML against the provided XSD schema files.
	 *
	 * @param xmlFilePathAndName Path/name of XML file to be validated; should not be null or empty.
	 * @param xsdFilesPathsAndNames XSDs against which to validate the XML; should not be null or
	 *            empty.
	 */
	public static void validateXmlAgainstXsds(final String xmlFilePathAndName, final String[] xsdFilesPathsAndNames) {
		if (xmlFilePathAndName == null || xmlFilePathAndName.isEmpty()) {
			out.println("ERROR: Path/name of XML to be validated cannot be null.");
			return;
		}
		if (xsdFilesPathsAndNames == null || xsdFilesPathsAndNames.length < 1) {
			out.println("ERROR: At least one XSD must be provided to validate XML against.");
			return;
		}
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		final StreamSource[] xsdSources = generateStreamSourcesFromXsdPathsJdk8(xsdFilesPathsAndNames);
		
		try {
			final Schema schema = schemaFactory.newSchema(xsdSources);
			final Validator validator = schema.newValidator();
			out.println(
			    "Validating " + xmlFilePathAndName + " against XSDs " + Arrays.toString(xsdFilesPathsAndNames) + "...");
			validator.validate(new StreamSource(new File(xmlFilePathAndName)));
		}
		catch (IOException | SAXException exception) // JDK 7 multi-exception catch
		{
			out.println("ERROR: Unable to validate " + xmlFilePathAndName + " against XSDs "
			        + Arrays.toString(xsdFilesPathsAndNames) + " - " + exception);
		}
		out.println("Validation process completed.");
	}
	
	/**
	 * Generates array of StreamSource instances representing XSDs associated with the file
	 * paths/names provided and use JDK 8 Stream API. This method can be commented out if using a
	 * version of Java prior to JDK 8.
	 *
	 * @param xsdFilesPaths String representations of paths/names of XSD files.
	 * @return StreamSource instances representing XSDs.
	 */
	private static StreamSource[] generateStreamSourcesFromXsdPathsJdk8(final String[] xsdFilesPaths) {
		return Arrays.stream(xsdFilesPaths).map(StreamSource::new).collect(Collectors.toList())
		        .toArray(new StreamSource[xsdFilesPaths.length]);
	}
	
	/**
	 * Validates provided XML against provided XSD.
	 *
	 * @param arguments XML file to be validated (first argument) and XSD against which it should be
	 *            validated (second and later arguments).
	 */
	public static void main(final String[] arguments) {
		if (arguments.length < 2) {
			out.println("\nUSAGE: java XmlValidator <xmlFile> <xsdFile1> ... <xsdFileN>\n");
			out.println("\tOrder of XSDs can be significant (place XSDs that are");
			out.println("\tdependent on other XSDs after those they depend on)");
			System.exit(-1);
		}
		// Arrays.copyOfRange requires JDK 6; see
		// http://stackoverflow.com/questions/7970486/porting-arrays-copyofrange-from-java-6-to-java-5
		// for additional details for versions of Java prior to JDK 6.
		final String[] schemas = Arrays.copyOfRange(arguments, 1, arguments.length);
		validateXmlAgainstXsds(arguments[0], schemas);
	}
}
