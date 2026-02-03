package moviles.pablohiguero.examen2evlistacompra.models;
import io.realm.RealmList;
import io.realm.RealmObject;

public class Store extends RealmObject {
    private String name;
    private String address;
    private Double lat;
    private Double lon;
    private boolean isActive; // "Tienda activa"
    private RealmList<Item> items; // Relación 1:N

    public Store() {
        this.items = new RealmList<>();
    }

    // Constructor compatible con Utils.java
    public Store(String name, String address, Double lat, Double lon, boolean isActive) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.isActive = isActive;
        this.items = new RealmList<>(); // ¡Importante inicializarla!
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public RealmList<Item> getItems() { return items; }
    public void setItems(RealmList<Item> items) { this.items = items; }
}