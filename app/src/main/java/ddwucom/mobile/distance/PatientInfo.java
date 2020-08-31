package ddwucom.mobile.distance;

public class PatientInfo {
    private String patient_no;
    private String district;
    private String diagDate;

    public PatientInfo() {
        this.patient_no = null;
        this.patient_no = null;
        this.patient_no = null;
    }

    public PatientInfo(String patient_no, String district, String diagDate) {
        this.patient_no = patient_no;
        this.district = district;
        this.diagDate = diagDate;
    }

    public String getPatient_no() {
        return patient_no;
    }

    public void setPatient_no(String patient_no) {
        this.patient_no = patient_no;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDiagDate() {
        return diagDate;
    }

    public void setDiagDate(String diagDate) {
        this.diagDate = diagDate;
    }

}
