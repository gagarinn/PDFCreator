//
//  Android PDF Writer
//  http://coderesearchlabs.com/androidpdfwriter
//
//  by Javier Santo Domingo (j-a-s-d@coderesearchlabs.com)
//

package my.example.pdfcreator;

import android.Manifest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import crl.android.pdfwriter.PDFWriter;
import crl.android.pdfwriter.PaperSize;
import crl.android.pdfwriter.StandardFonts;
import crl.android.pdfwriter.Transformation;

public class PDFWriterDemo extends Activity {

    private static final String TAG = "myLogs";
    TextView mText;
	
	private String generateHelloWorldPDF() {
        Log.e(TAG, "------PDFWriterDemo : generateHelloWorldPDF: ");
        PDFWriter mPDFWriter = new PDFWriter(PaperSize.FOLIO_WIDTH, PaperSize.FOLIO_HEIGHT);

		// note that to make this images snippet work
		// you have to uncompress the assets.zip file
		// included into your project assets folder
        AssetManager mngr = getAssets();
		try {
			Bitmap xoiPNG = BitmapFactory.decodeStream(mngr.open("CRL-borders.png"));
			Bitmap xoiJPG = BitmapFactory.decodeStream(mngr.open("CRL-star.jpg"));
			Bitmap xoiBMP1 = BitmapFactory.decodeStream(mngr.open("CRL-1bit.bmp"));
			Bitmap xoiBMP8 = BitmapFactory.decodeStream(mngr.open("CRL-8bits.bmp"));
			Bitmap xoiBMP24 = BitmapFactory.decodeStream(mngr.open("CRL-24bits.bmp"));
	        mPDFWriter.addImage(400, 600, xoiPNG, Transformation.DEGREES_315_ROTATION);
	        mPDFWriter.addImage(300, 500, xoiJPG);
	        mPDFWriter.addImage(200, 400, 135, 75, xoiBMP24);
	        mPDFWriter.addImage(150, 300, 130, 70, xoiBMP8);
	        mPDFWriter.addImageKeepRatio(100, 200, 50, 25, xoiBMP8);
	        mPDFWriter.addImageKeepRatio(50, 100, 30, 25, xoiBMP1, Transformation.DEGREES_270_ROTATION);
	        mPDFWriter.addImageKeepRatio(25, 50, 30, 25, xoiBMP1);
		} catch (IOException e) {
			e.printStackTrace();
            Log.e(TAG, "------PDFWriterDemo : generateHelloWorldPDF: ", e);
        }

		
        mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.TIMES_ROMAN);
        mPDFWriter.addRawContent("1 0 0 rg\n");
        mPDFWriter.addTextAsHex(70, 50, 12, "68656c6c6f20776f726c6420286173206865782921");
        mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.COURIER, StandardFonts.WIN_ANSI_ENCODING);
        mPDFWriter.addRawContent("0 0 0 rg\n");
        mPDFWriter.addText(30, 90, 10, " CRL", Transformation.DEGREES_270_ROTATION);
        
        mPDFWriter.newPage();
        mPDFWriter.addRawContent("[] 0 d\n");
        mPDFWriter.addRawContent("1 w\n");
        mPDFWriter.addRawContent("0 0 1 RG\n");
        mPDFWriter.addRawContent("0 1 0 rg\n");
        mPDFWriter.addRectangle(40, 50, 280, 50);
        mPDFWriter.addText(85, 75, 18, "Code Research Laboratories");
        
        mPDFWriter.newPage();
        mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.COURIER_BOLD);
        mPDFWriter.addText(150, 150, 14, "http://coderesearchlabs.com");
        mPDFWriter.addLine(150, 140, 270, 140);
        
        int pageCount = mPDFWriter.getPageCount();
        for (int i = 0; i < pageCount; i++) {
        	mPDFWriter.setCurrentPage(i);
        	mPDFWriter.addText(10, 10, 8, Integer.toString(i + 1) + " / " + Integer.toString(pageCount));
        }
        
        String s = mPDFWriter.asString();
        return s;
	}
	
	private void outputToScreen(int viewID, String pdfContent) {
        mText = (TextView) this.findViewById(viewID);
        mText.setText(pdfContent);
	}
	
	private void outputToFile(String fileName, String pdfContent, String encoding) {

        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
        Log.e(TAG, "------PDFWriterDemo : outputToFile: fileName = " + newFile);
        try {
            newFile.createNewFile();
            try {
            	FileOutputStream pdfFile = new FileOutputStream(newFile);
                Log.e(TAG, "------PDFWriterDemo : outputToFile: " + pdfFile);
                pdfFile.write(pdfContent.getBytes(encoding));
                pdfFile.close();
            } catch(FileNotFoundException e) {
                Log.e(TAG, "----    catch  ------PDFWriterDemo : outputToFile: ");
                Log.e(TAG, "------PDFWriterDemo : outputToFile: ", e);
            }
        } catch(IOException e) {
            Log.e(TAG, "------PDFWriterDemo : outputToFile: ", e);
        }
	}
	
    /** Called when the activity is first created. */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        String pdfcontent = generateHelloWorldPDF();
        outputToScreen(R.id.tv_hello, pdfcontent);
        outputToFile("helloworld.pdf",pdfcontent,"ISO-8859-1");
    }


    private boolean shouldAskPermission(){

        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);

    }
    String[] perms = {"android.permission. WRITE_EXTERNAL_STORAGE"};

    int permsRequestCode = 200;


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:

                boolean writeAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;

                break;

        }

    }

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}