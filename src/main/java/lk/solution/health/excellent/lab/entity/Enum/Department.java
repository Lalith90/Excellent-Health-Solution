package lk.solution.health.excellent.lab.entity.Enum;

public enum  Department {
     BIOCHEMISTRY("Bio Chemistry"),
     CLINICALPATHALOGY ("Clinical Pathalogy"),
     HEAMATOLOGY ("Heamatology"),
     MICROBIOLOGY  ("Microbiology"),
     IMMUNOLOGYSEROLOGY ("Immunology"),
     SEROLOGY ("Serology"),
     HISTOPATHALOGY  ("Histopathalogy Cytology"),
     MOLECULARBIOLOGY   ("Molecular Biology"),
     EXTRA ("Extra");


    private final String department;

    Department(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }
}