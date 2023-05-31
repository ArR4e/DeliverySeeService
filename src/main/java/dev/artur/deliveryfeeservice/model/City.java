package dev.artur.deliveryfeeservice.model;

public enum City {
    TALLINN("Tallinn-Harku"),
    TARTU("Tartu-Tõravere"),
    PARNU("Pärnu");

    private final String stationName;

    City(String stationName) {
        this.stationName = stationName;
    }

    public static City nameToEnum(String stationName) {
        return switch (stationName) {
            case "Tallinn-Harku" -> TALLINN;
            case "Tartu-Tõravere" -> TARTU;
            case "Pärnu" -> PARNU;
            default -> null;
        };
    }

    public String getStationName() {
        return stationName;
    }
}
