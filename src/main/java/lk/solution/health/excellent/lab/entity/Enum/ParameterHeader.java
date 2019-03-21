package lk.solution.health.excellent.lab.entity.Enum;

public enum ParameterHeader {
    Yes("Yes"),
    No("No");

    private final String parameterHeader;

    ParameterHeader(String parameterHeader) { this.parameterHeader = parameterHeader;
    }

    public String getParameterHeader() {
        return parameterHeader;
    }
}
