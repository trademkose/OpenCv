package org.opencv.samples.colorblobdetect;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ColorBlobDetectionActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";

    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv,mBlobColorHsv_car;
    private ColorBlobDetector    mDetector,mDetector_car;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;
    private ImageView btnLeft = null;
    private ImageView btnRight = null;
    private ImageView btnTop = null;
    private ImageView btnDown = null;
   
    private CameraBridgeViewBase mOpenCvCameraView;

 
    TextView textViewPoint ;
    Button Btngetdata;
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                    
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public ColorBlobDetectionActivity() {
    	
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
     
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                //WindowManager.LayoutParams.FLAG_FULLSCREEN);
         
        setContentView(R.layout.color_blob_detection_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
         
        ///button

        btnLeft = (ImageView)findViewById(R.id.btnLeft);

        OnTouchListener left=new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView iv = (ImageView) v;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    iv.setImageResource(R.drawable.leftred);

                    //Toast.makeText(getBaseContext(), "Push to Left Button touch", Toast.LENGTH_LONG).show();

                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    iv.setImageResource(R.drawable.left);
                    return true;
                }

                return false;
            }
        };
        btnLeft.setOnTouchListener(left);


        btnRight = (ImageView)findViewById(R.id.btnRight);

        OnTouchListener right=new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView iv = (ImageView) v;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    iv.setImageResource(R.drawable.rightred);
                    //Toast.makeText(getBaseContext(), "Push to Right Button", Toast.LENGTH_LONG).show();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    iv.setImageResource(R.drawable.right);
                    return true;
                }
                return false;
            }
        };
        btnRight.setOnTouchListener(right);

        btnTop = (ImageView)findViewById(R.id.btnTop);

        OnTouchListener top=new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView iv = (ImageView) v;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    iv.setImageResource(R.drawable.topred);
                    //Toast.makeText(getBaseContext(), "Push to Top Button", Toast.LENGTH_LONG).show();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    iv.setImageResource(R.drawable.top);
                    return true;
                }
                return false;
            }
        };
        btnTop.setOnTouchListener(top);

        btnDown = (ImageView)findViewById(R.id.btnDown);

        OnTouchListener down=new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView iv = (ImageView) v;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    iv.setImageResource(R.drawable.downred);
                    Toast.makeText(getBaseContext(), "Push to Down Button", Toast.LENGTH_LONG).show();

                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    iv.setImageResource(R.drawable.down);
                    return true;
                }
                return false;
            }
        };
        btnDown.setOnTouchListener(down);
        ///button
        
        textViewPoint= (TextView) findViewById(R.id.textViewPoint);      
      
        (new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!Thread.interrupted())
                    try
                    {
                        Thread.sleep(250);
                        runOnUiThread(new Runnable() // start actions in UI thread
                        {
                            @Override
                            public void run()
                            {    
                            	 int X_ball=(int)mDetector.getX();
                		         int Y_ball=(int)mDetector.getY(); 
                		         
                		         int X_car=(int)mDetector_car.getX();
                		         int Y_car=(int)mDetector_car.getY();
                		         
                		         int threshold=5;
                		         String buffer=new String();
                				 buffer="";
                				 
                		         //Araba için ileri sol
                		         if(X_ball-X_car > 0 && Y_ball-Y_car< -1*threshold){
                		        	 buffer+="ileri sol\n";
                		         } 
                		         //Araba için ileri sað
                		         else if(X_ball-X_car > 0 && Y_ball-Y_car>threshold ){
                		        	 buffer+="ileri sað\n";
                		         } 
                		         //Araba için sadece ileri
                		         else if(X_ball-X_car > 0 && (Y_ball-Y_car<threshold && Y_ball-Y_car>-1*threshold)){
                		        	 buffer+="sadece ileri\n";
                		         } 
                		         //Araba için geri sað
                		         else if(X_ball-X_car < 0 && Y_ball-Y_car>threshold ){
                		        	 buffer+="geri sað\n";
                		         } 
                		         //Araba için geri sol
                		         else if(X_ball-X_car < 0 && Y_ball-Y_car<-1*threshold ){
                		        	 buffer+="geri sol\n";
                		         } 
                		         //Araba için sadece geri
                		         else if(X_ball-X_car < 0 && (Y_ball-Y_car>-1*threshold && Y_ball-Y_car<threshold)){
                		        	 buffer+="sadece geri\n";
                		         }
                		           
                		         //Back
                		         /*
                				 String buffer=new String();
                				 buffer="";
                				 
                				 if(X>1 && X<100){
                					 buffer="SOL YASAK\n";
                				 }
                				 if(X>mOpenCvCameraView.getWidth()-100){
                					 buffer+="SAG YASAK\n";
                				 }
                				 
                				 if(Y>1&&Y<100){
                					 buffer+="YUKARI YASAK\n";
                				 }
                				 if(Y>mOpenCvCameraView.getHeight()-100){
                					 buffer+="AÞAÐI YASAK\n";
                				 }*/
                				 
                				  textViewPoint.setText("Ball :X:"+X_ball+" Y:"+Y_ball +"\nCar:"+"X:"+X_car +" Y:"+Y_car +"\n" +buffer);
                 				
                				 
                				
                            }
                        });
                    }
                    catch (InterruptedException e)
                    {
                        // ooops
                    }
            }
        })).start();
         
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {

        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
     
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mDetector_car = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        mBlobColorHsv_car = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255); 
    
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);
        
        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
        

        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        //Log.i(TAG,"once : "+mBlobColorHsv.val[0]+"--"+mBlobColorHsv.val[1]+"--"+mBlobColorHsv.val[2]);
        
        mBlobColorHsv.val[0]=0;
        mBlobColorHsv.val[1]=255;
        mBlobColorHsv.val[2]=179;
        mBlobColorHsv.val[3]=255;
        
        //***car**
        mBlobColorHsv_car.val[0]=0;
        mBlobColorHsv_car.val[1]=0;
        mBlobColorHsv_car.val[2]=0;
        mBlobColorHsv_car.val[3]=255;
        //***car**
        
        //Log.i(TAG,"burda : "+mBlobColorHsv.val[0]+"--"+mBlobColorHsv.val[1]+"--"+mBlobColorHsv.val[2]);
        mDetector.setHsvColor(mBlobColorHsv);
        mDetector_car.setHsvColor(mBlobColorHsv_car);
        
        mDetector.name="ball";
        mDetector_car.name="car";
        
        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        Imgproc.resize(mDetector_car.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        
       
        mIsColorSelected = true;

        touchedRegionRgba.release();
        touchedRegionHsv.release();
        
        return false; // don't need subsequent touch events
    }
    

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	 mRgba = inputFrame.rgba(); 
    	 /*
    	Mat mRgba2 = inputFrame.rgba().clone();
    	 Mat mRgbaT = mRgba2.t();
    	 Core.flip(mRgba2.t(), mRgbaT, 1);
    	Imgproc.resize(mRgbaT, mRgbaT, mRgba2.size());
    	mRgba=mRgbaT;
    	 */
         
        
        if (mIsColorSelected) {
            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            
            mDetector_car.process(mRgba);
            
            for(int i=contours.size();i< contours.size()+mDetector_car.getContours().size();i++){
            	contours.add(mDetector_car.getContours().get(i));
            }
            
            Log.e(TAG, "Contours count: " + contours.size());
            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(mBlobColorRgba);

            Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
            mSpectrum.copyTo(spectrumLabel);        
            Integer X=(int)mDetector.getX();
            Integer Y=(int)mDetector.getY();
            Log.i(TAG, "asd:"+X+"Y:"+Y);
          
        }
        
        return mRgba;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);
       
        return new Scalar(pointMatRgba.get(0, 0));
    }
}
