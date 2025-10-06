// ===================== TIME CLASS =====================
class Time {
    private int hours;
    private int minutes;

    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public int toMinutes() {
        return hours * 60 + minutes;
    }

    @Override
    public String toString() {
        return hours + "h " + minutes + "min";
    }
}

// ===================== POSITION CLASS =====================
class Position {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

// ===================== ABSTRACT RESOURCE CLASS =====================
abstract class Resources {
    protected String name;
    protected double costPerHour;

    public Resources(String name, double costPerHour) {
        this.name = name;
        this.costPerHour = costPerHour;
    }

    public abstract double calculateCost(Time duration);

    @Override
    public String toString() {
        return name + " (Cost/hr: " + costPerHour + ")";
    }
}

// ===================== HUMAN RESOURCES =====================
class HumanResources extends Resources {
    private String skillLevel;

    public HumanResources(String name, double costPerHour, String skillLevel) {
        super(name, costPerHour);
        this.skillLevel = skillLevel;
    }

    @Override
    public double calculateCost(Time duration) {
        return (duration.toMinutes() / 60.0) * costPerHour;
    }

    @Override
    public String toString() {
        return "Human: " + name + " (" + skillLevel + ")";
    }
}

// ===================== NON-HUMAN RESOURCES =====================
class NonHumanResources extends Resources {
    public NonHumanResources(String name, double costPerHour) {
        super(name, costPerHour);
    }

    @Override
    public double calculateCost(Time duration) {
        return (duration.toMinutes() / 60.0) * costPerHour;
    }

    @Override
    public String toString() {
        return "Non-Human Resource: " + name;
    }
}

// ===================== MATERIAL RESOURCES =====================
class MaterialResources extends NonHumanResources {
    private int quantity;

    public MaterialResources(String name, double costPerUnit, int quantity) {
        super(name, costPerUnit);
        this.quantity = quantity;
    }

    @Override
    public double calculateCost(Time duration) {
        return quantity * costPerHour; // cost per unit * quantity
    }

    @Override
    public String toString() {
        return "Material: " + name + " (Qty: " + quantity + ")";
    }
}

// ===================== SOFTWARE RESOURCES =====================
class SoftwareResources extends NonHumanResources {
    private String version;

    public SoftwareResources(String name, double costPerHour, String version) {
        super(name, costPerHour);
        this.version = version;
    }

    @Override
    public String toString() {
        return "Software: " + name + " v" + version;
    }
}

// ===================== HARDWARE RESOURCES =====================
class HardwareResources extends NonHumanResources {
    public HardwareResources(String name, double costPerHour) {
        super(name, costPerHour);
    }

    @Override
    public String toString() {
        return "Hardware: " + name;
    }
}

// ===================== AGV (AUTOMATED GUIDED VEHICLE) =====================
class AGV extends HardwareResources {
    private String ID;
    private double batteryLoad;
    private double consumption;
    private Time chargingTime;
    private Position position;
    private float maxSpeed;
    private float actSpeed;

    public AGV(String ID, double costPerHour, double batteryLoad, double consumption,
               Time chargingTime, Position position, float maxSpeed, float actSpeed) {
        super("AGV-" + ID, costPerHour);
        this.ID = ID;
        this.batteryLoad = batteryLoad;
        this.consumption = consumption;
        this.chargingTime = chargingTime;
        this.position = position;
        this.maxSpeed = maxSpeed;
        this.actSpeed = actSpeed;
    }

    @Override
    public double calculateCost(Time duration) {
        return super.calculateCost(duration) + consumption;
    }

    @Override
    public String toString() {
        return "AGV " + ID + " | Battery: " + batteryLoad + "% | Position: " + position;
    }
}

// ===================== OPERATION (ABSTRACT) =====================
abstract class IOperation {
    protected String ID;
    protected String description;
    protected Time nominalTime;
    protected Resources[] resources;

    public IOperation(String ID, String description, Time nominalTime, Resources[] resources) {
        this.ID = ID;
        this.description = description;
        this.nominalTime = nominalTime;
        this.resources = resources;
    }

    public double getOperationCost() {
        double total = 0;
        for (Resources r : resources) {
            total += r.calculateCost(nominalTime);
        }
        return total;
    }

    public int getResourceCount() {
        return resources.length;
    }

    public String getResourceList() {
        String list = "";
        for (Resources r : resources) {
            list += "   - " + r.toString() + "\n";
        }
        return list;
    }

    public String getData() {
        return "Operation: " + ID + " (" + description + ")\n"
                + "Duration: " + nominalTime + "\n"
                + "Resources:\n" + getResourceList()
                + "Operation Cost: " + getOperationCost() + " EUR";
    }
}

// ===================== OPERATION TYPES =====================
class TransportOperation extends IOperation {
    public TransportOperation(String ID, String description, Time nominalTime, Resources[] resources) {
        super(ID, description, nominalTime, resources);
    }

    @Override
    public String toString() {
        return "Transport Operation: " + ID;
    }
}

class HumanOperation extends IOperation {
    public HumanOperation(String ID, String description, Time nominalTime, Resources[] resources) {
        super(ID, description, nominalTime, resources);
    }

    @Override
    public String toString() {
        return "Human Operation: " + ID;
    }
}

// ===================== PROCESS (ABSTRACT) =====================
abstract class Process {
    protected String ID;
    protected IOperation[] operations;

    public Process(String ID, IOperation[] operations) {
        this.ID = ID;
        this.operations = operations;
    }

