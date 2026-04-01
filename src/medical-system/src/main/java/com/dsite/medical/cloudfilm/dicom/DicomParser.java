package com.dsite.medical.cloudfilm.dicom;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * DICOM文件解析器
 */
@Component
public class DicomParser {

    /**
     * 解析DICOM文件
     *
     * @param file DICOM文件
     * @return DICOM元数据
     */
    public DicomInfo parse(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             DicomInputStream dis = new DicomInputStream(is)) {

            Attributes attrs = dis.readFileMetaInformation();
            if (attrs == null) {
                attrs = new Attributes();
            }
            Attributes dataset = dis.readDataset(-1, -1);
            attrs.addAll(dataset);

            DicomInfo info = new DicomInfo();

            // 患者信息
            info.setPatientName(getString(attrs, Tag.PatientName));
            info.setPatientId(getString(attrs, Tag.PatientID));
            info.setPatientBirthDate(getString(attrs, Tag.PatientBirthDate));
            info.setPatientSex(getString(attrs, Tag.PatientSex));

            // 检查信息
            info.setStudyInstanceUID(getString(attrs, Tag.StudyInstanceUID));
            info.setSeriesInstanceUID(getString(attrs, Tag.SeriesInstanceUID));
            info.setSOPInstanceUID(getString(attrs, Tag.SOPInstanceUID));
            info.setModality(getString(attrs, Tag.Modality));

            // 图像信息
            info.setSeriesNumber(getInt(attrs, Tag.SeriesNumber));
            info.setInstanceNumber(getInt(attrs, Tag.InstanceNumber));
            info.setRows(getInt(attrs, Tag.Rows));
            info.setColumns(getInt(attrs, Tag.Columns));

            // 设备信息
            info.setManufacturer(getString(attrs, Tag.Manufacturer));
            info.setStationName(getString(attrs, Tag.StationName));

            // 检查日期
            info.setStudyDate(getString(attrs, Tag.StudyDate));
            info.setStudyTime(getString(attrs, Tag.StudyDate));

            return info;
        } catch (Exception e) {
            throw new RuntimeException("解析DICOM文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析DICOM文件 (InputStream版本)
     */
    public DicomInfo parse(InputStream inputStream) {
        try (DicomInputStream dis = new DicomInputStream(inputStream)) {

            Attributes attrs = dis.readFileMetaInformation();
            if (attrs == null) {
                attrs = new Attributes();
            }
            Attributes dataset = dis.readDataset(-1, -1);
            attrs.addAll(dataset);

            DicomInfo info = new DicomInfo();

            info.setPatientName(getString(attrs, Tag.PatientName));
            info.setPatientId(getString(attrs, Tag.PatientID));
            info.setStudyInstanceUID(getString(attrs, Tag.StudyInstanceUID));
            info.setSeriesInstanceUID(getString(attrs, Tag.SeriesInstanceUID));
            info.setSOPInstanceUID(getString(attrs, Tag.SOPInstanceUID));
            info.setModality(getString(attrs, Tag.Modality));
            info.setSeriesNumber(getInt(attrs, Tag.SeriesNumber));
            info.setInstanceNumber(getInt(attrs, Tag.InstanceNumber));
            info.setRows(getInt(attrs, Tag.Rows));
            info.setColumns(getInt(attrs, Tag.Columns));

            return info;
        } catch (Exception e) {
            throw new RuntimeException("解析DICOM文件失败: " + e.getMessage(), e);
        }
    }

    private String getString(Attributes attrs, int tag) {
        return attrs.getString(tag);
    }

    private Integer getInt(Attributes attrs, int tag) {
        int[] values = attrs.getInts(tag);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    /**
     * DICOM信息封装类
     */
    public static class DicomInfo {
        // 患者信息
        private String patientName;
        private String patientId;
        private String patientBirthDate;
        private String patientSex;

        // 检查信息
        private String studyInstanceUID;
        private String seriesInstanceUID;
        private String sopInstanceUID;
        private String modality;

        // 图像信息
        private Integer seriesNumber;
        private Integer instanceNumber;
        private Integer rows;
        private Integer columns;

        // 设备信息
        private String manufacturer;
        private String stationName;

        // 日期时间
        private String studyDate;
        private String studyTime;

        // Getters and Setters
        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }
        public String getPatientBirthDate() { return patientBirthDate; }
        public void setPatientBirthDate(String patientBirthDate) { this.patientBirthDate = patientBirthDate; }
        public String getPatientSex() { return patientSex; }
        public void setPatientSex(String patientSex) { this.patientSex = patientSex; }
        public String getStudyInstanceUID() { return studyInstanceUID; }
        public void setStudyInstanceUID(String studyInstanceUID) { this.studyInstanceUID = studyInstanceUID; }
        public String getSeriesInstanceUID() { return seriesInstanceUID; }
        public void setSeriesInstanceUID(String seriesInstanceUID) { this.seriesInstanceUID = seriesInstanceUID; }
        public String getSopInstanceUID() { return sopInstanceUID; }
        public void setSOPInstanceUID(String sopInstanceUID) { this.sopInstanceUID = sopInstanceUID; }
        public String getModality() { return modality; }
        public void setModality(String modality) { this.modality = modality; }
        public Integer getSeriesNumber() { return seriesNumber; }
        public void setSeriesNumber(Integer seriesNumber) { this.seriesNumber = seriesNumber; }
        public Integer getInstanceNumber() { return instanceNumber; }
        public void setInstanceNumber(Integer instanceNumber) { this.instanceNumber = instanceNumber; }
        public Integer getRows() { return rows; }
        public void setRows(Integer rows) { this.rows = rows; }
        public Integer getColumns() { return columns; }
        public void setColumns(Integer columns) { this.columns = columns; }
        public String getManufacturer() { return manufacturer; }
        public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
        public String getStationName() { return stationName; }
        public void setStationName(String stationName) { this.stationName = stationName; }
        public String getStudyDate() { return studyDate; }
        public void setStudyDate(String studyDate) { this.studyDate = studyDate; }
        public String getStudyTime() { return studyTime; }
        public void setStudyTime(String studyTime) { this.studyTime = studyTime; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("patientName", patientName);
            map.put("patientId", patientId);
            map.put("studyInstanceUID", studyInstanceUID);
            map.put("seriesInstanceUID", seriesInstanceUID);
            map.put("sopInstanceUID", sopInstanceUID);
            map.put("modality", modality);
            map.put("seriesNumber", seriesNumber);
            map.put("instanceNumber", instanceNumber);
            return map;
        }
    }
}
