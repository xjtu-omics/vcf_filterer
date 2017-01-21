package vcf_filterer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Can filter a VCF file, only keeping the events that obey specific criteria.
 * 
 * Usage: java -jar vcf_filterer.jar name_of_input_vcf.vcf
 * name_of_filtered_vcf_to_be_created.vcf
 * 
 * @author Eric-Wubbo Lameijer, Xi'an Jiaotong University,
 *         eric_wubbo@hotmail.com
 *
 */
public class VcfFilterer {

  public static void main(String[] args) {
    Utilities.require(args.length == 2, "VcfFilterer.main error: two arguments are required,"
        + " the name of the input file and the name of the output file.");
    String nameOfInputFile = args[0];
    String nameOfOutputFile = args[1];
    try (BufferedReader reader = new BufferedReader(new FileReader(nameOfInputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(nameOfOutputFile))) {

      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("#")) {
          writeln(writer, line);
        } else {
          Event event = new Event(line);
          if (!event.hasSnpAlt()) {
            writeln(writer, line);
          } else {
            System.out.println("Skipped " + line);
          }
        }
      }
    } catch (Exception e) {
      System.err.format("Exception occurred trying to read '%s'.", args[0]);
      e.printStackTrace();
    }
  }

  /**
   * Utility function that adds a neat "\n" to the line
   * 
   * @throws IOException
   **/
  static void writeln(BufferedWriter writer, String line) throws IOException {
    writer.write(line);
    writer.newLine();
  }
}