    public abstract double processCost();
    public abstract int processDuration();

    public String getProcessData() {
        String data = "Process ID: " + ID + "\n";
        for (IOperation op : operations) {
            data += op.getData() + "\n\n";
        }
        data += "Total Process Time: " + processDuration() + " minutes\n";
        data += "Total Process Cost: " + processCost() + " EUR\n";
        return data;
    }
}

// ===================== PROCESS TYPES =====================
class IndustrialProcess extends Process {
    public IndustrialProcess(String ID, IOperation[] operations) {
        super(ID, operations);
    }

    @Override
    public int processDuration() {
        int total = 0;
        for (IOperation op : operations) {
            total += op.nominalTime.toMinutes();
        }
        return total;
    }

    @Override
    public double processCost() {
        double total = 0;
        for (IOperation op : operations) {
            total += op.getOperationCost();
        }
        return total;
    }
}

class ManagementProcess extends Process {
    public ManagementProcess(String ID, IOperation[] operations) {
        super(ID, operations);
    }

    @Override
    public int processDuration() {
        int total = 0;
        for (IOperation op : operations) {
            total += op.nominalTime.toMinutes();
        }
        return total;
    }

    @Override
    public double processCost() {
        double total = 0;
        for (IOperation op : operations) {
            total += op.getOperationCost();
        }
        return total;
    }
}

// ===================== ABSTRACT WAREHOUSE =====================
abstract class Warehouse {
    protected String ID;
    protected Process[] processes;

    public Warehouse(String ID, Process[] processes) {
        this.ID = ID;
        this.processes = processes;
    }

    public double totalWarehouseCost() {
        double total = 0;
        for (Process p : processes) {
            total += p.processCost();
        }
        return total;
    }

    public int totalWarehouseTime() {
        int total = 0;
        for (Process p : processes) {
            total += p.processDuration();
        }
        return total;
    }

    public abstract void printWarehouseInfo();
}

// ===================== PRODUCTION WAREHOUSE =====================
class ProductionWarehouse extends Warehouse {
    public ProductionWarehouse(String ID, Process[] processes) {
        super(ID, processes);
    }

    @Override
    public void printWarehouseInfo() {
        System.out.println("🏭 Production Warehouse: " + ID);
        for (Process p : processes) {
            System.out.println(p.getProcessData());
            System.out.println("----------------------------------");
        }
        System.out.println("Total Production Time: " + totalWarehouseTime() + " minutes");
        System.out.println("Total Production Cost: " + totalWarehouseCost() + " EUR");
    }
}

// ===================== STORAGE WAREHOUSE =====================
class StorageWarehouse extends Warehouse {
    public StorageWarehouse(String ID, Process[] processes) {
        super(ID, processes);
    }

    @Override
    public void printWarehouseInfo() {
        System.out.println("📦 Storage Warehouse: " + ID);
        for (Process p : processes) {
            System.out.println(p.getProcessData());
            System.out.println("----------------------------------");
        }
        System.out.println("Total Storage Time: " + totalWarehouseTime() + " minutes");
        System.out.println("Total Storage Cost: " + totalWarehouseCost() + " EUR");
    }
}

// ===================== MAIN CLASS =====================
public class Main {
    public static void main(String[] args) {
        // === RESOURCES ===
        HumanResources h1 = new HumanResources("John", 20, "Skilled Worker");
        MaterialResources m1 = new MaterialResources("Steel Beams", 15, 5);
        SoftwareResources s1 = new SoftwareResources("WarehouseApp", 10, "v1.2");
        AGV agv1 = new AGV("A1", 8, 80, 5.5, new Time(1, 0), new Position(0, 0), 2.0f, 1.5f);

        HumanResources h2 = new HumanResources("Mary", 25, "Supervisor");
        MaterialResources m2 = new MaterialResources("Plastic Boxes", 5, 10);
        SoftwareResources s2 = new SoftwareResources("InventorySys", 12, "v2.0");
        AGV agv2 = new AGV("A2", 9, 85, 6.0, new Time(1, 15), new Position(2, 1), 2.5f, 2.0f);

        // === OPERATIONS ===
        Resources[] resOp1 = {h1, agv1, m1};
        IOperation op1 = new TransportOperation("OP1", "Move materials", new Time(2, 0), resOp1);

        Resources[] resOp2 = {h2, s1, m2};
        IOperation op2 = new HumanOperation("OP2", "Update system and package goods", new Time(1, 30), resOp2);

        Resources[] resOp3 = {agv2, s2};
        IOperation op3 = new TransportOperation("OP3", "Deliver products", new Time(2, 15), resOp3);

        // === PROCESSES ===
        IOperation[] process1Ops = {op1, op2};
        Process industrialProcess = new IndustrialProcess("IndustrialProcess01", process1Ops);

        IOperation[] process2Ops = {op2, op3};
        Process managementProcess = new ManagementProcess("ManagementProcess01", process2Ops);

        // === WAREHOUSES ===
        Process[] prodProcesses = {industrialProcess};
        Warehouse productionWarehouse = new ProductionWarehouse("Production WH", prodProcesses);

        Process[] storageProcesses = {managementProcess};
        Warehouse storageWarehouse = new StorageWarehouse("Storage WH", storageProcesses);

        // === PRINT RESULTS ===
        productionWarehouse.printWarehouseInfo();
        System.out.println("\n====================================\n");
        storageWarehouse.printWarehouseInfo();
    }
}
