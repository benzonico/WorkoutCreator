import com.garmin.fit.FileEncoder;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.FitRuntimeException;
import com.garmin.fit.Manufacturer;

public class BDDFitEncoder {

  public static void main(String[] args) {
    System.out.println("FIT Encode Example Application");

    FileEncoder encode;

    try {
      encode = new FileEncoder(new java.io.File("test.fit"));
    } catch (FitRuntimeException e) {
      System.err.println("Error opening file test.fit");
      return;
    }

    FileIdMesg fileIdMesg = new FileIdMesg();

    fileIdMesg.setManufacturer(Manufacturer.DYNASTREAM);
    fileIdMesg.setProduct(0);
    fileIdMesg.setSerialNumber(12345L);

    encode.write(fileIdMesg);

    try {
      encode.close();
    } catch(FitRuntimeException e) {
      System.err.println("Error closing encode.");
      return;
    }

    System.out.println("Encoded FIT file test.fit.");
  }
}
