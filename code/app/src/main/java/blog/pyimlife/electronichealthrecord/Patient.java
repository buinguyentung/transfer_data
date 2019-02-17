package blog.pyimlife.electronichealthrecord;

public class Patient {
    // Firestore requires all to have public variables or getter/setter.
    public String name;
    public String id;
    public String patid;
    public int age;

    public Patient(String name, String id, int age, String patid) {
        this.name = name;
        this.id = id;
        this.age = age;
        this.patid = patid;
    }

    public Patient() {
        // Default receiving data from firestore
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPatid() {
        return patid;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPatid(String patid) {
        this.patid = patid;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
