package vcf_filterer;

/**
 * Event: encodes a genomic event (basically, that a base or sequence of bases
 * at position X in a genome can be replaced by one (or multiple) other sequence
 * of bases.
 * 
 * 
 * @author Eric-Wubbo Lameijer, Xi'an Jiaotong University,
 *         eric_wubbo@hotmail.com
 *
 */
public class Event {
  String[] allFields; // saves all fields of the VCF line, handy for output
  String alternativeAllele; // the alt allele/alternative allele: the sequence
                            // of DNA in the individual that is other than
  // expected by consulting the reference chromosome.
  String chromosomeName; // the name of the chromosome in which this event takes
                         // place
  int position; // position of the event
  String referenceAllele; // the expected sequence at this place in this
                          // chromosome, as indicated in the reference genome
  boolean isPacBio; // PacBio VCF files have a different format, like T <DEL>
                    // instead of TTCG T

  /**
   * Event constructor; creates an event out of a line of a VCF file.
   * 
   * @param vcfLine
   *          The line of the VCF file to be turned into an event.
   */
  public Event(String vcfLine) {
    allFields = vcfLine.split("\\t");
    chromosomeName = allFields[0];
    // System.out.println("Chromosome name: " + chromosomeName);
    position = Integer.parseInt(allFields[1]);
    // id = vcfDataItems[2];
    referenceAllele = allFields[3];
    alternativeAllele = allFields[4];
    isPacBio = (alternativeAllele.equals("<INS>") || alternativeAllele.equals("DEL"));
    if (isPacBio) {
      if (alternativeAllele.equals("<INS>")) {
        String info = allFields[7];
        int sequencePosition = info.indexOf("SEQ=") + 4;
        alternativeAllele = "";
        char base = info.charAt(sequencePosition);
        while (isValidDnaBase(base)) {
          alternativeAllele += Character.toUpperCase(base);
          ++sequencePosition;
          base = info.charAt(sequencePosition);
        }
      } else if (alternativeAllele.equals("<DEL>")) {
        alternativeAllele = referenceAllele;
        String info = allFields[7];
        int sequencePosition = info.indexOf("SEQ=") + 4;
        referenceAllele = "";
        char base = info.charAt(sequencePosition);
        while (isValidDnaBase(base)) {
          referenceAllele += Character.toUpperCase(base);
          ++sequencePosition;
          base = info.charAt(sequencePosition);
        }
      } else {
        Utilities.require(false, "Event constructor error: event type " + alternativeAllele + " is unknown.");
      }
    }

  }

  /**
   * Is this a valid base of DNA?
   * 
   * @param base
   *          the base (or character) to be checked
   * @return whether the base is a valid DNA base
   */
  private boolean isValidDnaBase(char base) {
    char baseInUpperCase = Character.toUpperCase(base);
    return (baseInUpperCase == 'A' || baseInUpperCase == 'C' || baseInUpperCase == 'G' || baseInUpperCase == 'T');
  }

  /**
   * Returns the name of the chromosome in which this event takes place.
   * 
   * @return the name of the chromosome in which this event takes place.
   */
  public String getChromosome() {
    return chromosomeName;
  }

  /**
   * Does this event have multiple alternative alleles?
   * 
   * @return whether the event has multiple alternative alleles.
   */
  public boolean hasSingleAltAllele() {
    return !alternativeAllele.contains(",");
  }

  /**
   * Is this event a SNP?
   * 
   * @return whether the event is a Single Nucleotide Polymorphism
   */
  public boolean isSnp() {
    return (referenceAllele.length() == 1 && alternativeAllele.length() == 1);
  }

  /**
   * Does this event (line of VCF file) contain at least one SNP as alternative
   * allele?
   * 
   * @return Whether this event has at least one SNP as alternative allele
   */
  public boolean hasSnpAlt() {
    if (isSnp()) {
      return true;
    } else {
      if (referenceAllele.length() == 1) {
        // can be SNP
        String[] alternativeAlleles = alternativeAllele.split(",");
        for (String allele : alternativeAlleles) {
          if (allele.length() == 1) {
            return true;
          }
        }
      } // referenceAllele.length() > 1: is deletion, so cannot be SNP
    }
    return false;
  }

} // Event
