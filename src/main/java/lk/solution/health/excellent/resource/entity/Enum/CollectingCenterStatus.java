package lk.solution.health.excellent.resource.entity.Enum;

public enum CollectingCenterStatus {
    OPEN("Open"),
    TEMCLOSED("Temporary Closed"),
    CLOSED("Closed"),
    UNDERCON("Under Construction");

    private final String CollectingCenterStatus;

    CollectingCenterStatus(String collectingCenterStatus) {
        this.CollectingCenterStatus = collectingCenterStatus;
    }

    public String getCollectingCenterStatus() {
        return CollectingCenterStatus;
    }
}
