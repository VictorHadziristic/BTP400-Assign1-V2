import java.io.Serializable;

public class Part implements Serializable {
    int id;
    String name;

    public String getName() { return name; }
    public int getId() { return id; }

    public Part(int id, String name) {
        this.id = id;
        this.name = name;
    }
}