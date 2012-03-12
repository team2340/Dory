/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team2340;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.*;
import edu.wpi.first.wpilibj.networktables.NetworkTableKeyNotDefined;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Team 2340
 */
public class DoryCamera {

    AxisCamera camera;
    
    int hueHigh;
    int hueLow;
    int satHigh;
    int satLow;
    int intensityHigh;
    int intensityLow;
    double percentOfImageLow;
    double percentOfImageHigh;
    DoryLogger logger;
    
    public DoryCamera() {
        logger = DoryLogger.getInstance();
        SmartDashboard.putInt(DoryDefinitions.CAMERA_H_LOW, DoryDefinitions.CAMERA_H_LOW_INITIAL);
        SmartDashboard.putInt(DoryDefinitions.CAMERA_H_HIGH, DoryDefinitions.CAMERA_H_HIGH_INITIAL);
        SmartDashboard.putInt(DoryDefinitions.CAMERA_S_LOW, DoryDefinitions.CAMERA_S_LOW_INITIAL);
        SmartDashboard.putInt(DoryDefinitions.CAMERA_S_HIGH, DoryDefinitions.CAMERA_S_HIGH_INITIAL);
        SmartDashboard.putInt(DoryDefinitions.CAMERA_I_LOW, DoryDefinitions.CAMERA_I_LOW_INITIAL);
        SmartDashboard.putInt(DoryDefinitions.CAMERA_I_HIGH, DoryDefinitions.CAMERA_I_HIGH_INITIAL);
        
        hueLow = SmartDashboard.getInt(DoryDefinitions.CAMERA_H_LOW, DoryDefinitions.CAMERA_H_LOW_INITIAL);
        hueHigh = SmartDashboard.getInt(DoryDefinitions.CAMERA_H_HIGH, DoryDefinitions.CAMERA_H_HIGH_INITIAL);
        satLow = SmartDashboard.getInt(DoryDefinitions.CAMERA_S_LOW, DoryDefinitions.CAMERA_S_LOW_INITIAL);
        satHigh = SmartDashboard.getInt(DoryDefinitions.CAMERA_S_HIGH, DoryDefinitions.CAMERA_S_HIGH_INITIAL);
        intensityLow = SmartDashboard.getInt(DoryDefinitions.CAMERA_I_LOW, DoryDefinitions.CAMERA_I_LOW_INITIAL);
        intensityHigh = SmartDashboard.getInt(DoryDefinitions.CAMERA_I_HIGH, DoryDefinitions.CAMERA_I_HIGH_INITIAL);
        
        SmartDashboard.putDouble(DoryDefinitions.IP_PERCENT_OF_IMAGE_LOW, DoryDefinitions.IP_PERCENT_OF_IMAGE_LOW_INITIAL);
        SmartDashboard.putDouble(DoryDefinitions.IP_PERCENT_OF_IMAGE_HIGH, DoryDefinitions.IP_PERCENT_OF_IMAGE_HIGH_INITIAL);
        
        percentOfImageLow = SmartDashboard.getDouble(DoryDefinitions.IP_PERCENT_OF_IMAGE_LOW, DoryDefinitions.IP_PERCENT_OF_IMAGE_LOW_INITIAL);
        percentOfImageHigh = SmartDashboard.getDouble(DoryDefinitions.IP_PERCENT_OF_IMAGE_HIGH, DoryDefinitions.IP_PERCENT_OF_IMAGE_HIGH_INITIAL);
        
        camera = AxisCamera.getInstance();
        
    }

    private void updateValues() {
        try {
            hueLow = SmartDashboard.getInt(DoryDefinitions.CAMERA_H_LOW);
            hueHigh = SmartDashboard.getInt(DoryDefinitions.CAMERA_H_HIGH);
            satLow = SmartDashboard.getInt(DoryDefinitions.CAMERA_S_LOW);
            satHigh = SmartDashboard.getInt(DoryDefinitions.CAMERA_S_HIGH);
            intensityLow = SmartDashboard.getInt(DoryDefinitions.CAMERA_I_LOW);
            intensityHigh = SmartDashboard.getInt(DoryDefinitions.CAMERA_I_HIGH);
            
            percentOfImageLow = SmartDashboard.getDouble(DoryDefinitions.IP_PERCENT_OF_IMAGE_LOW);
            percentOfImageHigh = SmartDashboard.getDouble(DoryDefinitions.IP_PERCENT_OF_IMAGE_HIGH);
            
        } catch (NetworkTableKeyNotDefined ex) {
            ex.printStackTrace();
        }
        
    }
    
    private double analyzeImage() {
        updateValues();
        double distanceFromTarget = 0;
        try {
            ColorImage rawImage = camera.getImage();
            BinaryImage thresholded = rawImage.thresholdHSI(hueLow, hueHigh, satLow, satHigh, intensityLow, intensityHigh);
            rawImage.free();
            thresholded.removeSmallObjects(true, 1);
            thresholded.convexHull(true);
            
            CriteriaCollection cc = new CriteriaCollection();
            cc.addCriteria(NIVision.MeasurementType.IMAQ_MT_AREA_BY_IMAGE_AREA, (float)percentOfImageLow, (float)percentOfImageHigh, false);
            thresholded.particleFilter(cc);
            ParticleAnalysisReport[] reports = thresholded.getOrderedParticleAnalysisReports();
            SmartDashboard.putInt(DoryDefinitions.CAMERA_NUM_RECTANGLES_FOUND, reports.length);
            for(int i = 0; i < reports.length; ++i) {
                ParticleAnalysisReport report = reports[i];
                double area = report.particleArea;
                int boundingHeight = report.boundingRectHeight;
                int boundingWidth = report.boundingRectWidth;
                int boundingArea = boundingHeight * boundingWidth;
                double triangleArea = (boundingArea - area) / 2;
                double heightOfTriangle = (2 * triangleArea) / boundingWidth;
                double heightOfTarget = boundingHeight - heightOfTriangle;
                distanceFromTarget = 1128.32 * (MathUtils.pow(heightOfTarget, -1.0225));
            }
        } catch (AxisCameraException ex) {
            ex.printStackTrace();
        } catch (NIVisionException ex) {
            ex.printStackTrace();
        }
        return distanceFromTarget;
    }
    
    public double getDistanceFromTarget() {
        double imageInfo = analyzeImage();
        logger.log("getDistanceFromTarget" , imageInfo);
        return imageInfo;
    }
    
}
