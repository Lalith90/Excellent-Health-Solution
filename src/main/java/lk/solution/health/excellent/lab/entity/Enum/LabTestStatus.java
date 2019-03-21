package lk.solution.health.excellent.lab.entity.Enum;

public enum LabTestStatus {
    NOSAMPLE("No Sample"),
    SAMPLECOLLECT("Sample Collected"),
    WORKSHEET("Work sheet taken"),
    RESULTENTER("Result enter"),
    AUTHORIZED("Authorized"),
    PRINTED("Printed"),
    REPRINT("Reprinted");

     private final String LabTestStatus;

    LabTestStatus(String labTestStatus) {
        this.LabTestStatus = labTestStatus;
    }
    public String getLabTestStatus() {
        return LabTestStatus;
    }
}
