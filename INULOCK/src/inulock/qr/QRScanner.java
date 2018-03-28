package inulock.qr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;

public class QRScanner {

	private Scanner scanner;

	public String takeSnapAndScanForQRCodes() {
		Runtime rt = Runtime.getRuntime();
		String dateTime = null;
		String fileName = null;
		BinaryBitmap bitmap = null;

		System.out.println("press \"ENTER\" to snap...");
		scanner = new Scanner(System.in);
		scanner.nextLine();
		dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		fileName = "snap_" + dateTime + ".png";
		Process snap;

		try {
			snap = rt.exec("raspistill --timeout 20 --output " + fileName);
			snap.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		File imageFile = new File(fileName);
		if (imageFile.exists()) {
			try {
				BufferedImage image = ImageIO.read(imageFile);
				int[] pixels = image.getRGB(0, 0, image.getWidth(),
						image.getHeight(), null, 0, image.getWidth());
				RGBLuminanceSource source = new RGBLuminanceSource(
						image.getWidth(), image.getHeight(), pixels);
				bitmap = new BinaryBitmap(new HybridBinarizer(source));

			} catch (IOException e) {
				System.out
						.println("Error while reading the image:" + imageFile);
				e.printStackTrace();
			}
			if (bitmap == null) {
				System.out.println("Image not recognizable");
			}

			QRCodeReader reader = new QRCodeReader();

			try {
				String result = reader.decode(bitmap).getText();
				System.out.println(result + "\n");
				return result;
			} catch (NotFoundException e) {
				System.out
						.println("Image is not in a recognizable format, hence the Decode is unsuccessful because of the following exception:");
				e.printStackTrace();
				System.out
						.println("This NotFoundException is thrown when a QR Code was not found in the image. It might have been partially detected but could not be confirmed.");
			} catch (ChecksumException e) {
				System.out
						.println("Image is not in a recognizable format, hence the Decode is unsuccessful because of the following exception:");
				e.printStackTrace();
				System.out
						.println("This ChecksumException is thrown when a QR Code was successfully detected and decoded, but was not returned because its checksum feature failed.");
			} catch (FormatException e) {
				System.out
						.println("Image is not in a recognizable format, hence the Decode is unsuccessful because of the following exception:");
				e.printStackTrace();
				System.out
						.println("This FormatException is thrown when a QR Code was successfully detected, but some aspect of the content did not conform to the barcode's format rules. This could have been due to a mis-detection.");
			}
		}
		return null;
	}

	public String takeInstantSnap() throws FailedToRunRaspistillException {
		RPiCamera piCamera = new RPiCamera("./");
		BufferedImage image;
		try {
			image = piCamera.takeBufferedStill();
			BinaryBitmap bitmap = null;
			int[] pixels = image.getRGB(0, 0, image.getWidth(),
					image.getHeight(), null, 0, image.getWidth());
			RGBLuminanceSource source = new RGBLuminanceSource(
					image.getWidth(), image.getHeight(), pixels);
			bitmap = new BinaryBitmap(new HybridBinarizer(source));

			QRCodeReader reader = new QRCodeReader();

			try {
				String result = reader.decode(bitmap).getText();
				System.out.println(result + "\n");
				return result;
			} catch (NotFoundException e) {
				System.out
						.println("Image is not in a recognizable format, hence the Decode is unsuccessful because of the following exception:");
				e.printStackTrace();
				System.out
						.println("This NotFoundException is thrown when a QR Code was not found in the image. It might have been partially detected but could not be confirmed.");
			} catch (ChecksumException e) {
				System.out
						.println("Image is not in a recognizable format, hence the Decode is unsuccessful because of the following exception:");
				e.printStackTrace();
				System.out
						.println("This ChecksumException is thrown when a QR Code was successfully detected and decoded, but was not returned because its checksum feature failed.");
			} catch (FormatException e) {
				System.out
						.println("Image is not in a recognizable format, hence the Decode is unsuccessful because of the following exception:");
				e.printStackTrace();
				System.out
						.println("This FormatException is thrown when a QR Code was successfully detected, but some aspect of the content did not conform to the barcode's format rules. This could have been due to a mis-detection.");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		return null;
	}
}